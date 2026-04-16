package org.windowing.windowingproject.pst;

import org.junit.jupiter.api.Test;
import org.windowing.windowingproject.model.Point2D;
import org.windowing.windowingproject.model.Segment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IntervalTreeTest {

    private static Segment horiz(double x1, double y, double x2) {
        return new Segment(new Point2D(x1, y), new Point2D(x2, y));
    }

    private static Segment vert(double x, double y1, double y2) {
        return new Segment(new Point2D(x, y1), new Point2D(x, y2));
    }

    private List<Segment> query(IntervalTree tree, double point, double crossMin, double crossMax) {
        List<Segment> result = new ArrayList<>();
        tree.queryCrossing(point, crossMin, crossMax, result);
        return result;
    }

    // ---- horizontal tree ----

    @Test
    void horizontal_crossingFound() {
        IntervalTree tree = new IntervalTree(List.of(horiz(-100, 50, 100)), true);
        assertEquals(1, query(tree, 0, 0, 100).size());
    }

    @Test
    void horizontal_yOutOfRange_notFound() {
        IntervalTree tree = new IntervalTree(List.of(horiz(-100, 200, 100)), true);
        assertTrue(query(tree, 0, 0, 100).isEmpty());
    }

    @Test
    void horizontal_doesNotCrossX_notFound() {
        IntervalTree tree = new IntervalTree(List.of(horiz(10, 50, 50)), true);
        assertTrue(query(tree, 5, 0, 100).isEmpty());
    }

    @Test
    void horizontal_touchingXBoundary_found() {
        IntervalTree tree = new IntervalTree(List.of(horiz(-5, 50, 0)), true);
        assertEquals(1, query(tree, 0, 0, 100).size());
    }

    @Test
    void horizontal_yBoundaryInclusive() {
        IntervalTree tree = new IntervalTree(List.of(horiz(-10, 0, 10)), true);
        assertEquals(1, query(tree, 0, 0, 100).size());
        assertEquals(1, query(tree, 0, -100, 0).size());
    }

    // ---- vertical tree ----

    @Test
    void vertical_crossingFound() {
        IntervalTree tree = new IntervalTree(List.of(vert(50, -100, 100)), false);
        assertEquals(1, query(tree, 0, 0, 100).size());
    }

    @Test
    void vertical_xOutOfRange_notFound() {
        IntervalTree tree = new IntervalTree(List.of(vert(200, -100, 100)), false);
        assertTrue(query(tree, 0, 0, 100).isEmpty());
    }

    @Test
    void vertical_doesNotCrossY_notFound() {
        IntervalTree tree = new IntervalTree(List.of(vert(50, 10, 50)), false);
        assertTrue(query(tree, 5, 0, 100).isEmpty());
    }

    // ---- multiple segments ----

    @Test
    void multiple_onlyCrossingReturned() {
        List<Segment> segments = Arrays.asList(
                horiz(-100, 50, 100),  // crosses x=0, y=50 in [0,100] ✓
                horiz(10, 50, 90),     // does NOT cross x=0
                horiz(-50, 200, 50)    // y=200 out of [0,100]
        );
        IntervalTree tree = new IntervalTree(segments, true);
        assertEquals(1, query(tree, 0, 0, 100).size());
    }

    @Test
    void multiple_allCrossing_allReturned() {
        List<Segment> segs = Arrays.asList(
                horiz(-10, 10, 10),
                horiz(-20, 20, 20),
                horiz(-30, 30, 30)
        );
        IntervalTree tree = new IntervalTree(segs, true);
        assertEquals(3, query(tree, 0, 0, 50).size());
    }

    // ---- edge cases ----

    @Test
    void emptyTree_returnsEmpty() {
        IntervalTree tree = new IntervalTree(List.of(), true);
        assertTrue(query(tree, 0, -100, 100).isEmpty());
    }

    @Test
    void nullSegments_returnsEmpty() {
        IntervalTree tree = new IntervalTree(null, true);
        assertTrue(query(tree, 0, -100, 100).isEmpty());
    }
}
