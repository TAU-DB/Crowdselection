package utils;

public class FilterLimit implements Filter {

	private String name;
	private int limit;

	public FilterLimit(String filter, int limit) {
		this.name = filter;
		this.limit = limit;
	}
    
	
	public int getLimit(){
		return limit;
	}
	
	@Override
	public String getFilterName() {
		return name;
	}
	
	public String toString(){
		return name+ " "+limit;
	}

}
