package org.jfree.ui.about;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class Licences {
    public static final String GPL = "GNU GENERAL PUBLIC LICENSE\n";
    public static final String LGPL = "GNU LESSER GENERAL PUBLIC LICENSE\n";
    private static Licences singleton;

    public static Licences getInstance() {
        if (singleton == null) {
            singleton = new Licences();
        }
        return singleton;
    }

    public String getGPL() {
        return readStringResource("gpl-2.0.txt");
    }

    public String getLGPL() {
        return readStringResource("lgpl-2.1.txt");
    }

    private String readStringResource(String name) {
        UnsupportedEncodingException ex;
        Throwable th;
        IOException e;
        StringBuilder sb = new StringBuilder();
        InputStreamReader inputStreamReader = null;
        try {
            InputStreamReader streamReader = new InputStreamReader(getClass().getResourceAsStream(name), "UTF-8");
            try {
                BufferedReader in = new BufferedReader(streamReader);
                while (true) {
                    String line = in.readLine();
                    if (line != null) {
                        sb.append(line).append("\n");
                    } else {
                        try {
                            streamReader.close();
                            return sb.toString();
                        } catch (IOException ex2) {
                            throw new RuntimeException(ex2);
                        }
                    }
                }
            } catch (UnsupportedEncodingException e2) {
                ex = e2;
                inputStreamReader = streamReader;
                try {
                    throw new RuntimeException(ex);
                } catch (Throwable th2) {
                    th = th2;
                    try {
                        inputStreamReader.close();
                        throw th;
                    } catch (IOException ex22) {
                        throw new RuntimeException(ex22);
                    }
                }
            } catch (IOException e3) {
                e = e3;
                inputStreamReader = streamReader;
                throw new RuntimeException(e);
            } catch (Throwable th3) {
                th = th3;
                inputStreamReader = streamReader;
                inputStreamReader.close();
                throw th;
            }
        } catch (UnsupportedEncodingException e4) {
            ex = e4;
            throw new RuntimeException(ex);
        } catch (IOException e5) {
            e = e5;
            throw new RuntimeException(e);
        }
    }
}
