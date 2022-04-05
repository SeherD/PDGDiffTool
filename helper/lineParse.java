
import java.io.*;
import java.util.*; //just making sure


public class lineParse {

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