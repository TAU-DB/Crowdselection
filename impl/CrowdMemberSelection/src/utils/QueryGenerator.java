package utils;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.jena.ext.com.google.common.collect.Multimap;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;

public class QueryGenerator {
	
	private Parser parser;
	private RDF rdf;
	private Multimap<Term, Term> ontologyGraph;

	private List<User> users;
	private FactSetWithSupport currentUserTranOrig = new FactSetWithSupport();
	
	
	public QueryGenerator(Parser p, RDF r, Multimap<Term, Term> ontologyGraph){
		parser = p;
		rdf = r;
		this.ontologyGraph = ontologyGraph;
		users = new ArrayList<User>();
		
	}
	
	public Parser getParser(){
		return parser;
	}
	
	public ArrayList<User> executeQuery()
	{
		
		
		String user = parser.getUserName();
		String ontologyQuery = parser.getQueryToExecute();
		ArrayList<SimilarityStatment> similarityStatments = parser.getSimilarityStatments();
		ArrayList<Filter> filters = parser.getFilters();
		
		ArrayList<SemanticUnit> ontologyQueryResult = executeOntologyQuery(ontologyQuery);
		System.out.println("Results after query: ");
				for(SemanticUnit u: ontologyQueryResult)
					System.out.println(((Term)u).toSrting());
		
		
		
		executeSimilarityStatments(user,ontologyQueryResult, similarityStatments);
		System.out.println("Results after similarity statements ");
		for(User u: users)
				System.out.println(((Term)u.getUser()).toSrting());
		
		removeSelf(user);
		executeFilters(filters);
		
		return (ArrayList<User>) users;
		
	}
	/**
	 * 
	 * @param usersToCompare
	 * @param user
	 * @return remove the current user from the list
	 */
    private void removeSelf( String user) {
 
    	for(User u: users){
    		if (((Term)u.getUser()).toSrting().equals(user)){
    			users.remove(u);
    			break;
    		}
    			
    		
    	}
	}

	private void executeFilters(ArrayList<Filter> filters) {
    	for(Filter filter: filters)
    	{
    		//System.out.println(filter.toString());
    		if(filter instanceof FilterLimit)
    		{
    			executeLimitFilter(filter);
    			
    		}
    		//System.out.println(filter.toString());
    		else if(filter instanceof FilterOrder)
    		{
    			executeOrderFilter(filter);
    			
    		}
    	
    	}
    	
	
	}


	@SuppressWarnings("unchecked")
	private void executeOrderFilter(Filter filter) {
		//ArrayList<SemanticUnit> users = new ArrayList<SemanticUnit>();
		if(((FilterOrder)filter).getBy().equals("ProfileSim"))
		{
		
		
			Collections.sort(users, new Comparator<Object>(){
				@Override
				public int compare(Object o1, Object o2) {
					double temp = ((User)o1).getProfileSim() - ((User)o2).getProfileSim();
					if(temp<0)
						return 1;
					else if(temp>0)
						return -1;
					else
						return 0;
					
				}});
		}
		
		else if(((FilterOrder)filter).getBy().equals("HistorySim"))
		{
			
			Collections.sort(users, new Comparator<Object>(){
				@Override
				public int compare(Object o1, Object o2) {
					double temp = ((User)o1).getTranSim() - ((User)o2).getTranSim();
					if(temp<0)
						return 1;
					else if(temp>0)
						return -1;
					else
						return 0;
					
				}});
		}
		
		else if(((FilterOrder)filter).getBy().equals("QuerySim"))
		{
			
			Collections.sort(users, new Comparator<Object>(){
				@Override
				public int compare(Object o1, Object o2) {
					double temp = ((User)o1).getQuerySim() - ((User)o2).getQuerySim();
					if(temp<0)
						return 1;
					else if(temp>0)
						return -1;
					else
						return 0;
					
				}});
		}
		
		else if(((FilterOrder)filter).getBy().equals("AVGSim"))
		{
			
			
			Collections.sort(users, new Comparator<Object>(){
				@Override
				public int compare(Object o1, Object o2) {
					double s1 = (((User)o1).getProfileSim()+((User)o1).getTranSim())/2.0;
					((User)o1).setAVG(s1);
					double s2 = (((User)o2).getProfileSim()+((User)o2).getTranSim())/2.0;
					((User)o2).setAVG(s2);
					
					double temp = s1 - s2;
					if(temp<0)
						return 1;
					else if(temp>0)
						return -1;
					else
						return 0;
					
				}});
		}
	
	
	}

