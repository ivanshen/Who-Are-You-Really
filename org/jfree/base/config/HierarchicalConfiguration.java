package org.jfree.base.config;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.TreeSet;
import org.jfree.util.Configuration;
import org.jfree.util.PublicCloneable;

public class HierarchicalConfiguration implements ModifiableConfiguration, PublicCloneable {
    private Properties configuration;
    private transient Configuration parentConfiguration;

    public HierarchicalConfiguration() {
        this.configuration = new Properties();
    }

    public HierarchicalConfiguration(Configuration parentConfiguration) {
        this();
        this.parentConfiguration = parentConfiguration;
    }

    public String getConfigProperty(String key) {
        return getConfigProperty(key, null);
    }

    public String getConfigProperty(String key, String defaultValue) {
        String value = this.configuration.getProperty(key);
        if (value != null) {
            return value;
        }
        if (isRootConfig()) {
            return defaultValue;
        }
        return this.parentConfiguration.getConfigProperty(key, defaultValue);
    }

    public void setConfigProperty(String key, String value) {
        if (key == null) {
            throw new NullPointerException();
        } else if (value == null) {
            this.configuration.remove(key);
        } else {
            this.configuration.setProperty(key, value);
        }
    }

    private boolean isRootConfig() {
        return this.parentConfiguration == null;
    }

    public boolean isLocallyDefined(String key) {
        return this.configuration.containsKey(key);
    }

    protected Properties getConfiguration() {
        return this.configuration;
    }

    public void insertConfiguration(HierarchicalConfiguration config) {
        config.setParentConfig(getParentConfig());
        setParentConfig(config);
    }

    protected void setParentConfig(Configuration config) {
        if (this.parentConfiguration == this) {
            throw new IllegalArgumentException("Cannot add myself as parent configuration.");
        }
        this.parentConfiguration = config;
    }

    protected Configuration getParentConfig() {
        return this.parentConfiguration;
    }

    public Enumeration getConfigProperties() {
        return this.configuration.keys();
    }

    public Iterator findPropertyKeys(String prefix) {
        TreeSet keys = new TreeSet();
        collectPropertyKeys(prefix, this, keys);
        return Collections.unmodifiableSet(keys).iterator();
    }

    private void collectPropertyKeys(String prefix, Configuration config, TreeSet collector) {
        Enumeration enum1 = config.getConfigProperties();
        while (enum1.hasMoreElements()) {
            String key = (String) enum1.nextElement();
            if (key.startsWith(prefix) && !collector.contains(key)) {
                collector.add(key);
            }
        }
        if (config instanceof HierarchicalConfiguration) {
            HierarchicalConfiguration hconfig = (HierarchicalConfiguration) config;
            if (hconfig.parentConfiguration != null) {
                collectPropertyKeys(prefix, hconfig.parentConfiguration, collector);
            }
        }
    }

    protected boolean isParentSaved() {
        return true;
    }

    protected void configurationLoaded() {
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        if (isParentSaved()) {
            out.writeBoolean(true);
            out.writeObject(this.parentConfiguration);
            return;
        }
        out.writeBoolean(false);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (in.readBoolean()) {
            this.parentConfiguration = (ModifiableConfiguration) in.readObject();
        } else {
            this.parentConfiguration = null;
        }
        configurationLoaded();
    }

    public Object clone() throws CloneNotSupportedException {
        HierarchicalConfiguration config = (HierarchicalConfiguration) super.clone();
        config.configuration = (Properties) this.configuration.clone();
        return config;
    }
}
