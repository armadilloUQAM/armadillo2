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

package biologic;


import configuration.Util;
import java.io.Serializable;

/**
 * Simple holder of eSummary from Ncbi or Sequence information
 * @author Lord Etienne, Leclercq Mickael
 * @since Avril 2009
 */ 
public class InfoSequence extends Sequence implements Comparable, Serializable {
    private static final long serialVersionUID = 20090426L;

    private String hgncid="";         //Ensembldb (to download from)
    private String ensemblid = "";
    private String emsembldb = "";
    private String genbankdb="";

    private String description = "";     //Small description of the Sequence
    
    
    // Filename associated and is downloaded (used while downloading from Ncbi)
    private String fastaFilename="";     //fastaFilename
    private String genbankFilename="";   //genbankFilename
    private String ensemblFilename="";   //ensemblFilename
                                         //Note: MUST BE AN OBJECT BECAUSE WE
                                         //USE THE POINTER IN THE LOADING PROCESS
    private int type=0;                  //From where come this information
    private String stats="";             //Statistiques (note: calculated in databaseFunction)
    
    ////////////////////////////////////////////////////////////////////////////
    /// Flag
   
    private boolean loaded = false;                 //InfoSummary Loaded?
    private boolean inDatabase=false;               //Sequence already in database?
    private boolean fastaDownloaded=false;
    private boolean genbankDownloaded = false;
    private boolean ensemblDownloaded = false;
    private boolean modified=false;           //Information was changed from the original information?
    private boolean loading=false;         //We are downloading the file (depreciated)
    private boolean found=false;           //File is found on disk


    ////////////////////////////////////////////////////////////////////////////
    /// Type
    public static final int type_Ncbi=10;
    public static final int type_Ensembl = 20;
    public static final int type_HGNC = 30;

    ////////////////////////////////////////////////////////////////////////////
    /// Other
    
       
    public InfoSequence() {}
       
    public InfoSequence(int id) {
        this.loadFromDatabase(id);
    }

    @Override
    public boolean loadFromDatabase(int id) {
        InfoSequence seq=df.getInfoSequence(id);
        if (seq.getId()>0) {
            this.setName(seq.getName());
            this.setGi(seq.getGi());
            this.setAccession(seq.getAccession());
            this.setAccession_referee(seq.getAccession_referee());
            this.setAbbreviate(seq.getAbbreviate());
            this.setLen(seq.getLen());
            this.setType(seq.getType());           
            this.id=id;
            return true;
        } else return false;
    }

    public boolean update() {
        return df.updateSequence(this);
    }


    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }
  
    /**
     * @return the loaded
     */
    public boolean isLoaded() {
        return (loaded);
    }

