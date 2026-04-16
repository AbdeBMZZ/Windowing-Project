package org.windowing.windowingproject.pst;

/**
 * Composite index holding all data structures needed for a windowing query:
 * two {@link PrioritySearchTree} instances (forward and x-negated) for endpoint
 * queries, and two {@link IntervalTree} instances for crossing-segment queries.
 */
public class PstIndex {

    private final PrioritySearchTree forward;
    private final PrioritySearchTree negatedX;
    private final IntervalTree horizontalTree;
    private final IntervalTree verticalTree;

    /**
     * @param forward        PST with heap order on {@code (px|py)}
     * @param negatedX       PST with heap order on {@code (-px|-py)}, enabling {@code x ≥ xMin} queries
     * @param horizontalTree interval tree for horizontal segments (primary key = x)
     * @param verticalTree   interval tree for vertical segments (primary key = y)
     */
    public PstIndex(PrioritySearchTree forward, PrioritySearchTree negatedX,
                    IntervalTree horizontalTree, IntervalTree verticalTree) {
        this.forward         = forward;
        this.negatedX        = negatedX;
        this.horizontalTree  = horizontalTree;
        this.verticalTree    = verticalTree;
    }

    /** @return PST with ascending composite-x order */
    public PrioritySearchTree getForward()        { return forward; }

    /** @return PST with ascending negated-composite-x order */
    public PrioritySearchTree getNegatedX()       { return negatedX; }

    /** @return interval tree for horizontal segments */
    public IntervalTree getHorizontalTree()       { return horizontalTree; }

    /** @return interval tree for vertical segments */
    public IntervalTree getVerticalTree()         { return verticalTree; }
}
