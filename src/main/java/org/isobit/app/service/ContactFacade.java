package org.isobit.app.service;

import java.util.List;
import java.util.Map;

import org.isobit.app.model.User;

import jakarta.ejb.Local;

@Local
public interface ContactFacade {
    
    public static final String CONTACT_TEMPLATE_MAP="CONTACT_TEMPLATE_MAP";

    public void send(Map m);

    //public Map getDefaultTemplate(String module, String templateKey);
    
    public interface ContactModule {

        public Map mailTokens(Map tokens,User account,String language,Map m);

        public String mailText(String template, String language, Map tokens);
        
        public List getTemplateList();

    }

    public Map getTEMPLATE_MAP();

    //public Object getTemplate(String module, String templateKey);

    public void save();

    public List getTemplateList();

    public Object mail(Map m, ContactModule ext, String template, String destiny, String language, Map params);

}
