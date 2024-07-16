package lifecycle_ga;

import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class ClassIndividual implements Comparable<ClassIndividual>{
	private static final String header = "ClassIndividual";
	
	private long id_time = 0;
	private boolean evaluated = false;
	private double fitness = 0.0;
	private double error = -1000000000;
	
    private int status = 0;
    private int age = 0;
    private int mutations = 0;
    private int partners = 0;      
    private int offspring = 0;
    private int alpha_level = 0;  //----Se ajusta acorde a las experiencias de vida.

	private String operation = "";
	private List<Double> chromosome = null;

    
	//----Constructores de la clase.
	public ClassIndividual() {
		//----Agregado para facilitar la funcionalidad de SerializeIndividual.
	}
	
	public ClassIndividual(List<Double> p_chromosome) {
		try {
			id_time = System.nanoTime();
			chromosome = p_chromosome;	
		}
		catch (Exception e) {
			System.out.println(header + ".ClassIndividual(1).Exception: " + e);
		}
	}
	
	public ClassIndividual(List<Double> p_chromosome, String p_operation) {
		try {
			id_time = System.nanoTime();
			chromosome = p_chromosome;
			operation = p_operation;
		}
		catch (Exception e) {
			System.out.println(header + ".ClassIndividual(2).Exception: " + e);
		}
	}
	
	@Override
	public int compareTo(ClassIndividual individual) {
		int compareValue = 0;
		
		try {
			if (fitness == individual.get_fitness()) compareValue = 0;
			else if (fitness < individual.get_fitness()) compareValue = -1;
			else if (fitness > individual.get_fitness()) compareValue = 1;
		}
		catch (Exception e) {
			System.out.println(header + ".compareTo().Exception: " + e);
		}
		
		return compareValue;
	}
	
	
	public long get_id_time() { return id_time; }
	public void set_id_time(long id) { id_time = id; }
	
	public boolean is_evaluated() { return evaluated; }
	public void set_evaluated(boolean p_eval) { evaluated = p_eval; } 
	
	public double get_fitness() { return fitness; }
	public void set_fitness(double p_fit) { fitness = p_fit; }
	
	public double get_error() { return error; }
	public void set_error(double new_error) { error = new_error; }
	public String show_error() { return String.valueOf(error); }
	
	public int get_status() { return status; }
	public void set_status(int new_status) { status = new_status; }

	public int get_age() { return age; }
	public void set_age(int p_age) { age = p_age; }
	public void reset_age() { age = 0; }
	public void inc_age() { age += 1; }
	
	public int get_mutations() { return mutations; }
	public void set_mutations(int p_mut) { mutations = p_mut; }
	public void inc_mutations() { mutations += 1; }
	
	public int get_partners() { return partners; }
	public void set_partners(int p_part) { partners = p_part; }
	public void inc_partners() { partners += 1; }

	public int get_offspring() { return offspring; }
	public void set_offspring(int p_offs) { offspring = p_offs; }
	public void inc_offspring(int new_offspring) { offspring += new_offspring; }

	public int get_alpha_level() { return alpha_level; }
	public void set_alpha_level(int p_alp) { alpha_level = p_alp; } 
	public void inc_alpha_level() { alpha_level += 1; }  //----Incrementa en eventos afortunados, ej. cuando encuentra pareja, tiene hijos o supera la muerte.
	public void dec_alpha_level() { alpha_level -= 1; }  //----Decrementa en desgracias, ej. cuando no se pudo reproducir.

	public String get_operation() { return operation; }
	public void set_operation(String new_operation) { operation = new_operation; }
	
	
	public List<Double> get_chromosome() { return chromosome; }
	public void set_chromosome(List<Double> p_chrom) { chromosome = p_chrom; } 
	public String show_chromosome() { return String.valueOf(chromosome); }  //----Muestra el chromosoma del individuo.
	
	public Double[] get_chromo_Double() { return chromosome.toArray(new Double[0]); }
	public double[] get_chromo_double() {
		double[] chromo_double = null;
		
		try {
			int size = chromosome.size();
			chromo_double = new double[size];
			
			for (int x = 0; x < size; x++) {
				chromo_double[x] = chromosome.get(x);
			}			
		}
		catch(Exception e) {
			System.out.println(header + ".get_chromo_double().Exception: " + e);
		}
		
		return chromo_double;
	}
		
	
    //----Modifica un gen del chromosoma en una posicion aleatoria.
	public boolean perform_mutation(int mutation_rate, List<Double> mutation_factor) {
		boolean mutation = false;
		
		try {
			if (mutation_rate > random_int(0, 100)) {
				int dimensions = chromosome.size();
				
				if ((75 > random_int(0, 100)) || dimensions < 2) {
					int mutation_index = random_int(0, dimensions -1);
					//----double mutation_weight = random_int(0, mutation_rate) / 100;  //----Mas organico.
					double mutation_weight = 0.85;
					
					double mutation_gene = mutation_factor.get(mutation_index) * mutation_weight;
					double original_gene = chromosome.get(mutation_index) * (1 - mutation_weight);
					
					chromosome.set(mutation_index, original_gene + mutation_gene);
				}
				else {
					int midpoint_index = Math.round(dimensions / 2);
					int mutation_index1 = random_int(0, midpoint_index -1);
					int mutation_index2 = random_int(midpoint_index, dimensions -1);
					
					double mutation_gene1 = chromosome.get(mutation_index1);
					double mutation_gene2 = chromosome.get(mutation_index2);
					
					chromosome.set(mutation_index1, mutation_gene2);
					chromosome.set(mutation_index2, mutation_gene1);
				}
				
				mutations += 1;  //----Incrementa el numero de alteraciones en la secuencia del ADN.
				evaluated = false;
				fitness = 0.0;
				mutation = true;
			}
		}
		catch (Exception e) {
			System.out.println(header + ".perform_mutation().Exception: " + e);
		}
		
		return mutation;
	}
	
	
	//----Recorta el fitness a una cadena de X decimales.
    public String show_fitness(int format_fitness) {
    	String fitness_value = "";
    	
		try {
			double aux_fitness = 0.0;
			if (evaluated) aux_fitness = fitness; 
			
			switch (format_fitness) {
				case 24: fitness_value = new DecimalFormat("#.########################").format(aux_fitness); break;	
				case 20: fitness_value = new DecimalFormat("#.####################").format(aux_fitness); break;
				case 16: fitness_value = new DecimalFormat("#.################").format(aux_fitness); break;
				case 12: fitness_value = new DecimalFormat("#.############").format(aux_fitness); break;		
				case 8: fitness_value = new DecimalFormat("#.########").format(aux_fitness); break;		
				case 4: fitness_value = new DecimalFormat("#.####").format(aux_fitness); break;	
				case 2: fitness_value = new DecimalFormat("#.##").format(aux_fitness); break;
				default: fitness_value = String.valueOf(aux_fitness);
			}
		}
		catch (Exception e) {
			System.out.println(header + ".show_fitness().Exception: " + e);
		}

    	return fitness_value;
    }
    

    //----Imprime en consola los valores del objeto
    public void display_stats(String commentary) {
    	try {
        	int format_fitness = 16; 
        	boolean display_chromo = false;
        	
        	display_stats(commentary, format_fitness, display_chromo);	
    	}
		catch (Exception e) {
			System.out.println(header + ".display_stats(2).Exception: " + e);
		}
    }

    
    //----Imprime en consola los valores del objeto
    public void display_stats(String commentary, int format_fitness, boolean display_chromo) {
    	try {
    		if (display_chromo) {
    			System.out.println("F: " + show_fitness(format_fitness) + " .. E: " + show_error() + " ........ " + commentary + " chromosome: " + show_chromosome());
    		}
    		else {
    			System.out.println("F: " + show_fitness(format_fitness) + " .. E: " + show_error() + " ........ " + commentary);
    		}
    	}
		catch (Exception e) {
			System.out.println(header + ".display_stats(4).Exception: " + e);
		}
    }


    //----Imprime en consola los valores del objeto
    public void display_full_stats(String commentary, int format_fitness) {
    	try {
    		System.out.println("F: " + show_fitness(format_fitness) + " .. S: " + get_status_tag() + " .. A: " + age + " .. M: " + mutations + " .. P: " + partners + " .. O: " + offspring + " .. L: " + alpha_level + " ........ " + commentary + " chromosome: " + show_chromosome());
    	}
		catch (Exception e) {
			System.out.println(header + ".display_full_stats().Exception: " + e);
		}
    }
    
    
    //----Decodifica y regresa el status del individual.
    private String get_status_tag() {
    	String status_tag = "----";
    	
    	try {
    		switch(status) {
	    		case -1: status_tag = "inert"; break;
	    		case 0: status_tag = "alive"; break;
	    		case 1: status_tag = "exile"; break;
	    		case 2: status_tag = "fight"; break;
	    		default: status_tag = "----";
    		}
    	}
		catch (Exception e) {
			System.out.println(header + ".get_status_tag().Exception: " + e);
		}
    	
    	return status_tag;
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
	
	
}
