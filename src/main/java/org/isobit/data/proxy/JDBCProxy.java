package org.isobit.data.proxy;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.LockModeType;
import jakarta.persistence.Query;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.metamodel.Metamodel;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import org.isobit.app.X;
import org.isobit.data.JPA;
import org.isobit.data.Q;
import org.isobit.util.Request;
import org.isobit.util.SimpleException;
import org.isobit.util.Tag;
import org.isobit.util.XDate;
import org.isobit.util.XFile;
import org.isobit.util.XMap;
import org.isobit.util.XUtil;

public class JDBCProxy extends Proxy implements EntityManager {
  public static void println(ResultSet rs, PrintStream ps) throws SQLException {
    ResultSetMetaData md = rs.getMetaData();
    int cc = md.getColumnCount();
    int i;
    for (i = 1; i <= cc; i++)
      ps.print("\t" + md.getColumnName(i)); 
    while (rs.next()) {
      System.out.println();
      for (i = 1; i <= cc; i++)
        ps.print("\t" + rs.getObject(i)); 
    } 
  }
  
  public ResultSet getResultSet(String query, Connection cnx, int n) throws SQLException {
    return cnx.createStatement().executeQuery(sql(query, n));
  }
  
  public static String DS_NAME = "java:/comp/env/siigaa";
  
  private static Dialect[] dialectList = new Dialect[] { new Dialect("oledb") {
        public Object prepare(Object q, int option, int start, int limit) {
          switch (option) {
            case 112:
              if (q instanceof String[]) {
                String[] y = (String[])q;
                if (y.length == 1)
                  return (start > -1) ? y[0] : y[0]; 
                if (start > -1)
                  return "SELECT " + y[1] + " FROM " + y[0] + ((y[2] != null) ? (" " + y[2]) : "") + ((y[3] != null) ? (" ORDER BY " + y[3]) : "") + " LIMIT " + start + "," + ((limit <= 0) ? 20 : limit); 
                return "SELECT " + y[1] + " FROM " + y[0] + ((y[2] != null) ? (" " + y[2]) : "") + ((y[3] != null) ? (" ORDER BY " + y[3]) : "");
              } 
              if (start > 0)
                return q.toString().replaceFirst("SELECT", "SELECT SQL_CALC_FOUND_ROWS ") + " limit " + start + "," + ((limit <= 0) ? 20 : limit); 
              break;
          } 
          return q;
        }
      }, new Dialect("mysql") {
        public Object prepare(Object q, int option) {
          String x;
          switch (option) {
            case 111:
              return "SELECT FOUND_ROWS() as total";
            case 10:
              if (q instanceof String[]) {
                String[] y = (String[])q;
                for (int i = 0; i < y.length; i++) {
                  if (y[i] != null)
                    y[i] = y[i]
                      .replace("list(", "group_concat(")
                      .replace("charindex(", "locate(")
                      .replace("string(", "concat(")
                      .replace("isnull(", "ifnull("); 
                } 
                return y;
              } 
              x = q.toString();
              return x
                .replace("list(", "group_concat(")
                .replace("charindex(", "locate(")
                .replace("string(", "concat(")
                .replace("isnull(", "ifnull(");
            case 11:
              JDBCProxy.BOOL_TO_INT = false;
            case 9:
              return q.toString().replace("dbo.", "");
          } 
          return (q != null) ? q.toString() : q;
        }
      }, new Dialect("jconnect") {
        public Object prepare(Object q, int option, int start, int limit) {
          start++;
          if (q instanceof String[]) {
            String[] y = (String[])q;
            if (y.length == 1) {
              String str = y[0];
              return (limit > -1) ? (str.startsWith("SELECT DISTINCT") ? 
                str.replaceFirst("DISTINCT", "DISTINCT TOP " + limit + " START AT " + start + " ") : 
                str.replaceFirst("SELECT", "SELECT TOP " + limit + " START AT " + start + " ")) : str;
            } 
            return "SELECT " + ((start > -1) ? (y[1].startsWith("DISTINCT") ? 
              y[1].replaceFirst("DISTINCT", "DISTINCT TOP " + limit + " START AT " + start + " ") : (
              "TOP " + limit + " START AT " + start + " " + y[1])) : "") + " FROM " + y[0] + " " + ((y[2] != null) ? y[2] : "") + ((y[3] != null) ? (" ORDER BY " + y[3]) : "");
          } 
          String x = q.toString();
          return (limit > -1) ? (x.startsWith("SELECT DISTINCT") ? 
            x.replaceFirst("DISTINCT", "DISTINCT TOP " + limit + " START AT " + start + " ") : 
            x.replaceFirst("SELECT", "SELECT TOP " + limit + " START AT " + start + " ")) : x;
        }
        
        public Object prepare(Object q, int option) {
          String x = null;
          switch (option) {
            case 10:
              if (q instanceof String[]) {
                String[] y = (String[])q;
                for (int i = 0; i < y.length; i++) {
                  if (y[i] != null)
                    y[i] = y[i]
                      .replace("true", "1")
                      .replace("false", "0"); 
                } 
                return y;
              } 
              x = q.toString();
              if (x.startsWith("CALL")) {
                x = x.replaceFirst("CALL", "EXEC");
                x = x.replaceFirst("\\(", " ");
                x = x.substring(0, x.lastIndexOf(")"));
              } 
              return x;
            case 111:
              if (q instanceof String[]) {
                String[] y = (String[])q;
                if (y.length == 1)
                  return "SELECT COUNT(1) FROM (" + y[0] + ") x"; 
                return "SELECT count(1) FROM " + y[0] + ((y[2] != null) ? y[2] : "");
              } 
              return "SELECT COUNT(1) FROM (" + ((q instanceof String[]) ? ((String[])q)[0] : q) + ") x";
            case 9:
              return q.toString().replace("dba.ren_", "").replace("ren_", "");
          } 
          return x;
        }
      }, new Dialect("oracle"), new Dialect("postgresql") {
        public Object prepare(Object q, int option, int start, int limit) {
          if (q instanceof String[]) {
            String[] y = (String[])q;
            if (y.length == 1) {
              String str = y[0];
              return (limit > -1) ? (str + " LIMIT " + limit + ((start > -1) ? (" OFFSET " + start) : "")) : str;
            } 
            return "SELECT " + y[1] + " FROM " + y[0] + " " + (
              (y[2] != null) ? y[2] : "") + (
              (y[3] != null) ? (" ORDER BY " + y[3]) : "") + (
              (limit > -1) ? (" LIMIT " + limit + ((start > -1) ? (" OFFSET " + start) : "")) : "");
          } 
          String x = q.toString();
          if (limit > -1)
            x = x + " LIMIT " + limit + ((start > -1) ? (" OFFSET " + start) : ""); 
          return x;
        }
        
        public Object prepare(Object q, int option) {
          setGeneratedId("select LASTVAL()");
          String x = null;
          switch (option) {
            case 10:
              if (q instanceof String[]) {
                String[] y = (String[])q;
                for (int i = 0; i < y.length; i++) {
                  if (y[i] != null)
                    y[i] = y[i]
                      .replace("list(", "group_concat(")
                      .replace("charindex(", "locate(")
                      .replace("string(", "concat(")
                      .replace("ifnull(", "coalesce("); 
                } 
                return y;
              } 
              x = q.toString();
              x = x.replace("list(", "group_concat(").replace("charindex(", "locate(").replace("string(", "concat(").replace("ifnull(", "coalesce(");
              if (x.startsWith("CALL")) {
                x = x.replaceFirst("CALL", "EXEC");
                x = x.replaceFirst("\\(", " ");
                x = x.substring(0, x.lastIndexOf(")"));
              } 
              return x;
            case 111:
              if (q instanceof String[]) {
                String[] y = (String[])q;
                if (y.length == 1)
                  return "SELECT COUNT(1) FROM (" + y[0] + ") x"; 
                return "SELECT count(1) FROM " + y[0] + ((y[2] != null) ? y[2] : "");
              } 
              return "SELECT COUNT(1) FROM (" + ((q instanceof String[]) ? ((String[])q)[0] : q) + ") x";
            case 9:
              return q.toString().replace("dba.ren_", "").replace("ren_", "");
          } 
          return x;
        }
      } };
  
