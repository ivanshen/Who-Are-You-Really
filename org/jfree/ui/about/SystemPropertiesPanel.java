package org.jfree.ui.about;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import org.jfree.util.ResourceBundleWrapper;

public class SystemPropertiesPanel extends JPanel {
    private JMenuItem copyMenuItem;
    private PopupListener copyPopupListener;
    private JPopupMenu copyPopupMenu;
    private JTable table;

    class 1 implements ActionListener {
        1() {
        }

        public void actionPerformed(ActionEvent e) {
            SystemPropertiesPanel.this.copySystemPropertiesToClipboard();
        }
    }

    private class PopupListener extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                SystemPropertiesPanel.this.getCopyPopupMenu().show(SystemPropertiesPanel.this.getTable(), e.getX(), e.getY());
            }
        }
    }

    public SystemPropertiesPanel() {
        String baseName = "org.jfree.ui.about.resources.AboutResources";
        ResourceBundle resources = ResourceBundleWrapper.getBundle("org.jfree.ui.about.resources.AboutResources");
        setLayout(new BorderLayout());
        this.table = SystemProperties.createSystemPropertiesTable();
        add(new JScrollPane(this.table));
        this.copyPopupMenu = new JPopupMenu();
        String label = resources.getString("system-properties-panel.popup-menu.copy");
        KeyStroke accelerator = (KeyStroke) resources.getObject("system-properties-panel.popup-menu.copy.accelerator");
        this.copyMenuItem = new JMenuItem(label);
        this.copyMenuItem.setAccelerator(accelerator);
        this.copyMenuItem.getAccessibleContext().setAccessibleDescription(label);
        this.copyMenuItem.addActionListener(new 1());
        this.copyPopupMenu.add(this.copyMenuItem);
        this.copyPopupListener = new PopupListener();
        this.table.addMouseListener(this.copyPopupListener);
    }

    public void copySystemPropertiesToClipboard() {
        StringBuffer buffer = new StringBuffer();
        ListSelectionModel selection = this.table.getSelectionModel();
        int firstRow = selection.getMinSelectionIndex();
        int lastRow = selection.getMaxSelectionIndex();
        if (!(firstRow == -1 || lastRow == -1)) {
            for (int r = firstRow; r <= lastRow; r++) {
                for (int c = 0; c < this.table.getColumnCount(); c++) {
                    buffer.append(this.table.getValueAt(r, c));
                    if (c != 2) {
                        buffer.append("\t");
                    }
                }
                buffer.append("\n");
            }
        }
        StringSelection ss = new StringSelection(buffer.toString());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, ss);
    }

    protected final JPopupMenu getCopyPopupMenu() {
        return this.copyPopupMenu;
    }

    protected final JTable getTable() {
        return this.table;
    }
}
