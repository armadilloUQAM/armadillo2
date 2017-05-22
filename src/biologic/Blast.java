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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * This is a representation of a Blast File
 * @author Etienne Lord
 * @since 2010
 */
public class Blast extends Text implements Serializable{

    ////////////////////////////////////////////////////////////////////////////
    /// Variables
    public int id = 0;
    public LinkedList<BlastHit> blasthit_list = new LinkedList<BlastHit>();
    public String dbname = "";
    public String dbinfo = "";
    public String queryname = "";
    public String querylength = "";
    public String filename = "";
    public double max_bitscore = 0;
    public double min_bitscore = 0;


    ////////////////////////////////////////////////////////////////////////////
    /// Constructor
    public Blast() {super();}

    public Blast(String filename) {
        super(filename);
    }

    public Blast(int id) {
        super(id);
    }

    /**
     * @return the blasthit_list
     */
    public LinkedList<BlastHit> getBlasthit_list() {
        return blasthit_list;
    }

    /**
     * @param blasthit_list the blasthit_list to set
     */
    public void setBlasthit_list(ArrayList<BlastHit> blasthit_list) {
        this.setBlasthit_list(blasthit_list);
    }

   
    /**
     * @return the dbname
     */
    public String getDbname() {
        return dbname;
    }

    /**
     * @param dbname the dbname to set
     */
    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    /**
     * @return the dbinfo
     */
    public String getDbinfo() {
        return dbinfo;
    }

    /**
     * @param dbinfo the dbinfo to set
     */
    public void setDbinfo(String dbinfo) {
        this.dbinfo = dbinfo;
    }

    /**
     * @return the queryname
     */
    public String getQueryname() {
        return queryname;
    }

    /**
     * @param queryname the queryname to set
     */
    public void setQueryname(String queryname) {
        this.queryname = queryname;
    }

    /**
     * @return the querylength
     */
    public String getQuerylength() {
        return querylength;
    }

    /**
     * @param querylength the querylength to set
     */
    public void setQuerylength(String querylength) {
        this.querylength = querylength;
    }

    /**
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @param filename the filename to set
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * @return the max_bitscore
     */
    public double getMax_bitscore() {
        return max_bitscore;
    }

    /**
     * @param max_bitscore the max_bitscore to set
     */
    public void setMax_bitscore(double max_bitscore) {
        this.max_bitscore = max_bitscore;
    }

    /**
     * @return the min_bitscore
     */
    public double getMin_bitscore() {
        return min_bitscore;
    }

    /**
     * @param min_bitscore the min_bitscore to set
     */
    public void setMin_bitscore(double min_bitscore) {
        this.min_bitscore = min_bitscore;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public String getBiologicType() {
        return "Blast";
    }

    public String getExtendedString() {
        return toString();
    }

}
