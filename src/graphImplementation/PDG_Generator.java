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

	private static Graph<GraphNode, RelationshipEdge> hrefGraph;
	private static PDGCore astPrinter = new PDGCore();
	private static JTextArea consoleText;
	static HashMap<String, String> semanticTypesMap = new HashMap<String, String>();
	File file = new File("./input/dictionaryData.txt");
	
	public static HashMap<String, String> getSemanticTypesMap() {
		return semanticTypesMap;
	}
	
	public static void setSemanticTypesMap(HashMap<String, String> map)
    {
      semanticTypesMap = map;
    }
	
	
	
	public static void getAST(FileInputStream inArg) throws ParseException, IOException {
		CompilationUnit cu;
		cu= JavaParser.parse(inArg);
		inArg.close();
		ASTPrinter as = new ASTPrinter();
		as.astPrint(cu);
		
	}
	
	
	public static void getDotFile(String filename) {

		File selectedFile = new File(filename);
		consoleText = new JTextArea();

		try {
			String content = (new Scanner(selectedFile)).useDelimiter("\\Z").next();
			System.out.println("content: "+content);
			consoleText.setText(content);
		} catch (FileNotFoundException | NullPointerException var6) {
			var6.printStackTrace();
		}

		runAnalysisAndMakeGraph(selectedFile);

		try {
			checkIfFolderExists();
			GraphNode.exporting = true;
			String outputFilename = selectedFile.getName().substring(0, selectedFile.getName().length() - 4);  
			FileOutputStream out = new FileOutputStream("graphFiles/" + outputFilename);
			
			DOTExporter<GraphNode, RelationshipEdge> exporter = new DOTExporter(new StringNameProvider(),
					(VertexNameProvider) null, new StringEdgeNameProvider());
			exporter.export(new OutputStreamWriter(out), hrefGraph);
			out.close();
		} catch (IOException var6) {
			var6.printStackTrace();
		}

		GraphNode.exporting = false;
	}

	private static boolean checkIfFolderExists() {
		File theDir = new File("dotOutputs");
		return !theDir.exists() && theDir.mkdir();
	}

	private static void runAnalysisAndMakeGraph(File selectedFile) {
		createGraph();
		GraphNode gn = new GraphNode(0, "Entry");
		hrefGraph.addVertex(gn);
		HashMap<String, String> map = new HashMap<String, String>();
		String prevLine = "";
		try {
			File file = new File("./input/dictionaryData.txt"); 
			
			//Code segments are checked for to compute their semantic types
			System.out.println("\n\nAST Printer starts \n\n");
			getAST(new FileInputStream(selectedFile));
			
			//Dictionary (Hashmap called semanticTypesMap is created where code segments are keys and semantic types are values
			System.out.println("\n\n\n Dictionary function Start \n\n\n");
			
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
	                else if(line.contains("class") && !line.isEmpty()){
	                	System.out.println(line);
	                	String[] classSplit = line.split("\\.",6);
	                	prevLine = classSplit[5];
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
			System.out.println("\n\n\n Dictionary function End \n\n\n");
			
			System.out.println("\nAST Printer ends\n\n");
			
			astPrinter.addFile(new FileInputStream(selectedFile),
					(DirectedGraph<GraphNode, RelationshipEdge>) hrefGraph, gn, consoleText);
			
		} catch (IOException | ParseException var2) {
			var2.printStackTrace();
		}
	}

	private static void createGraph() {
		hrefGraph = new DefaultDirectedGraph(RelationshipEdge.class);
	}
	
	
	
}

class ASTPrinter {
	
