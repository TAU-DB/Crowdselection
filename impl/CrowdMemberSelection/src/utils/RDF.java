package utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

public class RDF {
	
	private Model ontology;
	private Model transactions;
	private Model profiles;
	
	
	public Model getOntologyModel(){
		return ontology;
	}
	
	public Model getTransactionsModel(){
		return transactions;
	}
	
	public Model getProfilesModel(){
		return profiles;
	}
	

	public void init() {
		initOntology();
		initProfiles();
		initTransactions();
	
	}
 /*
  * Create transaction model
  */
	private void initTransactions() {

	    transactions = ModelFactory.createDefaultModel();
	    try
	    {
			transactions.read(new FileInputStream("transactions.ttl"),null,"TTL");
		}
	    catch (FileNotFoundException e) {
	    	System.out.println("Error creating tansactions model" + e.getMessage());
			e.printStackTrace();
		}
		
	}
 /**
  * Create profiles model
  */
	private void initProfiles() {

	profiles = ModelFactory.createDefaultModel();
	try 
	{
		profiles.read(new FileInputStream("users.ttl"),null,"TTL");
	} 
	catch (FileNotFoundException e) {
		System.out.println("Error creating profiles model" + e.getMessage());
		e.printStackTrace();
	}
		
	}
 /**
  * Create a Model to store an ontology and initialize from source
  */
	private void initOntology() {

	ontology = ModelFactory.createDefaultModel();
			
	try 
	{
		ontology.read(new FileInputStream("ontology.ttl"),null,"TTL");
	} 
	catch (FileNotFoundException e) 
	{
		System.out.println("Error creating ontology model" +e.getMessage());
		e.printStackTrace();
	}
			
		
	}

}
