package org.isobit.directory.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.isobit.app.X;
import org.isobit.app.service.SystemFacade;
import org.isobit.directory.model.Company;
import org.isobit.directory.model.Directory;
import org.isobit.directory.model.District;
import org.isobit.directory.model.DrtPJContacto;
import org.isobit.directory.model.People;
import org.isobit.directory.model.Region;
import org.isobit.directory.model.RegionPK;
import org.isobit.directory.service.CompanyFacade;
import org.isobit.directory.service.DirectoryFacade;
import org.isobit.directory.service.PeopleFacade;
import org.isobit.util.AbstractFacade;
import org.isobit.util.XMap;
import org.isobit.util.XUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@ApplicationScoped
@Service
public class CompanyFacadeImpl implements CompanyFacade {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Company> load2(int peopleId) {
        return this.getEntityManager().createQuery("SELECT pj from DrtPJContacto pc JOIN pc.company pj where pc.people.id=:people")
                .setParameter("people", peopleId).getResultList();
    }

    private EntityManager getEntityManager() {
        return em;
    }

    @Override
    public List<Company> load(int first, int pageSize, String sortField, Map<String, Object> filters) {
        
        
        
        Object qs = filters.get("qs");
        Object businessName = filters.get("razonSocial");
        Object ruc = filters.get("ruc");
        EntityManager em = this.getEntityManager();
        List<Query> ql = new ArrayList();
        String sql;
        ql.add(em.createQuery("SELECT p " + (sql = "FROM Company p  WHERE 1=1"
                + (businessName != null ? " AND UPPER(p.businessName) LIKE :businessName" : "")
                + (ruc != null ? " AND UPPER(p.ruc) LIKE :ruc" : "")
                + (qs != null ? " AND (UPPER(p.businessName) LIKE :q OR UPPER(p.ruc) LIKE :q)" : ""))));
        if (pageSize > 0) {
            ql.get(0).setFirstResult(first).setMaxResults(pageSize);
            ql.add(em.createQuery("SELECT COUNT(p) " + sql));
        }
        for (Query q : ql) {
            if (qs != null) {
                q.setParameter("q", "%" + qs.toString().replace(" ", "%").toUpperCase() + "%");
            }
            if (businessName != null) {
                q.setParameter("businessName", "%" + businessName.toString().replace(" ", "%").toUpperCase() + "%");
            }
            if (ruc != null) {
                q.setParameter("ruc", "%" + ruc.toString().toUpperCase() + "%");
            }
        }
        if (pageSize > 0) {
            filters.put("size", ql.get(1).getSingleResult());
        }
        List<Company> l = ql.get(0).getResultList();
        Map tmp = new HashMap();
        List lp = new ArrayList();
        l.stream().forEach((p) -> {
            tmp.put(p.getId(), p);
            lp.add(p.getId());
            int residency = XUtil.intValue(p.getIdUbg());
            String ubigeo = null;
            if (residency < 0) {
                ubigeo = String.format("%06d", -residency);
                Region d = em.find(Region.class, new RegionPK(155, XUtil.intValue(ubigeo.substring(0, 2))));
                ubigeo = d.getName();
            } else {
                District d = em.find(District.class, residency);
                if (d != null) {
                    try {
                        ubigeo = em.find(Region.class, new RegionPK(d.getIdPais(),
                                d.getIdDpto())).getName();
                    } catch (Exception e) {
                        ubigeo = e.toString();
                    }
                }
            }
            //p.setExt(new Object[]{ubigeo, null});
        });
        if (!lp.isEmpty()) {
            List<Object[]> l2 = em.createQuery("SELECT c.company.id,p FROM DrtPJContacto c JOIN c.people p WHERE c.orden=0 AND c.company.id IN :idPerjur ORDER BY c.orden,c.company.id,c.idPrcl")
                    .setParameter("idPerjur", lp).getResultList();
            /*l2.stream().forEach((r) -> {
                Company p = (Company) tmp.get(r[0]);
                ((Object[]) p.getExt())[1] = 0;
            });*/
        }
        return l;
    }

