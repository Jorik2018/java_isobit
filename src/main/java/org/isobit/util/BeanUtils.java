package org.isobit.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.isobit.app.X;
import org.isobit.app.model.User;

import jakarta.ejb.EJB;

public class BeanUtils {

    public static abstract class FieldAdapter {

        public void adapte(Field f, Object v) {

        }
    }

    public static <T extends Object> Collection<T> getObject(Class<T> cls, Object[] o) {
        ArrayList l=new ArrayList();
        for (Object v :  o) {
            if (cls.isAssignableFrom(v.getClass())) {
                l.add(v);
            } else {
                l.add(getObject(cls, v));
            }
        }
        return l;
    }
    
    public static <T extends Object> Collection<T> getObject(Class<T> cls, Collection o) {
        ArrayList l=new ArrayList();
        for (Object v : o) {
            if (cls.isAssignableFrom(v.getClass())) {
                l.add(v);
            } else {
                l.add(getObject(cls, v));
            }
        }
        return l;
    }

    public static <T extends Object> T getObject(Class<T> cls, Object o) {
        return getObject(cls, o, null);
    }

    public static <T extends Object> T getObject(Class<T> cls, Object o, FieldAdapter fd) {
        if (o == null || cls.isAssignableFrom(o.getClass())) {
            return (T) o;
        } else {
            T no = null;
            Map mo = (Map) o;
            try {
                no = cls.newInstance();
                for (Field fie : cls.getDeclaredFields()) {
                    if (mo.containsKey(fie.getName())) {
                        fie.setAccessible(true);
                        Object v = mo.get(fie.getName());
                        Class typeCls = fie.getType();

                        if (v instanceof Map) {
                            //Si el valor es un mapa es posible que el destino sea un objeto no primitivo
                            if (!typeCls.isPrimitive()) {
                                if (Object.class.equals(typeCls)) {
                                    fie.set(no, v);
                                } else {
                                    fie.set(no, getObject(typeCls, v));
                                }
                            }
                        } else if (v == null || typeCls.isAssignableFrom(v.getClass())) {
                            fie.set(no, v);
                        } else if (fd != null) {
                            fd.adapte(fie, v);
                        }
                    }
                }
            } catch (Exception x) {
                throw new RuntimeException(x);
            }
            return no;
        }
    }
//private <T extends Object> T getObject(Class<T> cls, Object o)

    private static HashMap beanContainer = new HashMap();

    public static Object initialize(Object o) {

        Class cls = o.getClass();
        List<Field> fieldList = new ArrayList();
        fieldList.addAll(Arrays.asList(cls.getDeclaredFields()));
        fieldList.addAll(Arrays.asList(cls.getSuperclass().getDeclaredFields()));
        for (Field field : fieldList) {
            if (field.getType().toString().endsWith("jakarta.persistence.EntityManager")) {
                try {
//                    String.format("%02", args)
                    field.setAccessible(true);
                    for (Annotation an : field.getAnnotations()) {
                        //field.set(o, JPA.getInstance().getEntityManager(
                          //      ((jakarta.persistence.PersistenceContext) an).unitName()
                        //));
                    }

                } catch (Exception ex/*IllegalArgumentException  | IllegalAccessException ex*/) {
                    throw new RuntimeException(ex);
                }
            } else {
                for (Annotation an : field.getAnnotations()) {
                    if (an instanceof EJB) {
                        try {
                            Object no = beanContainer.get(field.getType());
                            if (no == null) {
                                if (field.getType().isInterface()) {
                                    try {
                                        no = Class.forName(field.getType().getCanonicalName().replace("Local", "")).newInstance();
                                    } catch (ClassNotFoundException cs) {
                                        no = Class.forName(field.getType().getCanonicalName() + "Impl").newInstance();
                                    }
                                } else {
                                    no = field.getType().newInstance();
                                }
                                beanContainer.put(field.getType(), no);
                                initialize(no);
                            }
                            field.setAccessible(true);
                            field.set(o, no);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            throw new RuntimeException(ex);
                        }
                    }
                }
            }
        }
        return o;
    }

    private BeanUtils() {
    }

    public static Object inject(Object o, Object value, Object... v) {
        for (Field f : o.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            try {
                if (f.getType().isInstance(value)) {
                    f.set(o, value);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        for (Field f : o.getClass().getSuperclass().getDeclaredFields()) {
            f.setAccessible(true);
            try {
                if (f.getType().isInstance(value)) {
                    f.set(o, value);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        for (Object valu : v) {
            for (Field f : o.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                try {
                    if (f.getType().isInstance(valu)) {
                        f.set(o, valu);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            for (Field f : o.getClass().getSuperclass().getDeclaredFields()) {
                f.setAccessible(true);
                try {
                    if (f.getType().isInstance(valu)) {
                        f.set(o, valu);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return o;
    }

    public static void inject(Object o, String property, Object value) {
        for (Field f : o.getClass().getSuperclass().getDeclaredFields()) {
            f.setAccessible(true);
            try {
                if (f.toString().contains(property)) {
                    f.set(o, value);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static <T extends Object> T clone(T o) {
        try {
            Class cls = o.getClass();
            Object no = cls.newInstance();
            for (Method m : cls.getMethods()) {
                try {
                    if (m.getName().startsWith("set")) {
                        m.invoke(no, cls.getMethod(m.getName().replaceFirst("set", "get")).invoke(o));
                    }
                } catch (Exception ex) {
                }
            }
            return (T) no;
        } catch (Exception ex) {
            new RuntimeException(ex);
        }
        return o;
    }

    public static Method getReadMethod(Class clazz, String propertyName) {
        Method readMethod = null;
        String base = XUtil.capitalize(propertyName, 1);
        // Since there can be multiple setter methods but only one getter
        // method, find the getter method first so that you know what the
        // property type is. For booleans, there can be "is" and "get"
        // methods. If an "is" method exists, this is the official
        // reader method so look for this one first.
        try {
            readMethod = clazz.getMethod("is" + base);
        } catch (Exception getterExc) {
            try {
                // no "is" method, so look for a "get" method.
                readMethod = clazz.getMethod("get" + base);
            } catch (Exception e) {
                // no is and no get, we will return null
            }
        }
        return readMethod;
    }

    public static Method getWriteMethod(Class clazz, String propertyName, Class propertyType) {
        try {
            return clazz.getMethod("set" + XUtil.capitalize(propertyName, 1), new Class[]{propertyType});
        } catch (Exception e) {
            return null;
        }
    }

    public static interface Testable {

        public void test();

    }

    public static void start(Testable t) {
        try {
            X.DEBUG = true;
            /*Session s = new Session();
            User user = new User(1, "ADMIN");
            s.put(X.USER, user);
            X.session.set(s);
            t.test();*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

}
