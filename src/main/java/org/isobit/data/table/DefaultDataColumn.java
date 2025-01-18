package org.isobit.data.table;

public class DefaultDataColumn extends AbstractDataColumn {
  private boolean editable = false;
  
  private Object config;
  
  public Object getConfig() {
    return this.config;
  }
  
  public void setConfig(Object config) {
    this.config = config;
  }
  
  public boolean isEditable() {
    return this.editable;
  }
  
  public void setEditable(boolean editable) {
    this.editable = editable;
  }
  
  public DefaultDataColumn(Object name) {
    setName(name.toString());
  }
  
  public String getName() {
    return this.name;
  }
  
  public String toString() {
    return getName();
  }
  
  public void setName(String name) {
    this.name = name;
  }
}
