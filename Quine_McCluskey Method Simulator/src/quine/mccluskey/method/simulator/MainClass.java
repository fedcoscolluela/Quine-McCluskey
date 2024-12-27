package quine.mccluskey.method.simulator;

import java.util.ArrayList;
import java.util.Stack;

public class MainClass {
	
	//initializing list of array for terms and groupings
	private int maxLength = -150;	//intended to contain the maximum count of digits in a binary value
    int [][] chartContainer;
	private ArrayList<Term> finalPrimeTerms = new ArrayList<Term>();
	private ArrayList<Term> essentials = new ArrayList<Term>();
	private ArrayList<ArrayList<Term>> primesLeft = new ArrayList<ArrayList<Term>>() ;
	private StringBuilder finalResult = new StringBuilder();
    
	private ArrayList<Term> terms = new ArrayList<Term>();
	private ArrayList<ArrayList<Term> > termsGroups = new ArrayList<ArrayList<Term>>();
	private ArrayList<ArrayList<ArrayList<Term>>> newGroup = new ArrayList<ArrayList<ArrayList<Term>>>(); 
	private ArrayList<Integer> minterms = new ArrayList<Integer>(); 
    
    //converts the number to binary
	public String toBinary(int num){
		return Integer.toBinaryString(num);
	}

	//method to add a value to minterm list
	public void addMinterm(int value){
	    Term termInput = new Term(toBinary(value), false, value);
	    terms.add(termInput);
	    minterms.add(value);
	}

	//method to add a value to don't care list
	public void addDontCare(int value){
	    Term termInput = new Term(toBinary(value), true, value);
	    terms.add(termInput);
	}
	
	//counts the number of digits of every integer's binary value and selects the maximum
	//(i.e., maxLength is set to 7 if highest integer input is 122 since its binary value is 1111010)
	public void getMaximumTerm(){  
	    for(int i=0; i<terms.size(); i++){
	        int curr = terms.get(i).getValue().length();
	        if(maxLength < curr)
	            maxLength = curr;
	    }
	}
	
	//left pads 0 in case there are missing digits at the left of binary value of the integer
	public void leftPadZero(){
	    getMaximumTerm();
	    for(int i=0; i<terms.size(); i+=1){
	        int diff = maxLength - terms.get(i).getValue().length();
	        for(int j=0; j<diff; j+=1)
	            terms.get(i).setValue("0" + terms.get(i).getValue());
	    }
	}
	
	//grouping terms based on the number of 1
	public void groupingTerms(){
	    leftPadZero();
	    
	    int numOfRows = maxLength + 3; //simply adds 3 extra rows
	    for(int i=0; i<numOfRows; i++){
	    	termsGroups.add(new ArrayList<Term>());
	    }
	    
	    for(int i=0; i<terms.size(); i++){
	        String t = terms.get(i).getValue();
	        Term newTerm = new Term(t, terms.get(i).isDontCare());
	        newTerm.addNumbersItCovers(terms.get(i).getNumbersItCovers());
	        int toRow = countOnes(t);
	        //adding the terms to termsGroups of respective count of 1's
			termsGroups.get(toRow).add(newTerm);
	    }
	}

	//using for loop to count the number of ones 
	private int countOnes(String t) {
		int count = 0;
	    for(int i=0; i<t.length(); i++){
	        count += (t.charAt(i) == '1') ? 1 : 0;
	    }
	    return count;
	}
	
	//simulates Quine Mccluskey Method by grouping terms thru calling the methods above
	public void tabulateNewGroups(){
		groupingTerms();	
		newGroup.add(termsGroups);
		levelRecursion(1);
	}
	
	//simulating the comparison of digits (i.e., different or not)
	public boolean onlyOneDiff(String a, String b){
		int countOfDiff = 0;
		for(int i=0; i<a.length(); i++) if(a.charAt(i) != b.charAt(i) )
			countOfDiff++;
			
		return (countOfDiff == 1);	//returns true of different count is only 1
	}
	
	//invokes the getReducedExpression method to group binary 
	//numbers with only one different digit, and replaces such digit 
	//with a dash (-)
	public void ReduceThem(int level, int i, int j, int k){
		String newExpr =  appendDashtoDiff(newGroup.get(level).get(i).get(j).getValue(), newGroup.get(level).get(i+1).get(k).getValue());
		newGroup.get(level).get(i).get(j).setDeleted(true);
		newGroup.get(level).get(i+1).get(k).setDeleted(true);
		int toRow = countOnes(newExpr);
		if(!isExist(newGroup.get(level+1).get(toRow),newExpr)){ // add it
			Term toBeAdded = new Term(newExpr, false);
			toBeAdded.addNumbersItCovers(newGroup.get(level).get(i).get(j).getNumbersItCovers());
			toBeAdded.addNumbersItCovers(newGroup.get(level).get(i+1).get(k).getNumbersItCovers());
			newGroup.get(level+1).get(toRow).add(toBeAdded);
		}
	}
	
