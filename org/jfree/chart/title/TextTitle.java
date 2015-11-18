package org.jfree.chart.title;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.block.BlockResult;
import org.jfree.chart.block.EntityBlockParams;
import org.jfree.chart.block.LengthConstraintType;
import org.jfree.chart.block.RectangleConstraint;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.entity.TitleEntity;
import org.jfree.chart.event.TitleChangeEvent;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.Range;
import org.jfree.io.SerialUtilities;
import org.jfree.text.G2TextMeasurer;
import org.jfree.text.TextBlock;
import org.jfree.text.TextBlockAnchor;
import org.jfree.text.TextUtilities;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.Size2D;
import org.jfree.ui.VerticalAlignment;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;

public class TextTitle extends Title implements Serializable, Cloneable, PublicCloneable {
    static final /* synthetic */ boolean $assertionsDisabled;
    public static final Font DEFAULT_FONT;
    public static final Paint DEFAULT_TEXT_PAINT;
    private static final long serialVersionUID = 8372008692127477443L;
    private transient Paint backgroundPaint;
    private TextBlock content;
    private boolean expandToFitSpace;
    private Font font;
    private int maximumLinesToDisplay;
    private transient Paint paint;
    private String text;
    private HorizontalAlignment textAlignment;
    private String toolTipText;
    private String urlText;

    static {
        $assertionsDisabled = !TextTitle.class.desiredAssertionStatus() ? true : $assertionsDisabled;
        DEFAULT_FONT = new Font("SansSerif", 1, 12);
        DEFAULT_TEXT_PAINT = Color.black;
    }

    public TextTitle() {
        this("");
    }

    public TextTitle(String text) {
        this(text, DEFAULT_FONT, DEFAULT_TEXT_PAINT, Title.DEFAULT_POSITION, Title.DEFAULT_HORIZONTAL_ALIGNMENT, Title.DEFAULT_VERTICAL_ALIGNMENT, Title.DEFAULT_PADDING);
    }

    public TextTitle(String text, Font font) {
        this(text, font, DEFAULT_TEXT_PAINT, Title.DEFAULT_POSITION, Title.DEFAULT_HORIZONTAL_ALIGNMENT, Title.DEFAULT_VERTICAL_ALIGNMENT, Title.DEFAULT_PADDING);
    }

    public TextTitle(String text, Font font, Paint paint, RectangleEdge position, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment, RectangleInsets padding) {
        super(position, horizontalAlignment, verticalAlignment, padding);
        this.expandToFitSpace = $assertionsDisabled;
        this.maximumLinesToDisplay = Integer.MAX_VALUE;
        if (text == null) {
            throw new NullPointerException("Null 'text' argument.");
        } else if (font == null) {
            throw new NullPointerException("Null 'font' argument.");
        } else if (paint == null) {
            throw new NullPointerException("Null 'paint' argument.");
        } else {
            this.text = text;
            this.font = font;
            this.paint = paint;
            this.textAlignment = horizontalAlignment;
            this.backgroundPaint = null;
            this.content = null;
            this.toolTipText = null;
            this.urlText = null;
        }
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        ParamChecks.nullNotPermitted(text, "text");
        if (!this.text.equals(text)) {
            this.text = text;
            notifyListeners(new TitleChangeEvent(this));
        }
    }

    public HorizontalAlignment getTextAlignment() {
        return this.textAlignment;
    }

    public void setTextAlignment(HorizontalAlignment alignment) {
        ParamChecks.nullNotPermitted(alignment, "alignment");
        this.textAlignment = alignment;
        notifyListeners(new TitleChangeEvent(this));
    }

    public Font getFont() {
        return this.font;
    }

    public void setFont(Font font) {
        ParamChecks.nullNotPermitted(font, "font");
        if (!this.font.equals(font)) {
            this.font = font;
            notifyListeners(new TitleChangeEvent(this));
        }
    }

    public Paint getPaint() {
        return this.paint;
    }

    public void setPaint(Paint paint) {
        ParamChecks.nullNotPermitted(paint, "paint");
        if (!this.paint.equals(paint)) {
            this.paint = paint;
            notifyListeners(new TitleChangeEvent(this));
        }
    }

