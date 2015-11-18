package org.jfree.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.io.Serializable;

public class FormatLayout implements LayoutManager, Serializable {
    public static final int C = 1;
    public static final int LC = 2;
    public static final int LCB = 3;
    public static final int LCBLC = 6;
    public static final int LCBLCB = 7;
    public static final int LCLC = 4;
    public static final int LCLCB = 5;
    private static final long serialVersionUID = 2866692886323930722L;
    private int[] columnGaps;
    private int[] columnWidths;
    private int columns0to5Width;
    private int columns1and2Width;
    private int columns1to4Width;
    private int columns1to5Width;
    private int columns4and5Width;
    private int[] rowFormats;
    private int rowGap;
    private int[] rowHeights;
    private int totalHeight;
    private int totalWidth;

    public FormatLayout(int rowCount, int[] rowFormats) {
        this.rowFormats = rowFormats;
        this.rowGap = LC;
        this.columnGaps = new int[LCLCB];
        this.columnGaps[0] = 10;
        this.columnGaps[C] = LCLCB;
        this.columnGaps[LC] = LCLCB;
        this.columnGaps[LCB] = 10;
        this.columnGaps[LCLC] = LCLCB;
        this.rowHeights = new int[rowCount];
        this.columnWidths = new int[LCBLC];
    }

    public Dimension preferredLayoutSize(Container parent) {
        Dimension dimension;
        synchronized (parent.getTreeLock()) {
            Insets insets = parent.getInsets();
            int componentIndex = 0;
            int rowCount = this.rowHeights.length;
            for (int i = 0; i < this.columnWidths.length; i += C) {
                this.columnWidths[i] = 0;
            }
            this.columns1and2Width = 0;
            this.columns4and5Width = 0;
            this.columns1to4Width = 0;
            this.columns1to5Width = 0;
            this.columns0to5Width = 0;
            this.totalHeight = 0;
            for (int rowIndex = 0; rowIndex < rowCount; rowIndex += C) {
                switch (this.rowFormats[rowIndex % this.rowFormats.length]) {
                    case C /*1*/:
                        updateC(rowIndex, parent.getComponent(componentIndex).getPreferredSize());
                        componentIndex += C;
                        break;
                    case LC /*2*/:
                        updateLC(rowIndex, parent.getComponent(componentIndex).getPreferredSize(), parent.getComponent(componentIndex + C).getPreferredSize());
                        componentIndex += LC;
                        break;
                    case LCB /*3*/:
                        updateLCB(rowIndex, parent.getComponent(componentIndex).getPreferredSize(), parent.getComponent(componentIndex + C).getPreferredSize(), parent.getComponent(componentIndex + LC).getPreferredSize());
                        componentIndex += LCB;
                        break;
                    case LCLC /*4*/:
                        updateLCLC(rowIndex, parent.getComponent(componentIndex).getPreferredSize(), parent.getComponent(componentIndex + C).getPreferredSize(), parent.getComponent(componentIndex + LC).getPreferredSize(), parent.getComponent(componentIndex + LCB).getPreferredSize());
                        componentIndex += LCLC;
                        break;
                    case LCLCB /*5*/:
                        updateLCLCB(rowIndex, parent.getComponent(componentIndex).getPreferredSize(), parent.getComponent(componentIndex + C).getPreferredSize(), parent.getComponent(componentIndex + LC).getPreferredSize(), parent.getComponent(componentIndex + LCB).getPreferredSize(), parent.getComponent(componentIndex + LCLC).getPreferredSize());
                        componentIndex += LCLCB;
                        break;
                    case LCBLC /*6*/:
                        updateLCBLC(rowIndex, parent.getComponent(componentIndex).getPreferredSize(), parent.getComponent(componentIndex + C).getPreferredSize(), parent.getComponent(componentIndex + LC).getPreferredSize(), parent.getComponent(componentIndex + LCB).getPreferredSize(), parent.getComponent(componentIndex + LCLC).getPreferredSize());
                        componentIndex += LCLCB;
                        break;
                    case LCBLCB /*7*/:
                        updateLCBLCB(rowIndex, parent.getComponent(componentIndex).getPreferredSize(), parent.getComponent(componentIndex + C).getPreferredSize(), parent.getComponent(componentIndex + LC).getPreferredSize(), parent.getComponent(componentIndex + LCB).getPreferredSize(), parent.getComponent(componentIndex + LCLC).getPreferredSize(), parent.getComponent(componentIndex + LCLCB).getPreferredSize());
                        componentIndex += LCBLC;
                        break;
                    default:
                        break;
                }
            }
            complete();
            dimension = new Dimension((this.totalWidth + insets.left) + insets.right, ((this.totalHeight + ((rowCount - 1) * this.rowGap)) + insets.top) + insets.bottom);
        }
        return dimension;
    }

