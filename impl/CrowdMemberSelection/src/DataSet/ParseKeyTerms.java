package DataSet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class ParseKeyTerms {
	
	private static Set<String> keyterms = new HashSet<String>();

	public static void parseAMinerKeyTermsOntology(){
  		
		 InputStream is = null; 
	     InputStreamReader isr = null;
	     BufferedReader br = null;
	     BufferedWriter bw = null; 

	      try{
	         // open input stream test.txt for reading purpose.
	        // is = new FileInputStream("Areas_of_computer_science_ontology _with_pages.ttl");
	         is = new FileInputStream("aminer_bottom_up_ontology_1000.ttl");
	        
	         
	         // create new input stream reader
	         isr = new InputStreamReader(is);
	         
	         // create new buffered reader
	         br = new BufferedReader(isr);
	      
	         String line;
	         
	        File file = new File("AMiner-KeyTerms Ontology.ttl");

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			Scanner s;
		
	         // reads to the end of the stream 
	         while((line = br.readLine()) != null)
	         { 
	        	line = line.replace("http://dbpedia.org/resource/", "");
	        	line = line.replace("Category:", "");
	        	s = new Scanner(line);
	        	String term1 = s.next();
	        	
	        	//if(keyterms.contains(term1)){
	        		s.next();
	        		String term2 = s.next();
	        		if(term1.equals(term2))
	        			continue;
	        		String str = "<"+term1+"> <instanceof> <"+term2+"> .\n";
	        		bw.write(str);
	        	//}
	  
	         }
	      
	         }
	      catch(Exception e){
	             e.printStackTrace();
	         }
	      finally{
	          // releases resources associated with the streams	
	    	 if(bw !=null)
				try {
					bw.close();
				} 
	    	 catch (IOException e1) {
					e1.printStackTrace();
				}
	  
	            if(is!=null){
					try {
						is.close();
					}
	            	catch (IOException e) {
						e.printStackTrace();
					}
	            }
	            if(isr!=null){
					try {
						isr.close();
					} 
	            	catch (IOException e) {
						e.printStackTrace();
					}
	            }
	            if(br!=null){
					try {
						br.close();
					} 
	            	catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	         }
	      }
}
	
	private static void buildKeyTermsSet() {
		 InputStream is = null; 
 	     InputStreamReader isr = null;
 	     BufferedReader br = null;

 	      try{
 	         // open input stream test.txt for reading purpose.
 	         is = new FileInputStream("aminer_crawling_output_dbpedia_terms.tsv");
 	         
 	         // create new input stream reader
 	         isr = new InputStreamReader(is);
 	         
 	         // create new buffered reader
 	         br = new BufferedReader(isr);
 	      
 	         String line;
 	         
			br.readLine();
 	         // reads to the end of the stream 
 	         while((line = br.readLine()) != null)
 	         {
 	        	 Scanner s = new Scanner(line);
 	        	 s.useDelimiter("\\t");
 	        	 s.next();
 	        	 s.next();
 	        	 s.next();
 	        	 if(s.hasNext()){
 	        		 String terms = s.next();
 	        		 s = new Scanner(terms);
 	        		 s.useDelimiter(";");
 	        		 while(s.hasNext()){
 	        			 String term = s.next();
 	        			 if(!keyterms .contains(term))
 	        				 keyterms.add(term);
 	        		 }
 	        	 }
 	        	 s.close();
 	         }
 	    
 	      
 	         }
 	      catch(Exception e){
 	             e.printStackTrace();
 	         }
 	      finally{
 	          // releases resources associated with the streams	
 	  
 	            if(is!=null){
 					try {
 						is.close();
 					}
 	            	catch (IOException e) {
 						e.printStackTrace();
 					}
 	            }
 	            if(isr!=null){
 					try {
 						isr.close();
 					} 
 	            	catch (IOException e) {
 						e.printStackTrace();
 					}
 	            }
 	            if(br!=null){
 					try {
 						br.close();
 					} 
 	            	catch (IOException e) {
 						// TODO Auto-generated catch block
 						e.printStackTrace();
 					}
 	         }
 	      }
		
		
	}
	
	public static void main(String[] args){
		buildKeyTermsSet();
		System.out.println(keyterms.size());
		parseAMinerKeyTermsOntology();
		
		
	}



}
