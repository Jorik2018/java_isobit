package org.isobit.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.isobit.app.X;
import org.isobit.util.XUtil;

public class Q {
  public static final String KEY = "#Q";
  
  protected String table;
  
  protected Object query;
  
  protected List metadata;
  
  private Object[][] params;
  
  private Object[] filter;
  
  protected Object[] tables;
  
  public static List copy(HttpServletRequest req, Q A, Q B, List data) {
    return copy(req, A, B, data, (short)0);
  }
  
  public static List copy(HttpServletRequest req, Q A, Q B, List data, short op) {
    List<Object[]> aux = new ArrayList();
    if (A == null) {
      List<Object[]> aux2 = new ArrayList();
      List<Object[]> list1 = new ArrayList();
      for (Object nn : data) {
        aux2.clear();
        List<Object[]> list = B.getMetadata();
        Object[] nr = (Object[])nn;
        boolean nu = true;
        for (int c = 0; c < list.size(); c++) {
          if (op == 4) {
            aux.add(new Object[] { nr[c], Integer.valueOf(c) });
          } else if (((Map<?, ?>)((Object[])list.get(c))[1]).containsKey("K")) {
            aux2.add(new Object[] { nr[c], Integer.valueOf(c) });
          } 
        } 
        list1.add(new Object[] { Short.valueOf(op), Integer.valueOf(0), aux.toArray(new Object[aux.size()][2]), aux2.toArray(new Object[aux2.size()][2]) });
      } 
      return list1;
    } 
    List<Object[]> mA = A.getMetadata();
    List<Object[]> mB = B.getMetadata();
    int[] x = new int[mA.size()];
    for (int i = 0; i < x.length; i++) {
      x[i] = -1;
      for (int j = 0; j < B.getMetadata().size(); j++) {
        if (((Object[])mA.get(i))[0].equals(((Object[])mB.get(j))[0])) {
          x[i] = j;
          break;
        } 
      } 
    } 
    List<Object[]> data2 = new ArrayList();
    try {
      for (Object nn : data) {
        Object[] record, nr = (Object[])nn;
        switch (XUtil.intValue(nr[0])) {
          case 4:
            aux.clear();
            for (Object n : (Object[])nr[2]) {
              if (x[XUtil.intValue(((Object[])n)[1])] > -1)
                aux.add(new Object[] { ((Object[])n)[0], Integer.valueOf(x[XUtil.intValue(((Object[])n)[1])]) }); 
            } 
            record = new Object[] { nr[0], nr[1], aux.toArray(new Object[aux.size()][2]) };
            data2.add(record);
          case 2:
            aux.clear();
            for (Object n : (Object[])nr[2]) {
              if (x[XUtil.intValue(((Object[])n)[1])] > -1)
                aux.add(new Object[] { ((Object[])n)[0], Integer.valueOf(x[XUtil.intValue(((Object[])n)[1])]) }); 
            } 
            aux.clear();
            record = new Object[] { nr[0], nr[1], aux.toArray(new Object[aux.size()][2]), null };
            for (Object n : (Object[])nr[3]) {
              if (x[XUtil.intValue(((Object[])n)[1])] > -1)
                aux.add(new Object[] { ((Object[])n)[0], Integer.valueOf(x[XUtil.intValue(((Object[])n)[1])]) }); 
            } 
            (new Object[4])[0] = nr[0];
            (new Object[4])[1] = nr[1];
            (new Object[4])[2] = aux.toArray(new Object[aux.size()][2]);
            (new Object[4])[3] = null;
            record[3] = new Object[4];
            data2.add(record);
        } 
      } 
    } catch (Exception ex) {
      X.alert(ex);
    } 
    return data2;
  }
  
  public static int find(List<Object[]> data, int start, Object... param) {
    int[] key = new int[(int)Math.floor((param.length / 2))];
    for (int i = 0; i < key.length; i++) {
      Object k = param[i * 2];
      key[i] = (k instanceof Integer) ? ((Integer)k).intValue() : 0;
    } 
    for (int rowCount = data.size(); start < rowCount; start++) {
      Object[] record = data.get(start);
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
  
  public void put(Object row, String name, Object v) {
    int i = -1;
    for (Object f : this.metadata) {
      Object[] ff = (Object[])f;
      i++;
      if (name.equals(ff[0])) {
        Object[] r = (Object[])row;
        r = (Object[])r[2];
        for (int j = 0; j < r.length; j++) {
          Object[] m = (Object[])r[j];
          if (m[1].equals(Integer.valueOf(i))) {
            if (XUtil.intValue(((Object[])row)[0]) != 4)
              ((Object[])row)[0] = Short.valueOf((short)2); 
            m[0] = v;
            return;
          } 
        } 
        List<Object[]> al = new ArrayList(Arrays.asList(r));
        al.add(new Object[] { v, Integer.valueOf(i) });
        if (XUtil.intValue(((Object[])row)[0]) != 4)
          ((Object[])row)[0] = Short.valueOf((short)2); 
        ((Object[])row)[2] = al.toArray(new Object[al.size()][2]);
      } 
    } 
  }
  
  public Object get(Object row, String name) {
    int i = -1;
    for (Object f : this.metadata) {
      Object[] ff = (Object[])f;
      i++;
      if (name.equals(ff[0])) {
        Object[] r = (Object[])row;
        r = (Object[])r[2];
        for (int j = 0; j < r.length; j++) {
          Object[] m = (Object[])r[j];
          if (m[1].equals(Integer.valueOf(i)))
            return m[0]; 
        } 
      } 
    } 
    return null;
  }
  
  public Object[] getTables() {
    return this.tables;
  }
  
  public void setTables(Object[] tables) {
    this.tables = tables;
  }
  
  public void beforeRetrieve(HttpServletRequest request, Map filters) {}
  
  public Object[] getFilter() {
    return this.filter;
  }
  
  public void setFilter(Object[] filter) {
    this.filter = filter;
  }
  
  public List afterRetrieve(HttpServletRequest request, List data) {
    return data;
  }
  
  public Q() {
    this(null);
  }
  
  public Q(String query) {
    this.query = query;
  }
  
  public String toString() {
    Class<?> cls = getClass();
    for (; cls != null && cls.isAnonymousClass(); cls = cls.getSuperclass());
    return "class=" + cls.getCanonicalName();
  }
  
  public Object[][] getParams() {
    return this.params;
  }
  
  public void setParams(Object[][] params) {
    this.params = params;
  }
  
  public List getMetadata() {
    return this.metadata;
  }
  
  public void setMetadata(List m) {
    this.metadata = m;
  }
  
  public Object getQuery() {
    return this.query;
  }
  
  public void setQuery(String... query) {
    this.query = query;
  }
  
  public void setQuery(Object query) {
    this.query = query;
  }
  
  public String getModel() {
    return this.table;
  }
  
  public void setModel(String table) {
    this.table = table;
  }
  
  public boolean update(HttpServletRequest request, List data, Object record, int step, int row) throws Exception {
    return true;
  }
}
