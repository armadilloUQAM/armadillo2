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

import biologic.Alignment;
import biologic.Sequence;
import configuration.Util;
import java.io.*;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import results.report;

/**
 *
 * @author Etienne Lord
 */
public class LoadPAML {

    static Pattern original_alignment;
    static Pattern final_alignment;
    static Pattern info_alignment;
    static Pattern alignment;
    static Pattern out_alignment;
    static Pattern instatistic;
    static Pattern statistic;
    static Pattern BEB;
    static Pattern NEB;

    int original_seq_nb=0;
    int original_seq_lenght=0;
    int final_seq_nb=0;
    int final_seq_lenght=0;

    private boolean found_original=false;
    private boolean found_final=false;
    private boolean found_statistic=false;
    private boolean found_BEB=false;
    private boolean found_NEB=false;

    private Alignment aa_original=new Alignment();
    private Alignment nn_original=new Alignment();
    private Alignment aa_final=new Alignment();

    Translator trans=new Translator();
    Vector<statisticPAML>annotation=new Vector<statisticPAML>();

    public LoadPAML() {
        if (original_alignment==null) original_alignment=Pattern.compile("Before deleting alignment gaps");
        if (final_alignment==null) final_alignment=Pattern.compile("After deleting gaps..([0-9]*).sites");
        if (out_alignment==null) out_alignment=Pattern.compile("Printing out site pattern counts");
        if (info_alignment==null) info_alignment=Pattern.compile("\\s*([0-9]*)\\s*([0-9]*)");
        if (alignment==null) alignment=Pattern.compile("(\\S*)\\s*(.*)");
        if (instatistic==null) instatistic=Pattern.compile("Pr\\(w>1\\)");
        if (statistic==null) statistic=Pattern.compile("([0-9]*)\\s([A-Z])\\s*([0-9]*.[0-9]*)(.*)");
        if (BEB==null) BEB=Pattern.compile("Bayes Empirical Bayes");
        if (NEB==null) NEB=Pattern.compile("Naive Empirical Bayes");
    }

    public LoadPAML(String filename) {
        if (original_alignment==null) original_alignment=Pattern.compile("Before deleting alignment gaps");
        if (final_alignment==null) final_alignment=Pattern.compile("After deleting gaps..([0-9]*).sites");
        if (out_alignment==null) out_alignment=Pattern.compile("Printing out site pattern counts");
        if (info_alignment==null) info_alignment=Pattern.compile("\\s*([0-9]*)\\s*([0-9]*)");
        if (alignment==null) alignment=Pattern.compile("(\\S*)\\s*(.*)");
        if (instatistic==null) instatistic=Pattern.compile("Pr\\(w>1\\)");
        if (statistic==null) statistic=Pattern.compile("([0-9]*)\\s([A-Z])\\s*([0-9]*.[0-9]*)(.*)");
        if (BEB==null) BEB=Pattern.compile("Bayes Empirical Bayes");
        if (NEB==null) NEB=Pattern.compile("Naive Empirical Bayes");
       this.generateResultFor(filename, filename+".html", filename);
    }

