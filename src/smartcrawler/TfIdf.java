package smartcrawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class TextFilter implements FileFilter
{
    public boolean accept(File file) 
        {
            String filename = file.getName();
            return filename.endsWith(".txt");
        }
        public String getDescription() 
        {
            return "*.txt";
        }
}

class Document
{
     public TreeMap<String, Double[]> words;
     public int sum_of_words;
     double vectorlength;
     
     public Document(BufferedReader br, TfIdf parent)
     {
         String line;
         sum_of_words = 0;
         vectorlength = 0;
         String word;
         StringTokenizer tokens;
         Double[] tempdata;
         words = new TreeMap<String, Double[]>();
         try
         {            
            line = br.readLine();
            while (line != null) 
            {
                tokens = new StringTokenizer(line, ":; \"\',.[]{}()!?-/");
                while(tokens.hasMoreTokens()) 
                {
                    word = tokens.nextToken().toLowerCase();
                    word.trim();
                    if (word.length() < 2) continue;
                    if (words.get(word) == null) {
                            tempdata = new Double[]{1.0,0.0,0.0};
                            words.put(word, tempdata);
                    }
                    else {
                            tempdata = words.get(word);
                            tempdata[0]++;
                            words.put(word,tempdata);
                    }
                    sum_of_words += 1;
                }
                line = br.readLine();
            }
            for (Iterator<String> it = words.keySet().iterator(); it.hasNext(); ) 
            {
                    word = it.next();
                    tempdata = words.get(word);
                    tempdata[1] = tempdata[0] / (float) sum_of_words;
                    words.put(word,tempdata);
                    parent.addWordOccurence(word);
            }  
         }
         catch(Exception e)
         {
             e.printStackTrace();
         }
     }
     public void calculateTfIdf(TfIdf parent)
     {
        String word;
        Double[] corpusdata;
        Double[] worddata;
        double tfidf;
        for (Iterator<String> it = words.keySet().iterator(); it.hasNext(); ) 
        {
            word = it.next();
            corpusdata= parent.corpus.get(word);
            worddata = words.get(word);
            tfidf = worddata[1] * corpusdata[1];
            worddata[2] = tfidf;
            vectorlength += tfidf * tfidf;
            words.put(word, worddata);
            System.out.println(word + " = " + worddata[0] + ", " + worddata[1] + ", " + worddata[2]);
        }
        vectorlength = Math.sqrt(vectorlength);
     }
}

public class TfIdf {
    
    File locationDir;    
    Map<String, Double[]> corpus;    
    Map<String, Document> documents;    
    static int totalDocs,numOfDocs;
    
    public TfIdf(File stemLocation)
    {
        locationDir = stemLocation;
        corpus = new TreeMap<String, Double[]>();    
        documents = new TreeMap<String,Document>();
        numOfDocs = 0;
        totalDocs = 0;
    }
    public void documentBuilder()
    {
        File[] files = locationDir.listFiles(new TextFilter()); 
        for(int i=0;i<files.length;i++)
        {
            numOfDocs += 1;
            countWords(files[i]);
        }
        totalDocs = files.length;
        //System.out.println("Word Count: " + corpus.toString());    
        score();
    }
    private void countWords(File file)
    {
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(file));                   
            Document doc = new Document(br, this);
            documents.put(file.getName(), doc);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public void addWordOccurence(String word)
    {
        Double[] tempdata;
                if (corpus.get(word) == null) 
                {
                    tempdata = new Double[]{1.0,0.0};
                    corpus.put(word, tempdata);
                } 
                else 
                {
                    tempdata = corpus.get(word);
                    tempdata[0]++;
                    corpus.put(word,tempdata);                        
                }            
    }
    private void score()
    {
        String word;
        for (Iterator<String> it = documents.keySet().iterator(); it.hasNext(); ) 
        {
            word = it.next();
            documents.get(word).calculateTfIdf(this);
        }
    }    
}
