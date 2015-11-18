package org.jfree.chart.editor;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.ColorBar;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.ContourPlot;
import org.jfree.chart.plot.MeterPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.util.ResourceBundleWrapper;
import org.jfree.layout.LCBLayout;
import org.jfree.ui.PaintSample;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.StrokeChooserPanel;
import org.jfree.ui.StrokeSample;
import org.jfree.util.BooleanUtilities;

class DefaultPlotEditor extends JPanel implements ActionListener {
    private static final int ORIENTATION_HORIZONTAL = 1;
    private static final int ORIENTATION_VERTICAL = 0;
    protected static ResourceBundle localizationResources;
    private static final String[] orientationNames;
    private StrokeSample[] availableStrokeSamples;
    private PaintSample backgroundPaintSample;
    private DefaultColorBarEditor colorBarAxisPropertyPanel;
    private DefaultAxisEditor domainAxisPropertyPanel;
    private Boolean drawLines;
    private JCheckBox drawLinesCheckBox;
    private Boolean drawShapes;
    private JCheckBox drawShapesCheckBox;
    private JComboBox orientationCombo;
    private PaintSample outlinePaintSample;
    private StrokeSample outlineStrokeSample;
    private RectangleInsets plotInsets;
    private PlotOrientation plotOrientation;
    private DefaultAxisEditor rangeAxisPropertyPanel;

    static {
        orientationNames = new String[]{"Vertical", "Horizontal"};
        localizationResources = ResourceBundleWrapper.getBundle("org.jfree.chart.editor.LocalizationBundle");
    }

    public DefaultPlotEditor(Plot plot) {
        add(createPlotPanel(plot));
    }

