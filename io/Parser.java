package io;

import common.Survey;
import common.Util;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Parser {
    public static final FileFilter fileFilter;
    private static Logger logger;

    class 1 implements FileFilter {
        1() {
        }

        public boolean accept(File pathname) {
            return pathname.getName().endsWith(".txt");
        }
    }

    static {
        logger = Logger.getLogger(Parser.class.getName());
        fileFilter = new 1();
    }

    public static String slurp(File file) throws IOException {
        char[] buffer = new char[((int) file.length())];
        Reader in = new FileReader(file);
        StringBuilder sb = new StringBuilder((int) file.length());
        while (in.read(buffer) > 0) {
            sb.append(buffer);
        }
        in.close();
        return sb.toString();
    }

    public static Survey readSurvey(File file) throws IOException {
        Survey s = Survey.parse(slurp(file));
        if (s == null) {
            Util.showError("Format error for file at " + file.getAbsolutePath() + " !");
        }
        return s;
    }

    public static Survey[] readAll(File dir) throws IOException, IllegalArgumentException {
        if (dir.isFile()) {
            throw new IllegalArgumentException("File given as argument instead of directory!");
        }
        File[] files = dir.listFiles(fileFilter);
        if (files == null) {
            throw new IOException("Error reading files from " + dir.getAbsolutePath());
        }
        Survey[] surveys = new Survey[files.length];
        for (int i = 0; i < files.length; i++) {
            logger.log(Level.INFO, "Reading " + files[i].getAbsolutePath());
            try {
                surveys[i] = readSurvey(files[i]);
            } catch (Exception e) {
                System.err.println("Error occurred while reading the maze@" + files[i].getAbsolutePath() + ":");
                e.printStackTrace();
            }
        }
        return surveys;
    }
}
