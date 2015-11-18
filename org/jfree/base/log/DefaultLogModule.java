package org.jfree.base.log;

import org.jfree.base.modules.AbstractModule;
import org.jfree.base.modules.ModuleInitializeException;
import org.jfree.base.modules.SubSystem;
import org.jfree.util.Log;
import org.jfree.util.PrintStreamLogTarget;

public class DefaultLogModule extends AbstractModule {
    public DefaultLogModule() throws ModuleInitializeException {
        loadModuleInfo();
    }

    public void initialize(SubSystem subSystem) throws ModuleInitializeException {
        if (!LogConfiguration.isDisableLogging() && LogConfiguration.getLogTarget().equals(PrintStreamLogTarget.class.getName())) {
            DefaultLog.installDefaultLog();
            Log.getInstance().addTarget(new PrintStreamLogTarget());
            if ("true".equals(subSystem.getGlobalConfig().getConfigProperty("org.jfree.base.LogAutoInit"))) {
                Log.getInstance().init();
            }
            Log.info("Default log target started ... previous log messages could have been ignored.");
        }
    }
}
