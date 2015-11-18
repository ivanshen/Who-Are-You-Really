package org.jfree.chart.renderer.category;

import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.DataUtilities;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.NormalizedMatrixSeries;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PublicCloneable;

public class StackedAreaRenderer extends AreaRenderer implements Cloneable, PublicCloneable, Serializable {
    private static final long serialVersionUID = -3595635038460823663L;
    private boolean renderAsPercentages;

    public StackedAreaRenderer() {
        this(false);
    }

    public StackedAreaRenderer(boolean renderAsPercentages) {
        this.renderAsPercentages = renderAsPercentages;
    }

    public boolean getRenderAsPercentages() {
        return this.renderAsPercentages;
    }

    public void setRenderAsPercentages(boolean asPercentages) {
        this.renderAsPercentages = asPercentages;
        fireChangeEvent();
    }

    public int getPassCount() {
        return 2;
    }

    public Range findRangeBounds(CategoryDataset dataset) {
        if (dataset == null) {
            return null;
        }
        if (this.renderAsPercentages) {
            return new Range(0.0d, NormalizedMatrixSeries.DEFAULT_SCALE_FACTOR);
        }
        return DatasetUtilities.findStackedRangeBounds(dataset);
    }

    public void drawItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset, int row, int column, int pass) {
        if (isSeriesVisible(row)) {
            float transY1;
            EntityCollection entities = state.getEntityCollection();
            double y1 = 0.0d;
            Number n = dataset.getValue(row, column);
            if (n != null) {
                y1 = n.doubleValue();
                if (this.renderAsPercentages) {
                    y1 /= DataUtilities.calculateColumnTotal(dataset, column, state.getVisibleSeriesArray());
                }
            }
            double[] stack1 = getStackValues(dataset, row, column, state.getVisibleSeriesArray());
            double xx1 = domainAxis.getCategoryMiddle(column, getColumnCount(), dataArea, plot.getDomainAxisEdge());
            double y0 = 0.0d;
            n = dataset.getValue(row, Math.max(column - 1, 0));
            if (n != null) {
                y0 = n.doubleValue();
                if (this.renderAsPercentages) {
                    y0 /= DataUtilities.calculateColumnTotal(dataset, Math.max(column - 1, 0), state.getVisibleSeriesArray());
                }
            }
            double[] stack0 = getStackValues(dataset, row, Math.max(column - 1, 0), state.getVisibleSeriesArray());
            double xx0 = domainAxis.getCategoryStart(column, getColumnCount(), dataArea, plot.getDomainAxisEdge());
            int itemCount = dataset.getColumnCount();
            double y2 = 0.0d;
            n = dataset.getValue(row, Math.min(column + 1, itemCount - 1));
            if (n != null) {
                y2 = n.doubleValue();
                if (this.renderAsPercentages) {
                    y2 /= DataUtilities.calculateColumnTotal(dataset, Math.min(column + 1, itemCount - 1), state.getVisibleSeriesArray());
                }
            }
            double[] stack2 = getStackValues(dataset, row, Math.min(column + 1, itemCount - 1), state.getVisibleSeriesArray());
            double xxLeft = xx0;
            double xxRight = domainAxis.getCategoryEnd(column, getColumnCount(), dataArea, plot.getDomainAxisEdge());
            double[] stackLeft = averageStackValues(stack0, stack1);
            double[] stackRight = averageStackValues(stack1, stack2);
            double[] adjStackLeft = adjustedStackValues(stack0, stack1);
            double[] adjStackRight = adjustedStackValues(stack1, stack2);
            RectangleEdge edge1 = plot.getRangeAxisEdge();
            GeneralPath left = new GeneralPath();
            GeneralPath right = new GeneralPath();
            float transStack1;
            float transStackLeft;
            float transYLeft;
            float transStackRight;
            float transYRight;
            if (y1 >= 0.0d) {
                transY1 = (float) rangeAxis.valueToJava2D(stack1[1] + y1, dataArea, edge1);
                transStack1 = (float) rangeAxis.valueToJava2D(stack1[1], dataArea, edge1);
                transStackLeft = (float) rangeAxis.valueToJava2D(adjStackLeft[1], dataArea, edge1);
                if (y0 >= 0.0d) {
                    transYLeft = (float) rangeAxis.valueToJava2D(((y0 + y1) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) + stackLeft[1], dataArea, edge1);
                    left.moveTo((float) xx1, transY1);
                    left.lineTo((float) xx1, transStack1);
                    left.lineTo((float) xxLeft, transStackLeft);
                    left.lineTo((float) xxLeft, transYLeft);
                    left.closePath();
                } else {
                    left.moveTo((float) xx1, transStack1);
                    left.lineTo((float) xx1, transY1);
                    left.lineTo((float) xxLeft, transStackLeft);
                    left.closePath();
                }
                transStackRight = (float) rangeAxis.valueToJava2D(adjStackRight[1], dataArea, edge1);
                if (y2 >= 0.0d) {
                    transYRight = (float) rangeAxis.valueToJava2D(((y1 + y2) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) + stackRight[1], dataArea, edge1);
                    right.moveTo((float) xx1, transStack1);
                    right.lineTo((float) xx1, transY1);
                    right.lineTo((float) xxRight, transYRight);
                    right.lineTo((float) xxRight, transStackRight);
                    right.closePath();
                } else {
                    right.moveTo((float) xx1, transStack1);
                    right.lineTo((float) xx1, transY1);
                    right.lineTo((float) xxRight, transStackRight);
                    right.closePath();
                }
            } else {
                transY1 = (float) rangeAxis.valueToJava2D(stack1[0] + y1, dataArea, edge1);
                transStack1 = (float) rangeAxis.valueToJava2D(stack1[0], dataArea, edge1);
                transStackLeft = (float) rangeAxis.valueToJava2D(adjStackLeft[0], dataArea, edge1);
                if (y0 >= 0.0d) {
                    left.moveTo((float) xx1, transStack1);
                    left.lineTo((float) xx1, transY1);
                    left.lineTo((float) xxLeft, transStackLeft);
                    left.clone();
                } else {
                    transYLeft = (float) rangeAxis.valueToJava2D(((y0 + y1) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) + stackLeft[0], dataArea, edge1);
                    left.moveTo((float) xx1, transY1);
                    left.lineTo((float) xx1, transStack1);
                    left.lineTo((float) xxLeft, transStackLeft);
                    left.lineTo((float) xxLeft, transYLeft);
                    left.closePath();
                }
                transStackRight = (float) rangeAxis.valueToJava2D(adjStackRight[0], dataArea, edge1);
                if (y2 >= 0.0d) {
                    right.moveTo((float) xx1, transStack1);
                    right.lineTo((float) xx1, transY1);
                    right.lineTo((float) xxRight, transStackRight);
                    right.closePath();
                } else {
                    transYRight = (float) rangeAxis.valueToJava2D(((y1 + y2) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) + stackRight[0], dataArea, edge1);
                    right.moveTo((float) xx1, transStack1);
                    right.lineTo((float) xx1, transY1);
                    right.lineTo((float) xxRight, transYRight);
                    right.lineTo((float) xxRight, transStackRight);
                    right.closePath();
                }
            }
            if (pass == 0) {
                g2.setPaint(getItemPaint(row, column));
                g2.fill(left);
                g2.fill(right);
                if (entities != null) {
                    GeneralPath generalPath = new GeneralPath(left);
                    generalPath.append(right, false);
                    addItemEntity(entities, dataset, row, column, generalPath);
                }
            } else if (pass == 1) {
                drawItemLabel(g2, plot.getOrientation(), dataset, row, column, xx1, (double) transY1, y1 < 0.0d);
            }
        }
    }

    protected double[] getStackValues(CategoryDataset dataset, int series, int index, int[] validRows) {
        double[] result = new double[2];
        double total = 0.0d;
        if (this.renderAsPercentages) {
            total = DataUtilities.calculateColumnTotal(dataset, index, validRows);
        }
        for (int i = 0; i < series; i++) {
            if (isSeriesVisible(i)) {
                double v = 0.0d;
                Number n = dataset.getValue(i, index);
                if (n != null) {
                    v = n.doubleValue();
                    if (this.renderAsPercentages) {
                        v /= total;
                    }
                }
                if (!Double.isNaN(v)) {
                    if (v >= 0.0d) {
                        result[1] = result[1] + v;
                    } else {
                        result[0] = result[0] + v;
                    }
                }
            }
        }
        return result;
    }

    private double[] averageStackValues(double[] stack1, double[] stack2) {
        return new double[]{(stack1[0] + stack2[0]) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS, (stack1[1] + stack2[1]) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS};
    }

    private double[] adjustedStackValues(double[] stack1, double[] stack2) {
        double[] result = new double[2];
        if (stack1[0] == 0.0d || stack2[0] == 0.0d) {
            result[0] = 0.0d;
        } else {
            result[0] = (stack1[0] + stack2[0]) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
        }
        if (stack1[1] == 0.0d || stack2[1] == 0.0d) {
            result[1] = 0.0d;
        } else {
            result[1] = (stack1[1] + stack2[1]) / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StackedAreaRenderer)) {
            return false;
        }
        if (this.renderAsPercentages == ((StackedAreaRenderer) obj).renderAsPercentages) {
            return super.equals(obj);
        }
        return false;
    }

    protected double getPreviousHeight(CategoryDataset dataset, int series, int category) {
        double result = 0.0d;
        double total = 0.0d;
        if (this.renderAsPercentages) {
            total = DataUtilities.calculateColumnTotal(dataset, category);
        }
        for (int i = 0; i < series; i++) {
            Number n = dataset.getValue(i, category);
            if (n != null) {
                double v = n.doubleValue();
                if (this.renderAsPercentages) {
                    v /= total;
                }
                result += v;
            }
        }
        return result;
    }
}
