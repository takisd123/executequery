package org.executequery.gui.browser;

import java.awt.BorderLayout;
import java.sql.ResultSet;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.executequery.gui.DefaultTable;
import org.executequery.gui.editor.ResultSetTableContainer;
import org.executequery.gui.resultset.ResultSetTableModel;

public class DatabaseObjectMetaDataPanel extends JPanel implements ResultSetTableContainer {

    private JTable table;
    private ResultSetTableModel tableModel;

    public DatabaseObjectMetaDataPanel() {

        super(new BorderLayout());
        
        tableModel = new ResultSetTableModel();
        table = new DefaultTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        setBorder(BorderFactory.createTitledBorder("Database object Meta Data"));
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public void setData(ResultSet resultSet) {

        tableModel.createTable(resultSet);
    }
    
    public JTable getTable() {
        
        return table;
    }
    
    public boolean isTransposeAvailable() {

        return false;
    }

    public void transposeRow(TableModel tableModel, int row) {}
    
}
