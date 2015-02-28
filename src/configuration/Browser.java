
package configuration;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * Code inspired by Ben Fry: Visualizing Data to download from a web page
 * @author Etienne Lord
 * 
 */
public class Browser {

    ////////////////////////////////////////////////////////////////////////////
    /// Variables
    
    private HttpClient client;
    private Logger logger;
    public InputStream last_stream = null; //--Last loaded String
    public boolean debug = true;          //--debug?
    
    ////////////////////////////////////////////////////////////////////////////
    /// Constant
    
    String USER_AGENT = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)";
    
    public Browser() {
        
        //BasicConfigurator.configure();
        logger = Logger.getAnonymousLogger();
        logger.setLevel(Level.SEVERE);
        
        client = new HttpClient();
        client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
        
        
    }
    
    public String load(String iurl) {
        HttpMethod method = new GetMethod(iurl);
        method.setFollowRedirects(true);
        method.setRequestHeader(new Header("USER_AGENT", USER_AGENT));
        method.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
        
        String responseBody = null;
        
        try {
            client.executeMethod(method);                        
            System.out.println(method.getQueryString());
            responseBody=method.getResponseBodyAsString();
            last_stream=method.getResponseBodyAsStream();
            
            
        } catch(Exception e) {
            return "Error "+e.getMessage();
        }
        method.releaseConnection();
        return responseBody;
    }
    
    public boolean outputFile(String filename) {
        try {           
           char[] buffer = new char[1024];
           OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(filename),"UTF-8");			
           BufferedReader reader = new BufferedReader(new InputStreamReader(last_stream, "UTF-8"));
           int n;
           
            while ((n = reader.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }
               reader.close();
               out.close();            
            } catch(Exception e) {
                if (debug) e.printStackTrace();
                return false;            
            } 
            
        return true;
        
    }
    
}
