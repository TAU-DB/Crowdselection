package utils;

import java.util.ArrayList;

import org.apache.jena.ext.com.google.common.collect.Multimap;


public class Similarity {
	
	public static double getSimilarityWithoutSupport(SemanticUnit x, SemanticUnit y, Multimap<Term, Term> ontologyGraph){
		if(x.toSrting().equals(y.toSrting()))
			return 1;
		
		double maxIC = 0;
		ArrayList<SemanticUnit> lca = LCA.getLCAs(x, y,ontologyGraph);
		if(lca != null && lca.size() != 0){
			maxIC = InformationContent.getIC(lca.get(0));
			for(int i = 1;i<lca.size(); i++){
				double ic = InformationContent.getIC(lca.get(i));
				if(ic > maxIC)
					maxIC = ic;
			}
			
		}
		
		return maxIC;
		
		
	}
	
	public static double getSimilarityWithSupport(FactSetWithSupport x, FactSetWithSupport y, Multimap<Term, Term> ontology){
		
		
		FactSet facts1 = x.getFacts();
		FactSet facts2 = y.getFacts();
		double icsim = getSimilarityWithoutSupport(facts1, facts2, ontology);
		FactSet intersection = Ontology.getFactsIntersection(x,y);
		double ICs = 1;
		for(Fact f: intersection)
		{
			ICs *= InformationContent.getIC(f);
		}
		double ICsupsim = 1;
		for(Fact f: intersection)
		{
			ICsupsim *= InformationContent.getIC(f)*supsim(f,x,y);
		}
		return (icsim*ICsupsim)/ICs;
		
		
	}
	
	private static double supsim(Fact f, FactSetWithSupport x,FactSetWithSupport y) {
		double ans = 0;
		double support1 = x.getSupport(f);
		double support2 = y.getSupport(f);
		ans = Math.abs(support1-support2);
		ans = ans*9/10 +0.1;
		ans = -Math.log10(ans);
		return ans;
	}

	public static void main(String[] args) {
		
	    
		

		}

}
