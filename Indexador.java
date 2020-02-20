//package com;
// Java program to read JSON from a file 

//import java.lang.Iterable;
//import java.io.*;
import java.util.*;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.FileReader;
import java.io.RandomAccessFile; 
import java.io.FileNotFoundException;
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
	public static int numVertices = 599128;
    
    public static LinkedList<Integer> adjLists[] = new LinkedList[numVertices+1];
    public static int atualS = 0;

   	static double meajuda = (numVertices)*(0.01);	
    static int numSeeds = (int) meajuda+1;
	static int seeds[] = new int[numSeeds];

	//static byte distVet[] = new byte[numVertices+1];
	public static LinkedList<Integer> distVet[] = new LinkedList[numVertices+1];

	static int m21 = 2097151;
    static int m2 = 7;
    static int cont = 0;

    public static void maskAndAdd(int userId,int id,byte level){
    	System.out.println(id + " " +level);
    	int nf = ((id&m21) << 3) + (m2&level);
    	distVet[userId].add(nf);
    }

	public static void indV(int v, RandomAccessFile grafo, RandomAccessFile seeks) throws IOException{

		seeks.writeInt(atualS);
		int n = adjLists[v].size();
		
		for(int j=0; j< n;j++){
			grafo.writeInt(adjLists[v].get(j));
		}

		atualS += n;
	}

	public static void indV2(int v, RandomAccessFile distSeeds) throws IOException{
		System.out.println("&"+v);
		byte vet[] = new byte[numSeeds];
		Arrays.fill(vet,(byte)5);


		Iterator<Integer> i = distVet[v].listIterator();
		while(i.hasNext()){
			int num = i.next();
			System.out.println((num) >> 2);
			vet[(num & 4194300) >> 3] = (byte)(num&7); 
		}

		distSeeds.write(vet);
	}		

	public static int getDataSet(String arquivo,int id) throws Exception 
	{	
		System.out.println("********************"+arquivo+"***********************");
		// perseando o arquivo .json
		Object obj = new JSONParser().parse(new FileReader(arquivo));

		JSONArray jo = (JSONArray) obj; 
		Iterator iterator = jo.iterator();

		int ant = 1;
		// iterando pelas relações para construir o grafo de relações
	    while (iterator.hasNext()) {

	    	JSONArray fo = (JSONArray) iterator.next();
	        Iterator it = fo.iterator();

	        String p1="",p2="";
	        int i=0,src=0,dest=0;
	        boolean p=true;

			while (it.hasNext()) {
				Object nome;
				nome = it.next();

				if(i==0) p1 = (String) nome; 
				if(i==1) p2 = (String) nome;

				if(i==2){
					 if(nome != null && (long)nome >= 2006) p = false;
				} 
				
				i++;
			}

			if(p){
				cont++;

				if (users.get(p1) == null){
					System.out.println(p1 + " " + id);
					users.put(p1, id);
					id++;	
				}

				if (users.get(p2) == null){
					System.out.println(p2 + " " + id);
					users.put(p2, id);
					id++;	
				}


				src = users.get(p1);
				dest = users.get(p2);

				adjLists[src].add(dest);
				adjLists[dest].add(src);
			}
			
	    }

	    return id;
	}

	public static void indexa() throws IOException 
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

        	//System.out.prsintln(doc);
        	iwriter.addDocument(doc);
		}
        
        iwriter.close();
	}


	private static void BFS(int s,int ind) 
    { 	

    	//System.out.println(s);
    	byte level = 0;
        boolean visited[] = new boolean[numVertices+1]; 
  
        LinkedList<Integer> queue = new LinkedList<Integer>(); 
   
        visited[s]=true;

        maskAndAdd(s,ind,level);
        queue.add(s);
  
        while(queue.size() != 0 && level < 4) 
        { 
            // Dequeue a vertex from queue and print it 
            s = queue.poll();  
 
            Iterator<Integer> i = adjLists[s].listIterator();
            level++;
            while (i.hasNext()) 
            {	

                int n = i.next();           
                if (!visited[n]) 
                { 	
                	maskAndAdd(n,ind,level);
                    visited[n] = true; 
                    queue.add(n); 
                } 
            } 
        } 
    }

	public static void computaDist(RandomAccessFile distSeeds) throws IOException{
		
		int passo = numVertices/numSeeds;
		int n = numSeeds;
		for(int i=0,r =1;r<numVertices;i++,r+=passo){
			if(i >= n) break;
			seeds[i] = r;
			numSeeds = i+1;
		}	

		/*Random rand = new Random();
		*/for(int i=0;i<numSeeds;i++){
			System.out.println(seeds[i]);
		}


		for(int i=0;i<numSeeds;i++){
			System.out.print(i);
			System.out.print(".");
			BFS(seeds[i],i);
			System.out.println(".");
		}
	}

	public static void main(String[] args) throws FileNotFoundException,Exception
	{ 	
	//	System.out.println(numSeeds);
		RandomAccessFile grafo = new RandomAccessFile("grafo", "rw");
		RandomAccessFile seeks = new RandomAccessFile("seeks", "rw");
		RandomAccessFile distSeeds = new RandomAccessFile("distSeeds", "rw");

		for(int i = 1;i<=numVertices;i++){
			adjLists[i] = new LinkedList();
			distVet[i] = new LinkedList();
    	}

		int id = 1;
		for(int i=1;i<=108;i++){
			if(i!=6) id = getDataSet("data/" + Integer.toString(i),id);
			//id = getDataSet("arquivoteste20",id);
		}

    //computaDist(distSeeds);



    	/*System.out.println("Indexação Grafo...");
		Date start = new Date();
		for(int i = 1;i<=numVertices;i++){
			//indV(i,grafo,seeks);
			indV2(i,distSeeds);
    	}

    	Date end = new Date();
        System.out.println("Time: "+(end.getTime()-start.getTime())+"ms");
        System.out.println(numSeeds);	
*/


        System.out.println(cont);
        grafo.close();
		seeks.close();
    	distSeeds.close();
		//indexa();
	}
}