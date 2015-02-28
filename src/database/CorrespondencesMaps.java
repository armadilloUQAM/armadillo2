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


package database;

import java.util.HashMap;

/**
 *
 * @author Leclercq Mickael
 */
public class CorrespondencesMaps {

    //////////////////////////////////////////////////////////////////////////
    ///// HashMaps

    public static String [] tablesSQL ={"Programs","RunPrograms","Sequence", "SequenceStats",
    "MultipleSequences","Alignment","SequencesAlignments","Tree",
    "TreesSequences","Ancestor","AncestorsSequences","Properties","PropertiesValues",
    "Workflows", "Objects", "Connectors"}; //This is the table that you want to display

    public static String [] selectForTreeLeafs ={"name","runProgram_id","name", "sequenceStats_id",
    "name","name","Alignment_id","tree_id",
    "tree_id","ancestor_id","tree_id","properties_name","properties_id",
    "workflows_name", "objects_name", "connectors_id"};

    public static String [] selectForTableModelColumnsRenamed ={
        //Programs
        "program_id AS 'Program ID', name AS Name",
        //RunPrograms
        "runProgram_id AS 'Run Program ID', properties_id AS 'Properties ID', " +
                "note AS Note, programTimeStart AS 'Program time start', programTimeEnd " +
                "AS 'Program time end'",
        //Sequence
        "sequence_id AS 'Sequence ID', name AS Name, accession AS 'Access Number', accessionReferee AS 'Accession Referee', " +
                "gi AS Gi, note AS Note, timeAdded AS 'Creation Date'",
        //SequenceStats
        "sequenceStats_id AS 'Sequence Stats ID', sequence_id AS 'Sequence ID', sequence_len AS 'Sequence lenght', " +
                "countA AS A, countU AS U, countT AS T, countG AS G, countC AS C, countN AS N, countGAP AS Gaps",
        //MultipleSequences
        "MultipleSequences_id AS 'Multiple Sequences ID', sequence_id AS 'Sequence ID', name AS Name, note AS Note ",
        //Alignment
        "Alignment_id AS 'Alignment ID', sequence_id AS 'Sequence ID', original_sequence_id AS 'Original Sequence ID', runProgram_id AS 'RunProgram ID', " +
                "name AS Name, note AS Note",
        //SequenceAlignment
         "Alignment_id AS 'Alignment ID', MultipleSequences_id AS 'Multiple Sequences ID'",
        //Tree
        "tree_id AS 'Tree ID', runProgram_id AS 'RunProgram ID', tree AS Tree, note AS Note",
        //TreeSequence
        "tree_id AS 'Tree ID', alignment_id AS 'Alignment ID'",
        //Ancestor
        "ancestor_id AS 'Ancestor ID', alignment_id AS 'Alignment ID', runProgram_id AS 'Run program ID', " +
                "ancestor AS Ancestor, note AS Note",
        //AncestorsSequences
        "tree_id AS 'Tree ID', ancestor_id AS 'Ancestor ID'",
        "properties_name",
        "properties_id",
        //Workflows
        "workflows_id AS 'Workflow ID', workflows_name AS 'Workflows Name', " +
                "workflows_filename AS 'Workflow Filename', note AS Note, " +
                "date_created AS 'Date created', date_modified AS 'Date Modified'",
        "objects_name",
        "connectors_id"};

    public static String[] ColumnsSQLOriginal ={
        //Programs 2
        "program_id","name",
        //RunPrograms 5
        "runProgram_id", "properties_id", "note", "programTimeStart", "programTimeEnd",
        //Sequence 7
        "sequence_id", "name", "accession", "accessionReferee","gi", "note", "timeAdded",
        //SequenceStats 10
        "sequenceStats_id", "sequence_id", "sequence_len", "countA", "countU", "countT", "countG", "countC"
                ,"countN", "countGAP",
        //MultipleSequences 4
        "MultipleSequences_id", "sequence_id", "name", "note",
        //Alignment 6
        "Alignment_id", "sequence_id", "original_sequence_id", "runProgram_id", "name", "note",
        //SequenceAlignment 2
         "Alignment_id", "MultipleSequences_id",
        //Tree 4
        "tree_id", "runProgram_id", "tree", "note",
        //TreeSequence 2
        "tree_id", "alignment_id",
        //Ancestor 5
        "ancestor_id", "alignment_id", "runProgram_id", "ancestor", "note",
        //AncestorsSequences 2
        "tree_id", "ancestor_id",

        "properties_name",

        "properties_id",
        //Workflows 6
        "workflows_id", "workflows_name", "workflows_filename", "note", "date_created" , "date_modified",

        "objects_name",

        "connectors_id"};//63

    public static String [] ColumnsSQLAlias={
        //Programs 2
        "Program ID", "Name",
        //RunPrograms 6
        "Run Program ID", "Properties Id", "Note", "Program time start", "Program time end",
        //Sequence 7
        "Sequence ID", "Name", "Access Number", "Accession Referee", "Gi", "Note", "Creation Date",
        //SequenceStats 10
        "Sequence Stats ID", "Sequence ID", "Sequence lenght", "A", "U", "T", "G", "C", "N", "Gaps",
        //MultipleSequences 4
        "Multiple Sequences ID", "Sequence ID", "Name", "Note",
        //Alignment 6
        "Alignment ID", "Sequence ID", "Original Sequence ID","RunProgram ID", "Name", "Note",
        //SequenceAlignment 2
         "Alignment ID", "Multiple Sequences ID",
        //Tree 4
        "Tree ID", "RunProgram ID", "Tree", "Note",
        //TreeSequence 2
        "Tree ID", "Alignment ID",
        //Ancestor 5
        "Ancestor ID", "Alignment ID", "Run program ID", "Ancestor", "Note",
        //AncestorsSequences 2
        "Tree ID","Ancestor ID",

        "properties_name",

        "properties_id",
        //Workflows 6
        "Workflow ID", "Workflows Name", "Workflow Filename", "Note", "Date created","Date Modified",

        "objects_name",

        "connectors_id"};//63
    public HashMap<String,String> treeNode=new HashMap<String,String>();
    public HashMap<String,String> tableNode=new HashMap<String,String>();
    public HashMap<String,String> Columns=new HashMap<String,String>();


}
