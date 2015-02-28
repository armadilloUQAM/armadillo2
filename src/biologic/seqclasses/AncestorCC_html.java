package biologic.seqclasses;

import biologic.Alignment;
import biologic.Sequence;
import configuration.Util;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class AncestorCC_html {

    /**
     * Old version
     * @param Original
     * @param AncestraleSequence
     * @param AncestraleConfidence
     * @return
     */
    public static String AlignmenttoHTML(Alignment Original, Alignment AncestraleSequence, Alignment AncestraleConfidence) {
        StringBuilder st=new StringBuilder();
        //==HEAD
        st.append(
                "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n"+
                "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n"+
                "<head>\n"+
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n"+
                "<title></title>\n"+
                "<meta name=\"keywords\" content=\"\" />\n"+
                "<meta name=\"description\" content=\"\" />\n"+
                //"<link href=\"../../css/styles.css\" rel=\"stylesheet\" type=\"text/css\" />\n"+
                ""+
                "</head>\n"+
                "\n");
        //==SEQUENCES ORIGINALES
        st.append("<body>");
        //==css
        Util util=new Util();
        st.append(util.getRessource("style.css"));
        st.append("<div id=\"bordure\" style=\"margin-left: 1cm; margin-top: 1cm; margin-bottom: 1cm;\">");
//    st.append("<table border=\"0\" bgcolor=\"#FFFFFF\"><thead><tr><td><b>Name</b></td><td><b>Sequences</b></td></tr></thead><tbody>");
//        st.append("<tr><td>"+"                                             "+"</td><td>");
//        for(int i=0; i<Original.getSequenceSize();i++) {
//            if (i%10==0) {
//                String str=String.format("%-10d",i+1);
//                st.append("<span class=\"paml\">"+str.replaceAll(" ", "&nbsp;")+"</span>");
//            }
//        }
//         st.append("</td></tr>");
//        for (int j=0;j<Original.getNbSequence();j++) {
//           Sequence A=Original.getSequences().get(j);
//           String tmpAA=A.getSequence();
//           st.append("<tr><td>"+A.getName().substring(0,Math.min(40, A.getName().length()))+"</td><td>");
//           st.append("<span class=\"paml\">");
//           for (int i=0; i<tmpAA.length();i++) {
//               st.append(tmpAA.charAt(i));
//              }
//           st.append("</span>");
//           st.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td></tr>");
//           } //--End for
//        st.append("</tbody></table>");
        st.append("<div style=\"border-top: 0px solid rgb(255, 255, 255); border-left: 0em solid rgb(170, 170, 170); border-bottom: 1px solid rgb(170, 170, 170); padding: 0px 0px 0px 0em; white-space: nowrap; font-weight: normal; font-size: 1.2em; margin-top: 2em; margin-bottom: 1em; color: rgb(51, 51, 51);\">Input sequences</div>");
        st.append("Total "+Original.getNbSequence()+" input sequences with total length of "+Original.getSequenceSize()+" bp<br><br>\n");
        st.append("<table border=\"0\" bgcolor=\"#FFFFFF\"><thead><tr><td><b>Name</b></td><td><b>Sequences</b></td></tr></thead><tbody>");
        st.append("<tr><td>"+"                                             "+"</td><td>");
        for(int i=0; i<Original.getSequenceSize();i++) {
            if (i%10==0) {
                String str=String.format("%-10d",i+1);
                st.append("<span class=\"paml\">"+str.replaceAll(" ", "&nbsp;")+"</span>");
            }
        }
         st.append("</td></tr>");
        for (int j=0;j<Original.getNbSequence();j++) {          
           Sequence A=Original.getSequences().get(j);          
           String tmpAA=A.getSequence();           
           st.append("<tr><td>"+A.getName().substring(0,Math.min(40, A.getName().length()))+"</td><td>");
           st.append("<span class=\"paml\">");
           for (int i=0; i<tmpAA.length();i++) {
               st.append(tmpAA.charAt(i));
              }
           st.append("</span>");
           st.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td></tr>");
           } //--End for
        st.append("</tbody></table>");

        //==SEQUENCE ANCESTRALE
        st.append("<div style=\"border-top: 0px solid rgb(255, 255, 255); border-left: 0em solid rgb(170, 170, 170); border-bottom: 1px solid rgb(170, 170, 170); padding: 0px 0px 0px 0em; white-space: nowrap; font-weight: normal; font-size: 1.2em; margin-top: 2em; margin-bottom: 1em; color: rgb(51, 51, 51);\">Ancestor v1.0 - Results</div>");
        st.append("Total "+AncestraleSequence.getNbSequence()+" ancestrale sequences with total length of "+AncestraleSequence.getSequenceSize()+" bp <br><br>\n");
        st.append("<table border=\"0\" bgcolor=\"#FFFFFF\"><thead><tr><td><b>Ancestors</b></td><td><b>Sequences</b></td></tr></thead><tbody>");
        st.append("<tr><td>"+"                                             "+"</td><td>");
        for(int i=0; i<AncestraleSequence.getSequenceSize();i++) {
            if (i%10==0) {
                String str=String.format("%-10d",i+1);
                st.append("<span class=\"paml\">"+str.replaceAll(" ", "&nbsp;")+"</span>");
            }
        }
         st.append("</td></tr>");
        for (int j=0;j<AncestraleSequence.getNbSequence();j++) {          
           Sequence A=AncestraleSequence.getSequences().get(j);
           Sequence C=AncestraleConfidence.getSequences().get(j);
           String tmpAA=A.getSequence();
           String tmpConfidence=C.getSequence();
           st.append("<tr><td>"+A.getName().substring(0,Math.min(40, A.getName().length()))+"</td><td>");
           for (int i=0; i<tmpAA.length();i++) {
               char c=tmpConfidence.charAt(i);
               float prob= ((float)c-33)/93;
               if (prob!=0) {
                if (prob>=0.9000) {st.append("<span class=\"pamlHit9900\">"+tmpAA.charAt(i)+"</span>");} else
                if (prob>=0.8000) {st.append("<span class=\"pamlHit9500\">"+tmpAA.charAt(i)+"</span>");} else
                if (prob>=0.7000) {st.append("<span class=\"pamlHit9000\">"+tmpAA.charAt(i)+"</span>");} else
                if (prob>=0.6000) {st.append("<span class=\"pamlHit8500\">"+tmpAA.charAt(i)+"</span>");} else
                if (prob>=0.5000) {st.append("<span class=\"pamlHit8000\">"+tmpAA.charAt(i)+"</span>");} else
                if (prob>=0.4000) {st.append("<span class=\"pamlHit7500\">"+tmpAA.charAt(i)+"</span>");} else
                if (prob>=0.3000) {st.append("<span class=\"pamlHit7000\">"+tmpAA.charAt(i)+"</span>");} else
                if (prob>=0.2000) {st.append("<span class=\"pamlHit6500\">"+tmpAA.charAt(i)+"</span>");} else
                if (prob>=0.1000) {st.append("<span class=\"pamlHit6000\">"+tmpAA.charAt(i)+"</span>");} else
                                  {st.append("<span class=\"pamlHit5500\">"+tmpAA.charAt(i)+"</span>");}
              } else {
                  st.append("<span class=\"paml\">"+tmpAA.charAt(i)+"</span>");
              }
           }
           st.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td></tr>");
        } //--End for
        st.append("<tr><td><span class=\"paml\">"+""+"</td><tr>");
        st.append("<tr><td><span class=\"paml\">"+"Confidence Levels "+"</td><td>");
        st.append("<span class=\"pamlHit9900\">"+">0.90&nbsp;"+"</span>");
        st.append("<span class=\"pamlHit9500\">"+">0.80&nbsp;"+"</span>");
        st.append("<span class=\"pamlHit9000\">"+">0.70&nbsp;"+"</span>");
        st.append("<span class=\"pamlHit8500\">"+">0.60&nbsp;"+"</span>");
        st.append("<span class=\"pamlHit8000\">"+">0.50&nbsp;"+"</span>");
        st.append("<span class=\"pamlHit7500\">"+">0.40&nbsp;"+"</span>");
        st.append("<span class=\"pamlHit7000\">"+">0.30&nbsp;"+"</span>");
        st.append("<span class=\"pamlHit6500\">"+">0.20&nbsp;"+"</span>");
        st.append("<span class=\"pamlHit6000\">"+">0.10&nbsp;"+"</span>");
        st.append("<span class=\"pamlHit5500\">"+"&nbsp;"+"</span>");
        st.append("</td></tr>");
        st.append("<tr><td>&nbsp;</td><td></td></tr>");
        st.append("<tr><td></td></tr>");      
        st.append("</tbody></table>");
        st.append("<div style=\"border-top: 0px solid rgb(255, 255, 255); border-left: 0em border-right: 0em solid rgb(170, 170, 170); border-bottom: 1px solid rgb(170, 170, 170); padding: 0px 0px 0px 0em; white-space: nowrap; font-weight: normal; font-size: 1.2em; margin-top: 2em; margin-bottom: 1em; color: rgb(51, 51, 51);\">");        
        st.append("</div>" +
                "Ancestors 1.0: a web server for ancestral sequence reconstruction.<br>Diallo AB, Makarenkov V, Blanchette M. Bioinformatics. 2010 Jan 1;26(1):130-1. Epub 2009 Oct 22. PMID: 19850756" +
                "<br><br>View created on : "+Util.returnCurrentDateAndTime()+"</div>");
        st.append( "\n" +
                "</body>\n"+
                "</html>");
        return st.toString();
    }

    /**
     * New version 
     * @param Original
     * @param AncestraleSequence
     * @param AncestraleConfidence
     * @param SVG_filename
     * @return
     */
    public static String AlignmenttoHTML(Alignment Original, Alignment AncestraleSequence, Alignment AncestraleConfidence, String SVG_filename) {
        StringBuilder st=new StringBuilder();
        //==HEAD
        st.append(
                "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n"+
                "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n"+
                "<head>\n"+
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n"+
                "<title></title>\n"+
                "<meta name=\"keywords\" content=\"\" />\n"+
                "<meta name=\"description\" content=\"\" />\n"+
                //"<link href=\"../../css/styles.css\" rel=\"stylesheet\" type=\"text/css\" />\n"+
                ""+
                "</head>\n"+
                "\n");
        //==SEQUENCES ORIGINALES
        st.append("<body>");
        //==css
        Util util=new Util();
        st.append(util.getRessource("style.css"));
        st.append("<div id=\"bordure\" style=\"margin-left: 1cm; margin-top: 1cm; margin-bottom: 1cm;\">");
//        st.append("<div style=\"border-top: 0px solid rgb(255, 255, 255); border-left: 0em solid rgb(170, 170, 170); border-bottom: 1px solid rgb(170, 170, 170); padding: 0px 0px 0px 0em; white-space: nowrap; font-weight: normal; font-size: 1.2em; margin-top: 2em; margin-bottom: 1em; color: rgb(51, 51, 51);\">Input sequences</div>");
//        st.append("Total "+Original.getNbSequence()+" input sequences with total length of "+Original.getSequenceSize()+" bp<br><br>\n");
//        st.append("<table border=\"0\" bgcolor=\"#FFFFFF\"><thead><tr><td><b>Name</b></td><td><b>Sequences</b></td></tr></thead><tbody>");
//        st.append("<tr><td>"+"                                             "+"</td><td>");
//        for(int i=0; i<Original.getSequenceSize();i++) {
//            if (i%10==0) {
//                String str=String.format("%-10d",i+1);
//                st.append("<span class=\"paml\">"+str.replaceAll(" ", "&nbsp;")+"</span>");
//            }
//        }
//         st.append("</td></tr>");
//        for (int j=0;j<Original.getNbSequence();j++) {
//           Sequence A=Original.getSequences().get(j);
//           String tmpAA=A.getSequence();
//           st.append("<tr><td>"+A.getName().substring(0,Math.min(40, A.getName().length()))+"</td><td>");
//           st.append("<span class=\"paml\">");
//           for (int i=0; i<tmpAA.length();i++) {
//               st.append(tmpAA.charAt(i));
//              }
//           st.append("</span>");
//           st.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td></tr>");
//           } //--End for
//        st.append("</tbody></table>");
        st.append("<div style=\"border-top: 0px solid rgb(255, 255, 255); border-left: 0em solid rgb(170, 170, 170); border-bottom: 1px solid rgb(170, 170, 170); padding: 0px 0px 0px 0em; white-space: nowrap; font-weight: normal; font-size: 1.2em; margin-top: 2em; margin-bottom: 1em; color: rgb(51, 51, 51);\">Input tree</div>");
        st.append("<embed src=\""+"input_tree.newick.svg"+"\" width=\"600\" height=\"600\" type=\"image/svg+xml\" pluginspage=\"http://www.adobe.com/svg/viewer/install/\" />");
        st.append("<br>Created with ScripTree v1.7 <a href=\"http://phylo.lirmm.fr/scriptree/\">Website</a>");
        //==SEQUENCE ANCESTRALE
        st.append("<div style=\"border-top: 0px solid rgb(255, 255, 255); border-left: 0em solid rgb(170, 170, 170); border-bottom: 1px solid rgb(170, 170, 170); padding: 0px 0px 0px 0em; white-space: nowrap; font-weight: normal; font-size: 1.2em; margin-top: 2em; margin-bottom: 1em; color: rgb(51, 51, 51);\">Ancestor v1.0 - Results</div>");
        st.append("Total "+AncestraleSequence.getNbSequence()+" ancestrale sequences with total length of "+AncestraleSequence.getSequenceSize()+" bp <br><br>\n");
        st.append("<table border=\"0\" bgcolor=\"#FFFFFF\"><thead><tr><td><b>Ancestors</b></td><td><b>Sequences</b></td></tr></thead><tbody>");
        st.append("<tr><td>"+"                                             "+"</td><td>");
        for(int i=0; i<AncestraleSequence.getSequenceSize();i++) {
            if (i%10==0) {
                String str=String.format("%-10d",i+1);
                st.append("<span class=\"paml\">"+str.replaceAll(" ", "&nbsp;")+"</span>");
            }
        }
          st.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td></tr>");
        for (int j=0;j<AncestraleSequence.getNbSequence();j++) {
           Sequence A=AncestraleSequence.getSequences().get(j);
           Sequence C=AncestraleConfidence.getSequences().get(j);
           String tmpAA=A.getSequence();
           String tmpConfidence=C.getSequence();
           st.append("<tr><td>"+A.getName().substring(0,Math.min(40, A.getName().length()))+"</td><td>");
           int last_prob_level=0;
           int prob_level=0;
           st.append("<span class=\"paml\">");
           for (int i=0; i<tmpAA.length();i++) {
               char c=tmpConfidence.charAt(i);
               //--Transform ASCII char to confidence level.
               float prob= ((float)c-33.0f)/93.0f;
                //calculate level (could have been a mod)
                if (prob==0)      {prob_level=0;} else
                if (prob>=0.9000) {prob_level=90;} else
                if (prob>=0.8000) {prob_level=80;} else
                if (prob>=0.7000) {prob_level=70;} else
                if (prob>=0.6000) {prob_level=60;} else
                if (prob>=0.5000) {prob_level=50;} else
                if (prob>=0.4000) {prob_level=40;} else
                if (prob>=0.3000) {prob_level=30;} else
                if (prob>=0.2000) {prob_level=20;} else
                if (prob>=0.1000) {prob_level=10;} else
                                  {prob_level=5;}

               if (prob_level!=last_prob_level) {
                   st.append("</span>");
                   last_prob_level=prob_level;
                    switch (last_prob_level) {
                        case 90: st.append("<span class=\"pamlHit9900\">"); break;
                        case 80: st.append("<span class=\"pamlHit9500\">"); break;
                        case 70: st.append("<span class=\"pamlHit9000\">"); break;
                        case 60: st.append("<span class=\"pamlHit8500\">"); break;
                        case 50: st.append("<span class=\"pamlHit8000\">"); break;
                        case 40: st.append("<span class=\"pamlHit7500\">"); break;
                        case 30: st.append("<span class=\"pamlHit7000\">"); break;
                        case 20: st.append("<span class=\"pamlHit6500\">"); break;
                        case 10: st.append("<span class=\"pamlHit6000\">"); break;
                        case  0: st.append("<span class=\"paml\">"); break;
                        default: st.append("<span class=\"pamlHit5500\">"); break;   // >0
                    }
               }
               st.append(tmpAA.charAt(i));
           }
           st.append("</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td></tr>");
        } //--End for
        st.append("<tr><td><span class=\"paml\">"+""+"</td><tr>");
        st.append("<tr><td><span class=\"paml\">"+"Confidence Levels "+"</td><td>");
        st.append("<span class=\"pamlHit9900\">"+">0.90&nbsp;"+"</span>");
        st.append("<span class=\"pamlHit9500\">"+">0.80&nbsp;"+"</span>");
        st.append("<span class=\"pamlHit9000\">"+">0.70&nbsp;"+"</span>");
        st.append("<span class=\"pamlHit8500\">"+">0.60&nbsp;"+"</span>");
        st.append("<span class=\"pamlHit8000\">"+">0.50&nbsp;"+"</span>");
        st.append("<span class=\"pamlHit7500\">"+">0.40&nbsp;"+"</span>");
        st.append("<span class=\"pamlHit7000\">"+">0.30&nbsp;"+"</span>");
        st.append("<span class=\"pamlHit6500\">"+">0.20&nbsp;"+"</span>");
        st.append("<span class=\"pamlHit6000\">"+">0.10&nbsp;"+"</span>");
        st.append("<span class=\"pamlHit5500\">"+"&nbsp;"+"</span>");
        st.append("</td></tr>");
        st.append("<tr><td>&nbsp;</td><td></td></tr>");
        st.append("<tr><td></td></tr>");
        st.append("</tbody></table>");
        st.append("<div style=\"border-top: 0px solid rgb(255, 255, 255); border-left: 0em border-right: 0em solid rgb(170, 170, 170); border-bottom: 1px solid rgb(170, 170, 170); padding: 0px 0px 0px 0em; white-space: nowrap; font-weight: normal; font-size: 1.2em; margin-top: 2em; margin-bottom: 1em; color: rgb(51, 51, 51);\">");
        st.append("</div>" +
                "Note: For nucleotides numbering correspond to the generated ancestral sequences.<br> "+
                "Diallo AB, Makarenkov V, Blanchette M. (2010) Ancestors 1.0: a web server for ancestral sequence reconstruction.<br> Bioinformatics. 26(1):130-1." +
                "<br><br>View created on : "+Util.returnCurrentDateAndTime()+"</div>");
        st.append( "\n" +
                "</body>\n"+
                "</html>");
        return st.toString();
    }

    public static void main(String[] argv) {
        if (argv.length<4) {
            System.out.println("java -jar ancestorCC_html.jar original_seq ancestrale_seq ancestral_conf outfile_html [rendered_tree.svg] [info]");
            System.exit(-1);
        }
        //==without a tree rendering by scripttree
        if (argv.length==4) {
                       
            Alignment Original=new Alignment(argv[0]);
            Alignment Ancestrale=new Alignment(argv[1]);
            Alignment Ancestrale_Confidence=new Alignment();
            Ancestrale_Confidence.readSequenceFromFasta(argv[2]);
           try {
               PrintWriter pw=new PrintWriter(new FileWriter(new File(argv[3])));
               pw.println(AlignmenttoHTML(Original,Ancestrale, Ancestrale_Confidence));
               pw.flush();
               pw.close();
           } catch(Exception e) {System.exit(-1);}
        }
        //==with tree rendering (must be a svg)
      if (argv.length==5) {

            Alignment Original=new Alignment(argv[0]);
            Alignment Ancestrale=new Alignment(argv[1]);
            Alignment Ancestrale_Confidence=new Alignment();
            Ancestrale_Confidence.readSequenceFromFasta(argv[2]);
           try {
               PrintWriter pw=new PrintWriter(new FileWriter(new File(argv[3])));
               pw.println(AlignmenttoHTML(Original,Ancestrale, Ancestrale_Confidence, argv[4]));
               pw.flush();
               pw.close();
           } catch(Exception e) {System.exit(-1);}
        }
        //==with tree rendering and search parameters
      if (argv.length==6) {

            Alignment Original=new Alignment(argv[0]);
            Alignment Ancestrale=new Alignment(argv[1]);
            Alignment Ancestrale_Confidence=new Alignment();
            Ancestrale_Confidence.readSequenceFromFasta(argv[2]);
           try {
               PrintWriter pw=new PrintWriter(new FileWriter(new File(argv[3])));
               pw.println(AlignmenttoHTML(Original,Ancestrale, Ancestrale_Confidence, argv[4]));
               pw.flush();
               pw.close();
           } catch(Exception e) {System.exit(-1);}
        }

       



    }


}
