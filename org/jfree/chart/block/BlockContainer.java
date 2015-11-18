package org.jfree.chart.block;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.util.ParamChecks;
import org.jfree.ui.Size2D;
import org.jfree.util.PublicCloneable;

public class BlockContainer extends AbstractBlock implements Block, Cloneable, PublicCloneable, Serializable {
    private static final long serialVersionUID = 8199508075695195293L;
    private Arrangement arrangement;
    private List blocks;

    public BlockContainer() {
        this(new BorderArrangement());
    }

    public BlockContainer(Arrangement arrangement) {
        ParamChecks.nullNotPermitted(arrangement, "arrangement");
        this.arrangement = arrangement;
        this.blocks = new ArrayList();
    }

    public Arrangement getArrangement() {
        return this.arrangement;
    }

    public void setArrangement(Arrangement arrangement) {
        ParamChecks.nullNotPermitted(arrangement, "arrangement");
        this.arrangement = arrangement;
    }

    public boolean isEmpty() {
        return this.blocks.isEmpty();
    }

    public List getBlocks() {
        return Collections.unmodifiableList(this.blocks);
    }

    public void add(Block block) {
        add(block, null);
    }

    public void add(Block block, Object key) {
        this.blocks.add(block);
        this.arrangement.add(block, key);
    }

    public void clear() {
        this.blocks.clear();
        this.arrangement.clear();
    }

    public Size2D arrange(Graphics2D g2, RectangleConstraint constraint) {
        return this.arrangement.arrange(this, g2, constraint);
    }

    public void draw(Graphics2D g2, Rectangle2D area) {
        draw(g2, area, null);
    }

    public Object draw(Graphics2D g2, Rectangle2D area, Object params) {
        StandardEntityCollection sec = null;
        if ((params instanceof EntityBlockParams) && ((EntityBlockParams) params).getGenerateEntities()) {
            sec = new StandardEntityCollection();
        }
        Rectangle2D contentArea = trimMargin((Rectangle2D) area.clone());
        drawBorder(g2, contentArea);
        contentArea = trimPadding(trimBorder(contentArea));
        for (Block block : this.blocks) {
            Rectangle2D bounds = block.getBounds();
            EntityBlockResult r = block.draw(g2, new Double(bounds.getX() + area.getX(), bounds.getY() + area.getY(), bounds.getWidth(), bounds.getHeight()), params);
            if (sec != null && (r instanceof EntityBlockResult)) {
                sec.addAll(r.getEntityCollection());
            }
        }
        if (sec == null) {
            return null;
        }
        BlockResult result = new BlockResult();
        result.setEntityCollection(sec);
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof BlockContainer)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        BlockContainer that = (BlockContainer) obj;
        if (!this.arrangement.equals(that.arrangement)) {
            return false;
        }
        if (this.blocks.equals(that.blocks)) {
            return true;
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        return (BlockContainer) super.clone();
    }
}
