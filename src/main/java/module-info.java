module org.windowing.windowingproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.swing;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    opens org.windowing.windowingproject to javafx.fxml;

    exports org.windowing.windowingproject;
    exports org.windowing.windowingproject.model;
    exports org.windowing.windowingproject.pst;
    exports org.windowing.windowingproject.strategy;
    exports org.windowing.windowingproject.ui;
    exports org.windowing.windowingproject.util;
}
