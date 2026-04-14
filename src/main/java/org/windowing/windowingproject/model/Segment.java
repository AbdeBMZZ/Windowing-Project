package org.windowing.windowingproject.model;

public class Segment {
    private final Point2D p1;
    private final Point2D p2;

    public Segment(Point2D p1, Point2D p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public Point2D getP1() { return p1; }
    public Point2D getP2() { return p2; }

    public boolean intersects(Window w) {
        double minX = Math.min(p1.getX(), p2.getX());
        double maxX = Math.max(p1.getX(), p2.getX());
        double minY = Math.min(p1.getY(), p2.getY());
        double maxY = Math.max(p1.getY(), p2.getY());

        return !(maxX < w.getXMin() || minX > w.getXMax()
                || maxY < w.getYMin() || minY > w.getYMax());
    }
}
