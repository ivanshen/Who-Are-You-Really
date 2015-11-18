package net.miginfocom.layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;

public final class ConstraintParser {
    private ConstraintParser() {
    }

    private static String[] getNumTextParts(String str) {
        int length = str.length();
        int i = 0;
        while (i < length) {
            char charAt = str.charAt(i);
            if (charAt == ' ') {
                throw new IllegalArgumentException("Space in UnitValue: '" + str + "'");
            } else if ((charAt >= '0' && charAt <= '9') || charAt == '.' || charAt == '-') {
                i++;
            } else {
                return new String[]{str.substring(0, i).trim(), str.substring(i).trim()};
            }
        }
        return new String[]{str, ""};
    }

    private static int getOper(String str) {
        int length = str.length();
        if (length < 3) {
            return 100;
        }
        if (length > 5 && str.charAt(3) == '(' && str.charAt(length - 1) == ')') {
            if (str.startsWith("min(")) {
                return UnitValue.MIN;
            }
            if (str.startsWith("max(")) {
                return UnitValue.MAX;
            }
            if (str.startsWith("mid(")) {
                return UnitValue.MID;
            }
        }
        for (int i = 0; i < 2; i++) {
            int i2 = 0;
            for (int i3 = length - 1; i3 > 0; i3--) {
                char charAt = str.charAt(i3);
                if (charAt == ')') {
                    i2++;
                } else if (charAt == '(') {
                    i2--;
                } else if (i2 != 0) {
                    continue;
                } else if (i == 0) {
                    if (charAt == '+') {
                        return UnitValue.ADD;
                    }
                    if (charAt == '-') {
                        return UnitValue.SUB;
                    }
                } else if (charAt == '*') {
                    return UnitValue.MUL;
                } else {
                    if (charAt == '/') {
                        return UnitValue.DIV;
                    }
                }
            }
        }
        return 100;
    }

    private static final ArrayList<String> getRowColAndGapsTrimmed(String str) {
        int i = 0;
        if (str.indexOf(124) != -1) {
            str = str.replaceAll("\\|", "][");
        }
        ArrayList<String> arrayList = new ArrayList(Math.max(str.length() >> 3, 3));
        int length = str.length();
        int i2 = 0;
        int i3 = 0;
        for (int i4 = 0; i4 < length; i4++) {
            char charAt = str.charAt(i4);
            if (charAt == '[') {
                i3++;
            } else if (charAt == ']') {
                i2++;
            } else {
                continue;
            }
            if (i3 != i2 && i3 - 1 != i2) {
                break;
            }
            arrayList.add(str.substring(i, i4).trim());
            i = i4 + 1;
        }
        if (i3 != i2) {
            throw new IllegalArgumentException("'[' and ']' mismatch in row/column format string: " + str);
        }
        if (i3 == 0) {
            arrayList.add("");
            arrayList.add(str);
            arrayList.add("");
        } else if (arrayList.size() % 2 == 0) {
            arrayList.add(str.substring(i, str.length()));
        }
        return arrayList;
    }

    static UnitValue parseAlignKeywords(String str, boolean z) {
        if (startsWithLenient(str, "center", 1, false) != -1) {
            return UnitValue.CENTER;
        }
        if (z) {
            if (startsWithLenient(str, "left", 1, false) != -1) {
                return UnitValue.LEFT;
            }
            if (startsWithLenient(str, "right", 1, false) != -1) {
                return UnitValue.RIGHT;
            }
            if (startsWithLenient(str, "leading", 4, false) != -1) {
                return UnitValue.LEADING;
            }
            if (startsWithLenient(str, "trailing", 5, false) != -1) {
                return UnitValue.TRAILING;
            }
            if (startsWithLenient(str, "label", 5, false) != -1) {
                return UnitValue.LABEL;
            }
        } else if (startsWithLenient(str, "baseline", 4, false) != -1) {
            return UnitValue.BASELINE_IDENTITY;
        } else {
            if (startsWithLenient(str, "top", 1, false) != -1) {
                return UnitValue.TOP;
            }
            if (startsWithLenient(str, "bottom", 1, false) != -1) {
                return UnitValue.BOTTOM;
            }
        }
        return null;
    }

    private static AC parseAxisConstraint(String str, boolean z) {
        int i = 0;
        String trim = str.trim();
        if (trim.length() == 0) {
            return new AC();
        }
        ArrayList rowColAndGapsTrimmed = getRowColAndGapsTrimmed(trim.toLowerCase());
        BoundSize[] boundSizeArr = new BoundSize[((rowColAndGapsTrimmed.size() >> 1) + 1)];
        int size = rowColAndGapsTrimmed.size();
        int i2 = 0;
        int i3 = 0;
        while (i3 < size) {
            boundSizeArr[i2] = parseBoundSize((String) rowColAndGapsTrimmed.get(i3), true, z);
            i3 += 2;
            i2++;
        }
        DimConstraint[] dimConstraintArr = new DimConstraint[(rowColAndGapsTrimmed.size() >> 1)];
        int i4 = 0;
        while (i < dimConstraintArr.length) {
            i2 = i4 >= boundSizeArr.length + -1 ? boundSizeArr.length - 2 : i4;
            dimConstraintArr[i] = parseDimConstraint((String) rowColAndGapsTrimmed.get((i << 1) + 1), boundSizeArr[i2], boundSizeArr[i2 + 1], z);
            i++;
            i4 = i2 + 1;
        }
        AC ac = new AC();
        ac.setConstaints(dimConstraintArr);
        return ac;
    }

