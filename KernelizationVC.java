import java.util.*; 

class KernelizationVC{
    /**
	 *
	 */
	public static void main(final String[] args){
        System.out.println("Welcome to Vertex Cover problem- Kernelization implementation");
        Graph instance = SearchingTreeVC.createInstance();
        int k = instance.getK();
        System.out.println("\n"+instance.sumEdges());

        long starTime = System.currentTimeMillis();
        Set<Integer> result = kernelVC(instance);

        if(result.contains(-1) || result.size() > k){
            System.out.println("There is no legal solution for this input");
            System.out.println("Algorithm Runtime: "+ (System.currentTimeMillis() - starTime) + " milli-seconds" );
            return;
        }
        else{
            for (int i=2; i<=instance.getVertex_num() ; i++){
                for( int j=1; j<i ; j++){
                    if(instance.isEdge(i, j)){
                        if( !result.contains(i) && !result.contains(j) ) {
                            System.out.println("\nThe algorithm could not decide if there is a legal solution. ");
                            if(!result.isEmpty()){
                                System.out.println("If there is a legal vertex cover it must contain the vertices:" + result.toString());
                            }
                            System.out.println("Algorithm Runtime: "+ (System.currentTimeMillis() - starTime) + " milli-seconds" );
                            return;
                        }
                    }
                }
            }
            System.out.println("The solution to VC problem is:" + result.toString());
            System.out.println("Algorithm Runtime: "+ (System.currentTimeMillis() - starTime) + " milli-seconds" );
            return;
        }
    }

    //return set that contains -1 iff there is no solution
    //otherwise, return set of rejected vertices
    public static Set<Integer> kernelVC(Graph instance){
        HashSet <Integer> vc = new HashSet<Integer>(); 
        boolean cont = true;
        while(cont){
            //check rule 1
            if (foundDeg0(instance, vc)== true){
            }
            else {
                //check rule 2
                if(instance.getK()>=0 && found_degKplus1(instance, vc)){
                }
                else{
                    //if k<0 and num of vertices > k^2+k or num of edges > k^2 there is no solution                    
                    if(instance.getK()<0 &&
                       instance.getVertex_set().size() > ( instance.getK() * instance.getK() ) + instance.getK() ||  

                       //instance.sumEdges() > (instance.getK()* instance.getK())){
                        instance.sumEdges() > (instance.getK() * instance.getDegree(instance.findMaxDegVrtx()) ) ){
                        vc.add(-1);
                        return vc;
                    }
                    cont = false;
                }
            }
        } 
        return vc;
    }

    //step 1: return true iff there is isolated vertex (degree=0)
    public static boolean foundDeg0 (Graph g,HashSet<Integer> vc){
        for(Integer j : g.getVertex_set()){
            int deg = g.getDegree(j);
            if(deg == 0){
                g.delete_vertex(j);
                return true;
            }
        }
        return false;
    }

    //step 2: return true iff there is vertex with degree>=k+1 (and remove it)
    public static boolean found_degKplus1(Graph g, HashSet<Integer> vc){
		for(Object j : g.getVertex_set()){
            int i=(int)j;
            int deg = g.getDegree(i);
            if(deg >= (g.getK()+1)){
                vc.add(i);
                g.delete_vertex(i);
                g.decK();
                return true;
            }
        }
        return false;
    }


}