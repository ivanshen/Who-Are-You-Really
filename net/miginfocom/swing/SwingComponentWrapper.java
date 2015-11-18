package net.miginfocom.swing;

import java.awt.BasicStroke;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Label;
import java.awt.List;
import java.awt.Point;
import java.awt.ScrollPane;
import java.awt.Scrollbar;
import java.awt.TextComponent;
import java.awt.TextField;
import java.awt.geom.Point2D.Float;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.JTextComponent;
import net.miginfocom.layout.ComponentWrapper;
import net.miginfocom.layout.ContainerWrapper;
import net.miginfocom.layout.PlatformDefaults;
import net.miginfocom.layout.UnitValue;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.plot.MeterPlot;
import org.jfree.chart.plot.Plot;

public class SwingComponentWrapper implements ComponentWrapper {
    private static Method BL_METHOD;
    private static Method BL_RES_METHOD;
    private static final Color DB_COMP_OUTLINE;
    private static final IdentityHashMap<FontMetrics, Float> FM_MAP;
    private static Method IMS_METHOD;
    private static final Font SUBST_FONT;
    private static boolean maxSet;
    private static boolean vp;
    private Boolean bl;
    private final Component c;
    private int compType;
    private boolean prefCalled;

    static {
        maxSet = false;
        vp = true;
        DB_COMP_OUTLINE = new Color(0, 0, ChartPanel.DEFAULT_MINIMUM_DRAW_HEIGHT);
        FM_MAP = new IdentityHashMap(4);
        SUBST_FONT = new Font("sansserif", 0, 11);
        BL_METHOD = null;
        BL_RES_METHOD = null;
        try {
            BL_METHOD = Component.class.getDeclaredMethod("getBaseline", new Class[]{Integer.TYPE, Integer.TYPE});
            BL_RES_METHOD = Component.class.getDeclaredMethod("getBaselineResizeBehavior", new Class[0]);
        } catch (Throwable th) {
        }
        IMS_METHOD = null;
        try {
            IMS_METHOD = Component.class.getDeclaredMethod("isMaximumSizeSet", (Class[]) null);
        } catch (Throwable th2) {
        }
    }

    public SwingComponentWrapper(Component component) {
        this.compType = -1;
        this.bl = null;
        this.prefCalled = false;
        this.c = component;
    }

    private int checkType(boolean z) {
        Component component = this.c;
        if (z) {
            if (component instanceof JScrollPane) {
                component = ((JScrollPane) component).getViewport().getView();
            } else if (component instanceof ScrollPane) {
                component = ((ScrollPane) component).getComponent(0);
            }
        }
        return ((component instanceof JTextField) || (component instanceof TextField)) ? 3 : ((component instanceof JLabel) || (component instanceof Label)) ? 2 : ((component instanceof JToggleButton) || (component instanceof Checkbox)) ? 16 : ((component instanceof AbstractButton) || (component instanceof Button)) ? 5 : ((component instanceof JComboBox) || (component instanceof Choice)) ? 2 : ((component instanceof JTextComponent) || (component instanceof TextComponent)) ? 4 : ((component instanceof JPanel) || (component instanceof Canvas)) ? 10 : ((component instanceof JList) || (component instanceof List)) ? 6 : component instanceof JTable ? 7 : component instanceof JSeparator ? 18 : component instanceof JSpinner ? 13 : component instanceof JProgressBar ? 14 : component instanceof JSlider ? 12 : component instanceof JScrollPane ? 8 : ((component instanceof JScrollBar) || (component instanceof Scrollbar)) ? 17 : component instanceof Container ? 1 : 0;
    }

    private boolean isMaxSet(Component component) {
        if (IMS_METHOD != null) {
            try {
                return ((Boolean) IMS_METHOD.invoke(component, (Object[]) null)).booleanValue();
            } catch (Exception e) {
                IMS_METHOD = null;
            }
        }
        return isMaxSizeSetOn1_4();
    }

