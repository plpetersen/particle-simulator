package com.petersen.partsim;
import java.util.ArrayList;
import java.applet.Applet;
import java.lang.Thread;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;

import com.petersen.partsim.particles.GravityParticle;
import com.petersen.partsim.particles.Particle;

import javax.swing.JButton;
import java.util.ArrayList;

public class ParticleSimulator extends Applet implements KeyListener
{
  private final static double ACCELERATION_CONSTANT=.25;
  private final static double MINIMUM_SIZE=5;
  private int width=1280;
  private int height=778;
  private final static int MENU_STATE=0;
  private final static int PLAY_STATE=1;
  private final static int PLAY_MODE=0;
  private final static int PAUSE_MODE=1;
  private final static int DEAD_MODE=2;
  private int gameMode=0;
  private int gameState=0;
  private Particle player;
  private static ArrayList<Particle> particles=new ArrayList<Particle>(0);
  private JButton openButton=new JButton("Open");
  private JButton menuButton=new JButton("Menu");
  private JButton editorButton=new JButton("Map Editor");
  private static JFrame frame;
  private Image buffer;
  private Graphics bufferGraphics;
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  private Thread collision=new Thread()
  {
    public void run()
    {
      while(true)
      {
        if(gameState==PLAY_STATE&&gameMode==PLAY_MODE)
        {//tests particle collisions for all particles, avoiding duplicate calculations
          double change=0;
          for(int i=0;i<particles.size()-1;i++)
          {
            Particle current=particles.get(i);
            for(int j=i+1;j<particles.size();j++)
            {
              Particle temp=particles.get(j);
              if(current.collidesWith(temp))
              {//if the particles collide the larger particle absorbs the smaller one
                change=Particle.calcOverlap(current,temp);
                if(change>current.getRadius())change=current.getRadius();
                if(change>temp.getRadius())change=temp.getRadius();
                if(current.getRadius()>=temp.getRadius())
                {
                  //Adjusting velocity
                  double previousRadius=current.getRadius();
                  current.changeRadius(change);
                  temp.changeRadius(-change);//below code isn't perfect, but is ok
                  current.setVelocity(((current.getXVelocity()*previousRadius+change*temp.getXVelocity())/current.getRadius()),
                                      (current.getYVelocity()*previousRadius+change*temp.getYVelocity())/current.getRadius());
                  if(temp.getRadius()<=0)particles.remove(j);
                }
                else
                {
                  double previousRadius=temp.getRadius();
                  current.changeRadius(-change);
                  temp.changeRadius(change);
                  
                  temp.setVelocity(((temp.getXVelocity()*previousRadius+change*current.getXVelocity())/temp.getRadius()),
                                   (temp.getYVelocity()*previousRadius+change*current.getYVelocity())/temp.getRadius());
                  if(current.getRadius()<=0)
                  {
                    particles.remove(i);
                    if(i==0)
                    {
                      gameMode=DEAD_MODE;
                    }
                  }
                }
              }
            }
          }
          try
          {
            for(Particle current: particles)
            {
              if(current instanceof GravityParticle)current.attract(particles);
              current.updatePos(width,height);
            }
          }
          catch(Exception e){};
        }
        repaint();
        try
        {
          sleep(30);
        }catch(Exception exc){exc.printStackTrace();}
      }
    }
  };
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public void init()
  {
    add(openButton);
    openButton.addActionListener(new ActionListener()
                                   {
      public void actionPerformed(ActionEvent e)
      {
        particles=ParticleSimulatorIO.read();
        openButton.setEnabled(false);
        menuButton.setEnabled(false);
        editorButton.setEnabled(false);
        gameState=PLAY_STATE;
        gameMode=PLAY_MODE;
        player=particles.get(0);
        collision.start();
        repaint();
      }
    }
    );
    
    add(menuButton);
    menuButton.addActionListener(new ActionListener()
                                   {
      public void actionPerformed(ActionEvent e)
      {
        openButton.setEnabled(true);
        menuButton.setEnabled(false);
        menuButton.setVisible(false);
        gameState=MENU_STATE;
        repaint();
      }
    }
    );
    menuButton.setEnabled(false);
    menuButton.setVisible(false);
    
    add(editorButton);
    editorButton.addActionListener(new ActionListener()
                                     {
      public void actionPerformed(ActionEvent e)
      {
        swapFrames(frame);
      }
    });
    
    addKeyListener(this);
  }
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public void update(Graphics g)///////////////////////////////////////////////////////update///////////////////////
  {
    if(buffer==null)
    {
      buffer=createImage(1280,780);
      bufferGraphics=buffer.getGraphics();
    }
    if(gameState==MENU_STATE)
    {
      openButton.updateUI();
      editorButton.updateUI();
    }
    else if(gameState==PLAY_STATE)
    {
      if(gameMode==PAUSE_MODE)
      {
        bufferGraphics.setColor(Color.red);
        bufferGraphics.setFont(new Font("TimesNewRoman",0,width/10));
        bufferGraphics.drawString("PAUSED",width/2,height/8);
        menuButton.updateUI();
      }
      else if(gameMode==DEAD_MODE)
      {
        bufferGraphics.setColor(Color.black);
        bufferGraphics.drawString("Game Over",width/4,height/4);
        menuButton.setEnabled(true);
        menuButton.setVisible(true);
        menuButton.updateUI();
      }
      else
      {
        bufferGraphics.setColor(Color.white);
        bufferGraphics.fillRect(0,0,width,height);
        try
        {
          for(Particle current: particles)
          {
            current.drawSelf(bufferGraphics,player);
          }
        }
        catch(Exception e){e.printStackTrace();}
      }
    }
    g.drawImage(buffer,0,0,this);
  }
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public void keyPressed(KeyEvent e)
  {
    double expelledMass=player.getRadius()/25;
    if(gameState==PLAY_STATE)
    {
      if(gameMode==PLAY_MODE)
      {
        if(e.getKeyCode()==KeyEvent.VK_RIGHT)
        {
          player.changeRadius(-expelledMass);
          Particle thrust=new Particle(expelledMass,player.getCenterX()-(player.getRadius()+expelledMass),player.getCenterY());
          player.changeVelocity(ACCELERATION_CONSTANT,0);
          thrust.changeVelocity(-Math.abs(player.getXVelocity()),0);
          particles.add(thrust);
          thrust.updatePos(width,height);
        }
        else if(e.getKeyCode()==KeyEvent.VK_LEFT)
        {
          player.changeRadius(-expelledMass);
          Particle thrust=new Particle(expelledMass,player.getCenterX()+(player.getRadius()+expelledMass),player.getCenterY());
          player.changeVelocity(-ACCELERATION_CONSTANT,0);
          thrust.changeVelocity(Math.abs(player.getXVelocity()),0);
          particles.add(thrust);
          thrust.updatePos(width,height);
        }
        else if(e.getKeyCode()==KeyEvent.VK_DOWN)
        {
          player.changeRadius(-expelledMass);
          Particle thrust=new Particle(expelledMass,player.getCenterX(),player.getCenterY()-(player.getRadius()+expelledMass));
          player.changeVelocity(0,ACCELERATION_CONSTANT);
          thrust.changeVelocity(0,-Math.abs(player.getYVelocity()));
          particles.add(thrust);
          thrust.updatePos(width,height);
        }
        else if(e.getKeyCode()==KeyEvent.VK_UP)
        {
          player.changeRadius(-expelledMass);
          Particle thrust=new Particle(expelledMass,player.getCenterX(),player.getCenterY()+(player.getRadius()+expelledMass));
          player.changeVelocity(0,-ACCELERATION_CONSTANT);
          thrust.changeVelocity(0,Math.abs(player.getYVelocity()));
          particles.add(thrust);
          thrust.updatePos(width,height);
        }
      }
      if(e.getKeyCode()==KeyEvent.VK_SPACE||e.getKeyCode()==KeyEvent.VK_PAUSE)
      {
        if(gameMode==PLAY_MODE)
        {
          gameMode=PAUSE_MODE;
          menuButton.setEnabled(true);
          menuButton.setVisible(true);
          editorButton.setEnabled(false);
          editorButton.setVisible(false);
        }
        else if(gameMode==PAUSE_MODE)
        {
          gameMode=PLAY_MODE;
          menuButton.setEnabled(false);
          menuButton.setVisible(false);
          editorButton.setEnabled(true);
          editorButton.setVisible(true);
        }
      }
    }
    if(player.getRadius()<=MINIMUM_SIZE)
    {
      gameMode=DEAD_MODE;
      repaint();
    }
  }
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public void keyReleased(KeyEvent e){}
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public void keyTyped(KeyEvent e){}
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public void swapFrames(JFrame changingFrame)
  {
    frame.getContentPane().removeAll();
    Applet applet = new ParticleSimulatorMapEditor();
    frame.setTitle("Particle Simulator Map Editor");
    frame.getContentPane().add(applet);
    applet.init();
    applet.start();
    frame.setVisible(true);
    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
  }
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public static void main(String[] args) 
  {
    Applet applet = new ParticleSimulator();
    frame = new JFrame("Particle Simulator");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().add(applet);
    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    applet.init();
    applet.start();
    frame.setVisible(true);
  }
}