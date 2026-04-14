package org.windowing.windowingproject.strategy;

import org.windowing.windowingproject.model.Segment;
import org.windowing.windowingproject.model.Window;
import org.windowing.windowingproject.pst.PstIndex;

import java.util.List;

/**
 * Selects a {@link WindowingStrategy} and runs it on a {@link PstIndex}.
 */
public class WindowingContext {

    private WindowingStrategy strategy;

    public void setStrategy(WindowingStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * @throws IllegalStateException if no strategy was set
     */
    public List<Segment> executeStrategy(PstIndex pstIndex, List<Segment> segments, Window window) {
        if (strategy == null) {
            throw new IllegalStateException("Strategy not set.");
        }
        return strategy.execute(pstIndex, segments, window);
    }
}
