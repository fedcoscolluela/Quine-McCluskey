package quine.mccluskey.method.simulator;

import java.util.ArrayList;
import java.util.Stack;

public class MainClass {
	
	//initializing list of array for terms and groupings
	private int maxLength = -110;
	private ArrayList<Term> terms = new ArrayList<Term>();
	private ArrayList<ArrayList<Term> > termsGroups = new ArrayList<ArrayList<Term>>();
	private ArrayList<ArrayList<ArrayList<Term>>> newGroup = new ArrayList<ArrayList<ArrayList<Term>>>(); 
	private ArrayList<Integer> minterms = new ArrayList<Integer>(); 
	
	private ArrayList<Term> finalPrimeTerms = new ArrayList<Term>();
	private ArrayList<Term> essentials = new ArrayList<Term>();
	private ArrayList<ArrayList<Term>> restPrimes = new ArrayList<ArrayList<Term>>() ;
	private StringBuilder outPut = new StringBuilder();
    int [][] tempChart ;
    
    //converts the number to binary
	public String toBinary(int num){
		return Integer.toBinaryString(num);
	}

	//method to add a value to minterm list
	public void addMinterm(int value){
	    Term mt = new Term(toBinary(value), false, value);
	    terms.add(mt);
	    minterms.add(value);
	}

