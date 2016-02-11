package DataSet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ParseKeyTerms {
	
	public static void parseAMinerAuthor(){
  		
		 InputStream is = null; 
	     InputStreamReader isr = null;
	     BufferedReader br = null;
	     BufferedWriter bw = null; 

	      try{
	         // open input stream test.txt for reading purpose.
	         is = new FileInputStream("AMiner-Author.txt");
	         
	         // create new input stream reader
	         isr = new InputStreamReader(is);
	         
	         // create new buffered reader
	         br = new BufferedReader(isr);
	      
	         String line;
	         
	        File file = new File("AMiner-KeyTerms.ttl");

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			String content = "@base <http://a.org/author/> .\n\n";
			bw.write(content);

			StringBuilder author = new StringBuilder();
			line = br.readLine();
			int count = 0;
		
	         // reads to the end of the stream 
	         while(line  != null)
	         { 
	        	 String prev = line;
	        	 line = br.readLine();
	        	 if(line.contains("Tova M"))
	        	 {
	        		 //line = line.replace("#t", "");
	        		 line = line + "\n";
	        		 bw.write(line);
	        		 count++;
	        		 if(count>50)
	        			 break;
	        		
	        	 }
	  
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
	
	public static void main(String[] args){
		
		parseAMinerAuthor();
		
		
	}

}
