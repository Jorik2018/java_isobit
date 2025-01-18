package org.isobit.swing;

import java.awt.Color;
import org.isobit.data.Store;

public interface XView {
  public static final Color COLOR_MODIFIED = new Color(220, 245, 225);
  
  public static final Color COLOR_MODIFIED_SELECT = new Color(160, 226, 221);
  
  Object retrieve(Object... paramVarArgs);
  
  int insert(Object... paramVarArgs);
  
  boolean update();
  
  Store getStore();
  
  XView setStore(Store paramStore);
  
  XView cancel();
  
  XView clear();
  
  void setEditable(boolean paramBoolean);
  
  boolean isEditable();
  
  int getRowCount();
  
  Object getValueAt(int paramInt, String paramString);
  
  int[] getSelectedRows();
  
  XView deleteRows(int[] paramArrayOfint);
  
  XView setSelected(Object... paramVarArgs);
}
