package utils;

import java.util.ArrayList;
import java.util.Collections;



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

	public static double getICHIndex(int h_index, int h_index2) {
		   double frequency = Frequency.getFrequencyHindex(h_index, h_index2);
		    frequency = 9*frequency;
		    frequency = frequency/10;
		    frequency = frequency + 0.1;
		    frequency = -(Math.log10(frequency));
		   // double temp = Math.pow(Math.E, frequency);
		    //frequency = temp/(1+ temp);
			return frequency;
	}

	public static double getICPC(int pc, int pc2) {
		  double frequency = Frequency.getFrequencyPC(pc, pc2);
		    frequency = 9*frequency;
		    frequency = frequency/10;
		    frequency = frequency + 0.1;
		    frequency = -(Math.log10(frequency));
		   // double temp = Math.pow(Math.E, frequency);
		    //frequency = temp/(1+ temp);
			return frequency;
	}
	
	public static double getICAR(int pc, int pc2) {
		  double frequency = Frequency.getFrequencyAcceptRate(pc, pc2);
		    frequency = 9*frequency;
		    frequency = frequency/10;
		    frequency = frequency + 0.1;
		    frequency = -(Math.log10(frequency));
		   // double temp = Math.pow(Math.E, frequency);
		    //frequency = temp/(1+ temp);
			return frequency;
	}
	
	public static double getICGold(int pc, int pc2) {
		  double frequency = Frequency.getFrequencyGold(pc, pc2);
		    frequency = 9*frequency;
		    frequency = frequency/10;
		    frequency = frequency + 0.1;
		    frequency = -(Math.log10(frequency));
		   // double temp = Math.pow(Math.E, frequency);
		    //frequency = temp/(1+ temp);
			return frequency;
	}
	
	public static double getICBronze(int pc, int pc2) {
		  double frequency = Frequency.getFrequencyBronze(pc, pc2);
		    frequency = 9*frequency;
		    frequency = frequency/10;
		    frequency = frequency + 0.1;
		    frequency = -(Math.log10(frequency));
		   // double temp = Math.pow(Math.E, frequency);
		    //frequency = temp/(1+ temp);
			return frequency;
	}
	
	public static double getICSilver(int pc, int pc2) {
		  double frequency = Frequency.getFrequencySilver(pc, pc2);
		    frequency = 9*frequency;
		    frequency = frequency/10;
		    frequency = frequency + 0.1;
		    frequency = -(Math.log10(frequency));
		   // double temp = Math.pow(Math.E, frequency);
		    //frequency = temp/(1+ temp);
			return frequency;
	}

	public static double avgIC(FactSet factset, double h_index, double pc) {
		ArrayList<Double> ICs = new ArrayList<Double>();
		ICs.add(h_index);
		ICs.add(pc);
		for(Fact f: factset){
			double ic = getIC(f);
			ICs.add(ic);
		}
		Collections.sort(ICs);
		
		int size = ICs.size();
		double ans = 0;
		
		if(size >= 5){
			double max1 = ICs.get(size-1);
			double max2 = ICs.get(size-2);
			double max3 = ICs.get(size-3);
			double max4 = ICs.get(size-4);
			double max5 = ICs.get(size-5);
		
			ans = (max1 + max2 + max3+ max4+ max5)/5;
		}
		else if(size >= 3){
			double max1 = ICs.get(size-1);
			double max2 = ICs.get(size-2);
			double max3 = ICs.get(size-3);
		
			ans = (max1 + max2 + max3)/5;
		}
		
		else if(size >= 2){
			double max1 = ICs.get(size-1);
			double max2 = ICs.get(size-2);
			//double max3 = ICs.get(size-3);
		
			ans = (max1 + max2)/5;
		}
		return ans;
	}

	public static double avgIC(FactSet factset, double bronze, double silver,double gold, double ar) {
		ArrayList<Double> ICs = new ArrayList<Double>();
		ICs.add(bronze);
		ICs.add(ar);
		ICs.add(gold);
		ICs.add(silver);
		for(Fact f: factset){
			double ic = getIC(f);
			ICs.add(ic);
		}
		Collections.sort(ICs);
		
		int size = ICs.size();
		double ans = 0;
		
		for(Double d: ICs)
			ans += d;
		ans = ans/ICs.size();
	/*	if(size >= 5){
			double max1 = ICs.get(size-1);
			double max2 = ICs.get(size-2);
			double max3 = ICs.get(size-3);
			double max4 = ICs.get(size-4);
			double max5 = ICs.get(size-5);
		
			ans = (max1 + max2 + max3+ max4+ max5)/5;
		}
		else if(size >= 3){
			double max1 = ICs.get(size-1);
			double max2 = ICs.get(size-2);
			double max3 = ICs.get(size-3);
		
			ans = (max1 + max2 + max3)/5;
		}
		
		else if(size >= 2){
			double max1 = ICs.get(size-1);
			double max2 = ICs.get(size-2);
			//double max3 = ICs.get(size-3);
		
			ans = (max1 + max2)/5;
		}*/
		return ans;
	}


}
