package DataSet;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import utils.User;

public class PredictCollaborations {
	
	public static Map<String, String> nameToId = new HashMap<String,String>();
	public static Map<String, Integer> nameToNum = new HashMap<String,Integer>();
	public static Map<Integer,String> numToName = new HashMap<Integer, String>();
	public static int[][] collaborationMatrix;
	public static ArrayList<String> Ids = new ArrayList<String>();
	public static ArrayList<String> names = new ArrayList<String>();
	
	public static void main(String [] args)
	{
		/*String Tova = "650652";
		String Noga = "1653815";
		
		getNeighbors(Tova);
		getNeighbors(Noga);
		Ids.add(Tova);
		Ids.add(Noga);
		collaborationMatrix = new int[Ids.size()][Ids.size()];
		buildNeighborsProfiles();

		buildNameTonum();
		buildPapersTran();
	    
		
		int i = getNum("Chris_Faloutsos");
		System.out.println(i);
		
		for(int j = 0; j<Ids.size(); j++)
		{
			int x = collaborationMatrix[i][j];
			if(x != 0)
			{
				String name = numToName.get(j);
				System.out.println(name+ " "+x);
			}
		}*/
		
		/*for(int i = 0; i<Ids.size(); i++)
			for(int j = 0; j<Ids.size(); j++)
			{
				int x = collaborationMatrix[i][j];
				if(x != 0)
					System.out.println(x);
			}*/
		sortFile("Tova collaborations - 2010-2015.txt");		
		
	}
	
