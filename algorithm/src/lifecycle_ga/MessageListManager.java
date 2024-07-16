package lifecycle_ga;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class MessageListManager {
	private static final String header = "MessageListManager";
	private static MessageListManager instance = null;
	
	private static List<String> population_pool = null;  //----S
	private static List<String> population_evaluate = null;  //----S
	private static List<String> population_record = null;  //----L
	private static List<String> endof_experiment = null;  //----L
	
	
	private MessageListManager() {
		population_pool = new ArrayList<String>();
		population_evaluate = new ArrayList<String>();
		population_record = new ArrayList<String>();
		endof_experiment = new ArrayList<String>();
	}
	
	//----Un metodo "synchronized" controla el aceso simultaneo de multiples procesos.
	public static synchronized MessageListManager getInstance() {
		if (instance == null) { instance = new MessageListManager(); }
		return instance;
	}
	
	
	//----Confirma la comunicacion con la instancia de la clase. 
	public String ping() { 
		return "PONG"; 
	}
	
	//----Elimina todos los elementos de todas las listas.
	public synchronized boolean flushDB() {
		boolean flag = false;
		
		try {
			//----population_pool = new ArrayList<String>();
			//----population_evaluate = new ArrayList<String>();
			//----population_record = new ArrayList<String>();
			//----endof_experiment = new ArrayList<String>();

			population_pool.clear();
			population_evaluate.clear();
			population_record.clear();
			endof_experiment.clear();
						
			flag = true;
		}
		catch (Exception e) {
			System.out.println(header + ".flushDB().Exception: " + e);
		}
		
		return flag;
	}

	//----Verifica la existencia de una lista y la selecciona.
	private synchronized List<String> select_queue(String queue_name){
		List<String> queue = null;
		
		try {			
			switch (queue_name.toLowerCase()) {
				case "population_pool": queue = population_pool; break;
				case "population_evaluate": queue = population_evaluate; break;
				case "population_record": queue = population_record; break;
				case "endof_experiment": queue = endof_experiment; break;
			}
		}
		catch (Exception e) {
			System.out.println(header + ".select_queue().Exception: " + e);
		}
		
		return queue;
	}

	//----Remueve todos los elementos de una lista determinada.
	public synchronized void clear_queue(String queue_name) {		
		try {
			List<String> queue = select_queue(queue_name);
			if (queue != null) { queue.clear(); }
		}
		catch (Exception e) {
			System.out.println(header + ".clear_queue().Exception: " + e);
		}
	}
		
	//----Inserta un nuevo elemento al final de la lista.
	public synchronized boolean rpush(String queue_name, String element) {
		boolean flag = false;
		
		try {
			List<String> queue = select_queue(queue_name);
			if (queue != null) { flag = queue.add(element); }
		}
		catch (Exception e) {
			System.out.println(header + ".rpush().Exception: " + e);
		}
		
		return flag;
	}
	
	//----Regresa el primer elemento y lo remueve de la lista.
	public synchronized String lpop(String queue_name) {
		String element = null;
		
		try {
			List<String> queue = select_queue(queue_name);
			if (queue != null && queue.size() > 0) { element = queue.remove(0); }
		}
		catch (Exception e) {
			System.out.println(header + ".lpop().Exception: " + e);
		}
		
		return element;
	}
	
	//----Inserta un nuevo elemento en el conjunto.
	public synchronized boolean sadd(String queue_name, String element) {
		return rpush(queue_name, element);
	}

	//----Regresa un elemento del conjunto y lo remueve de la lista.
	public synchronized String spop(String queue_name) {
		String element = null;
		
		try {
			List<String> queue = select_queue(queue_name);
			
			if (queue != null && queue.size() > 0) {
				if (queue.size() == 1) { element = queue.remove(0); }
				else { element = queue.remove(random_int(0, queue.size() -1)); }
			}
		}
		catch (Exception e) {
			System.out.println(header + ".spop().Exception: " + e);
		}
		
		return element;
	}
	

	//----Recupera un numero de elementos del conjunto y los remueve de la lista.
	public synchronized List<String> spop(String queue_name, int sample_size) {
		List<String> set_sample = new ArrayList<String>();
		
		try {
			List<String> queue = select_queue(queue_name);
			
			if (queue != null && queue.size() > 0) {
				int sample_fixed = sample_size > queue.size() ? queue.size() : sample_size ;
				
				for (int x = 0; x < sample_fixed; x++) {
					if (queue.size() == 1) { set_sample.add(queue.remove(0)); }
					else { set_sample.add(queue.remove(random_int(0, queue.size() -1))); }
				}
			}
		}
		catch (Exception e) {
			System.out.println(header + ".spop().Exception: " + e);
		}
		
		return set_sample;
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
