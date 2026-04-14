package org.windowing.windowingproject;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.windowing.windowingproject.model.PstEntry;
import org.windowing.windowingproject.model.Segment;
import org.windowing.windowingproject.model.Window;
import org.windowing.windowingproject.pst.PstIndex;
import org.windowing.windowingproject.pst.PrioritySearchTree;
import org.windowing.windowingproject.strategy.BoundedWindowStrategy;
import org.windowing.windowingproject.strategy.LeftBoundedWindowStrategy;
import org.windowing.windowingproject.strategy.RightBoundedWindowStrategy;
import org.windowing.windowingproject.strategy.WindowingContext;
import org.windowing.windowingproject.strategy.WindowingStrategy;
import org.windowing.windowingproject.ui.DrawingPane;
import org.windowing.windowingproject.util.FileLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * JavaFX UI: load segment files, build twin PSTs on endpoints, run windowing
 * and draw results.
 */
public class HelloApplication extends Application {

    private final List<Segment> segments = new ArrayList<>();
    private PstIndex pstIndex;
    private Window fileWindow;
    private DrawingPane canvas;

    @Override
    public void start(Stage stage) {
        // CORRECTION: Utilisation de setPrefSize au lieu du constructeur (standard
        // JavaFX)
        canvas = new DrawingPane(800, 600);
        canvas.setPrefSize(800, 600);
        canvas.setId("drawingPane"); // Lien avec le CSS (#drawingPane)

        Button loadBtn = new Button("Load");
        Button queryBtn = new Button("Windowing");

        // Lien avec le CSS (.button)
        loadBtn.getStyleClass().add("button");
        queryBtn.getStyleClass().add("button");

        TextField xminField = new TextField();
        xminField.setPromptText("xMin");
        xminField.setPrefWidth(70);

        TextField xmaxField = new TextField();
        xmaxField.setPromptText("xMax");
        xmaxField.setPrefWidth(70);

        TextField yminField = new TextField();
        yminField.setPromptText("yMin");
        yminField.setPrefWidth(70);

        TextField ymaxField = new TextField();
        ymaxField.setPromptText("yMax");
        ymaxField.setPrefWidth(70);

        loadBtn.setOnAction(e -> loadFile(stage));
        queryBtn.setOnAction(e -> runWindowing(xminField, xmaxField, yminField, ymaxField));

        HBox controls = new HBox(15, loadBtn, queryBtn,
                xminField, xmaxField, yminField, ymaxField);
        controls.setPadding(new Insets(15));
        controls.setAlignment(Pos.CENTER_LEFT);

        BorderPane root = new BorderPane();
        root.setTop(controls);
        root.setCenter(canvas);

        // Ajout d'une marge autour du canvas pour faire plus propre
        BorderPane.setMargin(canvas, new Insets(15));

        Scene scene = new Scene(root, 950, 700);

        // CORRECTION: Chargement sécurisé du CSS
        java.net.URL cssUrl = getClass().getResource("styles.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.out.println("Attention: Fichier styles.css introuvable dans les ressources.");
        }

        stage.setTitle("Windowing with Priority Search Tree");
        stage.setScene(scene);
        stage.show();
    }

    private void runWindowing(TextField xminField, TextField xmaxField,
            TextField yminField, TextField ymaxField) {
        try {
            if (pstIndex == null) {
                alert("Load a segment file first.");
                return;
            }

            double xmin = parseValue(xminField.getText(), Double.NEGATIVE_INFINITY);
            double xmax = parseValue(xmaxField.getText(), Double.POSITIVE_INFINITY);
            double ymin = parseValue(yminField.getText(), Double.NEGATIVE_INFINITY);
            double ymax = parseValue(ymaxField.getText(), Double.POSITIVE_INFINITY);

            Window queryWindow = new Window(xmin, xmax, ymin, ymax);

            WindowingStrategy strategy;
            if (xmin == Double.NEGATIVE_INFINITY) {
                strategy = new LeftBoundedWindowStrategy();
            } else if (xmax == Double.POSITIVE_INFINITY) {
                strategy = new RightBoundedWindowStrategy();
            } else {     
                strategy = new BoundedWindowStrategy();
            }

            WindowingContext context = new WindowingContext();
            context.setStrategy(strategy);

            List<Segment> result = context.executeStrategy(pstIndex, segments, queryWindow);

            canvas.drawSegments(segments);
            canvas.drawWindow(queryWindow);
            canvas.highlightSegments(result);

        } catch (Exception ex) {
            ex.printStackTrace();
            alert("Windowing failed: " + ex.getMessage());
        }
    }

    private static void alert(String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }

    private double parseValue(String text, double defaultValue) {
        if (text == null || text.isBlank()) {
            return defaultValue;
        }
        return Double.parseDouble(text);
    }

    private void loadFile(Stage stage) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open Segments File");
        File file = chooser.showOpenDialog(stage);

        if (file == null) {
            return;
        }

        try {
            FileLoader loader = new FileLoader();
            segments.clear();
            segments.addAll(loader.loadSegments(file.getAbsolutePath()));
            fileWindow = loader.getWindow();

            List<PstEntry> entries = new ArrayList<>();
            for (int i = 0; i < segments.size(); i++) {
                Segment s = segments.get(i);
                entries.add(new PstEntry(s.getP1().getX(), s.getP1().getY(), i, 0));
                entries.add(new PstEntry(s.getP2().getX(), s.getP2().getY(), i, 1));
            }

            PrioritySearchTree forward = new PrioritySearchTree(entries, false);
            PrioritySearchTree negatedX = new PrioritySearchTree(entries, true);
            pstIndex = new PstIndex(forward, negatedX);

            canvas.drawSegments(segments);
            canvas.drawWindow(fileWindow);

        } catch (Exception e) {
            e.printStackTrace();
            alert("Failed to load file: " + e.getMessage());
        }
    }
}