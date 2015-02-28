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
import biologic.MultipleSequences;
import biologic.Sequence;
import java.util.Vector;
import java.io.*;

/**
 * Utility to read MAF (UCSC) or Multiple Fasta File
 * @author Etienne Lord
 */
public class MAF extends MultipleSequences {
       Vector<mafcontig> Contig=new Vector<mafcontig>();
       final char TAB='\t';
       private String filename="";

       //--Iterative MAF extract
       private BufferedReader maf;                  //--The MAF file
       boolean maf_open=false;                      //--Status
       int maf_current_line_number=0;              //--Current position in file
       //--Internal variables
       private String maf_signature="s hg18"; //--The signature that we search to identify a new contig
       String maf_current_line="";         //--Curent read line
       private boolean maf_inContig=false;  //--InConfitg
       private int maf_contig_count=0;      //--Number of contig found to date
       private boolean loadMAFinfos=false;
       /**
        * Defaulf constructor
        * @param filename
        */
       public MAF(String filename) {
          
            open(filename);

       }


       /////////////////////////////////////////////////////////////////////////
       // MAF Functions

       public void open(String filename)  {
           this.filename=filename;
           try {
                maf=new BufferedReader(new FileReader(new File(filename)));
                if (maf!=null) {
                    maf_open=true;
                    maf_current_line_number=0;
                }
           } catch(Exception e) {Config.log("*** Error: Unable to open "+filename);maf_open=false;}
       }

       public void close() {
           try {
            if (maf_open) {
                maf.close();
                maf_open=false;
            }
           } catch(Exception e){Config.log("*** Error: Unable to close "+filename);}
       }
       
       public void reopen() {
           open(filename);
       }
       
      /**
       * get the Next mafcontif in the maf file 
       * @return the next mafcontig or null if not found
       */
       public mafcontig getNext() {
           //--tmp variables
           String desc="";
           String orientation="";
           String sequence="";
           String quality="";
           int position=0;

           try {
                if (!maf_open) return null;
                mafcontig tmp = new mafcontig("tmp");

                while(maf.ready()) {
                   
                    //CASE 1. New alignement -> return the contig
                    if (maf_inContig&&maf_current_line.startsWith("a")) {
                        maf_contig_count++;
                        maf_inContig=false;
                        return tmp;
                    }
                    //CASE 2: Currently in a contig and a new sequence
                    if (maf_current_line.startsWith("s")&&maf_inContig) {
                        String [] striv=maf_current_line.split(" ");
                        for (int i=0; i<striv.length;i++) striv[i]=striv[i].trim(); //remove space
                        int index=1;
                        desc=striv[index].toUpperCase();
                        for (int i=2;i<striv.length;i++) {
                            if (striv[i].equals("+")||striv[i].equals("-")) {
                                index=i;
                                break;
                            }
                        }
                        orientation=striv[index];
                        sequence=striv[striv.length-1];
                        tmp.addSequence(desc, sequence,"", orientation);
                    }
                    //CASE 3. MAF signature found 
                    if (maf_current_line.startsWith(getMaf_signature())&&!maf_inContig) {
                        String [] striv=maf_current_line.split(" ");
                        for (int i=0; i<striv.length;i++) striv[i]=striv[i].trim(); //remove space
                        int index=1;
                        desc=striv[index].toUpperCase();
                        //Position is the first non empty string
                        for (int i=index+1;i<striv.length;i++) {
                            if (!striv[i].equals("")) {
                                index=i;
                                break;
                            }
                        }
                        position=getInt(striv[index]);

                        for (int i=2;i<striv.length;i++) {
                            if (striv[i].equals("+")||striv[i].equals("-")) {
                                index=i;
                                break;
                            }
                    }
                    orientation=striv[index];
                    sequence=striv[striv.length-1];
                    int end=sequence.length();
                    maf_inContig=true;
                    tmp.setPosition_start((Integer) position);
                    tmp.setPosition_end((Integer) position+end);
                    tmp.setFilestart_line((Integer) maf_current_line_number);
                    //--Note we set the start and end
                    tmp.setNote("start="+position+"\nend="+(position+end)+"\n");
                    tmp.addSequence(desc, sequence,"", orientation);

                } //--End in new contig with signature

                maf_current_line=maf.readLine();
                maf_current_line_number++;
          
            } //--End while
            close();
           return null;
            } catch(Exception e) {if (isDebug()) e.printStackTrace();return null;}
       }

