package org.jfree.data.xml;

import org.jfree.data.DefaultKeyedValues;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class CategorySeriesHandler extends DefaultHandler implements DatasetTags {
    private RootHandler root;
    private Comparable seriesKey;
    private DefaultKeyedValues values;

    public CategorySeriesHandler(RootHandler root) {
        this.root = root;
        this.values = new DefaultKeyedValues();
    }

    public void setSeriesKey(Comparable key) {
        this.seriesKey = key;
    }

    public void addItem(Comparable key, Number value) {
        this.values.addValue(key, value);
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        if (qName.equals(DatasetTags.SERIES_TAG)) {
            setSeriesKey(atts.getValue("name"));
            this.root.pushSubHandler(new ItemHandler(this.root, this));
        } else if (qName.equals(DatasetTags.ITEM_TAG)) {
            ItemHandler subhandler = new ItemHandler(this.root, this);
            this.root.pushSubHandler(subhandler);
            subhandler.startElement(namespaceURI, localName, qName, atts);
        } else {
            throw new SAXException("Expecting <Series> or <Item> tag...found " + qName);
        }
    }

    public void endElement(String namespaceURI, String localName, String qName) {
        if (this.root instanceof CategoryDatasetHandler) {
            CategoryDatasetHandler handler = this.root;
            for (Comparable key : this.values.getKeys()) {
                handler.addItem(this.seriesKey, key, this.values.getValue(key));
            }
            this.root.popSubHandler();
        }
    }
}
