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

//
// Etienne Lord 2010
// A class to download or post to an URL
//
// WARNING : Blocking class
// NOTE: Run in a thread
//

import configuration.Config;
import java.io.*;
import java.net.*;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.regexp.RE;


public class httpconnection {
    public static Config config=new Config();

    ////////////////////////////////////////////////////////////////////////////
    /// Internal vatiables
    
    public StringBuilder buffer=new StringBuilder(); //--Return_buffer from the last Qblast
    private BufferedReader in_connection;
    private boolean in_connection_open = false;
    private PrintWriter output;
    private String composite_url="";
    URL url;

    ////////////////////////////////////////////////////////////////////////////
    /// Constant

    final static String default_url="http://www.life.umd.edu/labs/delwiche/alignments/rbcLgb7-95.distrib.mac.txt"; //add the good eutils here...
    private boolean outputToDisk=false; //--Setting to true will bypass saving to buffer : Note: valid only if execute(filename) is called, otherwise no result
    private String filename="";

   
    public httpconnection() {}

    /**
     * 
     * @return a String representing the eUtil results
     */
    public boolean download(String url) {
        this.composite_url=url;
        return download();
    }

    /**
     * Execute the eUtils query and save to filename
     * @param filename
     */
    public boolean download(String url, String filename) {
            this.composite_url=url;
            this.filename=filename;
            boolean b=download();
             if (!this.isOutputToDisk()) saveToFile(filename);
             return b;
    }

    ////////////////////////////////////////////////////////////////////////////
    /// Internal functions

 	/**
	* create a Bufferedreader that read from the open url
	* The parameter of the search term research can be setup using the
	* various command
	*/
        private boolean download()  {
                 buffer=new StringBuilder();
                 //--1. Encode url?
                                  
                //--2. Do the work
		try {
			url= new URL(composite_url);
		        //Config.log("ESearch for: "+search_term);
		} catch (MalformedURLException e) {Config.log("Error: URL is not good.\n"+composite_url);}
		try {
			if (in_connection_open) in_connection.close();
        		
                        in_connection = new BufferedReader(new InputStreamReader(url.openStream()));
			if (in_connection!=null) in_connection_open=true;
                        String inputLine="";
                        if (isOutputToDisk()) output=new PrintWriter(new FileWriter(new File(filename)));
                        while ((inputLine = in_connection.readLine()) != null) {
			  	if (!this.isOutputToDisk()) {
                                    buffer.append(inputLine+"\n");
                                } else {
                                    output.println(inputLine);                                  
                                }
                        }
                        if (isOutputToDisk()) {
                            output.flush();
                            output.close();
                        }
		} catch (IOException e) {Config.log("Unable to download from..."+composite_url);return false;}
                    return true;
	}

        /**
	* create a Bufferedreader that read from the open url
	* The parameter of the search term research can be setup using the
	* various command
	*/
        private String post(String post)  {
                 buffer=new StringBuilder();

                //--4. Do the work
		try {
			url= new URL(composite_url);
		        //Config.log("ESearch for: "+search_term);
		} catch (MalformedURLException e) {Config.log("Error: URL is not good.\n"+composite_url);}
		try {

        		URLConnection connection = url.openConnection();
                        //--Output the post
                        connection.setDoOutput(true);
                        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                        out.write(post);
                        out.close();
                        //--Read the answer
                        in_connection = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			if (in_connection!=null) in_connection_open=true;
                        String inputLine="";
                        if (isOutputToDisk()) output=new PrintWriter(new FileWriter(new File(filename)));
                        while ((inputLine = in_connection.readLine()) != null) {
			  	if (!this.isOutputToDisk()) {
                                    buffer.append(inputLine+"\n");
                                } else {
                                    output.println(inputLine);
                                }
                        }
                        if (isOutputToDisk()) {
                            output.flush();
                            output.close();
                        }
		} catch (IOException e) {Config.log("Unable to download from..."+composite_url);}
                    return buffer.toString();
	}


		
     public String getResult() {
        return buffer.toString();
    }

    boolean FileExists(String filename) {
     File f = new File(filename);
     return f.exists();
    }




    public boolean saveToFile(String filename) {
        try {
            PrintWriter pw=new PrintWriter(new FileWriter(new File(filename)));
            pw.println(buffer.toString());
            pw.close();
            return true;
        } catch(Exception e) {return false;}
    }

    /**
     * @return the outputToDisk
     */
    public boolean isOutputToDisk() {
        return outputToDisk;
    }

    /**
     * @param outputToDisk the outputToDisk to set
     */
    public void setOutputToDisk(boolean outputToDisk) {
        this.outputToDisk = outputToDisk;
    }


  
}
