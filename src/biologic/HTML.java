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
import java.io.IOException;
import java.io.Serializable;
import workflows.workflow_properties;

/**
 * This is really a mock for handling in the Armadillo Workflow
 *
 * @author Etienne Lord
 * @author JG 2016
 */
public class HTML extends Unknown implements Serializable {

    public HTML()                {super();}
    public HTML(String filename) {super(filename);}
    public HTML(int id)          {super(id);}
    
    public String[] getExtensionTab() {
        String[] t = {".htm",".html"};
        return t;
    }

    public static void saveFile (workflow_properties p, String s, String pgrmName, String type) {
        s = Util.relativeToAbsoluteFilePath(s);
        Text f=new Text();
        f.setFilename(s);
        f.setName(Util.getFileNameAndExt(s)+" ("+Util.returnCurrentDateAndTime()+")");
        f.setNote(pgrmName+"_stats ("+Util.returnCurrentDateAndTime()+")");
        f.setText(type+" is here \n"+s+"\n\nSelected on: "+Util.returnCurrentDateAndTime()+"\nLoaded From program: "+pgrmName);
        f.setUnknownType(type);
        String typedb = type.toLowerCase();
        boolean b = f.saveToDatabase();
        if (b){
            p.put("output_"+typedb+"_id", f.getId());
            p.put("output_"+typedb+"_fileName", s);
        }
        else System.out.println(type+" file not saved");
    }

    
    @Override
    public String getBiologicType() {
        return "HTML";
    }
}
