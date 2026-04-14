package org.windowing.windowingproject.strategy;

import org.windowing.windowingproject.model.Segment;
import org.windowing.windowingproject.model.Window;
import org.windowing.windowingproject.pst.PrioritySearchTree;

import java.util.ArrayList;
import java.util.List;

public class LeftBoundedWindowStrategy implements WindowingStrategy {

    @Override
    public List<Segment> execute(PrioritySearchTree pst,
                                 List<Segment> segments,
                                 Window window) {

        Window adapted = new Window(
                Double.NEGATIVE_INFINITY,
                window.getXMax(),
                window.getYMin(),
                window.getYMax()
        );

        List<Segment> result = new ArrayList<>();
        for (Segment s : segments) {
            if (s.intersects(adapted)) {
                result.add(s);
            }
        }
        return result;
    }
}
