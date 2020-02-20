import java.util.*;
import java.util.Scanner;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.FileInputStream;

import java.nio.file.Paths;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.document.Document;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;


class IdComparator implements Comparator<User> {
    @Override
    public int compare(User o1, User o2) {
        return  (o1.getId() <  o2.getId() ? -1 : (o1.getId() == o2.getId() ? 0 : 1));
    }
}

class ScoreComparator implements Comparator<User> {
    @Override
    public int compare(User o1, User o2) {
        return  (o1.getScore() >  o2.getScore() ? -1 : (o1.getScore() == o2.getScore() ? 0 : 1));
    }
}

public class Buscador
{   
    static int numSeeds = 29957;

	public static void main(String[] args) throws IOException
	{

		Analyzer analyzer = new StandardAnalyzer();
		// Now search the index:
        //DirectoryReader ireader = DirectoryReader.open(directory);
        IndexReader ireader = DirectoryReader.open(FSDirectory.open(Paths.get("index")));
        IndexSearcher isearcher = new IndexSearcher(ireader);
        // Parse a simple query that searches for "text":
        QueryParser parser = new QueryParser("name", analyzer);
        Social nr = new Social();
        
        try{
            while(true){
                System.out.println("Search : ");
                Scanner in = new Scanner(System.in);
                String s = in.nextLine();
                
                if(s == "quit" || s.isEmpty()){
                   System.out.println("Bye \\o/"); 
                   break; 
                } 

                Query query = parser.parse(s);
                ScoreDoc[] hits = isearcher.search(query, 100).scoreDocs;
                ArrayList<User> tops = new ArrayList<User>();

                for (int i = 0; i < hits.length; i++) {
                  User top = new User();
                  top.doc = hits[i].doc; 
                  top.score = hits[i].score; 
                  Document hitDoc = isearcher.doc(hits[i].doc);
                  top.name = hitDoc.get("name");
                  top.id = Integer.parseInt(hitDoc.get("id"));
                  tops.add(top);
                }


                Collections.sort(tops, new IdComparator());

                int user = 5784;
                System.out.println("Usuario:"+user);
                FileInputStream is = new FileInputStream("distSeeds");
                BufferedInputStream bis = new BufferedInputStream(is);
                long dif = ((user-1)*numSeeds);
                bis.skip(dif);
                byte vet1[] = new byte[numSeeds];
                bis.read(vet1);


                System.out.println("Resultados : " + hits.length);

                
                Date start = new Date();
                nr.alteraScore(vet1,tops);
                //nr.onTheFly(user,tops);
                Date end = new Date();
                System.out.println("Time: "+(end.getTime()-start.getTime())+"ms");

                // Iterate through the results:
                

                Collections.sort(tops, new ScoreComparator());
        
                for (int i = 0; i < Math.min(tops.size(),10); i++) {
                  //System.out.println(;
                  User u =  (User)tops.get(i);   
                  System.out.println(u.doc + " - " + u.score);  
                  System.out.println(i+ " - " +u.name +" / "+u.id);
                }
 
            }
        	
        }catch(Exception e){
            System.out.println(" caught a " + e.getClass() +
               "\n with message: " + e.getMessage());
        }
        
        ireader.close();
	}
}