package org.jfree.base;

import org.jfree.JCommon;
import org.jfree.base.config.ModifiableConfiguration;
import org.jfree.base.log.DefaultLogModule;
import org.jfree.util.Configuration;
import org.jfree.util.ObjectUtilities;

public class BaseBoot extends AbstractBoot {
    private static BaseBoot singleton;
    private BootableProjectInfo bootableProjectInfo;

    private BaseBoot() {
        this.bootableProjectInfo = JCommon.INFO;
    }

    public static ModifiableConfiguration getConfiguration() {
        return (ModifiableConfiguration) getInstance().getGlobalConfig();
    }

    protected synchronized Configuration loadConfiguration() {
        return createDefaultHierarchicalConfiguration("/org/jfree/base/jcommon.properties", "/jcommon.properties", true, BaseBoot.class);
    }

    public static synchronized AbstractBoot getInstance() {
        AbstractBoot abstractBoot;
        synchronized (BaseBoot.class) {
            if (singleton == null) {
                singleton = new BaseBoot();
            }
            abstractBoot = singleton;
        }
        return abstractBoot;
    }

    protected void performBoot() {
        ObjectUtilities.setClassLoaderSource(getConfiguration().getConfigProperty("org.jfree.ClassLoader"));
        getPackageManager().addModule(DefaultLogModule.class.getName());
        getPackageManager().load("org.jfree.jcommon.modules.");
        getPackageManager().initializeModules();
    }

    protected BootableProjectInfo getProjectInfo() {
        return this.bootableProjectInfo;
    }
}
