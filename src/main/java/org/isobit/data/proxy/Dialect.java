package org.isobit.data.proxy;

public class Dialect {
  private final String name;
  
  private String GeneratedId = "SELECT @@identity";
  
  public static final int INIT = 11;
  
  public static final int QUERY = 10;
  
  public static final int TABLE = 9;
  
  public static final int FOUND_ROWS = 111;
  
  public static final int PAGING = 112;
  
  public String getGeneratedId() {
    return this.GeneratedId;
  }
  
  public void setGeneratedId(String GeneratedId) {
    this.GeneratedId = GeneratedId;
  }
  
  public String getName() {
    return this.name;
  }
  
  public Dialect(String n) {
    this.name = n;
  }
  
  public Object prepare(Object q, int option) {
    return q;
  }
  
  public Object prepare(Object q, int option, int start, int limit) {
    switch (option) {
      case 112:
        if (q instanceof String[]) {
          String[] y = (String[])q;
          if (y.length == 1)
            return (start > -1) ? (y[0].replaceFirst("SELECT", "SELECT SQL_CALC_FOUND_ROWS ") + " limit " + start + "," + ((limit <= 0) ? 20 : limit)) : y[0]; 
          if (start > -1)
            return "SELECT SQL_CALC_FOUND_ROWS " + y[1] + " FROM " + y[0] + ((y[2] != null) ? (" " + y[2]) : "") + ((y[3] != null) ? (" ORDER BY " + y[3]) : "") + " LIMIT " + start + "," + ((limit <= 0) ? 20 : limit); 
          return "SELECT " + y[1] + " FROM " + y[0] + ((y[2] != null) ? (" " + y[2]) : "") + ((y[3] != null) ? (" ORDER BY " + y[3]) : "");
        } 
        if (start > 0)
          return q.toString().replaceFirst("SELECT", "SELECT SQL_CALC_FOUND_ROWS ") + " limit " + start + "," + ((limit <= 0) ? 20 : limit); 
        break;
    } 
    return q;
  }
}
