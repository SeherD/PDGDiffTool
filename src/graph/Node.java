package graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Node {

	private String id;
	private String sourceCode;
	private List<String> attributes;

	public Node(String id, String label) {
		this.id = id;
		this.sourceCode = label;
		this.attributes = new ArrayList<>();
	}

	public Node(String id, String label, String[] attributes) {
		this.id = id;
		this.sourceCode = label;
		this.attributes  = Arrays.asList(attributes);
	}

	public String getId() {
		return id;
	}

	public String  getSourceCode() {
		return sourceCode;
	}

	public List<String> getAttributes() {
		return attributes;
	}

	@Override
	public String toString() {
		return id;
	}

	public void addAttribute(String attr) {
		attributes.add(attr);
	}

	@Override
	public  boolean equals(Object obj) {
		if(getClass() == obj.getClass()) {
			Node other = (Node) obj;
			return sourceCode.equals(other.getSourceCode());
		}
		return false;
	}
}
