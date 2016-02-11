package utils;


public class Term implements SemanticUnit{
	
	private String term;
	

	public Term(String subjectName) {
		this.term = subjectName;
	}


	@Override
	public String toSrting() {
		return term;
	}


	public Term copy() {
		return new Term(new String(term));
	}


	

}
