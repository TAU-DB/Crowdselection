package utils;


import java.util.ArrayList;
import java.util.Scanner;

public class Parser {
	
	private String query;
	private String userName;

	private String queryToExecute;
	private ArrayList<SimilarityStatment> similarityStatments;
	private ArrayList<Filter> filters;
	
	public Parser(String q){
		query = q;
		similarityStatments = new ArrayList<SimilarityStatment>();
		filters = new ArrayList<Filter>();
	}
	
	//getters
	public String getUserName(){
		return userName;
	}
	
	
	public String getQueryToExecute(){
		return queryToExecute;
	}
	
	public ArrayList<SimilarityStatment> getSimilarityStatments(){
		return similarityStatments;
	}
	
	public ArrayList<Filter> getFilters(){
		return filters;
	}
	
	
	//parsers
	public void parseQuery()
	{
		parseUserName();
		parseQueryToExecute();
		parseSimilarityStatments();
		parseFilters();
		
	}

	private void parseQueryToExecute() {
		StringBuilder QTE = new StringBuilder("SELECT ?u WHERE {");
		
		
		
		String profileQuery = appendProfilesQuery();
		if(profileQuery != null)
			QTE.append(profileQuery);
		
		String transactionsQuery = appendTransactionsQuery();
		if(transactionsQuery != null)
			QTE.append(transactionsQuery);
		
		String ontologyQuery = appendOntologyQuery();
		if(ontologyQuery != null)
			QTE.append(ontologyQuery);
		
		QTE.append("}");
		queryToExecute = QTE.toString();
		
		
	}

	private String appendOntologyQuery() {
		//get the "FROM ontology" part
		int i = query.indexOf("FROM ontology");
		if (i != -1){
			String ontologyQuery = query.substring(i);
			i = ontologyQuery.indexOf("}");
			ontologyQuery = ontologyQuery.substring(0,i+1);
				
			//construct new SPARQL query
			return buildSPARQLOntologyQuery(ontologyQuery);
		}
		return null;
	}

	private String appendProfilesQuery() {
		//get the "FROM profile" part
		int i = query.indexOf("FROM profile");
		if(i != -1){
			String profilesQuery = query.substring(i);
			i = profilesQuery.indexOf("}");
			profilesQuery = profilesQuery.substring(0,i+1);
		
			//construct new SPARQL query
			return buildSPARQLProfileQuery(profilesQuery);
		}
		return null;
	}

	private String appendTransactionsQuery() {
		int i = query.indexOf("FROM tran");
		if (i != -1){
		String transactionsQuery = query.substring(i);
		i = transactionsQuery.indexOf("}");
		transactionsQuery = transactionsQuery.substring(0,i+1);
		
		String support = null;
		//add Filters
		if(query.contains("WITH SUPPORT")){
			i = query.indexOf("WITH SUPPORT"); 
			String supportFilter = query.substring(i);
			Scanner s = new Scanner(supportFilter);
			s.next();
			s.next();
			support = 	s.next()+" "+s.next()+" "; 
			s.close();
			
		}
		
		//construct new SPARQL query
		return buildSPARQLTransactionsQuery(support,transactionsQuery);
		
	}
	return null;
	}

	private void parseFilters() {
		int i = query.indexOf("ORDER");
		if (i != -1){
			String s = query.substring(i);
			Scanner scan = new Scanner(s);
			String filter = scan.next() + " "+ scan.next();
			String f = scan.next();
			FilterOrder filte = new FilterOrder(filter, f);
			filters.add(filte);
			scan.close();
		}
		i = query.indexOf("LIMIT");
		if (i != -1){
			
			String s = query.substring(i);
			Scanner scan = new Scanner(s);
			String filter = scan.next();
			String f = scan.next();
			int limit = Integer.parseInt(f);
			FilterLimit filte = new FilterLimit(filter, limit);
			filters.add(filte);
			scan.close();
			
		}
	}

	private void parseSimilarityStatments() {
		
		//get the Similarity statements part
		int i = query.indexOf("SIMILAR");
		String similarityStatment = query.substring(i);
		String [] s = similarityStatment.split("SIMILAR ");
		//create all similarity statements
		for(i =1; i<s.length; i++)
		{
			Scanner scan = new Scanner(s[i]);
			String src = scan.next();
			//to
			scan.next();
			String dst;
			String op;
			String TH;
			if(!s[i].contains("{"))
			{
				dst = scan.next();
				scan.next();
				scan.next();
				op = scan.next();
				TH = scan.next();
			}
			
			//similar to transaction
			else
			{
				int j = s[i].indexOf("{");
				int k = s[i].indexOf("}");
				dst = s[i].substring(j+1, k);
				s[i] = s[i].replace(dst, "");
				s[i] = s[i].replace("{", "");
				s[i] = s[i].replace("}", "");
				Scanner ss = new Scanner(s[i]);
				ss.next();
				ss.next();
				ss.next();
				ss.next();
				op = ss.next();
				TH = ss.next();
				ss.close();
				dst = "{ "+ dst + " }";
				
			}
			
			scan.close();
	
			double sim = Double.parseDouble(TH);
			SimilarityStatment ss = new SimilarityStatment(src,dst, op,sim);
			similarityStatments.add(ss);
			
		}
		
		
	}


