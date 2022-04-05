import java.io.*;
import java.util.*; //just making sure


public class lineParse {
	public static void main(String[] args) {

  public static class parser {
    //create hash table/dictionary 
    Hashtable<Integer, String> lineContent = new Hashtable<Integer, String>();

    public parser(){ //constructor to put the entry in the 0th index
      lineContent.put(0, "Entry");
    }
    //init line 1
    int lineNo = 1;
    //recursive method
    void recursiveLineFinder (String incomingBlock){
      // adding the block to dictionary
      lineContent.put(lineNo, incomingBlock);
      //step into block 
      lineNo ++;
      // iterating over contents of block
      Scanner reader = new Scanner(incomingBlock); //Incoming block -> everything from loop in main
      reader.nextLine();
      //loop over block
      while (reader.hasNextLine()) {
        String line = reader.nextLine();

        // CASE 1: regular line
        if (line.contains(";")){
          lineContent.put(lineNo, line);
          lineNo ++;
        }

        // CASE 2: inner block found
        else if(line.contains("{")) {
            String block = line+"\n";
            int counter = 1;

            do {
              line = reader.nextLine();
              if(line.contains("{")){
                counter ++;
              }
              if(line.contains("}")){
                counter --;
              }
              block = block.concat(line + "\n");
            }
            while(counter != 0);
            //recall method
            recursiveLineFinder(block);
        }

      }
      reader.close();

    }
  }

  static void myMethod() {
    parser p = new lineParse.parser();

		BufferedReader reader;
    String fileAll = "";
    String file = "../input/ifTest.java";


		try {
			reader = new BufferedReader(new FileReader(
					"../input/ifTest.java"));
			String line = reader.readLine();

      //create dictionary to store line data 
      Hashtable<Integer, String> lineContent = new Hashtable<Integer, String>();
      //init lineNo variable to create keys
      Integer lineNo = 0;
      //loop while file not done
			while (line != null) {
        //remove white space for dictionary values 
        String line1 = line.replaceAll("\\s+","");
        //add line with no white space to the dict with key being lineNo
        lineContent.put(lineNo, line1);
        //print line data for review
				System.out.println(line);
				// increase the line number
        lineNo ++; 
        // read next line
				line = reader.readLine();
			}
      //close reader
			reader.close();
      //todo export data for use in other files
      System.out.println("content of dict: "+ lineContent);
      //catch io errors 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
        reader = new BufferedReader(new FileReader(file));
        String line = reader.readLine();
        //make whole file into string
        while (line != null) {
          fileAll = fileAll.concat(line + "\n");
          line = reader.readLine();
        }
        //call recursive method with string file
        p.recursiveLineFinder(fileAll);
        reader.close();
        //print out contents of dictionary
        for (int i = 0; i < p.lineContent.size(); i++) {
          System.out.println("Line " + i + ": \n" + p.lineContent.get(i));
        }
        return;
      }
      catch (IOException e) {
        e.printStackTrace();
      }

  }


	public static void main(String[] args) {

      myMethod();


  }
} 
 51  
helper/parser.java
@@ -0,0 +1,51 @@

import java.io.*;
import java.util.*; //just making sure

public class parser{


  Hashtable<Integer, String> lineContent = new Hashtable<Integer, String>();
  int lineNo = 0;

  void recursiveLineFinder (String str){
    lineContent.put(lineNo, str);
    lineNo ++;
    System.out.println("coming in: " + str);

    BufferedReader reader = new BufferedReader(new StringReader(
					str));
    try{
      String line = reader.readLine();

      if(line.contains("{")) { // we have entered a block  { { }}
          String block = "";
          int counter = 1;
          //block += line;
          //System.out.println("Blocks" + line);
          while(counter != 0){ //while there is no closing bracket

            line = reader.readLine();
            if(line.contains("{")){
              counter ++;
            }
            if(line.contains("}")){
              counter --;
            }
            block = block.concat(line);
          }
          recursiveLineFinder(block);



      }
      reader.close();
      }catch (IOException e) {
			e.printStackTrace();
		  }



  }
}
