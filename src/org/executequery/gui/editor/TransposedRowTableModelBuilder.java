package org.executequery.gui.editor;

import org.executequery.gui.resultset.ResultSetTableModel;

public interface TransposedRowTableModelBuilder {

    ResultSetTableModel transpose(ResultSetTableModel resultSetTableModel, int rowIndex);

}