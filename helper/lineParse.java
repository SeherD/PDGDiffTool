import java.io.*;
import java.util.*; //just making sure


public class lineParse {
	public static void main(String[] args) {
		BufferedReader reader;
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
}