package lifecycle_ga;

import java.io.File;                   // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner;              // Import the Scanner class to read text files
import java.util.HashMap;              // Import the HashMap class to use as a dictionary
import java.util.Map;                  // Import Map class to use iteration over a HashMap


public class ClassConfiguration {	
	private static final String header = "ClassConfiguration";

	public String debug_monitor = "X";  //----Alternatives: X, T, and F
	public String input_data = "";  //----Directory location for the (CEC2017) input_data folder.
	public String benchmark = "";  //----CEC2017 will adjust target_fitness, max_evaluations, and max_stagnation accordingly.

	public double lower_bounds = 0;  //----"bounds_min" in search space
	public double upper_bounds = 0;  //----"bounds_max" in search space

	public int function_num = 0;
	public int dimensions = 0;
	public double target_fitness = 0;
	
	public int max_population = 0;  //----Valores de configuracion para la evolucion.
	public int sample_size = 0;
	public int max_evaluations = 0;
	public int max_stagnation = 0;
	public int mutation_rate = 0;
		
	public int max_age = 0;  //----Argumentos especificos para el algoritmo.

	public int survive_min = 0;  //----Argumentos para calcular la exigencia progresiva dinamica (ej: 80 a 100).
	public int survive_max = 0;
	public double pressure = 0;
	

	//----Constructores de la clase.
	public ClassConfiguration(){
		try {
			input_data = "bench/cec2017/input_data";  //----CEC2017 input_data folder location.
			benchmark = "CEC2017";  //----CEC2017 will adjust target_fitness, max_evaluations, and max_stagnation accordingly.

			lower_bounds = -100; //----BenchFunction CEC2017 MIN: -100.0
			upper_bounds = 100;  //----BenchFunction CEC2017 MAX:  100.0

			read_configuration();

			if (benchmark.toUpperCase().equals("CEC2017")){
				//----System.out.println("benchmark: " + benchmark + ": " + benchmark.equals("CEC2017"));

				target_fitness = function_num * 100;  //----Alternativas: [0.0, 0.0000000000001]
				max_evaluations = 10000 * dimensions;  //----Default: CEC2017 MaxFES 10,000 * Dimension => 30 Dim : 300,000
				max_stagnation = 10000 * dimensions;  //----Default: Disabled (equal to max_evaluations)
			}

			showConfiguration(true);
		}
		catch (Exception e) {
			System.out.println(header + ".ClassConfiguration().Exception: " + e);
		}
	}


	private void read_configuration() {
		try {
			String folder_location = "cfg";  //----Experiment configuration folder location.
            File file = new File (folder_location + "/" + "LCGA_Configuration" + ".txt");  //----Initialize file
            
            Scanner fileReader = new Scanner(file);
			while (fileReader.hasNextLine()){ 
				readLine(fileReader.nextLine()); 
			}
            fileReader.close();
		}
        catch (FileNotFoundException fnfe) {
            System.out.println(header + ".read_configuration().FileNotFoundException: " + fnfe);
        }
		catch (Exception e) {
			System.out.println(header + ".read_configuration().Exception: " + e);
		}
	}


	private void readLine(String current_line){
		try{
			int indexA = current_line.indexOf("=");
			int indexB = current_line.indexOf(";");

			if (indexA > 0 && indexB > indexA){
				String foundKey = current_line.substring(0, indexA).trim().toUpperCase();
				String foundValue = current_line.substring(indexA +1, indexB).trim();

				setConfigurationValue(foundKey, foundValue);
				//----System.out.println("current_line: " + foundKey + ": " + foundValue);
			}
		}
		catch (Exception e) {
			System.out.println(header + ".readLine().Exception: " + e);
		}
	}	

	
	public boolean setConfigurationValue(String key, String value){
		boolean flag = true;

		try {
			switch (key.toUpperCase()) {
				case "INPUT_DATA": input_data = value.trim(); break;
				case "BENCHMARK": benchmark = value.trim().toUpperCase(); break;
				case "LOWER_BOUNDS": lower_bounds = Double.parseDouble(value); break;
				case "UPPER_BOUNDS": upper_bounds = Double.parseDouble(value); break;					
				case "TARGET_FITNESS": target_fitness = Double.parseDouble(value); break;
				case "BENCHMARK_FUNCTION": function_num = Integer.parseInt(value); break;
				case "DIMENSIONS": dimensions = Integer.parseInt(value); break;
				case "POPULATION": max_population = Integer.parseInt(value); break;
				case "SAMPLE_SIZE": sample_size = Integer.parseInt(value); break;					
				case "EVALUATIONS": max_evaluations = Integer.parseInt(value); break;
				case "STAGNATION": max_stagnation = Integer.parseInt(value); break;
				case "MUTATION_RATE": mutation_rate = Integer.parseInt(value); break;
				case "MAX_AGE": max_age = Integer.parseInt(value); break;
				case "SURVIVE_MIN": survive_min = Integer.parseInt(value); break;
				case "SURVIVE_MAX": survive_max = Integer.parseInt(value); break;
				case "PRESSURE": pressure = Double.parseDouble(value); break;
				default: flag = false;
			}
		}
		catch (Exception e) {
			System.out.println(header + ".setConfigurationValue().Exception: " + e + ".key/value: " + key + "/" + value);
			flag = false;
		}		

		return flag;
	}


