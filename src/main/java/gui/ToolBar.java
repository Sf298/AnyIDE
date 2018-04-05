/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import com.mycompany.anyide.IDEProps;
import com.mycompany.anyide.MyCompiler;
import com.mycompany.anyide.Project;
import imported.GetterFrame;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InvalidNameException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author saud
 */
public class ToolBar extends JPanel {
    
    private EditorTabPane editorTabs;
    private JFrame parentFrame;
    private ProjectViewerComponent projectViewer;
    private ConsoleComponent console;
    
    public ToolBar(JFrame parentFrame, EditorTabPane editorTabs, ProjectViewerComponent projectViewer, ConsoleComponent console) {
        super(new BorderLayout());
        
        this.editorTabs = editorTabs;
        this.parentFrame = parentFrame;
        this.projectViewer = projectViewer;
        this.console = console;
        
        JPanel mainPanel = new JPanel();
        Font buttonFont = new Font(Font.SANS_SERIF, Font.PLAIN, 9);
        
        JButton saveButton = new JButton("Save");
         saveButton.setToolTipText("Save");
         saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                save();
            }
         });
         saveButton.setFont(buttonFont);
         mainPanel.add(saveButton);
         
        JButton saveAllButton = new JButton("Save All");
         saveAllButton.setToolTipText("Save All");
         saveAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAll();
            }
         });
         saveAllButton.setFont(buttonFont);
         mainPanel.add(saveAllButton);
         
        JButton openProjButton = new JButton("Open Project");
         openProjButton.setToolTipText("Open Project");
         openProjButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openProject();
            }
         });
         openProjButton.setFont(buttonFont);
         mainPanel.add(openProjButton);
         
        JButton newProjButton = new JButton("New Project");
         newProjButton.setToolTipText("New Project");
         newProjButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newProject();
            }
         });
         newProjButton.setFont(buttonFont);
         mainPanel.add(newProjButton);
         
        JButton newClassButton = new JButton("New Class");
         newClassButton.setToolTipText("New Class");
         newClassButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newClass();
            }
         });
         newClassButton.setFont(buttonFont);
         mainPanel.add(newClassButton);
         
        /*JButton refButton = new JButton("Refresh");
         refButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                projectViewer.refreshProjects();
            }
         });
         refButton.setFont(buttonFont);
         mainPanel.add(refButton);*/
         
        JButton cleanAndBuildButton = new JButton("Clean & Build");
         cleanAndBuildButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(parentFrame,
                            "Work In Progress", "Error", JOptionPane.WARNING_MESSAGE);
            }
         });
         cleanAndBuildButton.setFont(buttonFont);
         mainPanel.add(cleanAndBuildButton);
         
         JButton buildButton = new JButton("Build");
         buildButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /*JOptionPane.showMessageDialog(parentFrame,
                            "Work In Progress", "Error", JOptionPane.WARNING_MESSAGE);*/
                build();
            }
         });
         buildButton.setFont(buttonFont);
         mainPanel.add(buildButton);
         
         JButton buildAndRunButton = new JButton("Run");
         buildAndRunButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /*JOptionPane.showMessageDialog(parentFrame,
                            "Work In Progress", "Error", JOptionPane.WARNING_MESSAGE);*/
                run();
            }
         });
         buildAndRunButton.setFont(buttonFont);
         mainPanel.add(buildAndRunButton);
         
        //TODO: run
        //TODO: build
        //TODO: clean and build
        
        this.add(mainPanel, BorderLayout.WEST);
    }
    
    public void save() {
        MyClass c = editorTabs.getSelectedClass();
        if(c!=null) c.save();
    }
    public void saveAll() {
        ArrayList<MyClass> classes = editorTabs.getOpenClasses();
        for(MyClass c : classes) {
            c.save();
        }
    }
    public void newProject() {
        Project p = new Project(true, true);
        IDEProps.storedProjects.add(p);
        projectViewer.refreshProjects();
    }
    public void openProject() {
        while(true) {
            GetterFrame gf = new GetterFrame(parentFrame, "Open Project");
            JTextField pathField = gf.addDirectoryChooserField("Select Folder", "Select Project Folder", "");
            int result = gf.showAndComplete(500, 250);
            if(result == GetterFrame.RESULT_OK) {
                try {
                    File f = new File(pathField.getText());
                    Project p = Project.load(f);
                    IDEProps.storedProjects.add(p);
                    projectViewer.refreshProjects();
                    break;
                } catch (FileNotFoundException ex) {
                    JOptionPane.showMessageDialog(gf.getFrame(),
                            "Folder not found!", "Error", JOptionPane.WARNING_MESSAGE);
                    //Logger.getLogger(ToolBar.class.getName()).log(Level.SEVERE, null, ex);
                    
                } catch (InvalidNameException ex) {
                    JOptionPane.showMessageDialog(gf.getFrame(),
                                "Not A Project!", "Error", JOptionPane.WARNING_MESSAGE);
                    //Logger.getLogger(ToolBar.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                break;
            }
        }
    }
    public void newClass() {
        Project p = editorTabs.getSelectedClass().project; // TODO: change to selected proj in projTree
        String name = null;
        while(name==null || p.containsClass(name)) {
            GetterFrame gf = new GetterFrame(parentFrame, "Open Project");
            JTextField pathField = gf.addTextField("File Name (e.g. MyClass.java)");
            int result = gf.showAndComplete(500, 250);
            if(result == GetterFrame.RESULT_OK) {
                name = pathField.getText();
            } else {
                break;
            }
        }
        if(name!=null) {
            p.addClass(name);
            projectViewer.refreshProjects();
        }
    }
    public void build() {
        console.clearLog();
        Project p = editorTabs.getSelectedClass().project;
        MyCompiler mc = p.selectedCompiler;
        console.parseCommand(mc.onCompile, p, "Build");
    }
    public void run() {
        console.clearLog();
        Project p = editorTabs.getSelectedClass().project;
        MyCompiler mc = p.selectedCompiler;
        console.parseCommand(mc.onCompile, p, "Build");
        console.parseCommand(mc.onRun, p, "Run");
    }
    
}

