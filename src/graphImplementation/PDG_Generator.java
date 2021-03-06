package graphImplementation;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
import graphStructures.GraphNode;
import graphStructures.RelationshipEdge;
import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.StringEdgeNameProvider;
import org.jgrapht.ext.StringNameProvider;
import org.jgrapht.ext.VertexNameProvider;
import org.jgrapht.graph.DefaultDirectedGraph;
import pdg.PDGCore;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Scanner;

public class PDG_Generator {
	
	// Declare variables of nencessary componets of GUI PDG application
	private static Graph<GraphNode, RelationshipEdge> hrefGraph;
	private static PDGCore astPrinter = new PDGCore();
	private static JTextArea consoleText;
	static HashMap<String, String> semanticTypesMap = new HashMap<String, String>();
	File file = new File("./input/dictionaryData.txt");
	
	// Getter for SemanticTypes
	public static HashMap<String, String> getSemanticTypesMap() {
		return semanticTypesMap;
	}
	
	// Setter for SemanticTypes
	public static void setSemanticTypesMap(HashMap<String, String> map)
    {
      semanticTypesMap = map;
    }
	
	
	// Initialize AST Printer with use of Javaparser library to print all semantic types
	public static void getAST(FileInputStream inArg) throws ParseException, IOException {
		CompilationUnit cu;
		cu= JavaParser.parse(inArg);
		inArg.close();
		ASTPrinter as = new ASTPrinter();
		as.astPrint(cu);
		
	}
	
	// Running headless GUI application of PDG to generate DOT file
	public static void getDotFile(String filename) {

		File selectedFile = new File(filename);
		consoleText = new JTextArea();

		try {
			String content = (new Scanner(selectedFile)).useDelimiter("\\Z").next();
			
			consoleText.setText(content);
		} catch (FileNotFoundException | NullPointerException var6) {
			var6.printStackTrace();
		}
		
		// Make PDG graph given Java files
		runAnalysisAndMakeGraph(selectedFile);

		try {
			checkIfFolderExists();
			GraphNode.exporting = true;
			String outputFilename = selectedFile.getName().substring(0, selectedFile.getName().length() - 4);  
			FileOutputStream out = new FileOutputStream("graphFiles/" + outputFilename);
			
			// Export dot files
			DOTExporter<GraphNode, RelationshipEdge> exporter = new DOTExporter(new StringNameProvider(),
					(VertexNameProvider) null, new StringEdgeNameProvider());
			exporter.export(new OutputStreamWriter(out), hrefGraph);
			out.close();
		} catch (IOException var6) {
			var6.printStackTrace();
		}

		GraphNode.exporting = false;
	}
	
	// Check if the output folder exists
	private static boolean checkIfFolderExists() {
		File theDir = new File("dotOutputs");
		return !theDir.exists() && theDir.mkdir();
	}
	
	// Create graph and initialize the dictionary for semantic types
	private static void runAnalysisAndMakeGraph(File selectedFile) {
		createGraph();
		GraphNode gn = new GraphNode(0, "Entry");
		hrefGraph.addVertex(gn);
		HashMap<String, String> map = new HashMap<String, String>();
		String prevLine = "";
		try {
			File file = new File("./input/dictionaryData.txt"); 
			
			//Code segments are checked for to compute their semantic types
		
			getAST(new FileInputStream(selectedFile));
			
			//Dictionary (Hashmap called semanticTypesMap is created where code segments are keys and semantic types are values
			
			
			try (BufferedReader br = new BufferedReader(new InputStreamReader(
				//Raw code segments and their semantic types are stored in a file
	            new FileInputStream("./input/dictionaryData.txt"), StandardCharsets.UTF_8));) {

	            String line;
	            
	            //Data is stored in ASTPrinter class under as astPrint method 
	            while ((line = br.readLine()) != null) {
	                
	            	//Lines are parsed to differentiate between code segments and semantic types (stored in hashmap thereafter)
	                System.out.println(line);
	                if(line.contains("----------------------------------------") && !line.isEmpty()) {
	                	continue;
	                }
	                else if((line.contains("class")) && !line.isEmpty()){
	                	System.out.println(line);
	                	String[] classSplit = line.split("\\.",6);
	                	prevLine = classSplit[classSplit.length-1];
	                }	                
	                else if (line.contains("class") || 
	                		line.contains("Class.Name") || 
	                		line.contains("Class.ExtensionOf") || 
	                		line.contains("Method.Type") || 
	                		line.contains("Method.Name") ||
	                		line.contains("Method.Modifier") ||
	                		line.contains("ClassOrInterface.Modifier")){
	                	System.out.println(line);
	                	prevLine = line;
	                }
	                else if(!line.isEmpty()) {
	                	map.put(line, prevLine);
	                	prevLine = "";
	                }
	            }
	            
	            //Set local hashmap copy to object accessible one 
	            setSemanticTypesMap(map);
	            
	            //Raw data containing file is deleted
	            file.delete();
	            
	            //Dictionary key and values are printed for debugging purposes
	            System.out.println("\n\n Dictionary keys and values start\n");
	            for (String i : getSemanticTypesMap().keySet()) {
	            	  System.out.println(i);
	            	  System.out.println(map.get(i) + "\n");
	            }
	            System.out.println("\n Dictionary keys and values end\n\n");

	            
	            
	            
	        } catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//Dictionary population ends
			
			
			astPrinter.addFile(new FileInputStream(selectedFile),
					(DirectedGraph<GraphNode, RelationshipEdge>) hrefGraph, gn, consoleText);
			
		} catch (IOException | ParseException var2) {
			var2.printStackTrace();
		}
	}
	
