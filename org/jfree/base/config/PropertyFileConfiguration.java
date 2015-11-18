package org.jfree.base.config;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.jfree.util.Log;
import org.jfree.util.ObjectUtilities;

public class PropertyFileConfiguration extends HierarchicalConfiguration {
    public void load(String resourceName) {
        load(resourceName, PropertyFileConfiguration.class);
    }

    public void load(String resourceName, Class resourceSource) {
        InputStream in = ObjectUtilities.getResourceRelativeAsStream(resourceName, resourceSource);
        if (in != null) {
            try {
                load(in);
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        } else {
            Log.debug("Configuration file not found in the classpath: " + resourceName);
        }
    }

    public void load(InputStream in) {
        if (in == null) {
            throw new NullPointerException();
        }
        try {
            BufferedInputStream bin = new BufferedInputStream(in);
            Properties p = new Properties();
            p.load(bin);
            getConfiguration().putAll(p);
            bin.close();
        } catch (IOException ioe) {
            Log.warn("Unable to read configuration", ioe);
        }
    }
}
