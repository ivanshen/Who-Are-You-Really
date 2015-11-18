package net.miginfocom.layout;

import java.beans.Encoder;
import java.beans.Expression;
import java.beans.PersistenceDelegate;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;

public final class UnitValue implements Serializable {
    public static final int ADD = 101;
    public static final int ALIGN = 12;
    static final UnitValue BASELINE_IDENTITY;
    static final UnitValue BOTTOM;
    public static final int BUTTON = 16;
    static final UnitValue CENTER;
    public static final int CM = 4;
    private static final ArrayList<UnitConverter> CONVERTERS;
    public static final int DIV = 104;
    private static final int IDENTITY = -1;
    public static final int INCH = 5;
    static final UnitValue INF;
    static final UnitValue LABEL;
    public static final int LABEL_ALIGN = 27;
    static final UnitValue LEADING;
    static final UnitValue LEFT;
    public static final int LINK_H = 21;
    public static final int LINK_W = 20;
    public static final int LINK_X = 18;
    public static final int LINK_X2 = 22;
    public static final int LINK_XPOS = 24;
    public static final int LINK_Y = 19;
    public static final int LINK_Y2 = 23;
    public static final int LINK_YPOS = 25;
    public static final int LOOKUP = 26;
    public static final int LPX = 1;
    public static final int LPY = 2;
    public static final int MAX = 106;
    public static final int MAX_SIZE = 15;
    public static final int MID = 107;
    public static final int MIN = 105;
    public static final int MIN_SIZE = 13;
    public static final int MM = 3;
    public static final int MUL = 103;
    public static final int PERCENT = 6;
    public static final int PIXEL = 0;
    public static final int PREF_SIZE = 14;
    public static final int PT = 7;
    static final UnitValue RIGHT;
    private static final float[] SCALE;
    public static final int SPX = 8;
    public static final int SPY = 9;
    public static final int STATIC = 100;
    public static final int SUB = 102;
    static final UnitValue TOP;
    static final UnitValue TRAILING;
    private static final HashMap<String, Integer> UNIT_MAP;
    static final UnitValue ZERO;
    private static final long serialVersionUID = 1;
    private final transient boolean isHor;
    private transient String linkId;
    private final transient int oper;
    private final transient UnitValue[] subUnits;
    private final transient int unit;
    private final transient String unitStr;
    private final transient float value;

    static class 1 extends PersistenceDelegate {
        1() {
        }

        protected Expression instantiate(Object obj, Encoder encoder) {
            UnitValue unitValue = (UnitValue) obj;
            if (unitValue.getConstraintString() == null) {
                throw new IllegalStateException("Design time must be on to use XML persistence. See LayoutUtil.");
            }
            Class cls = ConstraintParser.class;
            String str = "parseUnitValueOrAlign";
            Object[] objArr = new Object[UnitValue.MM];
            objArr[UnitValue.PIXEL] = unitValue.getConstraintString();
            objArr[UnitValue.LPX] = unitValue.isHorizontal() ? Boolean.TRUE : Boolean.FALSE;
            objArr[UnitValue.LPY] = null;
            return new Expression(obj, cls, str, objArr);
        }
    }

    static {
        UNIT_MAP = new HashMap(32);
        CONVERTERS = new ArrayList();
        UNIT_MAP.put("px", Integer.valueOf(PIXEL));
        UNIT_MAP.put("lpx", Integer.valueOf(LPX));
        UNIT_MAP.put("lpy", Integer.valueOf(LPY));
        UNIT_MAP.put("%", Integer.valueOf(PERCENT));
        UNIT_MAP.put("cm", Integer.valueOf(CM));
        UNIT_MAP.put("in", Integer.valueOf(INCH));
        UNIT_MAP.put("spx", Integer.valueOf(SPX));
        UNIT_MAP.put("spy", Integer.valueOf(SPY));
        UNIT_MAP.put("al", Integer.valueOf(ALIGN));
        UNIT_MAP.put("mm", Integer.valueOf(MM));
        UNIT_MAP.put("pt", Integer.valueOf(PT));
        UNIT_MAP.put("min", Integer.valueOf(MIN_SIZE));
        UNIT_MAP.put("minimum", Integer.valueOf(MIN_SIZE));
        UNIT_MAP.put("p", Integer.valueOf(PREF_SIZE));
        UNIT_MAP.put("pref", Integer.valueOf(PREF_SIZE));
        UNIT_MAP.put("max", Integer.valueOf(MAX_SIZE));
        UNIT_MAP.put("maximum", Integer.valueOf(MAX_SIZE));
        UNIT_MAP.put("button", Integer.valueOf(BUTTON));
        UNIT_MAP.put("label", Integer.valueOf(LABEL_ALIGN));
        ZERO = new UnitValue(0.0f, null, PIXEL, true, STATIC, null, null, "0px");
        TOP = new UnitValue(0.0f, null, PERCENT, false, STATIC, null, null, "top");
        LEADING = new UnitValue(0.0f, null, PERCENT, true, STATIC, null, null, "leading");
        LEFT = new UnitValue(0.0f, null, PERCENT, true, STATIC, null, null, "left");
        CENTER = new UnitValue(50.0f, null, PERCENT, true, STATIC, null, null, "center");
        TRAILING = new UnitValue(100.0f, null, PERCENT, true, STATIC, null, null, "trailing");
        RIGHT = new UnitValue(100.0f, null, PERCENT, true, STATIC, null, null, "right");
        BOTTOM = new UnitValue(100.0f, null, PERCENT, false, STATIC, null, null, "bottom");
        LABEL = new UnitValue(0.0f, null, LABEL_ALIGN, false, STATIC, null, null, "label");
        INF = new UnitValue(2097051.0f, null, PIXEL, true, STATIC, null, null, "inf");
        BASELINE_IDENTITY = new UnitValue(0.0f, null, IDENTITY, false, STATIC, null, null, "baseline");
        SCALE = new float[]{25.4f, 2.54f, Plot.DEFAULT_FOREGROUND_ALPHA, 0.0f, 72.0f};
        LayoutUtil.setDelegate(UnitValue.class, new 1());
    }