	private void executeLimitFilter(Filter filter) {
	
		int limit = ((FilterLimit) filter).getLimit();
		if(limit <= users.size())
		{
			ArrayList<User> temp = new ArrayList<User>();
			for (int i =0; i<limit; i++)
			{
			
				temp.add((users.get(i)));
			}
			users= temp;
		}
	
	}

	/**
     * 
     * @param user
     * @param usersToCompare
     * @param similarityStatments
     * @return all the users passed the similarity threshold
     */
	private void executeSimilarityStatments(String user, ArrayList<SemanticUnit> usersToCompare, ArrayList<SimilarityStatment> similarityStatments) {
		//ArrayList<SemanticUnit> ans = new ArrayList<SemanticUnit>();
		//ArrayList<SemanticUnit> similarProfile = null;
		//ArrayList<SemanticUnit> similarTran = null;
		//Set<SemanticUnit> similarQueryTran = new HashSet<SemanticUnit>();
		
		
		
		for(SimilarityStatment s: similarityStatments)
		{
			//System.out.println(s.toString());
			//compare profiles
			if(s.getSrc().startsWith("profile"))
			{
				if(s.getDest().contains("{"))
				{
					executeProfilesSimilarity(usersToCompare,s);
				}
				else
				{
					//similarProfile = new ArrayList<SemanticUnit>();
					FactSet currentUserProfile = getUserProfile(user);
					//System.out.println(currentUserProfile.toSrting());
					executeProfilesSimilarity(currentUserProfile, usersToCompare,s);
				}
			
			}
			//compare transactions
			else if(s.getDest().contains(user))
			{
				
				//FactSetWithSupport currentUserTran = new FactSetWithSupport();
				currentUserTranOrig = getUserTransaction(user);
				
				executeTransSimilarity(currentUserTranOrig, usersToCompare,s);
			
			}
			//compare specific transaction
			else
			{
				
				executeQuerySimilarity(usersToCompare,s);
				
			}
		}

	}
	
	private void executeQuerySimilarity(ArrayList<SemanticUnit> usersToCompare, SimilarityStatment s) {
		FactSetWithSupport userTrans = new FactSetWithSupport();
	
		
    	for(SemanticUnit u: usersToCompare)
    	{
    		FactSetWithSupport queryTran = new FactSetWithSupport();
    		
    		FactSetWithSupport factsSupport = getFactSupport(s, u);
    		for(FactWithSupport f: factsSupport)
    			queryTran.add(f);
    	
    		
    		userTrans = new FactSetWithSupport();
    		userTrans = getUserTransaction(((Term)u).toSrting());
    		
    		/*System.out.println("*****User tran*********");
    		System.out.println(userTrans.toSrting());
    		System.out.println("*****Query*********");
    		System.out.println(queryTran.toSrting());*/
    	
    		double similarity = Similarity.getSimilarityWithSupportNoUser(queryTran, userTrans, ontologyGraph);
    		boolean result = false;
    		switch(s.getOp())
    		{
    	    	case(">"):
    	    		result = similarity > s.getTH();
    	    		break;
    	    	case("<"):
    	    		result = similarity < s.getTH();
    	    		break;
    	    	case(">="):
    	    		result = similarity >= s.getTH();
    	    		break;
    	    	case("<="):
    	    		result = similarity <= s.getTH();
    	    		break;
    	    }
    		//System.out.println(((FactSet)userProfile).toSrting());
    		//System.out.println(similarity);
    		if(result){
    			User user = new User();
    			boolean flag = false;
    			for(User uu: users){
    				if(uu.getUser().toSrting().equals(((Term)u).toSrting())){
    					user = uu;
    					flag = true;
    				}
    				
    			}
    			user.setQuerySim(similarity);
    			if(!flag){
    				user.setUser((Term)u);
    				users.add(user);
    			}
    				
    	
    			
    		}
    			
    	}
		
	}

