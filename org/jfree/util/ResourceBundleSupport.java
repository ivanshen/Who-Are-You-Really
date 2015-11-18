package org.jfree.util;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.KeyStroke;

public class ResourceBundleSupport {
    private TreeMap cache;
    private Locale locale;
    private TreeSet lookupPath;
    private String resourceBase;
    private ResourceBundle resources;

    public ResourceBundleSupport(Locale locale, String baseName) {
        this(locale, ResourceBundleWrapper.getBundle(baseName, locale), baseName);
    }

    protected ResourceBundleSupport(Locale locale, ResourceBundle resourceBundle, String baseName) {
        if (locale == null) {
            throw new NullPointerException("Locale must not be null");
        } else if (resourceBundle == null) {
            throw new NullPointerException("Resources must not be null");
        } else if (baseName == null) {
            throw new NullPointerException("BaseName must not be null");
        } else {
            this.locale = locale;
            this.resources = resourceBundle;
            this.resourceBase = baseName;
            this.cache = new TreeMap();
            this.lookupPath = new TreeSet();
        }
    }

    public ResourceBundleSupport(Locale locale, ResourceBundle resourceBundle) {
        this(locale, resourceBundle, resourceBundle.toString());
    }

    public ResourceBundleSupport(String baseName) {
        this(Locale.getDefault(), ResourceBundleWrapper.getBundle(baseName), baseName);
    }

    protected ResourceBundleSupport(ResourceBundle resourceBundle, String baseName) {
        this(Locale.getDefault(), resourceBundle, baseName);
    }

    public ResourceBundleSupport(ResourceBundle resourceBundle) {
        this(Locale.getDefault(), resourceBundle, resourceBundle.toString());
    }

    protected final String getResourceBase() {
        return this.resourceBase;
    }

    public synchronized String getString(String key) {
        String retval;
        retval = (String) this.cache.get(key);
        if (retval == null) {
            this.lookupPath.clear();
            retval = internalGetString(key);
        }
        return retval;
    }

    protected String internalGetString(String key) {
        if (this.lookupPath.contains(key)) {
            throw new MissingResourceException("InfiniteLoop in resource lookup", getResourceBase(), this.lookupPath.toString());
        }
        String fromResBundle = this.resources.getString(key);
        if (fromResBundle.startsWith("@@")) {
            int idx = fromResBundle.indexOf(64, 2);
            if (idx == -1) {
                throw new MissingResourceException("Invalid format for global lookup key.", getResourceBase(), key);
            }
            try {
                return ResourceBundleWrapper.getBundle(fromResBundle.substring(2, idx)).getString(fromResBundle.substring(idx + 1));
            } catch (Exception e) {
                Log.error("Error during global lookup", e);
                throw new MissingResourceException("Error during global lookup", getResourceBase(), key);
            }
        } else if (fromResBundle.startsWith("@")) {
            String newKey = fromResBundle.substring(1);
            this.lookupPath.add(key);
            String retval = internalGetString(newKey);
            this.cache.put(key, retval);
            return retval;
        } else {
            this.cache.put(key, fromResBundle);
            return fromResBundle;
        }
    }

    public Icon getIcon(String key, boolean large) {
        return createIcon(getString(key), true, large);
    }

    public Icon getIcon(String key) {
        return createIcon(getString(key), false, false);
    }

    public Integer getMnemonic(String key) {
        return createMnemonic(getString(key));
    }

    public Integer getOptionalMnemonic(String key) {
        String name = getString(key);
        if (name == null || name.length() <= 0) {
            return null;
        }
        return createMnemonic(name);
    }

    public KeyStroke getKeyStroke(String key) {
        return getKeyStroke(key, getMenuKeyMask());
    }

    public KeyStroke getOptionalKeyStroke(String key) {
        return getOptionalKeyStroke(key, getMenuKeyMask());
    }

    public KeyStroke getKeyStroke(String key, int mask) {
        return KeyStroke.getKeyStroke(createMnemonic(getString(key)).intValue(), mask);
    }

    public KeyStroke getOptionalKeyStroke(String key, int mask) {
        String name = getString(key);
        if (name == null || name.length() <= 0) {
            return null;
        }
        return KeyStroke.getKeyStroke(createMnemonic(name).intValue(), mask);
    }

    public JMenu createMenu(String keyPrefix) {
        JMenu retval = new JMenu();
        retval.setText(getString(keyPrefix + ".name"));
        retval.setMnemonic(getMnemonic(keyPrefix + ".mnemonic").intValue());
        return retval;
    }

    public URL getResourceURL(String key) {
        String name = getString(key);
        URL in = ObjectUtilities.getResource(name, ResourceBundleSupport.class);
        if (in == null) {
            Log.warn("Unable to find file in the class path: " + name + "; key=" + key);
        }
        return in;
    }

    private ImageIcon createIcon(String resourceName, boolean scale, boolean large) {
        URL in = ObjectUtilities.getResource(resourceName, ResourceBundleSupport.class);
        if (in == null) {
            Log.warn("Unable to find file in the class path: " + resourceName);
            return new ImageIcon(createTransparentImage(1, 1));
        }
        Image img = Toolkit.getDefaultToolkit().createImage(in);
        if (img == null) {
            Log.warn("Unable to instantiate the image: " + resourceName);
            return new ImageIcon(createTransparentImage(1, 1));
        } else if (!scale) {
            return new ImageIcon(img);
        } else {
            if (large) {
                return new ImageIcon(img.getScaledInstance(24, 24, 4));
            }
            return new ImageIcon(img.getScaledInstance(16, 16, 4));
        }
    }

    private Integer createMnemonic(String keyString) {
        if (keyString == null) {
            throw new NullPointerException("Key is null.");
        } else if (keyString.length() == 0) {
            throw new IllegalArgumentException("Key is empty.");
        } else {
            int character = keyString.charAt(0);
            if (keyString.startsWith("VK_")) {
                try {
                    character = ((Integer) KeyEvent.class.getField(keyString).get(null)).intValue();
                } catch (Exception e) {
                }
            }
            return new Integer(character);
        }
    }

    private int getMenuKeyMask() {
        try {
            return Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        } catch (UnsupportedOperationException e) {
            return 2;
        }
    }

    private BufferedImage createTransparentImage(int width, int height) {
        BufferedImage img = new BufferedImage(width, height, 2);
        int[] data = img.getRGB(0, 0, width, height, null, 0, width);
        Arrays.fill(data, 0);
        img.setRGB(0, 0, width, height, data, 0, width);
        return img;
    }

    public Icon createTransparentIcon(int width, int height) {
        return new ImageIcon(createTransparentImage(width, height));
    }

    public String formatMessage(String key, Object parameter) {
        return formatMessage(key, new Object[]{parameter});
    }

    public String formatMessage(String key, Object par1, Object par2) {
        return formatMessage(key, new Object[]{par1, par2});
    }

    public String formatMessage(String key, Object[] parameters) {
        MessageFormat format = new MessageFormat(getString(key));
        format.setLocale(getLocale());
        return format.format(parameters);
    }

    public Locale getLocale() {
        return this.locale;
    }
}
