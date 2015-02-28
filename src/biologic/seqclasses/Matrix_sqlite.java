
package biologic.seqclasses;

import database.database;
import java.io.*;
import java.sql.ResultSet;

/**
 *
 * @author Etienne Lord
 * @since August 2012
 */
public class Matrix_sqlite {

    
     static database db=null; 
     
    public Matrix_sqlite(database db) {
      this.db=db;
    } 
     
    public static void create_matrix(String name, Float array[][]) {
        int len_y=array.length;
        int len_x=array[0].length;
        //--Insert Matrix dimension and name
        db.execute("INSERT INTO Matrix(ID, X, Y, Data1) VALUES ('"+name+"','-1','0','"+len_x+"');");
        db.execute("INSERT INTO Matrix(ID, X, Y, Data1) VALUES ('"+name+"','0','-1','"+len_y+"');");
        for (int x=0; x<len_x;x++) {
            for (int y=0; y<len_y;y++) {
                db.execute("INSERT INTO Matrix(ID, X, Y, Data1) VALUES ('"+name+"','"+x+"','"+y+"','"+array[x][y]+"');");
            }
        }   
    }
    
    public static boolean setColumnXName(String matrix_name, int X, String column_name) {
        return db.execute("INSERT INTO Matrix(ID, X, Y, Data1) VALUES ('"+matrix_name+"','"+X+"','-2','"+column_name+"');");
    }
    
    public static boolean setRowYName(String matrix_name, int Y, String column_name) {
        return db.execute("INSERT INTO Matrix(ID, X, Y, Data1) VALUES ('"+matrix_name+"','-2','"+Y+"','"+column_name+"');");
    }
    
    public static boolean setDepthZName(String matrix_name, int Z, String column_name) {
        return db.execute("INSERT INTO Matrix(ID, X, Y,Z, Data1) VALUES ('"+matrix_name+"','-2','-2','"+Z+"','"+column_name+"');");
    }
    
   
    public static boolean setData2d(String matrix_name, int X, int Y, Float value) {
        return db.execute("INSERT INTO Matrix(ID, X, Y, Data1) VALUES ('"+matrix_name+"','"+X+"','"+Y+"','"+value+"');");
    }
   
    public static boolean setData3d(String matrix_name, int X, int Y,int Z, Float value) {
        return db.execute("INSERT INTO Matrix(ID, X, Y, Z, Data1) VALUES ('"+matrix_name+"','"+X+"','"+Y+"','"+"','"+Z+"','"+value+"');");
    }
    
    public static float getData2D(String matrix_name, int X, int Y) {
        try {
        ResultSet rs=db.executeQuery("SELECT Data1 FROM Matrix WHERE ID='"+matrix_name+"' AND X="+X+" AND Y="+Y);
            if (rs.next()) {
                   return rs.getInt(1); 
            } else {
                // Get simetrical?
                rs=db.executeQuery("SELECT Data1 FROM Matrix WHERE ID='"+matrix_name+"' AND X="+Y+" AND Y="+X);
                if (rs.next()) return rs.getInt(1);
            }       
         } catch(Exception e) {e.printStackTrace();}
        
        return 0.0f;
    }
    
    public static void get2DMatrix(String matrix_name) {
        int len_x=get_LenX(matrix_name);
        int len_y=get_LenY(matrix_name);
        //Display Column
        
        
        for (int x=0; x<len_x;x++) {
    
            for (int y=0; y<len_y;y++) {
                    System.out.print(getData2D(matrix_name, x, y)+"\t");
            }
            System.out.println("");
        }
        
    }
    
    public static int  get_LenX(String matrix_name) {
         try {
        ResultSet rs=db.executeQuery("SELECT Data1 FROM Matrix WHERE ID='"+matrix_name+"' AND X=-1;");
            if (rs.next()) {
                   return rs.getInt(1); 
            }       
         } catch(Exception e) {return 0;}
        return 0;
    }
    
    public static int get_LenY(String matrix_name) {
        try {
        ResultSet rs=db.executeQuery("SELECT Data1 FROM Matrix WHERE ID='"+matrix_name+"' AND Y=-1;");
            if (rs.next()) {
                   return rs.getInt(1); 
            }       
         } catch(Exception e) {return 0;}
        return 0;
    }
    
     public static int get_LenZ(String matrix_name) {
         try {
        ResultSet rs=db.executeQuery("SELECT Data1 FROM Matrix WHERE ID='"+matrix_name+"' AND Z=-1;");
            if (rs.next()) {
                   return rs.getInt(1); 
            }       
         } catch(Exception e) {return 0;}
        return 0;
    }
    
    /**
     * Read a matrix file of the form:
     * 
     * L  \t  
     * LL  \t 1  
     * LLL \t 2 \t 3
     * 
     * @param filename
     * @return 
     */
    public static Float[][] load_matrix(String filename) {
        Float[][] f=null;
        try{
            BufferedReader br=new BufferedReader(new FileReader(new File(filename)));
            //--First go to the last line and read the number of element    
            String buff="";
            int count_y=0;
            while (br.ready()) {
                String stri=br.readLine();
                if (!stri.isEmpty()) {
                    count_y++;
                    buff=stri;
                }
            }
            br.close();
            //--Second
            br=new BufferedReader(new FileReader(new File(filename)));            
            String stri2[]=buff.split("\t");
            int count_x=stri2.length;           
            f=new Float[count_y][count_x];
            System.out.println(count_y+"\t"+count_x);
            count_y=0;
            while (br.ready()) {
                String stri=br.readLine();
                if (!stri.isEmpty()) {
                     stri2=stri.split("\t");
                     System.out.println(stri2[0]+"\t"+stri2.length);
                     for (int x=1;x<stri2.length;x++) f[count_y][x]=Float.valueOf(stri2[x]);
                     count_y++;                   
                }
            }
            
            br.close();
            
        } catch(Exception e) {
            e.printStackTrace();
        }
        return f;
        
    }
    
    /**
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
      db=new database("test.db");
       
      // db.createTables("data/matrix.sql");
       //--LOAD THE MATRIX
      Float[][] data=load_matrix("data/test.txt");
      create_matrix("test", data);
        System.out.println(get_LenX("test"));
        System.out.println(get_LenY("test2"));
        System.out.println(getData2D("test2", 1, 1));
      get2DMatrix("test");
    }

}
