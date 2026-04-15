package org.windowing.windowingproject.pst;

import org.windowing.windowingproject.model.IntervalTree;

public class PstIndex {
    private final PrioritySearchTree forward;
    private final PrioritySearchTree negatedX;
    private final IntervalTree horizontalTree;
    private final IntervalTree verticalTree;

    public PstIndex(PrioritySearchTree forward, PrioritySearchTree negatedX, 
                    IntervalTree horizontalTree, IntervalTree verticalTree) {
        this.forward = forward;
        this.negatedX = negatedX;
        this.horizontalTree = horizontalTree;
        this.verticalTree = verticalTree;
    }

    public PrioritySearchTree getForward() { return forward; }
    public PrioritySearchTree getNegatedX() { return negatedX; }
    public IntervalTree getHorizontalTree() { return horizontalTree; }
    public IntervalTree getVerticalTree() { return verticalTree; }
}