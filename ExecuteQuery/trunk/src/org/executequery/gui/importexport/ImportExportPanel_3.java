/*
 * ImportExportPanel_3.java
 *
 * Copyright (C) 2002-2009 Takis Diakoumis
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.executequery.gui.importexport;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.executequery.GUIUtilities;
import org.executequery.components.FileChooserDialog;
import org.executequery.gui.DefaultTable;

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1460 $
 * @date     $Date: 2009-01-25 11:06:46 +1100 (Sun, 25 Jan 2009) $
 */
public class ImportExportPanel_3 extends JPanel {
    
    /** The table to display table names and file paths */
    private JTable table;
    
    /** The table model */
    private TableTransferModel tableModel;
    
    /** The last file path selected */
    private String lastPath;
    
    /** The controlling object for this process */
    private ImportExportProcess parent;
    
    /** <p>Creates a new instance with the specified
     *  process as the parent.
     *
     *  @param the parent controlling the process
     */
    public ImportExportPanel_3(ImportExportProcess parent) {
        super(new GridBagLayout());
        this.parent = parent;
        
        try {
            jbInit();
        } catch(Exception e) {
            e.printStackTrace();
        }
        
    }
    
    /** <p>Initialises the state of this instance and
     *  lays out components on the panel. */
    private void jbInit() throws Exception {
        JLabel label = new JLabel("Select respective data files for " +
                                  "the tables to be processed.");
        
        // build the table and add to a scroll pane
        table = new DefaultTable();
        table.setRowHeight(23);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setColumnSelectionAllowed(false);
        table.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scroller = new JScrollPane(table);
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        tableModel = new TableTransferModel();
        buildTable();
        
        TableColumnModel tcm = table.getColumnModel();
        TableColumn col = tcm.getColumn(0);
        col.setPreferredWidth(140);
        
        col = tcm.getColumn(1);
        col.setPreferredWidth(255);
        
        col = tcm.getColumn(2);
        col.setCellRenderer(new BrowseButtonRenderer());
        col.setCellEditor(new BrowseButtonEditor(new JCheckBox()));
        col.setPreferredWidth(80);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        add(label, gbc);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 1;
        gbc.insets.top = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        add(scroller, gbc);
        
        setPreferredSize(parent.getChildDimension());
    }
    
    /** <p>Generates and displays the table/data file <code>JTable</code>*/
    public void buildTable() {
        int type = parent.getTableTransferType();
        boolean hasData = tableModel.hasData();
        Vector<DataTransferObject> v = null;
        
        switch (type) {
            
            case ImportExportProcess.SINGLE_TABLE:
                
                String tableName = parent.getTableName();
                
                if (hasData && tableModel.getRowCount() > 1)
                    hasData = false;
                
                if (!hasData || !tableName.equals(tableModel.getDataVector().
                elementAt(0).toString())) {
                    v = new Vector(1);
                    v.add(new DataTransferObject(tableName));
                }
                
                else
                    v = tableModel.getDataVector();
                
                break;
                
            case ImportExportProcess.MULTIPLE_TABLE:
                String[] tables = parent.getSelectedTables();
                
                v = new Vector<DataTransferObject>(tables.length);
                
                int multipleFile = parent.getMutlipleTableTransferType();
                
                if (multipleFile == ImportExportProcess.SINGLE_FILE)
                    v.add(new DataTransferObject("ALL TABLES"));
                
                else {
                    
                    for (int i = 0; i < tables.length; i++) {
                        v.add(new DataTransferObject(tables[i]));
                    }

                }
                
                if (hasData) {
                    Vector<DataTransferObject> v_current = tableModel.getDataVector();
                    int v_currentSize =  v_current.size();
                    
                    String newTable = null;
                    Object obj = null;
                    
                    for (int i = 0, j = v.size(); i < j; i++) {
                        newTable = v.elementAt(i).toString();
                        
                        for (int k = 0; k < v_currentSize; k++) {
                            obj = v_current.elementAt(k);
                            
                            if (newTable.equals(obj.toString())) {

                                v.add(i, (DataTransferObject)obj);
                                break;
                            }
                            
                        }
                        
                    }
                    
                }
                
                break;
                
        }
        
        tableModel.setColumnDataVector(v);
        table.setModel(tableModel);
        table.revalidate();
        
    }
    
    /** <p>Returns a <code>Vector</code> of <code>
     *  DataTransferObject</code> objects containing
     *  all relevant data for the process.
     *
     *  @return a <code>Vector</code> of
     *          <code>DataTransferObject</code> objects
     */
    public Vector<DataTransferObject> getDataFileVector() {
        return tableModel.getDataVector();
    }
    
    /** <p>Validates that all tables selected have
     *  an associated data file selected.
     *
     *  @return whether transfer files are present
     */
    public boolean transferObjectsComplete() {
        
        if (table.isEditing()) {
            table.getCellEditor(table.getEditingRow(), 1).stopCellEditing();
        }

        Vector v = tableModel.getDataVector();
        int v_size = v.size();
        int type = parent.getTransferType();
        
        for (int i = 0; i < v_size; i++) {
            DataTransferObject dto = (DataTransferObject)v.elementAt(i);
            
            if (!dto.hasDataFile(type)) {
                GUIUtilities.displayErrorMessage(
                "You must provide a valid data file for each selected table.");
                return false;
            }
            
        }
        
        return true;
    }
    
