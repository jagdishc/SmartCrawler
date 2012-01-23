package smartcrawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;


public class RemoveStopWords {
    
    Map<String, Boolean> stopwords;
    StringTokenizer tokens;
    
    public RemoveStopWords()
    {
        stopwords = new HashMap<String, Boolean>();
        File stoplist = new File("G:\\Java\\SmartCrawler\\src\\smartcrawler\\stopwords.txt");        
        String line;
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(stoplist));
            line = br.readLine();            
            while(line != null)
            {                
                stopwords.put(line, true);
                line = br.readLine();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }        
    }
    
    public String remove(String text)
    {
        tokens = new StringTokenizer(text, ":; \"\',.[]{}()!?-/");
        String word, newtext = "";
        while(tokens.hasMoreTokens())
        {
            word = tokens.nextToken();
            if(!stopwords.containsKey(word))
            {
                newtext += word + " ";
            }
        }
        System.out.println("After removing:" + newtext);
        return newtext;
    }
    
}