	// Create graph with relationEdge
	private static void createGraph() {
		hrefGraph = new DefaultDirectedGraph(RelationshipEdge.class);
	}
	
	
	
}

class ASTPrinter {
	
	static File file = new File("./input/dictionaryData.txt");
	 void astPrint(Node child2){
	    
		//File writer and string s to use to write are declared and initialized 
		FileOutputStream out = null;
	    String s = "";
		
	    if(relevant(child2)) {
			if(child2.getClass().equals(com.github.javaparser.ast.body.MethodDeclaration.class)){
				printMethodModifiers(child2);
				MethodType(child2);
				MethodName(child2);
			}
			else if(child2.getClass().equals(com.github.javaparser.ast.body.ClassOrInterfaceDeclaration.class)){
				printClassIntModifiers(child2);
				ClassName(child2);
				ClassExtension(child2);
			}    		
			
			else{
				//If raw data containing file already exists
				if(file.exists()) {
					try {
						file.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				try {
					out = new FileOutputStream("./input/dictionaryData.txt", true);
					
					//code segment and semantic types seperator in raw data
					System.out.println(file.getAbsolutePath());
					System.out.println("------------------------------------------------------------");
					s = "------------------------------------------------------------";
					//Write this to file
					try {
						out.write(s.getBytes());
						out.write("\n".getBytes());
						out.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					//Semantic type of a code segment 
					System.out.println(child2.getClass());
					s = String.valueOf(child2.getClass());
					
					//Write this to file
					try {
						out.write(s.getBytes());
						out.write("\n".getBytes());
						out.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					//Code segment without newlines so that dictionary keys are populated correctly
					System.out.println(child2.toString());
					s = String.valueOf(child2.toString());
					s = s.replaceAll("\n", "");
					
					//Write this to file
					try {
						out.write(s.getBytes());
						out.write("\n".getBytes());
						out.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		}
		
		
		/*
		try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		
	
		//getLineSemanticTypeDictionary(file);
		
		
		//Continue with the other print documentation
		child2.getChildrenNodes().forEach(this::astPrint);
	}
	 
	//Method for all print and writing to dictionary data txt file purposes
	public static void printAndWriteToDictionaryData(boolean conditional, String semanticType) {
		String s = "";
		FileOutputStream out = null;
		
		if(conditional) {
			
			if(file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			try {
				out = new FileOutputStream("./input/dictionaryData.txt", true);
				
				//code segment and semantic types seperator in raw data
				System.out.println(file.getAbsolutePath());
				System.out.println("------------------------------------------------------------");
				s = "------------------------------------------------------------";
				//Write this to file
				try {
					out.write(s.getBytes());
					out.write("\n".getBytes());
					out.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				System.out.print(semanticType);
				s = semanticType;
				//Write this to file
				try {
					out.write(s.getBytes());
					out.write("\n".getBytes());
					out.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}	
		}
	}
	 
	public void getLineSemanticTypeDictionary(String filePath) {
		System.out.println("\n\n\n Dictionary function \n\n\n");
		FileInputStream in = null;
		try {
			in = new FileInputStream(filePath);
			int c;
	        while ((c = in.read()) != -1) {
	           Integer.toString(c);
	        }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static boolean relevant(Node child2) {
		return !child2.getClass().equals(com.github.javaparser.ast.body.VariableDeclarator.class) &&
				!child2.getClass().equals(CompilationUnit.class)
                && !child2.getClass().equals(com.github.javaparser.ast.stmt.ExpressionStmt.class)
                && !child2.getClass().equals(com.github.javaparser.ast.stmt.BlockStmt.class)
                && !child2.getClass().equals(com.github.javaparser.ast.type.VoidType.class)
                && !child2.getClass().equals(com.github.javaparser.ast.type.ClassOrInterfaceType.class);
	}
	
	private static void MethodType(Node child2){
		printAndWriteToDictionaryData(child2.getClass().equals(com.github.javaparser.ast.body.MethodDeclaration.class), "Method.Type\n"+((MethodDeclaration)child2).getType().toString());
	}
	
	private static void MethodName(Node child2){		
		printAndWriteToDictionaryData(child2.getClass().equals(com.github.javaparser.ast.body.MethodDeclaration.class), "Method.Name\n"+((MethodDeclaration)child2).getNameExpr());		
	}
		
	private static void ClassName(Node child2){ 	
		printAndWriteToDictionaryData(child2.getClass().equals(com.github.javaparser.ast.body.ClassOrInterfaceDeclaration.class), "Class.Name\n"+((ClassOrInterfaceDeclaration)child2).getNameExpr());
	}
	
	private static void ClassExtension(Node child2){ 
		printAndWriteToDictionaryData(child2.getClass().equals(com.github.javaparser.ast.body.ClassOrInterfaceDeclaration.class), "Class.ExtensionOf\n"+((ClassOrInterfaceDeclaration)child2).getExtends().toString());
	}
		
	private static void printMethodModifiers(Node child2) {
		printAndWriteToDictionaryData(ModifierSet.isPrivate(((MethodDeclaration) child2).getModifiers()),"Method.Modifier\nprivate");
		printAndWriteToDictionaryData(ModifierSet.isPublic(((MethodDeclaration) child2).getModifiers()),"Method.Modifier\npublic");
		printAndWriteToDictionaryData(ModifierSet.isStatic(((MethodDeclaration) child2).getModifiers()),"Method.Modifier\nstatic");
		printAndWriteToDictionaryData(ModifierSet.isStrictfp(((MethodDeclaration) child2).getModifiers()),"Method.Modifier\nstrictfp");
		printAndWriteToDictionaryData(ModifierSet.isSynchronized(((MethodDeclaration) child2).getModifiers()),"Method.Modifier\nsyncronized");
		printAndWriteToDictionaryData(ModifierSet.isTransient(((MethodDeclaration) child2).getModifiers()),"Method.Modifier\ntransient");
		printAndWriteToDictionaryData(ModifierSet.isVolatile(((MethodDeclaration) child2).getModifiers()),"Method.Modifier\nvolatile");
	}	
	
	private static void printClassIntModifiers(Node child2) {
		printAndWriteToDictionaryData(ModifierSet.isPrivate(((ClassOrInterfaceDeclaration) child2).getModifiers()),"ClassOrInterface.Modifier\nprivate");
		printAndWriteToDictionaryData(ModifierSet.isPublic(((ClassOrInterfaceDeclaration) child2).getModifiers()),"ClassOrInterface.Modifier\npublic");
		printAndWriteToDictionaryData(ModifierSet.isStatic(((ClassOrInterfaceDeclaration) child2).getModifiers()),"ClassOrInterface.Modifier\nstatic");
		printAndWriteToDictionaryData(ModifierSet.isStrictfp(((ClassOrInterfaceDeclaration) child2).getModifiers()),"ClassOrInterface.Modifier\nstrictfp");
		printAndWriteToDictionaryData(ModifierSet.isSynchronized(((ClassOrInterfaceDeclaration) child2).getModifiers()),"ClassOrInterface.Modifier\nsyncronized");
		printAndWriteToDictionaryData(ModifierSet.isTransient(((ClassOrInterfaceDeclaration) child2).getModifiers()),"ClassOrInterface.Modifier\ntransient");
		printAndWriteToDictionaryData(ModifierSet.isVolatile(((ClassOrInterfaceDeclaration) child2).getModifiers()),"ClassOrInterface.Modifier\nvolatile");
}
}
	
	
