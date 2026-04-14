package org.windowing.windowingproject.pst;

import org.windowing.windowingproject.model.PstEntry;
import org.windowing.windowingproject.model.Segment;
import org.windowing.windowingproject.model.Window;

import java.util.ArrayList;
import java.util.List;

/**
 * Windowing for orthogonal segments using two priority search trees on segment endpoints:
 * one in standard x-order and one in {@code -x} order, following the usual reduction of a
 * 4-sided query to 3-sided queries (de Berg et al.). Segments intersecting the window without
 * an endpoint inside are added in a second geometric pass.
 */
public final class PstWindowing {

    private PstWindowing() {
    }

    private static boolean isNegInf(double v) {
        return v == Double.NEGATIVE_INFINITY;
    }

    private static boolean isPosInf(double v) {
        return v == Double.POSITIVE_INFINITY;
    }

    /**
     * Returns segments that intersect {@code window}, using PST queries on endpoints first,
     * then adding any remaining intersecting segments (e.g. long segments crossing the window
     * with both endpoints outside).
     */
    public static List<Segment> findIntersectingSegments(
            PrioritySearchTree pstForward,
            PrioritySearchTree pstNegX,
            List<Segment> segments,
            Window window) {

        double xMin = window.getXMin();
        double xMax = window.getXMax();
        double yMin = window.getYMin();
        double yMax = window.getYMax();

        List<PstEntry> endpointHits = new ArrayList<>();

        boolean xOpenLeft = isNegInf(xMin);
        boolean xOpenRight = isPosInf(xMax);

        if (xOpenLeft && xOpenRight) {
            endpointHits.addAll(pstForward.queryYStrip(yMin, yMax));
        } else if (xOpenRight) {
            endpointHits.addAll(pstNegX.queryOpenLeft(-xMin, yMin, yMax));
            if (!isPosInf(xMax)) {
                endpointHits.removeIf(e -> e.getX() > xMax);
            }
        } else {
            endpointHits.addAll(pstForward.queryOpenLeft(xMax, yMin, yMax));
            if (!xOpenLeft) {
                endpointHits.removeIf(e -> e.getX() < xMin);
            }
        }

        boolean[] hit = new boolean[segments.size()];
        for (PstEntry e : endpointHits) {
            int i = e.getSegmentIndex();
            if (i >= 0 && i < hit.length) {
                hit[i] = true;
            }
        }

        for (int i = 0; i < segments.size(); i++) {
            if (hit[i]) {
                continue;
            }
            if (segments.get(i).intersects(window)) {
                hit[i] = true;
            }
        }

        List<Segment> result = new ArrayList<>();
        for (int i = 0; i < segments.size(); i++) {
            if (hit[i]) {
                result.add(segments.get(i));
            }
        }
        return result;
    }
}