	private FactSetWithSupport getFactSupport(SimilarityStatment s, SemanticUnit u) {
		FactSetWithSupport facts = new FactSetWithSupport();
		String str = s.getDest();
		str = str.replace("{", "");
		str = str.replace("}", "");
		str = str.replace("<http://a.org/ontology/", "");
		str = str.replace(">", "");
		
		String [] factss = str.split("\\.");
		for(int i = 0; i<factss.length-1 ; i++)
		{
			Scanner ss = new Scanner(factss[i]);
			ss.next();
			String sub = ((Term)u).toSrting();
			String pro = ss.next();
		
			String obj = ss.next();
		

			Fact f = new Fact(new Term(sub), new Term(pro), new Term(obj));
			FactWithSupport x = new FactWithSupport(1,f);
			facts.add(x);
			ss.close();
		}
		return facts;
	}
	
	private FactSet getFact(SimilarityStatment s, SemanticUnit u) {
		FactSet facts = new FactSet();
		String str = s.getDest();
		str = str.replace("{", "");
		str = str.replace("}", "");
		str = str.replace("<http://a.org/ontology/", "");
		str = str.replace(">", "");
		
		String [] factss = str.split("\\.");
		for(int i = 0; i<factss.length-1 ; i++)
		{
			Scanner ss = new Scanner(factss[i]);
			ss.next();
			String sub = ((Term)u).toSrting();
			String pro = ss.next();
		
			String obj = ss.next();
		

			Fact f = new Fact(new Term(sub), new Term(pro), new Term(obj));
		
			facts.add(f);
			ss.close();
		}
		return facts;
	}


	/**
	 * 
	 * @param similarProfile
	 * @param similarTran
	 * @return similar users in both profile and transactions
	 */
	private ArrayList<SemanticUnit> UsersSet(ArrayList<SemanticUnit> similarProfile, ArrayList<SemanticUnit> similarTran) {
		ArrayList<SemanticUnit> ans = new ArrayList<SemanticUnit>();
		for(SemanticUnit u: similarProfile){
			for(SemanticUnit s: similarTran){
				if (((Term)u).toSrting().equals(((Term)s).toSrting())){
					ans.add(u);
					break;
				}
			}
		}
		
		
		return ans;
	}

