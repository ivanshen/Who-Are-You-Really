package org.jfree.ui;

import java.awt.Component;
import java.text.DateFormat;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class DateCellRenderer extends DefaultTableCellRenderer {
    private DateFormat formatter;

    public DateCellRenderer() {
        this(DateFormat.getDateTimeInstance());
    }

    public DateCellRenderer(DateFormat formatter) {
        this.formatter = formatter;
        setHorizontalAlignment(0);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setFont(null);
        if (value != null) {
            setText(this.formatter.format(value));
        } else {
            setText("");
        }
        if (isSelected) {
            setBackground(table.getSelectionBackground());
        } else {
            setBackground(null);
        }
        return this;
    }
}