	//File to be created is initialized at given path
	File file = new File("./input/dictionaryData.txt");
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
							//write to this file
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
							
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					
				} else {
					// Same as other case except we create the file here 
					try {
						out = new FileOutputStream("./input/dictionaryData.txt", true);
						
						//Create file if file does not exist 
						file.createNewFile();
						
						System.out.println(file.getAbsolutePath());
						System.out.println("------------------------------------------------------------");
						s = "------------------------------------------------------------";
						try {
							out.write(s.getBytes());
							out.write("\n".getBytes());
							out.flush();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println(child2.getClass());
						s = String.valueOf(child2.getClass());
						try {
							out.write(s.getBytes());
							out.write("\n".getBytes());
							out.flush();

						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						System.out.println(child2.toString());
						s = String.valueOf(child2.toString());
						s = s.replaceAll("\n", "");
						try {
							out.write(s.getBytes());
							out.write("\n".getBytes());
							out.flush();

						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
					}
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
		if(child2.getClass().equals(com.github.javaparser.ast.body.MethodDeclaration.class)) {
				System.out.println("------------------------------------------------------------");
				System.out.print("Method.Type\n"+((MethodDeclaration)child2).getType().toString()+"\n");
		}
	}
	
	private static void MethodName(Node child2){
		if(child2.getClass().equals(com.github.javaparser.ast.body.MethodDeclaration.class)) {
				System.out.println("------------------------------------------------------------");
				System.out.print("Method.Name\n"+((MethodDeclaration)child2).getNameExpr()+"\n");
		}
	}
		
	private static void ClassName(Node child2){ 
		if(child2.getClass().equals(com.github.javaparser.ast.body.ClassOrInterfaceDeclaration.class)) {
				System.out.println("------------------------------------------------------------");
				System.out.print("Class.Name\n"+((ClassOrInterfaceDeclaration)child2).getNameExpr()+"\n");
		}
	}
	
	private static void ClassExtension(Node child2){ 
		if(child2.getClass().equals(com.github.javaparser.ast.body.ClassOrInterfaceDeclaration.class)) {
				System.out.println("------------------------------------------------------------");
				System.out.print("Class.ExtensionOf\n"+((ClassOrInterfaceDeclaration)child2).getExtends().toString()+"\n");
		}
	}
		
	private static void printMethodModifiers(Node child2) {
			if(ModifierSet.isPrivate(((MethodDeclaration) child2).getModifiers())){
				System.out.println("------------------------------------------------------------");
				System.out.print("Method.Modifier\nprivate\n");
			}
			if(ModifierSet.isPublic(((MethodDeclaration) child2).getModifiers())){
				System.out.println("------------------------------------------------------------");
				System.out.print("Method.Modifier\npublic\n");
			}
			if(ModifierSet.isStatic(((MethodDeclaration) child2).getModifiers())){
				System.out.println("------------------------------------------------------------");
				System.out.print("Method.Modifier\nstatic\n");    			
			}
			if(ModifierSet.isStrictfp(((MethodDeclaration) child2).getModifiers())){
				System.out.println("------------------------------------------------------------");
				System.out.print("Method.Modifier\nstrictfp\n");
			}
			if(ModifierSet.isSynchronized(((MethodDeclaration) child2).getModifiers())){
				System.out.println("------------------------------------------------------------");
				System.out.print("Method.Modifier\nsyncronized\n");
			}
			if(ModifierSet.isTransient(((MethodDeclaration) child2).getModifiers())){
				System.out.println("------------------------------------------------------------");
				System.out.print("Method.Modifier\ntransient\n");
			}
			if(ModifierSet.isVolatile(((MethodDeclaration) child2).getModifiers())){
				System.out.println("------------------------------------------------------------");
				System.out.print("Method.Modifier\nvolatile\n");
			}
	}	
	
	private static void printClassIntModifiers(Node child2) {
		if(ModifierSet.isPrivate(((ClassOrInterfaceDeclaration) child2).getModifiers())){
			System.out.println("------------------------------------------------------------");
			System.out.print("ClassOrInterface.Modifier\nprivate\n");
		}
		if(ModifierSet.isPublic(((ClassOrInterfaceDeclaration) child2).getModifiers())){
			System.out.println("------------------------------------------------------------");
			System.out.print("ClassOrInterface.Modifier\npublic\n");
		}
		if(ModifierSet.isStatic(((ClassOrInterfaceDeclaration) child2).getModifiers())){
			System.out.println("------------------------------------------------------------");
			System.out.print("ClassOrInterface.Modifier\nstatic\n");    			
		}
		if(ModifierSet.isStrictfp(((ClassOrInterfaceDeclaration) child2).getModifiers())){
			System.out.println("------------------------------------------------------------");
			System.out.print("ClassOrInterface.Modifier\nstrictfp\n");
		}
		if(ModifierSet.isSynchronized(((ClassOrInterfaceDeclaration) child2).getModifiers())){
			System.out.println("------------------------------------------------------------");
			System.out.print("ClassOrInterface.Modifier\nsyncronized\n");
		}
		if(ModifierSet.isTransient(((ClassOrInterfaceDeclaration) child2).getModifiers())){
			System.out.println("------------------------------------------------------------");
			System.out.print("ClassOrInterface.Modifier\ntransient\n");
		}
		if(ModifierSet.isVolatile(((ClassOrInterfaceDeclaration) child2).getModifiers())){
			System.out.println("------------------------------------------------------------");
			System.out.print("ClassOrInterface.Modifier\nvolatile\n");
		}
}
}


