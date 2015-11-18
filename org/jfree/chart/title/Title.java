package org.jfree.chart.title;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.swing.event.EventListenerList;
import org.jfree.chart.block.AbstractBlock;
import org.jfree.chart.block.Block;
import org.jfree.chart.event.TitleChangeEvent;
import org.jfree.chart.event.TitleChangeListener;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.xy.NormalizedMatrixSeries;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.VerticalAlignment;
import org.jfree.util.ObjectUtilities;

public abstract class Title extends AbstractBlock implements Block, Cloneable, Serializable {
    public static final HorizontalAlignment DEFAULT_HORIZONTAL_ALIGNMENT;
    public static final RectangleInsets DEFAULT_PADDING;
    public static final RectangleEdge DEFAULT_POSITION;
    public static final VerticalAlignment DEFAULT_VERTICAL_ALIGNMENT;
    private static final long serialVersionUID = -6675162505277817221L;
    private HorizontalAlignment horizontalAlignment;
    private transient EventListenerList listenerList;
    private boolean notify;
    private RectangleEdge position;
    private VerticalAlignment verticalAlignment;
    public boolean visible;

    public abstract void draw(Graphics2D graphics2D, Rectangle2D rectangle2D);

    static {
        DEFAULT_POSITION = RectangleEdge.TOP;
        DEFAULT_HORIZONTAL_ALIGNMENT = HorizontalAlignment.CENTER;
        DEFAULT_VERTICAL_ALIGNMENT = VerticalAlignment.CENTER;
        DEFAULT_PADDING = new RectangleInsets(NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR, NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR, NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR, NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR);
    }

    protected Title() {
        this(DEFAULT_POSITION, DEFAULT_HORIZONTAL_ALIGNMENT, DEFAULT_VERTICAL_ALIGNMENT, DEFAULT_PADDING);
    }

    protected Title(RectangleEdge position, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment) {
        this(position, horizontalAlignment, verticalAlignment, DEFAULT_PADDING);
    }

    protected Title(RectangleEdge position, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment, RectangleInsets padding) {
        ParamChecks.nullNotPermitted(position, "position");
        ParamChecks.nullNotPermitted(horizontalAlignment, "horizontalAlignment");
        ParamChecks.nullNotPermitted(verticalAlignment, "verticalAlignment");
        ParamChecks.nullNotPermitted(padding, "padding");
        this.visible = true;
        this.position = position;
        this.horizontalAlignment = horizontalAlignment;
        this.verticalAlignment = verticalAlignment;
        setPadding(padding);
        this.listenerList = new EventListenerList();
        this.notify = true;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        notifyListeners(new TitleChangeEvent(this));
    }

    public RectangleEdge getPosition() {
        return this.position;
    }

    public void setPosition(RectangleEdge position) {
        ParamChecks.nullNotPermitted(position, "position");
        if (this.position != position) {
            this.position = position;
            notifyListeners(new TitleChangeEvent(this));
        }
    }

    public HorizontalAlignment getHorizontalAlignment() {
        return this.horizontalAlignment;
    }

    public void setHorizontalAlignment(HorizontalAlignment alignment) {
        ParamChecks.nullNotPermitted(alignment, "alignment");
        if (this.horizontalAlignment != alignment) {
            this.horizontalAlignment = alignment;
            notifyListeners(new TitleChangeEvent(this));
        }
    }

    public VerticalAlignment getVerticalAlignment() {
        return this.verticalAlignment;
    }

    public void setVerticalAlignment(VerticalAlignment alignment) {
        ParamChecks.nullNotPermitted(alignment, "alignment");
        if (this.verticalAlignment != alignment) {
            this.verticalAlignment = alignment;
            notifyListeners(new TitleChangeEvent(this));
        }
    }

    public boolean getNotify() {
        return this.notify;
    }

    public void setNotify(boolean flag) {
        this.notify = flag;
        if (flag) {
            notifyListeners(new TitleChangeEvent(this));
        }
    }

    public Object clone() throws CloneNotSupportedException {
        Title duplicate = (Title) super.clone();
        duplicate.listenerList = new EventListenerList();
        return duplicate;
    }

    public void addChangeListener(TitleChangeListener listener) {
        this.listenerList.add(TitleChangeListener.class, listener);
    }

    public void removeChangeListener(TitleChangeListener listener) {
        this.listenerList.remove(TitleChangeListener.class, listener);
    }

    protected void notifyListeners(TitleChangeEvent event) {
        if (this.notify) {
            Object[] listeners = this.listenerList.getListenerList();
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == TitleChangeListener.class) {
                    ((TitleChangeListener) listeners[i + 1]).titleChanged(event);
                }
            }
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Title)) {
            return false;
        }
        Title that = (Title) obj;
        if (this.visible == that.visible && this.position == that.position && this.horizontalAlignment == that.horizontalAlignment && this.verticalAlignment == that.verticalAlignment && this.notify == that.notify) {
            return super.equals(obj);
        }
        return false;
    }

    public int hashCode() {
        return ((((ObjectUtilities.hashCode(this.position) + 7141) * 37) + ObjectUtilities.hashCode(this.horizontalAlignment)) * 37) + ObjectUtilities.hashCode(this.verticalAlignment);
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.listenerList = new EventListenerList();
    }
}
