package org.windowing.windowingproject.strategy;

import org.windowing.windowingproject.model.Segment;
import org.windowing.windowingproject.model.Window;
import org.windowing.windowingproject.pst.PstIndex;
import org.windowing.windowingproject.pst.PstWindowing;

import java.util.List;

/**
 * Window unbounded to the left: {@code (-∞, xMax] × [yMin, yMax]}.
 */
public class LeftBoundedWindowStrategy implements WindowingStrategy {

    @Override
    public List<Segment> execute(PstIndex pstIndex, List<Segment> segments, Window window) {
        Window adapted = new Window(
                Double.NEGATIVE_INFINITY,
                window.getXMax(),
                window.getYMin(),
                window.getYMax());
        return PstWindowing.findIntersectingSegments(
                pstIndex.getForward(),
                pstIndex.getNegatedX(),
                segments,
                adapted);
    }
}
