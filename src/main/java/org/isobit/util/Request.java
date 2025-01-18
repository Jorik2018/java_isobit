package org.isobit.util;

import java.util.Arrays;

import org.isobit.data.proxy.Proxy;

public class Request extends Result{
  public Request(Object... p) {
    super(new Object[0]);
    addAll(Arrays.asList((Object[])p));
  }
  
  public Request(Throwable ex) {
    super(new Object[0]);
    add(ex);
  }
  
    public void setAttribute(String string, Object prx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setAttribute'");
    }

    public boolean isOK() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isOK'");
    }
  
}
