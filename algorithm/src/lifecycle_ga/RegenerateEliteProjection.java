package lifecycle_ga;

import java.util.ArrayList;
import java.util.List;


public class RegenerateEliteProjection {
	private static final String header = "RegenerateEliteProjection";
	private ChromoOperations operations = null;
	
	
	//----Constructor de la clase.	
	public RegenerateEliteProjection() {
		operations = new ChromoOperations();
	}
	
	
	//----Genera nuevos cromosomas, basados en el mejor individuo de la poblacion.
	public List<ClassIndividual> generate_elite_projection(ClassIndividual top_individual, List<ClassIndividual> pop_legends, int max_population, int mutation_rate, List<Double> delta_chromo) {
		List<ClassIndividual> new_population = new ArrayList<ClassIndividual>();
		
		try {
			new_population.add(top_individual);  //----Agregamos el mejor individuo.
			
			List<Double> trending_chromo = operations.sum_chromosomes(top_individual.get_chromosome(), delta_chromo);
			
			List<ClassIndividual> top_population = get_trending_solutions(top_individual, trending_chromo);
			new_population.addAll(top_population);
			
			int elite_max = (int)Math.round(max_population * operations.random_double(0.75, 0.90)) - top_population.size();  //----[0.75, 0.85, 0.90]
			List<ClassIndividual> elite_population = get_projection_solutions(pop_legends, elite_max, mutation_rate);
			new_population.addAll(elite_population);
			
			int stone_max = max_population - new_population.size();
			List<ClassIndividual> stone_population = get_solutions_stone_skipping(top_individual, new_population, stone_max); 
			new_population.addAll(stone_population);
		}
		catch(Exception e) {
			System.out.println(header + ".generate_elite_projection().Exception: " + e);
		}
		
		return new_population;
	}
	
	
	//----Genera nuevos cromosomas, basados en el mejor individuo de la poblacion.
	private List<ClassIndividual> get_trending_solutions(ClassIndividual top_individual, List<Double> trending_chromo) { 
		List<ClassIndividual> new_population = new ArrayList<ClassIndividual>();
		
		try {
			List<Double> top_chromosome = top_individual.get_chromosome();
			new_population.addAll(swap_top_trending(top_chromosome, trending_chromo));
			
			List<Double> chromosome = new ArrayList<Double>(top_chromosome);
			
			int x = 0;
			int fibonacci_max = operations.random_int(4, 8);
			
			while (x < fibonacci_max) {
    	        chromosome = get_fibonacci_chromosome(chromosome);
    	        new_population.add(new ClassIndividual(chromosome));  //----Agregamos el individuo de cromosoma 'Fibonacci' a la lista.
				x += 1;
			}
		}
		catch(Exception e) {
			System.out.println(header + ".get_trending_solutions().Exception: " + e);
		}

		return new_population;
	}
	
	
	//----Genera un conjunto de nuevos cromosomas direccionados al mejor cromosoma, basados en la poblacion elite.
	private List<ClassIndividual> get_projection_solutions(List<ClassIndividual> pop_legends, int max_population, int mutation_rate){
		List<ClassIndividual> new_population = new ArrayList<ClassIndividual>();
		
		try {
			ClassIndividual top_individual = pop_legends.get(0);
			List<Double> top_chromosome = top_individual.get_chromosome();  //----Recuperamos el mejor cromosoma de la poblacion.
			new_population.add(top_individual);  //----Agregamos el mejor individuo a la lista.
			
			int max_index = pop_legends.size() -1;
			int x = 0;
			int fibonacci_max = 0;
			
			ClassIndividual individual_e = null;
			List<Double> chromosome_e = null;
			List<Double> chromosome_x = null;
			
			while (new_population.size() < max_population) {
				individual_e = pop_legends.get(operations.random_int(0, max_index));
				chromosome_e = individual_e.get_chromosome();
				new_population.add(individual_e);  //----Agregamos el individuo 'elite' a la lista.
				
				x = 0;
				fibonacci_max = (int)Math.round(max_population * get_divine_rate(mutation_rate));
				
				while (x < fibonacci_max) {  //----Recomendable: 5 a 10 individuos.
					chromosome_e = get_fibonacci_chromosome(chromosome_e);
					chromosome_x = operations.sum_chromosomes(chromosome_e, top_chromosome);  //----Ajustamos el cromosoma fibonacci al acercarlo al cromosoma Top.
					new_population.add(new ClassIndividual(chromosome_x));  //----Agregamos el individuo de cromosoma 'Fibonacci' a la lista.
					
					if (new_population.size() >= max_population) break;
					x += 1;
				}
			}
		}
		catch(Exception e) {
			System.out.println(header + ".get_projection_solutions().Exception: " + e);
		}
		
		return new_population;
	}
	
	
	//----Genera los nuevos cromosomas, basados en los mejores obtenidos de la poblacion.
	private List<ClassIndividual> get_solutions_stone_skipping(ClassIndividual top_individual, List<ClassIndividual> solutions_set, int max_population) {
		List<ClassIndividual> new_population = new ArrayList<ClassIndividual>();
		
		try {
			List<Double> top_chromosome = top_individual.get_chromosome();  //----Recuperamos el mejor cromosoma de la poblacion.
			int max_index = solutions_set.size() -1;
			
			ClassIndividual solution_random = null;
			List<Double> solution_chromo = null;
			List<Double> midpoint_chromo = null;
			
			List<Double> chromosome_lower = null;
			List<Double> chromosome_upper = null;
			List<Double> chromosome_middle = null;
			List<Double> chromosome_overlap = null;
			
			while (new_population.size() < max_population) {
				solution_random = solutions_set.get(operations.random_int(0, max_index));
				solution_chromo = solution_random.get_chromosome();
				midpoint_chromo = operations.get_midpoint_chromosome(solution_chromo, top_chromosome);
				
				chromosome_lower = operations.get_random_chromosome(solution_chromo, midpoint_chromo);
				chromosome_upper = operations.get_random_chromosome(midpoint_chromo, top_chromosome);
				chromosome_middle = operations.get_random_chromosome(chromosome_lower, chromosome_upper);
				chromosome_overlap = operations.get_overlap_chromosome(chromosome_upper, top_chromosome);
				
				new_population.add(new ClassIndividual(chromosome_lower));
				new_population.add(new ClassIndividual(chromosome_upper));
				new_population.add(new ClassIndividual(chromosome_middle));
				new_population.add(new ClassIndividual(chromosome_overlap));
			}
		}
		catch(Exception e) {
			System.out.println(header + ".get_solutions_stone_skipping().Exception: " + e);
		}
		
		return new_population;
	}
	
	
	//---------------------------------
	//---------------------------------
	
	
	//----Genera un conjunto de individuos a partir del mejor cromosoma y la tendencia.
	private List<ClassIndividual> swap_top_trending(List<Double> top_chromosome, List<Double> trending_chromo){
		List<ClassIndividual> solutions = new ArrayList<ClassIndividual>();
		
		try {
			int x = 0;
			int max_index = top_chromosome.size();
			List<Double> chromosome = null;
			
			while (x < max_index) {
				chromosome = new ArrayList<Double>(top_chromosome);  //----Duplicamos en nueva lista.
				chromosome.set(x, trending_chromo.get(x));
				
				solutions.add(new ClassIndividual(chromosome));
				x += 1;
			}			
		}
		catch(Exception e) {
			System.out.println(header + ".swap_top_trending().Exception: " + e);
		}

		return solutions;
	}
	
	
	//----Calcula una razon aleatoria dentro de un rango, acorde a la mutacion.
	private double get_divine_rate(int mutation_rate) {
		double divine_rate = 0;
		
		try {
			if (mutation_rate < 4) divine_rate = operations.random_int(1, 3) / 100;
			else if (mutation_rate < 8) divine_rate = operations.random_int(2, 4) / 100;
			else divine_rate = operations.random_int(3, 5) / 100;
		}
		catch(Exception e) {
			System.out.println(header + ".get_divine_rate().Exception: " + e);
		}

		return divine_rate;
	}

	
	//----Calcula el valor del Golden Ratio a partir de la diversidad.
	private double get_golden_ratio(int divergent) {
		double golden_ratio = 0;
		
		try {
			golden_ratio = (1 + Math.sqrt(5)) / 2;  //----Valor aproximado a '1.618033988749'.
			
			if (divergent >= 1) {  //----Para evitar generar el mismo resultado.
				golden_ratio *= operations.random_double(0.95, 1.05);
			}			
		}
		catch(Exception e) {
			System.out.println(header + ".get_golden_ratio().Exception: " + e);
		}
	
		return golden_ratio;
	}
	

	//----Calcula un nuevo cromosoma fibonacci a partir de un cromosoma de entrada.
	private List<Double> get_fibonacci_chromosome(List<Double> base_chromosome){
		return get_fibonacci_chromosome(base_chromosome, 2);
	}
	
	
	//----Calcula un nuevo cromosoma fibonacci a partir de un cromosoma de entrada.
	private List<Double> get_fibonacci_chromosome(List<Double> base_chromosome, int divergent){
		List<Double> fibonacci_chromosome = new ArrayList<Double>();
		
		try {
			int x = 0;
			double value = 0.0;
			int dimensions = base_chromosome.size();
			
			double golden_ratio = get_golden_ratio(divergent);
			
			while (x < dimensions) {
				if (divergent > 1) golden_ratio = get_golden_ratio(divergent);
				
				value = base_chromosome.get(x) / golden_ratio;  //----NOTA: Siempre estamos MINIMIZANDO.				
				fibonacci_chromosome.add(value);
				x += 1;
			}
		}
		catch(Exception e) {
			System.out.println(header + ".get_fibonacci_chromosome().Exception: " + e);
		}

		return fibonacci_chromosome;
	}
	
	
}
