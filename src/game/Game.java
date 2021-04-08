package game;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

public class Game extends JPanel implements KeyListener, ActionListener
{
    private Dimension windowSize;
    private int tileSize, boardWidth, boardHeight, score;
    private boolean running, gameOver, paused, started, settings;

    private JFrame frame;
    private JButton setSnake;
    private JButton setUi;
    private JButton done;
    private JButton reset;
    private JColorChooser colorChooser;

    private IRenderManager boardRenderer;
    private ISnakeController snakeController;
    private IAppleManager appleManager;

    //inits everything the program needs to run
    private Game()
    {   
        createGUI();
        UserPreferenceManager.loadUserInfo();
    }

    //inits everything the game needs to run
    private void start()
    {
        boardRenderer = new SimpleRenderManager();

        snakeController = new SnakeController(boardWidth/2, boardHeight/2, 3);
        appleManager = new AppleManager(1, new Point(boardWidth, boardHeight), new Random(System.currentTimeMillis()));

        boardRenderer.addRenderObject(snakeController);
        boardRenderer.addRenderObject(appleManager);
        boardRenderer.setScale(tileSize, tileSize);

        restart();
        loop();
    }

    //resets all game values
    private void restart()
    {
        appleManager.reset();
        snakeController.reset();

        running = true;
        gameOver=false;
        paused=false;
        started=false;
        settings=false;
        score=0;
    }

    //This detects if the player runs into his own tail or an apple
    private void collision()
    {
        if (snakeController.isCollidingWithSelf()) {
            gameOver = true;
            return;
        }

        if (snakeController.isOutOfBounds(boardWidth, boardHeight)) {
            gameOver = true;
            return;
        }

        score += appleManager.checkCollision(snakeController);
    }

    //Game loop
    private void loop(){
        long lastLoopTime = System.nanoTime();
        final int TARGET_FPS = 15;
        final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
        long lastFpsTime = 0;
        while(true){            
            long now = System.nanoTime();
            long updateLength = now - lastLoopTime;
            double delta = updateLength / ((double)OPTIMAL_TIME);

            lastFpsTime += updateLength;
            if(lastFpsTime >= 1000000000){
                lastFpsTime = 0;
            }

            if(!running)
            {
                break;
            }

            if(delta>=1)
            {
                paused= frame.getState() == Frame.ICONIFIED;

                if(!gameOver&&!paused&&!settings&&started)
                {
                    snakeController.move();
                    collision();
                }
                if(score > UserPreferenceManager.USER_PREFERENCES.getHighScore())
                {
                    UserPreferenceManager.USER_PREFERENCES.setHighScore(score);
                }

                if(settings)
                {
                    setUi.setVisible(true);
                    setUi.setBackground(UserPreferenceManager.USER_PREFERENCES.getUiColor());
                    setSnake.setVisible(true);
                    setSnake.setBackground(UserPreferenceManager.USER_PREFERENCES.getSnakeColor());
                    reset.setVisible(true);
                    done.setVisible(true);
                    colorChooser.setVisible(true);
                }else{
                    setUi.setVisible(false);
                    setSnake.setVisible(false);
                    reset.setVisible(false);
                    done.setVisible(false);
                    colorChooser.setVisible(false);
                }
                frame.setAlwaysOnTop(true);
                frame.requestFocus();
                this.repaint();
                lastLoopTime = now;
            }
        }
    }

