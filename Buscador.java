import java.util.Scanner;
import java.io.IOException;

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

public class Buscador
{
	public static void main(String[] args) throws IOException
	{

		Analyzer analyzer = new StandardAnalyzer();
		// Now search the index:
        //DirectoryReader ireader = DirectoryReader.open(directory);
        IndexReader ireader = DirectoryReader.open(FSDirectory.open(Paths.get("index")));
        IndexSearcher isearcher = new IndexSearcher(ireader);
        // Parse a simple query that searches for "text":
        QueryParser parser = new QueryParser("name", analyzer);
        
        try{
        	System.out.println("Search : ");
        	Scanner in = new Scanner(System.in);
        	String s = in.nextLine();
            Query query = parser.parse(s);
            ScoreDoc[] hits = isearcher.search(query, 10).scoreDocs;

            System.out.println("Resultados : " + hits.length);

            // Iterate through the results:
            for (int i = 0; i < hits.length; i++) {
              Document hitDoc = isearcher.doc(hits[i].doc);
              System.out.println(i+ " - " +hitDoc.get("name") +" / "+hitDoc.get("id"));
            }
        }catch(Exception e){
            System.out.println(" caught a " + e.getClass() +
               "\n with message: " + e.getMessage());
        }
        
        
        ireader.close();
	}
}