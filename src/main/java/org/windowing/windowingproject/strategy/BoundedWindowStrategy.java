package org.windowing.windowingproject.strategy;

import org.windowing.windowingproject.model.Segment;
import org.windowing.windowingproject.model.Window;
import org.windowing.windowingproject.pst.PstIndex;
import org.windowing.windowingproject.pst.PstWindowing;

import java.util.List;

/**
 * Fully bounded query window {@code [xMin, xMax] × [yMin, yMax]}.
 */
public class BoundedWindowStrategy implements WindowingStrategy {

    @Override
    public List<Segment> execute(PstIndex pstIndex, List<Segment> segments, Window window) {
        return PstWindowing.findIntersectingSegments(
                pstIndex.getForward(),
                pstIndex.getNegatedX(),
                segments,
                window);
    }
}
