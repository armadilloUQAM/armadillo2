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
import workflows.workflow_properties;



/**
 * Common Text REturn by a Program
 * @author Etienne Lord
 * @since 2010
 */
public class Text extends Unknown implements Serializable {

    public Text() {}

    public Text(String filename) {
        super(filename);
    }

    public Text(int id) {
        super(id);
    }

    public void setText(String text) {
        StringBuilder st=new StringBuilder();
        st.append(text);
        this.setUnknown(st);
    }

    public void appendText(String text) {
         this.getUnknownST().append(text);
    }

    public String getText() {
        return this.getUnknown().toString();
    }

    @Override
    public String getBiologicType() {
        return "Text";
    }

    public String getExtendedString() {
        return toString();
    }

    public workflow_properties getProperties() {
        workflow_properties tmp=new workflow_properties();
         String outputType="Text";
         tmp.setName(outputType);
         tmp.put("colorMode","GREEN");
         tmp.put("defaultColor","GREEN");
         tmp.put("Output"+outputType, "True");
         tmp.put("outputType", outputType);
         tmp.put("Connector1Output","True");
         tmp.put("Connector0Output", "True");
         tmp.put("Connector0Conditional", "True");
         tmp.put("ObjectType", "OutputDatabase");
         tmp.put("editorClass", "editors.OutputEditor");
         tmp.put("Description", this.getFilename());
         tmp.put("output_"+outputType.toLowerCase()+"_id", this.getId());
        return tmp;        
    }

}