    /** 
     * Defines the table model for the table to file selection for the transfer. 
     */
    class TableTransferModel extends AbstractTableModel {

        private Vector<DataTransferObject> data;

        private final String BROWSE = "Browse";

        private final String[] header = {"Table Name", "File Path", ""};
        
        public TableTransferModel() {}
        
        public TableTransferModel(Vector<DataTransferObject> data) {
            this.data = data;
        }
        
        public boolean hasData() {
            return data != null;
        }
        
        public int getRowCount() {
            return data.size();
        }
        
        public int getColumnCount() {
            return 3;
        }
        
        public void setColumnDataVector(Vector data) {
            this.data = data;
            if (table.isEditing()) {
                fireTableRowsUpdated(0, data.size());
            }
        }
        
        /** <p>Sets the data file for the specified row
         *  to the specified file.
         *
         *  @param the row (<code>Vector</code> index)
         *  @param the file name
         */
        public void setDataFile(int row, String fileName) {
            DataTransferObject obj = (DataTransferObject)data.elementAt(row);
            obj.setFileName(getFileName(fileName));
            fireTableRowsUpdated(row, row);
        }
        
        public Vector<DataTransferObject> getDataVector() {

            return data;
        }
        
        public Object getValueAt(int row, int col) {
            DataTransferObject obj = (DataTransferObject)data.elementAt(row);
            
            switch(col) {
                case 0:
                    return obj.getTableName();
                case 1:
                    return obj.getFileName();
                case 2:
                    return BROWSE;
                default:
                    return null;
            }
        }
        
        public void setValueAt(Object value, int row, int col) {
            DataTransferObject obj = (DataTransferObject)data.elementAt(row);
            
            switch (col) {
                case 0:
                    obj.setTableName((String)value);
                    break;
                case 1:
                    obj.setFileName(getFileName(value.toString()));
                    break;
            }
            
            fireTableRowsUpdated(row, row);
        }
        
        private String getFileName(String value) {
            // make sure we have the right file extension
            if (parent.getTransferType() == ImportExportProcess.EXPORT) {
                String defaultExtension = null;
                String fileExtension = findExtension(value);

                int transferFormat = parent.getTransferFormat();
                if (transferFormat == ImportExportProcess.XML) {
                    defaultExtension = ".xml";
                }
                else if (transferFormat == ImportExportProcess.EXCEL) {
                    defaultExtension = ".xls";
                }

                if (defaultExtension != null &&
                        !defaultExtension.equalsIgnoreCase(fileExtension)) {
                    value += defaultExtension;
                }

            }
            return value;
        }

        private String findExtension(String fileName) {
            int index = fileName.lastIndexOf('.');
            if (index != -1) {
                return fileName.substring(index);
            }
            return null;
        }
        
        public String getColumnName(int col) {
            return header[col];
        }
        
        public boolean isCellEditable(int row, int col) {
            return (col > 0);
        }
        
    } // TableTransferModel
    
    
    public class BrowseButtonEditor extends DefaultCellEditor {
        
        protected JButton button;
        private String label;
        private boolean isPushed;
        
        public BrowseButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }
        
        public Component getTableCellEditorComponent(JTable table, 
                                                     Object value,
                                                     boolean isSelected, 
                                                     int row, 
                                                     int column) {
            label = value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }
        
        public Object getCellEditorValue() {
            
            if (isPushed) {
                FileChooserDialog fileChooser = null;
                
                if (lastPath == null) {
                    fileChooser = new FileChooserDialog();
                } else {
                    fileChooser = new FileChooserDialog(lastPath);
                }

                String dialogTitle = null;                
                if (parent.getTransferType() == ImportExportProcess.EXPORT) {
                    dialogTitle = "Select Export File...";
                } else {
                    dialogTitle = "Select Import File...";
                }

                fileChooser.setDialogTitle(dialogTitle);
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);

                int result = fileChooser.showDialog(GUIUtilities.getInFocusDialogOrWindow(), "Select");                
                if (result == JFileChooser.CANCEL_OPTION) {
                    return label;
                }

                File fileName = fileChooser.getSelectedFile();
                if (parent.getTransferType() == ImportExportProcess.IMPORT) {
                    
                    if (fileName == null || (!fileName.exists() && !fileName.isFile()))
                        JOptionPane.showMessageDialog(GUIUtilities.getParentFrame(),
                        "Invalid File Name", "Error",
                        JOptionPane.ERROR_MESSAGE);
                    
                }
                
                lastPath = fileName.getParent();                
                tableModel.setDataFile(table.getEditingRow(), fileName.getAbsolutePath());
            }
            
            isPushed = false;
            return label;
        }
        
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
        
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
        
    } // BrowseButtonEditor
    
    class BrowseButtonRenderer extends JButton implements TableCellRenderer {
        
        public BrowseButtonRenderer() {
            setMargin(new Insets(1,1,1,1));
        }
        
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value.toString());
            return this;
        }

    } // BrowseButtonRenderer
    
}

