/*
 *  Armadillo Workflow Platform v2.0
 *  A simple pipeline system for phylogenetic analysis
 *  
 *  Copyright (C) 2009-2014  Etienne Lord, Mickael Leclercq
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

import configuration.Util;
import database.databaseFunction;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;
import workflows.workflow_properties;

/**  
 *
 * @author Etienne Lord
 * @since 2014
 */
public class Model extends Text implements Biologic, Iterator, Serializable {
    public Model()       {}
    public Model(int id) {this.loadFromDatabase(id);}
    /**
    * This
    * @param filename
    * @param type ("Name of the program")
    */
    public Model(String filename, String type) {
        loadFromFile(filename);       
    }
}
