package game;

import java.awt.*;
import java.util.Random;

interface IAppleManager extends IRenderObject {
    int checkCollision(ISnakeController snakeController);
    void reset();
}

public class AppleManager implements IAppleManager {
    private Point[] apples;
    private int lastApple;

    private Point boardSize;
    private Random random;

    public AppleManager(int appleCount, Point boardSize, Random random) {
        this.boardSize = boardSize;
        this.random = random;

        lastApple = 0;

        apples = new Point[appleCount];
        for (int i = 0; i < appleCount; i++) {
            apples[i] = new Point();
            setRandomPoint(apples[i], boardSize.x, boardSize.y);
        }
    }

    private void setRandomPoint(Point p, int boundWidth, int boundHeight) {
        p.x = random.nextInt(boundWidth - 1);;
        p.y = random.nextInt(boundHeight - 1);;
    }

    public int checkCollision(ISnakeController snakeController) {
        lastApple++;

        for (int i=0; i < apples.length; i++) {

            if (snakeController.isCollidingWith(new Point(apples[i].x, apples[i].y))) {
                snakeController.growTail();
                setRandomPoint(apples[i], boardSize.x, boardSize.y);

                int score;

                if (150 - lastApple > 100) {
                    score = 100;
                } else if (150 - lastApple < 20) {
                    score = 20;
                } else {
                    score = 150 - lastApple;
                }

                lastApple = 0;
                return score;
            }
        }

        return 0;
    }

    public void reset() {
        for (int i = 0; i < apples.length; i++) {
            setRandomPoint(apples[i], boardSize.x, boardSize.y);
        }
    }

    @Override
    public void render(Graphics2D g) {
        for (Point apple : apples) {
            g.setColor(UserPreferenceManager.USER_PREFERENCES.getUiColor());
            g.fillRect(apple.x, apple.y, 1, 1);
        }
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}
