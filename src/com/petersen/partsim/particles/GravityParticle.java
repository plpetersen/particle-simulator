package com.petersen.partsim.particles;
import java.awt.Graphics;
import java.awt.Color;
import java.util.ArrayList;

public class GravityParticle extends Particle
{
  private double attractionFactor;
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public GravityParticle(double r,double x,double y)
  {
    this.setRadius(r);
    this.setVelocity(0,0);
    this.setCenterX(x);
    this.setCenterY(y);
    attractionFactor=10;
  }
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public GravityParticle(double r,double x,double y,double factor)
  {
    this.setRadius(r);
    this.setVelocity(0,0);
    this.setCenterX(x);
    this.setCenterY(y);
    attractionFactor=factor;
  }
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public GravityParticle(double r,double x,double y,double xVel,double yVel,double factor)
  {
    this.setRadius(r);
    this.setVelocity(xVel,yVel);
    this.setCenterX(x);
    this.setCenterY(y);
    attractionFactor=factor;
  }
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public void drawSelf(Graphics g,Particle player)
  {
    super.drawSelf(g,player);
    if(attractionFactor>0)g.setColor(Color.cyan);
    else g.setColor(Color.orange);
    double innerCircleRadius=getRadius()-5;
    g.fillOval((int)(getCenterX()-innerCircleRadius),(int)(getCenterY()-innerCircleRadius),(int)(innerCircleRadius*2),(int)(innerCircleRadius*2));
  }
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public void attract(ArrayList<Particle> particles)
  {
    try
    {
      for(Particle current: particles)
      {
        if(current.equals(this)==true)return;
        double distance=Math.sqrt(Math.pow(this.getCenterX()-current.getCenterX(),2)+Math.pow(this.getCenterY()-current.getCenterY(),2));
        double attraction=(this.getRadius()+current.getRadius())/(Math.pow(distance,2)+1);
        double xChange=0;
        double yChange=0;
        if(current.getCenterX()>this.getCenterX())xChange=attraction*-attractionFactor;
        else if(current.getCenterX()<this.getCenterX())xChange=attraction*attractionFactor;
        if(current.getCenterY()>this.getCenterY())yChange=attraction*-attractionFactor;
        else if(current.getCenterY()<this.getCenterY())yChange=attraction*attractionFactor;
        current.changeVelocity(xChange,yChange);
      }
    }
    catch(Exception exc)
    {
      exc.printStackTrace();
    }
  }
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public double getFactor()
  {
    return attractionFactor;
  }
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public void setFactor(double factor)
  {
    attractionFactor=factor;
  }
}