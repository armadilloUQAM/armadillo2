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

import configuration.Config;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * This is a class to POST data to the Web
 * Note: Blocking class
 * @author Etienne Lord
 * @since June 2010
 */
public class www_post {
    public static Config config=new Config();
    private BufferedReader in_connection;
    private boolean in_connection_open = false;
    URL url=null;
    HttpURLConnection urlc=null;
    public StringBuilder buffer=new StringBuilder(); //--Return_buffer
    private static HashMap post_data=new HashMap();  //--Value which will be UTF-8 encoded
    boolean debug=false;

    public www_post() {}

    /**
     * This will do the POST an initialise the in_connection
     * @param server_cgi_url
     * @param header
     * @return
     */
    public boolean do_post(String server_cgi_url, String header)  {
        //-- 1. construct the data
        StringBuilder data=new StringBuilder();
        
        for (Object key:post_data.keySet()) {
           if (key!=null&&!((String)key).isEmpty()) {
                try {
                    data.append(URLEncoder.encode((String)key, "UTF-8"));
                    data.append("=");
                    data.append(URLEncoder.encode((String)post_data.get(key), "UTF-8"));
                    data.append("&");
                } catch(Exception e) {Config.log("Error in encoding post : "+e.getMessage());}
            }
           }  
        byte[] postDataBytes=new byte[0];
        if (data.lastIndexOf("&")==data.length()-1) {
            data.deleteCharAt(data.length()-1);
//            System.out.println("true");
//            return false;
        }
       try {
        postDataBytes = data.toString().getBytes("UTF-8");
       } catch(Exception e) {}
       
        System.out.println(data.toString());
       
       //-- 2. POST THE DATA
       try {
            url=new URL(server_cgi_url);
       } catch(Exception e) {Config.log("Error in server url: "+server_cgi_url);return false;}
       try {
           urlc=( HttpURLConnection)url.openConnection();
           urlc.setDoOutput(true);
            urlc.setRequestMethod("POST");
            urlc.setRequestProperty("timeout", "300");
            urlc.setRequestProperty("request_fulluri", "true");
            urlc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlc.setRequestProperty("Content-Length", String.valueOf(data.toString().length()));       
              urlc.getOutputStream().write(postDataBytes);
          // PrintWriter pw=new PrintWriter(new OutputStreamWriter(urlc.getOutputStream()));
           //pw.println(header);
           //pw.println(data);
           //pw.flush();
          // pw.close();
       } catch(Exception e) {Config.log("Unable to connect to server url: "+server_cgi_url);return false;}
        try {
            in_connection = new BufferedReader(new InputStreamReader(urlc.getInputStream(),"UTF-8"));
            this.in_connection_open=true;
            String inputLine="";
            while ((inputLine = in_connection.readLine()) != null) {
                    System.out.println(inputLine);
                    buffer.append(inputLine+"\n");
            }
        } catch (IOException ex) {
            Config.log("Unable to get answer from server: "+server_cgi_url);
            return false;
        }
        return true;
    }

    /**
     * Put a new key-value to POST
     * Note: an existing key will be replaced...
     * @param key
     * @param value
     */
    public void put(Object key, Object value) {
        post_data.put(key, value);
    }

    /**
     * Remove a key from the POST
     * @param key
     */
    public void remove(Object key) {
        post_data.remove(key);
    }

    /**
     * Remove all key from the POST
     */
    public void clear() {
        post_data.clear();
    }
}