//    Utilisé dans la liste
    @Override
    public int compareTo(Object o) {
        InfoSequence tmp=(InfoSequence)o;
        //Note: there is no break since we want to check all possiblitity
        //Note: we don't check for description since it can be the same
        switch (this.getType()) {
            case type_HGNC:
                if (!this.getHgncid().equals("")) return (this.getHgncid().compareTo(tmp.getHgncid()));
            case type_Ensembl:
                if (!this.getEnsemblid().equals("")) return (this.getEnsemblid().compareTo(tmp.getEnsemblid()));
            default:
                if (!this.getGi().equals("")) return (this.getGi().compareTo(tmp.getGi()));
                if (!this.getAccession().equals("")) return (this.getAccession().compareTo(tmp.getAccession()));
        }
        //Config.log("Error in compare");
        return 0; //Pas d'acession et pas de gi, pas de EnsemblId donc equals
    }


    @Override
    public boolean equals(Object o) {
        if (o==null) return false;
        if (!o.getClass().equals(this.getClass())) return false;
        InfoSequence tmp=(InfoSequence)o;
        return (this.compareTo(tmp)==0?true:false);
    }

    @Override
    public String toString() {
        //return this.getType()+"\t"+this.getHgncid()+"\t"+this.getEnsemblid()+"\t"+this.getGi()+"\t"+this.getAccession()+"\t"+this.getEmsembldb()+"\t"+this.getDescription()+"\t"+this.getLen()+"\t";
        //return this.getType()+"\t"+this.getHgncid()+"\t"+this.getEnsemblid()+"\t"+this.getEmsembldb()+"\t"+this.getGi()+"\t"+this.getEmsembldb()+"\t"+this.getAccession()+"\t"+this.getDescription()+"\t"+this.getLen()+"\t";
        return String.format("%d | %s | %s %s",id, name, len, getUnit());
    }

     public String toString2() {
        //return this.getType()+"\t"+this.getHgncid()+"\t"+this.getEnsemblid()+"\t"+this.getGi()+"\t"+this.getAccession()+"\t"+this.getEmsembldb()+"\t"+this.getDescription()+"\t"+this.getLen()+"\t";
        return this.getType()+"\t"+this.getHgncid()+"\t"+this.getEnsemblid()+"\t"+this.getEmsembldb()+"\t"+this.getGi()+"\t"+this.getEmsembldb()+"\t"+this.getAccession()+"\t"+this.getDescription()+"\t"+this.getLen()+"\t";
        
    }

       /**
     * @return the fastaFilename
     */
    public String getFastaFilename() {
        return fastaFilename;
    }

    /**
     * @param fastaFilename the fastaFilename to set
     */
    public void setFastaFilename(String fastaFilename) {
        this.fastaFilename = fastaFilename;
    }

    /**
     * @return the genbankFilename
     */
    public String getGenbankFilename() {
        return genbankFilename;
    }

    /**
     * @param genbankFilename the genbankFilename to set
     */
    public void setGenbankFilename(String genbankFilename) {
        this.genbankFilename = genbankFilename;
    }

    /**
     * @return the loading
     */
    public boolean isLoading() {
        return loading;
    }

    /**
     * @param loading the loading to set
     */
    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    /**
     * @return the found
     */
    public boolean isFound() {
        return found;
    }

    /**
     * Do we have the file?
     * Normaly called when we create the infoSummary
     */
    public void findFile() {
        if (fastaFilename.equals("")||genbankFilename.equals("")) {
            found=false;
        } else {
            if ((type==type_Ncbi||type==type_HGNC)&&Util.FileExists(fastaFilename)&&Util.FileExists(genbankFilename)) found=true;
            if (type==type_Ensembl&&Util.FileExists(ensemblFilename)) found=true;
        }
    }

    /**
     * @return the hgncid
     */
    public String getHgncid() {
        return hgncid;
    }

    /**
     * @return the ensemblid
     */
    public String getEnsemblid() {
        return ensemblid;
    }

    /**
     * @return the emsembldb
     */
    public String getEmsembldb() {
        return emsembldb;
    }

    /**
     * @return the ensemblFilename
     */
    public String getEnsemblFilename() {
        return ensemblFilename;
    }

    /**
     * @return the fastaDownloaded
     */
    public boolean isFastaDownloaded() {
        return fastaDownloaded;
    }

    /**
     * @return the genbankDownloaded
     */
    public boolean isGenbankDownloaded() {
        return genbankDownloaded;
    }

    /**
     * @return the ensemblDownloaded
     */
    public boolean isEnsemblDownloaded() {
        return ensemblDownloaded;
    }

    /**
     * @return the type (Ensembl, HGNC, Ncbi...)
     */

    public int getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @param hgncid the hgncid to set
     */
    public void setHgncid(String hgncid) {
        this.hgncid = hgncid;
    }

    /**
     * @param ensemblid the ensemblid to set
     */
    public void setEnsemblid(String ensemblid) {
        this.ensemblid = ensemblid;
    }

    /**
     * @param emsembldb the emsembldb to set
     */
    public void setEmsembldb(String emsembldb) {
        this.emsembldb = emsembldb;
    }

   
    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

  
    /**
     * @param loaded the loaded to set
     */
    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    /**
     * @param ensemblFilename the ensemblFilename to set
     */
    public void setEnsemblFilename(String ensemblFilename) {
        this.ensemblFilename = ensemblFilename;
    }

    /**
     * @param fastaDownloaded the fastaDownloaded to set
     */
    public void setFastaDownloaded(boolean fastaDownloaded) {
        this.fastaDownloaded = fastaDownloaded;
    }

    /**
     * @param genbankDownloaded the genbankDownloaded to set
     */
    public void setGenbankDownloaded(boolean genbankDownloaded) {
        this.genbankDownloaded = genbankDownloaded;
    }

    /**
     * @param ensemblDownloaded the ensemblDownloaded to set
     */
    public void setEnsemblDownloaded(boolean ensemblDownloaded) {
        this.ensemblDownloaded = ensemblDownloaded;
    }

    /**
     * @param found the found to set
     */
    public void setFound(boolean found) {
        this.found = found;
    }

 
     /**
     * @param sequence the sequence to set
     */
    public void setInfoSequence(Sequence sequence) {
        this.gi=sequence.getGi();
        this.accession=sequence.getAccession();
        this.setAccession_referee(sequence.getAccession_referee());
        this.len=sequence.getLen();
        this.name=sequence.getName();
        this.setOrientation(sequence.getOrientation());
    }

    /**
     * @return the stats
     */
    public String getStats() {
        return stats;
    }

    /**
     * @param stats the stats to set
     */
    public void setStats(String stats) {
        this.stats = stats;
    }
    

    /**
     * @return the inDatabase
     */
    public boolean isInDatabase() {
        return inDatabase;
    }

    /**
     * @param inDatabase the inDatabase to set
     */
    public void setInDatabase(boolean inDatabase) {
        this.inDatabase = inDatabase;
    }

 
    /**
     * @return the modified
     */
    public boolean isModified() {
        return modified;
    }

    /**
     * @param modified the modified to set
     */
    public void setModified(boolean modified) {
        this.modified = modified;
    }

    /**
     * @return the genbankdb
     */
    public String getGenbankdb() {
        return genbankdb;
    }

    /**
     * @param genbankdb the genbankdb to set
     */
    public void setGenbankdb(String genbankdb) {
        this.genbankdb = genbankdb;
    }

   

  

}
