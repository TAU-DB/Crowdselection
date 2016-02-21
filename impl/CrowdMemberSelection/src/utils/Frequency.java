package utils;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;

public class Frequency{
	
	private static HashMap<String, Double> dictionary = new HashMap<String, Double>();
	private static HashMap<Integer, Double> Hindexdictionary = new HashMap<Integer, Double>();
	private static HashMap<Integer, Double> PCdictionary = new HashMap<Integer, Double>();
	private static HashMap<Integer, Double> ARdictionary = new HashMap<Integer, Double>();
	private static HashMap<Integer, Double> Golddictionary = new HashMap<Integer, Double>();
	private static HashMap<Integer, Double> Bronzedictionary = new HashMap<Integer, Double>();
	private static HashMap<Integer, Double> Silverdictionary = new HashMap<Integer, Double>();

	private static int num;
	private static int numPC;
	private static int numAR;
	private static int numGold;
	private static int numSilver;
	private static int numBronze;
	/**
	 * 
	 * @param 
	 * @return
	 * @throws IOException 
	 */
	public static void buildWordCountDB(RDF rdf) throws IOException{
		
		
		 // buildEntitiesFrequencies();
		buildAMinerEntitiesFrequencies(rdf);
		//buildAcceptRateFrequencies();
		//buildGoldFrequencies();
		//buildSilverFrequencies();
		//buildBronzeFrequencies();
		buildHIndexFrequencies();
		buildPCFrequencies();
	}

	private static void buildGoldFrequencies() throws FileNotFoundException {
		 Scanner sc = new Scanner(new File("gold.txt"));
		    while(sc.hasNext())
		    {
		    	String line = sc.nextLine();
		    	String [] count = line.split("\\t");
		
		     //   count = count.substring(0,count.length()-3);
		        
		    	Golddictionary.put(Integer.parseInt(count[0]), Double.parseDouble(count[1]));
		    }
		 sc.close();
		 
		 //normalize the count to be within [0,1]
		 Collection<Double> count = Golddictionary.values();
		 numGold = 0;
	
		 for(Double d: count){
			 numGold += d;

		 }
		
	}
	
	private static void buildBronzeFrequencies() throws FileNotFoundException {
		 Scanner sc = new Scanner(new File("bronze.txt"));
		    while(sc.hasNext())
		    {
		    	String line = sc.nextLine();
		    	String [] count = line.split("\\t");
		
		     //   count = count.substring(0,count.length()-3);
		        
		    	Bronzedictionary.put(Integer.parseInt(count[0]), Double.parseDouble(count[1]));
		    }
		 sc.close();
		 
		 //normalize the count to be within [0,1]
		 Collection<Double> count = Bronzedictionary.values();
		 numBronze = 0;
	
		 for(Double d: count){
			 numBronze += d;

		 }
		
	}
	
	private static void buildSilverFrequencies() throws FileNotFoundException {
		 Scanner sc = new Scanner(new File("silver.txt"));
		    while(sc.hasNext())
		    {
		    	String line = sc.nextLine();
		    	String [] count = line.split("\\t");
		
		     //   count = count.substring(0,count.length()-3);
		        
		    	Silverdictionary.put(Integer.parseInt(count[0]), Double.parseDouble(count[1]));
		    }
		 sc.close();
		 
		 //normalize the count to be within [0,1]
		 Collection<Double> count = Silverdictionary.values();
		 numSilver = 0;
	
		 for(Double d: count){
			 numSilver += d;

		 }
		
	}


