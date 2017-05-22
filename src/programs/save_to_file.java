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
import configuration.Config;
import biologic.Alignment;
import biologic.Biologic;
import biologic.Input;
import biologic.Matrix;
import biologic.MultipleSequences;
import biologic.MultipleTrees;
import biologic.Output;
import biologic.Results;
import biologic.Sequence;
import biologic.TextFile;
import biologic.Tree;
import biologic.Unknown;
import configuration.Util;
import java.io.File;
import java.util.Vector;
import program.RunProgram;
import workflows.workflow_properties;
import workflows.workflow_properties_dictionnary;


public class save_to_file extends RunProgram {
    
    boolean typePhylip=false; //default is fasta
    
    
    public save_to_file(workflow_properties properties) {
        super(properties);
        String filename="";
        String path="";
        if (properties.isSet("path")) {
            path=properties.get("path");
            if (!Util.FileExists(path)) {
                Config.log("Path : "+path+" does not exists...");
                Config.log("Creating Path : "+path);
                File f=new File(path);
                f.mkdirs();
            }
        }
        if (properties.isSet("outputname")) {
            filename=properties.get("outputname");
        }
        if (properties.getBoolean("addID")) {
            filename=properties.get("properties_id")+"_"+filename;
        }
        if (properties.getBoolean("addExecutionID")) {
            filename=workbox.getCurrentWorkflows().getName()+"_"+filename;
        }
        this.setStatus(status_running, "Creating "+filename);
        //--Is it a phylip output
        String filename2=filename.toLowerCase();
        if (filename2.endsWith("phylip")||filename2.endsWith("phy")||properties.getBoolean("outputPhylip")) typePhylip=true;
        //--Find name in input port
        for (String inputtype:workflow_properties_dictionnary.InputOutputType) {
            Vector<Integer>ids_name=properties.getInputID(inputtype, save_to_file.PortInputUP);
            if (ids_name.size()>0) {
                int idt=ids_name.get(0); //--We care only for the first one
                Output out=new Output();
                out.setType(inputtype);
                out.setTypeid(0);
                String name=((Biologic)out.getBiologic()).getNameId(idt);
                filename=name+"."+filename;
            }
        }
        //--Adding path
        if (!path.isEmpty()) {
            filename=path+File.separator+filename;
        }
        //--Open filename to append or write
        Util util=new Util();
        if (properties.getBoolean("append")) {
            util.openAppend(filename);
        } else {
            util.open(filename);
        }
        //--Iterate over all the possible input type
        for (String inputtype:workflow_properties_dictionnary.InputOutputType) {
            
            Vector<Integer>ids=properties.getInputID(inputtype.toLowerCase(), save_to_file.PortInputDOWN);
            //--Try to find name
            
            if (ids.size()>0) {
                for (int ido:ids) {
                    Config.log("Saving to file : "+inputtype+" "+ido);
                    Output output=new Output();
                    output.setType(inputtype);
                    output.setId(ido);
                    
                    if (inputtype.equalsIgnoreCase("sequence")) {
                        Sequence s=new Sequence(ido);
                        MultipleSequences multi=new MultipleSequences();
                        multi.add(s);
                        if (typePhylip) {
                            util.print(multi.outputPhylip());
                        } else util.print(multi.outputFasta());
                    }
                    if (inputtype.equalsIgnoreCase("multiplesequences")) {
                        MultipleSequences multi=new MultipleSequences(ido);
                        if (typePhylip) {
                            util.print(multi.outputPhylip());
                        } else util.print(multi.outputFasta());
                        
                    }
                    if (inputtype.equalsIgnoreCase("alignment")) {
                        Alignment multi=new Alignment(ido);
                        if (typePhylip) {
                            util.print(multi.outputPhylip());
                        } else util.print(multi.outputFasta());
                    }
                    if (inputtype.equalsIgnoreCase("multiplealignment")) {
                        //TO DO
                    }
                    if (inputtype.equalsIgnoreCase("tree")) {
                        Tree tree=new Tree(ido);
                        util.print(tree.getTree());
                    }
                    if (inputtype.equalsIgnoreCase("multipletrees")) {
                        MultipleTrees multi=new MultipleTrees(ido);
                        for (Tree tree:multi.getTree()) util.print(tree.getTree());
                    }
                    if (inputtype.equalsIgnoreCase("unknown")||inputtype.equalsIgnoreCase("text")||inputtype.equalsIgnoreCase("blast")||inputtype.equalsIgnoreCase("results")||inputtype.equalsIgnoreCase("html")) {
                        Unknown unknown = new Unknown(ido);
                        util.print(unknown.getUnknown());
                    }
                    if (inputtype.equalsIgnoreCase("matrix")) {
                        Matrix matrix = new Matrix(ido);
                        util.print(matrix.getMatrix());
                    }
                }
            } //--End for ID
        }
        util.close();
        TextFile.saveFile(properties,filename,"Shrimp rmapper","TextFile");
        this.setStatus(status_done, "");
    }
}
