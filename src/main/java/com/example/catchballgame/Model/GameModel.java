package com.example.catchballgame.Model;

import javafx.beans.property.*;
import javafx.scene.paint.Color;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;

// модель игры с данными и логикой
public class GameModel {
    // свойства для привязки к view
    private final IntegerProperty score = new SimpleIntegerProperty(0);
    private final BooleanProperty gameActive = new SimpleBooleanProperty(true);
    private final DoubleProperty ballX = new SimpleDoubleProperty();
    private final DoubleProperty ballY = new SimpleDoubleProperty();
    private final ObjectProperty<Color> ballColor = new SimpleObjectProperty<>(Color.RED);  // цвет шарика

    // внутренние переменные
    private Timer timer;              // таймер движения
    private double fieldWidth = 800;   // ширина поля
    private double fieldHeight = 600;  // высота поля
    private double ballRadius = 30;    // радиус шарика
    private double dx = 2;              // скорость по x
    private double dy = 2;              // скорость по y
    private Random random = new Random();

    // конструктор, старт в центре
    public GameModel() {
        setBallX(fieldWidth / 2);
        setBallY(fieldHeight / 2);
    }

    // запуск игрового цикла
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

    // движение шарика с отскоком
    private void moveBall() {
        double newX = getBallX() + dx;
        double newY = getBallY() + dy;

        // отскок от границ с учетом текущих размеров поля
        if (newX < ballRadius) {
            dx = Math.abs(dx);  // меняем направление
            newX = ballRadius;
        } else if (newX > fieldWidth - ballRadius) {
            dx = -Math.abs(dx);
            newX = fieldWidth - ballRadius;
        }

        if (newY < ballRadius) {
            dy = Math.abs(dy);
            newY = ballRadius;
        } else if (newY > fieldHeight - ballRadius) {
            dy = -Math.abs(dy);
            newY = fieldHeight - ballRadius;
        }

        setBallX(newX);
        setBallY(newY);
    }

    // генерация случайного цвета
    private Color randomColor() {
        return Color.rgb(
                random.nextInt(256),    // красный 0-255
                random.nextInt(256),    // зеленый 0-255
                random.nextInt(256)     // синий 0-255
        );
    }

    // проверка попадания мыши по шарику
    public boolean handleHit(double clickX, double clickY) {
        if (!isGameActive()) return false;
        double distance = Math.sqrt(Math.pow(clickX - getBallX(), 2) + Math.pow(clickY - getBallY(), 2));
        if (distance <= ballRadius) {
            setScore(getScore() + 1);  // плюс очко
            setBallColor(randomColor()); // меняем цвет
            return true;
        }
        return false;
    }

    // убегание от курсора
    public void runFromMouse(double mouseX, double mouseY) {
        if (!isGameActive()) return;
        double distance = Math.sqrt(Math.pow(mouseX - getBallX(), 2) + Math.pow(mouseY - getBallY(), 2));
        if (distance < 100) {  // если мышь близко
            // вектор от мыши к шарику
            double dirX = getBallX() - mouseX;
            double dirY = getBallY() - mouseY;
            double len = Math.sqrt(dirX*dirX + dirY*dirY);
            if (len > 0) {
                // двигаемся в противоположную сторону
                dx = (dirX / len) * 4;  // увеличил скорость убегания
                dy = (dirY / len) * 4;
            }
        }
    }

    // остановка игры
    public void stopGame() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        setGameActive(false);
    }

    // геттеры и сеттеры для свойств
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

    // сеттеры для размеров поля
    public void setFieldWidth(double w) {
        this.fieldWidth = w;
        // проверка что шарик не за границей
        if (getBallX() + ballRadius > w) {
            setBallX(w - ballRadius);
        }
    }

    public void setFieldHeight(double h) {
        this.fieldHeight = h;
        // проверка что шарик не за границей
        if (getBallY() + ballRadius > h) {
            setBallY(h - ballRadius);
        }
    }
}