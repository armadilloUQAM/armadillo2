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

////////////////////////////////////////////////////////////////////////////////
///
/// Create a Thread to run mrbayes v3.1
/// Note: Seqboot is Special since a filename shoud alwaqys be associated with it
/// Note: We rename the outfile to seqboot.hashcode
/// Etienne Lord 2011 and Med Amine Remita 
/// Mars 2011

import biologic.Alignment;
import biologic.MultipleTrees;
import biologic.Phylip;
import biologic.Phylip_Seqboot;
import biologic.Text;
import biologic.Tree;
import biologic.Unknown;
import configuration.Config;
import configuration.Util;
import java.io.*;
import program.RunProgram;
import workflows.workflow_properties;


public class mrbayes extends RunProgram {

    public String filename="";
    public String infile="sequences.nex";
    public String outfile="sequences.nex1";
    Alignment multi;
    
    public static int replicate=10000;   //Default replicate
    public static int sample_freq=10; 
    
    private boolean debug;

    /**
     *
     * Note: il n'y a pas présentement de test d'erreur
     * @param config Fichier de configuration où se retourve les différents path
     * @param seq    Fichier de séquence contenant les séquences à partir desquelles l'arbre est généré
     */
    public mrbayes(workflow_properties properties) { 
        super(properties);        
        execute();
    }

    @Override
    public String[] init_createCommandLine() {
       String[] com=new String[11];
       for (int i=0; i<com.length;i++) com[i]="";
       com[0]="cmd.exe";
       com[1]="/C";
       com[2]=properties.getExecutable();
       com[3]="mrbayes.params"; 
       return com;
    }



    @Override
    public boolean init_checkRequirements() {
        int alignment_id=properties.getInputID("input_alignment_id");
        if (alignment_id==0) {
            setStatus(status_BadRequirements,"No Aligment_id found");
            return false;
        }
        return true;
    }



    @Override
    public void init_createInput() {
        try {
            Util.deleteFile("mrbayes.params");
            Util.deleteFile(infile);
            Util.deleteFile(outfile);
            //--TO DO other file
            Util.deleteFile("sequences.nex1.run1.p");
            Util.deleteFile("sequences.nex1.run1.t");
            Util.deleteFile("sequences.nex1.run2.p");
            Util.deleteFile("sequences.nex1.run2.t");
            Util.deleteFile("sequences.nex1.t");
            Util.deleteFile("sequences.nex1.p");
            Util.deleteFile("sequences.nex1.con");
            Util.deleteFile("sequences.nex1.con.tre");
            Util.deleteFile("sequences.nex.lstat");
            Util.deleteFile("sequences.nex1.mcmc");
            Util.deleteFile("sequences.nex1.parts");
            Util.deleteFile("sequences.nex1.trprobs");
            Util.deleteFile("sequences.nex");
        } catch (Exception e) {
        }
        
        
        
        int alignment_id=properties.getInputID("input_alignment_id");
        //--Delete Infile and Outfile
     
        multi=new Alignment(alignment_id);
        
        //--TO DO... Create Nexus MrBayes compatible alignment format
        if (properties.isSet("datatype")) {
            multi.outputNexus(infile, properties.get("datatype")); 
        } else multi.outputNexus(infile, "dna"); 
        //--WE don't resave the input - Etienne
        //multi.saveToDatabase(); //amine
        //        addInput(multi);
        createConfigFile("mrbayes.params");
    }


