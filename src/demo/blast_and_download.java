/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package demo;

import biologic.seqclasses.qblast;

/**
 * Arabidopsis thaliana (taxid:3702)
 * Brachypodium (taxid:15367)
 * @author Etienne Lord
 */
public class blast_and_download {

            boolean done = false;
            Thread thread;
            
            public boolean isDone() {
                return done;
            }       
            
            public blast_and_download(final String fasta, final String output) {
                 thread = new Thread()  {

                    @Override
                    public void run() {
                        try {
                            qblast q = new qblast();
                            q.setQUERY_FASTA(fasta);
                            q.setEXPECT(1000);
                            q.setFORMAT_TYPE("XML");
                            q.setDESCRIPTIONS(10);
                            q.setWORD_SIZE(16);
                            q.setGOP("5");
                            q.setGEP("2"); 
                            q.setMATCH("1");
                            q.setMISMATCH("-3");                       
                            q.setHITLIST_SIZE(10);
                            q.setDATABASE("nr");
                            q.setPERC_IDENT(80);
                            q.setMEGABLAST(true);
                            q.setFILTER("F");    
                            //--http://www.ncbi.nlm.nih.gov/staff/tao/URLAPI/new/node79.html#sub:Entrez-Query-Terms
                            //--http://www.ncbi.nlm.nih.gov/books/NBK3837/
                            q.setENTREZ_QUERY("Brachypodium[Organism]"); 
                            
                            
                            q.QBlast_PUT();
                            
                            
                            try {
                            String status="";
                            while(!status.equals(q.READY)||status.equals(q.UNKNOWN)) {
                                if (status.equals(q.UNKNOWN)) {
                                    System.out.println("Error with this QBLAST. Please retry.");
                                    return;
                                }
                                status=q.QBlast_GET();
                                System.out.println("QBLAST RID :"+q.returnRID()+" Status: "+status);
                                if (!status.equals(q.READY)) Thread.sleep(1000);
                            }
                            q.saveToFile(output);
                                }catch(Exception e) {e.printStackTrace();done=true;}

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        done = true;                        
                    }
                }; //--End thread               
                 thread.run();
            }
        }