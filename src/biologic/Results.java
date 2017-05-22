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
import workflows.workflow_properties;

/**
 * Common Text returned by a Program
 * @author Etienne Lord
 * @since 2010
 */
public class Results extends Unknown implements Serializable {

    public Results()                {}
    public Results(int id)          {super(id);}
    public Results(String filename) {super(filename);}

    public void setText(String text) {
        StringBuilder st=new StringBuilder();
        st.append(text);
        this.setUnknown(st);
    }

    public void log(String text) {
        this.getUnknownST().append(text);
    }

    public String getText() {
        return this.getUnknown().toString();
    }

    @Override
    public String getBiologicType() {
        return "Results";
    }

    public static void saveResultsPgrmOutput (workflow_properties p, String outputPgrm, String pgrmName) {
        Results text=new Results();
        text.setText(p.getName()+"_result\n"+outputPgrm+"\n");
        text.setNote(pgrmName+"_stats ("+Util.returnCurrentDateAndTime()+")");
        text.setName(pgrmName+" ("+Util.returnCurrentDateAndTime()+")");
        boolean b = text.saveToDatabase();
        if (b) p.put("output_results_id", text.getId());
        else System.out.println("WARNING : results not saved");
    }
}
