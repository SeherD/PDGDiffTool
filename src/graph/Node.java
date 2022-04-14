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

	
	private String getNumber(String code) {
		 return code.replaceAll("[^0-9]", "");
	}
	

	@Override
	public String toString() {
		return "Node [id=" + id + ", sourceCode=" + sourceCode + ", semanticType=" + semanticType + "]";
	}
	
	String getFirstLine(String code){
		String[] newCode = code.split("\n");
		
		return newCode[0];
	}
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
