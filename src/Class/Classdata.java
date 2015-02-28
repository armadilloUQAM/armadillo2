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
import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Holder for class information
 * CAN BE USE TO RUN THIS CLASS
 * WARNING: Class MUST BE IN PACKAGE programs or editors
 * @author Etienne Lord
 * @since July 2009
 */
public class Classdata implements Comparable, Serializable {
String ClassName="";
public Class data;                                     // Class
public Method[] method=new Method[0];                  // Class Methods
public Field[]  field=new Field[0];                    // Class Fields
public Constructor[] constructor=new Constructor[0];   // Class Constructor
public String[] All;                       // Array contraning a description of all
public static boolean debug=true;


 public Classdata(String ClassName) {
     try{


         if (ClassName.endsWith(".class")) ClassName=ClassName.substring(0,ClassName.indexOf(".class"));
         if (ClassName.endsWith(".jar")) ClassName=ClassName.substring(0,ClassName.indexOf(".jar"));
         //-- Load the Class
         //-- Note: we try for 4 packages at the moment: programs. and editors. biologic. local.
         try {
           data=Class.forName(ClassName);
         } catch (Exception e) {
           try {
               data=Class.forName("programs."+ClassName);
           } catch(Exception e2) {
               try {
               data=Class.forName("editors."+ClassName); 
               } catch (Exception e3) {
                   try {
                      data=Class.forName("biologic."+ClassName);
                   } catch (Exception e4) {}
               }
             }
         }
         if (data==null) {
             Config.log("Unable to get class: "+ClassName);
             return;
         }
        //data=ClassLoader.getSystemClassLoader().loadClass(classpath);
        this.ClassName=data.getName();
        method=data.getDeclaredMethods();
        field=data.getDeclaredFields();
        constructor=data.getDeclaredConstructors();
        int len=method.length+field.length+constructor.length+3;
        All=new String[len];
        len=0; //--Use as counter
        All[len++]="-- Methods --";
        for (Method m:method) All[len++]=m.toGenericString();
        All[len++]="-- Fields --";
        for (Field f:field) All[len++]=f.toGenericString();
        All[len++]="-- Constructor --";
        for (Constructor c:constructor) All[len++]=c.toGenericString();

     } catch (Exception e) {
         if (debug) {
             Config.log("Error loading "+this.ClassName);
             e.printStackTrace();
         }
     }
 }

 /**
  * 
  * @param classpath
  * @param packageName
  */
 public Classdata(String ClassName, String packageName) {
     try{
         if (ClassName.endsWith(".class")) ClassName=ClassName.substring(0,ClassName.indexOf(".class"));
         data=Class.forName(packageName+"."+ClassName);
        //data=ClassLoader.getSystemClassLoader().loadClass(classpath);
        this.ClassName=data.getName();
        method=data.getDeclaredMethods();
        field=data.getFields();

     } catch (Exception e) {
         if (debug) e.printStackTrace();
     }
 }

 ///////////////////////////////////////////////////////////////////////////////
 /// Class and cast object

 /**
 * Return a string array of the Class in the specify directory or null if not found
 * Note: filename will not include path
 */
public static String[] loadClasslisting (String path) {
  FilenameFilter filter=new FilenameFilter() {
  public boolean accept(File dir, String name) {
  if (name.charAt(0) == '.') return false;
  if (name.toLowerCase().endsWith(".class")) return true;
  return false;
  }
  };
  File dataFolder = new File(path);
  String[] names = dataFolder.list(filter);
  if (names==null) names=new String[0];
  return names;
}

/**
 * Return an object representing and instance of this class
 * @param ClassName
 * @return an object of this class
 */
public Object newObject() {
    Object T=null;
    if (data==null) return null;
    try {
       T=data.newInstance();
    return T;
    } catch(Exception e) {
        e.printStackTrace();
        //Config.log("Unable to initiate newObject(): "+ e.getMessage());
        return null;}

}

public String getName() {
    return this.ClassName;
}


public int getNbMethods() {
    return method.length;
}

public int getNbFields() {
    return field.length;
}

public Class getClassData() {
  return data;
}

@Override
public String toString() {
    return getName();
}
         //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// OVERRIDE DIVERS
            
            // On Override la methode compareTo car on veut se baser uniquement sur le id 
           // pour connaître dans le ArrayList<hgncdata> l'ordre des sequences
            public int compareTo(Object obj) {
                Classdata d = (Classdata)obj;
                return this.ClassName.compareTo(d.ClassName);
            }

           // On Override la methode equals car on veut se baser uniquement sur le id 
           // pour connaître dans le ArrayList<hgncdata> si la sequence est presente
           @Override
           public boolean equals(Object obj) {
                Classdata d = (Classdata)obj;
                return this.ClassName.equals(d.ClassName);
           }

}
