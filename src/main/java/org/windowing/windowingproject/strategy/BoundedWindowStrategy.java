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
        // On passe directement l'index complet (qui contient maintenant les PST et les IntervalTrees)
        return PstWindowing.findIntersectingSegments(pstIndex, segments, window);
    }
}