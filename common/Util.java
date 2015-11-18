package common;

import io.Parser;
import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

public class Util {
    public static final Font CHOICE_FONT;
    public static final Font QUESTION_FONT;
    public static final FileFilter TXT_FILTER;
    private static Logger logger;

    class 1 extends FileFilter {
        1() {
        }

        public String getDescription() {
            return "Survey files (.txt)";
        }

        public boolean accept(File f) {
            return f.isDirectory() || Parser.fileFilter.accept(f);
        }
    }

    class 2 extends FileFilter {
        private final /* synthetic */ String val$description;
        private final /* synthetic */ String val$pattern;

        2(String str, String str2) {
            this.val$description = str;
            this.val$pattern = str2;
        }

        public String getDescription() {
            return this.val$description;
        }

        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            return f.getName().toLowerCase().matches(this.val$pattern);
        }
    }

    static {
        logger = Logger.getLogger(Util.class.getName());
        QUESTION_FONT = new Font("Times New Roman", 0, 23);
        CHOICE_FONT = new Font("Times New Roman", 0, 20);
        TXT_FILTER = new 1();
    }

    public static Image getImageFromJComponent(JComponent src) {
        BufferedImage res = new BufferedImage(src.getWidth(), src.getHeight(), 2);
        src.paint(res.getGraphics());
        return res;
    }

    public static void showError(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error", 0);
    }

    public static boolean showConfirm(String msg) {
        return JOptionPane.showConfirmDialog(null, msg, "Confirm", 0) == 0;
    }

    public static File chooseFile(String btnText, FileFilter f) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(f);
        chooser.setVisible(true);
        if (chooser.showDialog(null, btnText) == 0) {
            logger.log(Level.INFO, "Got\t" + chooser.getSelectedFile().getAbsolutePath());
            return chooser.getSelectedFile();
        }
        logger.log(Level.INFO, "Error");
        return null;
    }

    public static FileFilter createFileFilter(String description, String pattern) {
        return new 2(description, pattern);
    }

    public static void open(File file) {
        try {
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            showError("Error opening " + file.getAbsolutePath() + ":\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void openWebpage(URI uri) {
        Desktop desktop = Desktop.getDesktop();
        if (desktop != null && desktop.isSupported(Action.BROWSE)) {
            try {
                desktop.browse(uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void openWebpage(URL url) {
        try {
            openWebpage(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
