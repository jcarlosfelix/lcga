package lifecycle_ga;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class ChromoOperations {
	private static final String header = "ChromoOperations";
		
	
	//----Calcula un valor entero aleatorio dentro del rango especificado.
	public int random_int(int range_one, int range_two) {
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
	public double random_double(double range_one, double range_two) {
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
	public List<Double> generate_chromosome(int dimensions, double lower_bounds, double upper_bounds) {
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
	
	
	//----Genera una lista de N elementos con valores '0'.
	public List<Double> generate_chromo_zero(int dimensions){
		List<Double> chromo_zero = new ArrayList<Double>();
		
		try {
			for (int x = 0; x < dimensions; x++) {
				chromo_zero.add(0.0);
			}
		}
		catch (Exception e) {
			System.out.println(header + ".generate_chromo_zero().Exception: " + e);
		}

		return chromo_zero;
	}
	
	
	//----Calcula un nuevo cromosoma con la sumatoria de dos cromosomas de igual dimension.
	public List<Double> sum_chromosomes(List<Double> chromosome_a, List<Double> chromosome_b) {
		List<Double> chromosome_x = new ArrayList<Double>();
		
		try {
			if (chromosome_a.size() == chromosome_b.size()) {
				double sum_value = 0.0;
				int dimensions = chromosome_a.size();
				
				for (int x = 0; x < dimensions; x++) {
					sum_value = chromosome_a.get(x) + chromosome_b.get(x);
					chromosome_x.add(sum_value);
				}
			}
		}
		catch (Exception e) {
			System.out.println(header + ".sum_chromosomes().Exception: " + e);
		}
		
		return chromosome_x;
	}
	
	
	//----Acumula la sumatoria de dos cromosomas de igual dimension en el cromosoma 'a'.
	public void add_chromosomes(List<Double> chromosome_a, List<Double> chromosome_b) {
		try {
			if (chromosome_a.size() == chromosome_b.size()) {
				double add_value = 0.0;
				int dimensions = chromosome_a.size();
				
				for (int x = 0; x < dimensions; x++) {
					add_value = chromosome_a.get(x) + chromosome_b.get(x);
					chromosome_a.set(x, add_value);
				}
			}
		}
		catch (Exception e) {
			System.out.println(header + ".add_chromosomes().Exception: " + e);
		}
	}
	
		
	//----Calcula el promedio de valores en el cromosoma de entrada.
	public List<Double> get_average_chromo(List<Double> chromosome, int num_elements){
		List<Double> chromosome_avg = new ArrayList<Double>();
		
		try {
			int dimensions = chromosome.size();
			
			for (int x = 0; x < dimensions; x++) {
				chromosome_avg.add(chromosome.get(x) / num_elements);
			}
		}
		catch (Exception e) {
			System.out.println(header + ".get_average_chromo().Exception: " + e);
		}
		
		return chromosome_avg;
	}
	

	//----Calcula el negativo de los valores en el cromosoma de entrada.
	public List<Double> get_negative_chromo(List<Double> chromosome){
		List<Double> chromosome_neg = new ArrayList<Double>();
		
		try {
			int dimensions = chromosome.size();
			
			for (int x = 0; x < dimensions; x++) {
				chromosome_neg.add(chromosome.get(x) * -1);
			}
		}
		catch (Exception e) {
			System.out.println(header + ".get_negative_chromo().Exception: " + e);
		}
		
		return chromosome_neg;
	}
	
	
	//----Calcula el punto intermedio entre dos cromosomas.
	public List<Double> get_midpoint_chromosome(List<Double> chromosome_a, List<Double> chromosome_b){
		List<Double> chromosome = new ArrayList<Double>();
		
		try {
			List<Double> chromo_aux = sum_chromosomes(chromosome_a, chromosome_b);
			int dimensions = chromo_aux.size();
			
			for (int x = 0; x < dimensions; x++) {
				chromosome.add(chromo_aux.get(x) / 2);
			}
		}
		catch (Exception e) {
			System.out.println(header + ".get_midpoint_chromosome().Exception: " + e);
		}

		return chromosome;
	}
	
	
	//----Genera un cromosoma aleatorio entre dos puntos de dominio.
	public List<Double> get_random_chromosome(List<Double> chromosome_a, List<Double> chromosome_b){
		List<Double> chromosome = new ArrayList<Double>();
		
		try {
			int dimensions = chromosome_a.size();
			
			for (int x = 0; x < dimensions; x++) {
				chromosome.add(random_double(chromosome_a.get(x), chromosome_b.get(x)));
			}
		}
		catch (Exception e) {
			System.out.println(header + ".get_random_chromosome().Exception: " + e);
		}

		return chromosome;
	}
	
	
	//----Calcula un cromosoma como proyeccion entre dos cromosomas.
	public List<Double> get_overlap_chromosome(List<Double> chromosome_a, List<Double> chromosome_b){
		List<Double> new_chromosome = null;

		try {
			new_chromosome = get_negative_chromo(chromosome_a);  //----Los volvemos negativos para realizar una resta contra el mejor individuo.
			
		    add_chromosomes(new_chromosome, chromosome_b);  //----Calculamos la diferencia contra el mejor individuo (cromosoma 'b').
		    add_chromosomes(new_chromosome, chromosome_b);  //----Sumamos la diferencia al mejor cromosoma para calcular el nuevo cromosoma.
		}
		catch (Exception e) {
			System.out.println(header + ".get_overlap_chromosome().Exception: " + e);
		}

		return new_chromosome;
	}
	
}