	//replaces different digit with a dash "-", provided they satisfy the rule of checking only one difference
	public String appendDashtoDiff(String a, String b){
		StringBuilder reducedExpr = new StringBuilder();
		for(int i=0; i<a.length(); i++){
			if(a.charAt(i) == b.charAt(i))
				reducedExpr.append(a.charAt(i));
			else
				reducedExpr.append("-");
		}
		return reducedExpr.toString();
	}
	
	//goes through level of comparison of differences and checks if 
	//two binary numbers only have one (1) different digit, then 
	//pairs such number for further comparison with other 
	//potential pairs/group
	public void levelRecursion(int level){
		
		int numOfTokens = 0;
		// create a new 2D array for the new level 
		newGroup.add(new ArrayList<ArrayList<Term>>());
		int numOfRows = maxLength + 3;
	    for(int i=0; i < numOfRows; i++){
	    	newGroup.get(level).add(new ArrayList<Term>());
	    }
	    int container = newGroup.get(level-1).size() - 1;
	    for(int i=0; i<container; i++){
	    	for(int j=0; j<newGroup.get(level-1).get(i).size(); j++){
	    		for(int k=0; k<newGroup.get(level-1).get(i+1).size(); k++){
		    		if(onlyOneDiff(newGroup.get(level-1).get(i).get(j).getValue(), newGroup.get(level-1).get(i+1).get(k).getValue())){
		    			ReduceThem(level-1,i,j,k);
		    			numOfTokens++;
		    		}
		    	}
	    	}
	    }
	    
	    if(numOfTokens > 0) // recurse
	    	levelRecursion(level + 1);
	    
	}
	
	//boolean method to check whether a term is already added in the list of terms during the grouping
	public boolean isExist(ArrayList<Term> arr, String expr){
		for(int i=0; i<arr.size(); i++){
			if(arr.get(i).getValue().equals(expr))
				return true;
		}
		return false;
	}
	
	//appends the output (including list of all entry of minterm 
	//and/or donâ€™t cares, if any) to the String output and shows the 
	//grouping of binary conversion of these terms based on 
	//number of 1â€™s, per level of comparison
	public void printTabulation(){
		finalResult.append("Shown below are the given terms:\n");
            for(int i=0; i<termsGroups.size(); i++){
                boolean checker2 = false;
                for(int j=0; j<termsGroups.get(i).size(); j++){
                	checker2 = true;
                    finalResult.append(termsGroups.get(i).get(j).getNumbersItCovers() + "\t");
                    finalResult.append(termsGroups.get(i).get(j).getValue());
                    if(termsGroups.get(i).get(j).isDontCare()){
                    	finalResult.append("    [don't care]\n");
                    }else{
                    	finalResult.append("    [minterm]\n");
                    }
                }
                if(checker2)
                	finalResult.append("---------------------\n");
            }
            
            // printing our 3D arraylist
            finalResult.append("\nQuine-McCluskey Simulation:\n");
            for(int i=0; i<newGroup.size(); i++){
                    boolean checker1 = false;
                    boolean checker3 = false;
                    for(int j=0; j<newGroup.get(i).size(); j++){
                            boolean checker2 = false;
                            for(int k=0; k<newGroup.get(i).get(j).size(); k++){
                            	checker1 = true;
                            	checker2 = true;
                                    if(!checker3){
                                    	finalResult.append("*Level " + i + ":\n");
                                            checker3 = true;
                                    }
                                    finalResult.append(newGroup.get(i).get(j).get(k).getValue() + "\t" + newGroup.get(i).get(j).get(k).getNumbersItCovers().toString());
                                    finalResult.append(newGroup.get(i).get(j).get(k).isDeleted()? "\t(/)\n":"\n");       
                            }
                            if(checker2 == true)
                            	finalResult.append("-------------------------\n");
                                    //System.out.println("-------------------------");
                    }
                    if(checker1 == true)
                    	finalResult.append("////////////////////////////\n");
            }
    }
	
	//appends the essential terms to String outPut to be printed
    public void printPrime(){
    	finalResult.append("\nEssential Ones:\n");
            for(int i=0; i<essentials.size(); i++){
            	finalResult.append(appendPrime(essentials.get(i).getValue().toString()) + "\t\t");
            	finalResult.append(essentials.get(i).getValue().toString() + "\t" + essentials.get(i).getNumbersItCovers().toString()+"\n");
            }
            finalResult.append("*************************\n");
            //System.out.println("-------------------------");
    }
    
