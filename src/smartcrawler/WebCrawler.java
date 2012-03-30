package smartcrawler;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class WebCrawler 
{
	
    class Counter 
    {
        public int count;
        Counter()
        {
            count =0;
        }
        
    }
    String query;
    HashMap<String,Object> seedURLs = new HashMap<String,Object>();
    //This can be maximum to 8 - ajax.gogle api restriction i guess.
    String seedURLLimit = "5";
    String seedURL;
    WebCrawlHistory wch;
    float crawlCount = 0;
    
    WebCrawler(String q)
    {
        //this.query=URLEncoder.encode(q);
        seedURL = q;
        wch = new WebCrawlHistory();
        wch.crawlHistory.setEditable(false);
        wch.crawlHistory.setText("");
        wch.setVisible(true);
        String val = crawl();                
    }
    
    public String crawl()
    {
        try
        {
            int threadCount = 5;            
            LinkCrawler_MultipleThreads[] lc = new LinkCrawler_MultipleThreads[threadCount];
            Counter ob = new Counter();
            //url is seedurl
            Vector<String> ar = getAllData(seedURL);
            for(int i=0;i<ar.size();i++)
            {                
                String tempURL = ar.elementAt(i);
                if(i<threadCount)
                {                    
                    synchronized(seedURLs)
                    {
                        if(!seedURLs.containsKey(tempURL))
                        {
                            lc[i]= new LinkCrawler_MultipleThreads(seedURLs,tempURL,ob,wch);
                            seedURLs.put(tempURL,null);
                        }
                    }
                }
                else
                {
                    synchronized(seedURLs)
                    {
                        if(!seedURLs.containsKey(tempURL))
                        {                            
                            seedURLs.put(tempURL,null);
                        }
                    }
                }
            }
            
            for(int i=0;i<threadCount;i++)
            {
                System.out.println("INFO : Starting Thread " + i);
                new Thread(lc[i]).start();
            }
            Integer val = ob.count;
            return val.toString();
        }
        catch(Exception ex)
        {
            System.out.println(ex.toString());
        }
        return "";
    }
    
    public Vector<String> getAllData(String url_string)
    {
        Vector<String> result = new Vector<String>();
        int maxLimit = 75;
        try
        {
            
            Document doc = Jsoup.connect(url_string).get();
            Elements links = doc.select("a[href]");
            for(Element link:doc.select("a[href]"))
            {
                if(result.size()==maxLimit)
                    break;
                result.add(link.attr("abs:href"));
            }
        }
        catch(Exception ex)
        {
            System.out.println(ex.toString());
        }
        
        return result;
    }
    
    public static void main(String[] args)
    {
        try 
        {
            String query = "Python";
            WebCrawler ob = new WebCrawler(query);            
        } 
        catch (Exception ex) 
        {
            System.out.println(ex.toString());
        }
    }
}