	private static void buildAMinerEntitiesFrequencies(RDF rdf) {
		Model transactions = rdf.getOntologyModel();
		

		
		String queryString = "SELECT ?x ?z { ?x <http://a.org/ontology/instanceof> ?z . }";
		
     
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
				result = result.replace("<http://a.org/ontology/", "");
				result = result.replace(">", "");
				result = result.replace("=", "");
				result = result.replace("(", "");
				result = result.replace(")", "");
				Scanner s = new Scanner(result);
				s.next();
				String term = s.next();
				s.next();
				String term2 = s.next();
				addTerm(term);
				addTerm(term2);
			}
		}
		catch(Exception e)
		{
			
		}
				
		normalDictionary();
	}


	private static void addTerm(String term) {
		if(dictionary.containsKey(term))
		{
			double val = dictionary.get(term);
			dictionary.replace(term, val, val+1);
		}
		else
		{
			dictionary.put(term, 1.0);
		}
			
		
	}

	private static void buildEntitiesFrequencies() throws FileNotFoundException, NumberFormatException {
		dictionary = new HashMap<String, Double>();
		  Scanner sc = new Scanner(new File("w1_.txt"));
		    while(sc.hasNext())
		    {
		        String word = sc.next();
		        String count = sc.next();
		     //   count = count.substring(0,count.length()-3);
		        
		        dictionary.put(word, Double.parseDouble(count));
		    }
		 sc.close();
		 
		 
		
		  sc = new Scanner(new File("w2_.txt"));
		    while(sc.hasNext())
		    {
		    	String count = sc.next();
		        String word = sc.next();
		        word = word +"_"+sc.next();
		     //   count = count.substring(0,count.length()-3);
		        
		        dictionary.put(word, Double.parseDouble(count));
		    }
		 sc.close();
		 
		 normalDictionary();
	}

	private static void normalDictionary() {
		//normalize the count to be within [0,1]
		 Collection<Double> values = dictionary.values();
		 double max =0;
		 double min = Double.POSITIVE_INFINITY;
		 for(Iterator<Double> iterator = values.iterator(); iterator.hasNext();)
		 {
			 double val = (double) iterator.next();
			 if( val <min)
				 min = val;
		 }
		 min = min - 0.001;
		 for(Iterator<Double> iterator = values.iterator(); iterator.hasNext();)
		 {
			 double val = (double) iterator.next();
			 if(max < val)
				 max = val;
		 }
		 Collection<Entry<String, Double>> entries = dictionary.entrySet();
		 for(Iterator<Entry<String, Double>> iterator = entries.iterator(); iterator.hasNext();)
		 {
			 Entry<String, Double> entry =  iterator.next();
			 double newVal = (entry.getValue()-min)/(max - min);
			 if(newVal < 0 || newVal > 1)
				 newVal = 0;
			// if(entry.getKey().equals("PODS")|| entry.getKey().equals("ICDT")|| entry.getKey().equals("SODA")||entry.getKey().equals("IEEE")||entry.getKey().equals("VLDB")||entry.getKey().equals("SIGMOD"))
				// newVal = Math.sqrt(newVal);
			 dictionary.replace(entry.getKey(), entry.getValue(), newVal);
			
		 }
	}
	
	private static void buildPCFrequencies() throws FileNotFoundException {
		  Scanner sc = new Scanner(new File("PC.txt"));
		    while(sc.hasNext())
		    {
		    	String line = sc.nextLine();
		    	String [] count = line.split("\\t");
		
		     //   count = count.substring(0,count.length()-3);
		        
		    	PCdictionary.put(Integer.parseInt(count[0]), Double.parseDouble(count[1]));
		    }
		 sc.close();
		 
		 //normalize the count to be within [0,1]
		 Collection<Double> count = PCdictionary.values();
		 numPC = 0;
	
		 for(Double d: count){
			 numPC += d;

		 }
		
	}
	private static void buildAcceptRateFrequencies() throws FileNotFoundException {
		  Scanner sc = new Scanner(new File("accept_rate.txt"));
		    while(sc.hasNext())
		    {
		    	String line = sc.nextLine();
		    	String [] count = line.split("\\t");
		
		     //   count = count.substring(0,count.length()-3);
		        
		    	ARdictionary.put(Integer.parseInt(count[0]), Double.parseDouble(count[1]));
		    }
		 sc.close();
		 
		 //normalize the count to be within [0,1]
		 Collection<Double> count = ARdictionary.values();
		 numAR = 0;
	
		 for(Double d: count){
			 numAR += d;

		 }
		
	}

	private static void buildHIndexFrequencies() throws FileNotFoundException {
		
		  Scanner sc = new Scanner(new File("H_Index.txt"));
		    while(sc.hasNext())
		    {
		    	String line = sc.nextLine();
		    	String [] count = line.split("\\t");
		
		     //   count = count.substring(0,count.length()-3);
		        
		    	Hindexdictionary.put(Integer.parseInt(count[0]), Double.parseDouble(count[1]));
		    }
		 sc.close();
		 
		 //normalize the count to be within [0,1]
		 Collection<Double> count = Hindexdictionary.values();
		 num = 0;
		 for(Double d: count)
			 num += d;	 
		
	}

	private static double getRange(int i, int j, HashMap<Integer, Double> hindex) {
		double ans = 0;
		int min = i;
		int max = j;
		if(j<i)
		{
			min = j;
			max = i;
		}
		for(int k = min; k <= max; k++){
			if(hindex.containsKey(k))
				ans += hindex.get(k);
		}
		return ans;
	}
	
	
	/**
	 * 
	 * @param unit
	 * @return the frequency of that term
	 */
	public static double getFrequency(SemanticUnit unit)
	{
		double ans = 1;
		if(unit instanceof Term)
		{
			String u = ((Term)unit).toSrting();
			if(dictionary.containsKey(u))
					ans = dictionary.get(u);
		}
		
		else if(unit instanceof Fact)
		{
			Term sub = ((Fact)unit).getSubject();
			Term obj = ((Fact)unit).getObject();
			double s1 = getFrequency(sub);
			double s2 = getFrequency(obj);
			ans = Math.min(s1, s2);
		}
		else if(unit instanceof FactSet)
		{
			//never used
		}
		return ans;
	}
	/**
	 * 
	 * @param unit
	 * @return the frequency of the semantic unit
	 */
	public static double getFrequencyOld(SemanticUnit unit){
		
		if(unit instanceof Term)
		{
			Term t = (Term)unit;
			boolean notOneWord = isOneWord(t.toSrting());
			
			String key = getKey(t.toSrting());
			
			if(key == null && notOneWord)
			{
				String [] words = t.toSrting().split("_");
				if(words.length >= 2)
				{
					String key1 = getKey(words[0]);
					String key2 = getKey(words[1]);
					if(dictionary.containsKey(key1) && dictionary.containsKey(key2)){
						double ans = dictionary.get(key1);
						ans *= dictionary.get(key2);
						return ans;
					}
						
				}
			}
			else{
				if(dictionary.containsKey(key))
					 return dictionary.get(key);
			}
		}
		else if (unit instanceof Fact)
		{
			Fact fact = (Fact)unit;
			Term obj = fact.getObject();
			Term sub = fact.getSubject();
			boolean notOneWordObj = isOneWord(obj.toSrting());
			boolean notOneWordSub = isOneWord(sub.toSrting());
			
			String key1 = getKey(obj.toSrting());
			double f1 = 0;
			if(key1 == null && notOneWordObj)
			{
				String [] words = obj.toSrting().split("_");
				if(words.length >= 2)
				{
					String s1 = getKey(words[0]);
					String s2 = getKey(words[1]);
					if(dictionary.containsKey(s1) && dictionary.containsKey(s2)){
						f1 = dictionary.get(s1);
						f1 *= dictionary.get(s2);
					
					}
						
				}
			}
			else
				f1 = dictionary.get(key1);
			
			String key2 = getKey(sub.toSrting());
			
			double f2 = 0;
			if(key2 == null && notOneWordSub)
			{
				String [] words = sub.toSrting().split("_");
				if(words.length >= 2)
				{
					String s1 = getKey(words[0]);
					String s2 = getKey(words[1]);
					if(dictionary.containsKey(s1) && dictionary.containsKey(s2)){
						f2 = dictionary.get(s1);
						f2 *= dictionary.get(s2);
					
					}
						
				}
			}
			else
				f2 = dictionary.get(key2);
			if(f1 > 0 && f2 > 0)
				return f1*f2;
			else if(f1 > 0)
				return f1;
			else if (f2 > 0)
				return f2;
		}
		
		else if (unit instanceof FactSet)
		{
			FactSet facts = (FactSet)unit;
			double ans = 1;
			Iterator<Fact> iter = facts.iterator();
			while(iter.hasNext())
			{
				Fact fact = iter.next();
				Term obj = fact.getObject();
				Term sub = fact.getSubject();
				String key1 = getKey(obj.toSrting());
				String key2 = getKey(sub.toSrting());
				
				//return the min IC
				double temp = 0;
				boolean flag = false;
				if(dictionary.containsKey(key1) && dictionary.containsKey(key2)){
					temp= dictionary.get(key1)*dictionary.get(key2);
					flag = true;
				}
				if(temp<ans && flag && temp >0)
					temp = ans;
				
				
				//old
				/*if(dictionary.containsKey(key1) && dictionary.containsKey(key2))
					ans *= dictionary.get(obj.toSrting())*dictionary.get(sub.toSrting());*/
			}
			return ans;
			
		}
		return 0;	
		
	}
	
	private static boolean isOneWord(String srting) {
		return srting.contains("_");
	}

	private static String getKey(String srting) {
		srting = srting.toLowerCase();
		Set<String> words = dictionary.keySet();
		Iterator<String> iter = words.iterator();
		String ans = null;
		while(iter.hasNext())
		{
			String temp = iter.next();
			if(temp.equals(srting))
			{
				ans = temp;
				break;
			}
		}
		return ans;
	}

	public static void main(String[] args) throws IOException {
		
	    
		//buildWordCountDB();
		Term t = new Term ("Relational_Databases");
		Term t1 = new Term ("Web_Services");
		Term t2 = new Term ("Boolean_Functions");
		
		Fact f1 = new Fact(new Term("Author"), new Term("keyTerm"), new Term("Relational_Databases"));
		Fact f2 = new Fact(new Term("Author"), new Term("keyTerm"), new Term("Term"));
	
			double a = getFrequency(f1);
			System.out.println(f1.toSrting()+" "+a);
		
		
	
			double a1 = getFrequency(f2);
			System.out.println(f2.toSrting()+" "+a1);

		
		
			//double a2 = getFrequency(t2);
			//System.out.println("Boolean_Functions:"+a2);
		
		
		
		

		}

	
	public static double getFrequencyHindex(int h_index, int h_index2) {
		double ans = 0;
		ans = getRange(h_index, h_index2, Hindexdictionary);
		ans = ans/num;	
		
		return ans;
	}

	public static double getFrequencyPC(int pc, int pc2) {
		double ans = 0;
		ans = getRange(pc, pc2, PCdictionary);
		ans = ans/numPC;
	
		return ans;
	}
	
	public static double getFrequencyAcceptRate(int pc, int pc2) {
		double ans = 0;
		ans = getRange(pc, pc2, ARdictionary);
		ans = ans/numAR;
	
		return ans;
	}
	
	public static double getFrequencyGold(int pc, int pc2) {
		double ans = 0;
		ans = getRange(pc, pc2, Golddictionary);
		ans = ans/numGold;
	
		return ans;
	}
	
	public static double getFrequencyBronze(int pc, int pc2) {
		double ans = 0;
		ans = getRange(pc, pc2, Bronzedictionary);
		ans = ans/numBronze;
	
		return ans;
	}
	
	public static double getFrequencySilver(int pc, int pc2) {
		double ans = 0;
		ans = getRange(pc, pc2, Silverdictionary);
		ans = ans/numSilver;
	
		return ans;
	}
	

}
