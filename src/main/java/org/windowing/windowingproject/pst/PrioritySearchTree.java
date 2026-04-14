package org.windowing.windowingproject.pst;

import org.windowing.windowingproject.model.Point2D;
import org.windowing.windowingproject.model.Window;

import java.util.*;

public class PrioritySearchTree {

    private final PSTNode root;

    public PrioritySearchTree(List<Point2D> points) {
        this.root = build(new ArrayList<>(points));
    }

    private PSTNode build(List<Point2D> pts) {
        if (pts.isEmpty()) return null;

        pts.sort(Comparator.comparingDouble(Point2D::getX));
        Point2D minX = pts.remove(0);

        PSTNode node = new PSTNode(minX);
        if (pts.isEmpty()) return node;

        pts.sort(Comparator.comparingDouble(Point2D::getY));
        int median = pts.size() / 2;
        node.medianY = pts.get(median).getY();

        List<Point2D> left = new ArrayList<>();
        List<Point2D> right = new ArrayList<>();

        for (Point2D p : pts) {
            if (p.getY() <= node.medianY) left.add(p);
            else right.add(p);
        }

        node.left = build(left);
        node.right = build(right);
        return node;
    }

    public List<Point2D> query(Window w) {
        List<Point2D> result = new ArrayList<>();
        query(root, w, result);
        return result;
    }

    private void query(PSTNode node, Window w, List<Point2D> result) {
        if (node == null) return;

        if (w.contains(node.point)) {
            result.add(node.point);
        }

        if (node.left != null && w.getYMin() <= node.medianY) {
            query(node.left, w, result);
        }

        if (node.right != null && w.getYMax() > node.medianY) {
            query(node.right, w, result);
        }
    }
}
