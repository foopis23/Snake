package game;

import java.awt.*;

public interface IRenderObject {
    void render(Graphics2D g);
    boolean isVisible();
}