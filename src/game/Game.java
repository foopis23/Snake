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
    private int appleX, appleY, headX, headY;
    private int[] tailX, tailY;
    private boolean up, down, left, right, movingUp, movingDown, movingLeft, movingRight;
    private JFrame frame;
    private UserInfo userInfo;
    private JButton setSnake;
    private JButton setUi;
    private JButton done;
    private JButton reset;
    private JColorChooser colorChooser;

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

    //ints everything the game needs to run
    private void start()
    {   
        headX = (int)(boardWidth/2);
        headY = (int)(boardHeight/2);
        tailX = new int[]{headX,headX,headX};
        tailY = new int[]{headY+1,headY+2,headY+3};
        up = true;
        down = false;
        left = false;
        right = false;
        movingUp = true;
        movingDown = false;
        movingLeft = false;
        movingRight = false;
        running = true;
        gameOver=false;
        paused=false;
        started=false;
        settings=false;
        score=0;
        lastApple=0;
        newApple();
        loop();
    }

    //resets all game values
    private void restart()
    {
        headX = (int)(boardWidth/2);
        headY = (int)(boardHeight/2);
        tailX = new int[]{headX,headX,headX};
        tailY = new int[]{headY+1,headY+2,headY+3};
        up = true;
        down = false;
        left = false;
        right = false;
        movingUp = true;
        movingDown = false;
        movingLeft = false;
        movingRight = false;
        running = true;
        gameOver=false;
        paused=false;
        settings=false;
        started=false;
        score=0;
        lastApple=0;
        newApple();
    }

    //called once every update, this moves each point of the tail to the point in front of it and then moves the head of the tail the correct direction
    private void move()
    {
        for(int i=tailX.length-1;i>0;i--)
        {
            tailX[i]=tailX[i-1];
            tailY[i]=tailY[i-1];
        }

        tailX[0] = headX;
        tailY[0] = headY;

        if(up)
        {
            headY--;
            movingUp = true;
            movingDown = false;
            movingLeft = false;
            movingRight = false;
        }else if(down)
        {
            headY++;
            movingUp = false;
            movingDown = true;
            movingLeft = false;
            movingRight = false;
        }else if(left)
        {
            headX--;
            movingUp = false;
            movingDown = false;
            movingLeft = true;
            movingRight = false;
        }else if(right)
        {
            headX++;
            movingUp = false;
            movingDown = false;
            movingLeft = false;
            movingRight = true;
        }
    }

    //This detects if the player runs into his own tail or an apple
    private void collision()
    {
        lastApple++;
        for(int i=0;i<tailX.length;i++)
        {
            if(headX==tailX[i]&&headY==tailY[i])
            {
                gameOver=true;
            }
        }

        if(headX>boardWidth-1||headX<0)
        {
            gameOver = true;
        }

        if(headY>boardHeight-1||headY<0)
        {
            gameOver = true;
        }

        if(headX==appleX&&headY==appleY)
        {
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
            addTail();
            newApple();
        }
    }

    //adds one more point to the tail
    private void addTail()
    {
        int[] tempX = new int[tailX.length+1];
        int[] tempY = new int[tailY.length+1];

        for(int i=0;i<tailX.length;i++)
        {
            tempX[i] = tailX[i];
            tempY[i] = tailY[i];
        }

        tempX[tempX.length-1] = tempX[tempX.length-2]-(tempX[tempX.length-2]-tempX[tempX.length-3]);
        tempY[tempY.length-1] = tempY[tempY.length-2]-(tempY[tempY.length-2]-tempY[tempY.length-3]);
        tailX = tempX;
        tailY = tempY;
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
                if(frame.getState()==Frame.ICONIFIED)
                {
                    paused=true;
                }else{
                    paused=false;
                }

                if(!gameOver&&!paused&&!settings&&started)
                {
                    move();
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
    public void paint(Graphics g)
    {
        super.paint(g);
        if(running)
        {
            g.setColor(userInfo.getSnakeColor());
            g.fillRect(headX*tileSize,headY*tileSize,tileSize,tileSize);
            for(int i=0;i<tailX.length;i++)
            {
                g.fillRect(tailX[i]*tileSize,tailY[i]*tileSize,tileSize,tileSize);
            }

            g.setColor(userInfo.getUiColor());
            g.fillRect(appleX*tileSize,appleY*tileSize,tileSize,tileSize);

            g.setFont(new Font("Helvatica",Font.PLAIN,16));
            FontMetrics font = g.getFontMetrics();
            g.drawString("Pause/Hide [P]",(int)(windowSize.getWidth()-font.stringWidth("Pause/Hide [P] ")),font.getHeight());
            g.drawString("Restart [Delete]",(int)(windowSize.getWidth()-font.stringWidth("Restart [Delete] ")),font.getHeight()*2);
            g.drawString("Settings [BackSpace]",(int)(windowSize.getWidth()-font.stringWidth("Settings [Backspace] ")),font.getHeight()*3);
            g.drawString("Exit [Esc]",(int)(windowSize.getWidth()-font.stringWidth("Exit [Esc] ")),font.getHeight()*4);
            g.drawString("Score: "+score,(int)(windowSize.getWidth()-font.stringWidth("Score: "+score))/2 ,font.getHeight());
            g.drawString("High Score: "+userInfo.getHighScore(),(int)(windowSize.getWidth()-font.stringWidth("High Score: "+userInfo.getHighScore()))/2,font.getHeight()*2);

            if(gameOver)
            {
                g.setFont(new Font("Helvatica",Font.PLAIN,60));
                font = g.getFontMetrics();
                int gox = (int)((windowSize.getWidth()-font.stringWidth("Game Over"))/2);
                int goy = (int)((windowSize.getHeight()-font.getHeight())/2);
                g.setColor(Color.BLACK);
                g.drawString("Game Over",gox+3,goy+3);
                g.setColor(Color.WHITE);
                g.drawString("Game Over",gox,goy);
            }
            super.paint(g);
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
            if(!movingDown)
            {
                up = true;
                down = false;
                left = false;
                right = false;
            }
        }

        if(k==KeyEvent.VK_DOWN||k==KeyEvent.VK_S)
        {
            started=true;
            if(!movingUp)
            {
                up = false;
                down = true;
                left = false;
                right = false;
            }
        }

        if(k==KeyEvent.VK_RIGHT||k==KeyEvent.VK_D)
        {
            started=true;
            if(!movingLeft)
            {
                up = false;
                down = false;
                left = false;
                right = true;
            }
        }

        if(k==KeyEvent.VK_LEFT||k==KeyEvent.VK_A)
        {
            started=true;
            if(!movingRight)
            {
                up = false;
                down = false;
                left = true;
                right = false;
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