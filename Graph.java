import java.util.*; 

class Graph<T> { 
  
    //hash map to store the edges in the graph 
    protected Map<T, List<T> > map = new HashMap<>(); 
    // This function adds a new vertex to the graph 
    public void addVertex(T s) 
    { 
        map.put(s, new LinkedList<T>()); 
    } 
  
    // This function adds the edge 
    // between source to destination 
    public void addEdge(T source, 
                        T destination, 
                        boolean bidirectional) 
    { 
  
        if (!map.containsKey(source)) 
            addVertex(source); 
  
        if (!map.containsKey(destination)) 
            addVertex(destination); 
  
        map.get(source).add(destination); 
        if (bidirectional == true) { 
            map.get(destination).add(source); 
        } 
    } 
    
    //finds the path between s and d
    public List<T> path(T s, T d) {
    	List<T> l = new LinkedList<>();
    	boolean found = false;
    	while(!found) {
    		for(T v : map.keySet()) {
    			if(map.get(v).contains(d)) {
    				l.add(d);
    				d = v; //makes the current key the new target
    				if(v.equals(s)) { //last vertex found
    					found = true;
    					l.add(v);
    					break;
    				}
    			}
	    	}
    	}
    	
    	return l;
	    	
    }
} 