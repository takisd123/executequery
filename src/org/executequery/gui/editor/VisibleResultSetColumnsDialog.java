package org.executequery.gui.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableModel;

import org.executequery.GUIUtilities;
import org.executequery.gui.BaseDialog;
import org.executequery.gui.DefaultPanelButton;
import org.executequery.gui.resultset.ResultSetTableModel;
import org.underworldlabs.swing.actions.ReflectiveAction;

@SuppressWarnings({"rawtypes", "unchecked"})
public class VisibleResultSetColumnsDialog extends BaseDialog {

    private ResultSetTableModel tableModel;
    private List<ResultSetColumn> columns = new ArrayList<VisibleResultSetColumnsDialog.ResultSetColumn>();
    
    public VisibleResultSetColumnsDialog(ResultSetTableModel tableModel) {
     
        super("Visible Columns", true);
        this.tableModel = tableModel;
        this.columns = createColumns(tableModel);
        init();
        
        pack();
        this.setLocation(GUIUtilities.getLocationForDialog(this.getSize()));
        setVisible(true);
    }
    
    private void init() {

        JPanel listPanel = new JPanel(new BorderLayout());
//        listPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JList list = new ResultSetColumnList(columns);
        JScrollPane scrollPane = new JScrollPane(list);
        listPanel.add(scrollPane, BorderLayout.CENTER);
        
        ReflectiveAction action = new ReflectiveAction(this);
        
        JButton okButton = new DefaultPanelButton(action, "OK", "update");
        JButton cancelButton = new DefaultPanelButton(action, "Cancel", "cancel");

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets.top = 5;
        gbc.weightx = 1.0;
        buttonPanel.add(okButton, gbc);
        gbc.weightx = 0;
        gbc.gridx = 1;
        gbc.insets.left = 5;
        buttonPanel.add(cancelButton, gbc);

        JPanel base = new JPanel(new BorderLayout());
        base.setPreferredSize(new Dimension(400, 350));

        base.add(listPanel, BorderLayout.CENTER);
        base.add(buttonPanel, BorderLayout.SOUTH);
        
        Container c = getContentPane();
        c.setLayout(new GridBagLayout());
        c.add(base, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
                            GridBagConstraints.SOUTHEAST, GridBagConstraints.BOTH,
                            new Insets(5, 5, 5, 5), 0, 0));

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    public void update(ActionEvent e) {

        for (ResultSetColumn column : columns) {
            
            if (column.visible) {
                
//                column.index;
                
            }
            
        }
        
        
    }

    public void cancel(ActionEvent e) {
        
        dispose();
    }

    private List<ResultSetColumn> createColumns(TableModel tableModel) {

        List<ResultSetColumn> list = new ArrayList<VisibleResultSetColumnsDialog.ResultSetColumn>();
        for (int i = 0, n = tableModel.getColumnCount(); i < n; i++) {
            
            list.add(new ResultSetColumn(i, tableModel.getColumnName(i), true));
        }

        return list;
    }

    class ResultSetColumnList extends JList {
        
        public ResultSetColumnList(List<ResultSetColumn> columns) {

            super(columns.toArray(new ResultSetColumn[columns.size()]));
            setCellRenderer(new CheckboxListRenderer());
            
            addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {

                    if (e.getKeyCode() == KeyEvent.VK_SPACE) {

                        int index = getSelectedIndex();
                        if (index != -1) {

                            ResultSetColumn checkbox = (ResultSetColumn) getModel().getElementAt(index);
                            checkbox.setVisible(!checkbox.visible);
                            repaint();
                        }
                    }
                }
            });

            setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            
            addMouseListener(new MouseAdapter() {

                public void mousePressed(MouseEvent e) {

                    int index = locationToIndex(e.getPoint());
                    if (index != -1) {

                        ResultSetColumn checkbox = (ResultSetColumn) getModel().getElementAt(index);
                        checkbox.setVisible(!checkbox.visible);
                        repaint();
                    }
                }
            });
            
        }
        
    }
    
    
    class CheckboxListRenderer extends JCheckBox implements ListCellRenderer {
        
        private Border noFocusBorder = new EmptyBorder(3, 5, 3, 3);
        
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {

            ResultSetColumn resultSetColumn = (ResultSetColumn) value;

            setText(resultSetColumn.name);
            setSelected(resultSetColumn.visible);

            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setFocusPainted(false);

            setBorderPainted(true);
//            setBorder(isSelected ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);
            setBorder(noFocusBorder);
            
            return this;
        }

    }
    
    
    class ResultSetColumn {        
        
        private int index;
        private String name;
        private boolean visible;
        
        public ResultSetColumn(int index, String name, boolean visible) {
            
            this.index = index;
            this.name = name;
            this.visible = visible;
        }

        public void setVisible(boolean visible) {

            this.visible = visible;
        }
        
        @Override
        public String toString() {

            return name;
        }
    }
    
}
