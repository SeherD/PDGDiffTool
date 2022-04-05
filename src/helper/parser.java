package helper;

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

