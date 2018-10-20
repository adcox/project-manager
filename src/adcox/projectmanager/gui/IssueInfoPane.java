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
import adcox.projectmanager.objects.Issue.*;
import adcox.projectmanager.tools.LightScrollPane;
import adcox.projectmanager.tools.TransferFocus;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;


/**
 *
 * @author Andrew
 * @version Jul 14, 2011
 */
public class IssueInfoPane extends JDialog {
    
    public static int LEFT_MARGIN = 10;
    public static int TOP_MARGIN = 10;
    public static int COMP_MARGIN = 7;
    
    //components
    private JTabbedPane tabPane;
    private JButton previous, next, cancel, ok, solve;
    private JPanel summary, problem, solution, buttonPane;
    private JLabel nameLbl, descriptionLbl, dateOpenLbl, dateCloseLbl,
            problemNotesLbl, solutionNotesLbl, priorityLbl;
    private JComboBox priorityCombo;
    private JTextField name, dateOpen, dateClose;
    private JTextArea description, problemNotes, solutionNotes;
    private LightScrollPane descriptScroller, problemNoteScroller, solutionNoteScroller;
    
    private JTable table;
    private ArrayList<Issue> projectIssues;
    private String ID;
    private Issue theIssue;
    
    private ProjectManager boss;
    
    public IssueInfoPane(String ID, ArrayList<Issue> projectIssues, JTable table, ProjectManager boss){
        super(boss);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setModal(true);
        setResizable(false);

        this.table = table;
        this.projectIssues = projectIssues;
        this.ID = ID;
        this.boss = boss;
        
        initComponents();
        
        setTitle(theIssue.getName());
        
        add(tabPane, BorderLayout.CENTER);
        add(buttonPane, BorderLayout.PAGE_END);
        
        pack();
        setLocation(200, 100);
        setVisible(true);
    }//================================
    
