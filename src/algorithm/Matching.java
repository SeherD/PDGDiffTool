package algorithm;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import application.Config;
import graph.Graph;
import misc.EditWeightService;

public class Matching {
	
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
	public static void getDistance(String[] files, Map<String,Double> posEditWeights, Map<String,Double> deprelEditWeights) throws FileNotFoundException  {
		Graph g1 = new Graph();
		g1=g1.generateGraph(files[0]);
		Graph g2 = new Graph();
		g2=g2.generateGraph(files[1]);
		
		//GraphEditDistance ged = new GraphEditDistance(g1, g2, posEditWeights, deprelEditWeights);
		//testSystem.setOut(new PrintStream(new FileOutputStream("output.txt")));
		//ged.printMatrix();
		
		/*System.out.println("Calculating graph edit distance for the two sentences:");
		System.out.println(files[0]);
		System.out.println(files[1]);
		System.out.println("Distance between the two files: "+ged.getDistance()+". Normalised: "+ged.getNormalizedDistance());
		System.out.println("Edit path:");
		/*for(String editPath : getEditPath(g1, g2, ged.getCostMatrix(), true)) {
			System.out.println(editPath);
		}*/
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


}
