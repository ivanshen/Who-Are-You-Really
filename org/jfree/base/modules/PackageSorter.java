package org.jfree.base.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.jfree.util.Log;
import org.jfree.util.Log.SimpleMessage;

public final class PackageSorter {

    private static class SortModule implements Comparable {
        private ArrayList dependSubsystems;
        private int position;
        private final PackageState state;

        public SortModule(PackageState state) {
            this.position = -1;
            this.state = state;
        }

        public ArrayList getDependSubsystems() {
            return this.dependSubsystems;
        }

        public void setDependSubsystems(ArrayList dependSubsystems) {
            this.dependSubsystems = dependSubsystems;
        }

        public int getPosition() {
            return this.position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public PackageState getState() {
            return this.state;
        }

        public String toString() {
            StringBuffer buffer = new StringBuffer();
            buffer.append("SortModule: ");
            buffer.append(this.position);
            buffer.append(" ");
            buffer.append(this.state.getModule().getName());
            buffer.append(" ");
            buffer.append(this.state.getModule().getModuleClass());
            return buffer.toString();
        }

        public int compareTo(Object o) {
            SortModule otherModule = (SortModule) o;
            if (this.position > otherModule.position) {
                return 1;
            }
            if (this.position < otherModule.position) {
                return -1;
            }
            return 0;
        }
    }

    private PackageSorter() {
    }

    public static void sort(List modules) {
        int i;
        HashMap moduleMap = new HashMap();
        ArrayList errorModules = new ArrayList();
        ArrayList weightModules = new ArrayList();
        for (i = 0; i < modules.size(); i++) {
            PackageState state = (PackageState) modules.get(i);
            if (state.getState() == -2) {
                errorModules.add(state);
            } else {
                SortModule mod = new SortModule(state);
                weightModules.add(mod);
                moduleMap.put(state.getModule().getModuleClass(), mod);
            }
        }
        SortModule[] weigths = (SortModule[]) weightModules.toArray(new SortModule[weightModules.size()]);
        for (SortModule sortMod : weigths) {
            sortMod.setDependSubsystems(collectSubsystemModules(sortMod.getState().getModule(), moduleMap));
        }
        boolean doneWork = true;
        while (doneWork) {
            doneWork = false;
            for (SortModule mod2 : weigths) {
                int position = searchModulePosition(mod2, moduleMap);
                if (position != mod2.getPosition()) {
                    mod2.setPosition(position);
                    doneWork = true;
                }
            }
        }
        Arrays.sort(weigths);
        modules.clear();
        for (SortModule state2 : weigths) {
            modules.add(state2.getState());
        }
        for (i = 0; i < errorModules.size(); i++) {
            modules.add(errorModules.get(i));
        }
    }

    private static int searchModulePosition(SortModule smodule, HashMap moduleMap) {
        Module module = smodule.getState().getModule();
        int position = 0;
        ModuleInfo[] modInfo = module.getOptionalModules();
        for (ModuleInfo moduleClass : modInfo) {
            SortModule reqMod = (SortModule) moduleMap.get(moduleClass.getModuleClass());
            if (reqMod != null && reqMod.getPosition() >= position) {
                position = reqMod.getPosition() + 1;
            }
        }
        modInfo = module.getRequiredModules();
        for (ModuleInfo moduleClass2 : modInfo) {
            String moduleName = moduleClass2.getModuleClass();
            reqMod = (SortModule) moduleMap.get(moduleName);
            if (reqMod == null) {
                Log.warn("Invalid state: Required dependency of '" + moduleName + "' had an error.");
            } else if (reqMod.getPosition() >= position) {
                position = reqMod.getPosition() + 1;
            }
        }
        String subSystem = module.getSubSystem();
        for (SortModule mod : moduleMap.values()) {
            if (mod.getState().getModule() != module) {
                Module subSysMod = mod.getState().getModule();
                if (!subSystem.equals(subSysMod.getSubSystem()) && smodule.getDependSubsystems().contains(subSysMod.getSubSystem()) && !isBaseModule(subSysMod, module) && mod.getPosition() >= position) {
                    position = mod.getPosition() + 1;
                }
            }
        }
        return position;
    }

    private static boolean isBaseModule(Module mod, ModuleInfo mi) {
        ModuleInfo[] info = mod.getRequiredModules();
        for (ModuleInfo moduleClass : info) {
            if (moduleClass.getModuleClass().equals(mi.getModuleClass())) {
                return true;
            }
        }
        info = mod.getOptionalModules();
        for (ModuleInfo moduleClass2 : info) {
            if (moduleClass2.getModuleClass().equals(mi.getModuleClass())) {
                return true;
            }
        }
        return false;
    }

    private static ArrayList collectSubsystemModules(Module childMod, HashMap moduleMap) {
        ArrayList collector = new ArrayList();
        ModuleInfo[] info = childMod.getRequiredModules();
        for (int i = 0; i < info.length; i++) {
            SortModule dependentModule = (SortModule) moduleMap.get(info[i].getModuleClass());
            if (dependentModule == null) {
                Log.warn(new SimpleMessage("A dependent module was not found in the list of known modules.", info[i].getModuleClass()));
            } else {
                collector.add(dependentModule.getState().getModule().getSubSystem());
            }
        }
        info = childMod.getOptionalModules();
        for (ModuleInfo moduleClass : info) {
            Module dependentModule2 = (Module) moduleMap.get(moduleClass.getModuleClass());
            if (dependentModule2 == null) {
                Log.warn("A dependent module was not found in the list of known modules.");
            } else {
                collector.add(dependentModule2.getSubSystem());
            }
        }
        return collector;
    }
}
