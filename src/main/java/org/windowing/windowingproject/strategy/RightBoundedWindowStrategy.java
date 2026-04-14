package org.windowing.windowingproject.strategy;

import org.windowing.windowingproject.model.Segment;
import org.windowing.windowingproject.model.Window;
import org.windowing.windowingproject.pst.PstIndex;
import org.windowing.windowingproject.pst.PstWindowing;

import java.util.List;

/**
 * Window unbounded to the right: {@code [xMin, +∞) × [yMin, yMax]}.
 */
public class RightBoundedWindowStrategy implements WindowingStrategy {

    @Override
    public List<Segment> execute(PstIndex pstIndex, List<Segment> segments, Window window) {
        Window adapted = new Window(
                window.getXMin(),
                Double.POSITIVE_INFINITY,
                window.getYMin(),
                window.getYMax());
        return PstWindowing.findIntersectingSegments(
                pstIndex.getForward(),
                pstIndex.getNegatedX(),
                segments,
                adapted);
    }
}
