package org.isobit.directory.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import org.isobit.app.X;
import org.isobit.app.model.User;

import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.PersistenceContext;

import org.isobit.app.service.SessionFacade;
import org.isobit.app.service.SystemFacade;
import org.isobit.directory.model.Directory;
import org.isobit.directory.model.District;
import org.isobit.directory.model.DocumentType;
import org.isobit.directory.model.DrtEstadoCivil;
import org.isobit.directory.model.People;
import org.isobit.directory.model.Province;
import org.isobit.directory.model.Region;
import org.isobit.directory.model.RegionPK;
import org.isobit.directory.service.DirectoryFacade;
import org.isobit.directory.service.PeopleFacade;
import org.isobit.util.AbstractFacade;
import org.isobit.util.Ext;
import org.isobit.util.XFile;
import org.isobit.util.XMap;
import org.isobit.util.XUtil;
import org.springframework.stereotype.Service;

@ApplicationScoped
//@Service("peopleFacadeLocalImpl2")
//@Transactional
@Service
public class PeopleFacadeImpl
        implements PeopleFacade//,
        //UserFacadeLocal.CrudModule<User>,
        //        QuizParticipacionFacade.QuizParticipacionModule,
        /*TestFacadeLocal*/ {
    @PersistenceContext
    private EntityManager em;
    /*@Override
    public void init(HttpSession session) {
        User u = (User) session.getAttribute(X.USER);
        if (u == null) {
            u = getEntityManager().find(User.class, 1);
            if (u == null) {
                u = new User();
            }
            u.setUid(1);
            u.setName("Jorik");
            if (XUtil.intValue(u.getIdDir()) == 0) {
                u.setIdDir(6108);
            }
            Map ext = new HashMap();
            session.setAttribute(X.USER, u);
            People pn = find(u.getIdDir());
            ext.put(People.class.getName(), pn);
            session.setAttribute("personaNatural", pn);
            session.setAttribute(People.class.getName(), pn);
        }
    }*/

    @Override
    public List load(int first, int pageSize, String sortField, Map<String, Object> filters) {
        Object people = filters.get("people");
        Object numeroPndid = XUtil.isEmpty(filters.get("code"), null);
        Object mail = XUtil.isEmpty(filters.get("mail"), null);
        List<Query> ql = new ArrayList();
        String sql;
        EntityManager em = this.getEntityManager();
//        em.createQuery("SELECT p FROM People p JOIN p.document d ");
        ql.add(em.createQuery("SELECT p,d " + (sql = "FROM People p JOIN p.document d WHERE 1=1 "
                + (people != null ? " AND (UPPER(p.fullName) LIKE :q OR p.code like :q)" : "")
                + (numeroPndid != null ? " AND p.code like :code" : "")
                + (mail != null ? " AND p.emailPrin like :mail" : ""))));
        if (pageSize > 0) {
            ql.get(0).setFirstResult(first).setMaxResults(pageSize);
            ql.add(em.createQuery("SELECT COUNT(p) " + sql));
        }
        for (Query q : ql) {
            if (people != null) {
                q.setParameter("q", "%" + people.toString().toUpperCase().replaceAll("\\s+", "%") + "%");
            }
            if (numeroPndid != null) {
                q.setParameter("code", "%" + numeroPndid.toString().toUpperCase().replaceAll("\\s+", "%") + "%");
            }
            if (mail != null) {
                q.setParameter("mail", "%" + mail.toString().toLowerCase().replaceAll("\\s+", "%") + "%");
            }
        }
        if (pageSize > 0) {
            filters.put("size", ql.get(1).getSingleResult());
        }
        List<People> l = AbstractFacade.getColumn(ql.get(0).getResultList());
        Map tmp = new HashMap();
        l.stream().forEach((p) -> {
            tmp.put(p.getId(), p);
            int idUbg = XUtil.intValue(p.getIdUbgNac());
            String ubigeo = null;
            if (idUbg < 0) {
                ubigeo = String.format("%06d", -idUbg);
                //System.out.println("u=" + ubigeo);
                Region d = em.find(Region.class, new RegionPK(155, XUtil.intValue(ubigeo.substring(0, 2))));
                ubigeo = d.getName();
            } else {
                District d = em.find(District.class, idUbg);
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
        return l;
    }

    @EJB
    private DirectoryFacade drtDirectorioFacade;

    @EJB
    private SessionFacade sessionFacade;

    @EJB
    private SystemFacade systemFacade;

    @Override
    public void edit(People entity) {
        EntityManager em = this.getEntityManager();
        entity.setFullName(XUtil.implode(new Object[]{
            entity.getFirstSurname(),
            entity.getLastSurname(),
            entity.getNames()
        }, " "));
        if (XUtil.isEmpty(entity.getSex())) {
            entity.setSex('?');
        }
        /*Object o = entity.getExt();
        if (o instanceof Map) {
            Map ext = (Map) o;
            int idDist = XUtil.intValue(ext.get("idDist"));
            int idUbg = XUtil.intValue(ext.get("idUbg"));
            if (idUbg > 0) {
                entity.setIdUbgNac(idUbg);
            } else if (idDist > 0) {
                entity.setIdUbgNac(-XUtil.intValue(String.format("%02d%02d%02d",
                        XUtil.intValue(ext.get("idDpto")),
                        XUtil.intValue(ext.get("idProv")),
                        idDist)));
            }
            idUbg = XUtil.intValue(ext.get("residency"));
            if (idUbg > 0) {
                entity.setIdUbgPro(idUbg);
            }
        }*/
        if (entity.getId() == null) {
            if (em.createQuery("SELECT count(p) FROM People p WHERE p.code=:code AND p.documentType=:document", Number.class)
                    .setParameter("code", entity.getCode())
                    .setParameter("documentType", entity.getDocumentType())
                    .getSingleResult().intValue() > 0) {
                throw new RuntimeException("La persona natural con " + entity.getDocumentType().getName() + " " + entity.getCode() + " ya esta en el directorio!");
            }
            Directory directory = new Directory();
            directory.setTypeId((short)1);
            //drtDirectorioFacade.create(directory);
            entity.setId(directory.getId());
            if (entity.getFechaIng() == null) {
                entity.setFechaIng(X.getServerDate());
            }

            em.persist(entity);
        } else {
            em.merge(entity);
        }
        /*if (o instanceof Map) {
            Map ext = (Map) o;
            User account = (User) ext.get("user");
            if (account
                    != null) {
                try {
                    getEntityManager().createQuery("UPDATE User u SET u.mail=:mail,u.idDir=:idDir WHERE u.uid=:uid")
                            .setParameter("uid", account.getUid())
                            .setParameter("mail", entity.getMail())
                            .setParameter("idDir", entity.getId())
                            .executeUpdate();
                    User user = (User) sessionFacade.get(X.USER);
                    if (user == account) {
                        user.setDirectoryId(entity.getId());
                    }
                } catch (PersistenceException e) {
                }
            }
            String tempFile = (String) ext.get("tempFile");
            if (!XUtil.isEmpty(tempFile)) {
                if (tempFile.startsWith("data")) {
                    System.out.println("tempFile.split(\",\")[1]=" + tempFile.split(",")[1].trim());
                    byte[] data = Base64.getDecoder().decode(tempFile.split(",")[1].trim());
                    try (OutputStream stream = new FileOutputStream(new File(XFile.getFile(new File(systemFacade.getUploadDir() + "foto/")), entity.getId() + ".jpg"))) {
                        stream.write(data);
                    } catch (Exception ex) {
                        throw new RuntimeException("Error al guardar la foto del empleado desde RENIEC.", ex);
                    }
                } else {
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
                }
            }

        }*/
        try {
            getEntityManager().createQuery("UPDATE User u SET u.mail=:mail WHERE u.idDir=:idDir")
                    .setParameter("mail", entity.getMail())
                    .setParameter("idDir", entity.getId())
                    .executeUpdate();
        } catch (PersistenceException e) {
        }
    }

    private EntityManager getEntityManager() {
        return null;
    }

    /*@Override
    public Object[] load(Map respuestaMap, EntityManager em, QuizParticipacion participacion, QuizPregunta parent, QuizPregunta pregunta) {
        Map m = (Map) pregunta.getExt();
        Object r = respuestaMap.get((parent != null ? parent.getIdPregunta() : "0") + "-" + pregunta.getIdPregunta());
        if (r != null) {
            if (!(r instanceof List)) {
                List lr = new ArrayList();
                lr.add(r);
                r = lr;
            }
        } else {
            r = Collections.EMPTY_LIST;
        }
        m.put("src", "/sgc/encuesta/GeneralInformation.xhtml");
        Object[] a = new Object[11];
        for (Valuable v : (List<Valuable>) r) {
            a[v.getQuizRespuestaPK().getIdItem()] = v.getValue();
        }
        People pn = participacion.getPersonaNatural();
        if (a[4] == null && pn != null) {
            a[4] = pn.getNumeroPndid();
            a[1] = pn.getNombre();
            a[2] = pn.getApPaterno();
            a[3] = pn.getApMaterno();
        }
        r = a;
        return new Object[]{r};
    }

    @Override
    public int save(Map m, EntityManager em, QuizParticipacion participacion, QuizPregunta parent, QuizPregunta pregunta, Object respuesta) {
        int i = 0;
        for (Object o : (Object[]) respuesta) {
            if (o != null && o.toString().trim().length() > 0) {
                QuizRespuestaTexto r = new QuizRespuestaTexto(new QuizRespuestaPK(
                        participacion.getIdParticipacion(),
                        parent != null ? parent.getIdPregunta() : 0,
                        pregunta.getIdPregunta(),
                        i),
                        participacion.getQuizEncuesta().getIdEncuesta(),
                        o.toString()
                );
                em.persist(r);
            }
            i++;
        }
        if (XUtil.isEmpty(participacion.getPersonaNatural().getNombreCompleto())) {
            People pn = participacion.getPersonaNatural();
            Object r[] = (Object[]) respuesta;
            Object o = r[1];
            if (o != null && o.toString().trim().length() > 0) {
                pn.setNombre("" + o);
            }
            o = r[2];
            if (o != null && o.toString().trim().length() > 0) {
                pn.setApPaterno("" + o);
            }
            o = r[3];
            if (o != null && o.toString().trim().length() > 0) {
                pn.setApMaterno("" + o);
            }
            pn.setNombreCompleto(XUtil.implode(new Object[]{pn.getApPaterno(), pn.getApMaterno(), pn.getNombre()}, " "));
            if (pn.getIdDir() != null) {
                em.merge(pn);
            }
        }
        return 0;
    }*/
    @Override
    public List<DrtEstadoCivil> getDrtEstadoCivilList() {
        return getEntityManager().createQuery("SELECT e FROM DrtEstadoCivil e ORDER BY e.name").getResultList();
    }

    @Override
    public People getAnonymus() {
        EntityManager em = this.getEntityManager();
        People p = em.find(People.class,
                0);
        if (p == null) {
            Directory d = new Directory();
            d.setId(0);
            /*this.drtDirectorioFacade.create(d);
            p = new People();
            p.setId(d.getIdDir());
            p.setNames("Anonymous");
            p.setApPaterno("");
            p.setApMaterno("");
            this.create(p);*/
        }
        return p;
    }

    @Override
    public int count(String opt, Object v) {
        EntityManager em = getEntityManager();
        return XUtil.intValue(em.createQuery("SELECT COUNT(p) FROM People p WHERE p.numeroPndid=:numeroPndid")
                .setParameter("numeroPndid", v).getSingleResult());
    }

    //@PostConstruct
    public void init() {
      //  add(this);
    }

    /*@Override
    public void afterEdit(User account, Object ref) {
        EntityManager em = this.getEntityManager();
        People personaNatural = (People) ref;
        if (personaNatural.getExt() == null) {
            personaNatural.setExt(new XMap("user", account));
        }
        personaNatural.setMail(account.getMail());
        edit(personaNatural);
        account.setIdDir(personaNatural.getId());
        em.merge(account);
    }
*/
    /*@Override
    public void afterDelete(User u) {

    }*/

    @Override
    public People find(Object id) {
        return em.find(People.class,id);
    }

    public Map loadExt(People people) {
        Ext ext = new Ext();
        if (XUtil.intValue(people.getIdUbgNac()) != 0) {
            try {
                Object r[] = (Object[]) getEntityManager().createQuery("SELECT d,p,r FROM District d JOIN d.province p JOIN p.region r WHERE d.id=:id")
                        .setParameter("id", people.getIdUbgNac()).getSingleResult();
                if (r != null) {
                    District district = (District) r[0];
                    ext.put("region", district.getIdDpto());
                    ext.put("regionName", ((Region) r[2]).getName());
                    ext.put("province", district.getIdProv());
                    ext.put("provinceName", ((Province) r[1]).getName());
                    ext.put("district", district.getId());
                    ext.put("districtName", district.getName());
                }
            } catch (NoResultException | NonUniqueResultException e) {
            }
        }
        if (XUtil.intValue(people.getIdUbgPro()) != 0) {
            try {
                Object r[] = (Object[]) getEntityManager().createQuery("SELECT d,p,r FROM District d JOIN d.province p JOIN p.region r WHERE d.id=:id")
                        .setParameter("id", people.getIdUbgPro()).getSingleResult();
                if (r != null) {
                    District district = (District) r[0];
                    ext.put("region2", String.format("%02d", district.getIdDpto()));
                    ext.put("regionName2", ((Region) r[2]).getName());
                    ext.put("province2", String.format("%02d%02d", district.getIdDpto(), district.getIdProv()));
                    ext.put("provinceName2", ((Province) r[1]).getName());
                    ext.put("districtName2", district.getName());
                }
            } catch (NoResultException | NonUniqueResultException e) {
            }
        }
        return ext;
    }

    @Override
    public Map getUbigeo(Integer districtId) {
        Ext ubigeo = new Ext();
        try {
            Object r[] = (Object[]) getEntityManager().createQuery("SELECT d,p,r FROM District d JOIN d.province p JOIN p.region r WHERE d.id=:id")
                    .setParameter("id", districtId).getSingleResult();
            if (r != null) {
                District district = (District) r[0];
                ubigeo.put("region", district.getIdDpto());
                ubigeo.put("regionName", ((Region) r[2]).getName());
                ubigeo.put("province", district.getIdProv());
                ubigeo.put("provinceName", ((Province) r[1]).getName());
                ubigeo.put("district", district.getId());
                ubigeo.put("districtName", district.getName());
            }
        } catch (NoResultException | NonUniqueResultException e) {
        }
        return ubigeo;
    }

    @Override
    public People load(Object id) {
        People p = null;
        if (id instanceof Map) {
            Map m = (Map) id;
            People people = (People) m.get("people");
            List l = load(0, 0, null,
                    new XMap("numeroPndid", people.getCode()));
            p = !l.isEmpty() ? (People) l.get(0) : null;
        } else {
            try {
                p = (People) ((Object[]) getEntityManager().createQuery("SELECT p,d FROM People p JOIN p.document d WHERE p.id=:id")
                        .setParameter("id", id instanceof People
                                ? ((People) id).getId() : XUtil.intValue(id)).getSingleResult())[0];
            } catch (NoResultException e) {
            }
        }
        if (p != null) {
            p.getDocumentType().getId();
            //p.setExt(this.loadExt(p));
        }
        return p;
    }

    @Override
    public Object importFile(File f) {
        List<Object[]> l;
        try {
            l = (List<Object[]>) ((Object[]) XFile.readObject(f))[0];
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        EntityManager em = getEntityManager();
        Object[] payroll = l.get(0);
        Query q = em.createQuery("SELECT p FROM People p WHERE p.code>0 AND (1*p.code)=:code");

        for (int r = 1; r < l.size(); r++) {
            Object row[] = l.get(r);
            People p = null;
            try {
                p = (People) q.setParameter("code", XUtil.intValue(row[0])).setFirstResult(0).setMaxResults(1).getSingleResult();
            } catch (Exception e) {
                p = new People();
                p.setDocumentType(em.find(DocumentType.class, (short) 4));
                p.setCode("" + XUtil.intValue(row[0]));
                p.setLastSurname(X.toText(row[1]));
                p.setFirstSurname("");
                p.setNames(X.toText(row[2]));
            }
            p.setSex(XUtil.intValue(row[3]) > 1 ? 'F' : 'M');
            //p.setBirthdate((Date) row[4]);
            edit(p);
        }

        return true;
    }

}
