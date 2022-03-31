import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class sourceCodeParser {
    public static void main(String[] args) {
        sourceCodeParser parser = new sourceCodeParser();
        parser.fileParser(args);
    }

    public List<Integer> fileParser(String[] args){
        //Similarity List will be the list of 0,1s for amount of lines
        List<Integer> similarityList = new ArrayList<>();
        String[] fileNames = new String[2]; 
        int countNumArgs = 0;
        int countNumLines = 0;
        int maxNumLines = 0;

        //For all files in the command line given as input
        for (String s: args) {

            //Creating a text file for string analysis for the input files
            String[] sSplit = s.split(Pattern.quote("."), 2);
            fileNames[countNumArgs] = sSplit[0];
            String outputSourceFile = String.format("%s.txt", sSplit[0]);
            
            countNumLines = 0;

            //Try block to feed into the newly created txt files for string analysis
            //Keeping track of most number of lines encountered so 1s can represent num line changes
            try (BufferedReader br = new BufferedReader(new FileReader(s)); PrintWriter pw = new PrintWriter(new FileWriter(outputSourceFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String lineNoSpace = line.replaceAll(" ","");
                    if(!lineNoSpace.isEmpty()){
                        pw.printf("%s%n", line);
                        countNumLines++;
                        if(countNumLines > maxNumLines){
                            maxNumLines = countNumLines;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            countNumArgs++;
        }


        String stringFileName1 = fileNames[0] + ".txt";
        String stringFileName2 = fileNames[1] + ".txt";
        System.out.println("Txt files created with input files: " + stringFileName1 + " " + stringFileName2);

        String lineSource1 = "";
        String lineSource2 = "";

        //try block to read created txt files of source code
        try (BufferedReader br1 = new BufferedReader(new FileReader(stringFileName1))) {
            try (BufferedReader br2 = new BufferedReader(new FileReader(stringFileName2))) {
                //Interate as many times as max number of lines of code between the 2 input files
                while(maxNumLines > 0)
                {
                    lineSource1 = br1.readLine(); 
                    lineSource2 = br2.readLine(); 
                    
                    //remove spaces
                    if(lineSource1 != null){
                        lineSource1 = lineSource1.replaceAll(" ","");
                    }
                    if(lineSource2 != null){
                        lineSource2 = lineSource2.replaceAll(" ","");
                    }

                    //if both lines being comapred are not null
                    if(((lineSource2 != null) && (lineSource2 != null))){
                        //Represent no change
                        if(lineSource1.equals(lineSource2)) {
                            similarityList.add(0);
                        }
                        //Represent change
                        else if (!(lineSource1.equals(lineSource2))){
                            similarityList.add(1);
                        }
                    }
                    //Represent one line is null and other is not null
                    else {
                        similarityList.add(1);
                    }

                    maxNumLines--;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(similarityList);

        //Try to automatically delete created txt files of source code
        File myObj1 = new File(fileNames[0] + ".txt"); 
        File myObj2 = new File(fileNames[1] + ".txt"); 
        if (myObj1.delete() && myObj2.delete()) { 
            System.out.println("Deleted the file: " + myObj1.getName());
            System.out.println("Deleted the file: " + myObj2.getName());
        } 
        else {
            System.out.println("Failed to delete the files.");
        } 

        //return the similarity list 
        return similarityList;
    }
}