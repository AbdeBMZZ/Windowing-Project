package org.windowing.windowingproject;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.windowing.windowingproject.model.*;
import org.windowing.windowingproject.pst.PrioritySearchTree;
import org.windowing.windowingproject.strategy.*;
import org.windowing.windowingproject.ui.DrawingPane;
import org.windowing.windowingproject.util.FileLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HelloApplication extends Application {

    private final List<Segment> segments = new ArrayList<>();
    private PrioritySearchTree pst;
    private Window window;
    private DrawingPane canvas;

    @Override
    public void start(Stage stage) {
        canvas = new DrawingPane(800, 600);

        Button loadBtn = new Button("Load");
        Button queryBtn = new Button("Windowing");

        TextField xminField = new TextField();
        xminField.setPromptText("xMin");

        TextField xmaxField = new TextField();
        xmaxField.setPromptText("xMax");

        TextField yminField = new TextField();
        yminField.setPromptText("yMin");

        TextField ymaxField = new TextField();
        ymaxField.setPromptText("yMax");

        loadBtn.setOnAction(e -> loadFile(stage));

        queryBtn.setOnAction(e -> {
            try {
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

                List<Segment> result = context.executeStrategy(
                        pst, segments, queryWindow);

                canvas.drawSegments(segments);
                canvas.drawWindow(queryWindow);
                canvas.highlightSegments(result);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        HBox controls = new HBox(10, loadBtn, queryBtn,
                xminField, xmaxField, yminField, ymaxField);
        controls.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setTop(controls);
        root.setCenter(canvas);

        stage.setTitle("Windowing with Priority Search Tree");
        stage.setScene(new Scene(root, 900, 650));
        stage.show();
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

        if (file == null) return;

        try {
            FileLoader loader = new FileLoader();
            segments.clear();
            segments.addAll(loader.loadSegments(file.getAbsolutePath()));
            window = loader.getWindow();

            List<Point2D> points = new ArrayList<>();
            for (Segment s : segments) {
                points.add(s.getP1());
                points.add(s.getP2());
            }

            pst = new PrioritySearchTree(points);

            canvas.drawSegments(segments);
            canvas.drawWindow(window);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
