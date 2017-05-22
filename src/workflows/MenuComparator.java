/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package workflows;

import java.awt.Menu;
import java.util.Comparator;

/**
 *
 * @author Lorde
 */
public class MenuComparator implements Comparator {

    public int compare(Object o1, Object o2) {
        Menu m1=(Menu)o1;
        Menu m2=(Menu)o2;
        return m1.getLabel().compareTo(m2.getLabel());


    }

}