    public static BoundSize parseBoundSize(String str, boolean z, boolean z2) {
        if (str.length() == 0 || str.equals("null") || str.equals("n")) {
            return null;
        }
        String substring;
        boolean z3;
        if (str.endsWith("push")) {
            substring = str.substring(0, str.length() - (str.endsWith(":push") ? 5 : 4));
            if (substring.length() == 0) {
                return new BoundSize(null, null, null, true, str);
            }
            z3 = true;
        } else {
            z3 = false;
            substring = str;
        }
        String[] toTrimmedTokens = toTrimmedTokens(substring, ':');
        substring = toTrimmedTokens[0];
        if (toTrimmedTokens.length == 1) {
            boolean endsWith = substring.endsWith("!");
            if (endsWith) {
                substring = substring.substring(0, substring.length() - 1);
            }
            UnitValue parseUnitValue = parseUnitValue(substring, null, z2);
            UnitValue unitValue = (z || endsWith) ? parseUnitValue : null;
            return new BoundSize(unitValue, parseUnitValue, endsWith ? parseUnitValue : null, z3, str);
        } else if (toTrimmedTokens.length == 2) {
            return new BoundSize(parseUnitValue(substring, null, z2), parseUnitValue(toTrimmedTokens[1], null, z2), null, z3, str);
        } else {
            if (toTrimmedTokens.length == 3) {
                return new BoundSize(parseUnitValue(substring, null, z2), parseUnitValue(toTrimmedTokens[1], null, z2), parseUnitValue(toTrimmedTokens[2], null, z2), z3, str);
            }
            throw new IllegalArgumentException("Min:Preferred:Max size section must contain 0, 1 or 2 colons. '" + str + "'");
        }
    }

    public static AC parseColumnConstraints(String str) {
        return parseAxisConstraint(str, true);
    }

