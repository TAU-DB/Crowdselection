package utils;

import java.util.ArrayList;

import org.apache.jena.ext.com.google.common.collect.Multimap;


public class Similarity {
	
	public static double getSimilarityWithoutSupport(SemanticUnit x, SemanticUnit y, Multimap<Term, Term> ontologyGraph){
		if(x.toSrting().equals(y.toSrting()))
			return 1;
		
		
		//debug:
		
		/*if(x instanceof FactSet)
			System.out.println(((FactSet)x).toSrting());
		if(y instanceof FactSet)
			System.out.println(((FactSet)y).toSrting());*/
		
		//For two facts, get the max IC of the LCA's 
		if((x instanceof Fact && y instanceof Fact )||( x instanceof Term && y instanceof Term)){
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
		
		else if(x instanceof FactSet && y instanceof FactSet)
		{
			double maxIC = 0;
			double temp = 0;
			for(Fact f: (FactSet)x){
				for(Fact g: (FactSet)y){
					temp = getSimilarityWithoutSupport(f,g,ontologyGraph);
					if(temp > maxIC)
						maxIC = temp;
				}
			}
			return maxIC;
		}
		return 0;
		
		
	}
	
	public static double getSimilarityWithSupport(FactSetWithSupport x, FactSetWithSupport y, Multimap<Term, Term> ontology){
		//System.out.println("*******Debug*********");
		//System.out.println("*******SimilarityWithoutSupport*********");
		//if(x instanceof FactSetWithSupport)
			//System.out.println(((FactSetWithSupport)x).toSrting());
		//if(y instanceof FactSetWithSupport)
			//System.out.println(((FactSetWithSupport)y).toSrting());
		
		
		FactSet facts1 = x.getFacts();
		FactSet facts2 = y.getFacts();
		//double icsim = getSimilarityWithoutSupport(facts1, facts2, ontology);
		FactSet intersection = Ontology.getFactsIntersection(x,y);
		//System.out.println("*******Intersection*********");
		//System.out.println(((FactSet)intersection).toSrting());
		
		
		double ICs = 0;
		for(Fact f: intersection)
		{
			ICs += InformationContent.getIC(f);
		}
		double ICsupsim = 0;
		for(Fact f: intersection)
		{
			ICsupsim += InformationContent.getIC(f)*supsim(f,x,y);
		}
		return (ICsupsim)/ICs;
		
		
	}
	
	private static double supsim(Fact f, FactSetWithSupport x,FactSetWithSupport y) {
		double ans = 0;
		double support1 = x.getSupport(f);
		double support2 = y.getSupport(f);
		
		
		//test
		//while(support1 < 0.1 || support2 < 0.1){
			support1 = Math.sqrt(support1);
			support1 = Math.sqrt(support1);
			support2 = Math.sqrt(support2);
			support2 = Math.sqrt(support2);
	//	}
		
		ans = Math.abs(support1-support2);
		ans = ans*9/10 +0.1;
		ans = -Math.log10(ans);
		return ans;
	}

	public static void main(String[] args) {
		
	    
		

		}

}
