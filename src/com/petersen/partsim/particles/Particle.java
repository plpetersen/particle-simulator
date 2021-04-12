package com.petersen.partsim.particles;
import java.awt.Graphics;
import java.awt.Color;
import java.util.ArrayList;

public class Particle
{
  private double radius;
  private double xVelocity;
  private double yVelocity;
  private double centerX;
  private double centerY;
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public Particle()
  {
    radius=5;
    xVelocity=0;
    yVelocity=0;
    centerX=5;
    centerY=5;
  }
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public Particle(double r,double x,double y)
  {
    radius=r;
    xVelocity=0;
    yVelocity=0;
    centerX=x;
    centerY=y;
  }
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public Particle(double r,double x,double y,double xVel,double yVel)
  {
    radius=r;
    this.setVelocity(xVel,yVel);
    centerX=x;
    centerY=y;
  }
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public void drawSelf(Graphics g, Particle player)
  {
    if(this.equals(player))g.setColor(Color.magenta);
    else
    {
    g.setColor(Color.blue);
    if(getRadius()>player.getRadius())g.setColor(Color.red);
    }
    g.fillOval((int)(centerX-radius),(int)(centerY-radius),(int)(radius*2),(int)(radius*2));
  }
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public static double calcOverlap(Particle one,Particle two)
  {
    double sum=Math.sqrt(Math.pow(one.getCenterX()-two.getCenterX(),2)+Math.pow(one.getCenterY()-two.getCenterY(),2));
    return ((one.getRadius()+two.getRadius())-sum);
  }
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public void changeVelocity(double xChange,double yChange)
  {
    xVelocity+=xChange;
    yVelocity+=yChange;
  }
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public void setVelocity(double xVel, double yVel)
  {
    xVelocity=xVel;
    yVelocity=yVel;
  }
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public void updatePos(double boardWidth,double boardHeight)
  {
    if(centerX-radius<0)
    {
      xVelocity=-1*xVelocity;
      centerX=radius;
    }
    else if(centerX+radius>boardWidth)
    {
      xVelocity=-1*xVelocity;
      centerX=boardWidth-radius;
    }
    else if(centerY-radius<0)
    {
      yVelocity=-1*yVelocity;
      centerY=radius;
    }
    else if(centerY+radius>boardHeight)
    {
      yVelocity=-1*yVelocity;
      centerY=boardHeight-radius;
    }
    centerX+=xVelocity;
    centerY+=yVelocity;
  }
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public boolean collidesWith(Particle other)
  {
    try
    {
    double sum=Math.sqrt(Math.pow(centerX-other.getCenterX(),2)+Math.pow(centerY-other.getCenterY(),2));
    if(sum<radius+other.getRadius())return true;
    else return false;
    }
    catch(Exception e)
    {
      return false;
    }
  }
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public double getCenterX()
  {
    return centerX;
  }
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public double getCenterY()
  {
    return centerY;
  }
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public double getRadius()
  {
    return radius;
  }
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public void changeRadius(double change)
  {
    radius+=change;
  }
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public double getXVelocity()
  {
    return xVelocity;
  }
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public double getYVelocity()
  {
    return yVelocity;
  }
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public void setRadius(double r)
  {
    radius=r;
  }
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public void setCenterX(double x)
  {
    centerX=x;
  }
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public void setCenterY(double y)
  {
    centerY=y;
  }
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public void setPos(double x, double y)
  {
    centerX=x;
    centerY=y;
  }
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public boolean equals(Particle other)
  {
    boolean track=true;
    if(this.getRadius()!=other.getRadius())track=false;
    if(this.getCenterX()!=other.getCenterX())track=false;
    if(this.getCenterY()!=other.getCenterY())track=false;
    if(this.getXVelocity()!=other.getXVelocity())track=false;
    if(this.getYVelocity()!=other.getYVelocity())track=false;
    return track;
  }
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public boolean contains(int x,int y)
  {
    int distance=(int)(Math.sqrt(Math.pow(centerX-x,2)+Math.pow(centerY-y,2)));
    if(distance<radius)return true;
    else return false;
  }
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public void attract(ArrayList<Particle> particles)
  {
    //I may or may not implement standard gravity
  }
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public double getFactor()
  {
    return 0.0;
  }
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public void setFactor(double factor)
  {
    
  }
}