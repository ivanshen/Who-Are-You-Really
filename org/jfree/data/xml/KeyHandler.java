package org.jfree.data.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class KeyHandler extends DefaultHandler implements DatasetTags {
    private StringBuffer currentText;
    private ItemHandler itemHandler;
    private RootHandler rootHandler;

    public KeyHandler(RootHandler rootHandler, ItemHandler itemHandler) {
        this.rootHandler = rootHandler;
        this.itemHandler = itemHandler;
        this.currentText = new StringBuffer();
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        if (qName.equals(DatasetTags.KEY_TAG)) {
            clearCurrentText();
            return;
        }
        throw new SAXException("Expecting <Key> but found " + qName);
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        if (qName.equals(DatasetTags.KEY_TAG)) {
            this.itemHandler.setKey(getCurrentText());
            this.rootHandler.popSubHandler();
            this.rootHandler.pushSubHandler(new ValueHandler(this.rootHandler, this.itemHandler));
            return;
        }
        throw new SAXException("Expecting </Key> but found " + qName);
    }

    public void characters(char[] ch, int start, int length) {
        if (this.currentText != null) {
            this.currentText.append(String.copyValueOf(ch, start, length));
        }
    }

    protected String getCurrentText() {
        return this.currentText.toString();
    }

    protected void clearCurrentText() {
        this.currentText.delete(0, this.currentText.length());
    }
}
