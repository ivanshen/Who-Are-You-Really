package net.miginfocom.layout;

import java.util.HashMap;
import org.jfree.chart.plot.MeterPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.util.LogTarget;

public final class PlatformDefaults {
    private static int BASE_DPI = 0;
    private static Integer BASE_DPI_FORCED = null;
    public static final int BASE_FONT_SIZE = 100;
    public static final int BASE_REAL_PIXEL = 102;
    public static final int BASE_SCALE_FACTOR = 101;
    private static String BUTTON_FORMAT = null;
    private static UnitValue BUTT_WIDTH = null;
    private static int CUR_PLAF = 0;
    private static BoundSize DEF_HGAP = null;
    private static int DEF_H_UNIT = 0;
    private static BoundSize DEF_VGAP = null;
    private static int DEF_V_UNIT = 0;
    private static final UnitValue[] DIALOG_INS;
    private static InCellGapProvider GAP_PROVIDER = null;
    public static final int GNOME = 2;
    private static final HashMap<String, UnitValue> HOR_DEFS;
    private static final UnitValue LPX10;
    private static final UnitValue LPX11;
    private static final UnitValue LPX12;
    private static final UnitValue LPX14;
    private static final UnitValue LPX16;
    private static final UnitValue LPX18;
    private static final UnitValue LPX20;
    private static final UnitValue LPX4;
    private static final UnitValue LPX6;
    private static final UnitValue LPX7;
    private static final UnitValue LPX9;
    private static final UnitValue LPY10;
    private static final UnitValue LPY11;
    private static final UnitValue LPY12;
    private static final UnitValue LPY14;
    private static final UnitValue LPY16;
    private static final UnitValue LPY18;
    private static final UnitValue LPY20;
    private static final UnitValue LPY4;
    private static final UnitValue LPY6;
    private static final UnitValue LPY7;
    private static final UnitValue LPY9;
    private static int LP_BASE = 0;
    public static final int MAC_OSX = 1;
    private static volatile int MOD_COUNT;
    private static final UnitValue[] PANEL_INS;
    static BoundSize RELATED_X;
    static BoundSize RELATED_Y;
    static BoundSize UNRELATED_X;
    static BoundSize UNRELATED_Y;
    private static final HashMap<String, UnitValue> VER_DEFS;
    public static final int WINDOWS_XP = 0;
    private static boolean dra;
    private static Float horScale;
    private static Float verScale;

    static {
        DEF_H_UNIT = MAC_OSX;
        DEF_V_UNIT = GNOME;
        GAP_PROVIDER = null;
        MOD_COUNT = 0;
        LPX4 = new UnitValue(4.0f, MAC_OSX, null);
        LPX6 = new UnitValue(6.0f, MAC_OSX, null);
        LPX7 = new UnitValue(7.0f, MAC_OSX, null);
        LPX9 = new UnitValue(9.0f, MAC_OSX, null);
        LPX10 = new UnitValue(MeterPlot.DEFAULT_CIRCLE_SIZE, MAC_OSX, null);
        LPX11 = new UnitValue(11.0f, MAC_OSX, null);
        LPX12 = new UnitValue(12.0f, MAC_OSX, null);
        LPX14 = new UnitValue(14.0f, MAC_OSX, null);
        LPX16 = new UnitValue(16.0f, MAC_OSX, null);
        LPX18 = new UnitValue(18.0f, MAC_OSX, null);
        LPX20 = new UnitValue(20.0f, MAC_OSX, null);
        LPY4 = new UnitValue(4.0f, GNOME, null);
        LPY6 = new UnitValue(6.0f, GNOME, null);
        LPY7 = new UnitValue(7.0f, GNOME, null);
        LPY9 = new UnitValue(9.0f, GNOME, null);
        LPY10 = new UnitValue(MeterPlot.DEFAULT_CIRCLE_SIZE, GNOME, null);
        LPY11 = new UnitValue(11.0f, GNOME, null);
        LPY12 = new UnitValue(12.0f, GNOME, null);
        LPY14 = new UnitValue(14.0f, GNOME, null);
        LPY16 = new UnitValue(16.0f, GNOME, null);
        LPY18 = new UnitValue(18.0f, GNOME, null);
        LPY20 = new UnitValue(20.0f, GNOME, null);
        CUR_PLAF = 0;
        PANEL_INS = new UnitValue[4];
        DIALOG_INS = new UnitValue[4];
        BUTTON_FORMAT = null;
        HOR_DEFS = new HashMap(32);
        VER_DEFS = new HashMap(32);
        DEF_VGAP = null;
        DEF_HGAP = null;
        RELATED_X = null;
        RELATED_Y = null;
        UNRELATED_X = null;
        UNRELATED_Y = null;
        BUTT_WIDTH = null;
        horScale = null;
        verScale = null;
        LP_BASE = BASE_SCALE_FACTOR;
        BASE_DPI_FORCED = null;
        BASE_DPI = 96;
        dra = true;
        setPlatform(getCurrentPlatform());
        MOD_COUNT = 0;
    }

