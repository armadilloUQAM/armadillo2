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

//#57442
import biologic.Alignment;
import biologic.HTML;
import biologic.Results;
import biologic.Sequence;
import biologic.Text;
import biologic.Tree;
import biologic.seqclasses.AncestorCC_html;
import configuration.Util;
import program.RunProgram;
import workflows.workflow_properties;
/**
 * Run AncestorCC version 1.0 (prior to 2011)
 * Note: Need a rooted tree
 * @author Etienne Lord
 * @since 2009
 */
public class ancestorcc extends RunProgram {  

    Alignment align;

    // Debug and programming variables   
    // AncestorCC vatiables -- Note: now in the properties file
    String insS = "0.01"; //Default parameters
    String insE = "0.1";
    String delS = "0.01";
    String delE = "0.1";
    String inSeqFile = "infile.seq"; //Do not change for now
    String inTreeFile = "infile.tre";//Do not change for now
    String outfile = "outfile.seq";
    String outstate="outfile.sta";
    String mode = "best1"; //best, best1, like, like1
    String modeMax ="f"; //(f|p)
    String prob="5";
   
  
    public ancestorcc(workflow_properties properties) {
      super(properties);
      execute();
    }



    @Override
    public boolean init_checkRequirements() {

         int alignment_id=properties.getInputID("input_alignment_id");
         int tree_id=properties.getInputID("input_tree_id");
         if (alignment_id==0) {
            setStatus(status_BadRequirements, "No Alignment found!");
            return false;
         }
        if (tree_id==0) {
            setStatus(status_BadRequirements, "No Tree found!");
            return false;
         }
         //--Note:
         // to do: test if the tree is rooted here.. 
        
        return true;
    }



    @Override
    public void init_createInput() {
        //create infile
        int alignment_id=properties.getInputID("input_alignment_id");
        int tree_id=properties.getInputID("input_tree_id");
        align=new Alignment(alignment_id);
        align.outputFasta(inSeqFile);
        Tree tree=new Tree(tree_id);
        tree.outputNewick(inTreeFile);
    }



    @Override
    public String[] init_createCommandLine() {
          String[] com=new String[14];
                       for (int i=0; i<com.length;i++) com[i]="";
                       //AncestorCC best1 infile.seq infile.tre outfile.seq outfile.sta 0.01 0.9 0.01 0.9 f 10
                       if (properties.isSet("insE")) insE=properties.get("insE");
                       com[0]="cmd.exe";
                       com[1]="/C";
                       com[2]=properties.getExecutable();
                       if (properties.getBoolean("exactScenario")){
                           com[3]="best";
                       } else if (properties.getBoolean("heuristicScenario")){
                           com[3]="best1";
                       } else if (properties.getBoolean("exactPosterior")){
                           com[3]="like";
                       } else if (properties.getBoolean("heuristicPosterior")){
                           com[3]="like1";
                       }
                       //com[3]=mode;
                       com[4]=inSeqFile;
                       com[5]=inTreeFile;
                       com[6]=outfile;
                       com[7]=outstate;
                       com[8]=properties.get("insertionStart");
                       com[9]=properties.get("insertionExt");
                       com[10]=properties.get("deletionStart");
                       com[11]=properties.get("deletionExt");
                       if (properties.getBoolean("heuristicScenario")){
                           if (properties.getBoolean("heuristicScenarioAbs")){
                               com[12]="f";
                               com[13]=properties.get("heuristicScenarioAbsData");
                           }
                           if (properties.getBoolean("heuristicScenarioProp")){
                               com[12]="p";
                               com[13]=properties.get("heuristicScenarioPropData");
                           }
                       }
                       if (properties.getBoolean("heuristicPosterior")){
                           if (properties.getBoolean("heuristicPosteriorAbs")){
                               com[12]="f";
                               com[13]=properties.get("heuristicPosteriorAbsData");
                           }
                           if (properties.getBoolean("heuristicPosteriorProp")){
                               com[12]="p";
                               com[13]=properties.get("heuristicPosteriorPropData");
                           }
                       }
                       return com;
    }

    @Override
    public void post_parseOutput() {
        //--Execute sbInferSubstitution to get Ancestrale Sequence
        properties.put("ancestral_filename", this.outfile);
        sbinferSubstitutions sb=new sbinferSubstitutions(properties);
        while(!sb.isDone()) {}

        //--Load ancestrale sequence here.
        Alignment Ancestrale_Confidence=new Alignment();
        Ancestrale_Confidence.readSequenceFromFasta("outfile.seq.nucConf_CC");
        Ancestrale_Confidence.PrintPhylip();
        Alignment multi=new Alignment();
        multi.readSequenceFromFasta("outfile.seq");
        multi.setName("Ancestor from "+align.getName()+"("+Util.returnCurrentDateAndTime()+")");
        multi.setNote("Created on "+Util.returnCurrentDateAndTime());
        if (Ancestrale_Confidence.getNbSequence()==multi.getNbSequence()) {
            //--Set the Confidence level
            //--Note: MUST be in the same order!
            int count=0;
            for (Sequence s:multi.getSequences()) {
                s.addToQuality(Ancestrale_Confidence.getSequences().get(count++).getSequence());
            }
        }
        //multi.PrintPhylip();
        multi.saveToDatabase();
        properties.put("output_alignment_id",multi.getId());
        
        //--Create HTML page
        AncestorCC_html ancestor_html=new AncestorCC_html();
        String html_string=ancestor_html.AlignmenttoHTML(align, multi, Ancestrale_Confidence);
        HTML html=new HTML();
        html.setData(html_string);
        html.setFilename("results.html");
        html.setName("AncestorCC results");
        html.setNote("Created on "+Util.returnCurrentDateAndTime());
        html.saveToDatabase();
        properties.put("output_html_id",html.getId());

        Results results=new Results();
        results.setText(this.getOutputText());
        results.setName(properties.getName()+" ("+Util.returnCurrentDateAndTime()+")");
        results.setNote("Created on "+Util.returnCurrentDateAndTime());
        results.saveToDatabase();
        properties.put("output_results_id", results.getId());
        //--Saving some file
        Text confidence=new Text("outfile.seq.nucConf_CC");
        confidence.setName("AncescorCC_confidence_level for "+align.getName());
        confidence.setNote("Created on "+Util.returnCurrentDateAndTime());
        confidence.saveToDatabase();
        properties.put("output_text_id", confidence.getId());
         //--Saving some file
        Text confidence2=new Text("outfile.seq.post_CC");
        confidence2.setName("AncescorCC_post_level for "+align.getName());
        confidence2.setNote("Created on "+Util.returnCurrentDateAndTime());
        confidence2.saveToDatabase();      
        //--Delete not needed files
        Util.deleteFile("outfile.seq.post_CC");
        Util.deleteFile("outfile.seq");
        Util.deleteFile("outfile.seq.nucConf_CC");
        Util.deleteFile("outfile.sta");
        Util.deleteFile("outfile.seq.edge");
        Util.deleteFile("outfile.seq.anc");
        Util.deleteFile("infile.seq");
        Util.deleteFile("infile.tre");
    }

}
