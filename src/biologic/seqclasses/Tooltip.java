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

import configuration.Config;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Serializable;
import java.util.HashMap;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author lore26107809
 */
public class Tooltip implements Serializable {
    private static final long serialVersionUID = 200904262L;
    public HashMap<String,String> tooltip=new HashMap<String,String>();
    private static boolean debug=true;

    public String getTooltip(String str) {
        if (debug&&tooltip.get(str)==null) System.err.println("Warning tooltip for "+str+" dont exists.");
        return tooltip.get(str);
    }

    public void load(String filename) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
            tooltip.clear();
            while (br.ready()) {
                String stri=br.readLine();
                if (!stri.startsWith("#")&&!stri.equals("")&&!stri.startsWith(";")) {
                    String data[]=stri.split(";");
                    if (data.length>1) tooltip.put(data[0],data[1]);
                }
            }
            br.close();
        Config.log("Tooltip loaded from "+filename);
        } catch(Exception e) {System.err.println("Unable to load tooltip from "+filename);}

    }

}
