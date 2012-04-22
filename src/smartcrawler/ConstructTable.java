package smartcrawler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

class MyFilter implements FileFilter 
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
public class ConstructTable {
    
    File locationPath;
    int numOfDocs;    
    BufferedWriter bw;
    
    public ConstructTable(File dir)
    {
        locationPath = dir;     
        numOfDocs = 0;
        
    }
    
    public TreeMap<String, Double[]> constructTable()
    {     
        
        try
        {                                  
            TfIdf tfidf = new TfIdf(locationPath);
            tfidf.documentBuilder();                       
            return tfidf.words;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return null;
        }        
    }
    
    public File stemming(File[] fileList)
    {
        PorterStemmer s = new PorterStemmer();
        File stemLocation = null;
        for (int i = 0; i < fileList.length; i++) 
        {
            numOfDocs += 1;
          try 
          {
            InputStream in = new FileInputStream(fileList[i]);
            stemLocation = new File(locationPath + "\\stemmedData");
            stemLocation.mkdir();
            bw = new BufferedWriter(new FileWriter(stemLocation + "\\data-" + i + ".txt"));
            byte[] buffer = new byte[1024];
            int bufferLen, offset, ch;
            bufferLen = in.read(buffer);
            offset = 0;
            s.reset();
            while(true) 
            {
              if (offset < bufferLen)
                ch = buffer[offset++];
              else {
                bufferLen = in.read(buffer);
                offset = 0;
                if (bufferLen < 0)
                  ch = -1;
                else
                  ch = buffer[offset++];
              }
              if (Character.isLetter((char) ch)) 
              {
                s.add(Character.toLowerCase((char) ch));
              }
              else 
              {
                 s.stem();
                 //System.out.print(s.toString());
                 bw.write(s.toString());
                 s.reset();
                 if (ch < 0)
                   break;
                 else {
                   //System.out.print((char) ch);
                     bw.write((char)ch);
                 }
               }
            }
            in.close();         
            bw.close();
          }
          catch (IOException e) 
          {
            //System.out.println("error reading " + fileList[i]);
              e.printStackTrace();
          }
          catch(Exception ex)
          {
              ex.printStackTrace();
          }          
        }    
        return stemLocation;
        }    
}
