/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import com.mycompany.anyide.IDEProps;
import com.mycompany.anyide.Project;
import java.awt.BorderLayout;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author saud
 */
public class ConsoleComponent extends JPanel {
    
    public static final String TOKEN_BIN_DIR = "<bin>";
    public static final String TOKEN_SRC_DIR = "<src>";
    public static final String TOKEN_MAIN_CLASS = "<main>";
    public static final String TOKEN_MAIN_CLASS_NAME = "<mainName>";
    
    private JTextPane textPanel;
    private StyledDocument doc;
    
    public ConsoleComponent() {
        super(new BorderLayout());
        textPanel = new JTextPane();
        doc = textPanel.getStyledDocument();
        clearLog();
        textPanel.setEditable(false);
        JPanel tempPanel = new JPanel(new BorderLayout());
        tempPanel.add(textPanel, BorderLayout.CENTER);
        JScrollPane scrollPane = new JScrollPane(tempPanel);
        this.add(scrollPane, BorderLayout.CENTER);
    }
    
    public void clearLog() {
        textPanel.setText("");
        SimpleAttributeSet keyWord = new SimpleAttributeSet();
        StyleConstants.setFontFamily(keyWord, "Monospaced");
        doc.setCharacterAttributes(0, textPanel.getText().length(), keyWord, true);
    }
    
    public void parseCommand(String command, Project p, String functionName) {
        command = command.replaceAll(TOKEN_BIN_DIR, p.projectFolder.getPath().replace('\\', '/')+"/bin/");
        command = command.replaceAll(TOKEN_SRC_DIR, p.projectFolder.getPath().replace('\\', '/')+"/src/");
        command = command.replaceAll(TOKEN_MAIN_CLASS, p.mainClass);
        command = command.replaceAll(TOKEN_MAIN_CLASS_NAME, p.mainClass.substring(0, p.mainClass.indexOf(".")));
        
        for(String line : command.split("\n")) {
            if(line.length()>0) {
                runCommand(line);
            }
        }
    }
    
    private Process p;
    private Thread t;
    private void runCommand(String command) {
        if(t != null && t.isAlive())
            return;
        
        System.out.println(command);
        String[] tokens = tokenise(command);
        
        Worker w = new Worker(tokens);
        try {
            w.doInBackground();
        } catch (Exception ex) {
            Logger.getLogger(ConsoleComponent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    class Worker extends SwingWorker<Void, Object> {
        private Scanner inStr;    
        private Scanner errStr;
        private PrintWriter outStr;
        private String[] tokens;
        
        public Worker(String[] tokens) {
            this.tokens = tokens;
        }

        @Override
        protected Void doInBackground() throws Exception {
            ProcessBuilder builder = new ProcessBuilder(tokens);
            builder.redirectErrorStream(true);
            p = builder.start();

            inStr = new Scanner(new BufferedInputStream(p.getInputStream()));
            errStr = new Scanner(new BufferedInputStream(p.getErrorStream()));
            outStr = new PrintWriter(new BufferedOutputStream(p.getOutputStream()));
            
            int textLen = textPanel.getText().length();
            String[] out = new String[1];
            DocumentListener listener = new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    out[0] = textPanel.getText().substring(textLen);
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    out[0] = textPanel.getText().substring(textLen);
                }

                @Override
                public void changedUpdate(DocumentEvent e) {}
            };
            
            doc.addDocumentListener(listener);
            textPanel.setEditable(true);
            while(true) {
                if(inStr.hasNextLine()) {
                    publish(inStr.nextLine(), listener);
                }
                if(errStr.hasNextLine()) {
                    publish(errStr.nextLine(), listener);
                }
                
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ConsoleComponent.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                //if(out[0] != null && out[0].contains("\n"))
                    break;
            }
            doc.removeDocumentListener(listener);
            textPanel.setEditable(false);

            if(out[0]!=null) {
                System.out.println("setting = "+out[0]);
                outStr.append(out[0]);
                outStr.flush();
            }
            
            return null;
        }

        @Override
        protected void process(List<Object> chunks){
            //this is executed in EDT you can update a label for example
            String str = (String) chunks.get(0);
            DocumentListener listener = (DocumentListener) chunks.get(1);
            
            SimpleAttributeSet keyWord = new SimpleAttributeSet();
            StyleConstants.setFontFamily(keyWord, "Monospaced");
            System.out.println("OUT = "+str);
            System.out.println(listener);
            doc.removeDocumentListener(listener);
            try {
                doc.insertString(doc.getLength(), str+"\n", keyWord);
            } catch (BadLocationException ex) {
                Logger.getLogger(ConsoleComponent.class.getName()).log(Level.SEVERE, null, ex);
            }
            doc.addDocumentListener(listener);
        }
    }
    
    private String[] tokenise(String command) {
        String[] tokens = command.split(" (?=(?:[^']*'[^']*')*[^']*$)");
        for(int i=0; i<tokens.length; i++) {
            if(tokens[i].startsWith("'") && tokens[i].endsWith("'"))
                tokens[i] = tokens[i].substring(1, tokens[i].length()-1);
        }
        return tokens;
    }
    
}
