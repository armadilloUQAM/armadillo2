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
 * This is really a mock for handling in the Armadillo Workflow
 * (in fact, we just save the image filename into the
 *  filename field
 * @author Etienne Lord
 * @author JG 2016
 */
public class ImageFile extends Text implements Serializable {

    public ImageFile()                {super();}
    public ImageFile(int id)          {super(id);}
    public ImageFile(String filename) {super(filename);}

    @Override
    public String toHtml() {
        return "<img src='"+this.getFilename()+"'></img>";
    }
    
    public String[] getExtensionTab() {
        String[] t = {".png",".jpg",".jpeg"};
        return t;
    }
    
    @Override
    public String getBiologicType() {
        return "ImageFile";
    }
}
