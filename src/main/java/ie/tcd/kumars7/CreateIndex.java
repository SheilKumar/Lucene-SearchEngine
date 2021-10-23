package ie.tcd.kumars7;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.Buffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CreateIndex {

    private static String CRAN_DOC_DIRECTORRY = "src/main/resources/cran/cran.all.1400";
    private static String INDEX_DIRECTORY = "src/main/resources/index/";


    public void generateIndex() throws IOException
    {
        // Use the standard analyzer to read text fields
        Analyzer analyzer = new StandardAnalyzer();
        //Store Directory on Disk
        Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        //set to open mode
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        //create the writer
        IndexWriter writer = new IndexWriter(directory, config);
        //Pass writer to indexFiles method
        indexFiles(writer);
    }

    public void indexFiles(IndexWriter indexWriter)
    {
        try {
            FileReader fileReader = new FileReader(CRAN_DOC_DIRECTORRY);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            //iterate till the end of the file
            String currLine;
            int id = 0;
            while ((currLine=bufferedReader.readLine())!=null) {
                String title = ""; String author = ""; String bibliography = ""; String content = "";
                id++;
                System.out.println("Indexing document: " + id);
                while(!currLine.contains(".T")) {
                    currLine = bufferedReader.readLine(); //skip lines till we come to the Title
                } // We're at .T keep going till  .A and add that to title
                while(!currLine.contains(".A")) {
                    currLine = bufferedReader.readLine();
                    if (currLine.contains(".A")) {
                        break;
                    }
                    title += currLine + " ";
                } //We're ar .A keep going till .B and add to author;
                while (!currLine.contains(".B")) {
                    currLine = bufferedReader.readLine();
                    if (currLine.contains(".B")) {
                        break;
                    }
                    author += currLine + " ";
                }
                while (!currLine.contains(".W")) {
                    currLine = bufferedReader.readLine();
                    if (currLine.contains(".W")) {
                        break;
                    }
                    bibliography += currLine + " ";
                }
                while (!currLine.contains(".I")) {
                    currLine = bufferedReader.readLine();
                    if (currLine.contains(".I")) {
                        break;
                    }
                    content += currLine + " ";
                }
                Document doc = createDocs(String.valueOf(id), title, author, bibliography, content);
                indexWriter.addDocument(doc);
            }
            indexWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.out.println("Finished Indexing Cranfield Dataset!");
        }
    }

    public Document createDocs(String id, String title, String author, String bibliography, String content)
    {
        Document doc = new Document();
        doc.add(new StringField("id", id, Field.Store.YES));
        doc.add(new TextField("title", title, Field.Store.YES));
        doc.add(new TextField("author", author, Field.Store.YES));
        doc.add(new TextField("bibliography", bibliography, Field.Store.YES));
        doc.add(new TextField("content", content, Field.Store.YES));

        return  doc;
    }
}
