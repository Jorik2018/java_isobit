package org.isobit.directory.service.impl;

import java.util.Map;
import javax.ejb.EJB;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;

import org.isobit.app.service.UserService;
import org.isobit.directory.model.Directory;
import org.isobit.directory.service.DirectoryFacade;
import org.isobit.util.XMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@ApplicationScoped
@Service
public class DirectoryFacadeImpl implements DirectoryFacade{

    @PersistenceContext
    private EntityManager em;
    
    @EJB
    @Autowired
    private UserService userFacade;

    enum Perm {
        ACCESS_DIRECTORY,
        ADMIN_DIRECTORY,
        ACCESS_DIRECTORY_TOWN,
        ADMIN_DIRECTORY_TOWN
    }

    @Override
    public void create(Directory drtDirectorio) {
        if(drtDirectorio.getId()!=null)
            em.persist(drtDirectorio);
        else{
            em.merge(drtDirectorio);
        }
    };
/* 
    @Override
    public Object getBlock(HttpServletRequest request, String op, Object delta) {
        if ("list".equals(op)) {
            Map blocks = (Map) request.getAttribute("#blocks");
            blocks.put("towns", new XMap("info", "Directory_web_admin"));
            return blocks;
        } else if ("view".equals(op)) {
            if (userFacade.access(Perm.ACCESS_DIRECTORY)) {
                return new XMap("title", "Directory_web_admins", "src", "/directory_web_admin/block/Block.xhtml");
            }
        }
        return null;

    }
*/
}