    @Override
    public void post_parseOutput() {
        //Text
        try {
            Text text = new Text("mrbayes.params");
            text.setName(properties.getName() + " (" + Util.returnCurrentDateAndTime() + ")");
            text.setNote("MrBayes - parameters (" + Util.returnCurrentDateAndTime() + ")");
            text.setRunProgram_id(this.getId());
            text.saveToDatabase();
            properties.put("output_results_id", text.getId());
        } catch (Exception e) {
        }        
        //MultipleTree
        try {
            MultipleTrees mtree = new MultipleTrees();
            mtree.setName(properties.getName() + " (" + Util.returnCurrentDateAndTime() + ")");          
            mtree.setNote("MrBayes at " + Util.returnCurrentDateAndTime() + "");
            mtree.setRunProgram_id(this.getId());
            //--2 case since MacOSX file is not the same as windows
            if (properties.get("Version").startsWith("3.2")) {
                mtree.readNewickTreeFromMrBayes("sequences.nex1.con.tre");
            } else {
                mtree.readNewickTreeFromMrBayes("sequences.nex.con.tre");
            }
            mtree.setAlignment_id(properties.getInputID("input_alignment_id"));
            mtree.replaceSequenceIDwithNames();
            //--MrBayes, replace sequence name
            
            
            int i=1;
            for (Tree t:mtree.getTree()) {
                t.setName(properties.getName()+"_"+(i++) + " (" + Util.returnCurrentDateAndTime() + ")");
                t.setNote("MrBayes at " + Util.returnCurrentDateAndTime() + "");
            }
            
            mtree.saveToDatabase();            
            properties.put("output_tree_id", mtree.getTree().get(0).getId());            
            properties.put("output_multipletrees_id", mtree.getId());    
        } catch (Exception e) {
        }
        
        try {
            //Util.deleteFile("mrbayes.params");
//            //--TO DO other file
            Util.deleteFile("sequences.nex1.run1.p");
            Util.deleteFile("sequences.nex1.run1.t");
            Util.deleteFile("sequences.nex1.run2.p");
            Util.deleteFile("sequences.nex1.run2.t");
            Util.deleteFile("sequences.nex1.t");
            Util.deleteFile("sequences.nex1.p");
            Util.deleteFile("sequences.nex1.con");
            Util.deleteFile("sequences.nex1.mcmc");
            Util.deleteFile("sequences.nex1.parts");
            Util.deleteFile("sequences.nex1.trprobs");
            Util.deleteFile("sequences.nex");
        } catch (Exception e) {
        }
    }

   ////////////////////////////////////////////////////////////////////////////
   // FUNCTIONS

