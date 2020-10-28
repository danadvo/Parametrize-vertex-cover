import java.util.*;

class SearchingTreeVC{
    /**
	 *
	 */
    int t=0;
	public static void main(final String[] args){
        System.out.println("Welcome to Vertex Cover problem- branching+kernelization implementation");
        Graph G = createInstance();
        int initK = G.getK();
        long starTime = System.currentTimeMillis();
        //Simplify input by kernelization
        Set<Integer> vc = KernelizationVC.kernelVC(G);
        if(vc.contains(-1) || vc.size() > initK){
            System.out.println("There is no legal solution for this input (kernelization)");
            System.out.println("Algorithm Runtime: " + (System.currentTimeMillis() - starTime) + "milli-seconds");
            return;
        }
        if (!vc.isEmpty())
            System.out.println("Simplified input by kernelization");

        vc = branchingVC(G, G.getK(), initK , vc);

        System.out.println("Algorithm Runtime: " + (System.currentTimeMillis() - starTime) + "milli-seconds");
        if (vc.contains(-1)){
            System.out.println("There is no legal solution for this input");
        }
        else{
            System.out.println("The solution to VC problem is:" + vc.toString());
        }

    }

    public static Graph createInstance (){
        //vertex-input
        Graph instance;
        int vertex_num = -1;
        int k_num = -1;
        Scanner input = new Scanner(System.in);
        try{
            System.out.println("Please enter number of vertices:");
            String vertex_str = input.nextLine();
            try{
                vertex_num = Integer.parseInt(vertex_str);
                if (vertex_num <=0){
                    throw new NumberFormatException("number of vertices is negtive, please try again\n");
                }
            } catch(NumberFormatException e){
                System.out.println("The number of vertices is Illegal , try again");
            }
            //k-input
            System.out.println("Please enter positive integer K or 0 for a random K:");
            String k_str = input.nextLine();
            try{
                k_num = Integer.parseInt(k_str);
                //check k>=0, vertex_num > k
                if(k_num < 0 || k_num > vertex_num){
                    throw new NumberFormatException(" K should be >= 0 and K < N, please try again\n");
                }
                if (k_num == 0){
                    double rand = Math.random();
                    k_num = (int)(rand * vertex_num);
                    System.out.println("The random K is:" + k_num);
                }
            }catch(NumberFormatException e){
                System.out.println("The value of K is Illegal , try again");
            }
        
            instance = new Graph(vertex_num, k_num);
            //edges-input
            System.out.println("1. To add random edges to the graph such that the proability of each edge is 0<p<=1 , enter: R p");
            System.out.println("    Examples: R 0.5, R 1, R 0.12");
            System.out.println("2. To insert your own edges: for each (i,j) insert i j, at the end enter 0 (1<=i,j<=N):");
            System.out.println("    Exapmle: if the edges are {(1,4) , (2,5)} - insert: 1 4 2 5 0");
            if (input.hasNext("R")){
                input.next();
                if(input.hasNextDouble()){
                    double p = input.nextDouble();
                    if (p<=1 && p>0){
                        for( int i=1 ; i<=vertex_num; i++){
                            for(int j=2; j<i ; j++){
                                double rand = Math.random();
                                if (rand<=p){
                                    instance.addEdge(i, j);
                                }
                            }
                        }
                    }
                    input.close();
                    //instance.printEdges();
                    return instance;
                }
                input.close();
                throw new IllegalStateException("Illegal p value, please insert 0<p<=1");
            }else if (input.hasNextInt()){
                int v1 = input.nextInt();
                if (v1 > vertex_num ){
                    throw new IllegalStateException("Illegal number of vertex, please try again");
                }
                while (v1!=0){
                    int v2 = -1;
                    if (input.hasNextInt()){
                        v2 = input.nextInt();
                        if (v2 > vertex_num ){
                            throw new IllegalStateException ("Illegal number of vertex, please try again");
                        }
                    }                
                    if (v2== 0 || v2==-1 ){
                        throw new IllegalStateException("Illegal Input, please insert even number of vertices and 0 in the end");
                    }
                    if (v2== v1){
                        throw new IllegalStateException("Illegal Input, the vertices of the edge must be different, try again");
                    }
                    instance.addEdge(v1, v2);
                    if (input.hasNextInt()){
                        v1 = input.nextInt();
                    }
                }
            }     
        }finally{   
            input.close();
        }
    return instance;     
    }


