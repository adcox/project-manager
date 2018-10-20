/**
 *  Project Manager: A program that assists you in managing your programming
 *  projects.
 * 
 *  Copyright (C) 2012 Andrew Cox
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
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;


/**
 *
 * @author Andrew
 * @version Jul 14, 2011
 */
public class WorkHistoryInfoPane extends JDialog {
    
    public static int LEFT_MARGIN = 10;
    public static int TOP_MARGIN = 10;
    public static int COMP_MARGIN = 7;
    
    //components
    private JLabel dateLbl, timeLbl, descriptionLbl;
    private JTextField dateField, timeField;
    private JTextArea descriptionArea;
    private LightScrollPane descriptionScroller;
    private JButton submit, cancel;
    
    private ProjectManager boss;
    private String date, time, description;
    private boolean proceed = false;
    private int entryIndex;
    
    public WorkHistoryInfoPane(ArrayList<String> history, int entryIndex, ProjectManager boss){
        super(boss);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setModal(true);
        setResizable(false);
        
        this.entryIndex = entryIndex;
        
        String[] entry = history.get(entryIndex).split(boss.REGEX);
        
        date = entry[0];
        time = entry[1];
        
        try{
            description = entry[2];   
        }catch(Exception e){
            description = "";
        }
        this.boss = boss;
        
        initComponents();
        
        
        pack();
        setLocation(200, 100);
        setVisible(true);
    }//================================
    
    private void initComponents(){
        
        setLayout(new GridBagLayout());

        GridBagConstraints leftCons = new GridBagConstraints();
        leftCons.anchor = GridBagConstraints.NORTHEAST;
        leftCons.fill = GridBagConstraints.NONE;
        leftCons.weightx = 1.0;
        leftCons.gridy = GridBagConstraints.RELATIVE;
        leftCons.gridx = 0;
        leftCons.insets = new Insets( 4, 8, 4, 8 );

        GridBagConstraints rightCons = new GridBagConstraints();
        rightCons.anchor = GridBagConstraints.NORTHWEST;
        rightCons.fill = GridBagConstraints.HORIZONTAL;
        rightCons.weightx = 1.0;
        rightCons.gridy = GridBagConstraints.RELATIVE;
        rightCons.gridx = 1;
        rightCons.insets = leftCons.insets;
        
        dateLbl = new JLabel("Date");
        dateLbl.setPreferredSize(new Dimension(150, 30));
        
        dateField = new JTextField(date);
        dateField.setPreferredSize(new Dimension(150, 30));
        dateField.setEditable(false);
        
        
        timeLbl = new JLabel("Time Worked");
        timeLbl.setPreferredSize(new Dimension(150, 30));
        
        timeField = new JTextField(time);
        timeField.setPreferredSize(new Dimension(150, 30));
        
        
        descriptionLbl = new JLabel("Description of Work");
        descriptionLbl.setPreferredSize(new Dimension(150, 30));
        
        descriptionArea = new JTextArea(description);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        TransferFocus.patch(descriptionArea);
        
        descriptionScroller = new LightScrollPane(descriptionArea);
        descriptionScroller.setPreferredSize(new Dimension(300, 200));
        
        submit = new JButton();
        submit.setText("OK");
        submit.setPreferredSize(new Dimension(70, 30));
        submit.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent evt){
                submitAction();
            }
        });
        submit.setDefaultCapable(true);
        JRootPane root = this.getRootPane();
        root.setDefaultButton(submit);
        
        cancel = new JButton();
        cancel.setText("Cancel");
        cancel.setPreferredSize(new Dimension(70, 30));
        cancel.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent evt){
                cancelAction();
            }
        });
        
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.add(cancel);
        buttonPane.add(submit);
        
        add(dateLbl, leftCons);
        add(dateField, rightCons);
        add(timeLbl, leftCons);
        add(timeField, rightCons);
        add(descriptionLbl, leftCons);
        add(descriptionScroller, rightCons);
        add(buttonPane, rightCons);
    }//========================================
    
    private void submitAction(){
        if (!isTimeOk())
            return;
        
        //save info to file
        //tell ProjectManager that it is ok to procede
        proceed = true;
        boss.adjustWorkHistory(entryIndex, dateField.getText(), 
                timeField.getText(), descriptionArea.getText());
        dispose();
    }//================================
    
    private void cancelAction(){
        dispose();
    }//================================
    
    private boolean isTimeOk(){
        //adjust any bad time values
        int seconds = 0, minutes = 0, hours = 0;
        String[] parsedTime = timeField.getText().split(":");
        
        try{
            hours = Integer.parseInt(parsedTime[0]);
            minutes = Integer.parseInt(parsedTime[1]);
            seconds = Integer.parseInt(parsedTime[2]);
            
            while(seconds >= 60){
                minutes ++;
                seconds -= 60;
            }
            
            while(minutes >= 60){
                hours ++;
                minutes -=60;
            }
            
            if(hours > 24 || (hours == 24 && (minutes > 0 || seconds > 0))){
                JOptionPane.showMessageDialog(this, "You have entered more time "
                        + "than there is in a day. Please adjust your time "
                        + "value.", "Too Much Time", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            timeField.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
        }catch(Exception e){
            JOptionPane.showMessageDialog(this, "Could not read time value!", 
                    "Oops", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }//=======================================
}
