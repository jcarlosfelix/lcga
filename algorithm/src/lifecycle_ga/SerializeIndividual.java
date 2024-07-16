package lifecycle_ga;

import java.util.ArrayList;
import java.util.List;

 
public class SerializeIndividual {
	private static final String header = "SerializeIndividual";
	private static final String SPLITTER = " ";
	private static final String SPECIAL = ",";
	private static final String TRUE = "true";
	
	
	
	//----Empaqueta todos los valores del individuo en una cadena separada por PIPE.
	public String toJson(ClassIndividual individual) {
		String individual_data = "";

		try {
			//----Recuperamos los valores del individuo.			
			long id_time = individual.get_id_time();
			boolean evaluated = individual.is_evaluated();
			double fitness = individual.get_fitness();
			
		    int status = individual.get_status();
		    int age = individual.get_age();
		    int mutations = individual.get_mutations();
		    int partners = individual.get_partners();
		    int offspring = individual.get_offspring();
		    int alpha_level = individual.get_alpha_level();

			String operation = individual.get_operation();
			List<Double> chromosome = individual.get_chromosome();
			
			//----Empaquetamos en la cadena.
			individual_data += String.valueOf(id_time) + SPLITTER;
			individual_data += String.valueOf(evaluated) + SPLITTER;
			individual_data += String.valueOf(fitness) + SPLITTER;
			individual_data += String.valueOf(status) + SPLITTER;
			individual_data += String.valueOf(age) + SPLITTER;
			individual_data += String.valueOf(mutations) + SPLITTER;
			individual_data += String.valueOf(partners) + SPLITTER;
			individual_data += String.valueOf(offspring) + SPLITTER;
			individual_data += String.valueOf(alpha_level) + SPLITTER;
			individual_data += String.valueOf(operation) + SPLITTER;
			individual_data += chromosome_to_string(chromosome);
		}
		catch (Exception e) {
			System.out.println(header + ".toJson().Exception: " + e);
		}
		
		//----System.out.println("-toJson: " + individual_data);
		return individual_data;
	}
	

	//----Genera un nuevo individuo con todos los valores recuperados de una cadena. 
	public ClassIndividual fromJson(String individual_data, Class<ClassIndividual> class_name) {
		ClassIndividual individual = new ClassIndividual();		
		
		try {
			//----System.out.println("-frJson: " + individual_data);
			//----Recuperamos los valores de la cadena.
			ArrayList<String> elements = separator(individual_data, SPLITTER);
			
			long id_time = Long.parseLong(elements.get(0));
			boolean evaluated = elements.get(1).equals(TRUE);			
			double fitness = Double.parseDouble(elements.get(2));			
			
		    int status = Integer.parseInt(elements.get(3));
		    int age = Integer.parseInt(elements.get(4));
		    int mutations = Integer.parseInt(elements.get(5));
		    int partners = Integer.parseInt(elements.get(6));      
		    int offspring = Integer.parseInt(elements.get(7));
		    int alpha_level = Integer.parseInt(elements.get(8));
		    
			String operation = elements.get(9);
			List<Double> chromosome = string_to_chromosome(elements.get(10));
			
			
			//----Definimos los valores del individuo.
			individual.set_id_time(id_time);
			individual.set_evaluated(evaluated);
			individual.set_fitness(fitness);
			
			individual.set_status(status);
			individual.set_age(age);
			individual.set_mutations(mutations);
			individual.set_partners(partners);
			individual.set_offspring(offspring);
			individual.set_alpha_level(alpha_level);
			
			individual.set_operation(operation);
			individual.set_chromosome(chromosome);			
		}
		catch (Exception e) {
			System.out.println(header + ".fromJson().Exception: " + e);
		}

		return individual;
	}
	
	
	//----Transforma un cromosoma en cadena de valores.
	private String chromosome_to_string(List<Double> chromosome) {
		String chromo_string = "";

		try {
			int max_index = chromosome.size() -1;  //----Hasta la penultima posicion.
			int x = 0;
			
			for (x = 0; x < max_index; x++) {
				chromo_string += chromosome.get(x) + SPECIAL;
			}
			chromo_string += chromosome.get(x);
		}
		catch(Exception e) {
			System.out.println(header + ".chromosome_to_string().Exception: " + e);
		}
		
		return chromo_string;
	}
	
	
	//----Transforma una cadena de valores en el cromosoma.
	private List<Double> string_to_chromosome(String chromo_string) {
		List<Double> chromosome = new ArrayList<Double>();
		
		try {
			ArrayList<String> elements = separator(chromo_string, SPECIAL);
			int num_elements = elements.size();
			
			for (int x = 0; x < num_elements; x++) {
				chromosome.add(Double.parseDouble(elements.get(x)));
			}
		}
		catch(Exception e) {
			System.out.println(header + ".string_to_chromosome().Exception: " + e);
		}		
		
		return chromosome;
	}
	
	
	//----Divide una cadena en un arreglo de elementos.
	private ArrayList<String> separator(String string_data, String splitter) {
		ArrayList<String> elements = new ArrayList<String>();
		
		try {
			int begin = 0;
			int index = string_data.indexOf(splitter, begin);
			int width = splitter.length();
			
			while (index >= 0) {
				elements.add(string_data.substring(begin, index));
				
				begin = index + width;  //----begin = index +1;
				index = string_data.indexOf(splitter, begin);
			}
			
			elements.add(string_data.substring(begin));
		}
		catch(Exception e) {
			System.out.println(header + ".separator().Exception: " + e);
		}
		
		return elements;
	}
	
	
}