    public Paint getBackgroundPaint() {
        return this.backgroundPaint;
    }

    public void setBackgroundPaint(Paint paint) {
        this.backgroundPaint = paint;
        notifyListeners(new TitleChangeEvent(this));
    }

    public String getToolTipText() {
        return this.toolTipText;
    }

    public void setToolTipText(String text) {
        this.toolTipText = text;
        notifyListeners(new TitleChangeEvent(this));
    }

    public String getURLText() {
        return this.urlText;
    }

    public void setURLText(String text) {
        this.urlText = text;
        notifyListeners(new TitleChangeEvent(this));
    }

    public boolean getExpandToFitSpace() {
        return this.expandToFitSpace;
    }

    public void setExpandToFitSpace(boolean expand) {
        this.expandToFitSpace = expand;
        notifyListeners(new TitleChangeEvent(this));
    }

    public int getMaximumLinesToDisplay() {
        return this.maximumLinesToDisplay;
    }

    public void setMaximumLinesToDisplay(int max) {
        this.maximumLinesToDisplay = max;
        notifyListeners(new TitleChangeEvent(this));
    }

    public Size2D arrange(Graphics2D g2, RectangleConstraint constraint) {
        RectangleConstraint cc = toContentConstraint(constraint);
        LengthConstraintType w = cc.getWidthConstraintType();
        LengthConstraintType h = cc.getHeightConstraintType();
        Size2D contentSize = null;
        if (w == LengthConstraintType.NONE) {
            if (h == LengthConstraintType.NONE) {
                contentSize = arrangeNN(g2);
            } else if (h == LengthConstraintType.RANGE) {
                throw new RuntimeException("Not yet implemented.");
            } else if (h == LengthConstraintType.FIXED) {
                throw new RuntimeException("Not yet implemented.");
            }
        } else if (w == LengthConstraintType.RANGE) {
            if (h == LengthConstraintType.NONE) {
                contentSize = arrangeRN(g2, cc.getWidthRange());
            } else if (h == LengthConstraintType.RANGE) {
                contentSize = arrangeRR(g2, cc.getWidthRange(), cc.getHeightRange());
            } else if (h == LengthConstraintType.FIXED) {
                throw new RuntimeException("Not yet implemented.");
            }
        } else if (w == LengthConstraintType.FIXED) {
            if (h == LengthConstraintType.NONE) {
                contentSize = arrangeFN(g2, cc.getWidth());
            } else if (h == LengthConstraintType.RANGE) {
                throw new RuntimeException("Not yet implemented.");
            } else if (h == LengthConstraintType.FIXED) {
                throw new RuntimeException("Not yet implemented.");
            }
        }
        if ($assertionsDisabled || contentSize != null) {
            return new Size2D(calculateTotalWidth(contentSize.getWidth()), calculateTotalHeight(contentSize.getHeight()));
        }
        throw new AssertionError();
    }

    protected Size2D arrangeNN(Graphics2D g2) {
        Range max = new Range(0.0d, 3.4028234663852886E38d);
        return arrangeRR(g2, max, max);
    }

    protected Size2D arrangeFN(Graphics2D g2, double w) {
        RectangleEdge position = getPosition();
        Size2D contentSize;
        if (position == RectangleEdge.TOP || position == RectangleEdge.BOTTOM) {
            float maxWidth = (float) w;
            g2.setFont(this.font);
            this.content = TextUtilities.createTextBlock(this.text, this.font, this.paint, maxWidth, this.maximumLinesToDisplay, new G2TextMeasurer(g2));
            this.content.setLineAlignment(this.textAlignment);
            contentSize = this.content.calculateDimensions(g2);
            if (this.expandToFitSpace) {
                return new Size2D((double) maxWidth, contentSize.getHeight());
            }
            return contentSize;
        } else if (position == RectangleEdge.LEFT || position == RectangleEdge.RIGHT) {
            g2.setFont(this.font);
            this.content = TextUtilities.createTextBlock(this.text, this.font, this.paint, Float.MAX_VALUE, this.maximumLinesToDisplay, new G2TextMeasurer(g2));
            this.content.setLineAlignment(this.textAlignment);
            contentSize = this.content.calculateDimensions(g2);
            if (this.expandToFitSpace) {
                return new Size2D(contentSize.getHeight(), (double) 2139095039);
            }
            return new Size2D(contentSize.height, contentSize.width);
        } else {
            throw new RuntimeException("Unrecognised exception.");
        }
    }

