package application;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import algorithm.GraphEditDistance;
import graph.Graph;
import graph.Node;
import misc.EditWeightService;

import com.konstantinosnedas.HungarianAlgorithm;

public class App {
	static PrintStream stdout = System.out;
	static Map<Integer, String> obs = new HashMap<Integer, String>();

	
	
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		Config cs = new Config("app.properties");
		//Sets the substitution matrix for weights of node substitutions
		Map<String, Double> posEditWeights = EditWeightService.getEditWeights(cs.getProperty("POS_SUB_WEIGHTS"),
				cs.getProperty("POS_INSDEL_WEIGHTS"));
		Map<String, Double> deprelEditWeights = null;

		
		boolean running = true;
		while (running) {
			System.out.println("\n------");
			String[] files = getInputTexts(args);
			getDistance(files, posEditWeights, deprelEditWeights);
			running = shouldContinue();
		}
		System.out.println("Exiting..");
	}

	
	//Uses the edit path to calculate the Graph edit distance and normalizes it based upon the length of the two graphs
	public static void getDistance(String[] files, Map<String, Double> posEditWeights,
			Map<String, Double> deprelEditWeights) throws FileNotFoundException {
		Graph g1 = new Graph();
		g1 = g1.generateGraph(files[0]);
		Graph g2 = new Graph();
		g2 = g2.generateGraph(files[1]);

		GraphEditDistance ged = new GraphEditDistance(g1, g2, posEditWeights, deprelEditWeights);
		
		
		//Generates Output to txt file
		System.setOut(new PrintStream(new FileOutputStream(files[0] + "_" + files[1] + "_" + "output.txt")));
		// ged.printMatrix();

		System.out.println("Calculating graph edit distance for the two files:");
		System.out.println(files[0]);
		System.out.println(files[1]);
		System.out.println("Distance between the two files: " + ged.getDistance() + ". Normalised: "
				+ ged.getNormalizedDistance());

		List<String> editPathFull = getEditPath(g1, g2, ged.getCostMatrix(), true);
		editPathAnalysis(editPathFull, ged.getNormalizedDistance());
		System.setOut(stdout);
	}
	
	//Returns results based upon normalized GED
	public static void editPathAnalysis(List<String> editPathFull, double normalisedDistance) {

		if (normalisedDistance > 1) {
			System.out.println("The given files are completely different.");
		} else if (normalisedDistance == 0) {
			System.out.println("The given files are semantically identical.");
		} else if (normalisedDistance >= 0.5 && normalisedDistance <= 1) {
			System.out.println("The given files are semantically similar but contains major changes.");
		} 
		else {
			System.out.println("The given files are semantically similar, contains only minor changes.");
			System.out.println("Edit path:");
			for (String editPath : editPathFull) {

				System.out.println(editPath);

			}

		}

	}

	
	//Gets the files to be compared for input
	public static String[] getInputTexts(String[] args) {
		String text1 = "", text2 = "";
		if (args.length != 2) {
			InputStreamReader converter = new InputStreamReader(System.in);
			BufferedReader in = new BufferedReader(converter);
			try {
				System.out.println("Please enter the first file: ");
				text1 = in.readLine();

				System.out.println("Please enter the second file: ");
				text2 = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			return args;
		}

		return new String[] { text1, text2 };
	}

	public static List<String> getEditPath(Graph g1, Graph g2, double[][] costMatrix, boolean printCost) {
		return getAssignment(g1, g2, costMatrix, true, printCost);
	}

	public static List<String> getFreeEdits(Graph g1, Graph g2, double[][] costMatrix) {
		return getAssignment(g1, g2, costMatrix, false, false);
	}

	//Calculates the optimal matching using the hungarian algorithm and the cost matrix
	public static List<String> getAssignment(Graph g1, Graph g2, double[][] costMatrix, boolean editPath,
			boolean printCost) {
		List<String> editPaths = new ArrayList<>();
		int[][] assignment = HungarianAlgorithm.hgAlgorithm(costMatrix, "min");

		for (int i = 0; i < assignment.length; i++) {
			String from = getEditPathAttribute(assignment[i][0], g1);
			String to = getEditPathAttribute(assignment[i][1], g2);

			double cost = costMatrix[assignment[i][0]][assignment[i][1]];
			if (cost != 0 && editPath) {
				if (printCost) {
					String[] froms = formatting(from);
					String[] tos = formatting(to);

					if (from.equals(to)) {
						obs.put(new Integer(i), "Number of edges attached to this node have changed.");
						editPaths.add("Node \n" + froms[0] + " -> " + tos[0] + "\n" + froms[1] + " -> " + tos[1] + "\n"
								+ froms[2] + " -> " + tos[2] + "\n" + "Cost= " + cost + "\n");
					}

					else if (from.equalsIgnoreCase("ε")) {

						editPaths.add("Node \n" + from + " -> " + tos[0] + "\n" + from + " -> " + tos[1] + "\n" + from
								+ " -> " + tos[2] + "\n" + "Cost= " + cost + "\n");
						obs.put(i, "This node was inserted.");
					} else if (to.equalsIgnoreCase("ε")) {
						editPaths.add("Node \n" + froms[0] + " -> " + to + "\n" + froms[1] + " -> " + to + "\n" + froms[2]
								+ " -> " + to + "\n" + "Cost= " + cost + "\n");
						obs.put(i, "This node was deleted.");
					} else {
						editPaths.add("Node \n" + froms[0] + " -> " + tos[0] + "\n" + froms[1] + " -> " + tos[1] + "\n"
								+ froms[2] + " -> " + tos[2] + "\n" + "Cost= " + cost + "\n");
					}
				}
			} else if (cost == 0 && !editPath) {
				editPaths.add(from + " -> " + to);

			}
		}

		return editPaths;

	}
	//splits strings based on commas
	static String[] formatting(String node) {
		String[] tokens = node.split(",");
		return tokens;
	}

	//gets either node.toString or Epsilon depending on if its an insertion or substitution
	private static String getEditPathAttribute(int nodeNumber, Graph g) {
		if (nodeNumber < g.getNodes().size()) {
			Node n = g.getNode(nodeNumber);
			return n.toString();
		} else {

			return "ε";
		}
	}

	public static boolean shouldContinue() {
		InputStreamReader converter = new InputStreamReader(System.in);
		BufferedReader in = new BufferedReader(converter);
		System.out.println("continue? [y/n]");
		try {
			String answer = in.readLine();
			return (answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
