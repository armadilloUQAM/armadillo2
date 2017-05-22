/*
 *  Armadillo Workflow Platform v2.0
 *  A simple pipeline system for phylogenetic analysis
 *  
 *  Copyright (C) 2009-2013  Etienne Lord, Mickael Leclercq
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
import java.io.Serializable;

/**
 * This is a genome class
 * A genome is a list of multiple sequence with annotation
 * Voir table ANNOTATION et Genome ALSO CALLED A DATASET
 * @author Etienne Lord
 * @since 2012
 */
public class DataSet extends Text implements Serializable {

    public DataSet()                {super();}
    public DataSet(int id)          {super(id);}
    public DataSet(String filename) {super(filename);}

    @Override
    public String getBiologicType() {
        return "Genome";
    }

    public void setGenomeFile(String filename) {
        this.setFilename(filename);
        this.setUnknownType("Solid Reads File");
        this.setText("Solid Reads File : "+filename+"\nSelected on: "+Util.returnCurrentDateAndTime());
    }
}
