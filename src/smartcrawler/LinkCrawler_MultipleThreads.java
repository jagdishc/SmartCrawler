package smartcrawler;

import smartcrawler.WebCrawler.Counter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class LinkCrawler_MultipleThreads implements Runnable
{
    private HashMap<String, Object> urls;
    Vector<String> urlList= new Vector<String>();
    int crawlDepth = 15;
    int maxURLCount = 15;
    WebCrawlHistory wch;
    HashMap<String,Object> filter = new HashMap<String, Object>();
    Counter ob;
    
    LinkCrawler_MultipleThreads(HashMap<String, Object> seedURLs,String url,Counter ob,WebCrawlHistory temp) 
    { //can be made to read from file too
        wch = temp;
        urls = seedURLs;
        urlList.add(url);
        this.ob = ob;        
        filter.put("text/html; charset=utf-8",null);
        filter.put("text/html;",null);
    }
    
    public void run()
    {
        populateURL();
    }
    
    public boolean isAllowedType(String url)
    {        
        return true;
    }
    
    public String getType(String u) throws IOException
    {
        try 
        {
            URL url = new URL(u);
            String type = url.openConnection().getContentType();
            if(type!=null)
                return type;
            return "";
        } 
        catch (MalformedURLException ex) 
        {
            System.out.println("Malformed URL");
        }
        return "";
    }
    
    String normalize_url(String s1)
    {        
        try
        {
         String s=java.net.URLDecoder.decode(s1);
         URL ip=new URL(s);
         String scheme=ip.getProtocol();
         String authority=ip.getAuthority();
         String path=ip.getPath();
         String query=ip.getQuery();
         String fragment=ip.getRef();
         int port=ip.getPort();
         URI ipi=new URI(scheme,authority,path,query,fragment);
         ipi=ipi.normalize();
         scheme=ipi.getScheme();
         authority=ipi.getAuthority();
         path=ipi.getPath();
         query=ipi.getQuery();
         fragment=ipi.getFragment();
         scheme=scheme.toLowerCase();
         authority=authority.toLowerCase();
        if(path==null || path.length()==0)
            path="/";
        else
        {
             if(path.charAt(path.length()-1)!='/')
             {
                String [] tmp=path.split("/");
                String h=tmp[tmp.length-1];
                if(!(h.contains(".htm") || h.contains(".html") || h.contains(".php") || h.contains(".jsp") || h.contains(".asp") || h.contains(".aspx")))
                path+="/";
             }

        }
             String op="";
             op+=scheme+"://"+authority+path;
             if(query!=null)
             op+="?"+query;
             return op;
        }
        catch(Exception e)
        {
            return s1;
        }
    }
    
    public void populateURL()
    {
        String c_url;
        int i;
        //Storing the urls parsed in this vector.. stop when length >=crawlDepth*maxURLCount
        Iterator it;
        //URL of string has been populated initially..
        //Set initial crawled count to URLs crawled via the google API
        synchronized(ob)
        {
            ob.count+= urlList.size();
        }
        int maxCrawlCount = crawlDepth * maxURLCount;
        it = urlList.iterator();
        int index=0;
        while(index<urlList.size())
        {
            synchronized(this)
            {
                c_url=urlList.elementAt(index++);
            }
            if(c_url.length()==0)
            {
                continue;
            }
            System.out.println("URL "+c_url );
            synchronized(wch)
            {
                wch.crawlHistory.append(c_url + "\n");
                Integer val = ob.count;
                wch.total.setText("Total crawled: " + val.toString());
            }
//            if(c_url.equals("http://docs.python.org/py-modindex.html"))
//            {
//                int c = index-1;
//                System.out.println("Index :" + c);
//            }
            
            String type ="";
            
                if(ob.count<maxCrawlCount)
                {
                    try
                    {
                    //Can crawl the current URL and get data & links too
                        //type = getType(c_url);
                        if(isAllowedType(type))
                        {
                            if(!type.equals("application/pdf"))
                            {
                                Document doc = Jsoup.connect(c_url).get();
                                Elements links = doc.select("a[href]");
                                //set depth to the minimum of the links size and the crawl depth
                                int depth = crawlDepth>links.size()?links.size():crawlDepth;
                                for(i=0;i<depth;i++)
                                {
                                    Element link_el = links.get(i);
                                    String parsed_url = link_el.attr("abs:href");
                                    synchronized(urls)
                                    {
                                        
                                        if(!urls.containsKey(parsed_url))
                                        {                                           
                                            urls.put(parsed_url,null);
                                            ob.count++;
                                            synchronized(urlList)
                                            {
                                                urlList.add(parsed_url);
                                            }
                                        }
                                    }
                                }
                            }
                            else
                            {
                                
                            }
                        }
                        else
                        {
                            
                        }
                    }
                    catch(Exception ex)
                    {
                        System.out.println("Error in parsing the document with URL : " + c_url +" "+ex.toString()+ " hello " +c_url.length());
                        //break;
                    }

                }
                else
                {
                    //All crawling of URL is done..Just get the content.. Just can retrive data and not get URLs anymore                    
                    System.out.println("Limit Reached..Just fetch data from "+c_url);
                    break;
                }            
        }        
    }
    
}

