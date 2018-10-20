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

import java.io.*;
import java.util.*;

import adcox.projectmanager.gui.ProjectManager;
import adcox.projectmanager.tools.FileIO;

/**
 *
 * @author Andrew
 * @version Jul 23, 2011
 */
public class MinorRelease {
    //my information
    private ArrayList<Issue> solvedIssues = new ArrayList<Issue>();
    private ArrayList<Issue> unsolvedIssues = new ArrayList<Issue>();
    private String name, dateOpened, filePath;
    private Version version;
    private int numBugs, numFeatures, numUpdates;
    
    public static String REGEX = ProjectManager.REGEX;
    
    public MinorRelease(String projectName, String filePath){
        name = projectName;
        this.filePath = filePath;
        
        System.out.println("Minor Release home directory: " + filePath);
    }//==================================
    
    public void rename(String newName){
        String path = new String();
        //take apart the homeDirectory string to get it without the filename
//        String[] parsedPath = homeDirectory.split("/");
//        for(int i = 0; i < parsedPath.length - 1; i++){
//            path = path.concat(parsedPath[i] + "/");
//        }
//        System.out.println("Path = " + path);
//        FileIO.renameFile(homeDirectory, path.concat(newName));
        
        name = newName;
        //homeDirectory = path.concat(newName);
    }//========================================
    
    public ArrayList<Issue> getUnsolvedIssues(){return unsolvedIssues;}
    public ArrayList<Issue> getSolvedIssues(){return solvedIssues;}
    public String getName(){return name;}
    
    public Version getVersion(){
        calculateVersion();
        return version;
    }//===========================================
    
    public String getDateStarted(){return dateOpened;}
    public void addToSolvedIssues(Issue iss){solvedIssues.add(iss);}
    public void addToUnsolvedIssues(Issue iss){unsolvedIssues.add(iss);}
    
    public void solveIssue(String ID){
        Issue iss = null;
        //find the issue in the unsolved list
        for(int i = 0; i < unsolvedIssues.size(); i++){
            if(unsolvedIssues.get(i).getID().equals(ID)){
                iss = unsolvedIssues.get(i);
            }
        }
        
        if(iss != null){
            //move the issue to the solved list and remove from unsolved list
            solvedIssues.add(iss);
            unsolvedIssues.remove(iss);
        }
    }//======================================
    
    public void unsolveIssue(String ID){
        Issue iss = null;
        //find the issue in the solved list
        for(int i = 0; i < solvedIssues.size(); i++){
            if(solvedIssues.get(i).getID().equals(ID)){
                iss = solvedIssues.get(i);
            }
        }
        
        if(iss != null){
            //move the issue to the unsolved list and remove from solved list
            unsolvedIssues.add(iss);
            solvedIssues.remove(iss);
        }
    }//========================================
    
    public Issue getSolvedIssue(String ID){
        for(int i = 0; i < solvedIssues.size(); i++){
            if(solvedIssues.get(i).getID().equals(ID))
                return solvedIssues.get(i);
        }
        
        return null;
    }//=====================================
    
    public Issue getUnsolvedIssue(String ID){
        for(int i = 0; i < unsolvedIssues.size(); i++){
            if(unsolvedIssues.get(i).getID().equals(ID))
                return unsolvedIssues.get(i);
        }
        
        return null;
    }//=====================================
    
    public void addIssue(Issue iss){
        if(iss.isSolved())
            solvedIssues.add(iss);
        else
            unsolvedIssues.add(iss);
        
        if(iss.getType().equals(Issue.IssueType.Bug))
            numBugs++;
        if(iss.getType().equals(Issue.IssueType.Feature))
            numFeatures++;
        if(iss.getType().equals(Issue.IssueType.Update))
            numUpdates++;
    }//=========================================
    
    public int getNumBugs(){return numBugs;}
    public int getNumFeatures(){return numFeatures;}
    public int getNumUpdates(){return numUpdates;}
    
    public void loadIssues(){
        //load issues
        File[] dir = new File(filePath).listFiles();

        //go through all the files in dir and load them
        for(int i = 0; i < dir.length; i++){
            if(!dir[i].isHidden() && dir[i].canWrite()){
                try{
                    Issue issue = Issue.openIssue(dir[i].getCanonicalPath()); 

                    if(issue.isSolved()){
                        solvedIssues.add(issue);
                    }
                    else{
                        unsolvedIssues.add(issue);
                    }
                }
                catch(Exception e){
                    System.err.println("Could not load issues.");
                    e.printStackTrace();
                }
            }
        }
    }//=====================================
    
    public void saveIssues(){
        FileIO.mkdir(filePath);
        
        //next save all issues
        for(int i = 0; i < solvedIssues.size(); i++){
            solvedIssues.get(i).saveIssue(filePath);
        }
        for(int i = 0; i < unsolvedIssues.size(); i++){
            unsolvedIssues.get(i).saveIssue(filePath);
        }
    }//=========================================
    
    public void deleteUnsolvedIssues(){
        for(int i = 0; i < unsolvedIssues.size(); i++){
            String path = filePath + "/" + unsolvedIssues.get(i).getID() + ".txt";
            FileIO.deleteFile(path);
        }
        unsolvedIssues.clear();
    }//==============================================
    
    private void calculateVersion(){
        Version v = new Version();
        for(int i = 0; i < solvedIssues.size(); i++){
            if(solvedIssues.get(i).getPriority().equals(Issue.IssuePriority.Tiny))
                v.incrementBuild(1);
            if(solvedIssues.get(i).getPriority().equals(Issue.IssuePriority.Small))
                v.incrementBuild(10);
            if(solvedIssues.get(i).getPriority().equals(Issue.IssuePriority.Medium))
                v.incrementRevision(1);
            if(solvedIssues.get(i).getPriority().equals(Issue.IssuePriority.Large))
                v.incrementRevision(10);
            if(solvedIssues.get(i).getPriority().equals(Issue.IssuePriority.ShowStopper))
                v.incrementRevision(20);
        }
        
        version = v;
    }//======================================
    
    @Override
    public String toString(){return name;}
}
