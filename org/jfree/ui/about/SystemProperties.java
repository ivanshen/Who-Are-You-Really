package org.jfree.ui.about;

import javax.swing.table.TableColumnModel;
import org.jfree.chart.ChartPanel;
import org.jfree.ui.SortableTable;

public class SystemProperties {
    private SystemProperties() {
    }

    public static SortableTable createSystemPropertiesTable() {
        SortableTable table = new SortableTable(new SystemPropertiesTableModel());
        TableColumnModel model = table.getColumnModel();
        model.getColumn(0).setPreferredWidth(ChartPanel.DEFAULT_MINIMUM_DRAW_HEIGHT);
        model.getColumn(1).setPreferredWidth(350);
        table.setAutoResizeMode(2);
        return table;
    }
}
