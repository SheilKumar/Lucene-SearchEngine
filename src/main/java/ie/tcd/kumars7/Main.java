package ie.tcd.kumars7;

import org.apache.lucene.analysis.Analyzer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        //Generates Index with CranfieldDocuments
        CreateIndex index = new CreateIndex();
        index.generateIndex();

        //Create Queries, performs search, and makes results file.
        QuerySearcher querySearcher = new QuerySearcher();
        querySearcher.searchQueries();
    }
}