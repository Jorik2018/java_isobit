package org.isobit.data;


import com.google.gson.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.swing.ComboBoxModel;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import org.isobit.app.X;
import org.isobit.data.proxy.Proxy;
import org.isobit.data.table.AbstractDataColumn;
import org.isobit.data.table.ComputedDataColumn;
import org.isobit.data.table.DefaultDataColumn;
import org.isobit.util.Constants;
import org.isobit.util.Pooler;
import org.isobit.util.Request;
import org.isobit.util.SimpleException;
import org.isobit.util.Tag;
import org.isobit.util.XDate;
import org.isobit.util.XFile;
import org.isobit.util.XMap;
import org.isobit.util.XUtil;

public class Store extends AbstractTableModel implements Constants {
  private Object[] where;
  
  protected Q Q;
  
  protected Object[][] metaQuery;
  
  protected int autoincrement = -1;
  
  protected boolean listening = true;
  
  public static int STATE = 0;
  
  public static final short INVALID = -1;
  
  public static final int _NAME = 0;
  
  public static final int _NULL = 1;
  
  public static final int _CLASS = 2;
  
  public static final int _TYPE = 3;
  
  public static final int _EDITABLE = 4;
  
  public static final int _PK = 5;
  
  public static final int _DBNAME = 6;
  
  public static final int _SIZE = 7;
  
  public static final int _UPDATE = 8;
  
  public static final int _MASK = 9;
  
  public static final int _SUBMIT = 10;
  
  public static final int _RETRIEVED = 10001;
  
  public static final int _UPDATED = 10002;
  
  private static final int PROPERTY_COUNT = 11;
  
  public static int CHANGED;
  
  protected String name;
  
  protected ArrayList records = new ArrayList();
  
  protected ArrayList<Object[]> removedRows = new ArrayList();
  
  protected ArrayList<AbstractDataColumn> dataColumns = new ArrayList<>();
  
  public XMap defaultValue = new XMap(new Object[0]);
  
  protected transient boolean loaded;
  
  protected String query = "";
  
  private Writer writer;
  
  private Reader reader;
  
  protected Object transObjectName = "system";
  
  public String sort;
  
  private static int increment;
  
  private int id;
  
  public int getId() {
    return this.id;
  }
  
  public final void setId(int id) {
    this.id = id;
  }
  
  public Store(Q jq) {
    this();
    this.Q = jq;
  }
  
  public Store(Class<Q> cls) {
    this();
    try {
      this.Q = cls.newInstance();
    } catch (InstantiationException|IllegalAccessException e) {
      throw new RuntimeException(e);
    } 
  }
  
  public Object[] getWHERE() {
    return this.where;
  }
  
  public void setWHERE(Object[] filter) {
    this.where = filter;
  }
  
  protected static Proxy defaultProxy;
  
  protected Dummy dummy;
  
  private Class entityCls;
  
  private Method[] mread;
  
  private Method[] mwrite;
  
  public void retrieve(Object... args) {
    if (this.entityCls != null) {
      String query = "select b from " + this.entityCls.getSimpleName() + " b";
      ArrayList<Object> arrayList = new ArrayList();
      for (Object e : JPA.getInstance().getEntityManager().createQuery(query, this.entityCls).getResultList())
        arrayList.add(e); 
      JPA.getInstance().close();
      setRecords(arrayList);
      return;
    } 
    if (getMetaQuery() == null)
      build(args); 
    if (args != null)
      if (args.length > 0 && args[0] instanceof Store) {
        Store so = (Store)args[0];
        setDefault("#params", so.getDefault("#params"));
      } else if (args.length > 0 && args[0] instanceof Map) {
        setDefault("#params", args[0]);
      }  
    if (STATE == 0)
      return; 
    Request request = new Request();//new Object[0]);
    Object result = null;
    Proxy prx = getTransObject();
    if (prx == null)
      throw new SimpleException("Service " + this.transObjectName + " don't exists! XD"); 
    Object params = getDefault("#params");
    request.setAttribute("#all", Boolean.valueOf(true));
    request.setAttribute("#params", (params != null) ? params : args);
    request.setAttribute("#filters", getDefault("#filters"));
    request.setAttribute("_SORT", getDefault("_SORT"));
    request.setAttribute("cnx", prx.getConnection());
    if (args != null)
      for (int i = 0; i < args.length; i++) {
        if (args[i] instanceof Tag)
          args[i] = ((Tag)args[i]).getId(); 
      }  
    request.add(new XMap(new Object[] { "#metaParams", 
            getParams(), "#metaQuery", 
            getMetaQuery(), "#where", 
            getWHERE(), "#sort", 
            getDefault("#sort") }));
    request.setAttribute("proxy", prx);
    result = prx.retrieve(request, this.Q);
    setDefault("#response", result);
    List data = null;
    if (result instanceof List) {
      data = (List)result;
    } else if (result instanceof Map) {
      data = (List)((Map)result).get("data");
    } else if (result instanceof Exception) {
      fireTableDataChanged();
      System.gc();
      throw new RuntimeException((Exception)result);
    } 
    if (!(result instanceof Request) || ((Request)result).isOK()) {
      setRecords(data);
      fireTableChanged(new TableModelEvent(this, 0, getRowCount(), -1, 301));
      setLoaded(true);
      Class<?> c = getClass();
      while (c != null && c.isAnonymousClass())
        c = c.getSuperclass(); 
      if (getDefault("#data") != null)
        try {
          File file = XFile.getFile(new File("model/" + c.getCanonicalName() + ".mds"), false);
          if (file != null) {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            request = (Request)ois.readObject();
            request.setAttribute("#data", getRecords(false));
            ois.close();
            X.saveTo(XFile.getFile(file), request);
          } 
        } catch (Exception ee) {
          ee.printStackTrace();
        }  
      System.gc();
    } else {
      fireTableDataChanged();
      System.gc();
      throw new RuntimeException(((Request)result).getException());
    } 
  }
  
