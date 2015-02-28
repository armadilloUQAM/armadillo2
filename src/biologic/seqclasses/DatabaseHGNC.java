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
 /// Class database - Contient la base de donnée un ArrayList de <sequence> et permet d'interragir avec elle
 ///
 /// Novembre 2008
 /// Revised Avril 2009

import java.io.*;
import java.util.ArrayList;
//Les regex sont utilisés pour la recherche
import java.util.regex.Pattern;
import java.util.regex.Matcher;
//Random est utilisé par la fonction mock qui qui fait une fausse base de données

public class DatabaseHGNC implements Serializable {

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// VARIABLES GLOBALES ET CONSTRUCTEUR
        private static final long serialVersionUID = 20090426L; 
        ArrayList<HGNCdata> Data = new ArrayList<HGNCdata>();           // Les sequences contenues dans cette database
        ArrayList<HGNCdata> LastSearchData = new ArrayList<HGNCdata>(); // Le résultat de la dernière recherche
        public String[] qualifier;                                                  //Identification de chaque colonne
        String lastSearch="";                                                // Identification de la derniere recherche
        public String filename="";                                                  // Le fichier associé a cette database
        final int MODE_ID=0;                                                 // Mode de recherche des <sequence> par <search>
        final int MODE_NAME=1;
        final int MODE_SYMBOL=2;
        final int MODE_ALIASES=3;
        public final int MODE_ALL=4;
        public int number_mock_sequence=20;                                 //Nombre de <sequence> fictive a generer
        
        // Les constructeurs
        public DatabaseHGNC() {}                                                // Constructeur de base
        
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
        
        /**
         * Fonction retournant toutes les <sequence> d'un array 
         * Sous la forme d'un String Array
         * Note: Requis pour le retour de <search(regex, mode)>
         * @param array (ArrayList<sequence>`) 
         * @return String Array des <sequence> de array
         */
        public String[] toStringArray(ArrayList<HGNCdata> array) {
            String[] returnArray=new String[array.size()];
            int i=0;
            for (HGNCdata data: Data) {
                returnArray[i++]=data.data[0]+" "+data.data[1]+" "+data.data[2];
            }
            return returnArray;
        }
       
        /**
        * Fonction retrounant tous les <hgncdata> pour la JTable 
        * @return Object[][]
        */
        
        public Object[][] toObjectArray() {
            Object[][] o = new Object[qualifier.length][Data.size()];
            int i=0;
            int j=0;
            for (HGNCdata data:Data) {
                for (String stri:data.data) o[i++][j] =stri;
                j++;
                i=0;
            }
            return o;
        }

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// FONCTIONS d'AJOUT OU DE DELETION
        
        /**
         * On vide la database de ses <sequence>
         * Note: Requis pour <loadDatabase(filename)> et <mock>
         */
        public void removeAllData() {
             if (!Data.isEmpty())Data.clear();
         }
     
