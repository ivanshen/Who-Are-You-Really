package org.jfree.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D.Float;
import java.awt.geom.Rectangle2D.Double;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.AttributedString;
import java.text.CharacterIterator;
import org.jfree.chart.renderer.xy.XYLine3DRenderer;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.general.Dataset;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.GradientPaintTransformer;
import org.jfree.ui.StandardGradientPaintTransformer;
import org.jfree.util.AttributedStringUtilities;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;
import org.jfree.util.ShapeUtilities;

public class LegendItem implements Cloneable, Serializable {
    private static final Shape UNUSED_SHAPE;
    private static final Stroke UNUSED_STROKE;
    private static final long serialVersionUID = -797214582948827144L;
    private transient AttributedString attributedLabel;
    private Dataset dataset;
    private int datasetIndex;
    private String description;
    private transient Paint fillPaint;
    private GradientPaintTransformer fillPaintTransformer;
    private String label;
    private Font labelFont;
    private transient Paint labelPaint;
    private transient Shape line;
    private transient Paint linePaint;
    private transient Stroke lineStroke;
    private boolean lineVisible;
    private transient Paint outlinePaint;
    private transient Stroke outlineStroke;
    private int series;
    private Comparable seriesKey;
    private transient Shape shape;
    private boolean shapeFilled;
    private boolean shapeOutlineVisible;
    private boolean shapeVisible;
    private String toolTipText;
    private String urlText;

    static {
        UNUSED_SHAPE = new Float();
        UNUSED_STROKE = new BasicStroke(0.0f);
    }

    public LegendItem(String label) {
        this(label, Color.black);
    }

    public LegendItem(String label, Paint paint) {
        this(label, null, null, null, (Shape) new Double(-4.0d, -4.0d, XYLine3DRenderer.DEFAULT_Y_OFFSET, XYLine3DRenderer.DEFAULT_Y_OFFSET), paint);
    }

    public LegendItem(String label, String description, String toolTipText, String urlText, Shape shape, Paint fillPaint) {
        this(label, description, toolTipText, urlText, true, shape, true, fillPaint, false, Color.black, UNUSED_STROKE, false, UNUSED_SHAPE, UNUSED_STROKE, Color.black);
    }

    public LegendItem(String label, String description, String toolTipText, String urlText, Shape shape, Paint fillPaint, Stroke outlineStroke, Paint outlinePaint) {
        this(label, description, toolTipText, urlText, true, shape, true, fillPaint, true, outlinePaint, outlineStroke, false, UNUSED_SHAPE, UNUSED_STROKE, Color.black);
    }

    public LegendItem(String label, String description, String toolTipText, String urlText, Shape line, Stroke lineStroke, Paint linePaint) {
        this(label, description, toolTipText, urlText, false, UNUSED_SHAPE, false, Color.black, false, Color.black, UNUSED_STROKE, true, line, lineStroke, linePaint);
    }

    public LegendItem(String label, String description, String toolTipText, String urlText, boolean shapeVisible, Shape shape, boolean shapeFilled, Paint fillPaint, boolean shapeOutlineVisible, Paint outlinePaint, Stroke outlineStroke, boolean lineVisible, Shape line, Stroke lineStroke, Paint linePaint) {
        ParamChecks.nullNotPermitted(label, "label");
        ParamChecks.nullNotPermitted(fillPaint, "fillPaint");
        ParamChecks.nullNotPermitted(lineStroke, "lineStroke");
        ParamChecks.nullNotPermitted(outlinePaint, "outlinePaint");
        ParamChecks.nullNotPermitted(outlineStroke, "outlineStroke");
        this.label = label;
        this.labelPaint = null;
        this.attributedLabel = null;
        this.description = description;
        this.shapeVisible = shapeVisible;
        this.shape = shape;
        this.shapeFilled = shapeFilled;
        this.fillPaint = fillPaint;
        this.fillPaintTransformer = new StandardGradientPaintTransformer();
        this.shapeOutlineVisible = shapeOutlineVisible;
        this.outlinePaint = outlinePaint;
        this.outlineStroke = outlineStroke;
        this.lineVisible = lineVisible;
        this.line = line;
        this.lineStroke = lineStroke;
        this.linePaint = linePaint;
        this.toolTipText = toolTipText;
        this.urlText = urlText;
    }

    public LegendItem(AttributedString label, String description, String toolTipText, String urlText, Shape shape, Paint fillPaint) {
        this(label, description, toolTipText, urlText, true, shape, true, fillPaint, false, Color.black, UNUSED_STROKE, false, UNUSED_SHAPE, UNUSED_STROKE, Color.black);
    }

