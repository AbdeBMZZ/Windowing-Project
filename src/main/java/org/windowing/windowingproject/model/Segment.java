package org.windowing.windowingproject.model;

/**
 * Axis-parallel line segment between two points in the plane.
 * Supports only horizontal (y1 == y2) and vertical (x1 == x2) segments,
 * as required by the windowing assignment.
 */
public class Segment {

    private final Point2D p1;
    private final Point2D p2;

    public Segment(Point2D p1, Point2D p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public Point2D getP1() { return p1; }
    public Point2D getP2() { return p2; }

    /**
     * Returns {@code true} iff this segment intersects the closed query window {@code w}.
     * Handles ±∞ window bounds correctly.
     *
     * <p>For a horizontal segment (y1 == y2): it intersects when its y value is within
     * {@code [wBottom, wTop]} and its x-interval overlaps {@code [wLeft, wRight]}.</p>
     *
     * <p>For a vertical segment (x1 == x2): it intersects when its x value is within
     * {@code [wLeft, wRight]} and its y-interval overlaps {@code [wBottom, wTop]}.</p>
     *
     * @param w query window
     * @return {@code true} iff the segment intersects {@code w}
     */
    public boolean intersects(Window w) {
        double wLeft   = w.getXMin();
        double wRight  = w.getXMax();
        double wBottom = w.getYMin();
        double wTop    = w.getYMax();

        double minX = Math.min(p1.getX(), p2.getX());
        double maxX = Math.max(p1.getX(), p2.getX());
        double minY = Math.min(p1.getY(), p2.getY());
        double maxY = Math.max(p1.getY(), p2.getY());

        if (minY == maxY) {
            // Horizontal segment: y must be in window, x-range must overlap window
            return minY >= wBottom && minY <= wTop
                    && minX <= wRight && maxX >= wLeft;
        }

        if (minX == maxX) {
            // Vertical segment: x must be in window, y-range must overlap window
            return minX >= wLeft && minX <= wRight
                    && minY <= wTop && maxY >= wBottom;
        }

        // Non-axis-parallel segments are not supported
        return false;
    }
}
