package org.executequery.datasource;

/**
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1521 $
 * @date     $Date: 2009-04-20 02:49:39 +1000 (Mon, 20 Apr 2009) $
 */
public interface PooledConnectionListener {

    void connectionClosed(PooledConnection pooledConnection);
    
}
