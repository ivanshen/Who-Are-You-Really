package org.jfree.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import org.jfree.util.ResourceBundleWrapper;

public class FontChooserPanel extends JPanel {
    public static final String[] SIZES;
    protected static ResourceBundle localizationResources;
    private JCheckBox bold;
    private JList fontlist;
    private JCheckBox italic;
    private JList sizelist;

    static {
        SIZES = new String[]{"9", "10", "11", "12", "14", "16", "18", "20", "22", "24", "28", "36", "48", "72"};
        localizationResources = ResourceBundleWrapper.getBundle("org.jfree.ui.LocalizationBundle");
    }

    public FontChooserPanel(Font font) {
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        setLayout(new BorderLayout());
        JPanel right = new JPanel(new BorderLayout());
        JPanel fontPanel = new JPanel(new BorderLayout());
        fontPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), localizationResources.getString("Font")));
        this.fontlist = new JList(fonts);
        JScrollPane fontpane = new JScrollPane(this.fontlist);
        fontpane.setBorder(BorderFactory.createEtchedBorder());
        fontPanel.add(fontpane);
        add(fontPanel);
        JPanel sizePanel = new JPanel(new BorderLayout());
        sizePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), localizationResources.getString("Size")));
        this.sizelist = new JList(SIZES);
        JScrollPane sizepane = new JScrollPane(this.sizelist);
        sizepane.setBorder(BorderFactory.createEtchedBorder());
        sizePanel.add(sizepane);
        JPanel attributes = new JPanel(new GridLayout(1, 2));
        this.bold = new JCheckBox(localizationResources.getString("Bold"));
        this.italic = new JCheckBox(localizationResources.getString("Italic"));
        attributes.add(this.bold);
        attributes.add(this.italic);
        attributes.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), localizationResources.getString("Attributes")));
        right.add(sizePanel, "Center");
        right.add(attributes, "South");
        add(right, "East");
        setSelectedFont(font);
    }

    public Font getSelectedFont() {
        return new Font(getSelectedName(), getSelectedStyle(), getSelectedSize());
    }

    public String getSelectedName() {
        return (String) this.fontlist.getSelectedValue();
    }

    public int getSelectedStyle() {
        if (this.bold.isSelected() && this.italic.isSelected()) {
            return 3;
        }
        if (this.bold.isSelected()) {
            return 1;
        }
        if (this.italic.isSelected()) {
            return 2;
        }
        return 0;
    }

    public int getSelectedSize() {
        String selected = (String) this.sizelist.getSelectedValue();
        if (selected != null) {
            return Integer.parseInt(selected);
        }
        return 10;
    }

    public void setSelectedFont(Font font) {
        if (font == null) {
            throw new NullPointerException();
        }
        int i;
        this.bold.setSelected(font.isBold());
        this.italic.setSelected(font.isItalic());
        String fontName = font.getName();
        ListModel model = this.fontlist.getModel();
        this.fontlist.clearSelection();
        for (i = 0; i < model.getSize(); i++) {
            if (fontName.equals(model.getElementAt(i))) {
                this.fontlist.setSelectedIndex(i);
                break;
            }
        }
        String fontSize = String.valueOf(font.getSize());
        model = this.sizelist.getModel();
        this.sizelist.clearSelection();
        for (i = 0; i < model.getSize(); i++) {
            if (fontSize.equals(model.getElementAt(i))) {
                this.sizelist.setSelectedIndex(i);
                return;
            }
        }
    }
}
