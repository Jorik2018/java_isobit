package org.isobit.data.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
//import java.lang.reflect.Proxy;
import com.google.gson.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.isobit.app.X;
import org.isobit.data.Q;
import org.isobit.util.BeanUtils;
import org.isobit.util.Request;
import org.isobit.util.Result;
import org.isobit.util.SimpleException;
import org.isobit.util.XMap;
import org.isobit.util.XUtil;

public abstract class Proxy {
  public enum Operation {
    DEFAULT, SINGLE, FIRST;
  }
  
  public Object executeQuery(String query, Object... params) {
    Operation de = Operation.DEFAULT;
    if (params.length > 0) {
      for (int i = (params[0].getClass() == Operation.class) ? 0 : 1; i < params.length; i++)
        query = query.replaceAll(XUtil.toString(params[i++]), XUtil.toString(params[i])); 
      de = (params[0].getClass() == Operation.class) ? (Operation)params[0] : de;
    } 
    List result = execute(query);
    return result;
  }
  
  public static String getQueryName(Object o) {
    Class c = (o instanceof Class) ? (Class)o : o.getClass();
    if (c.isAnonymousClass())
      c = c.getSuperclass(); 
    String q = c.getSimpleName();
    if (q.startsWith("d"))
      q = "q" + q.substring(1); 
    return c.getPackage().getName() + "." + q;
  }
  
  protected static XMap<String, Proxy> services = new XMap(new Object[0]);
  
  private static Proxy ERROR = new Proxy() {
      public List retrieve(Request request, Q q) {
        return null;
      }
      
      public List update(HttpServletRequest request, Q q, List data) {
        return null;
      }
      
      public Request build(HttpServletRequest params, Q q) {
        return null;
      }
      
      public boolean isConnected() {
        return false;
      }
      
      public Request execute(Request method, Object... args) {
        return null;
      }
    };
  
  private String _name;
  
  public static HashMap getServices() {
    return (HashMap)services;
  }
  
  public String getName() {
    return this._name;
  }
  
  public static Proxy registerService(String newAlias, Proxy proxy) {
    proxy.setName(newAlias);
    services.put(newAlias, proxy);
    return proxy;
  }
  
  public static Proxy getService(String name) {
    return getService(name, true);
  }
  
  public static Proxy getService(String name, boolean connect) {
    Proxy proxy = (Proxy)services.get(name);
    if (proxy == ERROR)
      return null; 
    if (proxy == null && connect) {
      if (X.CONTEXT_PATH != null)
        //return registerService(name, new JDBCProxy()); 
      try {
        proxy = getService(name, (JsonObject)X.getJSON("cnx/" + name + ".json", new Object[0]));
      } catch (Exception ee) {
        services.put(name, ERROR);
        throw (ee instanceof RuntimeException) ? (RuntimeException)ee : new RuntimeException(ee);
      } 
    } 
    return proxy;
  }
  
  public static Proxy getService(String name, JsonObject server) throws Exception {
    XMap baseJson = null;
    XMap datasource = new XMap((JsonElement)server);
    System.out.println("" + datasource);
    Object o = datasource.get("class");
    if (o == null)
      throw new SimpleException("Clase de proxy no definido."); 
    if (o instanceof String && !o.toString().contains("."))
      o = Proxy.class.getPackage().getName() + "." + o + "Proxy"; 
    if (datasource.containsKey("base")) {
      baseJson = new XMap(X.getJSON("cnx/" + datasource.get("base") + ".json", false));
      if (baseJson == null)
        throw new RuntimeException("Configuracion de proxy base '" + datasource.get("base") + "' no existe parar '" + name + "'"); 
      X.log("Usara como base " + baseJson);
    } 
    if (baseJson != null)
      datasource = baseJson.add((HashMap)datasource); 
    Object classProxy = datasource.get("class");
    if (classProxy == null && baseJson != null)
      classProxy = baseJson.get("class"); 
    if (classProxy == null)
      throw new RuntimeException("El proxy '" + name + "' no se pudo resolver su propiedad class"); 
    if (!classProxy.toString().contains("."))
      classProxy = Proxy.class.getPackage().getName() + "." + classProxy + "Proxy"; 
    Proxy service = (Proxy)Class.forName(classProxy.toString()).newInstance();
    if (datasource.get("promp") != null)
      X.login.connect(service, (Map)datasource); 
    X.log("Connecting with " + name + " using " + datasource);
    if (!service.isConnected()) {
      if (service.connect((Map)datasource, 
          datasource.containsKey("user") ? datasource.get("user").toString() : null, 
          datasource.containsKey("pass") ? datasource.get("pass").toString().toCharArray() : null)) {
        registerService(name, service);
        X.log("Connection successfull!");
        return service;
      } 
      return service;
    } 
    registerService(name, service);
    X.log("Connection successfull!");
    return service;
  }
  
