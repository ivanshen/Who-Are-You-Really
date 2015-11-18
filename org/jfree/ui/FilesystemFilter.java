package org.jfree.ui;

import java.io.File;
import java.io.FilenameFilter;
import javax.swing.filechooser.FileFilter;

public class FilesystemFilter extends FileFilter implements FilenameFilter {
    private boolean accDirs;
    private String descr;
    private String[] fileext;

    public FilesystemFilter(String fileext, String descr) {
        this(fileext, descr, true);
    }

    public FilesystemFilter(String fileext, String descr, boolean accDirs) {
        this(new String[]{fileext}, descr, accDirs);
    }

    public FilesystemFilter(String[] fileext, String descr, boolean accDirs) {
        this.fileext = (String[]) fileext.clone();
        this.descr = descr;
        this.accDirs = accDirs;
    }

    public boolean accept(File dir, String name) {
        if (new File(dir, name).isDirectory() && acceptsDirectories()) {
            return true;
        }
        for (String endsWith : this.fileext) {
            if (name.endsWith(endsWith)) {
                return true;
            }
        }
        return false;
    }

    public boolean accept(File dir) {
        if (dir.isDirectory() && acceptsDirectories()) {
            return true;
        }
        for (String endsWith : this.fileext) {
            if (dir.getName().endsWith(endsWith)) {
                return true;
            }
        }
        return false;
    }

    public String getDescription() {
        return this.descr;
    }

    public void acceptDirectories(boolean b) {
        this.accDirs = b;
    }

    public boolean acceptsDirectories() {
        return this.accDirs;
    }
}