    public Dimension minimumLayoutSize(Container parent) {
        Dimension dimension;
        synchronized (parent.getTreeLock()) {
            Insets insets = parent.getInsets();
            int componentIndex = 0;
            int rowCount = this.rowHeights.length;
            for (int i = 0; i < this.columnWidths.length; i += C) {
                this.columnWidths[i] = 0;
            }
            this.columns1and2Width = 0;
            this.columns4and5Width = 0;
            this.columns1to4Width = 0;
            this.columns1to5Width = 0;
            this.columns0to5Width = 0;
            for (int rowIndex = 0; rowIndex < rowCount; rowIndex += C) {
                switch (this.rowFormats[rowIndex % this.rowFormats.length]) {
                    case C /*1*/:
                        this.columns0to5Width = Math.max(this.columns0to5Width, parent.getComponent(componentIndex).getMinimumSize().width);
                        componentIndex += C;
                        break;
                    case LC /*2*/:
                        updateLC(rowIndex, parent.getComponent(componentIndex).getMinimumSize(), parent.getComponent(componentIndex + C).getMinimumSize());
                        componentIndex += LC;
                        break;
                    case LCB /*3*/:
                        updateLCB(rowIndex, parent.getComponent(componentIndex).getMinimumSize(), parent.getComponent(componentIndex + C).getMinimumSize(), parent.getComponent(componentIndex + LC).getMinimumSize());
                        componentIndex += LCB;
                        break;
                    case LCLC /*4*/:
                        updateLCLC(rowIndex, parent.getComponent(componentIndex).getMinimumSize(), parent.getComponent(componentIndex + C).getMinimumSize(), parent.getComponent(componentIndex + LC).getMinimumSize(), parent.getComponent(componentIndex + LCB).getMinimumSize());
                        componentIndex += LCB;
                        break;
                    case LCLCB /*5*/:
                        updateLCLCB(rowIndex, parent.getComponent(componentIndex).getMinimumSize(), parent.getComponent(componentIndex + C).getMinimumSize(), parent.getComponent(componentIndex + LC).getMinimumSize(), parent.getComponent(componentIndex + LCB).getMinimumSize(), parent.getComponent(componentIndex + LCLC).getMinimumSize());
                        componentIndex += LCLC;
                        break;
                    case LCBLC /*6*/:
                        updateLCBLC(rowIndex, parent.getComponent(componentIndex).getMinimumSize(), parent.getComponent(componentIndex + C).getMinimumSize(), parent.getComponent(componentIndex + LC).getMinimumSize(), parent.getComponent(componentIndex + LCB).getMinimumSize(), parent.getComponent(componentIndex + LCLC).getMinimumSize());
                        componentIndex += LCLC;
                        break;
                    case LCBLCB /*7*/:
                        updateLCBLCB(rowIndex, parent.getComponent(componentIndex).getMinimumSize(), parent.getComponent(componentIndex + C).getMinimumSize(), parent.getComponent(componentIndex + LC).getMinimumSize(), parent.getComponent(componentIndex + LCB).getMinimumSize(), parent.getComponent(componentIndex + LCLC).getMinimumSize(), parent.getComponent(componentIndex + LCLCB).getMinimumSize());
                        componentIndex += LCLCB;
                        break;
                    default:
                        break;
                }
            }
            complete();
            dimension = new Dimension((this.totalWidth + insets.left) + insets.right, ((((rowCount - 1) * this.rowGap) + 0) + insets.top) + insets.bottom);
        }
        return dimension;
    }

