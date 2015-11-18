package org.jfree.base.modules;

public class DefaultModuleInfo implements ModuleInfo {
    private String majorVersion;
    private String minorVersion;
    private String moduleClass;
    private String patchLevel;

    public DefaultModuleInfo(String moduleClass, String majorVersion, String minorVersion, String patchLevel) {
        if (moduleClass == null) {
            throw new NullPointerException("Module class must not be null.");
        }
        this.moduleClass = moduleClass;
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.patchLevel = patchLevel;
    }

    public String getModuleClass() {
        return this.moduleClass;
    }

    public void setModuleClass(String moduleClass) {
        if (moduleClass == null) {
            throw new NullPointerException();
        }
        this.moduleClass = moduleClass;
    }

    public String getMajorVersion() {
        return this.majorVersion;
    }

    public void setMajorVersion(String majorVersion) {
        this.majorVersion = majorVersion;
    }

    public String getMinorVersion() {
        return this.minorVersion;
    }

    public void setMinorVersion(String minorVersion) {
        this.minorVersion = minorVersion;
    }

    public String getPatchLevel() {
        return this.patchLevel;
    }

    public void setPatchLevel(String patchLevel) {
        this.patchLevel = patchLevel;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DefaultModuleInfo)) {
            return false;
        }
        if (this.moduleClass.equals(((ModuleInfo) o).getModuleClass())) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.moduleClass.hashCode();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getClass().getName());
        buffer.append("={ModuleClass=");
        buffer.append(getModuleClass());
        if (getMajorVersion() != null) {
            buffer.append("; Version=");
            buffer.append(getMajorVersion());
            if (getMinorVersion() != null) {
                buffer.append("-");
                buffer.append(getMinorVersion());
                if (getPatchLevel() != null) {
                    buffer.append("_");
                    buffer.append(getPatchLevel());
                }
            }
        }
        buffer.append("}");
        return buffer.toString();
    }
}
