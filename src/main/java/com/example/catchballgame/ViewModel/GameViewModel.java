package com.example.catchballgame.ViewModel;

import com.example.catchballgame.Model.GameModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.beans.binding.Bindings;
import java.net.URL;
import java.util.ResourceBundle;

public class GameViewModel implements Initializable {
    @FXML private Pane gamePane;
    @FXML private Circle ball;
    @FXML private Label scoreLabel;
    @FXML private Label statusLabel;

    private GameModel model;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = new GameModel();

        //получаем размеры поля после отрисовки
        gamePane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                model.setFieldWidth(gamePane.getWidth());
                model.setFieldHeight(gamePane.getHeight());
            }
        });

        if (gamePane.getWidth() > 0 && gamePane.getHeight() > 0) {
            model.setFieldWidth(gamePane.getWidth());
            model.setFieldHeight(gamePane.getHeight());
        }

        //следим за изменением окна
        gamePane.widthProperty().addListener((obs, oldVal, newVal) -> {
            model.setFieldWidth(newVal.doubleValue());
        });

        gamePane.heightProperty().addListener((obs, oldVal, newVal) -> {
            model.setFieldHeight(newVal.doubleValue());
        });

        //привязки
        ball.centerXProperty().bind(model.ballXProperty());
        ball.centerYProperty().bind(model.ballYProperty());
        ball.fillProperty().bind(model.ballColorProperty());

        scoreLabel.textProperty().bind(Bindings.concat("счет: ", model.scoreProperty()));
        statusLabel.textProperty().bind(Bindings.when(model.gameActiveProperty())
                .then("игра активна")
                .otherwise("пауза"));

        setupHandlers();
        model.startGameLoop();
    }

    private void setupHandlers() {
        ball.setOnMouseClicked(this::handleBallClick);
        gamePane.setOnMouseMoved(this::handleMouseMove);

        gamePane.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                handleDoubleClick();
                event.consume();
            }
        });
    }

    private void handleBallClick(MouseEvent event) {
        model.handleHit(event.getX(), event.getY());
        event.consume();
    }

    private void handleMouseMove(MouseEvent event) {
        model.runFromMouse(event.getX(), event.getY());
        event.consume();
    }

    private void handleDoubleClick() {
        if (model.isGameActive()) {
            model.stopGame();
        } else {
            model.setGameActive(true);
            model.startGameLoop();
        }
    }

    public void shutdown() {
        if (model != null) model.stopGame();
    }
}