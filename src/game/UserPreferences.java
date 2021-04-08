package game;

import java.awt.Color;

public class UserPreferences implements java.io.Serializable
{
    private int highScore;
    private Color snakeColor;
    private Color uiColor;

    void createUserInfo()
    {
        highScore=0;
        snakeColor = Color.RED;
        uiColor = Color.YELLOW;
    }

    void reset()
    {
        snakeColor = Color.RED;
        uiColor = Color.YELLOW;
    }

    void setHighScore(int highScore)
    {
        this.highScore=highScore;
    }

    void setSnakeColor(Color c)
    {
        this.snakeColor = c;
    }

    void setUiColor(Color c)
    {
        this.uiColor = c;
    }

    int getHighScore()
    {
        return highScore;
    }

    Color getSnakeColor()
    {
        return snakeColor;
    }

    Color getUiColor()
    {
        return uiColor;
    }
}
