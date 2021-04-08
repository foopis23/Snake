package game;

import java.awt.*;

public interface ISnakeController extends IRenderObject {
    void reset();

    void move();

    boolean isCollidingWithSelf();

    boolean isCollidingWith(Point point);

    boolean isOutOfBounds(int boundWidth, int boundHeight);

    void growTail();

    Direction getLastMoveDirection();
    void setDirection(Direction direction);
}
