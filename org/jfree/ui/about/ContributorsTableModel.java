package org.jfree.ui.about;

import java.util.List;
import java.util.ResourceBundle;
import javax.swing.table.AbstractTableModel;
import org.jfree.util.LogTarget;
import org.jfree.util.ResourceBundleWrapper;

public class ContributorsTableModel extends AbstractTableModel {
    private String contactColumnLabel;
    private List contributors;
    private String nameColumnLabel;

    public ContributorsTableModel(List contributors) {
        this.contributors = contributors;
        String baseName = "org.jfree.ui.about.resources.AboutResources";
        ResourceBundle resources = ResourceBundleWrapper.getBundle("org.jfree.ui.about.resources.AboutResources");
        this.nameColumnLabel = resources.getString("contributors-table.column.name");
        this.contactColumnLabel = resources.getString("contributors-table.column.contact");
    }

    public int getRowCount() {
        return this.contributors.size();
    }

    public int getColumnCount() {
        return 2;
    }

    public String getColumnName(int column) {
        switch (column) {
            case LogTarget.ERROR /*0*/:
                return this.nameColumnLabel;
            case LogTarget.WARN /*1*/:
                return this.contactColumnLabel;
            default:
                return null;
        }
    }

    public Object getValueAt(int row, int column) {
        Contributor contributor = (Contributor) this.contributors.get(row);
        if (column == 0) {
            return contributor.getName();
        }
        if (column == 1) {
            return contributor.getEmail();
        }
        return null;
    }
}
