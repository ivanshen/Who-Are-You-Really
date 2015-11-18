package org.jfree.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

public class RefineryUtilities {
    private RefineryUtilities() {
    }

    public static Point getCenterPoint() {
        try {
            return (Point) GraphicsEnvironment.class.getMethod("getCenterPoint", (Class[]) null).invoke(GraphicsEnvironment.getLocalGraphicsEnvironment(), (Object[]) null);
        } catch (Exception e) {
            Dimension s = Toolkit.getDefaultToolkit().getScreenSize();
            return new Point(s.width / 2, s.height / 2);
        }
    }

    public static Rectangle getMaximumWindowBounds() {
        try {
            return (Rectangle) GraphicsEnvironment.class.getMethod("getMaximumWindowBounds", (Class[]) null).invoke(GraphicsEnvironment.getLocalGraphicsEnvironment(), (Object[]) null);
        } catch (Exception e) {
            Dimension s = Toolkit.getDefaultToolkit().getScreenSize();
            return new Rectangle(0, 0, s.width, s.height);
        }
    }

    public static void centerFrameOnScreen(Window frame) {
        positionFrameOnScreen(frame, 0.5d, 0.5d);
    }

    public static void positionFrameOnScreen(Window frame, double horizontalPercent, double verticalPercent) {
        Rectangle s = frame.getGraphicsConfiguration().getBounds();
        Dimension f = frame.getSize();
        frame.setBounds(((int) (((double) Math.max(s.width - f.width, 0)) * horizontalPercent)) + s.x, ((int) (((double) Math.max(s.height - f.height, 0)) * verticalPercent)) + s.y, f.width, f.height);
    }

    public static void positionFrameRandomly(Window frame) {
        positionFrameOnScreen(frame, Math.random(), Math.random());
    }

    public static void centerDialogInParent(Dialog dialog) {
        positionDialogRelativeToParent(dialog, 0.5d, 0.5d);
    }

    public static void positionDialogRelativeToParent(Dialog dialog, double horizontalPercent, double verticalPercent) {
        Container parent = dialog.getParent();
        if (parent == null) {
            centerFrameOnScreen(dialog);
            return;
        }
        Dimension d = dialog.getSize();
        Dimension p = parent.getSize();
        int baseX = parent.getX();
        int x = baseX + ((int) (((double) p.width) * horizontalPercent));
        int y = parent.getY() + ((int) (((double) p.height) * verticalPercent));
        dialog.setBounds(new Rectangle(x, y, d.width, d.height).intersection(parent.getGraphicsConfiguration().getBounds()));
    }

    public static JPanel createTablePanel(TableModel model) {
        JPanel panel = new JPanel(new BorderLayout());
        JTable table = new JTable(model);
        for (int columnIndex = 0; columnIndex < model.getColumnCount(); columnIndex++) {
            TableColumn column = table.getColumnModel().getColumn(columnIndex);
            if (model.getColumnClass(columnIndex).equals(Number.class)) {
                column.setCellRenderer(new NumberCellRenderer());
            }
        }
        panel.add(new JScrollPane(table));
        return panel;
    }

    public static JLabel createJLabel(String text, Font font) {
        JLabel result = new JLabel(text);
        result.setFont(font);
        return result;
    }

    public static JLabel createJLabel(String text, Font font, Color color) {
        JLabel result = new JLabel(text);
        result.setFont(font);
        result.setForeground(color);
        return result;
    }

    public static JButton createJButton(String label, Font font) {
        JButton result = new JButton(label);
        result.setFont(font);
        return result;
    }
}
