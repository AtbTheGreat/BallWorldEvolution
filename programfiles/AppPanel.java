import javax.swing.JPanel;
import java.awt.Graphics;
import javax.swing.Timer;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Color;
import java.lang.Runnable; 
public class AppPanel extends JPanel implements MouseListener,ActionListener,Runnable
{
  private static final int refreshRate=100;
  private long lastRefresh;
  private Ball[] balls=new Ball[4];
  private GameField gameField;
  private Rectangle ballBounds;
  private Rectangle gameFieldBounds;
  private Timer timer;
  private boolean gameSwitch;
  private Thread gameLoop;
  private Color[] colors=new Color[4];
  private int[] xDisplacements=new int[4];
  private int[] yDisplacements=new int[4];
  public AppPanel()
  {
    setBackground(new Color(255, 255, 255));
    gameField=new GameField(0, 0, 500, 300);
    gameFieldBounds=gameField.getBounds();
    createBalls();
    addMouseListener(this);
    timer=new Timer(5000, this);
    timer.addActionListener(this);
    gameLoop=new Thread(this);
    gameSwitch=true;
    timer.start();
    gameLoop.start();
  }
  public void run()
  {
    while(gameSwitch==true)
    {
      long currentTime=System.currentTimeMillis();
      for(int i=0; i<balls.length; i++)
      {
        checkBallWallCollision(balls[i]);
        for(int j=0; j<balls.length; j++)
        {
          Rectangle ballIBounds=balls[i].getBounds();
          Rectangle ballJBounds=balls[j].getBounds();
          if(i!=j && ballIBounds.width<=ballJBounds.width)
          {
            checkBallCollision(balls[i], balls[j]);
          }
        }
        balls[i].animate(currentTime);
      }
      if((currentTime-lastRefresh)>refreshRate)    
      {                                                 
        repaint();                                    
        lastRefresh=currentTime;                    
      }
      if(gameLoop.interrupted())
      {
        gameSwitch=false;
      }
    }
  }
  public void paintComponent(Graphics g)
  {    
    super.paintComponent(g);
    draw(g);
  } 
  private void draw(Graphics g)
  {
    g.setColor(new Color(0, 0, 255));
    gameField.draw(g);
    for(int i=0; i<balls.length; i++)
    {
      g.setColor(colors[i]);
      balls[i].draw(g);
    }
  }
  private void checkBallCollision(Ball b1, Ball b2)
  {
    Rectangle b1Bounds=b1.getBounds();
    Rectangle b2Bounds=b2.getBounds();
    if(b1Bounds.x>=b2Bounds.x && (b1Bounds.x+b1Bounds.width)<=(b2Bounds.x+b2Bounds.width))
    {
      if((b1Bounds.y+b1Bounds.height)>=b2Bounds.y && b1Bounds.y<=b2Bounds.y)
      {
        int distance=b1Bounds.y+b1Bounds.height-b2Bounds.y;
        b1.collideY(b1Bounds.y-distance-1);
        b1.decelerate();
        b2.collideY(b2Bounds.y+distance+1);
        b2.decelerate();
      }
      else if(b1Bounds.y<=(b2Bounds.y+b2Bounds.height) && b1Bounds.y>=b2Bounds.y)
      {
        int distance=b1Bounds.y+b1Bounds.height-b1Bounds.y;
        b1.collideY(b1Bounds.y+distance+1);
        b1.decelerate();
        b2.collideY(b2Bounds.y-distance-1);
        b2.decelerate();
      }
    }
    else if(b1Bounds.y>=b2Bounds.y && (b1Bounds.y+b1Bounds.height)<=(b2Bounds.y+b2Bounds.height))
    {
      if((b1Bounds.x+b1Bounds.width)>=b2Bounds.x && b1Bounds.x<=b2Bounds.x)
      {
        int distance=b1Bounds.x+b1Bounds.width-b2Bounds.x;
        b1.collideX(b1Bounds.x-distance-1);
        b1.decelerate();
        b2.collideX(b2Bounds.x+distance+1);
        b2.decelerate();
      }
      else if(b1Bounds.x<=(b2Bounds.x+b2Bounds.width) && b1Bounds.x>=b2Bounds.x)
      {
        int distance=b2Bounds.x+b2Bounds.width-b1Bounds.x;
        b1.collideX(b1Bounds.x+distance+1);
        b1.decelerate();
        b2.collideX(b2Bounds.x-distance-1);
        b2.decelerate();
      }
    }
  }
  private void checkBallWallCollision(Ball ball)
  {
    Rectangle ballBounds=ball.getBounds();
    if(ballBounds.y<gameFieldBounds.y)
    {
      ball.collideY(gameFieldBounds.y);
    }
    else if((ballBounds.y+ballBounds.height)>(gameFieldBounds.y+gameFieldBounds.height))
    {
      ball.collideY(gameFieldBounds.y+gameFieldBounds.height-ballBounds.height);
    }
    else if(ballBounds.x<gameFieldBounds.x)
    {
      ball.collideX(gameFieldBounds.x);
    }
    else if((ballBounds.x+ballBounds.width)>(gameFieldBounds.x+gameFieldBounds.width))
    {
      ball.collideX(gameFieldBounds.x+gameFieldBounds.width-ballBounds.width);
    }
  }
  public void mousePressed(MouseEvent e)
  {
    int x=e.getX();
    int y=e.getY();
    if(x>=gameFieldBounds.x && x<=(gameFieldBounds.x+gameFieldBounds.width) && y>=gameFieldBounds.y && y<=(gameFieldBounds.y+gameFieldBounds.height))
    {
      for(int i=0; i<balls.length; i++)
      {
        balls[i].halt();
        Rectangle ballBounds=balls[i].getBounds();
        int ballCenterX=ballBounds.x+ballBounds.width/2;
        int ballCenterY=ballBounds.y+ballBounds.height/2;
        xDisplacements[i]=ballCenterX-x;
        yDisplacements[i]=ballCenterY-y;
      }
    }
  }
  public void mouseReleased(MouseEvent e)
  {
    for(int i=0; i<balls.length; i++)
    {
      int velocityX=gameFieldBounds.width-Math.abs(xDisplacements[i]);
      int velocityY=gameFieldBounds.height-Math.abs(yDisplacements[i]);
      balls[i].setDirection(xDisplacements[i], yDisplacements[i]);
      balls[i].changeSpeed(velocityX, velocityY);
    }
  }
  public void mouseClicked(MouseEvent e)
  {
  }
  public void mouseEntered(MouseEvent e)
  {
  }
  public void mouseExited(MouseEvent e)
  {
  }
  public void actionPerformed(ActionEvent e)
  {
    Object source=e.getSource();
    if(source==timer)
    {
      timer.stop();
      for(int i=0; i<balls.length; i++)
      {
        balls[i].decelerate();
      }
      timer.start();
    }
  }
  public void quitGameLoop()
  {
    gameLoop.interrupt();
  }
  private void createBalls()
  {
    int ball0Diameter=20;
    int ball0X=gameFieldBounds.x+(gameFieldBounds.width-ball0Diameter)/2;
    int ball0Y=gameFieldBounds.y;
    colors[0]=new Color(255, 0, 0);
    balls[0]=new Ball(ball0X, ball0Y, ball0Diameter);
    balls[0].setDirection(0, 1);
    int ball1Diameter=20;
    int ball1X=gameFieldBounds.x+gameFieldBounds.width-ball1Diameter;
    int ball1Y=gameFieldBounds.y+(gameFieldBounds.height-ball1Diameter)/2;
    colors[1]=new Color(0, 255, 0);
    balls[1]=new GoodBall(ball1X, ball1Y, ball1Diameter);
    balls[1].setDirection(-1, 0);
    int ball2Diameter=10;
    int ball2X=gameFieldBounds.x+(gameFieldBounds.width-ball2Diameter)/2;
    int ball2Y=gameFieldBounds.y+gameFieldBounds.height-ball2Diameter;
    colors[2]=new Color(255, 255, 0);
    balls[2]=new BadBall(ball2X, ball2Y, ball2Diameter);
    balls[2].setDirection(0, -1);
    int ball3Diameter=40;
    int ball3X=gameFieldBounds.x;
    int ball3Y=gameFieldBounds.y+(gameFieldBounds.height-ball3Diameter)/2;
    colors[3]=new Color(0, 0, 255);
    balls[3]=new UglyBall(ball3X, ball3Y, ball3Diameter);
    balls[3].setDirection(1, 0);
  }
}