    protected Size2D arrangeRN(Graphics2D g2, Range widthRange) {
        Size2D s = arrangeNN(g2);
        return widthRange.contains(s.getWidth()) ? s : arrangeFN(g2, widthRange.constrain(s.getWidth()));
    }

    protected Size2D arrangeRR(Graphics2D g2, Range widthRange, Range heightRange) {
        RectangleEdge position = getPosition();
        float maxWidth;
        Size2D contentSize;
        if (position == RectangleEdge.TOP || position == RectangleEdge.BOTTOM) {
            maxWidth = (float) widthRange.getUpperBound();
            g2.setFont(this.font);
            this.content = TextUtilities.createTextBlock(this.text, this.font, this.paint, maxWidth, this.maximumLinesToDisplay, new G2TextMeasurer(g2));
            this.content.setLineAlignment(this.textAlignment);
            contentSize = this.content.calculateDimensions(g2);
            if (this.expandToFitSpace) {
                return new Size2D((double) maxWidth, contentSize.getHeight());
            }
            return contentSize;
        } else if (position == RectangleEdge.LEFT || position == RectangleEdge.RIGHT) {
            maxWidth = (float) heightRange.getUpperBound();
            g2.setFont(this.font);
            this.content = TextUtilities.createTextBlock(this.text, this.font, this.paint, maxWidth, this.maximumLinesToDisplay, new G2TextMeasurer(g2));
            this.content.setLineAlignment(this.textAlignment);
            contentSize = this.content.calculateDimensions(g2);
            if (this.expandToFitSpace) {
                return new Size2D(contentSize.getHeight(), (double) maxWidth);
            }
            return new Size2D(contentSize.height, contentSize.width);
        } else {
            throw new RuntimeException("Unrecognised exception.");
        }
    }

    public void draw(Graphics2D g2, Rectangle2D area) {
        draw(g2, area, null);
    }

    public Object draw(Graphics2D g2, Rectangle2D area, Object params) {
        Object obj = null;
        if (this.content != null) {
            area = trimMargin(area);
            drawBorder(g2, area);
            if (!this.text.equals("")) {
                ChartEntity entity = null;
                if ((params instanceof EntityBlockParams) && ((EntityBlockParams) params).getGenerateEntities()) {
                    entity = new TitleEntity(area, this, this.toolTipText, this.urlText);
                }
                area = trimBorder(area);
                if (this.backgroundPaint != null) {
                    g2.setPaint(this.backgroundPaint);
                    g2.fill(area);
                }
                area = trimPadding(area);
                RectangleEdge position = getPosition();
                if (position == RectangleEdge.TOP || position == RectangleEdge.BOTTOM) {
                    drawHorizontal(g2, area);
                } else if (position == RectangleEdge.LEFT || position == RectangleEdge.RIGHT) {
                    drawVertical(g2, area);
                }
                obj = new BlockResult();
                if (entity != null) {
                    StandardEntityCollection sec = new StandardEntityCollection();
                    sec.add(entity);
                    obj.setEntityCollection(sec);
                }
            }
        }
        return obj;
    }

