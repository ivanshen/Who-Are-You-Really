package org.jfree.ui.about;

import java.util.List;
import java.util.ResourceBundle;
import javax.swing.table.AbstractTableModel;
import org.jfree.base.Library;
import org.jfree.util.LogTarget;
import org.jfree.util.ResourceBundleWrapper;

public class LibraryTableModel extends AbstractTableModel {
    private String infoColumnLabel;
    private Library[] libraries;
    private String licenceColumnLabel;
    private String nameColumnLabel;
    private String versionColumnLabel;

    public LibraryTableModel(List libraries) {
        this.libraries = (Library[]) libraries.toArray(new Library[libraries.size()]);
        String baseName = "org.jfree.ui.about.resources.AboutResources";
        ResourceBundle resources = ResourceBundleWrapper.getBundle("org.jfree.ui.about.resources.AboutResources");
        this.nameColumnLabel = resources.getString("libraries-table.column.name");
        this.versionColumnLabel = resources.getString("libraries-table.column.version");
        this.licenceColumnLabel = resources.getString("libraries-table.column.licence");
        this.infoColumnLabel = resources.getString("libraries-table.column.info");
    }

    public int getRowCount() {
        return this.libraries.length;
    }

    public int getColumnCount() {
        return 4;
    }

    public String getColumnName(int column) {
        switch (column) {
            case LogTarget.ERROR /*0*/:
                return this.nameColumnLabel;
            case LogTarget.WARN /*1*/:
                return this.versionColumnLabel;
            case LogTarget.INFO /*2*/:
                return this.licenceColumnLabel;
            case LogTarget.DEBUG /*3*/:
                return this.infoColumnLabel;
            default:
                return null;
        }
    }

    public Object getValueAt(int row, int column) {
        Library library = this.libraries[row];
        if (column == 0) {
            return library.getName();
        }
        if (column == 1) {
            return library.getVersion();
        }
        if (column == 2) {
            return library.getLicenceName();
        }
        if (column == 3) {
            return library.getInfo();
        }
        return null;
    }

    public Library[] getLibraries() {
        return (Library[]) this.libraries.clone();
    }
}
