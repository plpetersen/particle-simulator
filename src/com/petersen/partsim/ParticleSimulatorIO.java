package com.petersen.partsim;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JFileChooser;

import com.petersen.partsim.particles.GravityParticle;
import com.petersen.partsim.particles.Particle;

public class ParticleSimulatorIO
{
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public static ArrayList<Particle> read()
  {
    JFileChooser chooser=new JFileChooser(".//saves");
    chooser.showOpenDialog(null);
    return read(chooser.getSelectedFile());
  }
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public static ArrayList<Particle> read(String filePath)
  {
    return read(new File(filePath));
  }
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public static ArrayList<Particle> read(File file)
  {
    ArrayList<Particle> particles=new ArrayList<Particle>();
    try
    {
      BufferedReader reader=new BufferedReader(new FileReader(file));
      String line="";
      while((line=reader.readLine())!=null)
      {
        String[] input=line.split(":");
        double[] vals=new double[input.length];
        for(int i=1;i<input.length;i++)
        {
          vals[i]=Double.parseDouble(input[i]);
        }
        if(input[0].equals("GravityParticle"))
        {
          particles.add(new GravityParticle(vals[1],vals[2],vals[3],vals[4],vals[5],vals[6]));
        }//double r,double x,double y,double xVel,double yVel,attractionFactor
        else if(input[0].equals("Particle"))
        {
          particles.add(new Particle(vals[1],vals[2],vals[3],vals[4],vals[5]));
        }//double r,double x,double y,double xVel,double yVel
      }
    }
    catch(NullPointerException e)
    {
      System.out.println("Invalid file");
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    return particles;
  }
  
  /** 
   Description.
   @param name Description.
   @return Description.
   */
  public static void write(ArrayList<Particle> particles)
  {
    CharSequence textExtension=".map".subSequence(0,4);
    JFileChooser chooser=new JFileChooser(".//saves");
    if(chooser.showSaveDialog(null)==0)
    {
      File t=chooser.getSelectedFile();
      String fileName=t.getAbsolutePath();
      try
      {
        File file;
        if(fileName.contains(textExtension))file=new File(fileName);
        else file=new File(fileName+".map");
        BufferedWriter writer=new BufferedWriter(new FileWriter(file));
        for(Particle current:particles)
        {
          if(current instanceof GravityParticle)
          {
            GravityParticle temp=(GravityParticle)current;
            writer.write("GravityParticle:");
            writer.write(temp.getRadius()+":");
            writer.write(temp.getCenterX()+":");
            writer.write(temp.getCenterY()+":");
            writer.write(temp.getXVelocity()+":");
            writer.write(temp.getYVelocity()+":");
            writer.write(temp.getFactor()+"");
          }
          //double r,double x,double y,double xVel,double yVel,attractionFactor
          else 
          {
            writer.write("Particle:");
            writer.write(current.getRadius()+":");
            writer.write(current.getCenterX()+":");
            writer.write(current.getCenterY()+":");
            writer.write(current.getXVelocity()+":");
            writer.write(current.getYVelocity()+"");
          }//double r,double x,double y,double xVel,double yVel
          writer.newLine();
        }
        writer.close();
      }
      catch(Exception e)
      {
        e.printStackTrace();
      }
    }
  }
}