  public void build(Object... params) {
    Request request = new Request();//new Object[0]);
    request.setAttribute("#meta", Boolean.valueOf(true));
    List<Object[]> fields = null;
    try {
      if (this.entityCls != null) {
        fields = new ArrayList();
        Class ca = Class.forName("javax.persistence.Column");
        Class ia = Class.forName("javax.persistence.Id");
        List<Field> ff = new ArrayList<>();
        for (Field f : this.entityCls.getDeclaredFields()) {
          if (f.getAnnotation(ca) != null) {
            ff.add(f);
            fields.add(new Object[] { f.getName(), new XMap(new Object[] { "type", f
                      .getType(), "K", 
                      (f.getAnnotation(ia) != null) ? Boolean.valueOf(true) : null }), "S", f.getType().equals(String.class) ? Integer.valueOf(400) : null });
          } 
        } 
        this.mread = new Method[fields.size()];
        this.mwrite = new Method[fields.size()];
        for (int i = 0; i < this.mread.length; i++) {
          this.mread[i] = this.entityCls.getMethod("get" + XUtil.capitalize(((Field)ff.get(i)).getName(), 1), new Class[0]);
          this.mwrite[i] = this.entityCls.getMethod("set" + XUtil.capitalize(((Field)ff.get(i)).getName(), 1), new Class[] { ((Field)ff
                .get(i)).getType() });
        } 
      } else {
        Proxy prx = getTransObject();
        Object o = prx.build((HttpServletRequest)request, getQ());
        if (o instanceof Exception)
          throw (Exception)o; 
        fields = (List<Object[]>)o;
      } 
      this.metaQuery = new Object[fields.size()][11];
      this.dataColumns.clear();
      for (int c = 0; c < this.metaQuery.length; c++) {
        Object[] f = fields.get(c);
        this.metaQuery[c][0] = f[0];
        Map m = (Map)f[1];
        Object v = m.get("type");
        if ("int".equals(v)) {
          this.metaQuery[c][2] = Integer.class;
        } else if ("float".equals(v)) {
          this.metaQuery[c][2] = Float.class;
        } else if ("double".equals(v)) {
          this.metaQuery[c][2] = Double.class;
        } else if ("string".equals(v)) {
          this.metaQuery[c][2] = String.class;
        } else if ("date".equals(v)) {
          this.metaQuery[c][2] = Date.class;
        } else if ("datetime".equals(v)) {
          this.metaQuery[c][2] = Timestamp.class;
        } else if ("boolean".equals(v)) {
          this.metaQuery[c][2] = Boolean.class;
        } else if (v instanceof Class) {
          this.metaQuery[c][2] = v;
        } else if (v != null) {
          try {
            this.metaQuery[c][2] = Class.forName(v.toString());
          } catch (Exception exception) {}
        } 
        this.metaQuery[c][7] = m.get("S");
        this.metaQuery[c][1] = m.get("null");
        this.metaQuery[c][5] = m.get("K");
        this.metaQuery[c][6] = m.get("D");
        this.metaQuery[c][4] = Boolean.valueOf(true);
        if (m.containsKey("a") && this.autoincrement < 0) {
          this.autoincrement = XUtil.intValue(m.get("a"));
          this.metaQuery[c][1] = Boolean.valueOf(true);
          this.metaQuery[c][4] = Boolean.valueOf(false);
        } 
        DefaultDataColumn dc = new DefaultDataColumn(this.metaQuery[c][0]);
        dc.setEditable(((Boolean)this.metaQuery[c][4]).booleanValue());
        this.dataColumns.add(dc);
      } 
      setMetaQuery(this.metaQuery);
      fireTableStructureChanged();
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    } 
  }
  
  public Object[][] getMetaQuery() {
    return this.metaQuery;
  }
  
  public void setMetaQuery(Object[][] m) {
    this.metaQuery = m;
  }
  
  public Proxy getTransObject() {
    return (this.transObjectName instanceof Proxy) ? (Proxy)this.transObjectName : Proxy.getService(this.transObjectName.toString());
  }
  
  public void setTransObject(Object key) {
    this.transObjectName = key;
  }
  
  public Store clear() {
    this.removedRows.clear();
    this.records.clear();
    fireTableDataChanged();
    return this;
  }
  
  public boolean isLoaded() {
    return this.loaded;
  }
  
  public ArrayList<Object[]> getRemovedRows() {
    return this.removedRows;
  }
  
  public void setEditable(int indexColumn, boolean editable) {
    AbstractDataColumn dc = this.dataColumns.get(indexColumn);
    if (dc instanceof DefaultDataColumn)
      ((DefaultDataColumn)dc).setEditable(editable); 
  }
  
  public boolean isModified() {
    if (getRemovedRows().size() > 0)
      return true; 
    for (int r = 0; r < getRowCount(); r++) {
      Object[] record = (Object[]) this.records.get(r);
      if (((Short)record[0]).shortValue() > 0)
        return true; 
    } 
    return false;
  }
  
