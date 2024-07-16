package lifecycle_ga;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class GrowupIndividual implements Runnable {
	private static final String header = "GrowupIndividual";
	
	private SerializeIndividual serializer = null;
	private MessageListManager r = null;
	private ClassConfiguration cfg = null;

	private ClassIndividual top_individual = null;  //----Determina el cromosoma usado como factor de mutacion.  
	
	private long totalTime_nanos = 0;  //----Acumulan los tiempos de ejecucion.
	private long totalTime_milli = 0;
	
	public long getTotalTimeNanos() { return totalTime_nanos; }
	public long getTotalTimeMilli() { return totalTime_milli; }
	public double getTotalTime(boolean isNano) { 
		return (isNano ? (double)totalTime_nanos / 1000000000 : (double)totalTime_milli / 1000);
	}

	
	//----Constructor de la clase.	
	public GrowupIndividual(ClassConfiguration config) {
		r = MessageListManager.getInstance();
		serializer = new SerializeIndividual();
		cfg = config;
	}

	
	public void run() {
        while (r.lpop("endof_experiment") == null) single_run();
	}
	
	public boolean single_run() {
		boolean execution_flag = false;
		
		try {
			if (r.lpop("endof_experiment") == null) {
				long nanoTime_start = System.nanoTime();    //----Auxiliares para calcular tiempos de ejecucion.
				long milliTime_start = System.currentTimeMillis();
				
	        	List<String> pop_sample = r.spop("population_pool", cfg.sample_size);  //----Recupera un numero de elementos en un conjunto.
	        	ClassIndividual individual = null;
	        	
	        	
	        	for (String individual_aux : pop_sample) {
	            	if (individual_aux != null) {
	            		individual = serializer.fromJson(individual_aux, ClassIndividual.class);
	            		individual.inc_age();  //----El tiempo avanza de manera continua para todos los individuos.
	            		
	        			//-------------------
	            		//----Consideramos como el tope maximo de mutacion a 'cfg.mutation_rate'.
	            		
	            		int mutation_rate = get_mutation_rate(individual.get_age(), cfg.max_age, cfg.mutation_rate);  //----Es dinamica, en funcion de la edad del individuo.	            		
	            		List<Double> mutation_factor = generate_chromosome(cfg.dimensions, cfg.lower_bounds, cfg.upper_bounds);  //----Generamos un cromosoma como factor de mutacion.
	            		//----List<Double> mutation_factor = validate_chromosome(individual);  //----Recuperamos el cromosoma del mejor individuo.
	            		
	            		if (individual.perform_mutation(mutation_rate, mutation_factor)) {
	            			execution_flag = true;
	            			individual.set_operation("aging");  //----Conforme maduran, sus caracteristicas y habilidades sufren cambios.
	            			r.sadd("population_evaluate", serializer.toJson(individual));
	            		}
	            		else {
	            			r.sadd("population_pool", serializer.toJson(individual));
	            		}
	            				            		       		
	            		//----Mostramos las estadisticas del individuo.
	                    if (cfg.debug_monitor.equals("T")) individual.display_stats("aging");
	            	}
	        	}
	        	
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
	

	//----Genera un cromosoma de N dimensiones, acorde a la matrix de limites.
	private List<Double> validate_chromosome(ClassIndividual individual) {		
		try {
			if (top_individual == null || individual.get_fitness() < top_individual.get_fitness()) {
				top_individual = individual;
			}
		}
		catch (Exception e) {
			System.out.println(header + ".validate_chromosome().Exception: " + e);
		}
		
		return top_individual.get_chromosome();
	}
	
	
	//----Se calcula la razon de mutacion de manera dinamica, en funcion de la edad especifica para cada individuo.
	private int get_mutation_rate(int age, int max_age, int max_mutation) {
		int mutation_rate = 0;
		
		try {
		    //----Elegimos la funcion coseno porque muestra una curva similar al crecimiento de un individual en relacion a su edad.

		    double scale_age = age * Math.PI / max_age;  //----La edad se lleva a escala de PI (3.1416 equivale a la edad maxima de anios).
		    double mutation = Math.cos(scale_age) * 100;  //----Evaluamos la 'scale_age' en la funcion coseno. La volvemos porcentaje.
		    mutation_rate = (int)Math.round(Math.abs(mutation));  //----Se calcula el valor absoluto y se redondea en enteros.
		    
		    //----Cuando la razon de mutacion es mayor que el maximo se ajusta al tope (max_mutation).
		    if (mutation_rate > max_mutation) mutation_rate = max_mutation;
		}
		catch (Exception e) {
			System.out.println(header + ".get_mutation_rate().Exception: " + e);
		}
		
		return mutation_rate;
	}
	
   
}