    //appends the complete list of terms, and shows all possible 
    //options of answers based on the prime implicants
    public void solutionPrinter(){
    	finalResult.append("Possible Options:\n");
    		//no prime implicants to choose from
            if(primesLeft.size() == 0){
            	finalResult.append("None \n*************************\n");
                    return;
            }
            //if there are prime implicants to choose from
            for(int i=0; i<primesLeft.size(); i++){
                    if(i>0)finalResult.append("//////////////  OR  ////////////\n");
                    for(int j = 0 ; j < primesLeft.get(i).size() ; j ++){
                    	finalResult.append(appendPrime(primesLeft.get(i).get(j).getValue().toString()) + "  ");
                    	finalResult.append(primesLeft.get(i).get(j).getValue().toString() + "\t  " + primesLeft.get(i).get(j).getNumbersItCovers().toString() +"\n");  
                    }
            }
    }

    //finally displays the formatted solution, that is, in the form of 
    //F = AB™, etc
    public String printFormattedSols(){
            StringBuilder form = new StringBuilder();
            StringBuilder essen = new StringBuilder();
            form.append("\n");
            for(int i=0; i<essentials.size(); i++){
                    if(i>0) essen.append(" + ");
                    essen.append(appendPrime(essentials.get(i).getValue().toString()));
            }
            if(primesLeft.size() == 0)
                    form.append("F = " + essen);
            for(int i=0; i<primesLeft.size(); i++){
                    form.append("F = ");
                    form.append(essen);
                    if(essen.length() > 0 && primesLeft.get(i).size() > 0)
                            form.append(" + ");
                    for(int j = 0 ; j < primesLeft.get(i).size() ; j ++){
                            if(j>0) form.append(" + ");
                            form.append(appendPrime(primesLeft.get(i).get(j).getValue().toString()));
                    }
                    form.append("\n*************************\n");
            }
            return form.toString();
	}
	
    //replaces digit 0 with an apostrophe (') denoting a prime, and 
    //does no appending if digit is 1
	private String appendPrime(String s) {
		String primedString = "";
		boolean flag = false;
		for(int i=0; i<s.length(); i++){
			char ch = s.charAt(i);
			if(ch !='-'){
				flag = true;
				primedString += (char) ('A' + i);
				if(ch == '0')
					primedString += "'"; // ( ' )
			}
		}
		if(!flag)
			primedString = "1";
		return primedString;
	}
	
	// simply prints out the outPut string (w/c contains the formatted solution steps) 
	public String finalString() {
		return  "\n" + finalResult.toString();
	}

	//returns all prime implicants from the passed 2d primeChart
	public ArrayList<Term> processChart(int[][] primeChart){
		ArrayList<Term> primes = new ArrayList<Term>();
		int width = primeChart[0].length ;	//number of col
		int height = primeChart.length ;	//number of row
		boolean [] termsDeleted = new boolean [width];
		boolean [] primeDeleted = new boolean [height];
		for(int i = 0 ; i < termsDeleted.length ; i ++)
			termsDeleted[i] = false ;
		for(int i = 0 ; i < primeDeleted.length ; i ++)
			primeDeleted[i] = false ;
		boolean flag = true ;
		while (flag){
			flag = false ;
			int count = 0 ;
			int i, j ;
			int temp = 0 ;
			for(i = 0 ; i < width ; i ++){
				if(termsDeleted[i] == true)
					continue ;
				count = 0;
				for(j = 0 ; j < height ; j ++){
					if(primeDeleted[j] == true )
						continue ;
					if(primeChart[j][i] == 1)
						temp = j ;
					count += primeChart[j][i];}
				if(count == 1){
					flag = true ;
					break ;
					}
				}
			if(flag == true){
				primes.add(finalPrimeTerms.get(temp));
				primeDeleted[temp] =true ;
				for(int z = 0 ; z < width ; z++)
					if(primeChart[temp][z] == 1)
						termsDeleted[z] = true ;
			}
			
		}
		boolean f = false ;
		for(int v = 0 ; v < termsDeleted.length ; v ++){
			if(termsDeleted[v] == false){
				f=true ;
				break;
				}
			}
		
		if (f) {
		ArrayList<String> temp = checkPossible(termsDeleted , primeDeleted ,primeChart );
		ArrayList<String> poss = processMultiplication(temp);
		for(int i = 0 ; i < poss.size() ; i++){
			primesLeft.add(new ArrayList<Term>());
				for(int j = 0 ; j < poss.get(i).length() ; j ++){
					primesLeft.get(i).add(finalPrimeTerms.get((int)poss.get(i).charAt(j)-48));}}}
		return primes ;
	}
	
