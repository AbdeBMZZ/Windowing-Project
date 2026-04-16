package org.windowing.windowingproject.strategy;

import org.windowing.windowingproject.model.Segment;
import org.windowing.windowingproject.model.Window;
import org.windowing.windowingproject.pst.PstIndex;
import org.windowing.windowingproject.pst.PstWindowing;

import java.util.List;

/**
 * Window unbounded above in y: {@code [xMin, xMax] × [yMin, +∞)}.
 */
public class TopBoundedWindowStrategy implements WindowingStrategy {

    @Override
    public List<Segment> execute(PstIndex pstIndex, List<Segment> segments, Window window) {
        Window adapted = new Window(
                window.getXMin(),
                window.getXMax(),
                window.getYMin(),
                Double.POSITIVE_INFINITY);
        return PstWindowing.findIntersectingSegments(pstIndex, segments, adapted);
    }
}
