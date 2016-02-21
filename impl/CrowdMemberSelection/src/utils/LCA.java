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
	
	public static Map<Term, Set<Term>> bfsCache = new HashMap<Term, Set<Term>>();
	public static void main(String[] args) throws IOException {
		
		Ontology.buildOntologyGraph();
		Multimap<Term, Term> ontologyGraph = Ontology.getGraph();
		
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
		
		Set<Term> visit = new LinkedHashSet<Term>();
	    Queue<Term> queue = new LinkedList<Term>();
	    Set<Term> result = new LinkedHashSet<Term>();
	    queue.add((Term)x);
	    visit.add((Term)x);
	    while(!queue.isEmpty()) {
	    	Term current = queue.remove();
	    	current = Ontology.getTerm(current.toSrting());
	    	// System.out.println(((Term)s).toSrting());
	        result.add(current);
	        if(Ontology.getValues(current) != null)
	           for(Term s: Ontology.getValues(current)) {
	        	    if(!result.contains(s) && !contains(queue, s)){
	        	    	if(!visit.contains(s)){
	        	    		queue.add(s);
	        	    		visit.add(s);
	        	    	}
	        	    	
	        	    }
	            }
	    }
	    return result;
	}
	private static boolean contains(Queue<Term> queue, Term s) {
		for(Term t: queue)
			if(t.toSrting().equals(s.toSrting()))
				return true;
		return false;
	}

	/**
	 * 
	 * @param x - the first node
	 * @param y - the second node 
	 * @param ontologyGraph - the ontology represented as an adjacency list
	 * @return the LCA of node1 and node 2
	 */

	public static ArrayList<SemanticUnit> getLCAs(Term x, Term y, Multimap<Term, Term> ontologyGraph) {
		
		Set<Term> list1 = null;
		if(bfsCache.containsKey(x))
			list1 = bfsCache.get(x);
		else
		{
			list1 = BFS(ontologyGraph, (Term) x); 
			bfsCache.put(x, list1);
		}
		
		Set<Term> list2 = null;
		if(bfsCache.containsKey(y))
			list2 = bfsCache.get(y);
		else
		{
			list2 = BFS(ontologyGraph, (Term) y); 
			bfsCache.put(y, list2);
		}
	
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
		ArrayList<SemanticUnit> ans = new ArrayList<SemanticUnit>();
		
		Set<Term> listSubject1 = null;
		if(bfsCache.containsKey((Term) x.getSubject()))
			listSubject1 = bfsCache.get((Term) x.getSubject());
		else
		{
			listSubject1 = BFS(ontologyGraph, (Term) x.getSubject()); 
			bfsCache.put((Term) x.getSubject(), listSubject1);
		}
		
		Set<Term> listSubject2 = null;
		if(bfsCache.containsKey( (Term) y.getSubject()))
			listSubject2 = bfsCache.get( (Term) y.getSubject());
		else
		{
			listSubject2 = BFS(ontologyGraph,  (Term) y.getSubject()); 
			bfsCache.put( (Term) y.getSubject(), listSubject2);
		}
	
		//Set<Term> listSubject1 = BFS(ontologyGraph, (Term) x.getSubject()); 
	//	Set<Term> listSubject2 = BFS(ontologyGraph, (Term) y.getSubject());
		
	      //get all subject LCA
		ArrayList <SemanticUnit> subjects = mergeLCA(listSubject1, listSubject2);
		
		
		if(subjects.size()>0){
			
			Set<Term> listProperty1 = null;
			if(bfsCache.containsKey((Term) x.getProperty()))
				listProperty1 = bfsCache.get((Term) x.getProperty());
			else
			{
				listProperty1 = BFS(ontologyGraph, (Term) x.getProperty()); 
				bfsCache.put((Term) x.getProperty(), listProperty1);
			}
			
			Set<Term> listProperty2 = null;
			if(bfsCache.containsKey( (Term) y.getProperty()))
				listProperty2 = bfsCache.get( (Term) y.getProperty());
			else
			{
				listProperty2 = BFS(ontologyGraph, (Term) y.getProperty()); 
				bfsCache.put( (Term) y.getProperty(), listProperty2);
			}
		
			//get all property LCA
			ArrayList <SemanticUnit> properties = mergeLCA(listProperty1, listProperty2);
		//	for(SemanticUnit u: properties)
			//	System.out.println(((Term)u).toSrting());
			
			if(properties.size()>0){
		        
				Set<Term> listObject1 = null;
				if(bfsCache.containsKey((Term) x.getObject()))
					listObject1  = bfsCache.get((Term) x.getObject());
				else
				{
					listObject1  = BFS(ontologyGraph, (Term) x.getObject()); 
					bfsCache.put((Term) x.getObject(), listObject1 );
				}
				
				Set<Term> listObject2 = null;
				if(bfsCache.containsKey( (Term) y.getObject()))
					listObject2 = bfsCache.get( (Term) y.getObject());
				else
				{
					listObject2 = BFS(ontologyGraph, (Term) y.getObject()); 
					bfsCache.put( (Term) y.getObject(), listObject2);
				}
	
				//get all object LCA
				ArrayList <SemanticUnit> objects = mergeLCA(listObject1, listObject2);
				//for(SemanticUnit u: objects)
					//System.out.println(((Term)u).toSrting());
		
				if(objects.size()>0){
					ans = factLCA(subjects, properties, objects);
					
					ans = removeRedundantFacts(ans, ontologyGraph);
				}
			}
		}
		
		return ans;
	}
	


	public static ArrayList<SemanticUnit> removeRedundantFacts(ArrayList<SemanticUnit> ans,Multimap<Term, Term> ontologyGraph) {
		
		Iterator<SemanticUnit> iter = ans.iterator();
		//System.out.println();
		//System.out.println(ans.size());
		while(iter.hasNext())
		{
			//System.out.println(ans.size());
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
		Set<Term> s = null;
		if(bfsCache.containsKey(t2))
			s = bfsCache.get(t2);
		else{
			s = BFS(ontologyGraph,t2);
			bfsCache.put(t2, s);
		}
	
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
				
			//	if(fact1.getObject().toSrting().equals("Web_Services_Description_Language") && fact2.getObject().toSrting().equals("Linked_data")){
				//	System.out.println(fact1.toSrting());
					//System.out.println(fact2.toSrting());
				//}
				ArrayList<SemanticUnit> temp = getLCAs(fact1,fact2,ontologyGraph);

				if(temp.size()>0)
					ans.addAll(temp);
			}
		}
		ans = removeDuplicates(ans);
		return ans;
	}
    private static ArrayList<SemanticUnit> removeDuplicates( ArrayList<SemanticUnit> ans) {
    	ArrayList<SemanticUnit> a = new ArrayList<SemanticUnit>();
    	for(SemanticUnit u: ans){
    		if(!contains(a,u))
    			a.add(u);
    	}
		return a;
	}

	private static boolean contains(ArrayList<SemanticUnit> a, SemanticUnit u) {
		Fact f2 = (Fact)u;
		for(SemanticUnit uu: a){
			Fact f1 = (Fact)uu;
			if(Ontology.equalsFact(f1,f2))
				return true;
		}
		return false;
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
