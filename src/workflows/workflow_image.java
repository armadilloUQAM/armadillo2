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


package workflows;

import configuration.Config;
import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import processing.core.PApplet;
import processing.core.PImage;

/**
 * Holder for the images loaded and used by the armadillo_workflow
 * Also handle color palette
 * @author Etienne Lord
 * @since June 2009
 */
public class workflow_image extends PApplet {

    public static HashMap filedata=new HashMap();
    public static boolean debug=true;
    Config config=new Config();

    public workflow_image() {
        //-- Load PImage for each PNG in dataPath
        //-- ONLY if not used in library mode...
        if (filedata.isEmpty()&&!Config.library_mode) {
              String[] imageName=loadImageslisting(config.get("imagePath"));
              if (imageName!=null)  
              for (int i=0; i<imageName.length;i++) {
                PImage tmp=loadImage(config.get("imagePath")+File.separator+imageName[i]);
                filedata.put(imageName[i], tmp);
              }
              if (debug) Config.log("Loaded "+filedata.size()+" images for workflow");
        }
    }

    public PImage get(String key) {
        return (PImage) filedata.get(key);
    }

    /**
 * Return a string array of the PNG in the specify directory or null if not found
 */
public String[] loadImageslisting (String path) {
  FilenameFilter filter=new FilenameFilter() {
  public boolean accept(File dir, String name) {
  if (name.charAt(0) == '.') return false;
  if (name.toLowerCase().endsWith(".png")) return true;
  return false;
  }
  };
  File dataFolder = new File(path);
  String[] names = dataFolder.list(filter);
  return names;
}

}

