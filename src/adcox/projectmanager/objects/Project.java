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
import java.io.*;

import adcox.projectmanager.tools.FileIO;

/**
 *
 * @author Andrew
 * @version Jul 21, 2011
 */
public class Project {
    //my information
    private String name, dateOpened;
    private Version version;
    private String homeDirectory;     //the directory the project folder lives in
    private ArrayList<String> workHistory;
    private ArrayList<MajorRelease> majors;
    private int numBugs, numFeatures, numUpdates;
    private boolean isMain = false;
    private int index = -1;
    
    private ArrayList<Issue> unsolvedIssues = new ArrayList<Issue>();
    
    public static String REGEX = ProjectManager.REGEX;
    
    public Project(File homeDirectory, String projectName, boolean newProject, int index){
        name = projectName;
        this.index = index;
        workHistory = new ArrayList<String>();
        majors = new ArrayList<MajorRelease>();
        version = new Version();
        
        try{
            this.homeDirectory = homeDirectory.getCanonicalPath();
            System.out.println("Project home directory: " + this.homeDirectory);
        }
        catch(Exception e){
            System.err.println("Could not find home directory...");
            System.err.println(e.getStackTrace());
        }
        
        if(newProject){
            //make first major release
            addRelease(new MajorRelease("0", this.homeDirectory + "/_0"));
            //create a directory for this project, and sub folders it will need
            FileIO.mkdir(this.homeDirectory);
            FileIO.mkdir(this.homeDirectory + "/_" + majors.get(0).getName());
            FileIO.mkdir(this.homeDirectory + "/_" + majors.get(0).getName() + 
                    "/_" + majors.get(0).getMinors().get(0).getName());
            FileIO.mkdir(this.homeDirectory + "/managedproject");
            FileIO.writeToFile(this.homeDirectory + "/managedproject/project.txt", ProjectManager.getDate());
            this.version = new Version();
            dateOpened = ProjectManager.getDate();
            FileIO.writeToFile(this.homeDirectory + "/managedproject/work.txt", "");
        }
        else{
            loadProject();
        }
    }//==================================
    
    private void loadProject(){
        //read in the data from the project file
        try{
            ArrayList<String> info = FileIO.readFile(homeDirectory + "/managedproject/project.txt");
            //the first line contains info about the version and date project was started
            String[] parsedInfo = info.get(0).split(REGEX);
            dateOpened = parsedInfo[0];
            countIssues();
        }
        catch(Exception e){
            System.err.println("Could not load project info.");
            System.err.println(e.getStackTrace());
            countIssues();  //try to get this information anyway...
        }
        
        //load the work history
        try{
            workHistory = FileIO.readFile(homeDirectory + "/managedproject/work.txt");
        }
        catch(Exception e){
            System.err.println("Could not load work history.");
            System.err.println(e.getStackTrace());
        }
        //make sure workHistory isn't null
        if(workHistory == null){
            workHistory = new ArrayList<String>();
        }
        
        //load releases and issues
        try{
            File[] majorDir = new File(homeDirectory).listFiles();
            for(int d = 0; d < majorDir.length; d++){
                if(majorDir[d].isDirectory() && majorDir[d].getName().startsWith("_")){
                    //this directory is a major revision directory
                    String majorName = majorDir[d].getName().substring(1);
                    String path = "";
                    try{
                        path = majorDir[d].getCanonicalPath();
                    }
                    catch(Exception e){
                        System.err.println(e.getStackTrace());
                    }
                    
                    MajorRelease majorRelease = new MajorRelease(majorName, path);
                    
                    //find the minor releases in the majorRelease
                    File[] minorDir = majorDir[d].listFiles();
                    for(int md = 0; md < minorDir.length; md++){
                        if(minorDir[md].isDirectory() && minorDir[md].getName().startsWith("_")){
                            
                            //this directory is a major revision directory
                            String minorName = minorDir[md].getName().substring(1);

                            try{
                                String path2 = minorDir[md].getCanonicalPath();
                                MinorRelease minorRelease = new MinorRelease(minorName, path2);
                                
                                //tell the minorRelease to load it's issues
                                minorRelease.loadIssues();
                                
                                //add the minor release to the major
                                majorRelease.addRelease(minorRelease);
                            }
                            catch(Exception e){
                                System.err.println(e.getStackTrace());
                            }
                        }
                    }
                    
                    //add the major release to the project
                    majors.add(majorRelease);
                }
            }
        }
        catch(Exception e){
            System.err.println("Could not open issue directory.");
            System.err.println(e.getStackTrace());
        }
    }//==================================
    