    /**
     * Create a MrBayes configurationFile [param] (Override this method to change param file)
     * see: http://mrbayes.csit.fsu.edu/wiki/index.php/FAQ_3.2
     * begin mrbayes;
     *   set autoclose=yes;
     *   set nowarn=yes;
     *   execute primates.nex;
     * lset nst=6 rates=gamma;
     * mcmc nruns=1 ngen=1000 samplefreq=10  file=primates.nex1;
     * sump burnin=25;  //--Summarize the parameter value
     * sumt burnin=100; //--Summarize the tree (must be 25% of sample)
     * end;
     * 
     * @param filename
     * @return true if success!
     */
    public boolean createConfigFile(String filename) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(new File(filename)));
            pw.println("begin mrbayes;");
            pw.println("set autoclose=yes;");
            pw.println("set nowarn=yes;");
            pw.println("execute "+infile+";");

            //--outgroup <number>/<taxon name>

            if (properties.isSet("outgroup")){
                  int outgroup_nb=properties.getInt("outgroup");
                  pw.println("outgroup "+outgroup_nb+";");
            }
            ////////////////////////////////////////////////////////////////////
            //--Look on the datatype
            //--DNA or RNA
            if (properties.get("datatype").equals("DNA")||properties.get("datatype").equals("RNA")) {            
                //--Output nst and rates
                int nst=6;
                if (properties.isSet("nst")) {
                    if (properties.get("nst").startsWith("F81")) nst=1;
                    if (properties.get("nst").startsWith("HKY")) nst=2;
                    if (properties.get("nst").startsWith("GTR")) nst=6;
                }
                
                String rates=(properties.isSet("rates")?properties.get("rates"):"gamma");
                String covarion=(properties.isSet("covarion")?properties.get("covarion"):"no");
                String nucmodel=(properties.isSet("nucmodel")?properties.get("nucmodel"):"4by4");
                String omegavar=(properties.isSet("omegavar")?properties.get("omegavar"):"Equal");
                String statefreqpr=(properties.isSet("statefreqpr")?properties.get("statefreqpr"):"estimated");
                if (nucmodel.equals("4by4")) {
                    String lset=String.format("lset nst=%s nucmodel=%s rates=%s covarion=%s;", nst, nucmodel,rates, covarion, omegavar);
                    pw.println(lset);
                }
                if (nucmodel.equals("doublet")) {
                    String lset=String.format("lset nst=%s nucmodel=%s rates=%s;", nst, nucmodel,rates, covarion, omegavar);
                    pw.println(lset);
                }
                if (nucmodel.equals("codon")) {
                    String lset=String.format("lset nst=%s nucmodel=%s rates=%s omegavar=%s;", nst, nucmodel,rates, covarion, omegavar);
                 
                    pw.println(lset);
                }
                if (!statefreqpr.equals("estimated")&&statefreqpr.matches("\\w*\\(.*\\)")) {
                    String prset=String.format("prset statefreqpr=%s;", statefreqpr);
                    pw.println(prset);
                }                
            }
            
             //--PROTEIN
            if (properties.get("datatype").equals("Protein")) {            
                //--Output nst and rates
                String aamodelpr=((properties.isSet("aamodelpr"))?properties.get("aamodelpr"):"gtr");
                String statefreqpr=((properties.isSet("statefreqpr"))?properties.get("statefreqpr"):"fixed");
                String aarevmatpr=((properties.isSet("nst"))?properties.get("nst"):"estimated");
                String aamode="fixed";                
                String rates=(properties.isSet("rates")?properties.get("rates"):"gamma");                
                String covarion=(properties.isSet("covarion")?properties.get("covarion"):"no");
                if (statefreqpr.equals("estimated")) {
                    
                }
                String prset=String.format("prset aamodelpr=%s(%s) statefreqpr=%s aarevmatpr=%s;", aamode, aamodelpr, statefreqpr,aarevmatpr);                
                pw.println(prset);
                String lset=String.format("lset rates=%s covarion=%s;", rates, covarion);
                pw.println(lset);
            }
            
              //--RESTRICTION OR Standard
            if (properties.get("datatype").equals("Restriction")||properties.get("datatype").equals("Standard")) {            
                //--Output nst and rates
                
                String statefreqpr=((properties.isSet("statefreqpr"))?properties.get("statefreqpr"):"fixed");                
                String rates=(properties.isSet("rates")?properties.get("rates"):"gamma");                
                String coding=(properties.isSet("coding")?properties.get("coding"):"all");
                                
                String prset=String.format("prset statefreqpr=%s;", statefreqpr);                
                pw.println(prset);
                String lset=String.format("lset rates=%s coding=%s;", rates, coding);
                pw.println(lset);
            }
            
            ////////////////////////////////////////////////////////////////////            
            //--MCMC OPTIONS
            //--Actual run
            if (properties.isSet("replicate")) {
                replicate=properties.getInt("replicate");
            }
            if (properties.isSet("sample_freq")) {
                sample_freq=properties.getInt("sample_freq");
            }
            int default_freq=replicate/10;
            //http://mrbayes.csit.fsu.edu/Help/lset.html
            String st=String.format("mcmc nruns=2 ngen=%d samplefreq=%d  Diagnfreq="+default_freq+" file=%s;", replicate, sample_freq, outfile);
            pw.println(st);
                        
            //pw.println("Diagnfreq="+default_freq+";");
            //--sump
            int sump=(properties.isSet("sump_burnin")?properties.getInt("sump_burnin"):default_freq);
            pw.println("sump burnin="+sump+";");
            //--sumt
            int sumt=(properties.isSet("sumt_burnin")?properties.getInt("sumt_burnin"):default_freq);
            pw.println("sumt burnin="+sumt+";");
            //Output the replicate 
          
            pw.println("set quitonerror=no;");
            //--(Not needed in MacOSX) Removed on 14 March 2012
            pw.println("quit;");
            pw.println("end;");
            pw.flush();
            pw.close();

        } catch (Exception e) {Config.log("Error in creating ConfigFile "+filename);return false;}
        return true;
    }


}