    public static CC parseComponentConstraint(String str) {
        CC cc = new CC();
        if (str.length() == 0) {
            return cc;
        }
        String[] toTrimmedTokens = toTrimmedTokens(str, ',');
        for (String str2 : toTrimmedTokens) {
            if (str2.length() != 0) {
                char charAt = str2.charAt(0);
                if (charAt == 'n') {
                    if (str2.equals("north")) {
                        cc.setDockSide(0);
                    } else {
                        try {
                            if (str2.equals("newline")) {
                                cc.setNewline(true);
                            } else if (str2.startsWith("newline ")) {
                                cc.setNewlineGapSize(parseBoundSize(str2.substring(7).trim(), true, true));
                            }
                        } catch (Exception e) {
                            throw new IllegalArgumentException("Illegal Constraint: '" + str2 + "'\n" + e.getMessage());
                        }
                    }
                }
                if (charAt == 'f' && (str2.equals("flowy") || str2.equals("flowx"))) {
                    cc.setFlowX(str2.charAt(4) == 'x' ? Boolean.TRUE : Boolean.FALSE);
                } else {
                    int startsWithLenient;
                    String trim;
                    String[] toTrimmedTokens2;
                    char charAt2;
                    int i;
                    UnitValue[] pos;
                    UnitValue parseUnitValue;
                    if (charAt == 's') {
                        startsWithLenient = startsWithLenient(str2, "skip", 4, true);
                        String trim2;
                        if (startsWithLenient > -1) {
                            trim2 = str2.substring(startsWithLenient).trim();
                            cc.setSkip(trim2.length() != 0 ? Integer.parseInt(trim2) : 1);
                        } else {
                            startsWithLenient = startsWithLenient(str2, "split", 5, true);
                            if (startsWithLenient > -1) {
                                trim2 = str2.substring(startsWithLenient).trim();
                                cc.setSplit(trim2.length() > 0 ? Integer.parseInt(trim2) : 2097051);
                            } else if (str2.equals("south")) {
                                cc.setDockSide(2);
                            } else {
                                startsWithLenient = startsWithLenient(str2, new String[]{"spany", "sy"}, new int[]{5, 2}, true);
                                if (startsWithLenient > -1) {
                                    cc.setSpanY(parseSpan(str2.substring(startsWithLenient).trim()));
                                } else {
                                    startsWithLenient = startsWithLenient(str2, new String[]{"spanx", "sx"}, new int[]{5, 2}, true);
                                    if (startsWithLenient > -1) {
                                        cc.setSpanX(parseSpan(str2.substring(startsWithLenient).trim()));
                                    } else {
                                        startsWithLenient = startsWithLenient(str2, "span", 4, true);
                                        String[] toTrimmedTokens3;
                                        if (startsWithLenient > -1) {
                                            toTrimmedTokens3 = toTrimmedTokens(str2.substring(startsWithLenient).trim(), ' ');
                                            cc.setSpanX(toTrimmedTokens3[0].length() > 0 ? Integer.parseInt(toTrimmedTokens3[0]) : 2097051);
                                            cc.setSpanY(toTrimmedTokens3.length > 1 ? Integer.parseInt(toTrimmedTokens3[1]) : 1);
                                        } else {
                                            startsWithLenient = startsWithLenient(str2, "shrinkx", 7, true);
                                            if (startsWithLenient > -1) {
                                                cc.getHorizontal().setShrink(parseFloat(str2.substring(startsWithLenient).trim(), ResizeConstraint.WEIGHT_100));
                                            } else {
                                                startsWithLenient = startsWithLenient(str2, "shrinky", 7, true);
                                                if (startsWithLenient > -1) {
                                                    cc.getVertical().setShrink(parseFloat(str2.substring(startsWithLenient).trim(), ResizeConstraint.WEIGHT_100));
                                                } else {
                                                    startsWithLenient = startsWithLenient(str2, "shrink", 6, false);
                                                    if (startsWithLenient > -1) {
                                                        toTrimmedTokens3 = toTrimmedTokens(str2.substring(startsWithLenient).trim(), ' ');
                                                        cc.getHorizontal().setShrink(parseFloat(str2.substring(startsWithLenient).trim(), ResizeConstraint.WEIGHT_100));
                                                        if (toTrimmedTokens3.length > 1) {
                                                            cc.getVertical().setShrink(parseFloat(str2.substring(startsWithLenient).trim(), ResizeConstraint.WEIGHT_100));
                                                        }
                                                    } else {
                                                        startsWithLenient = startsWithLenient(str2, new String[]{"shrinkprio", "shp"}, new int[]{10, 3}, true);
                                                        if (startsWithLenient > -1) {
                                                            trim = str2.substring(startsWithLenient).trim();
                                                            if (trim.startsWith("x") || trim.startsWith("y")) {
                                                                (trim.startsWith("x") ? cc.getHorizontal() : cc.getVertical()).setShrinkPriority(Integer.parseInt(trim.substring(2)));
                                                            } else {
                                                                toTrimmedTokens2 = toTrimmedTokens(trim, ' ');
                                                                cc.getHorizontal().setShrinkPriority(Integer.parseInt(toTrimmedTokens2[0]));
                                                                if (toTrimmedTokens2.length > 1) {
                                                                    cc.getVertical().setShrinkPriority(Integer.parseInt(toTrimmedTokens2[1]));
                                                                }
                                                            }
                                                        } else {
                                                            startsWithLenient = startsWithLenient(str2, new String[]{"sizegroupx", "sizegroupy", "sgx", "sgy"}, new int[]{9, 9, 2, 2}, true);
                                                            if (startsWithLenient > -1) {
                                                                trim = str2.substring(startsWithLenient).trim();
                                                                charAt2 = str2.charAt(startsWithLenient - 1);
                                                                if (charAt2 != 'y') {
                                                                    cc.getHorizontal().setSizeGroup(trim);
                                                                }
                                                                if (charAt2 != 'x') {
                                                                    cc.getVertical().setSizeGroup(trim);
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (charAt == 'g') {
                        startsWithLenient = startsWithLenient(str2, "growx", 5, true);
                        if (startsWithLenient > -1) {
                            cc.getHorizontal().setGrow(parseFloat(str2.substring(startsWithLenient).trim(), ResizeConstraint.WEIGHT_100));
                        } else {
                            startsWithLenient = startsWithLenient(str2, "growy", 5, true);
                            if (startsWithLenient > -1) {
                                cc.getVertical().setGrow(parseFloat(str2.substring(startsWithLenient).trim(), ResizeConstraint.WEIGHT_100));
                            } else {
                                startsWithLenient = startsWithLenient(str2, "grow", 4, false);
                                if (startsWithLenient > -1) {
                                    toTrimmedTokens2 = toTrimmedTokens(str2.substring(startsWithLenient).trim(), ' ');
                                    cc.getHorizontal().setGrow(parseFloat(toTrimmedTokens2[0], ResizeConstraint.WEIGHT_100));
                                    cc.getVertical().setGrow(parseFloat(toTrimmedTokens2.length > 1 ? toTrimmedTokens2[1] : "", ResizeConstraint.WEIGHT_100));
                                } else {
                                    startsWithLenient = startsWithLenient(str2, new String[]{"growprio", "gp"}, new int[]{8, 2}, true);
                                    if (startsWithLenient > -1) {
                                        trim = str2.substring(startsWithLenient).trim();
                                        charAt2 = trim.length() > 0 ? trim.charAt(0) : ' ';
                                        if (charAt2 == 'x' || charAt2 == 'y') {
                                            (charAt2 == 'x' ? cc.getHorizontal() : cc.getVertical()).setGrowPriority(Integer.parseInt(trim.substring(2)));
                                        } else {
                                            toTrimmedTokens2 = toTrimmedTokens(trim, ' ');
                                            cc.getHorizontal().setGrowPriority(Integer.parseInt(toTrimmedTokens2[0]));
                                            if (toTrimmedTokens2.length > 1) {
                                                cc.getVertical().setGrowPriority(Integer.parseInt(toTrimmedTokens2[1]));
                                            }
                                        }
                                    } else if (str2.startsWith("gap")) {
                                        BoundSize[] parseGaps = parseGaps(str2);
                                        if (parseGaps[0] != null) {
                                            cc.getVertical().setGapBefore(parseGaps[0]);
                                        }
                                        if (parseGaps[1] != null) {
                                            cc.getHorizontal().setGapBefore(parseGaps[1]);
                                        }
                                        if (parseGaps[2] != null) {
                                            cc.getVertical().setGapAfter(parseGaps[2]);
                                        }
                                        if (parseGaps[3] != null) {
                                            cc.getHorizontal().setGapAfter(parseGaps[3]);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (charAt == 'a') {
                        startsWithLenient = startsWithLenient(str2, new String[]{"aligny", "ay"}, new int[]{6, 2}, true);
                        if (startsWithLenient > -1) {
                            cc.getVertical().setAlign(parseUnitValueOrAlign(str2.substring(startsWithLenient).trim(), false, null));
                        } else {
                            startsWithLenient = startsWithLenient(str2, new String[]{"alignx", "ax"}, new int[]{6, 2}, true);
                            if (startsWithLenient > -1) {
                                cc.getHorizontal().setAlign(parseUnitValueOrAlign(str2.substring(startsWithLenient).trim(), true, null));
                            } else {
                                startsWithLenient = startsWithLenient(str2, "align", 2, true);
                                if (startsWithLenient > -1) {
                                    toTrimmedTokens2 = toTrimmedTokens(str2.substring(startsWithLenient).trim(), ' ');
                                    cc.getHorizontal().setAlign(parseUnitValueOrAlign(toTrimmedTokens2[0], true, null));
                                    if (toTrimmedTokens2.length > 1) {
                                        cc.getVertical().setAlign(parseUnitValueOrAlign(toTrimmedTokens2[1], false, null));
                                    }
                                }
                            }
                        }
                    }
                    if ((charAt == 'x' || charAt == 'y') && str2.length() > 2) {
                        char charAt3 = str2.charAt(1);
                        if (charAt3 == ' ' || (charAt3 == '2' && str2.charAt(2) == ' ')) {
                            if (cc.getPos() == null) {
                                cc.setPos(new UnitValue[4]);
                            } else if (!cc.isBoundsInGrid()) {
                                throw new IllegalArgumentException("Cannot combine 'position' with 'x/y/x2/y2' keywords.");
                            }
                            i = (charAt == 'x' ? 0 : 1) + (charAt3 == '2' ? 2 : 0);
                            pos = cc.getPos();
                            pos[i] = parseUnitValue(str2.substring(2).trim(), null, charAt == 'x');
                            cc.setPos(pos);
                            cc.setBoundsInGrid(true);
                        }
                    }
                    if (charAt == 'c') {
                        startsWithLenient = startsWithLenient(str2, "cell", 4, true);
                        if (startsWithLenient > -1) {
                            toTrimmedTokens2 = toTrimmedTokens(str2.substring(startsWithLenient).trim(), ' ');
                            if (toTrimmedTokens2.length < 2) {
                                throw new IllegalArgumentException("At least two integers must follow " + str2);
                            }
                            cc.setCellX(Integer.parseInt(toTrimmedTokens2[0]));
                            cc.setCellY(Integer.parseInt(toTrimmedTokens2[1]));
                            if (toTrimmedTokens2.length > 2) {
                                cc.setSpanX(Integer.parseInt(toTrimmedTokens2[2]));
                            }
                            if (toTrimmedTokens2.length > 3) {
                                cc.setSpanY(Integer.parseInt(toTrimmedTokens2[3]));
                            }
                        }
                    }
                    if (charAt == 'p') {
                        startsWithLenient = startsWithLenient(str2, "pos", 3, true);
                        if (startsWithLenient <= -1) {
                            startsWithLenient = startsWithLenient(str2, "pad", 3, true);
                            if (startsWithLenient > -1) {
                                UnitValue[] parseInsets = parseInsets(str2.substring(startsWithLenient).trim(), false);
                                pos = new UnitValue[4];
                                pos[0] = parseInsets[0];
                                pos[1] = parseInsets.length > 1 ? parseInsets[1] : null;
                                pos[2] = parseInsets.length > 2 ? parseInsets[2] : null;
                                pos[3] = parseInsets.length > 3 ? parseInsets[3] : null;
                                cc.setPadding(pos);
                            } else {
                                startsWithLenient = startsWithLenient(str2, "pushx", 5, true);
                                if (startsWithLenient > -1) {
                                    cc.setPushX(parseFloat(str2.substring(startsWithLenient).trim(), ResizeConstraint.WEIGHT_100));
                                } else {
                                    startsWithLenient = startsWithLenient(str2, "pushy", 5, true);
                                    if (startsWithLenient > -1) {
                                        cc.setPushY(parseFloat(str2.substring(startsWithLenient).trim(), ResizeConstraint.WEIGHT_100));
                                    } else {
                                        startsWithLenient = startsWithLenient(str2, "push", 4, false);
                                        if (startsWithLenient > -1) {
                                            toTrimmedTokens2 = toTrimmedTokens(str2.substring(startsWithLenient).trim(), ' ');
                                            cc.setPushX(parseFloat(toTrimmedTokens2[0], ResizeConstraint.WEIGHT_100));
                                            cc.setPushY(parseFloat(toTrimmedTokens2.length > 1 ? toTrimmedTokens2[1] : "", ResizeConstraint.WEIGHT_100));
                                        }
                                    }
                                }
                            }
                        } else if (cc.getPos() == null || !cc.isBoundsInGrid()) {
                            String[] toTrimmedTokens4 = toTrimmedTokens(str2.substring(startsWithLenient).trim(), ' ');
                            UnitValue[] unitValueArr = new UnitValue[4];
                            for (i = 0; i < toTrimmedTokens4.length; i++) {
                                unitValueArr[i] = parseUnitValue(toTrimmedTokens4[i], null, i % 2 == 0);
                            }
                            if ((unitValueArr[0] == null && unitValueArr[2] == null) || (unitValueArr[1] == null && unitValueArr[3] == null)) {
                                throw new IllegalArgumentException("Both x and x2 or y and y2 can not be null!");
                            }
                            cc.setPos(unitValueArr);
                            cc.setBoundsInGrid(false);
                        } else {
                            throw new IllegalArgumentException("Can not combine 'pos' with 'x/y/x2/y2' keywords.");
                        }
                    }
                    if (charAt == 't') {
                        startsWithLenient = startsWithLenient(str2, "tag", 3, true);
                        if (startsWithLenient > -1) {
                            cc.setTag(str2.substring(startsWithLenient).trim());
                        }
                    }
                    if (charAt == 'w' || charAt == 'h') {
                        if (str2.equals("wrap")) {
                            cc.setWrap(true);
                        } else if (str2.startsWith("wrap ")) {
                            cc.setWrapGapSize(parseBoundSize(str2.substring(5).trim(), true, true));
                        } else {
                            boolean z = charAt == 'w';
                            if (z && (str2.startsWith("w ") || str2.startsWith("width "))) {
                                cc.getHorizontal().setSize(parseBoundSize(str2.substring(str2.charAt(1) == ' ' ? 2 : 6).trim(), false, true));
                            } else if (z || !(str2.startsWith("h ") || str2.startsWith("height "))) {
                                if (str2.startsWith("wmin ") || str2.startsWith("wmax ") || str2.startsWith("hmin ") || str2.startsWith("hmax ")) {
                                    String trim3 = str2.substring(5).trim();
                                    if (trim3.length() > 0) {
                                        parseUnitValue = parseUnitValue(trim3, null, z);
                                        Object obj = str2.charAt(3) == 'n' ? 1 : null;
                                        DimConstraint horizontal = z ? cc.getHorizontal() : cc.getVertical();
                                        UnitValue min = obj != null ? parseUnitValue : horizontal.getSize().getMin();
                                        UnitValue preferred = horizontal.getSize().getPreferred();
                                        if (obj != null) {
                                            parseUnitValue = horizontal.getSize().getMax();
                                        }
                                        horizontal.setSize(new BoundSize(min, preferred, parseUnitValue, trim3));
                                    }
                                }
                                if (str2.equals("west")) {
                                    cc.setDockSide(1);
                                } else if (str2.startsWith("hidemode ")) {
                                    cc.setHideMode(Integer.parseInt(str2.substring(9)));
                                }
                            } else {
                                cc.getVertical().setSize(parseBoundSize(str2.substring(str2.charAt(1) == ' ' ? 2 : 7).trim(), false, false));
                            }
                        }
                    }
                    if (charAt == 'i' && str2.startsWith("id ")) {
                        cc.setId(str2.substring(3).trim());
                        startsWithLenient = cc.getId().indexOf(46);
                        if (startsWithLenient == 0 || startsWithLenient == cc.getId().length() - 1) {
                            throw new IllegalArgumentException("Dot must not be first or last!");
                        }
                    } else {
                        if (charAt == 'e') {
                            if (str2.equals("east")) {
                                cc.setDockSide(3);
                            } else if (str2.equals("external")) {
                                cc.setExternal(true);
                            } else {
                                startsWithLenient = startsWithLenient(str2, new String[]{"endgroupx", "endgroupy", "egx", "egy"}, new int[]{-1, -1, -1, -1}, true);
                                if (startsWithLenient > -1) {
                                    (str2.charAt(startsWithLenient + -1) == 'x' ? cc.getHorizontal() : cc.getVertical()).setEndGroup(str2.substring(startsWithLenient).trim());
                                }
                            }
                        }
                        if (charAt == 'd') {
                            if (str2.equals("dock north")) {
                                cc.setDockSide(0);
                            } else if (str2.equals("dock west")) {
                                cc.setDockSide(1);
                            } else if (str2.equals("dock south")) {
                                cc.setDockSide(2);
                            } else if (str2.equals("dock east")) {
                                cc.setDockSide(3);
                            } else if (str2.equals("dock center")) {
                                cc.getHorizontal().setGrow(new Float(100.0f));
                                cc.getVertical().setGrow(new Float(100.0f));
                                cc.setPushX(new Float(100.0f));
                                cc.setPushY(new Float(100.0f));
                            }
                        }
                        parseUnitValue = parseAlignKeywords(str2, true);
                        if (parseUnitValue != null) {
                            cc.getHorizontal().setAlign(parseUnitValue);
                        } else {
                            parseUnitValue = parseAlignKeywords(str2, false);
                            if (parseUnitValue != null) {
                                cc.getVertical().setAlign(parseUnitValue);
                            } else {
                                throw new IllegalArgumentException("Unknown keyword.");
                            }
                        }
                    }
                }
            }
        }
        return cc;
    }

    public static Map<ComponentWrapper, CC> parseComponentConstraints(Map<ComponentWrapper, String> map) {
        Map hashMap = new HashMap();
        for (Entry entry : map.entrySet()) {
            hashMap.put(entry.getKey(), parseComponentConstraint((String) entry.getValue()));
        }
        return hashMap;
    }

    private static DimConstraint parseDimConstraint(String str, BoundSize boundSize, BoundSize boundSize2, boolean z) {
        DimConstraint dimConstraint = new DimConstraint();
        dimConstraint.setGapBefore(boundSize);
        dimConstraint.setGapAfter(boundSize2);
        String[] toTrimmedTokens = toTrimmedTokens(str, ',');
        for (String str2 : toTrimmedTokens) {
            if (str2.length() != 0) {
                if (str2.equals("fill")) {
                    dimConstraint.setFill(true);
                } else {
                    try {
                        if (str2.equals("nogrid")) {
                            dimConstraint.setNoGrid(true);
                        } else {
                            int startsWithLenient;
                            char charAt = str2.charAt(0);
                            if (charAt == 's') {
                                startsWithLenient = startsWithLenient(str2, new String[]{"sizegroup", "sg"}, new int[]{5, 2}, true);
                                if (startsWithLenient > -1) {
                                    dimConstraint.setSizeGroup(str2.substring(startsWithLenient).trim());
                                } else {
                                    startsWithLenient = startsWithLenient(str2, new String[]{"shrinkprio", "shp"}, new int[]{10, 3}, true);
                                    if (startsWithLenient > -1) {
                                        dimConstraint.setShrinkPriority(Integer.parseInt(str2.substring(startsWithLenient).trim()));
                                    } else {
                                        startsWithLenient = startsWithLenient(str2, "shrink", 6, true);
                                        if (startsWithLenient > -1) {
                                            dimConstraint.setShrink(parseFloat(str2.substring(startsWithLenient).trim(), ResizeConstraint.WEIGHT_100));
                                        }
                                    }
                                }
                            }
                            if (charAt == 'g') {
                                startsWithLenient = startsWithLenient(str2, new String[]{"growpriority", "gp"}, new int[]{5, 2}, true);
                                if (startsWithLenient > -1) {
                                    dimConstraint.setGrowPriority(Integer.parseInt(str2.substring(startsWithLenient).trim()));
                                } else {
                                    startsWithLenient = startsWithLenient(str2, "grow", 4, true);
                                    if (startsWithLenient > -1) {
                                        dimConstraint.setGrow(parseFloat(str2.substring(startsWithLenient).trim(), ResizeConstraint.WEIGHT_100));
                                    }
                                }
                            }
                            if (charAt == 'a') {
                                int startsWithLenient2 = startsWithLenient(str2, "align", 2, true);
                                if (startsWithLenient2 > -1) {
                                    dimConstraint.setAlign(parseUnitValueOrAlign(str2.substring(startsWithLenient2).trim(), z, null));
                                }
                            }
                            UnitValue parseAlignKeywords = parseAlignKeywords(str2, z);
                            if (parseAlignKeywords != null) {
                                dimConstraint.setAlign(parseAlignKeywords);
                            } else {
                                dimConstraint.setSize(parseBoundSize(str2, false, z));
                            }
                        }
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Illegal contraint: '" + str2 + "'\n" + e.getMessage());
                    }
                }
            }
        }
        return dimConstraint;
    }

    private static Float parseFloat(String str, Float f) {
        return str.length() > 0 ? new Float(Float.parseFloat(str)) : f;
    }

    private static BoundSize[] parseGaps(String str) {
        boolean z = false;
        BoundSize[] boundSizeArr = new BoundSize[4];
        int startsWithLenient = startsWithLenient(str, "gaptop", -1, true);
        if (startsWithLenient > -1) {
            boundSizeArr[0] = parseBoundSize(str.substring(startsWithLenient).trim(), true, false);
            return boundSizeArr;
        }
        startsWithLenient = startsWithLenient(str, "gapleft", -1, true);
        if (startsWithLenient > -1) {
            boundSizeArr[1] = parseBoundSize(str.substring(startsWithLenient).trim(), true, true);
            return boundSizeArr;
        }
        startsWithLenient = startsWithLenient(str, "gapbottom", -1, true);
        if (startsWithLenient > -1) {
            boundSizeArr[2] = parseBoundSize(str.substring(startsWithLenient).trim(), true, false);
            return boundSizeArr;
        }
        startsWithLenient = startsWithLenient(str, "gapright", -1, true);
        if (startsWithLenient > -1) {
            boundSizeArr[3] = parseBoundSize(str.substring(startsWithLenient).trim(), true, true);
            return boundSizeArr;
        }
        startsWithLenient = startsWithLenient(str, "gapbefore", -1, true);
        if (startsWithLenient > -1) {
            boundSizeArr[1] = parseBoundSize(str.substring(startsWithLenient).trim(), true, true);
            return boundSizeArr;
        }
        startsWithLenient = startsWithLenient(str, "gapafter", -1, true);
        if (startsWithLenient > -1) {
            boundSizeArr[3] = parseBoundSize(str.substring(startsWithLenient).trim(), true, true);
            return boundSizeArr;
        }
        int startsWithLenient2 = startsWithLenient(str, new String[]{"gapx", "gapy"}, null, true);
        if (startsWithLenient2 > -1) {
            boolean z2 = str.charAt(3) == 'x';
            String[] toTrimmedTokens = toTrimmedTokens(str.substring(startsWithLenient2).trim(), ' ');
            boundSizeArr[z2 ? 1 : 0] = parseBoundSize(toTrimmedTokens[0], true, z2);
            if (toTrimmedTokens.length > 1) {
                startsWithLenient2 = z2 ? 3 : 2;
                String str2 = toTrimmedTokens[1];
                if (!z2) {
                    z = true;
                }
                boundSizeArr[startsWithLenient2] = parseBoundSize(str2, true, z);
            }
            return boundSizeArr;
        }
        startsWithLenient = startsWithLenient(str, "gap ", 1, true);
        if (startsWithLenient > -1) {
            String[] toTrimmedTokens2 = toTrimmedTokens(str.substring(startsWithLenient).trim(), ' ');
            boundSizeArr[1] = parseBoundSize(toTrimmedTokens2[0], true, true);
            if (toTrimmedTokens2.length > 1) {
                boundSizeArr[3] = parseBoundSize(toTrimmedTokens2[1], true, false);
                if (toTrimmedTokens2.length > 2) {
                    boundSizeArr[0] = parseBoundSize(toTrimmedTokens2[2], true, true);
                    if (toTrimmedTokens2.length > 3) {
                        boundSizeArr[2] = parseBoundSize(toTrimmedTokens2[3], true, false);
                    }
                }
            }
            return boundSizeArr;
        }
        throw new IllegalArgumentException("Unknown Gap part: '" + str + "'");
    }

    public static UnitValue[] parseInsets(String str, boolean z) {
        int i = 0;
        if (str.length() != 0 && !str.equals("dialog") && !str.equals("panel")) {
            String[] toTrimmedTokens = toTrimmedTokens(str, ' ');
            UnitValue[] unitValueArr = new UnitValue[4];
            int i2 = 0;
            while (i2 < 4) {
                UnitValue parseUnitValue = parseUnitValue(toTrimmedTokens[i2 < toTrimmedTokens.length ? i2 : toTrimmedTokens.length - 1], UnitValue.ZERO, i2 % 2 == 1);
                if (parseUnitValue == null) {
                    parseUnitValue = PlatformDefaults.getPanelInsets(i2);
                }
                unitValueArr[i2] = parseUnitValue;
                i2++;
            }
            return unitValueArr;
        } else if (z) {
            boolean startsWith = str.startsWith("p");
            UnitValue[] unitValueArr2 = new UnitValue[4];
            while (i < 4) {
                unitValueArr2[i] = startsWith ? PlatformDefaults.getPanelInsets(i) : PlatformDefaults.getDialogInsets(i);
                i++;
            }
            return unitValueArr2;
        } else {
            throw new IllegalAccessError("Insets now allowed: " + str + "\n");
        }
    }

    public static LC parseLayoutConstraint(String str) {
        LC lc = new LC();
        if (str.length() == 0) {
            return lc;
        }
        int length;
        String[] toTrimmedTokens = toTrimmedTokens(str, ',');
        for (int i = 0; i < toTrimmedTokens.length; i++) {
            String str2 = toTrimmedTokens[i];
            if (str2 != null) {
                length = str2.length();
                if (length == 3 || length == 11) {
                    if (str2.equals("ltr") || str2.equals("rtl") || str2.equals("lefttoright") || str2.equals("righttoleft")) {
                        lc.setLeftToRight(str2.charAt(0) == 'l' ? Boolean.TRUE : Boolean.FALSE);
                        toTrimmedTokens[i] = null;
                    }
                    if (str2.equals("ttb") || str2.equals("btt") || str2.equals("toptobottom") || str2.equals("bottomtotop")) {
                        lc.setTopToBottom(str2.charAt(0) == 't');
                        toTrimmedTokens[i] = null;
                    }
                }
            }
        }
        for (String str22 : toTrimmedTokens) {
            if (!(str22 == null || str22.length() == 0)) {
                String trim;
                String[] toTrimmedTokens2;
                UnitValue parseUnitValueOrAlign;
                char charAt = str22.charAt(0);
                if (charAt == 'w' || charAt == 'h') {
                    length = startsWithLenient(str22, "wrap", -1, true);
                    if (length > -1) {
                        trim = str22.substring(length).trim();
                        lc.setWrapAfter(trim.length() != 0 ? Integer.parseInt(trim) : 0);
                    } else {
                        length = charAt == 'w' ? 1 : 0;
                        if (length != 0) {
                            try {
                                if (str22.startsWith("w ") || str22.startsWith("width ")) {
                                    lc.setWidth(parseBoundSize(str22.substring(str22.charAt(1) == ' ' ? 2 : 6).trim(), false, true));
                                }
                            } catch (Exception e) {
                                throw new IllegalArgumentException("Illegal Constraint: '" + str22 + "'\n" + e.getMessage());
                            }
                        }
                        if (length == 0 && (str22.startsWith("h ") || str22.startsWith("height "))) {
                            lc.setHeight(parseBoundSize(str22.substring(str22.charAt(1) == ' ' ? 2 : 7).trim(), false, false));
                        } else {
                            if (str22.length() > 5) {
                                trim = str22.substring(5).trim();
                                if (str22.startsWith("wmin ")) {
                                    lc.minWidth(trim);
                                } else if (str22.startsWith("wmax ")) {
                                    lc.maxWidth(trim);
                                } else if (str22.startsWith("hmin ")) {
                                    lc.minHeight(trim);
                                } else if (str22.startsWith("hmax ")) {
                                    lc.maxHeight(trim);
                                }
                            }
                            if (str22.startsWith("hidemode ")) {
                                lc.setHideMode(Integer.parseInt(str22.substring(9)));
                            }
                        }
                    }
                }
                if (charAt == 'g') {
                    if (str22.startsWith("gapx ")) {
                        lc.setGridGapX(parseBoundSize(str22.substring(5).trim(), true, true));
                    } else if (str22.startsWith("gapy ")) {
                        lc.setGridGapY(parseBoundSize(str22.substring(5).trim(), true, false));
                    } else if (str22.startsWith("gap ")) {
                        toTrimmedTokens2 = toTrimmedTokens(str22.substring(4).trim(), ' ');
                        lc.setGridGapX(parseBoundSize(toTrimmedTokens2[0], true, true));
                        lc.setGridGapY(toTrimmedTokens2.length > 1 ? parseBoundSize(toTrimmedTokens2[1], true, false) : lc.getGridGapX());
                    }
                }
                if (charAt == 'd') {
                    length = startsWithLenient(str22, "debug", 5, true);
                    if (length > -1) {
                        trim = str22.substring(length).trim();
                        lc.setDebugMillis(trim.length() > 0 ? Integer.parseInt(trim) : 1000);
                    }
                }
                if (charAt == 'n') {
                    if (str22.equals("nogrid")) {
                        lc.setNoGrid(true);
                    } else if (str22.equals("nocache")) {
                        lc.setNoCache(true);
                    } else if (str22.equals("novisualpadding")) {
                        lc.setVisualPadding(false);
                    }
                }
                if (charAt == 'f') {
                    if (str22.equals("fill") || str22.equals("fillx") || str22.equals("filly")) {
                        boolean z = str22.length() == 4 || str22.charAt(4) == 'x';
                        lc.setFillX(z);
                        z = str22.length() == 4 || str22.charAt(4) == 'y';
                        lc.setFillY(z);
                    } else if (str22.equals("flowy")) {
                        lc.setFlowX(false);
                    } else if (str22.equals("flowx")) {
                        lc.setFlowX(true);
                    }
                }
                if (charAt == 'i') {
                    length = startsWithLenient(str22, "insets", 3, true);
                    if (length > -1) {
                        trim = str22.substring(length).trim();
                        Object parseInsets = parseInsets(trim, true);
                        LayoutUtil.putCCString(parseInsets, trim);
                        lc.setInsets(parseInsets);
                    }
                }
                if (charAt == 'a') {
                    length = startsWithLenient(str22, new String[]{"aligny", "ay"}, new int[]{6, 2}, true);
                    if (length > -1) {
                        parseUnitValueOrAlign = parseUnitValueOrAlign(str22.substring(length).trim(), false, null);
                        if (parseUnitValueOrAlign == UnitValue.BASELINE_IDENTITY) {
                            throw new IllegalArgumentException("'baseline' can not be used to align the whole component group.");
                        }
                        lc.setAlignY(parseUnitValueOrAlign);
                    } else {
                        length = startsWithLenient(str22, new String[]{"alignx", "ax"}, new int[]{6, 2}, true);
                        if (length > -1) {
                            lc.setAlignX(parseUnitValueOrAlign(str22.substring(length).trim(), true, null));
                        } else {
                            length = startsWithLenient(str22, "align", 2, true);
                            if (length > -1) {
                                toTrimmedTokens2 = toTrimmedTokens(str22.substring(length).trim(), ' ');
                                lc.setAlignX(parseUnitValueOrAlign(toTrimmedTokens2[0], true, null));
                                lc.setAlignY(toTrimmedTokens2.length > 1 ? parseUnitValueOrAlign(toTrimmedTokens2[1], false, null) : lc.getAlignX());
                            }
                        }
                    }
                }
                if (charAt == 'p') {
                    if (str22.startsWith("packalign ")) {
                        String[] toTrimmedTokens3 = toTrimmedTokens(str22.substring(10).trim(), ' ');
                        lc.setPackWidthAlign(toTrimmedTokens3[0].length() > 0 ? Float.parseFloat(toTrimmedTokens3[0]) : JFreeChart.DEFAULT_BACKGROUND_IMAGE_ALPHA);
                        if (toTrimmedTokens3.length > 1) {
                            lc.setPackHeightAlign(Float.parseFloat(toTrimmedTokens3[1]));
                        }
                    } else if (str22.startsWith("pack ") || str22.equals("pack")) {
                        trim = str22.substring(4).trim();
                        if (trim.length() <= 0) {
                            trim = "pref pref";
                        }
                        toTrimmedTokens2 = toTrimmedTokens(trim, ' ');
                        lc.setPackWidth(parseBoundSize(toTrimmedTokens2[0], false, true));
                        if (toTrimmedTokens2.length > 1) {
                            lc.setPackHeight(parseBoundSize(toTrimmedTokens2[1], false, false));
                        }
                    }
                }
                if (lc.getAlignX() == null) {
                    parseUnitValueOrAlign = parseAlignKeywords(str22, true);
                    if (parseUnitValueOrAlign != null) {
                        lc.setAlignX(parseUnitValueOrAlign);
                    }
                }
                parseUnitValueOrAlign = parseAlignKeywords(str22, false);
                if (parseUnitValueOrAlign != null) {
                    lc.setAlignY(parseUnitValueOrAlign);
                } else {
                    throw new IllegalArgumentException("Unknown Constraint: '" + str22 + "'\n");
                }
            }
        }
        return lc;
    }

    public static AC parseRowConstraints(String str) {
        return parseAxisConstraint(str, false);
    }

    private static int parseSpan(String str) {
        return str.length() > 0 ? Integer.parseInt(str) : 2097051;
    }

    private static UnitValue parseUnitValue(String str, UnitValue unitValue, boolean z) {
        if (str == null || str.length() == 0) {
            return unitValue;
        }
        char charAt = str.charAt(0);
        String substring = (charAt == '(' && str.charAt(str.length() - 1) == ')') ? str.substring(1, str.length() - 1) : str;
        if (charAt == 'n' && (substring.equals("null") || substring.equals("n"))) {
            return null;
        }
        if (charAt == 'i' && substring.equals("inf")) {
            return UnitValue.INF;
        }
        int oper = getOper(substring);
        int i = (oper == UnitValue.ADD || oper == UnitValue.SUB || oper == UnitValue.MUL || oper == UnitValue.DIV) ? 1 : 0;
        String[] toTrimmedTokens;
        if (oper != 100) {
            String trim;
            if (i == 0) {
                trim = substring.substring(4, substring.length() - 1).trim();
                toTrimmedTokens = toTrimmedTokens(trim, ',');
                if (toTrimmedTokens.length == 1) {
                    return parseUnitValue(trim, null, z);
                }
            }
            char c = oper == UnitValue.ADD ? '+' : oper == UnitValue.SUB ? '-' : oper == UnitValue.MUL ? '*' : '/';
            toTrimmedTokens = toTrimmedTokens(substring, c);
            if (toTrimmedTokens.length > 2) {
                String substring2 = substring.substring(0, (substring.length() - toTrimmedTokens[toTrimmedTokens.length - 1].length()) - 1);
                toTrimmedTokens = new String[]{substring2, trim};
            }
            if (toTrimmedTokens.length != 2) {
                throw new IllegalArgumentException("Malformed UnitValue: '" + substring + "'");
            }
            UnitValue parseUnitValue = parseUnitValue(toTrimmedTokens[0], null, z);
            UnitValue parseUnitValue2 = parseUnitValue(toTrimmedTokens[1], null, z);
            if (parseUnitValue != null && parseUnitValue2 != null) {
                return new UnitValue(z, oper, parseUnitValue, parseUnitValue2, str);
            }
            throw new IllegalArgumentException("Malformed UnitValue. Must be two sub-values: '" + substring + "'");
        }
        try {
            toTrimmedTokens = getNumTextParts(substring);
            return new UnitValue(toTrimmedTokens[0].length() > 0 ? Float.parseFloat(toTrimmedTokens[0]) : Plot.DEFAULT_FOREGROUND_ALPHA, toTrimmedTokens[1], z, oper, str);
        } catch (Exception e) {
            throw new IllegalArgumentException("Malformed UnitValue: '" + substring + "'");
        }
    }

    public static UnitValue parseUnitValue(String str, boolean z) {
        return parseUnitValue(str, null, z);
    }

    public static UnitValue parseUnitValueOrAlign(String str, boolean z, UnitValue unitValue) {
        if (str.length() == 0) {
            return unitValue;
        }
        UnitValue parseAlignKeywords = parseAlignKeywords(str, z);
        return parseAlignKeywords != null ? parseAlignKeywords : parseUnitValue(str, unitValue, z);
    }

    public static final String prepare(String str) {
        return str != null ? str.trim().toLowerCase() : "";
    }

    private static int startsWithLenient(String str, String str2, int i, boolean z) {
        int i2 = 0;
        if (str.charAt(0) != str2.charAt(0)) {
            return -1;
        }
        if (i == -1) {
            i = str2.length();
        }
        int length = str.length();
        if (length < i) {
            return -1;
        }
        int length2 = str2.length();
        int i3 = 0;
        while (i3 < length2) {
            while (i2 < length && (str.charAt(i2) == ' ' || str.charAt(i2) == '_')) {
                i2++;
            }
            if (i2 >= length || str.charAt(i2) != str2.charAt(i3)) {
                if (i3 < i || ((!z && i2 < length) || (i2 < length && str.charAt(i2 - 1) != ' '))) {
                    i2 = -1;
                }
                return i2;
            }
            i3++;
            i2++;
        }
        return (i2 >= length || z || str.charAt(i2) == ' ') ? i2 : -1;
    }

    private static int startsWithLenient(String str, String[] strArr, int[] iArr, boolean z) {
        int i = 0;
        while (i < strArr.length) {
            int startsWithLenient = startsWithLenient(str, strArr[i], iArr != null ? iArr[i] : -1, z);
            if (startsWithLenient > -1) {
                return startsWithLenient;
            }
            i++;
        }
        return -1;
    }

    private static String[] toTrimmedTokens(String str, char c) {
        int i = 0;
        int length = str.length();
        int i2 = c == ' ' ? 1 : 0;
        int i3 = 0;
        int i4 = 0;
        int i5 = 0;
        while (i3 < length) {
            char charAt = str.charAt(i3);
            if (charAt == '(') {
                i4++;
            } else if (charAt == ')') {
                i4--;
            } else if (i4 == 0 && charAt == c) {
                i5++;
                while (i2 != 0 && i3 < length - 1 && str.charAt(i3 + 1) == ' ') {
                    i3++;
                }
            }
            if (i4 < 0) {
                throw new IllegalArgumentException("Unbalanced parentheses: '" + str + "'");
            }
            i3++;
        }
        if (i4 != 0) {
            throw new IllegalArgumentException("Unbalanced parentheses: '" + str + "'");
        } else if (i5 == 0) {
            return new String[]{str.trim()};
        } else {
            String[] strArr = new String[(i5 + 1)];
            int i6 = 0;
            i4 = 0;
            i5 = 0;
            while (i6 < length) {
                char charAt2 = str.charAt(i6);
                int i7;
                if (charAt2 == '(') {
                    i7 = i4;
                    i4 = i5 + 1;
                    i3 = i7;
                } else if (charAt2 == ')') {
                    i7 = i4;
                    i4 = i5 - 1;
                    i3 = i7;
                } else if (i5 == 0 && charAt2 == c) {
                    i3 = i + 1;
                    strArr[i] = str.substring(i4, i6).trim();
                    i = i6 + 1;
                    while (i2 != 0 && i6 < length - 1 && str.charAt(i6 + 1) == ' ') {
                        i6++;
                    }
                    i4 = i5;
                    i7 = i;
                    i = i3;
                    i3 = i7;
                } else {
                    i3 = i4;
                    i4 = i5;
                }
                i6++;
                i5 = i4;
                i4 = i3;
            }
            i2 = i + 1;
            strArr[i] = str.substring(i4, length).trim();
            return strArr;
        }
    }
}
