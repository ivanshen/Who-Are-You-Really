package org.jfree.ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.table.JTableHeader;

public class SortableTableHeaderListener implements MouseListener, MouseMotionListener {
    private SortableTableModel model;
    private SortButtonRenderer renderer;
    private int sortColumnIndex;

    public SortableTableHeaderListener(SortableTableModel model, SortButtonRenderer renderer) {
        this.model = model;
        this.renderer = renderer;
    }

    public void setTableModel(SortableTableModel model) {
        this.model = model;
    }

    public void mousePressed(MouseEvent e) {
        JTableHeader header = (JTableHeader) e.getComponent();
        if (header.getResizingColumn() == null && header.getDraggedDistance() < 1) {
            int columnIndex = header.columnAtPoint(e.getPoint());
            if (this.model.isSortable(header.getTable().convertColumnIndexToModel(columnIndex))) {
                this.sortColumnIndex = header.getTable().convertColumnIndexToModel(columnIndex);
                this.renderer.setPressedColumn(this.sortColumnIndex);
                header.repaint();
                if (header.getTable().isEditing()) {
                    header.getTable().getCellEditor().stopCellEditing();
                    return;
                }
                return;
            }
            this.sortColumnIndex = -1;
        }
    }

    public void mouseDragged(MouseEvent e) {
        JTableHeader header = (JTableHeader) e.getComponent();
        if (header.getDraggedDistance() > 0 || header.getResizingColumn() != null) {
            this.renderer.setPressedColumn(-1);
            this.sortColumnIndex = -1;
        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
        JTableHeader header = (JTableHeader) e.getComponent();
        if (header.getResizingColumn() == null && this.sortColumnIndex != -1) {
            SortableTableModel model = (SortableTableModel) header.getTable().getModel();
            boolean ascending = !model.isAscending();
            model.setAscending(ascending);
            model.sortByColumn(this.sortColumnIndex, ascending);
            this.renderer.setPressedColumn(-1);
            header.repaint();
        }
    }
}
