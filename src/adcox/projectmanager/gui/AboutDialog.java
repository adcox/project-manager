/**
 *  Project Manager: A program that assists you in managing your programming
 *  projects.
 * 
 *  Copyright (C) 2011, 2012 Andrew Cox
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

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author Andrew
 * @version Jul 21, 2011
 * 
 * This is the About Project Manager Window
 */
public class AboutDialog extends JDialog{
    
    public static int LEFT_MARGIN = 5;
    public static int TOP_MARGIN = 5;
    public static int COMP_MARGIN = 7;
    
    private Container contentPane;
    private JLabel icon, name, version, copyright, rights;
    
    public AboutDialog(Frame parent, String name){
        super(parent, name);
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setModal(true);
        
//        setBackground(new Color(0,0,0,0));  //make the background transparent
//        setContentPane(contentPane = new TransparentContentPane());
        
        contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
        
        initComponents();
        
        setLocationRelativeTo(parent);
        pack();
        setVisible(true);
    }//=========================================
    
    private void initComponents(){
        //initialize componenets here
        ImageIcon imageIcon = new ImageIcon(ProjectManager.utilities.getImage());
        imageIcon.setImage(ProjectManager.utilities.getImage().getScaledInstance(128, -1, Image.SCALE_AREA_AVERAGING));
        icon = new JLabel(imageIcon);
        icon.setPreferredSize(new Dimension(128, 128));
        icon.setAlignmentX(CENTER_ALIGNMENT);
        
        name = new JLabel("Project Manager");
        name.setFont(new Font(name.getFont().getName(), Font.BOLD, 18));
        name.setPreferredSize(new Dimension(100, 30));
        name.setAlignmentX(CENTER_ALIGNMENT);
        //name.setForeground(Color.white);
        
        version = new JLabel("Version " + ProjectManager.VERSION.toString());
        version.setPreferredSize(new Dimension(100, 30));
        version.setAlignmentX(CENTER_ALIGNMENT);
        //version.setForeground(Color.white);
        
        copyright = new JLabel("Copyright 2011, Andrew Cox");
        copyright.setPreferredSize(new Dimension(250, 30));
        copyright.setAlignmentX(CENTER_ALIGNMENT);
        //copyright.setForeground(Color.white);
        
        rights = new JLabel("All Rights Reserved");
        rights.setPreferredSize(new Dimension(250, 30));
        rights.setAlignmentX(CENTER_ALIGNMENT);
        //rights.setForeground(Color.white);
        
        contentPane.add(icon);
        contentPane.add(Box.createRigidArea(new Dimension(0, COMP_MARGIN)));
        contentPane.add(name);
        contentPane.add(Box.createRigidArea(new Dimension(0, COMP_MARGIN)));
        contentPane.add(version);
        contentPane.add(Box.createRigidArea(new Dimension(0, COMP_MARGIN)));
        contentPane.add(copyright);
        contentPane.add(Box.createRigidArea(new Dimension(0, COMP_MARGIN)));
        contentPane.add(rights);
    }//========================================
    
}
