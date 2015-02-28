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

import biologic.Results;
import biologic.TextFile;
import biologic.Unknown;
import biologic.seqclasses.eutil;
import configuration.Config;
import configuration.Util;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import program.RunProgram;
import workflows.workflow_properties;

/**
 * This is an Interface to run eUtils from NCBI
 * @author Etienne Lord
 */


public class eutils extends RunProgram {

    String outfile="";

    ////////////////////////////////////////////////////////////////////////////
    /// Variables
  

 public eutils(workflow_properties properties) {
        super(properties);
        execute();
 }

    @Override
    public boolean init_checkRequirements() {
        //--We verify some of the inputs only if we load value from them
//        int TextID=properties.getInputID("Text");
//        int ResultsID=properties.getInputID("Results");
//        int TextFileID=properties.getInputID("TextFile");
//            if (TextID==0&&ResultsID==0&&TextFileID==0&&(properties.isSet("AutomaticLoadWebEnvAndQuery")||properties.isSet("AutomaticLoadID")||properties.isSet("AutomaticLoadTerm"))) {
//                setStatus(this.status_BadRequirements,"Warning. No input found.");
//                return false;
//            }
        return true;
    }



    @Override
    public void init_createInput() {
        
    }


    @Override
    public String[] init_createCommandLine() {
        return new String[0];
    }


    @Override
    public boolean do_run() throws Exception {
        this.setStatus(this.status_running, this.properties.getName());
        Unknown inputDOWN=new Unknown();
        Unknown inputUP=new Unknown();
        //--Handle differents case depending on the inputs found
        Vector<Integer> TextFileID_DOWN=properties.getInputID("TextFile", PortInputDOWN);
        Vector<Integer> TextID_DOWN=properties.getInputID("Text", PortInputDOWN);
        Vector<Integer> ResultsID_DOWN=properties.getInputID("Results", PortInputDOWN);
         Vector<Integer> TextFileID_UP=properties.getInputID("TextFile", PortInputUP);
        Vector<Integer> TextID_UP=properties.getInputID("Text", PortInputUP);
        Vector<Integer> ResultsID_UP=properties.getInputID("Results", PortInputUP);
        //--We load but we don't save the file
        //--Note: Only one input will be use with this order
        if (TextFileID_DOWN.size()>0) {
            //--Load the textFile into an Unknown structure
            TextFile tx=new TextFile(TextFileID_DOWN.get(0));
            inputDOWN=new Unknown(tx.getFile());
        }
        if (TextID_DOWN.size()>0) {
            inputDOWN=new Unknown(TextID_DOWN.get(0));
        }
        if (ResultsID_DOWN.size()>0) {
            inputDOWN=new Unknown(ResultsID_DOWN.get(0));
        }
        //--Input UP for list
        if (TextFileID_UP.size()>0) {
            TextFile tx=new TextFile(TextFileID_UP.get(0));
            inputUP=new Unknown(tx.getFile());
        }
        if (TextID_UP.size()>0) {
            inputUP=new Unknown(TextID_UP.get(0));
        }
        if (ResultsID_UP.size()>0) {
            inputUP=new Unknown(ResultsID_UP.get(0));
        }


        eutil e=new eutil();
        e.setOutputToDisk(true);
        e.setEngine(properties.get("Engine"));
        if (properties.isSet("db")) e.setDatabase(properties.get("db"));
        if (properties.isSet("dbfrom")) e.setDatabaseFrom(properties.get("dbfrom")); //--Advanced
        //--WebEnv & QueryKey
        if (properties.getBoolean("AutomaticLoadWebEnvAndQuery")) {
            properties.put("WebEnv", e.returnWebEnv(inputDOWN.getUnknown()));
            properties.put("Query", e.returnQueryKey(inputDOWN.getUnknown()));
            if (properties.get("WebEnv").isEmpty()||properties.get("Query").isEmpty()) {
                setStatus(status_error,"Warning. Could not find a valid WEbEnv or Query key from the previous results\n");
                return false;
            }

        }
        if (properties.getBoolean("AutomaticLoadID")) {
           //--Load a list of ID
            //--Check up first
            if (!inputUP.getUnknown().isEmpty()) {
                e.setIDList(loadID(inputUP));
            } else {
            //--Otherwise assume its from the bottom
                e.setIDList(e.returnIDList(inputDOWN.getUnknown()));
            }
        } else {
            if (properties.isSet("ID")) e.setId(properties.get("ID"));            
        }
         if (properties.getBoolean("AutomaticLoadTerm")) {
           //--Load a list of ID
            //--Check up first
            if (!inputUP.getUnknown().isEmpty()) {
                e.setIDList(loadID(inputUP));
            } else {
            //--Otherwise assume its from the bottom
                e.setIDList(e.returnIDList(inputDOWN.getUnknown()));
            }
        } else {
            if (properties.isSet("Term")) e.setTerm(properties.get("Term"));
        }
        if (properties.isSet("WebEnv")) e.setWebEnv(properties.get("WebEnv"));
        if (properties.isSet("Query")) e.setQuery_key(properties.get("Query"));
        //--RetMax
        if (properties.isSet("RetMax")) e.setRetmax(properties.get("RetMax"));
        //--EntrezDate
        if (properties.isSet("Entrezdate")) e.setRelEntrezdate(properties.get("Entrezdate"));
        //--RetStart (usefull if we want to get a certains amount of data
        if (properties.isSet("RetStart")) e.setRetmax(properties.get("RetStart"));
        if (properties.isSet("Strand")) e.setStrand(properties.get("Strand"));
        if (properties.isSet("Seq_Stop")) e.setSeq_stop(properties.get("Seq_Stop"));
        if (properties.isSet("Seq_Start")) e.setSeq_start(properties.get("Seq_Start"));
        if (properties.isSet("Rettype")) e.setRettype(properties.get("Rettype"));
        if (properties.isSet("Retmode")) e.setRetmode(properties.get("Retmode"));
        
        outfile=e.getEngine()+".txt";
        e.setUseHistory(properties.getBoolean("UseHistory"));       
        setStatus(status_running, "Searching Ncbi...");
        //--Display some infos
        setStatus(status_running, "Database :"+e.getDatabase());
        setStatus(status_running, "Term     :"+e.getTerm());
        Util.deleteFile(outfile);
        e.execute(outfile);
        setStatus(status_running, e.toString());
        //--Parse here       
        return true;
    }

