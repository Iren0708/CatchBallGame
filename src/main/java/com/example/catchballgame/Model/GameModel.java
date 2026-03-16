package com.example.catchballgame.Model;

import javafx.beans.property.*;
import javafx.scene.paint.Color;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;

public class GameModel {
    private final IntegerProperty score = new SimpleIntegerProperty(0);
    private final BooleanProperty gameActive = new SimpleBooleanProperty(true);
    private final DoubleProperty ballX = new SimpleDoubleProperty();
    private final DoubleProperty ballY = new SimpleDoubleProperty();
    private final ObjectProperty<Color> ballColor = new SimpleObjectProperty<>(Color.RED);

    private Timer timer;
    private double fieldWidth = 800;
    private double fieldHeight = 600;
    private double ballRadius = 30;
    private double dx = 2;
    private double dy = 2;
    private Random random = new Random();

    public GameModel() {
        setBallX(fieldWidth / 2);
        setBallY(fieldHeight / 2);
    }

    public void startGameLoop() {
        if (timer != null) timer.cancel();
        timer = new Timer("gameLoop", true);
        TimerTask task = new TimerTask() {
            public void run() {
                if (isGameActive()) moveBall();
            }
        };
        timer.scheduleAtFixedRate(task, 0, 50);
    }

    private void moveBall() {
        double newX = getBallX() + dx;
        double newY = getBallY() + dy;

        //отскок от границ
        if (newX < ballRadius) {
            newX = ballRadius;
            dx = -dx;
        } else if (newX > fieldWidth - ballRadius) {
            newX = fieldWidth - ballRadius;
            dx = -dx;
        }

        if (newY < ballRadius) {
            newY = ballRadius;
            dy = -dy;
        } else if (newY > fieldHeight - ballRadius) {
            newY = fieldHeight - ballRadius;
            dy = -dy;
        }

        setBallX(newX);
        setBallY(newY);
    }

    //корректировка при изменении окна
    public void setFieldWidth(double width) {
        this.fieldWidth = width;
        if (getBallX() > width - ballRadius) {
            setBallX(width - ballRadius);
            dx = -Math.abs(dx);
        }
        if (getBallX() < ballRadius) {
            setBallX(ballRadius);
            dx = Math.abs(dx);
        }
    }

    public void setFieldHeight(double height) {
        this.fieldHeight = height;
        if (getBallY() > height - ballRadius) {
            setBallY(height - ballRadius);
            dy = -Math.abs(dy);
        }
        if (getBallY() < ballRadius) {
            setBallY(ballRadius);
            dy = Math.abs(dy);
        }
    }

    //убегание от курсора
    public void runFromMouse(double mouseX, double mouseY) {
        if (!isGameActive()) return;
        double distance = Math.sqrt(Math.pow(mouseX - getBallX(), 2) + Math.pow(mouseY - getBallY(), 2));
        if (distance < 100) {
            double dirX = getBallX() - mouseX;
            double dirY = getBallY() - mouseY;
            double len = Math.sqrt(dirX*dirX + dirY*dirY);
            if (len > 0) {
                dx = (dirX / len) * 4;
                dy = (dirY / len) * 4;
            }
        }
    }

    //проверка попадания
    public boolean handleHit(double clickX, double clickY) {
        if (!isGameActive()) return false;
        double distance = Math.sqrt(Math.pow(clickX - getBallX(), 2) + Math.pow(clickY - getBallY(), 2));
        if (distance <= ballRadius) {
            setScore(getScore() + 1);
            setBallColor(randomColor());
            return true;
        }
        return false;
    }

    private Color randomColor() {
        return Color.rgb(
                random.nextInt(256),
                random.nextInt(256),
                random.nextInt(256)
        );
    }

    public void stopGame() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        setGameActive(false);
    }

    //геттеры и сеттеры
    public int getScore() { return score.get(); }
    public void setScore(int value) { score.set(value); }
    public IntegerProperty scoreProperty() { return score; }

    public boolean isGameActive() { return gameActive.get(); }
    public void setGameActive(boolean value) { gameActive.set(value); }
    public BooleanProperty gameActiveProperty() { return gameActive; }

    public double getBallX() { return ballX.get(); }
    public void setBallX(double value) { ballX.set(value); }
    public DoubleProperty ballXProperty() { return ballX; }

    public double getBallY() { return ballY.get(); }
    public void setBallY(double value) { ballY.set(value); }
    public DoubleProperty ballYProperty() { return ballY; }

    public Color getBallColor() { return ballColor.get(); }
    public void setBallColor(Color value) { ballColor.set(value); }
    public ObjectProperty<Color> ballColorProperty() { return ballColor; }

    public double getBallRadius() { return ballRadius; }
}