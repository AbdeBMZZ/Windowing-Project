package org.windowing.windowingproject.strategy;

import org.windowing.windowingproject.model.Segment;
import org.windowing.windowingproject.model.Window;
import org.windowing.windowingproject.pst.PstIndex;
import org.windowing.windowingproject.pst.PstWindowing;

import java.util.List;

/**
 * Window unbounded below in y: {@code [xMin, xMax] × (-∞, yMax]}.
 */
public class BottomBoundedWindowStrategy implements WindowingStrategy {

    @Override
    public List<Segment> execute(PstIndex pstIndex, List<Segment> segments, Window window) {
        Window adapted = new Window(
                window.getXMin(),
                window.getXMax(),
                Double.NEGATIVE_INFINITY,
                window.getYMax());
        return PstWindowing.findIntersectingSegments(pstIndex, segments, adapted);
    }
}
