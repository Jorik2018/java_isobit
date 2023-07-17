package org.isobit.app;

import java.util.EventObject;
import java.util.Map;

public interface ViewBuilder {

    public void print(Object request) throws Exception;
    public Object addView(Class clazz,Map config) throws Exception;
    public void scheduleGC();
    public void close(EventObject e);

}
