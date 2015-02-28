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

 /**
  * Class contig - Hold the extracted sequences of a particular alignment a gene
  *
  *  @author  Etienne Lord (c) 2009
  *
  */
import biologic.Alignment;
import biologic.Sequence;
import java.util.Vector;

public class mafcontig extends Alignment {
  //--Position (line)in MAF file
    private Integer position_start=0;
    private Integer position_end = 0;
    private //--Position (line)in MAF file
    Integer filestart_line = 0;

  /**
   *
   * @param name Le nom du gene
   */
  public mafcontig(String name) {
    this.name=name;
  }


  void addSequence(String name, String sequence, String quality, String strand) {
       Sequence tmp=new Sequence();
       tmp.setSequence(sequence.toUpperCase());
       tmp.setQuality(quality);
       tmp.setName(name);
       tmp.setOrientation(strand);
       this.add(tmp);
  }

  /**
   * Return the shorten name of the MAF sequence name...
   * Ex. hg18.chr22 -> hg18
   *
   * @param s (the sequence)
   * @return the shorten name
   */
  public String getShortName(Sequence s) {
      String sname=s.getName();
      if (sname.indexOf('.')>-1) sname=s.getName().substring(0,s.getName().indexOf('.'));
      return sname;
  }

  /**
   * Return all this mafcontig short name as a vector of String
   * @return a Vector of String containing the short name...
   */
  public Vector<String>getShortNames() {
      Vector<String>tmp=new Vector<String>();
      for (Sequence s:getSequences()) tmp.add(getShortName(s));
      return tmp;
  }
   

// /**
// * Helper function to return the number of other carachter
// */
//
// int returnOthers(int pos) {
//     if (pos<0||pos>sequence.size()) return -1;
//     String Sequence=sequence.get(pos);
//     int Number_of_Others=0;
//     for (int i=0; i<Sequence.length(); i++) {
//      char c = Sequence.charAt(i);
//      if (others(c)||gaps(c)) Number_of_Others++;
//     }
//
//   return Number_of_Others;
//   }

 /**
 * Helper function to find if character c is unknown or N
 */
 boolean others(char c) {
 if (c=='R') return true;
 if (c=='K') return true;
 if (c=='S') return true;
 if (c=='W') return true;
 if (c=='B') return true;
 if (c=='D') return true;
 if (c=='H') return true;
 if (c=='V') return true;
 if (c=='Y') return true;
 if (c=='N') return true;
 return false;
 }

 /**
 * Helper function to find if character c is a gap
 */
 boolean gaps(char c) {
 if (c=='-') return true;
 return false;
 }

 @Override
 public String toString() {
       int max=0;
       for (Sequence s:this.getSequences()) max=(s.getLen()>max?s.getLen():max);
       //"(#seq, size (bp), start, end in hg18, nb species, species)\t
       return String.valueOf(this.getSequences().size())+"\t"+this.getFilestart_line()+"\t"+max+"\t"+getPosition_start()+"\t"+getPosition_end()+"\t"+VectorToString(this.getShortNames());
        }

    public String toStringDetails() {
        String s=toString()+"\n";
        for (Sequence sa:getSequences()) {
            s+="\t"+sa.getName()+"\n";
        }
        return s;
    }

    public String VectorToString(Vector<String>string) {
        String S="";
        for(String st:string) S+=st+"\t";
        return S;
    }

    @Override
    protected mafcontig clone() {
       mafcontig tmp=new mafcontig(this.name);
       tmp.getSequences().addAll(this.getSequences());
        tmp.setPosition_start(this.getPosition_start());
        tmp.setPosition_end(this.getPosition_end());
        tmp.setFilestart_line(this.getFilestart_line());
       tmp.setName(this.getName());
       return tmp;
    }

    /**
     * This will add the toadd mafcontig to the present one
     * if the sequence are not found, gap will be introduces...
     *
     * @param toadd
     * @return
     */
     public mafcontig concatenate(mafcontig contigToAdd) {
         String gap=createGap();
         String newgap=contigToAdd.createGap();
         mafcontig tmpcontig=this.clone();

         //Config.log(gap.length()+":"+this.getSequenceSize());
         for (Sequence s:contigToAdd.getSequences()) {
             int index=tmpcontig.findIndexByShortName(s);
             //CASE 1: Not found
             if (index==-1) {
               tmpcontig.addSequence(s.getName(), gap+s.getSequence(), s.getQuality(), s.getOrientation());
             } else {
             //CASE 2: Sequence found, concatenate
               Sequence tmp=tmpcontig.getSequences().get(index);
               tmp.addToSequence(s.getSequence());
               tmp.addToQuality(s.getQuality());
             }
         }
         //Put some GAP if we did'nt add sequence
         int maxlen=tmpcontig.getSequenceSize();
         for (Sequence s:tmpcontig.getSequences()) {
             
             if (s.getSize()<maxlen) {
                 s.addToSequence(newgap);
                 s.addToQuality(newgap);
              }
           
         }
         tmpcontig.setNote(this.getNote()+contigToAdd.getNote());
         return tmpcontig;
       }

    public String createGap() {
        String tmp="";
        for (int i=0; i<getSequenceSize();i++) tmp+="-";
        return tmp;
    }

    /**
     * @return the position_start
     */
    public Integer getPosition_start() {
        return position_start;
    }

    /**
     * @param position_start the position_start to set
     */
    public void setPosition_start(Integer position_start) {
        this.position_start = position_start;
    }

    /**
     * @return the position_end
     */
    public Integer getPosition_end() {
        return position_end;
    }

    /**
     * @param position_end the position_end to set
     */
    public void setPosition_end(Integer position_end) {
        this.position_end = position_end;
    }

    /**
     * @return the filestart_line
     */
    public Integer getFilestart_line() {
        return filestart_line;
    }

    /**
     * @param filestart_line the filestart_line to set
     */
    public void setFilestart_line(Integer filestart_line) {
        this.filestart_line = filestart_line;
    }

     /**
    * Return the sequence index
    * @param shortname
    * @return
    */
   public int findIndexByShortName(Sequence tofind) {
       int index=0;
       for (Sequence s:seq) {
          if (getShortName(s).equals(getShortName(tofind))) return index;
          index++;
       }
       return -1;
   }

   public void replaceNameWithShortName() {
       for (Sequence s:getSequences()) {
           s.setName(this.getShortName(s));
       }
   }

 }

