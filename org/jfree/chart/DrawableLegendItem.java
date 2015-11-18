package org.jfree.chart;

import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class DrawableLegendItem {
    private double height;
    private LegendItem item;
    private Point2D labelPosition;
    private Line2D line;
    private Shape marker;
    private double width;
    private double x;
    private double y;

    public DrawableLegendItem(LegendItem item) {
        this.item = item;
    }

    public LegendItem getItem() {
        return this.item;
    }

    public double getX() {
        return this.x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return this.y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return this.width;
    }

    public double getHeight() {
        return this.height;
    }

    public double getMaxX() {
        return getX() + getWidth();
    }

    public double getMaxY() {
        return getY() + getHeight();
    }

    public Shape getMarker() {
        return this.marker;
    }

    public void setMarker(Shape marker) {
        this.marker = marker;
    }

    public void setLine(Line2D l) {
        this.line = l;
    }

    public Line2D getLine() {
        return this.line;
    }

    public Point2D getLabelPosition() {
        return this.labelPosition;
    }

    public void setLabelPosition(Point2D position) {
        this.labelPosition = position;
    }

    public void setBounds(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