  public void cancel(boolean f) {
    cancel();
    this.records.addAll(this.removedRows);
    this.removedRows.clear();
  }
  
  public void cancel() {
    for (int r = this.records.size() - 1; r > -1; r--) {
      Object[] record;
      Object or;
      Object[] old;
      switch (getStateRow(r)) {
        case 2:
          record = (Object[]) this.records.get(r);
          or = record[1];
          old = (Object[])record[2];
          if (or instanceof Object[]) {
            Object[] nv = (Object[])or;
            for (int c = 0; c < old.length; c++) {
              if (!(old[c] instanceof Dummy)) {
                nv[c] = old[c];
                old[c] = this.dummy;
              } 
            } 
          } else {
            for (int c = 0; c < old.length; c++) {
              if (!(old[c] instanceof Dummy)) {
                try {
                  this.mwrite[c].invoke(or, new Object[] { old[c] });
                } catch (Exception ex) {
                  ex.printStackTrace();
                } 
                old[c] = this.dummy;
              } 
            } 
          } 
          record[0] = Short.valueOf((short)0);
          break;
        case 4:
          deleteRow(r);
          break;
      } 
    } 
    getRemovedRows().clear();
  }
  
  public void deleteRow(int row) {
    Object[] record = (Object[]) this.records.remove(row);
    if (((Short)record[0]).shortValue() != 4)
      this.removedRows.add(record); 
    fireTableRowsDeleted(row, row);
  }
  
  public void cancel(int row) {
    Object[] nv, old;
    int c;
    Object[] record = (Object[]) this.records.get(row);
    switch (((Short)record[0]).shortValue()) {
      case 2:
        nv = (Object[])record[1];
        old = (Object[])record[2];
        for (c = 0; c < old.length; c++) {
          if (!(old[c] instanceof Dummy)) {
            nv[c] = old[c];
            old[c] = this.dummy;
          } 
        } 
        record[0] = Short.valueOf((short)0);
        break;
      case 4:
        deleteRow(row);
        break;
    } 
  }
  
  public Store setDefault(String key, Object value) {
    this.defaultValue.put(key, value);
    X.log(getId() + " defaultValue=" + this.defaultValue);
    return this;
  }
  
  public Object getDefault(Object string) {
    return this.defaultValue.get(string);
  }
  
  public Store setDefault(Object col, Object o) {
    this.defaultValue.put(col, o);
    return this;
  }
  
  protected class Dummy {}
  
  public Store() {
    this.dummy = new Dummy();
    setId(increment++);
  }
  
  public int getRowCount() {
    return this.records.size();
  }
  
  public int getColumnCount(boolean withComputed) {
    if (getMetaQuery() == null)
      try {
        build(new Object[0]);
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }  
    return this.dataColumns.size();
  }
  
  public int getColumnCount() {
    return getColumnCount(true);
  }
  
  public String getName() {
    return this.name;
  }
  
  public void setValueAt(Object data, int row, String columnName) {
    int col = getColumnIndex(columnName);
    if (col == -1)
      throw new UnsupportedOperationException("Column " + columnName + " no defined!"); 
    setValueAt(data, row, col);
  }
  
  public Class getEntity() {
    return this.entityCls;
  }
  
  public void setTemplate(Class entity) {
    this.entityCls = entity;
  }
  
  public Object getObject(int r) {
    return getInstance(r, this.entityCls);
  }
  
  public Object getInstance(int r, Class cls) {
    HashMap<Object, Object> map = new HashMap<>();
    Object obj = null;
    if (cls instanceof Class) {
      try {
        obj = ((Class)cls).newInstance();
      } catch (Exception exception) {}
    } else if (cls != null) {
      obj = cls;
      cls = cls.getClass();
    } else {
      for (int j = 0; j < getColumnCount(false); j++)
        map.put(getColumnName(j), getValueAt(r, j)); 
      return map;
    } 
    for (Method mm : ((Class)cls).getMethods())
      map.put(mm.getName(), mm); 
    for (int i = 0; i < getColumnCount(false); i++) {
      String columnName = getColumnName(i);
      String nn = "set" + XUtil.capitalize(columnName.replaceAll("_", " ")).replaceAll(" ", "");
      Method mm = (Method)map.get(nn);
      if (mm != null) {
        Class[] clazz = mm.getParameterTypes();
        try {
          Object param = getValueAt(r, columnName);
          if (param != null)
            if (boolean.class.isAssignableFrom(clazz[0]) || Boolean.class.isAssignableFrom(clazz[0])) {
              param = "" + param;
              param = param.toString().trim();
              mm.invoke(obj, new Object[] { Boolean.valueOf(("on".equalsIgnoreCase(param.toString()) || "true".equalsIgnoreCase(param.toString()) || "1".equals(param))) });
            } else if (Date.class.isAssignableFrom(clazz[0])) {
              if (!(param instanceof Date) && 
                param != null)
                param = XDate.parseDate(param.toString()); 
              mm.invoke(obj, new Object[] { param });
            } else if (BigDecimal.class.isAssignableFrom(clazz[0])) {
              param = "" + param;
              mm.invoke(obj, new Object[] { new BigDecimal(param.toString()) });
            } else if (Number.class.isAssignableFrom(clazz[0])) {
              param = "" + param;
              mm.invoke(obj, new Object[] { clazz[0].getMethod("valueOf", new Class[] { String.class }).invoke(null, new Object[] { param }) });
            } else if (short.class.isAssignableFrom(clazz[0])) {
              param = "" + param;
              try {
                mm.invoke(obj, new Object[] { Short.valueOf((short)(int)Double.parseDouble(param.toString())) });
              } catch (Exception exception) {}
            } else if (int.class.isAssignableFrom(clazz[0])) {
              param = "" + param;
              try {
                mm.invoke(obj, new Object[] { Integer.valueOf((int)Double.parseDouble(param.toString())) });
              } catch (Exception exception) {}
            } else if (double.class.isAssignableFrom(clazz[0])) {
              param = "" + param;
              try {
                mm.invoke(obj, new Object[] { Double.valueOf(Double.parseDouble(param.toString())) });
              } catch (Exception exception) {}
            } else if (char.class.isAssignableFrom(clazz[0])) {
              param = "" + param;
              if (param.toString().trim().length() > 0)
                mm.invoke(obj, new Object[] { Character.valueOf(param.toString().charAt(0)) }); 
            } else {
              mm.invoke(obj, new Object[] { param });
            }  
        } catch (IllegalAccessException|IllegalArgumentException|java.lang.reflect.InvocationTargetException|NoSuchMethodException|SecurityException e) {
          X.log(columnName + " " + e.toString());
        } 
      } 
    } 
    return obj;
  }
  