        public boolean addData(HGNCdata data) {
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
        boolean loadDatabase(String filename) {
            this.filename=filename;
            //Precaution pour ne pas garder d'éléments
            removeAllData();
            //On verifie si on a vraiment un fichier comme entree
            File dbFile=new File(filename);
            if (dbFile==null|dbFile.isDirectory()) return false;
            //On charge les donnees
            try {
            BufferedReader br = new BufferedReader(new FileReader(dbFile));
            //On declare un index qui servira a derterminer ou l'on met les
            //donnees. Ceci est nécessaire dans le cas ou on aurait un probleme
            //d'enregistrement. (ex manque une ligne)... Donc plus safe que lire
            //3 lignes à la fois.
            int index=0;
            //On skip la première ligne où on retrouve la description des champs
            String id=br.readLine();
            Config.log(id);
            qualifier=id.split("\\t");
            int count=0;
            while (br.ready())  {
                String stri=br.readLine();
                HGNCdata tmpdata=new HGNCdata(stri);
                addData(tmpdata);
//                count++;
//                if (count>1000) {
//                    Config.log("*");
//                    count=0;
//                }
              } //end while
                br.close();

            } catch(FileNotFoundException e) {
                System.out.printf("La database: %s est introuvable.\n",filename);
                return false;
               }
               catch(IOException e) {
                System.out.printf("Erreur dans le chargement de la database: %s.\n",filename);
                return false;
                }
            return true;
        }
        
        
    /**
     * Fonction de sauvegarde des resultats
     * Fonction mode texte et graphique
     * @param filename (String contenant le nom du fichier et le path) du fichier de sauvegarde
     * @return true si la base de donnees est charge, meme si elle ne comprend rien
     *
     */   
        
     boolean saveResults(String filename) {
          Config.log("================================================================================");
            //CAS 1: On n'a pas encore fait de recherche, donc on ne sauve rien
            if (lastSearch.equals("")) {
                Config.log("Error: Unable to save results. No search made wet.");
                return false;
            }

            //CAS 2: On a aucun resultats pour cette recherche, donc rien a sauver
            if (LastSearchData.size()==0) {
                Config.log("Error: No result with this search. Nothing to save");
                return false;
            }
            //CAS 3: On sauve les resultats de la recherche dans le fichier filename
              try {
                PrintWriter pw = new PrintWriter(new File(filename));
                //Print description;
                for (String s:qualifier) pw.print(s+"\t");
                pw.println();
                for (HGNCdata data:LastSearchData) pw.println(data.original);
                pw.flush();
                pw.close();
              } catch (Exception e) {Config.log("Error: unable to save file "+filename);return false;}
             Config.log("Save successfull of "+LastSearchData.size()+" results in "+filename);
             return true;
     }
        
        
    /**
    * Fonction mode text et graphique
    * Note: Par design, on retourne les sequences dont la recherhce (voir mode) 
    * "matches" un certains regex (pratique pour des applications de type AJAX )
    * Note2: On assigne aussi a la variable lastSearch des informations sur la recherche
    * 
    * mode:
    * 0: MODE_ID       (recherche d'un identifiant dans <sequence.id>`
    * 1: MODE_NAME     (recherche d'un nom dans <sequence.gene_name>
    * 2: MODE_SEQUENCE (recherche d'un motif dans la <sequence.gene_sequence>)
    * 3: All           (recherche globale dans <sequence.id ou sequence.gene_name ou sequence.gene_seq>
    * @param String regex (la chaine de recherche), le mode
    * @return Un ArrayList<hgncdata> contenant toutes les <sequence> correspondantes a la recherche
    */
    public ArrayList<HGNCdata> search (String regex, int mode) {
        ArrayList<HGNCdata> returnArray = new ArrayList<HGNCdata>();
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        switch (mode) {
            case MODE_ID:       lastSearch="Id with: "+regex;
                                for (HGNCdata data: Data) {
                                      Matcher m = p.matcher(data.data[0]);
                                      if (m.find()) returnArray.add(data);
                                }
                                break;
            case MODE_NAME:     lastSearch="Name with: "+regex;
                                for (HGNCdata data: Data) {
                                      Matcher m = p.matcher(data.data[2]);
                                      if (m.find()) returnArray.add(data);
                                }
                                break;   
            case MODE_SYMBOL:  lastSearch="Symbol with: "+regex;
                                for (HGNCdata data: Data) {
                                      Matcher m = p.matcher(data.data[1]);
                                      if (m.find()) returnArray.add(data);
                                }
                                break;
            case MODE_ALIASES: lastSearch="Aliases with: "+regex;
                                for (HGNCdata data: Data) {
                                     Matcher m = p.matcher(data.data[7]);
                                     if (m.find()) returnArray.add(data);
                                }
                                break;
            case MODE_ALL:      lastSearch="All with: "+regex;
                                for (HGNCdata data: Data) {
                                    Matcher m = p.matcher(data.original);
                                    if (m.find()) returnArray.add(data);
                                }
        } //end switch
        Config.log("Searching for "+lastSearch);
        System.out.printf(" found %d result(s)\n", returnArray.size());
        LastSearchData=returnArray;
        return returnArray;
      }
      
        /**
         * Retourne le nombre de resultat de la deniere recherche
         * @return le nombre de resultats de la dernière recherche par <search>
         */
      int getNumberResult() {
           return LastSearchData.size();
      }
      
      String getSynonymn(HGNCdata data) {
      int[] result_index={1,2,5,6,7,8};
      String result="";
          if (data!=null) {
               for (int i:result_index) {
                  try {
                  if (data.data[i]!=null) result+=data.data[i]+" ";
                  } catch(Exception e) {}
               }
          }
           return result;
      }


      
} //End class database
