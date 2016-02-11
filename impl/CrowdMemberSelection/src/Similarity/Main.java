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
			"     $u <http://a.org/ontology/keyTerm> $term  ." +
			"     $u <http://a.org/ontology/affiliation> $uni  ." +
			"	  $u <http://a.org/ontology/h_index> $h ."+
			"     FILTER ( $h > 3 ) ."+
			"     $u <http://a.org/ontology/pc> $pc ."+
			"     FILTER ( $pc > 15 ) . "+
			"      } "+
			"FROM ontology WHERE{" +
			"     $term <http://a.org/ontology/instanceof> <http://a.org/ontology/Database> ." +
			"     $uni <http://a.org/ontology/in> <http://a.org/ontology/US> ." +
			"      } "+
			"SIMILAR tran($u) TO {$u <http://a.org/ontology/publishedAt> <http://a.org/ontology/VLDB> . "+
			"$u <http://a.org/ontology/publishedAt> <http://a.org/ontology/SIGMOD> . } "+
			"WITH SIMILARITY > 0 "+
			"SIMILAR profile($u) TO profile(Tova_Milo) "+
			"WITH SIMILARITY > 0 "+
			"ORDERED BY ProfileSim "+
			"LIMIT 5 ";
	



	Parser parser = new Parser(query);
	parser.parseQuery();
	
	System.out.println(parser.getQueryToExecute());
	

	RDF rdf = new RDF();
	rdf.init();
	QueryGenerator generator = new QueryGenerator(parser, rdf,ontologyGraph);
	ArrayList<User> ans = generator.executeQuery();
	System.out.println("Query result:");
	for(User t: ans){
		System.out.println(t.toString());
	}
	
	
    //similarityTest(ontologyGraph);

	}



