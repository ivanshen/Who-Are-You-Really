package org.jfree.base.modules;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import org.jfree.base.AbstractBoot;
import org.jfree.base.config.HierarchicalConfiguration;
import org.jfree.base.config.PropertyFileConfiguration;
import org.jfree.base.log.PadMessage;
import org.jfree.util.Configuration;
import org.jfree.util.Log;
import org.jfree.util.Log.SimpleMessage;
import org.jfree.util.ObjectUtilities;

public final class PackageManager {
    private static final int RETURN_MODULE_ERROR = 2;
    private static final int RETURN_MODULE_LOADED = 0;
    private static final int RETURN_MODULE_UNKNOWN = 1;
    private static HashMap instances;
    private AbstractBoot booter;
    private final ArrayList initSections;
    private final ArrayList modules;
    private final PackageConfiguration packageConfiguration;

    public static class PackageConfiguration extends PropertyFileConfiguration {
        public void insertConfiguration(HierarchicalConfiguration config) {
            super.insertConfiguration(config);
        }
    }

    public static PackageManager createInstance(AbstractBoot booter) {
        if (instances == null) {
            instances = new HashMap();
            PackageManager manager = new PackageManager(booter);
            instances.put(booter, manager);
            return manager;
        }
        manager = (PackageManager) instances.get(booter);
        if (manager == null) {
            manager = new PackageManager(booter);
            instances.put(booter, manager);
        }
        return manager;
    }

    private PackageManager(AbstractBoot booter) {
        if (booter == null) {
            throw new NullPointerException();
        }
        this.booter = booter;
        this.packageConfiguration = new PackageConfiguration();
        this.modules = new ArrayList();
        this.initSections = new ArrayList();
    }