    public UnitValue(float f) {
        this(f, null, PIXEL, true, STATIC, null, null, f + "px");
    }

    public UnitValue(float f, int i, String str) {
        this(f, null, i, true, STATIC, null, null, str);
    }

    private UnitValue(float f, String str, int i, boolean z, int i2, UnitValue unitValue, UnitValue unitValue2, String str2) {
        UnitValue[] unitValueArr = null;
        this.linkId = null;
        if (i2 < STATIC || i2 > MID) {
            throw new IllegalArgumentException("Unknown Operation: " + i2);
        } else if (i2 < ADD || i2 > MID || !(unitValue == null || unitValue2 == null)) {
            this.value = f;
            this.oper = i2;
            this.isHor = z;
            this.unitStr = str;
            if (str != null) {
                i = parseUnitString();
            }
            this.unit = i;
            if (!(unitValue == null || unitValue2 == null)) {
                unitValueArr = new UnitValue[LPY];
                unitValueArr[PIXEL] = unitValue;
                unitValueArr[LPX] = unitValue2;
            }
            this.subUnits = unitValueArr;
            LayoutUtil.putCCString(this, str2);
        } else {
            throw new IllegalArgumentException(i2 + " Operation may not have null sub-UnitValues.");
        }
    }

    UnitValue(float f, String str, boolean z, int i, String str2) {
        this(f, str, IDENTITY, z, i, null, null, str2);
    }

    UnitValue(boolean z, int i, UnitValue unitValue, UnitValue unitValue2, String str) {
        this(0.0f, "", IDENTITY, z, i, unitValue, unitValue2, str);
        if (unitValue == null || unitValue2 == null) {
            throw new IllegalArgumentException("Sub units is null!");
        }
    }

    public static synchronized void addGlobalUnitConverter(UnitConverter unitConverter) {
        synchronized (UnitValue.class) {
            if (unitConverter == null) {
                throw new NullPointerException();
            }
            CONVERTERS.add(unitConverter);
        }
    }

    public static int getDefaultUnit() {
        return PlatformDefaults.getDefaultHorizontalUnit();
    }

    public static synchronized UnitConverter[] getGlobalUnitConverters() {
        UnitConverter[] unitConverterArr;
        synchronized (UnitValue.class) {
            unitConverterArr = (UnitConverter[]) CONVERTERS.toArray(new UnitConverter[CONVERTERS.size()]);
        }
        return unitConverterArr;
    }

    private float lookup(float f, ContainerWrapper containerWrapper, ComponentWrapper componentWrapper) {
        for (int size = CONVERTERS.size() + IDENTITY; size >= 0; size += IDENTITY) {
            float convertToPixels = (float) ((UnitConverter) CONVERTERS.get(size)).convertToPixels(this.value, this.unitStr, this.isHor, f, containerWrapper, componentWrapper);
            if (convertToPixels != -8.7654312E7f) {
                return convertToPixels;
            }
        }
        return (float) PlatformDefaults.convertToPixels(this.value, this.unitStr, this.isHor, f, containerWrapper, componentWrapper);
    }

