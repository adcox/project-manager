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

import adcox.projectmanager.objects.Issue;
import adcox.projectmanager.objects.Issue.IssuePriority;
import adcox.projectmanager.tools.LightScrollPane;
import adcox.projectmanager.tools.TransferFocus;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 *
 * @author Andrew
 * @version Jul 7, 2011
 */
public class AddIssueFrame extends JDialog {
    
    public static int P_WIDTH = 400;
    public static int P_HEIGHT = 300;
    public static int LEFT_MARGIN = 10;
    public static int TOP_MARGIN = 10;
    public static int COMP_MARGIN = 15;
    
    private JLabel priorityLbl, nameLbl, dateLbl, descriptLbl, notesLbl;
    private String additionType;
    private JComboBox priorityCombo;
    private JTextField nameField, dateField;
    private JTextArea descriptionArea, notesArea;
    private LightScrollPane descriptScroller, notesScroller;
    private JButton submit, cancel;
    
    private ProjectManager boss;
    
    // These actions are used to move focus
    public Action nextFocusAction = new AbstractAction("Move Focus Forwards") {
        public void actionPerformed(ActionEvent evt) {
            ((Component)evt.getSource()).transferFocus();
        }
    };
    public Action prevFocusAction = new AbstractAction("Move Focus Backwards") {
        public void actionPerformed(ActionEvent evt) {
            ((Component)evt.getSource()).transferFocusBackward();
        }
    };
    
    public AddIssueFrame(String additionType, String date, String time, ProjectManager boss) {
        setTitle("Add a new " + additionType);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);
        setModal(true);
        setResizable(false);
        
        this.boss = boss;
        this.additionType = additionType;
        
        initComponents(date, time);
        
