package org.windowing.windowingproject.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Arbre d'intervalles permettant de trouver en O(log n + k) les segments
 * qui traversent complètement une coordonnée donnée.
 */
public class IntervalTree {
    private double midPoint;
    private List<Segment> leftSorted;
    private List<Segment> rightSorted;
    private IntervalTree leftChild;
    private IntervalTree rightChild;
    private boolean isHorizontal;

    public IntervalTree(List<Segment> segments, boolean isHorizontal) {
        this.isHorizontal = isHorizontal;
        if (segments == null || segments.isEmpty()) return;

        // Trouver la médiane de tous les points d'extrémité
        List<Double> endpoints = new ArrayList<>();
        for (Segment s : segments) {
            endpoints.add(getMin(s));
            endpoints.add(getMax(s));
        }
        endpoints.sort(Double::compareTo);
        this.midPoint = endpoints.get(endpoints.size() / 2);

        List<Segment> midSegments = new ArrayList<>();
        List<Segment> leftSegments = new ArrayList<>();
        List<Segment> rightSegments = new ArrayList<>();

        // Répartir les segments
        for (Segment s : segments) {
            if (getMax(s) < midPoint) leftSegments.add(s);
            else if (getMin(s) > midPoint) rightSegments.add(s);
            else midSegments.add(s);
        }

        // Trier les segments du milieu
        this.leftSorted = new ArrayList<>(midSegments);
        this.leftSorted.sort(Comparator.comparingDouble(this::getMin));

        this.rightSorted = new ArrayList<>(midSegments);
        this.rightSorted.sort((s1, s2) -> Double.compare(getMax(s2), getMax(s1)));

        // Construire les enfants récursivement
        if (!leftSegments.isEmpty()) this.leftChild = new IntervalTree(leftSegments, isHorizontal);
        if (!rightSegments.isEmpty()) this.rightChild = new IntervalTree(rightSegments, isHorizontal);
    }

    private double getMin(Segment s) {
        return isHorizontal ? Math.min(s.getP1().getX(), s.getP2().getX()) : Math.min(s.getP1().getY(), s.getP2().getY());
    }

    private double getMax(Segment s) {
        return isHorizontal ? Math.max(s.getP1().getX(), s.getP2().getX()) : Math.max(s.getP1().getY(), s.getP2().getY());
    }

    /**
     * Trouve les segments qui traversent 'point' ET dont l'autre coordonnée est dans [crossMin, crossMax]
     */
    public void queryCrossing(double point, double crossMin, double crossMax, List<Segment> result) {
        if (this.leftSorted == null) return;

        if (point < midPoint) {
            for (Segment s : leftSorted) {
                if (getMin(s) <= point) {
                    double crossVal = isHorizontal ? s.getP1().getY() : s.getP1().getX();
                    if (crossVal >= crossMin && crossVal <= crossMax) result.add(s);
                } else break; // On s'arrête tôt grâce au tri !
            }
            if (leftChild != null) leftChild.queryCrossing(point, crossMin, crossMax, result);
        } else {
            for (Segment s : rightSorted) {
                if (getMax(s) >= point) {
                    double crossVal = isHorizontal ? s.getP1().getY() : s.getP1().getX();
                    if (crossVal >= crossMin && crossVal <= crossMax) result.add(s);
                } else break; // On s'arrête tôt grâce au tri !
            }
            if (rightChild != null) rightChild.queryCrossing(point, crossMin, crossMax, result);
        }
    }
}