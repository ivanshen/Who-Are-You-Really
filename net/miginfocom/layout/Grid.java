package net.miginfocom.layout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.WeakHashMap;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.Plot;
import org.jfree.util.LogTarget;

public final class Grid {
    private static final DimConstraint DOCK_DIM_CONSTRAINT;
    private static final ResizeConstraint GAP_RC_CONST;
    private static final ResizeConstraint GAP_RC_CONST_PUSH;
    private static final Float[] GROW_100;
    private static final int MAX_DOCK_GRID = 32767;
    private static final int MAX_GRID = 30000;
    private static WeakHashMap<Object, LinkedHashMap<Integer, Cell>> PARENT_GRIDPOS_MAP = null;
    private static WeakHashMap[] PARENT_ROWCOL_SIZES_MAP = null;
    public static final boolean TEST_GAPS = true;
    private final ArrayList<LayoutCallback> callbackList;
    private final AC colConstr;
    private FlowSizeSpec colFlowSpecs;
    private final ArrayList<LinkedDimGroup>[] colGroupLists;
    private final TreeSet<Integer> colIndexes;
    private final ContainerWrapper container;
    private ArrayList<int[]> debugRects;
    private final int dockOffX;
    private final int dockOffY;
    private final LinkedHashMap<Integer, Cell> grid;
    private int[] height;
    private final LC lc;
    private HashMap<String, Boolean> linkTargetIDs;
    private final Float[] pushXs;
    private final Float[] pushYs;
    private final AC rowConstr;
    private FlowSizeSpec rowFlowSpecs;
    private final ArrayList<LinkedDimGroup>[] rowGroupLists;
    private final TreeSet<Integer> rowIndexes;
    private int[] width;
    private HashMap<Integer, BoundSize> wrapGapMap;

    private static class Cell {
        private final ArrayList<CompWrap> compWraps;
        private final boolean flowx;
        private boolean hasTagged;
        private final int spanx;
        private final int spany;

        private Cell(int i, int i2, boolean z) {
            this(null, i, i2, z);
        }

        private Cell(CompWrap compWrap) {
            this(compWrap, 1, 1, (boolean) Grid.TEST_GAPS);
        }

        private Cell(CompWrap compWrap, int i, int i2, boolean z) {
            this.compWraps = new ArrayList(1);
            this.hasTagged = false;
            if (compWrap != null) {
                this.compWraps.add(compWrap);
            }
            this.spanx = i;
            this.spany = i2;
            this.flowx = z;
        }

        static /* synthetic */ boolean access$476(Cell cell, int i) {
            boolean z = (byte) (cell.hasTagged | i);
            cell.hasTagged = z;
            return z;
        }
    }

    private static final class CompWrap {
        private final CC cc;
        private final ComponentWrapper comp;
        private int forcedPushGaps;
        private int[][] gaps;
        private int h;
        private final int[] horSizes;
        private final UnitValue[] pos;
        private final int[] verSizes;
        private int w;
        private int x;
        private int y;

        private CompWrap(ComponentWrapper componentWrapper, CC cc, int i, UnitValue[] unitValueArr, BoundSize[] boundSizeArr) {
            int i2 = -1;
            this.horSizes = new int[3];
            this.verSizes = new int[3];
            this.x = -2147471302;
            this.y = -2147471302;
            this.w = -2147471302;
            this.h = -2147471302;
            this.forcedPushGaps = 0;
            this.comp = componentWrapper;
            this.cc = cc;
            this.pos = unitValueArr;
            if (i <= 0) {
                int i3;
                BoundSize size = (boundSizeArr == null || boundSizeArr[0] == null) ? cc.getHorizontal().getSize() : boundSizeArr[0];
                BoundSize size2 = (boundSizeArr == null || boundSizeArr[1] == null) ? cc.getVertical().getSize() : boundSizeArr[1];
                if (this.comp.getWidth() <= 0 || this.comp.getHeight() <= 0) {
                    i3 = -1;
                } else {
                    i2 = this.comp.getHeight();
                    i3 = this.comp.getWidth();
                }
                int i4 = 0;
                while (i4 <= 2) {
                    this.horSizes[i4] = getSize(size, i4, Grid.TEST_GAPS, i2);
                    this.verSizes[i4] = getSize(size2, i4, false, i3 > 0 ? i3 : this.horSizes[i4]);
                    i4++;
                }
                Grid.correctMinMax(this.horSizes);
                Grid.correctMinMax(this.verSizes);
            }
            if (i > 1) {
                this.gaps = new int[4][];
                for (int i5 = 0; i5 < this.gaps.length; i5++) {
                    this.gaps[i5] = new int[3];
                }
            }
        }

        static /* synthetic */ int access$1412(CompWrap compWrap, int i) {
            int i2 = compWrap.x + i;
            compWrap.x = i2;
            return i2;
        }

        static /* synthetic */ int access$1612(CompWrap compWrap, int i) {
            int i2 = compWrap.y + i;
            compWrap.y = i2;
            return i2;
        }

        static /* synthetic */ int access$2376(CompWrap compWrap, int i) {
            int i2 = compWrap.forcedPushGaps | i;
            compWrap.forcedPushGaps = i2;
            return i2;
        }

        private void calcGaps(ComponentWrapper componentWrapper, CC cc, ComponentWrapper componentWrapper2, CC cc2, String str, boolean z, boolean z2) {
            BoundSize gapAfter;
            BoundSize gapBefore;
            ContainerWrapper parent = this.comp.getParent();
            int width = parent.getWidth();
            int height = parent.getHeight();
            if (componentWrapper != null) {
                gapAfter = (z ? cc.getHorizontal() : cc.getVertical()).getGapAfter();
            } else {
                gapAfter = null;
            }
            if (componentWrapper2 != null) {
                gapBefore = (z ? cc2.getHorizontal() : cc2.getVertical()).getGapBefore();
            } else {
                gapBefore = null;
            }
            mergeGapSizes(this.cc.getVertical().getComponentGaps(parent, this.comp, gapAfter, z ? null : componentWrapper, str, height, 0, z2), false, Grid.TEST_GAPS);
            mergeGapSizes(this.cc.getHorizontal().getComponentGaps(parent, this.comp, gapAfter, z ? componentWrapper : null, str, width, 1, z2), Grid.TEST_GAPS, Grid.TEST_GAPS);
            mergeGapSizes(this.cc.getVertical().getComponentGaps(parent, this.comp, gapBefore, z ? null : componentWrapper2, str, height, 2, z2), false, false);
            mergeGapSizes(this.cc.getHorizontal().getComponentGaps(parent, this.comp, gapBefore, z ? componentWrapper2 : null, str, width, 3, z2), Grid.TEST_GAPS, false);
        }

        private int filter(int i, int i2) {
            return i2 == -2147471302 ? i != 2 ? 0 : 2097051 : Grid.constrainSize(i2);
        }

        private int getBaseline(int i) {
            return this.comp.getBaseline(getSize(i, Grid.TEST_GAPS), getSize(i, false));
        }

        private int getGapAfter(int i, boolean z) {
            int[] gaps = getGaps(z, false);
            return gaps != null ? filter(i, gaps[i]) : 0;
        }

        private int getGapBefore(int i, boolean z) {
            int[] gaps = getGaps(z, Grid.TEST_GAPS);
            return gaps != null ? filter(i, gaps[i]) : 0;
        }

        private int getGapIx(boolean z, boolean z2) {
            return z ? z2 ? 1 : 3 : z2 ? 0 : 2;
        }

        private int[] getGaps(boolean z, boolean z2) {
            return this.gaps[getGapIx(z, z2)];
        }

        private int getSize(int i, boolean z) {
            return filter(i, z ? this.horSizes[i] : this.verSizes[i]);
        }

        private int getSize(BoundSize boundSize, int i, boolean z, int i2) {
            if (boundSize == null || boundSize.getSize(i) == null) {
                switch (i) {
                    case LogTarget.ERROR /*0*/:
                        return z ? this.comp.getMinimumWidth(i2) : this.comp.getMinimumHeight(i2);
                    case LogTarget.WARN /*1*/:
                        return z ? this.comp.getPreferredWidth(i2) : this.comp.getPreferredHeight(i2);
                    default:
                        return z ? this.comp.getMaximumWidth(i2) : this.comp.getMaximumHeight(i2);
                }
            } else {
                ContainerWrapper parent = this.comp.getParent();
                return boundSize.getSize(i).getPixels(z ? (float) parent.getWidth() : (float) parent.getHeight(), parent, this.comp);
            }
        }

        private int getSizeInclGaps(int i, boolean z) {
            return filter(i, (getGapBefore(i, z) + getSize(i, z)) + getGapAfter(i, z));
        }

        private boolean isBaselineAlign(boolean z) {
            Float grow = this.cc.getVertical().getGrow();
            if (grow != null && grow.intValue() != 0) {
                return false;
            }
            UnitValue align = this.cc.getVertical().getAlign();
            if (align != null) {
                if (align != UnitValue.BASELINE_IDENTITY) {
                    return false;
                }
            } else if (!z) {
                return false;
            }
            return this.comp.hasBaseline() ? Grid.TEST_GAPS : false;
        }

        private boolean isPushGap(boolean z, boolean z2) {
            if (z) {
                if (((z2 ? 1 : 2) & this.forcedPushGaps) != 0) {
                    return Grid.TEST_GAPS;
                }
            }
            DimConstraint dimConstraint = this.cc.getDimConstraint(z);
            BoundSize gapBefore = z2 ? dimConstraint.getGapBefore() : dimConstraint.getGapAfter();
            return (gapBefore == null || !gapBefore.getGapPush()) ? false : Grid.TEST_GAPS;
        }

        private void mergeGapSizes(int[] iArr, boolean z, boolean z2) {
            if (this.gaps == null) {
                this.gaps = new int[][]{null, null, null, null};
            }
            if (iArr != null) {
                int gapIx = getGapIx(z, z2);
                int[] iArr2 = this.gaps[gapIx];
                if (iArr2 == null) {
                    iArr2 = new int[]{0, 0, 2097051};
                    this.gaps[gapIx] = iArr2;
                }
                iArr2[0] = Math.max(iArr[0], iArr2[0]);
                iArr2[1] = Math.max(iArr[1], iArr2[1]);
                iArr2[2] = Math.min(iArr[2], iArr2[2]);
            }
        }

        private void setDimBounds(int i, int i2, boolean z) {
            if (z) {
                this.x = i;
                this.w = i2;
                return;
            }
            this.y = i;
            this.h = i2;
        }

        private void setGaps(int[] iArr, int i) {
            if (this.gaps == null) {
                this.gaps = new int[][]{null, null, null, null};
            }
            this.gaps[i] = iArr;
        }

        private void setSizes(int[] iArr, boolean z) {
            if (iArr != null) {
                int[] iArr2 = z ? this.horSizes : this.verSizes;
                iArr2[0] = iArr[0];
                iArr2[1] = iArr[1];
                iArr2[2] = iArr[2];
            }
        }

        private boolean transferBounds(boolean z) {
            this.comp.setBounds(this.x, this.y, this.w, this.h);
            return (!z || this.w == this.horSizes[1] || this.cc.getVertical().getSize().getPreferred() != null || this.comp.getPreferredHeight(-1) == this.verSizes[1]) ? false : Grid.TEST_GAPS;
        }
    }

    private static final class FlowSizeSpec {
        private final ResizeConstraint[] resConstsInclGaps;
        private final int[][] sizes;

        private FlowSizeSpec(int[][] iArr, ResizeConstraint[] resizeConstraintArr) {
            this.sizes = iArr;
            this.resConstsInclGaps = resizeConstraintArr;
        }

        private int expandSizes(DimConstraint[] dimConstraintArr, Float[] fArr, int i, int i2, int i3, int i4, int i5) {
            ResizeConstraint[] resizeConstraintArr = new ResizeConstraint[i3];
            int[][] iArr = new int[i3][];
            int i6 = 0;
            while (i6 < i3) {
                int[] iArr2 = this.sizes[i6 + i2];
                iArr[i6] = new int[]{iArr2[i4], iArr2[1], iArr2[2]};
                if (i5 <= 1 && i6 % 2 == 0) {
                    BoundSize size = ((DimConstraint) LayoutUtil.getIndexSafe(dimConstraintArr, ((i6 + i2) - 1) >> 1)).getSize();
                    if (i4 == 0) {
                        if (!(size.getMin() == null || size.getMin().getUnit() == 13)) {
                            i6++;
                        }
                    }
                    if (!(i4 != 1 || size.getPreferred() == null || size.getPreferred().getUnit() == 14)) {
                        i6++;
                    }
                }
                resizeConstraintArr[i6] = (ResizeConstraint) LayoutUtil.getIndexSafe(this.resConstsInclGaps, i6 + i2);
                i6++;
            }
            Float[] access$5600 = (i5 == 1 || i5 == 3) ? Grid.extractSubArray(dimConstraintArr, fArr, i2, i3) : null;
            int[] calculateSerial = LayoutUtil.calculateSerial(iArr, resizeConstraintArr, access$5600, 1, i);
            i6 = 0;
            for (int i7 = 0; i7 < i3; i7++) {
                int i8 = calculateSerial[i7];
                this.sizes[i7 + i2][i4] = i8;
                i6 += i8;
            }
            return i6;
        }
    }

    private static class LinkedDimGroup {
        private static final int TYPE_BASELINE = 2;
        private static final int TYPE_PARALLEL = 1;
        private static final int TYPE_SERIAL = 0;
        private ArrayList<CompWrap> _compWraps;
        private final boolean fromEnd;
        private final boolean isHor;
        private int lSize;
        private int lStart;
        private final String linkCtx;
        private final int linkType;
        private int[] sizes;
        private final int span;

        private LinkedDimGroup(String str, int i, int i2, boolean z, boolean z2) {
            this._compWraps = new ArrayList(4);
            this.sizes = null;
            this.lStart = 0;
            this.lSize = 0;
            this.linkCtx = str;
            this.span = i;
            this.linkType = i2;
            this.isHor = z;
            this.fromEnd = z2;
        }

        private void addCompWrap(CompWrap compWrap) {
            this._compWraps.add(compWrap);
            this.sizes = null;
        }

        private int[] getMinPrefMax() {
            if (this.sizes == null && this._compWraps.size() > 0) {
                this.sizes = new int[3];
                for (int i = 0; i <= TYPE_PARALLEL; i += TYPE_PARALLEL) {
                    if (this.linkType == TYPE_PARALLEL) {
                        this.sizes[i] = Grid.getTotalSizeParallel(this._compWraps, i, this.isHor);
                    } else if (this.linkType == TYPE_BASELINE) {
                        int[] access$4700 = Grid.getBaselineAboveBelow(this._compWraps, i, false);
                        this.sizes[i] = access$4700[TYPE_PARALLEL] + access$4700[0];
                    } else {
                        this.sizes[i] = Grid.getTotalSizeSerial(this._compWraps, i, this.isHor);
                    }
                }
                this.sizes[TYPE_BASELINE] = 2097051;
            }
            return this.sizes;
        }