    public static Set<Integer> branchingVC (Graph G, int k, int initK, Set<Integer> vc){
        if (k < 0 || vc.size()>initK || k==0){                 
            return checkLegality(G, vc, initK);
        }

        for(Integer v: vc){         // G= (V\vc, E\{(i,j) : i or j in vc})
            if(G.getVertex_set().contains(v))
                G.delete_vertex(v);
        }

        foundDeg0(G, vc);               // remove all isolated vertices from G

        int maxDeg = G.getDegree(G.findMaxDegVrtx());

        if(maxDeg == 1){            // if there are more edges in the graph- add one of their vertices to vc
            return handleMaxDeg1( G,  vc,  initK);
        }

        Graph GNvInVC = G.cpyGraph();
        Graph GvInVC = G.cpyGraph();
        Set<Integer> vcWithV = cpyVC(vc);
        Set<Integer> vcNoV = cpyVC(vc);
        
        int v = G.findMaxDegVrtx();

        //v in G=> N(v) out of G , N(v) in VC
        int nv =0;
        addNVtoVC(GNvInVC, vcNoV, v , nv);

        //v not in G=> remove v
        GvInVC.delete_vertex(v);
        GvInVC.decK();

        Set<Integer> res1= branchingVC(GvInVC, GvInVC.getK(), initK ,addNgetVc(vcWithV,v)) ;
        if(!checkLegality(G, res1, initK).contains(-1)){
            return res1;
        }
        Set<Integer> res2= branchingVC(GNvInVC, GNvInVC.getK(), initK, vcNoV);
        if(!checkLegality(G, res2, initK).contains(-1)){
            return res2;
        }
        return addNgetVc(vc, -1);    
    }

    public static void foundDeg0 (Graph G, Set<Integer> vc){
        Set<Integer> toDelete = new HashSet<>();
        for(Integer j : G.getVertex_set()){
            int deg = G.getDegree(j);
            if(deg == 0){
                toDelete.add(j);
            }
        }
        for(Integer i : toDelete)
            G.delete_vertex(i);
    }

    public static Set<Integer> checkSolSize (Set<Integer> vc , int initK){
        if(vc.size() > initK){
            Set<Integer> noSol= new HashSet<>();
            noSol.add(-1);
            return noSol;
        }
        return vc;
    }

    public static Set<Integer> cpyVC(Set<Integer> vc){
        Set<Integer> newVC = new HashSet<Integer>();
        for( Integer i : vc){
            newVC.add(i);
        }
        return newVC;
    }

    public static Set<Integer> addNgetVc (Set<Integer> vc, int toAdd){
        vc.add(toAdd);
        return vc;
    }

    public static Set<Integer> checkLegality (Graph G, Set<Integer> vc , int k){
        if (k < vc.size() || k<0){
            return addNgetVc(vc, -1);
        }
        for (int i=2; i<=G.getVertex_num() ; i++){
            for( int j=1; j<i ; j++){
                if(G.isEdge(i, j)){
                    if( !vc.contains(i) && !vc.contains(j) ) {
                        return addNgetVc(vc, -1);
                    }
                }
            }
        }
        return vc;
    }

    public static void addNVtoVC(Graph G,Set<Integer> vc1,int v ,int nv){
        for(int i=1; i<=G.getVertex_num(); i++){
            if(G.isEdge(v, i)){
                vc1 = addNgetVc(vc1, i);
                G.removeAllEdges(i);
                G.decK();
                nv++;
            }
        }
        G.delete_vertex(v);
    }

    public static Set<Integer> handleMaxDeg1(Graph G, Set<Integer> vc, int initK){
        for (int i=2; i<=G.getVertex_num() ; i++){
            for( int j=1; j<i ; j++){
                if (G.isEdge(i, j)){
                    vc.add(i);
                    G.decK();
                    G.removeEdge(i, j);
                    G.delete_vertex(i);
                    G.delete_vertex(j);
                }
            }
        }
        return checkLegality(G, vc, initK); 
    }
}

