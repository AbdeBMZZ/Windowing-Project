package org.windowing.windowingproject.pst;

/**
 * Pair of priority search trees on the same endpoints: standard x-order and negated-x order,
 * used together for 4-sided and half-infinite range queries.
 */
public final class PstIndex {

    private final PrioritySearchTree forward;
    private final PrioritySearchTree negatedX;

    public PstIndex(PrioritySearchTree forward, PrioritySearchTree negatedX) {
        this.forward = forward;
        this.negatedX = negatedX;
    }

    public PrioritySearchTree getForward() {
        return forward;
    }

    public PrioritySearchTree getNegatedX() {
        return negatedX;
    }
}
