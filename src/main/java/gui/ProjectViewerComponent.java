/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import com.mycompany.anyide.IDEProps;
import com.mycompany.anyide.Project;
import java.awt.BorderLayout;
import java.awt.MouseInfo;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author saud
 */
public class ProjectViewerComponent extends JPanel {

    private DefaultMutableTreeNode root;
    private DefaultTreeModel model;
    private JTree tree;
    private EditorTabPane ep;
    
    public ProjectViewerComponent(EditorTabPane ep) {
        super(new BorderLayout());
        this.ep = ep;
        root = new DefaultMutableTreeNode("Projects");
        model = new DefaultTreeModel(root);
        tree = new JTree(model);
        tree.setRootVisible(false);
        tree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                doMouseClicked(me);
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tree);
        this.add(scrollPane, BorderLayout.CENTER);
        IDEProps.addUpdateListener(new UpdateListener() {
            @Override
            public void onPostLoad() {
                refreshProjects();
            }
        });
    }
    
    public void refreshProjects() {
        root.removeAllChildren();
        for(Project project : IDEProps.storedProjects) {
            DefaultMutableTreeNode projNode = new DefaultMutableTreeNode(project);
            root.add(projNode);
            for(MyClass c : project.classFiles) {
                DefaultMutableTreeNode classNode = new DefaultMutableTreeNode(c);
                projNode.add(classNode);
            }
        }
        model.reload();
    }

    private void doMouseClicked(MouseEvent me) {
        TreePath tp = tree.getPathForLocation(me.getX(), me.getY());
        if(tp==null) return;
        int depth = tp.getPathCount();
        int mouseButton = me.getButton();
        int clicks = me.getClickCount();
        if(depth == 2) {
            if(mouseButton == MouseEvent.BUTTON2 && clicks == 1) {
                    // context menu
            }
        } else if(depth == 3) {
            if(mouseButton == MouseEvent.BUTTON1 && clicks == 2) {
                    // open file
                    ep.addTab((MyClass) ((DefaultMutableTreeNode) tp.getLastPathComponent()).getUserObject());
            } else if(mouseButton == MouseEvent.BUTTON2 && clicks == 1) {
                    // context menu
            }
        }
    }
    
}
