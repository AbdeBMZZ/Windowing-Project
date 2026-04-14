package org.windowing.windowingproject.strategy;

import org.windowing.windowingproject.model.Segment;
import org.windowing.windowingproject.model.Window;
import org.windowing.windowingproject.pst.PrioritySearchTree;

import java.util.List;

public class WindowingContext {

    private WindowingStrategy strategy;

    public void setStrategy(WindowingStrategy strategy) {
        this.strategy = strategy;
    }

    public List<Segment> executeStrategy(
            PrioritySearchTree pst,
            List<Segment> segments,
            Window window) {

        if (strategy == null) {
            throw new IllegalStateException("Strategy not set.");
        }
        return strategy.execute(pst, segments, window);
    }
}
