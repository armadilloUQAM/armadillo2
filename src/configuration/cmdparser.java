/*
 *  Armadillo Workflow Platform v2.0
 *  A simple pipeline system for phylogenetic analysis
 *  
 *  Copyright (C) 2009-2014  Etienne Lord, Mickael Leclercq
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package configuration;


import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import workflows.workflow_properties;


/**
 * Parse a command line
 * @author Etienne Lord
 * @since April 2014
 */
public class cmdparser {
    static Pattern p_w=Pattern.compile("(.*?)\\s", Pattern.CASE_INSENSITIVE);
    static Pattern p_w2=Pattern.compile("-(.*?)=(.*?)", Pattern.CASE_INSENSITIVE);
   static Pattern p_w4=Pattern.compile("(.*?)=(.*?)", Pattern.CASE_INSENSITIVE);
    static Pattern p_w3=Pattern.compile("-(.*?)", Pattern.CASE_INSENSITIVE);    
   
      public static workflow_properties parse(String[] cmd) {
        String cmds="";
        for (String s:cmd)cmds+=s+" ";
        return parse(cmds);
    }   
    
    public static workflow_properties parse(String cmd) {
          workflow_properties arg=new workflow_properties(); 
          arg.setCommandline(cmd);
          //Note: we add a space to get the last args
          Matcher m_w=p_w.matcher(cmd+" ");
        
         //--This will separate into args
         ArrayList<String>args=new ArrayList<String>();
                 
          while (m_w.find()) {
              // System.out.println(m_w.group());
                args.add(m_w.group());               
          }
           arg.put("CmdLine0", args.get(0));
           if (args.size()>1) arg.put("CmdLine1", args.get(1));
          
          //--Process each args
          int i=0;
          while(i<args.size()) {
                Matcher m_w2=p_w2.matcher(args.get(i));
                Matcher m_w3=p_w3.matcher(args.get(i));
                Matcher m_w4=p_w4.matcher(args.get(i));
               // System.out.println(args.get(i));
                //--We have a -type=data args
                if (m_w2.find()) {
                    String d[] =args.get(i).split("=");
                     String data1=d[0];
                     if (data1.startsWith("-")) data1=data1.substring(1);
                    arg.put(data1,d[1]);
                } else 
                //--We have a single args   -type  
                if (m_w3.find()) {
                    //--Is the next one is alone or a number?
                    if (i+1<args.size()) {
                        Matcher m_w5=p_w3.matcher(args.get(i+1));
                        //--Next is not another args
                        if (!m_w5.find()) {      
                            String data1=args.get(i).trim();
                            if (data1.startsWith("-")) data1=data1.substring(1);
                            arg.put(data1,args.get(i+1));
                           i++;   
                        } else {
                             String data1=args.get(i).trim();
                            if (data1.startsWith("-")) data1=data1.substring(1);
                            arg.put(data1,"");
                        }                        
                    } else {                        
                        arg.put(args.get(i),"");
                    }
                } else if (m_w4.find()) {
                    String d[] =args.get(i).split("=");
                     String data1=d[0];                    
                    arg.put(data1,d[1]);
                } 
                else {
                  //--Alone args  
                       arg.put(args.get(i),"");                   
                }                                        
              i++;
          }
           return arg;
    }
    
    /**
     * Test ofor this class
     * @param args 
     */
   public static void main(String[] args) {
        String arg="executable\\clutalw-2.1\\clustalw2.exe -ALIGN -INFILE=$infile -OUTFILE=outfile.fasta -NEWTREE=outfile.fasta.tre -OUTORDER=INPUT -PWGAPOPEN=15.0 -PWGAPEXT=6.66 -PAIRGAP=5.0 -KTUPLE=2.0 -GAPOPEN=20.0 -GAPEXT=0.2 -TRANSWEIGHT=0.5 -OUTPUT=FASTA";           
        String arg2="executable\\kalign.exe -f fasta -s 11 -e 0.85 -t 0.45 -m 0 outfile.fasta";
        if (args.length>0) {
            
        } 
        workflow_properties data=parse(arg);
        System.out.println(data.serializeToString());
        
    }
    
}
