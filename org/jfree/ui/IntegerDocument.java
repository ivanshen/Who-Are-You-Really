package org.jfree.ui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class IntegerDocument extends PlainDocument {
    public void insertString(int i, String s, AttributeSet attributes) throws BadLocationException {
        super.insertString(i, s, attributes);
        if (s == null) {
            return;
        }
        if (!s.equals("-") || i != 0 || s.length() >= 2) {
            try {
                Integer.parseInt(getText(0, getLength()));
            } catch (NumberFormatException e) {
                remove(i, s.length());
            }
        }
    }
}