    private PlatformDefaults() {
    }

    static final int convertToPixels(float f, String str, boolean z, float f2, ContainerWrapper containerWrapper, ComponentWrapper componentWrapper) {
        UnitValue unitValue = (UnitValue) (z ? HOR_DEFS : VER_DEFS).get(str);
        return unitValue != null ? Math.round(((float) unitValue.getPixels(f2, containerWrapper, componentWrapper)) * f) : UnitConverter.UNABLE;
    }

    public static final String getButtonOrder() {
        return BUTTON_FORMAT;
    }

    public static int getCurrentPlatform() {
        String property = System.getProperty("os.name");
        return property.startsWith("Mac OS") ? MAC_OSX : property.startsWith("Linux") ? GNOME : 0;
    }

    static BoundSize getDefaultComponentGap(ComponentWrapper componentWrapper, ComponentWrapper componentWrapper2, int i, String str, boolean z) {
        return GAP_PROVIDER != null ? GAP_PROVIDER.getDefaultGap(componentWrapper, componentWrapper2, i, str, z) : componentWrapper2 == null ? null : (i == GNOME || i == 4) ? RELATED_X : RELATED_Y;
    }

    public static int getDefaultDPI() {
        return BASE_DPI;
    }

    public static final int getDefaultHorizontalUnit() {
        return DEF_H_UNIT;
    }

    public static boolean getDefaultRowAlignmentBaseline() {
        return dra;
    }

    public static final int getDefaultVerticalUnit() {
        return DEF_V_UNIT;
    }

    public static UnitValue getDialogInsets(int i) {
        return DIALOG_INS[i];
    }

    public static InCellGapProvider getGapProvider() {
        return GAP_PROVIDER;
    }

    public static BoundSize getGridGapX() {
        return DEF_HGAP;
    }

    public static BoundSize getGridGapY() {
        return DEF_VGAP;
    }

    public static Float getHorizontalScaleFactor() {
        return horScale;
    }

    public static float getLabelAlignPercentage() {
        return CUR_PLAF == MAC_OSX ? Plot.DEFAULT_FOREGROUND_ALPHA : 0.0f;
    }

    public static int getLogicalPixelBase() {
        return LP_BASE;
    }

    public static UnitValue getMinimumButtonWidth() {
        return BUTT_WIDTH;
    }

    public static int getModCount() {
        return MOD_COUNT;
    }

    public static UnitValue getPanelInsets(int i) {
        return PANEL_INS[i];
    }

    public static int getPlatform() {
        return CUR_PLAF;
    }

    private static int getPlatformDPI(int i) {
        switch (i) {
            case LogTarget.ERROR /*0*/:
            case GNOME /*2*/:
                return 96;
            case MAC_OSX /*1*/:
                try {
                    return System.getProperty("java.version").compareTo("1.6") >= 0 ? 96 : 72;
                } catch (Throwable th) {
                    return 72;
                }
            default:
                throw new IllegalArgumentException("Unknown platform: " + i);
        }
    }