    public static boolean isMaxSizeSetOn1_4() {
        return maxSet;
    }

    public static boolean isVisualPaddingEnabled() {
        return vp;
    }

    public static void setMaxSizeSetOn1_4(boolean z) {
        maxSet = z;
    }

    public static void setVisualPaddingEnabled(boolean z) {
        vp = z;
    }

    public final boolean equals(Object obj) {
        return !(obj instanceof ComponentWrapper) ? false : getComponent().equals(((ComponentWrapper) obj).getComponent());
    }

    public final int getBaseline(int i, int i2) {
        if (BL_METHOD == null) {
            return -1;
        }
        try {
            Object[] objArr = new Object[2];
            if (i < 0) {
                i = this.c.getWidth();
            }
            objArr[0] = Integer.valueOf(i);
            if (i2 < 0) {
                i2 = this.c.getHeight();
            }
            objArr[1] = Integer.valueOf(i2);
            return ((Integer) BL_METHOD.invoke(this.c, objArr)).intValue();
        } catch (Exception e) {
            return -1;
        }
    }

    public final Object getComponent() {
        return this.c;
    }

    public int getComponetType(boolean z) {
        if (this.compType == -1) {
            this.compType = checkType(z);
        }
        return this.compType;
    }

    public final int getHeight() {
        return this.c.getHeight();
    }

    public final int getHorizontalScreenDPI() {
        return PlatformDefaults.getDefaultDPI();
    }

    public int getLayoutHashCode() {
        Dimension maximumSize = this.c.getMaximumSize();
        int i = (maximumSize.height << 5) + maximumSize.width;
        Dimension preferredSize = this.c.getPreferredSize();
        i += (preferredSize.height << 15) + (preferredSize.width << 10);
        preferredSize = this.c.getMinimumSize();
        i += (preferredSize.height << 25) + (preferredSize.width << 20);
        if (this.c.isVisible()) {
            i += 1324511;
        }
        String linkId = getLinkId();
        return linkId != null ? i + linkId.hashCode() : i;
    }

    public final String getLinkId() {
        return this.c.getName();
    }

    public final int getMaximumHeight(int i) {
        return !isMaxSet(this.c) ? 32767 : this.c.getMaximumSize().height;
    }

    public final int getMaximumWidth(int i) {
        return !isMaxSet(this.c) ? 32767 : this.c.getMaximumSize().width;
    }

    public final int getMinimumHeight(int i) {
        if (!this.prefCalled) {
            this.c.getPreferredSize();
            this.prefCalled = true;
        }
        return this.c.getMinimumSize().height;
    }

    public final int getMinimumWidth(int i) {
        if (!this.prefCalled) {
            this.c.getPreferredSize();
            this.prefCalled = true;
        }
        return this.c.getMinimumSize().width;
    }

    public final ContainerWrapper getParent() {
        Container parent = this.c.getParent();
        return parent != null ? new SwingContainerWrapper(parent) : null;
    }

    public final float getPixelUnitFactor(boolean z) {
        switch (PlatformDefaults.getLogicalPixelBase()) {
            case UnitValue.STATIC /*100*/:
                Font font = this.c.getFont();
                Component component = this.c;
                if (font == null) {
                    font = SUBST_FONT;
                }
                FontMetrics fontMetrics = component.getFontMetrics(font);
                Float floatR = (Float) FM_MAP.get(fontMetrics);
                if (floatR == null) {
                    Rectangle2D stringBounds = fontMetrics.getStringBounds("X", this.c.getGraphics());
                    floatR = new Float(((float) stringBounds.getWidth()) / 6.0f, ((float) stringBounds.getHeight()) / 13.277344f);
                    FM_MAP.put(fontMetrics, floatR);
                }
                return z ? floatR.x : floatR.y;
            case UnitValue.ADD /*101*/:
                Float horizontalScaleFactor = z ? PlatformDefaults.getHorizontalScaleFactor() : PlatformDefaults.getVerticalScaleFactor();
                if (horizontalScaleFactor != null) {
                    return horizontalScaleFactor.floatValue();
                }
                return ((float) (z ? getHorizontalScreenDPI() : getVerticalScreenDPI())) / ((float) PlatformDefaults.getDefaultDPI());
            default:
                return Plot.DEFAULT_FOREGROUND_ALPHA;
        }
    }