        private void layout(DimConstraint dimConstraint, int i, int i2, int i3) {
            this.lStart = i;
            this.lSize = i2;
            if (this._compWraps.size() != 0) {
                ContainerWrapper parent = ((CompWrap) this._compWraps.get(0)).comp.getParent();
                if (this.linkType == TYPE_PARALLEL) {
                    Grid.layoutParallel(parent, this._compWraps, dimConstraint, i, i2, this.isHor, this.fromEnd);
                } else if (this.linkType == TYPE_BASELINE) {
                    Grid.layoutBaseline(parent, this._compWraps, dimConstraint, i, i2, TYPE_PARALLEL, i3);
                } else {
                    Grid.layoutSerial(parent, this._compWraps, dimConstraint, i, i2, this.isHor, i3, this.fromEnd);
                }
            }
        }

        private void setCompWraps(ArrayList<CompWrap> arrayList) {
            if (this._compWraps != arrayList) {
                this._compWraps = arrayList;
                this.sizes = null;
            }
        }
    }

    static {
        GROW_100 = new Float[]{ResizeConstraint.WEIGHT_100};
        DOCK_DIM_CONSTRAINT = new DimConstraint();
        DOCK_DIM_CONSTRAINT.setGrowPriority(0);
        GAP_RC_CONST = new ResizeConstraint(ChartPanel.DEFAULT_MINIMUM_DRAW_HEIGHT, ResizeConstraint.WEIGHT_100, 50, null);
        GAP_RC_CONST_PUSH = new ResizeConstraint(ChartPanel.DEFAULT_MINIMUM_DRAW_HEIGHT, ResizeConstraint.WEIGHT_100, 50, ResizeConstraint.WEIGHT_100);
        PARENT_ROWCOL_SIZES_MAP = null;
        PARENT_GRIDPOS_MAP = null;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public Grid(net.miginfocom.layout.ContainerWrapper r33, net.miginfocom.layout.LC r34, net.miginfocom.layout.AC r35, net.miginfocom.layout.AC r36, java.util.Map<net.miginfocom.layout.ComponentWrapper, net.miginfocom.layout.CC> r37, java.util.ArrayList<net.miginfocom.layout.LayoutCallback> r38) {
        /*
        r32 = this;
        r32.<init>();
        r3 = new java.util.LinkedHashMap;
        r3.<init>();
        r0 = r32;
        r0.grid = r3;
        r3 = 0;
        r0 = r32;
        r0.wrapGapMap = r3;
        r3 = new java.util.TreeSet;
        r3.<init>();
        r0 = r32;
        r0.rowIndexes = r3;
        r3 = new java.util.TreeSet;
        r3.<init>();
        r0 = r32;
        r0.colIndexes = r3;
        r3 = 0;
        r0 = r32;
        r0.colFlowSpecs = r3;
        r3 = 0;
        r0 = r32;
        r0.rowFlowSpecs = r3;
        r3 = 0;
        r0 = r32;
        r0.width = r3;
        r3 = 0;
        r0 = r32;
        r0.height = r3;
        r3 = 0;
        r0 = r32;
        r0.debugRects = r3;
        r3 = 0;
        r0 = r32;
        r0.linkTargetIDs = r3;
        r0 = r34;
        r1 = r32;
        r1.lc = r0;
        r0 = r35;
        r1 = r32;
        r1.rowConstr = r0;
        r0 = r36;
        r1 = r32;
        r1.colConstr = r0;
        r0 = r33;
        r1 = r32;
        r1.container = r0;
        r0 = r38;
        r1 = r32;
        r1.callbackList = r0;
        r3 = r34.getWrapAfter();
        if (r3 == 0) goto L_0x00da;
    L_0x0065:
        r3 = r34.getWrapAfter();
        r13 = r3;
    L_0x006a:
        r27 = r33.getComponents();
        r9 = 0;
        r8 = 0;
        r7 = 0;
        r6 = 0;
        r3 = 2;
        r0 = new int[r3];
        r28 = r0;
        r29 = new java.util.ArrayList;
        r3 = 2;
        r0 = r29;
        r0.<init>(r3);
        r3 = r34.isFlowX();
        if (r3 == 0) goto L_0x00ec;
    L_0x0085:
        r3 = r35;
    L_0x0087:
        r30 = r3.getConstaints();
        r5 = 0;
        r4 = 0;
        r14 = 0;
        r3 = r33.getLayout();
        net.miginfocom.layout.LinkHandler.clearTemporaryBounds(r3);
        r3 = 0;
        r11 = r3;
        r12 = r4;
        r15 = r5;
        r16 = r6;
        r17 = r7;
        r18 = r8;
        r19 = r9;
    L_0x00a1:
        r0 = r27;
        r3 = r0.length;
        if (r11 >= r3) goto L_0x04a2;
    L_0x00a6:
        r4 = r27[r11];
        r0 = r37;
        r5 = getCC(r4, r0);
        r0 = r32;
        r0.addLinkIDs(r5);
        r3 = r4.isVisible();
        if (r3 == 0) goto L_0x00ef;
    L_0x00b9:
        r6 = -1;
    L_0x00ba:
        r3 = 3;
        if (r6 != r3) goto L_0x0100;
    L_0x00bd:
        r6 = r4.getX();
        r7 = r4.getY();
        r8 = r4.getWidth();
        r9 = r4.getHeight();
        r10 = r5.isExternal();
        r3 = r32;
        r3.setLinkedBounds(r4, r5, r6, r7, r8, r9, r10);
        r3 = r11 + 1;
        r11 = r3;
        goto L_0x00a1;
    L_0x00da:
        r3 = r34.isFlowX();
        if (r3 == 0) goto L_0x00e9;
    L_0x00e0:
        r3 = r36;
    L_0x00e2:
        r3 = r3.getConstaints();
        r3 = r3.length;
        r13 = r3;
        goto L_0x006a;
    L_0x00e9:
        r3 = r35;
        goto L_0x00e2;
    L_0x00ec:
        r3 = r36;
        goto L_0x0087;
    L_0x00ef:
        r3 = r5.getHideMode();
        r6 = -1;
        if (r3 == r6) goto L_0x00fb;
    L_0x00f6:
        r6 = r5.getHideMode();
        goto L_0x00ba;
    L_0x00fb:
        r6 = r34.getHideMode();
        goto L_0x00ba;
    L_0x0100:
        r3 = r5.getHorizontal();
        r3 = r3.getSizeGroup();
        if (r3 == 0) goto L_0x010c;
    L_0x010a:
        r15 = r15 + 1;
    L_0x010c:
        r3 = r5.getVertical();
        r3 = r3.getSizeGroup();
        if (r3 == 0) goto L_0x0118;
    L_0x0116:
        r12 = r12 + 1;
    L_0x0118:
        r0 = r32;
        r7 = r0.getPos(r4, r5);
        r0 = r32;
        r8 = r0.getCallbackSize(r4);
        if (r7 != 0) goto L_0x012c;
    L_0x0126:
        r3 = r5.isExternal();
        if (r3 == 0) goto L_0x0186;
    L_0x012c:
        r3 = new net.miginfocom.layout.Grid$CompWrap;
        r9 = 0;
        r3.<init>(r5, r6, r7, r8, r9);
        r0 = r32;
        r9 = r0.grid;
        r10 = 0;
        r9 = r9.get(r10);
        r9 = (net.miginfocom.layout.Grid.Cell) r9;
        if (r9 != 0) goto L_0x017e;
    L_0x013f:
        r0 = r32;
        r9 = r0.grid;
        r10 = 0;
        r20 = new net.miginfocom.layout.Grid$Cell;
        r21 = 0;
        r0 = r20;
        r1 = r21;
        r0.<init>(r1);
        r0 = r20;
        r9.put(r10, r0);
    L_0x0154:
        r3 = r5.isBoundsInGrid();
        if (r3 == 0) goto L_0x0160;
    L_0x015a:
        r3 = r5.isExternal();
        if (r3 == 0) goto L_0x0186;
    L_0x0160:
        r6 = r4.getX();
        r7 = r4.getY();
        r8 = r4.getWidth();
        r9 = r4.getHeight();
        r10 = r5.isExternal();
        r3 = r32;
        r3.setLinkedBounds(r4, r5, r6, r7, r8, r9, r10);
        r3 = r11 + 1;
        r11 = r3;
        goto L_0x00a1;
    L_0x017e:
        r9 = r9.compWraps;
        r9.add(r3);
        goto L_0x0154;
    L_0x0186:
        r3 = r5.getDockSide();
        r9 = -1;
        if (r3 == r9) goto L_0x01ab;
    L_0x018d:
        if (r14 != 0) goto L_0x07a0;
    L_0x018f:
        r3 = 4;
        r3 = new int[r3];
        r3 = {-32767, -32767, 32767, 32767};
        r10 = r3;
    L_0x0196:
        r14 = r5.getDockSide();
        r3 = new net.miginfocom.layout.Grid$CompWrap;
        r9 = 0;
        r3.<init>(r5, r6, r7, r8, r9);
        r0 = r32;
        r0.addDockingCell(r10, r14, r3);
        r3 = r11 + 1;
        r11 = r3;
        r14 = r10;
        goto L_0x00a1;
    L_0x01ab:
        r6 = r5.getFlowX();
        r4 = 0;
        r3 = r5.isNewline();
        if (r3 == 0) goto L_0x021f;
    L_0x01b6:
        r3 = r5.getNewlineGapSize();
        r0 = r32;
        r1 = r28;
        r0.wrap(r1, r3);
    L_0x01c1:
        r20 = 0;
        r3 = r34.isNoGrid();
        if (r3 != 0) goto L_0x01e0;
    L_0x01c9:
        r3 = r34.isFlowX();
        if (r3 == 0) goto L_0x022a;
    L_0x01cf:
        r3 = 1;
        r3 = r28[r3];
    L_0x01d2:
        r0 = r30;
        r3 = net.miginfocom.layout.LayoutUtil.getIndexSafe(r0, r3);
        r3 = (net.miginfocom.layout.DimConstraint) r3;
        r3 = r3.isNoGrid();
        if (r3 == 0) goto L_0x022e;
    L_0x01e0:
        r3 = 1;
        r16 = r3;
    L_0x01e3:
        r3 = r5.getCellX();
        r9 = r5.getCellY();
        if (r3 < 0) goto L_0x01ef;
    L_0x01ed:
        if (r9 >= 0) goto L_0x0232;
    L_0x01ef:
        if (r16 != 0) goto L_0x0232;
    L_0x01f1:
        r10 = r5.getSkip();
        if (r10 != 0) goto L_0x0232;
    L_0x01f7:
        r3 = 1;
        r3 = r28[r3];
        r9 = 0;
        r9 = r28[r9];
        r0 = r32;
        r1 = r29;
        r3 = r0.isCellFree(r3, r9, r1);
        if (r3 != 0) goto L_0x079d;
    L_0x0207:
        r3 = 1;
        r0 = r32;
        r1 = r28;
        r3 = r0.increase(r1, r3);
        r3 = java.lang.Math.abs(r3);
        if (r3 < r13) goto L_0x01f7;
    L_0x0216:
        r3 = 0;
        r0 = r32;
        r1 = r28;
        r0.wrap(r1, r3);
        goto L_0x01f7;
    L_0x021f:
        if (r16 == 0) goto L_0x01c1;
    L_0x0221:
        r3 = 0;
        r0 = r32;
        r1 = r28;
        r0.wrap(r1, r3);
        goto L_0x01c1;
    L_0x022a:
        r3 = 0;
        r3 = r28[r3];
        goto L_0x01d2;
    L_0x022e:
        r3 = 0;
        r16 = r3;
        goto L_0x01e3;
    L_0x0232:
        if (r3 < 0) goto L_0x023e;
    L_0x0234:
        if (r9 < 0) goto L_0x023e;
    L_0x0236:
        if (r9 < 0) goto L_0x027e;
    L_0x0238:
        r4 = 0;
        r28[r4] = r3;
        r3 = 1;
        r28[r3] = r9;
    L_0x023e:
        r3 = 1;
        r3 = r28[r3];
        r4 = 0;
        r4 = r28[r4];
        r0 = r32;
        r3 = r0.getCell(r3, r4);
    L_0x024a:
        r4 = 0;
        r9 = r5.getSkip();
    L_0x024f:
        if (r4 >= r9) goto L_0x028c;
    L_0x0251:
        r10 = 1;
        r0 = r32;
        r1 = r28;
        r10 = r0.increase(r1, r10);
        r10 = java.lang.Math.abs(r10);
        if (r10 < r13) goto L_0x0268;
    L_0x0260:
        r10 = 0;
        r0 = r32;
        r1 = r28;
        r0.wrap(r1, r10);
    L_0x0268:
        r10 = 1;
        r10 = r28[r10];
        r21 = 0;
        r21 = r28[r21];
        r0 = r32;
        r1 = r21;
        r2 = r29;
        r10 = r0.isCellFree(r10, r1, r2);
        if (r10 == 0) goto L_0x0251;
    L_0x027b:
        r4 = r4 + 1;
        goto L_0x024f;
    L_0x027e:
        r4 = r34.isFlowX();
        if (r4 == 0) goto L_0x0288;
    L_0x0284:
        r4 = 0;
        r28[r4] = r3;
        goto L_0x023e;
    L_0x0288:
        r4 = 1;
        r28[r4] = r3;
        goto L_0x023e;
    L_0x028c:
        if (r3 != 0) goto L_0x0799;
    L_0x028e:
        if (r16 == 0) goto L_0x0353;
    L_0x0290:
        r3 = r34.isFlowX();
        if (r3 == 0) goto L_0x0353;
    L_0x0296:
        r3 = 2097051; // 0x1fff9b float:2.938594E-39 double:1.036081E-317;
    L_0x0299:
        r4 = 0;
        r4 = r28[r4];
        r4 = 30000 - r4;
        r9 = java.lang.Math.min(r3, r4);
        if (r16 == 0) goto L_0x0359;
    L_0x02a4:
        r3 = r34.isFlowX();
        if (r3 != 0) goto L_0x0359;
    L_0x02aa:
        r3 = 2097051; // 0x1fff9b float:2.938594E-39 double:1.036081E-317;
    L_0x02ad:
        r4 = 1;
        r4 = r28[r4];
        r4 = 30000 - r4;
        r10 = java.lang.Math.min(r3, r4);
        r4 = new net.miginfocom.layout.Grid$Cell;
        if (r6 == 0) goto L_0x035f;
    L_0x02ba:
        r3 = r6.booleanValue();
    L_0x02be:
        r6 = 0;
        r4.<init>(r10, r3, r6);
        r3 = 1;
        r3 = r28[r3];
        r6 = 0;
        r6 = r28[r6];
        r0 = r32;
        r0.setCell(r3, r6, r4);
        r3 = 1;
        if (r9 > r3) goto L_0x02d3;
    L_0x02d0:
        r3 = 1;
        if (r10 <= r3) goto L_0x02ef;
    L_0x02d3:
        r3 = 4;
        r3 = new int[r3];
        r6 = 0;
        r21 = 0;
        r21 = r28[r21];
        r3[r6] = r21;
        r6 = 1;
        r21 = 1;
        r21 = r28[r21];
        r3[r6] = r21;
        r6 = 2;
        r3[r6] = r9;
        r6 = 3;
        r3[r6] = r10;
        r0 = r29;
        r0.add(r3);
    L_0x02ef:
        r26 = r4;
    L_0x02f1:
        r24 = 0;
        if (r16 == 0) goto L_0x0365;
    L_0x02f5:
        r3 = 2097051; // 0x1fff9b float:2.938594E-39 double:1.036081E-317;
    L_0x02f8:
        r23 = 0;
        r4 = r34.isFlowX();
        if (r4 == 0) goto L_0x036c;
    L_0x0300:
        r4 = r5.getSpanX();
    L_0x0304:
        r6 = 2097051; // 0x1fff9b float:2.938594E-39 double:1.036081E-317;
        if (r4 != r6) goto L_0x0371;
    L_0x0309:
        r4 = 1;
    L_0x030a:
        r10 = r7;
        r25 = r11;
        r22 = r19;
        r11 = r8;
        r19 = r18;
        r18 = r3;
        r31 = r12;
        r12 = r17;
        r17 = r31;
    L_0x031a:
        if (r18 < 0) goto L_0x0787;
    L_0x031c:
        r0 = r27;
        r3 = r0.length;
        r0 = r25;
        if (r0 >= r3) goto L_0x0787;
    L_0x0323:
        r7 = r27[r25];
        r0 = r37;
        r8 = getCC(r7, r0);
        r0 = r32;
        r0.addLinkIDs(r8);
        r6 = r7.isVisible();
        if (r6 == 0) goto L_0x0373;
    L_0x0336:
        r9 = -1;
    L_0x0337:
        r3 = r8.isExternal();
        if (r3 != 0) goto L_0x0340;
    L_0x033d:
        r3 = 3;
        if (r9 != r3) goto L_0x0384;
    L_0x0340:
        r6 = r25 + 1;
        r3 = r18 + 1;
        r7 = r12;
        r8 = r19;
        r9 = r22;
    L_0x0349:
        r18 = r3 + -1;
        r25 = r6;
        r12 = r7;
        r19 = r8;
        r22 = r9;
        goto L_0x031a;
    L_0x0353:
        r3 = r5.getSpanX();
        goto L_0x0299;
    L_0x0359:
        r3 = r5.getSpanY();
        goto L_0x02ad;
    L_0x035f:
        r3 = r34.isFlowX();
        goto L_0x02be;
    L_0x0365:
        r3 = r5.getSplit();
        r3 = r3 + -1;
        goto L_0x02f8;
    L_0x036c:
        r4 = r5.getSpanY();
        goto L_0x0304;
    L_0x0371:
        r4 = 0;
        goto L_0x030a;
    L_0x0373:
        r3 = r8.getHideMode();
        r9 = -1;
        if (r3 == r9) goto L_0x037f;
    L_0x037a:
        r9 = r8.getHideMode();
        goto L_0x0337;
    L_0x037f:
        r9 = r34.getHideMode();
        goto L_0x0337;
    L_0x0384:
        if (r6 != 0) goto L_0x0389;
    L_0x0386:
        r3 = 1;
        if (r9 <= r3) goto L_0x03f2;
    L_0x0389:
        r3 = r8.getPushX();
        if (r3 == 0) goto L_0x03f2;
    L_0x038f:
        r3 = 1;
    L_0x0390:
        r21 = r19 | r3;
        if (r6 != 0) goto L_0x0397;
    L_0x0394:
        r3 = 1;
        if (r9 <= r3) goto L_0x03f4;
    L_0x0397:
        r3 = r8.getPushY();
        if (r3 == 0) goto L_0x03f4;
    L_0x039d:
        r3 = 1;
    L_0x039e:
        r19 = r12 | r3;
        if (r8 == r5) goto L_0x041c;
    L_0x03a2:
        r3 = r8.isNewline();
        if (r3 != 0) goto L_0x0774;
    L_0x03a8:
        r3 = r8.isBoundsInGrid();
        if (r3 == 0) goto L_0x0774;
    L_0x03ae:
        r3 = r8.getDockSide();
        r6 = -1;
        if (r3 == r6) goto L_0x03f6;
    L_0x03b5:
        r12 = r23;
        r6 = r24;
        r3 = r25;
        r4 = r17;
        r5 = r15;
        r11 = r20;
        r7 = r19;
        r8 = r21;
        r9 = r22;
    L_0x03c6:
        if (r6 != 0) goto L_0x049f;
    L_0x03c8:
        if (r16 != 0) goto L_0x049f;
    L_0x03ca:
        r6 = r34.isFlowX();
        if (r6 == 0) goto L_0x0489;
    L_0x03d0:
        r6 = r26.spanx;
    L_0x03d4:
        r10 = r34.isFlowX();
        if (r10 == 0) goto L_0x048f;
    L_0x03da:
        r10 = 0;
        r10 = r28[r10];
    L_0x03dd:
        r10 = java.lang.Math.abs(r10);
        r10 = r10 + r6;
        if (r10 < r13) goto L_0x0494;
    L_0x03e4:
        r6 = 1;
    L_0x03e5:
        r11 = r3;
        r12 = r4;
        r15 = r5;
        r16 = r6;
        r17 = r7;
        r18 = r8;
        r19 = r9;
        goto L_0x00a1;
    L_0x03f2:
        r3 = 0;
        goto L_0x0390;
    L_0x03f4:
        r3 = 0;
        goto L_0x039e;
    L_0x03f6:
        if (r18 <= 0) goto L_0x0410;
    L_0x03f8:
        r3 = r8.getSkip();
        if (r3 <= 0) goto L_0x0410;
    L_0x03fe:
        r3 = 1;
        r12 = r3;
        r6 = r24;
        r4 = r17;
        r5 = r15;
        r11 = r20;
        r7 = r19;
        r8 = r21;
        r9 = r22;
        r3 = r25;
        goto L_0x03c6;
    L_0x0410:
        r0 = r32;
        r10 = r0.getPos(r7, r8);
        r0 = r32;
        r11 = r0.getCallbackSize(r7);
    L_0x041c:
        r6 = new net.miginfocom.layout.Grid$CompWrap;
        r12 = 0;
        r6.<init>(r8, r9, r10, r11, r12);
        r3 = r26.compWraps;
        r3.add(r6);
        r3 = r8.getTag();
        if (r3 == 0) goto L_0x0485;
    L_0x042f:
        r3 = 1;
    L_0x0430:
        r0 = r26;
        net.miginfocom.layout.Grid.Cell.access$476(r0, r3);
        r3 = r26.hasTagged;
        r7 = r22 | r3;
        if (r8 == r5) goto L_0x0770;
    L_0x043d:
        r3 = r8.getHorizontal();
        r3 = r3.getSizeGroup();
        if (r3 == 0) goto L_0x076d;
    L_0x0447:
        r3 = r15 + 1;
    L_0x0449:
        r6 = r8.getVertical();
        r6 = r6.getSizeGroup();
        if (r6 == 0) goto L_0x0768;
    L_0x0453:
        r6 = r17 + 1;
        r15 = r3;
        r3 = r6;
    L_0x0457:
        r6 = r25 + 1;
        r9 = r8.isWrap();
        if (r9 != 0) goto L_0x0463;
    L_0x045f:
        if (r4 == 0) goto L_0x075d;
    L_0x0461:
        if (r18 != 0) goto L_0x075d;
    L_0x0463:
        r4 = r8.isWrap();
        if (r4 == 0) goto L_0x0487;
    L_0x0469:
        r4 = r8.getWrapGapSize();
        r0 = r32;
        r1 = r28;
        r0.wrap(r1, r4);
        r4 = r20;
    L_0x0476:
        r5 = 1;
        r12 = r23;
        r11 = r4;
        r8 = r21;
        r9 = r7;
        r7 = r19;
        r4 = r3;
        r3 = r6;
        r6 = r5;
        r5 = r15;
        goto L_0x03c6;
    L_0x0485:
        r3 = 0;
        goto L_0x0430;
    L_0x0487:
        r4 = 1;
        goto L_0x0476;
    L_0x0489:
        r6 = r26.spany;
        goto L_0x03d4;
    L_0x048f:
        r10 = 1;
        r10 = r28[r10];
        goto L_0x03dd;
    L_0x0494:
        if (r12 == 0) goto L_0x0498;
    L_0x0496:
        r6 = r6 + -1;
    L_0x0498:
        r0 = r32;
        r1 = r28;
        r0.increase(r1, r6);
    L_0x049f:
        r6 = r11;
        goto L_0x03e5;
    L_0x04a2:
        if (r15 > 0) goto L_0x04a6;
    L_0x04a4:
        if (r12 <= 0) goto L_0x056e;
    L_0x04a6:
        if (r15 <= 0) goto L_0x0526;
    L_0x04a8:
        r3 = new java.util.HashMap;
        r3.<init>(r15);
        r7 = r3;
    L_0x04ae:
        if (r12 <= 0) goto L_0x0529;
    L_0x04b0:
        r3 = new java.util.HashMap;
        r3.<init>(r12);
        r5 = r3;
    L_0x04b6:
        r8 = new java.util.ArrayList;
        r3 = java.lang.Math.max(r15, r12);
        r8.<init>(r3);
        r0 = r32;
        r3 = r0.grid;
        r3 = r3.values();
        r9 = r3.iterator();
    L_0x04cb:
        r3 = r9.hasNext();
        if (r3 == 0) goto L_0x052c;
    L_0x04d1:
        r3 = r9.next();
        r3 = (net.miginfocom.layout.Grid.Cell) r3;
        r4 = 0;
        r6 = r4;
    L_0x04d9:
        r4 = r3.compWraps;
        r4 = r4.size();
        if (r6 >= r4) goto L_0x04cb;
    L_0x04e3:
        r4 = r3.compWraps;
        r4 = r4.get(r6);
        r4 = (net.miginfocom.layout.Grid.CompWrap) r4;
        r10 = r4.cc;
        r10 = r10.getHorizontal();
        r10 = r10.getSizeGroup();
        r11 = r4.cc;
        r11 = r11.getVertical();
        r11 = r11.getSizeGroup();
        if (r10 != 0) goto L_0x0509;
    L_0x0507:
        if (r11 == 0) goto L_0x0522;
    L_0x0509:
        if (r10 == 0) goto L_0x0514;
    L_0x050b:
        if (r7 == 0) goto L_0x0514;
    L_0x050d:
        r13 = r4.horSizes;
        addToSizeGroup(r7, r10, r13);
    L_0x0514:
        if (r11 == 0) goto L_0x051f;
    L_0x0516:
        if (r5 == 0) goto L_0x051f;
    L_0x0518:
        r10 = r4.verSizes;
        addToSizeGroup(r5, r11, r10);
    L_0x051f:
        r8.add(r4);
    L_0x0522:
        r4 = r6 + 1;
        r6 = r4;
        goto L_0x04d9;
    L_0x0526:
        r3 = 0;
        r7 = r3;
        goto L_0x04ae;
    L_0x0529:
        r3 = 0;
        r5 = r3;
        goto L_0x04b6;
    L_0x052c:
        r3 = 0;
        r6 = r3;
    L_0x052e:
        r3 = r8.size();
        if (r6 >= r3) goto L_0x056e;
    L_0x0534:
        r3 = r8.get(r6);
        r3 = (net.miginfocom.layout.Grid.CompWrap) r3;
        if (r7 == 0) goto L_0x0552;
    L_0x053c:
        r4 = r3.cc;
        r4 = r4.getHorizontal();
        r4 = r4.getSizeGroup();
        r4 = r7.get(r4);
        r4 = (int[]) r4;
        r9 = 1;
        r3.setSizes(r4, r9);
    L_0x0552:
        if (r5 == 0) goto L_0x056a;
    L_0x0554:
        r4 = r3.cc;
        r4 = r4.getVertical();
        r4 = r4.getSizeGroup();
        r4 = r5.get(r4);
        r4 = (int[]) r4;
        r9 = 0;
        r3.setSizes(r4, r9);
    L_0x056a:
        r3 = r6 + 1;
        r6 = r3;
        goto L_0x052e;
    L_0x056e:
        if (r15 > 0) goto L_0x0572;
    L_0x0570:
        if (r12 <= 0) goto L_0x063a;
    L_0x0572:
        if (r15 <= 0) goto L_0x05f2;
    L_0x0574:
        r3 = new java.util.HashMap;
        r3.<init>(r15);
        r7 = r3;
    L_0x057a:
        if (r12 <= 0) goto L_0x05f5;
    L_0x057c:
        r3 = new java.util.HashMap;
        r3.<init>(r12);
        r5 = r3;
    L_0x0582:
        r8 = new java.util.ArrayList;
        r3 = java.lang.Math.max(r15, r12);
        r8.<init>(r3);
        r0 = r32;
        r3 = r0.grid;
        r3 = r3.values();
        r9 = r3.iterator();
    L_0x0597:
        r3 = r9.hasNext();
        if (r3 == 0) goto L_0x05f8;
    L_0x059d:
        r3 = r9.next();
        r3 = (net.miginfocom.layout.Grid.Cell) r3;
        r4 = 0;
        r6 = r4;
    L_0x05a5:
        r4 = r3.compWraps;
        r4 = r4.size();
        if (r6 >= r4) goto L_0x0597;
    L_0x05af:
        r4 = r3.compWraps;
        r4 = r4.get(r6);
        r4 = (net.miginfocom.layout.Grid.CompWrap) r4;
        r10 = r4.cc;
        r10 = r10.getHorizontal();
        r10 = r10.getSizeGroup();
        r11 = r4.cc;
        r11 = r11.getVertical();
        r11 = r11.getSizeGroup();
        if (r10 != 0) goto L_0x05d5;
    L_0x05d3:
        if (r11 == 0) goto L_0x05ee;
    L_0x05d5:
        if (r10 == 0) goto L_0x05e0;
    L_0x05d7:
        if (r7 == 0) goto L_0x05e0;
    L_0x05d9:
        r12 = r4.horSizes;
        addToSizeGroup(r7, r10, r12);
    L_0x05e0:
        if (r11 == 0) goto L_0x05eb;
    L_0x05e2:
        if (r5 == 0) goto L_0x05eb;
    L_0x05e4:
        r10 = r4.verSizes;
        addToSizeGroup(r5, r11, r10);
    L_0x05eb:
        r8.add(r4);
    L_0x05ee:
        r4 = r6 + 1;
        r6 = r4;
        goto L_0x05a5;
    L_0x05f2:
        r3 = 0;
        r7 = r3;
        goto L_0x057a;
    L_0x05f5:
        r3 = 0;
        r5 = r3;
        goto L_0x0582;
    L_0x05f8:
        r3 = 0;
        r6 = r3;
    L_0x05fa:
        r3 = r8.size();
        if (r6 >= r3) goto L_0x063a;
    L_0x0600:
        r3 = r8.get(r6);
        r3 = (net.miginfocom.layout.Grid.CompWrap) r3;
        if (r7 == 0) goto L_0x061e;
    L_0x0608:
        r4 = r3.cc;
        r4 = r4.getHorizontal();
        r4 = r4.getSizeGroup();
        r4 = r7.get(r4);
        r4 = (int[]) r4;
        r9 = 1;
        r3.setSizes(r4, r9);
    L_0x061e:
        if (r5 == 0) goto L_0x0636;
    L_0x0620:
        r4 = r3.cc;
        r4 = r4.getVertical();
        r4 = r4.getSizeGroup();
        r4 = r5.get(r4);
        r4 = (int[]) r4;
        r9 = 0;
        r3.setSizes(r4, r9);
    L_0x0636:
        r3 = r6 + 1;
        r6 = r3;
        goto L_0x05fa;
    L_0x063a:
        if (r19 == 0) goto L_0x0649;
    L_0x063c:
        r0 = r32;
        r3 = r0.grid;
        r3 = r3.values();
        r0 = r33;
        sortCellsByPlatform(r3, r0);
    L_0x0649:
        r0 = r34;
        r1 = r33;
        r10 = net.miginfocom.layout.LayoutUtil.isLeftToRight(r0, r1);
        r0 = r32;
        r3 = r0.grid;
        r3 = r3.values();
        r13 = r3.iterator();
    L_0x065d:
        r3 = r13.hasNext();
        if (r3 == 0) goto L_0x06cb;
    L_0x0663:
        r3 = r13.next();
        r11 = r3;
        r11 = (net.miginfocom.layout.Grid.Cell) r11;
        r14 = r11.compWraps;
        r3 = 0;
        r4 = r14.size();
        r15 = r4 + -1;
        r12 = r3;
    L_0x0676:
        if (r12 > r15) goto L_0x065d;
    L_0x0678:
        r3 = r14.get(r12);
        r3 = (net.miginfocom.layout.Grid.CompWrap) r3;
        if (r12 <= 0) goto L_0x06c3;
    L_0x0680:
        r4 = r12 + -1;
        r4 = r14.get(r4);
        r4 = (net.miginfocom.layout.Grid.CompWrap) r4;
        r4 = r4.comp;
    L_0x068c:
        if (r12 >= r15) goto L_0x06c5;
    L_0x068e:
        r5 = r12 + 1;
        r5 = r14.get(r5);
        r5 = (net.miginfocom.layout.Grid.CompWrap) r5;
        r6 = r5.comp;
    L_0x069a:
        r5 = r3.comp;
        r0 = r37;
        r5 = getCC(r5, r0);
        r8 = r5.getTag();
        if (r4 == 0) goto L_0x06c7;
    L_0x06aa:
        r0 = r37;
        r5 = getCC(r4, r0);
    L_0x06b0:
        if (r6 == 0) goto L_0x06c9;
    L_0x06b2:
        r0 = r37;
        r7 = getCC(r6, r0);
    L_0x06b8:
        r9 = r11.flowx;
        r3.calcGaps(r4, r5, r6, r7, r8, r9, r10);
        r3 = r12 + 1;
        r12 = r3;
        goto L_0x0676;
    L_0x06c3:
        r4 = 0;
        goto L_0x068c;
    L_0x06c5:
        r6 = 0;
        goto L_0x069a;
    L_0x06c7:
        r5 = 0;
        goto L_0x06b0;
    L_0x06c9:
        r7 = 0;
        goto L_0x06b8;
    L_0x06cb:
        r0 = r32;
        r3 = r0.colIndexes;
        r3 = getDockInsets(r3);
        r0 = r32;
        r0.dockOffX = r3;
        r0 = r32;
        r3 = r0.rowIndexes;
        r3 = getDockInsets(r3);
        r0 = r32;
        r0.dockOffY = r3;
        r3 = 0;
        r4 = r35.getCount();
    L_0x06e8:
        if (r3 >= r4) goto L_0x06f8;
    L_0x06ea:
        r0 = r32;
        r5 = r0.rowIndexes;
        r6 = java.lang.Integer.valueOf(r3);
        r5.add(r6);
        r3 = r3 + 1;
        goto L_0x06e8;
    L_0x06f8:
        r3 = 0;
        r4 = r36.getCount();
    L_0x06fd:
        if (r3 >= r4) goto L_0x070d;
    L_0x06ff:
        r0 = r32;
        r5 = r0.colIndexes;
        r6 = java.lang.Integer.valueOf(r3);
        r5.add(r6);
        r3 = r3 + 1;
        goto L_0x06fd;
    L_0x070d:
        r3 = 0;
        r0 = r32;
        r3 = r0.divideIntoLinkedGroups(r3);
        r0 = r32;
        r0.colGroupLists = r3;
        r3 = 1;
        r0 = r32;
        r3 = r0.divideIntoLinkedGroups(r3);
        r0 = r32;
        r0.rowGroupLists = r3;
        if (r18 != 0) goto L_0x072b;
    L_0x0725:
        r3 = r34.isFillX();
        if (r3 == 0) goto L_0x0759;
    L_0x072b:
        r3 = 0;
        r0 = r32;
        r3 = r0.getDefaultPushWeights(r3);
    L_0x0732:
        r0 = r32;
        r0.pushXs = r3;
        if (r17 != 0) goto L_0x073e;
    L_0x0738:
        r3 = r34.isFillY();
        if (r3 == 0) goto L_0x075b;
    L_0x073e:
        r3 = 1;
        r0 = r32;
        r3 = r0.getDefaultPushWeights(r3);
    L_0x0745:
        r0 = r32;
        r0.pushYs = r3;
        r3 = net.miginfocom.layout.LayoutUtil.isDesignTime(r33);
        if (r3 == 0) goto L_0x0758;
    L_0x074f:
        r0 = r32;
        r3 = r0.grid;
        r0 = r33;
        saveGrid(r0, r3);
    L_0x0758:
        return;
    L_0x0759:
        r3 = 0;
        goto L_0x0732;
    L_0x075b:
        r3 = 0;
        goto L_0x0745;
    L_0x075d:
        r17 = r3;
        r8 = r21;
        r9 = r7;
        r3 = r18;
        r7 = r19;
        goto L_0x0349;
    L_0x0768:
        r15 = r3;
        r3 = r17;
        goto L_0x0457;
    L_0x076d:
        r3 = r15;
        goto L_0x0449;
    L_0x0770:
        r3 = r17;
        goto L_0x0457;
    L_0x0774:
        r12 = r23;
        r6 = r24;
        r3 = r25;
        r4 = r17;
        r5 = r15;
        r11 = r20;
        r7 = r19;
        r8 = r21;
        r9 = r22;
        goto L_0x03c6;
    L_0x0787:
        r6 = r24;
        r3 = r25;
        r4 = r17;
        r5 = r15;
        r11 = r20;
        r7 = r12;
        r8 = r19;
        r9 = r22;
        r12 = r23;
        goto L_0x03c6;
    L_0x0799:
        r26 = r3;
        goto L_0x02f1;
    L_0x079d:
        r3 = r4;
        goto L_0x024a;
    L_0x07a0:
        r10 = r14;
        goto L_0x0196;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.miginfocom.layout.Grid.<init>(net.miginfocom.layout.ContainerWrapper, net.miginfocom.layout.LC, net.miginfocom.layout.AC, net.miginfocom.layout.AC, java.util.Map, java.util.ArrayList):void");
    }

    private void addDockingCell(int[] iArr, int i, CompWrap compWrap) {
        int i2;
        int i3;
        int i4;
        int i5;
        switch (i) {
            case LogTarget.ERROR /*0*/:
            case LogTarget.INFO /*2*/:
                if (i == 0) {
                    i2 = iArr[0];
                    iArr[0] = i2 + 1;
                } else {
                    i2 = iArr[2];
                    iArr[2] = i2 - 1;
                }
                i3 = iArr[1];
                i4 = (iArr[3] - iArr[1]) + 1;
                this.colIndexes.add(Integer.valueOf(iArr[3]));
                i5 = i2;
                i2 = i3;
                i3 = 1;
                break;
            case LogTarget.WARN /*1*/:
            case LogTarget.DEBUG /*3*/:
                if (i == 1) {
                    i2 = iArr[1];
                    iArr[1] = i2 + 1;
                } else {
                    i2 = iArr[3];
                    iArr[3] = i2 - 1;
                }
                i4 = iArr[0];
                i3 = (iArr[2] - iArr[0]) + 1;
                this.rowIndexes.add(Integer.valueOf(iArr[2]));
                i5 = i4;
                i4 = 1;
                break;
            default:
                throw new IllegalArgumentException("Internal error 123.");
        }
        this.rowIndexes.add(Integer.valueOf(i5));
        this.colIndexes.add(Integer.valueOf(i2));
        this.grid.put(Integer.valueOf(i2 + (i5 << 16)), new Cell(i4, i3, i4 > 1 ? TEST_GAPS : false, null));
    }

