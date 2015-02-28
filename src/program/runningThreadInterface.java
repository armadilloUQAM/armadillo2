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

package program;


import java.util.ArrayList;
import java.util.Vector;


/**
 * Interface for the programs class
 * @author Etienne Lord
 */

public interface runningThreadInterface {

    /**
     * Kill the current thread
     */
     public boolean KillThread();
     
     /**
      * 
      * @return reunning time in ms
      */
     public long getRunningTime();

     /**
      *
      * @return the thread is done
      */
     public boolean isDone();

    /**
     *
     * @return Program exitVal
     */
     public int getExitVal();

     /**
      * Nice
      * @return a pointer on itself
      */
     public Object getItself();
     
     /**
      * 
      * @return this running Thread name
      */
     public String getName();     

     /**
      *
      * @return the program output
      */
     public ArrayList<String> getOutputTXT();
     
     /**
      *
      * @return the program StatusCode
      */
     public int getStatus();
     
     /**
      * Run a series of tests to verify the validity of results
      * @return 
      */
     public abstract boolean test();
     
}
