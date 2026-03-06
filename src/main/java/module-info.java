module com.example.catchballgame {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.catchballgame to javafx.fxml;
    opens com.example.catchballgame.ViewModel to javafx.fxml;
    opens com.example.catchballgame.Model to javafx.base;

    exports com.example.catchballgame;
}