    protected void drawHorizontal(Graphics2D g2, Rectangle2D area) {
        Rectangle2D titleArea = (Rectangle2D) area.clone();
        g2.setFont(this.font);
        g2.setPaint(this.paint);
        TextBlockAnchor anchor = null;
        float x = 0.0f;
        HorizontalAlignment horizontalAlignment = getHorizontalAlignment();
        if (horizontalAlignment == HorizontalAlignment.LEFT) {
            x = (float) titleArea.getX();
            anchor = TextBlockAnchor.TOP_LEFT;
        } else if (horizontalAlignment == HorizontalAlignment.RIGHT) {
            x = (float) titleArea.getMaxX();
            anchor = TextBlockAnchor.TOP_RIGHT;
        } else if (horizontalAlignment == HorizontalAlignment.CENTER) {
            x = (float) titleArea.getCenterX();
            anchor = TextBlockAnchor.TOP_CENTER;
        }
        float y = 0.0f;
        RectangleEdge position = getPosition();
        if (position == RectangleEdge.TOP) {
            y = (float) titleArea.getY();
        } else if (position == RectangleEdge.BOTTOM) {
            y = (float) titleArea.getMaxY();
            if (horizontalAlignment == HorizontalAlignment.LEFT) {
                anchor = TextBlockAnchor.BOTTOM_LEFT;
            } else if (horizontalAlignment == HorizontalAlignment.CENTER) {
                anchor = TextBlockAnchor.BOTTOM_CENTER;
            } else if (horizontalAlignment == HorizontalAlignment.RIGHT) {
                anchor = TextBlockAnchor.BOTTOM_RIGHT;
            }
        }
        this.content.draw(g2, x, y, anchor);
    }

    protected void drawVertical(Graphics2D g2, Rectangle2D area) {
        Rectangle2D titleArea = (Rectangle2D) area.clone();
        g2.setFont(this.font);
        g2.setPaint(this.paint);
        TextBlockAnchor anchor = null;
        float y = 0.0f;
        VerticalAlignment verticalAlignment = getVerticalAlignment();
        if (verticalAlignment == VerticalAlignment.TOP) {
            y = (float) titleArea.getY();
            anchor = TextBlockAnchor.TOP_RIGHT;
        } else if (verticalAlignment == VerticalAlignment.BOTTOM) {
            y = (float) titleArea.getMaxY();
            anchor = TextBlockAnchor.TOP_LEFT;
        } else if (verticalAlignment == VerticalAlignment.CENTER) {
            y = (float) titleArea.getCenterY();
            anchor = TextBlockAnchor.TOP_CENTER;
        }
        float x = 0.0f;
        RectangleEdge position = getPosition();
        if (position == RectangleEdge.LEFT) {
            x = (float) titleArea.getX();
        } else if (position == RectangleEdge.RIGHT) {
            x = (float) titleArea.getMaxX();
            if (verticalAlignment == VerticalAlignment.TOP) {
                anchor = TextBlockAnchor.BOTTOM_RIGHT;
            } else if (verticalAlignment == VerticalAlignment.CENTER) {
                anchor = TextBlockAnchor.BOTTOM_CENTER;
            } else if (verticalAlignment == VerticalAlignment.BOTTOM) {
                anchor = TextBlockAnchor.BOTTOM_LEFT;
            }
        }
        this.content.draw(g2, x, y, anchor, x, y, -1.5707963267948966d);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TextTitle)) {
            return $assertionsDisabled;
        }
        TextTitle that = (TextTitle) obj;
        if (ObjectUtilities.equal(this.text, that.text) && ObjectUtilities.equal(this.font, that.font) && PaintUtilities.equal(this.paint, that.paint) && this.textAlignment == that.textAlignment && PaintUtilities.equal(this.backgroundPaint, that.backgroundPaint) && this.maximumLinesToDisplay == that.maximumLinesToDisplay && this.expandToFitSpace == that.expandToFitSpace && ObjectUtilities.equal(this.toolTipText, that.toolTipText) && ObjectUtilities.equal(this.urlText, that.urlText)) {
            return super.equals(obj);
        }
        return $assertionsDisabled;
    }

    public int hashCode() {
        int hashCode;
        int i = 0;
        int hashCode2 = ((super.hashCode() * 29) + (this.text != null ? this.text.hashCode() : 0)) * 29;
        if (this.font != null) {
            hashCode = this.font.hashCode();
        } else {
            hashCode = 0;
        }
        hashCode2 = (hashCode2 + hashCode) * 29;
        if (this.paint != null) {
            hashCode = this.paint.hashCode();
        } else {
            hashCode = 0;
        }
        hashCode = (hashCode2 + hashCode) * 29;
        if (this.backgroundPaint != null) {
            i = this.backgroundPaint.hashCode();
        }
        return hashCode + i;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.paint, stream);
        SerialUtilities.writePaint(this.backgroundPaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.paint = SerialUtilities.readPaint(stream);
        this.backgroundPaint = SerialUtilities.readPaint(stream);
    }
}
