package lifecycle_ga;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class CalculateFitness {
	private final String header = "EvaluateFitness";
	
	
	//----Selecciona la funcion segun el nombre, para evaluar la aptitud con el cromosoma.
	public double evaluation(String function_name, List<Double> chromosome) {
		double fitness = 0.0;
		
		try {
			switch (function_name) {
				case "SPHERE": fitness = sphere(chromosome); break;
				case "ROSENBROCK": fitness = rosenbrock(chromosome); break;
				case "BOHACHEVSKY": fitness = bohachevsky(chromosome); break;
				case "RASTRIGIN": fitness = rastrigin(chromosome); break;
				case "ACKLEY": fitness = ackley(chromosome); break;
				case "GRIEWANK": fitness = griewank(chromosome); break;
				default: fitness = original(chromosome); break;
			}
		}
		catch (Exception e) {
			System.out.println(header + ".evaluation().Exception: " + e);
		}
		
		return fitness;
	}
	
	
	//----Definimos la funcion 'Sphere' para evaluar el calculo de aptitud (version DEAP).
	private double sphere(List<Double> chromosome) {
		double fitness = 0.0;
		
		try {
			for (double gene : chromosome) {
				fitness += gene * gene;
			}
		}
		catch (Exception e) {
			System.out.println(header + ".sphere().Exception: " + e);
		}
		
		return fitness;
	}
	

	//----Definimos la funcion 'Rosenbrock' para evaluar el calculo de aptitud (version DEAP).
	private double rosenbrock(List<Double> chromosome) {
		double fitness = 0.0;
		
		try {
			int max_dimension = chromosome.size() -1;
			double x = 0;  //----Current gene.
			double y = 0;  //----Next gene.
			double arg1 = 0;
			double arg2 = 0;
						
			for (int i = 0; i < max_dimension; i++) {
				x = chromosome.get(i);
				y = chromosome.get(i +1);
				
				//--------------------------
				//----Version: DEAP (Python)
				//arg1 = y - (x * x);
				//arg2 = 1 - x;

				//--------------------------
				//----Version: jMetal
				arg1 = (x * x) - y;  
				arg2 = x - 1;
								
				fitness += 100 * (arg1 * arg1) + (arg2 * arg2);
			}
		}
		catch (Exception e) {
			System.out.println(header + ".rosenbrock().Exception: " + e);
		}
		
		return fitness;
	}


	//----Definimos la funcion 'Bohachevsky' para evaluar el calculo de aptitud (version DEAP).
	private double bohachevsky(List<Double> chromosome) {
		double fitness = 0.0;
		
		try {
			/**************************************
			 * PRECAUCION: Falta por implementar *
			 **************************************/
			
			fitness = ThreadLocalRandom.current().nextDouble(0.0, 100.0);
		}
		catch (Exception e) {
			System.out.println(header + ".bohachevsky().Exception: " + e);
		}
		
		return fitness;
	}

	
	//----Definimos la funcion 'Rastrigin' para evaluar el calculo de aptitud (version DEAP).
	private double rastrigin(List<Double> chromosome) {
		double fitness = 0.0;
		
		try {
			int N = chromosome.size();
			double chromo_sum = 0;
			
			for (double gene : chromosome) {
				chromo_sum += gene * gene - 10 * Math.cos(2 * Math.PI * gene);				
			}
			
			fitness = 10 * N + chromo_sum;
		}
		catch (Exception e) {
			System.out.println(header + ".rastrigin().Exception: " + e);
		}
		
		return fitness;
	}

	
	//----Definimos la funcion 'Ackley' para evaluar el calculo de aptitud (version DEAP).
	private double ackley(List<Double> chromosome) {
		double fitness = 0.0;
		
		try {
			int N = chromosome.size();
			double chromo_sum1 = 0;
			double chromo_sum2 = 0;
			
			for (double x : chromosome) {
				chromo_sum1 += Math.pow(x, 2);
				chromo_sum2 += Math.cos(2 * Math.PI * x);
			}
						
			fitness = 20 - 20 * Math.exp(-0.2 * Math.sqrt(1.0/N * chromo_sum1)) + Math.E - Math.exp(1.0/N * chromo_sum2);
		}
		catch (Exception e) {
			System.out.println(header + ".ackley().Exception: " + e);
		}
		
		return fitness;
	}
		
	
	//----Definimos la funcion 'Griewank' para evaluar el calculo de aptitud (version DEAP).
	private double griewank(List<Double> chromosome) {
		double fitness = 0.0;
		
		try {
			int N = chromosome.size();
			double gene = 0;
			double argument1 = 0;
			double argument2 = 1;
			
			for (int i = 0; i < N; i++) {
				gene = chromosome.get(i);
				
				argument1 += Math.pow(gene, 2);
				argument2 *= Math.cos(gene / Math.sqrt(i +1));
			}
			
			fitness = 1 + (argument1/4000.0) - argument2;
		}
		catch (Exception e) {
			System.out.println(header + ".griewank().Exception: " + e);
		}
		
		return fitness;
	}
	
	
	//----Definimos la funcion 'original' para evaluar el calculo de aptitud.
	private double original(List<Double> chromosome) {
		double fitness = 0.0;
		
		try {
			double x = chromosome.get(0);
			fitness = -1 * Math.pow(x, 2) + 3;
		}
		catch (Exception e) {
			System.out.println(header + ".original().Exception: " + e);
		}
		
		return fitness;
	}
	
	
}
