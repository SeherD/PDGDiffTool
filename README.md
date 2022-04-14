# PDGDiffTool - SENG 541 A5

## Introduction
In this project, we are developing a software application that can compute the matches and differences between two java methods, in terms of the program dependence graphs (PDG) of those methods. We are going to calculate the PDGs and then convert the data into a programmatic form. Then we need to decide on a similarity measurement between nodes and edges. A working software computes a good matching of nodes and edges within the two input PDGs. The matching will show the semantic similarities and differences between the two PDGs. Lastly, data must be presented in a reasonable way to show the results of the software. 

## Environment Setup
1. Eclipse IDE
2. Java 13 SDK

## Input
The test files included in our PDG diff tool are created to test the ability to distinguish files with minor change, major change, no change or completely different. The PDG generator we used for our diff tool does not recognise switch clauses in Java properly, so the test files we carefully created does not contain any. Also, any empty line or line only containing curly brackets only will cause disruption for the line number in the PDG generator, so we create those test files to make sure we can test our diff tool without triggering these bugs.


## Output
The output of the project is piped to text files with the name format “file1 + file2 + output.txt”.
The Normalized Graph Edit Distance (GED) is used to classify the files into Semantically Identical, Semantically Similar and Semantically Different. If the Normalized GED is 0 then the files are identical, if it's between 0 and 1, then the files are similar and if it's greater than 1 then they are different.
The output for Semantically Similar files can be interpreted using the following guidelines :
1. If the output is ε -> Node, then a node was added to the graph, which means the code represented by the node was inserted into the graph.
2. If the output is Node ->ε, then a node was deleted from the graph, which means the code represented by the node was deleted from the graph.
3. If a Node is replaced by a Node that has the same ID, Source Code and Syntactic Type, then the edges associated with that node were changed. 
4. If a Node is replaced by a Node that looks the same but the ID is different, that means the node was moved in the graph.
5. If the Node is replaced by an entirely different node, then that is the closest match possible to the node that was replaced. 


## Team Member
1. Haotian Chen
2. Seher Dawar
3. Lucas Johannson
4. Prit Patel
5. Xiang Yu Shi


## Screen Shots

!["PDG tool"](https://github.com/SeherD/PDGDiffTool/blob/main/src/resources/Screen%20Shot%202022-04-14%20at%201.21.16%20PM.png?raw=true)
