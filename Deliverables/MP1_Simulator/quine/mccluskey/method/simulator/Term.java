package quine.mccluskey.method.simulator;

import java.util.ArrayList;

//Class for handling all the terms inputted by the user

public class Term {
	
	private String value;
	private boolean isDontCare;
	private boolean deleted;
	private ArrayList<Integer>  numbersItCovers = new ArrayList<Integer>();
	private boolean isEssential;
	
	//constructor to be called by the MainClass. Kindly refer accordingly :))
	public Term(String value, boolean isDontCare, int num){
		this.setValue(value);
		this.isDontCare = isDontCare;
		this.setDeleted(false);
		this.getNumbersItCovers().add(num);
		this.isEssential = false;
	}
	
	//constructor to be called by the MainClass. Kindly refer accordingly :))
	public Term(String value, boolean isDontCare){
		this.setValue(value);
		this.isDontCare = isDontCare;
		this.setDeleted(false);
		this.isEssential = false;
	}
	
	//retrieved the string value of term
	public String getValue(){
		return this.value;
	}
	
	//sets the value of the term
	public void setValue(String value) {
		this.value = value;
	}
	
	//depending on whether the term is deleted or not, the MainClass calls this
	//function to update the terms
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
	//checks whether a term is under don't care list or not
	public boolean isDontCare(){
		return this.isDontCare;
	}

	//checks whether a term is deleted or not
	public boolean isDeleted() {
		return deleted;
	}

	//implements the logic of similarity in number of 1's during the grouping
	//specifically retrieves such numbers in the same group
	public ArrayList<Integer> getNumbersItCovers() {
		return numbersItCovers;
	}

	//implements the logic of similarity in number of 1's during the grouping
	//specifically adds such numbers into the same group
	public void addNumbersItCovers(ArrayList<Integer> newCovered) {
		for(int i=0; i<newCovered.size(); i++)
			this.numbersItCovers.add(newCovered.get(i));
	}
	
	//sets a term as essential one
	public void setEssential(){
		this.isEssential = true;
	}
}

