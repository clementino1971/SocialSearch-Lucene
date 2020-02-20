import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;
import java.io.RandomAccessFile; 
import java.util.Date;
import java.util.LinkedList;
import java.util.ArrayList;
import java.io.BufferedInputStream;
import java.io.FileInputStream;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.document.Document;
  
class Social {
    static int numVertices = 599128;   
    //static int numSeeds = 59913;
    static int numSeeds = 29957;
    //static int numSeeds = 5992;

  public void alteraScore(byte[] vet1,ArrayList<User> hits) throws InterruptedException,FileNotFoundException, IOException{
    if(hits.size() == 1) return;


    int peso[] = {0,1000000,10000,100,1};
    long ini = 0;
    byte vet2[] = new byte[numSeeds];
    try (FileInputStream is = new FileInputStream("distSeeds");
         BufferedInputStream bis = new BufferedInputStream(is,16 * 1024)) {
        
        int b=1,cont1=1;

        for(int i=0;i<hits.size();i++){
            long dif = ((hits.get(i).id-1)*numSeeds) - ini;
            bis.skip(dif);
            if(bis.read(vet2) != -1){
                int cont[] ={0,0,0,0,0};
                //System.out.println(cont1);
                // cont1++;

                for(int j=0;j< numSeeds;j++){
                    byte r1 = vet1[j];
                    byte r2 = vet2[j];
                    if(r1+r2 < 5 && r1+r2 > 0) cont[r1+r2]++;
                }

                int sum = 0;
                //System.out.println(i);
                for(int j=1;j<=4;j++){    
                  //  System.out.println(j + " = " + (cont[j]));
                    sum += peso[j]*cont[j];
                    
                }
                hits.get(i).score = (float)hits.get(i).score*(float)(0.1) + (float)(sum/Math.log10(numSeeds));

            }

            ini = (hits.get(i).id)*numSeeds;
        }
    }
  }

  public void onTheFly(int u1,ArrayList<User> hits) throws FileNotFoundException, IOException{
    
    int k = hits.size();
    
    //System.out.println("CAme here");
    RandomAccessFile grafo = new RandomAccessFile("grafo", "r");
    RandomAccessFile seeks = new RandomAccessFile("seeks", "rw");

    seeks.seek((long)seeks.length());
    seeks.writeInt((int)seeks.length());

    int level = 0;
    boolean visited[] = new boolean[numVertices+1]; 
    int dist[] = new int[numVertices+1]; 

    for (int i=1;i<=numVertices;i++) {
        dist[i] = 1000000000;
    }

    LinkedList<Integer> queue = new LinkedList<Integer>(); 

    visited[u1]=true;
    queue.add(u1);
    int s,cont =0;


    while(queue.size() != 0 && level < 10000) 
    {   
        // Dequeue a vertex from queue and print it 
        s = queue.poll();  
        //System.out.println(s);
        seeks.seek((s-1)*4);
        //System.out.println("1a");
        int w = seeks.readInt();
        //System.out.println(w);
        //System.out.println("1b");
        int w1 = seeks.readInt();
        //System.out.println(w1);
        int n = w1-w;
        int vizinhos[] = new int[n];
        grafo.seek(w*4);
        level++;
        for(int i=0;i<n;i++){
            //System.out.println("2");
            vizinhos[i] = grafo.readInt();
            //System.out.println(cont++);

            int v = vizinhos[i];           
            if (visited[v] == false) 
            {   
                dist[v] = level; 
                visited[v] = true; 
                queue.add(v); 
            } 
        }

        boolean ja = true;
        for(int i=0;i<k;i++){
            if(visited[hits.get(i).id] == false){
                ja = false;
                break;
            }
        }

        if(ja) break;
    } 

    for(int i=0;i<k;i++){
        hits.get(i).score = (hits.get(i).score) + (float)(1.0/dist[hits.get(i).id])*10;
        //System.out.println(dist[sel[i]] + " - " + hits[i].score);
    }
  }
}
