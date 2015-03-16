import java.awt.Graphics;
import java.awt.Color; 
import java.awt.Rectangle;
import java.awt.geom.Point2D.Double;
public class UglyBall extends Ball
{
  public UglyBall(int x, int y, int diameter)
  {
    m_bounds=new Rectangle(x, y, diameter, diameter);
    m_speed=25;
    m_delay=100;
    m_direction=new Double(1, 0);
    calculateVelocity();
  }
  public void decelerate()
  {
    if(m_speed>=1)
    {
      m_speed-=1;
    }
    else if(m_speed<1)
    {
      m_speed=0;
    }
    calculateVelocity();
  }
}