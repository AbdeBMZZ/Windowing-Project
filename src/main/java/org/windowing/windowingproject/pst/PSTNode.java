package org.windowing.windowingproject.pst;

import org.windowing.windowingproject.model.PstEntry;

/**
 * Node of a priority search tree: one stored point (minimum tree-key in this subtree)
 * and a y-median value partitioning children.
 */
public class PSTNode {

    /** Point with smallest tree-x key among all points in this subtree. */
    public final PstEntry entry;
    /** Median y used to split the remaining points between left and right subtrees. */
    public double medianY;
    public PSTNode left;
    public PSTNode right;

    public PSTNode(PstEntry entry) {
        this.entry = entry;
    }
}
