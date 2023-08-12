package org.isobit.directory.service;

import java.io.File;
import java.util.List;
import java.util.Map;
import javax.ejb.Local;

import org.isobit.directory.model.DrtEstadoCivil;
import org.isobit.directory.model.People;
import org.isobit.util.AbstractFacadeLocal;

@Local
public interface PeopleFacade{

    void edit(People people);
    
    public Map getUbigeo(Integer districtId);

    People find(Object id);

    public List load(int first, int pageSize, String sortField, Map<String, Object> filters);

    public List<DrtEstadoCivil> getDrtEstadoCivilList();

    public People getAnonymus();

    public int count(String opt, Object v);

    public People load(Object p);
    
    public Map loadExt(People people);

    public Object importFile(File file);
    
}