  public void setName(String name) {
    this._name = name;
  }
  
  public abstract Object retrieve(Request paramRequest, Q paramQ);
  
  public List getResultList(Object query, Object... params) {
    return (List)retrieve(new Request(new Object[] { new XMap(new Object[] { "#transObject", getName() } ) } )
    , (Q)query);
  }
  
  public abstract Object update(HttpServletRequest paramHttpServletRequest, Q paramQ, List paramList);
  
  public abstract Object build(HttpServletRequest paramHttpServletRequest, Q paramQ);
  
  public boolean connect(Map params, String username, char[] password) throws Exception {
    return false;
  }
  
  public abstract boolean isConnected();
  
  public ResultSet getResultSet(String nQuery) throws Exception {
    return null;
  }
  
  public Connection getConnection(String name) {
    return null;
  }
  
  public Connection getConnection() {
    return null;
  }
  
  public boolean disconnect() throws Exception {
    return true;
  }
  
  public PreparedStatement prepareStatement(String SQL) {
    return null;
  }
  
  public int update(String SQL) {
    return 0;
  }
  
  public boolean isOK() {
    throw new UnsupportedOperationException("Not yet implemented");
  }
  
  public abstract Object execute(Request paramRequest, Object... paramVarArgs);
  
  public List execute(String cmd) {
    return null;
  }
  
  public Object[] getTables(String catalog, String string, String string0, String[] tableType) {
    throw new UnsupportedOperationException("Not yet implemented");
  }
  
  public static Proxy getInstance() {
    return getService("system");
  }
  
  public Object lookup(Object obj) {
    try {
      if (obj instanceof Class) {
        Class cls1 = (Class)obj;
        String cls2 = cls1.getCanonicalName().replace("Remote", "").replace("Local", "");
        obj = Class.forName(cls2).newInstance();
        obj = BeanUtils.initialize(obj);
        return java.lang.reflect.Proxy.newProxyInstance(obj.getClass().getClassLoader(), new Class[] { cls1 }, new ProxyHandler(obj));
      } 
      Class<?> iCls = Class.forName(obj.getClass().getCanonicalName() + "Remote");
      return java.lang.reflect.Proxy.newProxyInstance(obj.getClass().getClassLoader(), new Class[] { iCls }, new ProxyHandler(obj));
    } catch (ClassNotFoundException|InstantiationException|IllegalAccessException|IllegalArgumentException e) {
      throw new RuntimeException(e);
    } 
  }
  
  public static HashMap m = new HashMap<>();
  
  public class ProxyHandler implements InvocationHandler {
    private Object obj;
    
    public ProxyHandler(Object obj) {
      this.obj = obj;
    }
    
    public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
      Object oo;
      Proxy p = Proxy.getService("system");
      if (p instanceof JDBCProxy) {
        X.log("aqui debe inicar la transaccion jpa");
        if (args.length > 0 && args[0] instanceof Request)
          ((Request)args[0]).setAttribute("clientType", Integer.valueOf(1)); 
        Object result = m.invoke(this.obj, args);
        if (args.length > 0 && args[0] instanceof Request) {
          Object extra = ((Request)args[0]).getAttribute("clientType");
          if (extra instanceof Map);
        } 
        if (result instanceof Exception)
          X.alert(result); 
        oo = result;
        X.log("aqui debe terminar la transaccion");
      } else {
        Object o = p.execute(new Request());//new Object[] { this.obj, m }), args);
        if (o instanceof Result) {
          Result r = (Result)o;
          if (r.isOK()) {
            oo = r.get(0);
          } else {
            throw r.getException();
          } 
        } else {
          if (o instanceof Exception)
            throw (Exception)o; 
          oo = o;
        } 
      } 
      return oo;
    }
  }
}
