package utils;

public class FactWithSupport {

	private double support;
	private Fact fact;
	
	public FactWithSupport(double supp, Fact fact){
		support = supp;
		this.fact = fact;
	}
	
	public double getSupprt(){
		return support;
	}
	
	public Fact getFact(){
		return fact;
	}
	
	public String toString(){
		return fact.toSrting()+" "+support;
	}

	public FactWithSupport copy() {
		FactWithSupport f = new FactWithSupport(support,fact.copy());
		return f;
	}
}
