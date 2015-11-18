package org.jfree.ui.about;

import java.awt.BorderLayout;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

public class ContributorsPanel extends JPanel {
    private TableModel model;
    private JTable table;

    public ContributorsPanel(List contributors) {
        setLayout(new BorderLayout());
        this.model = new ContributorsTableModel(contributors);
        this.table = new JTable(this.model);
        add(new JScrollPane(this.table));
    }
}
