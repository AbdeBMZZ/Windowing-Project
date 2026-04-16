package org.windowing.windowingproject.pst;

import org.windowing.windowingproject.model.PstEntry;
import org.windowing.windowingproject.model.Segment;
import org.windowing.windowingproject.model.Window;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Static utility that executes a windowing query against a {@link PstIndex}.
 *
 * <p>A segment intersects the query window if and only if at least one of the
 * following holds:
 * <ol>
 *   <li>One of its endpoints lies inside the window (found via PST).</li>
 *   <li>It crosses the left or bottom edge of the window without an endpoint
 *       inside (found via interval trees).</li>
 * </ol>
 *
 * <p>Querying only the left edge for horizontal segments and only the bottom edge
 * for vertical segments is sufficient: any crossing segment that misses both
 * edges would have at least one endpoint inside the window and is caught by case 1.
 * </p>
 */
public final class PstWindowing {

    private PstWindowing() {}

    private static boolean isNegInf(double v) { return v == Double.NEGATIVE_INFINITY; }
    private static boolean isPosInf(double v) { return v == Double.POSITIVE_INFINITY; }

    /**
     * Returns all segments from {@code segments} that intersect {@code window}.
     *
     * @param index    prebuilt index over the segment endpoints
     * @param segments full segment list in the same order used when building {@code index}
     * @param window   query window (bounds may be ±∞)
     * @return deduplicated list of intersecting segments
     */
    public static List<Segment> findIntersectingSegments(PstIndex index,
                                                         List<Segment> segments,
                                                         Window window) {
        double xMin = window.getXMin();
        double xMax = window.getXMax();
        double yMin = window.getYMin();
        double yMax = window.getYMax();

        List<PstEntry> endpointHits = new ArrayList<>();
        boolean xOpenLeft  = isNegInf(xMin);
        boolean xOpenRight = isPosInf(xMax);

        // 1. PST queries — find endpoints whose (x,y) lies inside the window
        if (xOpenLeft && xOpenRight) {
            // Unbounded in x: pure y-strip query
            endpointHits.addAll(index.getForward().queryYStrip(yMin, yMax));
        } else if (xOpenRight) {
            // x in [xMin, +∞): negate x so that x ≥ xMin becomes -x ≤ -xMin
            endpointHits.addAll(index.getNegatedX().queryOpenLeft(-xMin, yMin, yMax));
            if (!isPosInf(xMax)) endpointHits.removeIf(e -> e.getX() > xMax);
        } else {
            // x in (-∞, xMax] or [xMin, xMax]
            endpointHits.addAll(index.getForward().queryOpenLeft(xMax, yMin, yMax));
            if (!xOpenLeft) endpointHits.removeIf(e -> e.getX() < xMin);
        }

        Set<Segment> resultSet = new HashSet<>();
        for (PstEntry e : endpointHits) {
            int i = e.getSegmentIndex();
            if (i >= 0 && i < segments.size()) {
                resultSet.add(segments.get(i));
            }
        }

        // 2. Interval tree queries — find segments that span across a window edge
        List<Segment> crossingSegments = new ArrayList<>();

        // Horizontal segments crossing the left edge (x = xMin, y in [yMin, yMax])
        if (!xOpenLeft && index.getHorizontalTree() != null) {
            index.getHorizontalTree().queryCrossing(xMin, yMin, yMax, crossingSegments);
        }

        // Vertical segments crossing the bottom edge (y = yMin, x in [xMin, xMax])
        if (!isNegInf(yMin) && index.getVerticalTree() != null) {
            index.getVerticalTree().queryCrossing(yMin, xMin, xMax, crossingSegments);
        }

        resultSet.addAll(crossingSegments);
        return new ArrayList<>(resultSet);
    }
}
