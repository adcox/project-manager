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

import adcox.projectmanager.objects.*;
import adcox.projectmanager.tools.*;
import com.apple.eawt.*;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import javax.swing.*;
import javax.swing.JToolBar.Separator;
import javax.swing.table.*;
import javax.swing.tree.*;

/**
 *
 * @author Andrew
 * @version July 5, 2010
 */
public class ProjectManager extends JFrame{

    //Variables
    static final int TOOL_BUTTON_SIZE = 35;
    static final int NORM_BUTTON_SIZE = 20;
    static final int TOP_MARGIN = 55;
    static final int LIST_HEIGHT = 250;
    static final int LEFT_MARGIN = 10;
    static final int COMP_MARGIN = 5;   //margin between components
    
    public static final String REGEX = "#&";
    public static final String FILE_PATH = "./";
    public static final Version VERSION = new Version(1,0,3,3);
    
    private Project mainProject = null;
    private int openMajor = 0, openMinor = 0;
    private int hours = 0, seconds = 0, minutes = 0;
    private boolean madeChanges = false;     //whether or not changes have been made since the last save
    private boolean clickedOnIssueTable;
    private int workHistoryLine = -1;       //the line that represents today's info
    public static enum OS{Mac, Windows, Linux};
    private OS osType;


    //Toolbar
    private JToolBar toolbar = new JToolBar();
    private JButton addProject = new JButton();
    private JButton openProject = new JButton();
    private JButton saveProject = new JButton();
    private JButton playPause = new JButton();
    private javax.swing.Timer projectTimer, statusTimer;
    private JButton addBug = new JButton();
    private JButton addFeature = new JButton();
    private JButton addUpdate = new JButton();
    private JButton addRelease = new JButton();
    
    //Bug Toolbar
    private JButton addToIssueTable = new JButton();
    private JButton addToFixedTable = new JButton();
    
    //Panels
    private JPanel project_workPane, buttonPane, issueTablesPane, statusPane;
    private LightScrollPane issueTableScroller;
        private ETable issueTable = new ETable();
    private LightScrollPane fixedTableScroller;
        private ETable fixedTable = new ETable();   
    private LightScrollPane projectTreeScroller;
        private ETree projectTree;      
    private LightScrollPane workHistoryScroller;
        private ETable workHistoryTable = new ETable();
    private JLabel statusLbl = new JLabel();
    
    private ArrayList<Project> projects = new ArrayList<Project>();
    private MyTableModel workModel, issueModel, fixedModel;   //these are loaded from the projectTableModels array and are the working TableModels for the program
    
    
    //Main Menu
    private JMenuBar menu = new JMenuBar();
    private JMenu file = new JMenu();
        private JMenuItem newProjectItem = new JMenuItem();
        private JMenuItem openProjectItem = new JMenuItem();
        private JMenuItem saveProjectItem = new JMenuItem();
        private JMenuItem exitItem = new JMenuItem();
    private JMenu window = new JMenu();
    private JMenu help = new JMenu();
        private JMenuItem viewHelp = new JMenuItem();
        private JMenuItem aboutItem = new JMenuItem();
    
    //Project List PopUp Menu
    private JPopupMenu projectListMenu = new JPopupMenu();
    private JMenuItem rename = new JMenuItem();
    private JMenuItem makeMain = new JMenuItem();
    private JMenuItem closeProject = new JMenuItem();
    private JMenuItem viewProject = new JMenuItem();
    
    //Issue PopUp Menu
    private JPopupMenu issueListMenu = new JPopupMenu();
    private JMenuItem viewIssue = new JMenuItem();
    
    //Icons
    public static ImageIcon add, add2, open, play, pause, arrow, bug, addBugIcon, clock,
            addClock, lightbulb, addLightbulb, save, upArrow, downArrow, project,
            utilities;
    
//******************************************************************************
//***********Initializations****************************************************
    
    public ProjectManager(OS osType){
        super("Project Manager");

        this.osType = osType;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(980, 600));
        setLocationByPlatform(true);
        
