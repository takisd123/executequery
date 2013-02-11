/*
 * ResultSetTablePopupMenu.java
 *
 * Copyright (C) 2002-2013 Takis Diakoumis
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

package org.executequery.gui.editor;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.print.Printable;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.executequery.Constants;
import org.executequery.GUIUtilities;
import org.executequery.UserPreferencesManager;
import org.executequery.gui.BaseDialog;
import org.executequery.gui.resultset.LobRecordDataItem;
import org.executequery.gui.resultset.RecordDataItem;
import org.executequery.gui.resultset.ResultSetTable;
import org.executequery.gui.resultset.ResultSetTableModel;
import org.executequery.gui.resultset.SimpleRecordDataItem;
import org.executequery.print.PrintingSupport;
import org.executequery.print.TablePrinter;
import org.underworldlabs.swing.actions.ReflectiveAction;
import org.underworldlabs.swing.menu.MenuItemFactory;
import org.underworldlabs.swing.table.TableSorter;
import org.underworldlabs.util.SystemProperties;

public class ResultSetTablePopupMenu extends JPopupMenu implements MouseListener {
    
    private Point lastPopupPoint;

    private ReflectiveAction reflectiveAction;

    private final ResultSetTable table;

    private final ResultSetTableContainer resultSetTableContainer;

    private boolean doubleClickCellOpensDialog;

    public ResultSetTablePopupMenu(ResultSetTable table, 
            ResultSetTableContainer resultSetTableContainer) {

        this.table = table;
        this.resultSetTableContainer = resultSetTableContainer;
        
        doubleClickCellOpensDialog = doubleClickCellOpensDialog();
        reflectiveAction = new ReflectiveAction(this);
        
        // the print sub-menu
        JMenu printMenu = MenuItemFactory.createMenu("Print");
        create(printMenu, "Selection", "printSelection");
        create(printMenu, "Table", "printTable");

        JCheckBoxMenuItem cellOpensDialog = 
            MenuItemFactory.createCheckBoxMenuItem(reflectiveAction);
        cellOpensDialog.setText("Double-Click Opens Item View");
        cellOpensDialog.setSelected(doubleClickCellOpensDialog());
        cellOpensDialog.setActionCommand("cellOpensDialog");

        add(create("Copy Selected Cell(s)", "copySelectedCells"));
        addSeparator();
        add(create("Select Row", "selectRow"));
        add(create("Select Column", "selectColumn"));
        
        if (resultSetTableContainer.isTransposeAvailable()) {
        
            add(create("Transpose Row", "transposeRow"));
        }

        addSeparator();
        add(create("Export Selection", "exportSelection"));
        add(create("Export Table", "exportTable"));
        addSeparator();
        add(create("View", "openDataItemViewer"));
        add(printMenu);
        addSeparator();        
        add(cellOpensDialog);

    }

    public void setLastPopupPoint(Point lastPopupPoint) {
     
        this.lastPopupPoint = lastPopupPoint;
    }
    
    private boolean doubleClickCellOpensDialog() {
        
        return UserPreferencesManager.doubleClickOpenItemView();
    }
    
    private JMenuItem create(JMenu menu, String text, String actionCommand) {
        
        JMenuItem menuItem = create(text, actionCommand);
        menu.add(menuItem);
        
        return menuItem;
    }
    
    private JMenuItem create(String text, String actionCommand) {

        JMenuItem menuItem = MenuItemFactory.createMenuItem(reflectiveAction);
        menuItem.setActionCommand(actionCommand);
        menuItem.setText(text);

        return menuItem;
    }
    
    private RecordDataItem tableCellDataAtPoint(Point point) {
        
        Object value = table.valueAtPoint(point);

        if (value instanceof RecordDataItem) {

            return (RecordDataItem) value;
        }
        
        return null;
    }

    private void showViewerForValueAt(Point point) {
        
        RecordDataItem recordDataItem = tableCellDataAtPoint(point);
        if (recordDataItem != null && !recordDataItem.isValueNull()) {

            if (recordDataItem instanceof SimpleRecordDataItem) {
                
                showSimpleRecordDataItemDialog(recordDataItem);
                
            } else if (recordDataItem instanceof LobRecordDataItem) {
            
                showLobRecordDataItemDialog(recordDataItem);
            }

        }

    }

    private void showSimpleRecordDataItemDialog(RecordDataItem recordDataItem) {

        BaseDialog dialog = new BaseDialog("Record Data Item Viewer", true);
        dialog.addDisplayComponentWithEmptyBorder(
                new SimpleDataItemViewerPanel(dialog, (SimpleRecordDataItem) recordDataItem));
        dialog.display();        
    }

    private void showLobRecordDataItemDialog(RecordDataItem recordDataItem) {

        BaseDialog dialog = new BaseDialog("LOB Record Data Item Viewer", true);
        dialog.addDisplayComponentWithEmptyBorder(
                new LobDataItemViewerPanel(dialog, (LobRecordDataItem) recordDataItem));
        dialog.display();
    }

    public void cellOpensDialog(ActionEvent e) {
        
        JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) e.getSource();

        doubleClickCellOpensDialog = menuItem.isSelected();
        resultSetTableModel().setCellsEditable(!doubleClickCellOpensDialog);
        
        SystemProperties.setBooleanProperty(
                Constants.USER_PROPERTIES_KEY, 
                "results.table.double-click.record.dialog", doubleClickCellOpensDialog);

        UserPreferencesManager.fireUserPreferencesChanged();
    }
    
    private ResultSetTableModel resultSetTableModel() {

        TableSorter tableSorter = (TableSorter) table.getModel();
        
        return (ResultSetTableModel) tableSorter.getReferencedTableModel();
    }

    public void exportSelection(ActionEvent e) {
        
        TableModel selected = table.selectedCellsAsTableModel();

        if (selected != null) {
         
            new QueryEditorResultsExporter(selected);
        }
    }
    
    public void transposeRow(ActionEvent e) {
        
        if (resultSetTableContainer != null) {
        
            table.selectRow(lastPopupPoint);
    
            int selectedRow = table.getSelectedRow();

            TableSorter model = (TableSorter) table.getModel();
            resultSetTableContainer.transposeRow(model.getTableModel(), selectedRow);
        }

    }
    
    public void selectColumn(ActionEvent e) {
        
        table.selectColumn(lastPopupPoint);
    }
    
    public void selectRow(ActionEvent e) {
        
        table.selectRow(lastPopupPoint);
    }
    
    public void copySelectedCells(ActionEvent e) {
        
        table.copySelectedCells();
    }
    
    public void exportTable(ActionEvent e) {

        new QueryEditorResultsExporter(resultSetTableModel());
    }
    
    public void printSelection(ActionEvent e) {
        
        printResultSet(true);
    }
    
    public void printTable(ActionEvent e) {
        
        printResultSet(false);
    }
    
    public void openDataItemViewer(ActionEvent e) {
        
        try {

            GUIUtilities.showWaitCursor();
            showViewerForValueAt(lastPopupPoint);
        
        } finally {
            
            GUIUtilities.showNormalCursor();
        }
    }

    private void printResultSet(boolean printSelection) {
        
        JTable printTable = null;

        if (printSelection) {

            TableModel model = table.selectedCellsAsTableModel();

            if (model != null) {
            
                printTable = new JTable(model);

            } else {

                return;
            }

        } else {
          
            printTable = table;
        }

        Printable printable = new TablePrinter(printTable, null);
        new PrintingSupport().print(printable, "Execute Query - table");
    }
    
    public void mousePressed(MouseEvent e) {

        maybeShowPopup(e);
    }

    public void mouseClicked(MouseEvent e) {

        if (e.getClickCount() >= 2 && doubleClickCellOpensDialog) {
            
            lastPopupPoint = e.getPoint();
            openDataItemViewer(null);
        }

    }
    
    public void mouseReleased(MouseEvent e) {
        maybeShowPopup(e);
    }

    private void maybeShowPopup(MouseEvent e) {

        if (e.isPopupTrigger()) {
            
            lastPopupPoint = e.getPoint();
            
            if (!table.hasMultipleColumnAndRowSelections()) {

                table.selectCellAtPoint(lastPopupPoint);
            }

            show(e.getComponent(), lastPopupPoint.x, lastPopupPoint.y);

        } else {
          
            // re-enable cell selection
            table.setColumnSelectionAllowed(true);
            table.setRowSelectionAllowed(true);
        }

    }

    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}


}




