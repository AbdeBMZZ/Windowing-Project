package org.windowing.windowingproject.model;

/**
 * Axis-aligned (possibly unbounded) query rectangle. Bounds are inclusive; use
 * {@link Double#NEGATIVE_INFINITY} and {@link Double#POSITIVE_INFINITY} for open sides.
 */
public class Window {

    private final double xMin;
    private final double xMax;
    private final double yMin;
    private final double yMax;

    public Window(double xMin, double xMax, double yMin, double yMax) {
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
    }

    public double getXMin() {
        return xMin;
    }

    public double getXMax() {
        return xMax;
    }

    public double getYMin() {
        return yMin;
    }

    public double getYMax() {
        return yMax;
    }

    /**
     * Point-in-window test with inclusive bounds.
     */
    public boolean contains(Point2D p) {
        return p.getX() >= xMin && p.getX() <= xMax
                && p.getY() >= yMin && p.getY() <= yMax;
    }
}
