package org.windowing.windowingproject.pst;

import org.windowing.windowingproject.model.PstEntry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Priority search tree for 2D points (de Berg et al., Ch. 10): heap order on one coordinate
 * (here tree-x) and binary partition on y. Supports efficient 3-sided queries of the form
 * {@code (-∞, xKeyMax] × [yMin, yMax]} in tree-x space.
 */
public class PrioritySearchTree {

    private final PSTNode root;
    private final boolean negateXKey;

    /**
     * @param points      endpoints to store (each becomes one node in the multiset)
     * @param negateXKey  if {@code true}, tree order uses {@code -x} instead of {@code x}
     *                    (for querying half-planes {@code x ≥ xMin} via a 3-sided query)
     */
    public PrioritySearchTree(List<PstEntry> points, boolean negateXKey) {
        this.negateXKey = negateXKey;
        this.root = build(new ArrayList<>(points));
    }

    private double treeX(PstEntry e) {
        return negateXKey ? -e.getX() : e.getX();
    }

    private Comparator<PstEntry> byTreeX() {
        return Comparator
                .comparingDouble(this::treeX)
                .thenComparingDouble(PstEntry::getY)
                .thenComparingInt(PstEntry::getSegmentIndex)
                .thenComparingInt(PstEntry::getEndpointIndex);
    }

    private static Comparator<PstEntry> byWorldY() {
        return Comparator
                .comparingDouble(PstEntry::getY)
                .thenComparingDouble(PstEntry::getX)
                .thenComparingInt(PstEntry::getSegmentIndex)
                .thenComparingInt(PstEntry::getEndpointIndex);
    }

    private PSTNode build(List<PstEntry> pts) {
        if (pts.isEmpty()) {
            return null;
        }

        pts.sort(byTreeX());
        PstEntry minKey = pts.remove(0);

        PSTNode node = new PSTNode(minKey);
        if (pts.isEmpty()) {
            return node;
        }

        pts.sort(byWorldY());
        int median = pts.size() / 2;
        node.medianY = pts.get(median).getY();

        List<PstEntry> left = new ArrayList<>();
        List<PstEntry> right = new ArrayList<>();
        for (PstEntry p : pts) {
            if (p.getY() <= node.medianY) {
                left.add(p);
            } else {
                right.add(p);
            }
        }

        node.left = build(left);
        node.right = build(right);
        return node;
    }

    /**
     * All points whose tree-x is ≤ {@code xKeyMax} and whose y lies in [{@code yMin}, {@code yMax}].
     * In the usual (non-negated) tree this is {@code (-∞, xKeyMax] × [yMin, yMax]}.
     */
    public List<PstEntry> queryOpenLeft(double xKeyMax, double yMin, double yMax) {
        List<PstEntry> result = new ArrayList<>();
        queryOpenLeft(root, xKeyMax, yMin, yMax, result);
        return result;
    }

    private void queryOpenLeft(PSTNode node, double xKeyMax, double yMin, double yMax, List<PstEntry> out) {
        if (node == null) {
            return;
        }
        if (treeX(node.entry) > xKeyMax) {
            return;
        }
        double y = node.entry.getY();
        if (y >= yMin && y <= yMax) {
            out.add(node.entry);
        }
        if (node.left != null && yMin <= node.medianY) {
            queryOpenLeft(node.left, xKeyMax, yMin, yMax, out);
        }
        if (node.right != null && yMax > node.medianY) {
            queryOpenLeft(node.right, xKeyMax, yMin, yMax, out);
        }
    }

    /**
     * When x is unbounded on both sides, report all points with y in [{@code yMin}, {@code yMax}]
     * (no pruning on x).
     */
    public List<PstEntry> queryYStrip(double yMin, double yMax) {
        List<PstEntry> result = new ArrayList<>();
        queryYStrip(root, yMin, yMax, result);
        return result;
    }

    private void queryYStrip(PSTNode node, double yMin, double yMax, List<PstEntry> out) {
        if (node == null) {
            return;
        }
        double y = node.entry.getY();
        if (y >= yMin && y <= yMax) {
            out.add(node.entry);
        }
        if (node.left != null && yMin <= node.medianY) {
            queryYStrip(node.left, yMin, yMax, out);
        }
        if (node.right != null && yMax > node.medianY) {
            queryYStrip(node.right, yMin, yMax, out);
        }
    }
}
