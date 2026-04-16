package org.windowing.windowingproject.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.windowing.windowingproject.model.Segment;
import org.windowing.windowingproject.model.Window;

import java.util.List;
import java.util.function.Consumer;

/**
 * JavaFX canvas pane that renders axis-parallel segments and a query window.
 *
 * <p>Coordinate transform: segment data lives in world coordinates; the canvas
 * renders in screen (pixel) coordinates.  Call {@link #setWorldBounds(Window)}
 * once after loading a file to establish the mapping.  If no world bounds are set,
 * world coordinates are used directly (identity transform).</p>
 *
 * <p>Mouse drag interaction: press-drag-release draws a selection rectangle whose
 * corners are converted to world coordinates before firing the
 * {@link #setOnWindowSelected(java.util.function.Consumer)} callback.</p>
 *
 * <p>Render order (back to front): all segments (gray) → highlighted segments (green)
 * → query window rectangle (blue, dashed) — the window is always on top.</p>
 */
public class DrawingPane extends Pane {
    private final Canvas canvas;
    private double dragX1, dragY1, dragX2, dragY2;
    private boolean isDragging = false;
    private Consumer<Window> onWindowSelected;

    private Window worldBounds;

    public DrawingPane() {
        canvas = new Canvas();
        this.getChildren().add(canvas);
        canvas.widthProperty().bind(this.widthProperty());
        canvas.heightProperty().bind(this.heightProperty());

        canvas.setOnMousePressed(e -> {
            dragX1 = e.getX();
            dragY1 = e.getY();
            isDragging = true;
        });

        canvas.setOnMouseDragged(e -> {
            dragX2 = e.getX();
            dragY2 = e.getY();
            repaint();
        });

        canvas.setOnMouseReleased(e -> {
            isDragging = false;
            dragX2 = e.getX();
            dragY2 = e.getY();

            double wx1 = screenToWorldX(Math.min(dragX1, dragX2));
            double wx2 = screenToWorldX(Math.max(dragX1, dragX2));
            double wy1 = screenToWorldY(Math.min(dragY1, dragY2));
            double wy2 = screenToWorldY(Math.max(dragY1, dragY2));

            if (onWindowSelected != null) {
                onWindowSelected.accept(new Window(wx1, wx2, wy1, wy2));
            }
        });

        canvas.widthProperty().addListener(evt -> repaint());
        canvas.heightProperty().addListener(evt -> repaint());
    }

    /**
     * Sets the world-space bounding box used for coordinate transforms.
     * Must be called after loading a new file.
     *
     * @param bounds world bounding window from the data file
     */
    public void setWorldBounds(Window bounds) {
        this.worldBounds = bounds;
        repaint();
    }

    /**
     * Registers a callback invoked when the user finishes a drag selection.
     * The callback receives a {@link Window} in <em>world</em> coordinates.
     *
     * @param callback consumer receiving the selected window
     */
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

    // ---- coordinate transforms ----

    private double worldToScreenX(double wx) {
        if (worldBounds == null) return wx;
        double w = canvas.getWidth();
        return (wx - worldBounds.getXMin()) / (worldBounds.getXMax() - worldBounds.getXMin()) * w;
    }

    private double worldToScreenY(double wy) {
        if (worldBounds == null) return wy;
        double h = canvas.getHeight();
        return (wy - worldBounds.getYMin()) / (worldBounds.getYMax() - worldBounds.getYMin()) * h;
    }

    private double screenToWorldX(double sx) {
        if (worldBounds == null) return sx;
        double w = canvas.getWidth();
        return sx / w * (worldBounds.getXMax() - worldBounds.getXMin()) + worldBounds.getXMin();
    }

    private double screenToWorldY(double sy) {
        if (worldBounds == null) return sy;
        double h = canvas.getHeight();
        return sy / h * (worldBounds.getYMax() - worldBounds.getYMin()) + worldBounds.getYMin();
    }

    // ---- rendering ----

    private void repaint() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        if (currentSegments != null) {
            gc.setStroke(Color.web("#6c7086"));
            gc.setLineWidth(1.0);
            for (Segment s : currentSegments) {
                gc.strokeLine(
                        worldToScreenX(s.getP1().getX()), worldToScreenY(s.getP1().getY()),
                        worldToScreenX(s.getP2().getX()), worldToScreenY(s.getP2().getY()));
            }
        }

        if (currentHighlighted != null) {
            gc.setStroke(Color.web("#a6e3a1"));
            gc.setLineWidth(2.5);
            for (Segment s : currentHighlighted) {
                gc.strokeLine(
                        worldToScreenX(s.getP1().getX()), worldToScreenY(s.getP1().getY()),
                        worldToScreenX(s.getP2().getX()), worldToScreenY(s.getP2().getY()));
            }
        }

        // Draw window on top so it is always visible
        if (isDragging) {
            drawPreviewWindowScreen(gc, dragX1, dragY1, dragX2, dragY2);
        } else if (currentWindow != null) {
            drawPreviewWindowScreen(gc,
                    worldToScreenX(currentWindow.getXMin()), worldToScreenY(currentWindow.getYMin()),
                    worldToScreenX(currentWindow.getXMax()), worldToScreenY(currentWindow.getYMax()));
        }
    }

    private void drawPreviewWindowScreen(GraphicsContext gc, double x1, double y1, double x2, double y2) {
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
