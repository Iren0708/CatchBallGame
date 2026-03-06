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

// связывает модель и представление
public class GameViewModel implements Initializable {
    // элементы из fxml
    @FXML private Pane gamePane;      // игровое поле
    @FXML private Circle ball;        // шарик
    @FXML private Label scoreLabel;    // счет
    @FXML private Label statusLabel;   // статус игры

    private GameModel model;  // ссылка на модель

    // инициализация после загрузки fxml
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = new GameModel();

        // важно: ждем, пока поле полностью отрисуется
        gamePane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                // передаем размеры поля в модель после отрисовки
                model.setFieldWidth(gamePane.getWidth());
                model.setFieldHeight(gamePane.getHeight());
            }
        });

        // немедленная установка если размеры уже есть
        if (gamePane.getWidth() > 0 && gamePane.getHeight() > 0) {
            model.setFieldWidth(gamePane.getWidth());
            model.setFieldHeight(gamePane.getHeight());
        }

        // следим за изменением размеров окна
        gamePane.widthProperty().addListener((obs, oldVal, newVal) -> {
            model.setFieldWidth(newVal.doubleValue());
            // корректировка позиции если вылетел за границы
            if (model.getBallX() + model.getBallRadius() > newVal.doubleValue()) {
                model.setBallX(newVal.doubleValue() - model.getBallRadius());
            }
        });

        gamePane.heightProperty().addListener((obs, oldVal, newVal) -> {
            model.setFieldHeight(newVal.doubleValue());
            // корректировка позиции если вылетел за границы
            if (model.getBallY() + model.getBallRadius() > newVal.doubleValue()) {
                model.setBallY(newVal.doubleValue() - model.getBallRadius());
            }
        });

        // привязка позиции шарика к модели
        ball.centerXProperty().bind(model.ballXProperty());
        ball.centerYProperty().bind(model.ballYProperty());

        // привязка цвета шарика к модели
        ball.fillProperty().bind(model.ballColorProperty());

        // привязка текста меток к модели
        scoreLabel.textProperty().bind(Bindings.concat("счет: ", model.scoreProperty()));
        statusLabel.textProperty().bind(Bindings.when(model.gameActiveProperty())
                .then("игра активна")
                .otherwise("пауза"));

        // ВАЖНО: сначала настраиваем обработчики
        setupHandlers();

        // потом запускаем игру
        model.startGameLoop();
    }

    // настройка событий мыши
    private void setupHandlers() {
        // обработчик клика по шарику (а не по полю!)
        ball.setOnMouseClicked(this::handleBallClick);

        // обработчик движения мыши по полю
        gamePane.setOnMouseMoved(this::handleMouseMove);

        // обработчик двойного клика по полю
        gamePane.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                handleDoubleClick();
                event.consume();
            }
        });
    }

    // обработка клика по шарику
    private void handleBallClick(MouseEvent event) {
        System.out.println("клик по шарику!"); // для отладки
        boolean hit = model.handleHit(event.getX(), event.getY());
        if (hit) {
            System.out.println("попадание! счет: " + model.getScore());
        }
        event.consume();
    }

    // обработка движения мыши
    private void handleMouseMove(MouseEvent event) {
        model.runFromMouse(event.getX(), event.getY());
        event.consume();
    }

    // обработка двойного клика - пауза
    private void handleDoubleClick() {
        if (model.isGameActive()) {
            model.stopGame();
            System.out.println("пауза");
        } else {
            model.setGameActive(true);
            model.startGameLoop();
            System.out.println("продолжение");
        }
    }

    // остановка при закрытии окна
    public void shutdown() {
        if (model != null) model.stopGame();
    }
}