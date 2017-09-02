package programs;

//////////////////////////////////////////////////////////////////////////////////////////////33
///
/// Create a Thread to run q_func_cpp
/// Note: specific to Doris Ransy sequences
/// Etienne Lord 2011
/// Ligne de commande:
/// q_funct
/// --msa_fasta /data/PROJETS2/q_func_cpp/files/etienne/test_etienne_function_q/805IJ.fasta
/// --x_ident_csv /data/PROJETS2/q_func_cpp/files/etienne/test_etienne_function_q/I.csv
/// --q_func_csv /data/PROJETS2/q_func_cpp/files/etienne/test_etienne_function_q/I_results_km_simple.csv (output)
/// --calc_type simple --f_opt_max 4 --align_type dna --dist ham --winl 20
/// --win_step 3 --optim km
///
///
/// This will test one possibility for an alignment
/// The possibility can be created using the create workflow_properties

import biologic.Alignment;
import biologic.Results;
import biologic.Sequence;
import biologic.Text;
import configuration.Util;
import java.util.Vector;
import java.util.regex.Pattern;
import program.RunProgram;
import workflows.workflow_properties;

public class q_func_cpp extends RunProgram {
    
    /////////////////////////////////////////////////////////////////////////////////////////////
    /// Variables 
    
    private String infile_sequence="sequences.fasta";
    private String group_sequence="sequences.csv";
    private String outfile_sequence="results.csv";
        
    /////////////////////////////////////////////////////////////////////////////////////////////
    /// Constructor        
    public q_func_cpp(workflow_properties properties) {
       super(properties);
       execute(); 
     }
    
    /////////////////////////////////////////////////////////////////////////////////////////////
    /// Initialization
    
    @Override
    public boolean init_checkRequirements() {
          int alignment_id=properties.getInputID("alignment"); 
          if (alignment_id==0) {
            setStatus(status_BadRequirements,"Error. No input alignment found.");
            return false;
          }
           //--Verify we have group...
          int text_id=properties.getInputID("text"); 
          if (!properties.isSet("group1")&&!properties.isSet("group2")&&text_id==0) {
              setStatus(status_BadRequirements,"Error. You need to define sequence groups using regular expression or through a text in the input.");
              return false;
          }  
        return true;
    }