    public void layoutContainer(Container parent) {
        synchronized (parent.getTreeLock()) {
            int rowIndex;
            Insets insets = parent.getInsets();
            int componentIndex = 0;
            int rowCount = this.rowHeights.length;
            for (int i = 0; i < this.columnWidths.length; i += C) {
                this.columnWidths[i] = 0;
            }
            this.columns1and2Width = 0;
            this.columns4and5Width = 0;
            this.columns1to4Width = 0;
            this.columns1to5Width = 0;
            this.columns0to5Width = (parent.getBounds().width - insets.left) - insets.right;
            this.totalHeight = 0;
            for (rowIndex = 0; rowIndex < rowCount; rowIndex += C) {
                switch (this.rowFormats[rowIndex % this.rowFormats.length]) {
                    case C /*1*/:
                        updateC(rowIndex, parent.getComponent(componentIndex).getPreferredSize());
                        componentIndex += C;
                        break;
                    case LC /*2*/:
                        updateLC(rowIndex, parent.getComponent(componentIndex).getPreferredSize(), parent.getComponent(componentIndex + C).getPreferredSize());
                        componentIndex += LC;
                        break;
                    case LCB /*3*/:
                        updateLCB(rowIndex, parent.getComponent(componentIndex).getPreferredSize(), parent.getComponent(componentIndex + C).getPreferredSize(), parent.getComponent(componentIndex + LC).getPreferredSize());
                        componentIndex += LCB;
                        break;
                    case LCLC /*4*/:
                        updateLCLC(rowIndex, parent.getComponent(componentIndex).getPreferredSize(), parent.getComponent(componentIndex + C).getPreferredSize(), parent.getComponent(componentIndex + LC).getPreferredSize(), parent.getComponent(componentIndex + LCB).getPreferredSize());
                        componentIndex += LCLC;
                        break;
                    case LCLCB /*5*/:
                        updateLCLCB(rowIndex, parent.getComponent(componentIndex).getPreferredSize(), parent.getComponent(componentIndex + C).getPreferredSize(), parent.getComponent(componentIndex + LC).getPreferredSize(), parent.getComponent(componentIndex + LCB).getPreferredSize(), parent.getComponent(componentIndex + LCLC).getPreferredSize());
                        componentIndex += LCLCB;
                        break;
                    case LCBLC /*6*/:
                        updateLCBLC(rowIndex, parent.getComponent(componentIndex).getPreferredSize(), parent.getComponent(componentIndex + C).getPreferredSize(), parent.getComponent(componentIndex + LC).getPreferredSize(), parent.getComponent(componentIndex + LCB).getPreferredSize(), parent.getComponent(componentIndex + LCLC).getPreferredSize());
                        componentIndex += LCLCB;
                        break;
                    case LCBLCB /*7*/:
                        updateLCBLCB(rowIndex, parent.getComponent(componentIndex).getPreferredSize(), parent.getComponent(componentIndex + C).getPreferredSize(), parent.getComponent(componentIndex + LC).getPreferredSize(), parent.getComponent(componentIndex + LCB).getPreferredSize(), parent.getComponent(componentIndex + LCLC).getPreferredSize(), parent.getComponent(componentIndex + LCLCB).getPreferredSize());
                        componentIndex += LCBLC;
                        break;
                    default:
                        break;
                }
            }
            complete();
            componentIndex = 0;
            int rowY = insets.top;
            int[] rowX = new int[LCBLC];
            rowX[0] = insets.left;
            rowX[C] = (rowX[0] + this.columnWidths[0]) + this.columnGaps[0];
            rowX[LC] = (rowX[C] + this.columnWidths[C]) + this.columnGaps[C];
            rowX[LCB] = (rowX[LC] + this.columnWidths[LC]) + this.columnGaps[LC];
            rowX[LCLC] = (rowX[LCB] + this.columnWidths[LCB]) + this.columnGaps[LCB];
            rowX[LCLCB] = (rowX[LCLC] + this.columnWidths[LCLC]) + this.columnGaps[LCLC];
            int w1to2 = (this.columnWidths[C] + this.columnGaps[C]) + this.columnWidths[LC];
            int w4to5 = (this.columnWidths[LCLC] + this.columnGaps[LCLC]) + this.columnWidths[LCLCB];
            int w1to4 = (((this.columnGaps[LC] + w1to2) + this.columnWidths[LCB]) + this.columnGaps[LCB]) + this.columnWidths[LCLC];
            int w1to5 = (this.columnGaps[LCLC] + w1to4) + this.columnWidths[LCLCB];
            int w0to5 = (this.columnWidths[0] + w1to5) + this.columnGaps[0];
            for (rowIndex = 0; rowIndex < rowCount; rowIndex += C) {
                Component c0;
                Component c1;
                Component c2;
                Component c3;
                Component c4;
                switch (this.rowFormats[rowIndex % this.rowFormats.length]) {
                    case C /*1*/:
                        c0 = parent.getComponent(componentIndex);
                        c0.setBounds(rowX[0], rowY, w0to5, c0.getPreferredSize().height);
                        componentIndex += C;
                        break;
                    case LC /*2*/:
                        c0 = parent.getComponent(componentIndex);
                        c0.setBounds(rowX[0], ((this.rowHeights[rowIndex] - c0.getPreferredSize().height) / LC) + rowY, this.columnWidths[0], c0.getPreferredSize().height);
                        c1 = parent.getComponent(componentIndex + C);
                        c1.setBounds(rowX[C], ((this.rowHeights[rowIndex] - c1.getPreferredSize().height) / LC) + rowY, w1to5, c1.getPreferredSize().height);
                        componentIndex += LC;
                        break;
                    case LCB /*3*/:
                        c0 = parent.getComponent(componentIndex);
                        c0.setBounds(rowX[0], ((this.rowHeights[rowIndex] - c0.getPreferredSize().height) / LC) + rowY, this.columnWidths[0], c0.getPreferredSize().height);
                        c1 = parent.getComponent(componentIndex + C);
                        c1.setBounds(rowX[C], ((this.rowHeights[rowIndex] - c1.getPreferredSize().height) / LC) + rowY, w1to4, c1.getPreferredSize().height);
                        c2 = parent.getComponent(componentIndex + LC);
                        c2.setBounds(rowX[LCLCB], ((this.rowHeights[rowIndex] - c2.getPreferredSize().height) / LC) + rowY, this.columnWidths[LCLCB], c2.getPreferredSize().height);
                        componentIndex += LCB;
                        break;
                    case LCLC /*4*/:
                        c0 = parent.getComponent(componentIndex);
                        c0.setBounds(rowX[0], ((this.rowHeights[rowIndex] - c0.getPreferredSize().height) / LC) + rowY, this.columnWidths[0], c0.getPreferredSize().height);
                        c1 = parent.getComponent(componentIndex + C);
                        c1.setBounds(rowX[C], ((this.rowHeights[rowIndex] - c1.getPreferredSize().height) / LC) + rowY, w1to2, c1.getPreferredSize().height);
                        c2 = parent.getComponent(componentIndex + LC);
                        c2.setBounds(rowX[LCB], ((this.rowHeights[rowIndex] - c2.getPreferredSize().height) / LC) + rowY, this.columnWidths[LCB], c2.getPreferredSize().height);
                        c3 = parent.getComponent(componentIndex + LCB);
                        c3.setBounds(rowX[LCLC], ((this.rowHeights[rowIndex] - c3.getPreferredSize().height) / LC) + rowY, w4to5, c3.getPreferredSize().height);
                        componentIndex += LCLC;
                        break;
                    case LCLCB /*5*/:
                        c0 = parent.getComponent(componentIndex);
                        c0.setBounds(rowX[0], ((this.rowHeights[rowIndex] - c0.getPreferredSize().height) / LC) + rowY, this.columnWidths[0], c0.getPreferredSize().height);
                        c1 = parent.getComponent(componentIndex + C);
                        c1.setBounds(rowX[C], ((this.rowHeights[rowIndex] - c1.getPreferredSize().height) / LC) + rowY, w1to2, c1.getPreferredSize().height);
                        c2 = parent.getComponent(componentIndex + LC);
                        c2.setBounds(rowX[LCB], ((this.rowHeights[rowIndex] - c2.getPreferredSize().height) / LC) + rowY, this.columnWidths[LCB], c2.getPreferredSize().height);
                        c3 = parent.getComponent(componentIndex + LCB);
                        c3.setBounds(rowX[LCLC], ((this.rowHeights[rowIndex] - c3.getPreferredSize().height) / LC) + rowY, this.columnWidths[LCLC], c3.getPreferredSize().height);
                        c4 = parent.getComponent(componentIndex + LCLC);
                        c4.setBounds(rowX[LCLCB], ((this.rowHeights[rowIndex] - c4.getPreferredSize().height) / LC) + rowY, this.columnWidths[LCLCB], c4.getPreferredSize().height);
                        componentIndex += LCLCB;
                        break;
                    case LCBLC /*6*/:
                        c0 = parent.getComponent(componentIndex);
                        c0.setBounds(rowX[0], ((this.rowHeights[rowIndex] - c0.getPreferredSize().height) / LC) + rowY, this.columnWidths[0], c0.getPreferredSize().height);
                        c1 = parent.getComponent(componentIndex + C);
                        c1.setBounds(rowX[C], ((this.rowHeights[rowIndex] - c1.getPreferredSize().height) / LC) + rowY, this.columnWidths[C], c1.getPreferredSize().height);
                        c2 = parent.getComponent(componentIndex + LC);
                        c2.setBounds(rowX[LC], ((this.rowHeights[rowIndex] - c2.getPreferredSize().height) / LC) + rowY, this.columnWidths[LC], c2.getPreferredSize().height);
                        c3 = parent.getComponent(componentIndex + LCB);
                        c3.setBounds(rowX[LCB], ((this.rowHeights[rowIndex] - c3.getPreferredSize().height) / LC) + rowY, this.columnWidths[LCB], c3.getPreferredSize().height);
                        c4 = parent.getComponent(componentIndex + LCLC);
                        c4.setBounds(rowX[LCLC], ((this.rowHeights[rowIndex] - c4.getPreferredSize().height) / LC) + rowY, w4to5, c4.getPreferredSize().height);
                        componentIndex += LCLCB;
                        break;
                    case LCBLCB /*7*/:
                        c0 = parent.getComponent(componentIndex);
                        c0.setBounds(rowX[0], ((this.rowHeights[rowIndex] - c0.getPreferredSize().height) / LC) + rowY, this.columnWidths[0], c0.getPreferredSize().height);
                        c1 = parent.getComponent(componentIndex + C);
                        c1.setBounds(rowX[C], ((this.rowHeights[rowIndex] - c1.getPreferredSize().height) / LC) + rowY, this.columnWidths[C], c1.getPreferredSize().height);
                        c2 = parent.getComponent(componentIndex + LC);
                        c2.setBounds(rowX[LC], ((this.rowHeights[rowIndex] - c2.getPreferredSize().height) / LC) + rowY, this.columnWidths[LC], c2.getPreferredSize().height);
                        c3 = parent.getComponent(componentIndex + LCB);
                        c3.setBounds(rowX[LCB], ((this.rowHeights[rowIndex] - c3.getPreferredSize().height) / LC) + rowY, this.columnWidths[LCB], c3.getPreferredSize().height);
                        c4 = parent.getComponent(componentIndex + LCLC);
                        c4.setBounds(rowX[LCLC], ((this.rowHeights[rowIndex] - c4.getPreferredSize().height) / LC) + rowY, this.columnWidths[LCLC], c4.getPreferredSize().height);
                        Component c5 = parent.getComponent(componentIndex + LCLCB);
                        c5.setBounds(rowX[LCLCB], ((this.rowHeights[rowIndex] - c5.getPreferredSize().height) / LC) + rowY, this.columnWidths[LCLCB], c5.getPreferredSize().height);
                        componentIndex += LCBLC;
                        break;
                    default:
                        break;
                }
                rowY = (this.rowHeights[rowIndex] + rowY) + this.rowGap;
            }
        }
    }

