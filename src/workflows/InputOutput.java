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

package workflows;


/**
 * This describe an input-output properties
 * @author Etienne Lord
 * @since July 2009
 */
public class InputOutput {
    
    private String InputOutput = "";
    private String Connector="";      //--Use by InputOutputTableModel
    private String tooltip = "";
    private boolean selected=false;   //--Use by InputOutputTableModel2
    

    /**
     * @return the tooltip
     */
    public String getTooltip() {
        return tooltip;
    }

    /**
     * @param tooltip the tooltip to set
     */
    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    /**
     * @return the InputOutput
     */
    public String getInputOutput() {
        return InputOutput;
    }

    /**
     * @param InputOutput the InputOutput to set
     */
    public void setInputOutput(String InputOutput) {
        this.InputOutput = InputOutput;
    }

    /**
     * @return the Connector
     */
    public String getConnector() {
        return Connector;
    }

    /**
     * @param Connector the Connector to set
     */
    public void setConnector(String Connector) {
        this.Connector = Connector;
    }

    @Override
    public String toString() {
        return getInputOutput();
    }

    /**
     * @return the selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
