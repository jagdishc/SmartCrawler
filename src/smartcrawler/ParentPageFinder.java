package smartcrawler;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import java.util.StringTokenizer;
import java.util.TreeMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ParentPageFinder extends Thread
{
    
    Map<String, String[]> parentPages;
    Map<String, Double> averageParentPage;
    Map<String, Double> wt;
    public static final String link = "http://www.seoprofiler.com/analyze/";
      
    
    public ParentPageFinder()
    {
        
    }
    
    public ParentPageFinder(TreeMap<String, Double> weight) 
    {
        
        wt = weight;
        parentPages = new HashMap<String, String[]>();        
        averageParentPage = new HashMap<String, Double>();
       
    }
    
    public void doScrape(String url)
    {
        Document doc;
        String webPage = link+url+"?pid=#backlinks";
        int i = 0;
        String[] parents = new String[2];
        
        try
        {            
            doc = Jsoup.connect(webPage).get();
            Element ele = doc.getElementById("backlinks");
            Elements divs = ele.getElementsByClass("nobr"), urls;
            for(Element links : divs)
            {
                if(i>1)
                {
                    break;
                }
                urls = links.select("a");
                for(Element x : urls)
                {
                    parents[i] = x.absUrl("href");
                    i += 1;
                }

            }
            if(!(parentPages.containsKey(url)))
            {
                parentPages.put(url, parents);                
            }           

            getParentPageRelevancy(url, parents);
                
             
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void getParentPageRelevancy(String url, String[] parents)
    {
        Double relevanceValue = 0.0;
        String text, word;
        int i;
        StringTokenizer tokens;
        Document doc;
        RemoveStopWords rm = new RemoveStopWords();
        try
        {
            for(i=0; i<parents.length; i++)
            {
                try
                {
                doc = Jsoup.connect(parents[i]).get();                
                text = doc.text();
                text = rm.remove(text);
                tokens = new StringTokenizer(text, ":; \"\',.[]{}()!?-/");
                while(tokens.hasMoreTokens())
                {
                    word = tokens.nextToken();
                    if(wt.containsKey(word))
                    {
                        relevanceValue += wt.get(word);
                    }
                }
                }
                catch(Exception e)
                {
                    continue;
                }
            }
            relevanceValue = relevanceValue/(i-1);  
            averageParentPage.put(url, relevanceValue);
            
        }
        catch(Exception e)
        {
            System.out.println("Exception: " + parents[0] + "\n" + parents[1] + "\n" + parents[2] + "\n" + parents[3] + "\n" + parents[4]);
            e.printStackTrace();
        }
        
    }
    
    public void run()
    {
        //doScrape();
    }
}
