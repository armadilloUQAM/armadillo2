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

package programs;

import biologic.Matrix;
import biologic.MultipleTrees;
import biologic.Results;
import biologic.Tree;
import configuration.Util;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import program.RunProgram;
import workflows.workflow_properties;


public class rf extends RunProgram {
  
    //Remove neg distance: -0.[0-9]*
  public Pattern replaceNegValue=Pattern.compile("(-0.[0-9]*)");     
 //  public Pattern replaceNegValue2=Pattern.compile("(0.[0]*([,]|[)]))");    
    boolean debug=true;
    Results results=new Results();

    public rf(workflow_properties properties) {
        super(properties);       
        execute();       
    }


    @Override
    public boolean init_checkRequirements() {       
        Vector<Integer>treeDOWN=properties.getInputID("tree",PortInputDOWN);
        Vector<Integer>multipletreesDOWN=properties.getInputID("multipletrees",PortInputDOWN);

        if (treeDOWN.size()==0&&multipletreesDOWN.size()==0) {
            setStatus(status_BadRequirements, "Error: We need 2 input trees");
            return false;
        }
        return true;
    }

    
    @Override
    public void init_createInput() {
        Util util=new Util("input.txt");
        Vector<Integer>MultipleTreesDOWN=properties.getInputID("multipletrees",PortInputDOWN);
        Vector<Integer>treeDOWN=properties.getInputID("tree",PortInputDOWN);       
        results.getUnknownST().append("* --------------------------------------------------*\n");
        setStatus(status_running,"* --------------------------------------------------*\n");
        //--We assume that the tree are first
        int count=1;
        if (properties.getBoolean("replace_negative_distance")) {
                      setStatus(status_running,"Replacing negative distance in tree with 0.00001");
        }
        for (int ids:treeDOWN) {
            if (ids!=0) {
                Tree tree=new Tree(ids);
                results.getUnknownST().append("Tree "+(count)+": "+tree.getName()+"\n");
                setStatus(status_running,"Tree "+count+": "+tree.getName());
                addInput(tree);
                String tt="";
                 if (properties.getBoolean("abbreviate")) {
                    tt=tree.getTreeAbbreviate();
                } else {
                     tt=tree.getTree();
                 }
                 if (properties.getBoolean("replace_negative_distance")) {                     
                     Matcher m=replaceNegValue.matcher(tt); 
                    while(m.find()) {
                      tt=tt.replaceAll(m.group(1), "0.00001");
                    } 
                    
                }
                 //--Remove 0 distance
                //  Matcher m=replaceNegValue2.matcher(tt); 
//                    while(m.find()) {
//                      tt=tt.replaceAll(m.group(1), "0.0000001");
//                    } 
                 util.println(tt);
                count++;
            }
        }
         for (int ids:MultipleTreesDOWN) {
            MultipleTrees trees=new  MultipleTrees(ids);
            for (Tree tree:trees.getTree()) {
                results.getUnknownST().append("Tree "+(count)+": "+tree.getName()+"\n");
                setStatus(status_running,"Tree "+(count)+": "+tree.getName());
                if (properties.getBoolean("abbreviate")) {
                    util.println(tree.getTreeAbbreviate());
                } else util.println(tree.getTree());
                 count++;
            }
        }
        util.close();
       results.getUnknownST().append("* --------------------------------------------------*\n");
        setStatus(status_running,"* --------------------------------------------------*\n");
        results.getUnknownST().append("\n");
    }

    @Override
    public String[] init_createCommandLine() {
        String[] com=new String[10];
        for (int i=0; i<com.length;i++) com[i]="";
                       com[0]="cmd.exe";
                       com[1]="/C";
                       com[2]=properties.getExecutable();
                       com[3]="input.txt";
                       com[4]="output1.txt";
                       com[5]="tmp.txt";
                       com[6]="matrix.txt";
        return com;
    }

    

    @Override
    public void post_parseOutput() {
        //--Note: many try catch since it's buggy
        try {            
            String[] str=Util.InputFile("output1.txt");
            for (String st:str) {
                results.getUnknownST().append(st+"\n");
            }
            //"output1.txt"
            results.setName(properties.getName()+" ("+Util.returnCurrentDateAndTime()+")");
            results.setNote(properties.getName()+" ("+Util.returnCurrentDateAndTime()+")");
            results.setRunProgram_id(this.getId());
            results.saveToDatabase();
            properties.put("output_results_id", results.getId());
        } catch(Exception e) {}
        try {
            Matrix matrix=new Matrix("matrix.txt");
            matrix.setName(properties.getName()+" ("+Util.returnCurrentDateAndTime()+")");
            matrix.setNote("Robinson&Fould generated matrix at "+Util.returnCurrentDateAndTime());
            matrix.setRunProgram_id(this.getId());
            matrix.saveToDatabase();
            properties.put("output_matrix_id",matrix.getId());
        } catch(Exception e) {}
        //--Tree file used
        try {
            MultipleTrees multi=new MultipleTrees();
            multi.setName(properties.getName()+" ("+Util.returnCurrentDateAndTime()+")");
            multi.setNote(properties.getName()+" ("+Util.returnCurrentDateAndTime()+")");
            multi.readNewick("input.txt");
            multi.setRunProgram_id(this.getId());
            multi.saveToDatabase();
            properties.put("output_multipletrees_id", multi.getId());
        } catch(Exception e) {}
        //
        Util.deleteFile("input.txt");
        Util.deleteFile("output1.txt");
        Util.deleteFile("tmp.txt");
        Util.deleteFile("matrix.txt");
    }

}
