package smartcrawler;

import java.sql.Connection;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;
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
    int total = 0, no_of_yes = 0, no_of_no = 0, accepted = 0, total_urls = 0;
    float p_of_yes, p_of_no;
    Vector valueVector;
    values value;        
    Progress progress;
    BufferedWriter bw;
    FileWriter fw;
    Connection conn = null;
    Statement st;
    ResultSet res;
    
    public RelevanceCalculator(File location)
    {
        loc = location;       
        String url,text;                  
        
        String[] data = new String[4];
        valueVector = new Vector();        
        progress = new Progress();
        progress.progressInfo.setEditable(false);
        progress.progressInfo.setText("");
        progress.setVisible(true);
        try
        {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/smart_crawler", "root", "");            
            st = conn.createStatement();
            res = st.executeQuery("select * from training_set");
            while(res.next())
            {
                total += 1;                
                url = res.getString(2);
                data[0] = res.getString(3);
                data[1] = res.getString(4);
                data[2] = res.getString(5);
                data[3] = res.getString(6);
                            
                value = new values(url, data[0],data[1],data[2],data[3]);                
                valueVector.add(value);               
            }
            
            st = conn.createStatement();
            res = st.executeQuery("select count(total_relevancy) from training_set where total_relevancy = 'yes'");
            while(res.next())
            {
                no_of_yes = res.getInt(1);
            }
            
            st = conn.createStatement();
            res = st.executeQuery("select count(total_relevancy) from training_set where total_relevancy = 'no'");
            while(res.next())
            {
                no_of_no = res.getInt(1);
            }
           
            p_of_yes = (float)no_of_yes/total;
            p_of_no = (float)no_of_no/total;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public String findRelevancy(String url, TreeMap<String, Double> wt, double[] div_count)
    {
        Double pRelevanceValue;
        String result = "";
        System.out.println(url);
        progress.progressInfo.append("Calculating parent page relevancy of " + url + "\n");
        ParentPageFinder ppf = new ParentPageFinder(wt); 
        ppf.doScrape(url);       
        try
        {            
            pRelevanceValue = ppf.averageParentPage.get(url);            
            total_urls += 1;
            result = calculateProbs(url, pRelevanceValue, div_count[0], div_count[1]); 
            if(result.equals("yes"))
            {
                accepted += 1;
                progress.progressInfo.append("Accepted: " + url + "\n");
            }
            else
            {
                progress.progressInfo.append("Discarded: " + url + "\n");
            }            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }
    
    public float[] getCount(String attr, String class_value)
    {
        int yes_count = 0, no_count = 0;
        float[] tmp = new float[2];
        if(attr.equals("parent"))
        {
            try
            {
                st = conn.createStatement();
                res = st.executeQuery("select count(total_relevancy) from training_set where total_relevancy = 'yes' and parent = '" + class_value + "'");
                while(res.next())
                {
                    yes_count = res.getInt(1);
                }
               
                st = conn.createStatement();
                res = st.executeQuery("select count(total_relevancy) from training_set where total_relevancy = 'no' and parent = '" + class_value + "'");
                while(res.next())
                {
                    no_count = res.getInt(1);
                }
               
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
            tmp[0] = (float) yes_count/no_of_yes;
            tmp[1] = (float) no_count/no_of_no;
            return tmp;
        }
        else if(attr.equals("div"))
        {  
            try
            {
                st = conn.createStatement();
                res = st.executeQuery("select count(total_relevancy) from training_set where total_relevancy = 'yes' and divi = '" + class_value + "'");
                while(res.next())
                {
                    yes_count = res.getInt(1);
                }
                
                st = conn.createStatement();
                res = st.executeQuery("select count(total_relevancy) from training_set where total_relevancy = 'no' and divi = '" + class_value + "'");
                while(res.next())
                {
                    no_count = res.getInt(1);
                }
                
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
            tmp[0] = (float) yes_count/no_of_yes;
            tmp[1] = (float) no_count/no_of_no;
            return tmp;
        }
        else if(attr.equals("anchor"))
        {  
            try
            {
                st = conn.createStatement();
                res = st.executeQuery("select count(total_relevancy) from training_set where total_relevancy = 'yes' and anchor = '" + class_value + "'");
                while(res.next())
                {
                    yes_count = res.getInt(1);
                }
                
                st = conn.createStatement();
                res = st.executeQuery("select count(total_relevancy) from training_set where total_relevancy = 'no' and anchor = '" + class_value + "'");
                while(res.next())
                {
                    no_count = res.getInt(1);
                }
                
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
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
        
        if(parent_score > 0.6)
        {
            parent = "yes";
        }
        else
        {
            parent = "no";
        }
        if(div_score > 0.2)
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
                
        temp_parent = getCount("parent", parent);
        temp_div = getCount("div", div);
        temp_anchor = getCount("anchor", anchor);
        
        relevant_yes = temp_parent[0]*temp_div[0]*temp_anchor[0];
        relevant_no = temp_parent[1]*temp_div[1]*temp_anchor[1];
                
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
            try
            {
                st = conn.createStatement();
                st.executeUpdate("insert into training_set (Id, url, parent, divi, anchor, total_relevancy) values(" + total + ", '" + url + "', '" + parent + "', '" + div + "', ' " + anchor + "', 'yes')" );
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }            
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
            try
            {
                st = conn.createStatement();
                st.executeUpdate("insert into training_set (Id, url, parent, divi, anchor, total_relevancy) values(" + total + ", '" + url + "', '" + parent + "', '" + div + "', ' " + anchor + "', 'no')" );
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
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
            try
            {
                st = conn.createStatement();
                st.executeUpdate("insert into training_set (Id, url, parent, divi, anchor, total_relevancy) values(" + total + ", '" + url + "', '" + parent + "', '" + div + "', '" + anchor + "', 'yes')" );             
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return "yes";
        }        
    }
    
    public void run()
    {
        
    }
    
}
