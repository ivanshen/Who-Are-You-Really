package org.jfree.chart;

import java.awt.Color;
import java.awt.Paint;

public class ChartColor extends Color {
    public static final Color DARK_BLUE;
    public static final Color DARK_CYAN;
    public static final Color DARK_GREEN;
    public static final Color DARK_MAGENTA;
    public static final Color DARK_RED;
    public static final Color DARK_YELLOW;
    public static final Color LIGHT_BLUE;
    public static final Color LIGHT_CYAN;
    public static final Color LIGHT_GREEN;
    public static final Color LIGHT_MAGENTA;
    public static final Color LIGHT_RED;
    public static final Color LIGHT_YELLOW;
    public static final Color VERY_DARK_BLUE;
    public static final Color VERY_DARK_CYAN;
    public static final Color VERY_DARK_GREEN;
    public static final Color VERY_DARK_MAGENTA;
    public static final Color VERY_DARK_RED;
    public static final Color VERY_DARK_YELLOW;
    public static final Color VERY_LIGHT_BLUE;
    public static final Color VERY_LIGHT_CYAN;
    public static final Color VERY_LIGHT_GREEN;
    public static final Color VERY_LIGHT_MAGENTA;
    public static final Color VERY_LIGHT_RED;
    public static final Color VERY_LIGHT_YELLOW;

    static {
        VERY_DARK_RED = new Color(128, 0, 0);
        DARK_RED = new Color(192, 0, 0);
        LIGHT_RED = new Color(255, 64, 64);
        VERY_LIGHT_RED = new Color(255, 128, 128);
        VERY_DARK_YELLOW = new Color(128, 128, 0);
        DARK_YELLOW = new Color(192, 192, 0);
        LIGHT_YELLOW = new Color(255, 255, 64);
        VERY_LIGHT_YELLOW = new Color(255, 255, 128);
        VERY_DARK_GREEN = new Color(0, 128, 0);
        DARK_GREEN = new Color(0, 192, 0);
        LIGHT_GREEN = new Color(64, 255, 64);
        VERY_LIGHT_GREEN = new Color(128, 255, 128);
        VERY_DARK_CYAN = new Color(0, 128, 128);
        DARK_CYAN = new Color(0, 192, 192);
        LIGHT_CYAN = new Color(64, 255, 255);
        VERY_LIGHT_CYAN = new Color(128, 255, 255);
        VERY_DARK_BLUE = new Color(0, 0, 128);
        DARK_BLUE = new Color(0, 0, 192);
        LIGHT_BLUE = new Color(64, 64, 255);
        VERY_LIGHT_BLUE = new Color(128, 128, 255);
        VERY_DARK_MAGENTA = new Color(128, 0, 128);
        DARK_MAGENTA = new Color(192, 0, 192);
        LIGHT_MAGENTA = new Color(255, 64, 255);
        VERY_LIGHT_MAGENTA = new Color(255, 128, 255);
    }

    public ChartColor(int r, int g, int b) {
        super(r, g, b);
    }

    public static Paint[] createDefaultPaintArray() {
        return new Paint[]{new Color(255, 85, 85), new Color(85, 85, 255), new Color(85, 255, 85), new Color(255, 255, 85), new Color(255, 85, 255), new Color(85, 255, 255), Color.pink, Color.gray, DARK_RED, DARK_BLUE, DARK_GREEN, DARK_YELLOW, DARK_MAGENTA, DARK_CYAN, Color.darkGray, LIGHT_RED, LIGHT_BLUE, LIGHT_GREEN, LIGHT_YELLOW, LIGHT_MAGENTA, LIGHT_CYAN, Color.lightGray, VERY_DARK_RED, VERY_DARK_BLUE, VERY_DARK_GREEN, VERY_DARK_YELLOW, VERY_DARK_MAGENTA, VERY_DARK_CYAN, VERY_LIGHT_RED, VERY_LIGHT_BLUE, VERY_LIGHT_GREEN, VERY_LIGHT_YELLOW, VERY_LIGHT_MAGENTA, VERY_LIGHT_CYAN};
    }
}