	//Returns an array of String which denote all terms that are 
	//possible to consider for alternative answers (employs 
	//Petrick™s method)
	public ArrayList<String> processMultiplication(ArrayList<String> temp){
		int minimumlength = 10000000 ;
		ArrayList<String> possibles = new ArrayList<String>(); 
		for(int i = 0 ; i < temp.size() ; i ++)
			if(temp.get(i).length() < minimumlength)
				minimumlength = temp.get(i).length() ;
		for(int i = 0 ; i < temp.size() ; i ++)
			if(temp.get(i).length() == minimumlength)
				possibles.add(temp.get(i));
		return possibles;
	}
	
	// Employs the Petrick's method which looks for other 
	//alternative solution by considering other prime implicants in 
	//the primeChart
	public ArrayList<String> checkPossible(boolean[] termsDeleted ,boolean[] primeDeleted , int[][] primeChart ){
		Stack<ArrayList<String>> stackOfPossibles = new Stack<ArrayList<String>>() ;
		for(int i = 0 ; i < termsDeleted.length ; i++){
			if(termsDeleted[i] == true)
				continue;
			ArrayList<String> temp = new ArrayList<String>();
			for(int j = 0 ; j < primeDeleted.length ;  j++){
				//checks the value at a specific location of 2d table if it evaluates to 1, then adds it to stack
				if(primeChart[j][i] == 1)
					temp.add(Integer.toString(j));
			}
			stackOfPossibles.push(temp);
			}
		while(true){
			ArrayList<String> possibles = new ArrayList<String>();
			//checker if only one element is in the array, hence return it
			ArrayList<String> term1 = stackOfPossibles.pop();
			if(stackOfPossibles.size() == 0)
				return term1 ;	
			
			//if more than element is in the array
			ArrayList<String> term2 = stackOfPossibles.pop();
			for(int i = 0 ; i < term1.size() ; i++){
				for(int j = 0 ;j < term2.size() ; j++){
					StringBuilder output = new StringBuilder();
					output.append(term1.get(i));
					if(output.lastIndexOf(term2.get(j)) == -1)
						output.append(term2.get(j));
					possibles.add(output.toString());
				}}
			stackOfPossibles.push(possibles);
		}
				
	}
	
	//adds prime implicants to the array of terms finalPrimeTerms
	public void getPrimeImplicants(){
		for(int i=0; i<newGroup.size(); i++){
			for(int j=0; j<newGroup.get(i).size(); j++){
				for(int k=0; k < newGroup.get(i).get(j).size(); k++){
					if(!newGroup.get(i).get(j).get(k).isDeleted() && !newGroup.get(i).get(j).get(k).isDontCare())
						finalPrimeTerms.add(newGroup.get(i).get(j).get(k));
				}
			}
		}
	}
	
	//fills in the initialized tempChart with 2d table that shows 0â€™s 
	//and 1â€™s (i.e., the pair (2, 6) shows 1 on both 2 and 6 meaning 
	//they are checked in the chart, while all other terms are not)
	public void fillChart (){
		int [] [] primeChart = new int [finalPrimeTerms.size()][minterms.size()] ;
		for(int i = 0 ; i < primeChart.length ; i++){
			for(int j = 0 ; j < primeChart[0].length ; j ++){
				//setting all values to 0 first, then change some into 1 at next for-loop
				primeChart[i][j] = 0 ;
			}}
		for(int i = 0 ; i < finalPrimeTerms.size() ; i ++){
			for(int j = 0 ; j < minterms.size() ; j ++){
				for(int k = 0 ; k < finalPrimeTerms.get(i).getNumbersItCovers().size() ; k++)
					//if prime term if [2,5], then these numbers both have 1 beneath them, meaning they are checked
					if(finalPrimeTerms.get(i).getNumbersItCovers().get(k).intValue() == minterms.get(j).intValue())
						primeChart[i][j] = 1 ;	//changes value from 0 to 1 (1 means checked)
			}
		}
        
		chartContainer = primeChart ;
		essentials = processChart(primeChart);
	}
        
	//prints the primeChart that simulates the last step for QuineMcCluskey method, which is determining the final terms to 
	//include in the answer through a table
	public void printChart(){
		finalResult.append("\nPrime Chart (1 stands for selected, 0 for not):\n\n");
            for(int i = 0 ; i < minterms.size() ; i ++)
            	finalResult.append(minterms.get(i)+"  ");
            finalResult.append("\n");
            for(int i = 0 ; i < chartContainer.length ; i ++){
                for(int j = 0 ; j < chartContainer[0].length ; j++){
                	finalResult.append(chartContainer[i][j]);
                        if(minterms.get(j) > 10) finalResult.append("");
                        if(minterms.get(j) == 1 || minterms.get(j) == 0){
                        	finalResult.append("  ");}
                        else{
                        	finalResult.append("  ");}
                }
                finalResult.append("  ");
                finalResult.append(appendPrime(finalPrimeTerms.get(i).getValue()));
                finalResult.append("\n");
            }
            finalResult.append("\n///////////////////////////////");
        }
}
	
