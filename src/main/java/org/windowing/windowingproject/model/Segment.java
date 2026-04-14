package org.windowing.windowingproject.model;

/**
 * Line segment between two points in the plane. Windowing uses axis-aligned
 * bounding box
 * intersection with the query window (valid for horizontal/vertical segments
 * from the assignment).
 */
public class Segment {

    private final Point2D p1;
    private final Point2D p2;

    public Segment(Point2D p1, Point2D p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public Point2D getP1() {
        return p1;
    }

    public Point2D getP2() {
        return p2;
    }

    /**
     * Closed intersection with an axis-aligned query window (supports {@code ±∞}
     * bounds).
     *
     * @param w query window
     * @return true iff the segment's bounding box intersects {@code w}'s box
     */
    public boolean intersects(Window window) {
        // Limites de la fenêtre
        double wLeft = window.getXMin();
        double wRight = window.getXMax();
        double wBottom = window.getYMin();
        double wTop = window.getYMax();

        // Récupération des coordonnées du segment
        // (Suppose que vous avez des méthodes getX() et getY() sur vos Point2D)
        double minX = Math.min(this.getP1().getX(), this.getP2().getX());
        double maxX = Math.max(this.getP1().getX(), this.getP2().getX());
        double minY = Math.min(this.getP1().getY(), this.getP2().getY());
        double maxY = Math.max(this.getP1().getY(), this.getP2().getY());

        // 1. Cas d'un segment HORIZONTAL (y1 == y2)
        if (minY == maxY) {
            // Le segment est à une hauteur (Y) contenue dans la fenêtre
            boolean isYInside = minY >= wBottom && minY <= wTop;
            // Le segment chevauche la largeur (X) de la fenêtre
            boolean isXOverlapping = minX <= wRight && maxX >= wLeft;

            return isYInside && isXOverlapping;
        }
        // 2. Cas d'un segment VERTICAL (x1 == x2)
        else if (minX == maxX) {
            // Le segment est à une position (X) contenue dans la fenêtre
            boolean isXInside = minX >= wLeft && minX <= wRight;
            // Le segment chevauche la hauteur (Y) de la fenêtre
            boolean isYOverlapping = minY <= wTop && maxY >= wBottom;

            return isXInside && isYOverlapping;
        }

        // Sécurité au cas où il y aurait des points simples ou des segments non
        // orthogonaux
        return false;
    }
}
