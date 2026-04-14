package org.windowing.windowingproject.strategy;

import org.windowing.windowingproject.model.Segment;
import org.windowing.windowingproject.model.Window;
import org.windowing.windowingproject.pst.PrioritySearchTree;

import java.util.ArrayList;
import java.util.List;

public class RightBoundedWindowStrategy implements WindowingStrategy {

    @Override
    public List<Segment> execute(PrioritySearchTree pst,
                                 List<Segment> segments,
                                 Window window) {

        Window adapted = new Window(
                window.getXMin(),
                Double.POSITIVE_INFINITY,
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
