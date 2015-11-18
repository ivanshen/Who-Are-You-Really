package org.jfree.chart;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Float;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.EventListenerList;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.editor.ChartEditor;
import org.jfree.chart.editor.ChartEditorManager;
import org.jfree.chart.encoders.ImageFormat;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.chart.event.ChartProgressListener;
import org.jfree.chart.event.OverlayChangeEvent;
import org.jfree.chart.event.OverlayChangeListener;
import org.jfree.chart.panel.Overlay;
import org.jfree.chart.plot.Pannable;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.chart.plot.Zoomable;
import org.jfree.chart.util.ParamChecks;
import org.jfree.chart.util.ResourceBundleWrapper;
import org.jfree.data.xy.NormalizedMatrixSeries;
import org.jfree.io.SerialUtilities;

public class ChartPanel extends JPanel implements ChartChangeListener, ChartProgressListener, ActionListener, MouseListener, MouseMotionListener, OverlayChangeListener, Printable, Serializable {
    public static final String COPY_COMMAND = "COPY";
    public static final boolean DEFAULT_BUFFER_USED = true;
    public static final int DEFAULT_HEIGHT = 420;
    public static final int DEFAULT_MAXIMUM_DRAW_HEIGHT = 768;
    public static final int DEFAULT_MAXIMUM_DRAW_WIDTH = 1024;
    public static final int DEFAULT_MINIMUM_DRAW_HEIGHT = 200;
    public static final int DEFAULT_MINIMUM_DRAW_WIDTH = 300;
    public static final int DEFAULT_WIDTH = 680;
    public static final int DEFAULT_ZOOM_TRIGGER_DISTANCE = 10;
    public static final String PRINT_COMMAND = "PRINT";
    public static final String PROPERTIES_COMMAND = "PROPERTIES";
    private static final String SAVE_AS_PDF_COMMAND = "SAVE_AS_PDF";
    private static final String SAVE_AS_PNG_COMMAND = "SAVE_AS_PNG";
    private static final String SAVE_AS_SVG_COMMAND = "SAVE_AS_SVG";
    public static final String SAVE_COMMAND = "SAVE";
    public static final String ZOOM_IN_BOTH_COMMAND = "ZOOM_IN_BOTH";
    public static final String ZOOM_IN_DOMAIN_COMMAND = "ZOOM_IN_DOMAIN";
    public static final String ZOOM_IN_RANGE_COMMAND = "ZOOM_IN_RANGE";
    public static final String ZOOM_OUT_BOTH_COMMAND = "ZOOM_OUT_BOTH";
    public static final String ZOOM_OUT_DOMAIN_COMMAND = "ZOOM_DOMAIN_BOTH";
    public static final String ZOOM_OUT_RANGE_COMMAND = "ZOOM_RANGE_BOTH";
    public static final String ZOOM_RESET_BOTH_COMMAND = "ZOOM_RESET_BOTH";
    public static final String ZOOM_RESET_DOMAIN_COMMAND = "ZOOM_RESET_DOMAIN";
    public static final String ZOOM_RESET_RANGE_COMMAND = "ZOOM_RESET_RANGE";
    protected static ResourceBundle localizationResources = null;
    private static final long serialVersionUID = 6046366297214274674L;
    private Point2D anchor;
    private JFreeChart chart;
    private transient Image chartBuffer;
    private int chartBufferHeight;
    private int chartBufferWidth;
    private transient EventListenerList chartMouseListeners;
    private File defaultDirectoryForSaveAs;
    private boolean domainZoomable;
    private boolean enforceFileExtensions;
    private boolean fillZoomRectangle;
    private boolean horizontalAxisTrace;
    private transient Line2D horizontalTraceLine;
    private ChartRenderingInfo info;
    private int maximumDrawHeight;
    private int maximumDrawWidth;
    private int minimumDrawHeight;
    private int minimumDrawWidth;
    private MouseWheelHandler mouseWheelHandler;
    private PlotOrientation orientation;
    private int originalToolTipDismissDelay;
    private int originalToolTipInitialDelay;
    private int originalToolTipReshowDelay;
    private List overlays;
    private boolean ownToolTipDelaysActive;
    private int ownToolTipDismissDelay;
    private int ownToolTipInitialDelay;
    private int ownToolTipReshowDelay;
    private double panH;
    private Point panLast;
    private int panMask;
    private double panW;
    private JPopupMenu popup;
    private boolean rangeZoomable;
    private boolean refreshBuffer;
    private double scaleX;
    private double scaleY;
    private boolean useBuffer;
    private boolean verticalAxisTrace;
    private transient Line2D verticalTraceLine;
    private boolean zoomAroundAnchor;
    private transient Paint zoomFillPaint;
    private JMenuItem zoomInBothMenuItem;
    private JMenuItem zoomInDomainMenuItem;
    private double zoomInFactor;
    private JMenuItem zoomInRangeMenuItem;
    private JMenuItem zoomOutBothMenuItem;
    private JMenuItem zoomOutDomainMenuItem;
    private double zoomOutFactor;
    private JMenuItem zoomOutRangeMenuItem;
    private transient Paint zoomOutlinePaint;
    private Point2D zoomPoint;
    private transient Rectangle2D zoomRectangle;
    private JMenuItem zoomResetBothMenuItem;
    private JMenuItem zoomResetDomainMenuItem;
    private JMenuItem zoomResetRangeMenuItem;
    private int zoomTriggerDistance;

    static {
        localizationResources = ResourceBundleWrapper.getBundle("org.jfree.chart.LocalizationBundle");
    }

    public ChartPanel(JFreeChart chart) {
        this(chart, DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_MINIMUM_DRAW_WIDTH, DEFAULT_MINIMUM_DRAW_HEIGHT, DEFAULT_MAXIMUM_DRAW_WIDTH, DEFAULT_MAXIMUM_DRAW_HEIGHT, DEFAULT_BUFFER_USED, DEFAULT_BUFFER_USED, DEFAULT_BUFFER_USED, DEFAULT_BUFFER_USED, DEFAULT_BUFFER_USED, DEFAULT_BUFFER_USED);
    }

    public ChartPanel(JFreeChart chart, boolean useBuffer) {
        this(chart, DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_MINIMUM_DRAW_WIDTH, DEFAULT_MINIMUM_DRAW_HEIGHT, DEFAULT_MAXIMUM_DRAW_WIDTH, DEFAULT_MAXIMUM_DRAW_HEIGHT, useBuffer, DEFAULT_BUFFER_USED, DEFAULT_BUFFER_USED, DEFAULT_BUFFER_USED, DEFAULT_BUFFER_USED, DEFAULT_BUFFER_USED);
    }

    public ChartPanel(JFreeChart chart, boolean properties, boolean save, boolean print, boolean zoom, boolean tooltips) {
        this(chart, DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_MINIMUM_DRAW_WIDTH, DEFAULT_MINIMUM_DRAW_HEIGHT, DEFAULT_MAXIMUM_DRAW_WIDTH, DEFAULT_MAXIMUM_DRAW_HEIGHT, DEFAULT_BUFFER_USED, properties, save, print, zoom, tooltips);
    }

    public ChartPanel(JFreeChart chart, int width, int height, int minimumDrawWidth, int minimumDrawHeight, int maximumDrawWidth, int maximumDrawHeight, boolean useBuffer, boolean properties, boolean save, boolean print, boolean zoom, boolean tooltips) {
        this(chart, width, height, minimumDrawWidth, minimumDrawHeight, maximumDrawWidth, maximumDrawHeight, useBuffer, properties, DEFAULT_BUFFER_USED, save, print, zoom, tooltips);
    }

