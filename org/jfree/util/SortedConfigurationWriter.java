package org.jfree.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import net.miginfocom.layout.UnitValue;
import org.jfree.ui.Align;

public class SortedConfigurationWriter {
    private static final String END_OF_LINE;
    private static final int ESCAPE_COMMENT = 2;
    private static final int ESCAPE_KEY = 0;
    private static final int ESCAPE_VALUE = 1;
    private static final char[] HEX_CHARS;

    static {
        END_OF_LINE = StringUtils.getLineSeparator();
        HEX_CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    }

    protected String getDescription(String key) {
        return null;
    }

    public void save(String filename, Configuration config) throws IOException {
        save(new File(filename), config);
    }

    public void save(File file, Configuration config) throws IOException {
        OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        save(out, config);
        out.close();
    }

    public void save(OutputStream outStream, Configuration config) throws IOException {
        ArrayList names = new ArrayList();
        Iterator defaults = config.findPropertyKeys("");
        while (defaults.hasNext()) {
            names.add((String) defaults.next());
        }
        Collections.sort(names);
        OutputStreamWriter out = new OutputStreamWriter(outStream, "iso-8859-1");
        for (int i = ESCAPE_KEY; i < names.size(); i += ESCAPE_VALUE) {
            String key = (String) names.get(i);
            String value = config.getConfigProperty(key);
            String description = getDescription(key);
            if (description != null) {
                writeDescription(description, out);
            }
            saveConvert(key, ESCAPE_KEY, out);
            out.write("=");
            saveConvert(value, ESCAPE_VALUE, out);
            out.write(END_OF_LINE);
        }
        out.flush();
    }

    private void writeDescription(String text, Writer writer) throws IOException {
        if (text.length() != 0) {
            writer.write("# ");
            writer.write(END_OF_LINE);
            LineBreakIterator iterator = new LineBreakIterator(text);
            while (iterator.hasNext()) {
                writer.write("# ");
                saveConvert((String) iterator.next(), ESCAPE_COMMENT, writer);
                writer.write(END_OF_LINE);
            }
        }
    }

    private void saveConvert(String text, int escapeMode, Writer writer) throws IOException {
        char[] string = text.toCharArray();
        int x = ESCAPE_KEY;
        while (x < string.length) {
            char aChar = string[x];
            switch (aChar) {
                case Align.TOP_RIGHT /*9*/:
                    if (escapeMode != ESCAPE_COMMENT) {
                        writer.write(92);
                        writer.write(116);
                        break;
                    }
                    writer.write(aChar);
                    break;
                case Align.SOUTH_EAST /*10*/:
                    writer.write(92);
                    writer.write(110);
                    break;
                case Align.FIT_HORIZONTAL /*12*/:
                    if (escapeMode != ESCAPE_COMMENT) {
                        writer.write(92);
                        writer.write(UnitValue.SUB);
                        break;
                    }
                    writer.write(aChar);
                    break;
                case UnitValue.MIN_SIZE /*13*/:
                    writer.write(92);
                    writer.write(114);
                    break;
                case ' ':
                    if (escapeMode != ESCAPE_COMMENT && (x == 0 || escapeMode == 0)) {
                        writer.write(92);
                    }
                    writer.write(32);
                    break;
                case '!':
                case '\"':
                case '#':
                case ':':
                case '=':
                    if (escapeMode != ESCAPE_COMMENT) {
                        writer.write(92);
                        writer.write(aChar);
                        break;
                    }
                    writer.write(aChar);
                    break;
                case '\\':
                    writer.write(92);
                    writer.write(92);
                    break;
                default:
                    if (aChar >= ' ' && aChar <= '~') {
                        writer.write(aChar);
                        break;
                    }
                    writer.write(92);
                    writer.write(117);
                    writer.write(HEX_CHARS[(aChar >> 12) & 15]);
                    writer.write(HEX_CHARS[(aChar >> 8) & 15]);
                    writer.write(HEX_CHARS[(aChar >> 4) & 15]);
                    writer.write(HEX_CHARS[aChar & 15]);
                    break;
                    break;
            }
            x += ESCAPE_VALUE;
        }
    }
}
