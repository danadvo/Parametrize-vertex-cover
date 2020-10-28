import java.util.*; 

class Graph{

    private int vertex_num;
    private Set<Integer> vertex_set = new HashSet<Integer>();
    private boolean[][] edges ;
    private int k;
    
    public Graph (int vertex_num, int k){
        this.k=k;
        this.vertex_num = vertex_num;
        edges = new boolean[vertex_num+1][vertex_num+1];
        //init vertices & edges
        for ( int i=1; i<=vertex_num; i++){
            vertex_set.add(i);
            for ( int j=1; j<=vertex_num; j++){
                edges[i][j]=false;
            }
        }
    }

    public Graph cpyGraph (){
        Graph cpy = new Graph(vertex_num, k);
        //init edges
        for (int i=1; i<=vertex_num ; i++){
            for( int j=1; j<=vertex_num; j++){
                if(edges[i][j]){
                    cpy.addEdge(i, j);;
                    cpy.vertex_set.add(i);
                    cpy.vertex_set.add(j);
                } else {
                    cpy.edges[i][j] = false;
                }
            }
        }
        return cpy;
    }
    public int getVertex_num (){
        return vertex_num;
    }
    public int getVertex_size (){
        return vertex_set.size();
    }
    public Set<Integer> getVertex_set (){
        return vertex_set;
    }
    public void delete_vertex(int v){
        vertex_set.remove(v);
        for(int i = 1; i<= vertex_num ; i++){
            edges[i][v] = false;
            edges[v][i] = false;
        }
    }
    public int getK (){
        return k;
    }
    public void decK (){
        k--;
    }

    public void addEdge(int v1, int v2){
        edges[v1][v2]= true;
        edges[v2][v1]= true;
    }
    public void removeEdge(int v1, int v2){
        edges[v1][v2]= false;
        edges[v2][v1]= false;
    }
    public void removeAllEdges(int v){
        if(getDegree(v)>0){
            for(int i=1; i<=vertex_num ; i++){
                if(isEdge(v, i))
                    removeEdge(v, i);
            }
        }
    }

    public int sumEdges(){
        int sum = 0;
        for (int i=2; i<=vertex_num ; i++){
            for( int j=1; j<i ; j++){
                if (edges[i][j]){
                    sum++;
                }
            }
        }
        return sum;
    }

    public boolean isEdge(int v1,int v2){
        return edges[v1][v2];
    }

    public int findMaxDegVrtx(){
        int maxDeg = 0;
        int res = -1;
        for(int v : getVertex_set()){
            if (getDegree(v) > maxDeg){
                res = v;
                maxDeg = getDegree(v);
            }
        }
        return res;
    }

    public Set<Integer> maxDegSet(int deg){
        Set<Integer> res = new HashSet<Integer>();
        for(Integer v : getVertex_set()){
            if (getDegree(v) == deg){
                res.add(v);
            }
        }
        return res;
    }

    public int getDegree (int v){
        int degree =0;
        for ( int i=1; i<=vertex_num; i++){
            if (isEdge(i, v)){
                degree++;
            }
        }
        return degree;
    }

    public void printEdges(){
        for(int i=1; i<=vertex_num; i++){
            for(int j=1; j< i ; j++){
                if (isEdge(i , j))
                    //System.out.print( "(" + i +","+ j + ") ");
                    System.out.print( " " + i +" "+ j );
            }
        }
    }
}