      /**
       * get the Next Sequence in the MultipleSequences files
       * @return the next mafcontig or null if not found
       */
       public Sequence getNextSequence() {
           boolean sequenceMode=false;
           try {
                if (!maf_open) return null;
                Sequence tmp = new Sequence();                        //Temp sequence
                StringBuilder tmpsequence=new StringBuilder();        //Temp sequence string

                while(maf.ready()) {
                     if (sequenceMode&&(maf_current_line.equals("")||maf_current_line.startsWith(">"))) {
                          tmp.setSequence(tmpsequence.toString());
                          if (this.isLoadMAFinfos()) {
                   tmp.loadInfoFromName();
               }
                          tmpsequence=new StringBuilder();
                          sequenceMode=false;
                          return tmp;
                      }
                      if (sequenceMode) {
                          tmpsequence.append(maf_current_line);
                      }
                      if (maf_current_line.startsWith(">")) {
                            //We have a fasta definition
                            tmp.setName(maf_current_line.substring(1)); //remove >
                            sequenceMode=true;
                      }
              maf_current_line=maf.readLine();
              maf_current_line_number++;
          } //end while
           close(); //close MAF file
           if (tmpsequence.length()>0) {
                tmp.setSequence(tmpsequence.toString());

                if (this.isLoadMAFinfos()) {
                   tmp.loadInfoFromName();
               }
                tmpsequence=new StringBuilder();
                return tmp;
           }
           return null;
          } catch(Exception e) {if (isDebug()) e.printStackTrace();return null;}
       }

    @Override
       public boolean hasNext() {
           return maf_open;
       }


       public void gotoLine(int line) {
           if (maf_open&&line>maf_current_line_number) {
              try {
                   while (maf.ready()) {
                      maf_current_line=maf.readLine();
                      maf_current_line_number++;
                      if (maf_current_line_number==line) return;
                  }
                  this.close(); //--No more MAF line
              } catch(Exception e) {}
           } //--End while
       }
      
       public mafcontig gotoContig(int number) {
           if(maf_open&&maf_contig_count<number) {
               while(hasNext()) {
                   mafcontig next=getNext();
                   if (maf_contig_count==number) return next;
               }
           }
           return null;
       }
       

    /**
     * @return the maf_identifier
     */
    public //--Current position in file
    String getMaf_identifier() {
        return getMaf_signature();
    }

    /**
     * @param maf_identifier the maf_identifier to set
     */
    public void setMaf_identifier(String maf_identifier) {
        this.setMaf_signature(maf_identifier);
    }
       


