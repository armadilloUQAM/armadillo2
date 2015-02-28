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


package resources;
import configuration.Config;
import java.awt.datatransfer.StringSelection;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceListener;
import javax.swing.JList;

/**
 * Special version of JList that implements a special Drag & Drop source
 * Inspiration: see
 * Java Swing, 2nd Edition
 * By Marc Loy, Robert Eckstein, Dave Wood, James Elliott, Brian Cole
 * Publisher: O'Reilly
 * @author Etienne Lord
 */
public class ToolJList extends JList implements DragGestureListener {


    ///////////////////////////////////////////////////////////////////////////
    /// VARIABLES

    DragSource ds=new DragSource();
    StringSelection transferable;

    /////////////////////////////////////////////////////////////////////////
    ///


   

    public void dragGestureRecognized(DragGestureEvent dge) {
         Config.log("Drag Gesture Recognized!");
         transferable = new StringSelection(this.getSelectedValue().toString());
         ds.startDrag(dge, DragSource.DefaultCopyDrop, transferable,(DragSourceListener) this);

    }

}
