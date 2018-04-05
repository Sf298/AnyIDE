/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.anyide;

import gui.MyClass;
import imported.GetterFrame;
import java.awt.Component;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import javax.naming.InvalidNameException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import org.json.simple.JSONObject;

/**
 *
 * @author saud
 */
public class Project {
    
    public String name;
    public String author;
    public String version;
    public MyCompiler selectedCompiler;
    public String mainClass;
    
    public File projectFolder;
    public File metaFile;
    public ArrayList<MyClass> classFiles = new ArrayList<>();
    public JFrame parent;
    
    /**
     * creates and it's files
     * @param showEditor
     * @param isNew
     */
    public Project(boolean showEditor, boolean isNew) {
        if(showEditor) {
            openEditor(isNew);
        }
    }
    
    public static Project load(File projectFolder) throws FileNotFoundException, InvalidNameException {
        Project out = new Project(false, false);
        out.projectFolder = projectFolder;
        out.metaFile = new File(projectFolder.getPath()+"/"+Viewer.projectMetaFileName);
        out.loadJSON();
        out.fixMainClass(null);
        return out;
    }
    
    public static Project findProjByName(String name) {
        for(Project p : IDEProps.storedProjects) {
            if(p.name.equals(name))
                return p;
        }
        return null;
    }
    
    private void loadJSON() throws FileNotFoundException, InvalidNameException {
        if(projectFolder!=null && projectFolder.exists()) {
            if(metaFile==null || !metaFile.exists()) 
                throw new InvalidNameException("Meta file not found!");
            JSONObject obj = (JSONObject) IDEProps.readJSON(metaFile); // load JSON object
            name = (String) obj.get("name");
            author = (String) obj.get("author");
            version = (String) obj.get("version");
            selectedCompiler = IDEProps.compilers.get((String) obj.get("selectedCompiler"));
            mainClass = (String) obj.get("mainClass");
            
            refreshClasses();
        } else {
            throw new FileNotFoundException("Error: Foler "+projectFolder+" does not exist!");
        }
    }
    
    public void openEditor() {
        openEditor(false);
    }
    private void openEditor(boolean isNew) {
        boolean debug = true;
        while(true) {
            GetterFrame gf = new GetterFrame(parent, (isNew)?"Create Project":"Edit Project");
            JTextField nameField = gf.addTextField("Name");
            nameField.setText(name);
            if(debug) nameField.setText("Proj1");
            JTextField authorField = gf.addTextField("Company");
            authorField.setText(author);
            if(debug) authorField.setText("SaudsProjects");
            JTextField versionField = gf.addTextField("Version");
            versionField.setText(version);
            if(debug) versionField.setText("1");
            JTextField compilerField = gf.addTextField("Compiler");
            compilerField.setText((selectedCompiler==null) ? "" : selectedCompiler.toString());
            if(debug) compilerField.setText("Java8");
            JTextField mainClassField = gf.addTextField("Main Class");
            mainClassField.setText(mainClass);
            if(debug) mainClassField.setText("Main.java");
            JTextField folderField = null;
            if(isNew) {
                folderField = gf.addDirectoryChooserField("Parent Directory", "Choose File", "");
                folderField.setText((projectFolder==null) ? "" : projectFolder.getAbsolutePath());
                if(debug) folderField.setText("C:\\Users\\demon\\Desktop\\TEMP\\New Folder (2)");
            }
            int result = gf.showAndComplete(500, 350);
            if(result == gf.RESULT_OK) {
                boolean canQuit = true;
                if(!nameField.getText().matches(".*[^A-Za-z0-9 _].*") && nameField.getText().length() > 0) {
                    name = nameField.getText();
                } else canQuit = false;
                if(!authorField.getText().matches(".*[^A-Za-z0-9.].*") && authorField.getText().length() > 0) {
                    author = authorField.getText();
                } else canQuit = false;
                if(!versionField.getText().matches(".*[^A-Za-z0-9.].*") && versionField.getText().length() > 0) {
                    version = versionField.getText();
                } else canQuit = false;
                if(!compilerField.getText().matches(".*[^A-Za-z0-9 _.].*") && compilerField.getText().length() > 0) {
                    String compName = compilerField.getText();
                    selectedCompiler = IDEProps.compilers.get(compName);
                } else canQuit = false;
                if(!mainClassField.getText().matches(".*[^A-Za-z0-9.].*") && mainClassField.getText().length() > 0) {
                    mainClass = mainClassField.getText();
                } else canQuit = false;
                if(folderField!=null && !folderField.getText().matches(".*[^A-Za-z0-9 _.:/\\\\()].*") && folderField.getText().length() > 0) {
                    projectFolder = new File(folderField.getText()+"/"+name+"/");
                    metaFile = new File(projectFolder.getPath()+"/"+Viewer.projectMetaFileName);
                } else {
                    canQuit = false;
                }
                if(canQuit) {
                    save();
                    if(!fixMainClass(gf.getFrame())) {
                        continue;
                    }
                    break;
                }
            } else {
                break;
            }
        }
    }
    
    public void save() {
        JSONObject obj = new JSONObject();
        obj.put("name", name);
        obj.put("author", author);
        obj.put("version", version);
        obj.put("selectedCompiler", selectedCompiler.name);
        obj.put("mainClass", mainClass);

        IDEProps.writeString(metaFile, obj.toJSONString());
    }
    
    public void addClass(String className) {
        File newClassFile = new File(projectFolder+"/src/"+className);
        MyClass c = new MyClass(this, newClassFile);
        c.save();
        classFiles.add(c);
        refreshClasses();
    }
    public boolean containsClass(String className) {
        return classFiles.contains(className);
    }
    public MyClass getClass(String className) {
        for(MyClass mc : classFiles) {
            if(mc.toString().equals(className)) {
                return mc;
            }
        }
        return null;
    }
    
    public boolean fixMainClass(Component c) {
        File mainClassFile = new File(projectFolder+"/src/"+mainClass);
        if(!mainClassFile.exists()) {
            int dialogResult = JOptionPane.showConfirmDialog(c, "Main File doesn't exist, would you like to create it?","Warning",JOptionPane.YES_NO_OPTION);
            if(dialogResult == JOptionPane.YES_OPTION){
                addClass(mainClass);
                return true;
            }
            return false;
        }
        return true;
    }
    
    private void refreshClasses() {
        File[] classes = new File(projectFolder+"/src").listFiles(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isFile() && !f.getPath().contains(Viewer.projectMetaFileName);
            }
        });
        for(int i=0; i<classes.length; i++) {
            if(!containsClass(classes[i])) {
                classFiles.add(new MyClass(this, classes[i]));
            }
        }
    }
    
    private boolean containsClass(File f) {
        for(MyClass c : classFiles) {
            if(c.classFile.equals(f)) return true;
        }
        return false;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
}
