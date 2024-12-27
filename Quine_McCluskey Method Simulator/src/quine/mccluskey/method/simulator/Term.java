package quine.mccluskey.method.simulator;

import java.util.ArrayList;

public class Term {
	
	private String value;
	private boolean isDontCare;
	private boolean deleted;
	private ArrayList<Integer>  numbersItCovers = new ArrayList<Integer>();
	private boolean isEssential;
	
	public Term(String value, boolean isDontCare, int num){
		this.setValue(value);
		this.isDontCare = isDontCare;
		this.setDeleted(false);
		this.getNumbersItCovers().add(num);
		this.isEssential = false;
	}
	// another constructor
	public Term(String value, boolean isDontCare){
		this.setValue(value);
		this.isDontCare = isDontCare;
		this.setDeleted(false);
		this.isEssential = false;
	}
	
	public String getValue(){
		return this.value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
	public boolean isDontCare(){
		return this.isDontCare;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public ArrayList<Integer> getNumbersItCovers() {
		return numbersItCovers;
	}

	public void addNumbersItCovers(ArrayList<Integer> newCovered) {
		for(int i=0; i<newCovered.size(); i++)
			this.numbersItCovers.add(newCovered.get(i));
	}
	
	public void setEssential(){
		this.isEssential = true;
	}
}

