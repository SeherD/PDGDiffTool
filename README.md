# PDGDiffTool - SENG 541 A5

## Introduction
In this project, we are developing a software application that can compute the matches and differences between two java methods, in terms of the program dependence graphs (PDG) of those methods. We are going to calculate the PDGs and then convert the data into a programmatic form. Then we need to decide on a similarity measurement between nodes and edges. A working software computes a good matching of nodes and edges within the two input PDGs. The matching will show the semantic similarities and differences between the two PDGs. Lastly, data must be presented in a reasonable way to show the results of the software. 

## Environment Setup
1. Eclipse IDE
2. Java 13 SDK

## Input
The test files included in our PDG diff tool are created to test the ability to distinguish files with minor change, major change, no change or completely different. The PDG generator we used for our diff tool does not recognise switch clauses in Java properly, so the test files we carefully created does not contain any. Also, any empty line or line only containing curly brackets only will cause disruption for the line number in the PDG generator, so we create those test files to make sure we can test our diff tool without triggering these bugs.


## Output


## Team Member
1. Haotian Chen
2. Seher Dawar
3. Lucas Johannson
4. Prit Patel
5. Xiang Yu Shi


## Screen Shots

!["PDG tool"](https://github.com/SeherD/PDGDiffTool/blob/main/src/resources/Screen%20Shot%202022-04-14%20at%201.21.16%20PM.png?raw=true)
