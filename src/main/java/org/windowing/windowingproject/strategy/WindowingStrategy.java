package org.windowing.windowingproject.strategy;

import org.windowing.windowingproject.model.Segment;
import org.windowing.windowingproject.model.Window;
import org.windowing.windowingproject.pst.PstIndex;

import java.util.List;

/**
 * Strategy for reporting segments that intersect a (possibly unbounded) query window using PST-backed search.
 */
public interface WindowingStrategy {

    /**
     * @param pstIndex pair of PSTs built from segment endpoints
     * @param segments full segment list (same order as when building {@code pstIndex})
     * @param window   query window in world coordinates
     * @return segments intersecting the window
     */
    List<Segment> execute(PstIndex pstIndex, List<Segment> segments, Window window);
}
