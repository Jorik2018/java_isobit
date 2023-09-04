package org.isobit.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import org.isobit.app.X;
import org.isobit.app.X.PersistAction;
//import org.primefaces.model.LazyDataModel;

public abstract class AbstractController<T> {

    private Exception RuntimeException(String generic_controller_require_class_type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static enum Scope {
        SESSION, PAGE, VIEW, REQUEST
    };

    private Scope SCOPE = Scope.VIEW;

    protected void setScope(Scope s) {
        SCOPE = s;
    }

    public static interface ControllerListener {

        public void afterPersist(AbstractController c, Object data);

    }

    public AbstractController() {
        try {
            String g = this.getClass().getGenericSuperclass().toString();
//            System.out.println("g="+g+" DESDE "+this.getClass());
            if (!g.contains("<")) {
                g = this.getClass().getSuperclass().getGenericSuperclass().toString();
            }
//            System.out.println("Buscando generico para "+g+" DESDE "+this.getClass());
            int i = g.indexOf("<");
            if (i > -1) {
                entityClass = (Class<T>) Class.forName(g.substring(i + 1, g.indexOf(">")));
            }
//            if(entityClass==null) throw new RuntimeException("Generic controller require class type");
//            System.out.println("Se reconocio generico para "+entityClass);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Class<T> entityClass;
    protected List<T> items = null;
    private T selected;
//    private Map params = new HashMap();
    protected List<T> selectedList;

    static {
        //tiene q evaluarze una manera mas sencilla ya se espera q jasperreports importe una font nuevas
        System.setProperty("java.awt.headless", "true");
    }

    public void process(Map m) {
        String s = X.toText(m.get("ACTION"));
        if ("delete".equalsIgnoreCase(s)) {
            destroy();
        } else if ("insert".equalsIgnoreCase(s)) {
            create();
        }
    }

    public void process(String action, Object o) {
        if ("delete".equalsIgnoreCase(action)) {
            destroy();
        } else if ("insert".equalsIgnoreCase(action)) {
            create();
        }
    }

    public Object put(String k, Object v) {
        getParams().put(k, v);
        return v;
    }

    public void reset(Object o) {
        reset();
    }

    public void reset() {
        this.selected = null;
        this.items = null;
        selectedList = null;
    }

    /*public <T> T getModule(Class<T> cls, String module) {
        try {
            String s = "java:module/" + module + "Facade";
//            X.log("cls=" + cls + ";getModule > " + s);
            Object o = new InitialContext().lookup(s);
            return (T) (o instanceof AbstractFacade ? ((AbstractFacade) o).getModule(cls) : ((AbstractFacadeLocal) o).getModule(cls));
        } catch (javax.naming.NameNotFoundException e) {
//            X.log("No se encuentra java:module/" + module + "Facade desde este war");
            return null;
            //throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }*/

    public Object clear() {
        reset();
        return this;
    }

    public Map getP() {
        return getParams();

    }

    public void setP(Map params) {
        setParams(params);
    }

    public Map getParams() {
        Map m = (Map) X.getSession().getAttribute(entityClass.getName() + ".p");
        if (m == null) {
            X.getSession().setAttribute(entityClass.getName() + ".p", m = new HashMap());
        }
        return m;

    }

    public void setParams(Map params) {
        //X.getSession().setAttribute(entityClass.getName() + ".p", params);
    }

    public T prepareCreate() {
        /*try {
            setSelected(selected = entityClass.newInstance());
        } catch (InstantiationException | IllegalAccessException ex) {
            X.alert(ex);
        }
        initializeEmbeddableKey();*/
        return selected;
    }

    public void setSelected(T selected) {
        /*if (SCOPE == Scope.SESSION) {
            X.getSession().setAttribute(entityClass.getName() + ".selected", this.selected = selected);
        } else {
            X.getViewMap().put(entityClass.getName() + ".selected", this.selected = selected);
        }*/
    }

    public T getSelected() {
        /*if (selected == null) {
            if (SCOPE == Scope.SESSION) {
                return (T) X.getSession().getAttribute(entityClass.getName() + ".selected");
            } else {
                return (T) X.getViewMap().get(entityClass.getName() + ".selected");
            }
        }*/
        return selected;
    }

    public void setSelectedList(List<T> selectedList) {
        if (selectedList != null && selectedList.size() > 0) {
            setSelected(selectedList.get(0));
        }
        if (SCOPE == Scope.SESSION) {
            X.getSession().setAttribute(entityClass.getName() + ".selectedList", this.selectedList = selectedList);
        } else {
            //X.getViewMap().put(entityClass.getName() + ".selectedList", this.selectedList = selectedList);
        }
        this.selectedList = selectedList;
    }

    /*public List<T> getSelectedList() {
        if (selectedList == null) {
            if (SCOPE == Scope.SESSION) {
                return (List<T>) X.getSession().getAttribute(entityClass.getName() + ".selectedList");
            } else {
                return (List<T>) X.getViewMap().get(entityClass.getName() + ".selectedList");
            }
        }
        return selectedList;
    }*/

    public void create() {
        persist(X.PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("RecordCreated"));
        /*if (!X.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }*/
    }

    public void update() {
        persist(X.PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("RecordUpdated"));
    }

    protected abstract void persist(PersistAction persistAction, String successMessage);

    public void destroy() {
        /*persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("RecordDeleted"));
        if (!X.isValidationFailed()) {
            setSelected(null);
            items = null;    // Invalidate list of items to trigger re-query.
        }*/
    }

    /*private LazyDataModel<T> dataModel = null;

    public LazyDataModel<T> getDataModel() {
        if (dataModel == null) {
            dataModel = createLazyListModel();
        }
        return dataModel;
    }

    protected LazyDataModel createLazyListModel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }*/

    protected void initializeEmbeddableKey() {

    }

    protected void setEmbeddableKeys() {

    }

    private Object content;

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }
}
