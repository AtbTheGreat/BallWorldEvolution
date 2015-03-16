import java.awt.Graphics;
import java.awt.Color; 
import java.awt.Rectangle;
import java.awt.geom.Point2D.Double;
public class BadBall extends Ball
{
  public BadBall(int x, int y, int diameter)
  {
    m_bounds=new Rectangle(x, y, diameter, diameter);
    m_speed=100;
    m_delay=100;
    m_direction=new Double(1, 0);
    calculateVelocity();
  }
}