package org.isobit.data;

import java.util.EventListener;

public interface DBListener extends EventListener {
  void itemChanged(int paramInt, String paramString, Object paramObject);
  
  void rowInserted(int paramInt);
  
  void retrieveEnd(Object paramObject);
  
  void updateEnd(Object paramObject);
}
