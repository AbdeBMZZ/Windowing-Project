package org.windowing.windowingproject.ui;

import org.windowing.windowingproject.model.*;
import org.windowing.windowingproject.pst.*;
import org.windowing.windowingproject.strategy.*;
import org.windowing.windowingproject.util.FileLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Business-logic controller: owns the PST index and dispatches windowing queries
 * via the Strategy pattern. UI layer calls load() once, then query() for each window.
 */
public class WindowingController {

    private List<Segment> segments = new ArrayList<>();
    private PstIndex pstIndex;
    private Window worldBounds;

    public void load(String path) throws Exception {
        FileLoader loader = new FileLoader();
        List<Segment> loaded = loader.loadSegments(path);
        worldBounds = loader.getWindow();

        List<PstEntry> entries = new ArrayList<>();
        List<Segment> horizSegs = new ArrayList<>();
        List<Segment> vertSegs = new ArrayList<>();

        for (int i = 0; i < loaded.size(); i++) {
            Segment s = loaded.get(i);
            entries.add(new PstEntry(s.getP1().getX(), s.getP1().getY(), i, 0));
            entries.add(new PstEntry(s.getP2().getX(), s.getP2().getY(), i, 1));
            if (s.getP1().getY() == s.getP2().getY()) {
                horizSegs.add(s);
            } else {
                vertSegs.add(s);
            }
        }

        PrioritySearchTree forward  = new PrioritySearchTree(entries, false);
        PrioritySearchTree negatedX = new PrioritySearchTree(entries, true);
        IntervalTree hTree = new IntervalTree(horizSegs, true);
        IntervalTree vTree = new IntervalTree(vertSegs,  false);

        pstIndex = new PstIndex(forward, negatedX, hTree, vTree);
        segments = loaded;
    }

    public List<Segment> query(Window window) {
        if (!isLoaded()) {
            throw new IllegalStateException("No file loaded.");
        }
        WindowingContext ctx = new WindowingContext();
        ctx.setStrategy(selectStrategy(window));
        return ctx.executeStrategy(pstIndex, segments, window);
    }

    private WindowingStrategy selectStrategy(Window w) {
        if (w.getXMin() == Double.NEGATIVE_INFINITY) return new LeftBoundedWindowStrategy();
        if (w.getXMax() == Double.POSITIVE_INFINITY)  return new RightBoundedWindowStrategy();
        if (w.getYMin() == Double.NEGATIVE_INFINITY)  return new BottomBoundedWindowStrategy();
        if (w.getYMax() == Double.POSITIVE_INFINITY)  return new TopBoundedWindowStrategy();
        return new BoundedWindowStrategy();
    }

    public boolean isLoaded() {
        return pstIndex != null;
    }

    public List<Segment> getSegments() {
        return Collections.unmodifiableList(segments);
    }

    public Window getWorldBounds() {
        return worldBounds;
    }
}
