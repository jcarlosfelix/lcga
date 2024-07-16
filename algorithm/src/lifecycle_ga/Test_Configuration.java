package lifecycle_ga;

import java.io.File;                   // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner;              // Import the Scanner class to read text files
import java.util.HashMap;              // Import the HashMap class to use as a dictionary
import java.util.Map;                  // Import Map class to use iteration over a HashMap



public class Test_Configuration {
	private static final String header = "Test_Configuration";
    private static HashMap<String, String> config_map = new HashMap<>(); //----Creating an empty hash map by declaring object of string (key) and string (value).


	public static void main(String[] args) {
    	ClassConfiguration cfg = new ClassConfiguration();
		
		//----read_configuration();
		//----showFoundConfig();
	}



	private static void read_configuration() {
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
	

	private static void readLine(String current_line){
		try{
			int indexA = current_line.indexOf("=");
			int indexB = current_line.indexOf(";");

			if (indexA > 0 && indexB > indexA){
				String foundKey = current_line.substring(0, indexA).trim().toUpperCase();
				String foundValue = current_line.substring(indexA +1, indexB).trim();

				config_map.put(foundKey, foundValue);
				//----System.out.println("current_line: " + foundKey + ": " + foundValue);
			}
		}
		catch (Exception e) {
			System.out.println(header + ".readLine().Exception: " + e);
		}
	}


	private static void showFoundConfig(){
		try{
			System.out.println(".............................");

			//----Iterate the map using for-each loop
			for (Map.Entry<String, String> iterator_map : config_map.entrySet()){
				System.out.println("  + " + iterator_map.getKey() + ": " + iterator_map.getValue());
			}
		}
		catch (Exception e) {
			System.out.println(header + ".showFoundConfig().Exception: " + e);
		}
	}


}