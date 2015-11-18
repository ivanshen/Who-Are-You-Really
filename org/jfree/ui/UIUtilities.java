package org.jfree.ui;

import java.awt.Color;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.BorderUIResource.CompoundBorderUIResource;
import javax.swing.plaf.BorderUIResource.EmptyBorderUIResource;
import javax.swing.plaf.BorderUIResource.EtchedBorderUIResource;

public class UIUtilities {
    private UIUtilities() {
    }

    public static void setupUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        UIDefaults defaults = UIManager.getDefaults();
        defaults.put("PopupMenu.border", new EtchedBorderUIResource(0, defaults.getColor("controlShadow"), defaults.getColor("controlLtHighlight")));
        CompoundBorderUIResource compBorder = new CompoundBorderUIResource(new MatteBorder(2, 2, 2, 2, defaults.getColor("control")), new MatteBorder(1, 1, 1, 1, Color.black));
        EmptyBorderUIResource emptyBorderUI = new EmptyBorderUIResource(0, 0, 0, 0);
        defaults.put("SplitPane.border", emptyBorderUI);
        defaults.put("Table.scrollPaneBorder", emptyBorderUI);
        defaults.put("ComboBox.border", compBorder);
        defaults.put("TextField.border", compBorder);
        defaults.put("TextArea.border", compBorder);
        defaults.put("CheckBox.border", compBorder);
        defaults.put("ScrollPane.border", emptyBorderUI);
    }
}
