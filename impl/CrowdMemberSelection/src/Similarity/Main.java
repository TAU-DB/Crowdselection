package Similarity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.jena.ext.com.google.common.collect.Multimap;

import utils.Fact;
import utils.FactSet;
import utils.FactSetWithSupport;
import utils.FactWithSupport;
import utils.Frequency;
import utils.Ontology;
import utils.Parser;
import utils.QueryGenerator;
import utils.RDF;
import utils.SemanticUnit;
import utils.Similarity;
import utils.Term;
import utils.User;

public class Main {
	
public static void main(String[] args) {
	
	Init();
	Multimap<Term, Term> ontologyGraph = Ontology.getGraph();
	String query = "ASSIGN BY Tova_Milo to $u" +
			"FROM profile($u) WHERE{" +
			"     $u <http://a.org/ontology/h_index> $h ." +
			"     FILTER ( $h > 15 ) ."+
			"      } "+
			"FROM tran($u) WHERE{" +
			"     $u <http://a.org/ontology/publishedAt> <http://a.org/ontology/SIGMOD> WITH SUPPORT > 0.0002 ." +
			"     $u <http://a.org/ontology/publishedAt> <http://a.org/ontology/VLDB> WITH SUPPORT > 0.0002 ." +
			"      } "+
		//	"SIMILAR profile($u) TO profile(Tova_Milo) "+
			//"WITH SIMILARITY > 0 "+
			"SIMILAR tran($u) TO tran(Tova_Milo) "+
			"WITH SIMILARITY > 0 "+
			"ORDERED BY HistorySim "+
			"LIMIT 10 ";
	

	Parser parser = new Parser(query);
	parser.parseQuery();
	
	System.out.println(parser.getQueryToExecute());
	

	RDF rdf = new RDF();
	rdf.init();
	try 
	{
		Frequency.buildWordCountDB(rdf);
	} 
	catch (IOException e) 
	{
		System.out.println(e.getMessage());
	
	}
	QueryGenerator generator = new QueryGenerator(parser, rdf,ontologyGraph);
	ArrayList<User> ans = generator.executeQuery();
	System.out.println("Query result:");
	for(User t: ans){
		System.out.println(t.toString());
	}
	

	}


private static void Init() {
	try 
	{
		Ontology.buildOntologyGraph();
	}
	catch (FileNotFoundException e)
	{
		System.out.println(e.getMessage());
		e.printStackTrace();
	}


}

}
