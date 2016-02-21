package utils;

public class User {
	
	private Term user;
	private double profileSim;
	private double tranSim;
	private double querySim;
	private double AVGSim;
	
	public User(Term t, double pro, double tran, double query){
		user = t;
		profileSim = pro;
		tranSim = tran;
		querySim = query;
		
	}
	
	public User(){
		
	}

	public Term getUser() {
		return user;
	}

	public void setUser(Term user) {
		this.user = user;
	}

	public double getProfileSim() {
		return profileSim;
	}

	public void setProfileSim(double profileSim) {
		this.profileSim = profileSim;
	}

	public double getTranSim() {
		return tranSim;
	}

	public void setTranSim(double tranSim) {
		this.tranSim = tranSim;
	}
	
	public String toString(){
		return user.toSrting() + " profile similarity: "+profileSim + " tarnsaction similarity: "+ tranSim + 
				" query similarity: "+ querySim +" AVG similarity: "+ AVGSim;
	}

	public double getQuerySim() {
	
		return querySim;
	}
	
	public void setQuerySim(double sim) {
		
		this.querySim = sim;
	}

	public void setAVG(double s1) {
		this.AVGSim = s1;
		
	}

}
