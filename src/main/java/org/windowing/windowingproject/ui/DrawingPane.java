package org.windowing.windowingproject.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.windowing.windowingproject.model.Segment;
import org.windowing.windowingproject.model.Window;

import java.util.List;

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
                    getHeight() - s.getP2().getY()
            );
        }
    }

    public void drawWindow(Window w) {
        GraphicsContext gc = getGraphicsContext2D();
        gc.setStroke(Color.RED);
        gc.strokeRect(
                w.getXMin(),
                getHeight() - w.getYMax(),
                w.getXMax() - w.getXMin(),
                w.getYMax() - w.getYMin()
        );
    }

    public void highlightSegments(List<Segment> segments) {
        GraphicsContext gc = getGraphicsContext2D();
        gc.setStroke(Color.BLUE);

        for (Segment s : segments) {
            gc.strokeLine(
                    s.getP1().getX(),
                    getHeight() - s.getP1().getY(),
                    s.getP2().getX(),
                    getHeight() - s.getP2().getY()
            );
        }
    }
}
