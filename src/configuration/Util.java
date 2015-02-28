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

package configuration;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;


/**
 * Collection of util command
 * @author Etienne Lord, Mickael Leclercq
 */
public class Util {
    //Util string
    //* Etienne Lord, Mickael Leclercq
    public static final int Mb=1048576;
    public static final int MaxMb=26214400; //25Mb (we don't try to load fasta file bigger)
    public static int count=0; //--Internal variable for returnCount...
    public static final SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat dateyear = new SimpleDateFormat("yyyy");
    //--Logging function
    public PrintWriter log;
    public boolean log_open=false;
    public String log_filename="";

    public Util() {}

    public Util(String filename) {
        open(filename);
    }





    /**
     * Print to System.out current Memory Allocation and Total System Core
     */
    public static String PrintMemory() {
        String stri="System allocated memory: "+Runtime.getRuntime().totalMemory()/Mb+" MB System free memory: "+Runtime.getRuntime().freeMemory()/Mb+" MB\n"+
                    "System total core: "+Runtime.getRuntime().availableProcessors()+"\n";
         Config.log(stri);
         return stri;
    }

   public static void CleanMemory() {
         Runtime r = Runtime.getRuntime();
         r.gc();
   }

   /**
    * Return the content of the Ressource with the specified name
    * @param name
    * @return
    */
   public String getRessource(String name) {
        String str="";
        try {
            BufferedReader br=new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(name)));
            while(br.ready()) {
                str+=br.readLine()+"\n";
            }
        } catch(Exception e) {}
        return str;
    }

    /**
     * Test if a file exists...
     * @param filename
     * @return true if file Exists
     */
    public static boolean FileExists(String filename) {
             File f = new File(filename);
             if (f==null||f.isDirectory()) return false;
             return f.exists();
    }

    /**
     * Test if a directory exists
     * @param filename
     * @return true if file Exists
     */
    public static boolean DirExists(String filename) {
             File f = new File(filename);
             if (f==null||!f.isDirectory()) return false;
             return f.exists();
    }
    
    /**
     * Create the directory or the directory arborescence 
     * @param directory
     * @return true if succes, false for any other reasons.. (dir exists...)
     */
    public static boolean CreateDir(String directory) {
        try {
            File f=new File(directory);            
            if (DirExists(directory)) return false;
            return (f.mkdirs());           
        } catch(Exception e) {
            return false;
        }
    }
    
    /**
     * Return the file size of a file or 0 if error or directory
     * @param filename
     * @return 
     */
    public static long FileSize(String filename) {
             File f = new File(filename);
             if (f==null||f.isDirectory()) return 0;
             return f.length();
    }

     /**
     * Return a random Value of N number
     * @return a Random String of N random number
     */
    public static String returnRandom(int N) {
        Random r=new Random();
        StringBuilder s=new StringBuilder();
        for (int i=0; i<N; i++) {
            s.append(String.valueOf(r.nextInt(10)));
        }
        return s.toString();
    }

    /**
     * Return a number containing the date and N random number
     * @return a String
     */
    public static String returnRandomAndDate() {
        Calendar today=Calendar.getInstance();
        String tmpdir="";
        //VARIABLE
        String dd=String.valueOf(today.get(Calendar.DAY_OF_MONTH));
        String mm=String.valueOf(today.get(Calendar.MONTH));
        String yyyy=String.valueOf(today.get(Calendar.YEAR));
         Random r=new Random();
        StringBuilder s=new StringBuilder();
        s.append(dd);
        s.append(mm);
        s.append(yyyy);
        s.append(count);
        return s.toString();
    }

    /**
     * This return a time code JJMMYYMMSS used by the hashcode
     * @return
     */
    public static int returnTimeCode() {
        Calendar today=Calendar.getInstance();
        int[] tt={0,0,0,0,0,0};
        tt[0]=today.get(Calendar.DAY_OF_MONTH);
        tt[1]=today.get(Calendar.MONTH);
        tt[2]=today.get(Calendar.HOUR);
        tt[3]=today.get(Calendar.MINUTE);
        return (tt[0]*1000000+tt[1]*10000+tt[2]*100+tt[3]);
    }

    /**
     *
     * @return s A String of the current Date and Time
     */
    public static String returnCurrentDateAndTime() {
        Calendar today=Calendar.getInstance();
        return dateformat.format(today.getTime());

    }

    /**
     *
     * @return s A String of the current Year
     */
    public static String returnCurrentYear() {
        Calendar today=Calendar.getInstance();
        return dateyear.format(today.getTime());

    }
    
    /**
     * Return a number increasing at each time    
     * @return a String
     */
    public static int returnCount() {
        count++;
        int c=returnTimeCode()*100+count;
        //--Endsure positive count...
        if (c<0) c*=-1;
        return c;
    }

    ////////////////////////////////////////////////////////////////////////////
    /// Rapid Files Function

    /**
     * Return the content of a file as an Array of String
     * @param filename
     * @return an Array of String
     */
    public static String[] InputFile(String filename) {
        filename=filename.trim();
        Vector<String>tmp=new Vector<String>();
        try {
            BufferedReader br=new BufferedReader(new FileReader(new File(filename)));
            while(br.ready()) {
                tmp.add(br.readLine());
            }
            br.close();
        } catch(Exception e) {Config.log("Unable to open "+filename); return new String[1];}
        String[] tmp2=new String[tmp.size()];
        int index=0;
        for (String s:tmp) tmp2[index++]=s;
        return tmp2;
    }

    /**
    * Delete a file
    * @param filename (to delete)
    * @return true if success
    */
    public static boolean deleteFile(String filename) {
        System.out.println("Deleting "+filename);
        try {
            File outtree=new File(filename);
            if (outtree.exists()) outtree.delete();
        } catch(Exception e) {return false;}
        return true;
    }

    /**
    * Delete a directory
    * @param directory (to delete)
    * @return true if success
    */
    public static boolean deleteDir(String directory) {
        try {
            File outtree=new File(directory);
            if (outtree.exists()&&outtree.isDirectory()) {
                for (String file:Config.listDir(directory)) {
                    deleteFile(directory+File.separator+file);
                }
                return outtree.delete();
            }
        } catch(Exception e) {return false;}
        return false;
    }

    /**
     * Return a unique filename
     * @param filename
     * @return
     */
    public static String unique(String filename) {
        return filename+"_"+Util.returnCount();
    }


    /** Fast & simple file copy. */
    public static void copy(File source, File dest) throws IOException {
         FileChannel in = null, out = null;
         try {
              in = new FileInputStream(source).getChannel();
              out = new FileOutputStream(dest).getChannel();

              long size = in.size();
              MappedByteBuffer buf = in.map(FileChannel.MapMode.READ_ONLY, 0, size);

              out.write(buf);

         } catch (Exception e) {
          e.printStackTrace();
         }
         if (in != null) in.close();
         if (out != null) out.close();
    }
    
     /** Fast & simple file copy. */
    public static void copy(String source, String dest) throws IOException {
         copy(new File(source), new File(dest));
    }

    public Vector<String> read(String filename) {
        Vector<String>tmp=new Vector<String>();
        try {
            BufferedReader br=new BufferedReader(new FileReader(new File(filename)));
            while (br.ready()) {
                tmp.add(br.readLine());
            }
        } catch(Exception e) {return tmp;}
        return tmp;
    }

    ////////////////////////////////////////////////////////////////////////////
    /// Util Print to File function
    
    /**
     * Open the filename in Replace Mode
     * 
     * @param filename 
     */
    public void open(String filename)  {
           log_filename=filename;
           try {
                log=new PrintWriter(new FileWriter(new File(filename)));
                if (log!=null) {
                    log_open=true;
                 }
           } catch(Exception e) {Config.log("*** Error: Unable to open "+filename);log_open=false;}
       }
    
    /**
     * Open the filename in Append Mode
     * @param filename 
     */
    public void openAppend(String filename)  {
           log_filename=filename;
           try {
                log=new PrintWriter(new FileWriter(new File(filename), true));
                if (log!=null) {
                    log_open=true;
                 }
           } catch(Exception e) {Config.log("*** Error: Unable to open "+filename);log_open=false;}
       }

    /**
     * Close the file
     */ 
    public void close() {
           try {
            if (log_open) {
                log.close();
                log_open=false;
            }
           } catch(Exception e){Config.log("*** Error: Unable to close "+log_filename);}
       }
       
       public void reopen() {
           open(log_filename);
       }

       public void println(String str) {
           if (log_open) log.println(str);
       }

       public void print(String str) {
           if (log_open) log.print(str);
       }
       
       /////////////////////////////////////////////////////////////////////////
       /// MISC FUNCTION
       
       
         /**
        * This Download the url (file) to the filename
        * @param url
        * @param filename
        * @return True if success
        */
       public static boolean download(String url, String filename) {
        try {
            URL u = new URL(url);
            URLConnection uc = u.openConnection();
            InputStream in = uc.getInputStream();
            OutputStream out=new FileOutputStream(filename);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
            }
            in.close();
            out.close();
        } catch (Exception e) {return false;}
       return true;
       }

       /**
        * This un-GZIP a the input_filename to the output_filename
        * @param input_filename
        * @param output_filename
        * @return True if success
        */
       public static boolean ungzip(String input_filename, String output_filename) {
           try {
               GZIPInputStream gzipInputStream = new GZIPInputStream(new FileInputStream(input_filename));
               OutputStream out = new FileOutputStream(output_filename);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = gzipInputStream.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
                gzipInputStream.close();
                out.close();
               return true;
           } catch(Exception e) {e.printStackTrace();return false;}
       }

        /**
        * This un-ZIP a the input_filename to the output_filename
        * @param input_filename
        * @param output_filename
        * @return True if success
        */
       public static boolean unzip(String input_filename, String output_filename) {
           try {
               ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(input_filename));
               OutputStream out = new FileOutputStream(output_filename);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = zipInputStream.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
                zipInputStream.close();
                out.close();
               return true;
           } catch(Exception e) {e.printStackTrace();return false;}
       }

     ///////////////////////////////////////////////////////////////////////////
     /// Search position in a String Array

    /**
     * Search the position of a key in an Array
     * Note: We handle null case and length case with exception
     * @param key
     * @param ArrayToSearch
     * @return the key index or -1 if not found
     */
    public static int indexOf(Object key, Object[] ArrayToSearch) {
        try {
            String NotSet="Not Set";           //--Special identifier for NotSet
            if (key.equals(NotSet)) return -1; //--We handle this special case
            for (int i=0; i<ArrayToSearch.length;i++) {
                if (ArrayToSearch[i].equals(key)) return i;
            }
        } catch(Exception e) {return -1;} //--Error
        return -1; //Not found
    }

   /**
    * Transforme a String Vector to a String representation
    * @param V
    * @return a String representing the elements of the Vector<String>
    */
       public static String toString(Vector<Object> V) {
           String s="";
           for (Object stri:V) s+=stri+", ";
           return s;
       }

       /**
    * Transform a String Vector to a String representation
    * @param V
    * @return a String representing the elements of the Vector<String>
    */
       public static String toString(Object[] V) {
           String s="";
           for (Object stri:V) s+=stri+" ";
           return s;
       }

       /**
        * Transform a number in ms to its d,h,m,s representation
        * @param ms (system millisecond)
        * @return a String representation
        */
       public static String msToString(long ms) {
            long d, h, m, s;
            d=ms / 86400000;
            ms=ms-(d*86400000);
            h = ms / 3600000;
            ms=ms-(h*3600000);
            m=ms/60000;
            ms=ms-(m*60000);
            s=ms/1000;
            ms=ms-(s*1000);
            String str="";
            if (d>2000) return ms+" ms "; //hack
            if (d!=0) return d+"d "+h+"h "+m+"m "+s+"s ";
            if (h!=0) return h+"h "+m+"m "+s+"s ";
            if (m!=0) return m+"m "+s+"s ";
            if (s!=0) return s+"s "+ms+"ms ";
            return ms+"ms ";
       }

      
        /**
     * List the files in directory and return filename with the full path
     * @return
     */
    public static Vector<String> listDirWithFullPath(String dir) {
        Vector<String> tmp=new Vector<String>();
        try {
            File f=new File(dir);
            if (!f.isDirectory()) {
                dir+=File.separator;
                f=new File(dir);
            }
            for (String filename:f.list()) tmp.add(f.getAbsolutePath()+File.separator+filename);
        } catch(Exception e) {}
        return tmp;
    }
    
       
       
       /**
        * Recursive fonction to list a directory 
        * Note: Use the Config.listDirFullPath function
        * @param path
        * @return an ArrayList of the full file path
        */
       public static ArrayList<String> recursive_list_dir(String path) {           
       ArrayList<String>filenames = new ArrayList<String>();
       Vector<String>  filename_tmp=listDirWithFullPath(path);        
        for (String fi:filename_tmp)        
        {
            File f=new File(fi);             
            if (f.isDirectory()) {
                filenames.addAll(recursive_list_dir(f.getAbsolutePath()));                
            } else {
                filenames.add(fi);
            }                     
        }
        return filenames;
    }
       
       /**
        * From : http://www.jeffreythompson.org/blog/2012/05/29/easy-processing-illustrator-export-bonus-svg-export/
        * Small function to execute external code
        * @param commandToRun
        * @param dir 
        */      
        public static ArrayList<String> runUnixCommand(String commandToRun, String dir) {
          ArrayList<String> str=new  ArrayList<String>();  
          File workingDir = new File("");          // where to do it - should be full path
          String returnedValues;                    // value to return any results

          // run the command!
          try {
              System.out.println(commandToRun);
              String[] cmd=new String[10];
              for (int i=0; i<10;i++) cmd[i]="";
              cmd[0]=commandToRun;

            Process p = Runtime.getRuntime().exec(commandToRun, null);
            int i = p.waitFor();
            if (i == 0) {
              BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
              while ( (returnedValues = stdInput.readLine ()) != null) {
                str.add(returnedValues);
                //println(returnedValues);
              }
            }
            else {
              BufferedReader stdErr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
              while ( (returnedValues = stdErr.readLine ()) != null) {
                  str.add(returnedValues);
                  //--Error to stdout
                  System.out.println(returnedValues);
              }
            }
          }
          catch (Exception e) {
             System.out.println("Error running command!");  
              System.out.println(e);
          }
          return str;
        }
      
    /**
     * Code from : http://stackoverflow.com/questions/8083479/java-getting-my-ip-address
     * 
     * @return 
     */ 
    public String ip() {
          String ip;
            try {
                Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                while (interfaces.hasMoreElements()) {
                    NetworkInterface iface = interfaces.nextElement();
                    // filters out 127.0.0.1 and inactive interfaces
                    if (iface.isLoopback() || !iface.isUp())
                        continue;

                    Enumeration<InetAddress> addresses = iface.getInetAddresses();
                    while(addresses.hasMoreElements()) {
                        InetAddress addr = addresses.nextElement();
                        ip = addr.getHostAddress();
                        return ip;
                        //System.out.println(iface.getDisplayName() + " " + ip);
                    }
                }
            } catch (Exception e) {  
                return "";
            }
            return "";
     }

    
	public static ArrayList<String> loadStrings(String filename) {
		ArrayList<String> tmp=new ArrayList<String>();
		try {
			//Change to read UTF-8 here
			BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(new File(filename)),"ISO-8859-1"));
			while (br.ready()) {
                            tmp.add(br.readLine());
                        }
                        br.close();
		} catch(Exception e) {}
		return tmp;
	}
    
}
