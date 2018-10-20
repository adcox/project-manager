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

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
/**
 *
 * @author Andrew
 * @version Jul 26, 2011
 */
public class NewReleaseDialog extends JDialog{
    
    private JButton cancel, newMajor, newMinor;
    private JPanel panel = new JPanel();
    
    public static enum ReturnType{major, minor, cancel};
    private ReturnType buttonPressed = ReturnType.cancel;
    
    public NewReleaseDialog(Component parent){
        setModal(true);
        setTitle("Add a Release...");
        setLocation(parent.getWidth()/2, parent.getHeight()/2);
        
        cancel = new JButton("Cancel");
        cancel.setPreferredSize(new Dimension(100, 30));
        cancel.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt){
                buttonPressed = ReturnType.cancel;
                dispose();
            }
        });
        cancel.setAlignmentX(CENTER_ALIGNMENT);
        
        newMajor = new JButton("Add a Major Release");
        newMajor.setPreferredSize(new Dimension(200, 30));
        newMajor.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt){
                buttonPressed = ReturnType.major;
                dispose();
            }
        });
        newMajor.setAlignmentX(CENTER_ALIGNMENT);
        
        newMinor = new JButton("Add a Minor Release");
        newMinor.setPreferredSize(new Dimension(200, 30));
        newMinor.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt){
                buttonPressed = ReturnType.minor;
                dispose();
            }
        });
        newMinor.setAlignmentX(CENTER_ALIGNMENT);
        
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        panel.add(newMajor);
        panel.add(newMinor);
        panel.add(cancel);
        
        add(panel, BorderLayout.CENTER);
        
        pack();
        setVisible(true);
    }//====================================
    
    public boolean pressedCancel(){return buttonPressed.equals(ReturnType.cancel);}
    public boolean pressedMajor(){return buttonPressed.equals(ReturnType.major);}
    public boolean pressedMinor(){return buttonPressed.equals(ReturnType.minor);}
}
