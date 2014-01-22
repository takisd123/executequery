package org.executequery.sql;

import java.io.File;
import java.sql.ResultSet;

public interface ResultSetExporter {

    int export(ResultSet resultSet, File output);

}
