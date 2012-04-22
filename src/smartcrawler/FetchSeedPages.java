package smartcrawler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import java.sql.DriverManager;
import java.sql.Connection;


import java.sql.Statement;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


public class FetchSeedPages extends Thread 
{    
    Progress fp;
    File dirLocation;
    String url = "http://ajax.googleapis.com/ajax/services/search/web?gl=en&userip=&hl=en&v=1.0&q=";
    String query, seedUrlLimit = "5";
    String kword;
    SmartCrawlerView smc;
    Object lock;
    Connection conn = null;
    
    public FetchSeedPages(String keyword, String location, SmartCrawlerView smc, Object lock)
    {        
        kword = keyword;
        try
        {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/smart_crawler", "root", "");
            query = URLEncoder.encode(keyword, "UTF-8");
        }
        catch(Exception e)
        {
            query = keyword;
            e.printStackTrace();
        }
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
            URL u = new URL(url+query+"&start=0&rsz="+seedUrlLimit);
            BufferedReader br = new BufferedReader(new InputStreamReader(u.openStream()));
            int ch;
            String result="";
            while((ch=br.read())!=-1)
                result=result+(char)ch;
            JSONObject jsonData = new JSONObject(result);
            jsonData = jsonData.getJSONObject("responseData");
            JSONArray jsonArray = jsonData.getJSONArray("results");
            Elements ele;
            List urls = new ArrayList();            
            for(int i=0;i<jsonArray.length();i++)
            {                
                JSONObject temp = jsonArray.getJSONObject(i);
                String tempURL = temp.getString("unescapedUrl");
                urls.add(tempURL);
                System.out.println(tempURL);
                Statement s = conn.createStatement();
                s.executeUpdate("insert into seed_url (keyword, url) values('" + kword + "', '" + tempURL + "')");                                
            }
            conn.close();
            String text;
            Document doc;
            BufferedWriter bw;
            int i = 0;  
            for(Iterator it = urls.iterator(); it.hasNext();)
            {                             
                doc = Jsoup.connect(it.next().toString()).get();
                text = doc.text();            
                System.out.println("page: " + i);
                int pg = i+1;
                smc.infoLabel.setText("processing page: " + pg);   
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
            smc.infoLabel.setText("Topics weight table constructed.");   
            synchronized(lock)
            {
                lock.wait();
            }
            SmartCrawler sc = new SmartCrawler(dirLocation, weights, smc);
            sc.addSeedUrls(urls);
            sc.start();            
        }
        catch(Exception ex)
        {
            System.out.println(ex.toString());
        }
        
    }
    
}
