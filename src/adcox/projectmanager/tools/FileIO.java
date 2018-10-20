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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Andrew
 * @version Jul 5, 2011
 */
public abstract class FileIO {
    
    public static ArrayList<String> readFile(String filePath){
        try{
            FileReader in = new FileReader(filePath);
            BufferedReader buff = new BufferedReader(in);

            ArrayList<String> lines = new ArrayList<String>();
            String temp = new String();

            int length = 0;
            while((temp = buff.readLine()) != null){
                length++;
                lines.add(temp);
            }

            in.close();
            return lines;

        }
        catch (IOException e){
            System.err.println("Unable to read from file" + '\n' + e.toString());
//            JOptionPane.showMessageDialog(null, "Unable to open the file.\n" + e,
//                    "Oops...", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }//===================================

    public static void writeToFile(String filePath, String strToWrite){
        BufferedWriter out;

        try {
        out = new BufferedWriter(new FileWriter(filePath, true));

        out.write(strToWrite);//Write out a string to the file

        out.newLine();//write a new line to the file so the next time you write to the file it does it on the next line

        out.close();//flushes and closes the stream
        }catch(IOException e){
            System.err.println("There was a problem:" + e);
//            JOptionPane.showMessageDialog(null, "Unable to write the file.\n" + e,
//                    "Oops...", JOptionPane.ERROR_MESSAGE);
        }
    }//===========================================
    
    public static void deleteFile(String fileName) {
        try {
            // Construct a File object for the file to be deleted.
            File target = new File(fileName);

            if (!target.exists()) {
                System.err.println("File " + fileName
                    + " not present to begin with!");
                return;
            }

            // Quick, now, delete it immediately:
            if (target.delete())
                System.err.println("** Deleted " + fileName + " **");
            else
                System.err.println("Failed to delete " + fileName);
            
         } catch (SecurityException e) {
            System.err.println("Unable to delete " + fileName + "("
                + e.getMessage() + ")");
        }
    }//====================================
    
    public static void mkdir(String filePath){
        //check to see if the file tree exists (it should if the program has been opened here before
        try{
            File rootDir = new File(filePath);
            if(!rootDir.exists()){
                //create it
                rootDir.mkdir();
                System.out.println("Making directory: " + filePath);
            }
        }
        catch(Exception e){
            System.err.println("Could not create root directory, /Projects");
        }
    }//==============================
    
    /**
     * Renames a file or directory. The Strings oldName and newName will need to
     * filePaths if the file is not in the root directory
     * @param oldName
     * @param newName 
     */
    public static void renameFile(String oldName, String newName){
        // File (or directory) with old name
        File file = new File(oldName);

        // File (or directory) with new name
        File file2 = new File(newName);

        // Rename file (or directory)
        boolean success = file.renameTo(file2);
        if (!success) {
            // File was not successfully renamed
            System.err.println("Could not rename the file " + oldName);
        }
    }//===================================
}