    public void saveProject(String newWorkHistoryEntry){
        countIssues();  //reset the numbers and count to make sure they are right
        if(newWorkHistoryEntry != null){
            //look to see if there is an entry for today in the history
            for(int i = 0; i < workHistory.size(); i++){
                if(workHistory.get(i).contains(newWorkHistoryEntry.split(REGEX)[0])){
                    workHistory.remove(i);  //if there is one for today, delete it
                    break;
                }
            }
            workHistory.add(newWorkHistoryEntry);   //save today's work
        }
        try{
            //first, save the work history
            FileIO.deleteFile(homeDirectory + "/managedproject/work.txt");   //we need to overwrite...
            for(int i = 0; i < workHistory.size(); i++){
                FileIO.writeToFile(homeDirectory + "/managedproject/work.txt", workHistory.get(i));
            }
        }
        catch(Exception e){
            System.err.println("Could not save work history.");
            System.err.println(e.getStackTrace());
        }
        
        try{
            //go through all major and minor releases...
            for(int maj = 0; maj < majors.size(); maj++){
                majors.get(maj).saveRelease();
                for(int min = 0; min < majors.get(maj).getMinors().size(); min++){
                    //tell the minor release to save it's issues
                    majors.get(maj).getMinors().get(min).saveIssues();
                }
            }
        }
        catch(Exception e){
            System.err.println("Could not save issues.");
            System.err.println(e.getStackTrace());
        }
        try{
            String general = dateOpened;
            FileIO.deleteFile(homeDirectory + "/managedproject/project.txt");
            FileIO.writeToFile(homeDirectory + "/managedproject/project.txt", general);
        }
        catch(Exception e){
            System.err.println("Could not load project info.");
            System.err.println(e.getStackTrace());
        }
    }//================================
    
    public void rename(String newName){
        String path = new String();
        //take apart the homeDirectory string to get it without the filename
        String[] parsedPath = homeDirectory.split("/");
        for(int i = 0; i < parsedPath.length - 1; i++){
            path = path.concat(parsedPath[i] + "/");
        }
        System.out.println("Path = " + path);
        FileIO.renameFile(homeDirectory, path.concat(newName));
        
        name = newName;
        homeDirectory = path.concat(newName);
    }//========================================
    
    public void setIsMain(Boolean b){isMain = b;}
    public void setIndex(int i){index = i;}
    
    public void addRelease(MajorRelease m){
        majors.add(m);
        m.addRelease(new MinorRelease("0", homeDirectory + "/_" + m.getName() + "/_0"));
    }//=========================
    public void addToUnsolvedIssues(Issue iss){unsolvedIssues.add(iss);}
    
    public ArrayList<MajorRelease> getMajors(){return majors;}
    public ArrayList<String> getWorkHistory(){return workHistory;}
    public String getName(){return name;}
    public int getNumBugs(){return numBugs;}
    public int getNumFeatures(){return numFeatures;}
    public int getNumUpdates(){return numUpdates;}
    public boolean isMain(){return isMain;}
    public int getIndex(){return index;}
    public String getDirectory(){return homeDirectory;}
    public ArrayList<Issue> getUnsolvedIssues(){return unsolvedIssues;}
    
    
    public Version getVersion(){
        calculateVersion();
        return version;
    }//===========================================
    
    public String getDateStarted(){return dateOpened;}
    
    public int[] getTimeWorked(){
        int hours = 0, minutes = 0, seconds = 0;
        for(int i = 0; i < workHistory.size(); i++){
            //split the data: the first part is the date, the second part is the time
            
            String[] data = workHistory.get(i).split(REGEX);
            if(data.length < 2){
                System.err.println("The work history data doesn't have enough information.");
                //return null;
            }
            else{
                String[] time = data[1].split(":");

                if(time.length != 3){
                    System.err.println("The time Data doesn't have three parts.");
                    return null;
                }
                else{
                    hours += Integer.parseInt(time[0]);
                    minutes += Integer.parseInt(time[1]);
                    seconds += Integer.parseInt(time[2]);
                }
            }
        }
        
        //reduce the time so they fit within 60 min/sec constraints
        
        while(seconds >= 60){
            minutes += 1;
            seconds -= 60;
        }
        
        while(minutes >= 60){
            hours += 1;
            minutes -= 60;
        }
        
        int[] totalTime = {hours, minutes, seconds};
        return totalTime;
    }//============================================
    
    private void calculateVersion(){
        int lastMajor = majors.size() - 1;
        int lastMinor = majors.get(lastMajor).getMinors().size() - 1;
        int revision = majors.get(lastMajor).getMinors().get(lastMinor).getVersion().getRevision();
        int build = majors.get(lastMajor).getMinors().get(lastMinor).getVersion().getBuild();
        
        version = new Version(lastMajor, lastMinor, revision, build);
    }//======================================
    
    public void countIssues(){
        //go through all the issues and count them so that the counts end up correctly
        numBugs = 0;
        numFeatures = 0;
        numUpdates = 0;
        
        ArrayList<Issue> issues = new ArrayList<Issue>();
        for(int maj = 0; maj < majors.size(); maj++){
            for(int min = 0; min < majors.get(maj).getMinors().size(); min++){
                issues.addAll(majors.get(maj).getMinors().get(min).getSolvedIssues());
                issues.addAll(majors.get(maj).getMinors().get(min).getUnsolvedIssues());
            }
        }
        
        for(int i = 0; i < issues.size(); i++){
            if(issues.get(i).getType().equals(Issue.IssueType.Bug))
                numBugs++;
            if(issues.get(i).getType().equals(Issue.IssueType.Feature))
                numFeatures++;
            if(issues.get(i).getType().equals(Issue.IssueType.Update))
                numUpdates++;
        }
    }//================================================
    
    @Override
    public String toString(){return name;}
    
}
