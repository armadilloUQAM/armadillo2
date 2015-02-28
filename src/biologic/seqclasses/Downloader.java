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
import biologic.*;
import java.io.*;
import java.util.Vector;

/**
 * Class to Handle the Loading and Downloding of Information from Web database
 * or local file (Genbank, Ensembl file)
 * @author Etienne Lord, Michael Leclerq, ...
 * @since Avril 2009
 */
public class Downloader {
    //private String description;
    //private RichSequence seq;
    
    public boolean OutputFile(String filename) {
        try {
            BufferedReader br=new BufferedReader(new FileReader(new File(filename)));
            while(br.ready()) {
                Config.log(br.readLine());
            }
            br.close();
        } catch(Exception e) {Config.log("Unable to open "+filename); return false;}
        return true;
    }
/**
 * Permet d'aller chercher sur ncbi les réponses à un mot clé
 * fait la recherche sur ncbi avec le mot clé, et récupère les id correspondants
 * puis les inscrit dans esearch.txt
 */
    public boolean eSearch (String keyWord){
        esearch es = new esearch (keyWord);
        return true;
    }
    /**
     *
     * @param filename
     * @return A Vector of InfoSummary with information in gi
     */
    public Vector<InfoSequence> readEsearchFile(String filename) {
        Vector<InfoSequence> v = new Vector<InfoSequence>();
        try {
            BufferedReader br=new BufferedReader(new FileReader(new File(filename)));
            while(br.ready()) {
               String stri=br.readLine();
               int index=0;
              if ((index=stri.indexOf("<Count>"))!=-1) {
                  String number=stri.substring(index+7, stri.indexOf("</Count>"));
                  //Config.log(number);
              }
              if ((index=stri.indexOf("<Id>"))!=-1) {
                  String number=stri.substring(index+4, stri.indexOf("</Id>"));
                  //Config.log(number);
                  InfoSequence tmp=new InfoSequence();
                  tmp.setType(InfoSequence.type_Ncbi);
                  tmp.setGi(number);
                  v.add(tmp);
              }
            }
            br.close();
        } catch(Exception e) {Config.log("Unable to open "+filename); return null;}
        //Config.log(v.size());
        //supprime esummary.txt si déjà existant
        if (esummary.FileExists("esummary.txt")){
                File f = new File("esummary.txt");
                f.delete();
            }
        return v;
    }
    
    /**
     * Load some info summary found in filename
     * @return a Vector of the loaded InfoSummary
     */
    public Vector<InfoSequence> LoadEsummary(String filename) {
        
        InfoSequence infos = new InfoSequence();      //Temp holder for InfoSummary
        Vector<InfoSequence> tmp = new Vector<InfoSequence>();                  //Return Vector
      
         //Read the esummary file in a buffer
         try {
            BufferedReader br =new BufferedReader(new FileReader(new File("esummary.txt")));
            StringBuilder stri=new StringBuilder();
            while (br.ready()) {
             stri=new StringBuilder();
             stri.append(br.readLine());
             int index=0;
               //numéro accession
               if ((index=stri.indexOf("Caption"))!=-1) {
                  try {
                     infos.setAccession(stri.substring(index+23, stri.indexOf("</Item>")));
                  } catch(Exception e){}
                  //Config.log("accessNumber :"+accessNumber);
               }
               //id
               if ((index=stri.indexOf("Gi"))!=-1) {
                      try {
                      infos.setGi(stri.substring(index+19, stri.indexOf("</Item>")));
                      } catch(Exception e2) {}
                      //Config.log("Gi :"+gi);
                   }
               //description
               if ((index=stri.indexOf("Title"))!=-1) {
                   try {
                      infos.setDescription(stri.substring(index+21, stri.indexOf("</Item>")));
                   } catch(Exception e3) {}
                  //Config.log("Title :"+Title);
               }
               //longueur sequence
               if ((index=stri.indexOf("Length"))!=-1) {
                  try {
                  String l=stri.substring(index+23, stri.indexOf("</Item>"));
                  infos.setLen(Integer.valueOf(l));
                  } catch(Exception e4) {}
                  //Config.log("Length :"+Length);
               }
               if ((index=stri.indexOf("</DocSum>"))!=-1) {
                   infos.setType(InfoSequence.type_Ncbi);
                   tmp.add(infos);
                   infos=new InfoSequence();
               }
         }
          br.close();
        }catch (Exception e6){Config.log("Error loading InfoSummary");e6.printStackTrace();}

        return tmp;
    }
    
