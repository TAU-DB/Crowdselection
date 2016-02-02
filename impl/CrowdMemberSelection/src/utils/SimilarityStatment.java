package utils;

public class SimilarityStatment {
	private String src;
	private String dst;
	private String op;
	private double sim;
	
	public SimilarityStatment(String src, String dst, String op, double sim2){
		this.src = src;
		this.dst = dst;
		this.op = op;
		this.sim = sim2;
	}
	
	public String getSrc(){
		return src;
	}
	
	public String getDest(){
		return dst;
	}
	
	
	public String getOp(){
		return op;
	}
	
	public double getTH(){
		return sim;
	}
	
	public String toString(){
		return src+" "+dst+" "+op+" "+sim;
	}

}
