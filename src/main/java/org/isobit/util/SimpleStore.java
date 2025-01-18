package org.isobit.util;

import java.util.List;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import org.isobit.data.Q;

public class SimpleStore implements TableModel {
  private List data;
  
  private String[] c;
  
  private int start = 0;
  
  private int size = -1;
  
  public int getStart() {
    return this.start;
  }
  
  public void setStart(int start) {
    this.start = start;
  }
  
  public int getSize() {
    return this.size;
  }
  
  public void setSize(int size) {
    this.size = size;
  }
  
  public List getData() {
    return this.data;
  }
  
  public void setData(List data) {
    this.data = data;
  }
  
  public SimpleStore(List d, Q q) {
    List md = q.getMetadata();
    String[] c = new String[md.size()];
    for (int i = 0, j = c.length; i < j; i++)
      c[i] = "" + ((Object[])md.get(i))[0]; 
    this.data = d;
    this.c = c;
  }
  
  public SimpleStore(Q q, List d) {
    List md = q.getMetadata();
    String[] c = new String[md.size()];
    for (int i = 0, j = c.length; i < j; i++)
      c[i] = "" + ((Object[])md.get(i))[0]; 
    this.data = d;
    this.c = c;
  }
  
  public SimpleStore(List d, String[] co) {
    this.data = d;
    this.c = co;
  }
  
  public void addTableModelListener(TableModelListener l) {}
  
  public Class getColumnClass(int col) {
    return Object.class;
  }
  
  public int getColumnCount() {
    return this.c.length;
  }
  
  public String getColumnName(int co) {
    return this.c[co];
  }
  
  public int getRowCount() {
    return (this.size > -1) ? this.size : this.data.size();
  }
  
  public Object getValueAt(int rowIndex, int columnIndex) {
    return ((Object[])this.data.get(this.start + rowIndex))[columnIndex];
  }
  
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return false;
  }
  
  public void removeTableModelListener(TableModelListener l) {}
  
  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}
}