     /**
     * Create a thread to download the specified Genbank File associated
     * Note: Handling of error, etc. must be done elsewhere
     * @param Gi (Genbank Gi or Accession Number)
     * @return efecth thread object
     */
    public efetch getGenbankFile (String Gi, String filename) {
        
        efetch e = new efetch(Gi, filename, "gb");
        return e;
    }

    /**
     * Create a thread to download the specified Fasta File associated
     * Note: Handling of error, etc. must be done elsewhere
     * @param Gi (Genbank Gi or Accession Number)
     * @return efecth thread object
     */
    public efetch getFastaFile (String Gi, String filename) {
        
        efetch e = new efetch(Gi, filename,"fasta");
        return e;
    }

    /**
     * Create a thread to download the specified Fasta File associated
     * Note: Handling of error, etc. must be done elsewhere
     * @param Gi (Genbank Gi or Accession Number)
     * @return efecth thread object
     */
    public efetch getEnsemblFile (String EnsemblID,String Ensembldb, String filename) {
        efetch e = new efetch(EnsemblID, filename, Ensembldb);
        return e;
    }

//     /**
//      *
//      * Note: We need a version which work with filename and not just Gi
//     * @return A RichSequence from a Genbank file
//     */
//    public Sequence readGenbankFile(String filename) {
//        try {
//            Class.forName("org.biojavax.bio.seq.io.EMBLFormat");
//            Class.forName("org.biojavax.bio.seq.io.GenbankFormat");
//         } catch(ClassNotFoundException e) {}
//            Namespace ns1 = RichObjectFactory.getDefaultNamespace();
//
//            RichSequence seq=null;
//       Sequence tmp=new Sequence();
//        try{
//            Namespace ns = RichObjectFactory.getDefaultNamespace();
//            RichSequenceIterator GenbankFile = RichSequence.IOTools.readFile(new File(filename),ns);
//            seq=GenbankFile.nextRichSequence();
//            tmp.setAccession(seq.getAccession());
//            tmp.setName(seq.getDescription());
//            tmp.setSequence(seq.seqString());
//        }catch (Exception e){
//            Config.log(e);
//        }
//        return tmp;
//    }
           
    /**
     * @return The name of the ADN sequence
     */
//    public String getDescription() {
//        Config.log(seq.getDescription());
//        return (String) seq.getDescription();
//    }
    
    /**@return la liste des elements de la Zone FEATURES
     */
//    public List getFeatures(){
//
//        List<String> uneListe = new LinkedList();
//         try{
//            Iterator i = seq.features();
//            Feature f;
//            while(i.hasNext()){
//                uneListe.add(((Feature)i.next()).getType());
//            }
//        }catch (Exception e){}
//        Config.log(uneListe);
//        return uneListe;
//    }
    
    /**@return le numéro d'accession 
     */
//    public String getAccession(){
//        Config.log(seq.getAccession());
//        return (String) seq.getAccession();
//    }
    
    /**@return la liste des elements d'une des sous-zone (subFeatures) de FEATURES
    ex : CDS,GENE, ...*/
//    public Set getSubFeaturesSet(String subFeatures){
//        try{
//            Iterator i = seq.features();
//            Feature f;
//            while(i.hasNext()){
//                f=(Feature)i.next();
//                if(f.getType().matches(subFeatures)){
//                    return f.getAnnotation().asMap().keySet();
//                }
//            }
//        }catch (Exception e){}
//        return null;
//    }
    
    /*LECTURE du contenu d'un sous element d'un feature
     */
//    public String getSubFeaturesContent(String subFeatures,String Content){
//        try{
//            Iterator i = this.seq.features();
//            Feature f;
//            while(i.hasNext()){
//                f=(Feature)i.next();
//                if(f.getType().matches(subFeatures)){
//                    return (String) f.getAnnotation().getProperty(Content);
//                }
//            }
//        }catch (Exception e){}
//
//        return null;
//    }
    
}