	/**
	 * 
	 * @param currentUserTran - all the transactions of the current user
	 * @param usersToCompare - list of users to compare
	 * @param s - the similarity statement
	 * @return
	 */
	private void executeTransSimilarity(FactSetWithSupport currentUserTran, ArrayList<SemanticUnit> usersToCompare, SimilarityStatment s) {
	
		//FactSetWithSupport CUT = copyUserTran(currentUserTran);
	

		FactSetWithSupport userTrans = new FactSetWithSupport();
	
    	for(SemanticUnit u: usersToCompare)
    	{
    	
    		
    		userTrans = new FactSetWithSupport();
    		userTrans = getUserTransaction(((Term)u).toSrting());
    		
    		/*System.out.println("Current user");
    		System.out.println(currentUserTran.toSrting());
    		
    		System.out.println("user");
    		System.out.println(userTrans.toSrting());*/

    		
    		double similarity = Similarity.getSimilarityWithSupport(currentUserTran, userTrans, ontologyGraph);
    		boolean result = false;
    		switch(s.getOp())
    		{
    	    	case(">"):
    	    		result = similarity > s.getTH();
    	    		break;
    	    	case("<"):
    	    		result = similarity < s.getTH();
    	    		break;
    	    	case(">="):
    	    		result = similarity >= s.getTH();
    	    		break;
    	    	case("<="):
    	    		result = similarity <= s.getTH();
    	    		break;
    	    }
    		//System.out.println(((FactSet)userProfile).toSrting());
    		//System.out.println(similarity);
    		if(result){
    			User user = new User();
    			boolean flag = false;
    			for(User uu: users){
    				if(uu.getUser().toSrting().equals(((Term)u).toSrting())){
    					user = uu;
    					flag = true;
    				}
    				
    			}
    			user.setTranSim(similarity);
    			if(!flag){
    				user.setUser((Term)u);
    				users.add(user);
    			}
    				
    	
    			
    		}
    			
    	}
	
	}

	

	
	/**
	 * 
	 * @param user
	 * @return all the transactions related to the user
	 */
	private FactSetWithSupport getUserTransaction(String user) {
		
		String query = "SELECT ?x ?val {  ?tran <http://a.org/ontology/By> <http://a.org/ontology/" + user + ">  ."+
	                    "?tran <http://a.org/ontology/WithSupport> ?val ."+
						"?tran <http://a.org/ontology/hasFact> ?x . }";
		ArrayList<SemanticUnit> ans = new ArrayList<SemanticUnit>();
	
		ans = executeTransactionQuery(query);
	
		FactSetWithSupport factSet = new FactSetWithSupport();

		
		Iterator<SemanticUnit> iter = ans.iterator();

		while(iter.hasNext())
		{

			Term support = (Term) iter.next();
			Term fact = (Term) iter.next();
			
			//System.out.println(support.toSrting()+" "+fact.toSrting());
			String s = support.toSrting();
		
			double sup = Double.parseDouble(s);
	
		
			ArrayList<SemanticUnit> factCompoment = getFactCompoments(fact);
			if(factCompoment.size()>=3){
				Fact f = new Fact((Term)factCompoment.get(0),(Term)factCompoment.get(1),(Term)factCompoment.get(2));
			//	if(f.getSubject().toSrting().contains("Serge")/*&&f.getProperty().toSrting().equals("publishedAt")*/)
				//	System.out.println(f.toSrting());
				boolean flag = true;
			    if(!f.getSubject().toSrting().equals(user) && !f.getObject().toSrting().equals(user))
			    	flag = false;
			    if(flag){
			    	FactWithSupport factSupport = new FactWithSupport(sup,f);
			    	factSet.add(factSupport);
			    }
			}
		}
		
		return factSet;
	}

	private ArrayList<SemanticUnit> getFactCompoments(Term fact) {
		String f = fact.toSrting();
		String query = "SELECT ?x ?y ?z {  <http://a.org/ontology/"+f+"> <http://a.org/ontology/hasSubject> ?x  ."+
                "<http://a.org/ontology/"+f+"> <http://a.org/ontology/hasProperty> ?y  ."+
				"<http://a.org/ontology/"+f+"> <http://a.org/ontology/hasObject> ?z  . }";
		ArrayList<SemanticUnit> ans = new ArrayList<SemanticUnit>();
		ans = executeTransactionQuery(query);
		return ans;
	}

	/**
	 * 
	 * @param currentUserProfile - as a fact-set
	 * @param usersToCompare - list of potentials users
	 * @param s - similarity statement to execute
	 * @return
	 */
    private void executeProfilesSimilarity(	FactSet currentUserProfile, ArrayList<SemanticUnit> usersToCompare,SimilarityStatment s) {
    	//ArrayList<SemanticUnit> ans = new ArrayList<SemanticUnit>();
    	for(SemanticUnit user: usersToCompare)
    	{
    		FactSet userProfile = getUserProfile(((Term)user).toSrting());
    		//System.out.println("Current:");
    		//System.out.println(currentUserProfile.toSrting());
    		//System.out.println("user:");
    		//System.out.println(userProfile.toSrting());
    		
    		double similarity = Similarity.getSimilarityWithoutSupport(currentUserProfile, userProfile, ontologyGraph);
    		boolean result = false;
    		switch(s.getOp())
    		{
    	    	case(">"):
    	    		result = similarity > s.getTH();
    	    		break;
    	    	case("<"):
    	    		result = similarity < s.getTH();
    	    		break;
    	    	case(">="):
    	    		result = similarity >= s.getTH();
    	    		break;
    	    	case("<="):
    	    		result = similarity <= s.getTH();
    	    		break;
    	    }
    		//System.out.println(((FactSet)userProfile).toSrting());
    		//System.out.println(similarity);
    		if(result){
    			User u = new User();
    			boolean flag = false;
    			for(User uu: users){
    				if(uu.getUser().toSrting().equals(((Term)user).toSrting())){
    					u = uu;
    					flag = true;
    				}
    				
    			}
    			u.setProfileSim(similarity);
    			if(!flag){
    				u.setUser((Term)user);
    				users.add(u);
    			}
    				
    	
    			
    		}
    			
    	}
	}
    
