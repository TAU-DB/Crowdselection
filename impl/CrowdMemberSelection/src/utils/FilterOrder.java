package utils;

public class FilterOrder implements Filter {
	
	private String filter;
	private String by;

	public FilterOrder(String filter, String f) {
		this.filter = filter;
		this.by = f;
	}
	
	public String getFilterName(){
		return filter;
	}
	
	public String getBy(){
		return by;
	}
	
	public String toString(){
		return filter+ " "+by;
	}
	
	
	

}
