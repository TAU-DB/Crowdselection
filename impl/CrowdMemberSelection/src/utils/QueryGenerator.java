package utils;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
	private Map<String, Double> simProfiles ;
	private Map<String, Double> simTran ;
	
	public QueryGenerator(Parser p, RDF r, Multimap<Term, Term> ontologyGraph){
		parser = p;
		rdf = r;
		this.ontologyGraph = ontologyGraph;
		simProfiles =  new HashMap <String, Double>();
		simTran =  new HashMap <String, Double>();
	}
	
	public Parser getParser(){
		return parser;
	}
	
	public ArrayList<User> executeQuery()
	{
		ArrayList<User> users = new ArrayList<User>();
		
		String user = parser.getUserName();
		String ontologyQuery = parser.getQueryToExecute();
		ArrayList<SimilarityStatment> similarityStatments = parser.getSimilarityStatments();
		ArrayList<Filter> filters = parser.getFilters();
		
		ArrayList<SemanticUnit> ontologyQueryResult = executeOntologyQuery(ontologyQuery);
		System.out.println("Results after query: ");
				for(SemanticUnit u: ontologyQueryResult)
					System.out.println(((Term)u).toSrting());
		
		
		
		ArrayList<SemanticUnit> usersToCompare = executeSimilarityStatments(user,ontologyQueryResult, similarityStatments);
		System.out.println("Results after similarity statements ");
		for(SemanticUnit u: usersToCompare)
				System.out.println(((Term)u).toSrting());
		
		usersToCompare = removeSelf(usersToCompare, user);
		users = executeFilters(usersToCompare,filters);
		
		return users;
		
	}
	/**
	 * 
	 * @param usersToCompare
	 * @param user
	 * @return remove the current user from the list
	 */
    private ArrayList<SemanticUnit> removeSelf(ArrayList<SemanticUnit> usersToCompare, String user) {
    	ArrayList<SemanticUnit> ans = new ArrayList<SemanticUnit>();
    	for(SemanticUnit u: usersToCompare){
    		if (!((Term)u).toSrting().equals(user))
    			ans.add(u);
    	}
    	return ans;
	}

	private ArrayList<User> executeFilters(ArrayList<SemanticUnit> usersToCompare, ArrayList<Filter> filters) {
    	for(Filter filter: filters)
    	{
    		//System.out.println(filter.toString());
    		if(filter instanceof FilterLimit)
    		{
    			usersToCompare = executeLimitFilter(filter, usersToCompare);
    			
    		}
    		//System.out.println(filter.toString());
    		else if(filter instanceof FilterOrder)
    		{
    			usersToCompare = executeOrderFilter(filter, usersToCompare);
    			
    		}
    	
    	}
    	
		return CreateUsersList(usersToCompare);
	}

	private ArrayList<User> CreateUsersList(ArrayList<SemanticUnit> usersToCompare) {
		ArrayList<User> users = new ArrayList<User>();
		for(SemanticUnit u: usersToCompare)
		{
			double simProfile = 0;
			if(simProfiles.containsKey(((Term)u).toSrting()))
				simProfile = simProfiles.get(((Term)u).toSrting());
			double simTransaction = 0;
			if(simTran.containsKey(((Term)u).toSrting()))
				simTransaction = simTran.get(((Term)u).toSrting());
		
			users.add(new User(((Term)u),simProfile, simTransaction));
			
		}
		
		return users;
	}

	private ArrayList<SemanticUnit> executeOrderFilter(Filter filter, ArrayList<SemanticUnit> usersToCompare) {
		ArrayList<SemanticUnit> users = new ArrayList<SemanticUnit>();
		if(((FilterOrder)filter).getBy().equals("ProfileSim"))
		{
			//TODO
		}
	
		return users;
	}

	private ArrayList<SemanticUnit> executeLimitFilter(Filter filter, ArrayList<SemanticUnit> usersToCompare) {
		ArrayList<SemanticUnit> users = new ArrayList<SemanticUnit>();
		int limit = ((FilterLimit) filter).getLimit();
		if(limit <= usersToCompare.size())
		{
			for (int i =0; i<limit; i++)
			{
				users.add(((Term)usersToCompare.get(i)));
			}
		}
		else
		{
			for (int i =0; i<usersToCompare.size(); i++)
			{
				users.add(((Term)usersToCompare.get(i)));
			}
			
		}
		return users;
	}

	/**
     * 
     * @param user
     * @param usersToCompare
     * @param similarityStatments
     * @return all the users passed the similarity threshold
     */
	private ArrayList<SemanticUnit> executeSimilarityStatments(String user, ArrayList<SemanticUnit> usersToCompare, ArrayList<SimilarityStatment> similarityStatments) {
		ArrayList<SemanticUnit> ans = new ArrayList<SemanticUnit>();
		ArrayList<SemanticUnit> similarProfile = null;
		ArrayList<SemanticUnit> similarTran = null;
		//Set<SemanticUnit> similarQueryTran = new HashSet<SemanticUnit>();
		
		
		
		for(SimilarityStatment s: similarityStatments)
		{
			//System.out.println(s.toString());
			//compare profiles
			if(s.getSrc().startsWith("profile"))
			{
				similarProfile = new ArrayList<SemanticUnit>();
				FactSet currentUserProfile = getUserProfile(user);
				//System.out.println(currentUserProfile.toSrting());
				similarProfile = executeProfilesSimilarity(currentUserProfile, usersToCompare,s);
			
			}
			//compare transactions
			else if(s.getDest().contains("tran"))
			{
				similarTran = new ArrayList<SemanticUnit>();
				FactSetWithSupport currentUserTran = getUserTransaction(user);
				//System.out.println(currentUserProfile.toSrting());
				similarTran = executeTransSimilarity(currentUserTran, usersToCompare,s);
			
			}
			//compare specific transaction
			else
			{
				//similarQueryTran = 
			}
		}
		if(similarProfile != null && similarTran != null)
			ans = UsersSet(similarProfile, similarTran);
		else if (similarProfile != null)
			ans = similarProfile;
		else if (similarTran != null)
			ans = similarTran;
		else
			ans = usersToCompare;
		return ans;
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
	private ArrayList<SemanticUnit> executeTransSimilarity(FactSetWithSupport currentUserTran, ArrayList<SemanticUnit> usersToCompare, SimilarityStatment s) {
		ArrayList<SemanticUnit> ans = new ArrayList<SemanticUnit>();
    	for(SemanticUnit user: usersToCompare)
    	{
    		FactSetWithSupport userTransaction = getUserTransaction(((Term)user).toSrting());
    		double similarity = Similarity.getSimilarityWithSupport(currentUserTran, userTransaction, ontologyGraph);
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
    			ans.add(user);
    			simTran.put(((Term)user).toSrting(), similarity);
    		}
    			
    	}
		return ans;
	}

	/**
	 * 
	 * @param user
	 * @return all the transactions related to the user
	 */
	private FactSetWithSupport getUserTransaction(String user) {
		String query = "SELECT ?x ?val {  ?tran <http://a.org/transactions/By> <http://a.org/transactions/" + user + ">  ."+
	                    "?tran <http://a.org/transactions/WithSupport> ?val ."+
						"?tran <http://a.org/transactions/hasFact> ?x . }";
		ArrayList<SemanticUnit> ans = executeTransactionQuery(query);
		FactSetWithSupport factSet = new FactSetWithSupport();
		
		Iterator<SemanticUnit> iter = ans.iterator();

		while(iter.hasNext())
		{
			Term support = (Term) iter.next();
			double sup = Double.parseDouble(support.toSrting());
	
			Term fact = (Term) iter.next();
			ArrayList<SemanticUnit> factCompoment = getFactCompoments(fact);
			//for(SemanticUnit u: factCompoment)
			Fact f = new Fact((Term)factCompoment.get(0),(Term)factCompoment.get(1),(Term)factCompoment.get(2));
			//System.out.println(f.toSrting());
			FactWithSupport factSupport = new FactWithSupport(sup,f);
			factSet.add(factSupport);
		}
		return factSet;
	}

	private ArrayList<SemanticUnit> getFactCompoments(Term fact) {
		String f = fact.toSrting();
		String query = "SELECT ?x ?y ?z {  <http://a.org/transactions/"+f+"> <http://a.org/transactions/hasSubject> ?x  ."+
                "<http://a.org/transactions/"+f+"> <http://a.org/transactions/hasProperty> ?y  ."+
				"<http://a.org/transactions/"+f+"> <http://a.org/transactions/hasObject> ?z  . }";
		ArrayList<SemanticUnit> ans = executeTransactionQuery(query);
		return ans;
	}

	/**
	 * 
	 * @param currentUserProfile - as a fact-set
	 * @param usersToCompare - list of potentials users
	 * @param s - similarity statement to execute
	 * @return
	 */
    private ArrayList<SemanticUnit> executeProfilesSimilarity(	FactSet currentUserProfile, ArrayList<SemanticUnit> usersToCompare,SimilarityStatment s) {
    	ArrayList<SemanticUnit> ans = new ArrayList<SemanticUnit>();
    	for(SemanticUnit user: usersToCompare)
    	{
    		FactSet userProfile = getUserProfile(((Term)user).toSrting());
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
    			ans.add(user);
    			simProfiles.put(((Term)user).toSrting(), similarity);
    			
    		}
    			
    	}
		return ans;
	}

	/**
     * 
     * @param user
     * @return the user profile (as a fact set)
     */
	private FactSet getUserProfile(String user) {
		String query = "SELECT ?x ?y {  <http://a.org/users/" + user + "> ?x ?y . }";
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
        Model transactions = rdf.getTransactionsModel();
			
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
					result = result.replace("<", "");
					result = result.replace(">", "");
					result = result.replace("=", "");
					result = result.replace("(", "");
					result = result.replace(")", "");
					result = result.replace("http", "");
					result = result.replace("org", "");
					result = result.replace("transactions", "");
					result = result.replaceAll("[\\/\\.\\:]","");
					String [] words = result.split(" ");
					for (int i = 0; i<words.length; i++){
						if (words[i].equals(""))
							continue;
						else if (words[i].contains("?"))
							continue;
						else if (words[i].contains("0"))
						{
							Term t = new Term(words[i]);
							ans.add(t);
						}
						else 
						{
							Term t = new Term(words[i].substring(1));
							ans.add(t);
						}
					}
				}
		return ans;
	}
	}

	private ArrayList<SemanticUnit> executeProfileQuery(String profileQuery) {
		ArrayList<SemanticUnit> ans = new ArrayList<SemanticUnit>();
        Model profiles = rdf.getProfilesModel();
			
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
					result = result.replace("http", "");
					result = result.replace("org", "");
					result = result.replace("users", "");
					result = result.replaceAll("[\\/\\.\\:]","");
					String [] words = result.split(" ");
					for (int i = 0; i<words.length; i++){
						if (words[i].equals(""))
							continue;
						else if (words[i].contains("?"))
							continue;
						else {
							Term t = new Term(words[i].substring(1));
							ans.add(t);
						}
					}
					
				}
		return ans;
	}
	}

	private ArrayList<SemanticUnit> executeOntologyQuery(String ontologyQuery) {
		ArrayList<SemanticUnit> ans = new ArrayList<SemanticUnit>();
           Model ontology = rdf.getOntologyModel();
			
			String queryString = ontologyQuery;
            
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
