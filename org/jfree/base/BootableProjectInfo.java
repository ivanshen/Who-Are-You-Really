package org.jfree.base;

import java.util.ArrayList;

public class BootableProjectInfo extends BasicProjectInfo {
    private boolean autoBoot;
    private String bootClass;

    public BootableProjectInfo() {
        this.autoBoot = true;
    }

    public BootableProjectInfo(String name, String version, String licence, String info) {
        this();
        setName(name);
        setVersion(version);
        setLicenceName(licence);
        setInfo(info);
    }

    public BootableProjectInfo(String name, String version, String info, String copyright, String licenceName) {
        this();
        setName(name);
        setVersion(version);
        setLicenceName(licenceName);
        setInfo(info);
        setCopyright(copyright);
    }

    public BootableProjectInfo[] getDependencies() {
        ArrayList dependencies = new ArrayList();
        Library[] libraries = getLibraries();
        for (Library lib : libraries) {
            if (lib instanceof BootableProjectInfo) {
                dependencies.add(lib);
            }
        }
        Library[] optionalLibraries = getOptionalLibraries();
        for (Library lib2 : optionalLibraries) {
            if (lib2 instanceof BootableProjectInfo) {
                dependencies.add(lib2);
            }
        }
        return (BootableProjectInfo[]) dependencies.toArray(new BootableProjectInfo[dependencies.size()]);
    }

    public void addDependency(BootableProjectInfo projectInfo) {
        if (projectInfo == null) {
            throw new NullPointerException();
        }
        addLibrary(projectInfo);
    }

    public String getBootClass() {
        return this.bootClass;
    }

    public void setBootClass(String bootClass) {
        this.bootClass = bootClass;
    }

    public boolean isAutoBoot() {
        return this.autoBoot;
    }

    public void setAutoBoot(boolean autoBoot) {
        this.autoBoot = autoBoot;
    }
}