        setSize(dateField.getX() + dateField.getWidth() + LEFT_MARGIN, 
                cancel.getY() + cancel.getHeight() + 3*TOP_MARGIN);
        setVisible(true);
    }//================================
    
    private void initComponents(String date, String time){
        priorityLbl = new JLabel("Priority:");
        priorityLbl.setLocation(LEFT_MARGIN, TOP_MARGIN);
        priorityLbl.setSize(100, 20);
        
        priorityCombo = new JComboBox();
        priorityCombo.setToolTipText("Priority of Bug/Feature/Update");
        priorityCombo.setModel(new DefaultComboBoxModel(
                new String[] {"--",
                    Issue.priorityToString(IssuePriority.Tiny) + " - Cosmetic, no functional impact.",
                    Issue.priorityToString(IssuePriority.Small) + " - You might notice.",
                    Issue.priorityToString(IssuePriority.Medium) + " - You'll notice, not many changes though.",
                    Issue.priorityToString(IssuePriority.Large) + " - Changes to major aspects.",
                    Issue.priorityToString(IssuePriority.ShowStopper) + " - a MUST fix!"}));
        
        priorityCombo.setSize(250, 30);
        priorityCombo.setLocation(LEFT_MARGIN, TOP_MARGIN + priorityLbl.getHeight());
        add(priorityCombo);
        add(priorityLbl);
        
        nameLbl = new JLabel("Name:");
        nameLbl.setLocation(priorityCombo.getX() + priorityCombo.getWidth() + 
                COMP_MARGIN, TOP_MARGIN);
        nameLbl.setSize(100, 20);
        
        nameField = new JTextField();
        nameField.setToolTipText("Name");
        nameField.setSize(100, 30);
        nameField.setLocation(priorityCombo.getX() + priorityCombo.getWidth() + 
                COMP_MARGIN, TOP_MARGIN + nameLbl.getHeight());
        add(nameField);
        add(nameLbl);
        
        dateLbl = new JLabel("Date & Time:");
        dateLbl.setLocation(nameField.getX() + nameField.getWidth() + COMP_MARGIN,
                TOP_MARGIN);
        dateLbl.setSize(150, 20);
        
        dateField = new JTextField();
        dateField.setText(date + ", " + time);
        dateField.setToolTipText("Date");
        dateField.setSize(175, 30);
        dateField.setLocation(nameField.getX() + nameField.getWidth() + COMP_MARGIN, 
                TOP_MARGIN + dateLbl.getHeight());
        add(dateField);
        add(dateLbl);
        
        descriptLbl = new JLabel("Description:");
        descriptLbl.setLocation(LEFT_MARGIN, nameField.getY() + nameField.getHeight() + COMP_MARGIN);
        descriptLbl.setSize(110, 20);
        
        descriptionArea = new JTextArea();
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setToolTipText("Description");
        descriptionArea.setSize(priorityCombo.getWidth() + 
                nameField.getWidth() + dateField.getWidth() + 2*COMP_MARGIN, 70);
        TransferFocus.patch(descriptionArea);
        
        
        descriptScroller = new LightScrollPane(descriptionArea);
        descriptScroller.setSize(descriptionArea.getSize());
        descriptScroller.setLocation(LEFT_MARGIN, 
                nameField.getY() + nameField.getHeight() + COMP_MARGIN + descriptLbl.getHeight());
        add(descriptScroller);
        add(descriptLbl);
        
        notesLbl = new JLabel("Notes:");
        notesLbl.setLocation(LEFT_MARGIN,
                descriptScroller.getY() + descriptScroller.getHeight() + COMP_MARGIN);
        notesLbl.setSize(100, 30);
        
        notesArea = new JTextArea();
        notesArea.setWrapStyleWord(true);
        notesArea.setLineWrap(true);
        notesArea.setSize(descriptScroller.getWidth(), 200);
        notesArea.setToolTipText("Notes");
        TransferFocus.patch(notesArea);        
        
        notesScroller = new LightScrollPane(notesArea);
        notesScroller.setSize(notesArea.getSize());
        notesScroller.setLocation(LEFT_MARGIN, 
                descriptScroller.getY() + descriptScroller.getHeight() + notesLbl.getHeight() + COMP_MARGIN);
        add(notesLbl);
        add(notesScroller);
        
        submit = new JButton();
        submit.setText("OK");
        submit.setSize(70, 30);
        submit.setLocation(notesScroller.getWidth()/4 + LEFT_MARGIN, 
                notesScroller.getY() + notesScroller.getHeight() + COMP_MARGIN);
        submit.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent evt){
                submitAction(evt);
            }
        });
        submit.setDefaultCapable(true);
        JRootPane root = this.getRootPane();
        root.setDefaultButton(submit);
        add(submit);
        
        cancel = new JButton();
        cancel.setText("Cancel");
        cancel.setSize(70, 30);
        cancel.setLocation(notesScroller.getWidth()/2 + LEFT_MARGIN, submit.getY());
        cancel.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent evt){
                cancelAction(evt);
            }
        });
        add(cancel);
    }//===========================================
    
    private void cancelAction(ActionEvent evt){
        dispose();
    }//========================================
    
    private void submitAction(ActionEvent evt){
        //check a few things before allowing the user to add this issue
        if(nameField.getText().length() <= 1){
            JOptionPane.showMessageDialog(this, "Please enter a better name for this issue.");
            return;
        }
        if(dateField.getText().length() <= 1){
            JOptionPane.showMessageDialog(this, "Please enter a date that this issue is being reported.");
            return;
        }
        if(priorityCombo.getSelectedIndex() == 0){
            JOptionPane.showMessageDialog(this, "Please select a priority for this issue.");
            return;
        }
        //submit info the the project manager
        String size = priorityCombo.getSelectedItem().toString().split(" - ")[0];
        boss.addIssue(additionType, nameField.getText(), 
                dateField.getText(), size, descriptionArea.getText(), notesArea.getText(), false);
        
        dispose();
    }//========================================
    
    
}
