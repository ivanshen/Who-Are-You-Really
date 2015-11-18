package org.jfree.ui;

import java.awt.Font;
import java.util.ResourceBundle;
import javax.swing.JTextField;
import org.jfree.util.ResourceBundleWrapper;

public class FontDisplayField extends JTextField {
    protected static final ResourceBundle localizationResources;
    private Font displayFont;

    static {
        localizationResources = ResourceBundleWrapper.getBundle("org.jfree.ui.LocalizationBundle");
    }

    public FontDisplayField(Font font) {
        super("");
        setDisplayFont(font);
        setEnabled(false);
    }

    public Font getDisplayFont() {
        return this.displayFont;
    }

    public void setDisplayFont(Font font) {
        this.displayFont = font;
        setText(fontToString(this.displayFont));
    }

    private String fontToString(Font font) {
        if (font != null) {
            return font.getFontName() + ", " + font.getSize();
        }
        return localizationResources.getString("No_Font_Selected");
    }
}
