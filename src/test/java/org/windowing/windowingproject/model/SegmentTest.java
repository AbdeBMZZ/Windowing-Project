package org.windowing.windowingproject.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SegmentTest {

    private static Segment horiz(double x1, double y, double x2) {
        return new Segment(new Point2D(x1, y), new Point2D(x2, y));
    }

    private static Segment vert(double x, double y1, double y2) {
        return new Segment(new Point2D(x, y1), new Point2D(x, y2));
    }

    // ---- horizontal segments ----

    @Test
    void horizontal_endpointInsideWindow() {
        assertTrue(horiz(0, 5, 10).intersects(new Window(3, 15, 0, 10)));
    }

    @Test
    void horizontal_fullyInsideWindow() {
        assertTrue(horiz(2, 5, 8).intersects(new Window(0, 10, 0, 10)));
    }

    @Test
    void horizontal_spanningWindowInX() {
        assertTrue(horiz(-100, 5, 100).intersects(new Window(0, 10, 0, 10)));
    }

    @Test
    void horizontal_touchingYBoundary() {
        assertTrue(horiz(0, 0, 10).intersects(new Window(0, 10, 0, 10)));
        assertTrue(horiz(0, 10, 10).intersects(new Window(0, 10, 0, 10)));
    }

    @Test
    void horizontal_outsideYRange() {
        assertFalse(horiz(0, 50, 10).intersects(new Window(0, 10, 0, 10)));
        assertFalse(horiz(0, -1, 10).intersects(new Window(0, 10, 0, 10)));
    }

    @Test
    void horizontal_outsideXRange() {
        assertFalse(horiz(20, 5, 30).intersects(new Window(0, 10, 0, 10)));
    }

    @Test
    void horizontal_touchingXBoundary() {
        assertTrue(horiz(-5, 5, 0).intersects(new Window(0, 10, 0, 10)));
        assertTrue(horiz(10, 5, 20).intersects(new Window(0, 10, 0, 10)));
    }

    // ---- vertical segments ----

    @Test
    void vertical_endpointInsideWindow() {
        assertTrue(vert(5, 0, 10).intersects(new Window(0, 10, 3, 15)));
    }

    @Test
    void vertical_spanningWindowInY() {
        assertTrue(vert(5, -100, 100).intersects(new Window(0, 10, 0, 10)));
    }

    @Test
    void vertical_touchingXBoundary() {
        assertTrue(vert(0, 0, 10).intersects(new Window(0, 10, 0, 10)));
        assertTrue(vert(10, 0, 10).intersects(new Window(0, 10, 0, 10)));
    }

    @Test
    void vertical_outsideXRange() {
        assertFalse(vert(50, 0, 10).intersects(new Window(0, 10, 0, 10)));
    }

    @Test
    void vertical_outsideYRange() {
        assertFalse(vert(5, 20, 30).intersects(new Window(0, 10, 0, 10)));
    }

    // ---- infinite window bounds ----

    @Test
    void infiniteWindow_alwaysIntersects() {
        Window inf = new Window(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
                                Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        assertTrue(horiz(5, 5, 10).intersects(inf));
        assertTrue(vert(5, 5, 10).intersects(inf));
    }

    @Test
    void halfInfiniteWindow_horizontal() {
        Window w = new Window(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0, 10);
        assertTrue(horiz(5, 5, 10).intersects(w));
        assertFalse(horiz(5, 20, 10).intersects(w));
    }
}
