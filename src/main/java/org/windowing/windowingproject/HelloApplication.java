package org.windowing.windowingproject;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.windowing.windowingproject.model.*;
import org.windowing.windowingproject.pst.*;
import org.windowing.windowingproject.strategy.*;
import org.windowing.windowingproject.ui.DrawingPane;
import org.windowing.windowingproject.util.FileLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HelloApplication extends Application {
    private final List<Segment> segments = new ArrayList<>();
    private PstIndex pstIndex;
    private DrawingPane drawingPane;

    // Labels pour les statistiques
    private Label totalSegmentsLabel = new Label("Total : 0");
    private Label foundSegmentsLabel = new Label("Trouvés : 0");
    private Label timeLabel = new Label("Temps : 0 ms");

    private TextField xminField = new TextField();
    private TextField xmaxField = new TextField();
    private TextField yminField = new TextField();
    private TextField ymaxField = new TextField();

    @Override
    public void start(Stage stage) {
        drawingPane = new DrawingPane();
        drawingPane.setId("drawingPane");

        // --- BARRE LATÉRALE (SIDEBAR) ---
        VBox sidebar = new VBox(20);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(250);
        sidebar.setStyle("-fx-background-color: #313244;");

        // --- CRÉATION PROPRE DES BOUTONS ---
        Button loadBtn = new Button("Charger Fichier");
        Button queryBtn = new Button("Lancer Recherche");

        // Style CSS
        loadBtn.getStyleClass().add("button");
        queryBtn.getStyleClass().add("button");

        // --- LIAISON DES ACTIONS ---
        loadBtn.setOnAction(e -> loadFile(stage));
        queryBtn.setOnAction(e -> runWindowing());

        // --- MISE EN PAGE CLAIRE DES COORDONNÉES ---
        // 1. Ajouter un texte grisé (prompt) dans les boîtes
        xminField.setPromptText("Ex: 10.0");
        xmaxField.setPromptText("Ex: 50.0");
        yminField.setPromptText("Ex: 10.0");
        ymaxField.setPromptText("Ex: 50.0");

        // 2. Créer une grille pour aligner proprement les labels et les champs
        GridPane coordGrid = new GridPane();
        coordGrid.setHgap(10); // Espace horizontal
        coordGrid.setVgap(10); // Espace vertical
        
        coordGrid.add(new Label("xMin :"), 0, 0);
        coordGrid.add(xminField, 1, 0);
        
        coordGrid.add(new Label("xMax :"), 0, 1);
        coordGrid.add(xmaxField, 1, 1);
        
        coordGrid.add(new Label("yMin :"), 0, 2);
        coordGrid.add(yminField, 1, 2);
        
        coordGrid.add(new Label("yMax :"), 0, 3);
        coordGrid.add(ymaxField, 1, 3);

        // 3. Ajouter la grille au lieu des champs en vrac
        VBox controlSection = new VBox(15, new Label("CONFIGURATION"),
                loadBtn, coordGrid, queryBtn);

        VBox statsSection = new VBox(10, new Label("STATISTIQUES"),
                totalSegmentsLabel, foundSegmentsLabel, timeLabel);
        statsSection.setStyle("-fx-padding: 15; -fx-background-color: #1e1e2e; -fx-background-radius: 10;");

        sidebar.getChildren().addAll(controlSection, statsSection);

        // Liaison de la souris (Drag & Drop)
        drawingPane.setOnWindowSelected(window -> {
            xminField.setText(String.format(java.util.Locale.US, "%.1f", window.getXMin()));
            xmaxField.setText(String.format(java.util.Locale.US, "%.1f", window.getXMax()));
            yminField.setText(String.format(java.util.Locale.US, "%.1f", window.getYMin()));
            ymaxField.setText(String.format(java.util.Locale.US, "%.1f", window.getYMax()));
            runWindowing();
        });

        BorderPane root = new BorderPane();
        root.setLeft(sidebar);
        root.setCenter(drawingPane);

        Scene scene = new Scene(root, 1100, 800);

        // Chargement sécurisé du CSS
        java.net.URL cssUrl = getClass().getResource("/org/windowing/windowingproject/styles.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        stage.setTitle("Windowing Pro - PST & Interval Trees");
        stage.setScene(scene);
        stage.show();
    }

    private void runWindowing() {
        if (pstIndex == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez d'abord charger un fichier de segments !");
            alert.setHeaderText("Aucun fichier chargé");
            alert.show();
            return;
        }

        try {
            double xmin = Double.parseDouble(xminField.getText().replace(",", "."));
            double xmax = Double.parseDouble(xmaxField.getText().replace(",", "."));
            double ymin = Double.parseDouble(yminField.getText().replace(",", "."));
            double ymax = Double.parseDouble(ymaxField.getText().replace(",", "."));
            Window window = new Window(xmin, xmax, ymin, ymax);

            // --- MESURE DE PERFORMANCE ---
            long start = System.nanoTime();
            
            // Utilisation du Design Pattern Strategy que vous aviez implémenté
            WindowingContext context = new WindowingContext();
            if (xmin == Double.NEGATIVE_INFINITY) {
                context.setStrategy(new LeftBoundedWindowStrategy());
            } else if (xmax == Double.POSITIVE_INFINITY) {
                context.setStrategy(new RightBoundedWindowStrategy());
            } else {
                context.setStrategy(new BoundedWindowStrategy());
            }

            List<Segment> result = context.executeStrategy(pstIndex, segments, window);
            long end = System.nanoTime();

            // Mise à jour UI
            drawingPane.drawWindow(window);
            drawingPane.highlightSegments(result);

            foundSegmentsLabel.setText("Trouvés : " + result.size());
            timeLabel.setText(String.format("Temps : %.3f ms", (end - start) / 1_000_000.0));

        } catch (NumberFormatException e) {
            // Ignorer silencieusement si l'utilisateur efface un champ
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFile(Stage stage) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open Segments File");
        File file = chooser.showOpenDialog(stage);

        if (file == null) {
            return; // L'utilisateur a annulé
        }

        try {
            FileLoader loader = new FileLoader();
            segments.clear();
            segments.addAll(loader.loadSegments(file.getAbsolutePath()));
            Window fileWindow = loader.getWindow();

            List<PstEntry> entries = new ArrayList<>();
            List<Segment> horizSegments = new ArrayList<>();
            List<Segment> vertSegments = new ArrayList<>();

            for (int i = 0; i < segments.size(); i++) {
                Segment s = segments.get(i);
                entries.add(new PstEntry(s.getP1().getX(), s.getP1().getY(), i, 0));
                entries.add(new PstEntry(s.getP2().getX(), s.getP2().getY(), i, 1));

                if (s.getP1().getY() == s.getP2().getY()) {
                    horizSegments.add(s);
                } else {
                    vertSegments.add(s);
                }
            }

            PrioritySearchTree forward = new PrioritySearchTree(entries, false);
            PrioritySearchTree negatedX = new PrioritySearchTree(entries, true);
            IntervalTree hTree = new IntervalTree(horizSegments, true);
            IntervalTree vTree = new IntervalTree(vertSegments, false);

            pstIndex = new PstIndex(forward, negatedX, hTree, vTree);

            // Mise à jour de l'interface
            totalSegmentsLabel.setText("Total : " + segments.size());
            drawingPane.drawSegments(segments);
            
            if (fileWindow != null) {
                drawingPane.drawWindow(fileWindow);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors du chargement : " + e.getMessage());
            alert.show();
        }
    }
}