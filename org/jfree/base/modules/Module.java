package org.jfree.base.modules;

public interface Module extends ModuleInfo {
    void configure(SubSystem subSystem);

    String getDescription();

    String getName();

    ModuleInfo[] getOptionalModules();

    String getProducer();

    ModuleInfo[] getRequiredModules();

    String getSubSystem();

    void initialize(SubSystem subSystem) throws ModuleInitializeException;
}