    public LegendItem(AttributedString label, String description, String toolTipText, String urlText, Shape shape, Paint fillPaint, Stroke outlineStroke, Paint outlinePaint) {
        this(label, description, toolTipText, urlText, true, shape, true, fillPaint, true, outlinePaint, outlineStroke, false, UNUSED_SHAPE, UNUSED_STROKE, Color.black);
    }

    public LegendItem(AttributedString label, String description, String toolTipText, String urlText, Shape line, Stroke lineStroke, Paint linePaint) {
        this(label, description, toolTipText, urlText, false, UNUSED_SHAPE, false, Color.black, false, Color.black, UNUSED_STROKE, true, line, lineStroke, linePaint);
    }

    public LegendItem(AttributedString label, String description, String toolTipText, String urlText, boolean shapeVisible, Shape shape, boolean shapeFilled, Paint fillPaint, boolean shapeOutlineVisible, Paint outlinePaint, Stroke outlineStroke, boolean lineVisible, Shape line, Stroke lineStroke, Paint linePaint) {
        ParamChecks.nullNotPermitted(label, "label");
        ParamChecks.nullNotPermitted(fillPaint, "fillPaint");
        ParamChecks.nullNotPermitted(lineStroke, "lineStroke");
        ParamChecks.nullNotPermitted(line, "line");
        ParamChecks.nullNotPermitted(linePaint, "linePaint");
        ParamChecks.nullNotPermitted(outlinePaint, "outlinePaint");
        ParamChecks.nullNotPermitted(outlineStroke, "outlineStroke");
        this.label = characterIteratorToString(label.getIterator());
        this.attributedLabel = label;
        this.description = description;
        this.shapeVisible = shapeVisible;
        this.shape = shape;
        this.shapeFilled = shapeFilled;
        this.fillPaint = fillPaint;
        this.fillPaintTransformer = new StandardGradientPaintTransformer();
        this.shapeOutlineVisible = shapeOutlineVisible;
        this.outlinePaint = outlinePaint;
        this.outlineStroke = outlineStroke;
        this.lineVisible = lineVisible;
        this.line = line;
        this.lineStroke = lineStroke;
        this.linePaint = linePaint;
        this.toolTipText = toolTipText;
        this.urlText = urlText;
    }

    private String characterIteratorToString(CharacterIterator iterator) {
        int count = iterator.getEndIndex() - iterator.getBeginIndex();
        if (count <= 0) {
            return "";
        }
        char[] chars = new char[count];
        int i = 0;
        char c = iterator.first();
        while (c != '\uffff') {
            chars[i] = c;
            i++;
            c = iterator.next();
        }
        return new String(chars);
    }

    public Dataset getDataset() {
        return this.dataset;
    }

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    public int getDatasetIndex() {
        return this.datasetIndex;
    }

    public void setDatasetIndex(int index) {
        this.datasetIndex = index;
    }

    public Comparable getSeriesKey() {
        return this.seriesKey;
    }

    public void setSeriesKey(Comparable key) {
        this.seriesKey = key;
    }

    public int getSeriesIndex() {
        return this.series;
    }

    public void setSeriesIndex(int index) {
        this.series = index;
    }

    public String getLabel() {
        return this.label;
    }

    public Font getLabelFont() {
        return this.labelFont;
    }

    public void setLabelFont(Font font) {
        this.labelFont = font;
    }

    public Paint getLabelPaint() {
        return this.labelPaint;
    }

    public void setLabelPaint(Paint paint) {
        this.labelPaint = paint;
    }

    public AttributedString getAttributedLabel() {
        return this.attributedLabel;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String text) {
        this.description = text;
    }

    public String getToolTipText() {
        return this.toolTipText;
    }

    public void setToolTipText(String text) {
        this.toolTipText = text;
    }

    public String getURLText() {
        return this.urlText;
    }

    public void setURLText(String text) {
        this.urlText = text;
    }

    public boolean isShapeVisible() {
        return this.shapeVisible;
    }

    public void setShapeVisible(boolean visible) {
        this.shapeVisible = visible;
    }

    public Shape getShape() {
        return this.shape;
    }

    public void setShape(Shape shape) {
        ParamChecks.nullNotPermitted(shape, "shape");
        this.shape = shape;
    }

    public boolean isShapeFilled() {
        return this.shapeFilled;
    }

    public Paint getFillPaint() {
        return this.fillPaint;
    }

