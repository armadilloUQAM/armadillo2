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


import configuration.Config;
import java.awt.Dimension;
import javax.swing.DefaultDesktopManager;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import tools.ToolJInternalFrame;
import tools.Toolbox;
import workflows.WorkFlowJInternalFrame;


/**
 * Armadillo DeskTop Manager
 * Handle the placement of JInternalFrame in the application windows
 * @author Alix Boc, Etienne Lord, Mickael Leclercq, Abdoulaye Baniré Diallo
 * @since May 2009
 *
 */
public class armadilloDesktopManager extends DefaultDesktopManager {

   public static boolean debug=false; //For debug purpose
   Toolbox toolbox=new Toolbox();

   @Override
   public void setBoundsForFrame(JComponent f, int x, int y, int w, int h) {
    if (f instanceof JInternalFrame == false) {
      super.setBoundsForFrame(f, x, y, w, h); // only deal w/internal frames
    }
    else {
      JInternalFrame frame = (JInternalFrame)f;
      JDesktopPane desktop = frame.getDesktopPane();
      Dimension d = desktop.getSize();
      //-- Custom code for JInternalFrame customization
        if (f instanceof ToolJInternalFrame) {
           h=d.height;
           w=toolbox.getDimension().width;
        }
        if (f instanceof WorkFlowJInternalFrame) {
            h=d.height;
            w=d.width-toolbox.getDimension().width;
        }
      //-- Code for clipping
           if (x < 0) x=0;
           //if (x+w>d.width) x = d.width-10; // Note-- We don't want to be restrictive in x
           if (y < 0) y=0;
           if (y+h > d.height) y = d.height-h;
           super.setBoundsForFrame(f, x, y, w, h);
           // --Debug
           if (debug) Config.log(frame.getTitle()+x+" "+y+" "+w+" "+h);
    }
  }




}
