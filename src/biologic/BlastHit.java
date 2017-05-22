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

import java.io.Serializable;

/**
 *
 * @author Etienne Lord
 * @since 2010
 */
public class BlastHit extends Text implements Comparable, Serializable  {
    ////////////////////////////////////////////////////////////////////////////
    /// Variables
    
    //Fields


      public int id=0;
      public int BlastHitList_id=0;
      public int RunProgram_id=0;
      public String dbname="";
      private boolean selected=false;

      public String query_id="";
      public String query_accession="";
      public String query_name="";

      public int subject_id=0; //--In Armadillo db
      public String subject_id_gi="";
      public String subject_accession="";
      public String subject_accession_referree="";
      public String subject_name="";

      public String qstrand=""; //Stand of the query   : ex. Plus/Minus
      public String sstrand=""; //Stand of the subject : ex. Plus/Minus

      public float identity=0;
      public float alignment_length=0;
      public float query_length=0;
      public float subject_length=0;
      public float positives=0;
      public float missmatches=0;
      public float gap=0;
      public int   qstart=0;
      public int   qend=0;
      public int   sstart=0;
      public int   send=0;
      public double evalue=0;
      public double bitscore=0;
      public double score=0;

      //--New sequence information?
      public String query_sequence="";
      public String subject_sequence="";

  ////////////////////////////////////////////////////////////////////////////////
/// CONSTRUCTOR

    public BlastHit() {} //Default constructor

//    public BlastHit(int id) {
//        loadFromDatabase(id);
//    }
//
//    public boolean loadFromDatabase(int id) {
//        BlastHit seq=df.getBlastHit(id);
//        if (seq.getId()>0) {
//
////           this.setSequenceStats_id(seq.getSequenceStats_id());
//            this.setId(id);
//        return true;
//        } else return false;
//    }
//
//    public boolean saveToDatabase() {
//        setId(df.addSequence(this));
//        return (getId()==0?false:true);
//    }
//
//    public boolean removeFromDatabase() {
//        return df.removeSequence(this);
//    }
//
//    public boolean updateDatabase() {
//        return df.updateSequence(this);
//    }

////////////////////////////////////////////////////////////////////////////////
/// Iterator

//    Vector<Integer>next=new Vector<Integer>();
//    int counter=0;
//    int maxid=-1;
//    public boolean hasNext() {
//       //next=df.getAllSequenceID();
//       if (next.size()==0) {
//           next=df.getAllBlastHitID();
//           maxid=next.size();
//       }
//       return (this.counter<maxid);
//    }
//
//    public Object next() {
//        return new Sequence(next.get(counter++));
//    }
//
//    public void remove() {
//        Sequence s=new Sequence(counter-1);
//        s.removeFromDatabase();
//    }
  
  public static String getToStringParam() {
        return "qstrand"+"\t"+"sstrand"+"\t"+"evalue"+"\t"+"bitscore"+"\t"+"subject_accession_referree"+"\t"+"subject_accession"+"\t"+"subject_name"+"\t"+"identity"+"\t"+
                "alignment_length"+"\t"+"query_length"+"\t"+"subject_length"+"\t"+"missmatches"+"\t"+"gap"+"\t"+"qstart"+"\t"+"query"+"\t"+"qend"+"\t"+"sstart"+"\t"+"subject"+"\t"+"send";
    }
  
    public String getString() {
        String stri=getQstrand()+"\t"+
                getSstrand()+"\t"+
                getEvalue()+"\t"+
                getBitscore()+"\t"+
                getSubject_accession_referree()+"\t"+
                getSubject_accession()+"\t"+
                getSubject_name()+"\t"+
                getIdentity()+"\t"+
                getAlignment_length()+"\t"+
                getQuery_length()+"\t"+
                getSubject_length()+"\t"+
                getMissmatches()+"\t"+
                getGap()+"\t"+
                getQstart()+"\t"+
                query_sequence+"\t"+
                getQend()+"\t"+
                getSstart()+"\t"+
                subject_sequence+"\t"+
                getSend();
        return stri;
    }

