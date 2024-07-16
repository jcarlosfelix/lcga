package lifecycle_ga;


public class EvaluateIndividual implements Runnable {
	private static final String header = "EvaluateIndividual";
	private static final int total_processes = 10;  //----Contador para difusion de finalizacion.
	
	private SerializeIndividual serializer = null;
	private MessageListManager r = null;
	private ClassConfiguration cfg = null;
	private BenchFunction bench_fun = null;

    private double target_fitness = 0;
    private int num_evaluations = 0;  //----Contadores del proceso de evaluaciones.
    private int num_stagnation = 0;
    private ClassIndividual individual = null;
    private ClassIndividual top_individual = null;
    public boolean endof_experiment = false;
	
    private String best_error = "";
	private long totalTime_nanos = 0;  //----Acumulan los tiempos de ejecucion.
	private long totalTime_milli = 0;
	
	public int getTotalEvaluations() { return num_evaluations; }
	public String getBestFoundError() { return best_error; }
	public long getTotalTimeNanos() { return totalTime_nanos; }
	public long getTotalTimeMilli() { return totalTime_milli; }
	public double getTotalTime(boolean isNano) { 
		return (isNano ? (double)totalTime_nanos / 1000000000 : (double)totalTime_milli / 1000);  
	}
	
    
	//----Constructores de la clase.	
	public EvaluateIndividual(ClassConfiguration config) {
		r = MessageListManager.getInstance();
		serializer = new SerializeIndividual();
		cfg = config;
		
		bench_fun = new BenchFunction(cfg.input_data, cfg.dimensions, cfg.lower_bounds, cfg.upper_bounds, cfg.function_num);
		target_fitness = cfg.target_fitness;  //----0.0000000000001;
	}
	
	
	public void run() {
		while (r.lpop("endof_experiment") == null) single_run();
	}
	
	public void multiple_runs() {
		while (single_run());
	}
		
	private boolean single_run() {
		boolean execution_flag = false;
		
		try {
			if (r.lpop("endof_experiment") == null) {
				long nanoTime_start = System.nanoTime();    //----Auxiliares para calcular tiempos de ejecucion.
				long milliTime_start = System.currentTimeMillis();
				        	
				String individual_aux = r.spop("population_evaluate");
	        	
	        	if (individual_aux != null) {  //----Encontramos un individuo a evaluar.
	        		execution_flag = true;
	        		individual = serializer.fromJson(individual_aux, ClassIndividual.class);
	        		
	        		String operation = individual.get_operation();
	                boolean termination = false;
	        		
	                //----La evaluacion del individuo es la funcion principal del proceso.
	    			if (!individual.is_evaluated()) {  	//----Actualiza fitness con el calculo de evaluacion del cromosoma.	   
	    				double new_fitness = bench_fun.compute(individual.get_chromo_double());
	    				
	    				individual.set_fitness( new_fitness );
	    				individual.set_error( Math.abs(target_fitness - new_fitness) );
	    				individual.set_evaluated( true );
		                
	    				num_evaluations += 1;
	    			}
	                
	                r.rpush("population_record", serializer.toJson(individual));
	                
	                //----Verificacion de fitness contra el mejor, para control de estancamiento.
	                if (top_individual == null) top_individual = individual;
	                else if (individual.get_fitness() < top_individual.get_fitness()) {
	                	top_individual = individual;
	                	num_stagnation = 0;
	                }
	                else num_stagnation += 1;
	                
	                //----Mostramos las estadisticas del individuo evaluado.
	                if (cfg.debug_monitor.equals("X")) individual.display_stats(operation + " - " + num_evaluations);
	                
	                //----Verificion de las condiciones de terminacion.
	                if (individual.get_fitness() <= target_fitness) { termination = display_success(individual, operation, num_evaluations); }
	                else if (num_evaluations >= cfg.max_evaluations) { termination = display_evaluations(top_individual, cfg.max_evaluations, num_evaluations); }
	                else if (num_stagnation >= cfg.max_stagnation) { termination = display_stagnation(top_individual, cfg.max_stagnation, num_stagnation); }
	                else { r.sadd("population_pool", serializer.toJson(individual)); }

	                //----Cuando finaliza se interrumpen todos los procesos.
	            	if (termination) {  
	            		endof_experiment = true;
	            		for (int x = 0; x < total_processes; x++) r.rpush("endof_experiment", "STOP");
	            	}
	        	}
	            
	            totalTime_nanos += System.nanoTime() - nanoTime_start;
	            totalTime_milli += System.currentTimeMillis() - milliTime_start;	
			}			
		}
		catch (Exception e) {
			System.out.println(header + ".single_run().Exception: " + e);
		}
		finally {
			best_error = top_individual.show_error();
		}		
		
		return execution_flag;
	}
	
	
	//-----Imprime en consola el total de evaluaciones.
	private void show_num_evaluations() {
		try {
	        System.out.println("---------------------------------------------------");
	        System.out.println("------ T O T A L - E V A L U A T I O N S : " + num_evaluations);			
		}
		catch (Exception e) {
			System.out.println(header + ".show_num_evaluations().Exception: " + e);
		}
	}
	
	
	//----Imprime en consola la finalizacion con Exito al encontrar la solucion.
	private boolean display_success(ClassIndividual individual, String operation, int num_evaluations) {
		boolean termination = true;
		
		try {
		    System.out.println("---------------------------------------------------");
		    System.out.println("----------- S O L U T I O N - F O U N D -----------");
		    individual.display_stats(operation + " - " + num_evaluations, 0, true);  //----Mostramos las estadisticas del individuo.
		    show_num_evaluations();
		}
		catch (Exception e) {
			System.out.println(header + ".display_success().Exception: " + e);
		}
		finally {
			top_individual = individual;
		}		
		
		return termination;
	}
	
	
	//----Imprime en consola la finalizacion por alcanzar el maximo de generaciones.
	private boolean display_evaluations(ClassIndividual individual, int max_evaluations, int num_evaluations) {
		boolean termination = true;
		
		try {
			System.out.println("---------------------------------------------------");
			System.out.println("-------------- E V A L U A T I O N S --------------");
		    individual.display_stats("Top individual ", 0, true);  //----Mostramos las estadisticas del individuo.
			System.out.println("Maximum of " + max_evaluations + " evaluations reached - " + num_evaluations + " evaluations");
			show_num_evaluations();
		}
		catch (Exception e) {
			System.out.println(header + ".display_evaluations().Exception: " + e);
		}
		
		return termination;
	}
	
	
	//----Imprime en consola la finalizacion por estancamiento.
	private boolean display_stagnation(ClassIndividual individual, int max_stagnation, int num_stagnation) {
		boolean termination = true;
		
		try {
			System.out.println("---------------------------------------------------");
			System.out.println("--------------- S T A G N A T I O N ---------------");
		    individual.display_stats("Top individual ", 0, true);  //----Mostramos las estadisticas del individuo.
			System.out.println("Maximum of " + max_stagnation + " evaluations without improvement - " + num_stagnation + " evaluations");
			show_num_evaluations();
		}
		catch (Exception e) {
			System.out.println(header + ".display_stagnation().Exception: " + e);
		}
		
		return termination;
	}
	
	
}