    static final String getTagForChar(char c) {
        switch (c) {
            case 'a':
                return "apply";
            case 'b':
                return "back";
            case 'c':
                return "cancel";
            case BASE_SCALE_FACTOR /*101*/:
                return "help2";
            case UnitValue.DIV /*104*/:
                return "help";
            case UnitValue.MIN /*105*/:
                return "finish";
            case 'l':
                return "left";
            case 'n':
                return "no";
            case 'o':
                return "ok";
            case 'r':
                return "right";
            case 'u':
                return "other";
            case 'x':
                return "next";
            case 'y':
                return "yes";
            default:
                return null;
        }
    }

    public static UnitValue getUnitValueX(String str) {
        return (UnitValue) HOR_DEFS.get(str);
    }

    public static UnitValue getUnitValueY(String str) {
        return (UnitValue) VER_DEFS.get(str);
    }

    public static Float getVerticalScaleFactor() {
        return verScale;
    }

    public static final void setButtonOrder(String str) {
        BUTTON_FORMAT = str;
        MOD_COUNT += MAC_OSX;
    }

    public static void setDefaultDPI(Integer num) {
        BASE_DPI = num != null ? num.intValue() : getPlatformDPI(CUR_PLAF);
        BASE_DPI_FORCED = num;
    }

    public static final void setDefaultHorizontalUnit(int i) {
        if (i < 0 || i > 27) {
            throw new IllegalArgumentException("Illegal Unit: " + i);
        } else if (DEF_H_UNIT != i) {
            DEF_H_UNIT = i;
            MOD_COUNT += MAC_OSX;
        }
    }

    public static void setDefaultRowAlignmentBaseline(boolean z) {
        dra = z;
    }

    public static final void setDefaultVerticalUnit(int i) {
        if (i < 0 || i > 27) {
            throw new IllegalArgumentException("Illegal Unit: " + i);
        } else if (DEF_V_UNIT != i) {
            DEF_V_UNIT = i;
            MOD_COUNT += MAC_OSX;
        }
    }

    public static void setDialogInsets(UnitValue unitValue, UnitValue unitValue2, UnitValue unitValue3, UnitValue unitValue4) {
        if (unitValue != null) {
            DIALOG_INS[0] = unitValue;
        }
        if (unitValue2 != null) {
            DIALOG_INS[MAC_OSX] = unitValue2;
        }
        if (unitValue3 != null) {
            DIALOG_INS[GNOME] = unitValue3;
        }
        if (unitValue4 != null) {
            DIALOG_INS[3] = unitValue4;
        }
        MOD_COUNT += MAC_OSX;
    }

    public static void setGapProvider(InCellGapProvider inCellGapProvider) {
        GAP_PROVIDER = inCellGapProvider;
    }

    public static void setGridCellGap(UnitValue unitValue, UnitValue unitValue2) {
        if (unitValue != null) {
            DEF_HGAP = new BoundSize(unitValue, unitValue, null, null);
        }
        if (unitValue2 != null) {
            DEF_VGAP = new BoundSize(unitValue2, unitValue2, null, null);
        }
        MOD_COUNT += MAC_OSX;
    }

    public static void setHorizontalScaleFactor(Float f) {
        if (!LayoutUtil.equals(horScale, f)) {
            horScale = f;
            MOD_COUNT += MAC_OSX;
        }
    }

    public static void setIndentGap(UnitValue unitValue, UnitValue unitValue2) {
        setUnitValue(new String[]{"i", "ind", "indent"}, unitValue, unitValue2);
    }

    public static void setLogicalPixelBase(int i) {
        if (LP_BASE == i) {
            return;
        }
        if (i < BASE_FONT_SIZE || i > BASE_SCALE_FACTOR) {
            throw new IllegalArgumentException("Unrecognized base: " + i);
        }
        LP_BASE = i;
        MOD_COUNT += MAC_OSX;
    }

    public static void setMinimumButtonWidth(UnitValue unitValue) {
        BUTT_WIDTH = unitValue;
        MOD_COUNT += MAC_OSX;
    }

    public static void setPanelInsets(UnitValue unitValue, UnitValue unitValue2, UnitValue unitValue3, UnitValue unitValue4) {
        if (unitValue != null) {
            PANEL_INS[0] = unitValue;
        }
        if (unitValue2 != null) {
            PANEL_INS[MAC_OSX] = unitValue2;
        }
        if (unitValue3 != null) {
            PANEL_INS[GNOME] = unitValue3;
        }
        if (unitValue4 != null) {
            PANEL_INS[3] = unitValue4;
        }
        MOD_COUNT += MAC_OSX;
    }

