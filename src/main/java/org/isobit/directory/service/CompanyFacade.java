package org.isobit.directory.service;

import java.util.List;
import java.util.Map;
import javax.ejb.Local;

import org.isobit.directory.model.Company;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Local
@Service
@Transactional
public interface CompanyFacade {
    
    public List<Company> load2(int idDir);
    
    public List getComCuentaSummary();

    List list(Object p,String cls);
    
    void edit(Company drtPersonaJuridica);

    public List<Company> load(int first, int pageSize, String sortField, Map<String, Object> filters);

    public void remove(List<Company> l);

    public Company load(Object p);

    public Object loadByNumero(Object get);
    
}
