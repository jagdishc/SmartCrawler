package smartcrawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;

class values
{
    String url,ppr, atr, urtr,relevant;
    public values(String v0, String v1, String v2, String v3, String v4)
    {
        url = v0;
        ppr = v1;
        urtr = v2;
        atr = v3;
        relevant = v4;
    }
}

public class RelevanceCalculator extends Thread
{
    File loc;
    TreeMap<String, Double> wt;   
    int total = 0, no_of_yes = 0, no_of_no = 0;
    float p_of_yes, p_of_no;
    Vector valueVector;
    values value;
    String link;
    double[] div_count;
    Vector vectorToSearch, vectorUrlsDiscarded;
    
    public RelevanceCalculator(File location, String urls, double[] div_counts, TreeMap<String, Double> weight, Vector x, Vector y)
    {
        loc = location;
        link = urls;
        div_count = div_counts;
        wt = weight;
        String url,text;
        StringTokenizer tokens;
        String[] data = new String[4];
        valueVector = new Vector();       
        vectorToSearch = x;
        vectorUrlsDiscarded = y;
        
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(loc+"\\training_set.txt"));
            while((text = br.readLine())!=null)
            {
                total += 1;
                tokens = new StringTokenizer(text, ",");
                url = tokens.nextToken();
                data[0] = tokens.nextToken();
                data[1] = tokens.nextToken();
                data[2] = tokens.nextToken();
                data[3] = tokens.nextToken();
                
                if(data[3].equalsIgnoreCase("yes"))
                {
                    no_of_yes += 1;
                }
                else
                {
                    no_of_no += 1;
                }
                
                p_of_yes = (float)no_of_yes/total;
                p_of_no = (float)no_of_no/total;
                
                value = new values(url, data[0],data[1],data[2],data[3]);
                valueVector.add(value);
                //trainer.put(url, data);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public String findRelevancy()
    {
        Double pRelevanceValue;
        ParentPageFinder ppf = new ParentPageFinder(wt);
        ppf.doScrape(link);
        pRelevanceValue = ppf.averageParentPage.get(link);
        String result = calculateProbs(link, pRelevanceValue, div_count[0], div_count[1]);
        if(result.equals("yes") && vectorToSearch.contains(link) == false && vectorUrlsDiscarded.contains(link) == false)
        {
           //System.out.println("Accepted: " + u);
           vectorToSearch.add(link);
        }
        else
        {
           //System.out.println("Discarded: " + u);
           vectorUrlsDiscarded.add(link);
        }
        return result;
    }
    
    public float[] getCount(String attr, String class_value)
    {
        int yes_count = 0, no_count = 0;
        float[] tmp = new float[2];
        if(attr.equals("parent"))
        {
            for(Iterator<values> iter = valueVector.iterator();iter.hasNext();)
            {
                values temp = iter.next();
                if(temp.relevant.equals("yes") && temp.ppr.equals(class_value))
                {
                    yes_count += 1;
                }
                else if(temp.relevant.equals("no") && temp.ppr.equals(class_value))
                {
                    no_count += 1;
                }
            }            
            tmp[0] = (float) yes_count/no_of_yes;
            tmp[1] = (float) no_count/no_of_no;
            return tmp;
        }
        else if(attr.equals("div"))
        {
            for(Iterator<values> iter = valueVector.iterator();iter.hasNext();)
            {
                values temp = iter.next();
                if(temp.relevant.equals("yes") && temp.urtr.equals(class_value))
                {
                    yes_count += 1;
                }
                else if(temp.relevant.equals("no") && temp.urtr.equals(class_value))
                {
                    no_count += 1;
                }
            }   
            tmp[0] = (float) yes_count/no_of_yes;
            tmp[1] = (float) no_count/no_of_no;
            return tmp;
        }
        else if(attr.equals("anchor"))
        {
            for(Iterator<values> iter = valueVector.iterator();iter.hasNext();)
            {
                values temp = iter.next();
                if(temp.relevant.equals("yes") && temp.atr.equals(class_value))
                {
                    yes_count += 1;
                }
                else if(temp.relevant.equals("no") && temp.atr.equals(class_value))
                {
                    no_count += 1;
                }
            }   
            tmp[0] = (float)yes_count/no_of_yes;
            tmp[1] = (float)no_count/no_of_no;
            return tmp;
        }
        
        return null;
    }
    
    public String calculateProbs(String url, double parent_score, double div_score, double anchor_score)
    {
        String[] data = new String[5];
        data[0] = url;
        String parent, div, anchor;
        float relevant_yes, relevant_no, temp_parent[], temp_div[], temp_anchor[];
        
        System.out.println("P(yes): " + p_of_yes);
        System.out.println("P(no): " + p_of_no);
        
        System.out.println("n(yes): " + no_of_yes);
        System.out.println("n(no): " + no_of_no);
        
        System.out.println("Total: " + total);
        if(parent_score > 0.4)
        {
            parent = "yes";
        }
        else
        {
            parent = "no";
        }
        if(div_score > 0.4)
        {
            div = "yes";
        }
        else
        {
            div = "no";
        }
        if(anchor_score > 0.4)
        {
            anchor = "yes";
        }
        else
        {
            anchor = "no";
        }
        
        System.out.println("parent: " + parent);
        
        temp_parent = getCount("parent", parent);
        temp_div = getCount("div", div);
        temp_anchor = getCount("anchor", anchor);
        
        relevant_yes = temp_parent[0]*temp_div[0]*temp_anchor[0];
        relevant_no = temp_parent[1]*temp_div[1]*temp_anchor[1];
        
        System.out.println("parent_s: " + temp_parent[0]);
        System.out.println("div_s: " + temp_div[0]);
        System.out.println("anchor_s: " + temp_anchor[0]);
        
        System.out.println("parent_n: " + temp_parent[1]);
        System.out.println("div_n: " + temp_div[1]);
        System.out.println("anchor_n: " + temp_anchor[1]);
        
        System.out.println("relevant_yes: " + relevant_yes);
        System.out.println("relevant_no: " + relevant_no);
        
        values temp;
        
        if((relevant_yes*p_of_yes) > (relevant_no*p_of_no))
        {
            temp = new values(url, parent, div, anchor, "yes");
            
            valueVector.add(temp);
            total += 1;
            no_of_yes += 1;
            p_of_yes = (float) no_of_yes/total;
            p_of_no = (float) no_of_no/total;
            System.out.println(url + "-" + parent + "-" + div + "-" + anchor + "-" + "yes " + "n(yes): " + no_of_yes);
            return "yes";
        }
        else if((relevant_yes*p_of_yes) < (relevant_no*p_of_no))
        {
            temp = new values(url, parent, div, anchor, "no");
            
            valueVector.add(temp);
            total += 1;
            no_of_no += 1;
            p_of_no = (float) no_of_no/total;
            p_of_yes = (float) no_of_yes/total;
            System.out.println(url + "-" + parent + "-" + div + "-" + anchor + "-" + "no " + "n(no): " + no_of_no);
            return "no";
        }
        else
        {
            temp = new values(url, parent, div, anchor, "yes");
            valueVector.add(temp);
            total += 1;
            no_of_yes += 1;
            p_of_yes = (float) no_of_yes/total;
            p_of_no = (float) no_of_no/total;
            System.out.println("In Else..");
            System.out.println(url + "-" + parent + "-" + div + "-" + anchor + "-" + "yes " + "n(yes): " + no_of_yes);
            return "yes";
        }        
    }
    
    public void run()
    {
        findRelevancy();
    }
    
}
