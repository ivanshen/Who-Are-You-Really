package org.jfree.chart.util;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class ResourceBundleWrapper {
    private static URLClassLoader noCodeBaseClassLoader;

    private ResourceBundleWrapper() {
    }

    public static void removeCodeBase(URL codeBase, URLClassLoader urlClassLoader) {
        List urlsNoBase = new ArrayList();
        URL[] urls = urlClassLoader.getURLs();
        for (int i = 0; i < urls.length; i++) {
            if (!urls[i].sameFile(codeBase)) {
                urlsNoBase.add(urls[i]);
            }
        }
        noCodeBaseClassLoader = URLClassLoader.newInstance((URL[]) urlsNoBase.toArray(new URL[0]));
    }

    public static ResourceBundle getBundle(String baseName) {
        if (noCodeBaseClassLoader != null) {
            return ResourceBundle.getBundle(baseName, Locale.getDefault(), noCodeBaseClassLoader);
        }
        return ResourceBundle.getBundle(baseName);
    }

    public static ResourceBundle getBundle(String baseName, Locale locale) {
        if (noCodeBaseClassLoader != null) {
            return ResourceBundle.getBundle(baseName, locale, noCodeBaseClassLoader);
        }
        return ResourceBundle.getBundle(baseName, locale);
    }

    public static ResourceBundle getBundle(String baseName, Locale locale, ClassLoader loader) {
        return ResourceBundle.getBundle(baseName, locale, loader);
    }
}
