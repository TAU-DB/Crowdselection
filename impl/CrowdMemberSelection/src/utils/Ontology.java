package utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.jena.ext.com.google.common.collect.ArrayListMultimap;
import org.apache.jena.ext.com.google.common.collect.Multimap;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

public class Ontology {


	private static Multimap<Term, Term> ontologyGraph = ArrayListMultimap.create();
	private static Set<Term> terms;
	
	
	/** @throws FileNotFoundException */
	
	public static void buildOntologyGraph() throws FileNotFoundException{
		
		terms = new HashSet<Term>();
		Model tran = ModelFactory.createDefaultModel();
		tran.read(new FileInputStream("Ontology.ttl"),null,"TTL");
		StmtIterator iter = tran.listStatements();

		// print out the predicate, subject and object of each statement
		while (iter.hasNext())
		{
		    Statement stmt      = iter.nextStatement();  // get next statement
		    Resource  subject   = stmt.getSubject();     // get the subject
		   // Property  predicate = stmt.getPredicate();   // get the predicate
		    RDFNode   object    = stmt.getObject();      // get the object
		    
		    
		    String subjectName = subject.getLocalName();
		    String objectName = "";
		    if(object.isResource())
		    	objectName = ((Resource) object).getLocalName();
		    else if (object.isLiteral())
		    	objectName = object.asLiteral().getString();
		   
		    put(subjectName, objectName);
		}
	}
    /**
     * 
     * @param subjectName - key
     * @param objectName - value
     * add new value to the multimap
     */
	private static void put(String subjectName, String objectName) 
	{
		Term key = getTerm(subjectName);
		Term value = getTerm(objectName);
		ontologyGraph.put(key, value);
		
	}



		

	public static Term getTerm(String t) {
		
		Iterator<Term> iter = terms.iterator();
		Term ans = null;
	
		while(iter.hasNext())
		{
			Term temp = iter.next();
			if(temp.toSrting().equals(t))
			{
				ans = temp;
				break;
			}
				
		}
		if(ans == null)
		{
			ans = new Term(t);
			terms.add(ans);
		}
		return ans;
	}
	public static Multimap<Term, Term> getGraph() {
		return ontologyGraph;
	}
	
	public static Collection<Term> getValues(Term key){
		
		Collection<Term> ans = new ArrayList<Term>();
		Set<Term> keys = ontologyGraph.keySet();
		Iterator<Term> iter = keys.iterator();
		Term temp = null;
		while(iter.hasNext())
		{
			temp = iter.next();
			if(temp.toSrting().equals(key.toSrting()))
			{
				ans = ontologyGraph.get(temp);
			}
		}
		
		return ans;
	}
	
	
	/**
	 * 
	 * @return all the nodes in the graph
	 */
	public Set<Term> getTerms()
	{
		return terms;
	}
	
	/**
	 * 
	 * @param f1 fact
	 * @param f2 fact
	 * @return true iff f1 == f2
	 */
	public static boolean equalsFact(Fact f1, Fact f2) {
		Term s1 = getTerm(f1.getSubject().toSrting());
		Term s2 = getTerm(f2.getSubject().toSrting());
		
		Term p1 = getTerm(f1.getProperty().toSrting());
		Term p2 = getTerm(f2.getProperty().toSrting());
		
		Term o1 = getTerm(f1.getObject().toSrting());
		Term o2 = getTerm(f2.getObject().toSrting());
		return s1 == s2 && p1 == p2 && o1 == o2;
	}
	
	public static FactSet getFactsIntersection(FactSetWithSupport x,FactSetWithSupport y) 
	{
		FactSet ans = new FactSet();
		for(FactWithSupport f1: x)
		{
			for(FactWithSupport f2: y)
			{
				if(equalsFact(f1.getFact(),f2.getFact()))
				{
					ans.add(f1.getFact());
				}
			}
		}
		return ans;
	}
	
	public static void main(String[] args) throws FileNotFoundException{
		buildOntologyGraph();
		Term s = new Term("fact1");
		Collection<Term> s1 = getValues(s);
		//System.out.println(ontologyGraph.containsValue(s));
		System.out.println(s1);

		}
	
	

}
