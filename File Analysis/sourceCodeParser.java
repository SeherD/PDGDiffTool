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

        //Similarity List will be the list of 0,1s for amount of lines
        List<Integer> similarityList = new ArrayList<>();
        String[] fileNames = new String[2]; 
        int count = 0;

        for (String s: args) {

            String[] sSplit = s.split(Pattern.quote("."), 2);
            fileNames[count] = sSplit[0];
            String outputSourceFile = String.format("%s.txt", sSplit[0]);
            
            try (BufferedReader br = new BufferedReader(new FileReader(s)); PrintWriter pw = new PrintWriter(new FileWriter(outputSourceFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String lineNoSpace = line.replaceAll(" ","");
                    if(!lineNoSpace.isEmpty()){
                        pw.printf("%s%n", line);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            count++;
        }

        String stringFileName1 = fileNames[0] + ".txt";
        String stringFileName2 = fileNames[1] + ".txt";
        System.out.println(stringFileName1);
        System.out.println(stringFileName2);


        String lineSource1 = "";
        String lineSource2 = "";

        try (BufferedReader br1 = new BufferedReader(new FileReader(stringFileName1))) {
            try (BufferedReader br2 = new BufferedReader(new FileReader(stringFileName2))) {
                while(((lineSource1 = br1.readLine()) != null) && ((lineSource2 = br2.readLine()) != null))
                {
                    lineSource1 = lineSource1.replaceAll(" ","");
                    lineSource2 = lineSource2.replaceAll(" ","");

                    if(lineSource1.equals(lineSource2)){
                        
                        similarityList.add(0);
                    }
                    else if (!(lineSource1.equals(lineSource2)) || (((lineSource1 = br1.readLine()) == null) && ((lineSource2 = br2.readLine()) != null)) || (((lineSource1 = br1.readLine()) != null) && ((lineSource2 = br2.readLine()) == null))){
                        similarityList.add(1);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(similarityList);


        File myObj1 = new File(fileNames[0] + ".txt"); 
        File myObj2 = new File(fileNames[1] + ".txt"); 
        if (myObj1.delete() && myObj2.delete()) { 
            System.out.println("Deleted the file: " + myObj1.getName());
            System.out.println("Deleted the file: " + myObj2.getName());
        } 
        else {
            System.out.println("Failed to delete the files.");
        } 


    }
}