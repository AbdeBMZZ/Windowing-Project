package org.windowing.windowingproject.model;

/**
 * Immutable point in {@code R²} with precomputed composite coordinates (Section 5.5).
 */
public class Point2D {

    private final double x;
    private final double y;
    private final CompositeNumber cx; // (px | py)
    private final CompositeNumber cy; // (py | px)

    public Point2D(double x, double y) {
        this.x  = x;
        this.y  = y;
        this.cx = CompositeNumber.compositeX(x, y);
        this.cy = CompositeNumber.compositeY(x, y);
    }

    public double getX()          { return x; }
    public double getY()          { return y; }

    /** @return composite x-coordinate {@code (px|py)} */
    public CompositeNumber getCx() { return cx; }

    /** @return composite y-coordinate {@code (py|px)} */
    public CompositeNumber getCy() { return cy; }
}