        //set the window closer
        addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                closingOperation();
            }
        });        

        loadAssets();
        if(osType == OS.Mac){
//            loadAssetsMac();
            setMacAttributes();
        }else{
//            loadAssets();
        }
        
        setIconImage(utilities.getImage());
        
        FileIO.mkdir(FILE_PATH.concat(".Projects"));
        
        workModel = new MyTableModel();
        issueModel = new MyTableModel();
        fixedModel = new MyTableModel();
        
        initMainMenu();
        loadNewTableModels(false, null);
        initPanes();
        
        projectTimer = new Timer(1000, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent evt){
                timerAction(evt);
            }
        });
        
        statusTimer = new Timer(4000, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent evt){
                statusLbl.setText(null);
                statusTimer.stop();
            }
        });
                
        loadLastOpenedProjects();
         
        pack();
        setVisible(true);
    }//=========================================
    
    public static void loadAssetsMac(){
        //larger icons
        add = new ImageIcon(Toolkit.getDefaultToolkit().getImage("NSImage://add.png"), null);
       
        add2 = new ImageIcon(Toolkit.getDefaultToolkit().getImage("NSImage://add2.png"), null);
        
        open = new ImageIcon(Toolkit.getDefaultToolkit().getImage("NSImage://open.png"), null);
        
        save = new ImageIcon(Toolkit.getDefaultToolkit().getImage("NSImage://save.png"), null);
        
        addBugIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage("NSImage://addBug.png"), null);
        
        addClock = new ImageIcon(Toolkit.getDefaultToolkit().getImage("NSImage://addClock.png"), null);        
        
        addLightbulb = new ImageIcon(Toolkit.getDefaultToolkit().getImage("NSImage://addLightbulb.png"), null);        
        
        upArrow = new ImageIcon(Toolkit.getDefaultToolkit().getImage("NSImage://upArrow.png"), null);
        
        downArrow = new ImageIcon(Toolkit.getDefaultToolkit().getImage("NSImage://downArrow.png"), null);
        
        play = new ImageIcon(Toolkit.getDefaultToolkit().getImage("NSImage://play.png"), null);
        
        pause = new ImageIcon(Toolkit.getDefaultToolkit().getImage("NSImage://pause.png"), null);

        project = new ImageIcon(Toolkit.getDefaultToolkit().getImage("NSImage://project.png"), null);
        
        utilities = new ImageIcon(Toolkit.getDefaultToolkit().getImage("NSImage://utilities_512.png"), null);
        
        //small icons
        
        arrow = new ImageIcon(Toolkit.getDefaultToolkit().getImage("NSImage://arrow.png"), null);
        
        bug = new ImageIcon(Toolkit.getDefaultToolkit().getImage("NSImage://bug.png"), null);
        
        clock = new ImageIcon(Toolkit.getDefaultToolkit().getImage("NSImage://clock.png"), null);
        
        lightbulb = new ImageIcon(Toolkit.getDefaultToolkit().getImage("NSImage://lightbulb.png"), null);
    }//=============================
    
    public static void loadAssets(){
        //larger icons
        add = new ImageIcon(Toolkit.getDefaultToolkit().getImage("img/add.png"), null);
       
        add2 = new ImageIcon(Toolkit.getDefaultToolkit().getImage("img/add2.png"), null);
        
        open = new ImageIcon(Toolkit.getDefaultToolkit().getImage("img/open.png"), null);
        
        save = new ImageIcon(Toolkit.getDefaultToolkit().getImage("img/save.png"), null);
        
        addBugIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage("img/addBug.png"), null);
        
        addClock = new ImageIcon(Toolkit.getDefaultToolkit().getImage("img/addClock.png"), null);        
        
        addLightbulb = new ImageIcon(Toolkit.getDefaultToolkit().getImage("img/addLightbulb.png"), null);        
        
        upArrow = new ImageIcon(Toolkit.getDefaultToolkit().getImage("img/upArrow.png"), null);
        
        downArrow = new ImageIcon(Toolkit.getDefaultToolkit().getImage("img/downArrow.png"), null);
        
        play = new ImageIcon(Toolkit.getDefaultToolkit().getImage("img/play.png"), null);
        
        pause = new ImageIcon(Toolkit.getDefaultToolkit().getImage("img/pause.png"), null);

        project = new ImageIcon(Toolkit.getDefaultToolkit().getImage("img/project.png"), null);
        
        utilities = new ImageIcon(Toolkit.getDefaultToolkit().getImage("img/utilities_512.png"), null);
        
        //small icons
        
        arrow = new ImageIcon(Toolkit.getDefaultToolkit().getImage("img/arrow.png"), null);
        
        bug = new ImageIcon(Toolkit.getDefaultToolkit().getImage("img/bug.png"), null);
        
        clock = new ImageIcon(Toolkit.getDefaultToolkit().getImage("img/clock.png"), null);
        
        lightbulb = new ImageIcon(Toolkit.getDefaultToolkit().getImage("img/lightbulb.png"), null);
    }//=============================
    
    private void setMacAttributes(){
        Application app = Application.getApplication();
        app.setAboutHandler(new AboutHandler(){
            @Override
            public void handleAbout(AppEvent.AboutEvent e){
                aboutAction();
            }
        });
        
        //***Set Mac Visual properties
        JRootPane root = this.getRootPane();
        root.putClientProperty("apple.awt.brushMetalLook", Boolean.TRUE);
        
        try {
            // set the look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }//============================================
    
    private void initMainMenu(){
        newProjectItem.setText("New Project");
        newProjectItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        newProjectItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent evt){
                addNewProject();
            }
        });
        
        openProjectItem.setText("Open Project");
        openProjectItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        openProjectItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent evt){
                openProjectAction(evt);
            }
        });
        
        saveProjectItem.setText("Save Project");
        saveProjectItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        saveProjectItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent evt){
                saveProject(mainProject);
            }
        });
        
        exitItem.setText("Quit Project Manager");
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        exitItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent evt){
                closingOperation();
            }
        });
        
        file.setText("File");
        file.add(newProjectItem);
        file.add(openProjectItem);
        file.add(saveProjectItem);
        
        if(!osType.equals(OS.Mac))
            file.add(exitItem);
        
        window.setText("Window");
        
        viewHelp.setText("View Help");
        viewHelp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        viewHelp.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent evt){
                viewHelpAction(evt);
            }
        });
        
        aboutItem.setText("About Project Manager");
        aboutItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent evt){
                aboutAction();
            }
        });
        
        help.setText("Help");
        help.add(viewHelp);
        
        if(!osType.equals(OS.Mac))
            help.add(aboutItem);
        
        menu.add(file);
        //menu.add(window);
        menu.add(help);
        
        setJMenuBar(menu);     //This line MUST be here to move the menu to the top menu bar
    }//=================================
    
    private void initToolbar(){
        addRelease.setIcon(add2);
        addRelease.setBorderPainted(false);
        addRelease.setPreferredSize(new Dimension(TOOL_BUTTON_SIZE, TOOL_BUTTON_SIZE));
        addRelease.setToolTipText("Add a Release");
        addRelease.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent evt){
                addNewRelease();
            }
        });
        
        addProject.setIcon(add);
        addProject.setBorderPainted(false);
        addProject.setPreferredSize(new Dimension(TOOL_BUTTON_SIZE, TOOL_BUTTON_SIZE));
        addProject.setToolTipText("New Project");
        addProject.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent evt){
                addNewProject();
            }
        });
        
        
        openProject.setIcon(open);
        openProject.setBorderPainted(false);
        openProject.setPreferredSize(new Dimension(TOOL_BUTTON_SIZE, TOOL_BUTTON_SIZE));
        openProject.setToolTipText("Open Project");
        openProject.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent evt){
                openProjectAction(evt);
            }

            
        });
        
        saveProject.setIcon(save);
        saveProject.setBorderPainted(false);
        saveProject.setPreferredSize(new Dimension(TOOL_BUTTON_SIZE, TOOL_BUTTON_SIZE));
        saveProject.setToolTipText("Save Project");
        saveProject.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent evt){
                saveProject(mainProject);
            }
        });
        
        //set up the play-pause button
        playPause.setIcon(play);
        playPause.setBorderPainted(false);
        playPause.setPreferredSize(new Dimension(TOOL_BUTTON_SIZE, TOOL_BUTTON_SIZE));
        playPause.setToolTipText("Starting Work on Project");
        playPause.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent evt){
                playPauseAction();
            }
        });
        
        
        addBug.setIcon(addBugIcon);
        addBug.setBorderPainted(false);
        addBug.setPreferredSize(new Dimension(TOOL_BUTTON_SIZE, TOOL_BUTTON_SIZE));
        addBug.setToolTipText("Report a Bug");
        addBug.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent evt){
                addBugAction(evt);
            }
        });
        
        addFeature.setIcon(addLightbulb);
        addFeature.setBorderPainted(false);
        addFeature.setPreferredSize(new Dimension(TOOL_BUTTON_SIZE, TOOL_BUTTON_SIZE));
        addFeature.setToolTipText("Add a Feature");
        addFeature.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent evt){
                addFeatureAction(evt);
            }
        });
        
        addUpdate.setIcon(addClock);
        addUpdate.setBorderPainted(false);
        addUpdate.setPreferredSize(new Dimension(TOOL_BUTTON_SIZE, TOOL_BUTTON_SIZE));
        addUpdate.setToolTipText("Add an Update");
        addUpdate.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent evt){
                addUpdateAction(evt);
            }
        });
        toolbar.setPreferredSize(new Dimension(1280, 35));
        toolbar.setFloatable(false);
        toolbar.add(addProject);
        toolbar.add(addRelease);
        toolbar.add(openProject);
        toolbar.add(saveProject);
        toolbar.addSeparator(new Dimension(65, toolbar.getPreferredSize().height));
        toolbar.add(playPause);
        toolbar.addSeparator(new Dimension(65, toolbar.getPreferredSize().height));
        toolbar.add(addBug);
        toolbar.add(addFeature);
        toolbar.add(addUpdate);
    }//======================================
    
    private void initPanes(){
        //initialze the project_workPane
        project_workPane = new JPanel();
        project_workPane.setLayout(new BoxLayout(project_workPane, BoxLayout.PAGE_AXIS));
        project_workPane.setBorder(BorderFactory.createEmptyBorder(COMP_MARGIN, LEFT_MARGIN, 0, COMP_MARGIN));
        
        //initialize the project table
        initProjectTable();
        initWorkHistoryTable();
        
        project_workPane.add(projectTreeScroller, Component.CENTER_ALIGNMENT);
        project_workPane.add(Box.createRigidArea(new Dimension(0, COMP_MARGIN)));
        project_workPane.add(workHistoryScroller, Component.CENTER_ALIGNMENT);
        
        //initialize the button pane
        buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(COMP_MARGIN,0,COMP_MARGIN,0)); //put a margin on the top and bottom of this one
        
        //initialize the buttons
        addToIssueTable.setIcon(upArrow);
        addToIssueTable.setPreferredSize(new Dimension(TOOL_BUTTON_SIZE, TOOL_BUTTON_SIZE));
        addToIssueTable.setToolTipText("Add to Unsolved");
        addToIssueTable.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent evt){
                addToIssueTableAction();
            }
        });
        
        addToFixedTable.setIcon(downArrow);
        addToFixedTable.setPreferredSize(new Dimension(TOOL_BUTTON_SIZE, TOOL_BUTTON_SIZE));
        addToFixedTable.setToolTipText("Add to Solved");
        addToFixedTable.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent evt){
                addToFixedTableAction();
            }
        });
        
        buttonPane.add(addToFixedTable);
        buttonPane.add(Box.createRigidArea(new Dimension(COMP_MARGIN, 0)));
        buttonPane.add(addToIssueTable);
        
        //initialize the pane that holds both issue tables and the button pane
        issueTablesPane = new JPanel();
        issueTablesPane.setLayout(new BoxLayout(issueTablesPane, BoxLayout.PAGE_AXIS));
        issueTablesPane.setBorder(BorderFactory.createEmptyBorder(COMP_MARGIN, 0, 0, LEFT_MARGIN));
        
        //init the two issue Tables
        initIssueTable();
        initFixedTable();
        
        //add the bug table, then button pane, then fixed table
        issueTablesPane.add(issueTableScroller, Component.LEFT_ALIGNMENT);
        issueTablesPane.add(buttonPane, Component.CENTER_ALIGNMENT);
        issueTablesPane.add(fixedTableScroller, Component.LEFT_ALIGNMENT);
        
        //initialize the toolbar
        initToolbar();
        
        //create the status Pane
        statusPane = new JPanel();
        statusPane.setLayout(new BoxLayout(statusPane, BoxLayout.LINE_AXIS));
        statusPane.setBorder(BorderFactory.createEmptyBorder(0, LEFT_MARGIN, 0, LEFT_MARGIN));
        
        statusLbl.setPreferredSize(new Dimension(200, 30));
        statusLbl.setForeground(Color.black);
        
        statusPane.add(statusLbl);
        //put everything onto the screen
        Container contentPane = getContentPane();
        contentPane.add(toolbar, BorderLayout.PAGE_START);
        contentPane.add(project_workPane, BorderLayout.WEST);
        contentPane.add(issueTablesPane, BorderLayout.CENTER);
        contentPane.add(new Separator(), BorderLayout.SOUTH);
        contentPane.add(statusPane, BorderLayout.PAGE_END);
    }//=============================================================
    
    private void initIssueTable(){
        issueModel = new MyTableModel();
        issueModel.addColumn("Type");
        issueModel.addColumn("ID");
        issueModel.addColumn("Date Reported");
        issueModel.addColumn("Name");
        issueModel.addColumn("Description");
        issueModel.addColumn("Priority");    
        issueModel.setEditable(false);
        
        issueTable.setModel(issueModel);
        //issueTable.setToolTipText("Unsolved issues are listed here.");
        issueTable.setMinimumSize(new Dimension(690, 100));
        issueTable.setRowHeight(20);
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(issueTable.getModel());
        sorter.setComparator(5, new PriorityComparator());
        issueTable.setRowSorter(sorter);
        
        issueTable.getColumnModel().getColumn(0).setMaxWidth(40);   //type
        issueTable.getColumnModel().getColumn(1).setMaxWidth(50);   //ID                
        issueTable.getColumnModel().getColumn(2).setMaxWidth(200);  //date
        issueTable.getColumnModel().getColumn(2).setMinWidth(150);
        issueTable.getColumnModel().getColumn(3).setMaxWidth(120);  //name
        issueTable.getColumnModel().getColumn(3).setMinWidth(100);
        issueTable.getColumnModel().getColumn(5).setMaxWidth(90);   //Size
            
        viewIssue.setText("Get Info");
        viewIssue.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent evt){
                viewIssueAction();
            }
        });
        
        issueListMenu.add(viewIssue);
        
        issueTable.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                clickedOnIssueTable = true;
                // Right mouse click
                if(SwingUtilities.isRightMouseButton(e)){
                    if(issueTable.getSelectedRows().length > 0){
                        // get the coordinates of the mouse click
                        Point p = e.getPoint();

                        int rowNumber = 0;
                        ListSelectionModel model;
                        int xOffset, yOffset;
                        // get the row index that contains that coordinate
                        rowNumber = issueTable.rowAtPoint(p);

                        // Get the ListSelectionModel of the JTable
                        model = issueTable.getSelectionModel();
                        xOffset = -getLocationOnScreen().x;
                        yOffset = -getLocationOnScreen().y;

                        // set the selected interval of rows. Using the "rowNumber"
                        // variable for the beginning and end selects only that one row.
                        model.setSelectionInterval(rowNumber, rowNumber);
                        showPopUp(issueListMenu, e.getXOnScreen() + xOffset, e.getYOnScreen() + yOffset);
                    }
                }
                else if(e.getClickCount() > 1 && issueTable.getSelectedRows().length > 0){
                    //double click
                    viewIssueAction();
                }
            }
        });
        
        //add the menu to the list object
        issueTable.add(issueListMenu);
        
        issueTableScroller = new LightScrollPane(issueTable);
        issueTableScroller.setPreferredSize(new Dimension(700, LIST_HEIGHT));
    }//=================================
    
    private void initFixedTable(){
        fixedModel = new MyTableModel();
        fixedModel.addColumn("Type");
        fixedModel.addColumn("ID");
        fixedModel.addColumn("Date Resolved");
        fixedModel.addColumn("Name");
        fixedModel.addColumn("Description");
        fixedModel.addColumn("Priority");
        fixedModel.setEditable(false);
        
        fixedTable.setModel(fixedModel);
        //fixedTable.setToolTipText("Solved issues are listed here.");
        fixedTable.setMinimumSize(new Dimension(690, 100));
        fixedTable.setRowHeight(20);
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(fixedTable.getModel());
        sorter.setComparator(5, new PriorityComparator());
        fixedTable.setRowSorter(sorter);
        fixedTable.getColumnModel().getColumn(0).setMaxWidth(40);   //type
        fixedTable.getColumnModel().getColumn(1).setMaxWidth(50);   //ID                
        fixedTable.getColumnModel().getColumn(2).setMaxWidth(200);  //date
        fixedTable.getColumnModel().getColumn(2).setMinWidth(150);
        fixedTable.getColumnModel().getColumn(3).setMaxWidth(120);  //name
        fixedTable.getColumnModel().getColumn(3).setMinWidth(100);
        fixedTable.getColumnModel().getColumn(5).setMaxWidth(90);   //Size
        fixedTable.add(issueListMenu);
        
        fixedTable.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                clickedOnIssueTable = false;
                // Right mouse click
                if(SwingUtilities.isRightMouseButton(e)){
                    if(fixedTable.getSelectedRows().length > 0){
                        // get the coordinates of the mouse click
                        Point p = e.getPoint();

                        int rowNumber = 0;
                        ListSelectionModel model;
                        int xOffset, yOffset;
                        // get the row index that contains that coordinate
                        rowNumber = fixedTable.rowAtPoint(p);

                        // Get the ListSelectionModel of the JTable
                        model = fixedTable.getSelectionModel();
                        xOffset = -getLocationOnScreen().x;
                        yOffset = -getLocationOnScreen().y;


                        // set the selected interval of rows. Using the "rowNumber"
                        // variable for the beginning and end selects only that one row.
                        model.setSelectionInterval(rowNumber, rowNumber);
                        showPopUp(issueListMenu, e.getXOnScreen() + xOffset, e.getYOnScreen() + yOffset);
                    }
                }
                else if(e.getClickCount() > 1 && fixedTable.getSelectedRows().length > 0){
                    //double click
                    viewIssueAction();
                }
            }
        });
        
        //put the table in a TablePanel so that the scroll bar will look ok
        fixedTableScroller = new LightScrollPane(fixedTable);
        fixedTableScroller.setPreferredSize(new Dimension(700, LIST_HEIGHT));
    }//============================================
    
    private void initProjectTable(){
        DefaultMutableTreeNode top = new DefaultMutableTreeNode("Projects");
        
        projectTree = new ETree(new DefaultTreeModel(top)); 
        projectTree.setEditable(false);
        projectTree.getSelectionModel().setSelectionMode
        (TreeSelectionModel.SINGLE_TREE_SELECTION);
        projectTree.setShowsRootHandles(true);
        projectTree.setRootVisible(false);
        
        //create a smaller version of the project Icon
        ImageIcon smallProject = project;
        projectTree.setCellRenderer(new MyTreeRenderer(smallProject));
        
        //init the pop up menu for this pane
        rename.setText("Rename");
        rename.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent evt){
                renameAction(evt);
            }
        });
        makeMain.setText("Set as Main Project");
        makeMain.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent evt){
                if(projectTree.getSelectionPaths().length > 0){
                    String projectName =
                            ((DefaultMutableTreeNode)projectTree.getSelectionPath().getLastPathComponent()).toString();
                    setAsMain(projectName, true);
                }
            }
        });
        
        closeProject.setText("Close Project");
        closeProject.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent evt){
                closeProjectAction(evt);
            }
        });
        
        viewProject.setText("Get Info");
        viewProject.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent evt){
                viewProjectAction(evt);
            }
        });
        
        projectListMenu.add(rename);
        projectListMenu.add(viewProject);
        projectListMenu.addSeparator();
        projectListMenu.add(makeMain);
        projectListMenu.add(closeProject);
        
        //add the menu to the list object
        projectTree.add(projectListMenu);
        
        projectTree.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e){showMenuIfPopupTrigger(e);}
            @Override
            public void mouseClicked(MouseEvent e){
                showMenuIfPopupTrigger(e);
                
                if(e.getClickCount() > 1){
                    //show the issues associated with this object
                    projectTreeDoubleClick();
                }
            }
            @Override
            public void mouseReleased(MouseEvent e){showMenuIfPopupTrigger(e);}
            
            private void showMenuIfPopupTrigger(final MouseEvent e){
                if (e.isPopupTrigger()) {
                    //set the new selections before showing the popup
                    setSelectedItemsOnPopupTrigger(e);

                    if(!projectTree.isSelectionEmpty()){
                        //show the menu, offsetting from the mouse click slightly
                        showPopUp(projectListMenu, e.getX() + projectTree.getX(), e.getY() + projectTree.getY());
                    }
                }
            }//========================================

             /**
             * Fix for right click not selecting tree nodes -
             * We want to implement the following behaviour which matches windows explorer:
             * If the item under the click is not already selected, clear the current selections and select the
             * item, prior to showing the popup.
             * If the item under the click is already selected, keep the current selection(s)
             */
            private void setSelectedItemsOnPopupTrigger(MouseEvent e) {
                TreePath p = projectTree.getPathForLocation(e.getX(), e.getY());
                if (!projectTree.getSelectionModel().isPathSelected(p)) {
                    projectTree.getSelectionModel().setSelectionPath(p);
                }
            }
        });
        
        projectTreeScroller = new LightScrollPane(projectTree);
        projectTreeScroller.setPreferredSize(new Dimension(240, LIST_HEIGHT));
    }//=====================================
    
    private void initWorkHistoryTable(){
        workModel.addColumn("Date");
        workModel.addColumn("Time Worked");
        workModel.setEditable(false);
        
        workHistoryTable.setModel(workModel);
        workHistoryTable.setMinimumSize(new Dimension(240, 100));
        workHistoryTable.setColumnSelectionAllowed(false);
        workHistoryTable.setCellSelectionEnabled(false);
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(workHistoryTable.getModel());
        workHistoryTable.setRowSorter(sorter);
        workHistoryTable.setRowSelectionAllowed(true);
        workHistoryTable.getColumnModel().getColumn(1).setMinWidth(80);
        workHistoryTable.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e){showMenuIfPopupTrigger(e);}
            @Override
            public void mouseClicked(MouseEvent e){
                showMenuIfPopupTrigger(e);
                
                if(e.getClickCount() > 1){
                    //show the issues associated with this object
                    workHistoryDoubleClick();
                }
            }
            @Override
            public void mouseReleased(MouseEvent e){showMenuIfPopupTrigger(e);}
            
            private void showMenuIfPopupTrigger(final MouseEvent e){
//                if (e.isPopupTrigger()) {
//                    //set the new selections before showing the popup
//                    setSelectedItemsOnPopupTrigger(e);
//
//                    if(!workHistoryTable){
//                        //show the menu, offsetting from the mouse click slightly
//                        showPopUp(projectListMenu, e.getX() + projectTree.getX(), e.getY() + projectTree.getY());
//                    }
//                }
            }//========================================

             /**
             * Fix for right click not selecting tree nodes -
             * We want to implement the following behaviour which matches windows explorer:
             * If the item under the click is not already selected, clear the current selections and select the
             * item, prior to showing the popup.
             * If the item under the click is already selected, keep the current selection(s)
             */
