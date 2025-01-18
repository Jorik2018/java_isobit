package org.isobit.app;

import com.google.gson.*;
import java.awt.Cursor;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.UIManager;

import org.isobit.app.X.JSON.Enviroment;
import org.isobit.app.X.JSON.Login;
import org.isobit.app.model.User;
import org.isobit.data.proxy.Proxy;
import org.isobit.util.Constants;
import org.isobit.util.RandomUtil;
import org.isobit.util.XDate;
import org.isobit.util.XFile;
import org.isobit.util.XMap;
import org.isobit.util.XUtil;
//import org.primefaces.context.RequestContext;
//import org.primefaces.model.DefaultStreamedContent;

public class X
        implements Constants {

    public static String COMPANY_LOGO = "COMPANY_LOGO";
    public static String COUNTRY_LOGO = "COUNTRY_LOGO";
    public static String PROJECTS_PATH = "projects";
    public static String TEMPLATE = "TEMPLATE";
    public static final Integer NEW = Integer.valueOf(-100);;
    public static Gson gson;
    public static Login login;

    public static String getClientIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("HTTP_X_FORWARDED");
        }
        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("HTTP_X_CLUSTER_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("HTTP_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("HTTP_FORWARDED");
        }
        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("HTTP_VIA");
        }
        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("REMOTE_ADDR");
        }
        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static Map<String, Object[]> moduleNameMap = new HashMap();
    public static String NO_LOAD = "noload";

    public static String[] getModuleNameList(String moduleName) {
        String[] so = (String[]) moduleNameMap.get(moduleName);
        if (so == null) {
            String s = "";// X.getStringProperty("modules", "Role");
            if (s == null) {
                s = "";
            }
            moduleNameMap.put(moduleName, so = s.split(" "));
        }
        return so;
    }

    /*
     * public static List getModuleList(Class cls) {
     * List l = new ArrayList();
     * ScanResult scanResult = new FastClasspathScanner().scan();
     * scanResult.getNamesOfClassesImplementing(cls).stream().forEach((o) -> {
     * l.add(o.substring(o.lastIndexOf(".") + 1, o.indexOf("Facade")));
     * });
     * return l;
     * }
     */

    /*
     * public static ViewParam getViewParam() {
     * return (ViewParam)
     * FacesContext.getCurrentInstance().getApplication().getELResolver().
     * getValue(FacesContext.getCurrentInstance().getELContext(), null,
     * "viewParam");
     * }
     */

    public static void initSession() {
        X.setSession(enviroment.getSession(true));
    }

    public static enum PersistAction {

        CREATE,
        DELETE,
        UPDATE
    }

    private static int id;
    public static boolean installed;
    // public static final Integer FXA_ESTUDIANTE;
    private static ViewBuilder viewBuilder;
    public static final String DESTINY = "_DESTINY";
    public static final String ROUTER = "#router";
    protected static String urlAll;
    // public static final Integer NEW;
    public static String CONTEXT_PATH;
    // public static JsonObject config;
    // public static Gson gson;
    public static boolean DEBUG = false;
    public static final int INFO = 1;
    public static final int WARN = 2;
    public static final int ERROR = 3;
    public static PrinterJob printer;
    public static Cursor WAIT_CURSOR;
    public static Cursor HAND_CURSOR;
    protected static JFileChooser fileChooser;
    private static Icon iconApplication;
    public static final String FILENAME = "fileName";
    public static final String MSG = "_MSG";
    // public static ArrayList<JsonObject> plugins;
    public static final String CLASSPATH = "java.class.path";
    public static String APPLICATION_NAME;
    // public static Login login;
    private static ThreadLocal<List> MSG_THREAD_LOCAL = new ThreadLocal();
    public static ThreadLocal<HttpServletRequest> Request = new ThreadLocal();
    public static ThreadLocal<HttpSession> session = new ThreadLocal();
    public static ThreadLocal<Object> error;
    protected static Object window;

    public static Date getServerDate() {
        return new Date();
    }

    static {
        /*
         * installed = false;
         * FXA_ESTUDIANTE = -100002;
         * urlAll = null;
         * NEW = -100;
         * CONTEXT_PATH = null;
         * // session = new Session();
         * // GsonBuilder gb = new GsonBuilder();
         * // gson =
         * gb.setDateFormat("dd/MM/yyyy HH:mm:ss").disableHtmlEscaping().create();
         * printer = PrinterJob.getPrinterJob();
         * WAIT_CURSOR = new Cursor(3);
         * HAND_CURSOR = new Cursor(12);
         * // plugins = new ArrayList();
         * APPLICATION_NAME = "ISOBIT Desktop Application";
         * error = new ThreadLocal();
         */
    }

    public static HttpServletRequest getRequest() {
        return X.Request.get();
    }

    public static void setRequest(HttpServletRequest Request) {
        X.Request.set(Request);
    }

    public static void setSession(HttpSession session) {
        X.session.set(session);
    }

    /*
     * public static Map getViewMap() {
     * return ((ViewParam)
     * FacesContext.getCurrentInstance().getApplication().getELResolver().
     * getValue(FacesContext.getCurrentInstance().getELContext(), null,
     * "viewParam")).getP();
     * }
     */

    public static int id() {
        return ++id;
    }

    public static User getUser() {
        return (User) null;// X.getSession().getAttribute(X.USER);
    }

    public static String toString(HttpServletRequest request, Throwable ex) {
        int j;
        String ss = "<div style='padding-bottom:10px'><b>" + (ex instanceof NullPointerException ? "Null pointer!" : ex)
                + "</b></div>";
        StackTraceElement[] v = ex.getStackTrace();
        for (j = 0; j < v.length; ++j) {
            ss = ss + v[j] + "<BR/>";
        }
        for (ex = ex.getCause(); ex != null; ex = ex.getCause()) {
            ss = ss + "<div style='padding-top:10px;padding-bottom:10px'><b>Caused by: "
                    + (ex instanceof NullPointerException ? "Null pointer!" : ex) + "</b></div>";
            v = ex.getStackTrace();
            for (j = 0; j < v.length; ++j) {
                ss = ss + v[j] + "<BR/>";
            }
        }
        return ss;
    }

    public static void open(HttpServletRequest request) {
        X.open(request, true);
        if (X.DEBUG) {
            // System.out.println(SystemUtilities.getMemory());
        }
    }

    public static void open(HttpServletRequest request, boolean all) {
        request.setAttribute("jump", (Object) (XUtil.intValue(request.getAttribute("jump")) + 1));
        String[] url = request.getRequestURI().replaceFirst("[/]+", "").split("[/]+");
        String[] q = new String[url.length > 0 ? url.length - 1 : 0];
        for (int i = 0; i < q.length; ++i) {
            q[i] = url[i + 1];
        }
        request.setAttribute("#q", (Object) q);
        if (CONTEXT_PATH == null) {
            urlAll = request.getRequestURL().toString();
            CONTEXT_PATH = request.getContextPath();
            int ii = urlAll.indexOf(CONTEXT_PATH);
            CONTEXT_PATH = CONTEXT_PATH + "/";
            if (ii > -1) {
                urlAll = urlAll.substring(0, ii);
            }
            /*
             * GsonBuilder gsonBuilder = new GsonBuilder();
             * if (urlAll.contains("localhost")) {
             * X.DEBUG = true;
             * gsonBuilder = gsonBuilder.setPrettyPrinting();
             * }
             * gson = gsonBuilder.serializeSpecialFloatingPointValues().
             * setDateFormat("yyyy/MM/dd HH:mm:ss").create();
             */
        }
    }

    public static void msg(HttpServletRequest request, Writer out) throws Exception {
        List ms = MSG_THREAD_LOCAL.get();
        if (ms == null) {
            MSG_THREAD_LOCAL.set(ms = new ArrayList());
        }
        MSG_THREAD_LOCAL.remove();
        if (ms != null) {
            Object type = null;
            int k = ms.size();
            for (int i = 0; i < k; ++i) {
                Object[] m = (Object[]) ms.get(i);
                /*
                 * if (!(m[1] instanceof DrupalException)) {
                 * if (type != m[0]) {
                 * String tt;
                 * if (type != null) {
                 * out.append("</DIV><BR/>");
                 * }
                 * switch (XUtil.intValue(m[0])) {
                 * case 2: {
                 * tt = "warning";
                 * break;
                 * }
                 * case 3: {
                 * tt = "error";
                 * break;
                 * }
                 * default: {
                 * tt = "info";
                 * }
                 * }
                 * out.append("<DIV class='alert " + tt + "'>");
                 * type = m[0];
                 * }
                 * X.log("ZZ " + m[1]);
                 * if (m[1] instanceof Exception) {
                 * int j;
                 * request.setAttribute("#error", (Object) 1);
                 * Throwable ex = (Exception) m[1];
                 * out.append("<div style='padding-bottom:10px'><b>" + ex + "</b></div>");
                 * StackTraceElement[] v = ex.getStackTrace();
                 * for (j = 0; j < v.length; ++j) {
                 * out.append(v[j] + "<BR/>");
                 * }
                 * for (ex = ex.getCause(); ex != null; ex = ex.getCause()) {
                 * out.append("<div style='padding-top:10px;padding-bottom:10px'><b>Caused by: "
                 * + ex + "</b></div>");
                 * v = ex.getStackTrace();
                 * for (j = 0; j < v.length; ++j) {
                 * out.append(v[j] + "<BR/>");
                 * }
                 * }
                 * } else {
                 * out.append("" + m[1]);
                 * }
                 * }
                 * out.append("</DIV><BR/>");
                 */
            }
            ms.clear();
        }
    }

    public static void showMemory(HttpServletRequest request, Object cls) {
        long used;
        if (!(cls instanceof Class) && cls != null) {
            cls = cls.getClass();
        }
        if (request.getAttribute("memory") == null) {
            used = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            X.log(request.getRequestURI() + " : " + cls + " empieza con=" + used / 1000);
        } else {
            int ol = XUtil.intValue(request.getAttribute("memory"));
            used = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            X.log(request.getRequestURI() + " : " + cls + " variacion de memoria=" + used / 1000 + "-" + " es "
                    + (used - (long) ol) / 1000);
        }
        request.setAttribute("memory", (Object) used);
    }

    public static Object copy(HttpServletRequest request, Object obj) {
        return X.write(request, obj, "");
    }

    public static ViewBuilder getViews() {
        return viewBuilder;
    }

    public static String toText(Object o) {
        return o != null ? o.toString() : "";
    }

    public static void setViewBuilder(ViewBuilder vb) {
        viewBuilder = vb;
    }

    public static String url(HttpServletRequest request) {
        return X.url(XUtil.implode((String[]) request.getAttribute("#q"), (Object) "/"));
    }

    public static String url(String url) {
        return url(url, false);
    }

    /*
     * public Object getObject(javax.json.stream.JsonParser parser, Object o) {
     * String keyName = "";
     * while (parser.hasNext()) {
     * javax.json.stream.JsonParser.Event e = parser.next();
     * switch (e) {
     * case START_ARRAY:
     * if (o == null) {
     * o = new ArrayList();
     * } else if (o instanceof Map) {
     * ((Map) o).put(keyName, getObject(parser, new ArrayList()));
     * } else {
     * ((List) o).add(getObject(parser, new ArrayList()));
     * }
     * break;
     * case END_ARRAY:
     * return o;
     * case START_OBJECT:
     * if (o == null) {
     * o = new HashMap();
     * } else if (o instanceof Map) {
     * ((Map) o).put(keyName, getObject(parser, new HashMap()));
     * } else {
     * ((List) o).add(getObject(parser, new HashMap()));
     * }
     * break;
     * case END_OBJECT:
     * return o;
     * case VALUE_NUMBER:
     * if (o instanceof List) {
     * ((List) o).add(parser.getInt());
     * } else {
     * ((Map) o).put(keyName, parser.getInt());
     * }
     * break;
     * case VALUE_STRING:
     * if (o instanceof List) {
     * ((List) o).add(parser.getString());
     * } else {
     * ((Map) o).put(keyName, parser.getString());
     * }
     * break;
     * case VALUE_TRUE:
     * if (o instanceof List) {
     * ((List) o).add(true);
     * } else {
     * ((Map) o).put(keyName, true);
     * }
     * break;
     * case VALUE_FALSE:
     * if (o instanceof List) {
     * ((List) o).add(false);
     * } else {
     * ((Map) o).put(keyName, false);
     * }
     * break;
     * case VALUE_NULL:
     * if (o instanceof List) {
     * ((List) o).add(null);
     * } else {
     * ((Map) o).put(keyName, null);
     * }
     * break;
     * case KEY_NAME:
     * keyName = parser.getString();
     * break;
     * }
     * }
     * return o;
     * }
     */

    public static String MASK_DOMAIN = "www.regionancash.gob.pe";

    public static String MASK_CONTEXT = "/decaa";

    public static String mask_url(String url, String context) {
        HttpServletRequest r = X.getRequest();
        if (r == null) {
            return "";
        }
        CONTEXT_PATH = r.getContextPath();
        String s = CONTEXT_PATH + url;
        if (!s.startsWith("/")) {
            s = "/" + s;
        }
        return MASK_DOMAIN + (context != null ? context : MASK_CONTEXT) + s;
    }

    public static String url(String url, boolean all) {
        HttpServletRequest r = X.getRequest();
        CONTEXT_PATH = r.getContextPath();
        String s = CONTEXT_PATH + url;
        if (!s.startsWith("/")) {
            s = "/" + s;
        }
        return (all ? r.getScheme() + "://"
                + r.getServerName() + (r.getServerPort() == 80 ? ""
                        : ":"
                                + r.getServerPort())
                + r.getContextPath() : "") + s;
    }

    public static void update(Map m) {
        String update = (String) m.remove("UPDATE");
        if (update != null) {
            int n = update.indexOf('>');
            if (n > -1) {
                X.update(update.substring(0, n));
                m.put("UPDATE", update.substring(n + 1));
            } else {
                X.update(update);
            }
        }
    }

    public static void update(String update) {
        if (update != null) {
            for (String s : update.split(" ")) {
                X.log("UPDATE>" + s);
                // FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add(s);
            }
        }
    }

    /*
     * public static <T> T write(JsonObject o, Object obj) {
     * XMap<String, Method> map = new XMap<String, Method>(new Object[0]);
     * for (Method mm : obj.getClass().getMethods()) {
     * map.put(mm.getName(), mm);
     * }
     * for (Map.Entry ee : o.entrySet()) {
     * String n = (String) ee.getKey();
     * String nn = "set" + XUtil.capitalize(n).replaceAll("_", "");
     * try {
     * String param;
     * Method mm = (Method) map.get(nn);
     * Class<?>[] clazz = mm.getParameterTypes();
     * if (clazz.length != 1 || (param = o.get(n).getAsString()) == null) {
     * continue;
     * }
     * System.out.println("" + Date.class.equals(clazz[0]) + n + "=" + param +
     * "  ???" + Date.class + "=" + clazz[0] + "  >" +
     * Date.class.isAssignableFrom(clazz[0]));
     * if (Boolean.TYPE.isAssignableFrom(clazz[0]) ||
     * Boolean.class.isAssignableFrom(clazz[0])) {
     * param = param.trim();
     * Object[] arrobject = new Object[1];
     * arrobject[0] = "on".equalsIgnoreCase(param) || "true".equalsIgnoreCase(param)
     * || "1".equals(param);
     * mm.invoke(obj, arrobject);
     * continue;
     * }
     * if (Date.class.isAssignableFrom(clazz[0])) {
     * System.out.println("" + param + "   con " + XDate.getDateFormat());
     * mm.invoke(obj, XDate.parseDate(param));
     * continue;
     * }
     * if (Number.class.isAssignableFrom(clazz[0])) {
     * mm.invoke(obj, clazz[0].getMethod("valueOf", String.class).invoke(null,
     * param));
     * continue;
     * }
     * if (Integer.TYPE.isAssignableFrom(clazz[0])) {
     * mm.invoke(obj, XUtil.intValue(param));
     * continue;
     * }
     * mm.invoke(obj, param);
     * } catch (Exception mm) {
     * }
     * }
     * return (T) obj;
     * }
     */

    public static Object write(HttpServletRequest request, Object obj, String key) {
        HashMap<String, Method> map = new HashMap<String, Method>();
        for (Method mm : obj.getClass().getMethods()) {
            map.put(mm.getName(), mm);
        }
        Enumeration en = request.getParameterNames();
        while (en.hasMoreElements()) {
            String n = (String) en.nextElement();
            String nn = "set" + XUtil.capitalize(n.replaceFirst(key, "").replaceAll("_", " ")).replace(" ", "");
            try {
                Method mm = (Method) map.get(nn);
                Class<?>[] clazz = mm.getParameterTypes();
                if (clazz.length != 1) {
                    continue;
                }
                Object param = request.getParameter(key + n);
                if (param == null) {
                    param = request.getAttribute(key + n);
                }
                if (param == null) {
                    continue;
                }
                if (Boolean.TYPE.isAssignableFrom(clazz[0]) || Boolean.class.isAssignableFrom(clazz[0])) {
                    param = param.toString().trim();
                    Object[] arrobject = new Object[1];
                    arrobject[0] = "on".equalsIgnoreCase(param.toString()) || "true".equalsIgnoreCase(param.toString())
                            || "1".equals(param);
                    mm.invoke(obj, arrobject);
                    continue;
                }
                if (Date.class.isAssignableFrom(clazz[0])) {
                    mm.invoke(obj, XDate.parseDate(param.toString()));
                    continue;
                }
                if (BigDecimal.class.isAssignableFrom(clazz[0])) {
                    mm.invoke(obj, new BigDecimal(param.toString()));
                    continue;
                }
                if (Number.class.isAssignableFrom(clazz[0])) {
                    mm.invoke(obj, clazz[0].getMethod("valueOf", String.class).invoke(null, param));
                    continue;
                }
                if (Short.TYPE.isAssignableFrom(clazz[0])) {
                    try {
                        mm.invoke(obj, (short) Double.parseDouble(param.toString()));
                    } catch (Exception exception) {
                    }
                    continue;
                }
                if (Integer.TYPE.isAssignableFrom(clazz[0])) {
                    try {
                        mm.invoke(obj, (int) Double.parseDouble(param.toString()));
                    } catch (Exception exception) {
                    }
                    continue;
                }
                if (Double.TYPE.isAssignableFrom(clazz[0])) {
                    try {
                        mm.invoke(obj, Double.parseDouble(param.toString()));
                    } catch (Exception exception) {
                    }
                    continue;
                }
                if (Character.class.isAssignableFrom(clazz[0]) || Character.TYPE.isAssignableFrom(clazz[0])) {
                    if (param.toString().trim().length() <= 0) {
                        continue;
                    }
                    mm.invoke(obj, Character.valueOf(param.toString().charAt(0)));
                    continue;
                }
                mm.invoke(obj, param);
            } catch (Exception mm) {
            }
        }
        return obj;
    }

    public static Map getConfig() {
        return null;// X.getMap(config);
    }

    public static Icon getIconApplication() {
        if (iconApplication == null) {
            iconApplication = new ImageIcon(X.class.getResource("/images/java-16.png"));
        }
        return iconApplication;
    }

    /*
     * public static void addPlugin(String s) {
     * File plugin = new File(s);
     * try {
     * JsonObject jso = (JsonObject) new JsonParser().parse((Reader) new
     * InputStreamReader(new FileInputStream(plugin)));
     * plugins.add(jso);
     * } catch (Exception ex) {
     * X.log(ex);
     * }
     * }
     */

    public static void setProperty(Object key, Object value) {
        // getSession().put(key.toString(), value);
    }

    public static Object getProperty(Object key) {
        return null;// session.get(key);
    }

    public static /* varargs */ Method getMethod(Class c, String n, Class... cc) {
        Method m = null;
        try {
            m = c.getMethod(n, cc);
        } catch (Exception ex) {
            m = null;
        }
        return m;
    }

    public static JFileChooser getFileChooser() {
        if (fileChooser == null) {
            try {
                fileChooser = (JFileChooser) Class.forName("org.isobit.swing.XFileChooser").newInstance();
            } catch (Exception ex) {
                // X.alert(ex);
            }
        }
        fileChooser.setCurrentDirectory(new File("./user/"));
        return fileChooser;
    }

    public static String getUserFolder() {
        return "./user/";
    }

    public static void printStackTrace(Exception ex) {
    }

    public static void loadConfig(InputStreamReader isr) {
        // config = (JsonObject) new JsonParser().parse((Reader) isr);
    }

    public static File loadConfig(String name) throws FileNotFoundException {
        String fs = System.getProperty("file.separator");
        // String eqUserHomeDir = System.getProperty("") + fs +
        // System.getProperty("jsmodula.user.home.dir");
        String uploadDir = System.getProperty("isobit.uploadDir");
        if (!uploadDir.endsWith(fs)) {
            uploadDir += fs;
        }
        File f = new File(uploadDir + name);
        if (!f.exists()) {
            try {
                XFile.copyResource("/META-INF/config.json", f = XFile.getFile(f));
            } catch (Exception e) {
            }
            if (!f.exists()) {
                throw new RuntimeException("No es posible crear archivo " + f);
            }
        }
        X.loadConfig(new FileReader(uploadDir + name));
        return f;
    }

    /*
     * public static String getStringProperty(String... path) {
     * int i;
     * JsonObject o = config;
     * for (i = 0; i < path.length - 1; ++i) {
     * if (o == null) {
     * return null;
     * }
     * o = o.getAsJsonObject(path[i]);
     * }
     * if (o instanceof JsonObject) {
     * try {
     * JsonElement oo = o.get(path[i]);
     * return oo != null ? oo.getAsString() : null;
     * } catch (Exception e) {
     * return null;
     * }
     * }
     * return null;
     * }
     * 
     * public static JsonElement getObjectProperty(Object... path) {
     * int i;
     * JsonObject o = config;
     * for (i = 0; i < path.length - 1; ++i) {
     * o = o.getAsJsonObject(path[i].toString());
     * }
     * return o.get(path[i].toString());
     * }
     * 
     * public static HashMap getMap(JsonObject propiedades) {
     * HashMap p = new HashMap();
     * Set<Entry<String, JsonElement>> it = propiedades.entrySet();
     * for (Entry<String, JsonElement> ee : it) {
     * if (!ee.getValue().isJsonNull()) {
     * p.put(ee.getKey(), ee.getValue().getAsString());
     * }
     * }
     * return p;
     * }
     */

    /*
     * public static JsonElement getJSON(String name, Object... option) {
     * return X.getJSON(name, true);
     * }
     * 
     * public static JsonElement getJSON(File f, boolean create) {
     * try {
     * JsonElement el = new JsonParser().parse((Reader) new FileReader(f));
     * return el instanceof JsonNull ? (create ? new JsonObject() : null) : el;
     * } catch (IOException e) {
     * X.log(e);
     * return create ? new JsonObject() : null;
     * }
     * }
     * 
     * public static JsonElement getJSON(String fileName, boolean create) {
     * return X.getJSON(XFile.getFile(fileName, create), create);
     * }
     * 
     * public static void set(Object... path) {
     * X.set((JsonElement) config, path);
     * }
     * 
     * public static JsonElement set(JsonElement config, Object... path) {
     * JsonObject o = (JsonObject) config;
     * JsonObject e = null;
     * for (int i = 0; i < path.length - 2; ++i) {
     * e = o.getAsJsonObject(path[i].toString());
     * if (e == null) {
     * e = new JsonObject();
     * o.add(path[i].toString(), (JsonElement) e);
     * }
     * o = e;
     * }
     * X.replace(o, path[path.length - 2].toString(), path[path.length - 1]);
     * return config;
     * }
     * 
     * public static void replace(JsonObject config, String key, Object nv) {
     * config.remove(key);
     * if (nv instanceof JsonElement) {
     * config.add(key, (JsonElement) nv);
     * } else if (nv instanceof Number) {
     * config.addProperty(key, (Number) nv);
     * } else {
     * config.addProperty(key, "" + nv);
     * }
     * }
     */

    public static void startup() throws Exception {
        // JsonObject java;
        String t;
        // JsonElement s;
        // Store.STATE = 1;
        UIManager.put("ComboBox.disabledF", UIManager.get("TextField.disabledF"));
        UIManager.put("MenuItem.font", UIManager.get("TextField.font"));
        UIManager.put("RadioButton.font", UIManager.get("TextField.font"));
        UIManager.put("ProgressBar.foreground", UIManager.get("TextField.foreground"));
        UIManager.put("Menu.font", UIManager.get("TextField.font"));
        UIManager.put("Button.font", UIManager.get("TextField.font"));
        UIManager.put("TabbedPane.font", UIManager.get("TextField.font"));
        UIManager.put("ComboBox.font", UIManager.get("TextField.font"));
        UIManager.put("Label.font", UIManager.get("TextField.font"));
        UIManager.put("CheckBox.font", UIManager.get("TextField.font"));
        // SystemProperties.loadPropertiesResource("system", "system.properties");
        // System.setProperty("isobit.user.home.dir",
        // SystemProperties.getProperty("system", "isobit.user.home.dir"));
        // System.setProperty("jsm.build", SystemProperties.getProperty("system",
        // "jsm.build"));
        if (new File("var").exists()) {
            System.setProperty("user.home", new File("var").getCanonicalFile().getParentFile().getCanonicalPath());
            System.setProperty("isobit.user.home.dir", "var");
        }
        try {
            // config = (JsonObject) X.getJSON("config.json", new Object[0]);
        } catch (Exception exception) {
            // empty catch block
        }
        /*
         * if (config == null || config.entrySet().size() <= 0) {
         * config = new JsonObject();
         * X.set("isobit", "server", "default", "");
         * X.set(new Object[]{"isobit", "server", "options", new JsonObject()});
         * X.set(new Object[]{"isobit", "resources", "files", new JsonObject()});
         * X.set(new Object[]{"isobit", "resources", "strings", new JsonObject()});
         * X.set("isobit", "classpath", "");
         * X.set("java", "http.proxyHost", "");
         * X.set("java", "http.proxyPort", "");
         * X.set("java", "http.supportHost", "http://www.isobit.net/");
         * X.saveTo("config.json", (JsonElement) config);
         * }
         * if ((t = X.getStringProperty("isobit", "resources", "strings", "titleApp"))
         * != null) {
         * APPLICATION_NAME = t;
         * }
         * if ((s = X.getObjectProperty("isobit", "classpath")) instanceof JsonElement)
         * {
         * for (String ss : s.getAsString().split(" ")) {
         * if (ss.length() <= 0) {
         * continue;
         * }
         * ClassPathUtil.addFile(ss, new String[0]);
         * }
         * }
         * if ((java = (JsonObject) X.getObjectProperty("java")) != null) {
         * for (Map.Entry ee : java.entrySet()) {
         * String property = "";
         * try {
         * property = java.get((String) ee.getKey()).getAsString();
         * } catch (UnsupportedOperationException e) {
         * property = "";
         * }
         * if (property.length() <= 0) {
         * continue;
         * }
         * System.setProperty((String) ee.getKey(), property);
         * }
         * }
         * File file = XFile.getFile("cnx");
         * for (File f : file.listFiles()) {
         * if (!f.getName().endsWith(".json")) {
         * continue;
         * }
         * X.set(new Object[]{"isobit", "server", "options",
         * f.getName().replace(".json", ""), new JsonObject()});
         * }
         * try {
         * // Proxy.registerService("http", new HTTPProxy()).connect(session,
         * "java.class.path", null);
         * } catch (Exception ex) {
         * ex.printStackTrace();
         * X.log(ex);
         * }
         */
    }

    /*
     * public static void saveTo(File f, JsonElement content) throws Exception {
     * block4:
     * {
     * FileWriter writer = null;
     * try {
     * writer = new FileWriter(f);
     * writer.write(gson.toJson(content));
     * writer.close();
     * } catch (Exception ex) {
     * X.log(ex);
     * if (writer == null) {
     * break block4;
     * }
     * try {
     * writer.close();
     * } catch (Exception ex1) {
     * X.log(ex1);
     * }
     * }
     * }
     * }
     */

    public static File saveTo(File f, Object content) throws Exception {
        FileOutputStream saveFile = new FileOutputStream(f);
        ObjectOutputStream save = new ObjectOutputStream(saveFile);
        save.writeObject(content);
        save.close();
        return f;
    }

    /*
     * public static void saveTo(String fileName, JsonElement content) throws
     * Exception {
     * X.saveTo(XFile.getFile(fileName), content);
     * }
     * 
     * public static boolean saveConfiguration() throws Exception {
     * X.saveTo("config.json", (JsonElement) config);
     * return true;
     * }
     */
    public static void log(Object msg) {
        // XLog.getInstance().append(msg);
    }

    /*
     * static class JSFEnviroment implements Enviroment {
     * 
     * @Override
     * public HttpSession getSession(boolean reset) {
     * if (reset) {
     * return X.getRequest().getSession(true);
     * }
     * HttpSession s = X.session.get();
     * return s != null ? s
     * : ((HttpSession) FacesContext.getCurrentInstance()
     * .getExternalContext().getSession(true));
     * }
     */
    // @Override
    public static Object alert(Object m) {
        System.out.println("enviroment.alert(m);");
        return enviroment.alert(m);
    }

    public Object alert(Object type, String summary, Object m) {
        /*
         * FacesContext fc = FacesContext.getCurrentInstance();
         * if (fc != null) {
         * if (m instanceof Throwable && !(m instanceof SimpleException)) {
         * UtilController uc = ((UtilController)
         * FacesContext.getCurrentInstance().getApplication().getELResolver().
         * getValue(FacesContext.getCurrentInstance().getELContext(), null,
         * "utilController"));
         * List l = (List) uc.getP().get("error");
         * if (l == null) {
         * uc.getP().put("error", l = new ArrayList());
         * update("ErrorForm");
         * //RequestContext.getCurrentInstance().execute("_.o('ErrorForm')");
         * }
         * l.add(m);
         * } else {
         * if (m instanceof SimpleException) {
         * type = X.ERROR;
         * m = ((SimpleException) m).getMessage();
         * }
         * Severity severity = FacesMessage.SEVERITY_INFO;
         * switch (XUtil.intValue(type)) {
         * case X.ERROR:
         * severity = FacesMessage.SEVERITY_ERROR;
         * break;
         * case X.WARN:
         * severity = FacesMessage.SEVERITY_WARN;
         * }
         * fc.addMessage(null, new FacesMessage(severity, summary, m.toString()));
         * if (severity == FacesMessage.SEVERITY_ERROR) {
         * // RequestContext.getCurrentInstance().execute("_.OK=false");
         * }
         * }
         * }
         */
        return null;
    }

    public static void alert(int type, Object detail) {
        // if (detail instanceof String) {
        // alert((type == X.WARN ? FacesMessage.SEVERITY_WARN :
        // FacesMessage.SEVERITY_INFO),
        // "Mensaje del Servidor",
        // (String) detail);
        // return;
        // }
        // List ms = MSG_THREAD_LOCAL.get();
        // if (ms == null) {
        // MSG_THREAD_LOCAL.set(ms = new ArrayList());
        // }
        // if (type == X.ERROR) {
        // error.set(detail);
        // }
        // ms.add(new Object[]{type, detail});
    }

    public static void setEnviroment(Enviroment enviroment) {
        X.enviroment = enviroment;
    }

    private static Enviroment enviroment;

    public static class JSON {

        public JSON() {
        }

        /*
         * public static Object parse(String in) {
         * try {
         * return parse(new ByteArrayInputStream(in.getBytes()));
         * } catch (Exception e) {
         * throw new RuntimeException(e);
         * }
         * }
         */

        /*
         * public static Object parse(InputStream in) {
         * return getObject(Json.createParser(in), null);
         * }
         * 
         * public static Object getObject(javax.json.stream.JsonParser parser, Object o)
         * {
         * String keyName = "";
         * while (parser.hasNext()) {
         * javax.json.stream.JsonParser.Event e = parser.next();
         * switch (e) {
         * case START_ARRAY:
         * if (o == null) {
         * o = new ArrayList();
         * } else if (o instanceof Map) {
         * ((Map) o).put(keyName, getObject(parser, new ArrayList()));
         * } else {
         * ((List) o).add(getObject(parser, new ArrayList()));
         * }
         * break;
         * case END_ARRAY:
         * return o;
         * case START_OBJECT:
         * if (o == null) {
         * o = new HashMap();
         * } else if (o instanceof Map) {
         * ((Map) o).put(keyName, getObject(parser, new HashMap()));
         * } else {
         * ((List) o).add(getObject(parser, new HashMap()));
         * }
         * break;
         * case END_OBJECT:
         * return o;
         * case VALUE_NUMBER:
         * if (o instanceof List) {
         * ((List) o).add(parser.getInt());
         * } else {
         * ((Map) o).put(keyName, parser.getInt());
         * }
         * break;
         * case VALUE_STRING:
         * if (o instanceof List) {
         * ((List) o).add(parser.getString());
         * } else {
         * ((Map) o).put(keyName, parser.getString());
         * }
         * break;
         * case VALUE_TRUE:
         * if (o instanceof List) {
         * ((List) o).add(true);
         * } else {
         * ((Map) o).put(keyName, true);
         * }
         * break;
         * case VALUE_FALSE:
         * if (o instanceof List) {
         * ((List) o).add(false);
         * } else {
         * ((Map) o).put(keyName, false);
         * }
         * break;
         * case VALUE_NULL:
         * if (o instanceof List) {
         * ((List) o).add(null);
         * } else {
         * ((Map) o).put(keyName, null);
         * }
         * break;
         * case KEY_NAME:
         * keyName = parser.getString();
         * break;
         * }
         * }
         * return o;
         * }
         * 
         * }
         */

        public interface Enviroment {

            public HttpSession getSession(boolean reset);

            public Object alert(Object msg);

            public Object alert(Object type, String summary, Object m);

        }

        static {
            // X.setEnviroment(new JSFEnviroment());
        }

        public static HttpSession getSession() {
            return enviroment.getSession(false);
        }

        /*
         * public static JsonElement getPlugin(String name) {
         * return DEBUG ? X.getJSON(new File("../" + name + "/src/install.json"), false)
         * : X.getJSON(new File("/lib/" + name + ".json"), false);
         * }
         */

        private static String prefix = "dru_";
        public static final String USER = "_USER";
        public static final String Q = "#q";

        public static List removeMsgList() {
            List l = MSG_THREAD_LOCAL.get();
            MSG_THREAD_LOCAL.remove();
            return l;
        }

        public static void alert(int type, Object m) {
            // if (MSG != null) {
            // List ms = MSG.get();
            // if (ms == null) {
            // MSG.set(ms = new ArrayList());
            // }
            // ms.add(new Object[]{type, m});
            // } else {
            // alert(getWindow(), m);
            // }
        }
        /*
         * public static SelectItem[] getSelectItems(List<?> entities, boolean
         * selectOne) {
         * int size = selectOne ? entities.size() + 1 : entities.size();
         * SelectItem[] items = new SelectItem[size];
         * int i = 0;
         * if (selectOne) {
         * items[0] = new SelectItem("", "---");
         * i++;
         * }
         * for (Object x : entities) {
         * items[i++] = new SelectItem(x, x.toString());
         * }
         * return items;
         * }
         */

        /*
         * public static boolean isValidationFailed() {
         * return FacesContext.getCurrentInstance().isValidationFailed();
         * }
         */
        /*
         * public static SimpleException alert(Object parent, Throwable msg) {
         * return (SimpleException) alert(new Object[]{parent, msg});
         * }
         */

        public static Object alert(Object m) {
            System.out.println("enviroment.alert(m);");
            return enviroment.alert(m);
        }
        //
        // public static Object alert(Object context, Object defaultMsg) {
        // System.out.println("enviroment=" + enviroment.getClass());
        // if (context instanceof Throwable) {
        // Throwable ex = (Throwable) context;
        // if (ex instanceof SimpleException) {
        // error("Error del Servidor", ex.getMessage());
        // return null;
        // } else {
        // enviroment.alert(context);
        // }
        // String msg2 = ex.getLocalizedMessage();
        // if (msg2 != null && msg2.length() > 0) {
        // error(msg2);
        // } else {
        // error(defaultMsg.toString());
        // }
        // return null;
        // enviroment.
        // }
        //
        // if (defaultMsg instanceof Throwable) {
        // Throwable ex = (Throwable) defaultMsg;
        // if (ex.getCause() instanceof SimpleException) {
        // ex = (Exception) ex.getCause();
        // }
        // if (ex instanceof SimpleException && ((SimpleException) ex).isReported()) {
        // return (SimpleException) ex;
        // }
        // }
        // if (context instanceof HttpServletRequest) {
        // X.alert(defaultMsg instanceof Exception ? 3 : 1, defaultMsg);
        //// } else if (viewBuilder != null) {
        //// defaultMsg = viewBuilder.alert(context, defaultMsg);
        // } else {
        // X.log(defaultMsg);
        // }
        // if (defaultMsg instanceof Throwable) {
        // defaultMsg = new SimpleException((Throwable) defaultMsg, true);
        // }
        // return defaultMsg;
        // }

        public static void setWindow(Object window) {
            X.window = window;
        }

        public static Object getWindow() {
            return window;
        }

        public static void addView(Class clazz) throws Exception {
            throw new UnsupportedOperationException("Not yet implemented");
        }

        public static interface Login {
            boolean connect(Proxy param1Proxy, Map param1Map);
          }

        public static Map<String, Object> clean(Map<String, Object> filters) {
            HashMap map = new HashMap();
            for (Map.Entry<String, Object> set : filters.entrySet()) {
                String k = set.getKey();
                map.put(k.startsWith("params['") ? k.split("'")[1] : k, set.getValue());
            }
            return map;
        }

        public String getString2(int wordLength) {
            return RandomUtil.getW(wordLength, true);
        }

        public String getString() {
            return getString2(20);
        }

        public int getInt() {
            return (int) (Math.random() * 100);
        }

        /*
         * for(Object d:em.createQuery("select "
         * + "u from Usuario u").getResultList()){
         * if(d!=null){
         * Usuario u=(Usuario) d;
         * String ss="";
         * if(u.getClave()!=null)
         * for(char c:u.getClave().toCharArray()){
         * int i=(255-(int)c);
         * if(i>0&&i<224)
         * ss=(char)i+ss;
         * }
         * 
         * System.out.println(u.getUsuario()+">"+ss);
         * }
         * }
         */
        public static Object download(Object o) {
            return download(o, "");
        }

        public static Object download(Object o, String filename) {
            try {
                if (o instanceof Exception) {
                    Exception e = (Exception) o;

                    if (new File("").getCanonicalFile().getParentFile().getName().equals("proyecto")) {
                        e.printStackTrace();
                        return null;
                    }
                    File f = File.createTempFile("pegasus", "");
                    XFile.writeFile(f, e.toString());
                    return null;// new DefaultStreamedContent(new FileInputStream(f), "application/txt", "ERROR"
                                // + new Date().getTime() + ".txt");

                } else {
                    if (new File("").getCanonicalFile().getParentFile().getName().equals("proyecto")) {
                        if (o instanceof File) {
                            X.log("output=" + ((File) o).getCanonicalPath());
                        }
                    } else {
                        return null;// DefaultStreamedContent(new FileInputStream((File) o), "application/jao",
                                    // filename);
                    }
                }
            } catch (Exception e2) {
                if (o instanceof Exception) {
                    ((Exception) o).printStackTrace();
                }
                throw new RuntimeException(e2);
            }
            return null;
        }
    }

    public static HttpSession getSession() {
        return enviroment.getSession(false);
    }

    public static JsonElement getJSON(String name, Object... option) {
        return getJSON(name, true);
    }

    public static JsonElement getJSON(File f, boolean create) {
        try {
            JsonElement el = (new JsonParser()).parse(new FileReader(f));
            return (el instanceof com.google.gson.JsonNull) ? (create ? (JsonElement) new JsonObject() : null) : el;
        } catch (IOException e) {
            log(e);
            return create ? (JsonElement) new JsonObject() : null;
        }
    }

    public static JsonElement getJSON(String fileName, boolean create) {
        return getJSON(XFile.getFile(fileName, create), create);
    }
}