  public static interface Renderer {
    Object render(ResultSet param1ResultSet, int param1Int) throws SQLException;
  }
  
  public String sql(String q, int n) {
    return (String)this.dialog.prepare(q.replaceAll("[{]", prefix).replaceAll("}", ""), 112, 0, n);
  }
  
  public String sql(String q) {
    if (this.dialog == null)
      try {
        for (Dialect d : dialectList) {
          if (JPA.getInstance().getConnection()
            .getMetaData().getDriverName()
            .toLowerCase().contains(d.getName())) {
            this.dialog = d;
            this.dialog.prepare("", 11);
            break;
          } 
        } 
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }  
    return (String)this.dialog.prepare(q.replaceAll("[{]", prefix).replaceAll("}", ""), 10);
  }
  
  public ResultSet getResultSet(Q model, HttpServletRequest request) throws SQLException {
    String[] query = buildQuery(model, request);
    return JPA.getInstance().getConnection().createStatement().executeQuery(query[0]);
  }
  
  private static String prefix = "dru_";
  
  private static boolean BOOL_TO_INT = true;
  
  public Dialect dialog;
  
  private Connection cnx;
  
  private List tableList;
  
  public static Map toMap(ResultSet rs) throws SQLException {
    XMap map = new XMap(new Object[0]);
    ResultSetMetaData md = rs.getMetaData();
    int c = md.getColumnCount();
    for (int i = 1; i <= c; i++)
      map.put(md.getColumnName(i), rs.getObject(i)); 
    return (Map)map;
  }
  
