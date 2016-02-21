package utils;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.jena.ext.com.google.common.collect.Multimap;


public class Similarity {
	
	public static double getSimilarityWithoutSupport(SemanticUnit x, SemanticUnit y, Multimap<Term, Term> ontologyGraph){
		if(x.toSrting().equals(y.toSrting()))
			return 1;
		
		//For two facts, get the max IC of the LCA's 
		if((x instanceof Fact && y instanceof Fact )/*||( x instanceof Term && y instanceof Term)*/){
			return getSimilarityWithoutSupportFacts(x, y, ontologyGraph);
		}
		
		else if(x instanceof FactSet && y instanceof FactSet)
		{
			return getSimilarityWithoutSupportFactSet(x, y, ontologyGraph);
		}
		return 0;
		
		
	}

	private static double getSimilarityWithoutSupportFactSet(SemanticUnit x,SemanticUnit y, Multimap<Term, Term> ontologyGraph) {
		double maxIC = 0;
		
		System.out.println("X:");
		System.out.println(((FactSet)x).toSrting());
		
		System.out.println("Y:");
		System.out.println(((FactSet)y).toSrting());
		
		ArrayList<SemanticUnit> lca = LCA.getLCAs(x, y,ontologyGraph);
		FactSet factset = new FactSet();
		for(SemanticUnit u: lca)
			factset.add((Fact)u);
		
		System.out.println("intersection:");
		System.out.println(((FactSet)factset).toSrting());
		
		SemanticUnit AR1 = getARFact(x); 
		
		SemanticUnit AR2 = getARFact(y); 
		double ar = 0;
		
		if(AR1 != null && AR2 != null)
			ar = ARIC(AR1,AR2);
		
		SemanticUnit gold1 = getGoldFact(x); 
		
		SemanticUnit gold2 = getGoldFact(y); 
		double gold = 0;
		
		if(gold1 != null && gold2 != null)
			gold = goldIC(gold1,gold2);
		
		SemanticUnit bronze1 = getBronzeFact(x); 
		
		SemanticUnit bronze2 = getBronzeFact(y); 
		double bronze = 0;
		
		if(bronze1 != null && bronze2 != null)
			bronze = bronzeIC(bronze1,bronze2);
		
		SemanticUnit silver1 = getSilverFact(x); 
		
		SemanticUnit silver2 = getSilverFact(y); 
		double silver = 0;
		
		if(silver1 != null && silver2 != null)
			silver = silverIC(silver1,silver2);
		
		SemanticUnit h_index1 = getHIndexFact(x); 
		
		SemanticUnit h_index2 = getHIndexFact(y); 
		double h_index = 0;
		
		if(h_index1 != null && h_index2 != null)
			h_index = hIndexIC(h_index1,h_index2);
		
		SemanticUnit pc1 = getPCFact(x); 
		
		SemanticUnit pc2 = getPCFact(y); 
		double pc = 0;
		
		if(pc1 != null && pc2 != null)
			pc =  PCIC(pc1,pc2);
		
		//AMiner
		if((h_index1 != null && h_index2 != null) || (pc1 != null && pc2 != null))
			maxIC = InformationContent.avgIC(factset,h_index,pc);
		else
			maxIC = InformationContent.avgIC(factset,bronze,silver,gold,ar); //SO
			
		
		return maxIC;
	}

	private static SemanticUnit getPCFact(SemanticUnit x) {
		SemanticUnit pc1 = null;
		
		for(Fact u1: ((FactSet)x).factSet){
			String pro1 = ( (Term) ((Fact) u1).getProperty()).toSrting();
			if(pro1.endsWith("pc"))
					pc1 = u1;	
		}
		return pc1;
	}

	private static SemanticUnit getARFact(SemanticUnit x) {
		SemanticUnit h_index1 = null;
		
		for(Fact u1: ((FactSet)x).factSet){
			String pro1 = ( (Term) ((Fact) u1).getProperty()).toSrting();
			if(pro1.endsWith("accept_rate"))
					h_index1 = u1;	
		}
		return h_index1;
	}
	