    private int parseUnitString() {
        if (this.unitStr.length() == 0) {
            return this.isHor ? PlatformDefaults.getDefaultHorizontalUnit() : PlatformDefaults.getDefaultVerticalUnit();
        } else {
            Integer num = (Integer) UNIT_MAP.get(this.unitStr);
            if (num != null) {
                return num.intValue();
            }
            if (this.unitStr.equals("lp")) {
                return this.isHor ? LPX : LPY;
            } else {
                if (this.unitStr.equals("sp")) {
                    return this.isHor ? SPX : SPY;
                } else {
                    if (lookup(0.0f, null, null) != -8.7654312E7f) {
                        return LOOKUP;
                    }
                    int indexOf = this.unitStr.indexOf(46);
                    if (indexOf != IDENTITY) {
                        this.linkId = this.unitStr.substring(PIXEL, indexOf);
                        String substring = this.unitStr.substring(indexOf + LPX);
                        if (substring.equals("x")) {
                            return LINK_X;
                        }
                        if (substring.equals("y")) {
                            return LINK_Y;
                        }
                        if (substring.equals("w") || substring.equals("width")) {
                            return LINK_W;
                        }
                        if (substring.equals("h") || substring.equals("height")) {
                            return LINK_H;
                        }
                        if (substring.equals("x2")) {
                            return LINK_X2;
                        }
                        if (substring.equals("y2")) {
                            return LINK_Y2;
                        }
                        if (substring.equals("xpos")) {
                            return LINK_XPOS;
                        }
                        if (substring.equals("ypos")) {
                            return LINK_YPOS;
                        }
                    }
                    throw new IllegalArgumentException("Unknown keyword: " + this.unitStr);
                }
            }
        }
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        LayoutUtil.setSerializedObject(this, LayoutUtil.readAsXML(objectInputStream));
    }

    private Object readResolve() throws ObjectStreamException {
        return LayoutUtil.getSerializedObject(this);
    }

    public static synchronized boolean removeGlobalUnitConverter(UnitConverter unitConverter) {
        boolean remove;
        synchronized (UnitValue.class) {
            remove = CONVERTERS.remove(unitConverter);
        }
        return remove;
    }

    public static void setDefaultUnit(int i) {
        PlatformDefaults.setDefaultHorizontalUnit(i);
        PlatformDefaults.setDefaultVerticalUnit(i);
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        if (getClass() == UnitValue.class) {
            LayoutUtil.writeAsXML(objectOutputStream, this);
        }
    }

    public final String getConstraintString() {
        return LayoutUtil.getCCString(this);
    }

    final String getLinkTargetId() {
        return this.linkId;
    }

    public final int getOperation() {
        return this.oper;
    }

    public final int getPixels(float f, ContainerWrapper containerWrapper, ComponentWrapper componentWrapper) {
        return Math.round(getPixelsExact(f, containerWrapper, componentWrapper));
    }