    /**
	 * 
	 * @param currentUserProfile - as a fact-set
	 * @param usersToCompare - list of potentials users
	 * @param s - similarity statement to execute
	 * @return
	 */
    private void executeProfilesSimilarity(	ArrayList<SemanticUnit> usersToCompare,SimilarityStatment s) {
    	//ArrayList<SemanticUnit> ans = new ArrayList<SemanticUnit>();
    	for(SemanticUnit user: usersToCompare)
    	{
    		FactSet userProfile = getUserProfile(((Term)user).toSrting());
    	
    	    FactSet queryTran = new FactSet();
    		
    		FactSet facts = getFact(s, user);
    		for(Fact f: facts)
    			queryTran.add(f);
    		//System.out.println("Current:");
    		//System.out.println(currentUserProfile.toSrting());
    		//System.out.println("user:");
    		//System.out.println(userProfile.toSrting());
    		
    		double similarity = Similarity.getSimilarityWithoutSupport(queryTran, userProfile, ontologyGraph);
    		boolean result = false;
    		switch(s.getOp())
    		{
    	    	case(">"):
    	    		result = similarity > s.getTH();
    	    		break;
    	    	case("<"):
    	    		result = similarity < s.getTH();
    	    		break;
    	    	case(">="):
    	    		result = similarity >= s.getTH();
    	    		break;
    	    	case("<="):
    	    		result = similarity <= s.getTH();
    	    		break;
    	    }
    		//System.out.println(((FactSet)userProfile).toSrting());
    		//System.out.println(similarity);
    		if(result){
    			User u = new User();
    			boolean flag = false;
    			for(User uu: users){
    				if(uu.getUser().toSrting().equals(((Term)user).toSrting())){
    					u = uu;
    					flag = true;
    				}
    				
    			}
    			u.setQuerySim(similarity);
    			if(!flag){
    				u.setUser((Term)user);
    				users.add(u);
    			}
    				
    	
    			
    		}
    			
    	}
	}

	/**
     * 
     * @param user
     * @return the user profile (as a fact set)
     */
	private FactSet getUserProfile(String user) {
		
		//String query = "SELECT ?x ?y {  <http://a.org/users/" + user + "> ?x ?y . }";
		String query = "SELECT ?x ?y {  <http://a.org/ontology/" + user + "> ?x ?y . }";
		ArrayList<SemanticUnit> ans = executeProfileQuery(query);
		FactSet factSet = new FactSet();
		
		Iterator<SemanticUnit> iter = ans.iterator();
		Term userName = new Term(user);
		while(iter.hasNext())
		{
			Term sub = (Term) iter.next();
			Term pro = (Term) iter.next();
			Fact fact = new Fact(userName, pro, sub);
			factSet.add(fact);
		}
		return factSet;
	}

	private ArrayList<SemanticUnit> executeTransactionQuery(String profileQuery) {
		ArrayList<SemanticUnit> ans = new ArrayList<SemanticUnit>();
      //  Model transactions = rdf.getTransactionsModel();
		Model transactions = rdf.getOntologyModel();
			
			String queryString = profileQuery;
         
	//		generate Query obj from queryString
			Query query = QueryFactory.create(queryString);
			
	//		execute query
			try (QueryExecution qexec = QueryExecutionFactory.create(query, transactions)) {
				
	//			gather results
				ResultSet results = qexec.execSelect();
				
	//			parse results
				while(results.hasNext()) {
					QuerySolution re =  results.next();
					String result = re.toString();
					
					result = result.replace(">", "");
					result = result.replace("<http://a.org/ontology/", "");
					result = result.replace("(", "");
					result = result.replace(")", "");
					result = result.replace("=", "");
				
					String [] words = result.split(" ");
					if(isAttribute(result))
						continue;
					for (int i = 0; i<words.length; i++){
						if (words[i].equals(""))
							continue;
						else if (words[i].contains("?"))
							continue;
						else 
						{
							Term t = new Term(words[i]);
							ans.add(t);
						}
					
					}
				}
		return ans;
	}
	}

