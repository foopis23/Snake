package game;

import java.awt.*;

public interface IRenderManager {
    void addRenderObject(IRenderObject object);

    void removeRenderObject(IRenderObject object);

    void setScale(double sx, double sy);

    void setTranslate(int dx, int dy);

    void setRotate(double theta);

    void render(Graphics2D g);
}
