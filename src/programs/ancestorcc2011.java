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
 * Class to run Ancestor version 2011.
 * @author Etienne Lord
 * @since 2011
 */
public class ancestorcc2011 extends RunProgram {  

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
    String outfile_params="params.txt";
    String outstate="outfile.sta";
    String mode = "best1"; //best, best1, like, like1
    String modeMax ="f"; //(f|p)
    String prob="5";
   
  
    public ancestorcc2011(workflow_properties properties) {
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
        //--Create new params.txt file for Ancestor
        Util u=new Util(outfile_params);
        
        String cmd="*This is the parameter file needed to run Ancestor1_1\n";	
        cmd+="* The lines starting by a star are ignored\n";
        cmd+="* please cite Diallo AB, Makarenkov V, Blanchette M. Bioinformatics, 2010: 130-131.\n";
        cmd+="*             Diallo AB, Makarenkov V, Blanchette M. J Comput Biol,2007 :446-461.\n";
        cmd+="* alignment\n";
        cmd+="msa = "+inSeqFile+"\n";
        cmd+="*tree file\n";
        cmd+="tree  = "+inTreeFile+"\n";
        cmd+="*method can be (ml or post)\n";
        cmd+="*precision can be (exact or heuristic),\n* if it is heuristique, the value of precision can be given (type and value)\n* otherwise the default parameter will be used.\n* type is either fixed or proportional\n";        
        if (properties.getBoolean("exactScenario")){
           cmd+="method=ml\n";
           cmd+="precision=exact\n";
       } else if (properties.getBoolean("heuristicScenario")){
           cmd+="method=ml\n";
           cmd+="precision=heuristic\n";
           if (properties.getBoolean("heuristicScenarioAbs")){
               cmd+="type=fixed\n";        
               cmd+="value="+properties.get("heuristicScenarioAbsData")+"\n";
           }
           if (properties.getBoolean("heuristicScenarioProp")){
               cmd+="type=proportional\n";        
               cmd+="value="+properties.get("heuristicScenarioPropData")+"\n";                       
           }
       } else if (properties.getBoolean("exactPosterior")){
           cmd+="method=post\n";
           cmd+="precision=exact\n";
       } else if (properties.getBoolean("heuristicPosterior")){                    
           cmd+="method=post\n";
           cmd+="precision=heuristic\n";
             if (properties.getBoolean("heuristicPosteriorAbs")){
                cmd+="type=fixed\n";  
                cmd+="value="+properties.get("heuristicPosteriorAbsData")+"\n";
           }
           if (properties.getBoolean("heuristicPosteriorProp")){
                 cmd+="type=proportional\n";      
                 cmd+="value="+properties.get("heuristicPosteriorPropData")+"\n";
           }           
       }
        cmd+="*tree-hmm parameters\n"; 
        cmd+="insStart="+properties.get("insertionStart")+"\n";
        cmd+="insExt="+properties.get("insertionExt")+"\n";
        cmd+="delStart="+properties.get("deletionStart")+"\n";
        cmd+="delExt="+properties.get("deletionExt")+"\n"; 
        cmd+="*prefix of the result files\n";
        cmd+="prefix=outfile\n";
        u.println(cmd);
        u.close();
    }



    @Override
    public String[] init_createCommandLine() {
          String[] com=new String[14];
                       for (int i=0; i<com.length;i++) com[i]="";                                        
                       com[0]="cmd.exe";
                       com[1]="/C";
                       com[2]=properties.getExecutable();
                       com[3]=this.outfile_params;                       
                       return com;
    }

    @Override
    public void post_parseOutput() {
        //--Execute sbInferSubstitution to get Ancestrale Sequence
        properties.put("ancestral_filename", this.outfile);
//        sbinferSubstitutions sb=new sbinferSubstitutions(properties);
//        while(!sb.isDone()) {}

        //--Load ancestrale sequence here.
        Alignment Ancestrale_Confidence=new Alignment();
        if (Ancestrale_Confidence.readSequenceFromFasta("outfile.seq.nucConf_CC")) {
            Ancestrale_Confidence.PrintPhylip();
            Alignment multi=new Alignment();
            if (multi.readSequenceFromFasta("outfile.seq")) {
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
            } 
            multi.saveToDatabase();
            properties.put("output_alignment_id",multi.getId());
        
        //multi.PrintPhylip();
       
        
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
        } //--End outfile.seq.nucConf_CC
    } //--End parse output

}
