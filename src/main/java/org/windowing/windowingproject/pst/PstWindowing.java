package org.windowing.windowingproject.pst;

import org.windowing.windowingproject.model.PstEntry;
import org.windowing.windowingproject.model.Segment;
import org.windowing.windowingproject.model.Window;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class PstWindowing {

    private PstWindowing() {}

    private static boolean isNegInf(double v) { return v == Double.NEGATIVE_INFINITY; }
    private static boolean isPosInf(double v) { return v == Double.POSITIVE_INFINITY; }

    public static List<Segment> findIntersectingSegments(PstIndex index, List<Segment> segments, Window window) {
        double xMin = window.getXMin();
        double xMax = window.getXMax();
        double yMin = window.getYMin();
        double yMax = window.getYMax();

        List<PstEntry> endpointHits = new ArrayList<>();
        boolean xOpenLeft = isNegInf(xMin);
        boolean xOpenRight = isPosInf(xMax);

        // 1. REQUÊTES PST (Pour les extrémités contenues dans la fenêtre)
        if (xOpenLeft && xOpenRight) {
            endpointHits.addAll(index.getForward().queryYStrip(yMin, yMax));
        } else if (xOpenRight) {
            endpointHits.addAll(index.getNegatedX().queryOpenLeft(-xMin, yMin, yMax));
            if (!isPosInf(xMax)) endpointHits.removeIf(e -> e.getX() > xMax);
        } else {
            endpointHits.addAll(index.getForward().queryOpenLeft(xMax, yMin, yMax));
            if (!xOpenLeft) endpointHits.removeIf(e -> e.getX() < xMin);
        }

        // Utilisation d'un Set (O(1)) pour éviter les doublons au lieu d'un tableau boolean[]
        Set<Segment> resultSet = new HashSet<>();
        for (PstEntry e : endpointHits) {
            int i = e.getSegmentIndex();
            if (i >= 0 && i < segments.size()) {
                resultSet.add(segments.get(i));
            }
        }

        // 2. REQUÊTES INTERVAL TREE (Pour les segments traversants de bout en bout)
        List<Segment> crossingSegments = new ArrayList<>();
        
        // Segments horizontaux traversant le bord gauche
        if (!isNegInf(xMin) && index.getHorizontalTree() != null) {
            index.getHorizontalTree().queryCrossing(xMin, yMin, yMax, crossingSegments);
        }
        
        // Segments verticaux traversant le bord inférieur
        if (!isNegInf(yMin) && index.getVerticalTree() != null) {
            index.getVerticalTree().queryCrossing(yMin, xMin, xMax, crossingSegments);
        }

        resultSet.addAll(crossingSegments);
        return new ArrayList<>(resultSet);
    }
}