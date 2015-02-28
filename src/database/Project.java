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

import configuration.Util;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;

/**
 * This represent a Project in the Armadillo Workflow Platform
 * --Note: initalize the database when Armadillo is started...
 * @author Etienne Lord
 */
public class Project implements Serializable, Iterator{
    private int id=0;
    private String Name="";
    private String Author = "";
    private String DateCreated = "";
    private String Note="";
    private String Institution="";
    private String Email="";
    private Boolean sample_data_in_database=false; //--This will include sample data in the database view
    
    public static databaseFunction df=new databaseFunction();

    public Project() {
        DateCreated=Util.returnCurrentDateAndTime();
    }

    public Project(int id) {
        this.loadFromDatabase(id);
    }

    public boolean loadFromDatabase(int id) {
       Project project=df.getProject(id);
        if (project.getId()>0) {
           this.setName(project.getName());
           this.setNote(project.getNote());
           this.setAuthor(project.getAuthor());
           this.setInstitution(project.getInstitution());
           this.setDateCreated(project.getDateCreated());
           this.setEmail(project.getEmail());
           this.id=id;
           return true;
        } 
        return false;
    }

    public boolean removeFromDatabase() {
       return df.removeProject(this);
    }

    public boolean saveToDatabase() {
        df.addProject(this);
        return (this.id==0);
    }

    public boolean updateDatabase() {
        return df.updateProject(this);
    };

////////////////////////////////////////////////////////////////////////////////
/// Iterator

    Vector<Integer>next=new Vector<Integer>();
    int counter=0;
    int maxid=-1;

    @Override
    public boolean hasNext() {
       //next=df.getAllSequenceID();
       if (next.size()==0) {
           next=df.getAllAlignmentID();
           maxid=next.size();
       }
       return (this.counter<maxid);
    }

    @Override
    public Object next() {
        return new Project(next.get(counter++));
    }

    
    public void remove() {
        Project s=new Project(counter-1);
        s.removeFromDatabase();
    }

    
     public boolean exists(Integer id) {
        return (df.getAllProjectID().contains(id));
    }

    ////////////////////////////////////////////////////////////////////////////
    ///


    /**
     * @return the Name
     */
    public String getName() {
        return Name;
    }

    /**
     * @param Name the Name to set
     */
    public void setName(String Name) {
        this.Name = Name;
    }

    /**
     * @return the Author
     */
    public String getAuthor() {
        return Author;
    }

    /**
     * @param Author the Author to set
     */
    public void setAuthor(String Author) {
        this.Author = Author;
    }

    /**
     * @return the DateCreated
     */
    public String getDateCreated() {
        return DateCreated;
    }

    /**
     * @param DateCreated the DateCreated to set
     */
    public void setDateCreated(String DateCreated) {
        this.DateCreated = DateCreated;
    }

    /**
     * @return the Note
     */
    public String getNote() {
        return Note;
    }

    /**
     * @param Note the Note to set
     */
    public void setNote(String Note) {
        this.Note = Note;
    }

    /**
     * @return the Institution
     */
    public String getInstitution() {
        return Institution;
    }

    /**
     * @param Institution the Institution to set
     */
    public void setInstitution(String Institution) {
        this.Institution = Institution;
    }

    /**
     * @return the Project_id
     */
    public int getId() {
        return id;
    }

    /**
     * @param Project_id the Project_id to set
     */
    public void setId(int Project_id) {
        this.id = Project_id;
    }

    @Override
    public String toString() {
        return Name+"\tCreated on: "+DateCreated+"\tBy "+Author;
    }

    /**
     * @return the Email
     */
    public String getEmail() {
        return Email;
    }

    /**
     * @param Email the Email to set
     */
    public void setEmail(String Email) {
        this.Email = Email;
    }




}