  public void setValueAt(Object o, int row, int col) {
    if (this.dataColumns.get(col) instanceof ComputedDataColumn)
      return; 
    Object[] record = (Object[]) this.records.get(row);
    Object oo = record[1];
    if (o instanceof Date && 
      !(o instanceof Date) && !(o instanceof Time) && !(o instanceof Timestamp))
      o = new Date(((Date)o).getTime()); 
    if (o != null && o.toString().length() == 0 && 
      getProperty(col, 1) != "")
      o = null; 
    if (o instanceof Tag)
      o = new Tag(((Tag)o).getId(), ((Tag)o).getData()); 
    Object ref = null;
    int ic = 0;
    if (oo instanceof Object[]) {
      Object[] d = (Object[])oo;
      ref = d[col];
      ic = d.length;
    } else {
      ic = this.mread.length;
      try {
        ref = this.mread[col].invoke(oo, new Object[0]);
      } catch (Exception ex) {
        ex.printStackTrace();
      } 
    } 
    if ((ref != null || o != null) && (ref == null || !ref.equals(o))) {
      if (((Short)record[0]).shortValue() != 4) {
        Object[] mirror = (Object[])record[2];
        if (mirror == null) {
          mirror = new Object[ic];
          for (int j = 0; j < ic; j++)
            mirror[j] = this.dummy; 
          record[2] = mirror;
        } 
        if ((o == null && mirror[col] == null) || (o != null && o.equals(mirror[col]))) {
          ref = mirror[col];
          mirror[col] = this.dummy;
        } else {
          if (mirror[col] instanceof Dummy)
            mirror[col] = ref; 
          ref = o;
        } 
        record[0] = Short.valueOf((short)0);
        for (int i = 0; i < ic; i++) {
          if (!(mirror[i] instanceof Dummy)) {
            record[0] = Short.valueOf((short)2);
            break;
          } 
        } 
      } else {
        ref = o;
      } 
      if (oo instanceof Object[]) {
        ((Object[])oo)[col] = ref;
      } else {
        try {
          this.mwrite[col].invoke(oo, new Object[] { ref });
        } catch (Exception ex) {
          ex.printStackTrace();
        } 
      } 
      col = getPreparedRow(col);
      if (col > -1)
        fireTableCellUpdated(row, col); 
    } 
  }
  
