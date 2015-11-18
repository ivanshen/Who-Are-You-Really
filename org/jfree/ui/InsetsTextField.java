package org.jfree.ui;

import java.awt.Insets;
import java.util.ResourceBundle;
import javax.swing.JTextField;
import org.jfree.util.ResourceBundleWrapper;

public class InsetsTextField extends JTextField {
    protected static ResourceBundle localizationResources;

    static {
        localizationResources = ResourceBundleWrapper.getBundle("org.jfree.ui.LocalizationBundle");
    }

    public InsetsTextField(Insets insets) {
        setInsets(insets);
        setEnabled(false);
    }

    public String formatInsetsString(Insets insets) {
        if (insets == null) {
            insets = new Insets(0, 0, 0, 0);
        }
        return localizationResources.getString("T") + insets.top + ", " + localizationResources.getString("L") + insets.left + ", " + localizationResources.getString("B") + insets.bottom + ", " + localizationResources.getString("R") + insets.right;
    }

    public void setInsets(Insets insets) {
        setText(formatInsetsString(insets));
    }
}
