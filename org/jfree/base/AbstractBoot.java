package org.jfree.base;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import org.jfree.base.config.HierarchicalConfiguration;
import org.jfree.base.config.PropertyFileConfiguration;
import org.jfree.base.config.SystemPropertyConfiguration;
import org.jfree.base.modules.PackageManager;
import org.jfree.base.modules.SubSystem;
import org.jfree.util.Configuration;
import org.jfree.util.ExtendedConfiguration;
import org.jfree.util.ExtendedConfigurationWrapper;
import org.jfree.util.Log;
import org.jfree.util.ObjectUtilities;

public abstract class AbstractBoot implements SubSystem {
    private boolean bootDone;
    private boolean bootInProgress;
    private ExtendedConfigurationWrapper extWrapper;
    private Configuration globalConfig;
    private PackageManager packageManager;

    protected abstract BootableProjectInfo getProjectInfo();

    protected abstract Configuration loadConfiguration();

    protected abstract void performBoot();

    protected AbstractBoot() {
    }

    public synchronized PackageManager getPackageManager() {
        if (this.packageManager == null) {
            this.packageManager = PackageManager.createInstance(this);
        }
        return this.packageManager;
    }

    public synchronized Configuration getGlobalConfig() {
        if (this.globalConfig == null) {
            this.globalConfig = loadConfiguration();
        }
        return this.globalConfig;
    }

    public final synchronized boolean isBootInProgress() {
        return this.bootInProgress;
    }

    public final synchronized boolean isBootDone() {
        return this.bootDone;
    }

    public final void start() {
        synchronized (this) {
            if (isBootDone()) {
                return;
            }
            while (isBootInProgress()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }
            if (isBootDone()) {
                return;
            }
            this.bootInProgress = true;
            BootableProjectInfo info = getProjectInfo();
            if (info != null) {
                BootableProjectInfo[] childs = info.getDependencies();
                for (BootableProjectInfo bootClass : childs) {
                    AbstractBoot boot = loadBooter(bootClass.getBootClass());
                    if (boot != null) {
                        synchronized (boot) {
                            boot.start();
                            while (!boot.isBootDone()) {
                                try {
                                    boot.wait();
                                } catch (InterruptedException e2) {
                                }
                            }
                        }
                    }
                }
            }
            performBoot();
            if (info != null) {
                Log.info(info.getName() + " " + info.getVersion() + " started.");
            } else {
                Log.info(getClass() + " started.");
            }
            synchronized (this) {
                this.bootInProgress = false;
                this.bootDone = true;
                notifyAll();
            }
        }
    }

    protected AbstractBoot loadBooter(String classname) {
        if (classname == null) {
            return null;
        }
        try {
            return (AbstractBoot) ObjectUtilities.getClassLoader(getClass()).loadClass(classname).getMethod("getInstance", (Class[]) null).invoke(null, (Object[]) null);
        } catch (Exception e) {
            Log.info("Unable to boot dependent class: " + classname);
            return null;
        }
    }

    protected Configuration createDefaultHierarchicalConfiguration(String staticConfig, String userConfig, boolean addSysProps) {
        return createDefaultHierarchicalConfiguration(staticConfig, userConfig, addSysProps, PropertyFileConfiguration.class);
    }

    protected Configuration createDefaultHierarchicalConfiguration(String staticConfig, String userConfig, boolean addSysProps, Class source) {
        HierarchicalConfiguration globalConfig = new HierarchicalConfiguration();
        if (staticConfig != null) {
            PropertyFileConfiguration rootProperty = new PropertyFileConfiguration();
            rootProperty.load(staticConfig, getClass());
            globalConfig.insertConfiguration(rootProperty);
            globalConfig.insertConfiguration(getPackageManager().getPackageConfiguration());
        }
        if (userConfig != null) {
            String userConfigStripped;
            if (userConfig.startsWith("/")) {
                userConfigStripped = userConfig.substring(1);
            } else {
                userConfigStripped = userConfig;
            }
            try {
                Enumeration userConfigs = ObjectUtilities.getClassLoader(getClass()).getResources(userConfigStripped);
                ArrayList configs = new ArrayList();
                while (userConfigs.hasMoreElements()) {
                    URL url = (URL) userConfigs.nextElement();
                    try {
                        PropertyFileConfiguration baseProperty = new PropertyFileConfiguration();
                        InputStream in = url.openStream();
                        baseProperty.load(in);
                        in.close();
                        configs.add(baseProperty);
                    } catch (IOException ioe) {
                        Log.warn("Failed to load the user configuration at " + url, ioe);
                    }
                }
                for (int i = configs.size() - 1; i >= 0; i--) {
                    globalConfig.insertConfiguration((PropertyFileConfiguration) configs.get(i));
                }
            } catch (IOException e) {
                Log.warn("Failed to lookup the user configurations.", e);
            }
        }
        if (addSysProps) {
            globalConfig.insertConfiguration(new SystemPropertyConfiguration());
        }
        return globalConfig;
    }

    public synchronized ExtendedConfiguration getExtendedConfig() {
        if (this.extWrapper == null) {
            this.extWrapper = new ExtendedConfigurationWrapper(getGlobalConfig());
        }
        return this.extWrapper;
    }
}