  protected int getPreparedRow(int r) {
    return r;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public AbstractDataColumn addColumn(String name) {
    return addColumn(name, (String)null, new Object[0]);
  }
  
  public AbstractDataColumn addColumn(String name, boolean isUpdatable, Object... o) {
    return addColumn(name, isUpdatable ? name : null, o);
  }
  
  public String getColumnName(int col) {
    return ((AbstractDataColumn)this.dataColumns.get(col)).getName();
  }
  
  public Object[][] getMatrix() {
    int nCol = getColumnCount();
    int nRow = getRowCount();
    Object[][] o = new Object[nRow][nCol];
    for (int i = 0; i < nRow; i++) {
      for (int c = 0; c < nCol; c++)
        o[i][c] = getValueAt(i, c); 
    } 
    return o;
  }
  
  public AbstractDataColumn addColumn(AbstractDataColumn newColumn) {
    this.dataColumns.add(newColumn);
    return newColumn;
  }
  
  public Object getValueAt(int row, String columnName) {
    return getValueAt(row, getColumnIndex(columnName));
  }
  
  public Object getValueAt(int row, int col) {
    if (this.dataColumns.get(col) instanceof ComputedDataColumn)
      return ((ComputedDataColumn)this.dataColumns.get(col)).getValue(this, row); 
    Object o = getRecord(row);
    if (o instanceof Object[])
      return ((Object[])o)[col]; 
    try {
      return this.mread[col].invoke(o, new Object[0]);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    } 
  }
  
  public Object getOriginalAt(int row, String columnName) {
    int in = getColumnIndex(columnName);
    Object[] d = (Object[])getRecord(row, true);
    return (d.length == 3 && ((Object[])d[2])[in] != this.dummy) ? ((Object[])d[2])[in] : ((Object[])d[1])[in];
  }
  
  public Object getRecord(int i) {
    return getRecord(i, false);
  }
  
  public Object getRecord(int i, boolean all) {
    Object[] record = (Object[]) this.records.get(i);
    return all ? record : record[1];
  }
  
  public void setValueAt(ComboBoxModel combo, int col) {
    for (int row = 0, rowCount = getRowCount(); row < rowCount; row++)
      setValueAt(combo, row, col); 
  }
  
  public void setValueAt(ComboBoxModel<Tag> combo, int row, int col) {
    Object o = getRecord(row);
    if (o instanceof Object[]) {
      Object[] record = (Object[])o;
      for (int j = 0, size = combo.getSize(); j < size; j++) {
        if (record[col] instanceof Object && record[col].toString().compareTo("" + ((Tag)combo.getElementAt(j)).getId()) == 0) {
          record[col] = combo.getElementAt(j);
          break;
        } 
      } 
    } 
  }
  
  public int findColumn(String columnName) {
    if (columnName != null)
      for (int i = 0, n = this.dataColumns.size(); i < n; i++) {
        if (((AbstractDataColumn)this.dataColumns.get(i)).getName().equalsIgnoreCase(columnName))
          return i; 
      }  
    return -1;
  }
  
  public int getColumnIndex(String columnName) {
    for (int i = 0, n = this.dataColumns.size(); i < n; i++) {
      if (((AbstractDataColumn)this.dataColumns.get(i)).getName().equalsIgnoreCase(columnName))
        return i; 
    } 
    return -1;
  }
  
  public boolean isCellEditable(int row, int col) {
    if (this.dataColumns.get(col) instanceof DefaultDataColumn)
      return ((DefaultDataColumn)this.dataColumns.get(col)).isEditable(); 
    return false;
  }
  
  public int insertRow() {
    return insertRow(getRowCount());
  }
  
  public int insertRow(Object newRecord) {
    return insertRow(getRowCount(), newRecord);
  }
  
  public int insertRow(Object newRecord, boolean all) {
    return insertRow(getRowCount(), newRecord, all);
  }
  
  private static int x = 0;
  
  public static Default ID = new Default() {
      public Object getValue(Store store, int row) {
        return Integer.valueOf(0);//Store.access$004());
      }
    };
  
  public static Default NOW = new Default() {
      public Object getValue(Store store, int row) {
        return new Date((new Date()).getTime());
      }
    };
  
  private Object[] params;
  
  public int insertRow(int row) {
    Object[] newRecord = new Object[getColumnCount(false)];
    X.log(getId() + " insert w defaultValue=" + this.defaultValue);
    for (int j = 0; j < getColumnCount(false); j++) {
      Object o = this.defaultValue.get(getColumnName(j));
      if (o instanceof Default)
        o = ((Default)o).getValue(this, row); 
      newRecord[j] = o;
    } 
    return insertRow(row, newRecord);
  }
  
  public int insertRow(int row, Object newRecord) {
    return insertRow(row, newRecord, false);
  }
  
  public int insertRow(int row, Object record, boolean all) {
    row = (this.records.size() > 0) ? row : 0;
    (new Object[3])[0] = Short.valueOf((short)4);
    (new Object[3])[1] = record;
    (new Object[3])[2] = null;
    this.records.add(row, all ? record : new Object[3]);
    fireTableRowsInserted(row, row);
    return row;
  }
  
  public short getStateRow(int row) {
    return ((Short)((Object[])this.records.get(row))[0]).shortValue();
  }
  
  public void setStateRow(int r, short state) {
    ((Object[])this.records.get(r))[0] = Short.valueOf(state);
    if (state == 4)
      ((Object[])this.records.get(r))[2] = null; 
  }
  
  public ArrayList getRecords(boolean all) {
    if (!all) {
      ArrayList<Object> data = new ArrayList();
      for (Object r : this.records) {
        if (r instanceof Object[])
          data.add(((Object[])r)[1]); 
      } 
      return data;
    } 
    return this.records;
  }
  
  public ArrayList getRecords() {
    return getRecords(true);
  }
  
  public String toString(int from, int to) {
    StringBuilder builder = new StringBuilder();
    int cc = getColumnCount();
    int rc = getRowCount();
    builder.append("\nColumnas=").append(cc).append("\tRegistros=\n").append(rc);
    builder.append("State\t");
    for (int c = 0; c < cc; c++)
      builder.append(getColumnName(c)).append("\t"); 
    builder.append("\n");
    for (int r = 0; r < rc; r++) {
      builder.append(getStateRow(r)).append(">\t");
      for (int i = 0; i < cc; i++)
        builder.append(getValueAt(r, i)).append("\t"); 
      builder.append("\n");
    } 
    return builder.toString();
  }
  
  public void println() {
    System.out.println(toString(0, getRowCount()));
  }
  
  public void validate() {
    for (Object r : this.records) {
      Object[] rr = (Object[])((Object[])r)[1];
      for (int c = 0, cc = rr.length; c < cc; c++) {
        Class<?> cl = getColumnClass(c);
        if (rr[c] != null) {
          if (rr[c] instanceof Tag)
            rr[c] = ((Tag)rr[c]).getId(); 
          if (String.class.isAssignableFrom(cl)) {
            rr[c] = rr[c].toString();
          } else if (BigDecimal.class.isAssignableFrom(cl)) {
            try {
              rr[c] = new BigDecimal(rr[c].toString());
            } catch (Exception ex) {
              rr[c] = null;
            } 
          } else if (Number.class.isAssignableFrom(cl)) {
            try {
              rr[c] = cl.getMethod("valueOf", new Class[] { String.class }).invoke(null, new Object[] { rr[c].toString() });
            } catch (Exception ex) {
              rr[c] = null;
            } 
          } 
        } 
      } 
    } 
  }
  
  public void setRecords(Object data_) {
    if (getMetaQuery() == null)
      try {
        build(new Object[0]);
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }  
    this.removedRows.clear();
    this.records.clear();
    if (data_ instanceof List) {
      List list = (List)data_;
      if (list.size() > 0) {
        Object oo = list.get(0);
        if (oo instanceof Object[]) {
          Object[] arrayOfObject = (Object[])oo;
          if (arrayOfObject.length > 1 && arrayOfObject[1] instanceof Object[]) {
            this.records.addAll(list);
          } else {
            for (Object data : list) {
              this.records.add(new Object[] { Short.valueOf((short)0), data, null });
            } 
          } 
        } else {
          for (Object data : list) {
            this.records.add(new Object[] { Short.valueOf((short)0), data, null });
          } 
        } 
      } 
    } else {
      Object[] list = (Object[])data_;
      if (list.length > 0) {
        Object[] arrayOfObject = (Object[])list[0];
        if (arrayOfObject.length > 1 && arrayOfObject[1] instanceof Object[]) {
          this.records.addAll(Arrays.asList(arrayOfObject));
        } else {
          for (Object data : list) {
            this.records.add(new Object[] { Short.valueOf((short)0), data, null });
          } 
        } 
      } 
    } 
    Object o = Integer.valueOf(this.records.size());
    Object[] listener = this.listenerList.getListenerList();
    for (int i = 0; i < listener.length; i++) {
      if (listener[i] instanceof DBListener)
        ((DBListener)listener[i]).retrieveEnd(o); 
    } 
    fireTableDataChanged();
  }
  
  private boolean isBlankNull(int col) {
    return false;
  }
  
  public void replaceData(String colKeys, String colDestiny, Store master, String colSource) {
    int indexKey = getColumnIndex(colKeys);
    int masterKey = master.getColumnIndex(colKeys);
    int indexDestiny = getColumnIndex(colDestiny);
    int indexSource = master.getColumnIndex(colSource);
    int rowCount = getRowCount();
    for (int r = 0; r < rowCount; r++) {
      int p = master.find(0, new Object[] { Integer.valueOf(masterKey), getValueAt(r, indexKey) });
      if (p > -1) {
        setValueAt(master.getValueAt(p, indexSource), r, indexDestiny);
        setStateRow(r, (short)0);
      } 
    } 
  }
  
  public int find(int start, Object... param) {
    int[] key = new int[(int)Math.floor((param.length / 2))];
    for (int i = 0; i < key.length; i++) {
      Object k = param[i * 2];
      key[i] = (k instanceof Integer) ? ((Integer)k).intValue() : getColumnIndex((String)k);
    } 
    for (int rowCount = getRowCount(); start < rowCount; start++) {
      Object[] record = (Object[])getRecord(start);
      boolean ok = true;
      for (int c = 0; c < key.length; c++) {
        Object v = record[key[c]];
        Object p = param[c * 2 + 1];
        if ((v == null && p != null) || v == null || !v.equals(p)) {
          ok = false;
          break;
        } 
      } 
      if (ok)
        return start; 
    } 
    return -1;
  }
  
  public Object[] getParams() {
    return (this.params != null) ? this.params : ((this.Q != null) ? (Object[])this.Q.getParams() : null);
  }
  
  public Store setParams(Object[] params) {
    this.params = params;
    return this;
  }
  
  public static <T extends Store> T getByQ(Class<Store> cls, Object x) {
    if (x != null) {
      if (x instanceof Boolean) {
        if (((Boolean)x).booleanValue())
          return (T)Pooler.getSingleton(cls); 
        try {
          Object o = cls.newInstance();
          if (o instanceof Q)
            o = new Store((Q)o); 
          return (T)o;
        } catch (Exception ex) {
          X.log(ex);
          return null;
        } 
      } 
      return (T)Pooler.getSingleton(cls, x);
    } 
    try {
      return (T)cls.newInstance();
    } catch (InstantiationException|IllegalAccessException ex) {
      X.log(ex);
      return null;
    } 
  }
  
  public static <T extends Store> T get(Class<T> aClass, Object x) {
    if (x != null) {
      if (x instanceof Boolean) {
        if (((Boolean)x).booleanValue())
          return (T)Pooler.getSingleton(aClass); 
        try {
          Object o = aClass.newInstance();
          if (o instanceof Q)
            o = new Store((Q)o); 
          return (T)o;
        } catch (Exception ex) {
          X.log(ex);
          return null;
        } 
      } 
      if (x instanceof Class) {
        String key = Store.class.getCanonicalName() + "$" + ((Class)x).getCanonicalName();
        Store s = (Store)Pooler.get(key);
        if (s == null)
          Pooler.put(key, s = new Store((Q)Pooler.getSingleton((Class)x, Boolean.valueOf(true)))); 
        return (T)s;
      } 
      return (T)Pooler.getSingleton(aClass, x);
    } 
    try {
      return aClass.newInstance();
    } catch (Exception ex) {
      X.log(ex);
      return null;
    } 
  }
  
  public Store copy(Store origin, int i, int nr) {
    for (int c = 0, cc = getColumnCount(); c < cc; c++) {
      String n = getColumnName(c);
      try {
        setValueAt(origin.getValueAt(i, n), nr, c);
      } catch (Exception e) {
        X.log("no podu");
      } 
    } 
    return this;
  }
  
  public Store loadFrom(Object... ofile) {
    File f;
    if (ofile.length == 0) {
      f = XFile.getFile("data/" + getClass().getCanonicalName() + ".json");
    } else if (ofile[0] instanceof File) {
      f = (File)ofile[0];
    } else {
      f = XFile.getFile(ofile[0].toString());
    } 
    JsonElement data = X.getJSON(f, true);
    //setRecords(_HTTP0.fillResult(data, this, new ArrayList()));
    return this;
  }
  
  public File saveTo(Object... ofile) throws Exception {
    File f;
    if (ofile.length == 0) {
      f = XFile.getFile("data/" + getClass().getCanonicalName() + ".json");
    } else if (ofile[0] instanceof File) {
      f = (File)ofile[0];
    } else {
      f = XFile.getFile(ofile[0].toString());
    } 
    X.saveTo(f, X.gson.toJsonTree(getMatrix()));
    return f;
  }
  
  public Q getQ() {
    return this.Q;
  }
  
  public AbstractDataColumn addColumn(String name, String dbName, Object... p) {
    Object[][] old = getMetaQuery();
    if (old != null) {
      Object[][] o = new Object[old.length + 1][11];
      System.arraycopy(old, 0, o, 0, old.length);
      setMetaQuery(o);
    } else {
      setMetaQuery(old = new Object[1][11]);
    } 
    DefaultDataColumn dc = new DefaultDataColumn(name);
    dc.setEditable((dbName != null));
    this.dataColumns.add(dc);
    if (this.dataColumns.size() - 1 == getAutoincrement())
      setProperty(name, 4, Boolean.valueOf(false)); 
    setProperty(name, 6, dbName);
    setProperty(name, 1, Boolean.valueOf((dbName == null)));
    if (p != null && p.length > 0 && p.length % 2 == 0)
      for (int i = 0; i < p.length; i += 2)
        setProperty(name, ((Integer)p[i]).intValue(), p[i + 1]);  
    if (this.entityCls != null)
      for (Field f : this.entityCls.getDeclaredFields()) {
        if (f.getName().equals(name))
          if (f.getType().equals(String.class)) {
            X.alert(f.getType());
            setProperty(name, 7, Integer.valueOf(260));
          }  
      }  
    return (AbstractDataColumn)dc;
  }
  
  public void setAutoincrement(int autoincrement) {
    this.autoincrement = autoincrement;
  }
  
  public void setProperty(int cIndex, int COLUMN_INFO, Object o) {
    this.metaQuery[cIndex][COLUMN_INFO] = o;
    switch (COLUMN_INFO) {
      case 4:
        ((DefaultDataColumn)this.dataColumns.get(cIndex)).setEditable(((Boolean)o).booleanValue());
        break;
      case 0:
        ((DefaultDataColumn)this.dataColumns.get(cIndex)).setName((String)o);
        break;
    } 
  }
  
  public Store setProperty(String columnName, int COLUMN_INFO, Object o) {
    setProperty(getColumnIndex(columnName), COLUMN_INFO, o);
    return this;
  }
  
  public Object getProperty(int index, int COLUMN_INFO) {
    if (this.metaQuery.length > index)
      return this.metaQuery[index][COLUMN_INFO]; 
    return "VARCHAR";
  }
  
  public String getColumnType(int i) {
    return this.metaQuery[i][3].toString();
  }
  
  public int getComputedCount() {
    return 0;
  }
  
  public Class<?> getColumnClass(int columnIndex) {
    if (this.metaQuery != null && columnIndex < this.metaQuery.length && this.metaQuery[columnIndex][2] != null)
      return (Class)this.metaQuery[columnIndex][2]; 
    return Object.class;
  }
  
  public void setLoaded(boolean loaded) {
    this.loaded = loaded;
  }
  
  public String toString() {
    return this.query;
  }
  
  public boolean isModified(int row, int col) {
    Object[] record = (Object[]) this.records.get(row);
    if (((Short)record[0]).shortValue() == 2) {
      if (record[2] == null)
        return false; 
      return !(((Object[])record[2])[col] instanceof Dummy);
    } 
    return false;
  }
  
  protected Request createRequest(Request request) {
    Request aux = new Request();//new Object[0]);
    ArrayList<Integer> index = new ArrayList<>();
    for (int i = 0; i < this.metaQuery.length; i++) {
      aux.add(this.metaQuery[i][0]);
      index.add(Integer.valueOf(i));
    } 
    request.add(aux.toArray((Object[])new String[aux.size()]));
    aux.clear();
    ArrayList<Object[]> actions = new ArrayList();
    for (int r = 0; r < this.records.size(); r++) {
      Object[] oldData;
      int c;
      Object[] record = (Object[]) this.records.get(r);
      Object[] data = (Object[])record[1];
      switch (((Short)record[0]).shortValue()) {
        case 4:
          for (c = 0; c < index.size(); c++) {
            int j = ((Integer)index.get(c)).intValue();
            if (this.autoincrement != j || data[j] != null)
              if (data[j] instanceof Tag) {
                aux.add(new Object[] { ((Tag)data[j]).getId(), Integer.valueOf(c) });
              } else if (data[j] instanceof Date) {
                if (this.metaQuery[j][2] == Timestamp.class && !(data[j] instanceof Timestamp)) {
                  aux.add(new Object[] { new Timestamp(((Date)data[j]).getTime()), Integer.valueOf(c) });
                } else if (this.metaQuery[j][2] == Time.class && !(data[j] instanceof Time)) {
                  aux.add(new Object[] { new Time(((Date)data[j]).getTime()), Integer.valueOf(c) });
                } else {
                  aux.add(new Object[] { data[j], Integer.valueOf(c) });
                } 
              } else {
                aux.add(new Object[] { data[j], Integer.valueOf(c) });
              }  
          } 
          actions.add(new Object[] { Short.valueOf((short)4), Integer.valueOf(r), aux.toArray((Object[])new Object[aux.size()][2]) });
          aux.clear();
          break;
        case 2:
          oldData = (Object[])record[2];
          for (c = 0; c < index.size(); c++) {
            int j = ((Integer)index.get(c)).intValue();
            if (!(oldData[j] instanceof Dummy) || getProperty(j, 10) != null)
              if (data[j] instanceof Tag) {
                aux.add(new Object[] { ((Tag)data[j]).getId(), Integer.valueOf(c) });
              } else {
                aux.add(new Object[] { data[j], Integer.valueOf(c) });
              }  
          } 
          record = new Object[] { Short.valueOf((short)2), Integer.valueOf(r), aux.toArray((Object[])new Object[aux.size()][2]), null };
          aux.clear();
          for (c = 0; c < index.size(); c++) {
            int j = ((Integer)index.get(c)).intValue();
            if (this.metaQuery[j][5] != null && ((Boolean)this.metaQuery[j][5])
              .booleanValue())
              if (oldData[j] instanceof Dummy) {
                aux.add(new Object[] { data[j], Integer.valueOf(c) });
              } else {
                aux.add(new Object[] { oldData[j], Integer.valueOf(c) });
              }  
          } 
          record[3] = aux.toArray((Object[])new Object[aux.size()][2]);
          aux.clear();
          actions.add(record);
          break;
      } 
    } 
    ArrayList<Object[]> actionsDelete = new ArrayList();
    if (this.removedRows.size() > 0) {
      for (int j = 0; j < this.removedRows.size(); j++) {
        Object[] record = this.removedRows.get(j);
        Object[] newData = (Object[])record[1];
        Object[] oldData = (Object[])record[2];
        for (int c = 0; c < index.size(); c++) {
          int k = ((Integer)index.get(c)).intValue();
          if (this.metaQuery[k][5] instanceof Boolean && ((Boolean)this.metaQuery[k][5])
            .booleanValue())
            if (oldData == null || oldData[k] instanceof Dummy) {
              aux.add(new Object[] { newData[k], Integer.valueOf(c) });
            } else {
              aux.add(new Object[] { oldData[k], Integer.valueOf(c) });
            }  
        } 
        actionsDelete.add(aux.toArray((Object[])new Object[aux.size()][2]));
        aux.clear();
      } 
      actions.add(new Object[] { Short.valueOf((short)-1), actionsDelete });
    } 
    if (actions.isEmpty()) {
      request.clear();
    } else {
      request.setAttribute("#data", actions);
    } 
    return request;
  }
  
  public void update() throws Exception {
    if (getMetaQuery() == null)
      build(new Object[0]); 
    Proxy service = getTransObject();
    Request request = createRequest(new Request());//;new Object[0]));
    if (request.isEmpty())
      return; 
    request.setAttribute("#ds", service.getName());
    X.log(service.getClass());
    Object re = service.update((HttpServletRequest)request, getQ(), (List)request.getAttribute("#data"));
    if (re instanceof Exception)
      throw (Exception)re; 
    List<Object[]> response = (List)re;
    if (response instanceof Request && !((Request)response).isOK())
      throw ((Request)response).getException(); 
    this.removedRows.clear();
    boolean updateUI = false;
    for (int i = 0; i < response.size(); i++) {
      Object[] record, o = response.get(i);
      switch (((Short)o[0]).shortValue()) {
        case 4:
          if (((Number)o[1]).intValue() > 0) {
            Object[] arrayOfObject = (Object[]) this.records.get(((Integer)o[2]).intValue());
            arrayOfObject[0] = Short.valueOf((short)0);
            if (this.autoincrement > -1)
              ((Object[])arrayOfObject[1])[this.autoincrement] = o[1]; 
            updateUI = true;
            break;
          } 
          record = (Object[]) this.records.get(((Integer)o[2]).intValue());
          record[0] = Short.valueOf((short)0);
          updateUI = true;
          break;
        case 2:
          if (((Integer)o[1]).intValue() > 0) {
            record = (Object[]) this.records.get(((Integer)o[2]).intValue());
            record[0] = Short.valueOf((short)0);
            Object[] oo = (Object[])record[2];
            for (int p = 0; p < oo.length; p++)
              oo[p] = this.dummy; 
            updateUI = true;
          } 
          break;
      } 
    } 
    if (updateUI)
      fireTableDataChanged(); 
    Store s = (Store)Pooler.exist(getClass(), Boolean.valueOf(true));
    if (s != null && s != this)
      s.setLoaded(false); 
  }
  
  public int getAutoincrement() {
    return this.autoincrement;
  }
}
