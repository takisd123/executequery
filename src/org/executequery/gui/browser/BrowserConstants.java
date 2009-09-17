/*
 * BrowserConstants.java
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

package org.executequery.gui.browser;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/** 
 *  Reuseable constants for construction and reference
 *  to the tree structure within the Database Browser Panel.<br>
 *  This is purely a convenience class due to the large
 *  use of the same String objects in many places.
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1460 $
 * @date     $Date: 2009-01-25 11:06:46 +1100 (Sun, 25 Jan 2009) $
 */
public class BrowserConstants {
    
    // --------------------------------------------
    // parent labels and hashtable keys for images
    // --------------------------------------------
    
    public static final int ROOT_NODE = 96;
    public static final int CATALOG_NODE = 98;
    public static final int HOST_NODE = 99;
    public static final int SCHEMA_NODE = 97;
    
    public static final int FUNCTIONS_NODE = 0;
    public static final int INDEX_NODE = 1;
    public static final int PROCEDURE_NODE = 2;
    public static final int SEQUENCE_NODE = 3;
    public static final int SYNONYM_NODE = 4;
    public static final int SYSTEM_TABLE_NODE = 5;
    public static final int TABLE_NODE = 6;
    public static final int TRIGGER_NODE = 7;
    public static final int VIEW_NODE = 8;

    public static final int SYSTEM_FUNCTION_NODE = 9;
    
    public static final int SYSTEM_STRING_FUNCTIONS_NODE = 10;
    
    public static final int SYSTEM_NUMERIC_FUNCTIONS_NODE = 11;
    
    public static final int SYSTEM_DATE_TIME_FUNCTIONS_NODE = 12;
    
    public static final int OTHER_NODE = 95;
    
    public static final int COLUMN_NODE =94;

    
    // same index as node values above
    public static final String[] META_TYPES = {"FUNCTION",
                                               "INDEX",
                                               "PROCEDURE",
                                               "SEQUENCE",
                                               "SYNONYM",
                                               "SYSTEM TABLE",
                                               "TABLE",
                                               "TRIGGER",
                                               "VIEW",
                                               "SYSTEM FUNCTIONS"};
    
    // ------------------------------------------
    // to add a new node - ALL icons must be in same order as META_TYPES
    // ------------------------------------------
                                               
    public static final String[] META_TYPE_ICONS = {"Function24.gif",
                                                    "TableIndex24.gif",
                                                    "Procedure24.gif",
                                                    "Sequence24.gif",
                                                    "Synonym24.gif",
                                                    "SystemTable24.gif",
                                                    "DatabaseTable24.gif",
                                                    "DatabaseTable24.gif", // make trigger image
                    //                              "Trigger24.gif",
                                                    "TableView24.gif",
                                                    "TableColumn24.gif",
                                                    "SystemFunction24.gif"}; // system function
    
    /** The String 'All Types' */
    //  String ALL_TYPES = "All Types";
    /** The String 'All Types Closed' */
    public static final String ALL_TYPES_CLOSED = "All Types Closed";

    /** The String 'Functions' */
    public static final String SYSTEM_FUNCTIONS_STRING = "System Functions";

    /** The String 'Functions' */
    public static final String FUNCTIONS_STRING = "Functions";
    /** The String 'Indexes' */
    public static final String INDEXES_STRING = "Indexes";
    /** The String 'Packages' */
    //String PACKAGES_STRING = "Packages";
    /** The String 'Procedures' */
    public static final String PROCEDURES_STRING = "Procedures";
    /** The String 'Sequences' */
    public static final String SEQUENCES_STRING = "Sequences";
    /** The String 'Synonyms' */
    public static final String SYNONYMS_STRING = "Synonyms";
    /** The String 'System Tables' */
    public static final String SYSTEM_TABLES_STRING = "System Tables";
    /** The String 'Tables' */
    public static final String TABLES_STRING = "Tables";
    /** The String 'Triggers' */
    public static final String TRIGGERS_STRING = "Triggers";
    /** The String 'Views' */
    public static final String VIEWS_STRING = "Views";
    /** The String 'Source' */
    public static final String SOURCE_STRING = "Source";
    /** The String 'Schema' */
    public static final String SCHEMA_STRING = "Schema";
    /** The String 'Functions' */
    
