package org.isobit.util;

//import com.google.gson.GsonBuilder;
import java.awt.event.KeyEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.table.TableModel;
import org.isobit.app.X;

public class XUtil {

    public static final String ACTION_DELIMETER = "+";

    private static DecimalFormat oneDigitFormat;
    private static DecimalFormat twoDigitFormat;

    public static boolean equals(String s1, String s2) {
        return s1.equalsIgnoreCase(s2);
    }

    public static double doubleValue(Object value) {
        try {
            return Double.parseDouble(value + "");
        } catch (Exception ex) {
        }
        return 0;
    }

    public static boolean isEmpty(List value) {
        return value == null || value.size() == 0;
    }

    public String getParameter(String data, String key) {
        for (String s : data.split(",")) {
            String ss[] = s.split(":");
            if (ss[0].equals(key)) {
                return ss[1];
            }
        }
        return null;
    }

    public static boolean isEmpty(TableModel value) {
        return value == null || value.getRowCount() == 0;
    }

    public static boolean isEmpty(Object[] value) {
        return value == null || value.length == 0;
    }

    public static boolean isEmpty(Object value) {
        return value == null || value.toString().trim().length() == 0;
    }

    public static Object isEmpty(Object value, Object option) {
        if (value == null||(value.getClass().isArray()&&((Object[]) value).length == 0)||(value instanceof Collection&&((Collection) value).isEmpty())) {
            return option;
        }
        return value.toString().trim().length() == 0 ? option : value;
    }

    public static String toPlural(String word) {
        if (word.endsWith("s")) {
            return word;
        }
        return word += word.matches(".*[aAeEiIoOuU]") ? "S" : "ES";
    }

    public static String implode(Object[] values) {
        return implode(values, ',');
    }

    public static String implode(Object[] values, Object delimiter) {
        return values!=null?implode(values, delimiter, values.length):"";
    }

    public static String implode(Object[] values, Object delimiter, int limit) {
        StringBuilder builder = new StringBuilder(values.length > 0 && values[0] != null ? values[0].toString() : "");
        for (int i = 1; i < limit; i++) {
            if (values[i] != null) {
                builder.append(delimiter).append(values[i]);
            }
        }
        return builder.toString();
    }

    public static String implode(List list, Object delimiter) {
        if (list==null||list.isEmpty()) {
            return null;
        }
        Object first = list.remove(0);
        StringBuilder builder = new StringBuilder(first != null ? first.toString() : "");
        for (Object o : list) {
            if (o != null) {
                builder.append(delimiter).append(o);
            }
        }
        list.add(0, first);
        return builder.toString();
    }

    public static Object implode(List list) {
        return implode(list, ',');
    }

    public static Stack getAllSuperClass(Object el, Class maxSuperClazz) {
        return getAllSuperClass(el.getClass(), maxSuperClazz);
    }

    public static Stack getAllSuperClass(Class cls, Class maxSuperClazz) {
        Stack<Class> allSuperClass = new Stack();
        if (maxSuperClazz == null) {
            maxSuperClazz = Object.class;
        }
        if (maxSuperClazz.isAssignableFrom(cls)) {
            while (!cls.equals(maxSuperClazz)) {
                allSuperClass.push(cls);
                cls = cls.getSuperclass();
            }
        } else {
            allSuperClass.push(cls);
        }
        return allSuperClass;
    }

