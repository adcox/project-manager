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

import javax.swing.JOptionPane;

/**
 *
 * @author Andrew
 * @version Jul 21, 2011
 */
public class Version {
    
    private int major, minor, revision, build;
    
    public Version(){
        major = 0;
        minor = 0;
        revision = 0;
        build = 0;
    }//====================================
    
    public Version(int maj, int min, int rev, int build){
        major = maj;
        minor = min;
        revision = rev;
        this.build = build;
    }//==================================
    
    public static Version fromString(String s){
        //parse a version from a string
        String[] parsed = s.split(".");
        int maj = 0, min = 0, rev = 0, bui = 0;
        try{
            maj = Integer.parseInt(parsed[0]);
            min = Integer.parseInt(parsed[1]);
            rev = Integer.parseInt(parsed[2]);
            bui = Integer.parseInt(parsed[3]);
        }
        catch(Exception e){
            System.err.println("Could not fully parse the version from string.");
            e.printStackTrace();
        }
        
        return new Version(maj, min, rev, bui);
    }//===================================
    
    public String toString(){
        return String.format("%d.%d.%d.%d", major, minor, revision, build);
    }//========================================
    
    public void incrementBuild(int howMuch){
        build += howMuch;
        if(build >= 100){
            revision++;
            build -= 100;
        }
    }//======================================
    
    public void incrementRevision(int howMuch){
        revision += howMuch;
        if(revision >= 100){
            JOptionPane.showMessageDialog(null, "You are ready to release a new MINOR VERSION!!!!!!!!");
        }
    }//======================================
    
    public void incrementMinor(int howMuch){
        minor += howMuch;
        if(minor >= 100){
            JOptionPane.showMessageDialog(null, "You are ready to release a new MAJOR VERSION!!!!!!!");
        }
    }//======================================
    
    public void incrementMajor(int howMuch){
        major += howMuch;
        if(major >= 100){
            JOptionPane.showMessageDialog(null, "Your version number is a little high...");
        }
    }//======================================
    
    public int getMajor(){return major;}
    public int getMinor(){return minor;}
    public int getRevision(){return revision;}
    public int getBuild(){return build;}
    
}
