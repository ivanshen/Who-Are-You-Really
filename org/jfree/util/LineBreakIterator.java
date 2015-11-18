package org.jfree.util;

import java.util.Iterator;

public class LineBreakIterator implements Iterator {
    public static final int DONE = -1;
    private int position;
    private char[] text;

    public LineBreakIterator() {
        setText("");
    }

    public LineBreakIterator(String text) {
        setText(text);
    }

    public synchronized int nextPosition() {
        int i = DONE;
        synchronized (this) {
            if (this.text != null) {
                if (this.position != DONE) {
                    int nChars = this.text.length;
                    int nextChar = this.position;
                    while (nextChar < nChars) {
                        boolean eol = false;
                        char c = '\u0000';
                        int i2 = nextChar;
                        while (i2 < nChars) {
                            c = this.text[i2];
                            if (c == '\n' || c == '\r') {
                                eol = true;
                                break;
                            }
                            i2++;
                        }
                        nextChar = i2;
                        if (eol) {
                            nextChar++;
                            if (c == '\r' && nextChar < nChars && this.text[nextChar] == '\n') {
                                nextChar++;
                            }
                            this.position = nextChar;
                            i = this.position;
                        }
                    }
                    this.position = DONE;
                }
            }
        }
        return i;
    }

    public int nextWithEnd() {
        int pos = this.position;
        if (pos == DONE) {
            return DONE;
        }
        if (pos == this.text.length) {
            this.position = DONE;
            return DONE;
        }
        int retval = nextPosition();
        if (retval == DONE) {
            return this.text.length;
        }
        return retval;
    }

    public String getText() {
        return new String(this.text);
    }

    public void setText(String text) {
        this.position = 0;
        this.text = text.toCharArray();
    }

    public boolean hasNext() {
        return this.position != DONE;
    }

    public Object next() {
        if (this.position == DONE) {
            return null;
        }
        int lastFound = this.position;
        int pos = nextWithEnd();
        if (pos == DONE) {
            return new String(this.text, lastFound, this.text.length - lastFound);
        }
        if (pos > 0) {
            int end = lastFound;
            while (pos > end && (this.text[pos + DONE] == '\n' || this.text[pos + DONE] == '\r')) {
                pos += DONE;
            }
        }
        return new String(this.text, lastFound, pos - lastFound);
    }

    public void remove() {
        throw new UnsupportedOperationException("This iterator is read-only.");
    }
}