    public ChartPanel(JFreeChart chart, int width, int height, int minimumDrawWidth, int minimumDrawHeight, int maximumDrawWidth, int maximumDrawHeight, boolean useBuffer, boolean properties, boolean copy, boolean save, boolean print, boolean zoom, boolean tooltips) {
        this.orientation = PlotOrientation.VERTICAL;
        this.domainZoomable = false;
        this.rangeZoomable = false;
        this.zoomPoint = null;
        this.zoomRectangle = null;
        this.fillZoomRectangle = DEFAULT_BUFFER_USED;
        this.horizontalAxisTrace = false;
        this.verticalAxisTrace = false;
        this.zoomInFactor = 0.5d;
        this.zoomOutFactor = DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
        this.panMask = 2;
        setChart(chart);
        this.chartMouseListeners = new EventListenerList();
        this.info = new ChartRenderingInfo();
        setPreferredSize(new Dimension(width, height));
        this.useBuffer = useBuffer;
        this.refreshBuffer = false;
        this.minimumDrawWidth = minimumDrawWidth;
        this.minimumDrawHeight = minimumDrawHeight;
        this.maximumDrawWidth = maximumDrawWidth;
        this.maximumDrawHeight = maximumDrawHeight;
        this.zoomTriggerDistance = DEFAULT_ZOOM_TRIGGER_DISTANCE;
        this.popup = null;
        if (properties || copy || save || print || zoom) {
            this.popup = createPopupMenu(properties, copy, save, print, zoom);
        }
        enableEvents(16);
        enableEvents(32);
        setDisplayToolTips(tooltips);
        addMouseListener(this);
        addMouseMotionListener(this);
        this.defaultDirectoryForSaveAs = null;
        this.enforceFileExtensions = DEFAULT_BUFFER_USED;
        ToolTipManager ttm = ToolTipManager.sharedInstance();
        this.ownToolTipInitialDelay = ttm.getInitialDelay();
        this.ownToolTipDismissDelay = ttm.getDismissDelay();
        this.ownToolTipReshowDelay = ttm.getReshowDelay();
        this.zoomAroundAnchor = false;
        this.zoomOutlinePaint = Color.blue;
        this.zoomFillPaint = new Color(0, 0, 255, 63);
        this.panMask = 2;
        if (System.getProperty("os.name").toLowerCase().startsWith("mac os x")) {
            this.panMask = 8;
        }
        this.overlays = new ArrayList();
    }

    public JFreeChart getChart() {
        return this.chart;
    }

    public void setChart(JFreeChart chart) {
        if (this.chart != null) {
            this.chart.removeChangeListener(this);
            this.chart.removeProgressListener(this);
        }
        this.chart = chart;
        if (chart != null) {
            this.chart.addChangeListener(this);
            this.chart.addProgressListener(this);
            Plot plot = chart.getPlot();
            this.domainZoomable = false;
            this.rangeZoomable = false;
            if (plot instanceof Zoomable) {
                Zoomable z = (Zoomable) plot;
                this.domainZoomable = z.isDomainZoomable();
                this.rangeZoomable = z.isRangeZoomable();
                this.orientation = z.getOrientation();
            }
        } else {
            this.domainZoomable = false;
            this.rangeZoomable = false;
        }
        if (this.useBuffer) {
            this.refreshBuffer = DEFAULT_BUFFER_USED;
        }
        repaint();
    }

    public int getMinimumDrawWidth() {
        return this.minimumDrawWidth;
    }

    public void setMinimumDrawWidth(int width) {
        this.minimumDrawWidth = width;
    }

    public int getMaximumDrawWidth() {
        return this.maximumDrawWidth;
    }

    public void setMaximumDrawWidth(int width) {
        this.maximumDrawWidth = width;
    }

    public int getMinimumDrawHeight() {
        return this.minimumDrawHeight;
    }

    public void setMinimumDrawHeight(int height) {
        this.minimumDrawHeight = height;
    }

    public int getMaximumDrawHeight() {
        return this.maximumDrawHeight;
    }

    public void setMaximumDrawHeight(int height) {
        this.maximumDrawHeight = height;
    }

    public double getScaleX() {
        return this.scaleX;
    }

    public double getScaleY() {
        return this.scaleY;
    }

    public Point2D getAnchor() {
        return this.anchor;
    }

    protected void setAnchor(Point2D anchor) {
        this.anchor = anchor;
    }

    public JPopupMenu getPopupMenu() {
        return this.popup;
    }

    public void setPopupMenu(JPopupMenu popup) {
        this.popup = popup;
    }

    public ChartRenderingInfo getChartRenderingInfo() {
        return this.info;
    }

    public void setMouseZoomable(boolean flag) {
        setMouseZoomable(flag, DEFAULT_BUFFER_USED);
    }

    public void setMouseZoomable(boolean flag, boolean fillRectangle) {
        setDomainZoomable(flag);
        setRangeZoomable(flag);
        setFillZoomRectangle(fillRectangle);
    }

    public boolean isDomainZoomable() {
        return this.domainZoomable;
    }

    public void setDomainZoomable(boolean flag) {
        boolean z = false;
        if (flag) {
            Plot plot = this.chart.getPlot();
            if (plot instanceof Zoomable) {
                Zoomable z2 = (Zoomable) plot;
                if (flag && z2.isDomainZoomable()) {
                    z = DEFAULT_BUFFER_USED;
                }
                this.domainZoomable = z;
                return;
            }
            return;
        }
        this.domainZoomable = false;
    }

    public boolean isRangeZoomable() {
        return this.rangeZoomable;
    }

    public void setRangeZoomable(boolean flag) {
        boolean z = false;
        if (flag) {
            Plot plot = this.chart.getPlot();
            if (plot instanceof Zoomable) {
                Zoomable z2 = (Zoomable) plot;
                if (flag && z2.isRangeZoomable()) {
                    z = DEFAULT_BUFFER_USED;
                }
                this.rangeZoomable = z;
                return;
            }
            return;
        }
        this.rangeZoomable = false;
    }

    public boolean getFillZoomRectangle() {
        return this.fillZoomRectangle;
    }

    public void setFillZoomRectangle(boolean flag) {
        this.fillZoomRectangle = flag;
    }

    public int getZoomTriggerDistance() {
        return this.zoomTriggerDistance;
    }

    public void setZoomTriggerDistance(int distance) {
        this.zoomTriggerDistance = distance;
    }

    public boolean getHorizontalAxisTrace() {
        return this.horizontalAxisTrace;
    }

    public void setHorizontalAxisTrace(boolean flag) {
        this.horizontalAxisTrace = flag;
    }

    protected Line2D getHorizontalTraceLine() {
        return this.horizontalTraceLine;
    }

    protected void setHorizontalTraceLine(Line2D line) {
        this.horizontalTraceLine = line;
    }

    public boolean getVerticalAxisTrace() {
        return this.verticalAxisTrace;
    }

    public void setVerticalAxisTrace(boolean flag) {
        this.verticalAxisTrace = flag;
    }

    protected Line2D getVerticalTraceLine() {
        return this.verticalTraceLine;
    }

    protected void setVerticalTraceLine(Line2D line) {
        this.verticalTraceLine = line;
    }

    public File getDefaultDirectoryForSaveAs() {
        return this.defaultDirectoryForSaveAs;
    }

    public void setDefaultDirectoryForSaveAs(File directory) {
        if (directory == null || directory.isDirectory()) {
            this.defaultDirectoryForSaveAs = directory;
            return;
        }
        throw new IllegalArgumentException("The 'directory' argument is not a directory.");
    }

    public boolean isEnforceFileExtensions() {
        return this.enforceFileExtensions;
    }

    public void setEnforceFileExtensions(boolean enforce) {
        this.enforceFileExtensions = enforce;
    }

    public boolean getZoomAroundAnchor() {
        return this.zoomAroundAnchor;
    }

    public void setZoomAroundAnchor(boolean zoomAroundAnchor) {
        this.zoomAroundAnchor = zoomAroundAnchor;
    }

    public Paint getZoomFillPaint() {
        return this.zoomFillPaint;
    }

