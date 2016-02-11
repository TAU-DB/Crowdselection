package utils;

public class Fact implements SemanticUnit{
	
	private Term subject;
	private Term property;
	private Term object;
	
	public Fact(Term sub, Term pro, Term obj){
		subject = sub;
		property = pro;
		object = obj;
		
	}

	@Override
	public String toSrting() {
		String s =  subject.toSrting()+" "+property.toSrting()+" "+object.toSrting();
		return s;
	}
	
	public Term getSubject(){
		return subject;
	}
	
	public Term getProperty(){
		return property;
	}
	
	public Term getObject(){
		return object;
	}

	public Fact copy() {
		Fact ans = new Fact(subject.copy(),property.copy(),object.copy());
		return ans;
	}

	

}