    public boolean isModuleAvailable(ModuleInfo moduleDescription) {
        PackageState[] packageStates = (PackageState[]) this.modules.toArray(new PackageState[this.modules.size()]);
        int i = RETURN_MODULE_LOADED;
        while (i < packageStates.length) {
            PackageState state = packageStates[i];
            if (!state.getModule().getModuleClass().equals(moduleDescription.getModuleClass())) {
                i += RETURN_MODULE_UNKNOWN;
            } else if (state.getState() == RETURN_MODULE_ERROR) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public void load(String modulePrefix) {
        if (!this.initSections.contains(modulePrefix)) {
            this.initSections.add(modulePrefix);
            Configuration config = this.booter.getGlobalConfig();
            Iterator it = config.findPropertyKeys(modulePrefix);
            int count = RETURN_MODULE_LOADED;
            while (it.hasNext()) {
                String key = (String) it.next();
                if (key.endsWith(".Module")) {
                    String moduleClass = config.getConfigProperty(key);
                    if (moduleClass != null && moduleClass.length() > 0) {
                        addModule(moduleClass);
                        count += RETURN_MODULE_UNKNOWN;
                    }
                }
            }
            Log.debug("Loaded a total of " + count + " modules under prefix: " + modulePrefix);
        }
    }

    public synchronized void initializeModules() {
        int i;
        PackageSorter.sort(this.modules);
        for (i = RETURN_MODULE_LOADED; i < this.modules.size(); i += RETURN_MODULE_UNKNOWN) {
            PackageState mod = (PackageState) this.modules.get(i);
            if (mod.configure(this.booter)) {
                Log.debug(new SimpleMessage("Conf: ", new PadMessage(mod.getModule().getModuleClass(), 70), " [", mod.getModule().getSubSystem(), "]"));
            }
        }
        for (i = RETURN_MODULE_LOADED; i < this.modules.size(); i += RETURN_MODULE_UNKNOWN) {
            mod = (PackageState) this.modules.get(i);
            if (mod.initialize(this.booter)) {
                Log.debug(new SimpleMessage("Init: ", new PadMessage(mod.getModule().getModuleClass(), 70), " [", mod.getModule().getSubSystem(), "]"));
            }
        }
    }

    public synchronized void addModule(String modClass) {
        ArrayList loadModules = new ArrayList();
        if (loadModule(new DefaultModuleInfo(modClass, null, null, null), new ArrayList(), loadModules, false)) {
            for (int i = RETURN_MODULE_LOADED; i < loadModules.size(); i += RETURN_MODULE_UNKNOWN) {
                this.modules.add(new PackageState((Module) loadModules.get(i)));
            }
        }
    }

    private int containsModule(ArrayList tempModules, ModuleInfo module) {
        int i;
        if (tempModules != null) {
            ModuleInfo[] mods = (ModuleInfo[]) tempModules.toArray(new ModuleInfo[tempModules.size()]);
            for (i = RETURN_MODULE_LOADED; i < mods.length; i += RETURN_MODULE_UNKNOWN) {
                if (mods[i].getModuleClass().equals(module.getModuleClass())) {
                    return RETURN_MODULE_LOADED;
                }
            }
        }
        PackageState[] packageStates = (PackageState[]) this.modules.toArray(new PackageState[this.modules.size()]);
        i = RETURN_MODULE_LOADED;
        while (i < packageStates.length) {
            if (packageStates[i].getModule().getModuleClass().equals(module.getModuleClass())) {
                return packageStates[i].getState() == -2 ? RETURN_MODULE_ERROR : RETURN_MODULE_LOADED;
            } else {
                i += RETURN_MODULE_UNKNOWN;
            }
        }
        return RETURN_MODULE_UNKNOWN;
    }

    private void dropFailedModule(PackageState state) {
        if (!this.modules.contains(state)) {
            this.modules.add(state);
        }
    }

    private boolean loadModule(ModuleInfo moduleInfo, ArrayList incompleteModules, ArrayList modules, boolean fatal) {
        try {
            Module module = (Module) ObjectUtilities.getClassLoader(getClass()).loadClass(moduleInfo.getModuleClass()).newInstance();
            if (acceptVersion(moduleInfo, module)) {
                int moduleContained = containsModule(modules, module);
                if (moduleContained == RETURN_MODULE_ERROR) {
                    Log.debug("Indicated failure for module: " + module.getModuleClass());
                    dropFailedModule(new PackageState(module, -2));
                    return false;
                }
                if (moduleContained == RETURN_MODULE_UNKNOWN) {
                    if (incompleteModules.contains(module)) {
                        Log.error(new SimpleMessage("Circular module reference: This module definition is invalid: ", module.getClass()));
                        dropFailedModule(new PackageState(module, -2));
                        return false;
                    }
                    incompleteModules.add(module);
                    ModuleInfo[] required = module.getRequiredModules();
                    int i = RETURN_MODULE_LOADED;
                    while (i < required.length) {
                        if (loadModule(required[i], incompleteModules, modules, true)) {
                            i += RETURN_MODULE_UNKNOWN;
                        } else {
                            Log.debug("Indicated failure for module: " + module.getModuleClass());
                            dropFailedModule(new PackageState(module, -2));
                            return false;
                        }
                    }
                    ModuleInfo[] optional = module.getOptionalModules();
                    for (i = RETURN_MODULE_LOADED; i < optional.length; i += RETURN_MODULE_UNKNOWN) {
                        if (!loadModule(optional[i], incompleteModules, modules, true)) {
                            Log.debug(new SimpleMessage("Optional module: ", optional[i].getModuleClass(), " was not loaded."));
                        }
                    }
                    if (containsModule(modules, module) == RETURN_MODULE_UNKNOWN) {
                        modules.add(module);
                    }
                    incompleteModules.remove(module);
                }
                return true;
            }
            Log.warn("Module " + module.getName() + ": required version: " + moduleInfo + ", but found Version: \n" + module);
            dropFailedModule(new PackageState(module, -2));
            return false;
        } catch (ClassNotFoundException cnfe) {
            if (fatal) {
                Log.warn(new SimpleMessage("Unresolved dependency for package: ", moduleInfo.getModuleClass()));
            }
            Log.debug(new SimpleMessage("ClassNotFound: ", cnfe.getMessage()));
            return false;
        } catch (Exception e) {
            Log.warn(new SimpleMessage("Exception while loading module: ", (Object) moduleInfo), e);
            return false;
        }
    }

    private boolean acceptVersion(ModuleInfo moduleRequirement, Module module) {
        if (moduleRequirement.getMajorVersion() == null) {
            return true;
        }
        int compare;
        if (module.getMajorVersion() == null) {
            Log.warn("Module " + module.getName() + " does not define a major version.");
        } else {
            compare = acceptVersion(moduleRequirement.getMajorVersion(), module.getMajorVersion());
            if (compare > 0) {
                return false;
            }
            if (compare < 0) {
                return true;
            }
        }
        if (moduleRequirement.getMinorVersion() == null) {
            return true;
        }
        if (module.getMinorVersion() == null) {
            Log.warn("Module " + module.getName() + " does not define a minor version.");
        } else {
            compare = acceptVersion(moduleRequirement.getMinorVersion(), module.getMinorVersion());
            if (compare > 0) {
                return false;
            }
            if (compare < 0) {
                return true;
            }
        }
        if (moduleRequirement.getPatchLevel() == null) {
            return true;
        }
        if (module.getPatchLevel() == null) {
            Log.debug("Module " + module.getName() + " does not define a patch level.");
            return true;
        } else if (acceptVersion(moduleRequirement.getPatchLevel(), module.getPatchLevel()) <= 0) {
            return true;
        } else {
            Log.debug("Did not accept patchlevel: " + moduleRequirement.getPatchLevel() + " - " + module.getPatchLevel());
            return false;
        }
    }

    private int acceptVersion(String modVer, String depModVer) {
        char[] modVerArray;
        char[] depVerArray;
        int mLength = Math.max(modVer.length(), depModVer.length());
        int delta;
        if (modVer.length() > depModVer.length()) {
            modVerArray = modVer.toCharArray();
            depVerArray = new char[mLength];
            delta = modVer.length() - depModVer.length();
            Arrays.fill(depVerArray, RETURN_MODULE_LOADED, delta, ' ');
            System.arraycopy(depVerArray, delta, depModVer.toCharArray(), RETURN_MODULE_LOADED, depModVer.length());
        } else if (modVer.length() < depModVer.length()) {
            depVerArray = depModVer.toCharArray();
            modVerArray = new char[mLength];
            char[] b1 = new char[mLength];
            delta = depModVer.length() - modVer.length();
            Arrays.fill(b1, RETURN_MODULE_LOADED, delta, ' ');
            System.arraycopy(b1, delta, modVer.toCharArray(), RETURN_MODULE_LOADED, modVer.length());
        } else {
            depVerArray = depModVer.toCharArray();
            modVerArray = modVer.toCharArray();
        }
        return new String(modVerArray).compareTo(new String(depVerArray));
    }

    public PackageConfiguration getPackageConfiguration() {
        return this.packageConfiguration;
    }

    public Module[] getAllModules() {
        Module[] mods = new Module[this.modules.size()];
        for (int i = RETURN_MODULE_LOADED; i < this.modules.size(); i += RETURN_MODULE_UNKNOWN) {
            mods[i] = ((PackageState) this.modules.get(i)).getModule();
        }
        return mods;
    }

    public Module[] getActiveModules() {
        ArrayList mods = new ArrayList();
        for (int i = RETURN_MODULE_LOADED; i < this.modules.size(); i += RETURN_MODULE_UNKNOWN) {
            PackageState state = (PackageState) this.modules.get(i);
            if (state.getState() == RETURN_MODULE_ERROR) {
                mods.add(state.getModule());
            }
        }
        return (Module[]) mods.toArray(new Module[mods.size()]);
    }

    public void printUsedModules(PrintStream p) {
        int i;
        Module[] allMods = getAllModules();
        ArrayList activeModules = new ArrayList();
        ArrayList failedModules = new ArrayList();
        for (i = RETURN_MODULE_LOADED; i < allMods.length; i += RETURN_MODULE_UNKNOWN) {
            if (isModuleAvailable(allMods[i])) {
                activeModules.add(allMods[i]);
            } else {
                failedModules.add(allMods[i]);
            }
        }
        p.print("Active modules: ");
        p.println(activeModules.size());
        p.println("----------------------------------------------------------");
        for (i = RETURN_MODULE_LOADED; i < activeModules.size(); i += RETURN_MODULE_UNKNOWN) {
            Module mod = (Module) activeModules.get(i);
            p.print(new PadMessage(mod.getModuleClass(), 70));
            p.print(" [");
            p.print(mod.getSubSystem());
            p.println("]");
            p.print("  Version: ");
            p.print(mod.getMajorVersion());
            p.print("-");
            p.print(mod.getMinorVersion());
            p.print("-");
            p.print(mod.getPatchLevel());
            p.print(" Producer: ");
            p.println(mod.getProducer());
            p.print("  Description: ");
            p.println(mod.getDescription());
        }
    }
}