	private ArrayList<SemanticUnit> executeProfileQuery(String profileQuery) {
		ArrayList<SemanticUnit> ans = new ArrayList<SemanticUnit>();
       // Model profiles = rdf.getProfilesModel();
		Model profiles = rdf.getOntologyModel();
			
			String queryString = profileQuery;
         
	//		generate Query obj from queryString
			Query query = QueryFactory.create(queryString);
			
	//		execute query
			try (QueryExecution qexec = QueryExecutionFactory.create(query, profiles)) {
				
	//			gather results
				ResultSet results = qexec.execSelect();
				
	//			parse results
				while(results.hasNext()) {
					QuerySolution re =  results.next();
					String result = re.toString();
					result = result.replace("<", "");
					result = result.replace(">", "");
					result = result.replace("=", "");
					result = result.replace("(", "");
					result = result.replace(")", "");
					result = result.replace("http://a.org/ontology/", "");
					//result = result.replace("http", "");
					//result = result.replace("org", "");
					//result = result.replace("users", "");
					//result = result.replace("ontology", "");
					//result = result.replaceAll("[\\/\\.\\:]","");
					if(isAttribute(result))
						continue;
					String [] words = result.split(" ");
					for (int i = 0; i<words.length; i++){
						if (words[i].equals(""))
							continue;
						else if (words[i].contains("?"))
							continue;
						else {
							Term t; 
						//	if(words[i].startsWith("a"))
							//	t = new Term(words[i].substring(1));
						//	else
								t = new Term(words[i]);
							ans.add(t);
						}
					}
					
				}
		return ans;
	}
	}

	private boolean isAttribute(String result) {
		if(/*result.contains("h_index")||*/result.contains("p_index")/*||result.contains("pc ")*/||result.contains("cn ")||result.contains("id ")||result.contains("year"))
			return true;
		return false;
	}

	private ArrayList<SemanticUnit> executeOntologyQuery(String ontologyQuery) {
		ArrayList<SemanticUnit> ans = new ArrayList<SemanticUnit>();
           Model ontology = rdf.getOntologyModel();
			
			String queryString = ontologyQuery;
			
			//DEBUG!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
         //  String queryString = "SELECT ?u WHERE {      ?u <http://a.org/ontology/keyTerm> ?term  .            {?term <http://a.org/ontology/instanceof> <http://a.org/ontology/Information_retrieval>} UNION {?term <http://a.org/ontology/instanceof> <http://a.org/ontology/Data_management>} .}";
            
	//		generate Query obj from queryString
			Query query = QueryFactory.create(queryString);
			
	//		execute query
			try (QueryExecution qexec = QueryExecutionFactory.create(query, ontology)) {
				
	//			gather results
				ResultSet results = qexec.execSelect();
				
	//			parse results
				while(results.hasNext()) {
					QuerySolution re =  results.next();
					String result = re.toString();
					result = result.replace("<", "");
					result = result.replace(">", "");
					result = result.replace("=", "");
					result = result.replace("(", "");
					result = result.replace(")", "");
					result = result.replace("http", "");
					result = result.replace("org", "");
					result = result.replace("ontology", "");
					result = result.replaceAll("[\\/\\.\\:]","");
					String [] words = result.split(" ");
					result = words[3].substring(1);
					Term t = new Term(result);
					if(!contains(ans,t))
						ans.add(t);
				}
		return ans;
	}
	


	}
	
	private boolean contains(ArrayList<SemanticUnit> ans, Term t) {
		for(SemanticUnit u: ans)
			if(((Term)u).toSrting().equals(t.toSrting()))
					return true;
		return false;
	}

	public static void main(String[] args) throws IOException {
		Parser parser = new Parser("");
		RDF rdf = new RDF();
		rdf.init();
		//QueryGenerator generator = new QueryGenerator(parser, rdf);
		//.executeOntologyQuery("");
	}
	
}