	@SuppressWarnings("unchecked")
	private static void sortFile(String string) {
		 InputStream is = null; 
	     InputStreamReader isr = null;
	     BufferedReader br = null;

	      try{
	         // open input stream test.txt for reading purpose.
	         is = new FileInputStream(string);
	         
	         // create new input stream reader
	         isr = new InputStreamReader(is);
	         
	         // create new buffered reader
	         br = new BufferedReader(isr);
	      
	         String line; 
	         
	         ArrayList<Collaborator> ans = new ArrayList<Collaborator>();
	         
	
			
	         // reads to the end of the stream 
	         while((line = br.readLine()) != null)
	         {
	        	 if(! line.equals("")){
	        	 Scanner s = new Scanner(line);
	        	 String name = s.next();
	        	 int n = s.nextInt();
	        	 
	        	 Collaborator c = new Collaborator(name, n);
	        	 ans.add(c);
	        	 }
	        	 
	       
	         }
	        
	       Collections.sort(ans, new Comparator()
	       {

			@Override
			public int compare(Object o1, Object o2) {
				int temp = ((Collaborator)o1).num - ((Collaborator)o2).num;
				return temp;
			}
	    	   
	       });
	       
	       for(Collaborator c: ans)
	    	   System.out.println(c.toString());
	      
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

	private static void buildNeighborsProfiles() {
		 InputStream is = null; 
	     InputStreamReader isr = null;
	     BufferedReader br = null;

	      try{
	         // open input stream test.txt for reading purpose.
	         is = new FileInputStream("AMiner-Author.txt");
	         
	         // create new input stream reader
	         isr = new InputStreamReader(is);
	         
	         // create new buffered reader
	         br = new BufferedReader(isr);
	      
	         String line;
	         
			StringBuilder author = new StringBuilder();
			line = br.readLine();
			author.append(line);
			
	         // reads to the end of the stream 
	         while((line = br.readLine()) != null)
	         {
	        	 if(line.contains("#index"))
	        	 {
	        		
	        		 if(inNeighbors(line)){
	        			addNewAuthor(author.toString());
	 	        		 author.delete(0, author.length());
	 	        		 author.append(line); 
	        		 }
	        		
	        	 }
	        	 else if (line.equals("")){
	        		 continue;
	        	 }
	        	 
	        	 else
	        	 {
	        		 author.append(line);
	        	 }
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
	
	  private static boolean inNeighbors(String line) {
			Scanner s = new Scanner(line);
			String temp = s.next();
			temp = s.next();
			for(int i = 0; i<Ids.size(); i++)
			{
				if(Ids.get(i).equals(temp))
					return true;
			}
			return false;
		}
	
	
	private static void addNewAuthor(String string) {
    	String[] lines = string.split("#");
    	String name = lines[2];
    	
    	name = name.substring(1);
    
    	name = name.replace(" ","_");
    	name = name.replace(".","");
    	if(name.startsWith("_")){
    		name = name.substring(1);
    		
    	}
    	names.add(name);
	}
	
	
	private static void addCollaborations(ArrayList<String> relevantAuthors)
	{
		for(String name: relevantAuthors)
		{
			for(String otherName: relevantAuthors)
			{
				if(!name.equals(otherName))
				{
					int i = getNum(name);
					int j = getNum(otherName);
					int x = collaborationMatrix[i][j];
					collaborationMatrix[i][j] = collaborationMatrix[i][j]+1;
				}
			}
		}
		
	}
	
	private static void buildPapersTran() {
		
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
	        
			
			
		
			StringBuilder paper = new StringBuilder();
			line = br.readLine();
			paper.append(line);
		
	         // reads to the end of the stream 
	         while((line = br.readLine()) != null)
	         {
	        	 if(line.contains("#index"))
	        	 {
	        		
	        			addNewPaper(paper.toString());
	 	        		 paper.delete(0, paper.length());
	 	        		 paper.append(line); 
	        		
	        	 }
	        	 else if (line.equals("")){
	        		 continue;
	        	 }
	        	 
	        	 else
	        	 {
	        		 paper.append(line);
	        	 }
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
						e.printStackTrace();
					}
	         }
		
		
	}
	}
	
	private static int getNum(String name) {
		return nameToNum.get(name);
	
	}
	
	private static void addNewPaper(String string) {
		String[] lines = string.split("#");
		
		String year = null;
		
		//only papers before 2010 and after 2005
		for(int i=0;i<lines.length;i++){
			if(lines[i].startsWith("t ")){
				year = lines[i];
				year = year.substring(2);
				if(year.equals(""))
					return;
				int y = Integer.parseInt(year);
				if(y < 2010 )
					return;
			}
				
		}
		if(year == null)
    		return;
		


    	
    	String [] authors = null;
    	
    	for(int i=0;i<lines.length;i++){
			if(lines[i].startsWith("@ ")){
				String temp = lines[i];
				temp = temp.substring(2);
				authors = temp.split(";");
				break;
			}
				
		}
    	
    	ArrayList<String> relevantAuthors = new ArrayList<>();
    	for(int i=0; i<authors.length;i++){
    		String name = authors[i];
    		name = name.replace(" ","_");
	    	name = name.replace(".","");
	    	if(name.startsWith("_")){
	    		name = name.substring(1);
	    		
	    	}
    		if(contains(names, name))
    			relevantAuthors.add(name);
    	}
    	
    	if(relevantAuthors.size() == 0)
    		return;
    	addCollaborations(relevantAuthors);
  
    	
    	
	}
	
	private static boolean contains(ArrayList<String> names2, String string) {
		for(String s: names2)
			if(s.equals(string))
				return true;
		return false;
	}
	private static void buildNameTonum() {
		int i = 0;
		for(String name: names)
		{
			nameToNum.put(name, i);
			numToName.put(i,name);
			i++;
		}
		
	}
	public static void getNeighbors(String ID)
	{
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
	         while((line = br.readLine()) != null)
	         {
	        	 if(line.contains(ID)){
	        		 line = line.substring(1);
	        		// System.out.println(line); 
	        		 Scanner s = new Scanner(line);
	        		 String i = s.next();
	        		 String j = s.next();
	        		 if(i.equals(ID))
	        			 addNewId(j);
	        		 else
	        			 addNewId(i);
	        		 s.close();
	        	
	        	 }
	        		
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
	
    private static void addNewId( String j) {
    	boolean flag = false;
		for(String id: Ids){
			if(id.equals(j)){
				flag = true;
				break;
			}
				
		}
		if(!flag)
			Ids.add(j);
		
	}
	}