  public Number getGeneratedId(Statement stmt) throws SQLException {
    ResultSet rs = stmt.executeQuery(this.dialog.getGeneratedId());
    rs.next();
    return rs.getBigDecimal(1);
  }
  
  public Number getGeneratedId(EntityManager em) throws SQLException {
    return (Number)em.createNativeQuery(this.dialog.getGeneratedId()).getSingleResult();
  }
  
  public ResultSet getResultSet(String nQuery) throws Exception {
    Statement stmt = this.cnx.createStatement();
    return stmt.executeQuery(nQuery);
  }
  
  public JDBCProxy() {
    this(null);
  }
  
  public JDBCProxy(Connection connection) {
    this.tableList = new ArrayList();
    this.cnx = connection;
  }
  
  public Connection getConnection() {
    return this.cnx;
  }
  
  public Connection getCnx(ServletRequest request) {
    if (this.dialog == null)
      try {
        for (Dialect d : dialectList) {
          if (this.cnx.getMetaData().getDriverName().toLowerCase().contains(d.getName())) {
            this.dialog = d;
            this.dialog.prepare("", 11);
            break;
          } 
        } 
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }  
    return this.cnx;
  }
  
  public boolean connect(Map map, String user, char[] password) throws Exception {
    if (map.get("driver") == null)
      throw new SimpleException("JDBC necesita definir Driver"); 
    System.out.println("map.get(\"driver\")=" + map.get("driver"));
    Driver d = (Driver)Class.forName((String)map.get("driver")).newInstance();
    String url = (String)map.get("url");
    Properties prop = new Properties();
    for (Object key : map.keySet()) {
      if (key.toString().equalsIgnoreCase("user"))
        prop.put("username", map.get(key)); 
      if (key.toString().equalsIgnoreCase("pass")) {
        prop.put("password", map.get(key));
      } else if (key.toString().equalsIgnoreCase("charset")) {
        prop.put("charSet", map.get(key));
      } 
      prop.put(key, map.get(key));
    } 
    if (map.get("integrated") != null) {
      this.cnx = DriverManager.getConnection(url, prop);
    } else {
      String u = XUtil.isEmpty("_USER") ? (String)map.get("username") : user;
      String p = (password != null) ? String.copyValueOf(password) : (String)map.get("password");
      System.out.println("u=" + u + ";p=" + p);
      Properties pr = new Properties();
      if (u != null)
        pr.setProperty("user", u); 
      if (p != null)
        pr.setProperty("password", p); 
      this.cnx = d.connect(url, pr);
    } 
    return true;
  }
  
  public Object execute(Request params, Object... args) {
    Method m = (Method)params.get(1);
    Object target = params.get(0);
    params.clear();
    try {
      params.add(m.invoke(target, args));
      params.setOK(true);
      Object extra = params.getAttribute("clientType");
      X.log("extra=" + extra);
      if (extra instanceof Map);
    } catch (Exception ex) {
      return Request.getResult(ex);
    } 
    return params;
  }
  
