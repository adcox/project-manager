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
package adcox.projectmanager.gui;

import adcox.projectmanager.objects.Project;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author Andrew
 * @version Jul 21, 2011
 * 
 * This dialog displays information about the project
 */
public class ProjectInfoPane extends JDialog{
    
    public static int LEFT_MARGIN = 10;
    public static int TOP_MARGIN = 10;
    public static int COMP_MARGIN = 7;
    
    private JLabel icon, name, version, dateStarted, totalTime;
    private Container contentPane;
    private Project project;
    
    public ProjectInfoPane(Project p){
        //super(null, p.getName());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        setModal(true);
        setResizable(false);
        setTitle(p.getName());
        setLocation(300, 150);
        
        project = p;
        contentPane = getContentPane();
        
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
//        panel.setOpaque(false);
//        panel.setBackground(new Color(0,0,0,0));
        
        initComponents();
        
        pack();
        setVisible(true);
    }//=========================================
    
    private void initComponents(){
        ImageIcon imageIcon = new ImageIcon(ProjectManager.utilities.getImage());
        icon = new JLabel(imageIcon);
        icon.setPreferredSize(new Dimension(64, 64));
        icon.setAlignmentX(CENTER_ALIGNMENT);
        
        name = new JLabel(project.getName());
        name.setFont(name.getFont().deriveFont(32));
        name.setPreferredSize(new Dimension(200, 40));
        name.setAlignmentX(Component.CENTER_ALIGNMENT);
        //name.setForeground(Color.white);
        
        version = new JLabel(project.getVersion().toString());
        version.setPreferredSize(new Dimension(200, 30));
        version.setAlignmentX(CENTER_ALIGNMENT);
        //version.setForeground(Color.white);
        
        dateStarted = new JLabel("Created on " + project.getDateStarted());
        dateStarted.setPreferredSize(new Dimension(200, 30));
        dateStarted.setAlignmentX(CENTER_ALIGNMENT);
        //dateStarted.setForeground(Color.white);
        
        int[] timeArray = project.getTimeWorked();
        
        String timeString = "";
        if(timeArray != null){
            timeString = String.valueOf(timeArray[0]) + ":" + 
                String.valueOf(timeArray[1]) + ":" + String.valueOf(timeArray[2]);
        }else{
            timeString = "ERROR";
        }
        
        totalTime = new JLabel("Time Worked: " + timeString);
        totalTime.setPreferredSize(new Dimension(200, 30));
        totalTime.setAlignmentX(CENTER_ALIGNMENT);
        
        contentPane.add(icon, Component.CENTER_ALIGNMENT);
        contentPane.add(Box.createRigidArea(new Dimension(0, COMP_MARGIN)));
        contentPane.add(name, Component.CENTER_ALIGNMENT);
        contentPane.add(Box.createRigidArea(new Dimension(0, COMP_MARGIN)));
        contentPane.add(version, Component.CENTER_ALIGNMENT);
        contentPane.add(Box.createRigidArea(new Dimension(0, COMP_MARGIN)));
        contentPane.add(dateStarted, Component.CENTER_ALIGNMENT);
        contentPane.add(Box.createRigidArea(new Dimension(0, COMP_MARGIN)));
        contentPane.add(totalTime, Component.CENTER_ALIGNMENT);
    }//========================================
    
}
