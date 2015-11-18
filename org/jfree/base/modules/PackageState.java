package org.jfree.base.modules;

import org.jfree.util.Log;
import org.jfree.util.Log.SimpleMessage;

public class PackageState {
    public static final int STATE_CONFIGURED = 1;
    public static final int STATE_ERROR = -2;
    public static final int STATE_INITIALIZED = 2;
    public static final int STATE_NEW = 0;
    private final Module module;
    private int state;

    public PackageState(Module module) {
        this(module, 0);
    }

    public PackageState(Module module, int state) {
        if (module == null) {
            throw new NullPointerException("Module must not be null.");
        } else if (state == STATE_CONFIGURED || state == STATE_ERROR || state == STATE_INITIALIZED || state == 0) {
            this.module = module;
            this.state = state;
        } else {
            throw new IllegalArgumentException("State is not valid");
        }
    }

    public boolean configure(SubSystem subSystem) {
        if (this.state == 0) {
            try {
                this.module.configure(subSystem);
                this.state = STATE_CONFIGURED;
                return true;
            } catch (NoClassDefFoundError noClassDef) {
                Log.warn(new SimpleMessage("Unable to load module classes for ", this.module.getName(), ":", noClassDef.getMessage()));
                this.state = STATE_ERROR;
            } catch (Exception e) {
                if (Log.isDebugEnabled()) {
                    Log.warn("Unable to configure the module " + this.module.getName(), e);
                } else if (Log.isWarningEnabled()) {
                    Log.warn("Unable to configure the module " + this.module.getName());
                }
                this.state = STATE_ERROR;
            }
        }
        return false;
    }

    public Module getModule() {
        return this.module;
    }

    public int getState() {
        return this.state;
    }

    public boolean initialize(SubSystem subSystem) {
        if (this.state == STATE_CONFIGURED) {
            try {
                this.module.initialize(subSystem);
                this.state = STATE_INITIALIZED;
                return true;
            } catch (NoClassDefFoundError noClassDef) {
                Log.warn(new SimpleMessage("Unable to load module classes for ", this.module.getName(), ":", noClassDef.getMessage()));
                this.state = STATE_ERROR;
            } catch (ModuleInitializeException me) {
                if (Log.isDebugEnabled()) {
                    Log.warn("Unable to initialize the module " + this.module.getName(), me);
                } else if (Log.isWarningEnabled()) {
                    Log.warn("Unable to initialize the module " + this.module.getName());
                }
                this.state = STATE_ERROR;
            } catch (Exception e) {
                if (Log.isDebugEnabled()) {
                    Log.warn("Unable to initialize the module " + this.module.getName(), e);
                } else if (Log.isWarningEnabled()) {
                    Log.warn("Unable to initialize the module " + this.module.getName());
                }
                this.state = STATE_ERROR;
            }
        }
        return false;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PackageState)) {
            return false;
        }
        if (this.module.getModuleClass().equals(((PackageState) o).module.getModuleClass())) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.module.hashCode();
    }
}
