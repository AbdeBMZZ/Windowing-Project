package org.windowing.windowingproject.strategy;

import org.windowing.windowingproject.model.Segment;
import org.windowing.windowingproject.model.Window;
import org.windowing.windowingproject.pst.PrioritySearchTree;

import java.util.List;

public interface WindowingStrategy {
    List<Segment> execute(
            PrioritySearchTree pst,
            List<Segment> segments,
            Window window);
}