 @Override
    public void init_createInput() {
        //--Create the sequence groups
        int alignment_id=properties.getInputID("alignment"); 
        Alignment align=new Alignment(alignment_id);              
        
        //--Create the group file...
        int text_id=properties.getInputID("text"); 
        if (text_id!=0) {
             Text text=new Text(text_id);
             //--HACK -- remove extra space in sequence name,,
             //--TO DO-- Check if the name are in the sequence file...
             String[] array_str=text.getText().split("\n");
             String new_text="";
             for (String s:array_str) {
                 if (!s.trim().isEmpty()) {
                     String name=s.trim();
                     //--Verify it is contained in the alignement...
                     boolean contained=false;
                     for (Sequence seq:align.getSequences()) {
                         if (seq.getName().equalsIgnoreCase(name)) contained=true;
                     }                                         
                     if (contained) new_text+=s.trim()+"\n";
                 }
             }
             text.setText(new_text);             
             text.Output(group_sequence);
             align.outputFasta(infile_sequence);
              properties.put("output_alignment_id", align.getId());
        } else {
            Vector<Alignment> align_group=Create_Group_Sequences(align);
            //--Create the sequence
            Alignment align2=concatenate(align_group.get(0), align_group.get(1));
            align2.setName("Input Q function alignment");
            align2.setNote("Created on "+Util.returnCurrentDateAndTime());
            align2.saveToDatabase();
            properties.put("output_alignment_id", align2.getId());
            addOutput(align2);
            align2.outputFasta(infile_sequence);        
            Text text=new Text();
            String t="";
            for (Sequence s:align_group.get(0).getSequences()) t+=s.getName()+"\n";
            text.setText(t);
            text.Output(group_sequence);
        }
        
    }
 
 
 /**
 * Concatenate two group of MultipleSequences into a new one
 * @param source
 * @param source2
 * @return a new Alignment
 */
   public Alignment concatenate(Alignment source, Alignment source2) {
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

 
 /**
  * This will split the alignment into 3 group according the the sequence name
  * --This is like a filter that need to be calibrated...maybe a regex?
  * @param align
  * @return
  */
public Vector<Alignment> Create_Group_Sequences(Alignment align) {
     Vector<Alignment> tmp=new Vector<Alignment>();
    Alignment group1=new Alignment();
    Alignment group2=new Alignment();
    boolean use_rest_group1=false;
    boolean use_rest_group2=false;
    Pattern group1_pattern=null;
    Pattern group2_pattern=null;
    
    int text_id=properties.getInputID("text"); 
    //--CASE 1. group specified by the user
    if (text_id!=0) {
        //--Nothing to do...
    } else {        
       //--CASE 2. group specified by regular expression      
    
    
        if (!properties.isSet("group1")||properties.get("group1").equals("")) use_rest_group1=true;
        if (!properties.isSet("group2")||properties.get("group2").equals("")) use_rest_group2=true;

        if (!use_rest_group1) group1_pattern=Pattern.compile(properties.get("group1"));
        if (!use_rest_group2) group2_pattern=Pattern.compile(properties.get("group2"));

        //--Name the sequences group
        group1.setName("Q_function_Group1-"+(use_rest_group1?"rest of sequences":properties.get("group1")));
        group2.setName("Q_function_Group2-"+(use_rest_group1?"rest of sequences":properties.get("group2")));

        if (properties.getBoolean("debug")) System.out.println(use_rest_group1+" "+use_rest_group2+" "+properties.get("group1")+" "+properties.get("group2"));

       
        if (align.getNbSequence()!=0) {
            for (Sequence s:align.getSequences()) {
                //--HANDLE THE DIFFERENT CASE
                // CASE 1. two group pattern 
                if (!use_rest_group1&&!use_rest_group2) {
                    if (group1_pattern.matcher(s.toString()).find()) {
                        group1.add(s);
                    }                
                    if (group2_pattern.matcher(s.toString()).find()) {
                        group2.add(s);
                    } 
                } else if (use_rest_group1) {
                    if (group2_pattern.matcher(s.toString()).find()) {
                        group2.add(s);
                    } else {
                        group1.add(s);
                    }
                } else if (use_rest_group2){
                     if (group1_pattern.matcher(s.toString()).find()) {
                        group1.add(s);
                    } else {
                        group2.add(s);
                    }
                }
            }
            group1.saveToDatabase();
            group2.saveToDatabase();        
            tmp.add(group1);
            tmp.add(group2);
        }
    }
    return tmp;
}  
 
   
     
    /////////////////////////////////////////////////////////////////////////////////////////////
    /// Creating program command line
    /// --msa_fasta /data/PROJETS2/q_func_cpp/files/etienne/test_etienne_function_q/805IJ.fasta
/// --x_ident_csv /data/PROJETS2/q_func_cpp/files/etienne/test_etienne_function_q/I.csv
/// --q_func_csv /data/PROJETS2/q_func_cpp/files/etienne/test_etienne_function_q/I_results_km_simple.csv
/// --calc_type simple --f_opt_max 4 --align_type dna --dist ham --winl 20
/// --win_step 3 --optim km

     @Override
     public String[] init_createCommandLine() {     

       String[] com=new String[30];
       for (int i=0; i<com.length;i++) com[i]="";
       //int index=11;
       com[0]="cmd.exe";
       com[1]="/C";
       com[2]=properties.getExecutable();
       com[3]="--msa_fasta";
       com[4]=infile_sequence;
       com[5]="--x_ident_csv";
       com[6]=group_sequence;
       com[7]="--q_func_csv";
       com[8]=outfile_sequence;
      
       com[9]="--calc_type";
       com[10]=properties.get("calc_type");
        
       com[11]="--f_opt_max";
       com[12]=properties.get("f_opt_max");

       com[13]="--align_type";
       //com[14]="dna";
       com[14]=properties.get("align_type");
       
      com[15]="--dist";
      com[16]=properties.get("dist");
      
       com[17]="--winl";
       com[18]=properties.get("winl");

       com[19]="--win_step";
       com[20]=properties.get("win_step");

       com[21]="--optim";
       com[22]=properties.get("optim");

       if (properties.isSet("protmatrix")) {
           com[21]="--protmatrix";
           com[22]=properties.get("protmatrix");
       }
       
       setStatus(status_running, Util.toString(com));
       return com;
    }
    
    /////////////////////////////////////////////////////////////////////////////////////////////
    /// Processing program output
    @Override
    public void post_parseOutput() {
          Results results=new Results(outfile_sequence);
          results.setName("Q Function results ("+Util.returnCurrentDateAndTime()+")");
          results.setNote("Created on "+Util.returnCurrentDateAndTime());
          results.saveToDatabase();
          properties.put("output_results_id", results.getId());
          addOutput(results);
          
          Text text=new Text(group_sequence);
          text.setName("Q Function group (.csv) ("+Util.returnCurrentDateAndTime()+")");
          text.setNote("Created on "+Util.returnCurrentDateAndTime());
          text.saveToDatabase();
          properties.put("output_text_id", text.getId());
          addOutput(text);
          
          Util.deleteFile(outfile_sequence);
          Util.deleteFile(group_sequence);
          Util.deleteFile(infile_sequence);
    }


///**
// * Create a workflow_properties file with the good parameters
// * @param calc_type      [simple, auto, single(whole sequence)]
// * @param opt_max        [function 0-4]
// * @param dist           [ham]
// * @param winl           [length of windows: ex. 20]
// * @param win_step       [step: ex. 1, 3...]
// * @param optimisation   [optimisation: km or nj]
// * @return a new workflow properties with also a header properties
// */
//    public static workflow_properties create_properties(String calc_type, int opt_max, String dist, int winl, int win_step, String optimisation) {
//        workflow_properties prop=new workflow_properties();
//        prop.put("calc_type", calc_type);
//        prop.put("f_opt_max",opt_max);
//        prop.put("dist", dist);
//        prop.put("winl", winl);
//        prop.put("win_step", win_step);
//        prop.put("optim", optimisation);
//        prop.setExecutable("q_func_cpp");
//        //--Create the results file header
//       String str=String.format("_%s_%d_%s_len_%d_step_%d_%s",calc_type,opt_max,dist,winl, win_step, optimisation);
//       prop.put("header",str);
//        return prop;
//    }
   
}
