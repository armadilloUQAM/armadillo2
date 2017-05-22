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

import biologic.Alignment;
import biologic.MultipleSequences;
import biologic.MultipleTrees;
import biologic.Text;
import biologic.Tree;
import configuration.Config;
import configuration.Util;
import workflows.workflow_properties;
import configuration.Util.*;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import program.RunProgram;
import uk.ac.ebi.webservices.FileUtil;
import uk.ac.ebi.webservices.WSEmbossClient;
import uk.ac.ebi.webservices.wsemboss.*;

/**
 * Note: mostly based on
 * WSEmboss Java client.
 * See:
 * <a href="http://www.ebi.ac.uk/Tools/Webservices/services/emboss">http://www.ebi.ac.uk/Tools/Webservices/services/emboss</a>
 * <a href="http://www.ebi.ac.uk/Tools/Webservices/clients/emboss">http://www.ebi.ac.uk/Tools/Webservices/clients/emboss</a>
 * <a href="http://www.ebi.ac.uk/Tools/Webservices/tutorials/java">http://www.ebi.ac.uk/Tools/Webservices/tutorials/java</a>
 */

public class EmbossWebEBI extends RunProgram {
    
    ////////////////////////////////////////////////////////////////////////////
    /// VARIABLES
    
    public static boolean debug=false;
    private String JobId="";
    MultipleSequences multi=new MultipleSequences();
    
    ////////////////////////////////////////////////////////////////////////////
    /// CONSTRUCTOR
    
    public EmbossWebEBI(workflow_properties properties) {
        super(properties);
        execute();
    }
    
    @Override
    public boolean init_checkRequirements() {
        if (config.get("email").equals("")) {
            setStatus(status_BadRequirements,"Error: Please set you email in Preferences->Email");
            return false;
        }
        
        JobId=(properties.isSet("JobId")?properties.get("JobId"):"");
        if (JobId.equals("")) {
            return false;
        } else if (!JobId.isEmpty()) {
            setStatus(status_running,"Retrieving result for "+JobId);
            return true;
        }
        if (!properties.isSet("Emboss_Params")) {
            setStatus(status_error, "No params found");
            return false;
        }
        return true;
    }
    
    @Override
    public void init_createInput() {
        //--Do notthing
    }
    