    protected void updateC(int rowIndex, Dimension d0) {
        this.rowHeights[rowIndex] = d0.height;
        this.totalHeight += this.rowHeights[rowIndex];
        this.columns0to5Width = Math.max(this.columns0to5Width, d0.width);
    }

    protected void updateLC(int rowIndex, Dimension d0, Dimension d1) {
        this.rowHeights[rowIndex] = Math.max(d0.height, d1.height);
        this.totalHeight += this.rowHeights[rowIndex];
        this.columnWidths[0] = Math.max(this.columnWidths[0], d0.width);
        this.columns1to5Width = Math.max(this.columns1to5Width, d1.width);
    }

    protected void updateLCB(int rowIndex, Dimension d0, Dimension d1, Dimension d2) {
        this.rowHeights[rowIndex] = Math.max(d0.height, Math.max(d1.height, d2.height));
        this.totalHeight += this.rowHeights[rowIndex];
        this.columnWidths[0] = Math.max(this.columnWidths[0], d0.width);
        this.columns1to4Width = Math.max(this.columns1to4Width, d1.width);
        this.columnWidths[LCLCB] = Math.max(this.columnWidths[LCLCB], d2.width);
    }

    protected void updateLCLC(int rowIndex, Dimension d0, Dimension d1, Dimension d2, Dimension d3) {
        this.rowHeights[rowIndex] = Math.max(Math.max(d0.height, d1.height), Math.max(d2.height, d3.height));
        this.totalHeight += this.rowHeights[rowIndex];
        this.columnWidths[0] = Math.max(this.columnWidths[0], d0.width);
        this.columns1and2Width = Math.max(this.columns1and2Width, d1.width);
        this.columnWidths[LCB] = Math.max(this.columnWidths[LCB], d2.width);
        this.columns4and5Width = Math.max(this.columns4and5Width, d3.width);
    }

