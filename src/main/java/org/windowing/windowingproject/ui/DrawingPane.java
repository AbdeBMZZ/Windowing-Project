package org.windowing.windowingproject.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.windowing.windowingproject.model.Segment;
import org.windowing.windowingproject.model.Window;

import java.util.List;

public class DrawingPane extends Pane {
    private Canvas canvas;

    public DrawingPane(double width, double height) {
        // Initialiser le canvas
        canvas = new Canvas(width, height);
        this.getChildren().add(canvas);

        // Lier la taille du canvas à celle du panneau pour le redimensionnement
        canvas.widthProperty().bind(this.widthProperty());
        canvas.heightProperty().bind(this.heightProperty());
        
        // S'assurer que le redessin se fait si la fenêtre change de taille (Optionnel mais recommandé)
        canvas.widthProperty().addListener(evt -> repaint());
        canvas.heightProperty().addListener(evt -> repaint());
    }

    // --- Variables pour stocker l'état actuel (utile pour le redimensionnement) ---
    private List<Segment> currentSegments;
    private Window currentWindow;
    private List<Segment> currentHighlighted;

    /**
     * 1. Dessine tous les segments de base (nettoie l'écran d'abord)
     */
    public void drawSegments(List<Segment> segments) {
        this.currentSegments = segments;
        this.currentWindow = null;       // Reset la fenêtre
        this.currentHighlighted = null;  // Reset la surbrillance
        repaint();
    }

    /**
     * 2. Dessine la fenêtre de requête par-dessus
     */
    public void drawWindow(Window window) {
        this.currentWindow = window;
        repaint();
    }

    /**
     * 3. Met en surbrillance les segments trouvés
     */
    public void highlightSegments(List<Segment> highlighted) {
        this.currentHighlighted = highlighted;
        repaint();
    }

    /**
     * Méthode interne qui s'occupe de tout dessiner dans le bon ordre
     */
    private void repaint() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // 0. Nettoyer tout le canvas avant de redessiner
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // 1. Dessiner les segments standards (Gris discret)
        if (currentSegments != null) {
            gc.setStroke(Color.web("#6c7086"));
            gc.setLineWidth(1.5);
            for (Segment s : currentSegments) {
                // On ne dessine en gris que s'il n'est pas dans la liste des highlight
                if (currentHighlighted == null || !currentHighlighted.contains(s)) {
                    gc.strokeLine(s.getP1().getX(), s.getP1().getY(), s.getP2().getX(), s.getP2().getY());
                }
            }
        }

        // 2. Dessiner la fenêtre de requête
        if (currentWindow != null) {
            // Gestion de l'infini (Si l'utilisateur laisse le champ vide)
            double drawMinX = currentWindow.getXMin() == Double.NEGATIVE_INFINITY ? 0 : currentWindow.getXMin();
            double drawMaxX = currentWindow.getXMax() == Double.POSITIVE_INFINITY ? canvas.getWidth() : currentWindow.getXMax();
            double drawMinY = currentWindow.getYMin() == Double.NEGATIVE_INFINITY ? 0 : currentWindow.getYMin();
            double drawMaxY = currentWindow.getYMax() == Double.POSITIVE_INFINITY ? canvas.getHeight() : currentWindow.getYMax();

            double w = drawMaxX - drawMinX;
            double h = drawMaxY - drawMinY;

            // Remplissage semi-transparent
            gc.setFill(Color.web("rgba(137, 180, 250, 0.2)"));
            gc.fillRect(drawMinX, drawMinY, w, h);

            // Bordure en pointillés
            gc.setStroke(Color.web("#89b4fa"));
            gc.setLineWidth(2.0);
            gc.setLineDashes(5, 5); // Pointillés
            gc.strokeRect(drawMinX, drawMinY, w, h);
            gc.setLineDashes(null); // Reset pour les prochains dessins
        }

        // 3. Dessiner les segments trouvés (Vert vif et épais)
        if (currentHighlighted != null) {
            gc.setStroke(Color.web("#a6e3a1"));
            gc.setLineWidth(3.0);
            gc.setFill(Color.web("#a6e3a1"));

            for (Segment s : currentHighlighted) {
                gc.strokeLine(s.getP1().getX(), s.getP1().getY(), s.getP2().getX(), s.getP2().getY());
                
                // Dessiner les extrémités (les points) pour plus de clarté
                gc.fillOval(s.getP1().getX() - 3, s.getP1().getY() - 3, 6, 6);
                gc.fillOval(s.getP2().getX() - 3, s.getP2().getY() - 3, 6, 6);
            }
        }
    }
}