package org.jfree.util;

public interface ExtendedConfiguration extends Configuration {
    boolean getBoolProperty(String str);

    boolean getBoolProperty(String str, boolean z);

    int getIntProperty(String str);

    int getIntProperty(String str, int i);

    boolean isPropertySet(String str);
}
