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

import configuration.Util;
import java.io.File;
import java.io.Serializable;
import java.util.Vector;
import workflows.workflow_properties;

/**
 *
 * @author Etienne Lord
 */
public class SOLIDFile extends Text implements Serializable {

    public SOLIDFile() {super();}
    public SOLIDFile(int id) {super(id);}
    public SOLIDFile(String filename) {super(filename);}

    @Override
    public String getBiologicType() {
        return "SOLIDFile";
    }

    public void setSolidFile(String filename) {
        this.setFilename(filename);
        this.setUnknownType("Solid Reads File");
        this.setText("Solid Reads File : "+filename+"\nSelected on: "+Util.returnCurrentDateAndTime());
    }
}
