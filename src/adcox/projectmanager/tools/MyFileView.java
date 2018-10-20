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
package adcox.projectmanager.tools;

import adcox.projectmanager.gui.ProjectManager;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;

/**
 *
 * @author Andrew
 * @version Jul 9, 2011
 * 
 * This class customizes the file view so that project folders show up differently
 */
public class MyFileView extends FileView{
    
    public String getName(File f) {
        return null; //let the L&F FileView figure this out
    }

    public String getDescription(File f) {
        return null; //let the L&F FileView figure this out
    }

    public Boolean isTraversable(File f) {
        return null; //let the L&F FileView figure this out
    }

    public String getTypeDescription(File f) {
        String type = null;

        //check to see if f is a directory containing the right folders to be a project
        File[] subFiles = f.listFiles();
            
        if(subFiles != null){
            for(int i = 0; i < subFiles.length; i++){
                if(subFiles[i].getName().equals("managedproject")){
                    //this IS a project folder, so open it
                    type = "Project Folder";
                }
            }
        }
        return type;
    }//=====================================

    public Icon getIcon(File f) {
        Icon icon = null;

        //check to see if f is a directory containing the right folders to be a project
        File[] subFiles = f.listFiles();
            
        if(subFiles != null){
            for(int i = 0; i < subFiles.length; i++){
                if(subFiles[i].getName().equals("managedproject")){
                    //this IS a project folder, so open it
                    icon = ProjectManager.project;
                }
            }
        }
        return icon;
    }//==================================
}
