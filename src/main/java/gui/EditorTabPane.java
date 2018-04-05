/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import com.mycompany.anyide.IDEProps;
import com.mycompany.anyide.Project;
import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 *
 * @author saud
 */
public class EditorTabPane extends JPanel {
    
    private JTabbedPane tabbedPane;
    
    public EditorTabPane() {
        super(new BorderLayout());
        tabbedPane = new JTabbedPane();
        this.add(tabbedPane, BorderLayout.CENTER);
        ArrayList<Integer> toRemove = new ArrayList<>();
        IDEProps.addUpdateListener(new UpdateListener() {
            @Override
            public void onPostLoad() {
                for(int i=0; i<IDEProps.openClasses.size(); i++) {
                    String[] strArr = IDEProps.openClasses.get(i);
                    Project p = Project.findProjByName(strArr[0]);
                    if(p!=null) {
                        MyClass mc = p.getClass(strArr[1]);
                        if(mc!=null) {
                            addTab(mc);
                        }
                    } else {
                        toRemove.add(i);
                    }
                }
                for(Integer i : toRemove) {
                    IDEProps.openClasses.remove((int) i);
                }
            }
            @Override
            public void onExit() {
                for(Component comp : tabbedPane.getComponents()) {
                    MyClass mc = (MyClass) comp;
                    IDEProps.openClasses.add(new String[] {mc.project.name, mc.toString()});
                }
            }
        });
    }
    
    public void addTab(MyClass mc) {
        tabbedPane.addTab(mc.toString(), mc);
    }
    
    public MyClass getSelectedClass() {
            return (MyClass) tabbedPane.getSelectedComponent();
    }
    public ArrayList<MyClass> getOpenClasses() {
        ArrayList<MyClass> out = new ArrayList<>();
        for(int i=0; i<tabbedPane.getTabCount(); i++) {
            out.add((MyClass) tabbedPane.getComponentAt(i));
        }
        return out;
    }
    
}
