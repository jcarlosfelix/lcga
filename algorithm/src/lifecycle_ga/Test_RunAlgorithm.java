package lifecycle_ga;

import java.text.DecimalFormat;
import java.util.concurrent.ThreadLocalRandom;



public class Test_RunAlgorithm {
	private static String header = "Test_RunAlgorithm";
	
	
	public static void main(String[] args) {
		try {			
			MessageListManager r = null;
			ClassStopwatch timer = null;

			GenerateIndividuals birth = null;			
			EvaluateIndividual gauge = null;
			GrowupIndividual aging = null;
			ReproduceCouple breed = null;
			RemoveIndividual death = null;
			RegeneratePopulation regen = null;

			
			r = MessageListManager.getInstance();
			r.flushDB();
			
			timer = new ClassStopwatch();
			timer.start();

			boolean setup_success = false;  //----Prepara la configuracion inicial del experimento y las listas de queue.
			
			try {			
				while (!r.ping().equals("PONG")){
					System.out.println(header + ".run().info: " + "Waiting for message list manager");
				}

				r.flushDB();  //----Redis flush cache/database and delete all keys from all databases.        
				//----System.out.println(header + ".run().info: " + "Setup: message list manager alive");
				
				//----Limpieza de queues: eliminamos los elementos residuales.
				boolean clear_queues = true;
				
				if (clear_queues) {
					r.clear_queue("population_pool");  //----S
					r.clear_queue("population_evaluate");  //----S
					r.clear_queue("population_record");  //----L
					r.clear_queue("endof_experiment");  //----L
				}
				
				setup_success = true;
			}
			catch (Exception e) {
				System.out.println(header + ".initiateExperimentSetup().Exception: " + e);
			}
			
			
			
			//-------------------
			if (!setup_success) {
				System.out.println("---------------------------------------------------");
				System.out.println("---------- UNSUCCESSFUL EXPERIMENT SETUP ----------");
				System.out.println("---------------------------------------------------");
			}
			else {												
				ClassConfiguration cfg = new ClassConfiguration();
				
				birth = new GenerateIndividuals(cfg);			
				gauge = new EvaluateIndividual(cfg);
				aging = new GrowupIndividual(cfg);
				breed = new ReproduceCouple(cfg);
				death = new RemoveIndividual(cfg);
				regen = new RegeneratePopulation(cfg);
			
				try {			
					birth.run();  //----Generacion inicial de la poblacion.
					gauge.multiple_runs();
					
					while (!gauge.endof_experiment) {
						switch (ThreadLocalRandom.current().nextInt(1, 3+1)) {
							case 1: aging.single_run(); break;
							case 2: breed.single_run(); break;
							case 3: death.single_run(); break;
						}
						
						gauge.multiple_runs();
						regen.performRegeneration();
					}
				}
				catch (Exception e) {
					System.out.println(header + ".run_sequential_A().Exception: " + e);
				}
				
				timer.stop();
				timer.display_totals();

				//----Acumulan los tiempos de ejecucion.
				double total_nanoseconds = birth.getTotalTime(true) + gauge.getTotalTime(true) + aging.getTotalTime(true) + breed.getTotalTime(true) + death.getTotalTime(true) + regen.getTotalTime(true);
				double total_milliseconds = birth.getTotalTime(false) + gauge.getTotalTime(false) + aging.getTotalTime(false) + breed.getTotalTime(false) + death.getTotalTime(false) + regen.getTotalTime(false);

				DecimalFormat df = new DecimalFormat("#.####");			

				System.out.println("---------------------------------------------------");
				System.out.println("------------ C L O C K E D - T I M E S ------------");   
				System.out.println("---------------------------------------------------");
				
				System.out.println("--- " + 1 + " : " + "BIRTH" + " : " + df.format(birth.getTotalTime(true)) + " / " + df.format(birth.getTotalTime(false)) + " sec ");
				System.out.println("--- " + 2 + " : " + "GAUGE" + " : " + df.format(gauge.getTotalTime(true)) + " / " + df.format(gauge.getTotalTime(false)) + " sec ");
				System.out.println("--- " + 3 + " : " + "AGING" + " : " + df.format(aging.getTotalTime(true)) + " / " + df.format(aging.getTotalTime(false)) + " sec ");
				System.out.println("--- " + 4 + " : " + "BREED" + " : " + df.format(breed.getTotalTime(true)) + " / " + df.format(breed.getTotalTime(false)) + " sec ");
				System.out.println("--- " + 5 + " : " + "DEATH" + " : " + df.format(death.getTotalTime(true)) + " / " + df.format(death.getTotalTime(false)) + " sec ");
				System.out.println("--- " + 6 + " : " + "REGEN" + " : " + df.format(regen.getTotalTime(true)) + " / " + df.format(regen.getTotalTime(false)) + " sec ");

				System.out.println("---------------------------------------------------");
				System.out.println("--- " + 7 + " : " + "TOTAL" + " : " + df.format(total_nanoseconds) + " / " + df.format(total_milliseconds) + " sec ");
				System.out.println("---------------------------------------------------");
				
				regen.display_totalRestarts();
				
				System.out.println("Error|" + gauge.getBestFoundError() + "|Evaluations|" + gauge.getTotalEvaluations() + "|Restarts|" + regen.getTotalRestarts() + "|Performance|" + timer.getPerformanceTime() + "|Clocked|" + total_nanoseconds);
				cfg.showConfiguration(false);
			}			
		}
		catch (Exception e) {
			System.out.println(header + ".run_single_experiment().Exception: " + e);
		}
	}		
	
}
