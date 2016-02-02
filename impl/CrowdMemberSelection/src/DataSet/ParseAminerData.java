package DataSet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ParseAminerData {
	
	public void parseAMinerCoauthor(){
		
		 InputStream is = null; 
	      InputStreamReader isr = null;
	      BufferedReader br = null;

	      try{
	         // open input stream test.txt for reading purpose.
	         is = new FileInputStream("AMiner-Coauthor.txt");
	         
	         // create new input stream reader
	         isr = new InputStreamReader(is);
	         
	         // create new buffered reader
	         br = new BufferedReader(isr);
	      
	         String line;
	         
	         // reads to the end of the stream 
	        /* while((line = br.readLine()) != null)
	         {
	        	  System.out.println(line);
	         }*/
	         //start with the first 50 lines
	         for (int i = 0; i < 50 ; i++) {
	        	 line = br.readLine();
	             System.out.println(line);
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
	      
	      
	      public void parseAMinerAuthor(){
	  		
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
	 	         
	 	        File file = new File("AMiner-Author-test.ttl");

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
				author.append(line);
				int authorCounter = 0;
	 	         // reads to the end of the stream 
	 	         while((line = br.readLine()) != null)
	 	         {
	 	        	 if(line.contains("#index"))
	 	        	 {
	 	        		 //System.out.println(author.toString());
	 	        		 authorCounter ++;
	 	        		 addNewAuthor(file,author.toString(),bw);
	 	        		 author.delete(0, author.length());
	 	        		 author.append(line);
	 	        		 if(authorCounter == 10)
	 	        			 break;
	 	        	 }
	 	        	 else if (line.equals("")){
	 	        		 continue;
	 	        	 }
	 	        	 
	 	        	 else
	 	        	 {
	 	        		 author.append(line);
	 	        	 }
	 	         }
	 	         //start with the first 50 lines
	 	        /* for (int i = 0; i < 500 ; i++) {
	 	        	 line = br.readLine();
	 	             System.out.println(line);
	 	          }*/
	 	      
	 	      
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
	
		
	      private void addNewAuthor(File file, String string, BufferedWriter bw) {
	    	String[] lines = string.split("#");
	    	String id = lines[1];
	    	String name = lines[2];
	    	name = name.replace("n ", "");
	    	name = name.replace(" ","_");
	    	name = name.replace(".","");
	    	if(name.startsWith("_")){
	    		name = name.substring(1);
	    	}
	    	
	    	
	    	
	    	id = id.replace("index ", "");
	    	int idd = Integer.parseInt(id);
	    	StringBuilder author = new StringBuilder();
	    	author.append("<"+name+"> <instanceOf> <Author> .\n");
	    	author.append("<"+name+"> <id> <"+idd+"> .\n");
	    	
	    	String pc = lines[4];
	    	pc = pc.replace("pc ", "");
	    	
	    	String cn = lines[5];
	    	cn = cn.replace("cn ", "");
	    	
	    	String hi = lines[6];
	    	hi = hi.replace("hi ", "");
	    	
	    	String pi = lines[7];
	    	pi = pi.replace("pi ", "");
	    	
	    	author.append("<"+name+"> <pc> <"+pc+"> .\n");
	    	author.append("<"+name+"> <cn> <"+cn+"> .\n");
	    	author.append("<"+name+"> <h_index> <"+hi+"> .\n");
	    	author.append("<"+name+"> <p_index> <"+pi+"> .\n");
	    	
	    	 String ter = lines[9];
	    	 ter = ter.replace("t ", "");
	    	 String [] terms = ter.split(";");
	    	for (int i = 0; i<terms.length;i++){
	    		String term = terms[i];
	    		term = term.replace(" ", "_");
	    		author.append("<"+term+"> <instanceOf> <Term> .\n");
	    		author.append("<"+name+"> <has_term> <"+term+"> .\n");
	    	}
	    	author.append("\n\n");
	    	try {
	    		
				bw.write(author.toString());
			} 
	    	catch (IOException e) {
				e.printStackTrace();
			}	
	    	//System.out.println(author.toString());
			
		}


		public void parseAMinerPapers(){
		  		
		 		 InputStream is = null; 
		 	      InputStreamReader isr = null;
		 	      BufferedReader br = null;

		 	      try{
		 	         // open input stream test.txt for reading purpose.
		 	         is = new FileInputStream("AMiner-Paper.txt");
		 	         
		 	         // create new input stream reader
		 	         isr = new InputStreamReader(is);
		 	         
		 	         // create new buffered reader
		 	         br = new BufferedReader(isr);
		 	      
		 	         String line;
		 	         
		 	         // reads to the end of the stream 
		 	        /* while((line = br.readLine()) != null)
		 	         {
		 	        	  System.out.println(line);
		 	         }*/
		 	         //start with the first 50 lines
		 	         for (int i = 0; i < 150 ; i++) {
		 	        	 line = br.readLine();
		 	             System.out.println(line);
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
		 
		 ParseAminerData p = new ParseAminerData();
		// p.parseAMinerCoauthor();
		 p.parseAMinerAuthor();
		// p.parseAMinerPapers();
		 
		 
	 }
	      
	     

}
