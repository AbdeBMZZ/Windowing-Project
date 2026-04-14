package org.windowing.windowingproject.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.windowing.windowingproject.model.Segment;
import org.windowing.windowingproject.model.Window;

import java.util.List;

/**
 * Canvas drawing of segments and query windows (world y is flipped for screen coordinates).
 */
public class DrawingPane extends Canvas {

    public DrawingPane(double width, double height) {
        super(width, height);
    }

    public void drawSegments(List<Segment> segments) {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        gc.setStroke(Color.BLACK);
        for (Segment s : segments) {
            gc.strokeLine(
                    s.getP1().getX(),
                    getHeight() - s.getP1().getY(),
                    s.getP2().getX(),
                    getHeight() - s.getP2().getY());
        }
    }

    /**
     * Draws the window as a rectangle when all sides are finite; otherwise skips drawing
     * (unbounded windows cannot be stroked meaningfully).
     */
    public void drawWindow(Window w) {
        if (!isFinite(w.getXMin()) || !isFinite(w.getXMax())
                || !isFinite(w.getYMin()) || !isFinite(w.getYMax())) {
            return;
        }
        double rw = w.getXMax() - w.getXMin();
        double rh = w.getYMax() - w.getYMin();
        if (rw <= 0 || rh <= 0) {
            return;
        }
        GraphicsContext gc = getGraphicsContext2D();
        gc.setStroke(Color.RED);
        gc.strokeRect(
                w.getXMin(),
                getHeight() - w.getYMax(),
                rw,
                rh);
    }

    public void highlightSegments(List<Segment> segments) {
        GraphicsContext gc = getGraphicsContext2D();
        gc.setStroke(Color.BLUE);

        for (Segment s : segments) {
            gc.strokeLine(
                    s.getP1().getX(),
                    getHeight() - s.getP1().getY(),
                    s.getP2().getX(),
                    getHeight() - s.getP2().getY());
        }
    }

    private static boolean isFinite(double v) {
        return !Double.isInfinite(v) && !Double.isNaN(v);
    }
}
