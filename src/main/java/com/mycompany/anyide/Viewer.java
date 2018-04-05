/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.anyide;

import javax.swing.JFrame;

/**
 *
 * @author saud
 */
public class Viewer {
    
    public static String programName = "AnyIDE";
    public static String version = "v0.1";
    public static String projectMetaFileName = "AnyIDE.proj";
    public static boolean hasStarted = false;
    
    public static void main(String[] args) {
        // load settings
        IDEProps.initAll();
        
        IDEProps.mainFrame = new IDEFrame();
        if(IDEProps.isFullScreen) {
            IDEProps.mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        }
        IDEProps.mainFrame.setSize(IDEProps.screenW, IDEProps.screenH);
        IDEProps.mainFrame.setLocation(Math.max(IDEProps.screenPosX, 0), Math.max(IDEProps.screenPosY, 0));
        IDEProps.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        IDEProps.mainFrame.setVisible(true);
    }
    
}
