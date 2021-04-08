package game;

import java.awt.*;
import java.util.LinkedList;

public class SimpleRenderManager implements IRenderManager {
    private LinkedList<IRenderObject> renderObjects;
    private double sx;
    private double sy;
    private int dx;
    private int dy;
    private double theta;

    public SimpleRenderManager() {
        renderObjects = new LinkedList<>();
        dx = 0;
        dy = 0;
        theta = 0;
        sx = 1;
        sy = 1;
    }

    @Override
    public void addRenderObject(IRenderObject object) {
        renderObjects.add(object);
    }

    @Override
    public void removeRenderObject(IRenderObject object) {
        renderObjects.remove(object);
    }

    @Override
    public void setScale(double sx, double sy) {
        this.sx = sx;
        this.sy = sy;
    }

    @Override
    public void setTranslate(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public void setRotate(double theta) {
        this.theta = theta;
    }

    @Override
    public void render(Graphics2D g) {
        // Create a new context
        g = (Graphics2D) g.create();

        // Apply Transformations
        g.translate(dx, dy);
        g.rotate(theta);
        g.scale(sx, sy);

        // Render Everything
        for (IRenderObject object : renderObjects) {
            if (object.isVisible()) {
                object.render(g);
            }
        }

        // Dispose
        g.dispose();
    }
}
