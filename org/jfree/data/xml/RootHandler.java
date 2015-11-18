package org.jfree.data.xml;

import java.util.Stack;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class RootHandler extends DefaultHandler implements DatasetTags {
    private Stack subHandlers;

    public RootHandler() {
        this.subHandlers = new Stack();
    }

    public Stack getSubHandlers() {
        return this.subHandlers;
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        DefaultHandler handler = getCurrentHandler();
        if (handler != this) {
            handler.characters(ch, start, length);
        }
    }

    public DefaultHandler getCurrentHandler() {
        DefaultHandler result = this;
        if (this.subHandlers == null || this.subHandlers.size() <= 0) {
            return result;
        }
        DefaultHandler top = this.subHandlers.peek();
        if (top != null) {
            return top;
        }
        return result;
    }

    public void pushSubHandler(DefaultHandler subhandler) {
        this.subHandlers.push(subhandler);
    }

    public DefaultHandler popSubHandler() {
        return (DefaultHandler) this.subHandlers.pop();
    }
}
