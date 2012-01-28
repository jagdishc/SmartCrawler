package smartcrawler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class FetchSeedPages extends Thread {
    
    FetchProgress fp;
    File urlFile,dirLocation;
    
    
    public FetchSeedPages(String file, String location)
    {
        urlFile = new File(file);
        dirLocation = new File(location);        
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
            while((url = br.readLine()) != null)
            {   
                urls.add(url);
                doc = Jsoup.connect(url).get();
                text = doc.text();
                ele = doc.select("title");
                name = ele.text();
                System.out.println(name.trim());
                bw = new BufferedWriter(new FileWriter(dirLocation + "\\" + name + ".txt"));           
                text = text.toLowerCase();               
                RemoveStopWords remove = new RemoveStopWords();
                text = remove.remove(text);
                bw.write(text);
                bw.close();               
            }
            ConstructTable constructor = new ConstructTable(dirLocation);
            TreeMap<String, Double[]> weights = constructor.constructTable();            
            SmartCrawler sc = new SmartCrawler(dirLocation, weights);
            sc.addSeedUrls(urls);
            sc.start();
            System.out.println("Finished");
        }
        catch(Exception ex)
        {
            System.out.println(ex.toString());
        }
        
    }
    
}
