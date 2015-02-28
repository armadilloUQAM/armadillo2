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

package splash;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 *
 *
 */
public class SplashWindow extends JWindow {

    Image image=null;
	String _MessageString="";
	private int count=0;


    public SplashWindow() {
		String defaultSplashPath="\\data\\images\\";
        String currentPath=new File("").getAbsolutePath();
        //_MessageString="";
        String filename=currentPath+defaultSplashPath+"splash1.png";
        File tmp=new File(filename);
            try {
                ImageIcon splashIcon = new ImageIcon(tmp.toURI().toURL());
                image = splashIcon.getImage();
            } catch(Exception e) {System.err.println("Warning. Unable to load "+filename);}
		 
        setSize(image.getWidth(null), image.getHeight(null));
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension d = getSize();
        setLocation((screenSize.width-d.width)/2,
					(screenSize.height-d.height)/2);
        setVisible(true);        
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Dimension size = getSize();
        g.setClip(0,0,size.width,size.height);
        g.drawImage(image,0,0,null);
        g.setColor(new Color(0,0,0));
        g.setFont(new java.awt.Font(Font.MONOSPACED, Font.PLAIN, 9));
        g.drawString(_MessageString, 5, 10);
        g.drawRect(0,0,size.width-1,size.height-1);
    }

	public void msg(String str) {
		_MessageString = str;
		super.repaint(200);
	}

}

