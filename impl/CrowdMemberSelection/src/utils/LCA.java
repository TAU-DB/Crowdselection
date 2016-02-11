package utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.jena.ext.com.google.common.collect.Multimap;

/**
 * 
 * @author user
 * Find the LCA of two nodes representing terms.
 * Assume the ontology is represented as a DAG
 *
 */
public class LCA {
	

	public static void main(String[] args) throws IOException {
		
		Ontology.buildOntologyGraph();
		Multimap<Term, Term> ontologyGraph = Ontology.getGraph();
		Term t1 = Ontology.getTerm("Sport");
		Term t2 = Ontology.getTerm("Surfing");
		boolean ans = isMoreGeneralThan(t1,t2,ontologyGraph);
		System.out.println(ans);
		
		/*Fact f1 = new Fact(new Term("Bill"),new Term("livesIn"), new Term("Sydney"));
		Fact f2 = new Fact(new Term("Carol"),new Term("livesIn"), new Term("Sydney"));*/
		Fact f1 = new Fact(Ontology.getTerm("Bill"),Ontology.getTerm("livesIn"), Ontology.getTerm("Sydney"));
		Fact f2 = new Fact(Ontology.getTerm("Bill"),Ontology.getTerm("livesIn"), Ontology.getTerm("Place"));
		
		boolean ans1 = isMoreGeneralThan(f2,f1,ontologyGraph);
		System.out.println(ans1);

		}

	/**
	 * 
	 * @param mapping: the ontology represented as an adjacency list
	 * @param (x - single node
	 * @return - all of the given nodes ancestors
	 */
	private static Set<Term> BFS(Multimap<Term, Term> ontologyGraph, SemanticUnit x) {
		
		x = Ontology.getTerm(((Term)x).toSrting());
		//System.out.println(((Term)x).toSrting());
	    Queue<Term> queue = new LinkedList<Term>();
	    Set<Term> result = new LinkedHashSet<Term>();
	    queue.add((Term)x);
	    while(!queue.isEmpty()) {
	    	Term current = queue.remove();
	    	current = Ontology.getTerm(current.toSrting());
	    	// System.out.println(((Term)s).toSrting());
	        result.add(current);
	        if(Ontology.getValues(current) != null)
	           for(Term s: Ontology.getValues(current)) {
	        	  
	                queue.add(s);
	            }
	    }
	    return result;
	}
	/**
	 * 
	 * @param x - the first node
	 * @param y - the second node 
	 * @param ontologyGraph - the ontology represented as an adjacency list
	 * @return the LCA of node1 and node 2
	 */

	public static ArrayList<SemanticUnit> getLCAs(Term x, Term y, Multimap<Term, Term> ontologyGraph) {
		
		Set<Term> list1 = BFS(ontologyGraph, (Term) x);      
		Set<Term> list2 = BFS(ontologyGraph, (Term) y);
		ArrayList<SemanticUnit> ans = mergeLCA(list1, list2);
		return ans;
	}
	/**
	 * 
	 * @param x - the first fact
	 * @param y - the second fact 
	 * @param ontologyGraph - the ontology represented as an adjacency list
	 * @return the LCA of node1 and node 2
	 */

	public static ArrayList<SemanticUnit> getLCAs(Fact x, Fact y, Multimap<Term, Term> ontologyGraph) {
		
	
		Set<Term> listSubject1 = BFS(ontologyGraph, (Term) x.getSubject()); 
		Set<Term> listSubject2 = BFS(ontologyGraph, (Term) y.getSubject());
		
		
		Set<Term> listProperty1 = BFS(ontologyGraph, (Term) x.getProperty());
		
		
		Set<Term> listProperty2 = BFS(ontologyGraph, (Term) y.getProperty());
		
		
		Set<Term> listObject1 = BFS(ontologyGraph, (Term) x.getObject());
	
		
		Set<Term> listObject2 = BFS(ontologyGraph, (Term) y.getObject());
		
        //get all subject LCA
		ArrayList <SemanticUnit> subjects = mergeLCA(listSubject1, listSubject2);
		
		//get all property LCA
		ArrayList <SemanticUnit> properties = mergeLCA(listProperty1, listProperty2);
		
	
		//get all object LCA
		ArrayList <SemanticUnit> objects = mergeLCA(listObject1, listObject2);
		
		
		ArrayList<SemanticUnit> ans = new ArrayList<SemanticUnit>();
		if(subjects.size()>0 && properties.size()>0 && objects.size()>0)
			ans = factLCA(subjects, properties, objects);
	
	    ans = removeRedundantFacts(ans, ontologyGraph);
	    
		
		return ans;
	}
	
