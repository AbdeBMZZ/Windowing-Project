package org.windowing.windowingproject.pst;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.windowing.windowingproject.model.Point2D;
import org.windowing.windowingproject.model.PstEntry;
import org.windowing.windowingproject.model.Segment;
import org.windowing.windowingproject.model.Window;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PstWindowingTest {

    private List<Segment> segments;
    private PstIndex index;

    private static Segment horiz(double x1, double y, double x2) {
        return new Segment(new Point2D(x1, y), new Point2D(x2, y));
    }

    private static Segment vert(double x, double y1, double y2) {
        return new Segment(new Point2D(x, y1), new Point2D(x, y2));
    }

    private PstIndex buildIndex(List<Segment> segs) {
        List<PstEntry> entries = new ArrayList<>();
        List<Segment> hSegs = new ArrayList<>();
        List<Segment> vSegs = new ArrayList<>();

        for (int i = 0; i < segs.size(); i++) {
            Segment s = segs.get(i);
            entries.add(new PstEntry(s.getP1().getX(), s.getP1().getY(), i, 0));
            entries.add(new PstEntry(s.getP2().getX(), s.getP2().getY(), i, 1));
            if (s.getP1().getY() == s.getP2().getY()) hSegs.add(s);
            else                                       vSegs.add(s);
        }

        return new PstIndex(
                new PrioritySearchTree(entries, false),
                new PrioritySearchTree(entries, true),
                new IntervalTree(hSegs, true),
                new IntervalTree(vSegs, false)
        );
    }

    @BeforeEach
    void setUp() {
        segments = List.of(
                horiz(0, 5, 10),      // 0 – horizontal endpoint inside
                vert(5, 0, 10),       // 1 – vertical endpoint inside
                horiz(-100, 5, 100),  // 2 – long horizontal, spans window
                vert(5, -100, 100),   // 3 – long vertical, spans window
                horiz(50, 50, 100)    // 4 – fully outside
        );
        index = buildIndex(segments);
    }

    // ---- bounded window ----

    @Test
    void bounded_endpointsInsideFound() {
        List<Segment> result = query(new Window(0, 10, 0, 10));
        assertTrue(result.contains(segments.get(0)));
        assertTrue(result.contains(segments.get(1)));
    }

    @Test
    void bounded_crossingSegmentsFound() {
        List<Segment> result = query(new Window(0, 10, 0, 10));
        assertTrue(result.contains(segments.get(2)));
        assertTrue(result.contains(segments.get(3)));
    }

    @Test
    void bounded_outsideSegmentExcluded() {
        assertFalse(query(new Window(0, 10, 0, 10)).contains(segments.get(4)));
    }

    @Test
    void bounded_emptyWindow_returnsEmpty() {
        assertTrue(query(new Window(20, 30, 20, 30)).isEmpty());
    }

    @Test
    void bounded_noDuplicates_whenBothEndpointsInside() {
        List<Segment> result = query(new Window(0, 10, 0, 10));
        long count = result.stream().filter(s -> s == segments.get(0)).count();
        assertEquals(1, count, "Segment 0 must appear exactly once");
    }

    // ---- unbounded windows ----

    @Test
    void openLeft_unboundedXMin() {
        List<Segment> result = query(new Window(Double.NEGATIVE_INFINITY, 10, 0, 10));
        assertTrue(result.contains(segments.get(0)));
        assertTrue(result.contains(segments.get(1)));
        assertTrue(result.contains(segments.get(2)));
        assertTrue(result.contains(segments.get(3)));
    }

    @Test
    void openRight_unboundedXMax() {
        List<Segment> result = query(new Window(0, Double.POSITIVE_INFINITY, 0, 10));
        assertTrue(result.contains(segments.get(0)));  // y=5 in [0,10]
        assertTrue(result.contains(segments.get(1)));  // x=5 in [0,+∞)
        assertFalse(result.contains(segments.get(4))); // y=50 not in [0,10]
    }

    @Test
    void yStrip_unboundedBothX() {
        List<Segment> result = query(new Window(
                Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 4, 6));
        assertTrue(result.contains(segments.get(0)));  // y=5 in [4,6]
        assertTrue(result.contains(segments.get(2)));  // y=5 in [4,6]
        assertFalse(result.contains(segments.get(4))); // y=50 not in [4,6]
    }

    @Test
    void unboundedY_bottomless() {
        List<Segment> result = query(new Window(0, 10, Double.NEGATIVE_INFINITY, 10));
        assertTrue(result.contains(segments.get(0)));
        assertTrue(result.contains(segments.get(1)));
    }

    @Test
    void unboundedY_topless() {
        List<Segment> result = query(new Window(0, 10, 0, Double.POSITIVE_INFINITY));
        assertTrue(result.contains(segments.get(0)));
        assertTrue(result.contains(segments.get(1)));
    }

    private List<Segment> query(Window w) {
        return PstWindowing.findIntersectingSegments(index, segments, w);
    }
}
