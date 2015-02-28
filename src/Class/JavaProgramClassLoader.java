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

package Class;

import configuration.Config;
import java.io.FileInputStream;

/**
 * ClassLoader to load to class
 * See : http://www.javaworld.com/javaworld/jw-10-1996/jw-10-indepth.html
 * @author Etienne Lord
 * @since June 2011
 */
public class JavaProgramClassLoader extends ClassLoader {

    @Override
      public Class findClass(String filename) {
             byte[] b = loadClassData(filename);
             //--Note: we restrict the name for data protection
             return defineClass("code.source", b, 0, b.length);
         }

    /**
     * This load a class from a filename
     * @param name
     * @return a byte representation of a class
     */
     private byte[] loadClassData(String name) {
        return getClassFromDisk(name);
     }

     /**
      * Helper function to load a class from disk
      * @param className
      * @return a byt representation of a class
      */     
    public byte getClassFromDisk(String className)[] {
    	byte data[];
    	try {
    	    FileInputStream imput = new FileInputStream(className);
    	    data = new byte[imput.available()];
    	    imput.read(data);
    	    return data;
    	} catch (Exception e) {
            Config.log("Unable to load "+className);
            return null;

        }
    }
}
