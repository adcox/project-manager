/**
 *  Project Manager: A program that assists you in managing your programming
 *  projects.
 * 
 *  Copyright (C) 2011 Andrew Cox
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
 *
 *  To contact me, send an email to <andrewandstuff@gmail.com>
 */
package adcox.projectmanager.objects;

import adcox.projectmanager.gui.ProjectManager;
import java.util.*;

import adcox.projectmanager.tools.FileIO;

/**
 *
 * @author Andrew
 * @version Jul 23, 2011
 */
public class MajorRelease {
    private String name, dateOpened, filePath;
    private Version version;
    private ArrayList<MinorRelease> minors = new ArrayList<MinorRelease>();
    
    public MajorRelease(String name, String filePath){
        this.name = name;
        this.filePath = filePath;
        
        version = new Version();
        dateOpened = ProjectManager.getDate();
        System.out.println("Major Release Home Directory: " + filePath);
    }//=================================
    
    public ArrayList<MinorRelease> getMinors(){return minors;}
    public String getName(){return name;}
    public String getFilepath(){return filePath;}
    
    public void addRelease(MinorRelease mm){
        minors.add(mm);
    }//=============================================
    
    @Override
    public String toString(){return name;}
    
    public void saveRelease(){
        FileIO.mkdir(filePath);
    }//==================================
}
