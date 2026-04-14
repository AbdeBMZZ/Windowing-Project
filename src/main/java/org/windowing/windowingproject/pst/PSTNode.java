package org.windowing.windowingproject.pst;

import org.windowing.windowingproject.model.Point2D;

public class PSTNode {
    public Point2D point;
    public double medianY;
    public PSTNode left, right;

    public PSTNode(Point2D point) {
        this.point = point;
    }
}
