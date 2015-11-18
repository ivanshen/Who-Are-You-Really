package org.jfree.data.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ItemHandler extends DefaultHandler implements DatasetTags {
    private Comparable key;
    private DefaultHandler parent;
    private RootHandler root;
    private Number value;

    public ItemHandler(RootHandler root, DefaultHandler parent) {
        this.root = root;
        this.parent = parent;
        this.key = null;
        this.value = null;
    }

    public Comparable getKey() {
        return this.key;
    }

    public void setKey(Comparable key) {
        this.key = key;
    }

    public Number getValue() {
        return this.value;
    }

    public void setValue(Number value) {
        this.value = value;
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        if (qName.equals(DatasetTags.ITEM_TAG)) {
            this.root.pushSubHandler(new KeyHandler(this.root, this));
        } else if (qName.equals(DatasetTags.VALUE_TAG)) {
            this.root.pushSubHandler(new ValueHandler(this.root, this));
        } else {
            throw new SAXException("Expected <Item> or <Value>...found " + qName);
        }
    }

    public void endElement(String namespaceURI, String localName, String qName) {
        if (this.parent instanceof PieDatasetHandler) {
            this.parent.addItem(this.key, this.value);
            this.root.popSubHandler();
        } else if (this.parent instanceof CategorySeriesHandler) {
            this.parent.addItem(this.key, this.value);
            this.root.popSubHandler();
        }
    }
}
