package lifecycle_ga;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class RegenerateMethods {
	private static final String header = "RegenerateMethods";
	private ChromoOperations operations = null;
	private RegenerateEliteProjection elite_projection = null;
	
	
	//----Constructor de la clase.	
	public RegenerateMethods() {
		operations = new ChromoOperations();
		elite_projection = new RegenerateEliteProjection();
	}
	
	
	//----Genera una nueva poblacion de cromosomas aleatorios.
	public List<ClassIndividual> generate_random_pop(ClassIndividual top_individual, int max_population, int dimensions, double lower_bounds, double upper_bounds){
		List<ClassIndividual> new_population = new ArrayList<ClassIndividual>();
		
		try {
			new_population.add(top_individual);
			
			List<Double> new_chromosome = null;
			
			for (int x = 0; x < max_population; x++) {
				new_chromosome = operations.generate_chromosome(dimensions, lower_bounds, upper_bounds);
				new_population.add(new ClassIndividual(new_chromosome));
			}
		}
		catch(Exception e) {
			System.out.println(header + ".generate_random_pop().Exception: " + e);
		}
		
		return new_population;
	}
	
	
	//----Genera nuevos cromosomas, basados en los mejores obtenidos de la poblacion.
	public List<ClassIndividual> generate_elite_mix(List<ClassIndividual> pop_legends, int max_population){
		List<ClassIndividual> new_population = new ArrayList<ClassIndividual>();
		
		try {
			ClassIndividual top_individual = pop_legends.get(0);
			new_population.add(top_individual);
			
			List<Double> new_chromosome = null;
			List<Double> random_chromo = null;
			double random_gene = 0.0;
			
			int dimensions = top_individual.get_chromosome().size();
			int max_legends = pop_legends.size() -1;
			int max_dimension = dimensions -1;
			
			
			while (new_population.size() < max_population) {
				new_chromosome = new ArrayList<Double>();
				
				while (new_chromosome.size() < dimensions) {
					random_chromo = pop_legends.get(random_int(0, max_legends)).get_chromosome();
					random_gene = random_chromo.get(random_int(0, max_dimension));
					
					new_chromosome.add(random_gene);
				}
				
				new_population.add(new ClassIndividual(new_chromosome));
			}
		}
		catch(Exception e) {
			System.out.println(header + ".generate_elite_mix().Exception: " + e);
		}
		
		return new_population;
	}
	
	
	//----Genera nuevos cromosomas, basados en los mejores obtenidos de la poblacion.
	public List<ClassIndividual> generate_elite_motation(List<ClassIndividual> pop_legends, int max_population, int dimensions, double lower_bounds, double upper_bounds){
		List<ClassIndividual> new_population = new ArrayList<ClassIndividual>();
	
		try {
			ClassIndividual top_individual = pop_legends.get(0);
			new_population.add(top_individual);
			
			int max_legends = pop_legends.size() -1;
			int max_dimension = top_individual.get_chromosome().size() -1;
			
			List<Double> chromosome_a = null;
			List<Double> chromosome_b = null;
			List<Double> chromosome = null;

			int dimensions_aux = 0;
			int dimension_index = 0;
			
			double motion_level = 0.0;
			double random_motion = 0.0;
			double gene_a = 0.0;
			double gene_b = 0.0;
			
			
			while (new_population.size() < max_population) {
				chromosome_a = operations.generate_chromosome(dimensions, lower_bounds, upper_bounds);
				chromosome_b = pop_legends.get(random_int(0, max_legends)).get_chromosome();
				chromosome = new ArrayList<Double>(chromosome_b);  //----Duplicamos en nueva lista.
				
				dimensions_aux = random_int(1, Math.round(max_dimension / 2));  //----Cambia desde uno hasta la mitad de los genes.
				motion_level = random_double(0.015, 0.030);
				
				
				for (int x = 0; x < dimensions_aux; x++) {
					dimension_index = random_int(0, max_dimension);
					random_motion = random_double(0, motion_level);
					
					gene_a = chromosome_a.get(dimension_index) * random_motion;
					gene_b = chromosome_b.get(dimension_index) * (1 - random_motion);
					chromosome.set(dimension_index, gene_a + gene_b);
				}
				
				new_population.add(new ClassIndividual(chromosome));
			}
		}
		catch(Exception e) {
			System.out.println(header + ".generate_elite_motation().Exception: " + e);
		}
		
		return new_population;
	}
	
	
	//----Genera nuevos cromosomas, explotando alrededor del mejor individuo.
	public List<ClassIndividual> generate_top_motation(ClassIndividual top_individual, int max_particles, int dimensions, double lower_bounds, double upper_bounds) {
		List<ClassIndividual> new_population = new ArrayList<ClassIndividual>();
		
		try {
			new_population.add(top_individual);
			
			List<Double> top_chromosome = top_individual.get_chromosome();
			int max_dimension = top_chromosome.size() -1;
			double explosion_level = random_double(0.015, 0.030);  //----[0.03, 0.07], [0.025, 0.05], [0.015, 0.030] 
			boolean exploit_on = true;
			
			List<Double> chromosome = null;
			List<Double> explosion_factor = null;
			
			int dimension_index = 0;
			double position_change = 0.0;
			double displacement_gene = 0.0;
			double original_gene = 0.0;
			
			
			for (int x = 1; x < max_particles; x++) {
				chromosome = new ArrayList<Double>(top_chromosome);  //----Duplicamos en nueva lista.
				explosion_factor = operations.generate_chromosome(dimensions, lower_bounds, upper_bounds);
				
				if (exploit_on) {
					dimension_index = random_int(0, max_dimension);
					position_change = random_double(0, explosion_level);
					
					displacement_gene = explosion_factor.get(dimension_index) * position_change;
					original_gene = chromosome.get(dimension_index) * (1 - position_change);
					
					chromosome.set(dimension_index, original_gene + displacement_gene);
				}
				
				new_population.add(new ClassIndividual(chromosome));
			}
		}
		catch(Exception e) {
			System.out.println(header + ".generate_top_motation().Exception: " + e);
		}
		
		return new_population;
	}
	
	
	//----Genera nuevos cromosomas, explotando alrededor del mejor individuo.
	public List<ClassIndividual> generate_top_elite_crosswap(ClassIndividual top_individual, List<ClassIndividual> pop_legends, int max_population) {
		List<ClassIndividual> new_population = new ArrayList<ClassIndividual>();
		
		try {
			new_population.add(top_individual);
			
			List<Double> top_chromosome = top_individual.get_chromosome();
			int max_dimension = top_chromosome.size() -1;
			int max_legends = pop_legends.size() -1;

			List<Double> chromosome_a = null;
			List<Double> chromosome_b = null;
			double gene_a = 0.0;
			double gene_b = 0.0;
			
			int dimensions_aux = 0;
			int dimension_index = 0;
			int x = 0;
			
			while (new_population.size() < max_population) {
				chromosome_a = new ArrayList<Double>(top_chromosome);  //----Duplicamos en nueva lista.
				chromosome_b = new ArrayList<Double>(pop_legends.get(random_int(0, max_legends)).get_chromosome());  //----Duplicamos en nueva lista.
				
				dimensions_aux = random_int(1, max_dimension);  //----Default: al menos un intercambio.
				x = 0;
				
				while ((x < dimensions_aux) && (new_population.size() < max_population)) {
					dimension_index = random_int(0, max_dimension);
					
					gene_a = chromosome_a.get(dimension_index);
					gene_b = chromosome_b.get(dimension_index);
					
					chromosome_a.set(dimension_index, gene_b);
					chromosome_b.set(dimension_index, gene_a);
					
					new_population.add(new ClassIndividual(chromosome_a));
					new_population.add(new ClassIndividual(chromosome_b));
					x += 1;
				}
			}
		}
		catch(Exception e) {
			System.out.println(header + ".generate_top_elite_crosswap().Exception: " + e);
		}
		
		return new_population;
	}
	
	
	//----Genera nuevos cromosomas, basados en el mejor individuo de la poblacion.
	public List<ClassIndividual> generate_elite_projection(ClassIndividual top_individual, List<ClassIndividual> pop_legends, int max_population, int mutation_rate, List<Double> delta_chromo) {
		return elite_projection.generate_elite_projection(top_individual, pop_legends, max_population, mutation_rate, delta_chromo);
	}
	
	
	//----Genera nuevos cromosomas, basados en el mejor individuo de la poblacion.
	public List<ClassIndividual> generate_stone_skipping(ClassIndividual top_individual, List<ClassIndividual> pop_legends, int max_population) {
		List<ClassIndividual> new_population = new ArrayList<ClassIndividual>();
		
		try {
			new_population.add(top_individual);  //----Agregamos el mejor individuo.
			
			int max_index = pop_legends.size() -1;

			List<Double> p0_chromo = top_individual.get_chromosome();  //----Recuperamos el mejor cromosoma de la poblacion.
			List<Double> p8_chromo = null;
			List<Double> p4_chromo = null;
			List<Double> p2_chromo = null;
			List<Double> p6_chromo = null;
			List<Double> p1_chromo = null;
			List<Double> p3_chromo = null;
			List<Double> p5_chromo = null;
			List<Double> p7_chromo = null;
			List<Double> p01_chromo = null;
			List<Double> p12_chromo = null;
			List<Double> p23_chromo = null;
			List<Double> p45_chromo = null;
			List<Double> p78_chromo = null;
			
			List<Double> chromosome_a = null;
			List<Double> chromosome_b = null;
			List<Double> chromosome_c = null;
			List<Double> chromosome_d = null;
			List<Double> chromosome_e = null;
			List<Double> chromosome_f = null;
			List<Double> chromosome_g = null;
			List<Double> chromosome_h = null;

			
			while (new_population.size() < max_population) {
				p8_chromo = pop_legends.get(operations.random_int(0, max_index)).get_chromosome();
				        
				p4_chromo = operations.get_midpoint_chromosome(p0_chromo, p8_chromo);
				p2_chromo = operations.get_midpoint_chromosome(p0_chromo, p4_chromo);
				p6_chromo = operations.get_midpoint_chromosome(p4_chromo, p8_chromo);
				
				p1_chromo = operations.get_midpoint_chromosome(p0_chromo, p2_chromo);
				p3_chromo = operations.get_midpoint_chromosome(p2_chromo, p4_chromo);
				p5_chromo = operations.get_midpoint_chromosome(p4_chromo, p6_chromo);
				p7_chromo = operations.get_midpoint_chromosome(p6_chromo, p8_chromo);
				
				p01_chromo = operations.get_midpoint_chromosome(p0_chromo, p1_chromo);
				p12_chromo = operations.get_midpoint_chromosome(p1_chromo, p2_chromo);
				p23_chromo = operations.get_midpoint_chromosome(p2_chromo, p3_chromo);
				p45_chromo = operations.get_midpoint_chromosome(p4_chromo, p5_chromo);
				p78_chromo = operations.get_midpoint_chromosome(p7_chromo, p8_chromo);
				
				chromosome_a = operations.get_random_chromosome(p78_chromo, p8_chromo);
				chromosome_b = operations.get_random_chromosome(p45_chromo, p5_chromo);
				chromosome_c = operations.get_random_chromosome(p23_chromo, p3_chromo);
				chromosome_d = operations.get_random_chromosome(p12_chromo, p2_chromo);
				chromosome_e = operations.get_random_chromosome(p01_chromo, p12_chromo);
				chromosome_f = operations.get_random_chromosome(p01_chromo, chromosome_e);
				chromosome_g = operations.get_random_chromosome(p0_chromo, p01_chromo);
				chromosome_h = operations.get_overlap_chromosome(chromosome_g, p0_chromo);
				
		        new_population.add(new ClassIndividual(chromosome_a));
		        new_population.add(new ClassIndividual(chromosome_b));
		        new_population.add(new ClassIndividual(chromosome_c));
		        new_population.add(new ClassIndividual(chromosome_d));
		        new_population.add(new ClassIndividual(chromosome_e));
		        new_population.add(new ClassIndividual(chromosome_f));
		        new_population.add(new ClassIndividual(chromosome_g));
		        new_population.add(new ClassIndividual(chromosome_h));
			}
		}
		catch(Exception e) {
			System.out.println(header + ".generate_stone_skipping().Exception: " + e);
		}
		
		return new_population;
	}
	
	
	//----------------------------------
	//----------------------------------
		
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
	
	
}