//            private void setSelectedItemsOnPopupTrigger(MouseEvent e) {
//                TreePath p = projectTree.getPathForLocation(e.getX(), e.getY());
//                if (!projectTree.getSelectionModel().isPathSelected(p)) {
//                    projectTree.getSelectionModel().setSelectionPath(p);
//                }
//            }
        });
        
        workHistoryScroller = new LightScrollPane(workHistoryTable);
        workHistoryScroller.setPreferredSize(new Dimension(240, LIST_HEIGHT + TOOL_BUTTON_SIZE + 2*COMP_MARGIN));
    }//=========================================
     
    private void loadNewTableModels(boolean loadData, File filePath){
        deleteAllRows(workModel);
        deleteAllRows(issueModel);
        deleteAllRows(fixedModel);
        
        if(loadData){
            if(filePath != null){
                loadProjectData(filePath);
            }
            else{
                System.err.println("Could not load a project with a null name.");
            }
        }
        
        syncTimer();
    }//==================================
      
    private void loadIssuesToTables(){
        deleteAllRows(issueModel);
        deleteAllRows(fixedModel);

        if(mainProject.getMajors().size() > openMajor &&
                mainProject.getMajors().get(openMajor).getMinors().size() > openMinor){
            MinorRelease openRelease = mainProject.getMajors().get(openMajor).getMinors().get(openMinor);
            //add solvedIssues to the fixedModel
            for(int i = 0; i < openRelease.getSolvedIssues().size(); i++){
                fixedModel.addRow(openRelease.getSolvedIssues().get(i).getIssueAsRow(true));
            }

            //add usnolvedIssues to the issueModel
            for(int i = 0; i < openRelease.getUnsolvedIssues().size(); i++){
                issueModel.addRow(openRelease.getUnsolvedIssues().get(i).getIssueAsRow(false));
            }
        }
    }//=============================================
    
    private void loadLastOpenedProjects(){
        ArrayList<String> lines = FileIO.readFile(FILE_PATH.concat(".Projects/openprojects.txt"));
        
        if(lines == null)
            return;
        
        for(int i = 0; i < lines.size(); i++){
            String[] aLine = lines.get(i).split(REGEX);
            
            //try to load the project
            try{
                File projectFile = new File(aLine[0]);
                loadProjectData(projectFile);
            }catch(Exception e){
                System.err.println("Was not able to load previously opened project.");
                System.err.println(e.getStackTrace());
            }
        }
        
        //collapse all the projects, which incidentally collapses the root node
        projectTree.collapseAll();
        //select the Projects node, which is the root
        projectTree.selectNodesMatching("Projects", false);
        projectTree.expandPath(projectTree.getSelectionPath());
        
        //find the main project and set it as the main one - loader sets each
        //load to the main project, so it has to be done after each project has 
        //been loaded
        for(int i = 0; i < lines.size(); i++){
            String[] aLine = lines.get(i).split(REGEX);
            //set the project as main if it was saved that way
            if(Boolean.valueOf(aLine[1])){
                this.setAsMain(projects.get(i).getName(), true);
                projectTree.selectNodesMatching(projects.get(i).getName(), true);
                
                //expand path of main project
                projectTree.expandPath(projectTree.getSelectionPath());
                
                //load the newest minor release
                try{
                    openMajor = projects.get(i).getMajors().size() - 1;
                    openMinor = mainProject.getMajors().get(openMajor).getMinors().size() - 1;
                }
                catch(Exception e){
                    openMajor = 0;
                    openMinor = 0;
                }
                //load the issues to the tables so they show up
                loadIssuesToTables();
                
                break;  //there should only be one main project
            }
        }
    }//============================================
    
