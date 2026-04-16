package org.windowing.windowingproject.pst;

import org.windowing.windowingproject.model.CompositeNumber;
import org.windowing.windowingproject.model.PstEntry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Priority search tree for 2D points (de Berg et al., Ch. 10 + Section 5.5):
 * heap order on composite-x key, BST partition on composite-y key.
 * Supports efficient 3-sided queries of the form
 * {@code (-∞, xKeyMax] × [yMin, yMax]} in composite-key space.
 */
public class PrioritySearchTree {

    private final PSTNode root;
    private final boolean negateXKey;

    /**
     * @param points     endpoints to store
     * @param negateXKey if {@code true}, tree key uses {@code (-px|-py)} instead of
     *                   {@code (px|py)}, enabling queries {@code x ≥ xMin} via negation
     */
    public PrioritySearchTree(List<PstEntry> points, boolean negateXKey) {
        this.negateXKey = negateXKey;
        this.root = build(new ArrayList<>(points));
    }

    // ---- composite key for the heap dimension ----

    private CompositeNumber treeKey(PstEntry e) {
        return negateXKey
                ? new CompositeNumber(-e.getX(), -e.getY())
                : e.getCx();
    }

    // ---- comparators ----

    private Comparator<PstEntry> byTreeX() {
        if (negateXKey) {
            return Comparator
                    .<PstEntry, CompositeNumber>comparing(e -> new CompositeNumber(-e.getX(), -e.getY()))
                    .thenComparingInt(PstEntry::getSegmentIndex)
                    .thenComparingInt(PstEntry::getEndpointIndex);
        }
        return Comparator
                .comparing(PstEntry::getCx)
                .thenComparingInt(PstEntry::getSegmentIndex)
                .thenComparingInt(PstEntry::getEndpointIndex);
    }

    private static Comparator<PstEntry> byWorldY() {
        return Comparator
                .comparing(PstEntry::getCy)
                .thenComparingInt(PstEntry::getSegmentIndex)
                .thenComparingInt(PstEntry::getEndpointIndex);
    }

    // ---- build ----

    private PSTNode build(List<PstEntry> pts) {
        if (pts.isEmpty()) return null;

        pts.sort(byTreeX());
        PstEntry minKey = pts.remove(0);

        PSTNode node = new PSTNode(minKey);
        if (pts.isEmpty()) return node;

        pts.sort(byWorldY());
        int median = pts.size() / 2;
        node.medianCy = pts.get(median).getCy();

        List<PstEntry> left  = new ArrayList<>();
        List<PstEntry> right = new ArrayList<>();
        for (PstEntry p : pts) {
            if (p.getCy().compareTo(node.medianCy) <= 0) {
                left.add(p);
            } else {
                right.add(p);
            }
        }

        node.left  = build(left);
        node.right = build(right);
        return node;
    }

    // ---- public queries ----

    /**
     * All points whose composite tree-key is ≤ {@code (xKeyMax|+∞)} and whose
     * composite-y lies in [{@code (yMin|−∞)}, {@code (yMax|+∞)}].
     * Per Section 5.5, the composite bounds correctly include boundary points.
     */
    public List<PstEntry> queryOpenLeft(double xKeyMax, double yMin, double yMax) {
        List<PstEntry> result = new ArrayList<>();
        CompositeNumber keyBound = new CompositeNumber(xKeyMax, Double.POSITIVE_INFINITY);
        CompositeNumber cyLo = CompositeNumber.lowerBound(yMin);
        CompositeNumber cyHi = CompositeNumber.upperBound(yMax);
        queryOpenLeft(root, keyBound, cyLo, cyHi, result);
        return result;
    }

    /**
     * All points whose composite-y lies in [{@code (yMin|−∞)}, {@code (yMax|+∞)}]
     * (no pruning on x).
     */
    public List<PstEntry> queryYStrip(double yMin, double yMax) {
        List<PstEntry> result = new ArrayList<>();
        CompositeNumber cyLo = CompositeNumber.lowerBound(yMin);
        CompositeNumber cyHi = CompositeNumber.upperBound(yMax);
        queryYStrip(root, cyLo, cyHi, result);
        return result;
    }

    // ---- private recursive traversals ----

    private void queryOpenLeft(PSTNode node, CompositeNumber keyBound,
                               CompositeNumber cyLo, CompositeNumber cyHi,
                               List<PstEntry> out) {
        if (node == null) return;
        if (treeKey(node.entry).compareTo(keyBound) > 0) return;

        CompositeNumber cy = node.entry.getCy();
        if (cy.compareTo(cyLo) >= 0 && cy.compareTo(cyHi) <= 0) {
            out.add(node.entry);
        }

        if (node.left != null && cyLo.compareTo(node.medianCy) <= 0) {
            queryOpenLeft(node.left, keyBound, cyLo, cyHi, out);
        }
        if (node.right != null && cyHi.compareTo(node.medianCy) > 0) {
            queryOpenLeft(node.right, keyBound, cyLo, cyHi, out);
        }
    }

    private void queryYStrip(PSTNode node, CompositeNumber cyLo,
                             CompositeNumber cyHi, List<PstEntry> out) {
        if (node == null) return;

        CompositeNumber cy = node.entry.getCy();
        if (cy.compareTo(cyLo) >= 0 && cy.compareTo(cyHi) <= 0) {
            out.add(node.entry);
        }

        if (node.left != null && cyLo.compareTo(node.medianCy) <= 0) {
            queryYStrip(node.left, cyLo, cyHi, out);
        }
        if (node.right != null && cyHi.compareTo(node.medianCy) > 0) {
            queryYStrip(node.right, cyLo, cyHi, out);
        }
    }
}
