package smartcrawler;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SmartCrawler extends Thread
{
    Vector vectorToSearch;
    Vector vectorSearched;    
    Vector vectorUrlsDiscarded;
    HashMap<String, double[]> url_div;
    TreeMap<String, Double> weight;
    int size = 1, limit = 15;
    File loc;
    RelevanceCalculator rc;
    final static Object syncObj = new Object();
    SmartCrawlerView smc;
    
    public SmartCrawler(File location, TreeMap<String, Double[]> twt, SmartCrawlerView smc)
    {       
        loc = location;
        url_div = new HashMap<String, double[]>();
        vectorToSearch = new Vector();
        vectorSearched = new Vector();    
        vectorUrlsDiscarded = new Vector();
        this.smc = smc;
        rc = new RelevanceCalculator(loc);
        weight = new TreeMap<String, Double>();
        try
        {
            for(Iterator<String> iter = twt.keySet().iterator(); iter.hasNext();)
            {
                String t = iter.next();
                weight.put(t, twt.get(t)[1]);
            }
            size = weight.size();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
            
    }
    
    public void addSeedUrls(List urls)
    {
        for(Iterator<String> it = urls.iterator();it.hasNext();)
        {
            vectorToSearch.add(it.next());
        }       
    }
    
    public void crawl()
    {
        Document doc;
        Elements anchorTags, parents;
        String anchor_txt;      
        int linkcount = 0, limitcount = 5;
        try
        {
            //for(Iterator<String> it = vectorToSearch.iterator();it.hasNext();)
            while(vectorToSearch.size()>0)
            {
                linkcount = 0;
                String u = vectorToSearch.elementAt(0).toString();
                doc = Jsoup.connect(u).get();
                smc.infoLabel.setText("Crawling: " + u);
                vectorSearched.add(vectorToSearch.remove(0));
                anchorTags = doc.select("a");
                parents = anchorTags.parents().select("div");
                double[] temp = new double[2];
                for(Element links:anchorTags)
                {
                    linkcount += 1;
                    if(linkcount < limitcount)
                    {
                        String absUrl = links.absUrl("href");
                        anchor_txt = links.text();
                        if(vectorToSearch.contains(absUrl)==false && vectorSearched.contains(absUrl)==false && url_div.containsKey(absUrl)==false && vectorUrlsDiscarded.contains(absUrl)==false && absUrl.equals("") == false)
                        {
                            String txt = parents.text();
                            int count = 0, acount = 0;
                            for(Iterator<String> it2 = weight.keySet().iterator(); it2.hasNext();)
                            {
                                it2.next();
                                if(txt.contains(it2.toString()))
                                {
                                    count += 1;
                                }
                                if(anchor_txt.contains(it2.toString()))
                                {
                                    acount += 1;
                                }
                            }
                            temp[0] = count/size;
                            temp[1] = acount/size;
                            url_div.put(absUrl, temp);                       
                        }                    
                    }                   
                }
            }
            smc.infoLabel.setText("Calculating relevancy for extracted links");
            checkRelevancy();
            Integer val1 = rc.accepted;
            Integer val2 = rc.total_urls;
            rc.progress.progressInfo.append(val1.toString() + " are accepted out of " + val2.toString());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void checkRelevancy()
    {       
       for(Iterator<String> iter = url_div.keySet().iterator();iter.hasNext();)
       {
           String u = iter.next(), result;
           double[] div = url_div.get(u);   
           if(vectorToSearch.contains(u) == false && vectorUrlsDiscarded.contains(u) == false)
           {
               result = rc.findRelevancy(u, weight,div);
               if(result.equals("yes"))
               {
                   //System.out.println("Accepted: " + u);
                   vectorToSearch.add(u);
               }
               else
               {
                   //System.out.println("Discarded: " + u);
                   vectorUrlsDiscarded.add(u);
               }
           }
       }
    }
    
    public void run()
    {
        while(vectorSearched.size() <= limit)
        {
            crawl();
        }
        System.out.println("Finished");
    }
    
}
