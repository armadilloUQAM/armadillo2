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
import workflows.workflow_properties;

/**
 * Smaller version of the MultipleSequences without the actual sequences
 * @author Etienne Lord
 * @since Mars 2009
 */ 
public class InfoMultipleSequences extends MultipleSequences implements Serializable {

    private static final long serialVersionUID = 200904263L;

    ////////////////////////////////////////////////////////////////////////////////
    /// CONSTRUCTOR
    
    public InfoMultipleSequences() {}
    
    public InfoMultipleSequences(int id) {
       this.loadFromDatabase(id);
    }

    ////////////////////////////////////////////////////////////////////////////////
    // Database function

    @Override
    public boolean loadFromDatabase(int id) {
        InfoMultipleSequences multi=df.getInfoMultipleSequence(id);
        if (multi.id>0) {
            this.name=multi.name;
            this.id=id;
            this.note=multi.note;
            this.seq.clear();
            this.seq.addAll(multi.getSequences());
            return true;
        } else return false;
    }
    
    @Override
    public String toString() {
        String s="MultipleSequences "+getName()+" with ID ["+getId()+"]\n";
         for (Sequence S:this.getSequences()) {
             s+=S.toString()+"\n";
        }
        s+="Total "+this.getNbSequence()+" sequence(s) with len "+this.getSequenceSize()+"\n";
        return s;
    }

    @Override
    public workflow_properties returnProperties() {
        workflow_properties tmp=new workflow_properties();
        if (id==0) this.saveToDatabase();
            tmp.put("input_multiplesequences_id", this.getId());
            tmp.put("output_ multiplesequences_id", this.getId());
        return tmp;
    }
}
