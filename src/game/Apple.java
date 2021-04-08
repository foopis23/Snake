package game;

import java.awt.*;
import java.util.Random;

public class Apple implements IRenderObject {

    public Point pos;
    private final Random random;

    public Apple() {
        pos = new Point();
        random = new Random(System.currentTimeMillis());
    }

    public void randomMove(int boundWidth, int boundHeight) {
        pos.x = random.nextInt(boundWidth - 1);
        pos.y = random.nextInt(boundHeight - 1);
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(UserPreferenceManager.USER_PREFERENCES.getUiColor());
        g.fillRect(pos.x, pos.y, 1, 1);
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}
