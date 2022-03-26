package pdg_wrapper;

import com.github.javaparser.ParseException;
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

public class pdg_generator {
    private static Graph<GraphNode, RelationshipEdge> hrefGraph;
    private static PDGCore astPrinter = new PDGCore();
    private static JTextArea consoleText;

    public static void getDotFile(String filename) {

        File selectedFile = new File(".\\" + filename);
        consoleText = new JTextArea();

        try {
            String content = (new Scanner(selectedFile)).useDelimiter("\\Z").next();
            consoleText.setText(content);
        } catch (FileNotFoundException |  NullPointerException var6) {
            var6.printStackTrace();
        }

        runAnalysisAndMakeGraph(selectedFile);

        try {
            checkIfFolderExists();
            GraphNode.exporting = true;

            FileOutputStream out = new FileOutputStream("dotOutputs/" + selectedFile.getName() + ".dot");
            DOTExporter<GraphNode, RelationshipEdge> exporter = new DOTExporter(new StringNameProvider(), (VertexNameProvider)null, new StringEdgeNameProvider());
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
            astPrinter.addFile(new FileInputStream(selectedFile), (DirectedGraph<GraphNode, RelationshipEdge>) hrefGraph, gn, consoleText);
        } catch (IOException | ParseException var2) {
            var2.printStackTrace();
        }
    }

    private static void createGraph() {
        hrefGraph = new DefaultDirectedGraph(RelationshipEdge.class);
    }
}