private static void similarityTest(Multimap<Term, Term> ontologyGraph) {
	//similarity terms
	/*Term x = new Term("Volleyball");
	Term y = new Term("Surfing");
	double simTerms = Similarity.getSimilarityWithoutSupport(x,y,ontologyGraph);
	System.out.println(simTerms);*/
	
	//similarity fact
	/*Fact f1 = new Fact(new Term("Bill"),new Term("livesIn"), new Term("Sydney"));
	Fact f2 = new Fact(new Term("Carol"),new Term("livesIn"), new Term("Sydney"));
	double simFact = Similarity.getSimilarityWithoutSupport(f1,f2,ontologyGraph);
	System.out.println(simFact);*/
	
	//similarity fact-set
	/*
	 * <Ann>	<livesIn> <Paris> .
	   <Ann>	<hasGender>	<Female> .
       <Ann>	<hasHobby>	<Photography> .
       <Ann>	<hasHobby>	<Bird_Watching> .
       <Ann>	<graduatedFrom>	<NYU> .
       <Bill>	<livesIn>	<Sydney> .
       <Bill>	<hasGender>	<Male> .
       <Bill>	<hasHobby>	<Photography> .
       <Bill>	<graduatedFrom>	<University_of_Melbourne> .
       <Carol>	<livesIn>	<Sydney> .
       <Carol>	<hasGender>	<Female> .
       <Carol>	<hasHobby>	<Art> .
       
       <David>	<livesIn>	<London> .
       <David>	<hasGender>	<Male> .
       <David>	<hasHobby>	<Art> .
       <David>	<graduatedFrom>	<Oxford> .
       
       <Dan>	<livesIn>	<Paris> .
       <Dan>	<hasGender>	<Male> .
       <Dan>	<hasHobby>	<Art> .
       <Dan>	<graduatedFrom>	<University_of_Melbourne> .
	 */
	Fact Bill1 = new Fact(new Term("Ann"),new Term("livesIn"), new Term("Paris"));
	Fact Bill2 = new Fact(new Term("Ann"),new Term("hasGender"), new Term("Female"));
	Fact Bill3 = new Fact(new Term("Ann"),new Term("hasHobby"), new Term("Photography"));
	Fact Bill5 = new Fact(new Term("Ann"),new Term("hasHobby"), new Term("Bird_Watching"));
	Fact Bill4 = new Fact(new Term("Ann"),new Term("graduatedFrom"), new Term("NYU"));
	ArrayList<Fact> bill = new ArrayList<Fact>();
	bill.add(Bill1);
	bill.add(Bill2);
	bill.add(Bill3);
	bill.add(Bill4);
	bill.add(Bill5);
	FactSet billProfile = new FactSet(bill);
	
	Fact Ann1 = new Fact(new Term("David"),new Term("livesIn"), new Term("Paris"));
	Fact Ann2 = new Fact(new Term("David"),new Term("hasGender"), new Term("Male"));
	Fact Ann3 = new Fact(new Term("David"),new Term("hasHobby"), new Term("Art"));
	Fact Ann4 = new Fact(new Term("David"),new Term("graduatedFrom"), new Term("University_of_Melbourne"));
	ArrayList<Fact> ann = new ArrayList<Fact>();
	ann.add(Ann1);
	ann.add(Ann2);
	ann.add(Ann3);
	ann.add(Ann4);
	FactSet annProfile = new FactSet(ann);
	
	double simFact = Similarity.getSimilarityWithoutSupport(billProfile,billProfile,ontologyGraph);
	System.out.println(simFact);
	/*
	 * <tran1> <By> <Bill> ; <WithSupport> 0.8 ;<hasFact> <fact1> .
       <tran2> <By> <Bill> ;<WithSupport> 0.08 ;<hasFact> <fact2> .
       <tran3> <By> <Bill> ;<WithSupport> 0.012 ;<hasFact> <fact3> .
	   <tran4> <By> <Bill> ;<WithSupport> 0 ;<hasFact> <fact4> .
       <tran5> <By> <Ann> ;<WithSupport> 0.85 ;<hasFact> <fact1> .
       <tran6> <By> <Ann> ;<WithSupport> 0.1 ;<hasFact> <fact2> .
       <tran7> <By> <Ann> ;<WithSupport> 0.005 ;<hasFact> <fact3> .
       <tran8> <By> <Ann> ;<WithSupport> 0.001 ;<hasFact> <fact4> .
       
       <fact1> <hasSubject>	<Sport> ;<hasProperty>	<doAt> ;<hasObject> <Place> .
       <fact2>	<hasSubject>	<Surfing> ;<hasProperty>	<doAt> ;<hasObject> <Place> .
       <fact3>	<hasSubject>	<Surfing> ;<hasProperty>	<doAt> ;<hasObject> <Wanda_beach> ;<hasObject> <In_January> .
	   <fact4> <hasSubject>  <Volleyball> ;<hasProperty>	<doAt> ;<hasObject> <Place> .
	 */
	
/*	FactWithSupport Ann1 = new FactWithSupport(0.85, new Fact(new Term("Sport"),new Term("doAt"), new Term("Place")));
	FactWithSupport Ann2 = new FactWithSupport(0.1, new Fact(new Term("Surfing"),new Term("doAt"), new Term("Place")));
	FactWithSupport Ann3 = new FactWithSupport(0.005, new Fact(new Term("Surfing"),new Term("doAt"), new Term("Wanda_beach")));
	FactWithSupport Ann4 = new FactWithSupport(0.001, new Fact(new Term("Volleyball"),new Term("doAt"), new Term("Place")));
	ArrayList<FactWithSupport> factsAnn = new ArrayList<FactWithSupport>();
	factsAnn.add(Ann1);
	factsAnn.add(Ann2);
	//factsAnn.add(Ann3);
	factsAnn.add(Ann4);
	FactSetWithSupport ann = new FactSetWithSupport(factsAnn);
	
    FactWithSupport Bill1 = new FactWithSupport(0.8, new Fact(new Term("Sport"),new Term("doAt"), new Term("Place")));
    FactWithSupport Bill2 = new FactWithSupport(0.08, new Fact(new Term("Surfing"),new Term("doAt"), new Term("Place")));
    FactWithSupport Bill3 = new FactWithSupport(0.012, new Fact(new Term("Surfing"),new Term("doAt"), new Term("Wanda_beach")));
    FactWithSupport Bill4 = new FactWithSupport(0.0, new Fact(new Term("Volleyball"),new Term("doAt"), new Term("Place")));
	ArrayList<FactWithSupport> factsBill = new ArrayList<FactWithSupport>();
	factsBill.add(Bill1);
	factsBill.add(Bill2);
	//factsBill.add(Bill3);
	factsBill.add(Bill4);
	FactSetWithSupport bill = new FactSetWithSupport(factsBill);
	
	double simFact = Similarity.getSimilarityWithSupport(ann, bill, ontologyGraph);
	System.out.println(simFact);*/
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
	try 
	{
		Frequency.buildWordCountDB();
	} 
	catch (IOException e) {
		System.out.println(e.getMessage());
		e.printStackTrace();
	}

}

}
