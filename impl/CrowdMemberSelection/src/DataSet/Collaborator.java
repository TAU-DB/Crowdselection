package DataSet;

public class Collaborator {
	
	public String name;
	public int num;
	
	public Collaborator(String name, int i){
		this.name = name;
		num = i;
	}
	
	public String toString(){
		return name +"\t" + num;
	}
	
	

}