    public void setZoomFillPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.zoomFillPaint = paint;
    }

    public Paint getZoomOutlinePaint() {
        return this.zoomOutlinePaint;
    }

    public void setZoomOutlinePaint(Paint paint) {
        this.zoomOutlinePaint = paint;
    }

    public boolean isMouseWheelEnabled() {
        return this.mouseWheelHandler != null ? DEFAULT_BUFFER_USED : false;
    }

    public void setMouseWheelEnabled(boolean flag) {
        if (flag && this.mouseWheelHandler == null) {
            this.mouseWheelHandler = new MouseWheelHandler(this);
        } else if (!flag && this.mouseWheelHandler != null) {
            removeMouseWheelListener(this.mouseWheelHandler);
            this.mouseWheelHandler = null;
        }
    }

    public void addOverlay(Overlay overlay) {
        ParamChecks.nullNotPermitted(overlay, "overlay");
        this.overlays.add(overlay);
        overlay.addChangeListener(this);
        repaint();
    }

    public void removeOverlay(Overlay overlay) {
        ParamChecks.nullNotPermitted(overlay, "overlay");
        if (this.overlays.remove(overlay)) {
            overlay.removeChangeListener(this);
            repaint();
        }
    }

    public void overlayChanged(OverlayChangeEvent event) {
        repaint();
    }

    public void setDisplayToolTips(boolean flag) {
        if (flag) {
            ToolTipManager.sharedInstance().registerComponent(this);
        } else {
            ToolTipManager.sharedInstance().unregisterComponent(this);
        }
    }

    public String getToolTipText(MouseEvent e) {
        if (this.info == null) {
            return null;
        }
        EntityCollection entities = this.info.getEntityCollection();
        if (entities == null) {
            return null;
        }
        Insets insets = getInsets();
        ChartEntity entity = entities.getEntity((double) ((int) (((double) (e.getX() - insets.left)) / this.scaleX)), (double) ((int) (((double) (e.getY() - insets.top)) / this.scaleY)));
        if (entity != null) {
            return entity.getToolTipText();
        }
        return null;
    }

    public Point translateJava2DToScreen(Point2D java2DPoint) {
        Insets insets = getInsets();
        return new Point((int) ((java2DPoint.getX() * this.scaleX) + ((double) insets.left)), (int) ((java2DPoint.getY() * this.scaleY) + ((double) insets.top)));
    }

    public Point2D translateScreenToJava2D(Point screenPoint) {
        Insets insets = getInsets();
        return new Double((screenPoint.getX() - ((double) insets.left)) / this.scaleX, (screenPoint.getY() - ((double) insets.top)) / this.scaleY);
    }

    public Rectangle2D scale(Rectangle2D rect) {
        Insets insets = getInsets();
        return new Rectangle2D.Double((rect.getX() * getScaleX()) + ((double) insets.left), (rect.getY() * getScaleY()) + ((double) insets.top), rect.getWidth() * getScaleX(), rect.getHeight() * getScaleY());
    }

    public ChartEntity getEntityForPoint(int viewX, int viewY) {
        if (this.info == null) {
            return null;
        }
        Insets insets = getInsets();
        double x = ((double) (viewX - insets.left)) / this.scaleX;
        double y = ((double) (viewY - insets.top)) / this.scaleY;
        EntityCollection entities = this.info.getEntityCollection();
        return entities != null ? entities.getEntity(x, y) : null;
    }

    public boolean getRefreshBuffer() {
        return this.refreshBuffer;
    }

    public void setRefreshBuffer(boolean flag) {
        this.refreshBuffer = flag;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.chart != null) {
            Graphics2D g2 = (Graphics2D) g.create();
            Dimension size = getSize();
            Insets insets = getInsets();
            double d = (double) insets.right;
            Rectangle2D available = new Rectangle2D.Double((double) insets.left, (double) insets.top, (size.getWidth() - ((double) insets.left)) - r0, (size.getHeight() - ((double) insets.top)) - ((double) insets.bottom));
            boolean scale = false;
            double drawWidth = available.getWidth();
            double drawHeight = available.getHeight();
            this.scaleX = NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR;
            this.scaleY = NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR;
            if (drawWidth < ((double) this.minimumDrawWidth)) {
                this.scaleX = drawWidth / ((double) this.minimumDrawWidth);
                drawWidth = (double) this.minimumDrawWidth;
                scale = DEFAULT_BUFFER_USED;
            } else if (drawWidth > ((double) this.maximumDrawWidth)) {
                this.scaleX = drawWidth / ((double) this.maximumDrawWidth);
                drawWidth = (double) this.maximumDrawWidth;
                scale = DEFAULT_BUFFER_USED;
            }
            if (drawHeight < ((double) this.minimumDrawHeight)) {
                this.scaleY = drawHeight / ((double) this.minimumDrawHeight);
                drawHeight = (double) this.minimumDrawHeight;
                scale = DEFAULT_BUFFER_USED;
            } else if (drawHeight > ((double) this.maximumDrawHeight)) {
                this.scaleY = drawHeight / ((double) this.maximumDrawHeight);
                drawHeight = (double) this.maximumDrawHeight;
                scale = DEFAULT_BUFFER_USED;
            }
            Rectangle2D chartArea = new Rectangle2D.Double(0.0d, 0.0d, drawWidth, drawHeight);
            AffineTransform saved;
            if (this.useBuffer) {
                if (!(this.chartBuffer != null && ((double) this.chartBufferWidth) == available.getWidth() && ((double) this.chartBufferHeight) == available.getHeight())) {
                    this.chartBufferWidth = (int) available.getWidth();
                    this.chartBufferHeight = (int) available.getHeight();
                    this.chartBuffer = g2.getDeviceConfiguration().createCompatibleImage(this.chartBufferWidth, this.chartBufferHeight, 3);
                    this.refreshBuffer = DEFAULT_BUFFER_USED;
                }
                if (this.refreshBuffer) {
                    this.refreshBuffer = false;
                    Rectangle2D bufferArea = new Rectangle2D.Double(0.0d, 0.0d, (double) this.chartBufferWidth, (double) this.chartBufferHeight);
                    Graphics2D bufferG2 = (Graphics2D) this.chartBuffer.getGraphics();
                    Composite savedComposite = bufferG2.getComposite();
                    bufferG2.setComposite(AlphaComposite.getInstance(1, 0.0f));
                    bufferG2.fill(new Rectangle(0, 0, this.chartBufferWidth, this.chartBufferHeight));
                    bufferG2.setComposite(savedComposite);
                    if (scale) {
                        saved = bufferG2.getTransform();
                        bufferG2.transform(AffineTransform.getScaleInstance(this.scaleX, this.scaleY));
                        this.chart.draw(bufferG2, chartArea, this.anchor, this.info);
                        bufferG2.setTransform(saved);
                    } else {
                        this.chart.draw(bufferG2, bufferArea, this.anchor, this.info);
                    }
                }
                g2.drawImage(this.chartBuffer, insets.left, insets.top, this);
            } else {
                saved = g2.getTransform();
                g2.translate(insets.left, insets.top);
                if (scale) {
                    g2.transform(AffineTransform.getScaleInstance(this.scaleX, this.scaleY));
                }
                this.chart.draw(g2, chartArea, this.anchor, this.info);
                g2.setTransform(saved);
            }
            for (Overlay paintOverlay : this.overlays) {
                paintOverlay.paintOverlay(g2, this);
            }
            drawZoomRectangle(g2, !this.useBuffer ? DEFAULT_BUFFER_USED : false);
            g2.dispose();
            this.anchor = null;
            this.verticalTraceLine = null;
            this.horizontalTraceLine = null;
        }
    }

    public void chartChanged(ChartChangeEvent event) {
        this.refreshBuffer = DEFAULT_BUFFER_USED;
        Plot plot = this.chart.getPlot();
        if (plot instanceof Zoomable) {
            this.orientation = ((Zoomable) plot).getOrientation();
        }
        repaint();
    }

    public void chartProgress(ChartProgressEvent event) {
    }

    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        double screenX = SpiderWebPlot.DEFAULT_MAX_VALUE;
        double screenY = SpiderWebPlot.DEFAULT_MAX_VALUE;
        if (this.zoomPoint != null) {
            screenX = this.zoomPoint.getX();
            screenY = this.zoomPoint.getY();
        }
        if (command.equals(PROPERTIES_COMMAND)) {
            doEditChartProperties();
        } else if (command.equals(COPY_COMMAND)) {
            doCopy();
        } else if (command.equals(SAVE_AS_PNG_COMMAND)) {
            try {
                doSaveAs();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "I/O error occurred.", "Save As PNG", 2);
            }
        } else if (command.equals(SAVE_AS_SVG_COMMAND)) {
            try {
                saveAsSVG(null);
            } catch (IOException e2) {
                JOptionPane.showMessageDialog(this, "I/O error occurred.", "Save As SVG", 2);
            }
        } else if (command.equals(SAVE_AS_PDF_COMMAND)) {
            saveAsPDF(null);
        } else if (command.equals(PRINT_COMMAND)) {
            createChartPrintJob();
        } else if (command.equals(ZOOM_IN_BOTH_COMMAND)) {
            zoomInBoth(screenX, screenY);
        } else if (command.equals(ZOOM_IN_DOMAIN_COMMAND)) {
            zoomInDomain(screenX, screenY);
        } else if (command.equals(ZOOM_IN_RANGE_COMMAND)) {
            zoomInRange(screenX, screenY);
        } else if (command.equals(ZOOM_OUT_BOTH_COMMAND)) {
            zoomOutBoth(screenX, screenY);
        } else if (command.equals(ZOOM_OUT_DOMAIN_COMMAND)) {
            zoomOutDomain(screenX, screenY);
        } else if (command.equals(ZOOM_OUT_RANGE_COMMAND)) {
            zoomOutRange(screenX, screenY);
        } else if (command.equals(ZOOM_RESET_BOTH_COMMAND)) {
            restoreAutoBounds();
        } else if (command.equals(ZOOM_RESET_DOMAIN_COMMAND)) {
            restoreAutoDomainBounds();
        } else if (command.equals(ZOOM_RESET_RANGE_COMMAND)) {
            restoreAutoRangeBounds();
        }
    }

    public void mouseEntered(MouseEvent e) {
        if (!this.ownToolTipDelaysActive) {
            ToolTipManager ttm = ToolTipManager.sharedInstance();
            this.originalToolTipInitialDelay = ttm.getInitialDelay();
            ttm.setInitialDelay(this.ownToolTipInitialDelay);
            this.originalToolTipReshowDelay = ttm.getReshowDelay();
            ttm.setReshowDelay(this.ownToolTipReshowDelay);
            this.originalToolTipDismissDelay = ttm.getDismissDelay();
            ttm.setDismissDelay(this.ownToolTipDismissDelay);
            this.ownToolTipDelaysActive = DEFAULT_BUFFER_USED;
        }
    }

    public void mouseExited(MouseEvent e) {
        if (this.ownToolTipDelaysActive) {
            ToolTipManager ttm = ToolTipManager.sharedInstance();
            ttm.setInitialDelay(this.originalToolTipInitialDelay);
            ttm.setReshowDelay(this.originalToolTipReshowDelay);
            ttm.setDismissDelay(this.originalToolTipDismissDelay);
            this.ownToolTipDelaysActive = false;
        }
    }

    public void mousePressed(MouseEvent e) {
        if (this.chart != null) {
            Plot plot = this.chart.getPlot();
            Rectangle2D screenDataArea;
            if ((this.panMask & e.getModifiers()) == this.panMask) {
                if (plot instanceof Pannable) {
                    Pannable pannable = (Pannable) plot;
                    if (pannable.isDomainPannable() || pannable.isRangePannable()) {
                        screenDataArea = getScreenDataArea(e.getX(), e.getY());
                        if (screenDataArea != null && screenDataArea.contains(e.getPoint())) {
                            this.panW = screenDataArea.getWidth();
                            this.panH = screenDataArea.getHeight();
                            this.panLast = e.getPoint();
                            setCursor(Cursor.getPredefinedCursor(13));
                        }
                    }
                }
            } else if (this.zoomRectangle == null) {
                screenDataArea = getScreenDataArea(e.getX(), e.getY());
                if (screenDataArea != null) {
                    this.zoomPoint = getPointInRectangle(e.getX(), e.getY(), screenDataArea);
                } else {
                    this.zoomPoint = null;
                }
                if (e.isPopupTrigger() && this.popup != null) {
                    displayPopupMenu(e.getX(), e.getY());
                }
            }
        }
    }

    private Point2D getPointInRectangle(int x, int y, Rectangle2D area) {
        return new Double(Math.max(area.getMinX(), Math.min((double) x, area.getMaxX())), Math.max(area.getMinY(), Math.min((double) y, area.getMaxY())));
    }

    public void mouseDragged(MouseEvent e) {
        if (this.popup != null && this.popup.isShowing()) {
            return;
        }
        if (this.panLast != null) {
            double dx = ((double) e.getX()) - this.panLast.getX();
            double dy = ((double) e.getY()) - this.panLast.getY();
            if (dx != 0.0d || dy != 0.0d) {
                double wPercent = (-dx) / this.panW;
                double hPercent = dy / this.panH;
                boolean old = this.chart.getPlot().isNotify();
                this.chart.getPlot().setNotify(false);
                Pannable p = (Pannable) this.chart.getPlot();
                if (p.getOrientation() == PlotOrientation.VERTICAL) {
                    p.panDomainAxes(wPercent, this.info.getPlotInfo(), this.panLast);
                    p.panRangeAxes(hPercent, this.info.getPlotInfo(), this.panLast);
                } else {
                    p.panDomainAxes(hPercent, this.info.getPlotInfo(), this.panLast);
                    p.panRangeAxes(wPercent, this.info.getPlotInfo(), this.panLast);
                }
                this.panLast = e.getPoint();
                this.chart.getPlot().setNotify(old);
            }
        } else if (this.zoomPoint != null) {
            boolean hZoom;
            boolean vZoom;
            Graphics2D g2 = (Graphics2D) getGraphics();
            if (!this.useBuffer) {
                drawZoomRectangle(g2, DEFAULT_BUFFER_USED);
            }
            if (this.orientation == PlotOrientation.HORIZONTAL) {
                hZoom = this.rangeZoomable;
                vZoom = this.domainZoomable;
            } else {
                hZoom = this.domainZoomable;
                vZoom = this.rangeZoomable;
            }
            Rectangle2D scaledDataArea = getScreenDataArea((int) this.zoomPoint.getX(), (int) this.zoomPoint.getY());
            if (hZoom && vZoom) {
                this.zoomRectangle = new Rectangle2D.Double(this.zoomPoint.getX(), this.zoomPoint.getY(), Math.min((double) e.getX(), scaledDataArea.getMaxX()) - this.zoomPoint.getX(), Math.min((double) e.getY(), scaledDataArea.getMaxY()) - this.zoomPoint.getY());
            } else if (hZoom) {
                this.zoomRectangle = new Rectangle2D.Double(this.zoomPoint.getX(), scaledDataArea.getMinY(), Math.min((double) e.getX(), scaledDataArea.getMaxX()) - this.zoomPoint.getX(), scaledDataArea.getHeight());
            } else if (vZoom) {
                this.zoomRectangle = new Rectangle2D.Double(scaledDataArea.getMinX(), this.zoomPoint.getY(), scaledDataArea.getWidth(), Math.min((double) e.getY(), scaledDataArea.getMaxY()) - this.zoomPoint.getY());
            }
            if (this.useBuffer) {
                repaint();
            } else {
                drawZoomRectangle(g2, DEFAULT_BUFFER_USED);
            }
            g2.dispose();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void mouseReleased(java.awt.event.MouseEvent r29) {
        /*
        r28 = this;
        r0 = r28;
        r0 = r0.panLast;
        r23 = r0;
        if (r23 == 0) goto L_0x001c;
    L_0x0008:
        r23 = 0;
        r0 = r23;
        r1 = r28;
        r1.panLast = r0;
        r23 = java.awt.Cursor.getDefaultCursor();
        r0 = r28;
        r1 = r23;
        r0.setCursor(r1);
    L_0x001b:
        return;
    L_0x001c:
        r0 = r28;
        r0 = r0.zoomRectangle;
        r23 = r0;
        if (r23 == 0) goto L_0x0205;
    L_0x0024:
        r0 = r28;
        r0 = r0.orientation;
        r23 = r0;
        r24 = org.jfree.chart.plot.PlotOrientation.HORIZONTAL;
        r0 = r23;
        r1 = r24;
        if (r0 != r1) goto L_0x00df;
    L_0x0032:
        r0 = r28;
        r14 = r0.rangeZoomable;
        r0 = r28;
        r0 = r0.domainZoomable;
        r20 = r0;
    L_0x003c:
        if (r14 == 0) goto L_0x00eb;
    L_0x003e:
        r23 = r29.getX();
        r0 = r23;
        r0 = (double) r0;
        r24 = r0;
        r0 = r28;
        r0 = r0.zoomPoint;
        r23 = r0;
        r26 = r23.getX();
        r24 = r24 - r26;
        r24 = java.lang.Math.abs(r24);
        r0 = r28;
        r0 = r0.zoomTriggerDistance;
        r23 = r0;
        r0 = r23;
        r0 = (double) r0;
        r26 = r0;
        r23 = (r24 > r26 ? 1 : (r24 == r26 ? 0 : -1));
        if (r23 < 0) goto L_0x00eb;
    L_0x0066:
        r21 = 1;
    L_0x0068:
        if (r20 == 0) goto L_0x00ef;
    L_0x006a:
        r23 = r29.getY();
        r0 = r23;
        r0 = (double) r0;
        r24 = r0;
        r0 = r28;
        r0 = r0.zoomPoint;
        r23 = r0;
        r26 = r23.getY();
        r24 = r24 - r26;
        r24 = java.lang.Math.abs(r24);
        r0 = r28;
        r0 = r0.zoomTriggerDistance;
        r23 = r0;
        r0 = r23;
        r0 = (double) r0;
        r26 = r0;
        r23 = (r24 > r26 ? 1 : (r24 == r26 ? 0 : -1));
        if (r23 < 0) goto L_0x00ef;
    L_0x0092:
        r22 = 1;
    L_0x0094:
        if (r21 != 0) goto L_0x0098;
    L_0x0096:
        if (r22 == 0) goto L_0x01d5;
    L_0x0098:
        if (r14 == 0) goto L_0x00b1;
    L_0x009a:
        r23 = r29.getX();
        r0 = r23;
        r0 = (double) r0;
        r24 = r0;
        r0 = r28;
        r0 = r0.zoomPoint;
        r23 = r0;
        r26 = r23.getX();
        r23 = (r24 > r26 ? 1 : (r24 == r26 ? 0 : -1));
        if (r23 < 0) goto L_0x00ca;
    L_0x00b1:
        if (r20 == 0) goto L_0x00f2;
    L_0x00b3:
        r23 = r29.getY();
        r0 = r23;
        r0 = (double) r0;
        r24 = r0;
        r0 = r28;
        r0 = r0.zoomPoint;
        r23 = r0;
        r26 = r23.getY();
        r23 = (r24 > r26 ? 1 : (r24 == r26 ? 0 : -1));
        if (r23 >= 0) goto L_0x00f2;
    L_0x00ca:
        r28.restoreAutoBounds();
    L_0x00cd:
        r23 = 0;
        r0 = r23;
        r1 = r28;
        r1.zoomPoint = r0;
        r23 = 0;
        r0 = r23;
        r1 = r28;
        r1.zoomRectangle = r0;
        goto L_0x001b;
    L_0x00df:
        r0 = r28;
        r14 = r0.domainZoomable;
        r0 = r28;
        r0 = r0.rangeZoomable;
        r20 = r0;
        goto L_0x003c;
    L_0x00eb:
        r21 = 0;
        goto L_0x0068;
    L_0x00ef:
        r22 = 0;
        goto L_0x0094;
    L_0x00f2:
        r0 = r28;
        r0 = r0.zoomPoint;
        r23 = r0;
        r24 = r23.getX();
        r0 = r24;
        r0 = (int) r0;
        r23 = r0;
        r0 = r28;
        r0 = r0.zoomPoint;
        r24 = r0;
        r24 = r24.getY();
        r0 = r24;
        r0 = (int) r0;
        r24 = r0;
        r0 = r28;
        r1 = r23;
        r2 = r24;
        r15 = r0.getScreenDataArea(r1, r2);
        r16 = r15.getMaxX();
        r18 = r15.getMaxY();
        if (r20 != 0) goto L_0x015c;
    L_0x0124:
        r0 = r28;
        r0 = r0.zoomPoint;
        r23 = r0;
        r6 = r23.getX();
        r8 = r15.getMinY();
        r0 = r28;
        r0 = r0.zoomRectangle;
        r23 = r0;
        r24 = r23.getWidth();
        r0 = r28;
        r0 = r0.zoomPoint;
        r23 = r0;
        r26 = r23.getX();
        r26 = r16 - r26;
        r10 = java.lang.Math.min(r24, r26);
        r12 = r15.getHeight();
    L_0x0150:
        r5 = new java.awt.geom.Rectangle2D$Double;
        r5.<init>(r6, r8, r10, r12);
        r0 = r28;
        r0.zoom(r5);
        goto L_0x00cd;
    L_0x015c:
        if (r14 != 0) goto L_0x018b;
    L_0x015e:
        r6 = r15.getMinX();
        r0 = r28;
        r0 = r0.zoomPoint;
        r23 = r0;
        r8 = r23.getY();
        r10 = r15.getWidth();
        r0 = r28;
        r0 = r0.zoomRectangle;
        r23 = r0;
        r24 = r23.getHeight();
        r0 = r28;
        r0 = r0.zoomPoint;
        r23 = r0;
        r26 = r23.getY();
        r26 = r18 - r26;
        r12 = java.lang.Math.min(r24, r26);
        goto L_0x0150;
    L_0x018b:
        r0 = r28;
        r0 = r0.zoomPoint;
        r23 = r0;
        r6 = r23.getX();
        r0 = r28;
        r0 = r0.zoomPoint;
        r23 = r0;
        r8 = r23.getY();
        r0 = r28;
        r0 = r0.zoomRectangle;
        r23 = r0;
        r24 = r23.getWidth();
        r0 = r28;
        r0 = r0.zoomPoint;
        r23 = r0;
        r26 = r23.getX();
        r26 = r16 - r26;
        r10 = java.lang.Math.min(r24, r26);
        r0 = r28;
        r0 = r0.zoomRectangle;
        r23 = r0;
        r24 = r23.getHeight();
        r0 = r28;
        r0 = r0.zoomPoint;
        r23 = r0;
        r26 = r23.getY();
        r26 = r18 - r26;
        r12 = java.lang.Math.min(r24, r26);
        goto L_0x0150;
    L_0x01d5:
        r4 = r28.getGraphics();
        r4 = (java.awt.Graphics2D) r4;
        r0 = r28;
        r0 = r0.useBuffer;
        r23 = r0;
        if (r23 == 0) goto L_0x01fb;
    L_0x01e3:
        r28.repaint();
    L_0x01e6:
        r4.dispose();
        r23 = 0;
        r0 = r23;
        r1 = r28;
        r1.zoomPoint = r0;
        r23 = 0;
        r0 = r23;
        r1 = r28;
        r1.zoomRectangle = r0;
        goto L_0x001b;
    L_0x01fb:
        r23 = 1;
        r0 = r28;
        r1 = r23;
        r0.drawZoomRectangle(r4, r1);
        goto L_0x01e6;
    L_0x0205:
        r23 = r29.isPopupTrigger();
        if (r23 == 0) goto L_0x001b;
    L_0x020b:
        r0 = r28;
        r0 = r0.popup;
        r23 = r0;
        if (r23 == 0) goto L_0x001b;
    L_0x0213:
        r23 = r29.getX();
        r24 = r29.getY();
        r0 = r28;
        r1 = r23;
        r2 = r24;
        r0.displayPopupMenu(r1, r2);
        goto L_0x001b;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jfree.chart.ChartPanel.mouseReleased(java.awt.event.MouseEvent):void");
    }

    public void mouseClicked(MouseEvent event) {
        Insets insets = getInsets();
        int x = (int) (((double) (event.getX() - insets.left)) / this.scaleX);
        int y = (int) (((double) (event.getY() - insets.top)) / this.scaleY);
        this.anchor = new Double((double) x, (double) y);
        if (this.chart != null) {
            this.chart.setNotify(DEFAULT_BUFFER_USED);
            Object[] listeners = this.chartMouseListeners.getListeners(ChartMouseListener.class);
            if (listeners.length != 0) {
                ChartEntity entity = null;
                if (this.info != null) {
                    EntityCollection entities = this.info.getEntityCollection();
                    if (entities != null) {
                        entity = entities.getEntity((double) x, (double) y);
                    }
                }
                ChartMouseEvent chartEvent = new ChartMouseEvent(getChart(), event, entity);
                for (int i = listeners.length - 1; i >= 0; i--) {
                    ((ChartMouseListener) listeners[i]).chartMouseClicked(chartEvent);
                }
            }
        }
    }

    public void mouseMoved(MouseEvent e) {
        Graphics2D g2 = (Graphics2D) getGraphics();
        if (this.horizontalAxisTrace) {
            drawHorizontalAxisTrace(g2, e.getX());
        }
        if (this.verticalAxisTrace) {
            drawVerticalAxisTrace(g2, e.getY());
        }
        g2.dispose();
        Object[] listeners = this.chartMouseListeners.getListeners(ChartMouseListener.class);
        if (listeners.length != 0) {
            Insets insets = getInsets();
            int x = (int) (((double) (e.getX() - insets.left)) / this.scaleX);
            int y = (int) (((double) (e.getY() - insets.top)) / this.scaleY);
            ChartEntity entity = null;
            if (this.info != null) {
                EntityCollection entities = this.info.getEntityCollection();
                if (entities != null) {
                    entity = entities.getEntity((double) x, (double) y);
                }
            }
            if (this.chart != null) {
                ChartMouseEvent event = new ChartMouseEvent(getChart(), e, entity);
                for (int i = listeners.length - 1; i >= 0; i--) {
                    ((ChartMouseListener) listeners[i]).chartMouseMoved(event);
                }
            }
        }
    }

    public void zoomInBoth(double x, double y) {
        Plot plot = this.chart.getPlot();
        if (plot != null) {
            boolean savedNotify = plot.isNotify();
            plot.setNotify(false);
            zoomInDomain(x, y);
            zoomInRange(x, y);
            plot.setNotify(savedNotify);
        }
    }

    public void zoomInDomain(double x, double y) {
        Plot plot = this.chart.getPlot();
        if (plot instanceof Zoomable) {
            boolean savedNotify = plot.isNotify();
            plot.setNotify(false);
            ((Zoomable) plot).zoomDomainAxes(this.zoomInFactor, this.info.getPlotInfo(), translateScreenToJava2D(new Point((int) x, (int) y)), this.zoomAroundAnchor);
            plot.setNotify(savedNotify);
        }
    }

    public void zoomInRange(double x, double y) {
        Plot plot = this.chart.getPlot();
        if (plot instanceof Zoomable) {
            boolean savedNotify = plot.isNotify();
            plot.setNotify(false);
            ((Zoomable) plot).zoomRangeAxes(this.zoomInFactor, this.info.getPlotInfo(), translateScreenToJava2D(new Point((int) x, (int) y)), this.zoomAroundAnchor);
            plot.setNotify(savedNotify);
        }
    }

    public void zoomOutBoth(double x, double y) {
        Plot plot = this.chart.getPlot();
        if (plot != null) {
            boolean savedNotify = plot.isNotify();
            plot.setNotify(false);
            zoomOutDomain(x, y);
            zoomOutRange(x, y);
            plot.setNotify(savedNotify);
        }
    }

    public void zoomOutDomain(double x, double y) {
        Plot plot = this.chart.getPlot();
        if (plot instanceof Zoomable) {
            boolean savedNotify = plot.isNotify();
            plot.setNotify(false);
            ((Zoomable) plot).zoomDomainAxes(this.zoomOutFactor, this.info.getPlotInfo(), translateScreenToJava2D(new Point((int) x, (int) y)), this.zoomAroundAnchor);
            plot.setNotify(savedNotify);
        }
    }

    public void zoomOutRange(double x, double y) {
        Plot plot = this.chart.getPlot();
        if (plot instanceof Zoomable) {
            boolean savedNotify = plot.isNotify();
            plot.setNotify(false);
            ((Zoomable) plot).zoomRangeAxes(this.zoomOutFactor, this.info.getPlotInfo(), translateScreenToJava2D(new Point((int) x, (int) y)), this.zoomAroundAnchor);
            plot.setNotify(savedNotify);
        }
    }

    public void zoom(Rectangle2D selection) {
        Point2D selectOrigin = translateScreenToJava2D(new Point((int) Math.ceil(selection.getX()), (int) Math.ceil(selection.getY())));
        PlotRenderingInfo plotInfo = this.info.getPlotInfo();
        Rectangle2D scaledDataArea = getScreenDataArea((int) selection.getCenterX(), (int) selection.getCenterY());
        if (selection.getHeight() > 0.0d && selection.getWidth() > 0.0d) {
            double hLower = (selection.getMinX() - scaledDataArea.getMinX()) / scaledDataArea.getWidth();
            double hUpper = (selection.getMaxX() - scaledDataArea.getMinX()) / scaledDataArea.getWidth();
            double vLower = (scaledDataArea.getMaxY() - selection.getMaxY()) / scaledDataArea.getHeight();
            double vUpper = (scaledDataArea.getMaxY() - selection.getMinY()) / scaledDataArea.getHeight();
            Plot p = this.chart.getPlot();
            if (p instanceof Zoomable) {
                boolean savedNotify = p.isNotify();
                p.setNotify(false);
                Zoomable z = (Zoomable) p;
                if (z.getOrientation() == PlotOrientation.HORIZONTAL) {
                    z.zoomDomainAxes(vLower, vUpper, plotInfo, selectOrigin);
                    z.zoomRangeAxes(hLower, hUpper, plotInfo, selectOrigin);
                } else {
                    z.zoomDomainAxes(hLower, hUpper, plotInfo, selectOrigin);
                    z.zoomRangeAxes(vLower, vUpper, plotInfo, selectOrigin);
                }
                p.setNotify(savedNotify);
            }
        }
    }

    public void restoreAutoBounds() {
        Plot plot = this.chart.getPlot();
        if (plot != null) {
            boolean savedNotify = plot.isNotify();
            plot.setNotify(false);
            restoreAutoDomainBounds();
            restoreAutoRangeBounds();
            plot.setNotify(savedNotify);
        }
    }

    public void restoreAutoDomainBounds() {
        Plot plot = this.chart.getPlot();
        if (plot instanceof Zoomable) {
            Zoomable z = (Zoomable) plot;
            boolean savedNotify = plot.isNotify();
            plot.setNotify(false);
            z.zoomDomainAxes(0.0d, this.info.getPlotInfo(), this.zoomPoint != null ? this.zoomPoint : new Point());
            plot.setNotify(savedNotify);
        }
    }

    public void restoreAutoRangeBounds() {
        Plot plot = this.chart.getPlot();
        if (plot instanceof Zoomable) {
            Zoomable z = (Zoomable) plot;
            boolean savedNotify = plot.isNotify();
            plot.setNotify(false);
            z.zoomRangeAxes(0.0d, this.info.getPlotInfo(), this.zoomPoint != null ? this.zoomPoint : new Point());
            plot.setNotify(savedNotify);
        }
    }

    public Rectangle2D getScreenDataArea() {
        Rectangle2D dataArea = this.info.getPlotInfo().getDataArea();
        Insets insets = getInsets();
        return new Rectangle2D.Double((dataArea.getX() * this.scaleX) + ((double) insets.left), (dataArea.getY() * this.scaleY) + ((double) insets.top), dataArea.getWidth() * this.scaleX, dataArea.getHeight() * this.scaleY);
    }

    public Rectangle2D getScreenDataArea(int x, int y) {
        PlotRenderingInfo plotInfo = this.info.getPlotInfo();
        if (plotInfo.getSubplotCount() == 0) {
            return getScreenDataArea();
        }
        int subplotIndex = plotInfo.getSubplotIndex(translateScreenToJava2D(new Point(x, y)));
        if (subplotIndex == -1) {
            return null;
        }
        return scale(plotInfo.getSubplotInfo(subplotIndex).getDataArea());
    }

    public int getInitialDelay() {
        return this.ownToolTipInitialDelay;
    }

    public int getReshowDelay() {
        return this.ownToolTipReshowDelay;
    }

    public int getDismissDelay() {
        return this.ownToolTipDismissDelay;
    }

    public void setInitialDelay(int delay) {
        this.ownToolTipInitialDelay = delay;
    }

    public void setReshowDelay(int delay) {
        this.ownToolTipReshowDelay = delay;
    }

    public void setDismissDelay(int delay) {
        this.ownToolTipDismissDelay = delay;
    }

    public double getZoomInFactor() {
        return this.zoomInFactor;
    }

    public void setZoomInFactor(double factor) {
        this.zoomInFactor = factor;
    }

    public double getZoomOutFactor() {
        return this.zoomOutFactor;
    }

    public void setZoomOutFactor(double factor) {
        this.zoomOutFactor = factor;
    }

    private void drawZoomRectangle(Graphics2D g2, boolean xor) {
        if (this.zoomRectangle != null) {
            if (xor) {
                g2.setXORMode(Color.gray);
            }
            if (this.fillZoomRectangle) {
                g2.setPaint(this.zoomFillPaint);
                g2.fill(this.zoomRectangle);
            } else {
                g2.setPaint(this.zoomOutlinePaint);
                g2.draw(this.zoomRectangle);
            }
            if (xor) {
                g2.setPaintMode();
            }
        }
    }

    private void drawHorizontalAxisTrace(Graphics2D g2, int x) {
        Rectangle2D dataArea = getScreenDataArea();
        g2.setXORMode(Color.orange);
        if (((int) dataArea.getMinX()) < x && x < ((int) dataArea.getMaxX())) {
            if (this.verticalTraceLine != null) {
                g2.draw(this.verticalTraceLine);
                this.verticalTraceLine.setLine((double) x, (double) ((int) dataArea.getMinY()), (double) x, (double) ((int) dataArea.getMaxY()));
            } else {
                this.verticalTraceLine = new Float((float) x, (float) ((int) dataArea.getMinY()), (float) x, (float) ((int) dataArea.getMaxY()));
            }
            g2.draw(this.verticalTraceLine);
        }
        g2.setPaintMode();
    }

    private void drawVerticalAxisTrace(Graphics2D g2, int y) {
        Rectangle2D dataArea = getScreenDataArea();
        g2.setXORMode(Color.orange);
        if (((int) dataArea.getMinY()) < y && y < ((int) dataArea.getMaxY())) {
            if (this.horizontalTraceLine != null) {
                g2.draw(this.horizontalTraceLine);
                this.horizontalTraceLine.setLine((double) ((int) dataArea.getMinX()), (double) y, (double) ((int) dataArea.getMaxX()), (double) y);
            } else {
                this.horizontalTraceLine = new Float((float) ((int) dataArea.getMinX()), (float) y, (float) ((int) dataArea.getMaxX()), (float) y);
            }
            g2.draw(this.horizontalTraceLine);
        }
        g2.setPaintMode();
    }

    public void doEditChartProperties() {
        ChartEditor editor = ChartEditorManager.getChartEditor(this.chart);
        if (JOptionPane.showConfirmDialog(this, editor, localizationResources.getString("Chart_Properties"), 2, -1) == 0) {
            editor.updateChart(this.chart);
        }
    }

    public void doCopy() {
        Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Insets insets = getInsets();
        systemClipboard.setContents(new ChartTransferable(this.chart, (getWidth() - insets.left) - insets.right, (getHeight() - insets.top) - insets.bottom, getMinimumDrawWidth(), getMinimumDrawHeight(), getMaximumDrawWidth(), getMaximumDrawHeight(), DEFAULT_BUFFER_USED), null);
    }

    public void doSaveAs() throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(this.defaultDirectoryForSaveAs);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(localizationResources.getString("PNG_Image_Files"), new String[]{ImageFormat.PNG});
        fileChooser.addChoosableFileFilter(filter);
        fileChooser.setFileFilter(filter);
        if (fileChooser.showSaveDialog(this) == 0) {
            String filename = fileChooser.getSelectedFile().getPath();
            if (isEnforceFileExtensions() && !filename.endsWith(".png")) {
                filename = filename + ".png";
            }
            ChartUtilities.saveChartAsPNG(new File(filename), this.chart, getWidth(), getHeight());
        }
    }

    private void saveAsSVG(File f) throws IOException {
        Throwable th;
        File file = f;
        if (file == null) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(this.defaultDirectoryForSaveAs);
            FileNameExtensionFilter filter = new FileNameExtensionFilter(localizationResources.getString("SVG_Files"), new String[]{"svg"});
            fileChooser.addChoosableFileFilter(filter);
            fileChooser.setFileFilter(filter);
            if (fileChooser.showSaveDialog(this) == 0) {
                String filename = fileChooser.getSelectedFile().getPath();
                if (isEnforceFileExtensions() && !filename.endsWith(".svg")) {
                    filename = filename + ".svg";
                }
                file = new File(filename);
                if (file.exists() && JOptionPane.showConfirmDialog(this, localizationResources.getString("FILE_EXISTS_CONFIRM_OVERWRITE"), "Save As SVG", 2) == 2) {
                    file = null;
                }
            }
        }
        if (file != null) {
            String svg = generateSVG(getWidth(), getHeight());
            BufferedWriter writer = null;
            try {
                BufferedWriter writer2 = new BufferedWriter(new FileWriter(file));
                try {
                    writer2.write("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n");
                    writer2.write(svg + "\n");
                    writer2.flush();
                    if (writer2 != null) {
                        try {
                            writer2.close();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                } catch (Throwable th2) {
                    th = th2;
                    writer = writer2;
                    if (writer != null) {
                        try {
                            writer.close();
                        } catch (IOException ex2) {
                            throw new RuntimeException(ex2);
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                if (writer != null) {
                    writer.close();
                }
                throw th;
            }
        }
    }

    private String generateSVG(int width, int height) {
        Graphics2D g2 = createSVGGraphics2D(width, height);
        if (g2 == null) {
            throw new IllegalStateException("JFreeSVG library is not present.");
        }
        g2.setRenderingHint(JFreeChart.KEY_SUPPRESS_SHADOW_GENERATION, Boolean.valueOf(DEFAULT_BUFFER_USED));
        String svg = null;
        this.chart.draw(g2, new Rectangle2D.Double(0.0d, 0.0d, (double) width, (double) height));
        try {
            return (String) g2.getClass().getMethod("getSVGElement", new Class[0]).invoke(g2, new Object[0]);
        } catch (NoSuchMethodException e) {
            return svg;
        } catch (SecurityException e2) {
            return svg;
        } catch (IllegalAccessException e3) {
            return svg;
        } catch (IllegalArgumentException e4) {
            return svg;
        } catch (InvocationTargetException e5) {
            return svg;
        }
    }

    private Graphics2D createSVGGraphics2D(int w, int h) {
        try {
            return (Graphics2D) Class.forName("org.jfree.graphics2d.svg.SVGGraphics2D").getConstructor(new Class[]{Integer.TYPE, Integer.TYPE}).newInstance(new Object[]{Integer.valueOf(w), Integer.valueOf(h)});
        } catch (ClassNotFoundException e) {
            return null;
        } catch (NoSuchMethodException e2) {
            return null;
        } catch (SecurityException e3) {
            return null;
        } catch (InstantiationException e4) {
            return null;
        } catch (IllegalAccessException e5) {
            return null;
        } catch (IllegalArgumentException e6) {
            return null;
        } catch (InvocationTargetException e7) {
            return null;
        }
    }

    private void saveAsPDF(File f) {
        File file = f;
        if (file == null) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(this.defaultDirectoryForSaveAs);
            FileNameExtensionFilter filter = new FileNameExtensionFilter(localizationResources.getString("PDF_Files"), new String[]{"pdf"});
            fileChooser.addChoosableFileFilter(filter);
            fileChooser.setFileFilter(filter);
            if (fileChooser.showSaveDialog(this) == 0) {
                String filename = fileChooser.getSelectedFile().getPath();
                if (isEnforceFileExtensions() && !filename.endsWith(".pdf")) {
                    filename = filename + ".pdf";
                }
                file = new File(filename);
                if (file.exists() && JOptionPane.showConfirmDialog(this, localizationResources.getString("FILE_EXISTS_CONFIRM_OVERWRITE"), "Save As PDF", 2) == 2) {
                    file = null;
                }
            }
        }
        if (file != null) {
            writeAsPDF(file, getWidth(), getHeight());
        }
    }

    private boolean isOrsonPDFAvailable() {
        Class pdfDocumentClass = null;
        try {
            pdfDocumentClass = Class.forName("com.orsonpdf.PDFDocument");
        } catch (ClassNotFoundException e) {
        }
        return pdfDocumentClass != null ? DEFAULT_BUFFER_USED : false;
    }

    private void writeAsPDF(File file, int w, int h) {
        if (isOrsonPDFAvailable()) {
            ParamChecks.nullNotPermitted(file, "file");
            try {
                Class pdfDocClass = Class.forName("com.orsonpdf.PDFDocument");
                Object pdfDoc = pdfDocClass.newInstance();
                Class cls = pdfDocClass;
                Object page = cls.getMethod("createPage", new Class[]{Rectangle2D.class}).invoke(pdfDoc, new Object[]{new Rectangle(w, h)});
                Method method = page.getClass().getMethod("getGraphics2D", new Class[0]);
                Graphics2D g2 = (Graphics2D) m2.invoke(page, new Object[0]);
                g2.setRenderingHint(JFreeChart.KEY_SUPPRESS_SHADOW_GENERATION, Boolean.valueOf(DEFAULT_BUFFER_USED));
                this.chart.draw(g2, new Rectangle2D.Double(0.0d, 0.0d, (double) w, (double) h));
                cls = pdfDocClass;
                cls.getMethod("writeToFile", new Class[]{File.class}).invoke(pdfDoc, new Object[]{file});
                return;
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            } catch (InstantiationException ex2) {
                throw new RuntimeException(ex2);
            } catch (IllegalAccessException ex3) {
                throw new RuntimeException(ex3);
            } catch (NoSuchMethodException ex4) {
                throw new RuntimeException(ex4);
            } catch (SecurityException ex5) {
                throw new RuntimeException(ex5);
            } catch (IllegalArgumentException ex6) {
                throw new RuntimeException(ex6);
            } catch (InvocationTargetException ex7) {
                throw new RuntimeException(ex7);
            }
        }
        throw new IllegalStateException("OrsonPDF is not present on the classpath.");
    }

    public void createChartPrintJob() {
        PrinterJob job = PrinterJob.getPrinterJob();
        PageFormat pf = job.defaultPage();
        PageFormat pf2 = job.pageDialog(pf);
        if (pf2 != pf) {
            job.setPrintable(this, pf2);
            if (job.printDialog()) {
                try {
                    job.print();
                } catch (PrinterException e) {
                    JOptionPane.showMessageDialog(this, e);
                }
            }
        }
    }

    public int print(Graphics g, PageFormat pf, int pageIndex) {
        if (pageIndex != 0) {
            return 1;
        }
        this.chart.draw((Graphics2D) g, new Rectangle2D.Double(pf.getImageableX(), pf.getImageableY(), pf.getImageableWidth(), pf.getImageableHeight()), this.anchor, null);
        return 0;
    }

    public void addChartMouseListener(ChartMouseListener listener) {
        ParamChecks.nullNotPermitted(listener, "listener");
        this.chartMouseListeners.add(ChartMouseListener.class, listener);
    }

    public void removeChartMouseListener(ChartMouseListener listener) {
        this.chartMouseListeners.remove(ChartMouseListener.class, listener);
    }

    public EventListener[] getListeners(Class listenerType) {
        if (listenerType == ChartMouseListener.class) {
            return this.chartMouseListeners.getListeners(listenerType);
        }
        return super.getListeners(listenerType);
    }

    protected JPopupMenu createPopupMenu(boolean properties, boolean save, boolean print, boolean zoom) {
        return createPopupMenu(properties, false, save, print, zoom);
    }

    protected JPopupMenu createPopupMenu(boolean properties, boolean copy, boolean save, boolean print, boolean zoom) {
        JPopupMenu result = new JPopupMenu(localizationResources.getString("Chart") + ":");
        boolean separator = false;
        if (properties) {
            JMenuItem propertiesItem = new JMenuItem(localizationResources.getString("Properties..."));
            propertiesItem.setActionCommand(PROPERTIES_COMMAND);
            propertiesItem.addActionListener(this);
            result.add(propertiesItem);
            separator = DEFAULT_BUFFER_USED;
        }
        if (copy) {
            if (separator) {
                result.addSeparator();
            }
            JMenuItem copyItem = new JMenuItem(localizationResources.getString("Copy"));
            copyItem.setActionCommand(COPY_COMMAND);
            copyItem.addActionListener(this);
            result.add(copyItem);
            separator = !save ? DEFAULT_BUFFER_USED : false;
        }
        if (save) {
            if (separator) {
                result.addSeparator();
            }
            JMenu saveSubMenu = new JMenu(localizationResources.getString("Save_as"));
            JMenuItem pngItem = new JMenuItem(localizationResources.getString("PNG..."));
            pngItem.setActionCommand(SAVE_AS_PNG_COMMAND);
            pngItem.addActionListener(this);
            saveSubMenu.add(pngItem);
            if (createSVGGraphics2D(DEFAULT_ZOOM_TRIGGER_DISTANCE, DEFAULT_ZOOM_TRIGGER_DISTANCE) != null) {
                JMenuItem svgItem = new JMenuItem(localizationResources.getString("SVG..."));
                svgItem.setActionCommand(SAVE_AS_SVG_COMMAND);
                svgItem.addActionListener(this);
                saveSubMenu.add(svgItem);
            }
            if (isOrsonPDFAvailable()) {
                JMenuItem pdfItem = new JMenuItem(localizationResources.getString("PDF..."));
                pdfItem.setActionCommand(SAVE_AS_PDF_COMMAND);
                pdfItem.addActionListener(this);
                saveSubMenu.add(pdfItem);
            }
            result.add(saveSubMenu);
            separator = DEFAULT_BUFFER_USED;
        }
        if (print) {
            if (separator) {
                result.addSeparator();
            }
            JMenuItem printItem = new JMenuItem(localizationResources.getString("Print..."));
            printItem.setActionCommand(PRINT_COMMAND);
            printItem.addActionListener(this);
            result.add(printItem);
            separator = DEFAULT_BUFFER_USED;
        }
        if (zoom) {
            if (separator) {
                result.addSeparator();
            }
            JMenu zoomInMenu = new JMenu(localizationResources.getString("Zoom_In"));
            this.zoomInBothMenuItem = new JMenuItem(localizationResources.getString("All_Axes"));
            this.zoomInBothMenuItem.setActionCommand(ZOOM_IN_BOTH_COMMAND);
            this.zoomInBothMenuItem.addActionListener(this);
            zoomInMenu.add(this.zoomInBothMenuItem);
            zoomInMenu.addSeparator();
            this.zoomInDomainMenuItem = new JMenuItem(localizationResources.getString("Domain_Axis"));
            this.zoomInDomainMenuItem.setActionCommand(ZOOM_IN_DOMAIN_COMMAND);
            this.zoomInDomainMenuItem.addActionListener(this);
            zoomInMenu.add(this.zoomInDomainMenuItem);
            this.zoomInRangeMenuItem = new JMenuItem(localizationResources.getString("Range_Axis"));
            this.zoomInRangeMenuItem.setActionCommand(ZOOM_IN_RANGE_COMMAND);
            this.zoomInRangeMenuItem.addActionListener(this);
            zoomInMenu.add(this.zoomInRangeMenuItem);
            result.add(zoomInMenu);
            JMenu zoomOutMenu = new JMenu(localizationResources.getString("Zoom_Out"));
            this.zoomOutBothMenuItem = new JMenuItem(localizationResources.getString("All_Axes"));
            this.zoomOutBothMenuItem.setActionCommand(ZOOM_OUT_BOTH_COMMAND);
            this.zoomOutBothMenuItem.addActionListener(this);
            zoomOutMenu.add(this.zoomOutBothMenuItem);
            zoomOutMenu.addSeparator();
            this.zoomOutDomainMenuItem = new JMenuItem(localizationResources.getString("Domain_Axis"));
            this.zoomOutDomainMenuItem.setActionCommand(ZOOM_OUT_DOMAIN_COMMAND);
            this.zoomOutDomainMenuItem.addActionListener(this);
            zoomOutMenu.add(this.zoomOutDomainMenuItem);
            this.zoomOutRangeMenuItem = new JMenuItem(localizationResources.getString("Range_Axis"));
            this.zoomOutRangeMenuItem.setActionCommand(ZOOM_OUT_RANGE_COMMAND);
            this.zoomOutRangeMenuItem.addActionListener(this);
            zoomOutMenu.add(this.zoomOutRangeMenuItem);
            result.add(zoomOutMenu);
            JMenu autoRangeMenu = new JMenu(localizationResources.getString("Auto_Range"));
            this.zoomResetBothMenuItem = new JMenuItem(localizationResources.getString("All_Axes"));
            this.zoomResetBothMenuItem.setActionCommand(ZOOM_RESET_BOTH_COMMAND);
            this.zoomResetBothMenuItem.addActionListener(this);
            autoRangeMenu.add(this.zoomResetBothMenuItem);
            autoRangeMenu.addSeparator();
            this.zoomResetDomainMenuItem = new JMenuItem(localizationResources.getString("Domain_Axis"));
            this.zoomResetDomainMenuItem.setActionCommand(ZOOM_RESET_DOMAIN_COMMAND);
            this.zoomResetDomainMenuItem.addActionListener(this);
            autoRangeMenu.add(this.zoomResetDomainMenuItem);
            this.zoomResetRangeMenuItem = new JMenuItem(localizationResources.getString("Range_Axis"));
            this.zoomResetRangeMenuItem.setActionCommand(ZOOM_RESET_RANGE_COMMAND);
            this.zoomResetRangeMenuItem.addActionListener(this);
            autoRangeMenu.add(this.zoomResetRangeMenuItem);
            result.addSeparator();
            result.add(autoRangeMenu);
        }
        return result;
    }

    protected void displayPopupMenu(int x, int y) {
        boolean z = DEFAULT_BUFFER_USED;
        if (this.popup != null) {
            JMenuItem jMenuItem;
            boolean z2;
            boolean isDomainZoomable = false;
            boolean isRangeZoomable = false;
            Plot plot = this.chart != null ? this.chart.getPlot() : null;
            if (plot instanceof Zoomable) {
                Zoomable z3 = (Zoomable) plot;
                isDomainZoomable = z3.isDomainZoomable();
                isRangeZoomable = z3.isRangeZoomable();
            }
            if (this.zoomInDomainMenuItem != null) {
                this.zoomInDomainMenuItem.setEnabled(isDomainZoomable);
            }
            if (this.zoomOutDomainMenuItem != null) {
                this.zoomOutDomainMenuItem.setEnabled(isDomainZoomable);
            }
            if (this.zoomResetDomainMenuItem != null) {
                this.zoomResetDomainMenuItem.setEnabled(isDomainZoomable);
            }
            if (this.zoomInRangeMenuItem != null) {
                this.zoomInRangeMenuItem.setEnabled(isRangeZoomable);
            }
            if (this.zoomOutRangeMenuItem != null) {
                this.zoomOutRangeMenuItem.setEnabled(isRangeZoomable);
            }
            if (this.zoomResetRangeMenuItem != null) {
                this.zoomResetRangeMenuItem.setEnabled(isRangeZoomable);
            }
            if (this.zoomInBothMenuItem != null) {
                jMenuItem = this.zoomInBothMenuItem;
                if (isDomainZoomable && isRangeZoomable) {
                    z2 = DEFAULT_BUFFER_USED;
                } else {
                    z2 = false;
                }
                jMenuItem.setEnabled(z2);
            }
            if (this.zoomOutBothMenuItem != null) {
                jMenuItem = this.zoomOutBothMenuItem;
                if (isDomainZoomable && isRangeZoomable) {
                    z2 = DEFAULT_BUFFER_USED;
                } else {
                    z2 = false;
                }
                jMenuItem.setEnabled(z2);
            }
            if (this.zoomResetBothMenuItem != null) {
                JMenuItem jMenuItem2 = this.zoomResetBothMenuItem;
                if (!(isDomainZoomable && isRangeZoomable)) {
                    z = false;
                }
                jMenuItem2.setEnabled(z);
            }
            this.popup.show(this, x, y);
        }
    }

    public void updateUI() {
        if (this.popup != null) {
            SwingUtilities.updateComponentTreeUI(this.popup);
        }
        super.updateUI();
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.zoomFillPaint, stream);
        SerialUtilities.writePaint(this.zoomOutlinePaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.zoomFillPaint = SerialUtilities.readPaint(stream);
        this.zoomOutlinePaint = SerialUtilities.readPaint(stream);
        this.chartMouseListeners = new EventListenerList();
        if (this.chart != null) {
            this.chart.addChangeListener(this);
        }
    }
}
