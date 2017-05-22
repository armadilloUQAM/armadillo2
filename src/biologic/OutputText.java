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

import configuration.Config;
import configuration.Util;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

/**
 * This represent a program output
 * @author Etienne Lord
 */
public class OutputText extends Unknown implements Serializable {

    public OutputText()                {super();}
    public OutputText(int id)          {super(id);}
    public OutputText(String filename) {super(filename);}

    public void setText(String txt) {
        StringBuilder st=new StringBuilder();        
        st.append(txt);
        this.setUnknown(st);
        this.setUnknownType("Text");
        this.setFilename("default.txt"); //--Default filename
        this.setName("Text");
        this.setNote("Loaded on "+Util.returnCurrentDateAndTime());
    }

    /**
     * This is a special function used by the Software output
     * @param txt
     */
    public void setText(ArrayList<String> txt) {
        StringBuilder st=new StringBuilder();
        //-- We might run out of memory, because size is doubled by the append method...
        try {
           //--Default case...
            for (String s:txt) st.append(s);
        } catch(Exception e) {
            //--Solution, try to calculate it before and;
            int size=0;
            for (String s:txt) {
                size+=s.length();
            }
            //--Ensure capacity but not more then 10 extra char...
            st=new StringBuilder(size+10);
            size=0;
            Config.log("Warning. StringBuilder capacity error." + this.getName()+Util.PrintMemory()+"\nTry to increase the momory allocated to Armadillo in the Preferences menu.");
              //--Ultime test (slow) - calculate the debording string...
            try {
                for (String s:txt) {                    
                    st.append(s);
                    size+=s.length();
                }
            } catch(Exception e2) {
                //--Ultime test (slow)
                st=new StringBuilder(size);
                int count=0;
                int i=0;
                while (count<=size&&i<txt.size()) {
                   st.append(txt.get(i));
                   count+=txt.get(i).length();
                }
            }
        }
        this.setUnknown(st);
        this.setUnknownType("stdout");
        this.setFilename("stdout.txt");        
        this.setNote("Software output ("+Util.returnCurrentDateAndTime()+")");
        
    }
    
    @Override
    public String getBiologicType() {
        return "OutputText";
    }
}