    @Override
    public boolean do_run() throws Exception {
        if (this.JobId.isEmpty()) {
            this.RunNormalWebService();
        } else {
            this.RunRetreiveWebService();
        }
        return true;
    }
    
    
    public void RunNormalWebService() throws Exception {
        setStatus(Config.status_running,"Running "+properties.getName());
        //--Initialize some variables here
        WSEmbossClient client = new WSEmbossClient();
        InputParams param =loadParams(properties.get("Emboss_Params"));
        
        //--Specific Client variables from properties
        param.setAsync(true);
        param.setEmail(config.get("email"));
        String dataOption = null;
        if(hasOption(properties.get("Emboss_Params"),"sequence")) {
            dataOption = getOptionValue(properties.get("Emboss_Params"),"sequence");
        }
        Vector inputsVec = loadData(dataOption);
        // Check for input files, and map them into Data inputs
        client.moveFilesToData(param, inputsVec);
        Data[] inputs = new Data[inputsVec.size()];
        for(int i = 0; i < inputsVec.size(); i++) {
            inputs[i] = (Data)inputsVec.elementAt(i);
        }
        //Data[] input=new Data[1];
        //input[0]=new Data();
        //input[0].setType("sequence");
        //input[0].setContent(multi.ouputFastaWithSequenceID());
        
        //--Run the Job with 3 retry
        String wsJobId=client.runApp(param,inputs);
        setStatus(status_running,"JobId: "+wsJobId);
        String wsstatus="PENDING";
        int retry=0;
        boolean wsdone=false;
        while(!wsdone&&retry<3) {
            wsstatus = client.checkStatus(wsJobId);
            //--Output information
            setStatus(Config.status_running,"Running "+properties.getName()+" for "+getRunningTime()+" ms status: "+wsstatus+" jobib: "+wsJobId+"\n");
            //--CASE 1: Still computing
            if (wsstatus.equals("RUNNING") || wsstatus.equals("PENDING")) Thread.sleep(10000);
            //--CASE 2: Error
            if (wsstatus.equals("ERROR") || wsstatus.equals("NOT)FOUND")) {
                setStatus(Config.status_error,"***Error with "+properties.getName()+" at "+getRunningTime()+" ms status: "+wsstatus+" jobib: "+wsJobId+"\n");
                wsdone=true;
            }
            //--CASE 3: Done
            if (wsstatus.equals("DONE")) {
                String[] outfiles=client.getResults(wsJobId, null, null);
                if (outfiles==null||outfiles[0]==null) {
                    retry++;
                    setStatus(Config.status_running,"Warning : No results retreive from EBI Website. Retry "+retry+"\n");
                    Thread.sleep(15000);
                } else {
                    for (String filename:outfiles) {
                        if (filename!=null) {
                            if (filename.endsWith(".aln")) {
                                Alignment newmulti=new Alignment();
                                newmulti.loadFromFile(filename);
                                newmulti.setName(properties.getName()+" ("+configuration.Util.returnCurrentDateAndTime()+")");
                                newmulti.setNote(filename);
                                newmulti.saveToDatabase();
                                properties.put("output_alignment_id", newmulti.getId());
                            }
                            if (filename.endsWith(".dnd")) {
                                //--This si the guide tree
                                MultipleTrees multitmp=new MultipleTrees();
                                multitmp.readNewick(filename);
                                multitmp.setMultiplesequences_id(properties.getInputID("input_multiplesequences_id"));
                                multitmp.replaceSequenceIDwithNames();
                                multitmp.setNote(properties.getName()+" ("+Util.returnCurrentDateAndTime()+")");
                                multitmp.saveToDatabase();
                                for (Tree tree:multitmp.getTree()) properties.put("output_tree_id", tree.getId());
                            }
                            if (filename.endsWith(".txt")) {
                                //--This is a running description with score
                                Text unknown=new Text(filename);
                                unknown.setNote("Running information from "+properties.getName());
                                unknown.setRunProgram_id(id);
                                unknown.saveToDatabase();
                                properties.put("output_text_id", unknown.getId());
                            }
                            if (!properties.getBoolean("debug")) Util.deleteFile(filename);
                        }
                    }
                    wsdone=true;
                }
            } //--End DONE
        } //-- End while
    }
    
    public void RunRetreiveWebService() throws Exception {
        //--Initialize some variables here
        WSEmbossClient client = new WSEmbossClient();
        String wsJobId = properties.get("JobId");
        
        //-- Retrieve the Alignment
        setStatus(Config.status_running,"Retrieving JobID "+wsJobId);
        String wsstatus="PENDING";
        int retry=0;
        boolean wsdone=false;
        while(!wsdone&&retry<3) {
            wsstatus = client.checkStatus(wsJobId);
            //--Output information
            setStatus(Config.status_running,"Running "+properties.getName()+" for "+getRunningTime()+" ms status: "+wsstatus+" jobib: "+wsJobId+"\n");
            //--CASE 1: Still computing
            if (wsstatus.equals("RUNNING") || wsstatus.equals("PENDING")) Thread.sleep(10000);
            //--CASE 2: Error
            if (wsstatus.equals("ERROR") || wsstatus.equals("NOT)FOUND")) {
                setStatus(Config.status_error,"***Error with "+properties.getName()+" at "+getRunningTime()+" ms status: "+wsstatus+" jobib: "+wsJobId+"\n");
                wsdone=true;
            }
            //--CASE 3: Done
            if (wsstatus.equals("DONE")) {
                String[] outfiles=client.getResults(wsJobId, null, null);
                if (outfiles==null||outfiles[0]==null) {
                    retry++;
                    setStatus(Config.status_running,"Warning : No results retreive from EBI Website. Retry "+retry+"\n");
                    Thread.sleep(15000);
                } else {
                    for (String filename:outfiles) {
                        if (filename!=null) {
                            if (filename.endsWith(".aln")) {
                                Alignment newmulti=new Alignment();
                                newmulti.loadFromFile(filename);
                                newmulti.setName(properties.getName()+" ("+configuration.Util.returnCurrentDateAndTime()+")");
                                newmulti.setNote(filename);
                                newmulti.saveToDatabase();
                                properties.put("output_alignment_id", newmulti.getId());
                                addOutput(newmulti);
                            }
                            if (filename.endsWith(".dnd")) {
                                //--This si the guide tree
                                MultipleTrees multi=new MultipleTrees();
                                multi.readNewick(filename);
                                multi.setMultiplesequences_id(properties.getInputID("input_multiplesequences_id"));
                                multi.replaceSequenceIDwithNames();
                                multi.setNote(properties.getName()+" ("+Util.returnCurrentDateAndTime()+")");
                                multi.saveToDatabase();
                                for (Tree tree:multi.getTree()) properties.put("output_tree_id", tree.getId());
                                addOutput(multi);
                            }
                            if (filename.endsWith(".txt")) {
                                //--This is a running description with score
                                Text unknown=new Text(filename);
                                unknown.setNote("Running information from "+properties.getName());
                                unknown.setRunProgram_id(id);
                                unknown.saveToDatabase();
                                properties.put("output_text_id", unknown.getId());
                                addOutput(unknown);
                            }
                            if (!properties.getBoolean("debug")) Util.deleteFile(filename);
                        }
                    }
                }
            } //--End Done
        } //-- End while
    }
    
