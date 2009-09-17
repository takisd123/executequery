package org.executequery.gui.editor;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.ListSelectionModel;

public class TypeAheadList extends JList {

    private TypeAheadListProvider typeAheadListProvider;
    
    public TypeAheadList(TypeAheadListProvider typeAheadListProvider) {
        
        super();

        this.typeAheadListProvider = typeAheadListProvider;

        init();
    }

    private void init() {

        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() >= 2) {
                
                    listValueSelected(getSelectedValue());
                } 

            }
        });

        addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {

                int keyCode = e.getKeyCode();
                
                if (keyCode == KeyEvent.VK_ENTER) {                
                
                    listValueSelected(getSelectedValue());

                } else if (keyCode == KeyEvent.VK_BACK_SPACE) {
                
                    typeAheadListProvider.refocus();
                }

            }

        });

        
    }

    @SuppressWarnings("unchecked")
    public void resetValues(List values) {

        Vector listData = new Vector(values.size());
        listData.addAll(values);

        setListData(listData);
    }

    public void setListItemSelectedAndFocus(int index) {

        int size = getModel().getSize();

        if (size > 0 && index < size) {

            requestFocus();
            setSelectedIndex(index);
        
        } else if (size == index) {
            
            requestFocus();
            setSelectedIndex(index - 1);
        }
        
    }

    private void listValueSelected(Object selectedValue) {

        typeAheadListProvider.listValueSelected(selectedValue);
    }
    
}
