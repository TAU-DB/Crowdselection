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
import org.apache.jena.rdf.model.Property;
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
	
		//tran.read(new FileInputStream("Ontology.ttl"),null,"TTL");
		tran.read(new FileInputStream("Aminer-data.ttl"),null,"TTL");
	//	tran.read(new FileInputStream("SO data.ttl"),null,"TTL");
		StmtIterator iter = tran.listStatements();

		// print out the predicate, subject and object of each statement
		while (iter.hasNext())
		{
		    Statement stmt      = iter.nextStatement();  // get next statement
		    Resource  subject   = stmt.getSubject();     // get the subject
		    Property p = stmt.getPredicate();
		    
		    
		    String s = p.getLocalName();
		    if(s.equals("id")||s.equals("h_index")||s.equals("p_index")||s.equals("pc")||s.equals("cn")){
		    	continue;
		    }
		    if(s.equals("by")||s.equals("WithSupport")||s.equals("hasFact")||s.equals("By")){
		    	continue;
		    }
		    if(s.equals("hasSubject")||s.equals("hasProperty")||s.equals("hasObject")){
		    	continue;
		    }
		    if(s.equals("year")||s.equals("hasProperty")||s.equals("hasObject")){
		    	continue;
		    }
		    
		    if(s.equals("keyTerm")||s.equals("affiliation")||s.equals("country")){
		    	continue;
		    }
		    if(s.equals("venue")||s.equals("in")){
		    	continue;
		    }
		    if(s.equals("id")||s.equals("creationDate")||s.equals("isAnswerTo")||s.equals("is_accepted")){
		    	continue;
		    }
		    if(s.equals("user_type")||s.equals("creation_date")||s.equals("last_access_date")||s.equals("accept_rate")){
		    	continue;
		    }
		    if(s.equals("bronze")||s.equals("silver")||s.equals("gold")||s.equals("isAnswered")){
		    	continue;
		    }
		    if(s.equals("accepted_answer_id")||s.equals("tag")||s.equals("score")){
		    	continue;
		    }
		
		    
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
		//if(key.equals("Algorithms"))
			//System.out.println(key.toSrting()+" "+value.toSrting());
		if(!key.toSrting().equals(""))
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
	
	public static FactSet getFactsIntersection(FactSetWithSupport x,FactSetWithSupport y,FactSetWithSupport xnew, FactSetWithSupport ynew) 
	{
		
		System.out.println("X");
		System.out.println(x.toSrting());
		
		System.out.println("Y");
		System.out.println(y.toSrting());
	    
		
		String user1 =  getUser(x);
		String user2 =  getUser(y);
		FactSet xFacts = x.getFacts();
		FactSet yFacts = y.getFacts();
		
		changeOrderCollaboration(xFacts, user1, user2);
		changeOrderCollaboration(yFacts,user2, user1);
		ArrayList<SemanticUnit> lca = LCA.getLCAs(x.getFacts(), y.getFacts(),ontologyGraph);
		LCA.removeRedundantFacts(lca,ontologyGraph );
		
		System.out.println("intersection:");
		for(SemanticUnit u: lca)
			System.out.println(((Fact)u).toSrting());
		
		FactSet ans = new FactSet();
		for(SemanticUnit u: lca)
		{
			//Term o2 = ((Fact)u).getObject();
			//if(o2.toSrting().equals("Author")||o2.toSrting().equals("Paper")||o2.toSrting().equals("Confrence"))
				//continue;
			ans.add((Fact)u);
		}
		
		
		getSupports(ans, xnew,ynew,x,y, user1, user2);
	/*	System.out.println("intersection + support X:");
		System.out.println(xnew.toSrting());
		System.out.println("intersection + support Y:");
		System.out.println(ynew.toSrting());*/
		
	
		return ans;
	}
	/**
	 * 
	 * @param ans
	 * @param xnew
	 * @param ynew
	 * @param x
	 * @param y
	 * @param user1 
	 * @param user2 
	 */
	private static void getSupports(FactSet ans, FactSetWithSupport xnew, FactSetWithSupport ynew, FactSetWithSupport x, FactSetWithSupport y, String user1, String user2) {
          for(Fact f: ans)
          {
        	  double s1;
        	  double s2;
        	  Term o2 = f.getObject();
  			  if(o2.toSrting().equals("Author")||o2.toSrting().equals("Paper")||o2.toSrting().equals("Confrence")){
  				  s1 = 1;
  				  s2 = 0;
  			  }
  			  else
  			  {
  				s1 = getSupport(x,f, user1, user2);
  				s2 = getSupport(y,f,user2, user1);
  			  }
        
        	  FactWithSupport newFact = new FactWithSupport(s1,f);
        	  xnew.add(newFact);
        	  
       
        	  FactWithSupport newFact2 = new FactWithSupport(s2,f);
        	  ynew.add(newFact2);
          }
		
	}
	private static double getSupport(FactSetWithSupport y, Fact f, String user1, String user2) {
		double ans = 0;
		for(FactWithSupport fact: y)
		{
			Term p1 = fact.getFact().getProperty();
			Term p2 = f.getProperty();
			
			Term s1 = fact.getFact().getSubject();
			Term s2 = f.getSubject();
			
			Term o1 = fact.getFact().getObject();
			Term o2 = f.getObject();
			
			if(o2.toSrting().equals("Author")||o2.toSrting().equals("Paper")||o2.toSrting().equals("Confrence"))
				return 1.0;
			else if(p1.toSrting().equals(p2.toSrting()))
			{
				if(p2.toSrting().equals("collaborate"))
				{
					
					if(o1.toSrting().equals(o2.toSrting()))
						ans = fact.getSupprt();
					else if(o2.toSrting().equals(s1.toSrting()))
						ans = fact.getSupprt();
					
				}
				
				else if(p2.toSrting().equals("published"))
				{
					
					if(o1.toSrting().equals(o2.toSrting()))
						ans = fact.getSupprt();
					
				}
				else if(p2.toSrting().equals("publishedAt"))
				{
					
					if(o1.toSrting().equals(o2.toSrting()))
						ans = fact.getSupprt();
				
				}
					
			}
		}
		
		
		return ans;
	}
	private static void changeOrderCollaboration(FactSet xFacts, String user1, String user2) {
		for(Fact f: xFacts){
			if(f.getProperty().toSrting().equals("collaborate"))
			{
				if(f.getObject().toSrting().equals(user1))
					if(!f.getSubject().toSrting().equals(user2)){
						String temp = f.getSubject().toSrting();
						f.getSubject().set(user1);
						f.getObject().set(temp);
					}
			}
		}
		
	}


	/**
	 * 
	 * @param collaborationsU1
	 * @param user2
	 * @return true if user1 collaborate user2
	 */
	private static boolean contains(ArrayList<Term> collaborationsU1,String user2) {
		for(Term t: collaborationsU1)
			if(t.toSrting().equals(user2))
				return true;

		return false;
	}
	

	/**
	 * 
	 * @param x
	 * @return the user name
	 */
	private static String getUser(FactSetWithSupport x) {
		String user1 = "";
		for(FactWithSupport f1: x)
		{
			if(f1.getFact().getProperty().toSrting().equals("published"))
			{
				user1 = f1.getFact().getSubject().toSrting();
				break;
		
			}
		}
		return user1;
	}
	
	

	public static void main(String[] args) throws FileNotFoundException{
		buildOntologyGraph();
		Term s = new Term("fact1");
		Collection<Term> s1 = getValues(s);
		//System.out.println(ontologyGraph.containsValue(s));
		System.out.println(s1);

		}
	public static FactSet getFactsIntersection(FactSetWithSupport queryTran,FactSetWithSupport userTrans) {
		FactSet ans = new FactSet();
		for(Fact f1: queryTran.getFacts())
			for(Fact f2: userTrans.getFacts())
				if(f1.equal(f2))
					ans.add(f1);
		return ans;
	}
	
	

}