	private static SemanticUnit getGoldFact(SemanticUnit x) {
		SemanticUnit h_index1 = null;
		
		for(Fact u1: ((FactSet)x).factSet){
			String pro1 = ( (Term) ((Fact) u1).getProperty()).toSrting();
			if(pro1.endsWith("gold"))
					h_index1 = u1;	
		}
		return h_index1;
	}
	
	private static SemanticUnit getBronzeFact(SemanticUnit x) {
		SemanticUnit h_index1 = null;
		
		for(Fact u1: ((FactSet)x).factSet){
			String pro1 = ( (Term) ((Fact) u1).getProperty()).toSrting();
			if(pro1.endsWith("bronze"))
					h_index1 = u1;	
		}
		return h_index1;
	}
	
	private static SemanticUnit getSilverFact(SemanticUnit x) {
		SemanticUnit h_index1 = null;
		
		for(Fact u1: ((FactSet)x).factSet){
			String pro1 = ( (Term) ((Fact) u1).getProperty()).toSrting();
			if(pro1.endsWith("silver"))
					h_index1 = u1;	
		}
		return h_index1;
	}
	
	private static SemanticUnit getHIndexFact(SemanticUnit x) {
		SemanticUnit h_index1 = null;
		
		for(Fact u1: ((FactSet)x).factSet){
			String pro1 = ( (Term) ((Fact) u1).getProperty()).toSrting();
			if(pro1.endsWith("h_index"))
					h_index1 = u1;	
		}
		return h_index1;
	}

	private static double getSimilarityWithoutSupportFacts(SemanticUnit x,
			SemanticUnit y, Multimap<Term, Term> ontologyGraph) {
		double maxIC = 0;
	    String pro1 = ( (Term) ((Fact) y).getProperty()).toSrting();
	    String pro2 = ( (Term) ((Fact) x).getProperty()).toSrting();
		if(pro1.endsWith("h_index") && pro2.endsWith("h_index"))
			 maxIC = hIndexIC(x,y);
		else if(pro1.endsWith("pc") && pro2.endsWith("pc"))
			 maxIC = PCIC(x,y);
		else if (!pro1.endsWith("pc") && !pro2.endsWith("pc") && !pro1.endsWith("h_index") && !pro2.endsWith("h_index")){
			ArrayList<SemanticUnit> lca = LCA.getLCAs(x, y,ontologyGraph);
			if(lca != null && lca.size() != 0){
				maxIC = InformationContent.getIC(lca.get(0));
				for(int i = 1;i<lca.size(); i++){
					double ic = InformationContent.getIC(lca.get(i));
					if(ic > maxIC){
						maxIC = ic;
					//k = i;
					}
				}
			
			}
		}
		
		
		
		return maxIC;
	}
	
	private static double PCIC(SemanticUnit x, SemanticUnit y) {
		Fact f1 = (Fact)x;
		Fact f2 = (Fact)y;
		
		int pc1 = Integer.parseInt(f1.getObject().toSrting());
		int pc2 = Integer.parseInt(f2.getObject().toSrting());
		
		double IC = InformationContent.getICPC(pc1, pc2);
		return IC;
	}

	private static double hIndexIC(SemanticUnit x, SemanticUnit y) {
		Fact f1 = (Fact)x;
		Fact f2 = (Fact)y;
		
		int h_index1 = Integer.parseInt(f1.getObject().toSrting());
		int h_index2 = Integer.parseInt(f2.getObject().toSrting());
		
		double IC = InformationContent.getICHIndex(h_index1, h_index2);
		return IC;
	}
	
	
	private static double ARIC(SemanticUnit x, SemanticUnit y) {
		Fact f1 = (Fact)x;
		Fact f2 = (Fact)y;
		
		int ar1 = Integer.parseInt(f1.getObject().toSrting());
		int ar2 = Integer.parseInt(f2.getObject().toSrting());
		
		double IC = InformationContent.getICAR(ar1, ar2);
		return IC;
	}
	
