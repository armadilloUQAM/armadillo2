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

import java.util.Comparator;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Lorde
 */
public class BlastTableSorter extends TableRowSorter {

    public BlastTableSorter(TableModel tm) {
        super(tm);
    }

    Comparator<String> comparator = new Comparator<String>() {

    public int compare(Double s1, Double s2) {    
        if (s1==s2) return 0;
        if (s1==0&&s2>0) return -1;
        if (s1==0&&s2<0) return +1;
        return s1.compareTo(s2);
    }

    public int compare(String o1, String o2) {
            return o1.compareTo(o2);
    }
    };




}
