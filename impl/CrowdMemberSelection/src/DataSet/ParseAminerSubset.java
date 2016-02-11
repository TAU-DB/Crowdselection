package DataSet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ParseAminerSubset {

	public static ArrayList<String> countryCode = new ArrayList<String>();
	public static ArrayList<String> affiliation = new ArrayList<String>();
	public static Map<String, String> affiliationToCountry = new HashMap<String,String>();
	
	public static ArrayList<String> Ids = new ArrayList<String>();
	public static ArrayList<String> names = new ArrayList<String>();
	public static Map<String, String> authors = new HashMap<String,String>();
	
	public static Map<String, String> GoodNamesId = new HashMap<String,String>();
	public static Map<String, String> papers = new HashMap<String,String>();
	public static Map<String, Integer> collaborations = new HashMap<String,Integer>();
	public static ArrayList<String> ketTerms = new ArrayList<String>();

	
	public static int globalNum = 1;
	public static double VLDBCounter = 0;
	public static double SIGMODCounter = 0;
	public static double PODSCounter = 0;
	public static double IEEECounter = 0;
	public static double ICDTCounter = 0;
	public static double SODACounter = 0;
	private static ArrayList<Author> authorsPC = new ArrayList();
	
	
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



	/**
     * Build the profiles for all authors in Ids
     */
	private static void buildNeighborsProfiles() {
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
 	         
 	        File file = new File("Aminer-profiles.ttl");

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			String content = "@base <http://a.org/author/> .\n\n";
			//bw.write(content);

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
 	        		// authorCounter ++;
 	        		 if(inNeighbors(line)){
 	        			addNewAuthor(file,author.toString(),bw);
 	 	        		 author.delete(0, author.length());
 	 	        		 author.append(line); 
 	        		 }
 	        		 
 	        		 //if(authorCounter == 50)
 	        			// break;
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
	  
	  private static boolean isNeighbore(String line) {
			for(int i = 0; i<Ids.size(); i++)
			{
				if(Ids.get(i).equals(line))
					return true;
			}
			return false;
		}

	private static void addNewAuthor(File file, String string, BufferedWriter bw) {
	    	String[] lines = string.split("#");
	    	String id = lines[1];
	    	String name = lines[2];
	    	
	    	name = name.substring(1);
	    	
	    	String goodName = name;
	    
	    	name = name.replace(" ","_");
	    	name = name.replace(".","");
	    	if(name.startsWith("_")){
	    		name = name.substring(1);
	    		
	    	}
	    	names.add(name);
	    	
	    	
	    	id = id.replace("index ", "");
	    	int x;
	    /*	if(id.contains("1690218"))
	    		x= 9;*/
	    	GoodNamesId.put(goodName,id);
	    	authors.put(id, name);
	    	
	    	int idd = Integer.parseInt(id);
	    
	    	
	    	StringBuilder author = new StringBuilder();
	    	author.append("<"+name+"> <instanceOf> <Author> .\n");
	    	author.append("<"+name+"> <id> <"+idd+"> .\n");
	    	
	    	//String pc = lines[4];
	    	String pc = "pc 0";
	    	int i = 0;
	    	int j = 0;
	    	for(i = 0; i<lines.length;i++){
	    		if(lines[i].startsWith("pc") && Pattern.matches("[a-zA-Z]+", pc.substring(2)) == false){
	    			pc = lines[i];
	    			break;
	    		}
	    			
	    	}
	    	j=i;
	    	pc = pc.replace("pc ", "");
	    
	    	Author a = new Author(name, id);
	    	authorsPC.add(a);
	    	
	    		//System.out.println(pc);
	    		int publishCount;
	    		
	    	
	    		//if(pc.contains("Institute of Automation")||pc.contains("Passau") )
	    		//if (Pattern.matches("[a-zA-Z]+", pc) == false && pc.length() > 1) 
	    			//publishCount = 0;
	    		//else
	    			publishCount = Integer.parseInt(pc);
		    	collaborations.put(id, publishCount);
	    
	    
	    	
	    	//String cn = lines[5];
	    	String cn = "cn 0";
	    	for(i = j; i<lines.length;i++){
	    		if(lines[i].startsWith("cn")){
	    			cn = lines[i];
	    			break;
	    		}
	    			
	    	}
	    	j=i;
	    	cn = cn.replace("cn ", "");
	    	
	    	//String hi = lines[6];
	    	String hi = "hi 0";
	    	for(i = j; i<lines.length;i++){
	    		if(lines[i].startsWith("hi")){
	    			hi = lines[i];
	    			break;
	    		}
	    			
	    	}
	    	j=i;
	    	hi = hi.replace("hi ", "");
	    	
	    	//String pi = lines[7];
	    	String pi = "pi 0";
	    	for(i = j; i<lines.length;i++){
	    		if(lines[i].startsWith("pi"))
	    		{
	    			pi = lines[i];
	    			break;
	    		}
	    		
	    	}
	    	pi = pi.replace("pi ", "");
	    	j = i;
	    	
	    
	    	String terms = "";
	    	for(i = j; i<lines.length;i++){
	    		if(lines[i].startsWith("t"))
	    		{
	    			terms = lines[i];
	    			break;
	    		}
	    		
	    	}
	    	terms = terms.substring(1);
	    	String [] keyterms = terms.split(";");
	    	ArrayList<String> t = new ArrayList<String>();
	    	for(int k = 0; k< keyterms.length; k++){
	    		if(ketTerms.contains(keyterms[k]))
	    			t.add(keyterms[k]);
	    	}
	    	
	    //	for(String s: t){
	    	//	author.append("<"+name+"> <keyTerm> <"+s+"> .\n");
	    	//}
	    	
	    	author.append("<"+name+"> <pc> "+pc+" .\n");
	    	author.append("<"+name+"> <cn> "+cn+" .\n");
	    	author.append("<"+name+"> <h_index> "+hi+" .\n");
	    	author.append("<"+name+"> <p_index> "+pi+" .\n");
	    	
	    	// String ter = lines[9];
	    	// ter = ter.replace("t ", "");
	    	/* String [] terms = ter.split(";");
	    	for (int i = 0; i<terms.length;i++){
	    		String term = terms[i];
	    		term = term.replace(" ", "_");
	    	
	    		author.append("<"+term+"> <instanceOf> <Term> .\n");
	    		author.append("<"+name+"> <has_term> <"+term+"> .\n");
	    	}*/
	    	author.append("\n\n");
	    	try {
	    		
				bw.write(author.toString());
			} 
	    	catch (IOException e) {
				e.printStackTrace();
			}	
	    	//System.out.println(author.toString());
			
		}
	
	private static void buildCollaborationTran() {
		 InputStream is = null; 
 	     InputStreamReader isr = null;
 	     BufferedReader br = null;
 	     BufferedWriter bw = null; 

 	      try{
 	         // open input stream test.txt for reading purpose.
 	         is = new FileInputStream("AMiner-Coauthor.txt");
 	         
 	         // create new input stream reader
 	         isr = new InputStreamReader(is);
 	         
 	         // create new buffered reader
 	         br = new BufferedReader(isr);
 	      
 	         String line;
 	         
 	        File file = new File("Aminer-collaborations.ttl");

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			String content = "@base <http://a.org/author/> .\n\n";
			//bw.write(content);

		
			line = br.readLine();
		
 	         // reads to the end of the stream 
 	         while((line = br.readLine()) != null)
 	         {
 	        	 if(line.contains("#") && relevantCollaboration(line))
 	        	 {
 	        		//System.out.println(line);
 	        		 parseCollaboration(line,bw);
 	        		
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
	
	/**
	 * 
	 * @param line = collaboration, create the transaction
	 * @param bw 
	 */
	private static void parseCollaboration(String line, BufferedWriter bw) {
		
		line = line.substring(1);
		Scanner s = new Scanner(line);
		String Id1 = s.next();
		String name1 = authors.get(Id1);
		
		
		String Id2 = s.next();
		String name2 = authors.get(Id2);
		
		String num = s.next();
		
		if(name1 != null && name2 != null)
			try {
				addCollaboration(name1,name2,num,bw,Id1,Id2);
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		
		s.close();
	}
	
/**
 * create transaction between the two authors
 * @param name1 
 * @param name2
 * @param num of collaboration
 * @param bw 
 * @param id2 
 * @param id1 
 * @throws IOException 
 */
	private static void addCollaboration(String name1, String name2, String num, BufferedWriter bw, String id1, String id2) throws IOException {
	
	double count = Integer.parseInt(num);
	
	//calculate the support
	int publicationCount1 = collaborations.get(id1);
	double s1 = 0;
	if(publicationCount1 != 0)
		 s1 = count/publicationCount1;
	
	if(name1.startsWith("Tova")||name2.startsWith("Tova")){
		int x = 3;
		x++;
	}
	int publicationCount2 = collaborations.get(id2);
	double s2 = 0;
	if(publicationCount2 != 0)
		s2 = count/publicationCount2;
	
	//tran1
	StringBuilder s = new StringBuilder();
	s.append("\n<tran");
	s.append(globalNum+">\n");
	globalNum++;
	s.append("\t<By> <"+name1+"> ;\n");
	s.append("\t<WithSupport> "+s1+" ;\n");
	int factNum = globalNum;
	s.append("\t<hasFact> <fact"+globalNum+"> .\n");
	globalNum++;
	
	String str = s.toString();
	bw.write(str);
	//System.out.println(s.toString());
	
	//tran2
	StringBuilder ss = new StringBuilder();
	ss.append("\n<tran");
	ss.append(globalNum+">\n");
	globalNum++;
	ss.append("\t<By> <"+name2+"> ;\n");
	ss.append("\t<WithSupport> "+s2+" ;\n");
	ss.append("\t<hasFact> <fact"+factNum+"> .\n");
	
	str = ss.toString();
	bw.write(str);
	//System.out.println(ss.toString());
	
	//fact
	StringBuilder sss = new StringBuilder();
	sss.append("\n<fact");
	sss.append(factNum+">\n");
	sss.append("\t<hasSubject> <"+name1+"> ;\n");
	sss.append("\t<hasProperty> <collaborate> ;\n");
	sss.append("\t<hasObject> <"+name2+"> .\n");
	
	str = sss.toString();
	bw.write(str);
	//System.out.println(sss.toString());

	}

	/**
	 * 
	 * @param line
	 * @return true if both the Ids are relevant;
	 */
	private static boolean relevantCollaboration(String line) {
		line = line.substring(1);
		Scanner s = new Scanner(line);
		String Id1 = s.next();
		String Id2 = s.next();
		String collabNum = s.next();
		if(isNeighbore(Id1) && isNeighbore(Id2))
			return true;
		
		return false;
	}
	
	private static void buildPapersTran() {
		
		 InputStream is = null; 
 	     InputStreamReader isr = null;
 	     BufferedReader br = null;
 	     BufferedWriter bw = null; 

 	      try{
 	         // open input stream test.txt for reading purpose.
 	         is = new FileInputStream("AMiner-Paper.txt");
 	         
 	         // create new input stream reader
 	         isr = new InputStreamReader(is);
 	         
 	         // create new buffered reader
 	         br = new BufferedReader(isr);
 	      
 	         String line;
 	         
 	        File file = new File("Aminer-papersTran.ttl");

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			String content = "@base <http://a.org/author/> .\n\n";
			//bw.write(content);
			
			
			ArrayList<String> confrences = new ArrayList();
			confrences.add("SIGMOD");
			confrences.add("VLDB");
			confrences.add("IEEE");
			confrences.add("ICDT");
			confrences.add("PODS");
			confrences.add("SODA");
			StringBuilder s = new StringBuilder();
			
			for(String confrence: confrences){
				s= new StringBuilder();
				s.append("<"+confrence+"> <instanceOf> <Confrence> .\n");
				bw.write(s.toString());
			}

			StringBuilder paper = new StringBuilder();
			line = br.readLine();
			paper.append(line);
		
 	         // reads to the end of the stream 
 	         while((line = br.readLine()) != null)
 	         {
 	        	 if(line.contains("#index"))
 	        	 {
 	        		
 	        			addNewPaper(file,paper.toString(),bw);
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
 						e.printStackTrace();
 					}
 	         }
		
		
	}
	}

	private static void addNewPaper(File file, String string, BufferedWriter bw) {
		String[] lines = string.split("#");
		
		String venue = null;
    	for(int i=0;i<lines.length - 1;i++){
			if(lines[i].startsWith("c ") && lines[i+1].startsWith("% ")){
				String temp = lines[i];
				if(temp.contains("SIGMOD")){
					venue = "SIGMOD";
					//SIGMODCounter++;
				}
				else if(temp.contains("VLDB")){
					venue = "VLDB";
					//VLDBCounter++;
				}
				else if(temp.contains("IEEE")){
					venue = "IEEE";
					//IEEECounter++;
				}
				else if(temp.contains("ICDT")){
					venue = "ICDT";
					//ICDTCounter++;
				}
				else if(temp.contains("PODS")){
					venue = "PODS";
					//PODSCounter++;
				}
				else if(temp.contains("SODA")){
					venue = "SODA";
					//SODACounter++;
				}
			
				break;
			}
				
		}
    	if(venue == null)
    		return;
		
		String year = null;
		
		//only papers before 2010 and after 2005
		for(int i=0;i<lines.length;i++){
			if(lines[i].startsWith("t ")){
				year = lines[i];
				year = year.substring(2);
				if(year.equals(""))
					return;
				int y = Integer.parseInt(year);
				if(y > 2010 || y < 2005)
					return;
			}
				
		}
		if(year == null)
    		return;
		
    	String id = lines[1];
    	String title = lines[2];
    	title = title.replace("* ", "");
    	title = title.replace(" ","_");
    	
    	if(title.startsWith("_")){
    		title = title.substring(1);
    		
    	}
    	
    	
    	
    	id = id.replace("index ", "");
        
       // System.out.println(id+" "+title);

    	
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
    	
    
    	papers.put(id, title);
    
			try {
				buildPaperTran(id, title, year, venue, relevantAuthors,bw);
			} 
    		catch (IOException e) {
				e.printStackTrace();
			}
    	
    	
	}
	
	/**
	 * build transaction for given author
	 * @param id
	 * @param title
	 * @param year
	 * @param venue
	 * @param 
	 * @param bw 
	 * @throws IOException 
	 */
	private static void buildPaperTran(String id, String title, String year,String venue, ArrayList<String> relevantAuthors, BufferedWriter bw) throws IOException {
		

		
		for(String author: relevantAuthors){
			
			int factNum = globalNum;
			globalNum++;
			//tran1
			StringBuilder s = new StringBuilder();
			s.append("\n<tran");
			s.append(globalNum+">\n");
			globalNum++;
			s.append("\t<By> <"+author+"> ;\n");
			s.append("\t<WithSupport> 1 ;\n");
			
			s.append("\t<hasFact> <fact"+factNum+"> .\n");
		
			addPaperVenue(author,id,venue);
			String str = s.toString();
			bw.write(str);
			
			s = new StringBuilder();
			//fact - wrote
			
			s.append("\n<fact");
			s.append(factNum+">\n");
			s.append("\t<hasSubject> <"+author+"> ;\n");
			s.append("\t<hasProperty> <published> ;\n");
			s.append("\t<hasObject> <"+title+"> .\n");
			str = s.toString();
			bw.write(str);
			

			
			str = s.toString();
			bw.write(str);
			
		}
		//System.out.println(s.toString());
		
		StringBuilder sss = new StringBuilder();
		//fact - wrote
		
		/**sss.append("\n<fact");
		sss.append(factNum+">\n");
		sss.append("\t<hasSubject> <"+title+"> ;\n");
		sss.append("\t<hasProperty> <instanceof> ;\n");
		sss.append("\t<hasObject> <Paper> .\n");
		String str = sss.toString();
		bw.write(str);*/
		
		//fact - venue
		sss = new StringBuilder();
		sss.append("\n<"+title+"> <venue> <"+venue+"> .\n");
		String str = sss.toString();
		bw.write(str);
		
		//fact - isA paper
		sss = new StringBuilder();
		sss.append("\n<"+title+"> ");
		sss.append("<instanceOf> <Paper> .\n");
		str = sss.toString();
		bw.write(str);
		
		//fact - year
		sss = new StringBuilder();
		sss.append("\n<"+title+"> ");
		sss.append("<year> "+year+" .\n");
		str = sss.toString();
		bw.write(str);
		
		
	}

	private static void addPaperVenue(String author, String id, String venue) {
		for(Author a: authorsPC)
		{
			if(a.name.equals(author))
			{
				switch(venue)
				{
				case "SIGMOD":
					a.SIGMOD++;
					break;
				case "ICDT":
					a.ICDT++;
					break;
				case "VLDB":
					a.VLDB++;
					break;
				case "SODA":
					a.SODA++;
					break;
				case "IEEE":
					a.IEEE++;
					break;
				case "PODS":
					a.PODS++;
					break;
				
				}
			}
		}
		
	}

	private static boolean contains(ArrayList<String> names2, String string) {
		for(String s: names2)
			if(s.equals(string))
				return true;
		return false;
	}
	
	/**
	 * build tran in the form: <Tova_milo> <publishedAt> <SIGMOD> with support 0.8
	 * @throws IOException 
	 */
	private static void buildPublicationVenueTran() throws IOException {
		
 	     BufferedWriter bw = null;  	         
 	     File file = new File("Aminer-publication venue.ttl");
		// if file doesnt exists, then create it
		 if (!file.exists()) {
				file.createNewFile();
		 }

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		bw = new BufferedWriter(fw);
		StringBuilder s;
		String str;
		
		for(Author a: authorsPC){
			double PCcount = collaborations.get(a.Id);
			
			
			//int x;
			//if(a.name.contains("Baruch_Awerbuch"))
				//x = 8;
			//SIGMOD
			//double SIGMOD = a.SIGMOD/PCcount;
			double SIGMOD = Math.pow(a.SIGMOD, 3)/SIGMODCounter;
			if (Double.isNaN(SIGMOD))
				SIGMOD = 0;
			
			if(SIGMOD > 0){
				
			SIGMOD = Math.max(SIGMOD, 0.001);
			s = new StringBuilder();
			s.append("<tran"+globalNum+">\n");

			globalNum++;
			s.append("\t<By> <"+a.name+"> ;\n");
			s.append("\t<WithSupport> "+SIGMOD+" ;\n");
			s.append("\t<hasFact> <fact"+globalNum+"> .\n");
			
			s.append("<fact"+globalNum+">\n");
			s.append("\t<hasSubject> <"+a.name+"> ;\n");
			s.append("\t<hasProperty> <publishedAt> ;\n");
			s.append("\t<hasObject> <SIGMOD> .\n");
			globalNum++;
			str = s.toString();
			bw.write(str);}
			
			//VLDB
			//double VLDB = a.VLDB/PCcount;
			double VLDB = Math.pow(a.VLDB, 3)/VLDBCounter;
			if (Double.isNaN(VLDB))
				VLDB = 0;
			
			if(VLDB > 0){
			VLDB = Math.max(VLDB, 0.001);
			s = new StringBuilder();
			s.append("<tran"+globalNum+">\n");

			globalNum++;
			s.append("\t<By> <"+a.name+"> ;\n");
			s.append("\t<WithSupport> "+VLDB+" ;\n");
			s.append("\t<hasFact> <fact"+globalNum+"> .\n");
			
			s.append("<fact"+globalNum+">\n");
			s.append("\t<hasSubject> <"+a.name+"> ;\n");
			s.append("\t<hasProperty> <publishedAt> ;\n");
			s.append("\t<hasObject> <VLDB> .\n");
			globalNum++;
			str = s.toString();
			bw.write(str);}
			
			//PODS
			//double PODS = a.PODS/PCcount;
			double PODS = Math.pow(a.PODS, 3)/PODSCounter;
			if (Double.isNaN(PODS))
				PODS = 0;
			
			if(PODS > 0){
			PODS = Math.max(PODS, 0.001);
			s = new StringBuilder();
			s.append("<tran"+globalNum+">\n");

			globalNum++;
			s.append("\t<By> <"+a.name+"> ;\n");
			s.append("\t<WithSupport> "+PODS+" ;\n");
			s.append("\t<hasFact> <fact"+globalNum+"> .\n");
			
			s.append("<fact"+globalNum+">\n");
			s.append("\t<hasSubject> <"+a.name+"> ;\n");
			s.append("\t<hasProperty> <publishedAt> ;\n");
			s.append("\t<hasObject> <PODS> .\n");
			globalNum++;
			str = s.toString();
			bw.write(str);}
			
			//IEEE
			//double IEEE = a.IEEE/PCcount;
			double IEEE = Math.pow(a.IEEE, 3)/IEEECounter;
			if (Double.isNaN(IEEE))
				IEEE = 0;
			
			if(IEEE > 0){
			IEEE = Math.max(IEEE, 0.001);
			s = new StringBuilder();
			s.append("<tran"+globalNum+">\n");

			globalNum++;
			s.append("\t<By> <"+a.name+"> ;\n");
			s.append("\t<WithSupport> "+IEEE+" ;\n");
			s.append("\t<hasFact> <fact"+globalNum+"> .\n");
			
			s.append("<fact"+globalNum+">\n");
			s.append("\t<hasSubject> <"+a.name+"> ;\n");
			s.append("\t<hasProperty> <publishedAt> ;\n");
			s.append("\t<hasObject> <IEEE> .\n");
			globalNum++;
			str = s.toString();
			bw.write(str);}
			
			//ICDT
			//double ICDT = a.ICDT/PCcount;
			double ICDT = Math.pow(a.ICDT, 3)/ICDTCounter;
			if (Double.isNaN(ICDT))
				ICDT = 0;
			
			if(ICDT > 0){
			ICDT = Math.max(ICDT, 0.001);
			s = new StringBuilder();
			s.append("<tran"+globalNum+">\n");

			globalNum++;
			s.append("\t<By> <"+a.name+"> ;\n");
			s.append("\t<WithSupport> "+ICDT+" ;\n");
			s.append("\t<hasFact> <fact"+globalNum+"> .\n");
			
			s.append("<fact"+globalNum+">\n");
			s.append("\t<hasSubject> <"+a.name+"> ;\n");
			s.append("\t<hasProperty> <publishedAt> ;\n");
			s.append("\t<hasObject> <ICDT> .\n");
			globalNum++;
			str = s.toString();
			bw.write(str);}
			
			//SODA
			//double SODA = a.SIGMOD/PCcount;
			double SODA = Math.pow(a.SODA, 3)/SODACounter;
			if (Double.isNaN(SODA))
				SODA = 0;
			if(SODA > 0){
			SODA = Math.max(SODA, 0.001);
			s = new StringBuilder();
			s.append("<tran"+globalNum+">\n");

			globalNum++;
			s.append("\t<By> <"+a.name+"> ;\n");
			s.append("\t<WithSupport> "+SODA+" ;\n");
			s.append("\t<hasFact> <fact"+globalNum+"> .\n");
			
			s.append("<fact"+globalNum+">\n");
			s.append("\t<hasSubject> <"+a.name+"> ;\n");
			s.append("\t<hasProperty> <publishedAt> ;\n");
			s.append("\t<hasObject> <SODA> .\n");
			globalNum++;
			str = s.toString();
			bw.write(str);}
		}
		
	
		bw.close();
 	       
		
		
	}
	
	private static void outputName() throws IOException {
		  BufferedWriter bw = null;  	         
	 	  File file = new File("Aminer-names.txt");
			// if file doesnt exists, then create it
		  if (!file.exists()) {
					file.createNewFile();
		  }

		  FileWriter fw = new FileWriter(file.getAbsoluteFile());
		  bw = new BufferedWriter(fw);
	
		  for(String name: names){
			String s = name;
			//s = s.replaceAll("_", " ");
			s = s+",";
			bw.write(s);
		  }
		  
		  bw.close();
		
	}
	
	private static void buildKeyTermsSet() {
		 InputStream is = null; 
 	     InputStreamReader isr = null;
 	     BufferedReader br = null;

 	      try{
 	         // open input stream test.txt for reading purpose.
 	         is = new FileInputStream("k.txt");
 	         
 	         // create new input stream reader
 	         isr = new InputStreamReader(is);
 	         
 	         // create new buffered reader
 	         br = new BufferedReader(isr);
 	      
 	         String line;		
 	         // reads to the end of the stream 
 	         while((line = br.readLine()) != null)
 	         {
 	        	 ketTerms.add(line);
 	        	 
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
	
	private static void buildProfiles() {
		 InputStream is = null; 
 	     InputStreamReader isr = null;
 	     BufferedReader br = null;
 	     BufferedWriter bw = null; 

 	      try{
 	         // open input stream test.txt for reading purpose.
 	         is = new FileInputStream("aminer_crawling_output.tsv");
 	         
 	         // create new input stream reader
 	         isr = new InputStreamReader(is);
 	         
 	         // create new buffered reader
 	         br = new BufferedReader(isr);
 	      
 	         String line;
 	         
 	        File file = new File("Aminer-keyterms and uni.ttl");

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
		

			StringBuilder author = new StringBuilder();
		
 	         // reads to the end of the stream 
 	         while((line = br.readLine()) != null)
 	         {
 	        	 addProfileDetails(line,bw);
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

	private static void addProfileDetails(String line, BufferedWriter bw) {
		Scanner s = new Scanner(line);
		s.useDelimiter("\\t");
		String name = s.next();
		if(!name.startsWith(" "))
			name = " "+name;
		if(GoodNamesId.containsKey(name)){
			String Id = GoodNamesId.get(name);
			String n = authors.get(Id);
			String affiliation = s.next();
			affiliation = affiliation.replace(" ", "_");
			String countryCode = s.next();
			
			ArrayList<String> keyterms = new ArrayList<String>();
			if(s.hasNext()){
			String terms = s.next();
			s = new Scanner(terms);
			s.useDelimiter(";");
			
			while(s.hasNext()){
				String term = s.next();
				if(contains(ketTerms,term))
					keyterms.add(term);
			}
			}
		  s.close();
		  StringBuilder author = new StringBuilder();
		  author.append("<"+n+"> <affiliation> <"+affiliation+"> .\n");
	      author.append("<"+n+"> <country> <"+countryCode+"> .\n");
	      for(String term: keyterms){
	    	  String t = term.replace(" ", "_");
	    	  author.append("<"+n+"> <keyTerm> <"+t+"> .\n");
	      }
	      try 
	      {
			bw.write(author.toString());
		  } 
	      catch (IOException e) {
			
			e.printStackTrace();
		}
	     
			
		}
		
	}
	
	private static void buildCountryOntology() {
		 InputStream is = null; 
	     InputStreamReader isr = null;
	     BufferedReader br = null;
	     BufferedWriter bw = null; 

	      try{
	         // open input stream test.txt for reading purpose.
	         is = new FileInputStream("aminer_crawling_output.tsv");
	         
	         // create new input stream reader
	         isr = new InputStreamReader(is);
	         
	         // create new buffered reader
	         br = new BufferedReader(isr);
	      
	         String line = br.readLine();
	         
	        File file = new File("Aminer-country Ontology.ttl");

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
		

			StringBuilder author = new StringBuilder();
		
	         // reads to the end of the stream 
	         while((line = br.readLine()) != null)
	         {
	        	 addCountry(line,bw);
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

	private static void addCountry(String line, BufferedWriter bw) {
		
		
		Scanner s = new Scanner(line);
		s.useDelimiter("\\t");
		String name = s.next();
		String aff = s.next();
		aff = aff.replace(" ", "_");
		String country = s.next();
		
		
		StringBuilder ss = new StringBuilder();
		
		if(!contains(affiliation, aff)){
			affiliation.add(aff);
			affiliationToCountry.put(aff, country);
			
			ss.append("<"+aff+"> <instanceof> <Affiliation> .\n");
		    ss.append("<"+aff+"> <in> <"+country+"> .\n");
			
		}
			
		
		if(!contains(countryCode, country)){
			countryCode.add(country);
			ss.append("<"+country+"> <instanceof> <Country> .\n");
		}
		
		try {
			bw.write(ss.toString());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private static void papersPreProcessing() {
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
 	         while((line = br.readLine()) != null)
 	         {
 	        	 if(line.contains("#c"))
 	        	 {
 	        		
 	        		if(line.contains("SIGMOD")){
 						SIGMODCounter++;
 					}
 					else if(line.contains("VLDB")){
 						VLDBCounter++;
 					}
 					else if(line.contains("IEEE")){
 						IEEECounter++;
 					}
 					else if(line.contains("ICDT")){
 						ICDTCounter++;
 					}
 					else if(line.contains("PODS")){
 						PODSCounter++;
 					}
 					else if(line.contains("SODA")){
 						SODACounter++;
 					}
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

	public static void main(String [] args)
	{
		papersPreProcessing();
		String Tova = "650652";
		String Noga = "1653815";
		
		getNeighbors(Tova);
		getNeighbors(Noga);
		Ids.add(Tova);
		Ids.add(Noga);
		buildKeyTermsSet();
		//for (String s: ketTerms)
			//System.out.println(s);
		buildNeighborsProfiles();
		
		/*Iterator<String> iter = collaborations.keySet().iterator();
		while(iter.hasNext()){
			String author = iter.next();
			int count = collaborations.get(author);
			System.out.println(author +" pc: "+ count);
		}*/
		
		
		buildCollaborationTran();
		buildPapersTran();
		
		buildProfiles();
		
		
		try {
			buildPublicationVenueTran();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		//try {
			//outputName();
		//}
		//catch (IOException e) {
			
			//e.printStackTrace();
		//}*/
		
		
		//buildCountryOntology();
		System.out.println(Ids.size());
	}





	









	

	

	

	
	

}
