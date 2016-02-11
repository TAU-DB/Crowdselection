package utils;

import java.util.ArrayList;
import java.util.Iterator;


public class FactSetWithSupport implements Iterable<FactWithSupport> {
	
	private ArrayList<FactWithSupport> facts;
	
	public FactSetWithSupport(ArrayList<FactWithSupport> f){
		facts = f;
	}
	
	public FactSetWithSupport(){
		facts = new ArrayList<FactWithSupport>();
	}
	
	public ArrayList<FactWithSupport> getFactsWithSupport(){
		return facts;
	}

	@Override
	public Iterator<FactWithSupport> iterator() {
		return facts.iterator();
	}

	public FactSet getFacts() {
		FactSet ans = new FactSet();
		for(FactWithSupport f: facts)
		{
			ans.add(f.getFact());
		}
		return ans;
	}

	public double getSupport(Fact f) {
		for(FactWithSupport fact: facts)
		{
			if(Ontology.equalsFact(f, fact.getFact()))
				return fact.getSupprt();
		}
		return 0;
	}

	public void add(FactWithSupport factSupport) {
		facts.add(factSupport);
		
	}

	public String toSrting() {
		String ans = "";
		for(FactWithSupport fact: facts)
		{
			ans = ans + ((FactWithSupport)fact).toString()+"\n";
		}
		return ans;
	}

	public FactSetWithSupport copy() {
		FactSetWithSupport ans = new FactSetWithSupport();
		for(FactWithSupport fact: facts)
		{
			FactWithSupport copyFact = fact.copy();
			ans.add(copyFact);
		}
		return ans;
		
	}

}