    protected void updateLCBLC(int rowIndex, Dimension d0, Dimension d1, Dimension d2, Dimension d3, Dimension d4) {
        this.rowHeights[rowIndex] = Math.max(d0.height, Math.max(Math.max(d1.height, d2.height), Math.max(d3.height, d4.height)));
        this.totalHeight += this.rowHeights[rowIndex];
        this.columnWidths[0] = Math.max(this.columnWidths[0], d0.width);
        this.columnWidths[C] = Math.max(this.columnWidths[C], d1.width);
        this.columnWidths[LC] = Math.max(this.columnWidths[LC], d2.width);
        this.columnWidths[LCB] = Math.max(this.columnWidths[LCB], d3.width);
        this.columns4and5Width = Math.max(this.columns4and5Width, d4.width);
    }

    protected void updateLCLCB(int rowIndex, Dimension d0, Dimension d1, Dimension d2, Dimension d3, Dimension d4) {
        this.rowHeights[rowIndex] = Math.max(d0.height, Math.max(Math.max(d1.height, d2.height), Math.max(d3.height, d4.height)));
        this.totalHeight += this.rowHeights[rowIndex];
        this.columnWidths[0] = Math.max(this.columnWidths[0], d0.width);
        this.columns1and2Width = Math.max(this.columns1and2Width, d1.width);
        this.columnWidths[LCB] = Math.max(this.columnWidths[LCB], d2.width);
        this.columnWidths[LCLC] = Math.max(this.columnWidths[LCLC], d3.width);
        this.columnWidths[LCLCB] = Math.max(this.columnWidths[LCLCB], d4.width);
    }

