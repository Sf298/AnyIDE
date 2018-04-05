/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import com.mycompany.anyide.IDEFrame;
import com.mycompany.anyide.IDEProps;
import TextPane.Mod.Language;
import com.mycompany.anyide.Project;
import TextPane.Mod.SyntaxHighlighting;
import TextPane.Mod.TextLineNumber;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

/**
 *
 * @author saud
 */
public class MyClass extends JPanel {
    
    public Project project;
    public File classFile;
    private JTextPane textPane;
    private StyledDocument doc;
    
    public MyClass(Project project, File classFile) {
        super(new BorderLayout());
        this.classFile = classFile;
        this.textPane = new JTextPane();
        this.project = project;
        doc = new DefaultStyledDocument() {
            @Override
            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                String temp = "";
                for(int i=0; i<IDEProps.spacesPerTab; i++) {
                    temp += " ";
                }
                str = str.replaceAll("\t", temp);
                super.insertString(offs, str, a);
            }
        };
        textPane.setStyledDocument(doc);
        SyntaxHighlighting synHigh = new SyntaxHighlighting(textPane, 
                IDEProps.languages.get(project.selectedCompiler.language),
                IDEProps.colourSchemes.get(IDEProps.selectedColourScheme));
        
        
        // Listen for undo and redo events
        final UndoManager undo = new UndoManager();
        doc.addUndoableEditListener(new UndoableEditListener() {
            @Override
            public void undoableEditHappened(UndoableEditEvent e) {
                undo.addEdit(e.getEdit());
            }
        });
        
        // Create an undo action and add it to the text component
        textPane.getActionMap().put("Undo", new AbstractAction("Undo") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (undo.canUndo()) {
                        undo.undo();
                    }
                } catch (CannotUndoException e) {
                }
            }
       });
        // Bind the undo action to ctl-Z
        textPane.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");
        
        textPane.getActionMap().put("Redo", new AbstractAction("Redo") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (undo.canRedo()) {
                        undo.redo();
                    }
                } catch (CannotRedoException e) {
                }
            }
        });
        textPane.getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");
        
        JPanel noWrap = new JPanel(new BorderLayout());
        noWrap.add(textPane, BorderLayout.CENTER);
        JScrollPane scrollPane = new JScrollPane(noWrap);
        TextLineNumber tln = new TextLineNumber(textPane);
        scrollPane.setRowHeaderView(tln);
        scrollPane.getVerticalScrollBar().setUnitIncrement(17); // 6-8, 12-17, 24-33, 48-64
        this.add(scrollPane, BorderLayout.CENTER);
        load();
        
        textPane.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if ((e.getKeyCode() == KeyEvent.VK_S)
                            && e.isControlDown() && e.isShiftDown()) {
                    // ctrl-shift-s
                    System.out.println("KEY PRESS saveAll");
                    IDEFrame.toolBar.saveAll();
                } else if ((e.getKeyCode() == KeyEvent.VK_S) && e.isControlDown()) {
                    // ctrl-s
                    System.out.println("KEY PRESS save");
                    save();
                } else if ((e.getKeyCode() == KeyEvent.VK_F5)) {
                    // f5
                    System.out.println("KEY PRESS run");
                    IDEFrame.toolBar.run();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        });
    }
    
    public void save() {
        try {
            //TODO: save on close
            classFile.getParentFile().mkdirs();
            FileWriter fw = new FileWriter(classFile);
            fw.write(textPane.getText());
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(IDEProps.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void load() {
        if(!classFile.exists()) {
            save();
        }
        try {
            //TODO: save on close
            Scanner in = new Scanner(classFile);
            StringBuilder sb = new StringBuilder();
            while(in.hasNextLine()) {
                sb.append(in.nextLine());
                sb.append("\n");
            }
            textPane.setText(sb.toString());
        } catch (IOException ex) {
            Logger.getLogger(IDEProps.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public String toString() {
        return classFile.getName();
    }
    
    @Override
    public boolean equals(Object o) {
        if(o instanceof MyClass) {
            return classFile.equals(((MyClass)o).classFile);
        } else if(o instanceof File) {
            return classFile.equals((File)o);
        } else if(o instanceof String) {
            return classFile.getName().contains((String)o);
        }
        return false;
    }
    
    
    
}
