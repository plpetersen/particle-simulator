package com.petersen.partsim;
import java.applet.Applet;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.ChangeEvent;
import java.awt.Color;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import com.petersen.partsim.particles.GravityParticle;
import com.petersen.partsim.particles.Particle;

import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.awt.Image;

public class ParticleSimulatorMapEditor extends Applet implements MouseListener, MouseMotionListener, KeyListener
{
  private ArrayList<Particle> particles=new ArrayList<Particle>();
  private Particle selectedParticle;
  private Particle draggedParticle;
  private int width=1280;
  private int height=778;
  private int dragStartX=0;
  private int dragStartY=0;
  private int dragCurrentX=0;
  private int dragCurrentY=0;
  private boolean dragged=false;
  private boolean displayOnParticle=true;
  private boolean draggingParticle=false;
  private boolean changingData=false;
  private JButton saveButton=new JButton("Save"); 
  private JButton openButton=new JButton("Open");
  private JButton menuButton=new JButton("Menu");
  private JTable optionList;
  private static JFrame frame=new JFrame();
  private Image buffer=createImage(1280,780);
  private File currentSave;
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public void init()
  {
    setLayout(null);
    saveButton.setBounds(width/2-100,10,80,40);
    add(saveButton);
    saveButton.addActionListener(new ActionListener()
                                   {
      public void actionPerformed(ActionEvent e)
      {
        ParticleSimulatorIO.write(particles);
      }
    });
    
    openButton.setBounds(width/2,10,80,40);
    add(openButton);
    openButton.addActionListener(new ActionListener()
                                   {
      public void actionPerformed(ActionEvent e)
      {
        particles=ParticleSimulatorIO.read();
        repaint();
      }
    });
    
    menuButton.setBounds(width/2+90,10,80,40);
    add(menuButton);
    menuButton.addActionListener(new ActionListener()
                                   {
      public void actionPerformed(ActionEvent e)
      {
        swapFrames();
      }
    });
    
    String[][] tempString={
      {"X Coordinate",null},
      {"Y Coordinate",null},
      {"Radius",null},
      {"X Velocity",null},
      {"Y Velocity",null},
      {"Attraction Factor",null}
    };
    String[] headings=new String[]{"Property","Value"};
    TableModel model=new DefaultTableModel(tempString,headings)
    {
      public boolean isCellEditable(int row,int col)
      {
        if(col==0)return false;
        else if(col==1)return true;
        return true;
      }
      
      public void setValueAt(Object obj,int row,int col)
      {
        super.setValueAt(obj,row,col);
        if(!changingData&&selectedParticle!=null)updateParticleValues();
        repaint();
      }
      
      public Class<?> getColumnClass(int col) 
      {  // Get data type of column.
        if (col == 0)return String.class;
        else return Double.class;
      }
    };
    optionList=new JTable(model);
    optionList.setFont(new Font("Times New Roman",0,25));
    add(optionList);
    optionList.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    optionList.setBounds(width-250,0,250,optionList.getRowCount()*40);
    for(int i=0;i<6;i++)
    {
      optionList.setRowHeight(i,40);
    }
    optionList.getColumnModel().getColumn(0).setPreferredWidth(200);
    
    addKeyListener(this);
    addMouseListener(this);
    addMouseMotionListener(this);
    repaint();
  }
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public void update(Graphics g)
  {
    buffer=createImage(1280,780);
    Graphics bufferG=buffer.getGraphics();
    bufferG.setColor(Color.darkGray);
    bufferG.fillRect(0,0,width,height);
    if(particles.size()>0)
    {
      Particle player=particles.get(0);
      for(Particle current: particles)
      {
        current.drawSelf(bufferG,player);
        if(displayOnParticle)
        {
          bufferG.setColor(Color.white);
          bufferG.drawString("R:"+(int)(current.getRadius())+" - X:"+(int)(current.getCenterX())+" Y:"+(int)(current.getCenterY()),(int)(current.getCenterX()),(int)(current.getCenterY()));
          bufferG.drawString("XVelocity:"+(int)(current.getXVelocity())+" YVelocity:"+(int)(current.getYVelocity()),(int)(current.getCenterX()),(int)(current.getCenterY()+15));
        }
      }
      if(selectedParticle!=null)
      {
        bufferG.setColor(Color.yellow);
        double highlightCircleRadius=selectedParticle.getRadius()+1;
        bufferG.drawOval((int)(selectedParticle.getCenterX()-highlightCircleRadius),(int)(selectedParticle.getCenterY()-highlightCircleRadius),(int)(highlightCircleRadius*2),(int)(highlightCircleRadius*2));
        selectedParticle.drawSelf(bufferG,player);
        bufferG.setColor(Color.white);
        bufferG.drawString("R:"+(int)(selectedParticle.getRadius())+" - X:"+(int)(selectedParticle.getCenterX())+" Y:"+(int)(selectedParticle.getCenterY()),(int)(selectedParticle.getCenterX()),(int)(selectedParticle.getCenterY()));
        bufferG.drawString("XVelocity:"+(int)(selectedParticle.getXVelocity())+" YVelocity:"+(int)(selectedParticle.getYVelocity()),(int)(selectedParticle.getCenterX()),(int)(selectedParticle.getCenterY()+15));
      }
    }
    if(dragged)
    {
      int radius=(int)(Math.sqrt(Math.pow(dragStartX-dragCurrentX,2)+Math.pow(dragStartY-dragCurrentY,2)));
      bufferG.setColor(Color.green);
      bufferG.fillOval(dragStartX-radius,dragStartY-radius,radius*2,radius*2);
      bufferG.setColor(Color.black);
      bufferG.drawString("R:"+radius+" - X:"+dragStartX+" Y:"+dragStartY,dragCurrentX,dragCurrentY);
    }
    g.drawImage(buffer,0,0,this);
    openButton.updateUI();
    saveButton.updateUI();
    optionList.updateUI();
    menuButton.updateUI();
  }
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  private void updateList()
  {
    changingData=true;
    if(selectedParticle==null)
    {
      optionList.setValueAt(null,0,1);
      optionList.setValueAt(null,1,1);
      optionList.setValueAt(null,2,1);
      optionList.setValueAt(null,3,1);
      optionList.setValueAt(null,4,1);
      changingData=false;
      optionList.setValueAt(null,5,1);
    }
    else
    {
      optionList.setValueAt(selectedParticle.getCenterX(),0,1);
      optionList.setValueAt(selectedParticle.getCenterY(),1,1);
      optionList.setValueAt(selectedParticle.getRadius(),2,1);
      optionList.setValueAt(selectedParticle.getXVelocity(),3,1);
      optionList.setValueAt(selectedParticle.getYVelocity(),4,1);
      changingData=false;
      optionList.setValueAt(selectedParticle.getFactor(),5,1);
    }
  }
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public void updateParticleValues()
  {
    try
    {
      selectedParticle.setCenterX((Double)optionList.getValueAt(0,1));
      selectedParticle.setCenterY((Double)optionList.getValueAt(1,1));
      selectedParticle.setRadius((Double)optionList.getValueAt(2,1));
      selectedParticle.setVelocity((Double)optionList.getValueAt(3,1),(Double)optionList.getValueAt(4,1));
      if(!(selectedParticle instanceof GravityParticle)&&(Double)optionList.getValueAt(5,1)!=0)
      {
        GravityParticle temp=new GravityParticle(selectedParticle.getRadius(),selectedParticle.getCenterX(),selectedParticle.getCenterY(),
                                       selectedParticle.getXVelocity(),selectedParticle.getYVelocity(),(Double)optionList.getValueAt(5,1));
        particles.remove(selectedParticle);
        particles.add(temp);
        selectedParticle=temp;
      }
      else if(selectedParticle instanceof GravityParticle&&(Double)optionList.getValueAt(5,1)==0)
       {
        Particle temp=new Particle(selectedParticle.getRadius(),selectedParticle.getCenterX(),selectedParticle.getCenterY(),
                                       selectedParticle.getXVelocity(),selectedParticle.getYVelocity());
        particles.remove(selectedParticle);
        particles.add(temp);
        selectedParticle=temp;
      }
      else
      {
        selectedParticle.setFactor((Double)optionList.getValueAt(5,1));
      }
    }
    catch(Exception e){e.printStackTrace();}
  }
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public void mouseReleased(MouseEvent e)
  {
    int radius=(int)(Math.sqrt(Math.pow(dragStartX-dragCurrentX,2)+Math.pow(dragStartY-dragCurrentY,2)));
    if(dragged)
    {
      Particle temp=new Particle(radius,dragStartX,dragStartY);
      particles.add(temp);
      selectedParticle=temp;
    }
    else updateList();
    dragged=false;
    draggingParticle=false;
    repaint();
  }
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public void mousePressed(MouseEvent e)
  {
    dragStartX=e.getX();
    dragStartY=e.getY();
    for(Particle current:particles)
    if(current.contains(e.getX(),e.getY()))
    {
      draggingParticle=true;
      draggedParticle=current;
      selectedParticle=current;
    }
    updateList();
  }
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public void mouseClicked(MouseEvent e)
  {
    selectedParticle=null;
    for(Particle current:particles)if(current.contains(e.getX(),e.getY()))selectedParticle=current;
    if(e.getButton()==MouseEvent.BUTTON3)
    {
      particles.remove(selectedParticle);
      selectedParticle=null;
    }
    updateList();
    repaint();
  }
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public void mouseDragged(MouseEvent e)
  {
    if(draggingParticle)draggedParticle.setPos(e.getX(),e.getY());
    else dragged=true;
    dragCurrentX=e.getX();
    dragCurrentY=e.getY();
    repaint();
  }
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public void keyPressed(KeyEvent e)
  {
    if(e.getKeyCode()==KeyEvent.VK_R)displayOnParticle=!displayOnParticle;
    repaint();
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
  public void mouseMoved(MouseEvent e){}
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public void mouseExited(MouseEvent e){}
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public void mouseEntered(MouseEvent e){}
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public void swapFrames()
  {
    frame.getContentPane().removeAll();
    Applet applet = new ParticleSimulator();
    frame.setTitle("Particle Simulator");
    frame.getContentPane().add(applet);
    applet.init();
    applet.start();
    frame.setVisible(true);
  }
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public static void main(String[] args) 
  {
    Applet applet = new ParticleSimulatorMapEditor();
    frame = new JFrame("Particle Simulator Map Editor");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().add(applet);
    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    applet.init();
    applet.start();
    frame.setVisible(true);
  }
}