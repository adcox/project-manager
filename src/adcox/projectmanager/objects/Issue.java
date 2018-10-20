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

import adcox.projectmanager.gui.*;
import adcox.projectmanager.tools.FileIO;
import java.io.File;
import java.util.ArrayList;

import javax.swing.ImageIcon;

/**
 *
 * @author Andrew
 * @version Jul 13, 2011
 */
public class Issue {
    
    public static enum IssueType{Bug, Feature, Update};
    IssueType type;
    
    public static enum IssuePriority{Tiny, Small, Medium, Large, ShowStopper};
    IssuePriority priority;
    
    //general info
    private String description, name, ID;
    
    //unsolved info
    private String dateOpened, problemNotes;
    private String dateClosed, solutionNotes;
    
    private ImageIcon icon;
    private boolean solved = false;
    
    public static final String REGEX = "##";
    public static final String SUPER_REGEX = "@@";
    public static final String OPEN_MESSAGE = "stillopen";

//******************************************************************************    
//*****Constructors*************************************************************
    
    public Issue(String type, String ID, String name, String description, String dateOpened, 
            String priority){
        this.type = stringToType(type);
        this.ID = ID;
        this.name = name;
        this.description = description;
        this.dateOpened = dateOpened;
        this.priority = stringToPriority(priority);
        dateClosed = OPEN_MESSAGE;
        solutionNotes = OPEN_MESSAGE;
    }//=============================================
    
    public Issue(String ID){
        this.ID = ID;
        description = "";
        name = "";
        dateOpened = "";
        problemNotes = "";
        type = IssueType.Bug;
        priority = IssuePriority.Tiny;
        dateClosed = OPEN_MESSAGE;
        solutionNotes = OPEN_MESSAGE;
    }//=============================================
    
    
//******************************************************************************    
//*****Set and Gets*************************************************************
    
    public String getName(){return name;}
    public String getDescription(){return description;}
    public String getID(){return ID;}
    public String getDateOpened(){return dateOpened;}
    public String getDateClosed(){return dateClosed;}
    public String getProblemNotes(){return problemNotes;}
    public String getSolutionNotes(){return solutionNotes;}
    public boolean isSolved(){return solved;}
    public IssueType getType(){return type;}
    public IssuePriority getPriority(){return priority;}
    
    public void setName(String n){name = n;}
    public void setDescription(String d){description = d;}
    public void setID(String id){ID = id;}
    public void setDateOpened(String date){dateOpened = date;}
    public void setDateClosed(String date){dateClosed = date;}
    public void setProblemNotes(String s){problemNotes = s;}
    public void setSolutionNotes(String s){solutionNotes = s;}
    public void setSolved(boolean b){solved = b;}
    public void setType(IssueType it){type = it;}
    public void setPriority(IssuePriority ip){priority = ip;}
    
    public Object[] getIssueAsRow(boolean asSolved){
        Object[] data = {typeToIcon(type), ID, dateOpened, name, description, priorityToString(priority)};
        
        if(asSolved)
            data[2] = dateClosed;
        
        return data;
    }//============================================ 
    
//******************************************************************************    
//*****Utilities****************************************************************
    
    public ImageIcon typeToIcon(IssueType it){
        if(it == IssueType.Bug)
            return ProjectManager.bug;
        if(it == IssueType.Feature)
            return ProjectManager.lightbulb;
        if(it == IssueType.Update)
            return ProjectManager.clock;
        
        //default, return bug
        return ProjectManager.bug;
    }//===================================================
    
    public IssueType stringToType(String s){
        if(s.equals("Bug"))
            return IssueType.Bug;
        if(s.equals("Feature"))
            return IssueType.Feature;
        else
            return IssueType.Update;
    }//===================================================
    
    public static String priorityToString(IssuePriority ip){
        if(ip == IssuePriority.Tiny)
            return "Tiny";
        if(ip == IssuePriority.Small)
            return "Small";
        if(ip == IssuePriority.Medium)
            return "Medium";
        if(ip == IssuePriority.Large)
            return "Large";
        if(ip == IssuePriority.ShowStopper)
            return "Show-Stopper";
        
        return "Could not get Priority";
    }//====================================
    