	private String buildSPARQLTransactionsQuery(String support, String transactionsQuery) {

		transactionsQuery = transactionsQuery.replace("$", "?");
		int i = transactionsQuery.indexOf("?");
				
		//get the variable name
		//char var = transactionsQuery.charAt(i+1);
		//System.out.println(var);
		
		//Replace prefix "From profile(?var)" to "SELECT ?var"
		i =  transactionsQuery.indexOf(")");
		transactionsQuery = transactionsQuery.substring(i+1);
	
		
		//build according to transaction Model
		return buildTransactions('u',support,transactionsQuery );
		
	}

	private String buildTransactions(char var, String support, String transactionsQuery) {
		int j = transactionsQuery.indexOf("{");
		int k = transactionsQuery.indexOf("}");
		String trans = transactionsQuery.substring(j+1, k);
		String [] t = trans.split(" \\.");
		StringBuilder newTrans = new  StringBuilder();
		newTrans.append("	?tran <http://a.org/ontology/By> ?"+var+" .");
	
		
		//System.out.println(trans);
		for(int m = 0; m < t.length; m++)
		{
			String [] terms = t[m].split(" ");
			int x = firstWord(terms);
			if(x>0 && x+2 <= terms.length){
				newTrans.append("	?tran <http://a.org/ontology/hasFact> ?fact .");
				newTrans.append("	?fact <http://a.org/ontology/hasSubject> "+terms[x]+" .");
				newTrans.append("	?fact <http://a.org/ontology/hasProperty> "+terms[x+1]+" .");
				newTrans.append("	?fact <http://a.org/ontology/hasObject> "+terms[x+2]+" .");
			}
			
		}
		if(support != null){
			newTrans.append("	?tran <http://a.org/ontology/WithSupport> ?value .");
			newTrans.append("      FILTER ( ?value "+support+" ) .");
		}
		transactionsQuery = transactionsQuery.replace(trans, newTrans.toString());
		transactionsQuery = transactionsQuery.replace("WHERE", "");
		transactionsQuery = transactionsQuery.replace("{", "");
		transactionsQuery = transactionsQuery.replace("}", "");
		return transactionsQuery;
		
		
	}

	private int firstWord(String[] terms) {
		for(int i = 0; i<terms.length; i++)
			if (!terms[i].equals(""))
				return i;
		return 0;
	}


	private String buildSPARQLProfileQuery(String profilesQuery) {
		//change to SPARQL syntax
		profilesQuery = profilesQuery.replace("$", "?");
		int i = profilesQuery.indexOf("?");
				
	
		
		//Replace prefix "From profile(?var)" to "SELECT ?var"
		i =  profilesQuery.indexOf(")");
		profilesQuery = profilesQuery.substring(i+1);
		//profilesQuery = "SELECT ?"+var+ profilesQuery;
		profilesQuery = profilesQuery.replace("WHERE", "");
		profilesQuery = profilesQuery.replace("{", "");
		profilesQuery = profilesQuery.replace("}", "");
		return profilesQuery;
		
	}


	private String buildSPARQLOntologyQuery(String ontologyQuery) {
		//change to SPARQL syntax
		ontologyQuery = ontologyQuery.replace("$", "?");
		
		
		//Replace prefix "From ontology" to "SELECT ?var"
		ontologyQuery = ontologyQuery.replaceAll("FROM ontology", "");
		//System.out.println(ontologyQuery);
		
		ontologyQuery = ontologyQuery.replace("WHERE", "");
		ontologyQuery = ontologyQuery.replace("{", "");
		ontologyQuery = ontologyQuery.replace("}", "");
		return ontologyQuery;
		
	}

	public void parseUserName() {
		//get the first line
		int i = query.indexOf("FROM");
		userName = query.substring(0, i);
		//drop ASSIGN BY 
		userName = userName.substring(10);
		String [] words = userName.split(" ");
		//the user name is the first word
		userName = words[0];
	
	}
	
	
	
	public static void main(String[] args) {
		
		String queryString = 
				"ASSIGN BY Ann to $u" +
						"FROM profile($u) WHERE{" +
						"      $u <http://a.org/ontology/livesIn> <http://a.org/ontology/Sydney> ." +
						"      }"+
						"FROM tran($u) WHERE{" +
			             "      <http://a.org/ontology/Sport> <http://a.org/ontology/doAt> $x ." +
			             "      }"+
						"WITH SUPPORT > 0.02 "+
						"FROM ontology WHERE{" +
						"      $x <http://a.org/ontology/instanceOf> <http://a.org/ontology/Place> ." +
						"      $x <http://a.org/ontology/near> <http://a.org/ontology/Sydney> ." +
						"      }"+ 
				        "SIMILAR profile($u) TO profile(Ann) "+
						"WITH SIMILARITY >= 0.75 "+
						"SIMILAR tran($u) TO tran(Ann) "+
						"WITH SIMILARITY >= 0.75 "+
						"WITH SIMILARITY  >= 0 " +
						"LIMIT 1 ";
				
		      
		
		Parser p = new Parser(queryString);
		p.parseUserName();
		p.parseQueryToExecute();
		p.parseSimilarityStatments();
		p.parseFilters();
		
		
		System.out.println(p.getUserName());
		System.out.println(p.queryToExecute);
		//System.out.println(p.getOntologyQuery());
		//System.out.println(p.getProfileQuery());
		//System.out.println(p.getTransactionsQuery());
		for(SimilarityStatment s: p.getSimilarityStatments())
			System.out.println(s);
		for(Filter s: p.getFilters())
			System.out.println(s);
		

	}

}
