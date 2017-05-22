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

package biologic;

import database.databaseFunction;
import java.util.Iterator;
import java.util.Vector;
import workflows.workflow_properties;

/**
 * Interface for some needed function for Biologic data from the database
 * @author Etienne Lord
 */
public interface Biologic extends Iterator {
    

    //--CRUD Interface
    public boolean saveToDatabase();
    public boolean loadFromDatabase(int id);
    public boolean removeFromDatabase();
    public boolean updateDatabase();
    
    public boolean loadFromFile(String filename);
    
    public workflow_properties returnProperties();

    public int getId();
    public String getName();
    public String getNote();
    public String getBiologicType();
    public int getRunProgram_id();
    public Vector<Integer>getAllId();
    public String getNameId(int id);
    public String getFileNameId(int id);
    public void setName(String name);
    public void setNote(String note);
    public void setData(String data);
    public boolean exists(Integer id);
    public Biologic getParent();

    @Override
    public String toString();
    public String toHtml();
    public String getFasta();
    public String getPhylip();    
    public String getExtendedString(); //tabular representation of all the data
      
}
