package lifecycle_ga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class RegeneratePopulation implements Runnable {
	private static final String header = "RegeneratePopulation";
	private static final int min_sleep = 0;
	private static final int max_sleep = 3;  //----Tiempos entre 0 a 3 milesimas de seg.
	
	private SerializeIndividual serializer = null;
	private MessageListManager r = null;
	private ClassConfiguration cfg = null;	
	private ChromoOperations operations = null;
	private RegenerateMethods methods = null;
	private List<ClassIndividual> pop_evolution = null;
	
    private int num_restarts = 0;  //----Contadores del proceso de evaluaciones.	
	private final double threshold = 0.99;  //----Umbral de seleccion para la elite.
	private long totalTime_nanos = 0;  //----Acumulan los tiempos de ejecucion.
	private long totalTime_milli = 0;
	
	public int getTotalRestarts() { return num_restarts; }	
	public long getTotalTimeNanos() { return totalTime_nanos; }
	public long getTotalTimeMilli() { return totalTime_milli; }
	public double getTotalTime(boolean isNano) { 
		return (isNano ? (double)totalTime_nanos / 1000000000 : (double)totalTime_milli / 1000);
	}
		
	
	//----Constructor de la clase.	
	public RegeneratePopulation(ClassConfiguration config) {
		r = MessageListManager.getInstance();
		serializer = new SerializeIndividual();
		cfg = config;
		
		methods = new RegenerateMethods();
		operations = new ChromoOperations();
		pop_evolution = new ArrayList<ClassIndividual>();
	}
	
	
	public void run() {
		try {
			while (r.lpop("endof_experiment") == null) {
				Thread.sleep(random_int(min_sleep, max_sleep));  //----Espera X unidades de tiempo (milli seconds).
				performRegeneration();
			}	
		}
		catch (Exception e) {
			System.out.println(header + ".run().Exception: " + e);
		}		
	}
	

	//----Verificacion para regenerar poblacion.
	public boolean performRegeneration() {
		boolean execution_flag = false;
		
		try {
			if (r.lpop("endof_experiment") == null) {
				long nanoTime_start = System.nanoTime();    //----Auxiliares para calcular tiempos de ejecucion.
				long milliTime_start = System.currentTimeMillis();
				
	    		String individual_aux = r.spop("population_pool");
	    		
	    		if (individual_aux != null) {  //----Existe al menos un individuo en la poblacion.
	    			r.sadd("population_pool", individual_aux);  //----Regresamos individuo a la poblacion (serializado).
	    		}
	    		else {
	    			execution_flag = true;
	    			display_emptypop(cfg.max_population);
	    			
	    			System.out.println("---------------------------------------------------");
	    			System.out.println("---- R E G E N E R A T E - P O P U L A T I O N ----");
	    			System.out.println("---------------------------------------------------");
	    			
	    			List<ClassIndividual> pop_elite = get_elite_population("population_record", cfg.max_population);
	    			List<Double> delta_chromo = get_delta_trending(pop_elite);
	    			
	    			int max_trending = random_int(3, 9);  //----Calculamos un conjunto aleatorio de cromosomas para la tendencia.
	    			List<Double> trending_chromo = pop_elite.get(0).get_chromosome();  //----Asignamos el mejor cromosoma como valor inicial de la tendencia.
	    			
	    			for (int x = 0; x < max_trending; x++) {
	    				trending_chromo = sum_chromosomes(trending_chromo, delta_chromo);  //----Sumamos delta al mejor cromosoma para calcular el nuevo cromosoma.
	    				r.sadd("population_evaluate", serializer.toJson(new ClassIndividual(trending_chromo, "trend")));
	    			}
	    			
	    			List<ClassIndividual> new_population = regenerate_population(pop_elite, cfg.max_population, cfg.mutation_rate, cfg.dimensions, cfg.lower_bounds, cfg.upper_bounds, delta_chromo);
	    			int num_individuals = new_population.size();
	    			ClassIndividual individual = null;
	    			
	    			for (int x = 0; x < num_individuals; x++) {
	    				individual = new_population.get(x);
	    				individual.reset_age();
	    				
	    				if (individual.is_evaluated()) {
	    					r.sadd("population_pool", serializer.toJson(individual));
	    				}
	    				else {
	    					individual.set_operation("spawn");
	    					r.sadd("population_evaluate", serializer.toJson(individual));
	    				}
	    			}
	    			
    				num_restarts += 1;
	    		}
	    		
	            totalTime_nanos += System.nanoTime() - nanoTime_start;
	            totalTime_milli += System.currentTimeMillis() - milliTime_start;
			}
		}
		catch (Exception e) {
			System.out.println(header + ".performRegeneration().Exception: " + e);
		}
		
		return execution_flag;
	}
	
	
	//-----Imprime en consola el total de reinicios de la poblacion.
	public void display_totalRestarts() {
		try {
	        System.out.println("---------------------------------------------------");
			System.out.println("----------------- R E S T A R T S -----------------");
	        System.out.println("----- P O P U L A T I O N - R E S T A R T S : " + num_restarts);
	        System.out.println("---------------------------------------------------");
		}
		catch (Exception e) {
			System.out.println(header + ".display_totalRestarts().Exception: " + e);
		}
	}	
	
	
	//----Imprime en consola la finalizacion cuando la poblacion queda vacia.
	private void display_emptypop(int max_population) {		
		try {
			System.out.println("---------------------------------------------------");
			System.out.println("---------------- E M P T Y - P O P ----------------");
			System.out.println("Empty population of " + max_population + " individuals");
		}
		catch (Exception e) {
			System.out.println(header + ".display_emptypop().Exception: " + e);
		}
	}
	
	
	//----Recupera todos los individuos de una poblacion, ordenando por menor error (en indice '0') a mayor ('n').
	private List<ClassIndividual> get_elite_population(String population_queue, int max_population){
		List<ClassIndividual> pop_elite = new ArrayList<ClassIndividual>();
		
		try {
			if (r.ping().equals("PONG")) {
				String individual_aux = r.lpop(population_queue);
				
				while (individual_aux != null) {
					pop_evolution.add(serializer.fromJson(individual_aux, ClassIndividual.class));
					individual_aux = r.lpop(population_queue);
				}
				
		        Collections.sort(pop_evolution);  //----Ordenamos lista de individuos por fitness de menor a mayor (ascendente).
		        //----Para ordenar de mayor a menor agregar: Collections.reverse(pop_evolution);

		        if (pop_evolution.size() < max_population) {
		        	pop_elite = new ArrayList<ClassIndividual>(pop_evolution);  //----Clonamos y regresamos una nueva lista.	
		        }
		        else {
		        	for (int x = 0; x < max_population; x++) {
		        		pop_elite.add(pop_evolution.get(x));  //----Seleccionamos el maximo de elementos de 'pop_evolution'.
		        	}

					//----Clonamos la lista de elementos reducida en 'pop_evolution'.
		        	pop_evolution = new ArrayList<ClassIndividual>(pop_elite);
		        }
			}
		}
		catch (Exception e) {
			System.out.println(header + ".get_elite_population().Exception: " + e);
		}
		
		return pop_elite;
	}
	
	
	//----Calcula un nuevo cromosoma considerando las tendencias de los mejores individuos encontrados.
	private List<Double> get_delta_trending(List<ClassIndividual> pop_elite){
		List<Double> delta_chromo = new ArrayList<Double>();
		
		try {
			if (pop_elite != null) {
				List<ClassIndividual> pop_legends = select_pop_legends(pop_elite, threshold);  //----Recupera los mejores individuos de la poblacion.
				
				List<Double> top_chromosome = pop_legends.get(0).get_chromosome();  //----Recuperamos el mejor cromosoma de la poblacion.
				List<Double> chromosome_x = operations.generate_chromo_zero(top_chromosome.size());  //----Genera una lista de N elementos con valores '0.0'.
								
				int num_legends = pop_legends.size();
				for (int x = 0; x < num_legends; x++) {  //----Sumamos todos los chromosomas del grupo elite, para posteriormente calcular su promedio.
					operations.add_chromosomes(chromosome_x, pop_legends.get(x).get_chromosome());
				}
				
				List<Double> chromosome_avg = operations.get_average_chromo(chromosome_x, num_legends);
				List<Double> chromosome_neg = operations.get_negative_chromo(chromosome_avg);  //----Los volvemos negativos para realizar una resta contra el mejor individuo.
								
				delta_chromo = operations.sum_chromosomes(top_chromosome, chromosome_neg);  //----Calculamos la diferencia contra el mejor individuo.
			}
		}
		catch (Exception e) {
			System.out.println(header + ".get_delta_trending().Exception: " + e);
		}
		
		return delta_chromo;
	}
	
	
	//----Genera los nuevos cromosomas de la poblacion.
	private List<ClassIndividual> regenerate_population(List<ClassIndividual> pop_elite, int max_population, int mutation_rate, int dimensions, double lower_bounds, double upper_bounds, List<Double> delta_chromo){
		List<ClassIndividual> new_population = new ArrayList<ClassIndividual>();
		
		try {
			if (pop_elite != null) {
				boolean reset_population = false;  //----default: false
				
				if (reset_population) {
					new_population = methods.generate_random_pop(pop_elite.get(0), max_population, dimensions, lower_bounds, upper_bounds);
				}
				else {
					List<ClassIndividual> pop_legends = select_pop_legends(pop_elite, threshold);
					int regen_mode = operations.random_int(0, 100);
					
					if (regen_mode < 10) new_population = methods.generate_elite_mix(pop_legends, max_population);
					else if (regen_mode < 25) new_population = methods.generate_elite_motation(pop_legends, max_population, dimensions, lower_bounds, upper_bounds);
					else if (regen_mode < 35) new_population = methods.generate_top_motation(pop_elite.get(0), max_population, dimensions, lower_bounds, upper_bounds);
					else if (regen_mode < 60) new_population = methods.generate_top_elite_crosswap(pop_elite.get(0), pop_legends, max_population);
					else new_population = methods.generate_elite_projection(pop_elite.get(0), pop_legends, max_population, mutation_rate, delta_chromo);
					
					//----else if (regen_mode < 85) new_population = methods.generate_elite_projection(pop_elite.get(0), pop_legends, max_population, mutation_rate, delta_chromo);
					//----else new_population = methods.generate_stone_skipping(pop_elite.get(0), pop_legends, max_population);
				}
			}
		}
		catch(Exception e) {
			System.out.println(header + ".regenerate_population().Exception: " + e);
		}
		
		return new_population;
	}
	
		
	//----Calcula un valor entero aleatorio dentro del rango especificado.
	private int random_int(int range_one, int range_two) {
		int random_value = 0;
		
		try { 
			if (range_one == range_two) random_value = range_one;
			else if (range_one < range_two) random_value = ThreadLocalRandom.current().nextInt(range_one, range_two +1); 
			else random_value = ThreadLocalRandom.current().nextInt(range_two, range_one +1); 
		}
		catch (Exception e) { 
			System.out.println(header + ".random_int().Exception: " + e); 
		}
		
		return random_value;
	}
	

	//---------------------------------
	//---------------------------------

	
	//----Recupera los mejores individuos de la poblacion, donde el mejor esta en indice '0'.
	private List<ClassIndividual> select_pop_legends(List<ClassIndividual> pop_elite, double threshold_value){
		List<ClassIndividual> pop_legends = new ArrayList<ClassIndividual>();
		
		try {
			ClassIndividual top_individual = pop_elite.get(0);
			ClassIndividual next_individual = null;

			pop_legends.add(top_individual);  //----Agregamos al mejor individuo.
			
			double top_fitness = top_individual.get_fitness();
			int max_index = pop_elite.size();
			
			for (int x = 1; x < max_index; x++) {
				next_individual = pop_elite.get(x);
				
				if (next_individual.get_fitness() / top_fitness > threshold_value) {
					pop_legends.add(next_individual);
				}
				else {
					break;
				}
			}
		}
		catch (Exception e) {
			System.out.println(header + ".select_pop_legends().Exception: " + e);
		}
		
		return pop_legends;
	}
		
	
	//----Calcula un nuevo cromosoma con la sumatoria de dos cromosomas de igual dimension.
	private List<Double> sum_chromosomes(List<Double> chromosome_a, List<Double> chromosome_b) {
		return operations.sum_chromosomes(chromosome_a, chromosome_b);
	}
	
}