    public static IssuePriority stringToPriority(String s){
        if(s.equals(priorityToString(IssuePriority.Tiny)))
            return IssuePriority.Tiny;
        if(s.equals(priorityToString(IssuePriority.Small)))
            return IssuePriority.Small;
        if(s.equals(priorityToString(IssuePriority.Medium)))
            return IssuePriority.Medium;
        if(s.equals(priorityToString(IssuePriority.Large)))
            return IssuePriority.Large;
        
        return IssuePriority.ShowStopper;
    }//=========================================
    
    public void saveIssue(String filePath){
        String genData = name + REGEX + description + REGEX + priorityToString(priority) + SUPER_REGEX;
        String openData = dateOpened + REGEX + problemNotes + SUPER_REGEX;
        String closeData = dateClosed + REGEX + solutionNotes + SUPER_REGEX;
        
        FileIO.deleteFile(filePath + "/" + ID + ".txt");  //delete the old file
        FileIO.writeToFile(filePath + "/" + ID + ".txt", genData);
        FileIO.writeToFile(filePath + "/" + ID + ".txt", openData);
        if(solved)
            FileIO.writeToFile(filePath + "/" + ID + ".txt", closeData);
    }//============================================
    
    public static Issue openIssue(String filePath){
        ArrayList<String> lines = FileIO.readFile(filePath);
        File f = new File(filePath);
        Issue iss;
        if(lines != null && f != null){
            String id = f.getName().split(".t")[0]; //the ID is the stuff in front of .txt 
            iss = new Issue(id);
            if(id.charAt(0) == 'B')
                iss.setType(IssueType.Bug);
            if(id.charAt(0) == 'F')
                iss.setType(IssueType.Feature);
            if(id.charAt(0) == 'U')
                iss.setType(IssueType.Update);
            
            //concatenate all the lines into one big line
            String data = "";
            for(int i = 0; i < lines.size(); i++){
                data = data.concat(lines.get(i));
            }
            
            //System.out.println("***\nfileData:\n"+data);
            String[] parsedData = data.split(SUPER_REGEX);
            String[] genData = parsedData[0].split(REGEX);
            String[] openData = parsedData[1].split(REGEX);
            String[] closeData = null;
            if(parsedData.length > 2)
                closeData = parsedData[2].split(REGEX);
            
            try{
                iss.setName(genData[0]);
                iss.setDescription(genData[1]);
                iss.setPriority(stringToPriority(genData[2]));
            }
            catch(Exception e){
                System.err.println("There was an error while attempting to load " + 
                        filePath + "\n");
                e.printStackTrace();
            }
            try{
                iss.setDateOpened(openData[0]);
                if(openData.length > 1)
                    iss.setProblemNotes(openData[1]);
                else
                    iss.setProblemNotes("");
            }
            catch(Exception e){
                System.err.println("There was an error while attempting to load " + 
                        filePath + "\n");
                e.printStackTrace();
            }
            try{
                if(closeData != null && closeData.length > 0){
                    if(closeData[0].equals(OPEN_MESSAGE)){
                        iss.setSolved(false);
                    }
                    else{
                        iss.setSolved(true);
                        iss.setDateClosed(closeData[0]);
                        if(closeData.length > 1)
                            iss.setSolutionNotes(closeData[1]);
                        else
                            iss.setSolutionNotes("");
                    }
                }
            }
            catch(Exception e){
                System.err.println("There was an error while attempting to load " + 
                        filePath + "\n");
                e.printStackTrace();
            }
            
        }
        else{
            iss = new Issue("XXXXX");
        }
        
//        System.out.println("Issue loaded:\nName: " + iss.getName() + 
//                "\nDescription: " + iss.getDescription() + 
//                "\nDateOpened: " + iss.getDateOpened() + 
//                "\nProblemNotes: " + iss.getProblemNotes() + 
//                "\nDateClosed: " + iss.getDateClosed() + 
//                "\nSolutionNotes: " + iss.getSolutionNotes());
        
        return iss;
    }//=====================================
}
