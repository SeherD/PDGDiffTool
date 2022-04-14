package application;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import algorithm.GraphEditDistance;
import graph.Graph;
import graph.Node;
import misc.EditWeightService;


import com.konstantinosnedas.HungarianAlgorithm;


public class App {
	static PrintStream stdout = System.out;
	static List<String> obs = new ArrayList<String>();
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		Config cs = new Config("app.properties");
		
		Map<String, Double> posEditWeights = EditWeightService.getEditWeights(cs.getProperty("POS_SUB_WEIGHTS"), cs.getProperty("POS_INSDEL_WEIGHTS"));
		Map<String, Double> deprelEditWeights = EditWeightService.getInsDelCosts(cs.getProperty("DEPREL_INSDEL_WEIGHTS"));

		for (String key: posEditWeights.keySet()){
            System.out.println(key +" = "+posEditWeights.get(key));
        }
		boolean running = true;
		while(running) {
			System.out.println("\n------");
			String[] files = getInputTexts(args);
			getDistance(files, posEditWeights, deprelEditWeights);
			running = shouldContinue();
		}
		System.out.println("Exiting..");
	}
	
	public static void getDistance(String[] files, Map<String,Double> posEditWeights, Map<String,Double> deprelEditWeights) throws FileNotFoundException  {
		Graph g1 = new Graph();
		g1=g1.generateGraph(files[0]);
		Graph g2 = new Graph();
		g2=g2.generateGraph(files[1]);
		
		GraphEditDistance ged = new GraphEditDistance(g1, g2, posEditWeights, deprelEditWeights);
		System.setOut(new PrintStream(new FileOutputStream(files[0] + "_" + files[1] + "_" + "output.txt")));
		//ged.printMatrix();
		
		System.out.println("Calculating graph edit distance for the two files:");
		System.out.println(files[0]);
		System.out.println(files[1]);
		System.out.println("Distance between the two files: "+ged.getDistance()+". Normalised: "+ged.getNormalizedDistance());
		
		List<String> editPathFull= getEditPath(g1, g2, ged.getCostMatrix(), true);
		editPathAnalysis(editPathFull,ged.getNormalizedDistance());
		System.setOut(stdout);
	}
	
		public static void editPathAnalysis(List<String> editPathFull, double normalisedDistance) {
			
			if(normalisedDistance>1) {
				System.out.println("The given files are completely different.");
			}
			else if(normalisedDistance==0) {
				System.out.println("The given files are semantically identical.");
			}
			else {
				System.out.println("Edit path:");
				for(String editPath : editPathFull) {
					System.out.println(editPath);
				}
				
				for (String ob : obs) {
					//System.out.println(ob);
				}
			}
		
			}

	public static String[] getInputTexts(String[] args)  {
		String text1="", text2="";
		if(args.length!=2) {
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
		}else {
			return args;
		}

		return new String[] {text1, text2};
	}

	
	
	public static List<String> getEditPath(Graph g1, Graph g2, double[][] costMatrix, boolean printCost) {
		return getAssignment(g1, g2, costMatrix, true, printCost);
	}

	public static List<String> getFreeEdits(Graph g1, Graph g2, double[][] costMatrix) {
		return getAssignment(g1, g2, costMatrix, false, false);
	}

	public static List<String> getAssignment(Graph g1, Graph g2, double[][] costMatrix, boolean editPath, boolean printCost) {
		List<String> editPaths = new ArrayList<>();
		int[][] assignment = HungarianAlgorithm.hgAlgorithm(costMatrix, "min");

		for (int i = 0; i < assignment.length; i++) {
			String from = getEditPathAttribute(assignment[i][0], g1);
			String to = getEditPathAttribute(assignment[i][1], g2);

			double cost = costMatrix[assignment[i][0]][assignment[i][1]];
			if(cost != 0 && editPath) {
				if(printCost) {
					editPaths.add(from+" -> "+to+cost);
					if(from.equals(to)) {
						obs.add(from+" -> "+to + " " + "Edge Association Changed"); 
					}
				}
			}else if(cost == 0 && !editPath) {
				editPaths.add(from+" -> "+to);
				
			}
		}

		return editPaths;

	}

	private static String getEditPathAttribute(int nodeNumber, Graph g) {
		if(nodeNumber < g.getNodes().size()) {
			Node n= g.getNode(nodeNumber);
			return n.toString();
		}else {
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
