package utils;

public class User {
	
	private Term user;
	private double profileSim;
	private double tranSim;
	
	public User(Term t, double pro, double tran){
		user = t;
		profileSim = pro;
		tranSim = tran;
		
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
		return user.toSrting() + " profile similarity: "+profileSim + " tarnsaction similarity: "+ tranSim;
	}

}
