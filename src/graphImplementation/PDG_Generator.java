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
import java.util.Scanner;

public class PDG_Generator {

	private static Graph<GraphNode, RelationshipEdge> hrefGraph;
	private static PDGCore astPrinter = new PDGCore();
	private static JTextArea consoleText;
	public static void getAST(FileInputStream inArg) throws ParseException, IOException {
		CompilationUnit cu;
		cu= JavaParser.parse(inArg);
		inArg.close();
		ASTPrinter as = new ASTPrinter();
		as.astPrint(cu);
		
	}
	
	
	public static void getDotFile(String filename) {

		File selectedFile = new File(".\\" + filename);
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
		try {
			System.out.println("AST Printer starts");
			getAST(new FileInputStream(selectedFile));
			System.out.println("AST Printer ends");
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
	 void astPrint(Node child2){
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
				System.out.println("------------------------------------------------------------");
				System.out.println(child2.getClass());
				System.out.println(child2.toString());
			}
		}
		child2.getChildrenNodes().forEach(this::astPrint);
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