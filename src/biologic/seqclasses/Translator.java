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
import biologic.Sequence;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is used to translate a DNA to Protein and back
 * @author Etienne Lord
 */
public class Translator {

    public static Vector<GeneticCode>code;;
    public int start=0; //Start codon


    public Translator() {
        if (code==null) {
            code=new Vector<GeneticCode>();
            if(!loadCode()) Config.log("Error. Unable to read GeneticCode...");
        }
    }

    public boolean loadCode() {
        Pattern p_name=Pattern.compile("name.\"(.*)\"");
        Pattern p_id=Pattern.compile("id.([0-9]{1,})");
        Pattern p_ncbieaa=Pattern.compile("ncbieaa..\"(.*)\"");
        Pattern p_sncbieaa=Pattern.compile("sncbieaa.\"(.*)\"");
        Pattern p_base1=Pattern.compile("Base1.*(T.{62,}G)");
        Pattern p_base2=Pattern.compile("Base2.*(T.{62,}G)");
        Pattern p_base3=Pattern.compile("Base3.*(T.{62,}G)");
        GeneticCode current=new GeneticCode();

        String base1="",base2="",base3="";
        String ncbieaa="",sncbieaa="";

        try {
            InputStream in=getClass().getResourceAsStream("geneticcode.asn");
            BufferedReader br=new BufferedReader(new InputStreamReader(in));
            while (br.ready()) {
                String stri=br.readLine();
                    Matcher m=p_name.matcher(stri);
                if (m.find()) current.setName(current.getName() + m.group(1) + " ");
                    Matcher m2=p_id.matcher(stri);
                if (m2.find())current.setId(Integer.valueOf(m2.group(1)));
                    Matcher m3=p_ncbieaa.matcher(stri);
                if (m3.find())ncbieaa=m3.group(1);
                    Matcher m4=p_sncbieaa.matcher(stri);
                if (m4.find())sncbieaa=m4.group(1);
                    Matcher m5=p_base1.matcher(stri);
                if (m5.find())base1=m5.group(1);
                    Matcher m6=p_base2.matcher(stri);
                if (m6.find())base2=m6.group(1);
                    Matcher m7=p_base3.matcher(stri);
                if (m7.find()){
                    base3=m7.group(1);
                    //--Compute code
                    for (int i=0; i<base1.length();i++) {
                        String codon=""+base1.charAt(i)+base2.charAt(i)+base3.charAt(i);
                        String AA=""+ncbieaa.charAt(i);
                        String Start=""+sncbieaa.charAt(i);
                        current.getCode().put(codon, AA);
                        if (!Start.equals("-")) current.getStart_code().put(codon,Start);
                    }
                    code.add(current);
                    current=new GeneticCode();
                }
            }
        } catch(Exception e) {e.printStackTrace();return false;}
        return true;
    }

    public String translate_without_stop(Sequence s,GeneticCode gncode) {
        String aa=this.translate(s, gncode);
        aa=aa.replace('*', '-');
        return aa;
    }

    public String translate(Sequence s, GeneticCode gncode) {
        String AA="";
        Sequence n=new Sequence();
        n.setSequence(s.getSequence().toUpperCase());
        int pos=0;
        boolean found=false;
        //--Find start codon

//        while(!found&&pos<n.getLen()-2) {
//            String start=n.getSequence().substring(pos, pos+3);
//            if (gncode.getStart_code().get(start)!=null) {
//                AA+=""+gncode.getStart_code().get(start);
//                pos+=3;
//                found=true;
//            }
//            pos++;
//        }
        //--return empty string if not found
//        if (!found) {
//            Config.log("No Start");
//            return AA;
//        }
        pos=start;
        //--Continue
        n.Remove(0, pos); //remove till start
        
        String codon=n.Remove(0, 3);
        //Config.log(n.getSequence());
        while(!codon.isEmpty()&&n.getLen()>0) {
             //Config.log(codon+" "+gncode.getCode().get(codon));
             String aa=gncode.getCode().get(codon);
             //Config.log(aa);
             if (aa!=null) {                 
                 AA+=aa;                 
             } else {
                 if (codon.length()==3) AA+="-";
             }
             codon=n.Remove(0, 3);
        }
        //--Remove ending "*" if found
       if (AA.endsWith("*")) {
            AA=AA.substring(0, AA.length()-1);
        }
        return AA;
    }

    public String aa_to_dna(Sequence original_dna, Sequence protein, GeneticCode gncode) {
        LinkedList<String>codons=new LinkedList<String>();
        String AA="";
        String dna="";
        Sequence n=new Sequence();
        n.setSequence(original_dna.getSequence().toUpperCase());
        int pos=0;
        boolean found=false;
        //--Find start codon

//        while(!found&&pos<n.getLen()-2) {
//            String start=n.getSequence().substring(pos, pos+3);
//            if (gncode.getStart_code().get(start)!=null) {
//                AA+=""+gncode.getStart_code().get(start);
//                codons.add(start);
//                pos+=3;
//                found=true;
//            }
//            pos++;
//        }
        //--return empty string if not found
//        if (!found) {
//            Config.log("No Start");
//            return AA;
//        }

        pos=start;
        //--MAKE THE ORIGINAL TRANSLATION
        n.Remove(0, pos); //remove till start
        String codon=n.Remove(0, 3);
        //--Debug
        //Config.log(n.getSequence());
        while(!codon.isEmpty()&&n.getLen()>0) {
             //Config.log(codon+" "+gncode.getCode().get(codon));
             String aa=gncode.getCode().get(codon);
             //--Debug
             //Config.log(aa);
             if (aa!=null) {
                 AA+=aa;
                 codons.add(codon);
             } else {
                 if (codon.length()==3) AA+="-";
             }
             codon=n.Remove(0, 3);
        }
        //--MAKE THE REAL cDNA
        for (int i=0; i<protein.getLen();i++) {
            char c=protein.getSequence().charAt(i);
            //Config.log(c);
            if (c=='-'||c=='.') {
                dna+="---";
            } else {
                //--remove stop codon
                String codon_to_add=codons.poll();
                if (codon_to_add!=null) {
                    if (gncode.getCode().get(codon_to_add).equals("*")) codon_to_add="---";
                    dna+=codon_to_add;
                }
            }
        }
    return dna;
    }
 
    /**
     * Return the index in the code Vector of this genetic code
     * @param genetic_code
     * @return 0 if not found, the index otherwise...
     */
    public int getIndex(int genetic_code) {        
         for (int i=0; i<code.size();i++) {
             GeneticCode g=code.get(i);
             if (g.getId()==genetic_code) return i;
         }
        return 0;
    }

    @Override
    public String toString() {
        String str="";
        for (GeneticCode g:code) {
            str+=g+"\n";
        }
        return str;
    }


}
