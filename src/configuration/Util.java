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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserPrincipal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import workflows.workflow_properties;


/**
 * Collection of util command
 * @author Etienne Lord, Mickael Leclercq, Jeremy Goimard
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
        } catch(Exception e) {
            System.out.println("Get Ressource "+name+" Failed!");
            System.out.println(e);
        }
        return str;
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
        } catch(Exception e) {
            System.out.println("Open "+filename+" Failed!");
            System.out.println(e);
            Config.log("Unable to open "+filename);
            return new String[1];
        }
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
        } catch(Exception e) {
            System.out.println("Can't Delete "+filename);
            return false;
        }
        return true;
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
            System.out.println("Copy File Directory Failed!");
            System.out.println(e);
        }
        if (in != null) in.close();
        if (out != null) out.close();
    }
    
    /** Fast & simple file copy. */
    public static boolean copy(String source, String dest) {
        try {
            copy(new File(source), new File(dest));
        } catch (IOException e) {
            System.out.println("Copy Directory Failed!");
            System.out.println(e);
            return false;
        }
        return true;
    }
    
    /** Fast & simple file rename. */
    public boolean rename(String filename, String new_filename) {
        if (!Util.FileExists(new_filename)) {
            boolean b = Util.copy(filename, new_filename);
            if (!b) return b;
            b = Util.deleteFile(filename);
            if (!b) return b;
        } else return false;
        return true;
    }
    
    public Vector<String> read(String filename) {
        Vector<String>tmp=new Vector<String>();
        try {
            BufferedReader br=new BufferedReader(new FileReader(new File(filename)));
            while (br.ready()) {
                tmp.add(br.readLine());
            }
        } catch(Exception e) {
            System.out.println("Copy Failed!");
            System.out.println(e);
            return tmp;
        }
        return tmp;
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
     * Test if a file exists...
     * @param filename
     * @return true if file Exists
     */
    public static boolean CreateFile(String path) {
        // Use relative path for Unix systems
        File f = new File(path);
        f.getParentFile().mkdirs(); 
        try {
            return f.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
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
        } catch(Exception ex) {
            System.out.println("Directory File Failed!");
            System.out.println(ex);
        }
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
     * Added by JG 2016
     * @param s path ex: ./path/to/file/file.f
     * @return CanonicalPath ex: /home/user/path/to/file/file.f
     */
    public static String getCanonicalPath(String s) {
        File f= new File(s);
        try {
            s = f.getCanonicalPath();
        }catch (IOException ex) {
            System.out.println("Error cananical path!");
            System.out.println(ex);
        }
        return s;
    }
    
    /**
     * Added by JG 2015
     * @param  s a path ex: ./path/to/file/file.f
     * @return absolute (kind of cannonical) path ex: /home/user/path/to/file/file.f
     */
    public static String relativeToAbsoluteFilePath(String s) {
        if (s.matches("^\\.\\/.*")) {
            File f = new File(s);
            s = f.getAbsolutePath();
            s = s.replaceAll(File.separator+"\\."+File.separator,File.separator);
        }
        return s;
    }
    
    /**
     * Added by JG 2015
     * @in string path ex: ./path/to/file/file.f
     * @return file name ex: file
     */
    public static String getFileName(String s){
        String name = s;
        
        // Test for several input name
        String sFirstName = name;
        if (s.contains(",")) {
            String[] tab = s.split(",");
            sFirstName = tab[0];
        }
        if (!name.equals(sFirstName)) name = sFirstName;
        
        // Find the name
        int pos1 = name.lastIndexOf(File.separator);
        int pos2 = name.lastIndexOf(".");
        int pos3 = name.length();
        if (pos1 > 0 && pos2>pos1) name = name.substring(pos1+1,pos2);
        else if (pos1 > 0 && pos2<pos1) name = name.substring(pos1+1,pos3);
        else return s;
        
        return name;
    }
    
    /**
     * Added by JG 2015
     * @in string path ex: ./path/to/file/file.f
     * @return file name ex: file.f
     */
    public static String getFileNameAndExt(String s){
        String name = s;
        
        // Test for several input name
        String sFirstName = name;
        if (s.contains(",")) {
            String[] tab = s.split(",");
            sFirstName = tab[0];
        }
        if (!name.equals(sFirstName)) name = sFirstName;
        
        // Find the name
        int pos1 = name.lastIndexOf(File.separator);
        int pos2 = name.length();
        if (pos1 > 0 && pos2>pos1) name = name.substring(pos1+1,pos2);
        else return s;
        
        return name;
    }
    
        /**
     * Added by JG 2015
     * @in string path ex: ./path/to/file/file.f
     * @return file name ex: file.f
     */
    public static String getFileExt(String s){
        String name = s;
        
        // Test for several input name
        String sFirstName = name;
        if (s.contains(",")) {
            String[] tab = s.split(",");
            sFirstName = tab[0];
        }
        if (!name.equals(sFirstName)) name = sFirstName;
        
        // Find the extension
        int pos1 = name.lastIndexOf(".");
        int pos2 = name.length();
        if (pos1 > 0 && pos2>pos1) name = name.substring(pos1,pos2);
        else return s;
        
        return name;
    }
    
    /**
     * Get the current jar path
     * @param
     * @return current jar path
     */

    public static String getCurrentJarPath(){
        File jarDir = new File(ClassLoader.getSystemClassLoader().getResource(".").getPath());
        return jarDir.getAbsolutePath();
    }
    
    /**
     * Change owner of a directory
     * @param filepath
     * @return true if dir owner is changed
     */
    public static boolean changeOwnerDir(String filepath) {
        Path jpath = Paths.get(getCurrentJarPath());
        Path dpath = Paths.get(filepath);
        UserPrincipal owner;
        try {
            owner = Files.getOwner(jpath);
            Files.setOwner(dpath, owner);
            return true;
        } catch (IOException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Directory Owner change Failed!");
            System.out.println(ex);
            return false;
        }
    }
    
    /**
     * Change owner of a file
     * @param filepath
     * @return true if file is changed
     */
    public static boolean changeOwnerFile(String filepath) {
        Path jpath = Paths.get(getCurrentJarPath());
        Path fpath = Paths.get(filepath);
        UserPrincipal owner;
        try {
            owner = Files.getOwner(jpath);
            Files.setOwner(fpath, owner);
            return true;
        } catch (IOException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("File Owner change Failed!");
            System.out.println(ex);
            return false;
        }
    }

    /**
     * Change owner of a path recursively
     * @param filename
     * @return true if file Exists
     */
    public static boolean changeOwnerPath(String filename) {
        
        return true;
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
            System.out.println("Create Directory Failed!");
            System.out.println(e);
            return false;
        }
    }
    
    /*
    * Copy a dir source to dir dest
    */
    public static boolean copyDirectory (String shd, String sad) {
        File s = new File(shd);
        File t = new File(sad);
        try {
            copyDirectory(s,t);
        } catch (IOException e) {
            System.out.println("Copy Directory Failed!");
            System.out.println(e);
            return false;
        }
        return true;
    }
    
    /*
    * Copy a dir source to dir dest
    * Source
    * http://www.java-tips.org/java-se-tips-100019/18-java-io/854-how-to-copy-a-directory-from-one-location-to-another-location.html
    * If targetLocation does not exist, it will be created.
    * 2016
    */
    public static void copyDirectory(File s, File t) throws IOException {
        if (s.isDirectory()) {
            if (!t.exists()) {
                t.mkdir();
            }
            
            String[] children = s.list();
            for (int i=0; i<children.length; i++) {
                copyDirectory(new File(s, children[i]),
                        new File(t, children[i]));
            }
        } else {
            InputStream in = new FileInputStream(s);
            OutputStream out = new FileOutputStream(t);
            
            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            if (in != null)  in.close();
            if (out != null) out.close();
        }
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
        } catch(Exception e) {
            System.out.println("Can't Delete "+directory);
            return false;
        }
        return false;
    }
    
    /*
    * http://www.tutorialspoint.com/javaexamples/dir_delete.htm
    */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir
                        (new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        //System.out.println("The directory is deleted.");
        return dir.delete();
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
        } catch (Exception e) {
            System.out.println("Download Failed!");
            System.out.println(e);
            return false;
        }
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
        } catch(Exception e) {
            System.out.println("ungzip Failed!");
            System.out.println(e);
            e.printStackTrace();
            return false;
        }
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
        } catch(Exception e) {
            System.out.println("Unzip Failed!");
            System.out.println(e);
            e.printStackTrace();
            return false;
        }
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
     * From : http://www.jeffreythompson.org/blog/2012/05/29/easy-processing-illustrator-export-bonus-svg-export/
     * Small function to execute external code
     * @param commandToRun
     * @param dir
     */
    public static ArrayList<String> runSilentUnixCommand(String commandToRun, String dir) {
        ArrayList<String> str=new  ArrayList<String>();
        File workingDir = new File("");          // where to do it - should be full path
        String returnedValues;                    // value to return any results
        
        // run the command!
        try {
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
                    System.out.println(commandToRun);
                    System.out.println(returnedValues);
                }
            }
        }
        catch (Exception e) {
            System.out.println(commandToRun);
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
            System.out.println("Ip Failed!");
            System.out.println(e);
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
        } catch(Exception e) {
            System.out.println("LoadStrings Failed!");
            System.out.println(e);
        }
        return tmp;
    }
    
    
    /*
    * From stackoverflow.com
    * http://stackoverflow.com/questions/13501142/java-arraylist-how-can-i-tell-if-two-lists-are-equal-order-not-mattering
    * with very few modifications
    */
    public static boolean equalArrayLists(ArrayList<String> one, ArrayList<String> two){
        if (one == null && two == null){
            return true;
        }
        
        if((one == null && two != null)
                || one != null && two == null
                || one.size() != two.size()){
            return false;
        }
        
        //to avoid messing the order of the lists we will use a copy
        //as noted in comments by A. R. S.
        one = new ArrayList<String>(one);
        two = new ArrayList<String>(two);
        
        Collections.sort(one);
        Collections.sort(two);
        return one.equals(two);
    }
    
    // *************************************************************************
    //  * PROGRAM file FUNCTIONS
    //  ************************************************************************
    
    ////////////////////////////////////////////////////////////////////////////
    // /!\ DONT FORGET TO USE NAME in EDITOR FILE /!\
    // @param Save_Values
    ////////////////////////////////////////////////////////////////////////////
    
    // Old
    // Obsolet do not take care of the -- or - it consider multiple text as -- and single lettre as -
    public static String findOptions(String[] tab, workflow_properties properties) {
        String s = ""; // Final string
        String t = ""; // Box type or option
        for (String op:tab){
            if (properties.isSet(op)) {
                t = op;
                t = t.replaceAll("_[a-z]*$","_");
                t = t.replaceAll(".*_(.*)_$","$1");
                if (t.length()>1) {
                    t = t.replaceAll("([A-Z])","-$1");
                    t = t.toLowerCase();
                    t = " --"+t;
                } else {
                    t = " -"+t;
                }
                
                if (!properties.get(op).equals("true")) {
                    t = t+" " + properties.get(op);
                }
                
                s = s+" "+t;
            }
        }
        return s;
    }
    
    /**
     * Added by JG 2015
     * New Find options
     * Take care of the -- or - it consider the number of _ to select -- or -.
     * One "_" is "-". Two "__" is "--".
     *
     * A command like --hello-world has been transformed in propertie as NAME__helloWorld_TYPE
     * A command like --hello       has been transformed in propertie as NAME__helloWorld
     * A command like --h           has been transformed in propertie as NAME__h
     * A command like -h            has been transformed in propertie as NAME_h
     *
     * @in tab of options name
     * @return options selected and values in one string
     */
    public static String findOptionsNew(String[] tab,workflow_properties properties) {
        String s = ""; // Final string
        String t = ""; // Box type or option
        for (String op:tab){
            if (properties.isSet(op)) {
                String prefix = " -";
                if (op.contains("__")) prefix=" --";
                
                t = op;
                t = t.replaceAll("_[a-z,A-Z]*$",""); // Remove extention name (box,button, etc.)
                t = t.replaceAll(".*_(.*)$","$1");
                
                if (t.matches(".*EQUALSYMBOL.*")) // EQUAL = is back
                    t = t.replaceAll("EQUALSYMBOL","=");
                
                if (t.matches(".*UNDERSCORESYMBOL.*")) // UNDERSCORE _ is back
                    t = t.replaceAll("UNDERSCORESYMBOL","_");
                
                if (t.matches(".*HYPHENSYMBOL.*")) // MINUS - is back
                    t = t.replaceAll("HYPHENSYMBOL","_");
                
                if (t.matches(".*PLUS.*")) // MINUS - is back
                    t = t.replaceAll("PLUS","_");
                
                if (t.matches(".*DOT.*")) // MINUS - is back
                    t = t.replaceAll("DOT","_");
                
                if (t.length()>1 && !t.matches("^[A-Z].*")) // Composed command is back
                    t = t.replaceAll("([A-Z])","-$1");
                
                t = t.toLowerCase();
                t = prefix+t;
                
                if (!properties.get(op).equals("true")) {
                    if (t.matches(".*=$"))
                        t = t+"" + properties.get(op);
                    else
                        t = t+" " + properties.get(op);
                }
                s = s+" "+t;
            }
        }
        return s;
    }
    
    
    // *************************************************************************
    // * EDITOR FUNCTIONS
    // *************************************************************************
    
    ////////////////////////////////////////////////////////////////////////////
    // Save Values in program editor
    // /!\ DONT FORGET TO ADD A NAME in the design for all /!\
    // @param Save_Values
    ////////////////////////////////////////////////////////////////////////////
    
    //For Box and combobox
    // Added by JG 2016
    public static void boxEventComboBox(workflow_properties properties,javax.swing.JCheckBox b,javax.swing.JComboBox s){
        if (b == null) {
            properties.put(b.getName(),s);
        } else {
            if (b.isSelected()==true){
                String i = (String)s.getSelectedItem();
                if (s == null) {
                    properties.put(b.getName(),b.isSelected());
                } else {
                    s.setEnabled(true);
                    properties.put(s.getName(),i);
                    properties.put(b.getName(),i);
                }
            } else {
                properties.remove(b.getName());
                if (s != null){
                    s.setEnabled(false);
                }
            }
        }
    }
    
    //For Box and spinner
    // Added by JG 2015
    public static void boxEventSpinner(workflow_properties properties,javax.swing.JCheckBox b,javax.swing.JSpinner s){
        if (b == null) {
            properties.put(b.getName(),s.getValue());
        } else {
            if (b.isSelected()==true){
                if (s == null) {
                    properties.put(b.getName(),b.isSelected());
                } else {
                    s.setEnabled(true);
                    properties.put(s.getName(),s.getValue());
                    properties.put(b.getName(),s.getValue());
                }
            } else {
                properties.remove(b.getName());
                if (s != null){
                    s.setEnabled(false);
                }
            }
        }
    }
    
    //For Button and Spinner
    // Added by JG 2015
    public static void buttonEventSpinner (workflow_properties properties, javax.swing.JRadioButton b,javax.swing.JSpinner s){
        if (b == null) {
            properties.put(b.getName(),s.getValue());
        } else {
            if (b.isSelected()==true){
                if (s == null) {
                    properties.put(b.getName(),b.isSelected());
                } else {
                    s.setEnabled(true);
                    properties.put(s.getName(),s.getValue());
                    properties.put(b.getName(),s.getValue());
                }
            }
        }
    }
    
    //For Box and text
    // Added by JG 2015
    public static void boxEventText(workflow_properties properties,javax.swing.JCheckBox b,javax.swing.JTextField t){
        if (b == null) {
            properties.put(t.getName(),t.getText());
        } else {
            if (b.isSelected()==true){
                if (t == null) {
                    properties.put(b.getName(),b.isSelected());
                } else {
                    t.setEnabled(true);
                    properties.put(t.getName(),t.getText());
                    properties.put(b.getName(),t.getText());
                }
            } else {
                properties.remove(b.getName());
                if (t != null){
                    t.setEnabled(false);
                }
            }
        }
    }
    
    //For Button and text
    // Added by JG 2015
    public static void buttonEventText (workflow_properties properties, javax.swing.JRadioButton b,javax.swing.JTextField t){
        if (b == null) {
            properties.put(t.getName(),t.getText());
        } else {
            if (b.isSelected()==true){
                if (t == null) {
                    properties.put(b.getName(),b.isSelected());
                } else {
                    t.setEnabled(true);
                    properties.put(t.getName(),t.getText());
                    properties.put(b.getName(),t.getText());
                }
            }
        }
    }
    
    /**
     * Added by JG 2015
     * remove a series of properties from a tab depending on the presence of a value
     * Exemple : remove setted properties valueA,valueB,valueC,valueD that are not valueD
     * It will remove all properties A,B,C and let properties valueD setted
     * Usefull when a selection have to remove previous selected values
     */
    public static void removePropertiesIn(workflow_properties properties, String[] sTab, String s) {
        if (s.equals("null")){
            for (String sT:sTab){
                if (properties.isSet(sT)){
                    properties.remove(sT);
                }
            }
        } else {
            for (String sT:sTab){
                if (properties.isSet(sT)&&!s.equals(sT)){
                    properties.remove(sT);
                }
            }
        }
    }
    
    // **************************************************************************
    // * EDITOR AND PROGRAM FUNCTIONS
    // **************************************************************************
    
    // test the type of input
    private static boolean valIsDouble(String str) {
        if (str.toUpperCase().endsWith("DOUBLE") ||
            str.toUpperCase().endsWith("DOUBLEVALUE") ) return true;
        return false;
    }
    private static boolean valIsInt(String str) {
        if (str.toUpperCase().endsWith("INT") ||
            str.toUpperCase().endsWith("INTVALUE") ) return true;
        return false;
    }
    private static boolean valIsFloat(String str) {
        if (str.toUpperCase().endsWith("FLOAT") ||
            str.toUpperCase().endsWith("FLOATVALUE") ) return true;
        return false;
    }
    private static boolean valIsByte(String str) {
        if (str.toUpperCase().endsWith("BYTE") ||
            str.toUpperCase().endsWith("BYTEVALUE") ) return true;
        return false;
    }
    private static boolean valIsLong(String str) {
        if (str.toUpperCase().endsWith("LONG") ||
            str.toUpperCase().endsWith("LONGVALUE") ) return true;
        return false;
    }
    private static boolean valIsShort(String str) {
        if (str.toUpperCase().endsWith("SHORT") ||
            str.toUpperCase().endsWith("SHORTVALUE") ) return true;
        return false;
    }
    /*
    * Added by JG 2015
    * Load default program values setted in properties file if the test is false
    *
    * @param b for the test
    * @results add default parameter in properties
    */
    
    public static void getDefaultPgrmValues(workflow_properties properties,boolean b) {
        if (b!=true && properties.isSet("defaultPgrmValues")) {
            String defaultEditorStatus = properties.get("defaultPgrmValues");
            String[] arrayDefault = defaultEditorStatus.split("<>");
            int z;
            for (int i =0 ; i < arrayDefault.length ; i=i+2){
                z = i;
                properties.put(arrayDefault[z],arrayDefault[z+1]);
            }
        }
    }
    
    /*
    * Added by JG 2016
    * Load saved program values update the editor
    *
    * @param listdict linked all dictionnaries:
        - Dict of Box         and Spinner
        - Dict of Box         and TextField
        - Dict of Box         and ComboBox
        - Dict of RadioButton and Spinner
        - Dict of RadioButton and TextField
    * @results add default parameter in properties
    */
    
    public static void updateSavedProperties(workflow_properties properties, ArrayList<HashMap> listDicts, JTextField Editor_name) {
        // Values
        uspBoxSpinVal(properties, listDicts.get(0));
        uspBoxTextVal(properties, listDicts.get(1));
        uspBoxComboboxVal(properties, listDicts.get(2));
        uspRButSpinVal(properties, listDicts.get(3));
        uspRButTextVal(properties, listDicts.get(4));
        // Button, boxes, values
        uspBoxSpin(properties, listDicts.get(0));
        uspBoxText(properties, listDicts.get(1));
        uspBoxCombo(properties, listDicts.get(2));
        uspRButSpin(properties, listDicts.get(3));
        uspRButText(properties, listDicts.get(4));
        
        Editor_name.setText(properties.getName());
    }
        // Update Values
        private static void uspBoxSpinVal(workflow_properties properties, HashMap<JCheckBox,JSpinner> BoxSpin) {
            for (JCheckBox cb : BoxSpin.keySet()) {
                boolean b = false;
                String k_s = cb.getName();
                if(properties.isSet(k_s)){
                    b = true;
                }
                if (BoxSpin.get(cb)!=null) {
                    String v_s = BoxSpin.get(cb).getName();
                    if (properties.isSet(v_s)){
                        String s_bk = properties.get(v_s);
                        if (valIsInt(v_s)) {
                            BoxSpin.get(cb).setValue(Integer.parseInt(s_bk));
                        } else if (valIsDouble(v_s)) {
                            BoxSpin.get(cb).setValue(Double.parseDouble(s_bk));
                        } else if (valIsFloat(v_s)) {
                            BoxSpin.get(cb).setValue(Float.parseFloat(s_bk));
                        } else if (valIsByte(v_s)) {
                            BoxSpin.get(cb).setValue(Byte.parseByte(s_bk));
                        } else if (valIsShort(v_s)) {
                            BoxSpin.get(cb).setValue(Short.parseShort(s_bk));
                        } else if (valIsLong(v_s)) {
                            BoxSpin.get(cb).setValue(Long.parseLong(s_bk));
                        }
                        BoxSpin.get(cb).setEnabled(false);
                        if (b) {
                            properties.put(cb.getName(),s_bk);
                        }
                    }
                }
            }
        }
        private static void uspBoxTextVal(workflow_properties properties, HashMap<JCheckBox,JTextField> BoxText) {
            for (JCheckBox cb : BoxText.keySet()) {
                boolean b = false;
                String k_s = cb.getName();
                if(properties.isSet(k_s)){
                    b = true;
                }
                if (BoxText.get(cb)!=null) {
                    String v_s = BoxText.get(cb).getName();
                    if (properties.isSet(v_s)){
                        String s_bk = properties.get(v_s);
                        BoxText.get(cb).setText(s_bk);
                        BoxText.get(cb).setEnabled(false);
                        if (b) {
                            properties.put(cb.getName(),s_bk);
                        }
                    }
                }
            }
        }
        private static void uspBoxComboboxVal(workflow_properties properties, HashMap<JCheckBox,JComboBox> BoxCombo) {
            for (JCheckBox cb : BoxCombo.keySet()) {
                boolean b = false;
                String k_s = cb.getName();
                if(properties.isSet(k_s)){
                    b = true;
                }
                if (BoxCombo.get(cb)!=null) {
                    String v_s = BoxCombo.get(cb).getName();
                    if (properties.isSet(v_s)){
                        String s_bk = properties.get(v_s);
                        BoxCombo.get(cb).setSelectedItem(s_bk);
                        BoxCombo.get(cb).setEnabled(false);
                        if (b) {
                            properties.put(cb.getName(),s_bk);
                        }
                    }
                }
            }
        }
        private static void uspRButSpinVal(workflow_properties properties, HashMap<JRadioButton,JSpinner> RButSpin) {
            for (JRadioButton cb : RButSpin.keySet()) {
                boolean b = false;
                String k_s = cb.getName();
                if(properties.isSet(k_s)){
                    b = true;
                }
                if (RButSpin.get(cb)!=null) {
                    String v_s = RButSpin.get(cb).getName();
                    if (properties.isSet(v_s)){
                        String s_bk = properties.get(v_s);
                        if (valIsInt(v_s)) {
                            RButSpin.get(cb).setValue(Integer.parseInt(s_bk));
                        } else if (valIsDouble(v_s)) {
                            RButSpin.get(cb).setValue(Double.parseDouble(s_bk));
                        } else if (valIsFloat(v_s)) {
                            RButSpin.get(cb).setValue(Float.parseFloat(s_bk));
                        } else if (valIsByte(v_s)) {
                            RButSpin.get(cb).setValue(Byte.parseByte(s_bk));
                        } else if (valIsShort(v_s)) {
                            RButSpin.get(cb).setValue(Short.parseShort(s_bk));
                        } else if (valIsLong(v_s)) {
                            RButSpin.get(cb).setValue(Long.parseLong(s_bk));
                        }
                        RButSpin.get(cb).setEnabled(false);
                        if (b) {
                            properties.put(cb.getName(),s_bk);
                        }
                    }
                }
            }
        }
        private static void uspRButTextVal(workflow_properties properties, HashMap<JRadioButton,JTextField> RButText) {
            for (JRadioButton cb : RButText.keySet()) {
                boolean b = false;
                String k_s = cb.getName();
                if(properties.isSet(k_s)){
                    b = true;
                }
                if (RButText.get(cb)!=null) {
                    String v_s = RButText.get(cb).getName();
                    if (properties.isSet(v_s)){
                        String s_bk = properties.get(v_s);
                        RButText.get(cb).setText(s_bk);
                        RButText.get(cb).setEnabled(false);
                        if (b) {
                            properties.put(cb.getName(),s_bk);
                        }
                    }
                }
            }
        }
        // Update Buttons and boxes
        private static void uspBoxSpin(workflow_properties properties, HashMap<JCheckBox,JSpinner> BoxSpin) {
            for (JCheckBox cb : BoxSpin.keySet()) {
                String s = cb.getName();
                if (properties.isSet(s)){
                    cb.setEnabled(true);
                    cb.setSelected(true);
                    if (BoxSpin.get(cb)!=null) {
                        BoxSpin.get(cb).setEnabled(true);
                    }
                }
            }
        }
        private static void uspBoxText(workflow_properties properties, HashMap<JCheckBox,JTextField> BoxText) {
            for (JCheckBox cb : BoxText.keySet()) {
                String s = cb.getName();
                if (properties.isSet(s)){
                    cb.setEnabled(true);
                    cb.setSelected(true);
                    if (BoxText.get(cb)!=null) {
                        BoxText.get(cb).setEnabled(true);
                    }
                }
            }
        }
        private static void uspBoxCombo(workflow_properties properties,  HashMap<JCheckBox,JComboBox> BoxCombo) {
            for (JCheckBox cb : BoxCombo.keySet()) {
                String s = cb.getName();
                if (properties.isSet(s)){
                    cb.setEnabled(true);
                    cb.setSelected(true);
                    if (BoxCombo.get(cb)!=null) {
                        BoxCombo.get(cb).setEnabled(true);
                    }
                }
            }
        }
        private static void uspRButSpin(workflow_properties properties,HashMap<JRadioButton,JSpinner> RButSpin) {
            for (JRadioButton cb : RButSpin.keySet()) {
                String s = cb.getName();
                if (properties.isSet(s)){
                    cb.setEnabled(true);
                    cb.setSelected(true);
                    if (RButSpin.get(cb)!=null) {
                        RButSpin.get(cb).setEnabled(true);
                    }
                }
            }
        }
        private static void uspRButText(workflow_properties properties, HashMap<JRadioButton,JTextField> RButText) {
            for (JRadioButton cb : RButText.keySet()) {
                String s = cb.getName();
                if (properties.isSet(s)){
                    cb.setEnabled(true);
                    cb.setSelected(true);
                    if (RButText.get(cb)!=null) {
                        RButText.get(cb).setEnabled(true);
                    }
                }
            }
        }
        
    /*
    * Added by JG 2016
    * Enabled or disabled options in the editor
    *
    * @param listdict linked all dictionnaries:
        - Dict of Box         and Spinner
        - Dict of Box         and TextField
        - Dict of Box         and ComboBox
        - Dict of RadioButton and Spinner
        - Dict of RadioButton and TextField
    * @results add default parameter in properties
    */
    

    public static void enabled_Advanced_Options(workflow_properties properties, boolean e, ArrayList<HashMap> listDicts) {
        eao_BoxSpin(properties, e, listDicts.get(0));
        eao_BoxText(properties, e, listDicts.get(1));
        eao_BoxCombo(properties, e, listDicts.get(2));
        eao_RButSpin(properties, e, listDicts.get(3));
        eao_RButText(properties, e, listDicts.get(4));

    }
        // Enabled or not Advanced Options
        private static void eao_BoxSpin(workflow_properties properties, boolean e, HashMap<JCheckBox,JSpinner> BoxSpin) {
            for (JCheckBox cb : BoxSpin.keySet()) {
                cb.setEnabled(e);
                if (BoxSpin.get(cb)!=null) {
                    String s = cb.getName();
                    if (properties.isSet(s) && e==true){
                        BoxSpin.get(cb).setEnabled(true);
                    } else {
                        BoxSpin.get(cb).setEnabled(false);
                    }
                }
            }
        }
        private static void eao_BoxText(workflow_properties properties, boolean e, HashMap<JCheckBox,JTextField> BoxText) {
            for (JCheckBox cb : BoxText.keySet()) {
                cb.setEnabled(e);
                if (BoxText.get(cb)!=null) {
                    String s = cb.getName();
                    if (properties.isSet(s) && e==true){
                        BoxText.get(cb).setEnabled(true);
                    } else {
                        BoxText.get(cb).setEnabled(false);
                    }
                }
            }
        }
        private static void eao_BoxCombo(workflow_properties properties, boolean e, HashMap<JCheckBox,JComboBox> BoxCombo) {
            for (JCheckBox cb : BoxCombo.keySet()) {
                cb.setEnabled(e);
                if (BoxCombo.get(cb)!=null) {
                    String s = cb.getName();
                    if (properties.isSet(s) && e==true){
                        BoxCombo.get(cb).setEnabled(true);
                    } else {
                        BoxCombo.get(cb).setEnabled(false);
                    }
                }
            }
        }
        private static void eao_RButSpin(workflow_properties properties, boolean e, HashMap<JRadioButton,JSpinner> RButSpin) {
            for (JRadioButton cb : RButSpin.keySet()) {
                cb.setEnabled(e);
                if (RButSpin.get(cb)!=null) {
                    String s = cb.getName();
                    if (properties.isSet(s) && e==true){
                        RButSpin.get(cb).setEnabled(true);
                    } else {
                        RButSpin.get(cb).setEnabled(false);
                    }
                }
            }
        }
        private static void eao_RButText(workflow_properties properties, boolean e, HashMap<JRadioButton,JTextField> RButText) {
            for (JRadioButton cb : RButText.keySet()) {
                cb.setEnabled(e);
                if (RButText.get(cb)!=null) {
                    String s = cb.getName();
                    if (properties.isSet(s) && e==true){
                        RButText.get(cb).setEnabled(true);
                    } else {
                        RButText.get(cb).setEnabled(false);
                    }
                }
            }
        }

    /*
    * Added by JG 2016
    * Reset all dictionnaries to get the new editor options
    *
    * @param listdict linked all dictionnaries:
        - Dict of Box         and Spinner
        - Dict of Box         and TextField
        - Dict of Box         and ComboBox
        - Dict of RadioButton and Spinner
        - Dict of RadioButton and TextField
    * @results add default parameter in properties
    */
    public static void dictsReset(ArrayList<HashMap> listDicts, HashMap<JCheckBox, JSpinner> DictBoxSpinner, HashMap<JCheckBox, JTextField> DictBoxTextField, HashMap<JCheckBox, JComboBox> DictBoxComboBox, HashMap<JRadioButton, JSpinner> DictRadioButtonSpinner, HashMap<JRadioButton, JTextField> DictRadioButtonTextField) {
        DictBoxSpinner.clear();
        DictBoxTextField.clear();
        DictBoxComboBox.clear();
        DictRadioButtonSpinner.clear();
        DictRadioButtonTextField.clear();

        listDicts.clear();
        listDicts.add(DictBoxSpinner);
        listDicts.add(DictBoxTextField);
        listDicts.add(DictBoxComboBox);
        listDicts.add(DictRadioButtonSpinner);
        listDicts.add(DictRadioButtonTextField);
    }

}
