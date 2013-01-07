package org.executequery.event;

import org.executequery.gui.browser.ConnectionsFolder;

public class DefaultConnectionsFolderRepositoryEvent extends AbstractApplicationEvent  
                                                     implements ConnectionsFolderRepositoryEvent {

    private final ConnectionsFolder connectionsFolder;

    public DefaultConnectionsFolderRepositoryEvent(
            Object source, String method, ConnectionsFolder connectionsFolder) {

        super(source, method);
        this.connectionsFolder = connectionsFolder;
    }
    
    public ConnectionsFolder getConnectionsFolder() {
        return connectionsFolder;
    }

}
