package programs;


import configuration.*;
import biologic.*;
import configuration.Util;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.HashMap;
import workflows.workflow_properties;
import org.apache.commons.lang.SystemUtils;
/**
 * //--This is the command-line program to launch a Q_func...
 * @author Etienne Lord 2011
 */
public class LaunchQfunc {
    
    private String filename="";
    
 public  void execute(String filename) throws IOException {
      this.filename=filename;  
     //--Enabled logging or error
      Util.deleteFile("armadillo.log"); //--Hack
      Config config=new Config();
       Config.log_file=config.dataPath()+File.separator+"armadillo.log";
       Config.library_mode=true;  
       System.out.println("Version alpha 1.0 - Doris-Dunarel q_func_cpp - 11 Fevrier 2011");
       System.out.println("java -jar Armadillo_simple.jar [filename|random]");
       //--Setting path     
       System.out.println("Linux: "+(SystemUtils.IS_OS_UNIX||SystemUtils.IS_OS_LINUX));
       System.out.println("Windows: " + (SystemUtils.IS_OS_WINDOWS));
       System.out.println("MAC OS X: " + (SystemUtils.IS_OS_MAC_OSX)); 
       Config.log("dataPath():"+config.dataPath());
    
      //--Each of the step required in the Doris workflow 
      // See Doris pdf in sketch folder  
      Config.log("Starting...");     
      Alignment read_align=null;
      Alignment align=null;
          read_align=new Alignment(filename);
          //--Remove HXB2
          align=Filter_Sequences(read_align);
          
      if (align==null||align.getNbSequence()==0) {
          System.out.println("Unable to read filename: "+filename);
          System.exit(-1);
      }

      //--Note: group 0: I, group 1: J, group 2: K
      Vector<Alignment> groups=Create_Group_Sequences(align);
        //--Create intergroups
      groups.get(0).outputFasta(filename+"_I.fasta");
      groups.get(1).outputFasta(filename+"_J.fasta");
      groups.get(2).outputFasta(filename+"_K.fasta");
      
      //--Create 3 filenames with the different sequences groups
      //--IJ
      Alignment IJ=this.concatenate(groups.get(0), groups.get(1));
      IJ.setName("IJ");
      //IJ.outputFasta(config.dataPath()+File.separator+filename+"IJ.fasta");
      //--IK
      Alignment IK=this.concatenate(groups.get(0), groups.get(2));
      IK.setName("IK");
      //IK.outputFasta(config.dataPath()+File.separator+filename+"IK.fasta");
      //--JK
      Alignment JK=this.concatenate(groups.get(1), groups.get(2));
      JK.setName("JK");
      //JK.outputFasta(config.dataPath()+File.separator+filename+"JK.fasta");
      //--Now, run the function with different combination of factor
      //--Il y a un problème avec auto, peut-être au niveau de f_opt_max = 4?
      for (String calc_type:new String[]{"simple"}) { //simple, auto
          for (String optim:new String[]{"km","nj"}) {
            //workflow_properties work=q_func_cpp.create_properties(calc_type, 0, "ham", 20, 3, optim);
              System.out.println(calc_type+" "+optim);
//            q_func_cpp ij1=new q_func_cpp(work, IJ, groups.get(0), filename);
//         
//            q_func_cpp ij2=new q_func_cpp(work, IJ, groups.get(1), filename);
//         
//            //-- PAS K q_func_cpp ij3=new q_func_cpp(work, IJ, groups.get(2), filename);
//            q_func_cpp ik1=new q_func_cpp(work, IK, groups.get(0), filename);
//         
//            //-- PAS J q_func_cpp ik2=new q_func_cpp(work, IK, groups.get(1), filename);
//            q_func_cpp ik3=new q_func_cpp(work, IK, groups.get(2), filename);
//         
//            //--PAS I q_func_cpp jk1=new q_func_cpp(work, JK, groups.get(0), filename);
//            q_func_cpp jk2=new q_func_cpp(work, JK, groups.get(1), filename);
//         
//            q_func_cpp jk3=new q_func_cpp(work, JK, groups.get(2), filename);
         
       }
     }
    System.exit(0);
    }
            


 /**
  * This will split the alignment into 3 group according the the sequence name
  * --This is like a filter that need to be calibrated...maybe a regex?
  * @param align
  * @return
  */
public Vector<Alignment> Create_Group_Sequences(Alignment align) {
    Alignment I=new Alignment();
    Alignment J=new Alignment();
    Alignment K=new Alignment();
    I.setName("I");
    J.setName("J");
    K.setName("K");
    Vector<Alignment> tmp=new Vector<Alignment>();
    if (align.getNbSequence()!=0) {
        for (Sequence s:align.getSequences()) {
            if (s.getName().contains("I")) {
                I.add(s);
            } else if (s.getName().contains("J")) {
                J.add(s);
            } else {
                K.add(s);
            }
        }
        tmp.add(I);
        tmp.add(J);
        tmp.add(K);
    }
    return tmp;
}

////////////////////////////////////////////////////////////////////
///
/// Filter_Sequences
/// Input: Sequences 
/// Output: Sequences without duplicate and without HXB2
///
public Alignment Filter_Sequences(Alignment multi) {
  Config.log("Filter_Sequences(multi)");
  Alignment output=new Alignment();
  Config.log("[Processing "+multi.getName()+"]");
  output.setName(multi.getName());
  output.setNote(multi.getNote());
  //--HashMap (Sequence, Name), 
  HashMap<String,String> matching_sequences=new HashMap<String,String>();
  
  //--Remove sequence matching "HXB2"
  //--Remove duplicate sequence by DNA
  
  for (int i=multi.getSequences().size()-1;i>-1;i--) {
       biologic.Sequence s=multi.getSequences().get(i);
       // Remove sequence with HXB2 in name
       if (s.getName().contains("HXB2")) {
  	 multi.getSequences().remove(i);
  	 Config.log("*"+s.getName()+" as been removed\n");
       } else {
  	  //--Add it to output if no matching sequence
  	  if (matching_sequences.put(s.getSequence(), s.getName())==null) {
               output.add(s);
  	  } else {
            //--Add to a list of duplicate
               Config.log(s.getName()+ "as been removed - duplicate\n");
  	  }
      }
  }
  return output;
}


/**
 * Concatenate two group of MultipleSequences into a new one
 * @param source
 * @param source2
 * @return a new Alignment
 */
   public Alignment concatenate(biologic.MultipleSequences source, biologic.MultipleSequences source2) {
       Alignment align=new Alignment();
       if (source==null||source2==null) return null;
       for (biologic.Sequence s:source.getSequences()) {
           align.add(s);
       }
       for (biologic.Sequence s:source2.getSequences()) {
           align.add(s);
       }
       return align;
   }
}
