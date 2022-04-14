package graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Node {

	private String id;
	private String sourceCode;
	private String semanticType;
	private ArrayList<String> notNumber = new ArrayList<String>( Arrays.asList("ImportDeclaration", "Class.Name", "Method.Name", "Other"));

	public Node(String id, String label,String type) {
		this.id = id;
		this.sourceCode = label;
		this.semanticType = type ;
	}

	public Node(String id, String label, String[] attributes) {
		this.id = id;
		this.sourceCode = label;
		
	}

	public String getId() {
		return id;
	}

	public String  getSourceCode() {
		return sourceCode;
	}

	//returns numbers in the string
	private String getNumber(String code) {
		 return code.replaceAll("[^0-9]", "");
	}
	

	@Override
	public String toString() {
		return "Node ID =" + id + ",Source Code =" + sourceCode + ",Syntactic Type =" + semanticType ;
	}
	//gets first line of code blocks
	String getFirstLine(String code){
		String[] newCode = code.split("\n");
		
		return newCode[0];
	}
	
	
	//Is used as the basis for determining if two nodes are equal
	// first it checks for syntactic types 
	//if those are equal it checks whether the source code has any numerical literals which are compared against each other
	//finally if there are any string literals in the sourcecode, those are compared against each other also.
	@Override
	public  boolean equals(Object obj) {
		if(getClass() == obj.getClass()) {
			Node other = (Node) obj;
			if(semanticType.equals(other.getSemanticType())){
				if(!notNumber.contains(semanticType)){
					if (!getNumber(getFirstLine(sourceCode)).equals("") && !getNumber(getFirstLine(other.getSourceCode())).equals("")) {
						if(getNumber(getFirstLine(sourceCode)).equals(getNumber(getFirstLine((other.getSourceCode()))))){
							return true;
						}
						else {
							return false;
						}
					}
					else if(getLiteral(getFirstLine(sourceCode))!=null && getLiteral(getFirstLine(other.getSourceCode()))!=null ) {
						if(getLiteral(getFirstLine(sourceCode)).equals(getLiteral(getFirstLine(other.getSourceCode())))) {
							return true;
						}
						else {
							return false;
						}
						
					}
					else {
						return true;
					}
				}
				else {
					return true;
				}
				
			}
		}
		return false;
	}
//gets quoted text
	String getLiteral(String code) {
		String literal = "";
		Pattern p = Pattern.compile("\"([^\"]*)\"");
		Matcher m = p.matcher(code);
		while (m.find()) {
		  literal = m.group(1);
		}
		
		return literal;
	}
	public String getSemanticType() {
		return semanticType;
	}

	public void setSemanticType(String semanticType) {
		this.semanticType = semanticType;
	}
}
