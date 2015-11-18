package org.jfree.ui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class FontChooserDialog extends StandardDialog {
    private FontChooserPanel fontChooserPanel;

    public FontChooserDialog(Dialog owner, String title, boolean modal, Font font) {
        super(owner, title, modal);
        setContentPane(createContent(font));
    }

    public FontChooserDialog(Frame owner, String title, boolean modal, Font font) {
        super(owner, title, modal);
        setContentPane(createContent(font));
    }

    public Font getSelectedFont() {
        return this.fontChooserPanel.getSelectedFont();
    }

    private JPanel createContent(Font font) {
        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        if (font == null) {
            font = new Font("Dialog", 10, 0);
        }
        this.fontChooserPanel = new FontChooserPanel(font);
        content.add(this.fontChooserPanel);
        JPanel buttons = createButtonPanel();
        buttons.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        content.add(buttons, "South");
        return content;
    }
}
