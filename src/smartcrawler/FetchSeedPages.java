package smartcrawler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


public class FetchSeedPages extends Thread 
{
    
    Progress fp;
    File urlFile,dirLocation;
    SmartCrawlerView smc;
    Object lock;
    
    public FetchSeedPages(String file, String location, SmartCrawlerView smc, Object lock)
    {
        urlFile = new File(file);
        dirLocation = new File(location);        
        this.smc = smc;
        this.lock = lock;
    }
    
    public void run()
    {
        fetch();
    }
    
    public void fetch()
    {
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(urlFile));
            Elements ele;
            List urls = new ArrayList();
            String name = urlFile.getName();            
            String text, url;
            Document doc;
            BufferedWriter bw;
            int i = 0;
            while((url = br.readLine()) != null)
            {   
                urls.add(url);
                doc = Jsoup.connect(url).get();
                text = doc.text();
                //ele = doc.select("title");
                //name = ele.text().substring(0, 8);
                System.out.println("page: " + i);
                bw = new BufferedWriter(new FileWriter(dirLocation + "\\data-" + i + ".txt"));     
                i += 1;
                text = text.toLowerCase();               
                RemoveStopWords remove = new RemoveStopWords();
                text = remove.remove(text);
                bw.write(text);
                bw.close();               
            }
            smc.infoLabel.setText("Seed pages are fetched.");           
            ConstructTable constructor = new ConstructTable(dirLocation);
            TreeMap<String, Double[]> weights = constructor.constructTable();         
            smc.infoLabel.setText("Topics weight table contructed.");   
            synchronized(lock)
            {
                lock.wait();
            }
            SmartCrawler sc = new SmartCrawler(dirLocation, weights, smc);
            sc.addSeedUrls(urls);
            sc.start();
            //System.out.println("Finished");
        }
        catch(Exception ex)
        {
            System.out.println(ex.toString());
        }
        
    }
    
}
