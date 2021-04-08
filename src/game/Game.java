package game;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Game extends JPanel implements KeyListener, ActionListener
{
    private Dimension windowSize;
    private int tileSize, boardWidth, boardHeight, score, lastApple;
    private boolean running, gameOver, paused, started, settings;
    private int appleX, appleY;
    private JFrame frame;
    private UserInfo userInfo;
    private JButton setSnake;
    private JButton setUi;
    private JButton done;
    private JButton reset;
    private JColorChooser colorChooser;

    private Snake snake;

    //inits everything the program needs to run
    private Game()
    {   
        createGUI();
        userInfo = InfoFileHandler.loadUserInfo();
        if(userInfo==null)
        {
            userInfo = new UserInfo();
            userInfo.createUserInfo();
            InfoFileHandler.saveUserInfo(userInfo);
        }
    }

    //inits everything the game needs to run
    private void start()
    {
        restart();
        loop();
    }

    //resets all game values
    private void restart()
    {
        snake = new Snake(boardWidth/2, boardHeight/2, 3);
        snake.setColor(userInfo.getSnakeColor());
        running = true;
        gameOver=false;
        paused=false;
        started=false;
        settings=false;
        score=0;
        lastApple=0;
        newApple();
    }

    //This detects if the player runs into his own tail or an apple
    private void collision()
    {
        lastApple++;

        if (snake.isCollidingWithSelf()) {
            gameOver = true;
            return;
        }

        if (snake.isOutOfBounds(boardWidth, boardHeight)) {
            gameOver = true;
            return;
        }

        if (snake.isCollidingWith(new Point(appleX, appleY))) {
            if(150-lastApple>100)
            {
                score+=100;
                lastApple=0;
            }else if(150-lastApple<20)
            {
                score+=20;
                lastApple=0;
            }else{
                score+=(150-lastApple);
                lastApple=0;
            }
            snake.growTail();
            newApple();
        }
    }

    //moves the apple to a random location on the map
    private void newApple()
    {
        Random random = new Random(System.currentTimeMillis());
        appleX = random.nextInt(boardWidth-1);
        appleY = random.nextInt(boardHeight-1);
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
                    snake.move();
                    collision();
                }
                if(score>userInfo.getHighScore())
                {
                    userInfo.setHighScore(score);
                }

                if(settings)
                {
                    setUi.setVisible(true);
                    setUi.setBackground(userInfo.getUiColor());
                    setSnake.setVisible(true);
                    setSnake.setBackground(userInfo.getSnakeColor());
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
        if(running)
        {
            Graphics2D g = (Graphics2D) graphics.create();
            g.scale(tileSize, tileSize);
            snake.render(g);
            g.setColor(userInfo.getUiColor());
            g.fillRect(appleX,appleY,1,1);
            g.dispose();

            g = (Graphics2D) graphics.create();
            g.setColor(userInfo.getUiColor());

            g.setFont(new Font("Helvetica",Font.PLAIN,16));
            FontMetrics font = g.getFontMetrics();
            g.drawString("Pause/Hide [P]",(int)(windowSize.getWidth()-font.stringWidth("Pause/Hide [P] ")),font.getHeight());
            g.drawString("Restart [Delete]",(int)(windowSize.getWidth()-font.stringWidth("Restart [Delete] ")),font.getHeight()*2);
            g.drawString("Settings [BackSpace]",(int)(windowSize.getWidth()-font.stringWidth("Settings [Backspace] ")),font.getHeight()*3);
            g.drawString("Exit [Esc]",(int)(windowSize.getWidth()-font.stringWidth("Exit [Esc] ")),font.getHeight()*4);
            g.drawString("Score: "+score,(int)(windowSize.getWidth()-font.stringWidth("Score: "+score))/2 ,font.getHeight());
            g.drawString("High Score: "+userInfo.getHighScore(),(int)(windowSize.getWidth()-font.stringWidth("High Score: "+userInfo.getHighScore()))/2,font.getHeight()*2);

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
            if(snake.getLastMoveDirection() != Direction.DOWN)
            {
                snake.direction = Direction.UP;
            }
        }

        if(k==KeyEvent.VK_DOWN||k==KeyEvent.VK_S)
        {
            started=true;
            if(snake.getLastMoveDirection() != Direction.UP)
            {
                snake.direction = Direction.DOWN;
            }
        }

        if(k==KeyEvent.VK_RIGHT||k==KeyEvent.VK_D)
        {
            started=true;
            if(snake.getLastMoveDirection() != Direction.LEFT)
            {
                snake.direction = Direction.RIGHT;
            }
        }

        if(k==KeyEvent.VK_LEFT||k==KeyEvent.VK_A)
        {
            started=true;
            if(snake.getLastMoveDirection() != Direction.RIGHT)
            {
                snake.direction = Direction.LEFT;
            }
        }

        if(k==KeyEvent.VK_DELETE)
        {
            if(!settings)
            {
                InfoFileHandler.saveUserInfo(userInfo);
                restart();
            }
        }

        if(k==KeyEvent.VK_ESCAPE)
        {
            if(!settings)
            {
                InfoFileHandler.saveUserInfo(userInfo);
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
            userInfo.setSnakeColor(colorChooser.getColor());
            InfoFileHandler.saveUserInfo(userInfo);
        }

        if(e.getSource()==setUi)
        {
            userInfo.setUiColor(colorChooser.getColor());
            InfoFileHandler.saveUserInfo(userInfo);
        }

        if(e.getSource()==reset)
        {
            userInfo.reset();
            InfoFileHandler.saveUserInfo(userInfo);
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