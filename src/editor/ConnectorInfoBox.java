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


package editor;

import configuration.Config;
import workflows.*;
import java.awt.Frame;
import workflows.armadillo_workflow.workflow_connector;

/**
 * Holded for the connectorInfoJDialog (this is a connector)
 * @author Etienne Lord
 *
 */
public class ConnectorInfoBox {

    public static ConnectorInfoJDialog connectorInfo;

    public ConnectorInfoBox(Frame parent) {
        connectorInfo=new ConnectorInfoJDialog(parent);
    }

    public ConnectorInfoBox() {}

    public void display(workflow_connector connector) {
        if (connectorInfo!=null) {
            connectorInfo.display(connector);
        }
    }

    /**
     *
     * @return
     */
    public int getStatus() {
        return connectorInfo.getStatus();
    }

    /**
     * Reset this 
     * @param code
     */
    public void resetStatus(int code) {
        connectorInfo.setStatus(Config.status_idle);
    }

}
