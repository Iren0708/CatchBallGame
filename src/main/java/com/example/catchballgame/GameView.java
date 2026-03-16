package com.example.catchballgame;

import com.example.catchballgame.ViewModel.GameViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GameView extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/catchballgame/GameView.fxml"));
        VBox root = loader.load();
        GameViewModel viewModel = loader.getController();

        Scene scene = new Scene(root);
        stage.setTitle("лови шарик");
        stage.setScene(scene);
        stage.show();

        //остановка таймера при закрытии
        stage.setOnCloseRequest(event -> viewModel.shutdown());
    }

    public static void main(String[] args) {
        launch(args);
    }
}