    public int compareTo(Object o) {
        BlastHit b=(BlastHit)o;
        //if (this.getSubject_name().startsWith(b.getSubject_name())) return 0;
        if (this.getEvalue()==b.getEvalue()) return 0;
        if (this.getEvalue()==0&&b.getEvalue()>0) return -1;
        if (this.getEvalue()==0&&b.getEvalue()<0) return +1;
//
//        if (this.getEvalue()<b.getEvalue()) return 1;
//        if (this.getEvalue()>b.getEvalue()) return -1;
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BlastHit)) return false;
        BlastHit b=(BlastHit)obj;
        //if (this.getSubject_name().equals(b.getSubject_name())) return true;
        return this.getSubject_accession().equals(b.getSubject_accession());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (this.getSubject_accession() != null ? this.getSubject_accession().hashCode() : 0);
        return hash;
    }

  
    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the BlastHitList_id
     */
    public int getBlastHitList_id() {
        return BlastHitList_id;
    }

    /**
     * @param BlastHitList_id the BlastHitList_id to set
     */
    public void setBlastHitList_id(int BlastHitList_id) {
        this.BlastHitList_id = BlastHitList_id;
    }

    /**
     * @param RunProgram_id the RunProgram_id to set
     */
    public void setRunProgram_id(int RunProgram_id) {
        this.RunProgram_id = RunProgram_id;
    }

    /**
     * @return the dbname
     */
    public String getDbname() {
        return dbname;
    }

    /**
     * @param dbname the dbname to set
     */
    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    /**
     * @return the query_id
     */
    public String getQuery_id() {
        return query_id;
    }

    /**
     * @param query_id the query_id to set
     */
    public void setQuery_id(String query_id) {
        this.query_id = query_id;
    }

    /**
     * @return the query_accession
     */
    public String getQuery_accession() {
        return query_accession;
    }

    /**
     * @param query_accession the query_accession to set
     */
    public void setQuery_accession(String query_accession) {
        this.query_accession = query_accession;
    }

    /**
     * @return the query_name
     */
    public String getQuery_name() {
        return query_name;
    }

    /**
     * @param query_name the query_name to set
     */
    public void setQuery_name(String query_name) {
        this.query_name = query_name;
    }

    /**
     * @return the subject_id
     */
    public int getSubject_id() {
        return subject_id;
    }

    /**
     * @param subject_id the subject_id to set
     */
    public void setSubject_id(int subject_id) {
        this.subject_id = subject_id;
    }

    /**
     * @return the subject_id_gi
     */
    public String getSubject_id_gi() {
        return subject_id_gi;
    }

    /**
     * @param subject_id_gi the subject_id_gi to set
     */
    public void setSubject_id_gi(String subject_id_gi) {
        this.subject_id_gi = subject_id_gi;
    }

    /**
     * @return the subject_accession
     */
    public String getSubject_accession() {
        return subject_accession;
    }

    /**
     * @param subject_accession the subject_accession to set
     */
    public void setSubject_accession(String subject_accession) {
        this.subject_accession = subject_accession;
    }

    /**
     * @return the subject_accession_referree
     */
    public String getSubject_accession_referree() {
        return subject_accession_referree;
    }

    /**
     * @param subject_accession_referree the subject_accession_referree to set
     */
    public void setSubject_accession_referree(String subject_accession_referree) {
        this.subject_accession_referree = subject_accession_referree;
    }

    /**
     * @return the subject_name
     */
    public String getSubject_name() {
        return subject_name;
    }

    /**
     * @param subject_name the subject_name to set
     */
    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
    }

    /**
     * @return the qstrand
     */
    public String getQstrand() {
        return qstrand;
    }

    /**
     * @param qstrand the qstrand to set
     */
    public void setQstrand(String qstrand) {
        this.qstrand = qstrand;
    }

    /**
     * @return the sstrand
     */
    public String getSstrand() {
        return sstrand;
    }

    /**
     * @param sstrand the sstrand to set
     */
    public void setSstrand(String sstrand) {
        this.sstrand = sstrand;
    }

    /**
     * @return the identity
     */
    public float getIdentity() {
        return identity;
    }

    /**
     * @param identity the identity to set
     */
    public void setIdentity(float identity) {
        this.identity = identity;
    }

    /**
     * @return the alignment_length
     */
    public float getAlignment_length() {
        return alignment_length;
    }

    /**
     * @param alignment_length the alignment_length to set
     */
    public void setAlignment_length(float alignment_length) {
        this.alignment_length = alignment_length;
    }

    /**
     * @return the query_length
     */
    public float getQuery_length() {
        return query_length;
    }

    /**
     * @param query_length the query_length to set
     */
    public void setQuery_length(float query_length) {
        this.query_length = query_length;
    }

    /**
     * @return the subject_length
     */
    public float getSubject_length() {
        return subject_length;
    }

    /**
     * @param subject_length the subject_length to set
     */
    public void setSubject_length(float subject_length) {
        this.subject_length = subject_length;
    }

    /**
     * @return the positives
     */
    public float getPositives() {
        return positives;
    }

    /**
     * @param positives the positives to set
     */
    public void setPositives(float positives) {
        this.positives = positives;
    }

    /**
     * @return the missmatches
     */
    public float getMissmatches() {
        return missmatches;
    }

    /**
     * @param missmatches the missmatches to set
     */
    public void setMissmatches(float missmatches) {
        this.missmatches = missmatches;
    }

    /**
     * @return the gap
     */
    public float getGap() {
        return gap;
    }

    /**
     * @param gap the gap to set
     */
    public void setGap(float gap) {
        this.gap = gap;
    }

    /**
     * @return the qstart
     */
    public int getQstart() {
        return qstart;
    }

    /**
     * @param qstart the qstart to set
     */
    public void setQstart(int qstart) {
        this.qstart = qstart;
    }

    /**
     * @return the qend
     */
    public int getQend() {
        return qend;
    }

    /**
     * @param qend the qend to set
     */
    public void setQend(int qend) {
        this.qend = qend;
    }

    /**
     * @return the sstart
     */
    public int getSstart() {
        return sstart;
    }

    /**
     * @param sstart the sstart to set
     */
    public void setSstart(int sstart) {
        this.sstart = sstart;
    }

    /**
     * @return the send
     */
    public int getSend() {
        return send;
    }

    /**
     * @param send the send to set
     */
    public void setSend(int send) {
        this.send = send;
    }

    /**
     * @return the evalue
     */
    public double getEvalue() {
        return evalue;
    }

    /**
     * @param evalue the evalue to set
     */
    public void setEvalue(double evalue) {
        this.evalue = evalue;
    }

    /**
     * @return the bitscore
     */
    public double getBitscore() {
        return bitscore;
    }

    /**
     * @param bitscore the bitscore to set
     */
    public void setBitscore(double bitscore) {
        this.bitscore = bitscore;
    }

    /**
     * @return the score
     */
    public double getScore() {
        return score;
    }

    /**
     * @param score the score to set
     */
    public void setScore(double score) {
        this.score = score;
    }

    /**
     * @return the query_sequence
     */
    public String getQuery_sequence() {
        return query_sequence;
    }

    /**
     * @param query_sequence the query_sequence to set
     */
    public void setQuery_sequence(String query_sequence) {
        this.query_sequence = query_sequence;
    }

    /**
     * @return the subject_sequence
     */
    public String getSubject_sequence() {
        return subject_sequence;
    }

    /**
     * @param subject_sequence the subject_sequence to set
     */
    public void setSubject_sequence(String subject_sequence) {
        this.subject_sequence = subject_sequence;
    }

    /**
     * @return the selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String getBiologicType() {
        return "BlastHit";
    }
  }
