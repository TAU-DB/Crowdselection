package utils;

import java.util.ArrayList;
import java.util.Iterator;

public class FactSet implements SemanticUnit, Iterable<Fact>{
	
	ArrayList<Fact> factSet;
	
	public FactSet(ArrayList<Fact> facts){
		factSet = facts;
	}
	
	
	public FactSet() {
		factSet = new ArrayList<Fact>();
	}


	@Override
	public String toSrting() {
		String ans = "";
		for(int i =0; i<factSet.size();i++)
			ans += factSet.get(i).toSrting()+"\n";
	
		return ans;

	}


	@Override
	public Iterator<Fact> iterator() {
		return factSet.iterator();
	}


	public void add(Fact fact) {
		factSet.add(fact);
		
	}
}
