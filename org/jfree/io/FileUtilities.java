package org.jfree.io;

import java.io.File;
import java.util.StringTokenizer;

public class FileUtilities {
    private FileUtilities() {
    }

    public static File findFileOnClassPath(String name) {
        StringTokenizer tokenizer = new StringTokenizer(System.getProperty("java.class.path"), System.getProperty("path.separator"));
        while (tokenizer.hasMoreTokens()) {
            File directoryOrJar = new File(tokenizer.nextToken());
            File absoluteDirectoryOrJar = directoryOrJar.getAbsoluteFile();
            File target;
            if (absoluteDirectoryOrJar.isFile()) {
                target = new File(absoluteDirectoryOrJar.getParent(), name);
                if (target.exists()) {
                    return target;
                }
            } else {
                target = new File(directoryOrJar, name);
                if (target.exists()) {
                    return target;
                }
            }
        }
        return null;
    }
}
