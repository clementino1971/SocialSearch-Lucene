// Java program to read JSON from a file 

//import java.lang.Iterable;
import java.io.FileReader; 
import java.util.Iterator; 
import java.util.Map; 
import java.util.HashMap;
import java.lang.Integer;

import org.json.simple.JSONArray; 
import org.json.simple.JSONObject; 
import org.json.simple.parser.*; 

public class Indexador
{ 
	//Map usado para atribuir id aos Autores
	public static Map<String, Integer> users = new HashMap<String, Integer>();

	public static int getDataSet(String arquivo,int id) throws Exception {
		// perseando o arquivo .json 
		Object obj = new JSONParser().parse(new FileReader(arquivo));

		JSONArray jo = (JSONArray) obj; 
		Iterator iterator = jo.iterator();

		// iterando pelas relações para construir o grafo de relações
	    while (iterator.hasNext()) {

	    	JSONArray fo = (JSONArray) iterator.next();
	        Iterator it = fo.iterator();

	        int i=0;
			while (it.hasNext()) {
				Object nome;
				nome = it.next();
				if(i != 2){
					
					if (users.get((String) nome) == null){
						System.out.println((String) nome + " " + id);
						users.put((String)nome, id);
						id++;	
					} 
				}
				i++;	
			}
	    }

	    return id;
	}

	/*public static void Indexa(){

	}*/


	public static void main(String[] args) throws Exception 
	{ 	
		int id = 1;
		id = getDataSet("arquivoteste20",id);
		System.out.println(id);


		for (String i : users.keySet()) {
		  System.out.println("key: " + i + " value: " + users.get(i));
		}
	}
}