    private void initSummaryPanel(Issue iss){
        summary = new JPanel();
        summary.setSize(tabPane.getSize());
        summary.setLayout(null);
        summary.setOpaque(false);
        
        nameLbl = new JLabel("Name");
        nameLbl.setLocation(LEFT_MARGIN, 0);
        nameLbl.setSize(100, 30);
        
        name = new JTextField(iss.getName());
        name.setLocation(LEFT_MARGIN, nameLbl.getY() + nameLbl.getHeight());
        name.setSize(100, 30);
        
        descriptionLbl = new JLabel("Description");
        descriptionLbl.setSize(100, 30);
        descriptionLbl.setLocation(LEFT_MARGIN, name.getY() + name.getHeight() + COMP_MARGIN);
        //descriptionLbl.setForeground(Color.white);
        
        description = new JTextArea();
        description.setText(iss.getDescription());
        description.setSize(summary.getWidth() - 50, 150);
        description.setWrapStyleWord(true);
        description.setLineWrap(true);
        TransferFocus.patch(description);
        
        descriptScroller = new LightScrollPane(description);
        descriptScroller.setLocation(LEFT_MARGIN, descriptionLbl.getY() + descriptionLbl.getHeight());
        descriptScroller.setSize(description.getSize());
        
        solve = new JButton("Solve Issue");
        if(iss.isSolved()){
            solve.setText("Reopen Issue");
        }
        solve.setSize(140, 30);
        solve.setLocation(LEFT_MARGIN + descriptScroller.getWidth()/2 - 
                solve.getWidth()/2, descriptScroller.getY() + 
                descriptScroller.getHeight() + COMP_MARGIN);
        solve.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt){
                solveAction();
            }
        });
        
        summary.add(nameLbl);
        summary.add(name);
        summary.add(descriptionLbl);
        summary.add(descriptScroller);
        summary.add(solve);
    }//===============================================
    
    private void initProblemPanel(Issue iss){
        problem = new JPanel();
        problem.setSize(tabPane.getSize());
        problem.setLayout(null);
        problem.setOpaque(false);
        
        dateOpenLbl = new JLabel("Date Reported");
        dateOpenLbl.setSize(110, 30);
        dateOpenLbl.setLocation(LEFT_MARGIN, 0);
        
        dateOpen = new JTextField(iss.getDateOpened());
        dateOpen.setLocation(LEFT_MARGIN, dateOpenLbl.getY() + dateOpenLbl.getHeight());
        dateOpen.setSize(160, 30);
        
        priorityLbl = new JLabel("Priority:");
        priorityLbl.setLocation(LEFT_MARGIN, dateOpen.getY() + dateOpen.getHeight() + COMP_MARGIN);
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
        priorityCombo.setLocation(LEFT_MARGIN, priorityLbl.getY() + priorityLbl.getHeight());
        selectComboRow();
        
        problemNotesLbl = new JLabel("Notes");
        problemNotesLbl.setSize(100, 30);
        problemNotesLbl.setLocation(LEFT_MARGIN, priorityCombo.getY() + priorityCombo.getHeight() + COMP_MARGIN);
        
        problemNotes = new JTextArea();
        problemNotes.setText(iss.getProblemNotes());
        problemNotes.setSize(problem.getWidth() - 50, 140);
        problemNotes.setWrapStyleWord(true);
        problemNotes.setLineWrap(true);
        TransferFocus.patch(problemNotes);
        
        problemNoteScroller = new LightScrollPane(problemNotes);
        problemNoteScroller.setSize(problemNotes.getSize());
        problemNoteScroller.setLocation(LEFT_MARGIN, problemNotesLbl.getY() + problemNotesLbl.getHeight());
        
        problem.add(dateOpenLbl);
        problem.add(dateOpen);
        problem.add(priorityLbl);
        problem.add(priorityCombo);
        problem.add(problemNotesLbl);
        problem.add(problemNoteScroller);
    }//=============================================
    
    private void initSolutionPanel(Issue iss){
        solution = new JPanel();
        solution.setSize(tabPane.getSize());
        solution.setLayout(null);
        solution.setOpaque(false);
        
        dateCloseLbl = new JLabel("Date Resolved");
        dateCloseLbl.setSize(110, 30);
        dateCloseLbl.setLocation(LEFT_MARGIN, 0);
        
        dateClose = new JTextField(iss.getDateClosed());
        dateClose.setLocation(LEFT_MARGIN, dateCloseLbl.getY() + dateCloseLbl.getHeight());
        dateClose.setSize(160, 30);
        
        solutionNotesLbl = new JLabel("Notes");
        solutionNotesLbl.setSize(100, 30);
        solutionNotesLbl.setLocation(LEFT_MARGIN, dateClose.getY() + dateClose.getHeight() + COMP_MARGIN);
        
        solutionNotes = new JTextArea();
        solutionNotes.setText(iss.getSolutionNotes());
        solutionNotes.setSize(solution.getWidth() - 50, 150);
        solutionNotes.setWrapStyleWord(true);
        solutionNotes.setLineWrap(true);
        TransferFocus.patch(solutionNotes);
        
        solutionNoteScroller = new LightScrollPane(solutionNotes);
        solutionNoteScroller.setSize(solutionNotes.getSize());
        solutionNoteScroller.setLocation(LEFT_MARGIN, solutionNotesLbl.getY() + solutionNotesLbl.getHeight());
        
        solution.add(dateCloseLbl);
        solution.add(dateClose);
        solution.add(solutionNotesLbl);
        solution.add(solutionNoteScroller);
    }//=============================================
    
    private void initComponents(){
        //find the Issue with the correct ID
        for(int i = 0; i < projectIssues.size(); i++){
            if(projectIssues.get(i).getID().equals(ID)){
                theIssue = projectIssues.get(i);
                break;
            }
        }
        
        tabPane = new JTabbedPane();
        tabPane.setSize(400, 350);
        tabPane.setPreferredSize(tabPane.getSize());
        tabPane.setLocation(LEFT_MARGIN, TOP_MARGIN);
        
        initSummaryPanel(theIssue);
        tabPane.addTab("Summary", summary);
        
        initProblemPanel(theIssue);
        tabPane.addTab("Problem", problem);
        
        if(theIssue.isSolved()){
            initSolutionPanel(theIssue);
            tabPane.addTab("Solution", solution);
        }
        
        previous = new JButton("Previous");
        previous.setPreferredSize(new Dimension(90, 30));
        previous.setLocation(LEFT_MARGIN, tabPane.getY() + tabPane.getHeight() + COMP_MARGIN);
        previous.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt){
                previousAction(evt);
            }
        });
        
        next = new JButton("Next");
        next.setPreferredSize(new Dimension(70, 30));
        next.setLocation(previous.getX() + previous.getWidth() + COMP_MARGIN, previous.getY());
        next.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt){
                nextAction(evt);
            }
        });
        
        ok = new JButton("OK");
        ok.setPreferredSize(new Dimension(70, 30));
        ok.setLocation(tabPane.getX() + tabPane.getWidth() - COMP_MARGIN - ok.getWidth(), previous.getY());
        ok.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt){
                okAction(evt);
            }
        });
     
        cancel = new JButton("Cancel");
        cancel.setPreferredSize(new Dimension(70, 30));
        cancel.setLocation(ok.getX() - COMP_MARGIN - cancel.getWidth(), previous.getY());
        cancel.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt){
                cancelAction(evt);
            }
        });
        
        buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setOpaque(false);
        buttonPane.add(previous);
        buttonPane.add(Box.createRigidArea(new Dimension(COMP_MARGIN, 0)));
        buttonPane.add(next);
        buttonPane.add(Box.createRigidArea(new Dimension(100, 0)));
        buttonPane.add(cancel);
        buttonPane.add(Box.createRigidArea(new Dimension(COMP_MARGIN, 0)));
        buttonPane.add(ok);
    }//================================================
    
    private void selectComboRow(){
        String priority = Issue.priorityToString(theIssue.getPriority());
        if(priority.startsWith("Tiny")){
            priorityCombo.setSelectedIndex(1);
        }
        else if(priority.startsWith("Small")){
            priorityCombo.setSelectedIndex(2);
        }
        else if(priority.startsWith("Medium")){
            priorityCombo.setSelectedIndex(3);
        }
        else if(priority.startsWith("Large")){
            priorityCombo.setSelectedIndex(4);
        }
        else if(priority.startsWith("Show")){
            priorityCombo.setSelectedIndex(5);
        }
        else
            priorityCombo.setSelectedIndex(0);
    }//=======================================
    
    /**
     * This function sets the priority of the issue based on what is selected
     * in the combo box.
     * @return a boolean representing whether or not the selected value is ok.
     */
    private boolean setIssuePriority(){
        switch(priorityCombo.getSelectedIndex()){
            case 1: theIssue.setPriority(IssuePriority.Tiny); break;
            case 2: theIssue.setPriority(IssuePriority.Small); break;
            case 3: theIssue.setPriority(IssuePriority.Medium); break;
            case 4: theIssue.setPriority(IssuePriority.Large); break;
            case 5: theIssue.setPriority(IssuePriority.ShowStopper); break;
            default: return false;
        }
        return true;
    }//========================================
    
    private void previousAction(ActionEvent evt){
        int currentSelected = table.getSelectedRow();
        if(currentSelected > 0){
            if(madeChanges()){
                saveInfoToIssue();
                boss.setMadeChanges(true);
            }
            ID = String.valueOf(table.getValueAt(currentSelected - 1, 1));
            reLoadTabPane();
            table.setRowSelectionInterval(currentSelected - 1, currentSelected - 1);
        }
    }//=========================================
    
    private void nextAction(ActionEvent evt){
        int currentSelected = table.getSelectedRow();
        if(currentSelected < table.getRowCount() - 1){
            if(madeChanges()){
                saveInfoToIssue();
                boss.setMadeChanges(true);
            }
            ID = String.valueOf(table.getValueAt(currentSelected + 1, 1));
            reLoadTabPane();
            table.setRowSelectionInterval(currentSelected + 1, currentSelected + 1);
        }
    }//=========================================
    
    private void reLoadTabPane(){
        //find the Issue with the correct ID
        for(int i = 0; i < projectIssues.size(); i++){
            if(projectIssues.get(i).getID().equals(ID)){
                theIssue = projectIssues.get(i);
                break;
            }
        }
        
        setTitle(theIssue.getName());
        
        tabPane.removeAll();
        initSummaryPanel(theIssue);
        tabPane.addTab("Summary", summary);
        
        initProblemPanel(theIssue);
        tabPane.addTab("Problem", problem);
        
        if(theIssue.isSolved()){
            initSolutionPanel(theIssue);
            tabPane.addTab("Solution", solution);
        }
    }//=================================
    
    private void okAction(ActionEvent evt){
        if(madeChanges()){
            saveInfoToIssue();
            boss.setMadeChanges(true);
        }
        dispose();
    }//=====================================
    
    private void cancelAction(ActionEvent evt){
        dispose();
    }//=====================================
    
    private void saveInfoToIssue(){
        //save all data to the Issue object
        if(!setIssuePriority()){
            JOptionPane.showMessageDialog(this, "You must select a priority for this issue.");
            return;
        }
        if(name.getText().length() > 1)
            theIssue.setName(name.getText());
        else{
            JOptionPane.showMessageDialog(this, "You must enter a name for this issue");
            return;
        }
        theIssue.setDescription(description.getText());
        if(dateOpen.getText().length() > 1)
            theIssue.setDateOpened(dateOpen.getText());
        else{
            JOptionPane.showMessageDialog(this, "You must enter a date for the opening of this Issue");
            return;
        }
        theIssue.setProblemNotes(problemNotes.getText());
        
        if(theIssue.isSolved()){
            theIssue.setDateClosed(dateClose.getText());
            theIssue.setSolutionNotes(solutionNotes.getText());
        }
    }//====================================
    
    private void solveAction(){
        boolean okPressed = false;
        if(theIssue.isSolved()){
            okPressed = boss.addToIssueTableAction();
        }else{
            okPressed = boss.addToFixedTableAction();
        }
        if(okPressed){
            dispose();
        }
    }//==================================
    
    private boolean madeChanges(){
        if(!name.getText().equals(theIssue.getName()))
            return true;
        if(!description.getText().equals(theIssue.getDescription()))
            return true;
        if(!dateOpen.getText().equals(theIssue.getDateOpened()))
            return true;
        if(!problemNotes.getText().equals(theIssue.getProblemNotes()))
            return true;
        //check to see if the selected value in the combo starts with the same word as the priority is saved as
        if(!String.valueOf(priorityCombo.getSelectedItem()).startsWith(Issue.priorityToString(theIssue.getPriority())))
            return true;
        
        if(theIssue.isSolved()){
            if(!dateClose.getText().equals(theIssue.getDateClosed()))
                return true;
            if(!solutionNotes.getText().equals(theIssue.getSolutionNotes()))
                return true;
        }
        
        return false;
    }//=================================
}
