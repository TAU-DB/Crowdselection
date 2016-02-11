package utils;


import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

public class Frequency{
	
	private static HashMap<String, Double> dictionary;
	/**
	 * 
	 * @param 
	 * @return
	 * @throws IOException 
	 */
	public static void buildWordCountDB() throws IOException{
		
          //get all words from file		
		  dictionary = new HashMap<String, Double>();
		  Scanner sc = new Scanner(new File("wordCount.txt"));
		    while(sc.hasNext())
		    {
		        String word = sc.next();
		        String count = sc.next();
		        count = count.substring(0,count.length()-3);
		        dictionary.put(word, Double.parseDouble(count));
		    }
		 sc.close();
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
			 dictionary.replace(entry.getKey(), entry.getValue(),(entry.getValue()-min)/max );
			
		 }
	
		
	}
	
	/**
	 * 
	 * @param unit
	 * @return the frequency of the semantic unit
	 */
	public static double getFrequency(SemanticUnit unit){
		
		if(unit instanceof Term)
		{
			Term t = (Term)unit;
			String key = getKey(t.toSrting());
			if(dictionary.containsKey(key))
				return dictionary.get(key);
		}
		else if (unit instanceof Fact)
		{
			Fact fact = (Fact)unit;
			Term obj = fact.getObject();
			Term sub = fact.getSubject();
			String key1 = getKey(obj.toSrting());
			String key2 = getKey(sub.toSrting());
			if(dictionary.containsKey(key1) && dictionary.containsKey(key2))
				return dictionary.get(key1)*dictionary.get(key2);
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
					temp= dictionary.get(obj.toSrting())*dictionary.get(sub.toSrting());
					flag = true;
				}
				if(temp<ans && flag)
					temp = ans;
				
				
				//old
				/*if(dictionary.containsKey(key1) && dictionary.containsKey(key2))
					ans *= dictionary.get(obj.toSrting())*dictionary.get(sub.toSrting());*/
			}
			return ans;
			
		}
		return 0;	
		
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
		
	    
		buildWordCountDB();
		Term t = new Term ("Sport");
		String key = getKey(t.toSrting());
		if(dictionary.containsKey(key)){
			double a = dictionary.get(key);
			System.out.println(a);
		}
			
		

		}
	

}
