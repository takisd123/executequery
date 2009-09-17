package org.executequery.gui.editor;

import javax.swing.table.TableModel;

public interface ResultSetTableContainer {

    boolean isTransposeAvailable();

    void transposeRow(TableModel tableModel,  int row);

}
