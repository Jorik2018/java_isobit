package org.isobit.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import org.isobit.app.X;

public class Result extends ArrayList {
  public static Result EMPTY_LIST = new Result(new Object[0]);
  
  private HashMap map = null;
  
  private boolean _ok;
  
  public String toString() {
    return "Request{map=" + this.map + "_ok=" + this._ok + '}' + super.toString();
  }
  
  public Object getAttribute(Object key) {
    return (this.map != null) ? this.map.get(key) : null;
  }
  
  public Object getAttribute(String key) {
    return getAttribute(key);
  }
  
  public Result setAttribute(Object key, Object value) {
    if (this.map == null)
      this.map = new HashMap<>(); 
    this.map.put(key, value);
    return this;
  }
  
  public void setAttribute(String key, Object o) {
    setAttribute(key, o);
  }
  
  public static Result get(Exception ex) {
    Result result = new Result(ex);
    result.add(ex);
    return result;
  }
  
  public static Result getResult(Throwable ex) {
    Result result = new Result(ex);
    result.add(ex);
    return result;
  }
  
  public Result(Object... p) {
    this._ok = false;
    addAll(Arrays.asList(p));
  }
  
  public Result(Throwable ex) {
    this._ok = false;
    add(ex);
  }
  
  public boolean isOK() {
    return this._ok;
  }
  
  public Exception getException() {
    return isOK() ? null : (Exception)get(0);
  }
  
  public Result setOK(boolean b) {
    this._ok = b;
    return this;
  }
  
  public Enumeration getAttributeNames() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public String getCharacterEncoding() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public void setCharacterEncoding(String string) throws UnsupportedEncodingException {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public int getContentLength() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public String getContentType() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public ServletInputStream getInputStream() throws IOException {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public String getParameter(String key) {
    Object o = (this.map != null) ? this.map.get(key) : null;
    return (o != null) ? o.toString() : null;
  }
  
  public String getProtocol() {
    return PROTOCOL;
  }
  
  public static String PROTOCOL = "java";
  
  public void removeAttribute(String key) {
    remove(key);
  }
  
  public Locale getLocale() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public Enumeration getLocales() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public boolean isSecure() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public RequestDispatcher getRequestDispatcher(String string) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public String getRealPath(String string) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public String getAuthType() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public Cookie[] getCookies() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public long getDateHeader(String string) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public String getHeader(String string) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public Enumeration getHeaders(String string) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public Enumeration getHeaderNames() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public int getIntHeader(String string) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public String getMethod() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public String getPathInfo() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public String getPathTranslated() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public String getContextPath() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public String getQueryString() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public String getRemoteUser() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public boolean isUserInRole(String string) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public Principal getUserPrincipal() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public String getRequestedSessionId() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public String getRequestURI() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public StringBuffer getRequestURL() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public String getServletPath() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public HttpSession getSession(boolean bln) {
    return X.getSession();
  }
  
  public HttpSession getSession() {
    return X.getSession();
  }
  
  public boolean isRequestedSessionIdValid() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public boolean isRequestedSessionIdFromCookie() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public boolean isRequestedSessionIdFromURL() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public boolean isRequestedSessionIdFromUrl() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
