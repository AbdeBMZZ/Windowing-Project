package org.windowing.windowingproject.model;

/**
 * Endpoint stored in a priority search tree: world coordinates plus the segment it belongs to.
 * The tree may order points by {@code x} or by {@code -x} (see {@link org.windowing.windowingproject.pst.PrioritySearchTree})
 * while keeping these geometric coordinates for filtering and reporting.
 */
public final class PstEntry {

    private final double x;
    private final double y;
    private final int segmentIndex;
    private final int endpointIndex;

    private final CompositeNumber cx; // (x | y)
    private final CompositeNumber cy; // (y | x)

    /**
     * @param x            x-coordinate in the plane
     * @param y            y-coordinate in the plane
     * @param segmentIndex index of the segment in the loaded segment list
     * @param endpointIndex 0 for first endpoint, 1 for second
     */
    public PstEntry(double x, double y, int segmentIndex, int endpointIndex) {
        this.x = x;
        this.y = y;
        this.segmentIndex = segmentIndex;
        this.endpointIndex = endpointIndex;
        this.cx = new CompositeNumber(x, y);
        this.cy = new CompositeNumber(y, x);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getSegmentIndex() {
        return segmentIndex;
    }

    public int getEndpointIndex() {
        return endpointIndex;
    }
    public CompositeNumber getCx() { return cx; }
    public CompositeNumber getCy() { return cy; }
}