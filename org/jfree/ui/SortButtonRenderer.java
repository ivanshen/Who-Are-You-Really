package org.jfree.ui;

import java.awt.Component;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

public class SortButtonRenderer implements TableCellRenderer {
    public static final int DOWN = 1;
    public static final int NONE = 0;
    public static final int UP = 2;
    private JButton ascendingButton;
    private JLabel ascendingLabel;
    private JButton descendingButton;
    private JLabel descendingLabel;
    private JButton normalButton;
    private JLabel normalLabel;
    private int pressedColumn;
    private boolean useLabels;

    public SortButtonRenderer() {
        this.pressedColumn = -1;
        this.pressedColumn = -1;
        this.useLabels = UIManager.getLookAndFeel().getID().equals("Aqua");
        Border border = UIManager.getBorder("TableHeader.cellBorder");
        if (this.useLabels) {
            this.normalLabel = new JLabel();
            this.normalLabel.setHorizontalAlignment(10);
            this.ascendingLabel = new JLabel();
            this.ascendingLabel.setHorizontalAlignment(10);
            this.ascendingLabel.setHorizontalTextPosition(UP);
            this.ascendingLabel.setIcon(new BevelArrowIcon(DOWN, false, false));
            this.descendingLabel = new JLabel();
            this.descendingLabel.setHorizontalAlignment(10);
            this.descendingLabel.setHorizontalTextPosition(UP);
            this.descendingLabel.setIcon(new BevelArrowIcon(NONE, false, false));
            this.normalLabel.setBorder(border);
            this.ascendingLabel.setBorder(border);
            this.descendingLabel.setBorder(border);
            return;
        }
        this.normalButton = new JButton();
        this.normalButton.setMargin(new Insets(NONE, NONE, NONE, NONE));
        this.normalButton.setHorizontalAlignment(10);
        this.ascendingButton = new JButton();
        this.ascendingButton.setMargin(new Insets(NONE, NONE, NONE, NONE));
        this.ascendingButton.setHorizontalAlignment(10);
        this.ascendingButton.setHorizontalTextPosition(UP);
        this.ascendingButton.setIcon(new BevelArrowIcon(DOWN, false, false));
        this.ascendingButton.setPressedIcon(new BevelArrowIcon(DOWN, false, true));
        this.descendingButton = new JButton();
        this.descendingButton.setMargin(new Insets(NONE, NONE, NONE, NONE));
        this.descendingButton.setHorizontalAlignment(10);
        this.descendingButton.setHorizontalTextPosition(UP);
        this.descendingButton.setIcon(new BevelArrowIcon(NONE, false, false));
        this.descendingButton.setPressedIcon(new BevelArrowIcon(NONE, false, true));
        this.normalButton.setBorder(border);
        this.ascendingButton.setBorder(border);
        this.descendingButton.setBorder(border);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (table == null) {
            throw new NullPointerException("Table must not be null.");
        }
        JComponent component;
        SortableTableModel model = (SortableTableModel) table.getModel();
        int cc = table.convertColumnIndexToModel(column);
        boolean isSorting = model.getSortingColumn() == cc;
        boolean isAscending = model.isAscending();
        JTableHeader header = table.getTableHeader();
        boolean isPressed = cc == this.pressedColumn;
        if (this.useLabels) {
            JLabel label = getRendererLabel(isSorting, isAscending);
            label.setText(value == null ? "" : value.toString());
            component = label;
        } else {
            JButton button = getRendererButton(isSorting, isAscending);
            button.setText(value == null ? "" : value.toString());
            button.getModel().setPressed(isPressed);
            button.getModel().setArmed(isPressed);
            component = button;
        }
        if (header != null) {
            component.setForeground(header.getForeground());
            component.setBackground(header.getBackground());
            component.setFont(header.getFont());
        }
        return component;
    }

    private JButton getRendererButton(boolean isSorting, boolean isAscending) {
        if (!isSorting) {
            return this.normalButton;
        }
        if (isAscending) {
            return this.ascendingButton;
        }
        return this.descendingButton;
    }

    private JLabel getRendererLabel(boolean isSorting, boolean isAscending) {
        if (!isSorting) {
            return this.normalLabel;
        }
        if (isAscending) {
            return this.ascendingLabel;
        }
        return this.descendingLabel;
    }

    public void setPressedColumn(int column) {
        this.pressedColumn = column;
    }
}