    // -----------------------------
    // image icons for tree nodes
    // -----------------------------
    
    public static final String DATABASE_OBJECT_IMAGE = "DatabaseObject16.gif";

    /** The image icon 'SavedConnection16.gif' */
    public static final String CONNECTIONS_IMAGE = "DatabaseConnections16.gif";

    /** The image icon 'Database16.gif' */
    public static final String CATALOG_IMAGE = "DBImage16.gif";
    
    /** The image icon 'Database16.gif' */
    public static final String HOST_IMAGE = "Database16.gif";

    /** The image icon 'DatabaseNotConnected16.gif' */
    public static final String HOST_NOT_CONNECTED_IMAGE = "DatabaseNotConnected16.gif";
    
    /** The image icon 'DatabaseConnected16.gif' */
    public static final String HOST_CONNECTED_IMAGE = "DatabaseConnected16.gif";

    /** The image icon 'User16.gif' */
    public static final String SCHEMA_IMAGE = "User16.gif";

    /** The image icon 'SystemFunction16.gif' */
    public static final String SYSTEM_FUNCTIONS_IMAGE = "SystemFunction16.gif";

    /** The image icon 'Function16.gif' */
    public static final String FUNCTIONS_IMAGE = "Function16.gif";
    
    /** The image icon 'TableIndex16.gif' */
    public static final String INDEXES_IMAGE = "TableIndex16.gif";
    
    /** The image icon 'Procedure16.gif' */    
    public static final String PROCEDURES_IMAGE = "Procedure16.gif";
    
    /** The image icon 'Sequence16.gif' */
    public static final String SEQUENCES_IMAGE = "Sequence16.gif";
    
    /** The image icon 'Synonym16.gif' */
    public static final String SYNONYMS_IMAGE = "Synonym16.gif";
    
    /** The image icon 'SystemTables16.gif' */
    public static final String SYSTEM_TABLES_IMAGE = "SystemTable16.gif";
    
    /** The image icon 'PlainTable16.gif' */
    public static final String TABLES_IMAGE = "PlainTable16.gif";
    
    /** The image icon 'TableColumn16.gif' */
    public static final String COLUMNS_IMAGE = "TableColumn16.gif";
    
    public static final String PRIMARY_COLUMNS_IMAGE = "TableColumnPrimary16.gif";
    
    public static final String FOREIGN_COLUMNS_IMAGE = "TableColumnForeign16.gif";
    
    /** The image icon 'dbNode.gif' */
    //  String TRIGGERS_IMAGE = "Triggers";

    /** The image icon 'Find16.gif' */
    public static final String VIEWS_IMAGE = "TableView16.gif";
    
    
    public static final String[] NODE_ICONS = {CONNECTIONS_IMAGE,
                                               CATALOG_IMAGE,
                                               HOST_IMAGE,
                                               HOST_NOT_CONNECTED_IMAGE,
                                               HOST_CONNECTED_IMAGE,
                                               SCHEMA_IMAGE,
                                               FUNCTIONS_IMAGE,
                                               INDEXES_IMAGE,
                                               PROCEDURES_IMAGE,
                                               SEQUENCES_IMAGE,
                                               SYNONYMS_IMAGE,
                                               SYSTEM_TABLES_IMAGE,
                                               TABLES_IMAGE,
                                               VIEWS_IMAGE,
                                               SYSTEM_FUNCTIONS_IMAGE,
                                               COLUMNS_IMAGE,
                                               PRIMARY_COLUMNS_IMAGE,
                                               FOREIGN_COLUMNS_IMAGE
                                               };
    
}