    protected JPanel createPlotPanel(Plot plot) {
        this.plotInsets = plot.getInsets();
        this.backgroundPaintSample = new PaintSample(plot.getBackgroundPaint());
        this.outlineStrokeSample = new StrokeSample(plot.getOutlineStroke());
        this.outlinePaintSample = new PaintSample(plot.getOutlinePaint());
        if (plot instanceof CategoryPlot) {
            this.plotOrientation = ((CategoryPlot) plot).getOrientation();
        } else if (plot instanceof XYPlot) {
            this.plotOrientation = ((XYPlot) plot).getOrientation();
        }
        if (plot instanceof CategoryPlot) {
            CategoryItemRenderer renderer = ((CategoryPlot) plot).getRenderer();
            if (renderer instanceof LineAndShapeRenderer) {
                LineAndShapeRenderer r = (LineAndShapeRenderer) renderer;
                this.drawLines = BooleanUtilities.valueOf(r.getBaseLinesVisible());
                this.drawShapes = BooleanUtilities.valueOf(r.getBaseShapesVisible());
            }
        } else if (plot instanceof XYPlot) {
            XYItemRenderer renderer2 = ((XYPlot) plot).getRenderer();
            if (renderer2 instanceof StandardXYItemRenderer) {
                StandardXYItemRenderer r2 = (StandardXYItemRenderer) renderer2;
                this.drawLines = BooleanUtilities.valueOf(r2.getPlotLines());
                this.drawShapes = BooleanUtilities.valueOf(r2.getBaseShapesVisible());
            }
        }
        setLayout(new BorderLayout());
        this.availableStrokeSamples = new StrokeSample[4];
        this.availableStrokeSamples[0] = new StrokeSample(null);
        this.availableStrokeSamples[ORIENTATION_HORIZONTAL] = new StrokeSample(new BasicStroke(Plot.DEFAULT_FOREGROUND_ALPHA));
        this.availableStrokeSamples[2] = new StrokeSample(new BasicStroke(Axis.DEFAULT_TICK_MARK_OUTSIDE_LENGTH));
        this.availableStrokeSamples[3] = new StrokeSample(new BasicStroke(MeterPlot.DEFAULT_BORDER_SIZE));
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), plot.getPlotType() + localizationResources.getString(":")));
        JPanel general = new JPanel(new BorderLayout());
        general.setBorder(BorderFactory.createTitledBorder(localizationResources.getString("General")));
        JPanel interior = new JPanel(new LCBLayout(7));
        interior.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        interior.add(new JLabel(localizationResources.getString("Outline_stroke")));
        JButton button = new JButton(localizationResources.getString("Select..."));
        button.setActionCommand("OutlineStroke");
        button.addActionListener(this);
        interior.add(this.outlineStrokeSample);
        interior.add(button);
        interior.add(new JLabel(localizationResources.getString("Outline_Paint")));
        button = new JButton(localizationResources.getString("Select..."));
        button.setActionCommand("OutlinePaint");
        button.addActionListener(this);
        interior.add(this.outlinePaintSample);
        interior.add(button);
        interior.add(new JLabel(localizationResources.getString("Background_paint")));
        button = new JButton(localizationResources.getString("Select..."));
        button.setActionCommand("BackgroundPaint");
        button.addActionListener(this);
        interior.add(this.backgroundPaintSample);
        interior.add(button);
        if (this.plotOrientation != null) {
            int index = this.plotOrientation.equals(PlotOrientation.VERTICAL) ? 0 : ORIENTATION_HORIZONTAL;
            interior.add(new JLabel(localizationResources.getString("Orientation")));
            this.orientationCombo = new JComboBox(orientationNames);
            this.orientationCombo.setSelectedIndex(index);
            this.orientationCombo.setActionCommand("Orientation");
            this.orientationCombo.addActionListener(this);
            interior.add(new JPanel());
            interior.add(this.orientationCombo);
        }
        if (this.drawLines != null) {
            interior.add(new JLabel(localizationResources.getString("Draw_lines")));
            this.drawLinesCheckBox = new JCheckBox();
            this.drawLinesCheckBox.setSelected(this.drawLines.booleanValue());
            this.drawLinesCheckBox.setActionCommand("DrawLines");
            this.drawLinesCheckBox.addActionListener(this);
            interior.add(new JPanel());
            interior.add(this.drawLinesCheckBox);
        }
        if (this.drawShapes != null) {
            interior.add(new JLabel(localizationResources.getString("Draw_shapes")));
            this.drawShapesCheckBox = new JCheckBox();
            this.drawShapesCheckBox.setSelected(this.drawShapes.booleanValue());
            this.drawShapesCheckBox.setActionCommand("DrawShapes");
            this.drawShapesCheckBox.addActionListener(this);
            interior.add(new JPanel());
            interior.add(this.drawShapesCheckBox);
        }
        general.add(interior, "North");
        JPanel appearance = new JPanel(new BorderLayout());
        appearance.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        appearance.add(general, "North");
        JTabbedPane tabs = createPlotTabs(plot);
        tabs.add(localizationResources.getString("Appearance"), appearance);
        panel.add(tabs);
        return panel;
    }

    protected JTabbedPane createPlotTabs(Plot plot) {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        Axis domainAxis = null;
        if (plot instanceof CategoryPlot) {
            domainAxis = ((CategoryPlot) plot).getDomainAxis();
        } else if (plot instanceof XYPlot) {
            domainAxis = ((XYPlot) plot).getDomainAxis();
        }
        this.domainAxisPropertyPanel = DefaultAxisEditor.getInstance(domainAxis);
        if (this.domainAxisPropertyPanel != null) {
            this.domainAxisPropertyPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            tabs.add(localizationResources.getString("Domain_Axis"), this.domainAxisPropertyPanel);
        }
        Axis rangeAxis = null;
        if (plot instanceof CategoryPlot) {
            rangeAxis = ((CategoryPlot) plot).getRangeAxis();
        } else if (plot instanceof XYPlot) {
            rangeAxis = ((XYPlot) plot).getRangeAxis();
        } else if (plot instanceof PolarPlot) {
            rangeAxis = ((PolarPlot) plot).getAxis();
        }
        this.rangeAxisPropertyPanel = DefaultAxisEditor.getInstance(rangeAxis);
        if (this.rangeAxisPropertyPanel != null) {
            this.rangeAxisPropertyPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            tabs.add(localizationResources.getString("Range_Axis"), this.rangeAxisPropertyPanel);
        }
        ColorBar colorBar = null;
        if (plot instanceof ContourPlot) {
            colorBar = ((ContourPlot) plot).getColorBar();
        }
        this.colorBarAxisPropertyPanel = DefaultColorBarEditor.getInstance(colorBar);
        if (this.colorBarAxisPropertyPanel != null) {
            this.colorBarAxisPropertyPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            tabs.add(localizationResources.getString("Color_Bar"), this.colorBarAxisPropertyPanel);
        }
        return tabs;
    }

    public RectangleInsets getPlotInsets() {
        if (this.plotInsets == null) {
            this.plotInsets = new RectangleInsets(0.0d, 0.0d, 0.0d, 0.0d);
        }
        return this.plotInsets;
    }

    public Paint getBackgroundPaint() {
        return this.backgroundPaintSample.getPaint();
    }

    public Stroke getOutlineStroke() {
        return this.outlineStrokeSample.getStroke();
    }

    public Paint getOutlinePaint() {
        return this.outlinePaintSample.getPaint();
    }

    public DefaultAxisEditor getDomainAxisPropertyEditPanel() {
        return this.domainAxisPropertyPanel;
    }

    public DefaultAxisEditor getRangeAxisPropertyEditPanel() {
        return this.rangeAxisPropertyPanel;
    }

    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (command.equals("BackgroundPaint")) {
            attemptBackgroundPaintSelection();
        } else if (command.equals("OutlineStroke")) {
            attemptOutlineStrokeSelection();
        } else if (command.equals("OutlinePaint")) {
            attemptOutlinePaintSelection();
        } else if (command.equals("Orientation")) {
            attemptOrientationSelection();
        } else if (command.equals("DrawLines")) {
            attemptDrawLinesSelection();
        } else if (command.equals("DrawShapes")) {
            attemptDrawShapesSelection();
        }
    }

    private void attemptBackgroundPaintSelection() {
        Color c = JColorChooser.showDialog(this, localizationResources.getString("Background_Color"), Color.blue);
        if (c != null) {
            this.backgroundPaintSample.setPaint(c);
        }
    }

    private void attemptOutlineStrokeSelection() {
        StrokeChooserPanel panel = new StrokeChooserPanel(this.outlineStrokeSample, this.availableStrokeSamples);
        if (JOptionPane.showConfirmDialog(this, panel, localizationResources.getString("Stroke_Selection"), 2, -1) == 0) {
            this.outlineStrokeSample.setStroke(panel.getSelectedStroke());
        }
    }

    private void attemptOutlinePaintSelection() {
        Color c = JColorChooser.showDialog(this, localizationResources.getString("Outline_Color"), Color.blue);
        if (c != null) {
            this.outlinePaintSample.setPaint(c);
        }
    }

    private void attemptOrientationSelection() {
        if (this.orientationCombo.getSelectedIndex() == 0) {
            this.plotOrientation = PlotOrientation.VERTICAL;
        } else {
            this.plotOrientation = PlotOrientation.HORIZONTAL;
        }
    }

    private void attemptDrawLinesSelection() {
        this.drawLines = BooleanUtilities.valueOf(this.drawLinesCheckBox.isSelected());
    }

    private void attemptDrawShapesSelection() {
        this.drawShapes = BooleanUtilities.valueOf(this.drawShapesCheckBox.isSelected());
    }

    public void updatePlotProperties(Plot plot) {
        CategoryItemRenderer r;
        XYItemRenderer r2;
        plot.setOutlinePaint(getOutlinePaint());
        plot.setOutlineStroke(getOutlineStroke());
        plot.setBackgroundPaint(getBackgroundPaint());
        plot.setInsets(getPlotInsets());
        if (this.domainAxisPropertyPanel != null) {
            Axis domainAxis = null;
            if (plot instanceof CategoryPlot) {
                domainAxis = ((CategoryPlot) plot).getDomainAxis();
            } else if (plot instanceof XYPlot) {
                domainAxis = ((XYPlot) plot).getDomainAxis();
            }
            if (domainAxis != null) {
                this.domainAxisPropertyPanel.setAxisProperties(domainAxis);
            }
        }
        if (this.rangeAxisPropertyPanel != null) {
            Axis rangeAxis = null;
            if (plot instanceof CategoryPlot) {
                rangeAxis = ((CategoryPlot) plot).getRangeAxis();
            } else if (plot instanceof XYPlot) {
                rangeAxis = ((XYPlot) plot).getRangeAxis();
            } else if (plot instanceof PolarPlot) {
                rangeAxis = ((PolarPlot) plot).getAxis();
            }
            if (rangeAxis != null) {
                this.rangeAxisPropertyPanel.setAxisProperties(rangeAxis);
            }
        }
        if (this.plotOrientation != null) {
            if (plot instanceof CategoryPlot) {
                ((CategoryPlot) plot).setOrientation(this.plotOrientation);
            } else if (plot instanceof XYPlot) {
                ((XYPlot) plot).setOrientation(this.plotOrientation);
            }
        }
        if (this.drawLines != null) {
            if (plot instanceof CategoryPlot) {
                r = ((CategoryPlot) plot).getRenderer();
                if (r instanceof LineAndShapeRenderer) {
                    ((LineAndShapeRenderer) r).setLinesVisible(this.drawLines.booleanValue());
                }
            } else if (plot instanceof XYPlot) {
                r2 = ((XYPlot) plot).getRenderer();
                if (r2 instanceof StandardXYItemRenderer) {
                    ((StandardXYItemRenderer) r2).setPlotLines(this.drawLines.booleanValue());
                }
            }
        }
        if (this.drawShapes != null) {
            if (plot instanceof CategoryPlot) {
                r = ((CategoryPlot) plot).getRenderer();
                if (r instanceof LineAndShapeRenderer) {
                    ((LineAndShapeRenderer) r).setShapesVisible(this.drawShapes.booleanValue());
                }
            } else if (plot instanceof XYPlot) {
                r2 = ((XYPlot) plot).getRenderer();
                if (r2 instanceof StandardXYItemRenderer) {
                    ((StandardXYItemRenderer) r2).setBaseShapesVisible(this.drawShapes.booleanValue());
                }
            }
        }
        if (this.colorBarAxisPropertyPanel != null) {
            ColorBar colorBar = null;
            if (plot instanceof ContourPlot) {
                colorBar = ((ContourPlot) plot).getColorBar();
            }
            if (colorBar != null) {
                this.colorBarAxisPropertyPanel.setAxisProperties(colorBar);
            }
        }
    }
}