    //inits all GUI values, etc..
    private void createGUI()
    {
        Toolkit tk = Toolkit.getDefaultToolkit();
        windowSize = tk.getScreenSize();

        if(windowSize.getWidth()<windowSize.getHeight())
        {
            tileSize = (int)(windowSize.getWidth()/30);
            boardWidth = 30;
            boardHeight = (int)(windowSize.getHeight()/tileSize);
        }else{
            tileSize = (int)windowSize.getHeight()/30;
            boardHeight = 30;
            boardWidth = (int)(windowSize.getWidth()/tileSize);
        }

        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true);
        this.setPreferredSize(windowSize);
        this.addKeyListener(this);
        this.setOpaque(false);
        setSnake = new JButton("Set Snake Color");
        setUi = new JButton("Set Highlight Color");
        reset = new JButton("Reset");
        done = new JButton("Done");
        colorChooser = new JColorChooser();
        int width = (int)(windowSize.getWidth()-(15*5))/4;
        int height = (int)(width*.25);
        Dimension button = new Dimension(width,height);
        setSnake.setVisible(false);
        setSnake.addActionListener(this);
        setSnake.setPreferredSize(button);
        setSnake.setBackground(Color.WHITE);
        setUi.setVisible(false);
        setUi.addActionListener(this);
        setUi.setPreferredSize(button);
        setUi.setBackground(Color.WHITE);
        reset.setVisible(false);
        reset.addActionListener(this);
        reset.setPreferredSize(button);
        reset.setBackground(Color.WHITE);
        done.setVisible(false);
        done.addActionListener(this);
        done.setPreferredSize(button);
        done.setBackground(Color.WHITE);
        colorChooser.setVisible(false);
        colorChooser.setBackground(new Color(0,0,0,0));
        this.add(setSnake);
        this.add(setUi);
        this.add(reset);
        this.add(done);
        this.add(colorChooser);
        frame.addKeyListener(this);
        frame.setLocation(0,0);
        frame.setBackground(new Color(0,0,0,20));
        frame.getContentPane().add(this);
        frame.pack();
        frame.setVisible(true);
        frame.setFocusableWindowState(true);
        frame.requestFocus();
    }

    //All graphics math and rendering is done here
    @Override
    public void paint(Graphics graphics)
    {
        super.paint(graphics);

        Graphics2D g = (Graphics2D) graphics;

        if(running)
        {
            boardRenderer.render(g);

            g = (Graphics2D) graphics.create();
            g.setColor(UserPreferenceManager.USER_PREFERENCES.getUiColor());
            g.setFont(new Font("Helvetica",Font.PLAIN,16));
            FontMetrics font = g.getFontMetrics();
            g.drawString("Pause/Hide [P]",(int)(windowSize.getWidth()-font.stringWidth("Pause/Hide [P] ")),font.getHeight());
            g.drawString("Restart [Delete]",(int)(windowSize.getWidth()-font.stringWidth("Restart [Delete] ")),font.getHeight()*2);
            g.drawString("Settings [BackSpace]",(int)(windowSize.getWidth()-font.stringWidth("Settings [Backspace] ")),font.getHeight()*3);
            g.drawString("Exit [Esc]",(int)(windowSize.getWidth()-font.stringWidth("Exit [Esc] ")),font.getHeight()*4);
            g.drawString("Score: "+score,(int)(windowSize.getWidth()-font.stringWidth("Score: "+score))/2 ,font.getHeight());
            g.drawString("High Score: "+ UserPreferenceManager.USER_PREFERENCES.getHighScore(),(int)(windowSize.getWidth()-font.stringWidth("High Score: "+ UserPreferenceManager.USER_PREFERENCES.getHighScore()))/2,font.getHeight()*2);

            if(gameOver)
            {
                g.setFont(new Font("Helvetica",Font.PLAIN,60));
                font = g.getFontMetrics();
                int gox = (int)((windowSize.getWidth()-font.stringWidth("Game Over"))/2);
                int goy = (int)((windowSize.getHeight()-font.getHeight())/2);
                g.setColor(Color.BLACK);
                g.drawString("Game Over",gox+3,goy+3);
                g.setColor(Color.WHITE);
                g.drawString("Game Over",gox,goy);
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e)
    {

    }

    //Called everytime a key is pressed
    @Override
    public void keyPressed(KeyEvent e)
    {
        int k = e.getKeyCode();

        if(k==KeyEvent.VK_UP||k==KeyEvent.VK_W)
        {
            started=true;
            if(snakeController.getLastMoveDirection() != Direction.DOWN)
            {
                snakeController.setDirection(Direction.UP);
            }
        }

        if(k==KeyEvent.VK_DOWN||k==KeyEvent.VK_S)
        {
            started=true;
            if(snakeController.getLastMoveDirection() != Direction.UP)
            {
                snakeController.setDirection(Direction.DOWN);
            }
        }

        if(k==KeyEvent.VK_RIGHT||k==KeyEvent.VK_D)
        {
            started=true;
            if(snakeController.getLastMoveDirection() != Direction.LEFT)
            {
                snakeController.setDirection(Direction.RIGHT);
            }
        }

        if(k==KeyEvent.VK_LEFT||k==KeyEvent.VK_A)
        {
            started=true;
            if(snakeController.getLastMoveDirection() != Direction.RIGHT)
            {
                snakeController.setDirection(Direction.LEFT);
            }
        }

        if(k==KeyEvent.VK_DELETE)
        {
            if(!settings)
            {
                UserPreferenceManager.saveUserInfo();
                restart();
            }
        }

        if(k==KeyEvent.VK_ESCAPE)
        {
            if(!settings)
            {
                UserPreferenceManager.saveUserInfo();
                System.exit(0);
            }else{
                settings = false;
            }
        }

        if(k==KeyEvent.VK_P)
        {
            frame.setState(Frame.ICONIFIED);
        }

        if(k==KeyEvent.VK_BACK_SPACE)
        {
            settings = !settings;
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {

    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource()==setSnake)
        {
            UserPreferenceManager.USER_PREFERENCES.setSnakeColor(colorChooser.getColor());
            UserPreferenceManager.saveUserInfo();
        }

        if(e.getSource()==setUi)
        {
            UserPreferenceManager.USER_PREFERENCES.setUiColor(colorChooser.getColor());
            UserPreferenceManager.saveUserInfo();
        }

        if(e.getSource()==reset)
        {
            UserPreferenceManager.USER_PREFERENCES.reset();
            UserPreferenceManager.saveUserInfo();
        }

        if(e.getSource()==done)
        {
            settings = false;
        }
    } 

    public static void main(String[] args)
    {
        new Game().start();
    }
}