	public String getConfigurationValue(String key){
		String value = "";

		try{
			switch (key.toUpperCase()) {
				case "INPUT_DATA": value = String.valueOf(input_data); break;
				case "BENCHMARK": value = String.valueOf(benchmark); break;
				case "LOWER_BOUNDS": value = String.valueOf(lower_bounds); break;
				case "UPPER_BOUNDS": value = String.valueOf(upper_bounds); break;
				case "TARGET_FITNESS": value = String.valueOf(target_fitness); break;
				case "BENCHMARK_FUNCTION": value = String.valueOf(function_num); break;
				case "DIMENSIONS": value = String.valueOf(dimensions); break;
				case "POPULATION": value = String.valueOf(max_population); break;
				case "SAMPLE_SIZE": value = String.valueOf(sample_size); break;					
				case "EVALUATIONS": value = String.valueOf(max_evaluations); break;
				case "STAGNATION": value = String.valueOf(max_stagnation); break;
				case "MUTATION_RATE": value = String.valueOf(mutation_rate); break;
				case "MAX_AGE": value = String.valueOf(max_age); break;
				case "SURVIVE_MIN": value = String.valueOf(survive_min); break;
				case "SURVIVE_MAX": value = String.valueOf(survive_max); break;
				case "PRESSURE": value = String.valueOf(pressure); break;
				default: value = "";
			}
		}
		catch (Exception e) {
			System.out.println(header + ".getConfigurationValue().Exception: " + e);
		}

		return value;
	}


	public void showConfiguration(boolean user_input){
		try{
			System.out.println(".............................");
			System.out.println("  + " + "BENCHMARK" + ": " + getConfigurationValue("BENCHMARK") + " (" + getConfigurationValue("INPUT_DATA") + ")");
			System.out.println("  + " + "LOWER_BOUNDS" + ": " + getConfigurationValue("LOWER_BOUNDS"));
			System.out.println("  + " + "UPPER_BOUNDS" + ": " + getConfigurationValue("UPPER_BOUNDS"));
			System.out.println("  + " + "TARGET_FITNESS" + ": " + getConfigurationValue("TARGET_FITNESS"));
			System.out.println("  + " + "BENCHMARK_FUNCTION" + ": " + getConfigurationValue("BENCHMARK_FUNCTION") + " - " + getFunctionName());
			System.out.println("  + " + "DIMENSIONS" + ": " + getConfigurationValue("DIMENSIONS"));
			System.out.println("  + " + "POPULATION" + ": " + getConfigurationValue("POPULATION"));
			System.out.println("  + " + "SAMPLE_SIZE" + ": " + getConfigurationValue("SAMPLE_SIZE"));			
			System.out.println("  + " + "EVALUATIONS" + ": " + getConfigurationValue("EVALUATIONS"));
			System.out.println("  + " + "STAGNATION" + ": " + getConfigurationValue("STAGNATION"));
			System.out.println("  + " + "MUTATION_RATE" + ": " + getConfigurationValue("MUTATION_RATE"));
			System.out.println("  + " + "MAX_AGE" + ": " + getConfigurationValue("MAX_AGE"));
			System.out.println("  + " + "SURVIVE_MIN" + ": " + getConfigurationValue("SURVIVE_MIN"));
			System.out.println("  + " + "SURVIVE_MAX" + ": " + getConfigurationValue("SURVIVE_MAX"));
			System.out.println("  + " + "PRESSURE" + ": " + getConfigurationValue("PRESSURE"));
			System.out.println(".............................");

			if (user_input){
				Scanner scanner = new Scanner (System.in);
				System.out.print("(Please press Enter key to continue...)");
				String name = scanner.nextLine(); // Get what the user types.				
			}
		}
		catch (Exception e) {
			System.out.println(header + ".showConfiguration().Exception: " + e);
		}
	}
	
	//----Returns the full name of the chosen function.
    public String getFunctionName () {
    	String function_name = "";

    	try{
	        switch (function_num) {
	            case 0:  function_name = "Shifted and Rotated Sum of Different Power Function"; break;
	            case 1:  function_name = "Shifted and Rotated Bent Cigar Function"; break;
	            case 2:  function_name = "Shifted and Rotated Zakharov Function"; break;
	            case 3:  function_name = "Shifted and Rotated Rosenbrock's Function"; break;
	            case 4:  function_name = "Shifted and Rotated Rastrigin's Function"; break;
	            case 5:  function_name = "Shifted and Rotated Schaffer F7 Function"; break;
	            case 6:  function_name = "Shifted and Rotated Lunacek Bi-Rastrigin's Function"; break;
	            case 7:  function_name = "Shifted and Rotated Non-Continuous Rastrigin's Function"; break;
	            case 8:  function_name = "Shifted and Rotated Levy Function"; break;
	            case 9:  function_name = "Shifted and Rotated Schwefel's Function"; break;
	            case 10: function_name = "Hybrid function 1"; break;
	            case 11: function_name = "Hybrid function 2"; break;
	            case 12: function_name = "Hybrid function 3"; break;
	            case 13: function_name = "Hybrid function 4"; break;
	            case 14: function_name = "Hybrid function 5"; break;
	            case 15: function_name = "Hybrid function 6"; break;
	            case 16: function_name = "Hybrid function 7"; break;
	            case 17: function_name = "Hybrid function 8"; break;
	            case 18: function_name = "Hybrid function 9"; break;
	            case 19: function_name = "Hybrid function 10"; break;
	            case 20: function_name = "Composition function 1"; break;
	            case 21: function_name = "Composition function 2"; break;
	            case 22: function_name = "Composition function 3"; break;
	            case 23: function_name = "Composition function 4"; break;
	            case 24: function_name = "Composition function 5"; break;
	            case 25: function_name = "Composition function 6"; break;
	            case 26: function_name = "Composition function 7"; break;
	            case 27: function_name = "Composition function 8"; break;
	            case 28: function_name = "Composition function 9"; break;
	            case 29: function_name = "Composition function 10"; break;
	            default: function_name = "Function not found";
	        }    		
    	}
		catch (Exception e) {
			System.out.println(header + ".getFunctionName().Exception: " + e);
		}

		return function_name;
    }


}
