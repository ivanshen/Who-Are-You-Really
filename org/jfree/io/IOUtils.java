package org.jfree.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class IOUtils {
    private static IOUtils instance;

    private IOUtils() {
    }

    public static IOUtils getInstance() {
        if (instance == null) {
            instance = new IOUtils();
        }
        return instance;
    }

    private boolean isFileStyleProtocol(URL url) {
        if (url.getProtocol().equals("http") || url.getProtocol().equals("https") || url.getProtocol().equals("ftp") || url.getProtocol().equals("file") || url.getProtocol().equals("jar")) {
            return true;
        }
        return false;
    }

    private List parseName(String name) {
        ArrayList list = new ArrayList();
        StringTokenizer strTok = new StringTokenizer(name, "/");
        while (strTok.hasMoreElements()) {
            String s = (String) strTok.nextElement();
            if (s.length() != 0) {
                list.add(s);
            }
        }
        return list;
    }

    private String formatName(List name, String query) {
        StringBuffer b = new StringBuffer();
        Iterator it = name.iterator();
        while (it.hasNext()) {
            b.append(it.next());
            if (it.hasNext()) {
                b.append("/");
            }
        }
        if (query != null) {
            b.append('?');
            b.append(query);
        }
        return b.toString();
    }

    private int startsWithUntil(List baseName, List urlName) {
        int minIdx = Math.min(urlName.size(), baseName.size());
        for (int i = 0; i < minIdx; i++) {
            if (!((String) baseName.get(i)).equals((String) urlName.get(i))) {
                return i;
            }
        }
        return minIdx;
    }

    private boolean isSameService(URL url, URL baseUrl) {
        if (url.getProtocol().equals(baseUrl.getProtocol()) && url.getHost().equals(baseUrl.getHost()) && url.getPort() == baseUrl.getPort()) {
            return true;
        }
        return false;
    }

    public String createRelativeURL(URL url, URL baseURL) {
        if (url == null) {
            throw new NullPointerException("content url must not be null.");
        } else if (baseURL == null) {
            throw new NullPointerException("baseURL must not be null.");
        } else if (!isFileStyleProtocol(url) || !isSameService(url, baseURL)) {
            return url.toExternalForm();
        } else {
            List urlName = parseName(getPath(url));
            List baseName = parseName(getPath(baseURL));
            String query = getQuery(url);
            if (!isPath(baseURL)) {
                baseName.remove(baseName.size() - 1);
            }
            if (url.equals(baseURL)) {
                return (String) urlName.get(urlName.size() - 1);
            }
            int commonIndex = startsWithUntil(urlName, baseName);
            if (commonIndex == 0) {
                return url.toExternalForm();
            }
            if (commonIndex == urlName.size()) {
                commonIndex--;
            }
            ArrayList retval = new ArrayList();
            if (baseName.size() >= urlName.size()) {
                int levels = baseName.size() - commonIndex;
                for (int i = 0; i < levels; i++) {
                    retval.add("..");
                }
            }
            retval.addAll(urlName.subList(commonIndex, urlName.size()));
            return formatName(retval, query);
        }
    }

    private boolean isPath(URL baseURL) {
        if (getPath(baseURL).endsWith("/")) {
            return true;
        }
        if (baseURL.getProtocol().equals("file")) {
            try {
                if (new File(getPath(baseURL)).isDirectory()) {
                    return true;
                }
            } catch (SecurityException e) {
            }
        }
        return false;
    }

    private String getQuery(URL url) {
        String file = url.getFile();
        int queryIndex = file.indexOf(63);
        if (queryIndex == -1) {
            return null;
        }
        return file.substring(queryIndex + 1);
    }

    private String getPath(URL url) {
        String file = url.getFile();
        int queryIndex = file.indexOf(63);
        return queryIndex == -1 ? file : file.substring(0, queryIndex);
    }

    public void copyStreams(InputStream in, OutputStream out) throws IOException {
        copyStreams(in, out, 4096);
    }

    public void copyStreams(InputStream in, OutputStream out, int buffersize) throws IOException {
        byte[] bytes = new byte[buffersize];
        int bytesRead = in.read(bytes);
        while (bytesRead > -1) {
            out.write(bytes, 0, bytesRead);
            bytesRead = in.read(bytes);
        }
    }

    public void copyWriter(Reader in, Writer out) throws IOException {
        copyWriter(in, out, 4096);
    }

    public void copyWriter(Reader in, Writer out, int buffersize) throws IOException {
        char[] bytes = new char[buffersize];
        int bytesRead = in.read(bytes);
        while (bytesRead > -1) {
            out.write(bytes, 0, bytesRead);
            bytesRead = in.read(bytes);
        }
    }

    public String getFileName(URL url) {
        String file = getPath(url);
        int last = file.lastIndexOf("/");
        return last < 0 ? file : file.substring(last + 1);
    }

    public String stripFileExtension(String file) {
        int idx = file.lastIndexOf(".");
        return idx < 1 ? file : file.substring(0, idx);
    }

    public String getFileExtension(String file) {
        int idx = file.lastIndexOf(".");
        if (idx < 1) {
            return "";
        }
        return file.substring(idx);
    }

    public boolean isSubDirectory(File base, File child) throws IOException {
        base = base.getCanonicalFile();
        for (File parentFile = child.getCanonicalFile(); parentFile != null; parentFile = parentFile.getParentFile()) {
            if (base.equals(parentFile)) {
                return true;
            }
        }
        return false;
    }
}
