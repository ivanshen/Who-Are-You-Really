package org.jfree.base;

public class Library {
    private String info;
    private String licenceName;
    private String name;
    private String version;

    public Library(String name, String version, String licence, String info) {
        this.name = name;
        this.version = version;
        this.licenceName = licence;
        this.info = info;
    }

    protected Library() {
    }

    public String getName() {
        return this.name;
    }

    public String getVersion() {
        return this.version;
    }

    public String getLicenceName() {
        return this.licenceName;
    }

    public String getInfo() {
        return this.info;
    }

    protected void setInfo(String info) {
        this.info = info;
    }

    protected void setLicenceName(String licenceName) {
        this.licenceName = licenceName;
    }

    protected void setName(String name) {
        this.name = name;
    }

    protected void setVersion(String version) {
        this.version = version;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Library library = (Library) o;
        if (this.name != null) {
            if (this.name.equals(library.name)) {
                return true;
            }
        } else if (library.name == null) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.name != null ? this.name.hashCode() : 0;
    }
}
