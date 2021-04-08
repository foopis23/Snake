package game;

import java.awt.*;
import java.util.LinkedList;

public class Snake implements Renderer {
    private Point head;
    private LinkedList<Point> body;
    public Direction direction;
    private Direction lastMoveDirection;
    private Color color;

    public Snake(int x, int y, int length) {
        head = new Point();
        head.x = x;
        head.y = y;
        body = new LinkedList<>();
        direction = Direction.UP;
        lastMoveDirection = Direction.UP;

        for (int i = 1; i < length; i++) {
            body.add(new Point(x, y + i));
        }
    }

    public void move() {
        // Move the body
        for (int i = body.size() - 1; i > 0; i--) {
            body.get(i).setLocation(body.get(i - 1));
        }
        body.get(0).setLocation(head);

        // Move the head
        switch (direction) {
            case UP:
                head.y--;
                break;
            case DOWN:
                head.y++;
                break;
            case LEFT:
                head.x--;
                break;
            case RIGHT:
                head.x++;
                break;
            default:
                System.err.println("SNAKE DIRECTION IS NULL!");
                break;
        }

        lastMoveDirection = direction;
    }

    public boolean isCollidingWithSelf() {
        for (Point bodyPart : body) {
            if (bodyPart.equals(head)) {
                return true;
            }
        }

        return false;
    }

    public boolean isCollidingWith(Point spot) {
        if (head.equals(spot)) {
            return true;
        }

        for (Point bodyPart : body) {
            if (bodyPart.equals(spot)) {
                return true;
            }
        }

        return false;
    }

    public boolean isOutOfBounds(int boundWidth, int boundHeight) {
        return (head.x >= boundWidth || head.x < 0) || (head.y >= boundHeight || head.y < 0);
    }

    public void growTail() {
        // calc x,y for new tail piece
        int x = body.get(body.size() - 1).x - (body.get(body.size() - 1).x - body.get(body.size() - 2).x);
        int y = body.get(body.size() - 1).y - (body.get(body.size() - 1).y - body.get(body.size() - 2).y);

        // Add new part to tail
        body.add(new Point(x, y));
    }

    @Override
    public void setColor(Color c) {
        color = c;
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(color);

        g.fillRect(head.x, head.y, 1, 1);
        for (Point tailPiece : body) {
            g.fillRect(tailPiece.x, tailPiece.y, 1, 1);
        }
    }

    public Direction getLastMoveDirection() {
        return lastMoveDirection;
    }
}