    protected void updateLCBLCB(int rowIndex, Dimension d0, Dimension d1, Dimension d2, Dimension d3, Dimension d4, Dimension d5) {
        this.rowHeights[rowIndex] = Math.max(Math.max(d0.height, d1.height), Math.max(Math.max(d2.height, d3.height), Math.max(d4.height, d5.height)));
        this.totalHeight += this.rowHeights[rowIndex];
        this.columnWidths[0] = Math.max(this.columnWidths[0], d0.width);
        this.columnWidths[C] = Math.max(this.columnWidths[C], d1.width);
        this.columnWidths[LC] = Math.max(this.columnWidths[LC], d2.width);
        this.columnWidths[LCB] = Math.max(this.columnWidths[LCB], d3.width);
        this.columnWidths[LCLC] = Math.max(this.columnWidths[LCLC], d4.width);
        this.columnWidths[LCLCB] = Math.max(this.columnWidths[LCLCB], d5.width);
    }

    public void complete() {
        this.columnWidths[C] = Math.max(this.columnWidths[C], (this.columns1and2Width - this.columnGaps[C]) - this.columnWidths[LC]);
        this.columnWidths[LCLC] = Math.max(this.columnWidths[LCLC], Math.max((this.columns4and5Width - this.columnGaps[LCLC]) - this.columnWidths[LCLCB], Math.max((((((this.columns1to4Width - this.columnGaps[C]) - this.columnGaps[LC]) - this.columnGaps[LCB]) - this.columnWidths[C]) - this.columnWidths[LC]) - this.columnWidths[LCB], ((((((this.columns1to5Width - this.columnGaps[C]) - this.columnGaps[LC]) - this.columnGaps[LCB]) - this.columnWidths[C]) - this.columnWidths[LC]) - this.columnWidths[LCB]) - this.columnGaps[LCLC])));
        int leftWidth = (((this.columnWidths[0] + this.columnGaps[0]) + this.columnWidths[C]) + this.columnGaps[C]) + this.columnWidths[LC];
        int rightWidth = (((this.columnWidths[LCB] + this.columnGaps[LCB]) + this.columnWidths[LCLC]) + this.columnGaps[LCLC]) + this.columnWidths[LCLCB];
        if (splitLayout()) {
            int mismatch;
            if (leftWidth > rightWidth) {
                mismatch = leftWidth - rightWidth;
                this.columnWidths[LCLC] = this.columnWidths[LCLC] + mismatch;
                rightWidth += mismatch;
            } else {
                mismatch = rightWidth - leftWidth;
                this.columnWidths[C] = this.columnWidths[C] + mismatch;
                leftWidth += mismatch;
            }
        }
        this.totalWidth = (this.columnGaps[LC] + leftWidth) + rightWidth;
        if (this.columns0to5Width > this.totalWidth) {
            int spaceToAdd = this.columns0to5Width - this.totalWidth;
            if (splitLayout()) {
                int halfSpaceToAdd = spaceToAdd / LC;
                this.columnWidths[C] = this.columnWidths[C] + halfSpaceToAdd;
                this.columnWidths[LCLC] = (this.columnWidths[LCLC] + spaceToAdd) - halfSpaceToAdd;
                this.totalWidth += spaceToAdd;
                return;
            }
            this.columnWidths[C] = this.columnWidths[C] + spaceToAdd;
            this.totalWidth += spaceToAdd;
        }
    }

    private boolean splitLayout() {
        for (int i = 0; i < this.rowFormats.length; i += C) {
            if (this.rowFormats[i] > LCB) {
                return true;
            }
        }
        return false;
    }

    public void addLayoutComponent(Component comp) {
    }

    public void removeLayoutComponent(Component comp) {
    }

    public void addLayoutComponent(String name, Component comp) {
    }

    public void removeLayoutComponent(String name, Component comp) {
    }
}