	//method to add a value to don't care list
	public void addDontCare(int value){
	    Term mt = new Term(toBinary(value), true, value);
	    terms.add(mt);
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
	public void callQuine(){
		groupingTerms();	
		newGroup.add(termsGroups);
		RecursiveQuine(1);
	}
	
	//simulating the comparison of digits (i.e., different or not)
	public boolean canBeReducedTogether(String a, String b){
		int countOfDiff = 0;
		for(int i=0; i<a.length(); i++) if(a.charAt(i) != b.charAt(i) )
			countOfDiff++;
			
		return (countOfDiff == 1);	//returns true of different count is only 1
	}
	
	//invokes the getReducedExpression method to group binary 
	//numbers with only one different digit, and replaces such digit 
	//with a dash (-)
	public void ReduceThem(int level, int i, int j, int k){
		String newExpr =  getReducedExpression(newGroup.get(level).get(i).get(j).getValue(), newGroup.get(level).get(i+1).get(k).getValue());
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
	public String getReducedExpression(String a, String b){
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
	public void RecursiveQuine(int level){
		
		int numOfTokens = 0;
		// create a new 2D array for the new level 
		newGroup.add(new ArrayList<ArrayList<Term>>());
		int numOfRows = maxLength + 3;
	    for(int i=0; i<numOfRows; i++){
	    	newGroup.get(level).add(new ArrayList<Term>());
	    }
	    int cache1 = newGroup.get(level-1).size() - 1;
	    for(int i=0; i<cache1; i++){
	    	for(int j=0; j<newGroup.get(level-1).get(i).size(); j++){
	    		for(int k=0; k<newGroup.get(level-1).get(i+1).size(); k++){
		    		if(canBeReducedTogether(newGroup.get(level-1).get(i).get(j).getValue(), newGroup.get(level-1).get(i+1).get(k).getValue())){
		    			ReduceThem(level-1,i,j,k);
		    			numOfTokens++;
		    		}
		    	}
	    	}
	    }
	    
	    if(numOfTokens > 0) // recurse
	    	RecursiveQuine(level + 1);
	    
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
	//and/or don’t cares, if any) to the String output and shows the 
	//grouping of binary conversion of these terms based on 
	//number of 1’s, per level of comparison
	public void printIt(){
            outPut.append("Terms:\n");
            for(int i=0; i<termsGroups.size(); i++){
                boolean ok2 = false;
                for(int j=0; j<termsGroups.get(i).size(); j++){
                    ok2 = true;
                    outPut.append(termsGroups.get(i).get(j).getNumbersItCovers() + "\t");
                    outPut.append(termsGroups.get(i).get(j).getValue());
                    //outPut.append("  " + termsGroups.get(i).get(j).isDontCare());
                    if(termsGroups.get(i).get(j).isDontCare()){
                        outPut.append("\t[don't care]\n");
                    }else{
                        outPut.append("\t[minterm]\n");
                    }
                }
                if(ok2)
                    outPut.append("---------------------\n");
            }
            
            // printing our 3D arraylist
            outPut.append("\nQuine-McCluskey Simulation:\n");
            for(int i=0; i<newGroup.size(); i++){
                    boolean checker1 = false;
                    boolean checker3 = false;
                    for(int j=0; j<newGroup.get(i).size(); j++){
                            boolean checker2 = false;
                            for(int k=0; k<newGroup.get(i).get(j).size(); k++){
                            	checker1 = true;
                            	checker2 = true;
                                    if(!checker3){
                                            outPut.append("*Level " + i + ":\n");
                                            checker3 = true;
                                    }
                                    outPut.append(newGroup.get(i).get(j).get(k).getValue() + "\t" + newGroup.get(i).get(j).get(k).getNumbersItCovers().toString());
                                    outPut.append(newGroup.get(i).get(j).get(k).isDeleted()? "\t(/)\n":"\n");
                                    //System.out.println(G.get(i).get(j).get(k).getValue() + "  " + G.get(i).get(j).get(k).getNumbersItCovers().toString());
                            }
                            if(checker2 == true)
                                    outPut.append("-------------------------\n");
                                    //System.out.println("-------------------------");
                    }
                    if(checker1 == true)
                            outPut.append("=================\n");
                    //System.out.println("=================");
            }
    }
	
	//appends the essential terms to String outPut to be printed
    public void printPrime(){
            outPut.append("\nEssential Ones:\n");
            //System.out.println("Essential Ones");
            for(int i=0; i<essentials.size(); i++){
                    outPut.append(appendPrime(essentials.get(i).getValue().toString()) + "\t\t");
                    outPut.append(essentials.get(i).getValue().toString() + "\t" + essentials.get(i).getNumbersItCovers().toString()+"\n");
                    //System.out.println(essentials.get(i).getValue().toString() + "  " + essentials.get(i).getNumbersItCovers().toString());
            }
        outPut.append("*************************\n");
            //System.out.println("-------------------------");
    }
    
    //appends the complete list of terms, and shows all possible 
    //options of answers based on the prime implicants
    public void printSols(){
            outPut.append("Possible Options:\n");
            if(restPrimes.size() == 0){
                    outPut.append("None \n*************************\n");
                    return;
            }
            for(int i=0; i<restPrimes.size(); i++){
                    if(i>0)outPut.append("////////////// OR ////////////\n");
                    for(int j = 0 ; j < restPrimes.get(i).size() ; j ++){
                    outPut.append(appendPrime(restPrimes.get(i).get(j).getValue().toString()) + " ");
                    outPut.append(restPrimes.get(i).get(j).getValue().toString() + "\t" + restPrimes.get(i).get(j).getNumbersItCovers().toString() +"\n");  
                    }
            }
    }

    //finally displays the formatted solution, that is, in the form of 
    //F = AB’, etc
    public String printFormattedSols(){
            StringBuilder form = new StringBuilder();
            StringBuilder essen = new StringBuilder();
            form.append("Solutions:\n\n");
            for(int i=0; i<essentials.size(); i++){
                    if(i>0) essen.append(" + ");
                    essen.append(appendPrime(essentials.get(i).getValue().toString()));
            }
            if(restPrimes.size() == 0)
                    form.append("F = " + essen);
            for(int i=0; i<restPrimes.size(); i++){
                    form.append("F = ");
                    form.append(essen);
                    if(essen.length() > 0 && restPrimes.get(i).size() > 0)
                            form.append(" + ");
                    for(int j = 0 ; j < restPrimes.get(i).size() ; j ++){
                            if(j>0) form.append(" + ");
                            form.append(appendPrime(restPrimes.get(i).get(j).getValue().toString()));
                    }
                    form.append("\n*************************\n");
            }
            return form.toString();
	}
	
    //replaces digit 0 with an apostrophe (‘) denoting a prime, and 
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
		return  "Solution:\n\n" + outPut.toString();
	}

	//returns all prime implicants from the passed 2d primeChart
	public ArrayList<Term> processChart(int [][] primeChart){
		ArrayList<Term> primes = new ArrayList<Term>();
		int width = primeChart[0].length ;
		int height = primeChart.length ;
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
			int i , j ;
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
					break ;}
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
		for(int v = 0 ; v <termsDeleted.length ; v ++){
			if(termsDeleted[v] == false){
				f=true ;
				break;}}
		
		if(f){
		ArrayList<String> temp = checkPossible(termsDeleted , primeDeleted ,primeChart );
		ArrayList<String> poss = processMultiplication(temp);
		for(int i = 0 ; i < poss.size() ; i ++){
			restPrimes.add(new ArrayList<Term>());
				for(int j = 0 ; j < poss.get(i).length() ; j ++){
					restPrimes.get(i).add(finalPrimeTerms.get((int)poss.get(i).charAt(j)-48));}}}
		return primes ;
	}
	
	//Returns an array of String which denote all terms that are 
	//possible to consider for alternative answers (employs 
	//Petrick’s method)
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
	
	// Employs the Petrick’s method which looks for other 
	//alternative solution by considering other prime implicants in 
	//the primeChart
	public ArrayList<String> checkPossible(boolean [] termsDeleted ,boolean [] primeDeleted , int [][] primeChart ){
		Stack<ArrayList<String>> multi = new Stack<ArrayList<String>>() ;
		for(int i = 0 ; i < termsDeleted.length ; i ++){
			if(termsDeleted[i] == true)
				continue ;
			ArrayList<String> temp = new ArrayList<String>();
			for(int j = 0 ; j < primeDeleted.length ;  j ++){
				if(primeChart[j][i] == 1)
					temp.add(Integer.toString(j));
			}
			multi.push(temp);
			}
		while(true){
			ArrayList<String> cache = new ArrayList<String>();
			ArrayList<String> term1 = multi.pop();
			if(multi.size() == 0)
				return term1 ;
			ArrayList<String> term2 = multi.pop();
			for(int i = 0 ; i < term1.size() ; i ++){
				for(int j = 0 ;j < term2.size() ; j ++){
					StringBuilder res = new StringBuilder();
					res.append(term1.get(i));
					if(res.lastIndexOf(term2.get(j)) == -1)
						res.append(term2.get(j));
					cache.add(res.toString());
				}}
			multi.push(cache);
		}
				
	}
	
	//adds prime implicants to the array of terms finalPrimeTerms
	public void getPrimeImplicants(){
		for(int i=0; i<newGroup.size(); i++){
			for(int j=0; j<newGroup.get(i).size(); j++){
				for(int k=0; k<newGroup.get(i).get(j).size(); k++){
					if(!newGroup.get(i).get(j).get(k).isDeleted() && !newGroup.get(i).get(j).get(k).isDontCare())
						finalPrimeTerms.add(newGroup.get(i).get(j).get(k));
				}
			}
		}
	}
	
	//fills in the initialized tempChart with 2d table that shows 0’s 
	//and 1’s (i.e., the pair (2, 6) shows 1 on both 2 and 6 meaning 
	//they are checked in the chart, while all other terms are not)
	public void createChart (){
		int [] [] primeChart = new int [finalPrimeTerms.size()][minterms.size()] ;
		for(int i = 0 ; i < primeChart.length ; i ++){
			for(int j = 0 ; j < primeChart[0].length ; j ++){
				primeChart[i][j] = 0 ;
			}}
		for(int i = 0 ; i < finalPrimeTerms.size() ; i ++){
			for(int j = 0 ; j < minterms.size() ; j ++){
				for(int k = 0 ; k < finalPrimeTerms.get(i).getNumbersItCovers().size() ; k ++)
					if(finalPrimeTerms.get(i).getNumbersItCovers().get(k).intValue() == minterms.get(j).intValue())
						primeChart[i][j] = 1 ;	//changes value from 0 to 1 (1 means checked)
			}
		}
        
		tempChart = primeChart ;
		essentials = processChart(primeChart);
	}
        
	//prints the primeChart that simulates the last step for QuineMcCluskey method, which is determining the final terms to 
	//include in the answer through a table
	public void printChart(){
            outPut.append("\nPrime Chart:\n\n");
            for(int i = 0 ; i < minterms.size() ; i ++)
                outPut.append(minterms.get(i)+"  ");
            outPut.append("\n");
            for(int i = 0 ; i < tempChart.length ; i ++){
                for(int j = 0 ; j < tempChart[0].length ; j++){
                        outPut.append(tempChart[i][j]);
                        if(minterms.get(j) > 10) outPut.append("");
                        if(minterms.get(j) == 1 || minterms.get(j) == 0){
                        outPut.append("  ");}
                        else{
                        if(Math.log10(minterms.get(j)) == (int)Math.log10(minterms.get(j)) && minterms.get(j)!=1)
                            outPut.append("  ");
                        for(int g=0; g<Math.log10(minterms.get(j)); g++)
                           outPut.append("  ");}
                }
                outPut.append("  ");
                outPut.append(appendPrime(finalPrimeTerms.get(i).getValue()));
                outPut.append("\n");
            }
            outPut.append("\n=======================n");
        }
}
	