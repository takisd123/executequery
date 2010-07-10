package org.executequery.gui;

import java.util.Vector;

import javax.swing.JList;
import javax.swing.ListModel;

public class DefaultList extends JList {

    private static final int DEFAULT_ROW_HEIGHT = 20;
    
    public DefaultList() {

        super();
        init();
    }

    public DefaultList(ListModel dataModel) {

        super(dataModel);
        init();
    }

    public DefaultList(Object[] listData) {

        super(listData);
        init();
    }

    public DefaultList(Vector<?> listData) {

        super(listData);
        init();
    }

    private void init() {
        
        setFixedCellHeight(DEFAULT_ROW_HEIGHT);
    }

}