    @Override
    public void remove(List<Company> l) {
        //super.remove(l); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Company load(Object id) {
        Company p = em.find(Company.class,id instanceof Company ? ((Company) id).getId() : XUtil.intValue(id));
        p.getDirectory();
        HashMap ext = new HashMap();
        int residency = XUtil.intValue(p.getIdUbg());
        if (residency < 0) {
            String ubigeo = String.format("%06d", -residency);
            ext.put("region", XUtil.intValue(ubigeo.substring(0, 2)));
            ext.put("province", XUtil.intValue(ubigeo.substring(2, 4)));
            ext.put("idDist", XUtil.intValue(ubigeo.substring(4, 6)));
        } else if (residency > 0) {
            District d = getEntityManager().find(District.class, residency);
            String ubigeo = d.getCode();
            ext.put("region", XUtil.intValue(ubigeo.substring(0, 2)));
            ext.put("province", XUtil.intValue(ubigeo.substring(2, 4)));
            ext.put("idDist", XUtil.intValue(ubigeo.substring(4, 6)));
            ext.put("residency", d.getId());
        }
        DrtPJContacto c = null;
        try {
            EntityManager em = getEntityManager();
            c = em.createQuery("SELECT c FROM DrtPJContacto c WHERE c.company=:idPerjur ORDER BY c.orden", DrtPJContacto.class)
                    .setMaxResults(1).setParameter("idPerjur", p).getSingleResult();
            if (c.getPeople() == null) {
                em.detach(c);
                People people = new People();
                String s = c.getNombre();
                if (s != null) {
                    s = s.trim();
                    String s2[] = s.split(" ");
                    if (s2.length > 0) {
                        people.setFirstSurname(s2[0]);
                    }
                    if (s2.length > 1) {
                        people.setFirstSurname(s2[1]);
                    }
                    if (s2.length > 2) {
                        people.setFirstSurname(s2[2]
                                + (s2.length > 3 ? (" " + s2[3]) : "")
                        );
                    }
                    people.setFullName(s);
                }
                c.setPeople(people);

            }
        } catch (NoResultException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
        X.log("despues de tratar de encontrar DrtPJContacto=" + c);
        if (c == null) {
            c = new DrtPJContacto();
            c.setPeople(new People());
        }
        ext.put("contacto", c);
        ArrayList tipo = new ArrayList();
        int pc = XUtil.intValue(p.getProvclie());
        if ((pc & 1) == 1) {
            tipo.add(1);
        }
        if ((pc & 2) == 2) {
            tipo.add(2);
        }
        ext.put("tipo", tipo);
        //p.setExt(ext);
        return p;
    }

    @Inject
    @Autowired
    private DirectoryFacade directoryFacade;

    @Inject
    @Autowired
    //@Qualifier("peopleFacadeLocalImpl2")
    private PeopleFacade peopleFacade;

    @Inject
    @Autowired
    private SystemFacade systemFacade;

    @Override
    public void edit(Company entity) {
        /*
            Otra persona juridica no debe tener el mismo ruc a grabar
            
         */
//        if (!systemFacade.hasAllAccess()) {
//            return;
//        }
        /*Map ext = (Map) entity.getExt();
        if (ext != null) {
            int idDist = XUtil.intValue(ext.get("idDist"));
            int residency = XUtil.intValue(ext.get("residency"));
            if (residency > 0) {
                entity.setIdUbg(residency);
            } else {
                entity.setIdUbg(-XUtil.intValue(String.format("%02d%02d%02d",
                        XUtil.intValue(ext.get("region")),
                        XUtil.intValue(ext.get("province")),
                        idDist)));
            }
            int t = 0;
            Object tl0 = ext.get("tipo");
            if (tl0 instanceof Object[]) {
                Object[] tl = (Object[]) tl0;
                for (Object o : tl) {
                    t = t | XUtil.intValue(o);
                }
                entity.setProvclie((char) (t + " ").charAt(0));
            }
        }*/
        if (entity.getId() == null) {
            Directory drtDirectorio = entity.getDirectory();
            if (drtDirectorio == null) {
                drtDirectorio = new Directory();
            }
            //drtDirectorio.setIdDclas(2);
            if (XUtil.isEmpty(entity.getRuc())) {//ruc REGEXP '^[0-9]+$'
                entity.setRuc("" + (XUtil.intValue(getEntityManager().createQuery("SELECT MIN(1*ruc) FROM Company c WHERE (1*c.ruc)<0").getSingleResult()) - 1));
            }
            directoryFacade.create(drtDirectorio);
            entity.setId(drtDirectorio.getId());
            entity.setDirectory(drtDirectorio);
            if (entity.getFechaIng() == null) {
                entity.setFechaIng(X.getServerDate());
            }
            //if(entity.get)
            em.persist(entity);
        } else {
            em.merge(entity);
        }
        /*if (ext != null) {
            Object oo = ext.get("contacto");
            if (oo != null) {*/
                /*DrtPJContacto c = (DrtPJContacto) (oo instanceof Map ? BeanUtils.getObject(DrtPJContacto.class, oo) : oo);
                People p = c.getPeople();
                //Debe modificarse
                if (!XUtil.isEmpty(p.getCode())) {
                    EntityManager em = this.getEntityManager();
                    peopleFacade.edit(p);
                    if (c.getIdPrcl() == null) {
                        c.setIdPrcl(1 + XUtil.intValue(em.createQuery("SELECT MAX(p.idPrcl) FROM DrtPJContacto p").getSingleResult()));
                        c.setCompany(entity);
                        c.setOrden(1);
                        c.setTratamiento("");
                        c.setNombre(p.getFullName());
                        em.persist(c);
                    } else {
                        em.merge(c);
                    }
                }*/
            /*}
            String tempFile = (String) ext.get("tempFile");
            if (!XUtil.isEmpty(tempFile)) {
                try {
                    File f = new File(File.createTempFile("temp-file-name", ".tmp").getParentFile(), tempFile);
                    File f0 = new File(XFile.getFile(new File(systemFacade.getUploadDir() + "foto/")), entity.getId() + ".jpg");
                    if (f0.exists()) {
                        f0.delete();
                    }
                    if (XFile.copy(f, f0) == null) {
                        throw new RuntimeException("La foto " + f + " no pudo ser guardada en " + f0);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(PeopleFacadeLocalImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }*/
       // }
    }

    @Override
    public Object loadByNumero(Object numeroPndid) {
        List l = peopleFacade.load(0, 0, null, new XMap("numeroPndid", numeroPndid));
        return l.size() > 0 ? l.get(0) : null;
    }

    @Override
    public List list(Object p, String cls) {
        Query q = null;
        if (p instanceof Company) {
            q = getEntityManager().createQuery("SELECT p,pn,d FROM DrtPJContacto p JOIN p.people pn JOIN pn.document d WHERE p.company.id=:idPerjur")
                    .setParameter("idPerjur", ((Company) p).getId());
        } else {
            q = getEntityManager().createQuery("SELECT p,0 FROM DrtPJContacto p WHERE p.people.id=:idPerjur")
                    .setParameter("idPerjur", ((People) p).getId());
        }
        return AbstractFacade.getColumn(q.getResultList());
    }

    @Override
    public List getComCuentaSummary() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
