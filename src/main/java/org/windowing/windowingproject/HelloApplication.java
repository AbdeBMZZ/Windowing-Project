package org.windowing.windowingproject;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.windowing.windowingproject.model.Window;
import org.windowing.windowingproject.ui.DrawingPane;
import org.windowing.windowingproject.ui.WindowingController;

import java.io.File;

/**
 * JavaFX entry point for the Windowing application.
 *
 * <p>Builds the sidebar (file loader, coordinate fields, query button, statistics)
 * and a central {@link DrawingPane}. All algorithmic logic is delegated to
 * {@link org.windowing.windowingproject.ui.WindowingController}.</p>
 */
public class HelloApplication extends Application {

    private final WindowingController controller = new WindowingController();
    private DrawingPane drawingPane;

    private final Label totalSegmentsLabel = new Label("Total : 0");
    private final Label foundSegmentsLabel = new Label("Trouvés : 0");
    private final Label timeLabel          = new Label("Temps : 0 ms");

    private final TextField xminField = new TextField();
    private final TextField xmaxField = new TextField();
    private final TextField yminField = new TextField();
    private final TextField ymaxField = new TextField();

    @Override
    public void start(Stage stage) {
        drawingPane = new DrawingPane();
        drawingPane.setId("drawingPane");

        VBox sidebar = new VBox(20);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(250);
        sidebar.setStyle("-fx-background-color: #313244;");

        Button loadBtn  = new Button("Charger Fichier");
        Button queryBtn = new Button("Lancer Recherche");
        loadBtn.getStyleClass().add("button");
        queryBtn.getStyleClass().add("button");

        loadBtn.setOnAction(e -> loadFile(stage));
        queryBtn.setOnAction(e -> runWindowing());

        xminField.setPromptText("-inf ou valeur");
        xmaxField.setPromptText("+inf ou valeur");
        yminField.setPromptText("-inf ou valeur");
        ymaxField.setPromptText("+inf ou valeur");

        GridPane coordGrid = new GridPane();
        coordGrid.setHgap(10);
        coordGrid.setVgap(10);
        coordGrid.add(new Label("xMin :"), 0, 0);  coordGrid.add(xminField, 1, 0);
        coordGrid.add(new Label("xMax :"), 0, 1);  coordGrid.add(xmaxField, 1, 1);
        coordGrid.add(new Label("yMin :"), 0, 2);  coordGrid.add(yminField, 1, 2);
        coordGrid.add(new Label("yMax :"), 0, 3);  coordGrid.add(ymaxField, 1, 3);

        VBox controlSection = new VBox(15,
                new Label("CONFIGURATION"), loadBtn, coordGrid, queryBtn);

        VBox statsSection = new VBox(10,
                new Label("STATISTIQUES"), totalSegmentsLabel, foundSegmentsLabel, timeLabel);
        statsSection.setStyle(
                "-fx-padding: 15; -fx-background-color: #1e1e2e; -fx-background-radius: 10;");

        sidebar.getChildren().addAll(controlSection, statsSection);

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
        java.net.URL cssUrl = getClass().getResource(
                "/org/windowing/windowingproject/styles.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        stage.setTitle("Windowing Pro - PST & Interval Trees");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Reads the coordinate fields, builds a {@link Window}, selects the right
     * strategy via {@link WindowingController}, and updates the canvas.
     * Empty fields or "-inf"/"+inf" text are treated as ±∞.
     */
    private void runWindowing() {
        if (!controller.isLoaded()) {
            Alert alert = new Alert(Alert.AlertType.WARNING,
                    "Veuillez d'abord charger un fichier de segments !");
            alert.setHeaderText("Aucun fichier chargé");
            alert.show();
            return;
        }

        try {
            double xmin = parseCoord(xminField.getText(), Double.NEGATIVE_INFINITY);
            double xmax = parseCoord(xmaxField.getText(), Double.POSITIVE_INFINITY);
            double ymin = parseCoord(yminField.getText(), Double.NEGATIVE_INFINITY);
            double ymax = parseCoord(ymaxField.getText(), Double.POSITIVE_INFINITY);
            Window window = new Window(xmin, xmax, ymin, ymax);

            long start = System.nanoTime();
            var result  = controller.query(window);
            long end    = System.nanoTime();

            drawingPane.drawWindow(window);
            drawingPane.highlightSegments(result);

            foundSegmentsLabel.setText("Trouvés : " + result.size());
            timeLabel.setText(String.format("Temps : %.3f ms", (end - start) / 1_000_000.0));

        } catch (NumberFormatException e) {
            // field still being typed — ignore
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Parses a coordinate field.
     * Empty, {@code -inf}, {@code −∞} → {@link Double#NEGATIVE_INFINITY}.
     * {@code +inf}, {@code inf}, {@code +∞}, {@code ∞} → {@link Double#POSITIVE_INFINITY}.
     * Otherwise parsed as a decimal number.
     *
     * @param text         raw text from the input field
     * @param defaultValue fallback when the text is empty or null
     * @return the parsed coordinate value
     */
    private double parseCoord(String text, double defaultValue) {
        if (text == null) return defaultValue;
        String t = text.trim().replace(",", ".");
        if (t.isEmpty() || t.equals("-") || t.equalsIgnoreCase("-inf")
                || t.equals("-∞") || t.equals("−∞")) {
            return Double.NEGATIVE_INFINITY;
        }
        if (t.equalsIgnoreCase("+inf") || t.equalsIgnoreCase("inf")
                || t.equals("+∞") || t.equals("∞")) {
            return Double.POSITIVE_INFINITY;
        }
        return Double.parseDouble(t);
    }

    /**
     * Opens a file chooser, loads segments via {@link WindowingController#load},
     * sets the world bounds on the drawing pane, and refreshes the canvas.
     *
     * @param stage owner stage for the file-chooser dialog
     */
    private void loadFile(Stage stage) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open Segments File");
        File file = chooser.showOpenDialog(stage);
        if (file == null) return;

        try {
            controller.load(file.getAbsolutePath());

            Window wb = controller.getWorldBounds();
            if (wb != null) {
                drawingPane.setWorldBounds(wb);
            }
            drawingPane.drawSegments(controller.getSegments());
            totalSegmentsLabel.setText("Total : " + controller.getSegments().size());

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Erreur lors du chargement : " + e.getMessage()).show();
        }
    }
}
