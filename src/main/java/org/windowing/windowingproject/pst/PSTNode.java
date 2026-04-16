package org.windowing.windowingproject.pst;

import org.windowing.windowingproject.model.CompositeNumber;
import org.windowing.windowingproject.model.PstEntry;

/**
 * Node of a priority search tree: one stored point (minimum tree-key in this subtree)
 * and a composite-y median partitioning children.
 */
public class PSTNode {

    /** Point with smallest tree-x key among all points in this subtree. */
    public final PstEntry entry;
    /** Composite-y median used to split remaining points between left and right subtrees. */
    public CompositeNumber medianCy;
    public PSTNode left;
    public PSTNode right;

    public PSTNode(PstEntry entry) {
        this.entry = entry;
    }
}