  public String toString() {
    return getName();
  }
  /* 
  public Object update(HttpServletRequest request, Q q, List<Object[]> data) {
    try {
      if (this.dialog == null)
        getCnx((ServletRequest)request); 
      if (q.getMetadata() == null) {
        request.setAttribute("#meta", Boolean.valueOf(true));
        buildQuery(q, request);
      } 
      System.out.println("q.getTables(" + X.gson.toJson(q.getTables()));
      String table = (String)this.dialog.prepare(q.getTables()[0].toString(), 9);
      System.out.println("table=" + table);
      int ti = 1;
      for (int i = 0; i < (q.getTables()).length; i++) {
        if (table.equals(q.getTables()[i])) {
          ti = i + 1;
          break;
        } 
      } 
      List<Object[]> fields = q.getMetadata();
      Map[] columns = new Map[fields.size()];
      for (int j = 0; j < columns.length; j++) {
        System.out.println("fields.get(i)[0]=" + ((Object[])fields.get(j))[0] + ((Object[])fields.get(j))[1]);
        columns[j] = (Map)((Object[])fields.get(j))[1];
      } 
      ArrayList<Object[]> actions = new ArrayList();
      StringBuilder builder = new StringBuilder();
      for (int k = 0; k < data.size(); k++) {
        Object[][] d;
        int n;
        List<Object> v;
        int i1;
        List<Object[][]> eliminar;
        Object[] record = data.get(k);
        System.out.println("rcord=" + X.gson.toJson(record));
        if (!q.update(request, data, record, 0, k));
        builder.setLength(0);
        switch (((Short)record[0]).shortValue()) {
          case 4:
            d = (Object[][])record[2];
            builder.append("INSERT INTO ").append(table).append("(");
            for (n = 0; n < d.length; n++) {
              if (((Integer)columns[((Integer)d[n][1]).intValue()].get("TI")).intValue() == ti)
                builder.append(columns[((Integer)d[n][1]).intValue()].get("D")).append(","); 
            } 
            builder.deleteCharAt(builder.length() - 1).append(") VALUES (");
            v = new ArrayList();
            for (i1 = 0; i1 < d.length; i1++) {
              if (((Integer)columns[((Integer)d[i1][1]).intValue()].get("TI")).intValue() == ti)
                if (d[i1][0] != null) {
                  v.add(d[i1][0]);
                  builder.append("?,");
                } else {
                  builder.append("null,");
                }  
            } 
            builder.deleteCharAt(builder.length() - 1).append(")");
            actions.add(new Object[] { Short.valueOf((short)4), builder.toString(), record[1], d, v });
            break;
          case 2:
            d = (Object[][])record[2];
            builder.append(" UPDATE ").append(table).append(" SET ");
            for (i1 = 0; i1 < d.length; i1++) {
              if (((Integer)columns[((Integer)d[i1][1]).intValue()].get("TI")).intValue() == ti) {
                Object o = d[i1][0];
                if (o instanceof Date) {
                  o = "'" + XDate.getSQLDate((Date)o) + "'";
                } else if (o instanceof Boolean) {
                  if (BOOL_TO_INT)
                    o = Integer.valueOf(((Boolean)o).booleanValue() ? 1 : 0); 
                } else if (!(o instanceof Number) && o != null) {
                  o = "'" + o + "'";
                } 
                builder.append(columns[((Integer)d[i1][1]).intValue()].get("D")).append("=").append(o).append(",");
              } 
            } 
            builder.deleteCharAt(builder.length() - 1);
            d = (Object[][])record[3];
            System.out.println("ANTES de UPDATE " + d.length);
            if (d.length > 0) {
              builder.append(" WHERE ");
              for (i1 = 0; i1 < d.length; i1++) {
                System.out.println("columns[(Integer) d[j][1]]=" + columns[((Integer)d[i1][1]).intValue()]);
                if (((Integer)columns[((Integer)d[i1][1]).intValue()].get("TI")).intValue() == ti) {
                  Object o = d[i1][0];
                  if (o instanceof Tag)
                    o = ((Tag)o).getId(); 
                  if (o instanceof Boolean) {
                    if (BOOL_TO_INT)
                      o = Integer.valueOf(((Boolean)o).booleanValue() ? 1 : 0); 
                  } else if (!(o instanceof Number) && o != null) {
                    o = "'" + o + "'";
                  } 
                  builder.append(columns[((Integer)d[i1][1]).intValue()].get("D")).append("=").append(o).append(" AND ");
                } 
              } 
              builder.delete(builder.length() - 5, builder.length() - 1);
              actions.add(new Object[] { Short.valueOf((short)2), builder.toString(), record[1] });
            } 
            break;
          case -1:
            eliminar = (List)record[1];
            builder.append("DELETE FROM ").append(table).append(" WHERE ");
            for (k = 0; k < eliminar.size(); k++) {
              d = eliminar.get(k);
              builder.append("(");
              for (int i2 = 0; i2 < d.length; i2++) {
                if (((Integer)columns[((Integer)d[i2][1]).intValue()].get("TI")).intValue() == ti) {
                  Object o = d[i2][0];
                  if (o instanceof Boolean) {
                    if (BOOL_TO_INT)
                      o = Integer.valueOf(((Boolean)o).booleanValue() ? 1 : 0); 
                  } else if (!(o instanceof Number) && o != null) {
                    o = "'" + o + "'";
                  } 
                  builder.append((i2 > 0) ? " AND " : "").append(columns[((Integer)d[i2][1]).intValue()].get("D")).append("=").append(o);
                } 
              } 
              builder.append(") OR ");
            } 
            builder.delete(builder.length() - 4, builder.length() - 1);
            eliminar.clear();
            actions.add(new Object[] { Short.valueOf((short)-1), builder.toString() });
            break;
        } 
      } 
      Connection cnx = getCnx((ServletRequest)request);
      Statement statement = cnx.createStatement();
      List<Object[]> nd = new ArrayList();
      for (int m = 0; m < actions.size(); m++) {
        Object[][] d;
        PreparedStatement p;
        List v;
        int n;
        Object[] record = actions.get(m);
        q.update(request, data, record, 3, m);
        String sql = record[1].toString();
        switch (((Short)record[0]).shortValue()) {
          case 4:
            p = cnx.prepareStatement(sql);
            d = (Object[][])record[3];
            v = (List)record[4];
            System.out.println("Ejecutar sql=" + sql + "\nparameters=" + X.gson.toJson(v));
            for (n = 0; n < v.size(); n++) {
              Object[] me = fields.get(n);
              Object oo = v.get(n);
              if (oo instanceof Date) {
                if (!(oo instanceof Date))
                  oo = new Date(((Date)oo).getTime()); 
                p.setDate(n + 1, (Date)oo);
              } else if (oo != null) {
                System.out.println(X.gson.toJson(fields.get(n)));
                if ("string".equals(((Map<?, ?>)me[1]).get("type"))) {
                  p.setString(n + 1, oo.toString());
                } else {
                  System.out.println(n + " '" + oo + "'");
                  p.setObject(n + 1, oo);
                } 
              } 
            } 
            record[1] = Integer.valueOf(p.executeUpdate());
            try {
              record[1] = getGeneratedId(statement);
            } catch (SQLException sQLException) {}
            record[0] = Short.valueOf((short)4);
            break;
          case 2:
            System.out.println("Ejecutar " + record[1].toString());
            record[1] = Integer.valueOf(statement.executeUpdate(record[1].toString()));
            System.out.println(record[1] + " modificados");
            record[0] = Short.valueOf((short)2);
            break;
          case -1:
            record[1] = Integer.valueOf(statement.executeUpdate(record[1].toString()));
            System.out.println(record[1] + " eliminados");
            record[0] = Short.valueOf((short)-1);
            break;
        } 
        q.update(request, data, record, 4, m);
        nd.add(record);
      } 
      statement.close();
      return nd;
    } catch (Exception ex) {
      return ex;
    } 
  }
  */
  public Request retrieve(Request request, Q query) {
    List data = getResultList((HttpServletRequest)request, query);
    request.clear();
    request.addAll(data);
    request.setOK(true);
    return request;
  }
  