    public static BigDecimal toBigDecimal(Object value) {
        try {
            return new BigDecimal(value.toString().trim());
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }

    public static int intValue(Object value) {
        if (value instanceof Tag) {
            value = ((Tag) value).getId();
        }
        try {
            return value instanceof Integer ? (Integer) value : (value instanceof Number ? ((Number) value).intValue()
                    : (int) Double.parseDouble(value + ""));
        } catch (Exception ex) {
            return 0;
        }
    }

    public static String toBinary(String s) {
        StringBuilder binary = new StringBuilder();
        for (byte b : s.getBytes()) {
            int val = b;
            for (int i = 0; i < 8; i++) {
                binary.append((val & 128) == 0 ? 0 : 1);
                val <<= 1;
            }
        }
        return binary.toString();
    }

    public static String print(String string) {
        System.out.println(string);
        return string;
    }

    public static Map merge(Map form, Map map) {
        form.putAll(map);
        return form;
    }

    public static List avg(List<Object[]> data, int o, int d) {
        double avg = 0.0;
        for (Object[] row : data) {
            avg += XUtil.doubleValue(row[o]);
        }
        for (Object[] row : data) {
            row[d] = XUtil.doubleValue(row[o]) / avg;
        }
        return data;
    }

    private XUtil() {
    }

    public static String toString(Object value) {
        if (value instanceof Set) {
            Set iterator = (Set) value;
            String s = "";
            for (Object o : iterator) {
                s += o + ",";
            }
            return s.length() > 0 ? s.substring(0, s.length() - 1) : s;
        } else if (value instanceof List) {
            List list = (List) value;
            if (list.size() > 0) {
                String ids = list.toString();
                return ids.substring(1, ids.length() - 1);
            } else {
                return null;
            }
        } else if (value instanceof Object[]) {
            Object[] list = (Object[]) value;
            if (list.length > 0) {
                StringBuilder sb = new StringBuilder();
                for (Object v : (Object[]) value) {
                    sb.append(v).append(',');
                }
                return sb.deleteCharAt(sb.length() - 1).toString();
            } else {
                return null;
            }
        } else {
            return value != null ? value.toString() : null;
        }
    }

    /**
     * Tests if the specified value contains the specified word as a WHOLE word.
     *
     * @param value the value to test for the word
     * @param word the whole word we are looking for
     * @return <code>true</code> if found, <code>false</code> otherwise
     */
    public static boolean containsWholeWord(String value, String word) {
        int index = value.indexOf(word);
        if (index == -1) {
            return false;
        }
        int valueLength = value.length();
        int wordLength = word.length();
        int indexLength = index + wordLength;
        if (indexLength == valueLength) // same word
        {
            return true;
        }
        if (indexLength != valueLength) { // check for embedded word
            if (index > 0) {
                return Character.isWhitespace(value.charAt(indexLength))
                        && Character.isWhitespace(value.charAt(index - 1));
            } else {
                return Character.isWhitespace(value.charAt(indexLength));
            }
        } else {
            return true;
        }
    }

    public static String getExceptionName(Throwable e) {
        String exceptionName = "";
        if (e.getCause() != null) {
            Throwable _e = e.getCause();
            exceptionName = _e.getClass().getName();
        } else {
            exceptionName = e.getClass().getName();
        }

        int index = exceptionName.lastIndexOf('.');
        if (index != -1) {
            exceptionName = exceptionName.substring(index + 1);
        }
        return exceptionName;
    }

    public static String[] capitalize(String[] value) {
        for (int i = 0; i < value.length; i++) {
            value[i] = capitalize(value[i]);
        }
        return value;
    }

    public static String capitalize(String value) {
        return value != null ? capitalize(value, value.length()) : null;
    }

    public static String capitalize(String value, int limit) {
        boolean nextUpper = false;
        char[] chars = value.toCharArray();
        //StringBuilder sb = new StringBuilder(chars.length);
        limit = Math.min(limit, chars.length);
        for (int i = 0; i < limit; i++) {
            if (i == 0 || nextUpper) {
                chars[i] = Character.toUpperCase(chars[i]);
                nextUpper = false;
                continue;
            }
            if (Character.isWhitespace(chars[i])) {
                nextUpper = true;
                //sb.append(chars[i]);
                continue;
            }
            chars[i] = Character.toLowerCase(chars[i]);
            nextUpper = false;
        }
        return String.valueOf(chars);//sb.toString();
    }

    /*public static String capitalize(String s) {
     if (s.length() == 0) {
     return s;
     }else{
     char chars[] = s.toCharArray();
     chars[0] = Character.toUpperCase(chars[0]);
     return String.valueOf(chars);
     }
     }*/

 /*public static String upperFirstLetter(String value) {

     return value.substring(0, 1).toUpperCase()+value.substring(1);
     }*/
    /**
     * Formats the specified the SQL exception object displaying the error
     * message, error code and the SQL state code.
     *
     * @param e - the SQL exception
     */
    public static String formatSQLError(SQLException e) {
        if (e == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(e.getMessage());
        sb.append("\nError Code: ").append(e.getErrorCode());
        String state = e.getSQLState();
        if (state != null) {
            sb.append("\nSQL State Code: ").append(state);
        }
        sb.append("\n");
        return sb.toString();
    }

    /**
     * Returns a <code>String</code> array of the the CSV value specified with
     * the specfied delimiter.
     *
     * @param csvString the CSV value
     * @param delim the delimiter used in the CSV value
     * @return an array of split values
     */
    public static String[] splitSeparatedValues(String csvString, String delim) {
        StringTokenizer st = new StringTokenizer(csvString, delim);
        ArrayList list = new ArrayList(st.countTokens());
        while (st.hasMoreTokens()) {
            list.add(st.nextToken());
        }
        String[] values = (String[]) list.toArray(new String[list.size()]);
        return values;
    }

    public static boolean containsValue(String[] values, String value) {
        for (int i = 0; i < values.length; i++) {
            if (values[i].compareTo(value) == 0) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidNumber(String number) {
        char[] chars = number.toCharArray();

        for (int i = 0; i < chars.length; i++) {

            if (!Character.isDigit(chars[i])) {
                return false;
            }

        }

        return true;

    }

    public static String getClassName(String path) {
        int index = path.indexOf(".class");

        if (index == -1) {
            return null;
        }

        char dot = '.';
        char pathSeparator = '/';
        char[] chars = path.toCharArray();
        StringBuilder sb = new StringBuilder(chars.length);

        for (int i = 0; i < chars.length; i++) {

            if (i == index) {
                break;
            }

            if (chars[i] == pathSeparator) {
                sb.append(dot);
            } else {
                sb.append(chars[i]);
            }

        }

        return sb.toString();
    }

    public static String[] findImplementingClasses(
            String interfaceName, String paths) throws MalformedURLException, IOException {
        return findImplementingClasses(interfaceName, paths, true);
    }

    public static String[] findImplementingClasses(
            String interfaceName, String paths, boolean interfaceOnly)
            throws MalformedURLException, IOException {

        URL[] urls = loadURLs(paths);
        URLClassLoader loader = new URLClassLoader(
                urls, ClassLoader.getSystemClassLoader());

        JarFile jarFile = null;
        String className = null;
        String[] files = splitSeparatedValues(paths, ";");
        ArrayList clazzes = new ArrayList();

        for (int i = 0; i < files.length; i++) {

            jarFile = new JarFile(files[i]);

            for (Enumeration j = jarFile.entries(); j.hasMoreElements();) {
                ZipEntry entry = (ZipEntry) j.nextElement();
                className = getClassName(entry.getName());

                if (className == null) {
                    continue;
                }

                try {
                    Class clazz = loader.loadClass(className);

                    if (clazz.isInterface()) {
                        continue;
                    }

                    String name = getImplementedClass(clazz, interfaceName);
                    if (name != null) {
                        clazzes.add(className);
                    }

                    if (!interfaceOnly) {
                        Class _clazz = clazz;
                        Class superClazz = null;
                        while ((superClazz = _clazz.getSuperclass()) != null) {
                            name = superClazz.getName();
                            if (interfaceName.compareTo(name) == 0) {
                                clazzes.add(clazz.getName());
                                break;
                            }
                            _clazz = superClazz;
                        }

                    }

                } catch (Exception e) {
                } // ignore and continue

            }

        }
        return (String[]) clazzes.toArray(new String[clazzes.size()]);
    }

    public static String getImplementedClass(Class clazz, String implementation) {
        Class[] interfaces = clazz.getInterfaces();

        for (int k = 0; k < interfaces.length; k++) {
            String interfaceName = interfaces[k].getName();
            if (interfaceName.compareTo(implementation) == 0) {
                return clazz.getName();
            }
        }

        Class superClazz = clazz.getSuperclass();

        if (superClazz != null) {
            return getImplementedClass(superClazz, implementation);
        }

        return null;
    }

    public static URL[] loadURLs(String paths) throws MalformedURLException {
        String token = ";";
        ArrayList pathsVector = new ArrayList();

        if (paths.indexOf(token) != -1) {
            StringTokenizer st = new StringTokenizer(paths, token);
            while (st.hasMoreTokens()) {
                pathsVector.add(st.nextToken());
            }
        } else {
            pathsVector.add(paths);
        }

        URL[] urls = new URL[pathsVector.size()];
        for (int i = 0; i < urls.length; i++) {
            File f = new File((String) pathsVector.get(i));
            urls[i] = f.toURL();
        }
        return urls;
    }

    public static String format(Number number, String pattern) {
        if (number == null) {
            number = 0;
        }
        NumberFormat nf = NumberFormat.getNumberInstance();
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern(pattern);
        return df.format(number);
    }

    public static String keyStrokeToString(KeyStroke keyStroke) {
        String value = null;
        if (keyStroke != null) {
            int mod = keyStroke.getModifiers();
            value = KeyEvent.getKeyModifiersText(mod);

            if (!XUtil.isEmpty(value)) {
                value += ACTION_DELIMETER;
            }

            String keyText = KeyEvent.getKeyText(keyStroke.getKeyCode());

            if (!XUtil.isEmpty(keyText)) {
                value += keyText;
            }

        }
        return value;
    }

    /**
     * Returns the system properties from <code>System.getProperties()</code> as
     * a 2 dimensional array of key/name.
     */
    public static String[][] getSystemProperties() {
        Properties sysProps = System.getProperties();
        String[] keys = new String[sysProps.size()];

        int count = 0;
        for (Enumeration i = sysProps.propertyNames(); i.hasMoreElements();) {
            keys[count++] = (String) i.nextElement();
        }

        Arrays.sort(keys);
        String[][] properties = new String[keys.length][2];
        for (int i = 0; i < keys.length; i++) {
            properties[i][0] = keys[i];
            properties[i][1] = sysProps.getProperty(keys[i]);
        }
        return properties;
    }

    /**
     * Prints the system properties as [key: name].
     */
    public static void printSystemProperties() {
        String[][] properties = getSystemProperties();
        for (int i = 0; i < properties.length; i++) {
            X.log(properties[i][0] + ": " + properties[i][1]);
        }
    }

    public static void printActionMap(JComponent component) {
        printActionMap(component.getActionMap(), component.getClass().getName());
    }

    public static void printInputMap(JComponent component) {
        printInputMap(component.getInputMap(JComponent.WHEN_FOCUSED),
                "Input map used when focused");
        printInputMap(component.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT),
                "Input map used when ancestor of focused component");
        printInputMap(component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW),
                "Input map used when in focused window");
    }

    public static void printActionMap(ActionMap actionMap, String who) {
        System.out.println("Action map for " + who + ":");
        Object[] keys = actionMap.allKeys();
        if (keys != null) {
            for (int i = 0; i < keys.length; i++) {
                Object key = keys[i];
                Action targetAction = actionMap.get(key);
                System.out.println("\tName: <" + key + ">, action: "
                        + targetAction.getClass().getName());
            }
        }
    }

    public static void printInputMap(InputMap inputMap, String heading) {
        X.log("\n" + heading + ":");
        KeyStroke[] keys = inputMap.allKeys();
        if (keys != null) {
            for (int i = 0; i < keys.length; i++) {
                KeyStroke key = keys[i];
                Object actionName = inputMap.get(key);
                System.out.println("\tKey: <" + key + ">, action name: "
                        + actionName);
            }
        }
    }

    public static String formatDuration(long value) {

        if (twoDigitFormat == null || oneDigitFormat == null) {
            oneDigitFormat = new DecimalFormat("0");
            twoDigitFormat = new DecimalFormat("00");
            //threeDigitFormat = new DecimalFormat("000");
        }

        // {"milliseconds","seconds","minutes","hours"}
        long[] divisors = {1000, 60, 60, 24};
        double[] result = new double[divisors.length];

        for (int i = 0; i < divisors.length; i++) {
            result[i] = value % divisors[i];
            value /= divisors[i];
        }
        /*
         String[] labels = {"milliseconds","seconds","minutes","hours"};
         for(int i = divisors.length-1;i >= 0;i--) {
         System.out.print(" " + result[i] + " " + labels[i]);
         } 
         System.out.println();
         */

        //build "hh:mm:ss.SSS"
        StringBuilder buffer = new StringBuilder(" ");
        buffer.append(oneDigitFormat.format(result[3]));
        buffer.append(':');
        buffer.append(twoDigitFormat.format(result[2]));
        buffer.append(':');
        buffer.append(twoDigitFormat.format(result[1]));
        buffer.append('.');
        buffer.append(twoDigitFormat.format(result[0]));

        return buffer.toString();
    }

    public static boolean booleanValue(Object value) {
        return value instanceof Boolean
                ? (Boolean) value : value instanceof Number
                        ? ((Number) value).intValue() > 0 : value != null && Boolean.parseBoolean(value.toString());
    }

    public static String charsToString(char[] chars) {
        StringBuilder sb = new StringBuilder(chars.length);
        for (int i = 0; i < chars.length; i++) {
            sb.append(chars[i]);
        }
        return sb.toString();
    }

    /**
     * Returns whether the current version of the JVM is at least that specified
     * for major and minor version numbers. For example, with a minium required
     * of 1.4, the major version is 1 and minor is 4.
     *
     * @param major - the major version
     * @param minor - the minor version
     * @return whether the system version is at least major.minor
     */
    public static boolean isMinJavaVersion(int major, int minor) {
        String version = System.getProperty("java.vm.version");
        String installedVersion = version;

        int index = version.indexOf("_");
        if (index > 0) {
            installedVersion = version.substring(0, index);
        }

        String[] installed = splitSeparatedValues(installedVersion, ".");

        // expect to get something like x.x.x - need at least x.x
        if (installed.length < 2) {
            return false;
        }

        // major at position 0
        int _version = Integer.parseInt(installed[0]);
        if (_version < major) {
            return false;
        }

        _version = Integer.parseInt(installed[1]);
        if (_version < minor) {
            return false;
        }

        return true;
    }

    public static byte[] inputStreamToBytes(InputStream is) {
        byte[] retVal = new byte[0];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (is != null) {
            byte[] elementi = new byte[10000];
            int size = 0;
            try {
                while ((size = is.read(elementi)) != -1) {
                    //retVal = addBytes(retVal,elementi,(retVal.length),size);
                    System.out.print(".");
                    baos.write(elementi, 0, size);
                }
                retVal = baos.toByteArray();
            } catch (IOException e) {
                X.log(e);
            } catch (Exception e) {
                X.log(e);
                retVal = new byte[0];
            }
        }
        return retVal;
    }

    public static Object[] insert(Object original[], Object element, int index) {
        int length = original.length;
        Object destination[] = new Object[length + 1];
        System.arraycopy(original, 0, destination, 0, index);
        destination[index] = element;
        System.arraycopy(original, index, destination, index + 1, length - index);
        return destination;
    }

    public static Object[] remove(Object original[], int index) {
        int length = original.length;
        Object destination[] = new Object[length - 1];
        System.arraycopy(original, 0, destination, 0, index);
        System.arraycopy(original, index + 1, destination, index, length - index - 1);
        return destination;
    }

    public static void printJson(Object o) {
        //X.log(new GsonBuilder().setPrettyPrinting().create().toJson(o));
    }

    public static String getAttribute(JarFile jarFile, String attributeName) throws IOException {
        Manifest manifest = jarFile.getManifest();
        Attributes attrs = (Attributes) manifest.getMainAttributes();
        for (Iterator it = attrs.keySet().iterator(); it.hasNext();) {
            Attributes.Name attrName = (Attributes.Name) it.next();
            if (attrName.toString().trim().compareToIgnoreCase(attributeName) == 0) {
                return attrs.getValue(attrName);
            }
        }
        return null;
    }

    private static final HashMap<Object, Class> WrapperMap = new HashMap();

    static {
        WrapperMap.put(int.class, Integer.class);
        WrapperMap.put("int", Integer.class);
        WrapperMap.put("integer", Integer.class);
        WrapperMap.put(double.class, Double.class);
        WrapperMap.put("double", Integer.class);
        WrapperMap.put(short.class, Short.class);
        WrapperMap.put("string", String.class);
    }

    public static Class getWrapper(Class primitiveClass) {
        Class fo = WrapperMap.get(primitiveClass);
        return fo != null ? fo : primitiveClass;
    }

    public static Class getWrapper(Object name) {
        if (name instanceof String) {
            name = ((String) name).toLowerCase();
        }
        Class fo = WrapperMap.get(name);
        return fo != null ? fo : Object.class;
    }

    public static String toRoman(int number) {

        String riman[] = {"M", "XM", "CM", "D", "XD", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        int arab[] = {1000, 990, 900, 500, 490, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (number > 0 || arab.length == (i - 1)) {
            while ((number - arab[i]) >= 0) {
                number -= arab[i];
                result.append(riman[i]);
            }
            i++;
        }
        return result.toString();
    }

}
