#Lucene-SearchEngine 

## Installation 

1. Clone this Repository
```
git clone https://github.com/SheilKumar/Lucene-SearchEngine.git
```

2. Navigate to the repository 

```
mvn package
``` 
3. Run .jar file with desired arguments 

```
java -jar target/lucene-information-retrieval-1.0.jar arg0 arg1 arg2
```

## Arguments 

#### Choice of Analyzer  

`arg0` decides the analyzer used for indexing and searching the queries. You have a choice between the `EnglishAnalyzer()` and the `StandardAnalyzer()` both analyzers will use a `CharArraySet stopWordsSet` from the `EnglishAnalyzer()` 
```
CharArraySet stopWordsSet = EnglishAnalyzer.getDefaultStopSet()
```

```
arg0  = {
         0: EnglishAnalyzer(), 
         1: StandardAnalyzer() }
```

#### Choice of Scoring Method 

`arg1` decides the scoring method used for generating the similarity report. You have a choice between the *Vector Space Model* `ClassicSimilarity()` and the *BM25 Model* `BM25Similarity()`

```
arg1 = {
        0: ClassicSimilarity(), 
        1: BM25Similarity()     }
```

#### Results/Similarity Report File Name

`arg2` decides the name of the file generated dby your choice of analyzer and scoring method. Reccomended naming format: 
```
arg3 : results-{Analyzer}-{ScoringMethod}.txt
```

So if one were to choose the `StandardAnalyzer()` with the *Vector Space Model*, they would use the command 
```
java -jar target/lucene-information-retrieval-1.0.jar 1 0 results-Standard-VSM.txt 
```
