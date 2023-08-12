package org.isobit.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ejb.EJBException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import jakarta.persistence.PersistenceContext;
import org.isobit.app.X;

public abstract class AbstractFacade<T> implements AbstractFacadeLocal {

    @Override
    public void preProcess(Object o) {
        X.log("preProcess");
    }

    ;

    @Override
    public void postProcess(Object o) {
        X.log("postProcess");
    }

    ;

    protected void reset() {
    }

    private List modulesList = new ArrayList();

    protected void add(Object module) {
        modulesList.add(module);
    }

    public void remove(List<T> l) {
        X.log("remove " + l);
        for (T t : l) {
            remove(t);
        }
    }

    private Method idField;

    private Method getIdFieldMethod() {
        if (idField == null) {
            Field f2 = null;
            for (Field f : entityClass.getDeclaredFields()) {
                if (f.getAnnotation(Id.class) != null) {
                    f2 = f;
                } else if (f.getAnnotation(EmbeddedId.class) != null) {
                    f2 = f;
                }
            }
            idField = BeanUtils.getReadMethod(entityClass, f2.getName());
        }
        return idField;
    }

    public void delete(Object a) {
        if (entityClass.isAssignableFrom(a.getClass())) {
            this.remove((T) a);
        } else {
            this.remove(this.find(a));
        }
    }

    public void remove(T a) {
        try {
            this.getEntityManager().remove(getEntityManager().find(a.getClass(), getIdFieldMethod().invoke(a)));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public <T> T getModule(Class<T> cls, Object module) {
        try {
            try {
                Object o = new InitialContext().lookup("java:module/" + module + "Facade");
                if (o.getClass().isAssignableFrom(cls)) {
                    return (T) o;
                }
//                System.out.println("oooo="+o);
                o = (o instanceof AbstractFacade
                        ? ((AbstractFacade) o).getModule(cls)
                        : ((AbstractFacadeLocal) o).getModule(cls));
                if (o instanceof ModuleNotFoundException) {
                    throw (ModuleNotFoundException) o;
                }
                return (T) o;
            } catch (NamingException e) {
                Object o = new InitialContext().lookup("java:module/" + module + "FacadeLocalImpl");
                o = (o instanceof AbstractFacade
                        ? ((AbstractFacade) o).getModule(cls)
                        : ((AbstractFacadeLocal) o).getModule(cls));
                if (o instanceof ModuleNotFoundException) {
                    throw (ModuleNotFoundException) o;
                }
                return (T) o;
            }
        } catch (NamingException | java.lang.ClassCastException | ModuleNotFoundException e) {

            X.log("ERROR getModule(cls=" + cls + ";module=" + module + ")=>NamingException: " + e.getMessage());
            throw new SimpleException(e.getMessage(), e);
        } catch (EJBException e) {
            Exception ex = e.getCausedByException();
            if (ex instanceof ModuleNotFoundException) {
                X.log("ERROR getModule(cls=" + cls + ";module=" + module + ")=>NamingException: " + ex.getMessage());
                throw new SimpleException(ex.getMessage(), ex);
            } else {
                throw e;
            }
        }
    }

    public <T> T getModule(Class<T> cls) {
        for (Object m : modulesList) {
            if (cls.isAssignableFrom(m.getClass())) {
                return (T) m;
            }
        }
        throw new ModuleNotFoundException("Module '" + cls + "' no found in " + this.getClass());
    }

    public Object getModule(Object o) {
        for (Object m : modulesList) {
            if (((Class) o).isAssignableFrom(m.getClass())) {
                return m;
            }
        }
        return new ModuleNotFoundException("Module '" + o + "' no found in " + this.getClass());
    }

    public static List getColumn(List<Object[]> l) {
        List l2 = new ArrayList();
        l.stream().forEach((r) -> {
            l2.add(r[0]);
        });
        return l2;
    }

    public interface RowAdapter {

        public Object adapting(Object[] row);
    }

    public static List getColumn(List<Object[]> l, RowAdapter rowAdapter) {
        List l2 = new ArrayList();
        l.stream().forEach((r) -> {
            l2.add(rowAdapter.adapting(r));
        });
        return l2;
    }

    @PersistenceContext(unitName = SYSTEM_UNIT_NAME)
    private EntityManager em;

    public static final String SYSTEM_UNIT_NAME = "isobit";

    protected EntityManager getEntityManager() {
        return em;
    }

    public AbstractFacade() {
        try {
            String g = this.getClass().getGenericSuperclass().toString();
//            X.log("1er intento para "+g+" DESDE "+this.getClass());
            if (!g.contains("<")) {
                g = this.getClass().getSuperclass().getGenericSuperclass().toString();
            }
//            X.log("Buscando generico para "+g+" DESDE "+this.getClass())
            int i = g.indexOf("<");
            if (i > -1) {
                entityClass = (Class<T>) Class.forName(g.substring(i + 1, g.indexOf(">")));
            } else {
                X.log(this.getClass() + "->" + g);
            }
        } catch (ClassNotFoundException ex) {
            //X.alert(ex);
        }
    }

    private Class<T> entityClass;

    public AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public void create(T entity) {
        getEntityManager().persist(entity);
    }

    public void edit(T entity) {
        getEntityManager().merge(entity);
    }

//    @Transactional
    public T find(Object id) {
        return getEntityManager().find(entityClass, id);
    }

    public List<T> findAll() {
        jakarta.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).getResultList();
    }

    public List<T> findRange(int[] range) {
        jakarta.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        jakarta.persistence.Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(range[1]);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    public long count() {
        jakarta.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        jakarta.persistence.criteria.Root<T> rt = cq.from(entityClass);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        jakarta.persistence.Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult());
    }

    public List<T> load(int first, int pageSize, String sortField, Map<String, Object> filters) {
        jakarta.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        jakarta.persistence.Query q = getEntityManager().createQuery(cq);
        if (pageSize > 0) {
            q.setMaxResults(pageSize);
            q.setFirstResult(first);
        }
        return q.getResultList();
    }

}

class ModuleNotFoundException extends RuntimeException {

    public ModuleNotFoundException(String message) {
        super(message);
    }

}
