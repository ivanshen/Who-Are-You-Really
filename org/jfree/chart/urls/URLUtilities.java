package org.jfree.chart.urls;

import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;

public class URLUtilities {
    private static final Class[] STRING_ARGS_2;

    static {
        STRING_ARGS_2 = new Class[]{String.class, String.class};
    }

    public static String encode(String s, String encoding) {
        String result = null;
        try {
            try {
                return (String) URLEncoder.class.getDeclaredMethod("encode", STRING_ARGS_2).invoke(null, new Object[]{s, encoding});
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                return result;
            } catch (IllegalAccessException e2) {
                e2.printStackTrace();
                return result;
            }
        } catch (NoSuchMethodException e3) {
            return URLEncoder.encode(s);
        }
    }
}
