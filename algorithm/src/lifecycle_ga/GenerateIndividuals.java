package lifecycle_ga;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class GenerateIndividuals implements Runnable {
	private static final String header = "GenerateIndividuals";
		
	private SerializeIndividual serializer = null;
	private MessageListManager r = null;
	private ClassConfiguration cfg = null;
	private int population = 0;

	private long totalTime_nanos = 0;  //----Acumulan los tiempos de ejecucion.
	private long totalTime_milli = 0;
	
	public long getTotalTimeNanos() { return totalTime_nanos; }
	public long getTotalTimeMilli() { return totalTime_milli; }
	public double getTotalTime(boolean isNano) {
		return (isNano ? (double)totalTime_nanos / 1000000000 : (double)totalTime_milli / 1000);
	}
	
	
	//----Constructor de la clase.	
	public GenerateIndividuals(ClassConfiguration config) {
		r = MessageListManager.getInstance();
		serializer = new SerializeIndividual();
		cfg = config;
	}
	

	public void run() {
		if (r.lpop("endof_experiment") == null) single_run();
	}
	
	public boolean single_run() {
		boolean execution_flag = false;
		
		try {
			if (r.lpop("endof_experiment") == null) {
				long nanoTime_start = System.nanoTime();    //----Auxiliares para calcular tiempos de ejecucion.
				long milliTime_start = System.currentTimeMillis();

				List<Double> chromosome = null;
							
				System.out.println("---------------------------------------------------");
				System.out.println("---- P O P U L A T I O N - G E N E R A T I O N ----");
				System.out.println("---------------------------------------------------");
				
				population = 0; //----Generacion de la poblacion inicial.
				execution_flag = true;
				
				while (population < cfg.max_population) {
					chromosome = generate_chromosome(cfg.dimensions, cfg.lower_bounds, cfg.upper_bounds);
					r.sadd("population_evaluate", serializer.toJson(new ClassIndividual(chromosome, "begin")));
					population += 1;
				}
						        
				System.out.println("---------------------------------------------------");
				
	            totalTime_nanos += System.nanoTime() - nanoTime_start;
	            totalTime_milli += System.currentTimeMillis() - milliTime_start;	
			}			
		}
		catch (Exception e) {
			System.out.println(header + ".single_run().Exception: " + e);
		}
		
		return execution_flag;
	}
	
	
	//----Calcula un valor double aleatorio dentro del rango especificado.
	private double random_double(double range_one, double range_two) {
		double random_value = 0.0;
		
		try {
			if (range_one == range_two) random_value = range_one;
			else if (range_one < range_two) random_value = ThreadLocalRandom.current().nextDouble(range_one, range_two);
			else random_value = ThreadLocalRandom.current().nextDouble(range_two, range_one);
		}
		catch (Exception e) { 
			System.out.println(header + ".random_double().Exception: " + e); 
		}
		
		return random_value;
	}
	
	
	//----Genera un cromosoma de N dimensiones, acorde a la matrix de limites.
	private List<Double> generate_chromosome(int dimensions, double lower_bounds, double upper_bounds) {
		List<Double> chromosome = new ArrayList<Double>();
		
		try {
			for (int x = 0; x < dimensions; x++)
				chromosome.add( random_double(lower_bounds, upper_bounds) );
		}
		catch (Exception e) {
			System.out.println(header + ".generate_chromosome().Exception: " + e);
		}
		
		return chromosome;
	}
	
	
}
