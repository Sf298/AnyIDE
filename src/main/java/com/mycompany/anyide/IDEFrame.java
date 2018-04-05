/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.anyide;

import static com.mycompany.anyide.IDEProps.screenPosX;
import static com.mycompany.anyide.IDEProps.screenPosY;
import gui.*;
import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

/**
 *
 * @author saud
 */
public class IDEFrame extends JFrame {
    
    private EditorTabPane editor;
    private ConsoleComponent console;
    private ProjectViewerComponent projectViewer;
    public static ToolBar toolBar;
    
    private JSplitPane editor_console;
    private JSplitPane editorConsole_project;
    
    public IDEFrame() {
        super("AnyIDE "+Viewer.version);
        
        editor = new EditorTabPane();
        projectViewer = new ProjectViewerComponent(editor);
        console = new ConsoleComponent();
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        editor_console = new JSplitPane(JSplitPane.VERTICAL_SPLIT, editor, console);
        editorConsole_project = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, projectViewer, editor_console);
        mainPanel.add(editorConsole_project, BorderLayout.CENTER);
        
        toolBar = new ToolBar(this, editor, projectViewer, console);
        mainPanel.add(toolBar, BorderLayout.NORTH);
        
        add(mainPanel);
        
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent componentEvent) {
                removeComponentListener(this);
                editor_console.setDividerLocation(IDEProps.vertScroll);
                editorConsole_project.setDividerLocation(IDEProps.horrizScroll);
                IDEProps.runOnPostLoadListeners();
            }
        });
        IDEProps.addUpdateListener(new UpdateListener() {
            @Override
            public void onExit() {
                updateWindowParams();
            }
        });
    }
    
    private void updateWindowParams() {
        Point p = this.getLocationOnScreen();
        screenPosX = p.x;
        screenPosY = p.y;
                
        IDEProps.isFullScreen = (this.getExtendedState() == JFrame.MAXIMIZED_BOTH);
        if(!IDEProps.isFullScreen) {
            IDEProps.screenW = this.getWidth();
            IDEProps.screenH = this.getHeight();
        }
        
        IDEProps.vertScroll = dividerPos(editor_console);
        IDEProps.horrizScroll = dividerPos(editorConsole_project);
    }
    private double dividerPos(JSplitPane pane) {
        if(pane.getOrientation() == JSplitPane.VERTICAL_SPLIT) {
            return pane.getDividerLocation() / ((double)pane.getHeight()-pane.getDividerSize());
        } else if(pane.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
            return pane.getDividerLocation() / ((double)pane.getWidth()-pane.getDividerSize());
        }
        return -1;
    }
    
}
