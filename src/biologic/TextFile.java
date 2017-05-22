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
import java.io.Serializable;
import java.util.Vector;
import workflows.workflow_properties;

/**
 * @author Etienne Lord
 * @since  2010
 * @author JG
 * @since  2016
 */
public class TextFile extends Text implements Serializable {

    public TextFile()                {super();}
    public TextFile(String filename) {super(filename);}
    public TextFile(int id)          {super(id);}

    @Override
    public String getBiologicType() {
        return "TextFile";
    }

    @Override
    public String toString() {
        if (Util.FileExists(getFilename())) {
            //--We need to keep the line...
            String[] str=Util.InputFile(this.getFilename());
            String rtn="";
            for (String s:str) rtn+=s+"\n"; //--Uniform string            
            return rtn;
        } else {
            return super.toString()+"\n"+this.getNote();
        }
    }
}
