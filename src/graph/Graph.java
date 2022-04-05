package graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;
import helper.lineParse;

import graphImplementation.PDG_Generator;

public class Graph {

	protected String id, sourceCode;
	protected List<Node> nodes;
	protected HashMap<String, List<Edge>> edges;

	public Graph(String id) {
		this();
		this.id = id;
	}

	public Graph(String id, String originalText) {
		this();
		this.id = id;
		this.sourceCode = originalText;
	}

	public Graph() {
		nodes = new ArrayList<>();
		edges = new HashMap<>();
	}

	public void addNode(Node node) {
		if (!edges.containsKey(node.getId())) {
			edges.put(node.getId(), new ArrayList<Edge>());
		}
		nodes.add(node);
	}

	public int getSize() {
		return nodes.size();
	}

	public HashMap<String, List<Edge>> getEdges() {
		return edges;
	}

	public void addEdge(Edge edge) {
		edges.get(edge.getFrom().getId()).add(edge);

	}

	public List<Edge> getEdges(Node node) {
		return getEdges(node.getId());
	}

	public List<Edge> getEdges(String nodeId) {
		return edges.get(nodeId);
	}

	public void removeNode(int i) {
		nodes.remove(i);
	}

	public Node getNode(int i) {
		return nodes.get(i);
	}

	public Node getNode(String id) {

		for (Node node : nodes) {

			if (node.getId().equals(id)) {

				return node;
			}
		}
		return null;
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public String getId() {
		return id;
	}

	public String getSourceCode() {
		return sourceCode;
	}

	Graph createGraph(File graphFile, Hashtable<Integer, String> sourceCode) throws FileNotFoundException {
		Graph graph = new Graph();

		Scanner scanner = new Scanner(graphFile);
		if (scanner.hasNext()) {
			scanner.nextLine();
		}
		int counter = 0;
		int edgeID = 0;
		while (scanner.hasNext()) {
			String line = scanner.nextLine();
			if (line.length() <= 12) {
				String tokens[] = line.split(";");
				if (!tokens[0].equalsIgnoreCase("}")) {
					Node node = new Node(tokens[0].replaceAll(" ", ""), sourceCode.get(counter));

					graph.addNode(node);
					counter++;

				}

			} else {

				String tokens[] = line.split("->");
				String token[] = tokens[1].split("\\[");
				Node from = graph.getNode(tokens[0].replaceAll(" ", ""));
				Node to = graph.getNode(token[0].replaceAll(" ", ""));
				Edge edge = new Edge(edgeID + "", from, to, "");
				graph.addEdge(edge);
				edgeID++;
			}

		}

		System.out.println(graph.toString());
		return graph;

	}

	@Override
	public String toString() {
		return "Graph [nodes=" + nodes + ", edges=" + edges + "]";
	}

	public Graph generateGraph(String file) throws FileNotFoundException {
		String filename = file;
		lineParse p = new lineParse();
		PDG_Generator.getDotFile(".\\input\\" + filename + ".java");
		Graph graph = new Graph();
		File graphFile = new File(".\\graphFiles\\" + filename);
		Hashtable<Integer, String> sourceCode= p.getLineContent(filename);
		graph = graph.createGraph(graphFile,sourceCode);
		return graph;

	}

}
