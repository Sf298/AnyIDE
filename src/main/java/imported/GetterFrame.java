/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imported;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author Saud
 */
public class GetterFrame {
    
    public static final int RESULT_OK = 0;
    public static final int RESULT_CANCEL = 1;
    
    private boolean isComplete = false;
    private JDialog frame;
    private JPanel internalJPanel;
    private GridBagConstraints c = new GridBagConstraints();
    private int result;
    
    public JDialog getFrame() {
        return frame;
    }
    
    public GetterFrame(JFrame parentFrame, JComponent contentPanel, String title) {
        internalJPanel = new JPanel(new GridBagLayout());
        c.gridy++;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(7, 0, 7, 0);
                
        if(contentPanel!=null)
            internalJPanel.add(contentPanel);
        
        frame = new JDialog(parentFrame, title, true);
        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {
                isComplete = true;
                result = RESULT_CANCEL;
            }
            @Override
            public void windowClosed(WindowEvent e) {}
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {}
            @Override
            public void windowDeactivated(WindowEvent e) {}
        });
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
            JButton doneButton = new JButton("Done");
            doneButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    frame.setVisible(false);
                    frame.dispose();
                    isComplete = true;
                    result = RESULT_OK;
                }
            });
            mainPanel.add(doneButton);
        
        frame.add(mainPanel, BorderLayout.SOUTH);
        frame.add(internalJPanel, BorderLayout.CENTER);
        
    }
    public GetterFrame(JFrame parentFrame, String title) {
        this(parentFrame, null, title);
    }
    
    /**
     * Shows the dialog. Must call complete() afterwards.
     * @param width width of the dialog
     * @param height height of the dialog
     */
    public void showDialog(int width, int height) {
        frame.setSize(width, height);
        frame.setVisible(true);
    }
    
    /*public void exitOnClose(boolean exit) {
        if(exit) {
            frame.setDefaultCloseOperation(JDialog.EXIT_ON_CLOSE);
        } else {
            frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        }
    }*/
    
    /**
     * Waits for the user to click 'done'.
     * @return RESULT_OK or RESULT_CANCEL
     */
    public int complete() {
        while(!isComplete) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException ex) {
                //Logger.getLogger(TextGetter2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }
    
    /**
     * Shows the dialog then waits for the user to click 'done'. Do not  need to call complete().
     * @param width width of the dialog
     * @param height height of the dialog
     * @return RESULT_OK or RESULT_CANCEL
     */
    public int showAndComplete(int width, int height) {
        showDialog(width, height);
        return complete();
    }
    
    
    /**
     * initialises a plain text field
     * @param title The label to use
     * @return [The component to add to the frame, the text field to get the text]
     */
    public static JComponent[] getTextField(String title) {
        JPanel out = new JPanel(new BorderLayout());
        
            JLabel label = new JLabel(title + ": ");
            out.add(label, BorderLayout.WEST);
            
            JTextField field = new JTextField();
            field.setPreferredSize(new Dimension(250, 0));
            out.add(field, BorderLayout.CENTER);
        
        return new JComponent[] {out, field};
    }
    public JTextField addTextField(String title) {
        JComponent[] components = getTextField(title);
        internalJPanel.add(components[0], c);
        c.gridy++;
        return (JTextField) components[1];
    }
    
    /**
     * initialises a password text field
     * @param title The label to use
     * @return [The component to add to the frame, the text field to get the text]
     */
    public static JComponent[] getHiddenTextField(String title) {
        JPanel out = new JPanel(new BorderLayout());
        
            JLabel label = new JLabel(title + ": ");
            out.add(label, BorderLayout.WEST);
            
            JTextField field = new JPasswordField();
            field.setPreferredSize(new Dimension(250, 0));
            out.add(field, BorderLayout.CENTER);
        
        return new JComponent[] {out, field};
    }
    public JTextField addHiddenTextField(String title) {
        JComponent[] components = getHiddenTextField(title);
        internalJPanel.add(components[0], c);
        c.gridy++;
        return (JTextField) components[1];
    }
    
    /**
     * initialises a directory selection field
     * @param title The label to use
     * @param dialogTitle The title for the FileChooser when opened
     * @return [The component to add to the frame, the JTextField to get the text]
     */
    public static JComponent[] getDirectoryChooserField(String title, String dialogTitle, String defaultPath) {
        JPanel out = new JPanel(new BorderLayout());
        
            JLabel label = new JLabel(title + ": ");
            out.add(label, BorderLayout.WEST);
            
            JTextField field = new JTextField();
            field.setText(defaultPath);
            field.setPreferredSize(new Dimension(250, 0));
            out.add(field, BorderLayout.CENTER);
            
            JButton button = new JButton("...");
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setCurrentDirectory(new File(field.getText()));
                    chooser.setDialogTitle(dialogTitle);
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    chooser.setAcceptAllFileFilterUsed(false);

                    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                      field.setText(chooser.getSelectedFile().getAbsolutePath());
                    }
                }
            });
            out.add(button, BorderLayout.EAST);
        
        return new JComponent[] {out, field};
    }
    public JTextField addDirectoryChooserField(String title, String dialogTitle, String defaultPath) {
        JComponent[] components = getDirectoryChooserField(title, dialogTitle, defaultPath);
        internalJPanel.add(components[0], c);
        c.gridy++;
        return (JTextField) components[1];
    }
    
    /*private JPanel addBoolPropertyField(String title, String key) {
        JPanel out = new JPanel(new BorderLayout());
        
            JLabel label = new JLabel(title + ": ");
            out.add(label, BorderLayout.WEST);
            
            JCheckBox box = new JCheckBox();
            box.setSelected(Boolean.valueOf(si.getProp(key)));
            box.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    si.putProp(key, box.isSelected()+"");
                }
            });
            out.add(box, BorderLayout.CENTER);
        
        return out;
    }
    
    private JPanel addLabelField(String title, String text) {
        JPanel out = new JPanel(new BorderLayout());
        
            JLabel label = new JLabel(title + ": ");
            out.add(label, BorderLayout.WEST);
            
            JLabel valueLabel = new JLabel(text);
            out.add(valueLabel, BorderLayout.CENTER);
        
        return out;
    }*/
    
}
