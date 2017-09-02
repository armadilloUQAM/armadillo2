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

import java.util.Vector;
import workflows.workflow_properties;

/**
 * Note: Strip down version of an alignment to have SequenceID.. but not the Sequence
 * @author Lord Etienne, Leclercq Mickael
 * @since Avril 2009 
 */
public class InfoAlignment extends Alignment {
    private static final long serialVersionUID = 20090427L;
    private int program_id=0;
    private boolean modified=false;
       
    private Vector<Integer>Sequence_id=new Vector<Integer>(); //Associated sequence_id with this alignment
    private Vector<Integer>originalSequence_id=new Vector<Integer>(); //Associated sequence_id with this alignment


    public InfoAlignment() {}

    public InfoAlignment(int id) {
       this.loadFromDatabase(id);
    }


    ////////////////////////////////////////////////////////////////////////////////
    // Database function

    @Override
    public boolean loadFromDatabase(int id) {
        InfoAlignment multi=df.getInfoAlignment(id);
        if (multi.id>0) {
            this.name=multi.name;
            this.id=id;
            this.note=multi.note;
            this.program_id=multi.getProgram_id();
            //==DEPRECATED
            this.originalSequence_id.clear();
            this.originalSequence_id.addAll(multi.getOriginalSequence_id());
            this.Sequence_id.clear();
            this.Sequence_id.addAll(multi.getSequence_id());        
            return true;
        } else return false;
    }

    public void clear() {
        getSequence_id().clear();
        originalSequence_id.clear();
    }

    @Override
    public String toString() {
        String s=getName()+" "+getId()+"\n";
        for (Integer ids:getSequence_id()) {
            InfoSequence info=df.getInfoSequence(ids);          
            s+=info.toString2();
        }
        return s;
    } 

    /**
     * @return the Sequence_id
     */
    public Vector<Integer> getSequence_id() {
        return Sequence_id;
    }

    /**
     * @param Sequence_id the Sequence_id to set
     */
    public void setSequence_id(Vector<Integer> Sequence_id) {
        this.Sequence_id = Sequence_id;
    }

    /**
     * @return the originalSequence_id
     */
    public Vector<Integer> getOriginalSequence_id() {
        return originalSequence_id;
    }

    /**
     * @return the program_id
     */
    public int getProgram_id() {
        return program_id;
    }

    /**
     * @param program_id the program_id to set
     */
    public void setProgram_id(int program_id) {
        this.program_id = program_id;
    }

     @Override
    public workflow_properties returnProperties() {
        workflow_properties tmp=new workflow_properties();
        if (id==0) this.saveToDatabase();
            tmp.put("input_alignment_id", this.getId());
            tmp.put("output_alignment_id", this.getId());
        return tmp;
     }

    /**
     * @return the modified
     */
    public boolean isModified() {
        return modified;
    }

    /**
     * @param modified the modified to set
     */
    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public void update() {
        super.updateDatabase();
    }
}