	private static double goldIC(SemanticUnit x, SemanticUnit y) {
		Fact f1 = (Fact)x;
		Fact f2 = (Fact)y;
		
		int gold1 = Integer.parseInt(f1.getObject().toSrting());
		int gold2 = Integer.parseInt(f2.getObject().toSrting());
		
		double IC = InformationContent.getICGold(gold1, gold2);
		return IC;
	}
	
	private static double bronzeIC(SemanticUnit x, SemanticUnit y) {
		Fact f1 = (Fact)x;
		Fact f2 = (Fact)y;
		
		int gold1 = Integer.parseInt(f1.getObject().toSrting());
		int gold2 = Integer.parseInt(f2.getObject().toSrting());
		
		double IC = InformationContent.getICBronze(gold1, gold2);
		return IC;
	}
	
	private static double silverIC(SemanticUnit x, SemanticUnit y) {
		Fact f1 = (Fact)x;
		Fact f2 = (Fact)y;
		
		int gold1 = Integer.parseInt(f1.getObject().toSrting());
		int gold2 = Integer.parseInt(f2.getObject().toSrting());
		
		double IC = InformationContent.getICSilver(gold1, gold2);
		return IC;
	}
	
	public static double getSimilarityWithSupport(FactSetWithSupport x, FactSetWithSupport y, Multimap<Term, Term> ontology){
		
		FactSetWithSupport facts1 = new FactSetWithSupport();
		FactSetWithSupport facts2 = new FactSetWithSupport();
		
	
		FactSet intersection = Ontology.getFactsIntersection(x,y,facts1,facts2);
	
		System.out.println("X (history):\n\n");
		if(x instanceof FactSetWithSupport)
			System.out.println(((FactSetWithSupport)x).toSrting());
		System.out.println("Y (history):\n\n");
		if(y instanceof FactSetWithSupport)
			System.out.println(((FactSetWithSupport)y).toSrting());
		System.out.println("*******Intersection*********");
		System.out.println(((FactSet)intersection).toSrting());
		if(x.getFacts().factSet.size()<5)
			return 0;
		if(y.getFacts().factSet.size()<5)
			return 0;
		
		double ICs = 0;
		for(Fact f: intersection)
		{
			ICs += InformationContent.getIC(f);
		}
		double ICsupsim = 0;
		for(Fact f: intersection)
		{
			ICsupsim += InformationContent.getIC(f)*supsim(f,facts1,facts2);
		}
		double ans = getSimilarityWithoutSupport(x.getFacts(),y.getFacts(),ontology);
		ans = ans*((ICsupsim)/ICs);
		return ans;
		
		
	}
	
	private static double supsim(Fact f, FactSetWithSupport x,FactSetWithSupport y) {
		double ans = 0;
		double support1 = x.getSupport(f);
		double support2 = y.getSupport(f);
		
		
			support1 = Math.sqrt(support1);
			support1 = Math.sqrt(support1);
			support2 = Math.sqrt(support2);
			support2 = Math.sqrt(support2);

		
		ans = Math.abs(support1-support2);
		ans = ans*9/10 +0.1;
		ans = -Math.log10(ans);
		return ans;
	}

	public static void main(String[] args) {
		
	    
		

		}

	public static double getSimilarityWithSupportNoUser(FactSetWithSupport queryTran, FactSetWithSupport userTrans,Multimap<Term, Term> ontologyGraph) {
	
		FactSet intersection = Ontology.getFactsIntersection(queryTran,userTrans);
		/*System.out.println("*******user*********");
		System.out.println(userTrans.toSrting());
		System.out.println("*******query*********");
		System.out.println(queryTran.toSrting());*/
	

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
			ICsupsim += InformationContent.getIC(f)*supsim(f,queryTran,userTrans);
		}
		return (ICsupsim)/ICs;
	
	}

}