    @Override
    public void post_parseOutput() { 
        if (Util.FileExists(outfile)) {
            Results outputtext=new Results(outfile);
            outputtext.setName("Results from "+properties.getName()+" at "+Util.returnCurrentDateAndTime());
            outputtext.setNote("Results from "+properties.getName()+" at "+Util.returnCurrentDateAndTime());
            outputtext.saveToDatabase();        
            properties.put("output_results_id", outputtext.getId());
            Util.deleteFile(outfile);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    /// Functions

    Vector<String>loadID(Unknown txt) {
        System.out.println("Loading EUtils ID...");
        eutil dummy = new eutil();              //--Use to process id from Ncbi
        Vector<String>tmp=new Vector<String>(); //--Return buffer
        //--1. No text
        if (txt.getUnknown().isEmpty()) {
            System.out.println("Found no ids...");
            return new Vector<String>();
        }
        //--2. XML from Ncbi
        Pattern pXML=Pattern.compile("<?xml version=");
        Matcher m=pXML.matcher(txt.getUnknown());
        if (m.find()) return dummy.returnIDList(txt.getUnknown());
        //--3. Text either comma separed, line break, etc.
        try {
        String[] buffer_comma=txt.getUnknown().split(",");
        String[] buffer_point=txt.getUnknown().split(";");
        String[] buffer_line=txt.getUnknown().split("\n");
        //--We take the best one
        if (buffer_comma.length>buffer_point.length&&buffer_comma.length>buffer_line.length) {
            for (int i=0; i<buffer_comma.length;i++) tmp.add(buffer_comma[i].trim());            
        }
        if (buffer_point.length>buffer_comma.length&&buffer_point.length>buffer_line.length) {
            for (int i=0; i<buffer_point.length;i++) tmp.add(buffer_point[i].trim());            
        }
        if (buffer_line.length>buffer_comma.length&&buffer_line.length>buffer_point.length) {
            for (int i=0; i<buffer_line.length;i++) tmp.add(buffer_line[i].trim());            
        }
        } catch(Exception e) {
            Config.log("Error in processing IDS list in eutils\n"+e.getMessage()+"\n");
        }        
        return tmp;
    }


  }
    

