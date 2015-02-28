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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package database;
import configuration.Config;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Scanner;

/**
 *
 * @author Etienne
 */
public class databaseGO {


    public static void test() {
            boolean done=false;

            String command="SELECT * FROM gene_product INNER JOIN dbxref ON (gene_product.dbxref_id=dbxref.id) INNER JOIN species ON (gene_product.species_id=species.id) WHERE    symbol = 'BRCA1';";
            Config.log("Connecting to ensembl ancestral DNA database");
            while (!done) {
            Scanner sc=new Scanner(System.in);
            Config.log("Will connect to : go_latest");
            Config.log("And performed   :"+command);
            Config.log("Choices:")   ;
            Config.log("1. Perform.");
            Config.log("0. Quit.");
            Config.log("Otherwise will execute the command typed");
            int choice=0;
            String stri =sc.nextLine();
            try {
                choice=Integer.valueOf(stri);
            } catch(Exception e) {choice=0;command=stri;}
            switch (choice) {

                case 0: done=true;
                        break;

                default:
                    try {
                    Class.forName("com.mysql.jdbc.Driver").newInstance();
                    java.sql.Connection conn;
                    conn = DriverManager.getConnection("jdbc:mysql://mysql.ebi.ac.uk:4085/go_latest", "go_select", "amigo");

                   ResultSet rs;

                    rs = (ResultSet) conn.createStatement().executeQuery(command);

                                while (rs.next()) {

                                    int n=rs.getMetaData().getColumnCount();
                                    //Get the column name here and print
                                    for (int i=1; i<n+1;i++) Config.log(rs.getMetaData().getColumnName(i)+"\t");
                                    System.out.println();
                                    //Get the data
                                    for (int i=1; i<n+1;i++) {
                                        Config.log(rs.getString(i)+"\t");
                                    }
                                    System.out.println();
                                    //Config.log(rs.getString(1));
                                }
                    conn.close();
                    } catch (Exception ex) {ex.printStackTrace();}
                    break;

            } //End switch
            } //End while
        }


}
