/*
 * DerivedQuery.java
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

package org.executequery.sql;

import org.apache.commons.lang.StringUtils;

public final class DerivedQuery {

    private String derivedQuery;
    
    private final String originalQuery;
    
    public DerivedQuery(String originalQuery) {
        super();
        this.originalQuery = originalQuery;
    }

    public String getOriginalQuery() {
        return originalQuery;
    }

    public String getLoggingQuery() {
        
        if (derivedQuery.length() > 50) {
            
            return derivedQuery.substring(0, 50) + " ... ";
        }
        
        return derivedQuery;
    }
    
    public String getDerivedQuery() {
        return derivedQuery;
    }

    public void setDerivedQuery(String derivedQuery) {
        
        String query = derivedQuery.replaceAll("\t", " ");
        
        if (query.endsWith(";")) {
            
            query = query.substring(0, query.length() - 1);
        }
        
        this.derivedQuery = query;
    }

    public boolean isExecutable() {
        return StringUtils.isNotBlank(getDerivedQuery()); 
    }
    
}