  public boolean isConnected() {
    try {
      return (this.cnx != null && !this.cnx.isClosed());
    } catch (SQLException ex) {
      X.log(ex.toString());
      return false;
    } 
  }
  
  public List getResultList(HttpServletRequest request, Q model) {
    Proxy p = (Proxy)request.getAttribute("proxy");
    String[] query = buildQuery(model, request);
    if (request.getAttribute("#meta") != null) {
      request.removeAttribute("#meta");
      return model.getMetadata();
    } 
    Connection cnx = (p != null) ? p.getConnection() : JPA.getInstance().getConnection();
    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = cnx.createStatement();
      List data = model.afterRetrieve(request, getResultList(rs = stmt.executeQuery(query[0]), true, (model instanceof Renderer) ? (Renderer)model : null));
      if (query[1] != null) {
        rs = stmt.executeQuery(query[1]);
        request.setAttribute("totalCount", Integer.valueOf(rs.next() ? rs.getInt(1) : 0));
        rs.close();
      } else {
        request.setAttribute("totalCount", Integer.valueOf(data.size()));
      } 
      return data;
    } catch (Exception e) {
      try {
        if (rs != null && !rs.isClosed())
          rs.close(); 
      } catch (SQLException ex) {
        ex.printStackTrace();
      } 
      try {
        if (stmt != null && !stmt.isClosed())
          stmt.close(); 
      } catch (SQLException ex) {
        ex.printStackTrace();
      } 
      throw new RuntimeException(e);
    } 
  }
  
  public Object build(ServletRequest request, Q model, Object query) {
    Connection cnx = getCnx(request);
    query = this.dialog.prepare(query, 10);
    model.setQuery(this.dialog.prepare(model.getQuery(), 10));
    try {
      if (query instanceof String[]) {
        String[] q = (String[])query;
        if (q.length > 1 && q[2] != null)
          q[2] = " WHERE " + q[2]; 
      } 
      String nq = (String)this.dialog.prepare(query, 112, 0, 1);
      Object[] keyFilter = model.getFilter();
      if (keyFilter != null)
        nq = nq.replace(":" + keyFilter[0], ""); 
      List<Object[]> fields = null;
      String[] tables = null;
      Class<?> c = model.getClass();
      while (c != null && c.isAnonymousClass())
        c = c.getSuperclass(); 
      File file = XFile.getFile(new File("model/" + c.getCanonicalName() + ".mds"), false);
      if (file != null)
        try {
          FileInputStream fis = new FileInputStream(file);
          ObjectInputStream ois = new ObjectInputStream(fis);
          Object[] m = (Object[])ois.readObject();
          fields = (List)m[0];
          tables = (String[])m[1];
          ois.close();
        } catch (Exception exception) {} 
      if (fields == null || fields.isEmpty()) {
        fields = new ArrayList();
        List<String> tList = new ArrayList();
        ResultSet rs = cnx.createStatement().executeQuery(nq);
        ResultSetMetaData mD = rs.getMetaData();
        XMap<String, XMap> tableMap = new XMap(new Object[0]);
        DatabaseMetaData db = cnx.getMetaData();
        for (int i = 1, j = mD.getColumnCount(); i <= j; i++) {
          Class<?> clazz = Class.forName(mD.getColumnClassName(i));
          HashMap<Object, Object> map = new HashMap<>();
          if (Time.class.isAssignableFrom(clazz)) {
            map.put("type", "time");
          } else if (Timestamp.class.isAssignableFrom(clazz)) {
            map.put("type", "datetime");
          } else if (Date.class.isAssignableFrom(clazz)) {
            map.put("type", "date");
          } else if (BigDecimal.class.isAssignableFrom(clazz)) {
            map.put("type", "double");
          } else if (Float.class.isAssignableFrom(clazz)) {
            map.put("type", "float");
          } else if (Double.class.isAssignableFrom(clazz)) {
            map.put("type", "double");
          } else if (Number.class.isAssignableFrom(clazz)) {
            map.put("type", "int");
          } else if (Boolean.class.isAssignableFrom(clazz)) {
            map.put("type", "boolean");
            map.put("S", Integer.valueOf(mD.getColumnDisplaySize(i)));
          } else {
            map.put("type", "string");
            map.put("S", Integer.valueOf(mD.getColumnDisplaySize(i)));
          } 
          if (mD.isAutoIncrement(i))
            map.put("a", Integer.valueOf(i - 1)); 
          map.put("null", Boolean.valueOf((mD.isNullable(i) > 0)));
          if (!XUtil.isEmpty(mD.getTableName(i))) {
            int nn = tList.indexOf(mD.getTableName(i)) + 1;
            if (nn < 1) {
              tList.add(mD.getTableName(i));
              nn = tList.size();
            } 
            map.put("TI", Integer.valueOf(nn));
            map.put("D", mD.getColumnName(i));
          } else {
            map.put("TI", Integer.valueOf(0));
            map.put("D", mD.getColumnName(i));
          } 
          fields.add(new Object[] { mD.getColumnLabel(i), map });
          if (!tableMap.containsKey(mD.getTableName(i)))
            tableMap.put(mD.getTableName(i), new XMap(new Object[0])); 
          ((XMap)tableMap.get(mD.getTableName(i))).put(mD.getColumnName(i), map);
        } 
        tables = tList.<String>toArray(new String[tList.size()]);
        ArrayList pk = new ArrayList();
        boolean ok = true;
        for (Object t : tableMap.keySet()) {
          try {
            ResultSet rst = db.getColumns(null, null, t.toString(), "*");
            while (rst.next()) {
              Map<String, String> k = (Map)((XMap)tableMap.get(t.toString())).get(rst.getString(4));
              if (k != null)
                k.put("default", rst.getString(13)); 
            } 
            if (ok) {
              rst = db.getPrimaryKeys(null, null, t.toString());
              while (rst.next()) {
                Map<String, Boolean> k = (Map)((XMap)tableMap.get(t.toString())).get(rst.getString(4));
                if (k != null) {
                  k.put("K", Boolean.valueOf(true));
                  ok = false;
                } 
              } 
            } 
          } catch (Exception e) {
            e.printStackTrace();
          } 
        } 
        try {
          X.saveTo(XFile.getFile(new File("model/" + c.getCanonicalName() + ".mds")), new Object[] { fields, tables });
        } catch (Exception ee) {
          ee.printStackTrace();
        } 
      } 
      model.setTables((Object[])tables);
      model.setMetadata(fields);
    } catch (Exception e) {
      throw new RuntimeException(e);
    } 
    return query;
  }
  
  public String[] buildQuery(Q model, HttpServletRequest request) {
    Map filter = (Map)request.getAttribute("#filters");
    request.removeAttribute("#filters");
    model.beforeRetrieve(request, (filter != null) ? filter : XMap.EMPTY_MAP);
    Object oq = request.getAttribute("query");
    if (oq == null) {
      oq = model.getQuery();
      if (oq == null)
        model.setQuery(oq = "SELECT * FROM " + model.getModel()); 
    } 
    (new String[1])[0] = (String)oq;
    String[] q = (oq instanceof String[]) ? (String[])((String[])oq).clone() : new String[1];
    Object[][] pm = model.getParams();
    if (pm != null) {
      Object o = request.getAttribute("#params");
      request.removeAttribute("#params");
      if (o instanceof Object[]) {
        Object[] pp = (Object[])o;
        Map<Object, Object> mm = new HashMap<>();
        for (int jj = 0; jj < pp.length; jj++) {
          if (pm.length > jj)
            mm.put(pm[jj][0], pp[jj]); 
          mm.put("" + (jj + 1), pp[jj]);
        } 
        o = mm;
      } 
      Map params = (Map)o;
      int k = 1;
      int j = 0;
      Object meta = request.getAttribute("#meta");
      if (params != null || meta == Boolean.TRUE) {
        for (Object[] paramProperty : pm) {
          for (j = 0; j < q.length; j++) {
            String qa = q[j];
            if (qa != null)
              if (meta == Boolean.TRUE) {
                qa = qa.replace(":" + k++, "null");
                qa = qa.replace(":" + paramProperty[0], "null");
              } else {
                Object v = params.get(paramProperty[0]);
                Class<?> clazz = (Class)paramProperty[1];
                if (Number.class.isAssignableFrom(clazz)) {
                  if (!XUtil.isEmpty(v)) {
                    try {
                      v = clazz.getMethod("valueOf", new Class[] { String.class }).invoke(null, new Object[] { "" + v });
                    } catch (Exception e) {
                      throw new RuntimeException("Parametro " + paramProperty[0] + " no es un numero valido.");
                    } 
                  } else {
                    v = null;
                  } 
                } else {
                  if (Timestamp.class.isAssignableFrom(clazz)) {
                    if (v instanceof Date)
                      v = new Timestamp(((Date)v).getTime()); 
                  } else if (Time.class.isAssignableFrom(clazz)) {
                    if (v instanceof Date)
                      v = new Time(((Date)v).getTime()); 
                  } else if (Date.class.isAssignableFrom(clazz) && 
                    v instanceof Date) {
                    v = new Date(((Date)v).getTime());
                  } 
                  if (v != null)
                    v = "'" + v + "'"; 
                } 
                if (params.containsKey(paramProperty[0])) {
                  qa = qa.replace(":" + k++, "" + v);
                  qa = qa.replace(":" + paramProperty[0], "" + v);
                } else {
                  throw new RuntimeException("Parametro " + paramProperty[0] + " no definido");
                } 
              }  
            q[j] = qa;
          } 
        } 
      } else {
        throw new RuntimeException("No se han definido los parametros para " + model.getClass());
      } 
    } 
    q = (model.getMetadata() == null) ? (String[])build((ServletRequest)request, model, q) : (String[])this.dialog.prepare(q, 10);
    Object[] keyFilter = model.getFilter();
    int i = (q.length > 1) ? 2 : 0;
    if (filter != null) {
      String f = "";
      for (Object keyFilt : ((HashMap)filter).keySet()) {
        Object keyc = null;
        Object filterValue = null;
        Object type = null;
        if (keyFilt instanceof Tag) {
          Tag tag = (Tag)keyFilt;
          keyc = tag.getId();
          filterValue = tag.getData();
        } else {
          for (Object[] r : (List<Object[]>)model.getMetadata()) {
            if (keyFilt.equals(r[0])) {
              keyc = ((HashMap<?, ?>)r[1]).get("D");
              int ti = XUtil.intValue(((HashMap<?, ?>)r[1]).get("TI"));
              if (ti > 0)
                keyc = model.getTables()[ti - 1] + "." + keyc; 
              if (keyc == null)
                keyc = keyFilt; 
              filterValue = filter.get(keyFilt);
              type = ((HashMap<?, ?>)r[1]).get("type");
              break;
            } 
          } 
        } 
        if (keyc != null) {
          if (filterValue instanceof List) {
            f = f + keyc + " IN (" + XUtil.toString(filterValue) + ") AND ";
            continue;
          } 
          if (filterValue instanceof Object[]) {
            if (((Object[])filterValue)[0] != null)
              f = f + ((Object[])filterValue)[0] + " AND "; 
            continue;
          } 
          if ("string".equals(type)) {
            f = f + "  UPPER(" + keyc + ") LIKE '" + filterValue + "' AND ";
            continue;
          } 
          if (filterValue instanceof Date) {
            f = f + keyc + "='" + XDate.getSQLDate((Date)filterValue) + "' AND ";
            continue;
          } 
          if (filterValue != null) {
            f = f + keyc + "='" + filterValue + "' AND ";
            continue;
          } 
          f = f + keyc + " IS " + filterValue + " AND ";
        } 
      } 
      if (f.length() > 0) {
        f = f.substring(0, f.length() - 4);
        for (i = 0; i < 3; i++) {
          if (keyFilter != null && 
            q[i] != null && q[i].indexOf(":" + keyFilter[0]) > -1) {
            if (i == 2) {
              q[i] = " WHERE " + q[i].replace(":" + keyFilter[0], keyFilter[2] + " " + f);
              break;
            } 
            q[i] = q[i].replace(":" + keyFilter[0], keyFilter[2] + " " + f);
            break;
          } 
          if (q.length == 1) {
            q[i] = q[i] + " WHERE " + f;
            break;
          } 
          if (i == 2)
            q[i] = " WHERE " + f; 
        } 
      } else if (keyFilter != null) {
        for (i = 0; i < q.length; i++)
          q[i] = q[i].replace(":" + keyFilter[0], ""); 
      } 
    } else if (keyFilter != null) {
      if (q.length == 1) {
        q[0] = q[0].replace(":" + keyFilter[0], "");
      } else {
        for (i = 0; i < 3; i++) {
          if (q[i] != null && q[i].indexOf(":" + keyFilter[0]) > -1) {
            q[i] = " WHERE " + q[i].replace(":" + keyFilter[0], "");
          } else if (i == 2) {
            q[i] = " WHERE " + q[i];
          } 
          if (q.length == 1)
            break; 
        } 
      } 
    } 
    String total = null;
    String sort = (String)request.getAttribute("_SORT");
    if (q[0].length() == 1)
      if (!XUtil.isEmpty(sort)) {
        q[0] = q[0] + sort;
      } else if (!XUtil.isEmpty(sort)) {
        q[3] = q[3] + sort;
      }  
    if (request.getAttribute("#all") == null) {
      int start = XUtil.intValue(request.getParameter("start"));
      int limit = XUtil.intValue(request.getParameter("limit"));
      if (limit < 1)
        limit = 20; 
      total = (String)this.dialog.prepare(q[0], 111);
      q[0] = (String)this.dialog.prepare(q, 112, start, limit);
    } else {
      q[0] = (String)this.dialog.prepare(q, 112, -1, -1);
      request.removeAttribute("#all");
    } 
    return new String[] { q[0], total };
  }
  
  public Object build(HttpServletRequest request, Q model) {
    Object[][] paramsModel = model.getParams();
    if (paramsModel != null) {
      HashMap<Object, Object> params = (HashMap)request.getAttribute("#params");
      if (params == null)
        request.setAttribute("#params", params = new HashMap<>()); 
      for (Object[] paramProperty : paramsModel) {
        Object v = params.get(paramProperty[0]);
        if (v == null)
          if (paramProperty[1] == Integer.class) {
            params.put(paramProperty[0], Integer.valueOf(0));
          } else {
            params.put(paramProperty[0], null);
          }  
      } 
    } 
    request.setAttribute("proxy", this);
    List fields = getResultList(request, model);
    if (request instanceof Request) {
      Request r = (Request)request;
      r.clear();
      r.addAll(fields);
    } 
    return request;
  }
  
  public void persist(Object entity) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public <T> T merge(T entity) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public void remove(Object entity) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public <T> T find(Class<T> entityClass, Object primaryKey) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode, Map<String, Object> properties) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public <T> T getReference(Class<T> entityClass, Object primaryKey) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public void flush() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public void setFlushMode(FlushModeType flushMode) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public FlushModeType getFlushMode() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public void lock(Object entity, LockModeType lockMode) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public void lock(Object entity, LockModeType lockMode, Map<String, Object> properties) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public void refresh(Object entity) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public void refresh(Object entity, Map<String, Object> properties) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public void refresh(Object entity, LockModeType lockMode) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public void refresh(Object entity, LockModeType lockMode, Map<String, Object> properties) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public void clear() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public void detach(Object entity) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public boolean contains(Object entity) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public LockModeType getLockMode(Object entity) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public void setProperty(String propertyName, Object value) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public Map<String, Object> getProperties() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public Query createQuery(String qlString) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public Query createNamedQuery(String name) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public Query createNativeQuery(String sqlString) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public Query createNativeQuery(String sqlString, Class resultClass) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public Query createNativeQuery(String sqlString, String resultSetMapping) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public void joinTransaction() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public <T> T unwrap(Class<T> cls) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public Object getDelegate() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public void close() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public boolean isOpen() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public EntityTransaction getTransaction() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public EntityManagerFactory getEntityManagerFactory() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public CriteriaBuilder getCriteriaBuilder() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public Metamodel getMetamodel() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public Query createQuery(CriteriaUpdate cu) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public Query createQuery(CriteriaDelete cd) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public StoredProcedureQuery createNamedStoredProcedureQuery(String string) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public StoredProcedureQuery createStoredProcedureQuery(String string) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public StoredProcedureQuery createStoredProcedureQuery(String string, Class... types) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public StoredProcedureQuery createStoredProcedureQuery(String string, String... strings) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public boolean isJoinedToTransaction() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public <T> EntityGraph<T> createEntityGraph(Class<T> type) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public EntityGraph<?> createEntityGraph(String string) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public EntityGraph<?> getEntityGraph(String string) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> type) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public static ArrayList getResultList(ResultSet rs, boolean close) throws SQLException {
    return getResultList(rs, close, null);
  }
  
  public static ArrayList getResultList(ResultSet rs, boolean close, int pos, int limit) throws SQLException {
    return getResultList(rs, close, null, pos, limit);
  }
  
  public static ArrayList getResultList(ResultSet rs, boolean close, Renderer renderer) throws SQLException {
    return getResultList(rs, close, renderer, -1, -1);
  }
  
  public static ArrayList getResultList(ResultSet rs, boolean close, Renderer renderer, int pos, int limit) throws SQLException {
    int cc = rs.getMetaData().getColumnCount();
    ArrayList<Object[]> lista = new ArrayList();
    if (pos == -100) {
      Object[] row = new Object[cc];
      for (int i = 0; i < row.length; i++)
        row[i] = rs.getMetaData().getColumnName(i + 1); 
      lista.add(row);
    } 
    if (renderer != null) {
      while (rs.next()) {
        Object[] row = new Object[cc];
        for (int i = 0; i < row.length; i++)
          row[i] = renderer.render(rs, i + 1); 
        lista.add(row);
      } 
    } else if (limit > 0) {
      rs.absolute(pos);
      rs.setFetchSize(limit);
    } 
    while (rs.next()) {
      Object[] row = new Object[cc];
      for (int i = 0; i < row.length; i++)
        row[i] = rs.getObject(i + 1); 
      lista.add(row);
    } 
    if (close)
      rs.close(); 
    return lista;
  }
  
  public boolean disconnect() throws Exception {
    this.cnx.close();
    return true;
  }

@Override
public Object update(HttpServletRequest paramHttpServletRequest, Q paramQ, List paramList) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'update'");
}
}
