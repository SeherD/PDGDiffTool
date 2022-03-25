import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Graph {
	 private Map<Vertex, List<Vertex>> adjVertices;
	    
	    public Graph() {
		
		this.adjVertices = new HashMap<Vertex, List<Vertex>>();
	}

		void addVertex(String label) {
	        adjVertices.putIfAbsent(new Vertex(label), new ArrayList<>());
	    }

	    void removeVertex(String label) {
	        Vertex v = new Vertex(label);
	        adjVertices.values().stream().forEach(e -> e.remove(v));
	        adjVertices.remove(new Vertex(label));
	    }
	    void addEdge(String label1, String label2) {
	        Vertex v1 = new Vertex(label1);
	        Vertex v2 = new Vertex(label2);
	        adjVertices.get(v1).add(v2);
	        
	    }
	    void removeEdge(String label1, String label2) {
	        Vertex v1 = new Vertex(label1);
	        Vertex v2 = new Vertex(label2);
	        List<Vertex> eV1 = adjVertices.get(v1);
	        
	        if (eV1 != null)
	            eV1.remove(v2);
	        
	    }
	    
	    List<Vertex> getAdjVertices(String label) {
	        return adjVertices.get(new Vertex(label));
	    }
	    
	    
	    Set<String> depthFirstTraversal(Graph graph, String root) {
	        Set<String> visited = new LinkedHashSet<String>();
	        Stack<String> stack = new Stack<String>();
	        stack.push(root);
	        while (!stack.isEmpty()) {
	            String vertex = stack.pop();
	            if (!visited.contains(vertex)) {
	                visited.add(vertex);
	                for (Vertex v : graph.getAdjVertices(vertex)) {              
	                    stack.push(v.label);
	                }
	            }
	        }
	        return visited;
	    }
	    
	    Set<String> breadthFirstTraversal(Graph graph, String root) {
	        Set<String> visited = new LinkedHashSet<String>();
	        Queue<String> queue = new LinkedList<String>();
	        queue.add(root);
	        visited.add(root);
	        while (!queue.isEmpty()) {
	            String vertex = queue.poll();
	            for (Vertex v : graph.getAdjVertices(vertex)) {
	                if (!visited.contains(v.label)) {
	                    visited.add(v.label);
	                    queue.add(v.label);
	                }
	            }
	        }
	        return visited;
	    }
	    
	    Graph createGraph(File graphFile) throws FileNotFoundException {
	    	Graph graph = new Graph();
	    	
	    	
	    	    Scanner scanner = new Scanner(graphFile);
	    	    if(scanner.hasNext()) {
	    	    	scanner.nextLine();
	    	    }
	    	    while(scanner.hasNext()){
	    	    	String line =scanner.nextLine();
	    	    	if(line.length()<=12)
	    	    	{
	    	    		String tokens[] = line.split(";");
	    	    		if(!tokens[0].equalsIgnoreCase("}"))
	    	    		{
	    	    			graph.addVertex(tokens[0].replaceAll(" ", ""));
	    	    			System.out.println(tokens[0].replaceAll(" ", ""));
	    	    		}
	    	    		
	    	    	}
	    	    	else {
	    	    		
	    	    		String tokens[]=line.split("->");
	    	    		String token[] = tokens[1].split("\\[");
	    	    		graph.addEdge(tokens[0].replaceAll(" ", ""), token[0].replaceAll(" ", ""));
	    	    		System.out.println(tokens[0].replaceAll(" ", "")+"+"+token[0].replaceAll(" ", ""));
	    	    	}
	    	        
	    	        
	    	    }
	    	    
	    	
	    	
			return graph;
	    	
	    }

		public Map<Vertex, List<Vertex>> getAdjVertices() {
			return adjVertices;
		}

		public void setAdjVertices(Map<Vertex, List<Vertex>> adjVertices) {
			this.adjVertices = adjVertices;
		}
	    
	    
		public static void main(String[] args) throws FileNotFoundException {
			Graph graph = new Graph();
			File graphFile = new File(".\\graphFiles\\trial");
			graph=graph.createGraph(graphFile);
			System.out.println(graph.breadthFirstTraversal(graph, "Line_0").toString());

	    }
	    
}