    private void addLinkIDs(CC cc) {
        String[] linkTargets = cc.getLinkTargets();
        for (Object put : linkTargets) {
            if (this.linkTargetIDs == null) {
                this.linkTargetIDs = new HashMap();
            }
            this.linkTargetIDs.put(put, null);
        }
    }

    private static HashMap<String, Integer> addToEndGroup(HashMap<String, Integer> hashMap, String str, int i) {
        if (str != null) {
            if (hashMap == null) {
                hashMap = new HashMap(2);
            }
            Integer num = (Integer) hashMap.get(str);
            if (num == null || i > num.intValue()) {
                hashMap.put(str, Integer.valueOf(i));
            }
        }
        return hashMap;
    }

    private static void addToSizeGroup(HashMap<String, int[]> hashMap, String str, int[] iArr) {
        int[] iArr2 = (int[]) hashMap.get(str);
        if (iArr2 == null) {
            hashMap.put(str, new int[]{iArr[0], iArr[1], iArr[2]});
            return;
        }
        iArr2[0] = Math.max(iArr[0], iArr2[0]);
        iArr2[1] = Math.max(iArr[1], iArr2[1]);
        iArr2[2] = Math.min(iArr[2], iArr2[2]);
    }

    private void adjustMinPrefForSpanningComps(DimConstraint[] dimConstraintArr, Float[] fArr, FlowSizeSpec flowSizeSpec, ArrayList<LinkedDimGroup>[] arrayListArr) {
        for (int length = arrayListArr.length - 1; length >= 0; length--) {
            ArrayList arrayList = arrayListArr[length];
            for (int i = 0; i < arrayList.size(); i++) {
                LinkedDimGroup linkedDimGroup = (LinkedDimGroup) arrayList.get(i);
                if (linkedDimGroup.span != 1) {
                    int[] access$3500 = linkedDimGroup.getMinPrefMax();
                    for (int i2 = 0; i2 <= 1; i2++) {
                        int i3 = access$3500[i2];
                        if (i3 != -2147471302) {
                            int i4 = 0;
                            int i5 = (length << 1) + 1;
                            int min = Math.min(linkedDimGroup.span << 1, flowSizeSpec.sizes.length - i5) - 1;
                            for (int i6 = i5; i6 < i5 + min; i6++) {
                                int i7 = flowSizeSpec.sizes[i6][i2];
                                if (i7 != -2147471302) {
                                    i4 += i7;
                                }
                            }
                            if (i4 < i3) {
                                i4 = 0;
                                for (int i8 = 0; i8 < 4 && r1 < i3; i8++) {
                                    i4 = flowSizeSpec.expandSizes(dimConstraintArr, fArr, i3, i5, min, i2, i8);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void adjustSizeForAbsolute(boolean z) {
        int[] iArr = z ? this.width : this.height;
        Cell cell = (Cell) this.grid.get(null);
        if (cell != null && cell.compWraps.size() != 0) {
            ArrayList access$200 = cell.compWraps;
            int i = 0;
            int size = cell.compWraps.size();
            for (int i2 = 0; i2 < size + 3; i2++) {
                int i3 = 0;
                int i4 = 0;
                while (i4 < size) {
                    CompWrap compWrap = (CompWrap) access$200.get(i4);
                    int[] absoluteDimBounds = getAbsoluteDimBounds(compWrap, 0, z);
                    int i5 = absoluteDimBounds[0] + absoluteDimBounds[1];
                    i4++;
                    i3 = this.linkTargetIDs != null ? setLinkedBounds(compWrap.comp, compWrap.cc, absoluteDimBounds[0], absoluteDimBounds[0], absoluteDimBounds[1], absoluteDimBounds[1], false) | i3 : i3;
                    i = i < i5 ? i5 : i;
                }
                if (i3 == 0) {
                    break;
                }
                i = 0;
                clearGroupLinkBounds();
            }
            int pixels = LayoutUtil.getInsets(this.lc, z ? 3 : 2, !hasDocks() ? TEST_GAPS : false).getPixels(0.0f, this.container, null) + i;
            if (iArr[0] < pixels) {
                iArr[0] = pixels;
            }
            if (iArr[1] < pixels) {
                iArr[1] = pixels;
            }
        }
    }

    private FlowSizeSpec calcRowsOrColsSizes(boolean z) {
        ArrayList[] arrayListArr = z ? this.colGroupLists : this.rowGroupLists;
        Float[] fArr = z ? this.pushXs : this.pushYs;
        int width = z ? this.container.getWidth() : this.container.getHeight();
        BoundSize width2 = z ? this.lc.getWidth() : this.lc.getHeight();
        int constrain = !width2.isUnset() ? width2.constrain(width, (float) getParentSize(this.container, z), this.container) : width;
        DimConstraint[] constaints = (z ? this.colConstr : this.rowConstr).getConstaints();
        TreeSet treeSet = z ? this.colIndexes : this.rowIndexes;
        int[][] iArr = new int[treeSet.size()][];
        HashMap hashMap = new HashMap(2);
        DimConstraint[] dimConstraintArr = new DimConstraint[treeSet.size()];
        Iterator it = treeSet.iterator();
        for (width = 0; width < iArr.length; width++) {
            int intValue = ((Integer) it.next()).intValue();
            int[] iArr2 = new int[3];
            if (intValue < -30000 || intValue > MAX_GRID) {
                dimConstraintArr[width] = DOCK_DIM_CONSTRAINT;
            } else {
                dimConstraintArr[width] = constaints[intValue >= constaints.length ? constaints.length - 1 : intValue];
            }
            ArrayList arrayList = arrayListArr[width];
            int[] iArr3 = new int[]{getTotalGroupsSizeParallel(arrayList, 0, false), getTotalGroupsSizeParallel(arrayList, 1, false), 2097051};
            correctMinMax(iArr3);
            BoundSize size = dimConstraintArr[width].getSize();
            for (int i = 0; i <= 2; i++) {
                int i2 = iArr3[i];
                UnitValue size2 = size.getSize(i);
                if (size2 != null) {
                    i2 = size2.getUnit();
                    i2 = i2 == 14 ? iArr3[1] : i2 == 13 ? iArr3[0] : i2 == 15 ? iArr3[2] : size2.getPixels((float) constrain, this.container, null);
                } else if (intValue >= -30000 && intValue <= MAX_GRID && i2 == 0) {
                    i2 = LayoutUtil.isDesignTime(this.container) ? LayoutUtil.getDesignTimeEmptySize() : 0;
                }
                iArr2[i] = i2;
            }
            correctMinMax(iArr2);
            addToSizeGroup(hashMap, dimConstraintArr[width].getSizeGroup(), iArr2);
            iArr[width] = iArr2;
        }
        if (hashMap.size() > 0) {
            for (width = 0; width < iArr.length; width++) {
                if (dimConstraintArr[width].getSizeGroup() != null) {
                    iArr[width] = (int[]) hashMap.get(dimConstraintArr[width].getSizeGroup());
                }
            }
        }
        boolean[] zArr = new boolean[(dimConstraintArr.length + 1)];
        FlowSizeSpec mergeSizesGapsAndResConstrs = mergeSizesGapsAndResConstrs(getRowResizeConstraints(dimConstraintArr), zArr, iArr, getRowGaps(dimConstraintArr, constrain, z, zArr));
        adjustMinPrefForSpanningComps(dimConstraintArr, fArr, mergeSizesGapsAndResConstrs, arrayListArr);
        return mergeSizesGapsAndResConstrs;
    }

    private void checkSizeCalcs() {
        if (this.colFlowSpecs == null) {
            this.colFlowSpecs = calcRowsOrColsSizes(TEST_GAPS);
            this.rowFlowSpecs = calcRowsOrColsSizes(false);
            this.width = getMinPrefMaxSumSize(TEST_GAPS);
            this.height = getMinPrefMaxSumSize(false);
            if (this.linkTargetIDs == null) {
                resetLinkValues(false, TEST_GAPS);
            } else {
                layout(new int[4], null, null, false, false);
                resetLinkValues(false, false);
            }
            adjustSizeForAbsolute(TEST_GAPS);
            adjustSizeForAbsolute(false);
        }
    }

    private void clearGroupLinkBounds() {
        if (this.linkTargetIDs != null) {
            for (Entry entry : this.linkTargetIDs.entrySet()) {
                if (entry.getValue() == Boolean.TRUE) {
                    LinkHandler.clearBounds(this.container.getLayout(), (String) entry.getKey());
                }
            }
        }
    }

    private static int constrainSize(int i) {
        return i > 0 ? i < 2097051 ? i : 2097051 : 0;
    }

    private static int convertSpanToSparseGrid(int i, int i2, TreeSet<Integer> treeSet) {
        int i3 = i + i2;
        Iterator it = treeSet.iterator();
        int i4 = 1;
        while (it.hasNext()) {
            int intValue = ((Integer) it.next()).intValue();
            if (intValue > i) {
                if (intValue >= i3) {
                    break;
                }
                i4++;
            }
        }
        return i4;
    }

    private static UnitValue correctAlign(CC cc, UnitValue unitValue, boolean z, boolean z2) {
        UnitValue align = (z ? cc.getHorizontal() : cc.getVertical()).getAlign();
        if (align != null) {
            unitValue = align;
        }
        align = unitValue == UnitValue.BASELINE_IDENTITY ? UnitValue.CENTER : unitValue;
        return z2 ? align == UnitValue.LEFT ? UnitValue.RIGHT : align == UnitValue.RIGHT ? UnitValue.LEFT : align : align;
    }

    private static void correctMinMax(int[] iArr) {
        if (iArr[0] > iArr[2]) {
            iArr[0] = iArr[2];
        }
        if (iArr[1] < iArr[0]) {
            iArr[1] = iArr[0];
        }
        if (iArr[1] > iArr[2]) {
            iArr[1] = iArr[2];
        }
    }

    private ArrayList<LinkedDimGroup>[] divideIntoLinkedGroups(boolean z) {
        boolean z2 = (z ? this.lc.isTopToBottom() : LayoutUtil.isLeftToRight(this.lc, this.container)) ? false : TEST_GAPS;
        TreeSet treeSet = z ? this.rowIndexes : this.colIndexes;
        TreeSet treeSet2 = z ? this.colIndexes : this.rowIndexes;
        DimConstraint[] constaints = (z ? this.rowConstr : this.colConstr).getConstaints();
        ArrayList<LinkedDimGroup>[] arrayListArr = new ArrayList[treeSet.size()];
        Iterator it = treeSet.iterator();
        int i = 0;
        while (it.hasNext()) {
            DimConstraint dimConstraint;
            int intValue = ((Integer) it.next()).intValue();
            if (intValue < -30000 || intValue > MAX_GRID) {
                dimConstraint = DOCK_DIM_CONSTRAINT;
            } else {
                dimConstraint = constaints[intValue >= constaints.length ? constaints.length - 1 : intValue];
            }
            ArrayList arrayList = new ArrayList(2);
            int i2 = i + 1;
            arrayListArr[i] = arrayList;
            Iterator it2 = treeSet2.iterator();
            while (it2.hasNext()) {
                i = ((Integer) it2.next()).intValue();
                Cell cell = z ? getCell(intValue, i) : getCell(i, intValue);
                if (!(cell == null || cell.compWraps.size() == 0)) {
                    int access$600 = z ? cell.spany : cell.spanx;
                    if (access$600 > 1) {
                        access$600 = convertSpanToSparseGrid(intValue, access$600, treeSet);
                    }
                    Object obj = cell.flowx == z ? 1 : null;
                    LinkedDimGroup linkedDimGroup;
                    if ((obj != null || cell.compWraps.size() <= 1) && access$600 <= 1) {
                        for (int i3 = 0; i3 < cell.compWraps.size(); i3++) {
                            boolean z3;
                            Object obj2;
                            String str;
                            int size;
                            int i4;
                            CompWrap compWrap = (CompWrap) cell.compWraps.get(i3);
                            if (z && this.lc.isTopToBottom()) {
                                if (dimConstraint.getAlignOrDefault(!z ? TEST_GAPS : false) == UnitValue.BASELINE_IDENTITY) {
                                    z3 = TEST_GAPS;
                                    obj2 = (z || !compWrap.isBaselineAlign(z3)) ? null : 1;
                                    str = obj2 == null ? "baseline" : null;
                                    size = arrayList.size() - 1;
                                    i4 = 0;
                                    while (i4 <= size) {
                                        linkedDimGroup = (LinkedDimGroup) arrayList.get(i4);
                                        if (linkedDimGroup.linkCtx == str) {
                                            if (str != null) {
                                                if (str.equals(linkedDimGroup.linkCtx)) {
                                                }
                                            }
                                            i4++;
                                        }
                                        linkedDimGroup.addCompWrap(compWrap);
                                        obj = 1;
                                        break;
                                    }
                                    obj = null;
                                    if (obj == null) {
                                        linkedDimGroup = new LinkedDimGroup(1, obj2 != null ? 2 : 1, z ? TEST_GAPS : false, z2, null);
                                        linkedDimGroup.addCompWrap(compWrap);
                                        arrayList.add(linkedDimGroup);
                                    }
                                }
                            }
                            z3 = false;
                            if (z) {
                            }
                            if (obj2 == null) {
                            }
                            size = arrayList.size() - 1;
                            i4 = 0;
                            while (i4 <= size) {
                                linkedDimGroup = (LinkedDimGroup) arrayList.get(i4);
                                if (linkedDimGroup.linkCtx == str) {
                                    if (str != null) {
                                        if (str.equals(linkedDimGroup.linkCtx)) {
                                        }
                                    }
                                    i4++;
                                }
                                linkedDimGroup.addCompWrap(compWrap);
                                obj = 1;
                                break;
                                if (obj == null) {
                                    if (obj2 != null) {
                                    }
                                    if (z) {
                                    }
                                    linkedDimGroup = new LinkedDimGroup(1, obj2 != null ? 2 : 1, z ? TEST_GAPS : false, z2, null);
                                    linkedDimGroup.addCompWrap(compWrap);
                                    arrayList.add(linkedDimGroup);
                                }
                            }
                            obj = null;
                            if (obj == null) {
                                if (obj2 != null) {
                                }
                                if (z) {
                                }
                                linkedDimGroup = new LinkedDimGroup(1, obj2 != null ? 2 : 1, z ? TEST_GAPS : false, z2, null);
                                linkedDimGroup.addCompWrap(compWrap);
                                arrayList.add(linkedDimGroup);
                            }
                        }
                    } else {
                        linkedDimGroup = new LinkedDimGroup(access$600, obj != null ? 1 : 0, !z ? TEST_GAPS : false, z2, null);
                        linkedDimGroup.setCompWraps(cell.compWraps);
                        arrayList.add(linkedDimGroup);
                    }
                }
            }
            i = i2;
        }
        return arrayListArr;
    }

    private boolean doAbsoluteCorrections(CompWrap compWrap, int[] iArr) {
        int[] absoluteDimBounds = getAbsoluteDimBounds(compWrap, iArr[2], TEST_GAPS);
        if (absoluteDimBounds != null) {
            compWrap.setDimBounds(absoluteDimBounds[0], absoluteDimBounds[1], TEST_GAPS);
        }
        absoluteDimBounds = getAbsoluteDimBounds(compWrap, iArr[3], false);
        if (absoluteDimBounds != null) {
            compWrap.setDimBounds(absoluteDimBounds[0], absoluteDimBounds[1], false);
        }
        return this.linkTargetIDs != null ? setLinkedBounds(compWrap.comp, compWrap.cc, compWrap.x, compWrap.y, compWrap.w, compWrap.h, false) : false;
    }

    private static Float[] extractSubArray(DimConstraint[] dimConstraintArr, Float[] fArr, int i, int i2) {
        Float[] fArr2;
        int i3;
        if (fArr == null || fArr.length < i + i2) {
            fArr2 = new Float[i2];
            for (i3 = (i + i2) - 1; i3 >= 0; i3 -= 2) {
                if (dimConstraintArr[i3 >> 1] != DOCK_DIM_CONSTRAINT) {
                    fArr2[i3 - i] = ResizeConstraint.WEIGHT_100;
                    break;
                }
            }
        } else {
            fArr2 = new Float[i2];
            for (i3 = 0; i3 < i2; i3++) {
                fArr2[i3] = fArr[i + i3];
            }
        }
        return fArr2;
    }

    private int[] getAbsoluteDimBounds(CompWrap compWrap, int i, boolean z) {
        if (!compWrap.cc.isExternal()) {
            int[] visualPadding = this.lc.isVisualPadding() ? compWrap.comp.getVisualPadding() : null;
            UnitValue[] padding = compWrap.cc.getPadding();
            if (compWrap.pos == null && visualPadding == null && padding == null) {
                return null;
            }
            UnitValue unitValue;
            int min;
            int i2;
            int i3;
            int access$1400 = z ? compWrap.x : compWrap.y;
            int access$1500 = z ? compWrap.w : compWrap.h;
            if (compWrap.pos != null) {
                UnitValue unitValue2;
                if (compWrap.pos != null) {
                    unitValue = compWrap.pos[z ? 0 : 1];
                } else {
                    unitValue = null;
                }
                if (compWrap.pos != null) {
                    unitValue2 = compWrap.pos[z ? 2 : 3];
                } else {
                    unitValue2 = null;
                }
                int access$2800 = compWrap.getSize(0, z);
                int access$28002 = compWrap.getSize(2, z);
                min = Math.min(Math.max(compWrap.getSize(1, z), access$2800), access$28002);
                if (unitValue != null) {
                    int pixels = unitValue.getPixels(unitValue.getUnit() == 12 ? (float) min : (float) i, this.container, compWrap.comp);
                    if (unitValue2 != null) {
                        access$1400 = Math.min(Math.max((z ? compWrap.x + compWrap.w : compWrap.y + compWrap.h) - pixels, access$2800), access$28002);
                        min = pixels;
                    } else {
                        access$1400 = min;
                        min = pixels;
                    }
                } else {
                    int i4 = min;
                    min = access$1400;
                    access$1400 = i4;
                }
                if (unitValue2 == null) {
                    i2 = access$1400;
                    access$1400 = min;
                } else if (unitValue != null) {
                    i2 = Math.min(Math.max(unitValue2.getPixels((float) i, this.container, compWrap.comp) - min, access$2800), access$28002);
                    access$1400 = min;
                } else {
                    i2 = access$1400;
                    access$1400 = unitValue2.getPixels((float) i, this.container, compWrap.comp) - access$1400;
                }
            } else {
                i2 = access$1500;
            }
            if (padding != null) {
                unitValue = padding[z ? 1 : 0];
                min = unitValue != null ? unitValue.getPixels((float) i, this.container, compWrap.comp) : 0;
                access$1400 += min;
                unitValue = padding[z ? 3 : 2];
                i2 += (unitValue != null ? unitValue.getPixels((float) i, this.container, compWrap.comp) : 0) + (-min);
            }
            if (visualPadding != null) {
                access$1500 = visualPadding[z ? 1 : 0];
                i3 = (visualPadding[z ? 3 : 2] + (-access$1500)) + i2;
                access$1500 = access$1400 + access$1500;
            } else {
                i3 = i2;
                access$1500 = access$1400;
            }
            return new int[]{access$1500, i3};
        } else if (z) {
            return new int[]{compWrap.comp.getX(), compWrap.comp.getWidth()};
        } else {
            return new int[]{compWrap.comp.getY(), compWrap.comp.getHeight()};
        }
    }

    private static int[] getBaselineAboveBelow(ArrayList<CompWrap> arrayList, int i, boolean z) {
        int size = arrayList.size();
        int i2 = -32768;
        int i3 = -32768;
        for (int i4 = 0; i4 < size; i4++) {
            CompWrap compWrap = (CompWrap) arrayList.get(i4);
            int access$2800 = compWrap.getSize(i, false);
            if (access$2800 >= 2097051) {
                return new int[]{1048525, 1048525};
            }
            int access$5100 = compWrap.getBaseline(i);
            i3 = Math.max(compWrap.getGapBefore(i, false) + access$5100, i3);
            i2 = Math.max((access$2800 - access$5100) + compWrap.getGapAfter(i, false), i2);
            if (z) {
                compWrap.setDimBounds(-access$5100, access$2800, false);
            }
        }
        return new int[]{i3, i2};
    }

    private static CC getCC(ComponentWrapper componentWrapper, Map<ComponentWrapper, CC> map) {
        CC cc = (CC) map.get(componentWrapper);
        return cc != null ? cc : new CC();
    }

    private BoundSize[] getCallbackSize(ComponentWrapper componentWrapper) {
        if (this.callbackList != null) {
            for (int i = 0; i < this.callbackList.size(); i++) {
                BoundSize[] size = ((LayoutCallback) this.callbackList.get(i)).getSize(componentWrapper);
                if (size != null) {
                    return size;
                }
            }
        }
        return null;
    }

    private Cell getCell(int i, int i2) {
        return (Cell) this.grid.get(Integer.valueOf((i << 16) + i2));
    }

    private static boolean[] getComponentGapPush(ArrayList<CompWrap> arrayList, boolean z) {
        boolean[] zArr = new boolean[(arrayList.size() + 1)];
        int i = 0;
        while (i < zArr.length) {
            boolean access$3300 = i > 0 ? ((CompWrap) arrayList.get(i - 1)).isPushGap(z, false) : false;
            if (!access$3300 && i < zArr.length - 1) {
                access$3300 = ((CompWrap) arrayList.get(i)).isPushGap(z, TEST_GAPS);
            }
            zArr[i] = access$3300;
            i++;
        }
        return zArr;
    }

    private static ResizeConstraint[] getComponentResizeConstraints(ArrayList<CompWrap> arrayList, boolean z) {
        ResizeConstraint[] resizeConstraintArr = new ResizeConstraint[arrayList.size()];
        for (int i = 0; i < resizeConstraintArr.length; i++) {
            CC access$700 = ((CompWrap) arrayList.get(i)).cc;
            resizeConstraintArr[i] = access$700.getDimConstraint(z).resize;
            int dockSide = access$700.getDockSide();
            if (z) {
                if (!(dockSide == 0 || dockSide == 2)) {
                }
            } else if (dockSide != 1) {
                if (dockSide != 3) {
                }
            }
            ResizeConstraint resizeConstraint = resizeConstraintArr[i];
            resizeConstraintArr[i] = new ResizeConstraint(resizeConstraint.shrinkPrio, resizeConstraint.shrink, resizeConstraint.growPrio, ResizeConstraint.WEIGHT_100);
        }
        return resizeConstraintArr;
    }

    private static int[][] getComponentSizes(ArrayList<CompWrap> arrayList, boolean z) {
        int[][] iArr = new int[arrayList.size()][];
        for (int i = 0; i < iArr.length; i++) {
            CompWrap compWrap = (CompWrap) arrayList.get(i);
            iArr[i] = z ? compWrap.horSizes : compWrap.verSizes;
        }
        return iArr;
    }

    private Float[] getDefaultPushWeights(boolean z) {
        ArrayList[] arrayListArr = z ? this.rowGroupLists : this.colGroupLists;
        Float[] fArr = GROW_100;
        int i = 1;
        for (ArrayList arrayList : arrayListArr) {
            Float f = null;
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                LinkedDimGroup linkedDimGroup = (LinkedDimGroup) arrayList.get(i2);
                int i3 = 0;
                while (i3 < linkedDimGroup._compWraps.size()) {
                    CompWrap compWrap = (CompWrap) linkedDimGroup._compWraps.get(i3);
                    int hideMode = compWrap.comp.isVisible() ? -1 : compWrap.cc.getHideMode() != -1 ? compWrap.cc.getHideMode() : this.lc.getHideMode();
                    Float pushY = hideMode < 2 ? z ? compWrap.cc.getPushY() : compWrap.cc.getPushX() : null;
                    if (f != null && (r0 == null || r0.floatValue() <= f.floatValue())) {
                        pushY = f;
                    }
                    i3++;
                    f = pushY;
                }
            }
            if (f != null) {
                if (fArr == GROW_100) {
                    fArr = new Float[((arrayListArr.length << 1) + 1)];
                }
                fArr[i] = f;
            }
            i += 2;
        }
        return fArr;
    }

    private static int getDockInsets(TreeSet<Integer> treeSet) {
        Iterator it = treeSet.iterator();
        int i = 0;
        while (it.hasNext() && ((Integer) it.next()).intValue() < -30000) {
            i++;
        }
        return i;
    }

    private static int[][] getGaps(ArrayList<CompWrap> arrayList, boolean z) {
        int size = arrayList.size();
        int[][] iArr = new int[(size + 1)][];
        iArr[0] = ((CompWrap) arrayList.get(0)).getGaps(z, TEST_GAPS);
        int i = 0;
        while (i < size) {
            iArr[i + 1] = mergeSizes(((CompWrap) arrayList.get(i)).getGaps(z, false), i < size + -1 ? ((CompWrap) arrayList.get(i + 1)).getGaps(z, TEST_GAPS) : null);
            i++;
        }
        return iArr;
    }

    static synchronized HashMap<Object, int[]> getGridPositions(Object obj) {
        HashMap<Object, int[]> hashMap;
        synchronized (Grid.class) {
            if (PARENT_GRIDPOS_MAP == null) {
                hashMap = null;
            } else {
                LinkedHashMap linkedHashMap = (LinkedHashMap) PARENT_GRIDPOS_MAP.get(obj);
                if (linkedHashMap == null) {
                    hashMap = null;
                } else {
                    HashMap<Object, int[]> hashMap2 = new HashMap();
                    for (Entry entry : linkedHashMap.entrySet()) {
                        Cell cell = (Cell) entry.getValue();
                        Integer num = (Integer) entry.getKey();
                        if (num != null) {
                            int intValue = num.intValue();
                            int i = 65535 & intValue;
                            int i2 = intValue >> 16;
                            Iterator it = cell.compWraps.iterator();
                            while (it.hasNext()) {
                                hashMap2.put(((CompWrap) it.next()).comp.getComponent(), new int[]{i, i2, cell.spanx, cell.spany});
                            }
                        }
                    }
                    hashMap = hashMap2;
                }
            }
        }
        return hashMap;
    }

    private static LinkedDimGroup getGroupContaining(ArrayList<LinkedDimGroup>[] arrayListArr, CompWrap compWrap) {
        for (ArrayList arrayList : arrayListArr) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                ArrayList access$2500 = ((LinkedDimGroup) arrayList.get(i))._compWraps;
                int size2 = access$2500.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    if (access$2500.get(i2) == compWrap) {
                        return (LinkedDimGroup) arrayList.get(i);
                    }
                }
            }
        }
        return null;
    }

    private int[] getMinPrefMaxSumSize(boolean z) {
        int[][] access$2900 = z ? this.colFlowSpecs.sizes : this.rowFlowSpecs.sizes;
        int[] iArr = new int[3];
        BoundSize width = z ? this.lc.getWidth() : this.lc.getHeight();
        for (int i = 0; i < access$2900.length; i++) {
            if (access$2900[i] != null) {
                int[] iArr2 = access$2900[i];
                for (int i2 = 0; i2 <= 2; i2++) {
                    if (width.getSize(i2) == null) {
                        int i3 = iArr2[i2];
                        if (i3 != -2147471302) {
                            int i4;
                            if (i2 == 1) {
                                i4 = iArr2[2];
                                if (i4 == -2147471302 || i4 >= i3) {
                                    i4 = i3;
                                }
                                i3 = iArr2[0];
                                if (i3 > i4) {
                                    i4 = i3;
                                }
                            } else {
                                i4 = i3;
                            }
                            iArr[i2] = i4 + iArr[i2];
                        }
                        if (iArr2[2] == -2147471302 || iArr[2] > 2097051) {
                            iArr[2] = 2097051;
                        }
                    } else if (i == 0) {
                        iArr[i2] = width.getSize(i2).getPixels((float) getParentSize(this.container, z), this.container, null);
                    }
                }
            }
        }
        correctMinMax(iArr);
        return iArr;
    }

    private static int getParentSize(ComponentWrapper componentWrapper, boolean z) {
        return componentWrapper.getParent() != null ? z ? componentWrapper.getWidth() : componentWrapper.getHeight() : 0;
    }

    private UnitValue[] getPos(ComponentWrapper componentWrapper, CC cc) {
        int i = 0;
        UnitValue[] unitValueArr = null;
        if (this.callbackList != null) {
            int i2 = 0;
            while (i2 < this.callbackList.size() && r0 == null) {
                UnitValue[] position = ((LayoutCallback) this.callbackList.get(i2)).getPosition(componentWrapper);
                i2++;
                unitValueArr = position;
            }
        }
        UnitValue[] pos = cc.getPos();
        if (unitValueArr == null || pos == null) {
            if (unitValueArr == null) {
                unitValueArr = pos;
            }
            return unitValueArr;
        }
        while (i < 4) {
            UnitValue unitValue = unitValueArr[i];
            if (unitValue != null) {
                pos[i] = unitValue;
            }
            i++;
        }
        return pos;
    }

    private int[][] getRowGaps(DimConstraint[] dimConstraintArr, int i, boolean z, boolean[] zArr) {
        BoundSize gridGapX = z ? this.lc.getGridGapX() : this.lc.getGridGapY();
        if (gridGapX == null) {
            gridGapX = z ? PlatformDefaults.getGridGapX() : PlatformDefaults.getGridGapY();
        }
        int[] pixelSizes = gridGapX.getPixelSizes((float) i, this.container, null);
        boolean z2 = !hasDocks() ? TEST_GAPS : false;
        UnitValue insets = LayoutUtil.getInsets(this.lc, z ? 1 : 0, z2);
        UnitValue insets2 = LayoutUtil.getInsets(this.lc, z ? 3 : 2, z2);
        int[][] iArr = new int[(dimConstraintArr.length + 1)][];
        int i2 = 0;
        int i3 = 0;
        while (i2 < iArr.length) {
            DimConstraint dimConstraint = i2 > 0 ? dimConstraintArr[i2 - 1] : null;
            DimConstraint dimConstraint2 = i2 < dimConstraintArr.length ? dimConstraintArr[i2] : null;
            Object obj = (dimConstraint == DOCK_DIM_CONSTRAINT || dimConstraint == null) ? 1 : null;
            Object obj2 = (dimConstraint2 == DOCK_DIM_CONSTRAINT || dimConstraint2 == null) ? 1 : null;
            if (obj == null || obj2 == null) {
                int i4;
                if (this.wrapGapMap == null || z == this.lc.isFlowX()) {
                    i4 = i3;
                    gridGapX = null;
                } else {
                    i4 = i3 + 1;
                    gridGapX = (BoundSize) this.wrapGapMap.get(Integer.valueOf(i3));
                }
                if (gridGapX == null) {
                    int[] rowGaps = dimConstraint != null ? dimConstraint.getRowGaps(this.container, null, i, false) : null;
                    int[] rowGaps2 = dimConstraint2 != null ? dimConstraint2.getRowGaps(this.container, null, i, TEST_GAPS) : null;
                    if (obj != null && rowGaps2 == null && insets != null) {
                        i3 = insets.getPixels((float) i, this.container, null);
                        iArr[i2] = new int[]{i3, i3, i3};
                    } else if (obj2 == null || rowGaps != null || insets == null) {
                        iArr[i2] = rowGaps2 != rowGaps ? mergeSizes(rowGaps2, rowGaps) : new int[]{pixelSizes[0], pixelSizes[1], pixelSizes[2]};
                    } else {
                        i3 = insets2.getPixels((float) i, this.container, null);
                        iArr[i2] = new int[]{i3, i3, i3};
                    }
                    if ((dimConstraint != null && dimConstraint.isGapAfterPush()) || (dimConstraint2 != null && dimConstraint2.isGapBeforePush())) {
                        zArr[i2] = TEST_GAPS;
                    }
                    i3 = i4;
                } else {
                    if (gridGapX.isUnset()) {
                        iArr[i2] = new int[]{pixelSizes[0], pixelSizes[1], pixelSizes[2]};
                    } else {
                        iArr[i2] = gridGapX.getPixelSizes((float) i, this.container, null);
                    }
                    zArr[i2] = gridGapX.getGapPush();
                    i3 = i4;
                }
            }
            i2++;
        }
        return iArr;
    }

    private static ResizeConstraint[] getRowResizeConstraints(DimConstraint[] dimConstraintArr) {
        ResizeConstraint[] resizeConstraintArr = new ResizeConstraint[dimConstraintArr.length];
        for (int i = 0; i < resizeConstraintArr.length; i++) {
            resizeConstraintArr[i] = dimConstraintArr[i].resize;
        }
        return resizeConstraintArr;
    }

    static synchronized int[][] getSizesAndIndexes(Object obj, boolean z) {
        int[][] iArr;
        synchronized (Grid.class) {
            if (PARENT_ROWCOL_SIZES_MAP == null) {
                iArr = (int[][]) null;
            } else {
                iArr = (int[][]) PARENT_ROWCOL_SIZES_MAP[z ? 0 : 1].get(obj);
            }
        }
        return iArr;
    }

    private static int getTotalGroupsSizeParallel(ArrayList<LinkedDimGroup> arrayList, int i, boolean z) {
        int i2 = i == 2 ? 2097051 : 0;
        int size = arrayList.size();
        int i3 = 0;
        int i4 = i2;
        while (i3 < size) {
            LinkedDimGroup linkedDimGroup = (LinkedDimGroup) arrayList.get(i3);
            if (z || linkedDimGroup.span == 1) {
                i2 = linkedDimGroup.getMinPrefMax()[i];
                if (i2 >= 2097051) {
                    return 2097051;
                }
                if (i == 2) {
                    if (i2 < i4) {
                        i3++;
                        i4 = i2;
                    }
                } else if (i2 > i4) {
                    i3++;
                    i4 = i2;
                }
            }
            i2 = i4;
            i3++;
            i4 = i2;
        }
        return constrainSize(i4);
    }

    private static int getTotalSizeParallel(ArrayList<CompWrap> arrayList, int i, boolean z) {
        int i2 = i == 2 ? 2097051 : 0;
        int size = arrayList.size();
        int i3 = 0;
        int i4 = i2;
        while (i3 < size) {
            i2 = ((CompWrap) arrayList.get(i3)).getSizeInclGaps(i, z);
            if (i2 >= 2097051) {
                return 2097051;
            }
            if (i == 2) {
                if (i2 < i4) {
                }
                i2 = i4;
            } else {
                if (i2 > i4) {
                }
                i2 = i4;
            }
            i3++;
            i4 = i2;
        }
        return constrainSize(i4);
    }

    private static int getTotalSizeSerial(ArrayList<CompWrap> arrayList, int i, boolean z) {
        int size = arrayList.size();
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        while (i3 < size) {
            CompWrap compWrap = (CompWrap) arrayList.get(i3);
            int access$5200 = compWrap.getGapBefore(i, z);
            if (access$5200 > i2) {
                i4 += access$5200 - i2;
            }
            i4 += compWrap.getSize(i, z);
            int access$5300 = compWrap.getGapAfter(i, z);
            i2 = i4 + access$5300;
            if (i2 >= 2097051) {
                return 2097051;
            }
            i3++;
            i4 = i2;
            i2 = access$5300;
        }
        return constrainSize(i4);
    }

    private boolean hasDocks() {
        return (this.dockOffX > 0 || this.dockOffY > 0 || ((Integer) this.rowIndexes.last()).intValue() > MAX_GRID || ((Integer) this.colIndexes.last()).intValue() > MAX_GRID) ? TEST_GAPS : false;
    }

    private int increase(int[] iArr, int i) {
        if (this.lc.isFlowX()) {
            int i2 = iArr[0] + i;
            iArr[0] = i2;
            return i2;
        }
        i2 = iArr[1] + i;
        iArr[1] = i2;
        return i2;
    }

    private final boolean isCellFree(int i, int i2, ArrayList<int[]> arrayList) {
        if (getCell(i, i2) != null) {
            return false;
        }
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            int[] iArr = (int[]) arrayList.get(i3);
            if (iArr[0] <= i2 && iArr[1] <= i && iArr[0] + iArr[2] > i2) {
                if (iArr[3] + iArr[1] > i) {
                    return false;
                }
            }
        }
        return TEST_GAPS;
    }

    private static void layoutBaseline(ContainerWrapper containerWrapper, ArrayList<CompWrap> arrayList, DimConstraint dimConstraint, int i, int i2, int i3, int i4) {
        int i5 = 0;
        int[] baselineAboveBelow = getBaselineAboveBelow(arrayList, i3, TEST_GAPS);
        int i6 = baselineAboveBelow[1] + baselineAboveBelow[0];
        UnitValue align = ((CompWrap) arrayList.get(0)).cc.getVertical().getAlign();
        if (i4 == 1 && align == null) {
            align = dimConstraint.getAlignOrDefault(false);
        }
        if (align == UnitValue.BASELINE_IDENTITY) {
            align = UnitValue.CENTER;
        }
        int max = (baselineAboveBelow[0] + i) + (align != null ? Math.max(0, align.getPixels((float) (i2 - i6), containerWrapper, null)) : 0);
        i6 = arrayList.size();
        while (i5 < i6) {
            CompWrap compWrap = (CompWrap) arrayList.get(i5);
            CompWrap.access$1612(compWrap, max);
            if (compWrap.y + compWrap.h > i + i2) {
                compWrap.h = (i + i2) - compWrap.y;
            }
            i5++;
        }
    }

    private void layoutInOneDim(int i, UnitValue unitValue, boolean z, Float[] fArr) {
        int i2;
        if (z ? this.lc.isTopToBottom() : LayoutUtil.isLeftToRight(this.lc, this.container)) {
            Object obj = null;
        } else {
            int i3 = 1;
        }
        DimConstraint[] constaints = (z ? this.rowConstr : this.colConstr).getConstaints();
        FlowSizeSpec flowSizeSpec = z ? this.rowFlowSpecs : this.colFlowSpecs;
        ArrayList[] arrayListArr = z ? this.rowGroupLists : this.colGroupLists;
        int[] calculateSerial = LayoutUtil.calculateSerial(flowSizeSpec.sizes, flowSizeSpec.resConstsInclGaps, fArr, 1, i);
        if (LayoutUtil.isDesignTime(this.container)) {
            TreeSet treeSet = z ? this.rowIndexes : this.colIndexes;
            int[] iArr = new int[treeSet.size()];
            i2 = 0;
            Iterator it = treeSet.iterator();
            while (it.hasNext()) {
                int i4 = i2 + 1;
                iArr[i2] = ((Integer) it.next()).intValue();
                i2 = i4;
            }
            putSizesAndIndexes(this.container.getComponent(), calculateSerial, iArr, z);
        }
        int pixels = unitValue != null ? unitValue.getPixels((float) (i - LayoutUtil.sum(calculateSerial)), this.container, null) : 0;
        if (obj != null) {
            pixels = i - pixels;
        }
        i2 = 0;
        while (i2 < arrayListArr.length) {
            DimConstraint dimConstraint;
            ArrayList arrayList = arrayListArr[i2];
            i4 = i2 - (z ? this.dockOffY : this.dockOffX);
            int i5 = i2 << 1;
            int i6 = i5 + 1;
            int i7 = pixels + (obj != null ? -calculateSerial[i5] : calculateSerial[i5]);
            if (i4 >= 0) {
                dimConstraint = constaints[i4 >= constaints.length ? constaints.length - 1 : i4];
            } else {
                dimConstraint = DOCK_DIM_CONSTRAINT;
            }
            int i8 = calculateSerial[i6];
            for (i5 = 0; i5 < arrayList.size(); i5++) {
                LinkedDimGroup linkedDimGroup = (LinkedDimGroup) arrayList.get(i5);
                linkedDimGroup.layout(dimConstraint, i7, linkedDimGroup.span > 1 ? LayoutUtil.sum(calculateSerial, i6, Math.min((linkedDimGroup.span << 1) - 1, (calculateSerial.length - i6) - 1)) : i8, linkedDimGroup.span);
            }
            if (obj != null) {
                i8 = -i8;
            }
            i2++;
            pixels = i7 + i8;
        }
    }

    private static void layoutParallel(ContainerWrapper containerWrapper, ArrayList<CompWrap> arrayList, DimConstraint dimConstraint, int i, int i2, boolean z, boolean z2) {
        int[][] iArr = new int[arrayList.size()][];
        for (int i3 = 0; i3 < iArr.length; i3++) {
            CompWrap compWrap = (CompWrap) arrayList.get(i3);
            DimConstraint dimConstraint2 = compWrap.cc.getDimConstraint(z);
            ResizeConstraint[] resizeConstraintArr = new ResizeConstraint[3];
            resizeConstraintArr[0] = compWrap.isPushGap(z, TEST_GAPS) ? GAP_RC_CONST_PUSH : GAP_RC_CONST;
            resizeConstraintArr[1] = dimConstraint2.resize;
            resizeConstraintArr[2] = compWrap.isPushGap(z, false) ? GAP_RC_CONST_PUSH : GAP_RC_CONST;
            int[][] iArr2 = new int[3][];
            iArr2[0] = compWrap.getGaps(z, TEST_GAPS);
            iArr2[1] = z ? compWrap.horSizes : compWrap.verSizes;
            iArr2[2] = compWrap.getGaps(z, false);
            iArr[i3] = LayoutUtil.calculateSerial(iArr2, resizeConstraintArr, dimConstraint.isFill() ? GROW_100 : null, 1, i2);
        }
        setCompWrapBounds(containerWrapper, iArr, (ArrayList) arrayList, dimConstraint.getAlignOrDefault(z), i, i2, z, z2);
    }

    private static void layoutSerial(ContainerWrapper containerWrapper, ArrayList<CompWrap> arrayList, DimConstraint dimConstraint, int i, int i2, boolean z, int i3, boolean z2) {
        FlowSizeSpec mergeSizesGapsAndResConstrs = mergeSizesGapsAndResConstrs(getComponentResizeConstraints(arrayList, z), getComponentGapPush(arrayList, z), getComponentSizes(arrayList, z), getGaps(arrayList, z));
        setCompWrapBounds(containerWrapper, LayoutUtil.calculateSerial(mergeSizesGapsAndResConstrs.sizes, mergeSizesGapsAndResConstrs.resConstsInclGaps, dimConstraint.isFill() ? GROW_100 : null, 1, i2), (ArrayList) arrayList, dimConstraint.getAlignOrDefault(z), i, i2, z, z2);
    }

    private static int mergeSizes(int i, int i2, boolean z) {
        if (i == -2147471302 || i == i2) {
            return i2;
        }
        if (i2 == -2147471302) {
            return i;
        }
        return z == (i > i2 ? TEST_GAPS : false) ? i : i2;
    }

    private static int[] mergeSizes(int[] iArr, int[] iArr2) {
        if (iArr == null) {
            return iArr2;
        }
        if (iArr2 == null) {
            return iArr;
        }
        int[] iArr3 = new int[iArr.length];
        for (int i = 0; i < iArr3.length; i++) {
            iArr3[i] = mergeSizes(iArr[i], iArr2[i], TEST_GAPS);
        }
        return iArr3;
    }

    private static FlowSizeSpec mergeSizesGapsAndResConstrs(ResizeConstraint[] resizeConstraintArr, boolean[] zArr, int[][] iArr, int[][] iArr2) {
        int i = 0;
        int[][] iArr3 = new int[((iArr.length << 1) + 1)][];
        ResizeConstraint[] resizeConstraintArr2 = new ResizeConstraint[iArr3.length];
        iArr3[0] = iArr2[0];
        int i2 = 1;
        int i3 = 0;
        while (i3 < iArr.length) {
            resizeConstraintArr2[i2] = resizeConstraintArr[i3];
            iArr3[i2] = iArr[i3];
            iArr3[i2 + 1] = iArr2[i3 + 1];
            if (iArr3[i2 - 1] != null) {
                resizeConstraintArr2[i2 - 1] = zArr[i3 < zArr.length ? i3 : zArr.length + -1] ? GAP_RC_CONST_PUSH : GAP_RC_CONST;
            }
            if (i3 == iArr.length - 1 && iArr3[i2 + 1] != null) {
                resizeConstraintArr2[i2 + 1] = zArr[i3 + 1 < zArr.length ? i3 + 1 : zArr.length + -1] ? GAP_RC_CONST_PUSH : GAP_RC_CONST;
            }
            i3++;
            i2 += 2;
        }
        while (i < iArr3.length) {
            if (iArr3[i] == null) {
                iArr3[i] = new int[3];
            }
            i++;
        }
        return new FlowSizeSpec(resizeConstraintArr2, null);
    }

    private static synchronized void putSizesAndIndexes(Object obj, int[] iArr, int[] iArr2, boolean z) {
        int i = 0;
        synchronized (Grid.class) {
            if (PARENT_ROWCOL_SIZES_MAP == null) {
                PARENT_ROWCOL_SIZES_MAP = new WeakHashMap[]{new WeakHashMap(4), new WeakHashMap(4)};
            }
            WeakHashMap[] weakHashMapArr = PARENT_ROWCOL_SIZES_MAP;
            if (!z) {
                i = 1;
            }
            weakHashMapArr[i].put(obj, new int[][]{iArr2, iArr});
        }
    }

    private void resetLinkValues(boolean z, boolean z2) {
        Object layout = this.container.getLayout();
        if (z2) {
            LinkHandler.clearTemporaryBounds(layout);
        }
        boolean z3 = !hasDocks() ? TEST_GAPS : false;
        int constrain = z ? this.lc.getWidth().constrain(this.container.getWidth(), (float) getParentSize(this.container, TEST_GAPS), this.container) : 0;
        int constrain2 = z ? this.lc.getHeight().constrain(this.container.getHeight(), (float) getParentSize(this.container, false), this.container) : 0;
        int pixels = LayoutUtil.getInsets(this.lc, 0, z3).getPixels(0.0f, this.container, null);
        int pixels2 = LayoutUtil.getInsets(this.lc, 1, z3).getPixels(0.0f, this.container, null);
        LinkHandler.setBounds(layout, "visual", pixels, pixels2, (constrain - pixels) - LayoutUtil.getInsets(this.lc, 2, z3).getPixels(0.0f, this.container, null), (constrain2 - pixels2) - LayoutUtil.getInsets(this.lc, 3, z3).getPixels(0.0f, this.container, null), TEST_GAPS, false);
        LinkHandler.setBounds(layout, "container", 0, 0, constrain, constrain2, TEST_GAPS, false);
    }

    private static synchronized void saveGrid(ComponentWrapper componentWrapper, LinkedHashMap<Integer, Cell> linkedHashMap) {
        synchronized (Grid.class) {
            if (PARENT_GRIDPOS_MAP == null) {
                PARENT_GRIDPOS_MAP = new WeakHashMap();
            }
            PARENT_GRIDPOS_MAP.put(componentWrapper.getComponent(), linkedHashMap);
        }
    }

    private void setCell(int i, int i2, Cell cell) {
        if (i2 < 0 || i2 > MAX_GRID || i < 0 || i > MAX_GRID) {
            throw new IllegalArgumentException("Cell position out of bounds. row: " + i + ", col: " + i2);
        }
        this.rowIndexes.add(Integer.valueOf(i));
        this.colIndexes.add(Integer.valueOf(i2));
        this.grid.put(Integer.valueOf((i << 16) + i2), cell);
    }

    private static void setCompWrapBounds(ContainerWrapper containerWrapper, int[] iArr, ArrayList<CompWrap> arrayList, UnitValue unitValue, int i, int i2, boolean z, boolean z2) {
        int min;
        int i3 = 0;
        int sum = LayoutUtil.sum(iArr);
        UnitValue correctAlign = correctAlign(((CompWrap) arrayList.get(0)).cc, unitValue, z, z2);
        sum = i2 - sum;
        if (sum > 0 && correctAlign != null) {
            min = Math.min(sum, Math.max(0, correctAlign.getPixels((float) sum, containerWrapper, null)));
            if (z2) {
                min = -min;
            }
            i += min;
        }
        int size = arrayList.size();
        sum = 0;
        while (sum < size) {
            CompWrap compWrap = (CompWrap) arrayList.get(sum);
            int i4;
            if (z2) {
                i4 = i3 + 1;
                i3 = i - iArr[i3];
                compWrap.setDimBounds(i3 - iArr[i4], iArr[i4], z);
                min = i4 + 1;
                i3 -= iArr[i4];
            } else {
                i4 = i3 + 1;
                i3 = iArr[i3] + i;
                compWrap.setDimBounds(i3, iArr[i4], z);
                min = i4 + 1;
                i3 += iArr[i4];
            }
            sum++;
            i = i3;
            i3 = min;
        }
    }

    private static void setCompWrapBounds(ContainerWrapper containerWrapper, int[][] iArr, ArrayList<CompWrap> arrayList, UnitValue unitValue, int i, int i2, boolean z, boolean z2) {
        for (int i3 = 0; i3 < iArr.length; i3++) {
            int i4;
            CompWrap compWrap = (CompWrap) arrayList.get(i3);
            UnitValue correctAlign = correctAlign(compWrap.cc, unitValue, z, z2);
            int[] iArr2 = iArr[i3];
            int i5 = iArr2[0];
            int i6 = iArr2[1];
            int i7 = iArr2[2];
            int i8 = z2 ? i - i5 : i + i5;
            i5 = ((i2 - i6) - i5) - i7;
            if (i5 <= 0 || correctAlign == null) {
                i4 = i8;
            } else {
                i4 = Math.min(i5, Math.max(0, correctAlign.getPixels((float) i5, containerWrapper, null)));
                if (z2) {
                    i4 = -i4;
                }
                i4 += i8;
            }
            if (z2) {
                i4 -= i6;
            }
            compWrap.setDimBounds(i4, i6, z);
        }
    }

    private boolean setLinkedBounds(ComponentWrapper componentWrapper, CC cc, int i, int i2, int i3, int i4, boolean z) {
        String id = cc.getId() != null ? cc.getId() : componentWrapper.getLinkId();
        if (id == null) {
            return false;
        }
        String str;
        int indexOf = id.indexOf(46);
        if (indexOf != -1) {
            String substring = id.substring(0, indexOf);
            id = id.substring(indexOf + 1);
            str = substring;
        } else {
            str = null;
        }
        Object layout = this.container.getLayout();
        boolean z2 = false;
        if (z || (this.linkTargetIDs != null && this.linkTargetIDs.containsKey(id))) {
            z2 = LinkHandler.setBounds(layout, id, i, i2, i3, i4, !z ? TEST_GAPS : false, false);
        }
        if (str == null || (!z && (this.linkTargetIDs == null || !this.linkTargetIDs.containsKey(str)))) {
            return z2;
        }
        if (this.linkTargetIDs == null) {
            this.linkTargetIDs = new HashMap(4);
        }
        this.linkTargetIDs.put(str, Boolean.TRUE);
        return LinkHandler.setBounds(layout, str, i, i2, i3, i4, !z ? TEST_GAPS : false, TEST_GAPS) | z2;
    }

    private static void sortCellsByPlatform(Collection<Cell> collection, ContainerWrapper containerWrapper) {
        String buttonOrder = PlatformDefaults.getButtonOrder();
        String toLowerCase = buttonOrder.toLowerCase();
        if (PlatformDefaults.convertToPixels(Plot.DEFAULT_FOREGROUND_ALPHA, "u", TEST_GAPS, 0.0f, containerWrapper, null) == UnitConverter.UNABLE) {
            throw new IllegalArgumentException("'unrelated' not recognized by PlatformDefaults!");
        }
        int[] iArr = new int[]{PlatformDefaults.convertToPixels(Plot.DEFAULT_FOREGROUND_ALPHA, "u", TEST_GAPS, 0.0f, containerWrapper, null), PlatformDefaults.convertToPixels(Plot.DEFAULT_FOREGROUND_ALPHA, "u", TEST_GAPS, 0.0f, containerWrapper, null), -2147471302};
        int[] iArr2 = new int[]{0, 0, -2147471302};
        for (Cell cell : collection) {
            if (cell.hasTagged) {
                CompWrap compWrap;
                CompWrap compWrap2 = null;
                Object obj = null;
                Object obj2 = null;
                ArrayList arrayList = new ArrayList(cell.compWraps.size());
                int length = toLowerCase.length();
                for (int i = 0; i < length; i++) {
                    char charAt = toLowerCase.charAt(i);
                    if (charAt == '+' || charAt == '_') {
                        obj = 1;
                        if (charAt == '+') {
                            obj2 = 1;
                        }
                    } else {
                        String tagForChar = PlatformDefaults.getTagForChar(charAt);
                        if (tagForChar != null) {
                            int size = cell.compWraps.size();
                            int i2 = 0;
                            while (i2 < size) {
                                CompWrap compWrap3;
                                Object obj3;
                                compWrap = (CompWrap) cell.compWraps.get(i2);
                                if (tagForChar.equals(compWrap.cc.getTag())) {
                                    if (Character.isUpperCase(buttonOrder.charAt(i))) {
                                        int pixels = PlatformDefaults.getMinimumButtonWidth().getPixels(0.0f, containerWrapper, compWrap.comp);
                                        if (pixels > compWrap.horSizes[0]) {
                                            compWrap.horSizes[0] = pixels;
                                        }
                                        correctMinMax(compWrap.horSizes);
                                    }
                                    arrayList.add(compWrap);
                                    if (obj != null) {
                                        (compWrap2 != null ? compWrap2 : compWrap).mergeGapSizes(iArr, cell.flowx, compWrap2 == null ? TEST_GAPS : false);
                                        if (obj2 != null) {
                                            compWrap.forcedPushGaps = 1;
                                            obj = null;
                                            obj2 = null;
                                        }
                                    }
                                    if (charAt == 'u') {
                                        obj = 1;
                                    }
                                    Object obj4 = obj2;
                                    obj2 = obj;
                                    compWrap3 = compWrap;
                                    obj3 = obj4;
                                } else {
                                    obj3 = obj2;
                                    obj2 = obj;
                                    compWrap3 = compWrap2;
                                }
                                i2++;
                                compWrap2 = compWrap3;
                                obj = obj2;
                                obj2 = obj3;
                            }
                        }
                    }
                }
                if (arrayList.size() > 0) {
                    compWrap = (CompWrap) arrayList.get(arrayList.size() - 1);
                    if (obj != null) {
                        compWrap.mergeGapSizes(iArr, cell.flowx, false);
                        if (obj2 != null) {
                            CompWrap.access$2376(compWrap, 2);
                        }
                    }
                    if (compWrap.cc.getHorizontal().getGapAfter() == null) {
                        compWrap.setGaps(iArr2, 3);
                    }
                    compWrap = (CompWrap) arrayList.get(0);
                    if (compWrap.cc.getHorizontal().getGapBefore() == null) {
                        compWrap.setGaps(iArr2, 1);
                    }
                }
                if (cell.compWraps.size() == arrayList.size()) {
                    cell.compWraps.clear();
                } else {
                    cell.compWraps.removeAll(arrayList);
                }
                cell.compWraps.addAll(arrayList);
            }
        }
    }

    private void wrap(int[] iArr, BoundSize boundSize) {
        boolean isFlowX = this.lc.isFlowX();
        iArr[0] = isFlowX ? 0 : iArr[0] + 1;
        iArr[1] = isFlowX ? iArr[1] + 1 : 0;
        if (boundSize != null) {
            if (this.wrapGapMap == null) {
                this.wrapGapMap = new HashMap(8);
            }
            this.wrapGapMap.put(Integer.valueOf(iArr[isFlowX ? 1 : 0]), boundSize);
        }
        if (isFlowX) {
            this.rowIndexes.add(Integer.valueOf(iArr[1]));
        } else {
            this.colIndexes.add(Integer.valueOf(iArr[0]));
        }
    }

    public ContainerWrapper getContainer() {
        return this.container;
    }

    public final int[] getHeight() {
        checkSizeCalcs();
        return (int[]) this.height.clone();
    }

    public final int[] getWidth() {
        checkSizeCalcs();
        return (int[]) this.width.clone();
    }

    public void invalidateContainerSize() {
        this.colFlowSpecs = null;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean layout(int[] r20, net.miginfocom.layout.UnitValue r21, net.miginfocom.layout.UnitValue r22, boolean r23, boolean r24) {
        /*
        r19 = this;
        if (r23 == 0) goto L_0x000b;
    L_0x0002:
        r2 = new java.util.ArrayList;
        r2.<init>();
        r0 = r19;
        r0.debugRects = r2;
    L_0x000b:
        r19.checkSizeCalcs();
        r2 = 1;
        r3 = 1;
        r0 = r19;
        r0.resetLinkValues(r2, r3);
        r2 = 2;
        r2 = r20[r2];
        r3 = 0;
        r0 = r19;
        r4 = r0.pushXs;
        r0 = r19;
        r1 = r21;
        r0.layoutInOneDim(r2, r1, r3, r4);
        r2 = 3;
        r2 = r20[r2];
        r3 = 1;
        r0 = r19;
        r4 = r0.pushYs;
        r0 = r19;
        r1 = r22;
        r0.layoutInOneDim(r2, r1, r3, r4);
        r5 = 0;
        r4 = 0;
        r0 = r19;
        r2 = r0.container;
        r12 = r2.getComponentCount();
        r7 = 0;
        if (r12 <= 0) goto L_0x01cb;
    L_0x0040:
        r2 = 0;
        r10 = r2;
    L_0x0042:
        r0 = r19;
        r2 = r0.linkTargetIDs;
        if (r2 == 0) goto L_0x019a;
    L_0x0048:
        r2 = 2;
    L_0x0049:
        if (r10 >= r2) goto L_0x01cb;
    L_0x004b:
        r2 = 0;
        r9 = r2;
    L_0x004d:
        r6 = 0;
        r0 = r19;
        r2 = r0.grid;
        r2 = r2.values();
        r13 = r2.iterator();
    L_0x005a:
        r2 = r13.hasNext();
        if (r2 == 0) goto L_0x01af;
    L_0x0060:
        r2 = r13.next();
        r2 = (net.miginfocom.layout.Grid.Cell) r2;
        r14 = r2.compWraps;
        r2 = 0;
        r15 = r14.size();
        r11 = r2;
    L_0x0070:
        if (r11 >= r15) goto L_0x005a;
    L_0x0072:
        r2 = r14.get(r11);
        r3 = r2;
        r3 = (net.miginfocom.layout.Grid.CompWrap) r3;
        if (r10 != 0) goto L_0x025e;
    L_0x007b:
        r0 = r19;
        r1 = r20;
        r2 = r0.doAbsoluteCorrections(r3, r1);
        r6 = r6 | r2;
        if (r6 != 0) goto L_0x025a;
    L_0x0086:
        r2 = r3.cc;
        r2 = r2.getHorizontal();
        r2 = r2.getEndGroup();
        if (r2 == 0) goto L_0x00ae;
    L_0x0094:
        r2 = r3.cc;
        r2 = r2.getHorizontal();
        r2 = r2.getEndGroup();
        r8 = r3.x;
        r16 = r3.w;
        r8 = r8 + r16;
        r5 = addToEndGroup(r5, r2, r8);
    L_0x00ae:
        r2 = r3.cc;
        r2 = r2.getVertical();
        r2 = r2.getEndGroup();
        if (r2 == 0) goto L_0x025a;
    L_0x00bc:
        r2 = r3.cc;
        r2 = r2.getVertical();
        r2 = r2.getEndGroup();
        r8 = r3.y;
        r16 = r3.h;
        r8 = r8 + r16;
        r2 = addToEndGroup(r4, r2, r8);
        r4 = r5;
    L_0x00d7:
        r0 = r19;
        r5 = r0.linkTargetIDs;
        if (r5 == 0) goto L_0x026a;
    L_0x00dd:
        r0 = r19;
        r5 = r0.linkTargetIDs;
        r8 = "visual";
        r5 = r5.containsKey(r8);
        if (r5 != 0) goto L_0x00f5;
    L_0x00e9:
        r0 = r19;
        r5 = r0.linkTargetIDs;
        r8 = "container";
        r5 = r5.containsKey(r8);
        if (r5 == 0) goto L_0x026a;
    L_0x00f5:
        r5 = 1;
        r7 = r4;
        r4 = r6;
        r6 = r2;
    L_0x00f9:
        r0 = r19;
        r2 = r0.linkTargetIDs;
        if (r2 == 0) goto L_0x0102;
    L_0x00ff:
        r2 = 1;
        if (r10 != r2) goto L_0x01a0;
    L_0x0102:
        r2 = r3.cc;
        r2 = r2.getHorizontal();
        r2 = r2.getEndGroup();
        if (r2 == 0) goto L_0x012e;
    L_0x0110:
        r2 = r3.cc;
        r2 = r2.getHorizontal();
        r2 = r2.getEndGroup();
        r2 = r7.get(r2);
        r2 = (java.lang.Integer) r2;
        r2 = r2.intValue();
        r8 = r3.x;
        r2 = r2 - r8;
        r3.w = r2;
    L_0x012e:
        r2 = r3.cc;
        r2 = r2.getVertical();
        r2 = r2.getEndGroup();
        if (r2 == 0) goto L_0x015a;
    L_0x013c:
        r2 = r3.cc;
        r2 = r2.getVertical();
        r2 = r2.getEndGroup();
        r2 = r6.get(r2);
        r2 = (java.lang.Integer) r2;
        r2 = r2.intValue();
        r8 = r3.y;
        r2 = r2 - r8;
        r3.h = r2;
    L_0x015a:
        r2 = 0;
        r2 = r20[r2];
        net.miginfocom.layout.Grid.CompWrap.access$1412(r3, r2);
        r2 = 1;
        r2 = r20[r2];
        net.miginfocom.layout.Grid.CompWrap.access$1612(r3, r2);
        if (r24 == 0) goto L_0x019d;
    L_0x0168:
        if (r5 != 0) goto L_0x019d;
    L_0x016a:
        r2 = 1;
    L_0x016b:
        r2 = r3.transferBounds(r2);
        r8 = r5 | r2;
        r0 = r19;
        r2 = r0.callbackList;
        if (r2 == 0) goto L_0x019f;
    L_0x0177:
        r2 = 0;
        r5 = r2;
    L_0x0179:
        r0 = r19;
        r2 = r0.callbackList;
        r2 = r2.size();
        if (r5 >= r2) goto L_0x019f;
    L_0x0183:
        r0 = r19;
        r2 = r0.callbackList;
        r2 = r2.get(r5);
        r2 = (net.miginfocom.layout.LayoutCallback) r2;
        r16 = r3.comp;
        r0 = r16;
        r2.correctBounds(r0);
        r2 = r5 + 1;
        r5 = r2;
        goto L_0x0179;
    L_0x019a:
        r2 = 1;
        goto L_0x0049;
    L_0x019d:
        r2 = 0;
        goto L_0x016b;
    L_0x019f:
        r5 = r8;
    L_0x01a0:
        r2 = r11 + 1;
        r11 = r2;
        r17 = r4;
        r4 = r6;
        r6 = r17;
        r18 = r5;
        r5 = r7;
        r7 = r18;
        goto L_0x0070;
    L_0x01af:
        r19.clearGroupLinkBounds();
        r2 = r9 + 1;
        r3 = r12 << 3;
        r3 = r3 + 10;
        if (r2 <= r3) goto L_0x01c6;
    L_0x01ba:
        r2 = java.lang.System.err;
        r3 = "Unstable cyclic dependency in absolute linked values!";
        r2.println(r3);
    L_0x01c1:
        r2 = r10 + 1;
        r10 = r2;
        goto L_0x0042;
    L_0x01c6:
        if (r6 == 0) goto L_0x01c1;
    L_0x01c8:
        r9 = r2;
        goto L_0x004d;
    L_0x01cb:
        if (r23 == 0) goto L_0x0259;
    L_0x01cd:
        r0 = r19;
        r2 = r0.grid;
        r2 = r2.values();
        r4 = r2.iterator();
    L_0x01d9:
        r2 = r4.hasNext();
        if (r2 == 0) goto L_0x0259;
    L_0x01df:
        r2 = r4.next();
        r2 = (net.miginfocom.layout.Grid.Cell) r2;
        r5 = r2.compWraps;
        r2 = 0;
        r6 = r5.size();
        r3 = r2;
    L_0x01ef:
        if (r3 >= r6) goto L_0x01d9;
    L_0x01f1:
        r2 = r5.get(r3);
        r2 = (net.miginfocom.layout.Grid.CompWrap) r2;
        r0 = r19;
        r8 = r0.colGroupLists;
        r8 = getGroupContaining(r8, r2);
        r0 = r19;
        r9 = r0.rowGroupLists;
        r9 = getGroupContaining(r9, r2);
        if (r8 == 0) goto L_0x0251;
    L_0x0209:
        if (r9 == 0) goto L_0x0251;
    L_0x020b:
        r0 = r19;
        r10 = r0.debugRects;
        r2 = 4;
        r11 = new int[r2];
        r12 = 0;
        r2 = r8.lStart;
        r13 = 0;
        r13 = r20[r13];
        r13 = r13 + r2;
        r2 = r8.fromEnd;
        if (r2 == 0) goto L_0x0255;
    L_0x0221:
        r2 = r8.lSize;
    L_0x0225:
        r2 = r13 - r2;
        r11[r12] = r2;
        r12 = 1;
        r2 = r9.lStart;
        r13 = 1;
        r13 = r20[r13];
        r13 = r13 + r2;
        r2 = r9.fromEnd;
        if (r2 == 0) goto L_0x0257;
    L_0x0238:
        r2 = r9.lSize;
    L_0x023c:
        r2 = r13 - r2;
        r11[r12] = r2;
        r2 = 2;
        r8 = r8.lSize;
        r11[r2] = r8;
        r2 = 3;
        r8 = r9.lSize;
        r11[r2] = r8;
        r10.add(r11);
    L_0x0251:
        r2 = r3 + 1;
        r3 = r2;
        goto L_0x01ef;
    L_0x0255:
        r2 = 0;
        goto L_0x0225;
    L_0x0257:
        r2 = 0;
        goto L_0x023c;
    L_0x0259:
        return r7;
    L_0x025a:
        r2 = r4;
        r4 = r5;
        goto L_0x00d7;
    L_0x025e:
        r17 = r6;
        r6 = r4;
        r4 = r17;
        r18 = r7;
        r7 = r5;
        r5 = r18;
        goto L_0x00f9;
    L_0x026a:
        r5 = r7;
        r7 = r4;
        r4 = r6;
        r6 = r2;
        goto L_0x00f9;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.miginfocom.layout.Grid.layout(int[], net.miginfocom.layout.UnitValue, net.miginfocom.layout.UnitValue, boolean, boolean):boolean");
    }

    public void paintDebug() {
        if (this.debugRects != null) {
            int i;
            this.container.paintDebugOutline();
            ArrayList arrayList = new ArrayList();
            int size = this.debugRects.size();
            for (i = 0; i < size; i++) {
                int[] iArr = (int[]) this.debugRects.get(i);
                if (!arrayList.contains(iArr)) {
                    this.container.paintDebugCell(iArr[0], iArr[1], iArr[2], iArr[3]);
                    arrayList.add(iArr);
                }
            }
            for (Cell access$200 : this.grid.values()) {
                ArrayList access$2002 = access$200.compWraps;
                int size2 = access$2002.size();
                for (i = 0; i < size2; i++) {
                    ((CompWrap) access$2002.get(i)).comp.paintDebugOutline();
                }
            }
        }
    }
}
