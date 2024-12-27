package quine.mccluskey.method.simulator;


public class ConsoleUI {

	public static void main(String[] args) {
		
		//testing
		
		MainClass ms = new MainClass();
		// 0, 1, 2, 5, 6, 7, 8, 9, 10, 14
		ms.addMinterm(1);
		ms.addMinterm(2);
		ms.addMinterm(4);
		ms.callQuine();
                ms.getPrimeImplicants();
                ms.createChart();
		ms.printIt();
		ms.printPrime();
		ms.printSols();
                String x = ms.finalString();
		System.out.println(x);
	}

}
