package org.isobit.app.service;

import java.util.Map;

import jakarta.ejb.Local;

import java.io.File;

@Local
public interface SystemFacade{

    public String getConfig();

    public void save(Map m);

    public boolean hasAllAccess();

    public enum Perm{
        ADMIN_SITE_CONFIGURATION,
        ACCESS_ADMIN_PAGES,
        ADMIN_ACTIONS,
        ACCESS_SITE_REPORTS,
        SELECT_DIFFERENT_THEME,
        ADMIN_FILES
    };

    public Map getConfig(String variables);

    public void save(String variables, Map variables0);

    public void prepareReport(Map map);

    public boolean hasAdmin();

    public Object get(Object k, Object def);

    public Object getV(Object k, Object def);

    public String getUploadDir();
    
    public File getUploadFile();
    
    public boolean existsPegasus();

    public void initSystem();
    
}
