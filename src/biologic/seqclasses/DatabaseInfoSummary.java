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
 //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 /// Class database - Contient les InfoSummary downloader
 ///
 /// Novembre 2008
 /// Revised Avril 2009

import biologic.InfoSequence;
import java.io.*;
import java.util.ArrayList;
//Les regex sont utilisés pour la recherche
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
//Random est utilisé par la fonction mock qui qui fait une fausse base de données

public class DatabaseInfoSummary implements Serializable {

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// VARIABLES GLOBALES ET CONSTRUCTEUR
        private static final long serialVersionUID = 20090426L; 
        public ArrayList<InfoSequence> Data = new ArrayList<InfoSequence>();           // Les sequences contenues dans cette database
        Vector<Integer> LastSearchData = new Vector<Integer>();               // Vecteur contenant l'index des données corespondant a la recherche
        String lastSearch="";                                                // Identification de la derniere recherche
        String filename="";                                                  // Le fichier associé a cette database
        public final int MODE_ID=0;                                                 // Mode de recherche des <sequence> par <search>
        public final int MODE_ACCESSION=1;
        public final int MODE_DESC=2;
        public final int MODE_ALIASES=3;
        public final int MODE_ALL=4;
        public final int MODE_LENMORE=6;
        public final int MODE_LENLESS=7;


        // Les constructeurs
        public DatabaseInfoSummary() {}                                                // Constructeur de base
        
         //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
         /// FONCTIONS SPECIALES
     
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// FONCTIONS DE RETOUR ET D'AFFICHAGE
       
       /**
        * Teste si la database contient des séquences
        * @return true si la databse ne contient pas de sequences, sinon false
        */
        public boolean isEmpty() {
            return  Data.isEmpty();
        }
          
        /**
         * 
         * @return le nombre de <sequence> dans la database
         */
        public int getSize() {
            return Data.size();
        }

          /**
         *
         * @return the number of <InfoSummary> selected in the database
         */
        public int getNbSelected() {
            int count=0;
            for (InfoSequence i:Data) if (i.isSelected()) count++;
            return count;
        }

        /**
         * Fonction mode text
         * Affiche toute les <hgncdata> de la database
         */
        void print() {
            
         }
       
        /**
         * Fonction mode text
         * Affiche toute les <sequence> de la derniere recherche par <search>
         * @return le nombre de resultats ou -1 si l'on a pas fait de recherche
         */
        int printResult() {
            Config.log("================================================================================");
            //CAS 1: On n'a pas encore fait de recherche
            if (lastSearch.equals("")) {
                Config.log("Error:No search made wet.");
                return -1;
            }

            //CAS 2: On a aucun resultats pour cette recherche
            if (LastSearchData.size()==0) {
                Config.log("No result.");
                return 0;
            }

            //CAS 3: On affiche les resultats de la recherche
            //for (hgncdata data:LastSearchData) Config.log(data[0]+"\t"+seq.gene_name+"\t"+seq.gene_seq);
            Config.log("Total results: "+LastSearchData.size());
            return LastSearchData.size();
        }

        /**
         * Fonction retournant toutes le <sequence> de la database 
         * Sous la forme d'un String Array
         * Note: Requis pour <ShowAllSequences> de la vue principale
         * @return String Array des <sequence> de la database
         */
                      
        
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// FONCTIONS d'AJOUT OU DE DELETION
        
        /**
         * On vide la database de ses <sequence>
         * Note: Requis pour <loadDatabase(filename)> et <mock>
         */
        public void removeAllData() {
             if (!Data.isEmpty())Data.clear();
         }
     
        public boolean addData(InfoSequence data) {
           return(Data.add(data));
        }
   
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// FONCTIONS DE CHARGEMENT ET RECHERCHE
        
        /**
         * Fonction de chargement de database
         * Fonction mode texte et graphique
         * @param filename (String contenant le nom du fichier et le path)
         * @return true si la base de donnees est charge, meme si elle ne comprend rien
         *
         */
        public boolean loadResults(String filename) {
            this.filename=filename;
            //Precaution pour ne pas garder d'éléments
            removeAllData();
            //On verifie si on a vraiment un fichier comme entree
            File dbFile=new File(filename);
            if (dbFile==null|dbFile.isDirectory()) return false;
            //On charge les donnees
            int count=0; //Number of InfoSummary
            try {
            BufferedReader br = new BufferedReader(new FileReader(dbFile));
            //TO DO
            
            while (br.ready()) {
                String stri=br.readLine();
                if (!stri.startsWith("#")) {
                    // Normalement [0]Type+TAB+[1]Hgncid+TAB+[2]getEnsemblid+TAB+[3]Emsembldb+TAB+[4]Gi+TAB+[5]Accession+TAB+[6]Description+TAB+[7]Len(bp);
                    //.getType()+"\t"+this.getHgncid()+"\t"+this.getEnsemblid()+"\t"+this.getGi()+"\t"+this.getAccession()+"\t"+this.getEmsembldb()+"\t"+this.getDescription()+"\t"+this.getLen()+"\t";
                    String data[]=stri.split("\t");
                   
                        InfoSequence i = new InfoSequence();
                        i.setType(Integer.valueOf(data[0]));
                        i.setHgncid(data[1]);
                        i.setEnsemblid(data[2]);
                        i.setGi(data[3]);
                        i.setAccession(data[4]);
                        i.setEmsembldb(data[5]);
                        i.setDescription(data[6]);
                        i.setLen(Integer.valueOf(data[7]));
                        Data.add(i);
                        count++;
                    
                }
            }
            br.close();
            } catch(Exception e) {System.err.println("Unable ot load "+filename); e.printStackTrace(); return false;}
            Config.log("Loading successfull of "+count+" results from "+filename);
            return true;
        }
        
        
    /**
     * Fonction de sauvegarde des resultats
     * Fonction mode texte et graphique
     * @param filename (String contenant le nom du fichier et le path) du fichier de sauvegarde
     * @return true si la base de donnees est charge, meme si elle ne comprend rien
     *
     */   
        
