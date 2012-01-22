package smartcrawler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
            String name = urlFile.getName();            
            String text, url;
            Document doc;
            BufferedWriter bw;
            while((url = br.readLine()) != null)
            {          
                doc = Jsoup.connect(url).get();
                text = doc.text();
                ele = doc.select("title");
                name = ele.text();
                System.out.println(name.trim());
                bw = new BufferedWriter(new FileWriter(dirLocation + "\\" + name + ".txt"));                
                bw.write(text);
                bw.close();               
            }
            ConstructTable constructor = new ConstructTable(dirLocation);
            constructor.constructTable();
            System.out.println("Finished");
        }
        catch(Exception ex)
        {
            System.out.println(ex.toString());
        }
        
    }
    
}
