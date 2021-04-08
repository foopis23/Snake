package game;

import java.awt.*;

public interface Renderer {
    void setColor(Color c);

    void render(Graphics2D g);
}
