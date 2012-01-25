package org.executequery.gui.browser.tree;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class JTreeEditable extends JFrame {

    DefaultTreeModel treeModel;

    public JTreeEditable() {
        super("Editable Tree Frame");
        setSize(200, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void init() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        DefaultMutableTreeNode file = new DefaultMutableTreeNode("File");
        DefaultMutableTreeNode open = new DefaultMutableTreeNode("Open");
        DefaultMutableTreeNode save = new DefaultMutableTreeNode("Save");
        DefaultMutableTreeNode saveas = new DefaultMutableTreeNode(
                "Save As");
        DefaultMutableTreeNode exit = new DefaultMutableTreeNode("Exit");
        treeModel = new DefaultTreeModel(root);
        JTree tree = new JTree(treeModel);
        tree.setEditable(true);
        treeModel.insertNodeInto(file, root, 0);
        file.add(open);
        file.add(save);
        file.add(saveas);
        file.add(exit);
        getContentPane().add(tree, BorderLayout.CENTER);
    }

    public static void main(String args[]) {
        JTreeEditable st = new JTreeEditable();
        st.init();
        st.setVisible(true);
    }
}
