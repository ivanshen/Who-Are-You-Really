package org.jfree.ui.about;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import org.jfree.base.Library;

public class LibraryPanel extends JPanel {
    private LibraryTableModel model;
    private JTable table;

    public LibraryPanel(List libraries) {
        setLayout(new BorderLayout());
        this.model = new LibraryTableModel(libraries);
        this.table = new JTable(this.model);
        add(new JScrollPane(this.table));
    }

    public LibraryPanel(ProjectInfo projectInfo) {
        this(getLibraries(projectInfo));
    }

    private static List getLibraries(ProjectInfo info) {
        if (info == null) {
            return new ArrayList();
        }
        List libs = new ArrayList();
        collectLibraries(info, libs);
        return libs;
    }

    private static void collectLibraries(ProjectInfo info, List list) {
        Library[] libs = info.getLibraries();
        for (Library lib : libs) {
            if (!list.contains(lib)) {
                list.add(lib);
                if (lib instanceof ProjectInfo) {
                    collectLibraries((ProjectInfo) lib, list);
                }
            }
        }
        libs = info.getOptionalLibraries();
        for (Library lib2 : libs) {
            if (!list.contains(lib2)) {
                list.add(lib2);
                if (lib2 instanceof ProjectInfo) {
                    collectLibraries((ProjectInfo) lib2, list);
                }
            }
        }
    }

    public LibraryTableModel getModel() {
        return this.model;
    }

    protected JTable getTable() {
        return this.table;
    }
}
