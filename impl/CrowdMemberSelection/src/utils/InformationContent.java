package utils;



public class InformationContent {

	/**
	 * 
	 * @param frequency
	 * @return the IC for the given frequency
	 */
public static double getIC(SemanticUnit unit){
	    double frequency = Frequency.getFrequency(unit);
	    frequency = 9*frequency;
	    frequency = frequency/10;
	    frequency = frequency + 0.1;
	    frequency = -(Math.log10(frequency));
	   // double temp = Math.pow(Math.E, frequency);
	    //frequency = temp/(1+ temp);
		return frequency;
		
	}


}
