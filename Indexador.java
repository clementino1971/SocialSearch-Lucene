//package com;

// Java program to read JSON from a file 

//import java.lang.Iterable;
import java.io.IOException;
import java.io.FileReader; 
import java.util.Iterator; 
import java.util.Map; 
import java.util.HashMap;
import java.lang.Integer;
//import java.lang.ClassNotFoundException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

import org.json.simple.JSONArray; 
import org.json.simple.JSONObject; 
import org.json.simple.parser.*; 

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.StoredField;

public class Indexador
{ 
	//Map usado para atribuir id aos Autores
	public static Map<String, Integer> users = new HashMap<String, Integer>();

	public static int getDataSet(String arquivo,int id) throws Exception 
	{
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

	public static void Indexa() throws IOException 
	{
		Analyzer analyzer = new StandardAnalyzer();

        String indexPath = "index";
        Directory directory = FSDirectory.open(Paths.get(indexPath));
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter iwriter = new IndexWriter(directory, config);

        for (String i : users.keySet()) {
		  	//System.out.println("key: " + i + " value: " + users.get(i));

		  	Document doc = new Document();
        	doc.add(new Field("name", i, TextField.TYPE_STORED));
        	doc.add(new StoredField("id",users.get(i)));

        	//System.out.println(doc);
        	iwriter.addDocument(doc);
		}
        
        iwriter.close();
	}


	public static void main(String[] args) throws Exception
	{ 	
		int id = 1;
		for(int i=1;i<=108;i++){
			if(i!=6) id = getDataSet("data/" + Integer.toString(i),id);
		}

		/*for (String i : users.keySet()) {
		  System.out.println("key: " + i + " value: " + users.get(i));
		}*/

		Indexa();
	}
}