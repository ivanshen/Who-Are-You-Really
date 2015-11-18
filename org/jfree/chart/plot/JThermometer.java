package org.jfree.chart.plot;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.io.Serializable;
import java.text.DecimalFormat;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.title.Title;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.ui.RectangleInsets;

public class JThermometer extends JPanel implements Serializable {
    private static final long serialVersionUID = 1079905665515589820L;
    private JFreeChart chart;
    private DefaultValueDataset data;
    private ChartPanel panel;
    private ThermometerPlot plot;

    public JThermometer() {
        super(new CardLayout());
        this.plot = new ThermometerPlot();
        this.plot.setInsets(new RectangleInsets(XYPointerAnnotation.DEFAULT_ARROW_LENGTH, XYPointerAnnotation.DEFAULT_ARROW_LENGTH, XYPointerAnnotation.DEFAULT_ARROW_LENGTH, XYPointerAnnotation.DEFAULT_ARROW_LENGTH));
        this.data = new DefaultValueDataset();
        this.plot.setDataset(this.data);
        this.chart = new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT, this.plot, false);
        this.panel = new ChartPanel(this.chart);
        add(this.panel, "Panel");
        setBackground(getBackground());
    }

    public void addSubtitle(Title subtitle) {
        this.chart.addSubtitle(subtitle);
    }

    public void addSubtitle(String subtitle) {
        this.chart.addSubtitle(new TextTitle(subtitle));
    }

    public void addSubtitle(String subtitle, Font font) {
        this.chart.addSubtitle(new TextTitle(subtitle, font));
    }

    public void setValueFormat(DecimalFormat df) {
        this.plot.setValueFormat(df);
    }

    public void setRange(double lower, double upper) {
        this.plot.setRange(lower, upper);
    }

    public void setSubrangeInfo(int range, double displayLow, double displayHigh) {
        this.plot.setSubrangeInfo(range, displayLow, displayHigh);
    }

    public void setSubrangeInfo(int range, double rangeLow, double rangeHigh, double displayLow, double displayHigh) {
        this.plot.setSubrangeInfo(range, rangeLow, rangeHigh, displayLow, displayHigh);
    }

    public void setValueLocation(int loc) {
        this.plot.setValueLocation(loc);
        this.panel.repaint();
    }

    public void setValuePaint(Paint paint) {
        this.plot.setValuePaint(paint);
    }

    public Number getValue() {
        if (this.data != null) {
            return this.data.getValue();
        }
        return null;
    }

    public void setValue(double value) {
        setValue(new Double(value));
    }

    public void setValue(Number value) {
        if (this.data != null) {
            this.data.setValue(value);
        }
    }

    public void setUnits(int i) {
        if (this.plot != null) {
            this.plot.setUnits(i);
        }
    }

    public void setOutlinePaint(Paint p) {
        if (this.plot != null) {
            this.plot.setOutlinePaint(p);
        }
    }

    public void setForeground(Color fg) {
        super.setForeground(fg);
        if (this.plot != null) {
            this.plot.setThermometerPaint(fg);
        }
    }

    public void setBackground(Color bg) {
        super.setBackground(bg);
        if (this.plot != null) {
            this.plot.setBackgroundPaint(bg);
        }
        if (this.chart != null) {
            this.chart.setBackgroundPaint(bg);
        }
        if (this.panel != null) {
            this.panel.setBackground(bg);
        }
    }

    public void setValueFont(Font f) {
        if (this.plot != null) {
            this.plot.setValueFont(f);
        }
    }

    public Font getTickLabelFont() {
        return this.plot.getRangeAxis().getTickLabelFont();
    }

    public void setTickLabelFont(Font font) {
        this.plot.getRangeAxis().setTickLabelFont(font);
    }

    public void changeTickFontSize(int delta) {
        Font f = getTickLabelFont();
        setTickLabelFont(new Font(f.getFontName(), f.getStyle(), f.getSize() + delta));
    }

    public void setTickFontStyle(int style) {
        Font f = getTickLabelFont();
        setTickLabelFont(new Font(f.getFontName(), style, f.getSize()));
    }

    public void setFollowDataInSubranges(boolean flag) {
        this.plot.setFollowDataInSubranges(flag);
    }

    public void setShowValueLines(boolean b) {
        this.plot.setShowValueLines(b);
    }

    public void setShowAxisLocation(int location) {
        this.plot.setAxisLocation(location);
    }

    public int getShowAxisLocation() {
        return this.plot.getAxisLocation();
    }
}
