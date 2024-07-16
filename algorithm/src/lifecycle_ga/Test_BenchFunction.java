package lifecycle_ga;


public class Test_BenchFunction {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	
		//File file = new File ("src/lifecycle_ga/input_data/M_" + 1 + "_D" + 30 + ".txt");
		//File fileShift = new File ("src/main/java/thesis/CEC2017/input_data/shift_data_" + funcNum + ".txt");		
		
		//String input_data = "src/lifecycle_ga/input_data";
		String input_data = "bench/cec2017/input_data";  //----CEC2017 input_data folder location.

		BenchFunction test = new BenchFunction(input_data, 30, 1);
		
		//public double compute (double [] x) {
		double[] chromo30D = new double[] {-1.6284996220051426E-8, -1.5443887187371026E-8, -7.614843769635385E-9, -5.988771896782568E-9, -3.0887654827658174E-8, -2.7843098105069148E-8, -5.8706394020079025E-9, -1.151485896059765E-8, 1.137371695365178E-8, -2.575599325119266E-9, 1.8423469280603688E-8, 1.96042039413075E-8, -2.4535107830331557E-8, 3.946457140557963E-8, -6.145966406431776E-9, -1.2017189521950826E-7, 1.608609708684603E-8, -7.387744379846048E-10, -1.363555142302918E-8, 1.4771877420133069E-8, -3.4018801432734496E-8, -9.232101706627139E-8, -1.858578682744495E-8, 6.9169239106440085E-9, -5.769221374425453E-9, -2.741467508241492E-8, 1.3482886767834856E-8, -3.360192035979877E-8, -1.823338083318675E-8, -6.59362034212529E-8};
		
		double fitness = test.compute(chromo30D);
		System.out.println(fitness);
		//----System.out.println(fitness -100);
	}

}