    public boolean generateResultFor(String infile, String outfile, String projectName) {
        Util u=new Util();
        trans.loadCode();
        try {
            BufferedReader br=new BufferedReader(new FileReader(new File(infile)));
            while(br.ready()) {
                String stri=br.readLine();
                Matcher m1=original_alignment.matcher(stri);
                Matcher m2=final_alignment.matcher(stri);
                Matcher m3=info_alignment.matcher(stri);
                Matcher m4=alignment.matcher(stri);
                Matcher m5=out_alignment.matcher(stri);
                Matcher m6=instatistic.matcher(stri);
                Matcher m7=statistic.matcher(stri);
                Matcher m8=NEB.matcher(stri);
                Matcher m9=BEB.matcher(stri);

                boolean fm1=m1.find();
                boolean fm2=m2.find();
                boolean fm3=m3.find();
                boolean fm4=m4.find();
                boolean fm5=m5.find();
                boolean fm6=m6.find();
                boolean fm7=m7.find();
                boolean fm8=m8.find();
                boolean fm9=m9.find();

                //--CASE 1. Found original or final alignment
                if (!stri.isEmpty()) {
                if (fm1) {
                    found_original=true;
                } else
                if (fm2) {
                    found_original=false;
                    found_final=true;

                }  else
                if (fm5) {
                    found_final=false;
                } else
                if (fm6) {
                    found_statistic=true;
                }
                if (fm8) {
                    this.found_NEB=true;
                }
                if (fm9) {
                    this.found_NEB=false;
                    this.found_BEB=true;
                }

                if (fm3&&!fm1&&!fm2) {
                    if(found_original) {
                        try {
                        this.original_seq_nb=Integer.valueOf(m3.group(1));
                        this.original_seq_lenght=Integer.valueOf(m3.group(2));
                        //System.out.println(original_seq_nb+" "+original_seq_lenght+" "+stri);
                        } catch(Exception e) {}
                    }
                    if (found_final) {
                         try {
                        this.final_seq_nb=Integer.valueOf(m3.group(1));
                        this.final_seq_lenght=Integer.valueOf(m3.group(2));
                         //System.out.println(final_seq_nb+" "+final_seq_lenght+" "+stri);
                        } catch(Exception e) {}
                    }
                }
                if (fm4&&!fm1&&!fm2) {
                    if (found_original) {
                        Sequence s=new Sequence();
                        Sequence o=new Sequence();
                        s.setName(m4.group(1));
                        s.setSequence(m4.group(2).replaceAll(" ",""));
                        o.setName(s.getName());
                        o.setSequence(s.getSequence());
                        this.nn_original.add(o);
                        s.setSequence(trans.translate(s, trans.code.get(0)));
                        this.aa_original.add(s);                       
                    }
                    if (found_final) {

                        Sequence s=new Sequence();
                        s.setName(m4.group(1));
                        s.setSequence(m4.group(2).replaceAll(" ",""));
                        s.setSequence(trans.translate(s, trans.code.get(0)));
                        this.aa_final.add(s);
                    }
                }
                if (fm7&&found_statistic) {
                    statisticPAML s=new statisticPAML();                    
                    s.AA=m7.group(2);                    
                    try {
                        int pos=Integer.valueOf(m7.group(1));
                        s.pos=pos;
                        s.prob=Float.valueOf(m7.group(3));
                        s.statistic=m7.group(4);
                        if (found_NEB) s.analysis="NEB";
                        if (found_BEB) s.analysis="BEB";                        
                        this.annotation.add(s);
                    }catch(Exception e) {}
                }
                }
            }
            //--Regex not perfecr, remove first sequence
            //System.out.println(infile);
            if (aa_original.getSequences().size()>0) {
                aa_original.getSequences().remove(0);
            } else System.out.println(aa_original);
            aa_original.setName(projectName);
            nn_original.getSequences().remove(0);
            aa_final.getSequences().remove(0);
            for (statisticPAML p:this.annotation) {
                //System.out.print(p+" ");
                p.pos=map(aa_original.getSequences().get(0),aa_final.getSequences().get(0),p.pos);
                //System.out.println(p);
            }
//            System.out.println(aa_original.getSequences().get(0));
//            System.out.println("");
//            System.out.println(aa_final.getSequences().get(0));
            report r=new report();
//            System.out.println(aa_original);
//            System.out.println(nn_original);
            u.open(outfile);

            u.println(  "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n"+
                    "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n"+
                    "<head>\n"+
                    "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n"+
                    "<title>Armadillo Report : "+projectName+"</title>\n"+
                    "<link rel=\"shortcut icon\" href=\"favicon.ico\"/>"+
                    "</head>\n"+u.getRessource("style.css")+"<div id=\"bordure\" style=\"margin-left: 1cm; margin-top: 1cm; margin-bottom: 1cm;\">"+
                    "<span class=\"paml\">");
            u.println( AlignmenttoHTML(aa_original));
            u.println( r.foot());
            u.close();
        } catch(Exception e) {
            //e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Function to map position of PAML result to original sequence
     */
    public int map(Sequence original, Sequence paml, int pos) {
        int pos_original=0;
        char[] coriginal=original.getSequence().toCharArray();
        char[] cpaml=paml.getSequence().toCharArray();
        for (int i=0; i<pos;i++) {
            if (cpaml[i]==coriginal[pos_original]) {
                pos_original++;
            } else
                while(cpaml[i]!=coriginal[pos_original]) {
                   pos_original++;
                }
        }
        return pos_original+1;
    }

    public float prob(int pos, String mode) {
        for (statisticPAML s:this.annotation) {
            if (s.pos==pos&&s.analysis.equals(mode)) return s.prob;
        }
        return 0;
    }

    public String AlignmenttoHTML(Alignment AA) {
        StringBuilder st=new StringBuilder();
        report r = new report();
        st.append("<b>Results from PAML from : "+AA.getName()+"</b><br>");
        st.append("<span class=\"pamlTitle\"><br><b>&nbsp;Results Naive Empirical Bayes (NEB) analysis</b><br></span>");

        st.append("<table border=\"0\" bgcolor=\"#FFFFFF\"><thead><tr><td><b>Name</b></td><td><b>Sequence</b></td></tr></thead><tbody>");
        st.append("<tr><td>"+"          "+"</td><td>");
        for(int i=0; i<AA.getSequenceSize();i++) {
            if (i%10==0) {
                String str=String.format("%-10d",i+1);
                st.append("<span class=\"paml\">"+str.replaceAll(" ", "&nbsp;")+"</span>");
            }
        }
         st.append("</td></tr>");
        for (int j=0;j<AA.getNbSequence();j++) {
           //Sequence S=a.getSequences().get(j);
           Sequence A=AA.getSequences().get(j);
           //String tmp=S.getSequence();
           String tmpAA=A.getSequence();
           st.append("<tr><td>"+A.getName().substring(0,Math.min(10, A.getName().length()))+"</td><td>");
           for (int i=0; i<tmpAA.length();i++) {
              float prob=prob(i+1,"NEB");
               if (prob!=0) {
                if (prob>=0.9900) {st.append("<span class=\"pamlHit9900\">"+tmpAA.charAt(i)+"</span>");} else
                if (prob>=0.9500) {st.append("<span class=\"pamlHit9500\">"+tmpAA.charAt(i)+"</span>");} else
                if (prob>=0.9000) {st.append("<span class=\"pamlHit9000\">"+tmpAA.charAt(i)+"</span>");} else
                if (prob>=0.8500) {st.append("<span class=\"pamlHit8500\">"+tmpAA.charAt(i)+"</span>");} else
                                  {st.append("<span class=\"pamlHit8000\">"+tmpAA.charAt(i)+"</span>");}
              } else {
                  st.append("<span class=\"paml\">"+tmpAA.charAt(i)+"</span>");
              }
           }
                
          
           st.append("</td></tr>");
        } //--End for
         st.append("<tr><td><span class=\"paml\">"+""+"</td><td>");
        for(int i=0; i<AA.getSequenceSize();i++) {
             float prob=prob(i+1,"NEB");
             if (prob>0) {
               //--Show last digit of position 
               String Str=String.format("%d",i+1);
               Str=Str.substring(Str.length()-1);
               //String Str=String.format("*");
               //Str=Str.substring(Str.length()-1);
               st.append("<span class=\"paml\">"+Str+"</span>");
             } else {
                 st.append("<span class=\"paml\">"+"&nbsp;"+"</span>");
             }

        }
      st.append("<tr><td><span class=\"paml\">"+"Prob "+"</td><td>");
                st.append("<span class=\"pamlHit9900\">"+">0.99&nbsp;"+"</span>");
                st.append("<span class=\"pamlHit9500\">"+">0.95&nbsp;"+"</span>");
                st.append("<span class=\"pamlHit9000\">"+">0.90&nbsp;"+"</span>");
                st.append("<span class=\"pamlHit8500\">"+">=0.85&nbsp;"+"</span>");
                st.append("<span class=\"pamlHit8000\">"+"<0.85"+"</span>");
        st.append("</td></tr>");

         st.append("<tr><td>&nbsp;</td><td></td></tr>");
        st.append("<tr><td><span class=\"pamlTitle\">Results </span></td><td></td></tr>");
        for (statisticPAML p:this.annotation) {
            if (p.analysis.equals("NEB")) {
                st.append("<tr><td><span class=\"paml\">"+p.pos+"&nbsp;"+p.AA+"</span></td><td><span class=\"paml\">"+p.prob+"&nbsp;&nbsp;&nbsp;"+p.statistic+"</span></td></tr>");
            }
        }
        st.append("</tbody></table>");

         //st.append("<br><b>Bayes Empirical Bayes (BEB) analysis (Yang, Wong & Nielsen 2005. Mol. Biol. Evol. 22:1107-1118)</b><br>");
         st.append("<span class=\"pamlTitle\"><br><b>&nbsp;Bayes Empirical Bayes (BEB) analysis (Yang, Wong & Nielsen 2005. Mol. Biol. Evol. 22:1107-1118)</b><br></span>");
        st.append("<table border=\"0\" bgcolor=\"#FFFFFF\"><thead><tr><td><b>Name</b></td><td><b>Sequence</b></td></tr></thead><tbody>");
        st.append("<tr><td>"+"          "+"</td><td>");
        for(int i=0; i<AA.getSequenceSize();i++) {
            if (i%10==0) {
                String str=String.format("%-10d",i+1);
                st.append("<span class=\"paml\">"+str.replaceAll(" ", "&nbsp;")+"</span>");
            }
        }
         st.append("</td></tr>");
        for (int j=0;j<AA.getNbSequence();j++) {
           //Sequence S=a.getSequences().get(j);
           Sequence A=AA.getSequences().get(j);
           //String tmp=S.getSequence();
           String tmpAA=A.getSequence();
           st.append("<tr><td>"+A.getName().substring(0,Math.min(10, A.getName().length()))+"</td><td>");
           for (int i=0; i<tmpAA.length();i++) {
              float prob=prob(i+1,"BEB");
               if (prob!=0) {
                if (prob>=0.9900) {st.append("<span class=\"pamlHit9900\">"+tmpAA.charAt(i)+"</span>");} else
                if (prob>=0.9500) {st.append("<span class=\"pamlHit9500\">"+tmpAA.charAt(i)+"</span>");} else
                if (prob>=0.9000) {st.append("<span class=\"pamlHit9000\">"+tmpAA.charAt(i)+"</span>");} else
                if (prob>=0.8500) {st.append("<span class=\"pamlHit8500\">"+tmpAA.charAt(i)+"</span>");} else
                                  {st.append("<span class=\"pamlHit8000\">"+tmpAA.charAt(i)+"</span>");}
              } else {
                  st.append("<span class=\"paml\">"+tmpAA.charAt(i)+"</span>");
              }
           }


           st.append("</td></tr>");
        } //--End for
         st.append("<tr><td><span class=\"paml\">"+""+"</td><td>");
        for(int i=0; i<AA.getSequenceSize();i++) {
             float prob=prob(i+1,"BEB");
             if (prob>0) {
               //--Show last digit of position
               String Str=String.format("%d",i+1);
               Str=Str.substring(Str.length()-1);
               //--Or * 
               // String Str=String.format("*");

               st.append("<span class=\"paml\">"+Str+"</span>");
             } else {
                 st.append("<span class=\"paml\">"+"&nbsp;"+"</span>");
             }

        }
      st.append("<tr><td><span class=\"paml\">"+"Prob"+"</td><td>");
                st.append("<span class=\"pamlHit9900\">"+">0.99&nbsp;"+"</span>");
                st.append("<span class=\"pamlHit9500\">"+">0.95&nbsp;"+"</span>");
                st.append("<span class=\"pamlHit9000\">"+">0.90&nbsp;"+"</span>");
                st.append("<span class=\"pamlHit8500\">"+">=0.85&nbsp;"+"</span>");
                st.append("<span class=\"pamlHit8000\">"+"<0.85"+"</span>");
        st.append("</td></tr>");
        st.append("<tr><td>&nbsp;</td><td></td></tr>");
        st.append("<tr><td><span class=\"pamlTitle\">Results </span></td><td></td></tr>");
        for (statisticPAML p:this.annotation) {
            if (p.analysis.equals("BEB")) {
                st.append("<tr><td><span class=\"paml\">"+p.pos+"&nbsp;"+p.AA+"</span></td><td><span class=\"paml\">"+p.prob+"&nbsp;&nbsp;&nbsp;"+p.statistic+"</span></td></tr>");
            }
        }
        st.append("</tbody></table>");


        st.append("Total "+AA.getNbSequence()+" sequence(s) with len "+AA.getSequenceSize()+"\n");
        return st.toString();
    }

}
