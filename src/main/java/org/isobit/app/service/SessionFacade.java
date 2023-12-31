package org.isobit.app.service;

import jakarta.ejb.Local;

@Local
public interface SessionFacade {

    public Object get(String key);

    public void put(String key, Object value);

    public void logout();

    public void invalidate();
    
}
