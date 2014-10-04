package org.executequery.gui.resultset;

import java.util.UUID;

import org.apache.commons.lang.StringUtils;

public class ResultSetColumnHeader {

    private String id;
    private int originalIndex;
    private String label;
    private String name;
    private boolean visible;
    private int dataType;
    private String dataTypeName;
    private boolean editable;
    
    public ResultSetColumnHeader(int index, String label) {
        this(index, label, label);
    }
    
    public ResultSetColumnHeader(int index, String label, String name) {
        this(index, label, name, -1, null);
    }
    
    public ResultSetColumnHeader(int index, String label, String name, int dataType, String dataTypeName) {
        this.id = UUID.randomUUID().toString();
        this.originalIndex = index;
        this.label = label;
        this.name = name;
        this.dataType = dataType;
        this.dataTypeName = dataTypeName;
        this.visible = true;
        this.editable = true;
    }
    
    public String getId() {
        return id;
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    public String getName() {
        return name;
    }
    
    public String getLabel() {
        return label;
    }
    
    public String getDataTypeName() {
        return dataTypeName;
    }
    
    public int getDataType() {
        return dataType;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    public int getOriginalIndex() {
        return originalIndex;
    }
    
    public String getNameHint() {

        if (StringUtils.equals(label, name)) {
            
            return label;
        }
        return label + " [ " + name + " ]";
    }
    
}
