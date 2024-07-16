package lifecycle_ga;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class ClassStopwatch {
	private static final String header = "ClassStopwatch";
	
    private long perform_nanos_start = 0;
    private List<Long> perform_nanos_split = null;
    private long perform_nanos_time = 0;
    private long perform_millis_start = 0;
    private List<Long> perform_millis_split = null;
    private long perform_millis_time = 0;
    
    
	//----Constructor de la clase.
	public ClassStopwatch() {
		try {
		    perform_nanos_start = 0;
		    perform_nanos_split = new ArrayList<Long>();
		    perform_nanos_time = 0;
		    perform_millis_start = 0;
		    perform_millis_split = new ArrayList<Long>();
		    perform_millis_time = 0;	
		}
		catch (Exception e) {
			System.out.println(header + ".ClassStopwatch().Exception: " + e);
		}
	}
    

	public void reset() {
		try {
		    perform_nanos_start = 0;
		    perform_nanos_split = new ArrayList<Long>();
		    perform_nanos_time = 0;
		    perform_millis_start = 0;
		    perform_millis_split = new ArrayList<Long>();
		    perform_millis_time = 0;	
		}
		catch (Exception e) {
			System.out.println(header + ".reset().Exception: " + e);
		}
	}

	
	public void start() {
		try {
			reset();
	        perform_nanos_start = System.nanoTime();
	        perform_millis_start = System.currentTimeMillis();
		}
		catch (Exception e) {
			System.out.println(header + ".start().Exception: " + e);
		}
	}
	

	public void stop() {
		try {
			perform_nanos_time = System.nanoTime() - perform_nanos_start;
			perform_millis_time = System.currentTimeMillis() - perform_millis_start;
			perform_nanos_split.add(perform_nanos_time);
			perform_millis_split.add(perform_millis_time);
		}
		catch (Exception e) {
			System.out.println(header + ".stop().Exception: " + e);
		}
	}
	
	
	public void split() {
		try {
			perform_nanos_split.add(System.nanoTime() - perform_nanos_start);
			perform_millis_split.add(System.currentTimeMillis() - perform_millis_start);
		}
		catch (Exception e) {
			System.out.println(header + ".split().Exception: " + e);
		}
	}

	
	public void display_totals() {
		try {
			double perform_nanos_sec = get_nano_seconds(perform_nanos_time);
			double perform_millis_sec = get_milli_seconds(perform_millis_time);
			
			String performance_nanos = double_toString(perform_nanos_sec);
			String performance_millis = double_toString(perform_millis_sec);
			
			System.out.println("---------------------------------------------------");
			System.out.println("-------- S T O P W A T C H - R E C O R D S  -------");
			System.out.println("---------------------------------------------------");
			System.out.println("--- P E R F O R M A N C E - N A N O S : " + performance_nanos + " sec");
			System.out.println("--- P E R F O R M A N C E - M I L L I : " + performance_millis + " sec");
			System.out.println("---------------------------------------------------");
		}
		catch (Exception e) {
			System.out.println(header + ".display_totals().Exception: " + e);
		}
	}
	
	
	//----Regresa el tiempo performance en segundos.
	public String getPerformanceTime() {
		String performance_time = "";
		
		try {
			double perform_nanos_sec = get_nano_seconds(perform_nanos_time);
			performance_time = double_toString(perform_nanos_sec);
		}
		catch (Exception e) {
			System.out.println(header + ".getPerformanceTime().Exception: " + e);
		}
		
		return performance_time;
	}
	
	
    //----Transforma de nanosegundos a segundos.
    private double get_nano_seconds(long timed_ns) {
    	double seconds = 0;
    	
    	try { seconds = (double)timed_ns / 1000000000; }
		catch (Exception e) { System.out.println(header + ".get_nano_seconds().Exception: " + e); }
    	
    	return seconds;
    }
    
    //----Transforma de milisegundos a segundos.
    private double get_milli_seconds(long timed_ms) {
    	double seconds = 0;
    	
    	try { seconds = (double)timed_ms / 1000; }
		catch (Exception e) { System.out.println(header + ".get_milli_seconds().Exception: " + e); }
    	
    	return seconds;
    }
    

    //----Transforma un numero double a cadena, reduciendo a 4 decimales.
    private String double_toString(double number) {
    	return double_toString(number, 4);
    }
    
    //----Transforma un numero double a cadena, reduciendo el numero de decimales.
    private String double_toString(double number, int decimal_places) {
    	String value = "";
    	
    	try {
    		String numerical_format = "";
    		
    		switch (decimal_places) {
	    		case 0: numerical_format = "#"; break;
	    		case 1: numerical_format = "#.#"; break;
	    		case 2: numerical_format = "#.##"; break;
	    		case 3: numerical_format = "#.###"; break;
	    		case 4: numerical_format = "#.####"; break;
	    		case 5: numerical_format = "#.#####"; break;
	    		case 6: numerical_format = "#.######"; break;
	    		default: numerical_format = "#.#######"; break;
    		}
    		
        	DecimalFormat df = new DecimalFormat(numerical_format);
        	value = df.format(number);
    	}
		catch (Exception e) { 
			System.out.println(header + ".double_toString().Exception: " + e); 
		}
    	
    	return value;
    }
	
    
}
