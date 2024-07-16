package lifecycle_ga;

import java.util.ArrayList;
import java.util.List;


public class RemoveIndividual implements Runnable {
	private static final String header = "RemoveIndividual";
	
	private SerializeIndividual serializer = null;
	private MessageListManager r = null;
	private ClassConfiguration cfg = null;
	
	private int death_i = 0;
	private ClassIndividual champion = null;
	
	private long totalTime_nanos = 0;  //----Acumulan los tiempos de ejecucion.
	private long totalTime_milli = 0;
	
	public long getTotalTimeNanos() { return totalTime_nanos; }
	public long getTotalTimeMilli() { return totalTime_milli; }
	public double getTotalTime(boolean isNano) { 
		return (isNano ? (double)totalTime_nanos / 1000000000 : (double)totalTime_milli / 1000);  
	}

	
	//----Constructor de la clase.	
	public RemoveIndividual(ClassConfiguration config) {
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
	            		death_i += 1;
	            		individual = serializer.fromJson(individual_aux, ClassIndividual.class);
	            		
	            		boolean survives = survival_threshold(individual, cfg.max_population, cfg.max_evaluations, cfg.max_age, cfg.survive_min, cfg.survive_max, cfg.pressure);
	            		
	            		if (survives) {
	            			individual.inc_alpha_level();
	            			r.sadd("population_pool", serializer.toJson(individual));
	            		}
	            		else {
	            			execution_flag = true;
	            			individual.set_status(-1);  //----Estatus del individuo: [-1:'inert', 0:'alive', 1:'exile', 2:'fight']
	            			individual.dec_alpha_level();
	            		}
	            		
	            		//----Mostramos las estadisticas del individuo.
	                    if (cfg.debug_monitor.equals("T")) individual.display_stats("dying");
	            	}
	        	}
	            
	            //-------------------
	            totalTime_nanos += System.nanoTime() - nanoTime_start;
	            totalTime_milli += System.currentTimeMillis() - milliTime_start;	
			}			
		}
		catch (Exception e) {
			System.out.println(header + ".single_run().Exception: " + e);
		}
		
		return execution_flag;
	}
	
	
	//----Verifica si el individual sobrevive o muere, acorde al incremento progresivo 'pressure'.
	private boolean survival_threshold(ClassIndividual individual, int max_population, int eval_max, int max_age, int survive_min, int survive_max, double pressure) {
		boolean survives = true;
		
		try {
			if (champion == null) {
				champion = individual;
			}
			else {
				if (individual.get_age() >= max_age) {  //----El individuo muere cuando supera la edad maxima.
					survives = false;
				}				
				else if (individual.get_fitness() <= champion.get_fitness()) {  //----Protegemos al campeon y a los individuos de menor o igual aptitud.
					champion = individual;
				}
				else if (death_i > max_population) {  //----Despues de tantas iteraciones como max_population se activa la eliminacion.
					double fitness_rate = get_fitness_rate(individual.get_fitness(), champion.get_fitness());  //----Comparamos la aptitud contra el campeon.
					survives = fitness_rate >= get_dynamic_survival(death_i, eval_max, survive_min, survive_max, pressure);
				}
			}
		}
		catch (Exception e) {
			System.out.println(header + ".survival_threshold().Exception: " + e);
		}
		
		return survives;
	}
	
	
	//----Calcula la razon de aptitud (relativa) en comparacion con el mejor individuo encontrado.
	private double get_fitness_rate(double individual_fitness, double champion_fitness) {
		double fitness_rate = 0.0;
		
		try {
			if (individual_fitness <= champion_fitness) {
				fitness_rate = 100;
			}
			else if (individual_fitness > 0) {
				fitness_rate = (champion_fitness / individual_fitness) * 100;
			}
			else if (individual_fitness <= 0) {
		        double adjusted_individual = 1 + Math.abs(individual_fitness) + individual_fitness;
		        double adjusted_champion = 1 + Math.abs(individual_fitness) + champion_fitness;
		        
		        fitness_rate = (adjusted_champion / adjusted_individual) * 100;
			}
		}
		catch (Exception e) {
			System.out.println(header + ".get_fitness_rate().Exception: " + e);
		}
		
		return fitness_rate;
	}
	
	
	//----Se debe incrementar de manera progresiva (dinamica) el nivel de exigencia para seguir siendo elite.
	private double get_dynamic_survival(int death_i, int eval_max, int survive_min, int survive_max, double pressure) {
		double survival = 0.0;
		
		try {
		    survival = survive_min + death_i * ((survive_max - survive_min) / eval_max) * pressure;
		}
		catch (Exception e) {
			System.out.println(header + ".get_dynamic_survival().Exception: " + e);
		}
		
		return survival;
	}
	
	
}
