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


import biologic.Biologic;
import biologic.MultipleTrees;
import biologic.Output;
import biologic.Results;
import biologic.Text;
import biologic.TextFile;
import biologic.Tree;
import configuration.Util;
import java.util.Vector;
import program.RunProgram;
import workflows.workflow_properties;
import workflows.workflow_properties_dictionnary;

/**
 * Create a load Tree from a file
 * @author Etienne Lord
 * @since Mars 2010
 */
public class loadTree extends RunProgram {
 


    /**
     * Main contructor
     */
    public loadTree(workflow_properties properties) {
        super(properties);       
        execute();
    }


    @Override
    public boolean init_checkRequirements() {
        int TextFileID=properties.getInputID("TextFile");
        int TextID=properties.getInputID("Text");
        int ResultsID=properties.getInputID("Results");

        if (!properties.isSet("inputname")&&TextFileID==0&&TextID==0&&ResultsID==0) {
            setStatus(status_BadRequirements,"No filename specified or no file found.");
            return false;
        }
        return true;

    }


    @Override
    public void init_createInput() {
        
    }


    @Override
    public String[] init_createCommandLine() {
         String[] com=new String[11];
           for (int i=0; i<com.length;i++) com[i]="";        
        return com;
    }



    @Override
    public boolean do_run() throws Exception {
        return true;
    }


    @Override
    public void post_parseOutput() {
       int TextFileID=properties.getInputID("TextFile");
      int TextID=properties.getInputID("Text");
      int ResultsID=properties.getInputID("Results");
      if (TextFileID!=0) {
          TextFile text=new TextFile(TextFileID);
          properties.put("inputname",text.getFile());
      }
      if (TextID!=0) {
          Text text=new Text(TextID);
          text.Output("outfile");
          properties.put("inputname","outfile");
      }
      if (ResultsID!=0) {
          Results text=new Results(ResultsID);
          text.Output("outfile");
          properties.put("inputname","outfile");
      }
        String name="";
        for (String inputtype:workflow_properties_dictionnary.InputOutputType) {
        Vector<Integer>ids_name=properties.getInputID(inputtype, save_to_file.PortInputUP);
        if (ids_name.size()>0) {
               int idt=ids_name.get(0); //--We care only for the first one
               Output out=new Output();
               out.setType(inputtype);
               out.setTypeid(0);
               name=((Biologic)out.getBiologic()).getNameId(idt);
            }
         }
      if (name.isEmpty()) name=properties.get("inputname");
       MultipleTrees trees=new MultipleTrees();
       trees.setName(name);
       trees.setNote("Loaded on "+Util.returnCurrentDateAndTime());
       trees.readNewick(properties.get("inputname"));
       trees.replaceSequenceIDwithNames();
       trees.saveToDatabase();
       for (Tree t:trees.getTree()) properties.put("output_tree_id",t.getId());
       properties.put("output_multipletrees_id", trees.getId());
       
    }
}
