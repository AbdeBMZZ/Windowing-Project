package org.windowing.windowingproject.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.windowing.windowingproject.model.Segment;
import org.windowing.windowingproject.model.Window;

import java.util.List;
import java.util.function.Consumer;

public class DrawingPane extends Pane {
    private Canvas canvas;
    private double dragX1, dragY1, dragX2, dragY2;
    private boolean isDragging = false;
    private Consumer<Window> onWindowSelected; // Callback pour HelloApplication

    public DrawingPane() {
        canvas = new Canvas();
        this.getChildren().add(canvas);
        canvas.widthProperty().bind(this.widthProperty());
        canvas.heightProperty().bind(this.heightProperty());

        // --- GESTION DE LA SOURIS ---
        canvas.setOnMousePressed(e -> {
            dragX1 = e.getX();
            dragY1 = e.getY();
            isDragging = true;
        });

        canvas.setOnMouseDragged(e -> {
            dragX2 = e.getX();
            dragY2 = e.getY();
            repaint(); // Dessine le rectangle en temps réel
        });

        canvas.setOnMouseReleased(e -> {
            isDragging = false;
            dragX2 = e.getX();
            dragY2 = e.getY();
            
            // Calcul de la fenêtre finale
            double xMin = Math.min(dragX1, dragX2);
            double xMax = Math.max(dragX1, dragX2);
            double yMin = Math.min(dragY1, dragY2);
            double yMax = Math.max(dragY1, dragY2);
            
            if (onWindowSelected != null) {
                onWindowSelected.accept(new Window(xMin, xMax, yMin, yMax));
            }
        });

        canvas.widthProperty().addListener(evt -> repaint());
        canvas.heightProperty().addListener(evt -> repaint());
    }

    public void setOnWindowSelected(Consumer<Window> callback) {
        this.onWindowSelected = callback;
    }

    private List<Segment> currentSegments;
    private Window currentWindow;
    private List<Segment> currentHighlighted;

    public void drawSegments(List<Segment> segments) {
        this.currentSegments = segments;
        repaint();
    }

    public void drawWindow(Window window) {
        this.currentWindow = window;
        repaint();
    }

    public void highlightSegments(List<Segment> highlighted) {
        this.currentHighlighted = highlighted;
        repaint();
    }

    private void repaint() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // 1. Segments de base
        if (currentSegments != null) {
            gc.setStroke(Color.web("#6c7086"));
            gc.setLineWidth(1.0);
            for (Segment s : currentSegments) {
                gc.strokeLine(s.getP1().getX(), s.getP1().getY(), s.getP2().getX(), s.getP2().getY());
            }
        }

        // 2. Fenêtre de requête (fixe ou en cours de drag)
        if (isDragging) {
            drawPreviewWindow(gc, dragX1, dragY1, dragX2, dragY2);
        } else if (currentWindow != null) {
            drawPreviewWindow(gc, currentWindow.getXMin(), currentWindow.getYMin(), 
                             currentWindow.getXMax(), currentWindow.getYMax());
        }

        // 3. Segments trouvés
        if (currentHighlighted != null) {
            gc.setStroke(Color.web("#a6e3a1"));
            gc.setLineWidth(2.5);
            for (Segment s : currentHighlighted) {
                gc.strokeLine(s.getP1().getX(), s.getP1().getY(), s.getP2().getX(), s.getP2().getY());
            }
        }
    }

    private void drawPreviewWindow(GraphicsContext gc, double x1, double y1, double x2, double y2) {
        double x = Math.min(x1, x2);
        double y = Math.min(y1, y2);
        double w = Math.abs(x1 - x2);
        double h = Math.abs(y1 - y2);

        gc.setFill(Color.web("rgba(137, 180, 250, 0.2)"));
        gc.fillRect(x, y, w, h);
        gc.setStroke(Color.web("#89b4fa"));
        gc.setLineDashes(5);
        gc.strokeRect(x, y, w, h);
        gc.setLineDashes(0);
    }
}