    public final float getPixelsExact(float f, ContainerWrapper containerWrapper, ComponentWrapper componentWrapper) {
        int i = LPX;
        if (containerWrapper == null) {
            return Plot.DEFAULT_FOREGROUND_ALPHA;
        }
        float f2;
        if (this.oper == STATIC) {
            Integer value;
            switch (this.unit) {
                case PIXEL /*0*/:
                    return this.value;
                case LPX /*1*/:
                case LPY /*2*/:
                    boolean z;
                    if (this.unit != LPX) {
                        z = false;
                    }
                    return containerWrapper.getPixelUnitFactor(z) * this.value;
                case MM /*3*/:
                case CM /*4*/:
                case INCH /*5*/:
                case PT /*7*/:
                    f2 = SCALE[this.unit - 3];
                    Float horizontalScaleFactor = this.isHor ? PlatformDefaults.getHorizontalScaleFactor() : PlatformDefaults.getVerticalScaleFactor();
                    if (horizontalScaleFactor != null) {
                        f2 *= horizontalScaleFactor.floatValue();
                    }
                    return (((float) (this.isHor ? containerWrapper.getHorizontalScreenDPI() : containerWrapper.getVerticalScreenDPI())) * this.value) / f2;
                case PERCENT /*6*/:
                    return (this.value * f) * 0.01f;
                case SPX /*8*/:
                case SPY /*9*/:
                    return (((float) (this.unit == SPX ? containerWrapper.getScreenWidth() : containerWrapper.getScreenHeight())) * this.value) * 0.01f;
                case ALIGN /*12*/:
                    Object layout = containerWrapper.getLayout();
                    String str = "visual";
                    if (this.isHor) {
                        i = PIXEL;
                    }
                    Integer value2 = LinkHandler.getValue(layout, str, i);
                    value = LinkHandler.getValue(containerWrapper.getLayout(), "visual", this.isHor ? LPY : MM);
                    if (value2 == null || value == null) {
                        return 0.0f;
                    }
                    return ((((float) Math.max(PIXEL, value.intValue())) - f) * this.value) + ((float) value2.intValue());
                case MIN_SIZE /*13*/:
                    return componentWrapper == null ? 0.0f : this.isHor ? (float) componentWrapper.getMinimumWidth(componentWrapper.getHeight()) : (float) componentWrapper.getMinimumHeight(componentWrapper.getWidth());
                case PREF_SIZE /*14*/:
                    return componentWrapper == null ? 0.0f : this.isHor ? (float) componentWrapper.getPreferredWidth(componentWrapper.getHeight()) : (float) componentWrapper.getPreferredHeight(componentWrapper.getWidth());
                case MAX_SIZE /*15*/:
                    return componentWrapper == null ? 0.0f : this.isHor ? (float) componentWrapper.getMaximumWidth(componentWrapper.getHeight()) : (float) componentWrapper.getMaximumHeight(componentWrapper.getWidth());
                case BUTTON /*16*/:
                    return (float) PlatformDefaults.getMinimumButtonWidth().getPixels(f, containerWrapper, componentWrapper);
                case LINK_X /*18*/:
                case LINK_Y /*19*/:
                case LINK_W /*20*/:
                case LINK_H /*21*/:
                case LINK_X2 /*22*/:
                case LINK_Y2 /*23*/:
                case LINK_XPOS /*24*/:
                case LINK_YPOS /*25*/:
                    value = LinkHandler.getValue(containerWrapper.getLayout(), getLinkTargetId(), this.unit - (this.unit >= LINK_XPOS ? LINK_XPOS : LINK_X));
                    if (value == null) {
                        return 0.0f;
                    }
                    if (this.unit == LINK_XPOS) {
                        return (float) (value.intValue() + containerWrapper.getScreenLocationX());
                    } else if (this.unit != LINK_YPOS) {
                        return (float) value.intValue();
                    } else {
                        return (float) (value.intValue() + containerWrapper.getScreenLocationY());
                    }
                case LOOKUP /*26*/:
                    f2 = lookup(f, containerWrapper, componentWrapper);
                    if (f2 != -8.7654312E7f) {
                        return f2;
                    }
                    break;
                case LABEL_ALIGN /*27*/:
                    break;
                default:
                    throw new IllegalArgumentException("Unknown/illegal unit: " + this.unit + ", unitStr: " + this.unitStr);
            }
            return PlatformDefaults.getLabelAlignPercentage() * f;
        }
        if (this.subUnits != null && this.subUnits.length == LPY) {
            float pixelsExact = this.subUnits[PIXEL].getPixelsExact(f, containerWrapper, componentWrapper);
            f2 = this.subUnits[LPX].getPixelsExact(f, containerWrapper, componentWrapper);
            switch (this.oper) {
                case ADD /*101*/:
                    return f2 + pixelsExact;
                case SUB /*102*/:
                    return pixelsExact - f2;
                case MUL /*103*/:
                    return f2 * pixelsExact;
                case DIV /*104*/:
                    return pixelsExact / f2;
                case MIN /*105*/:
                    return pixelsExact < f2 ? pixelsExact : f2;
                case MAX /*106*/:
                    if (pixelsExact <= f2) {
                        pixelsExact = f2;
                    }
                    return pixelsExact;
                case MID /*107*/:
                    return (f2 + pixelsExact) * JFreeChart.DEFAULT_BACKGROUND_IMAGE_ALPHA;
            }
        }
        throw new IllegalArgumentException("Internal: Unknown Oper: " + this.oper);
    }

    final int getSubUnitCount() {
        return this.subUnits != null ? this.subUnits.length : PIXEL;
    }

    final UnitValue getSubUnitValue(int i) {
        return this.subUnits[i];
    }

    public final UnitValue[] getSubUnits() {
        return this.subUnits != null ? (UnitValue[]) this.subUnits.clone() : null;
    }

    public final int getUnit() {
        return this.unit;
    }

    public final String getUnitString() {
        return this.unitStr;
    }

    public final float getValue() {
        return this.value;
    }

    public final int hashCode() {
        return ((((int) (this.value * 12345.0f)) + (this.oper >>> INCH)) + this.unit) >>> 17;
    }

    public final boolean isHorizontal() {
        return this.isHor;
    }

    final boolean isLinked() {
        return this.linkId != null;
    }

    final boolean isLinkedDeep() {
        if (this.subUnits == null) {
            return this.linkId != null;
        } else {
            for (int i = PIXEL; i < this.subUnits.length; i += LPX) {
                if (this.subUnits[i].isLinkedDeep()) {
                    return true;
                }
            }
            return false;
        }
    }

    public final String toString() {
        return getClass().getName() + ". Value=" + this.value + ", unit=" + this.unit + ", unitString: " + this.unitStr + ", oper=" + this.oper + ", isHor: " + this.isHor;
    }
}