     public boolean saveResults(String filename, String search) {
            //CAS 1: On sauve les resultats de la recherche dans le fichier filename
              try {
                PrintWriter pw = new PrintWriter(new File(filename));
                //Print description;
                pw.println("#InfoSummary Results");
                pw.println("#Etienne Lord, Mickael Leclercq 2009");
                pw.println("#You searched for "+search+" and refine for "+(lastSearch.equals("")?"nothing":lastSearch));
                pw.println("#Remove # from next line for easy loading in Excel");
                pw.println("#[0]Type\t[1]Hgncid\t[2]getEnsemblid\t[3]Emsembldb\t[4]Gi\t[5]Accession\t[6]Description\t[7]Len(bp);");
                int count=0;
                for (InfoSequence data:Data) {
                    //save only if we have complete info
                    if (data.isLoaded()||data.getType()==InfoSequence.type_HGNC) {
                        count++;
                        pw.println(data.toString2());
                    } 
                }
                pw.flush();
                pw.close();
                //Config.log("Save successfull of "+count+" results of "+Data.size()+" results in "+filename);
              } catch (Exception e) {Config.log("Error: unable to save file "+filename);return false;}
             
             return true;
     }
        
        
    /**
    * Fonction mode text et graphique
    * Note: Par design, on retourne les sequences dont la recherhce (voir mode) 
    * "matches" un certains regex (pratique pour des applications de type AJAX )
    * Note2: On assigne aussi a la variable lastSearch des informations sur la recherche
    * @return A vector of the Index position of Matching result
    */
    public Vector<Integer> search (String regex, int mode) {
        Vector<Integer> returnArray = new Vector<Integer>();
        Pattern p;
        try {
            p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        } catch(java.util.regex.PatternSyntaxException e) {return returnArray;}
        int search_len=0;
        try {
            search_len=Integer.valueOf(regex);
        } catch (Exception e) {search_len=0;}
        switch (mode) {
            case MODE_ID:       lastSearch="Id with: "+regex;
                                for (int i=0; i<Data.size();i++) {
                                      InfoSequence data=Data.get(i);
                                      Matcher m = p.matcher(data.getGi());
                                      if (m.find()) returnArray.add(i);
                                }
                                break;
            case MODE_ACCESSION:lastSearch="Accession with: "+regex;
                                for (int i=0; i<Data.size();i++) {
                                     InfoSequence data=Data.get(i);
                                      Matcher m = p.matcher(data.getAccession());
                                      if (m.find()) returnArray.add(i);
                                }
                                break;   
            case MODE_DESC:  lastSearch="Description with: "+regex;
                                for (int i=0; i<Data.size();i++) {
                                      InfoSequence data=Data.get(i);
                                       Matcher m = p.matcher(data.getDescription());
                                      if (m.find()) returnArray.add(i);
                                }
                                break;
            case MODE_LENMORE: lastSearch="Len(bp) greater: "+regex;
                                for (int i=0; i<Data.size();i++) {
                                    InfoSequence data=Data.get(i);
                                    try {
                                        int len=Integer.valueOf(data.getLen());
                                        if (len>=search_len) returnArray.add(i);
                                    } catch(Exception e) {}
                                }
                                break;
             case MODE_LENLESS: lastSearch="Len(bp) less: "+regex;
                                for (int i=0; i<Data.size();i++) {
                                    InfoSequence data=Data.get(i);
                                    try {
                                        int len=Integer.valueOf(data.getLen());
                                        if (len<=search_len) returnArray.add(i);
                                    } catch(Exception e) {}
                                }
                                break;
            case MODE_ALL:      lastSearch="All with: "+regex;
                                for (int i=0; i<Data.size();i++) {
                                    InfoSequence data=Data.get(i);
                                    Matcher m = p.matcher(data.toString2());
                                    if (m.find()) returnArray.add(i);
                                }
        } //end switch
        Config.log("Searching for "+lastSearch);
        //System.out.printf(" found %d result(s)\n", returnArray.size());
        this.LastSearchData.clear();
        this.LastSearchData.addAll(returnArray);
        return returnArray;
      }
      
    /**
    * Fonction mode text et graphique
    * Note: Par design, on retourne les sequences dont la recherhce (voir mode) 
    * "matches" un certains regex (pratique pour des applications de type AJAX )
    * Note2: On assigne aussi a la variable lastSearch des informations sur la recherche
    * @return A vector of the Index position of Matching result
    */
    public Vector<Integer> search (int low, int max) {
        lastSearch="Searching for range between "+low+" and "+max;
        Vector<Integer> returnArray = new Vector<Integer>();                
            for (int i=0; i<Data.size();i++) {
                InfoSequence data=Data.get(i);
                try {
                    int len=Integer.valueOf(data.getLen());
                    if (len<=max&&len>=low) returnArray.add(i);
                } catch(Exception e) {}
            }
        Config.log("Searching for range between "+low+" and "+max);
        //System.out.printf(" found %d result(s)\n", returnArray.size());
        this.LastSearchData.clear();
        this.LastSearchData.addAll(returnArray);
        return returnArray;
      }
    
        /**
         * Retourne le nombre de resultat de la deniere recherche
         * @return le nombre de resultats de la dernière recherche par <search>
         */
      public int getNumberResult() {
           return LastSearchData.size();
      }
      
} //End class database
