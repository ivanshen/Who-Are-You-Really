package org.jfree.util;

import java.util.Enumeration;
import java.util.Iterator;

public class ExtendedConfigurationWrapper implements ExtendedConfiguration {
    private Configuration parent;

    public ExtendedConfigurationWrapper(Configuration parent) {
        if (parent == null) {
            throw new NullPointerException("Parent given must not be null");
        }
        this.parent = parent;
    }

    public boolean getBoolProperty(String name) {
        return getBoolProperty(name, false);
    }

    public boolean getBoolProperty(String name, boolean defaultValue) {
        return "true".equals(this.parent.getConfigProperty(name, String.valueOf(defaultValue)));
    }

    public int getIntProperty(String name) {
        return getIntProperty(name, 0);
    }

    public int getIntProperty(String name, int defaultValue) {
        String retval = this.parent.getConfigProperty(name);
        if (retval != null) {
            try {
                defaultValue = Integer.parseInt(retval);
            } catch (Exception e) {
            }
        }
        return defaultValue;
    }

    public boolean isPropertySet(String name) {
        return this.parent.getConfigProperty(name) != null;
    }

    public Iterator findPropertyKeys(String prefix) {
        return this.parent.findPropertyKeys(prefix);
    }

    public String getConfigProperty(String key) {
        return this.parent.getConfigProperty(key);
    }

    public String getConfigProperty(String key, String defaultValue) {
        return this.parent.getConfigProperty(key, defaultValue);
    }

    public Enumeration getConfigProperties() {
        return this.parent.getConfigProperties();
    }

    public Object clone() throws CloneNotSupportedException {
        ((ExtendedConfigurationWrapper) super.clone()).parent = (Configuration) this.parent.clone();
        return this.parent;
    }
}
