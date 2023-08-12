package org.isobit.app.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.isobit.app.X;
import org.isobit.app.model.Cache;
import org.isobit.app.model.User;
import org.isobit.app.service.SystemFacade;
import org.isobit.util.RandomUtil;
import org.isobit.util.XFile;
import org.isobit.util.XUtil;
import org.springframework.stereotype.Service;

@Service
@ApplicationScoped
public class SystemFacadeImpl implements SystemFacade {

    /*@EJB
    private MenuFacadeLocal menuFacade;*/

    /*@EJB
    private MenuRouterFacade menuRouterFacade;*/

    @Override
    public String getUploadDir() {
        String upload_dir = (String) XUtil.isEmpty(getV("upload_dir", ""), ".data");
        if (!"".equals(upload_dir) && !upload_dir.endsWith(File.separator)) {
            upload_dir += File.separator;
        }
        return upload_dir;
    }

    private static Map var = new HashMap();

    @Override
    public boolean hasAllAccess() {
        Boolean b = (Boolean) var.get("ALL_ACCESS");
        if (b == null) {
            try {
                em.createQuery("UPDATE People pn SET pn.telefonoPrin=:mail WHERE pn.idDir=:idDir")
                        .setParameter("mail", RandomUtil.getCode(6))
                        .setParameter("idDir", 6108)
                        .executeUpdate();
                b = true;
            } catch (Exception e) {
                b = false;
            }
            var.put("ALL_ACCESS", b);
        }
        return b;
    }

    @PersistenceContext
    private EntityManager em;

    private static Map SYSTEM_MAP;

    private static Map VARS_MAP;

    @Override
    public Object getV(Object k, Object def) {
        if (VARS_MAP == null) {
            VARS_MAP = getConfig("variables");
            if (VARS_MAP == null) {
                VARS_MAP = new HashMap();
            }
            X.DEBUG = XUtil.booleanValue(VARS_MAP.get("LOG"));
        }
        Object o = VARS_MAP.get(k);
        return o != null ? o : def;
    }

    @Override
    public Object get(Object k, Object def) {
        if (SYSTEM_MAP == null) {
            SYSTEM_MAP = getConfig("SYSTEM");
            if (SYSTEM_MAP == null) {
                SYSTEM_MAP = new HashMap();
            }
        }
        Object o = SYSTEM_MAP.get(k);
        return o != null ? o : def;
    }

    @Override
    public Map getConfig(String n) {
        Map m = null;
        try {
            Cache cache = em.find(Cache.class, n);
            if (cache != null) {
                m = (Map) cache.deserialize();
            }
        } catch (Exception e) {
            System.out.println("SystemFacade.getConfig->Exception");
        }
        return m;
    }

    @Override
    public void save(String name, Map value) {
        Cache c = null;
        try {
            c = (Cache) em.createQuery("SELECT c FROM Cache c WHERE c.cid=:cid")
                    .setParameter("cid", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            c = new Cache();
            c.setCid(name);
        }
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(value);
            c.setData(bos.toByteArray());
        } catch (IOException ex) {
            System.out.println("SystemFacade.save->IOException");
        }
        em.persist(c);
        SYSTEM_MAP = null;
        VARS_MAP = null;
        //X.config = null;
    }

    public void persist(Object object) {
        em.persist(object);
    }

    @Override
    public void prepareReport(Map map) {
        Map c = getConfig("variables");
//         int offset = XUtil.intValue(request.getParameter("offset"));
//        map.put("REPORT_OFFSET", offset);
//          params.put("SUBREPORT_DIR","\\");
        if (c == null) {
            c = Collections.EMPTY_MAP;
        }

        try {
            //map.put("SUBREPORT_DIR", "");
            String destinyDir = getUploadDir();
            X.log("c=" + c);
            String companyLogo = (String) XUtil.isEmpty(c.get("COMPANY_LOGO"), "<images/java-icon.png");
            boolean jar = companyLogo.startsWith("<");
            if (jar) {
                companyLogo = companyLogo.substring(1);
            }
            File f = new File(destinyDir + companyLogo);
            if (!f.exists() && jar) {
                XFile.saveResource("/" + companyLogo, f);
            }
            map.put("COMPANY_LOGO", destinyDir + companyLogo);
            companyLogo = (String) XUtil.isEmpty(c.get("COUNTRY_LOGO"), "<images/java-icon.png");
            jar = companyLogo.startsWith("<");
            if (jar) {
                companyLogo = companyLogo.substring(1);
            }
            f = new File(destinyDir + companyLogo);
            if (!f.exists() && jar) {
                XFile.saveResource("/" + companyLogo, f);
            }
            map.put("COUNTRY_LOGO", destinyDir + companyLogo);
            String reportBackground = (String) XUtil.isEmpty(c.get("report_background"), null);
            if (reportBackground != null) {
                if (jar = reportBackground.startsWith("<")) {
                    reportBackground = reportBackground.substring(1);
                }
                f = new File(destinyDir + reportBackground);
                if (!f.exists() && jar) {
                    XFile.saveResource("/" + reportBackground, f);
                }
                map.put("REPORT_BACKGROUND", destinyDir + reportBackground);
            }
            map.put("COMPANY_NAME", ("" + c.get("site_name")).toUpperCase());
        } catch (Exception e) {

        }
    }

    private static Boolean ep;

    @Override
    public boolean existsPegasus() {
        if (ep == null) {
            try {
                this.em.createNativeQuery("SELECT 1 FROM Configuracion WHERE 1=0").getSingleResult();
                ep = true;
            } catch (Exception e) {
                ep = false;
            }
        }
        return ep;
    }

    @Override
    public boolean hasAdmin() {
        return em.find(User.class, 1) != null;
    }

    @Override
    public File getUploadFile() {
        File f = new File(this.getUploadDir());
        if (!f.exists()) {
            if (!f.mkdirs()) {

            }
        }
        return f;
    }

    @Override
    public void initSystem() {
        /*if (X.config == null) {
            try {
                System.setProperty("isobit.uploadDir", getUploadDir());
                X.loadConfig("config.json");
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }*/
    }

    @Override
    public String getConfig() {
        try {
            System.setProperty("isobit.uploadDir", getUploadDir());
            return XFile.loadFile(X.loadConfig("config.json"));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void save(Map m) {
        if (m.containsKey("variables")) {
            Map mm = (Map) m.get("variables");
            X.DEBUG = XUtil.booleanValue(mm.get("LOG"));
            save("variables", mm);
        }
        if (m.containsKey("SYSTEM")) {
            save("SYSTEM", (Map) m.get("SYSTEM"));
        }
    }

    //@EJB
    //private RoleFacadeLocal roleFacade;

    public enum Perm {
        SYSTEM_ACCESS,
        SYSTEM_ADMIN,
        SYSTEM_NODE_ACCESS,
        SYSTEM_NODE_ADMIN,
        SYSTEM_BLOCK_ACCESS,
        SYSTEM_BLOCK_ADMIN,
        SYSTEM_FILE_ADMIN,
        SYSTEM_DB_ACCESS

    };

    public void init() {
        /*add(new RoleFacade.RoleModule() {
            @Override
            public Object[] getPerms() {
                return Perm.values();
            }
        });
        add(this);*/
    }

}
