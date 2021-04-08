package game;

import java.awt.*;

public class AppleManager implements IRenderObject {
    public Apple[] apples;
    private int lastApple;

    private Point boardSize;

    public AppleManager(int appleCount, Point boardSize) {
        this.boardSize = boardSize;
        lastApple = 0;

        apples = new Apple[appleCount];
        for (int i = 0; i < appleCount; i++) {
            apples[i] = new Apple();
            apples[i].randomMove(boardSize.x, boardSize.y);
        }
    }

    public int CheckCollision(Snake snake) {
        lastApple++;

        for (Apple apple : apples) {
            if (snake.isCollidingWith(new Point(apple.pos.x, apple.pos.y))) {
                snake.growTail();
                apple.randomMove(boardSize.x, boardSize.y);

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
            apples[i].randomMove(boardSize.x, boardSize.y);
        }
    }

    @Override
    public void render(Graphics2D g) {
        for (Apple apple : apples) {
            apple.render(g);
        }
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}
