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

import java.util.HashMap;

/**
 *
 * @author Etienne Lord
 */
public class GeneticCode {
    private String    name="";
    private int id = 0;
    private HashMap<String,String> code=new HashMap<String,String>();
    private HashMap<String,String> start_code=new HashMap<String,String>();




    @Override
    public String toString() {
        return getName()+" "+getId();
    }


    /**
     * @return the code
     */
    public HashMap<String, String> getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(HashMap<String, String> code) {
        this.code = code;
    }

    /**
     * @return the start_code
     */
    public HashMap<String, String> getStart_code() {
        return start_code;
    }

    /**
     * @param start_code the start_code to set
     */
    public void setStart_code(HashMap<String, String> start_code) {
        this.start_code = start_code;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }
}
