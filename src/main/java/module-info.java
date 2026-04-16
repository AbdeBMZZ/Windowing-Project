module org.windowing.windowingproject {
    requires javafx.controls;
    requires javafx.fxml;

    // JavaFX FXML reflective access
    opens org.windowing.windowingproject to javafx.fxml;

    // Open to unnamed module (test classpath) for JUnit reflective access
    opens org.windowing.windowingproject.model;
    opens org.windowing.windowingproject.pst;

    // Public API: entry point + domain model
    exports org.windowing.windowingproject;
    exports org.windowing.windowingproject.model;
}
