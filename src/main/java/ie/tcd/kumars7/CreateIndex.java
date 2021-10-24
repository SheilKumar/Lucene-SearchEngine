package ie.tcd.kumars7;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

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
        IndexWriter indexWriter = new IndexWriter(directory, config);
        //Pass writer to indexFiles method which completes the index
        indexFiles(indexWriter);
    }

    public void indexFiles(IndexWriter indexWriter)
    {
        try {
            FileReader fileReader = new FileReader(CRAN_DOC_DIRECTORRY);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            //iterate till the end of the file
            String currLine = bufferedReader.readLine();
            int id = 0; //increases each iteration to keep track of document id

            while (currLine!=null) {
                StringBuilder title = new StringBuilder(); StringBuilder author = new StringBuilder(); StringBuilder bibliography = new StringBuilder(); StringBuilder content = new StringBuilder();
                id++; //increases every time we pass .I

                System.out.println("Indexing document: " + id);

                while(!currLine.contains(".T")) {
                    currLine = bufferedReader.readLine(); //skip lines till we come to the Title
                } // We're at .T keep going till  .A and add that to title

                while(!currLine.contains(".A")) {
                    currLine = bufferedReader.readLine();
                    if (currLine.contains(".A")) {
                        break;
                    }
                    title.append(currLine).append(" ");
                } //We're ar .A keep going till .B and add to author;

                while (!currLine.contains(".B")) {
                    currLine = bufferedReader.readLine();
                    if (currLine.contains(".B")) {
                        break;
                    }
                    author.append(currLine).append(" ");
                } //We're at .B keep going till .W and add to bibliography

                while (!currLine.contains(".W")) {
                    currLine = bufferedReader.readLine();
                    if (currLine.contains(".W")) {
                        break;
                    }
                    bibliography.append(currLine).append(" ");
                } //We're at .W keep going till next .I and add to content

                while (!currLine.contains(".I")) {
                    currLine = bufferedReader.readLine();
                    if (currLine==null || currLine.contains(".I")) {
                        break;
                    }
                    content.append(currLine).append(" ");
                }

                //Use createDoc method to assign to lucene doc and use writer to write to index
                Document doc = createDocs(String.valueOf(id), title.toString(), author.toString(), bibliography.toString(), content.toString());
                indexWriter.addDocument(doc);
                System.out.println(doc);
            }

            bufferedReader.close();
            indexWriter.close(); //close IndexWriter
        } catch (IOException e) {
            e.printStackTrace();
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