//******************************************************************************
//****************ActionPerformed Methods***************************************
    
    private void addNewRelease(){
        if(mainProject == null)
            return;
        
        NewReleaseDialog dialog = new NewReleaseDialog(this);
        
        if(!dialog.pressedCancel()){
            //get the project
            Object ob = projectTree.getSelectedObject();
            
            Project theProject;
            //if there is no selection, we want the main project
            if(ob == null){
                theProject = mainProject;
                
                //if there is no main project, exit the method
                if(mainProject == null){
                    return;
                }
            }
            else if(ob instanceof Project){
                theProject = (Project)ob;
            }
            else if(ob instanceof MajorRelease){
                theProject = (Project)((DefaultMutableTreeNode)projectTree.getSelectedNode().getParent()).getUserObject();
            }
            else{
                DefaultMutableTreeNode parent = (DefaultMutableTreeNode)projectTree.getSelectedNode().getParent();
                theProject = (Project)((DefaultMutableTreeNode)parent.getParent()).getUserObject();
            }
            
            //Now that we have the project...
            if(dialog.pressedMajor()){
                String majorName = Integer.toString(theProject.getVersion().getMajor() + 1);
                String path = theProject.getDirectory() + "/_" + majorName;
                
                //create new Major Release
                MajorRelease mr = new MajorRelease(majorName, path);
                theProject.addRelease(mr);
                
                openMajor = theProject.getMajors().size() - 1;
                openMinor = theProject.getMajors().get(openMajor).getMinors().size() - 1;
                
                //select the project
                projectTree.selectNodesMatching(theProject.toString(), false);
                
                //add the release and it's minor release to the tree
                projectTree.addObject(mr);
                
                //select the newly created major release in the tree
                for(int i = 0; i < projectTree.getRowCount(); i++){
                    projectTree.setSelectionRow(i);
                    Object object = projectTree.getSelectedObject();
                    if(object.equals(mr)){
                        System.out.println("Found the major release: " + mr.getFilepath());
                        //DefaultMutableTreeNode node = projectTree.getSelectedNode();
                        projectTree.addObject(mr.getMinors().get(0));
                    }
                }
                
                
                setMadeChanges(true);
            }
            else{
                String minorName = Integer.toString(theProject.getVersion().getMinor() + 1);
                String path = theProject.getMajors().get(theProject.getMajors().size() - 1).getFilepath() + "/_" + minorName;
                
                MinorRelease minor = new MinorRelease(minorName, path);
                theProject.getMajors().get(theProject.getMajors().size() - 1).addRelease(minor); 
                
                openMajor = theProject.getMajors().size() - 1;
                openMinor = theProject.getMajors().get(openMajor).getMinors().size() - 1;
                
                //select the major release in the tree
                for(int i = 0; i < projectTree.getRowCount(); i++){
                    projectTree.setSelectionRow(i);
                    Object object = projectTree.getSelectedObject();
                    if(object.equals(theProject.getMajors().get(openMajor))){
                        //DefaultMutableTreeNode node = projectTree.getSelectedNode();
                        projectTree.addObject(minor);
                    }
                }
                
                setMadeChanges(true);
            }
            
            ArrayList<Issue> unsolved = new ArrayList<Issue>();
            //go through all the minor releases and strip them of their unsolved issues
            for(int maj = 0; maj < theProject.getMajors().size(); maj++){
                MajorRelease major = theProject.getMajors().get(maj);
                
                for(int min = 0; min < major.getMinors().size(); min++){
                    unsolved.addAll(major.getMinors().get(min).getUnsolvedIssues());
                    major.getMinors().get(min).deleteUnsolvedIssues();
                    
                    if(min + 1 == major.getMinors().size() && maj + 1 == theProject.getMajors().size()){
                        //this is the LAST known minor release
                        major.getMinors().get(min).getUnsolvedIssues().addAll(unsolved);
                    }
                }
            }
            
            //Save the project!! Unless the user remembers to save, data will be lost
            saveProject(theProject);
        }
    }//============================================
    
    private void addNewProject(){
        String aName = JOptionPane.showInputDialog("Name your project:");
        
        if(aName != null){
            //create a new project
            Project p = new Project(new File(FILE_PATH.concat(".Projects/") + aName), aName, true, projects.size());
            
            //select the Projects node, which is the root and add the project to the tree
            projectTree.selectNodesMatching("Projects", false);
            projectTree.addObject(p);
            
            //add all releases of the project
            for(int maj = 0; maj < p.getMajors().size(); maj++){
                //select the new project node
                projectTree.selectNodesMatching(aName, false);
                //add the major release as a child
                projectTree.addObject(p.getMajors().get(maj));
                //select the newly created Major Release node
                projectTree.setSelectionRow(projectTree.getRowCount() - 1);
                //go through all the minors add add them to the major release
                for(int min = 0; min < p.getMajors().get(maj).getMinors().size(); min++){
                    projectTree.addObject(p.getMajors().get(maj).getMinors().get(min));
                }
            }
            
            //add project to projects array
            projects.add(p);
            
            if(mainProject == null){
                mainProject = projects.get(projects.size() - 1); 
                //this is the first project opened
                setAsMain(aName, false);
            }
        }
    }//==============================
    
    private void setAsMain(String projectName, boolean projectIsOpen){
        //find the project that is selected
        if(projectTimer.isRunning()){
            playPauseAction();
        }
        
        //find the index of the main project
        int projectIndex = 0;
        for(int i = 0; i < projects.size(); i++){
            if(projects.get(i).getName().equals(projectName)){
                projectIndex = i;
                break;
            }
        }
        
        mainProject = projects.get(projectIndex);
        
        //set all projects to NOT main
        for(int i = 0; i < projects.size(); i++){
            projects.get(i).setIsMain(false);
        }
        mainProject.setIsMain(true); 
        
        if(projectIsOpen){
            loadIssuesToTables();
            loadWorkHistory(mainProject);
            syncTimer();
        }
        mainProject.countIssues();
        projectTree.repaint();
    }//=======================================
    
    private void openProjectAction(ActionEvent evt) {
        //FileDialog dialog = new FileDialog(this, "Open a Project", FileDialog.LOAD);
        JFileChooser dialog = new JFileChooser();
        dialog.setFileView(new MyFileView());
        dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        dialog.setMultiSelectionEnabled(false);
        dialog.setFileHidingEnabled(true);
        
        //create  a File object to get the file path
        File f = new File(FILE_PATH.concat(".Projects"));
        try{
            dialog.setCurrentDirectory(f.getCanonicalFile());
        }
        catch(Exception e){
            System.err.println(e.getMessage());
        }
        int returnValue = dialog.showOpenDialog(this);
        
        //check to make sure the user pressed ok
        if(returnValue == JFileChooser.APPROVE_OPTION){
            File theFile = dialog.getSelectedFile(); //returns null on cancel

            if(theFile != null){
                //check to see if this is a project folder
                File[] subFiles = theFile.listFiles();

                if(subFiles != null){
                    for(int i = 0; i < subFiles.length; i++){
                        if(subFiles[i].getName().equals("managedproject")){
                            //this IS a project folder, so open it
                            loadNewTableModels(true, theFile);
                            return;
                        }
                    }
                }
            }
        }
        
        //if we reach this point, the project was not opened
        System.err.println("Could not open project.");
    }//=====================================
    
    private void closeProjectAction(ActionEvent evt){
        if(mainProject == null)
            return;
        
        if(madeChanges){
            //prompt user to save their project
            int response = JOptionPane.showConfirmDialog(this, "Would you like to save your work?", 
                    "Save?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, save);
            
            if(response == JOptionPane.OK_OPTION || response == JOptionPane.NO_OPTION){
                String projectName =
                        ((DefaultMutableTreeNode)projectTree.getSelectionPath().getLastPathComponent()).toString();
                
                //find the project that is selected
                int projectToDelete = 0;
                for(int i = 0; i < projects.size(); i++){
                    if(projects.get(i).getName().equals(projectName)){
                        projectToDelete = i;
                        break;
                    }
                }
                
                if(response == JOptionPane.OK_OPTION)
                    saveProject(projects.get(projectToDelete));      //save the project

                //remove from projectTable also
                projectTree.removeCurrentNode();
                
                //if main project was just deleted, set it to -1, otherwise decrement by one
                if(projects.get(projectToDelete).isMain())
                    mainProject = null;
                else if(projects.indexOf(mainProject) > projectToDelete)
                    mainProject = projects.get(projectToDelete - 1); 
                
                //remove the project from the list of projects
                projects.remove(projectToDelete);
                
                //go through and reset indexes
                for(int i = 0; i < projects.size(); i++){
                    projects.get(i).setIndex(i);
                }
                
                loadNewTableModels(false, null);
            }
        }
        else{
            String projectName =
                    ((DefaultMutableTreeNode)projectTree.getSelectionPath().getLastPathComponent()).toString();

            //find the project that is selected
            int projectToDelete = 0;
            for(int i = 0; i < projects.size(); i++){
                if(projects.get(i).getName().equals(projectName)){
                    projectToDelete = i;
                    break;
                }
            }
            //remove from projectTable also
            projectTree.removeCurrentNode();

            //if main project was just deleted, set it to -1, otherwise decrement by one
            if(projects.get(projectToDelete) == mainProject)
                mainProject = null;
            else if(projects.indexOf(mainProject) > projectToDelete)
                mainProject = projects.get(projectToDelete - 1); 

            //remove the project from the list of projects
            projects.remove(projectToDelete); 

            loadNewTableModels(false, null);
        }
    }//================================
    
    private void viewProjectAction(ActionEvent evt){
        Object ob = projectTree.getSelectedObject();
        if(ob instanceof Project){
            ProjectInfoPane pip = new ProjectInfoPane(((Project)ob));
        }
    }//==============================
    
    private void playPauseAction(){
        if(mainProject != null){
            
            syncTimer();
            
            if(playPause.getIcon().equals(play)){
                playPause.setIcon(pause);
                playPause.setToolTipText("Stop Work on Project");

                //look and see if today has an entry yet
                boolean entryForToday = false;
                String today = getDate();
                for(int i = 0; i < workModel.getRowCount(); i++){
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                    String date = dateFormat.format(workModel.getValueAt(i, 0));
                    if(date.equals(today)){
                        entryForToday = true;
                        workHistoryLine = i;
                    }
                }
                
                //add an entry to the work history if there isn't one from today yet.
                if(!entryForToday){
                    try{
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                        Date date = dateFormat.parse(getDate());
                        Object[] data = {date, "00:00:00"};
                        workModel.addRow(data);
                        workHistoryLine = workModel.getRowCount() - 1;
                    }catch(ParseException e){
                        System.err.println(e.toString());
                        System.err.println(e.getStackTrace());
                    }
                }
                
                projectTimer.start();
            }
            else{
                playPause.setIcon(play);
                playPause.setToolTipText("Start Work on Project");
                projectTimer.stop();
            }
            
            //save the project data
            saveProject(mainProject);
        }
    }//================================
    
    private void timerAction(ActionEvent evt){
        //update time
        seconds++;
        if(seconds >= 60){
            minutes++;
            seconds -= 60;
        }
        if(minutes >= 60){
            hours++;
            minutes -= 60;
        }
        
        //update the time on today's entry (should be top one)
        workModel.setValueAt(String.format("%02d:%02d:%02d", hours, minutes, seconds), workHistoryLine, 1);
        
        if(minutes % 5 == 0 && seconds == 0){
            saveProject(mainProject);
        }
        else{
            //the project is now different
            setMadeChanges(true);
        }
    }//========================================
    
    private void renameAction(ActionEvent evt){
        Object proj = projectTree.getSelectedObject();
        if(proj instanceof Project){
            
            String aName = JOptionPane.showInputDialog("Enter the new name:");

            //Reset any labels identifying the project
            if(((Project)proj).isMain()){
                this.setTitle(aName + " - Project Manager");
            }
            //edit any nessesary files
            ((Project)proj).rename(aName);
            setMadeChanges(true);
        }
    }//==============================
    
    private void showPopUp(JPopupMenu pm, int x, int y){
        pm.show(this, x, y);
    }//=======================
    
    private void addBugAction(ActionEvent evt){
        if(mainProject != null){
            AddIssueFrame aif = new AddIssueFrame("Bug", getDate(), getTime(), this);
        }
    }//===================================
    
    private void addFeatureAction(ActionEvent evt){
        if(mainProject != null){
            AddIssueFrame aif = new AddIssueFrame("Feature", getDate(), getTime(), this);
        }
    }//===================================
    
    private void addUpdateAction(ActionEvent evt){
        if(mainProject != null){
            AddIssueFrame aif = new AddIssueFrame("Update", getDate(), getTime(), this);
        }
    }//===================================
    
    public boolean addToIssueTableAction() {
        int row = fixedTable.getSelectedRow();
        
        //only allow this action if a row is selected
        if(row < 0){return false;}
        
        //get the ID of the issue
        String issueID = String.valueOf(fixedTable.getValueAt(row, 1));
        
        ResolveBugFrame bf = new ResolveBugFrame(issueID, getDate(), getTime(), false, this);
        
        if(bf.okPushed()){
            //find the issue in the list
            Issue iss = mainProject.getMajors().get(openMajor).getMinors().get(openMinor).getSolvedIssue(issueID);
            
            if(iss != null){
                Object[] data = iss.getIssueAsRow(true);

                //add data to the bugTableModel
                issueModel.addRow(data);

                //remove row from fixedModel
                fixedModel.removeRow(row);

                mainProject.getMajors().get(openMajor).getMinors().get(openMinor).unsolveIssue(issueID);
                
                setMadeChanges(true);
            }
            else{
                System.err.println("Could not find the solved issue.");
            }
        }
        
        return bf.okPushed();
    }//=========================================
    
    public boolean addToFixedTableAction() {
        int row = issueTable.getSelectedRow();
        
        //only allow this action if a row is selected
        if(row < 0){return false;}
        
        //get the ID of the issue
        String issueID = String.valueOf(issueTable.getValueAt(row, 1));
        
        ResolveBugFrame bf = new ResolveBugFrame(issueID, getDate(), getTime(), true, this);
        
        if(bf.okPushed()){
            //find the issue in the list
            Issue iss = mainProject.getMajors().get(openMajor).getMinors().get(openMinor).getUnsolvedIssue(issueID);
            
            if(iss != null){
                Object[] data = iss.getIssueAsRow(true);

                //add data to the bugTableModel
                fixedModel.addRow(data);

                //remove row from fixedModel
                issueModel.removeRow(row);

                mainProject.getMajors().get(openMajor).getMinors().get(openMinor).solveIssue(issueID);
                
                setMadeChanges(true);
            }
            else{
                System.err.println("Could not find the unsolved issue.");
            }
        }
        
        return bf.okPushed();
    }//============================================
    
    private void viewIssueAction(){
        //get the selected Issue
        String ID = "none";
        if(clickedOnIssueTable){
            if(issueTable.getSelectedRow() >= 0){
                int row = issueTable.convertRowIndexToModel(issueTable.getSelectedRow());   //adjust for any sorting
                ID = String.valueOf(issueModel.getValueAt(row, 1));
                IssueInfoPane iip = new IssueInfoPane(ID, mainProject.getMajors().get(openMajor).getMinors().get(openMinor).getUnsolvedIssues(), issueTable, this);
            }
        }
        else{
            if(fixedTable.getSelectedRow() >= 0){
                int row = fixedTable.convertRowIndexToModel(fixedTable.getSelectedRow());   //adjust for any sorting
                ID = String.valueOf(fixedTable.getValueAt(row, 1));
                IssueInfoPane iip = new IssueInfoPane(ID, mainProject.getMajors().get(openMajor).getMinors().get(openMinor).getSolvedIssues(), fixedTable, this);
            }
        }
        
        //refresh the issue tables
        loadIssuesToTables();
    }//================================
    
    private void aboutAction(){
        AboutDialog ad = new AboutDialog(this, "About");
    }//==============================
    
    private void projectTreeDoubleClick(){
        Object ob = projectTree.getSelectedObject();
        DefaultMutableTreeNode obParent1 = (DefaultMutableTreeNode)projectTree.getSelectedNode().getParent();
        DefaultMutableTreeNode obParent2 = (DefaultMutableTreeNode)obParent1.getParent();
        
        if(ob instanceof MinorRelease){
            //obParent1 should be a Major Release and obParent 2 should be the project
            if(obParent2.getUserObject() instanceof Project){
                if(!((Project)obParent2.getUserObject()).isMain()){
                    //if the project isn't main yet, make it so
                    setAsMain(obParent2.getUserObject().toString(), true);
                }
                
                //find which minorRelease we're dealing with
                openMajor = mainProject.getMajors().indexOf((MajorRelease)(obParent1.getUserObject()));
                openMinor = mainProject.getMajors().get(openMajor).getMinors().indexOf((MinorRelease)ob);                
            }
        }
        else if(obParent1.getUserObject() instanceof Project){
            //check to see if this project is the main one and make it the main if it isn't
            if(!((Project)obParent1.getUserObject()).isMain())
                setAsMain(obParent1.getUserObject().toString(), true);

            openMajor = mainProject.getMajors().indexOf(ob);
            openMinor = mainProject.getMajors().get(openMajor).getMinors().size() - 1;
        }
        else if(ob instanceof Project){
            //check to see if this project is the main one and make it the main if it isn't
            if(!((Project)ob).isMain())
                setAsMain(ob.toString(), true);

            openMajor = mainProject.getVersion().getMajor();
            openMinor = mainProject.getVersion().getMinor();
        }
        
        if(openMajor == -1){
            System.err.println("Could not find that major release!");
            openMajor = 0;
        }
        if(openMinor == -1){
            System.err.println("Could not find that minor release!");
            openMinor = 0;
        }
        
        loadIssuesToTables();
    }//=================================
    
    private void workHistoryDoubleClick(){
        //get the date for the work history
        int row = workHistoryTable.getSelectedRow();
        row = workHistoryTable.convertRowIndexToModel(row);   //adjust for any sorting
        
        MyTableModel model = (MyTableModel)workHistoryTable.getModel();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String date = dateFormat.format(model.getValueAt(row, 0));
        
        //find the entry in work history
        ArrayList<String> history = mainProject.getWorkHistory();
        int entryIndex = -1;
        
        for(int i = 0; i < history.size(); i++){
            if(history.get(i).startsWith(date)){
                //System.out.println("Found work history: " + history.get(i));
                entryIndex = i;
                break;
            }
        }
        
        //show work history edit dialog
        if(entryIndex != -1){
            WorkHistoryInfoPane whif = new WorkHistoryInfoPane(history, entryIndex, this);
        }else{
            System.err.println("Couldnot find work-history entry for " + date);
        }
    }//=============================================
    
    private void viewHelpAction(ActionEvent evt){
        if(Desktop.isDesktopSupported()){
            Desktop desktop = Desktop.getDesktop();
            if(desktop.isSupported(Desktop.Action.OPEN)){
                try{
                    URI uri = new URI("http://www.andrewdcox.com/software_files/projectManagerHelp/Project_Manager_Help/Home.html");
                    desktop.browse(uri);
                }
                catch(Exception e){
                    System.err.println(e.getStackTrace());
                }
                return;
            }
        }
        
        JOptionPane.showMessageDialog(this, "The help file could not be opened.\n"
                + "To manually open the help, browse to:\n"
                + "http://www.andrewdcox.com/software_files/projectManagerHelp/Project_Manager_Help/Home.html", "Sorry...", 
                JOptionPane.ERROR_MESSAGE);
    }//========================================
    
    
    
//******************************************************************************
//**************Utilities*******************************************************
    
    /**
     * Input a bug into the issueTable, and an ID is generated based on the number
     * of bugs/features/updates currently reported for the main project
     * @param name a String that names the bug descriptively
     * @param date a String representing the date and time this bug was reported
     * @param size a String representing the priority label for this bug
     * @param description a String describing the bug
     */
    public void addIssue(String type, String name, String date, String priority,
            String description, String notes, boolean isSolved){

        int idNumber = -1;
        mainProject.countIssues();
        
        if(type.equals("Bug")){
            idNumber = mainProject.getNumBugs();
        }
        else if(type.equals("Feature")){
            idNumber = mainProject.getNumFeatures();
        }
        else{
            idNumber =  mainProject.getNumUpdates();
        }
        
        //set the ID
        String ID = String.valueOf(type.charAt(0)) + String.format("%04d", idNumber);
        
        Issue issue = new Issue(type, ID, name, description, date, priority);
        issue.setProblemNotes(notes);
        
        mainProject.getMajors().get(openMajor).getMinors().get(openMinor).addIssue(issue);
        //add to table and to the ArrayList of issues
        if(isSolved)
            fixedModel.addRow(issue.getIssueAsRow(isSolved));
        else
            issueModel.addRow(issue.getIssueAsRow(isSolved));

        setMadeChanges(true);
    }//=====================================
    
    public void resolveIssue(String issueID, String dateResolved, String notes,
            boolean intoFixedTable){
        //find which issue is being resolved
        Issue iss = null;
        if(intoFixedTable)
            iss = mainProject.getMajors().get(openMajor).getMinors().get(openMinor).getUnsolvedIssue(issueID);
        else
            iss = mainProject.getMajors().get(openMajor).getMinors().get(openMinor).getSolvedIssue(issueID);
        
        if(iss != null){
            //set the info in the issue
            if(intoFixedTable){
                iss.setDateClosed(dateResolved);
                iss.setSolved(true);
                String currentNotes = iss.getSolutionNotes();
                if(currentNotes.equals(Issue.OPEN_MESSAGE))
                    iss.setSolutionNotes(notes);
                else
                    iss.setSolutionNotes(currentNotes + "\n\n" + notes);
            }
            else{
                iss.setDateClosed(Issue.OPEN_MESSAGE);
                iss.setSolved(false);
                String currentNotes = iss.getProblemNotes();
                if(currentNotes.equals(Issue.OPEN_MESSAGE))
                    iss.setProblemNotes(notes);
                else
                    iss.setProblemNotes(currentNotes + "\n\n" + notes);
            }
            
        }
    }//===========================================
    
    public void adjustWorkHistory(int index, String date, String time, String description){
        
        if(index >= 0 && index < mainProject.getWorkHistory().size()){
            String newHistory = date.concat(REGEX).concat(time).concat(REGEX).concat(description);
            mainProject.getWorkHistory().set(index, newHistory);
            loadWorkHistory(mainProject);   //reload the data to the table
            setMadeChanges(true);
        }
    }//===========================================
    
    public static String getDate(){
        Calendar theCalendar = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
       
        try{
            String date = dateFormat.format(theCalendar.getTime());
            return date;
        }catch(Exception e){
            return "--/--/----";
        }
    }//====================================
    
    public static String getTime(){
        Calendar theCalendar = Calendar.getInstance(TimeZone.getDefault());
        int hour = theCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = theCalendar.get(Calendar.MINUTE);
        int second = theCalendar.get(Calendar.SECOND);
        try{
            String time = String.format("%02d:%02d:%02d", hour, minute, second);
            return time;
        }catch(Exception e){
            return "--:--:--";
        }
    }//==================================
    
    private void syncTimer(){
        if(mainProject != null){
            //Sync the projectTimer
            if(workModel.getRowCount() > 0){

                //check to see if there is an entry for today
                String today = getDate();
                boolean entryForToday = false;
                for(int i = 0; i < workModel.getRowCount(); i++){
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                    String date = dateFormat.format(workModel.getValueAt(i, 0));
                    if(date.equals(today)){
                        workHistoryLine = i;
                        entryForToday = true;
                        break;
                    }
                }
                
                if(entryForToday){
                    //ok, so we worked on this thing today, so load the time we worked
                    String timeWorked = String.valueOf(workModel.getValueAt(workHistoryLine, 1));
                    System.out.println("Loaded time worked: " + timeWorked);
                    String[] times = timeWorked.split(":");
                    try{
                        hours = Integer.parseInt(times[0]);
                        minutes = Integer.parseInt(times[1]);
                        seconds = Integer.parseInt(times[2]);
                    }
                    catch(Exception e){
                        System.err.println("Could not parse work time for today.");
                        e.printStackTrace();
                    }
                }else{
                    //we haven't worked today, so reset time to 0
                    hours = 0; minutes = 0; seconds = 0;
                }
            }
            else{
                //there is no projectTimer data, so time should be at 0
                hours = 0; minutes = 0; seconds = 0;
            }
        }
    }//=============================================
    
    public void setMadeChanges(boolean b){
        madeChanges = b;
        JRootPane root = this.getRootPane( );
        root.putClientProperty("Window.documentModified", Boolean.valueOf(madeChanges));
    }//========================================
    
    public void setStatus(String message){
        statusLbl.setText(message);
        statusTimer.start();
    }//==============================
    
    private void saveProject(Project p){
        String projectName = p.getName();
        setStatus("Saving " + projectName);
        
        
        //check to see if there is an entry for today
        String today = getDate();
        boolean newEntry = false;
        int entryIndex = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        for(int i = 0; i < workModel.getRowCount(); i++){
            String date = dateFormat.format(workModel.getValueAt(i, 0));
            if(date.equals(today)){
                entryIndex = i;
                newEntry = true;
                break;
            }
        }
        
        
        if(newEntry){
            //get the description of today's work
            String date = dateFormat.format(workModel.getValueAt(entryIndex, 0));
        
            //find the entry in work history
            ArrayList<String> history = mainProject.getWorkHistory();
            int entryIndex2 = -1;
        
            for(int i = 0; i < history.size(); i++){
                if(history.get(i).startsWith(date)){
                    //System.out.println("Found work history: " + history.get(i));
                    entryIndex2 = i;
                    break;
                }
            }
            
            //pull the description from the split history line
            String description = "";
            try{
                description = history.get(entryIndex2).split(REGEX)[2];
            }catch(Exception e){System.err.println("Could not get description from work history");}
            
            //save the project
            mainProject.saveProject(dateFormat.format(workModel.getValueAt(entryIndex, 0)) + 
                    REGEX + workModel.getValueAt(entryIndex, 1) + REGEX + description);
        }else
            mainProject.saveProject(null);
        
        setMadeChanges(false);
        setStatus("Saved " + projectName);
    }//===============================================
    
    private void loadIssues(Project p){
        ArrayList<Issue> solved = p.getMajors().get(openMajor).getMinors().get(openMinor).getSolvedIssues();
        ArrayList<Issue> unsolved = p.getMajors().get(openMajor).getMinors().get(openMinor).getUnsolvedIssues();
        int i;
        //add the issues to their tables
        for(i = 0; i < solved.size(); i++){
            fixedModel.addRow(solved.get(i).getIssueAsRow(true));
        }
        for(i = 0; i < unsolved.size(); i++){
            issueModel.addRow(unsolved.get(i).getIssueAsRow(false));
        }
    }//===========================================
    
    private void loadWorkHistory(Project p){
        deleteAllRows(workModel);
        
        ArrayList<String> work = p.getWorkHistory();
        if(work != null){
            for(int i = 0; i < work.size(); i++){
                String[] parsedData = work.get(i).split(REGEX);
                
                if(parsedData.length >= workHistoryTable.getColumnCount()){
                    //parse the date from String
                    try{
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                        Date date = dateFormat.parse(parsedData[0]);
                        Object[] data = {date, parsedData[1]};
                        workModel.addRow(data);
                    }catch(ParseException e){
                        System.err.println("Could not parse date from file");
                        System.err.println(e.getStackTrace());
                    }
                }
                else{
                    System.err.println("The work history data doesn't have enough info.");
                }
            }
            try{
                //sort the workHistory Table - toggle twice to keep order the same
                if(workHistoryTable.getRowCount() > 0){
                    workHistoryTable.getRowSorter().toggleSortOrder(0);
                    workHistoryTable.getRowSorter().toggleSortOrder(0);
                }
            }
            catch(Exception e){
                System.err.println("Could not sort work History");
                e.printStackTrace();
            }
        }
    }//====================================
    
    private void loadProjectData(File filePath){
        setStatus("Opening " + filePath.getName());
        
        //add the project to the list
        Project newProject = new Project(filePath, filePath.getName(), false, projects.size());
        projects.add(newProject);
        setAsMain(newProject.getName(), false);
        //select the Projects node, which is the root and add the project to the tree
        projectTree.selectNodesMatching("Projects", false);
        //first, add this project to the projectList
        projectTree.addObject(newProject);

        //remember which row the newly created project is in
        int projectRow = projectTree.getRowCount() - 1;

        //add all releases of the project
        for(int maj = 0; maj < newProject.getMajors().size(); maj++){
            //add the major revision
            projectTree.setSelectionRow(projectRow);
            projectTree.addObject(newProject.getMajors().get(maj));
            projectTree.setSelectionRow(projectTree.getRowCount() - 1);
            for(int min = 0; min < newProject.getMajors().get(maj).getMinors().size(); min++){
                projectTree.addObject(newProject.getMajors().get(maj).getMinors().get(min));
            }
        }

        openMajor = newProject.getVersion().getMajor();
        openMinor = newProject.getVersion().getMinor();
        
        loadWorkHistory(newProject);
        loadIssues(newProject);
        
        setMadeChanges(false);
        setStatus("Opened " + newProject.getName());
    }//=================================
    
    private void deleteAllRows(DefaultTableModel tm){
        while(tm.getRowCount() > 0)
            tm.removeRow(0);
    }//=================================
    
    private void closingOperation(){
        if(madeChanges){
            int response = JOptionPane.showConfirmDialog(this, 
                      "You have made changes since you last saved."
                      + "\nWould you like to save before exiting?", 
                      "Save?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, save);
            switch(response){
                case(JOptionPane.YES_OPTION):
                    saveProject(mainProject);
                    System.exit(0);
                    break;
                case(JOptionPane.NO_OPTION):
                    System.exit(0);
                    break;
                default:
                    return;
            }
        }
        
        //delete old openprojects.txt file so we can write a new one
        FileIO.deleteFile(FILE_PATH.concat(".Projects/openprojects.txt"));
        //save the currently open projects so they open when the program is opened next
        for(int i = 0; i < projects.size(); i++){
            String string = projects.get(i).getDirectory();
            string = string.concat(REGEX).concat(String.valueOf(projects.get(i).isMain()));
            FileIO.writeToFile(FILE_PATH.concat(".Projects/openprojects.txt"), string);
        }
    }//======================================
    
    private void getRows(){
        System.out.println(fixedTable.getModel().getRowCount());
    }
            
}