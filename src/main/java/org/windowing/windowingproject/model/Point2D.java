package org.windowing.windowingproject.model;

/**
 * Immutable point in {@code R²}.
 */
public class Point2D {
    
    private final double x, y;
    public final CompositeNumber cx; // (px | py)
    public final CompositeNumber cy; // (py | px)

    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
        this.cx = CompositeNumber.compositeX(x, y);
        this.cy = CompositeNumber.compositeY(x, y);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}