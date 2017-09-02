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

import database.databaseFunction;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;
import workflows.workflow_properties;

/**
 *
 * @author Etienne Lord
 * @since 2010
 */
public class Ancestor extends Alignment implements Serializable,Biologic, Iterator {
  

    public Ancestor() {name="UnknownAncestor";}

    public Ancestor(int id) {}   

    ////////////////////////////////////////////////////////////////////////////////
/// Iterator

    @Override
    public boolean hasNext() {
        if (next.size()==0) {
            next=df.getAllAncestorID();
            maxid=next.size();
        }
        return (this.counter<maxid);
    }

    @Override
    public Object next() {
        return new Ancestor(next.get(counter++));
    }

    @Override
    public void remove() {
        Ancestor s=new Ancestor(counter-1);
        s.removeFromDatabase();
    }

    @Override
    public Vector<Integer> getAllId() {
        return next;
    }

    ////////////////////////////////////////////////////////////////////////////
    ///

    @Override
    public String getBiologicType() {
        return "Ancestor";
    }

    @Override
    public String toHtml() {
        return toString();
    }

    public String getFileNameId(int id) {
        return "";
    }
}
