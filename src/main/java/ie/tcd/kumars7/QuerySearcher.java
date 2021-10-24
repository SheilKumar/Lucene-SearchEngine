package ie.tcd.kumars7;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.AnalyzerWrapper;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import javax.print.DocFlavor;
import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;

public class QuerySearcher {

    private String QRY_DIR =    "src/main/resources/cran/cran.qry";
    private String INDEX_DIR =  "src/main/resources/index/";
    //change results string based on Analyzer and Scoring Method
    //format: results_{Analyzer}_{Scoring Method}.txt
    private String RESULT_DIR = "src/main/resources/results/results-Standard-VSM.txt";

    public void searchQueries() throws IOException
    {

        // Set up directories and readers
        Directory directory = FSDirectory.open(Paths.get(INDEX_DIR));
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        PrintWriter printWriter = new PrintWriter(RESULT_DIR);

        //Choose Analyzer;
        CharArraySet stopWordsSet = EnglishAnalyzer.getDefaultStopSet();
        Analyzer analyzer = new StandardAnalyzer(stopWordsSet);

        //Choose Scoring Method

        //VectorSpaceModel
        indexSearcher.setSimilarity(new ClassicSimilarity());

        //BM25
//        indexSearcher.setSimilarity(new BM25Similarity());

        //Define MultiField Parser
        MultiFieldQueryParser multiFieldQueryParser =
                new MultiFieldQueryParser(new String[]
                        {"title",
                         "author",
                         "bibliography",
                         "content"}, analyzer);

        //read the queries as strings
        ArrayList<String> queriesAsStrings = readQueries();

        //parse the queries to Query
        ArrayList<Query> queryArrayList = parseQueries(multiFieldQueryParser,
                queriesAsStrings);

        //loop through the elements in queryArrayList perform the search
        int queryID = 0;
        for (Query currQuery : queryArrayList) {
            queryID++;
            generateSearch(indexSearcher, currQuery, queryID, printWriter);
        }
        indexReader.close();
        printWriter.close();

    }

    public ArrayList<String> readQueries() throws IOException
    {
        System.out.println("Reading Queries ...");
        FileReader fileReader = new FileReader(QRY_DIR);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        ArrayList<String> queries = new ArrayList<>();
        //iterate till the end of the file
        String currLine = bufferedReader.readLine();
        int id = 0;

        while (currLine != null) {
            StringBuilder currQuery = new StringBuilder();
            id++;

            while (!currLine.contains(".W")) {
                currLine = bufferedReader.readLine(); //skip till we begin reading the actual query
            } //We're at .W keep going till next .I and add to currQuery

            while (!currLine.contains(".I")) {
                currLine = bufferedReader.readLine();
                if (currLine == null || currLine.contains(".I")) {
                    break;
                }
                currQuery.append(currLine).append(" ");
            }
            System.out.println("Query Id: " + id);
            System.out.println(currQuery);
            // add Queries to ArrayList
            queries.add(String.valueOf(currQuery));
        }
        return queries;
    }

    public ArrayList<Query> parseQueries(MultiFieldQueryParser parser,
                                         ArrayList<String> queryList)
    {
        System.out.println("Parsing Queries ...");
        ArrayList<Query> queryArrayList = new ArrayList<>();
        for (int i=0; i<queryList.size(); i++) {
            try {
                Query currQuery = parser.parse(QueryParser.escape(queryList.get(i)));
                queryArrayList.add(currQuery);
                System.out.println(currQuery);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return queryArrayList;
    }

    public void generateSearch(IndexSearcher indexSearcher,
                               Query currQuery,
                               Integer queryID,
                               PrintWriter printWriter) throws IOException
    {
        //generate results for each query, ranking all 1400 docs
        TopDocs result = indexSearcher.search(currQuery, 1400);
        ScoreDoc[] hits = result.scoreDocs;
        // write to file with required trec_eval format
        //query-id Q0 document-id rank score STANDARD
        for (int i=0; i<hits.length; i++) {
            Document doc = indexSearcher.doc(hits[i].doc);
            printWriter.println(queryID + " Q0 " + doc.get("id") +  " " +
                    i + " " + hits[i].score + " STANDARD");
        }
    }

}
