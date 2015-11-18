package org.jfree.ui;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class ExtensionFileFilter extends FileFilter {
    private String description;
    private String extension;

    public ExtensionFileFilter(String description, String extension) {
        this.description = description;
        this.extension = extension;
    }

    public boolean accept(File file) {
        if (file.isDirectory() || file.getName().toLowerCase().endsWith(this.extension)) {
            return true;
        }
        return false;
    }

    public String getDescription() {
        return this.description;
    }
}
