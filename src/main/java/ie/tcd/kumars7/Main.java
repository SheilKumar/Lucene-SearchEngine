package ie.tcd.kumars7;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;

import java.io.IOException;


public class Main {
    public static void main(String[] args) throws IOException {

        if (args.length<3) {
            System.out.println("Please provide arguments for: " +
                    "\n     Analyzer " +
                    "\n     Scoring Method" +
                    "\n     Similarity Report FileName");
        }

        //Choices of Analyzers
        CharArraySet stopWordsSet = EnglishAnalyzer.getDefaultStopSet();
        //args[0]==0 --> EnglishAnalyzer, args[0]==1 --> StandardAnalyzer
        int arg0 = Integer.parseInt(args[0]);
        //-------------------------------
        Analyzer engAnalyzer  = new EnglishAnalyzer(stopWordsSet);
        Analyzer stdAnalyzer  = new StandardAnalyzer(stopWordsSet);
        Analyzer[] analyzersList = {engAnalyzer, stdAnalyzer};
        //-------------------------------
        System.out.println("Using Analyzer: " + analyzersList[arg0]);

        //Choices of Scoring Methods
        //args[1]==0 --> Vector Space Model, args[1]==1 --> BM25
        int arg1 = Integer.parseInt(args[1]);
        //-------------------------------
        Similarity vsmScore = new ClassicSimilarity();
        Similarity bm25Score = new BM25Similarity();
        Similarity[] similaritiesList = {vsmScore, bm25Score};
        //-------------------------------
        System.out.println("Using Scoring Method: " + similaritiesList[arg1]);

        //Generates Index with Cranfield Documents
        CreateIndex index = new CreateIndex();
        index.generateIndex(analyzersList[arg0]);

        //Create Queries, performs search, and makes results file.
        QuerySearcher querySearcher = new QuerySearcher(args[2]);
        querySearcher.searchQueries(analyzersList[arg0], similaritiesList[arg1]);
    }
}