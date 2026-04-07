module org.example.daibetes {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens org.example.daibetes to javafx.fxml;
    exports org.example.daibetes;
}