	private static ArrayList<SemanticUnit> removeRedundantFacts(ArrayList<SemanticUnit> ans,Multimap<Term, Term> ontologyGraph) {
		
		Iterator<SemanticUnit> iter = ans.iterator();
		while(iter.hasNext())
		{
			Iterator<SemanticUnit> iter2 = ans.iterator();
			Fact f = (Fact) iter.next();
			boolean flag = false;
			while(iter2.hasNext())
			{
				Fact f2 = (Fact) iter2.next();
				if (isMoreGeneralThan(f, f2, ontologyGraph) && ! Ontology.equalsFact(f,f2))
					flag = true;
					
			}
			if (flag)
				iter.remove();
		}
		return ans;
		
		
	}

	private static boolean isMoreGeneralThan(Fact f, Fact f2,Multimap<Term, Term> ontologyGraph) {
		Term sub1 = f.getSubject();
		Term sub2 = f2.getSubject();
		Term pro1 = f.getProperty();
		Term pro2 = f2.getProperty();
		Term obj1 = f.getObject();
		Term obj2 = f2.getObject();
		return isMoreGeneralThan(sub1,sub2, ontologyGraph) && isMoreGeneralThan(pro1,pro2, ontologyGraph) && isMoreGeneralThan(obj1,obj2, ontologyGraph);
	}

	private static boolean isMoreGeneralThan(Term t1, Term t2, Multimap<Term, Term> ontologyGraph) {
	    Set<Term> s = BFS(ontologyGraph,t2);
	    t1 = Ontology.getTerm(t1.toSrting());
		return s.contains(t1);
	}

	/**
	 * 
	 * @param x - the first fact-set
	 * @param y - the second fact-set 
	 * @param ontologyGraph - the ontology represented as an adjacency list
	 * @return the LCA of node1 and node 2
	 */

	public static ArrayList<SemanticUnit> getLCAs(FactSet x, FactSet y, Multimap<Term, Term> ontologyGraph) {
		
		ArrayList<SemanticUnit> ans = new ArrayList<SemanticUnit>();
		for(Fact fact1: x)
		{
			for(Fact fact2: y)
			{
				ArrayList<SemanticUnit> temp = getLCAs(fact1,fact2,ontologyGraph);

				if(temp.size()>0)
					ans.addAll(temp);
			}
		}
		return ans;
	}
    /**
     * 
     * @param subjects
     * @param properties
     * @param objects
     * @return subjects X properties X objects
     */
	private static ArrayList<SemanticUnit> factLCA(ArrayList<SemanticUnit> subjects, ArrayList<SemanticUnit> properties, ArrayList<SemanticUnit> objects) {
		ArrayList<SemanticUnit> ans = new ArrayList<SemanticUnit>();
		for(SemanticUnit sub: subjects)
		{
			for(SemanticUnit pro: properties)
			{
				for(SemanticUnit obj: objects)
				{
					ans.add(new Fact((Term)sub,(Term)pro,(Term)obj));
				}
			}
		}
		
		return ans;
	}

	private static ArrayList<SemanticUnit> mergeLCA(Set<Term> list1,Set<Term> list2) {
		ArrayList <SemanticUnit> ans = new ArrayList<SemanticUnit>();
		Map<Term, Boolean> src = new HashMap<Term, Boolean>();

		for(Term s: list1) {
			src.put(s, true);
		}

		for(Term s: list2) {
		    if(src.get(s) != null && src.get(s)) {
		    	ans.add(s);
		    }
		}
		return ans;
	}

	public static ArrayList<SemanticUnit> getLCAs(SemanticUnit x,SemanticUnit y, Multimap<Term, Term> ontologyGraph) {
		if(x instanceof Term)
			return getLCAs((Term) x,(Term) y, ontologyGraph);
		else if (x instanceof Fact)
			return getLCAs((Fact) x,(Fact) y, ontologyGraph);
		return getLCAs((FactSet) x,(FactSet) y, ontologyGraph);
	}


}
