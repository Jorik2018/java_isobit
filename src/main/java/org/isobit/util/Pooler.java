package org.isobit.util;

import java.util.ArrayList;
import java.util.HashMap;

public class Pooler {
  public static <T> T getSingleton(Class<T> c, Object key) {
    key = (key != null) ? (c.getCanonicalName() + "$" + key) : c;
    Object obj = null;
    try {
      obj = mapa.get(key);
      if (obj == null)
        mapa.put(key, obj = c.newInstance()); 
    } catch (Exception e) {
      throw new RuntimeException(e);
    } 
    return (T)obj;
  }
  
  public static <T> T getSingleton(Class<T> c) {
    return getSingleton(c, null);
  }
  
  private static ArrayList loader = new ArrayList();
  
  private static HashMap mapa = new HashMap<>();
  
  static {
    loader.add(new DefaultLoader());
  }
  
  public static void put(Object key, Object value) {
    mapa.put(key, value);
  }
  
  public static Object exist(Object key, Object extra) {
    return mapa.get("" + key + extra);
  }
  
  public static Object exist(Object key) {
    return mapa.get(key);
  }
  
  public static Object get(Object key, Object extra) {
    if (extra != null)
      key = key + "," + extra; 
    Object o = mapa.get(key);
    if (o == null) {
      if (key instanceof Class) {
        try {
          o = ((Class)key).newInstance();
        } catch (Exception e) {
          e.printStackTrace();
        } 
      } else {
        for (int i = 0; i < loader.size(); i++) {
          o = ((LoaderPool)loader.get(i)).load(key.toString(), mapa);
          if (o != null)
            break; 
        } 
      } 
      if (o != null)
        mapa.put(key, o); 
      o = mapa.get(key);
    } 
    return o;
  }
  
  public static Object get(Object key) {
    return get(key, null);
  }
}
