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


import java.io.Serializable;

 //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 /// Class HGNCData - Class containing Hugo data with link for each protein
 /// Field separated in file by TAB: Field in this order
 ///
 /// Novembre 2008
 /// Revised Avril 2009
//[0] "HGNC ID"
//
//[1] "Approved Symbol"
//
//[2] "Approved Name"
//
//[3] "Status"
//
//[4] "Locus Type"
//
//[5] "Previous Symbols"
//
//[6] "Previous Names"
//
//[7] "Aliases"
//
//[8] "Name Aliases"
//
//[9] "Chromosome"
//
//[10] "Date Approved"
//
//[11] "Date Modified"
//
//[12] "Date Symbol Changed"
//
//[13] "Date Name Changed"
//
//[14] "Accession Numbers"
//
//[15] "Enzyme IDs"
//
//[16] "Entrez Gene ID"
//
//[17] "Ensembl Gene ID"
//
//[18] "Mouse Genome Database ID"
//
//[19] "Specialist Database Links"
//
//[20] "Specialist Database IDs"
//
//[21] "Pubmed IDs"
//
//[22] "RefSeq IDs"
//
//[23] "Gene Family Name"
//
//[24] "Record Type"
//
//[25] "Primary IDs"
//
//[26] "Secondary IDs"
//
//[27] "CCDS ID"
//
//[28] "VEGA IDs"
//
//[29] "Locus Specific Databases"
//
//[30] "GDB ID (mapped data)"
//
//[31] "Entrez Gene ID (mapped data supplied by NCBI)"
//
//[32] "OMIM ID (mapped data supplied by NCBI)"
//
//[33] "RefSeq (mapped data supplied by NCBI)"
//
//[34] "UniProt ID (mapped data supplied by UniProt)"
//
//[35] "Ensembl ID (mapped data supplied by Ensembl)"
//
//[36] "UCSC ID (mapped data supplied by UCSC)"
//
//[37] "Rat Genome Database ID (mapped data supplied by RGD)"

//        0HGNC ID	
//        1Approved Symbol	
//        2Approved Name	
//        3Status	
//        4Locus Type
//        5Locus Group	
//        6Previous Symbols	
//        7Previous Names	
//        8Aliases	Name 
//        9Aliases	
//        10Chromosome	
//        11Date Approved	
//        12Date Modified	
//        13Date Symbol Changed	
//        14Date Name Changed	
//        15Accession Numbers	
//        16Enzyme IDs	
//        17Entrez Gene ID	
//        18Ensembl Gene ID	
//        19Mouse Genome Database ID	
//        20Specialist Database Links	
//        21Specialist Database IDs	
//        22Pubmed IDs	
//        23RefSeq IDs	
//        24Gene Family Name	
//        25Record Type	
//        26Primary IDs	
//        27Secondary IDs	
//        28CCDS IDs	
//        29VEGA IDs	
//        30Locus Specific Databases	
//        31GDB ID (mapped data)	
//        32Entrez Gene ID (mapped data supplied by NCBI)	
//        33OMIM ID (mapped data supplied by NCBI)	
//        34RefSeq (mapped data supplied by NCBI)	
//        35UniProt ID (mapped data supplied by UniProt)	
//        36Ensembl ID (mapped data supplied by Ensembl)	
//        37UCSC ID (mapped data supplied by UCSC)	
//        38Mouse Genome Database ID (mapped data supplied by MGI)	
//        39Rat Genome Database ID (mapped data supplied by RGD)

public class HGNCdata implements Comparable, Serializable {
String[] data=new String[1];
String original;  // String original pour rechercher avec regex ALL


 public HGNCdata(String stri) {
     //hack we just load the comparable item
     data[0]=stri.substring(0,stri.indexOf("\t"));
     this.original=stri;
 }

 /**
  * We just process the data if we really need it
  */
 void loadData() {
     if (data.length==1) {
     this.original=this.original.replaceAll("\"", "");
        //stri=stri.replaceAll(",", "|");
        this.data=this.original.split("\\t");
     }
 }

 /**
  * @return the EnsemblID associated with this data
  */
 public String getEnsemblID() {
     loadData();
     try {
         return (!data[18].equals("")?data[18]:data[36]);
     } catch(Exception e) {return "";}
 }
 
 public String getGenbankID() {
     loadData();
     try {
         //--Note: changed on 1 June 2010
         return (!data[23].equals("")?data[23]:data[34]);
     } catch(Exception e) {return "";}
 }

  public String getDescription() {
     loadData();
      try {
         return data[1]+" "+data[2];
     } catch(Exception e) {return "";}
 }

  public String getHGNCid() {
      loadData();
      try {
         return data[0];
     } catch(Exception e) {return "";}
  }

         //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// OVERRIDE DIVERS
            
            // On Override la methode compareTo car on veut se baser uniquement sur le id 
           // pour connaître dans le ArrayList<hgncdata> l'ordre des sequences
            public int compareTo(Object obj) {
                HGNCdata d = (HGNCdata)obj;
                return this.data[0].compareTo(data[0]);
            }

           // On Override la methode equals car on veut se baser uniquement sur le id 
           // pour connaître dans le ArrayList<hgncdata> si la sequence est presente
           @Override
           public boolean equals(Object obj) {
                HGNCdata d = (HGNCdata)obj;
                return this.data[0].equals(d.data[0]);
           }

}
