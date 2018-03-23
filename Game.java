package game;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Game extends JPanel implements KeyListener
{
    private Dimension windowSize;
    private int tileSize;
    private int boardWidth;
    private int boardHeight;
    private boolean running;
    private boolean gameOver;
    private boolean paused;
    private int appleX;
    private int appleY;
    private int headX;
    private int headY;
    private int[] tailX;
    private int[] tailY;
    private boolean up;
    private boolean down;
    private boolean left;
    private boolean right;
    private boolean movingUp;
    private boolean movingDown;
    private boolean movingLeft;
    private boolean movingRight;
    private int score;
    private int lastApple;
    private JFrame frame;

    private Game()
    {   
        createGUI();
    }

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
        score=0;
        lastApple=0;
        newApple();
        loop();
    }

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
        score=0;
        lastApple=0;
        newApple();
    }

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
            }else if(150-lastApple<20)
            {
                score+=20;
            }else{
                score+=(150-lastApple);
            }
            addTail();
            newApple();
        }
    }

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

    private void newApple()
    {
        Random random = new Random(System.currentTimeMillis());
        appleX = random.nextInt(boardWidth-1);
        appleY = random.nextInt(boardHeight-1);
    }

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

            frame.requestFocus();

            if(delta>=1)
            {
                if(frame.getState()==Frame.ICONIFIED)
                {
                    paused=true;
                }else{
                    paused=false;
                }

                if(!gameOver&&!paused)
                {
                    move();
                    collision();
                }
                this.repaint();
                frame.setAlwaysOnTop(true);
                frame.requestFocus();
                lastLoopTime = now;
            }
        }
    }

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
        frame.addKeyListener(this);
        frame.setLocation(0,0);
        frame.setBackground(new Color(0,0,0,20));
        frame.getContentPane().add(this);
        frame.pack();
        frame.setVisible(true);
        frame.setFocusableWindowState(true);
        frame.requestFocus();
    }

    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
        if(running)
        {
            g.setColor(Color.RED);
            g.fillRect(headX*tileSize,headY*tileSize,tileSize,tileSize);
            for(int i=0;i<tailX.length;i++)
            {
                g.fillRect(tailX[i]*tileSize,tailY[i]*tileSize,tileSize,tileSize);
            }

            g.setColor(Color.YELLOW);
            g.fillRect(appleX*tileSize,appleY*tileSize,tileSize,tileSize);

            g.setFont(new Font("Helvatica",Font.PLAIN,16));
            FontMetrics font = g.getFontMetrics();
            g.drawString("Pause/Hide [P]",(int)(windowSize.getWidth()-font.stringWidth("Pause/Hide [P]")),font.getHeight());
            g.drawString("Restart [Delete]",(int)(windowSize.getWidth()-font.stringWidth("Restart [Delete]")),font.getHeight()*2);
            g.drawString("Exit [Esc]",(int)(windowSize.getWidth()-font.stringWidth("Exit [Esc]")),font.getHeight()*3);
            g.drawString("Score: "+score,(int)(windowSize.getWidth()-font.stringWidth("Score: "+score))/2 ,font.getHeight());

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
        }
    }

    @Override
    public void keyTyped(KeyEvent e)
    {

    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        int k = e.getKeyCode();

        if(k==KeyEvent.VK_UP||k==KeyEvent.VK_W)
        {
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
            restart();
        }

        if(k==KeyEvent.VK_ESCAPE)
        {
            System.exit(0);
        }

        if(k==KeyEvent.VK_P)
        {
            frame.setState(Frame.ICONIFIED);
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {

    }

    public static void main(String[] args)
    {
        new Game().start();
    }
}
