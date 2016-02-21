package StackOverflowData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.jena.atlas.json.JsonObject;


public class StackOverflowDataQueries {

	
	
	public static void main(String [] args)
	{
		
		buildProfiles();
		

		
	}

	private static void buildProfiles() {
		 InputStream is = null; 
 	     InputStreamReader isr = null;
 	     BufferedReader br = null;
 	     BufferedWriter bw = null; 

 	      try{
 	         // open input stream test.txt for reading purpose.
 	         is = new FileInputStream("SO users.txt");
 	         
 	         // create new input stream reader
 	         isr = new InputStreamReader(is);
 	         
 	         // create new buffered reader
 	         br = new BufferedReader(isr);
 	      
 	         String line;
 	         
 	        File file = new File("SO-profiles.ttl");

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
		
			StringBuilder s = new StringBuilder();
		
			
 	         // reads to the end of the stream 
 	         while((line = br.readLine()) != null)
 	         {
 	        	s.append(line);
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
}
