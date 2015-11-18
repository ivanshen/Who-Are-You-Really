package org.jfree.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

public final class ObjectUtilities {
    public static final String CLASS_CONTEXT = "ClassContext";
    public static final String THREAD_CONTEXT = "ThreadContext";
    private static ClassLoader classLoader;
    private static String classLoaderSource;

    static {
        classLoaderSource = THREAD_CONTEXT;
    }

    private ObjectUtilities() {
    }

    public static String getClassLoaderSource() {
        return classLoaderSource;
    }

    public static void setClassLoaderSource(String classLoaderSource) {
        classLoaderSource = classLoaderSource;
    }

    public static boolean equal(Object o1, Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 != null) {
            return o1.equals(o2);
        }
        return false;
    }

    public static int hashCode(Object object) {
        if (object != null) {
            return object.hashCode();
        }
        return 0;
    }

    public static Object clone(Object object) throws CloneNotSupportedException {
        if (object == null) {
            throw new IllegalArgumentException("Null 'object' argument.");
        } else if (object instanceof PublicCloneable) {
            return ((PublicCloneable) object).clone();
        } else {
            try {
                Method method = object.getClass().getMethod("clone", (Class[]) null);
                if (Modifier.isPublic(method.getModifiers())) {
                    return method.invoke(object, (Object[]) null);
                }
            } catch (NoSuchMethodException e) {
                Log.warn("Object without clone() method is impossible.");
            } catch (IllegalAccessException e2) {
                Log.warn("Object.clone(): unable to call method.");
            } catch (InvocationTargetException e3) {
                Log.warn("Object without clone() method is impossible.");
            }
            throw new CloneNotSupportedException("Failed to clone.");
        }
    }

    public static Collection deepClone(Collection collection) throws CloneNotSupportedException {
        if (collection == null) {
            throw new IllegalArgumentException("Null 'collection' argument.");
        }
        Collection result = (Collection) clone(collection);
        result.clear();
        for (Object item : collection) {
            if (item != null) {
                result.add(clone(item));
            } else {
                result.add(null);
            }
        }
        return result;
    }

    public static synchronized void setClassLoader(ClassLoader classLoader) {
        synchronized (ObjectUtilities.class) {
            classLoader = classLoader;
        }
    }

    public static ClassLoader getClassLoader() {
        return classLoader;
    }

    public static ClassLoader getClassLoader(Class c) {
        synchronized (ObjectUtilities.class) {
            if (classLoader != null) {
                ClassLoader classLoader = classLoader;
                return classLoader;
            }
            String localClassLoaderSource = classLoaderSource;
            if (THREAD_CONTEXT.equals(localClassLoaderSource)) {
                classLoader = Thread.currentThread().getContextClassLoader();
                if (classLoader != null) {
                    return classLoader;
                }
            }
            ClassLoader applicationCL = c.getClassLoader();
            if (applicationCL == null) {
                return ClassLoader.getSystemClassLoader();
            }
            return applicationCL;
        }
    }

    public static URL getResource(String name, Class c) {
        ClassLoader cl = getClassLoader(c);
        if (cl == null) {
            return null;
        }
        return cl.getResource(name);
    }

    public static URL getResourceRelative(String name, Class c) {
        ClassLoader cl = getClassLoader(c);
        String cname = convertName(name, c);
        if (cl == null) {
            return null;
        }
        return cl.getResource(cname);
    }

    private static String convertName(String name, Class c) {
        if (name.startsWith("/")) {
            return name.substring(1);
        }
        while (c.isArray()) {
            c = c.getComponentType();
        }
        String baseName = c.getName();
        int index = baseName.lastIndexOf(46);
        if (index == -1) {
            return name;
        }
        return baseName.substring(0, index).replace('.', '/') + "/" + name;
    }

    public static InputStream getResourceAsStream(String name, Class context) {
        InputStream inputStream = null;
        URL url = getResource(name, context);
        if (url != null) {
            try {
                inputStream = url.openStream();
            } catch (IOException e) {
            }
        }
        return inputStream;
    }

    public static InputStream getResourceRelativeAsStream(String name, Class context) {
        InputStream inputStream = null;
        URL url = getResourceRelative(name, context);
        if (url != null) {
            try {
                inputStream = url.openStream();
            } catch (IOException e) {
            }
        }
        return inputStream;
    }

    public static Object loadAndInstantiate(String className, Class source) {
        try {
            return getClassLoader(source).loadClass(className).newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    public static Object loadAndInstantiate(String className, Class source, Class type) {
        Object obj = null;
        try {
            Class c = getClassLoader(source).loadClass(className);
            if (type.isAssignableFrom(c)) {
                obj = c.newInstance();
            }
        } catch (Exception e) {
        }
        return obj;
    }

    public static boolean isJDK14() {
        boolean z = true;
        try {
            ClassLoader loader = getClassLoader(ObjectUtilities.class);
            if (loader != null) {
                try {
                    loader.loadClass("java.util.RandomAccess");
                    return true;
                } catch (ClassNotFoundException e) {
                    return false;
                } catch (Exception e2) {
                }
            }
        } catch (Exception e3) {
        }
        try {
            String version = System.getProperty("java.vm.specification.version");
            if (version == null) {
                return false;
            }
            if (ArrayUtilities.compareVersionArrays(parseVersions(version), new String[]{"1", "4"}) < 0) {
                z = false;
            }
            return z;
        } catch (Exception e4) {
            return false;
        }
    }

    private static String[] parseVersions(String version) {
        if (version == null) {
            return new String[0];
        }
        ArrayList versions = new ArrayList();
        StringTokenizer strtok = new StringTokenizer(version, ".");
        while (strtok.hasMoreTokens()) {
            versions.add(strtok.nextToken());
        }
        return (String[]) versions.toArray(new String[versions.size()]);
    }
}