    public static void setParagraphGap(UnitValue unitValue, UnitValue unitValue2) {
        setUnitValue(new String[]{"p", "para", "paragraph"}, unitValue, unitValue2);
    }

    public static void setPlatform(int i) {
        switch (i) {
            case LogTarget.ERROR /*0*/:
                setRelatedGap(LPX4, LPY4);
                setUnrelatedGap(LPX7, LPY9);
                setParagraphGap(LPX14, LPY14);
                setIndentGap(LPX9, LPY9);
                setGridCellGap(LPX4, LPY4);
                setMinimumButtonWidth(new UnitValue(75.0f, MAC_OSX, null));
                setButtonOrder("L_E+U+YNBXOCAH_R");
                setDialogInsets(LPY11, LPX11, LPY11, LPX11);
                setPanelInsets(LPY7, LPX7, LPY7, LPX7);
                break;
            case MAC_OSX /*1*/:
                setRelatedGap(LPX4, LPY4);
                setUnrelatedGap(LPX7, LPY9);
                setParagraphGap(LPX14, LPY14);
                setIndentGap(LPX10, LPY10);
                setGridCellGap(LPX4, LPY4);
                setMinimumButtonWidth(new UnitValue(68.0f, MAC_OSX, null));
                setButtonOrder("L_HE+U+NYBXCOA_R");
                setDialogInsets(LPY14, LPX20, LPY20, LPX20);
                setPanelInsets(LPY16, LPX16, LPY16, LPX16);
                break;
            case GNOME /*2*/:
                setRelatedGap(LPX6, LPY6);
                setUnrelatedGap(LPX12, LPY12);
                setParagraphGap(LPX18, LPY18);
                setIndentGap(LPX12, LPY12);
                setGridCellGap(LPX6, LPY6);
                setMinimumButtonWidth(new UnitValue(85.0f, MAC_OSX, null));
                setButtonOrder("L_HE+UNYACBXIO_R");
                setDialogInsets(LPY12, LPX12, LPY12, LPX12);
                setPanelInsets(LPY6, LPX6, LPY6, LPX6);
                break;
            default:
                throw new IllegalArgumentException("Unknown platform: " + i);
        }
        CUR_PLAF = i;
        BASE_DPI = BASE_DPI_FORCED != null ? BASE_DPI_FORCED.intValue() : getPlatformDPI(i);
    }

    public static void setRelatedGap(UnitValue unitValue, UnitValue unitValue2) {
        setUnitValue(new String[]{"r", "rel", "related"}, unitValue, unitValue2);
        RELATED_X = new BoundSize(unitValue, unitValue, null, "rel:rel");
        RELATED_Y = new BoundSize(unitValue2, unitValue2, null, "rel:rel");
    }

    public static final void setUnitValue(String[] strArr, UnitValue unitValue, UnitValue unitValue2) {
        for (int i = 0; i < strArr.length; i += MAC_OSX) {
            String trim = strArr[i].toLowerCase().trim();
            if (unitValue != null) {
                HOR_DEFS.put(trim, unitValue);
            }
            if (unitValue2 != null) {
                VER_DEFS.put(trim, unitValue2);
            }
        }
        MOD_COUNT += MAC_OSX;
    }

    public static void setUnrelatedGap(UnitValue unitValue, UnitValue unitValue2) {
        setUnitValue(new String[]{"u", "unrel", "unrelated"}, unitValue, unitValue2);
        UNRELATED_X = new BoundSize(unitValue, unitValue, null, "unrel:unrel");
        UNRELATED_Y = new BoundSize(unitValue2, unitValue2, null, "unrel:unrel");
    }

    public static void setVerticalScaleFactor(Float f) {
        if (!LayoutUtil.equals(verScale, f)) {
            verScale = f;
            MOD_COUNT += MAC_OSX;
        }
    }

    public void invalidate() {
        MOD_COUNT += MAC_OSX;
    }
}
