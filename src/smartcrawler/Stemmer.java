package smartcrawler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
public class Stemmer {
    
    File locationPath;
    int numOfDocs;
    ArrayList valueList;
    Map <String, ArrayList> map;
    BufferedWriter bw;
    
    public Stemmer(File dir)
    {
        locationPath = dir;     
        numOfDocs = 0;
        valueList = new ArrayList();
        map = new HashMap<String, ArrayList>();
    }
    
    public void countWords()
    {        
        try
        {
            File fileList[] = locationPath.listFiles(new MyFilter());
            //PorterStemmer stem = new PorterStemmer();
            stemming(fileList);          
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    public void stemming(File[] fileList)
    {
        PorterStemmer s = new PorterStemmer();
        try
        {
           bw = new BufferedWriter(new FileWriter(locationPath + "\\stemmeddata.txt"));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        for (int i = 0; i < fileList.length; i++) 
        {
          try 
          {
            InputStream in = new FileInputStream(fileList[i]);
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
        try
          {
            bw.close();
          }
          catch(Exception e)
          {
              e.printStackTrace();
          }
        }    
}