    /**
     * Note: Code From WSEmboss (at EBI)
     * Create data input structure from the option value.
     *
     * The option can be either an entry identifier in the
     * format dbname:id or a filename. If a filename is used
     * the contents of the file will be used as the input data.
     * This method makes no attempt to parse the input file to
     * handle it as individual sequences.
     *
     * @param fileOptionStr Filename or entry identifier.
     * @return Data structure for use with runApp().
     * @throws IOException
     */
    public static Vector loadData(String fileOptionStr) throws IOException {
        Vector inputsVec = new Vector();
        if(fileOptionStr != null) {
            Data input= new Data();
            input.setType("sequence");
            if(new File(fileOptionStr).exists()) {
                String fileContent = FileUtil.readFile(new File(fileOptionStr));
                input.setContent(fileContent);
            } else { // Entry Id
                // Do not need to pass entry Id content here
                // The params will pass the entry Id
                input.setContent(fileOptionStr);
            }
            inputsVec.add(input);
        }
        return inputsVec;
    }
    
    
    
    public static InputParams loadParams(String line) throws IOException {
        InputParams params = new InputParams();
        // Standard options
        if (hasOption(line,"email")) params.setEmail(getOptionValue(line,"email"));
        params.setAsync(new Boolean(true)); // Always perform an async submission
        // Tool specific options
        if (hasOption(line,"blocktype")) params.setBlocktype(getOptionValue(line,"blocktype"));
        if (hasOption(line,"entry")) params.setEntry(getOptionValue(line,"entry"));
        if (hasOption(line,"sequence")) params.setSequence(getOptionValue(line,"sequence"));
        if (hasOption(line,"files")) params.setFiles(getOptionValue(line,"files"));
        if (hasOption(line,"vectorfile")) params.setVectorfile(getOptionValue(line,"vectorfile"));
        if (hasOption(line,"nummismatches")) params.setNummismatches(Integer.valueOf(getOptionValue(line,"nummismatches")));
        if (hasOption(line,"program")) params.setProgram(getOptionValue(line,"program"));
        if (hasOption(line,"minlen")) params.setMinlen(Integer.valueOf(getOptionValue(line,"minlen")));
        if (hasOption(line,"saltconc")) params.setSaltconc(new Float(getOptionValue(line,"saltconc")));
        if (hasOption(line,"primersfile")) params.setPrimersfile(getOptionValue(line,"primersfile"));
        if (hasOption(line,"target")) params.setTarget(getOptionValue(line,"target"));
        if (hasOption(line,"gaplimit")) params.setGaplimit(Integer.valueOf(getOptionValue(line,"gaplimit")));
        if (hasOption(line,"wordsize")) params.setWordsize(Integer.valueOf(getOptionValue(line,"wordsize")));
        if (hasOption(line,"shiftincrement")) params.setShiftincrement(Integer.valueOf(getOptionValue(line,"shiftincrement")));
        if (hasOption(line,"count")) params.setCount(Integer.valueOf(getOptionValue(line,"count")));
        if (hasOption(line,"second")) params.setSecond(getOptionValue(line,"second"));
        if (hasOption(line,"btype")) params.setBtype(getOptionValue(line,"btype"));
        if (hasOption(line,"name")) params.setName(getOptionValue(line,"name"));
        if (hasOption(line,"minpc")) params.setMinpc(new Float(getOptionValue(line,"minpc")));
        if (hasOption(line,"match")) params.setMatch(Integer.valueOf(getOptionValue(line,"match")));
        if (hasOption(line,"estsequence")) params.setEstsequence(getOptionValue(line,"estsequence"));
        if (hasOption(line,"description")) params.setDescription(getOptionValue(line,"description"));
        if (hasOption(line,"winsize")) params.setWinsize(Integer.valueOf(getOptionValue(line,"winsize")));
        if (hasOption(line,"minpallen")) params.setMinpallen(Integer.valueOf(getOptionValue(line,"minpallen")));
        if (hasOption(line,"regions")) params.setRegions(getOptionValue(line,"regions"));
        if (hasOption(line,"emin")) params.setEmin(Integer.valueOf(getOptionValue(line,"emin")));
        if (hasOption(line,"sitelen")) params.setSitelen(Integer.valueOf(getOptionValue(line,"sitelen")));
        if (hasOption(line,"number")) params.setNumber(Integer.valueOf(getOptionValue(line,"number")));
        if (hasOption(line,"threshold")) params.setThreshold(Integer.valueOf(getOptionValue(line,"threshold")));
        if (hasOption(line,"seqall")) params.setSeqall(getOptionValue(line,"seqall"));
        if (hasOption(line,"range")) params.setRange(getOptionValue(line,"range"));
        if (hasOption(line,"frames")) params.setFrames(getOptionValue(line,"frames"));
        if (hasOption(line,"firstset")) params.setFirstset(getOptionValue(line,"firstset"));
        if (hasOption(line,"genomesequence")) params.setGenomesequence(getOptionValue(line,"genomesequence"));
        if (hasOption(line,"to")) params.setTo(Integer.valueOf(getOptionValue(line,"to")));
        if (hasOption(line,"maxrange")) params.setMaxrange(Integer.valueOf(getOptionValue(line,"maxrange")));
        if (hasOption(line,"boutfeat")) params.setBoutfeat(getOptionValue(line,"boutfeat"));
        if (hasOption(line,"from")) params.setFrom(Integer.valueOf(getOptionValue(line,"from")));
        if (hasOption(line,"minrange")) params.setMinrange(Integer.valueOf(getOptionValue(line,"minrange")));
        if (hasOption(line,"pos")) params.setPos(Integer.valueOf(getOptionValue(line,"pos")));
        if (hasOption(line,"overlap")) params.setOverlap(new Boolean(true));
        if (hasOption(line,"emax")) params.setEmax(Integer.valueOf(getOptionValue(line,"emax")));
        if (hasOption(line,"graphlb")) params.setGraphlb(getOptionValue(line,"graphlb"));
        if (hasOption(line,"dnaconc")) params.setDnaconc(new Float(getOptionValue(line,"dnaconc")));
        if (hasOption(line,"secondset")) params.setSecondset(getOptionValue(line,"secondset"));
        if (hasOption(line,"skip")) params.setSkip(Integer.valueOf(getOptionValue(line,"skip")));
        if (hasOption(line,"exclude")) params.setExclude(getOptionValue(line,"exclude"));
        if (hasOption(line,"gapopen")) params.setGapopen(new Float(getOptionValue(line,"gapopen")));
        if (hasOption(line,"motif")) params.setMotif(getOptionValue(line,"motif"));
        if (hasOption(line,"search")) params.setSearch(getOptionValue(line,"search"));
        if (hasOption(line,"bsequence")) params.setBsequence(getOptionValue(line,"bsequence"));
        if (hasOption(line,"aoutfeat")) params.setAoutfeat(getOptionValue(line,"aoutfeat"));
        if (hasOption(line,"infile")) params.setInfile(getOptionValue(line,"infile"));
        if (hasOption(line,"directory")) params.setDirectory(getOptionValue(line,"directory"));
        if (hasOption(line,"asequence")) params.setAsequence(getOptionValue(line,"asequence"));
        if (hasOption(line,"maxpallen")) params.setMaxpallen(Integer.valueOf(getOptionValue(line,"maxpallen")));
        if (hasOption(line,"orfml")) params.setOrfml(Integer.valueOf(getOptionValue(line,"orfml")));
        if (hasOption(line,"gap")) params.setGap(Integer.valueOf(getOptionValue(line,"gap")));
        if (hasOption(line,"mismatch")) params.setMismatch(Integer.valueOf(getOptionValue(line,"mismatch")));
        if (hasOption(line,"order")) params.setOrder(getOptionValue(line,"order"));
        if (hasOption(line,"posticks")) params.setPosticks(getOptionValue(line,"posticks"));
        if (hasOption(line,"tolerance")) params.setTolerance(new Float(getOptionValue(line,"tolerance")));
        if (hasOption(line,"weight")) params.setWeight(Integer.valueOf(getOptionValue(line,"weight")));
        if (hasOption(line,"minrepeat")) params.setMinrepeat(Integer.valueOf(getOptionValue(line,"minrepeat")));
        if (hasOption(line,"minweight")) params.setMinweight(new Float(getOptionValue(line,"minweight")));
        if (hasOption(line,"windowsize")) params.setWindowsize(Integer.valueOf(getOptionValue(line,"windowsize")));
        if (hasOption(line,"besthits")) params.setBesthits(new Boolean(true));
        if (hasOption(line,"maxrepeat")) params.setMaxrepeat(Integer.valueOf(getOptionValue(line,"maxrepeat")));
        if (hasOption(line,"word")) params.setWord(Integer.valueOf(getOptionValue(line,"word")));
        if (hasOption(line,"shift")) params.setShift(Integer.valueOf(getOptionValue(line,"shift")));
        if (hasOption(line,"type")) params.setType(getOptionValue(line,"type"));
        if (hasOption(line,"graph")) params.setGraph(getOptionValue(line,"graph"));
        if (hasOption(line,"window")) params.setWindow(Integer.valueOf(getOptionValue(line,"window")));
        if (hasOption(line,"ruler")) params.setRuler(new Boolean(true));
        if (hasOption(line,"enzymes")) params.setEnzymes(getOptionValue(line,"enzymes"));
        if (hasOption(line,"enzyme")) params.setEnzyme(getOptionValue(line,"enzyme"));
        if (hasOption(line,"compdatafile")) params.setCompdatafile(getOptionValue(line,"compdatafile"));
        if (hasOption(line,"sequences")) params.setSequences(getOptionValue(line,"sequences"));
        if (hasOption(line,"gapextend")) params.setGapextend(new Float(getOptionValue(line,"gapextend")));
        if (hasOption(line,"posblocks")) params.setPosblocks(getOptionValue(line,"posblocks"));
        if (hasOption(line,"letters")) params.setLetters(getOptionValue(line,"letters"));
        if (hasOption(line,"first")) params.setFirst(getOptionValue(line,"first"));
        if (hasOption(line,"atype")) params.setAtype(getOptionValue(line,"atype"));
        if (hasOption(line,"menu")) params.setMenu(getOptionValue(line,"menu"));
        if (hasOption(line,"score")) params.setScore(Integer.valueOf(getOptionValue(line,"score")));
        if (hasOption(line,"mismatchpercent")) params.setMismatchpercent(Integer.valueOf(getOptionValue(line,"mismatchpercent")));
        if (hasOption(line,"graphout")) params.setGraphout(getOptionValue(line,"graphout"));
        if (hasOption(line,"pattern")) params.setPattern(getOptionValue(line,"pattern"));
        if (hasOption(line,"point")) params.setPoint(getOptionValue(line,"point"));
        if (hasOption(line,"format")) params.setFormat(getOptionValue(line,"format"));
        if (hasOption(line,"seqcomp")) params.setSeqcomp(getOptionValue(line,"seqcomp"));
        if (hasOption(line,"block")) params.setBlock(getOptionValue(line,"block"));
        if (hasOption(line,"cfile")) params.setCfile(getOptionValue(line,"cfile"));
        if (hasOption(line,"minoe")) params.setMinoe(new Float(getOptionValue(line,"minoe")));
        return params;
    }
    
    public static boolean hasOption(String line, String option) {
        return line.contains(option);
    }
    
    public static String getOptionValue(String line, String option) {
        Pattern option_to_search=Pattern.compile(option+"=(.*)\\s",Pattern.CASE_INSENSITIVE);
        Matcher m=option_to_search.matcher(line);
        if (m.find()) {
            return m.group(1);
        }
        return "";
    }
    
}
