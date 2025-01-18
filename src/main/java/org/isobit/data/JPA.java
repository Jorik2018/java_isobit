package org.isobit.data;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.isobit.app.X;
import org.isobit.data.proxy.Proxy;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import jakarta.persistence.EntityManager;

public class JPA {
  private static JPA instance;
  
  public static JPA getInstance() {
    if (instance == null)
      instance = new JPA(); 
    return instance;
  }
  
  public static boolean client = false;
  
  private static final ThreadLocal<Connection> tc = new ThreadLocal<>();
  
  private static final ThreadLocal<Map<String, EntityManager>> tj = new ThreadLocal<>();
  
  private static final Map<String, EntityManagerFactory> entityManagerFactoryMap = new HashMap<>();
  
  private static final ThreadLocal<EntityTransaction> tt = new ThreadLocal<>();
  
  private static final ThreadLocal tk = new ThreadLocal();
  
  private static final ThreadLocal msg = new ThreadLocal();
  
  private String ds_name;
  
  public Connection getConnection() {
    if (this.ds_name == null)
      try {
        InputStream in = getClass().getResourceAsStream("/META-INF/persistence.xml");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);
        factory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(in);
        NodeList nodes = doc.getElementsByTagName("jta-data-source");
        if (nodes != null && nodes.getLength() > 0) {
          int length = nodes.getLength();
          int j = 0;
          if (j < length) {
            Node node = nodes.item(j);
            this.ds_name = node.getTextContent();
          } 
        } 
      } catch (Exception e) {
        X.log(e);
      }  
    return getConnection(this.ds_name);
  }
  
  public Connection getConnection(String n) {
    Connection cnx = tc.get();
    if (cnx == null)
      if (client) {
        cnx = Proxy.getService("system").getConnection();
      } else {
        try {
          tc.set(cnx = ((DataSource)(new InitialContext()).lookup(n)).getConnection());
          X.log("CONNECTION CREATED!");
        } catch (SQLException e) {
          throw new RuntimeException(e);
        } catch (NamingException e) {
          throw new RuntimeException(e);
        } 
      }  
    return cnx;
  }
  
  public EntityManager getEntityManager(boolean transaction) {
    EntityManager em = getEntityManager();
    if (transaction)
      getTransaction(); 
    return em;
  }
  
  public EntityManager getEntityManager() {
    return getEntityManager("oceca_test");
  }
  
  public EntityManager getEntityManager(String n) {
    Map<String,EntityManager> m = (Map)tj.get();
    if (m == null) tj.set(m = new HashMap<>()); 
    EntityManager em = m.get(n);
    if (em == null)
      try {
        EntityManagerFactory emf = entityManagerFactoryMap.get(n);
        if (emf == null)
          entityManagerFactoryMap.put(n, emf = Persistence.createEntityManagerFactory(n)); 
        X.log("Tratando crea EntityManager");
        m.put(n, em = emf.createEntityManager());
        X.log("EntityManager '" + n + "' created!");
      } catch (Exception ex) {
        ex.printStackTrace();
      }  
    return em;
  }
  
  public EntityTransaction getTransaction(Object key) {
    EntityTransaction t = tt.get();
    if (t == null) {
      EntityManager em = getEntityManager();
      t = em.getTransaction();
      t.begin();
      tt.set(t);
      tk.set(key);
      X.log("se reserva transaccion usando " + key);
    } 
    return t;
  }
  
  public EntityTransaction getTransaction() {
    EntityTransaction t = tt.get();
    if (t == null) {
      EntityManager em = getEntityManager();
      t = em.getTransaction();
      t.begin();
      tt.set(t);
    } 
    return t;
  }
  
  public boolean close() {
    boolean ok = true;
    Connection c = tc.get();
    if (c != null)
      try {
        if (!c.isClosed()) {
          c.close();
          X.log("CONNECTION CLOSED!");
        } 
      } catch (SQLException ex) {
        X.alert(ex);
        ok = false;
      } finally {
        X.log("CONNECTION REMOVED!");
        tc.remove();
      }  
    Map<String, EntityManager> m = tj.get();
    for (Map.Entry<String, EntityManager> entry : m.entrySet()) {
      EntityManager em = entry.getValue();
      if (em.isOpen()) {
        commit();
        if (em.isOpen())
          em.close(); 
      } 
      X.log("EntityManager '" + (String)entry.getKey() + "' closed!");
    } 
    m.clear();
    return ok;
  }
  
  public <T> T get(Class<T> clazz, Object obj) {
    Object id = obj;
    if (obj == X.NEW) {
      try {
        obj = clazz.getDeclaredConstructor().newInstance();
      } catch (InstantiationException e) {
        throw new RuntimeException(e);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      } catch (IllegalArgumentException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    } catch (InvocationTargetException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    } catch (NoSuchMethodException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    } catch (SecurityException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    } 
    } else {
      if (clazz.isInstance(obj))
        return (T)obj; 
      obj = getInstance().getEntityManager().find(clazz, obj);
    } 
    return (T)obj;
  }
  
  public boolean commit() {
    EntityTransaction t = tt.get();
    boolean ok = true;
    if (t != null && t.isActive())
      try {
        if (!t.getRollbackOnly()) {
          t.commit();
          X.log("COMMITED!");
        } 
      } catch (Exception e) {
        e.printStackTrace();
        X.alert(e);
        ok = false;
        if (t.isActive())
          t.rollback(); 
      }  
    tt.remove();
    return ok;
  }
  
  public boolean commit(Object k) {
    if (k == tk.get()) {
      commit();
      X.log("COMMITED USING " + k + "!");
      tk.remove();
      return true;
    } 
    return false;
  }
  
  public void rollback(Object k) {
    if (k == tk.get()) {
      rollback();
      tk.remove();
    } 
  }
  
  public void rollback(Object k, Exception e) {
    e.printStackTrace();
    X.alert(e);
    if (k == tk.get()) {
      rollback();
      tk.remove();
    } 
  }
  
  public void rollback() {
    EntityTransaction t = tt.get();
    if (t != null && t.isActive())
      try {
        t.rollback();
        tt.remove();
      } catch (Exception e) {
        X.alert(e);
      }  
  }
}