       /**
        * This will create an index of the MAF file in indexFilename
        * Note: the MAF file is specified in the constructor or via
        * setFilename();
        * @param identifier
        * @param indexFilename
        */
      public void createMAFIndex(String identifier, String indexFilename) {
        if (identifier.isEmpty()) identifier="s hg18";
        //On vide les contig deja dans l'object
        int count=0;
        Contig.clear();

        try {
        BufferedReader br = new BufferedReader(new FileReader(new File(getFilename())));
        //--Create an index of the MAF
        PrintWriter pw=new PrintWriter(new File(indexFilename));
        pw.println("#nbseq\tLine in file\tseqsize\tstart\tend\tnb species\tspecies");
        // VAriable local
        boolean inContig=false;
        mafcontig tmp = new mafcontig("tmp");
        String sequence="";
        String orientation="";
        String desc="";
        int position=0;  //position de la sequence en cours
        int numberLine=0;
        //REad the number of line in file
        //On lit toute les ligne
        //On assume que c'est un fichier humain + espece
        while (br.ready()) {
            String stri=br.readLine();
            //Cas 1: on a deja toute les information d'un congit -> On sauve
            if (inContig&&stri.startsWith("a")) {
                //save Contig to list
                Contig.add(tmp);
                tmp = new mafcontig("tmp");
                inContig=false;
                count++;
                if (count%1000==1) {
                    for (mafcontig t:Contig) {
                         //Config.log(t.toString());
                        //--TO DO PUT INFORMATION HERE...


                        //--This generate the Index in filename + ".txt";
                        pw.println(t.toString());
                    }
                    Config.log("*");
                    Contig.clear();
                }
            }

            //Cas 2: On est dans un contig, et on a une nouvelle sequence
             if (stri.startsWith("s")&&inContig) {
                String [] striv=stri.split(" ");
                for (int i=0; i<striv.length;i++) striv[i]=striv[i].trim(); //remove space
                int index=1;
                desc=striv[index].toUpperCase();
                for (int i=2;i<striv.length;i++) {
                    if (striv[i].equals("+")||striv[i].equals("-")) {
                        index=i;
                        break;
                    }
                }
                orientation=striv[index];
                sequence=striv[striv.length-1];
               //Config.log(orientation+TAB+desc+TAB+position+TAB+sequence);
               tmp.addSequence(desc, sequence,"", orientation);
             }

            //Cas 3: On a une nouvelle sequence, on est pas dans un contig
            // ->On verifie si on doit sauver
            //Note: on assume une sequence hg18.chr6
            if (stri.startsWith(identifier)&&!inContig) {
               //On a une sequence, la sequence devrait être la sequence humain
               //Identification
                String [] striv=stri.split(" ");
                for (int i=0; i<striv.length;i++) striv[i]=striv[i].trim(); //remove space
                int index=1;
                desc=striv[index].toUpperCase();
                //Position is the first non empty string
                for (int i=index+1;i<striv.length;i++) {
                  if (!striv[i].equals("")) {
                         index=i;
                         break;
                   }
                }
               try {
                    position=Integer.valueOf(striv[index]);
               } catch(Exception e) {}

                for (int i=2;i<striv.length;i++) {
                    if (striv[i].equals("+")||striv[i].equals("-")) {
                        index=i;
                        break;
                    }
                }
                orientation=striv[index];
                sequence=striv[striv.length-1];
                //Calcul de la longueur de la sequence sans "-"
                int end=sequence.length();
                
                      inContig=true;
                      tmp.setPosition_start((Integer) position);
                      tmp.setPosition_end((Integer) position+end);
                      tmp.setFilestart_line((Integer) numberLine);
                      tmp.addSequence(desc, sequence,"", orientation);

            } //--End in new contig for identifier

            numberLine++;
        } //--End while
        Config.log("done. Total "+count+" contig");
        br.close();
        pw.flush();
        pw.close();
        //this.InfoText.setText("Extraction des séquences réussie! On a extrait "+String.valueOf(Contig.size())+ " gènes extraits");
        } catch(Exception e) {e.printStackTrace();}
    }

     /**
      * Create a MAFIndex file with filename MAFfilename+".txt"
      * @param identifier
      */
     public void createMAFIndex(String identifier) {
         this.createMAFIndex(identifier, filename+".txt");
     }
    

    /**
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @param filename the filename to set
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * This parse the MAF file and ouput the number of line
     * @return
     */
    public int calculateNbLine() {
        int count=0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(getFilename())));
            Config.log("Counting the number of line in MAF "+filename);
            while(br.ready()) {
                br.readLine();
                count++;
                if (count%100000==1) Config.log("*");
            } //--End Count the number of line
            Config.log("done.");
            return count;
        } catch(Exception e) {Config.log("***Error: Unable to read MAF file "+filename);return 0;}

    }

    ////////////////////////////////////////////////////////////////////////////
    /// Helper function

    public int getInt(String stri) {
        try {
            return Integer.valueOf(stri);
        } catch(Exception e) {return 0;}
    }

    /**
     * @return the maf_contig_count
     */
    public int getMaf_contig_count() {
        return maf_contig_count;
    }

    /**
     * @return the maf_signature
     */
    public String getMaf_signature() {
        return maf_signature;
    }

    /**
     * Set the maf signature to look for example:
     *  hg18, hg19... for human alignment...
     * @param maf_signature the maf_signature to set
     */
    public void setMaf_signature(String maf_signature) {
        this.maf_signature = "s "+maf_signature;
    }

    /**
     * @return the loadMAFinfos
     */
    public boolean isLoadMAFinfos() {
        return loadMAFinfos;
    }

    /**
     * @param loadMAFinfos the loadMAFinfos to set
     */
    public void setLoadMAFinfos(boolean loadMAFinfos) {
        this.loadMAFinfos = loadMAFinfos;
    }

    

} //--End extract maf