    public void setFillPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.fillPaint = paint;
    }

    public boolean isShapeOutlineVisible() {
        return this.shapeOutlineVisible;
    }

    public Stroke getLineStroke() {
        return this.lineStroke;
    }

    public void setLineStroke(Stroke stroke) {
        ParamChecks.nullNotPermitted(stroke, "stroke");
        this.lineStroke = stroke;
    }

    public Paint getLinePaint() {
        return this.linePaint;
    }

    public void setLinePaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.linePaint = paint;
    }

    public Paint getOutlinePaint() {
        return this.outlinePaint;
    }

    public void setOutlinePaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        this.outlinePaint = paint;
    }

    public Stroke getOutlineStroke() {
        return this.outlineStroke;
    }

    public void setOutlineStroke(Stroke stroke) {
        ParamChecks.nullNotPermitted(stroke, "stroke");
        this.outlineStroke = stroke;
    }

    public boolean isLineVisible() {
        return this.lineVisible;
    }

    public void setLineVisible(boolean visible) {
        this.lineVisible = visible;
    }

    public Shape getLine() {
        return this.line;
    }

    public void setLine(Shape line) {
        ParamChecks.nullNotPermitted(line, "line");
        this.line = line;
    }

    public GradientPaintTransformer getFillPaintTransformer() {
        return this.fillPaintTransformer;
    }

    public void setFillPaintTransformer(GradientPaintTransformer transformer) {
        ParamChecks.nullNotPermitted(transformer, "transformer");
        this.fillPaintTransformer = transformer;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LegendItem)) {
            return false;
        }
        LegendItem that = (LegendItem) obj;
        if (this.datasetIndex != that.datasetIndex || this.series != that.series || !this.label.equals(that.label) || !AttributedStringUtilities.equal(this.attributedLabel, that.attributedLabel) || !ObjectUtilities.equal(this.description, that.description) || this.shapeVisible != that.shapeVisible || !ShapeUtilities.equal(this.shape, that.shape) || this.shapeFilled != that.shapeFilled || !PaintUtilities.equal(this.fillPaint, that.fillPaint) || !ObjectUtilities.equal(this.fillPaintTransformer, that.fillPaintTransformer) || this.shapeOutlineVisible != that.shapeOutlineVisible || !this.outlineStroke.equals(that.outlineStroke) || !PaintUtilities.equal(this.outlinePaint, that.outlinePaint)) {
            return false;
        }
        if ((!this.lineVisible) != that.lineVisible && ShapeUtilities.equal(this.line, that.line) && this.lineStroke.equals(that.lineStroke) && PaintUtilities.equal(this.linePaint, that.linePaint) && ObjectUtilities.equal(this.labelFont, that.labelFont) && PaintUtilities.equal(this.labelPaint, that.labelPaint)) {
            return true;
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        LegendItem clone = (LegendItem) super.clone();
        if (this.seriesKey instanceof PublicCloneable) {
            clone.seriesKey = (Comparable) this.seriesKey.clone();
        }
        clone.shape = ShapeUtilities.clone(this.shape);
        if (this.fillPaintTransformer instanceof PublicCloneable) {
            clone.fillPaintTransformer = (GradientPaintTransformer) ((PublicCloneable) this.fillPaintTransformer).clone();
        }
        clone.line = ShapeUtilities.clone(this.line);
        return clone;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeAttributedString(this.attributedLabel, stream);
        SerialUtilities.writeShape(this.shape, stream);
        SerialUtilities.writePaint(this.fillPaint, stream);
        SerialUtilities.writeStroke(this.outlineStroke, stream);
        SerialUtilities.writePaint(this.outlinePaint, stream);
        SerialUtilities.writeShape(this.line, stream);
        SerialUtilities.writeStroke(this.lineStroke, stream);
        SerialUtilities.writePaint(this.linePaint, stream);
        SerialUtilities.writePaint(this.labelPaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.attributedLabel = SerialUtilities.readAttributedString(stream);
        this.shape = SerialUtilities.readShape(stream);
        this.fillPaint = SerialUtilities.readPaint(stream);
        this.outlineStroke = SerialUtilities.readStroke(stream);
        this.outlinePaint = SerialUtilities.readPaint(stream);
        this.line = SerialUtilities.readShape(stream);
        this.lineStroke = SerialUtilities.readStroke(stream);
        this.linePaint = SerialUtilities.readPaint(stream);
        this.labelPaint = SerialUtilities.readPaint(stream);
    }
}
