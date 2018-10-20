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

import adcox.projectmanager.tools.LightScrollPane;
import adcox.projectmanager.tools.TransferFocus;
import java.awt.event.*;
import javax.swing.*;

/**
 *
 * @author Andrew
 * @version Jul 13, 2011
 */
public class ResolveBugFrame extends JDialog {
    
    public static int P_WIDTH = 400;
    public static int P_HEIGHT = 400;
    public static int LEFT_MARGIN = 10;
    public static int TOP_MARGIN = 10;
    public static int COMP_MARGIN = 10;
    
    private JTextField dateField;
    private JTextArea notesArea;
    private LightScrollPane noteScroller;
    private JLabel dateLbl, notesLbl;
    private JButton submit, cancel;
    
    private ProjectManager boss;
    private boolean proceed = false;
    private boolean dataIntoFixedTable;
    private String ID;
    
    public ResolveBugFrame(String ID, String date, String time, boolean intoFixedTable,
            ProjectManager pm) {
        if(intoFixedTable)
            setTitle("Resolve an Issue");
        else
            setTitle("Re-Open an Issue");
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);
        setModal(true);
        dataIntoFixedTable = intoFixedTable;
        boss = pm;
        this.ID = ID;
        initComponents(date, time);
        
        setSize(LEFT_MARGIN*2 + noteScroller.getWidth(), TOP_MARGIN*3 + submit.getY() + submit.getHeight());
        
        setVisible(true);
    }//================================
    
    private void initComponents(String date, String time){
        dateLbl = new JLabel("Date resolved:");
        if(!dataIntoFixedTable)
            dateLbl.setText("Date re-Opened:");
        dateLbl.setSize(120, 30);
        dateLbl.setLocation(LEFT_MARGIN, TOP_MARGIN);
        add(dateLbl);
        
        dateField = new JTextField(date + ", " + time);
        dateField.setSize(150, 30);
        dateField.setLocation(LEFT_MARGIN, dateLbl.getY() + dateLbl.getHeight());
        dateField.setToolTipText("The date this issue was resolved");
        if(!dataIntoFixedTable)
            dateField.setToolTipText("The date this issue was re-opened");
        add(dateField);
        
        notesLbl = new JLabel("How I fixed it:");
        if(!dataIntoFixedTable)
            notesLbl.setText("How it broke:");
        notesLbl.setSize(150, 30);
        notesLbl.setLocation(LEFT_MARGIN, dateField.getY() + dateField.getHeight() + COMP_MARGIN);
        add(notesLbl);
        
        notesArea = new JTextArea();
        notesArea.setSize(400, 200);
        notesArea.setPreferredSize(notesArea.getSize());
        notesArea.setWrapStyleWord(true);
        notesArea.setLineWrap(true);
        notesArea.setToolTipText("Describe how you fixed the issue");
        TransferFocus.patch(notesArea);
        
        noteScroller = new LightScrollPane(notesArea);
        noteScroller.setSize(notesArea.getSize());
        noteScroller.setLocation(LEFT_MARGIN, notesLbl.getY() + notesLbl.getHeight());
        add(noteScroller);
        
        submit = new JButton();
        submit.setText("OK");
        submit.setSize(70, 30);
        submit.setLocation(noteScroller.getWidth()/4 + LEFT_MARGIN, 
                noteScroller.getY() + noteScroller.getHeight() + COMP_MARGIN);
        submit.addActionListener(new ActionListener(){
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
        cancel.setLocation(noteScroller.getWidth()/2 + LEFT_MARGIN, submit.getY());
        cancel.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt){
                cancelAction(evt);
            }
        });
        add(cancel);
    }//==============================================
    
    private void submitAction(ActionEvent evt){
        //make sure the user has sumbitted SOME kind of explanation
        if(notesArea.getText().length() <= 5){
            if(dataIntoFixedTable)
                JOptionPane.showMessageDialog(this, "You must enter a description of"
                    + " how this issue was resolved");
            else
                JOptionPane.showMessageDialog(this, "You must enter a description of"
                    + " how this issue is no longer solved");
        }
        else{
            //save info to file
            //tell ProjectManager that it is ok to procede
            proceed = true;
            boss.resolveIssue(ID, dateField.getText(), notesArea.getText(), dataIntoFixedTable);
            dispose();
        }
    }
    private void cancelAction(ActionEvent evt){
        //tell project manager not to proceed
        proceed = false;
        dispose();
    }
    
    public boolean okPushed(){return proceed;}
}
