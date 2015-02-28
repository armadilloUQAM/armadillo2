/*
 *  Armadillo Workflow Platform v1.0
 *  A simple pipeline system for phylogenetic analysis
 *  
 *  Copyright (C) 2009-2011  Etienne Lord, Mickael Leclercq
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

package biologic.seqclasses;


import biologic.Alignment;
import biologic.MultipleSequences;
import biologic.MultipleTrees;
import biologic.Sequence;
import biologic.Text;
import biologic.Tree;
import configuration.Config;
import configuration.Util;
import java.util.regex.Pattern;
import java.io.*;
import java.util.regex.Matcher;


/**
 * This will load the RapidEdit files from all the group
 * @author Lorde
 */
public class RapidLoad {

    Pattern pat_group=Pattern.compile("\\[(.*)\\]");
    Pattern pat_sequence=Pattern.compile("\\s*(\\S*)\\s{2,5}(.*)");

    /**
     * RapidEdit MultipleSequences 
     * @param filename
     * @return new multipleSequence ID
     */
    public int loadMultipleSequences(String filename) {
        try {
            BufferedReader br=new BufferedReader(new FileReader(new File(filename)));
            MultipleSequences multi=new MultipleSequences();
            while(br.ready()) {
                String str=br.readLine();
                Matcher m1=pat_group.matcher(str);
                Matcher m2=pat_sequence.matcher(str);
               if (!str.startsWith("#"))  {
                   if (m1.find()) {
                       String name=m1.group(1);
                       Config.log("RapidEdit Loading Group: "+name);
                        if (multi.getNbSequence()>0) {
                            multi.saveToDatabase();
                        }
                        multi=new MultipleSequences();
                        multi.setName(name);
                    }
                    if (m2.find()) {                     
                        Sequence s=new Sequence();
                        s.setName(m2.group(1).trim().replace('-', '_'));
                        s.setSequence(m2.group(2));
                        multi.add(s);
                    }
               }
            }
            br.close();
            multi.saveToDatabase();
            return multi.getId();
        } catch(Exception e) {Config.log("Error.");return 0;}
    }

     public int loadAlignment(String filename) {
        try {
            BufferedReader br=new BufferedReader(new FileReader(new File(filename)));
            Alignment align=new Alignment();
            while(br.ready()) {
                String str=br.readLine();
                Matcher m1=pat_group.matcher(str);
                Matcher m2=pat_sequence.matcher(str);
               if (!str.startsWith("#"))  {
                   if (m1.find()) {
                       String name=m1.group(1);
                       Config.log("RapidEdit Loading Group: "+name);
                        if (align.getNbSequence()>0) {
                            align.saveToDatabase();
                        }
                        align=new Alignment();
                        align.setName(name);
                    }
                    if (m2.find()) {
                        //System.out.println(m2.group());
                        Sequence s=new Sequence();
                        s.setName(m2.group(1).trim().replace('-', '_'));
                        s.setSequence(m2.group(2));                       
                        align.add(s);
                    }
               }
            }
            br.close();
            align.saveToDatabase();
            return align.getId();
        } catch(Exception e) {Config.log("Error : "+e.getMessage()); return 0;}
    }

    public int loadSequence(String filename) {
        try {
            BufferedReader br=new BufferedReader(new FileReader(new File(filename)));            
            int id=0;
            while(br.ready()) {
                String str=br.readLine();
                Matcher m1=pat_group.matcher(str);
                Matcher m2=pat_sequence.matcher(str);
               if (!str.startsWith("#"))  {                  
                    if (m2.find()) {                    
                        Sequence s=new Sequence();
                        s.setName(m2.group(1).trim().replace('-', '_'));
                        s.setSequence(m2.group(2));                      
                       s.saveToDatabase();
                       id=s.getId();
                    }
               }
            }            
            br.close();
            return id;
        } catch(Exception e) {return 0;}
    }

    public int loadTree(String filename) {
            MultipleTrees multi=new MultipleTrees();
            multi.readNewick(filename);
            multi.saveToDatabase();
            for (Tree tree:multi.getTree()) {
                return tree.getId();
            }
            return 0;        
    }

     public int loadText(String filename, String name, String note) {
            Text text=new Text(filename);
            text.setName(name);
            text.setNote(note+"\n"+"Modified on "+Util.returnCurrentDateAndTime());
            if (text.getText().indexOf("#Note:")!=-1) {
                text.setText(text.getText().substring(0, text.getText().indexOf("#Note:")));
            } else {
                text.setText(text.getText());
            }
            text.saveToDatabase();
            return text.getId();        
    }

}