    public final int getPreferredHeight(int i) {
        if (this.c.getWidth() == 0 && this.c.getHeight() == 0 && i != -1) {
            this.c.setBounds(this.c.getX(), this.c.getY(), i, 1);
        }
        return this.c.getPreferredSize().height;
    }

    public final int getPreferredWidth(int i) {
        if (this.c.getWidth() == 0 && this.c.getHeight() == 0 && i != -1) {
            this.c.setBounds(this.c.getX(), this.c.getY(), 1, i);
        }
        return this.c.getPreferredSize().width;
    }

    public final int getScreenHeight() {
        try {
            return this.c.getToolkit().getScreenSize().height;
        } catch (HeadlessException e) {
            return ChartPanel.DEFAULT_MAXIMUM_DRAW_HEIGHT;
        }
    }

    public final int getScreenLocationX() {
        Point point = new Point();
        SwingUtilities.convertPointToScreen(point, this.c);
        return point.x;
    }

    public final int getScreenLocationY() {
        Point point = new Point();
        SwingUtilities.convertPointToScreen(point, this.c);
        return point.y;
    }

    public final int getScreenWidth() {
        try {
            return this.c.getToolkit().getScreenSize().width;
        } catch (HeadlessException e) {
            return ChartPanel.DEFAULT_MAXIMUM_DRAW_WIDTH;
        }
    }

    public final int getVerticalScreenDPI() {
        return PlatformDefaults.getDefaultDPI();
    }

    public final int[] getVisualPadding() {
        return (vp && (this.c instanceof JTabbedPane) && UIManager.getLookAndFeel().getClass().getName().endsWith("WindowsLookAndFeel")) ? new int[]{-1, 0, 2, 2} : null;
    }

    public final int getWidth() {
        return this.c.getWidth();
    }

    public final int getX() {
        return this.c.getX();
    }

    public final int getY() {
        return this.c.getY();
    }

    public final boolean hasBaseline() {
        boolean z = false;
        if (this.bl == null) {
            try {
                if (BL_RES_METHOD == null || BL_RES_METHOD.invoke(this.c, new Object[0]).toString().equals("OTHER")) {
                    this.bl = Boolean.FALSE;
                } else {
                    Dimension minimumSize = this.c.getMinimumSize();
                    if (getBaseline(minimumSize.width, minimumSize.height) > -1) {
                        z = true;
                    }
                    this.bl = Boolean.valueOf(z);
                }
            } catch (Throwable th) {
                this.bl = Boolean.FALSE;
            }
        }
        return this.bl.booleanValue();
    }

    public final int hashCode() {
        return getComponent().hashCode();
    }

    public boolean isVisible() {
        return this.c.isVisible();
    }

    public final void paintDebugOutline() {
        if (this.c.isShowing()) {
            Graphics2D graphics2D = (Graphics2D) this.c.getGraphics();
            if (graphics2D != null) {
                graphics2D.setPaint(DB_COMP_OUTLINE);
                graphics2D.setStroke(new BasicStroke(Plot.DEFAULT_FOREGROUND_ALPHA, 2, 0, MeterPlot.DEFAULT_CIRCLE_SIZE, new float[]{Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH, 4.0f}, 0.0f));
                graphics2D.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
            }
        }
    }

    public final void setBounds(int i, int i2, int i3, int i4) {
        this.c.setBounds(i, i2, i3, i4);
    }
}
