package org.jfree.ui.about;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import org.jfree.ui.SortableTableModel;
import org.jfree.util.ResourceBundleWrapper;

public class SystemPropertiesTableModel extends SortableTableModel {
    private String nameColumnLabel;
    private List properties;
    private String valueColumnLabel;

    protected static class SystemProperty {
        private String name;
        private String value;

        public SystemProperty(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return this.name;
        }

        public String getValue() {
            return this.value;
        }
    }

    protected static class SystemPropertyComparator implements Comparator {
        private boolean ascending;

        public SystemPropertyComparator(boolean ascending) {
            this.ascending = ascending;
        }

        public int compare(Object o1, Object o2) {
            if (!(o1 instanceof SystemProperty) || !(o2 instanceof SystemProperty)) {
                return 0;
            }
            SystemProperty sp1 = (SystemProperty) o1;
            SystemProperty sp2 = (SystemProperty) o2;
            if (this.ascending) {
                return sp1.getName().compareTo(sp2.getName());
            }
            return sp2.getName().compareTo(sp1.getName());
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof SystemPropertyComparator)) {
                return false;
            }
            if (this.ascending != ((SystemPropertyComparator) o).ascending) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return this.ascending ? 1 : 0;
        }
    }

    public SystemPropertiesTableModel() {
        this.properties = new ArrayList();
        try {
            for (String name : System.getProperties().keySet()) {
                this.properties.add(new SystemProperty(name, System.getProperty(name)));
            }
        } catch (SecurityException e) {
        }
        Collections.sort(this.properties, new SystemPropertyComparator(true));
        String baseName = "org.jfree.ui.about.resources.AboutResources";
        ResourceBundle resources = ResourceBundleWrapper.getBundle("org.jfree.ui.about.resources.AboutResources");
        this.nameColumnLabel = resources.getString("system-properties-table.column.name");
        this.valueColumnLabel = resources.getString("system-properties-table.column.value");
    }

    public boolean isSortable(int column) {
        if (column == 0) {
            return true;
        }
        return false;
    }

    public int getRowCount() {
        return this.properties.size();
    }

    public int getColumnCount() {
        return 2;
    }

    public String getColumnName(int column) {
        if (column == 0) {
            return this.nameColumnLabel;
        }
        return this.valueColumnLabel;
    }

    public Object getValueAt(int row, int column) {
        SystemProperty sp = (SystemProperty) this.properties.get(row);
        if (column == 0) {
            return sp.getName();
        }
        if (column == 1) {
            return sp.getValue();
        }
        return null;
    }

    public void sortByColumn(int column, boolean ascending) {
        if (isSortable(column)) {
            super.sortByColumn(column, ascending);
            Collections.sort(this.properties, new SystemPropertyComparator(ascending));
        }
    }
}
