package org.windowing.windowingproject.pst;

import org.windowing.windowingproject.model.Segment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Interval tree for axis-parallel segments, supporting O(log n + k) crossing queries.
 *
 * <p>Each node stores the mid-point of its subtree's endpoint range. Segments whose
 * interval contains {@code midPoint} are stored at that node, sorted in two lists:
 * by left endpoint ascending (for left-of-mid queries) and by right endpoint descending
 * (for right-of-mid queries), enabling early termination.</p>
 *
 * <p>Two flavours are supported via {@code isHorizontal}:
 * <ul>
 *   <li>{@code true}  — horizontal segments; primary key is x, cross-coordinate is y.</li>
 *   <li>{@code false} — vertical segments;   primary key is y, cross-coordinate is x.</li>
 * </ul>
 * </p>
 */
public class IntervalTree {

    private double midPoint;
    private List<Segment> leftSorted;
    private List<Segment> rightSorted;
    private IntervalTree leftChild;
    private IntervalTree rightChild;
    private final boolean isHorizontal;

    /**
     * Builds an interval tree over the given segments.
     *
     * @param segments     axis-parallel segments to index
     * @param isHorizontal {@code true} for horizontal segments (key = x-interval),
     *                     {@code false} for vertical segments (key = y-interval)
     */
    public IntervalTree(List<Segment> segments, boolean isHorizontal) {
        this.isHorizontal = isHorizontal;
        if (segments == null || segments.isEmpty()) return;

        List<Double> endpoints = new ArrayList<>();
        for (Segment s : segments) {
            endpoints.add(primaryMin(s));
            endpoints.add(primaryMax(s));
        }
        endpoints.sort(Double::compareTo);
        this.midPoint = endpoints.get(endpoints.size() / 2);

        List<Segment> midSegments  = new ArrayList<>();
        List<Segment> leftSegments = new ArrayList<>();
        List<Segment> rightSegments = new ArrayList<>();

        for (Segment s : segments) {
            if (primaryMax(s) < midPoint)      leftSegments.add(s);
            else if (primaryMin(s) > midPoint) rightSegments.add(s);
            else                               midSegments.add(s);
        }

        // Sort mid-segments for early-exit traversal
        this.leftSorted = new ArrayList<>(midSegments);
        this.leftSorted.sort(Comparator.comparingDouble(this::primaryMin));

        this.rightSorted = new ArrayList<>(midSegments);
        this.rightSorted.sort((s1, s2) -> Double.compare(primaryMax(s2), primaryMax(s1)));

        if (!leftSegments.isEmpty())  this.leftChild  = new IntervalTree(leftSegments,  isHorizontal);
        if (!rightSegments.isEmpty()) this.rightChild = new IntervalTree(rightSegments, isHorizontal);
    }

    // ---- helpers ----

    private double primaryMin(Segment s) {
        return isHorizontal
                ? Math.min(s.getP1().getX(), s.getP2().getX())
                : Math.min(s.getP1().getY(), s.getP2().getY());
    }

    private double primaryMax(Segment s) {
        return isHorizontal
                ? Math.max(s.getP1().getX(), s.getP2().getX())
                : Math.max(s.getP1().getY(), s.getP2().getY());
    }

    // ---- query ----

    /**
     * Finds all segments whose primary interval contains {@code point} and whose
     * cross-coordinate lies in [{@code crossMin}, {@code crossMax}].
     *
     * <p>For a horizontal tree: {@code point} is an x-value, cross-coordinates are y-values.
     * For a vertical tree: {@code point} is a y-value, cross-coordinates are x-values.</p>
     *
     * @param point    value that must be inside the segment's primary interval
     * @param crossMin lower bound (inclusive) on the segment's cross-coordinate
     * @param crossMax upper bound (inclusive) on the segment's cross-coordinate
     * @param result   output list; matching segments are appended
     */
    public void queryCrossing(double point, double crossMin, double crossMax,
                              List<Segment> result) {
        if (this.leftSorted == null) return;

        if (point < midPoint) {
            for (Segment s : leftSorted) {
                if (primaryMin(s) <= point) {
                    double cross = isHorizontal ? s.getP1().getY() : s.getP1().getX();
                    if (cross >= crossMin && cross <= crossMax) result.add(s);
                } else break; // sorted by left endpoint — no further match possible
            }
            if (leftChild != null) leftChild.queryCrossing(point, crossMin, crossMax, result);
        } else {
            for (Segment s : rightSorted) {
                if (primaryMax(s) >= point) {
                    double cross = isHorizontal ? s.getP1().getY() : s.getP1().getX();
                    if (cross >= crossMin && cross <= crossMax) result.add(s);
                } else break; // sorted by right endpoint desc — no further match possible
            }
            if (rightChild != null) rightChild.queryCrossing(point, crossMin, crossMax, result);
        }
    }
}
