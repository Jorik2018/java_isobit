package org.isobit.util;

public interface AbstractFacadeLocal {
    
    public Object getModule(Object o);
            
    public <T> T getModule(Class<T> cls);
    
    public void postProcess(Object o);
    
    public void preProcess(Object o);
    
}
