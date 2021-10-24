package ie.tcd.kumars7;

import org.apache.lucene.analysis.Analyzer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
//        CreateIndex index = new CreateIndex();
//        index.generateIndex();
        QuerySearcher querySearcher = new QuerySearcher();
        querySearcher.readQueries();
    }
}