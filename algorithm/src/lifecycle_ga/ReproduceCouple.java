package lifecycle_ga;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;



public class ReproduceCouple implements Runnable {
	private static final String header = "ReproduceCouple";
	
	private SerializeIndividual serializer = null;
	private MessageListManager r = null;
	private ClassConfiguration cfg = null;
    
	private long totalTime_nanos = 0;  //----Acumulan los tiempos de ejecucion.
	private long totalTime_milli = 0;
	
	public long getTotalTimeNanos() { return totalTime_nanos; }
	public long getTotalTimeMilli() { return totalTime_milli; }
	public double getTotalTime(boolean isNano) { 
		return (isNano ? (double)totalTime_nanos / 1000000000 : (double)totalTime_milli / 1000);  
	}

	
	
	//----Constructor de la clase.	
	public ReproduceCouple(ClassConfiguration config) {
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
				

            	ClassIndividual individual = get_sample_champion("population_pool");

            	if (individual != null) {
	            	ClassIndividual partner = get_sample_champion("population_pool");
                    
	            	if (partner == null) {
	            		r.sadd("population_pool", serializer.toJson(individual));
	            	}
	            	else {
		        		execution_flag = true;  //----Tenemos a la pareja de individuos ganadores de su torneo.
	        			
						//----La pareja acepta al individuo.
						individual.inc_alpha_level();  //----Encontro pareja.        
						individual.inc_partners();
						partner.inc_partners();

						individual.inc_alpha_level();  //----Logro reproducirse.
						individual.inc_offspring(2);
						partner.inc_offspring(2);

						//----Se reproducen en pares (cruce de cromosomas de un solo punto).
						List<ClassIndividual> offspring = perform_reproduction(individual, partner);

						for (ClassIndividual newborn : offspring) {
							r.sadd("population_evaluate", serializer.toJson(newborn));
						}

	                    
	                    //----Regresamos padres (actualizados) a la poblacion.
	                    r.sadd("population_pool", serializer.toJson(individual));
	                    r.sadd("population_pool", serializer.toJson(partner));
	                    
	            		//----Mostramos las estadisticas del individuo.
	                    if (cfg.debug_monitor.equals("T")) individual.display_stats("breed");		            		
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
	
		
	//----Selecciona el mejor individuo de una muestra de la poblacion.
	public ClassIndividual get_sample_champion(String queue_name){
		ClassIndividual sample_champion = null;
		
		try {        	        	
        	List<String> pop_sample = r.spop(queue_name, cfg.sample_size);  //----Elegimos muestra de candidatos a participar en el torneo.            		
        	List<ClassIndividual> auxpop_list = purge_deserialize_sample(pop_sample);  //----Depuramos elementos 'None' y se deserializan los individuos en la lista.
    		
    		if (auxpop_list.size() >= 1) {
		        //----Collections.sort(auxpop_sample);  //----Ordenamos lista de individuos por fitness de menor a mayor (ascendente).
    			//----sample_champion = auxpop_list.remove(0);  //----Tomamos el individuo de menor error en la muestra.
		        
    			int best_index = get_best_index(auxpop_list);
    			sample_champion = auxpop_list.remove(best_index);  //----Tomamos el individuo de menor aptitud en la muestra.
    			    			
    			while (!auxpop_list.isEmpty()) {  //----Regresamos el resto de individuos a la poblacion.
    				r.sadd(queue_name, serializer.toJson(auxpop_list.remove(0)));
    			}
    		}
		}
		catch (Exception e) {
			System.out.println(header + ".get_sample_champion().Exception: " + e);
		}
		
		return sample_champion;
	}
	
	
	//----Depuramos elementos 'None' y se deserializan los individuos en la lista.
	private List<ClassIndividual> purge_deserialize_sample(List<String> pop_sample){
		List<ClassIndividual> purged_list = new ArrayList<ClassIndividual>();
		
		try {
        	for (String individual_aux : pop_sample) {
            	if (individual_aux != null) {
            		purged_list.add(serializer.fromJson(individual_aux, ClassIndividual.class));
            	}
        	}
		}
		catch (Exception e) {
			System.out.println(header + ".purge_deserialize_sample().Exception: " + e);
		}
		
		return purged_list;
	}
	
	
	//----Identificamos la posicion del individuo de menor aptitud en la muestra.
	private int get_best_index(List<ClassIndividual> purged_list) {
		int best_index = -1;
		
		try {
			ClassIndividual top_individual = null;
			ClassIndividual aux_individual = null;
			
			for (int x = 0; x < purged_list.size(); x++) {
				aux_individual = purged_list.get(x);
				
				if ((top_individual == null) || (aux_individual.get_fitness() < top_individual.get_fitness())) {
					top_individual = aux_individual;
					best_index = x;
				}
			}
		}
		catch (Exception e) {
			System.out.println(header + ".get_best_index().Exception: " + e);
		}
		
		return best_index;
	}
	
	
	//----Operador de reproduccion para una pareja de individuos.
	private List<ClassIndividual> perform_reproduction(ClassIndividual parent_1, ClassIndividual parent_2){
		List<ClassIndividual> offspring_list = null;
		
		try {
			offspring_list = continuous_range_xover(parent_1, parent_2);  //----Alternative: one-point crossover.
			//----offspring_list = onepoint_xover(parent_1, parent_2);
		}
		catch (Exception e) {
			System.out.println(header + ".perform_reproduction().Exception: " + e);
		}
		
		return offspring_list;
	}	
	
	
	
	//----Operador de reproduccion para una pareja de individuos, usando la tecnica del rango continuo.
	private List<ClassIndividual> continuous_range_xover(ClassIndividual parent_1, ClassIndividual parent_2){
		List<ClassIndividual> offspring_list = new ArrayList<ClassIndividual>();
		
		try {
			List<Double> p1_chromosome = parent_1.get_chromosome();
			List<Double> p2_chromosome = parent_2.get_chromosome();
			int dimensions = p1_chromosome.size();
			
			List<Double> chromosome_1 = new ArrayList<Double>();
			List<Double> chromosome_2 = new ArrayList<Double>();
			
			for (int x = 0; x < dimensions; x++) {
				chromosome_1.add(random_double(p1_chromosome.get(x), p2_chromosome.get(x)));
				chromosome_2.add(random_double(p1_chromosome.get(x), p2_chromosome.get(x)));
			}
			
			offspring_list.add(new ClassIndividual(chromosome_1, "birth"));
			offspring_list.add(new ClassIndividual(chromosome_2, "birth"));
		}
		catch (Exception e) {
			System.out.println(header + ".continuous_range_xover().Exception: " + e);
		}
		
		return offspring_list;
	}
	
	
	//----Operador de reproduccion para una pareja de individuos, usando el tradicional cruce de un solo punto.
	private List<ClassIndividual> onepoint_xover(ClassIndividual parent_1, ClassIndividual parent_2){
		List<ClassIndividual> offspring_list = new ArrayList<ClassIndividual>();
		
		try {
			List<Double> p1_chromosome = parent_1.get_chromosome();
			List<Double> p2_chromosome = parent_2.get_chromosome();
			int dimensions = p1_chromosome.size();
			
			List<Double> chromosome_1 = new ArrayList<Double>();
			List<Double> chromosome_2 = new ArrayList<Double>();

			int index_point = random_int(0, dimensions -1);
			
			for (int x = 0; x < index_point; x++) {
				chromosome_1.add(p1_chromosome.get(x));
				chromosome_2.add(p2_chromosome.get(x));
			}

			for (int x = index_point; x < dimensions; x++) {
				chromosome_1.add(p2_chromosome.get(x));
				chromosome_2.add(p1_chromosome.get(x));
			}
			
			offspring_list.add(new ClassIndividual(chromosome_1, "birth"));
			offspring_list.add(new ClassIndividual(chromosome_2, "birth"));
		}
		catch (Exception e) {
			System.out.println(header + ".onepoint_xover().Exception: " + e);
		}
		
		return offspring_list;
	}


}
