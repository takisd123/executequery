/*
 * ImportExportDataModel.java
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

import java.util.List;

import org.executequery.databaseobjects.DatabaseColumn;
import org.executequery.databaseobjects.DatabaseHost;
import org.executequery.databaseobjects.DatabaseSource;
import org.executequery.databaseobjects.DatabaseTable;

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1241 $
 * @date     $Date: 2008-03-24 18:19:43 +1100 (Mon, 24 Mar 2008) $
 */
public interface ImportExportDataModel {

    DatabaseHost getDatabaseHost();

    void setDatabaseHost(DatabaseHost databaseHost);

    DatabaseSource getDatabaseSource();

    void setDatabaseSource(DatabaseSource databaseSource);

    List<DatabaseTable> getDatabaseTables();

    void setDatabaseTables(List<DatabaseTable> databaseTables);

    List<DatabaseColumn> getDatabaseTableColumns();

    void setDatabaseTableColumns(List<DatabaseColumn> databaseTableColumns);

    ImportExportType getImportExportType();

    void setImportExportType(ImportExportType importExportType);

    List<ImportExportFile> getImportExportFiles();

    void setSingleFileExport(String singleFileExport);

    String getSingleFileExport();

    boolean isMultipleTableImportExport();
    
    boolean isHostSelectionChanged();

    boolean isImportExportTypeChanged();

    boolean isExport();

    boolean isSingleFileExport();

    ImportExportFile getImportExportFileForTable(DatabaseTable databaseTable);

    OnErrorOption getOnErrorOption();
    
}

