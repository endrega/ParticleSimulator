package net.starvec;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class Main extends Canvas implements Runnable
{
	private static final long serialVersionUID = -3378274985677112147L;

	static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	public static final int WINDOWWIDTH = screenSize.width;
	public static final int WINDOWHEIGHT = screenSize.height;
	//public static final int WINDOWWIDTH = 1920, WINDOWHEIGHT = WINDOWWIDTH/16*9;
	public static final int XSTARTBOUND = 0, XENDBOUND = WINDOWWIDTH - 0;
	public static final int YSTARTBOUND = 0, YENDBOUND = WINDOWHEIGHT - 0;
	public static final int WIDTH = XENDBOUND-XSTARTBOUND, HEIGHT = YENDBOUND-YSTARTBOUND;
	public static final boolean frameCounterState = true;
	public static int frames;
	public static int ticks;
	public static int displayFrames;
	public static int displayTicks;
	public static long timer;
	public static int simTime = 0;
	private Thread thread;
	private boolean running = false;
	private static double DEFAULTPARTICLECOUNT;
	private static double gravMultiplier;

	private static ArrayList<Particle> particles = new ArrayList<Particle>();
	private Image backgroundImage = texture("res/background.jpg");

	public static void main(String[] args)
	{
		do
		{
			try
			{
				DEFAULTPARTICLECOUNT = Double.parseDouble(JOptionPane.showInputDialog("Enter the number of particles (1 - 1000)"));
			} catch (NullPointerException e)
			{
				System.exit(1);
			}
			
		} while (DEFAULTPARTICLECOUNT < 0 || DEFAULTPARTICLECOUNT > 1000);
		
		do
		{
			try
			{
				gravMultiplier = Double.parseDouble(JOptionPane.showInputDialog("Enter the gravitational multiplier (1 - 2,147,483,647)"));
			} catch (NullPointerException e)
			{
				System.exit(1);
			}
			
		} while (gravMultiplier < 1 || gravMultiplier > 2147483647);
		
		//DEFAULTPARTICLECOUNT = 300;
		for (int p = 0; p < DEFAULTPARTICLECOUNT; p++)
			particles.add(new Particle(XSTARTBOUND, YSTARTBOUND, XENDBOUND, YENDBOUND, 3.0, 12.0, 2000.0, 0.0, 1000.00, 0.9, 0.3, 1.0, 10.0));

		new Main();
	}

	public Main()
	{
		new Window(WINDOWWIDTH, WINDOWHEIGHT, "Particle Simulator", this);
	}

	public synchronized void start()
	{
		thread = new Thread(this);
		thread.start();
		running = true;
	}

	public synchronized void stop()
	{
		try
		{
			thread.join();
			running = false;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void run()
	{
		long lastTime = System.nanoTime();
		double amountOfTicks = 24.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		timer = System.currentTimeMillis();
		frames = 0;
		ticks = 0;
		while(running)
		{
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while(delta >= 1)
			{
				tick();
				ticks++;
				delta--;
				//render();
				//frames++;
			}
			if(running)
			{
				render();
				frames++;
			}
		}
		stop();
	}

	private static void tick()
	{	
		//System.out.println(particles.size());
		Particle tParticle = null;
		Particle cParticle = null;
		for (int tp = 0; tp < particles.size(); tp++)
		{
			//System.out.println(particles.get(tp).getTemperature());
			//System.out.println(particles.get(tp).getThermalEnergy());

			if (particles.size() > 1)
			{
				for (int cp = tp+1; cp < particles.size(); cp++)
				{
					if(tp != cp)
					{
						tParticle = particles.get(tp);
						cParticle = particles.get(cp);

						double distance = Math.sqrt(Math.pow(tParticle.getxLoc()-cParticle.getxLoc(), 2) + Math.pow(tParticle.getyLoc()-cParticle.getyLoc(), 2));
						//System.out.println(distance);

						if (distance < (tParticle.getRadius() + cParticle.getRadius())/1.5 || distance < 1)
						{
							//System.out.println("tParticle = " + particles.indexOf(tParticle) + ", cParticle = " + particles.indexOf(cParticle));
							//System.out.print("tCoords = (" + tParticle.getxLoc() + ", " + tParticle.getyLoc() + ") cCoords = (" + cParticle.getxLoc() + ", " + cParticle.getyLoc() + ") ");
							double newXLoc = weightedAverage(tParticle.getxLoc(), tParticle.getMass(), cParticle.getxLoc(), cParticle.getMass());
							double newYLoc = weightedAverage(tParticle.getyLoc(), tParticle.getMass(), cParticle.getyLoc(), cParticle.getMass());
							//System.out.println("nCoords = (" + newXLoc + ", " + newYLoc + ")");
							double newRadius = Math.pow( (3* (tParticle.getVolume()+cParticle.getVolume()) ) / (4*Math.PI), (1/3.0) );
							//System.out.println("tRadius = " + tParticle.getRadius() + ", cRadius = " + cParticle.getRadius() + ", nRadius = " + newRadius);
							double newDensity = weightedAverage(tParticle.getDensity(), tParticle.getMass(), cParticle.getDensity(), cParticle.getMass());
							//System.out.println("tDensity = " + tParticle.getDensity() + ", cDensity = " + cParticle.getDensity() + ", nDensity = " + newDensity);
							double newThermalEnergy = tParticle.getThermalEnergy()*1.025 + cParticle.getThermalEnergy()*1.025;
							double newThermalMass = weightedAverage(tParticle.getThermalMass(), tParticle.getMass(), cParticle.getThermalMass(), cParticle.getMass());
							double newEmissivity = weightedAverage(tParticle.getEmissivity(), tParticle.getMass(), cParticle.getEmissivity(), cParticle.getMass());
							//System.out.println("tEmissivity = " + tParticle.getEmissivity() + ", cEmissivity = " + cParticle.getEmissivity() + ", nEmissivity = " + newEmissivity);
							double xCom = (Math.cos(tParticle.getDirection())*tParticle.getSpeed()*tParticle.getMass() + Math.cos(cParticle.getDirection())*cParticle.getSpeed()*cParticle.getMass()) / (tParticle.getMass() + cParticle.getMass());
							double yCom = (Math.sin(tParticle.getDirection())*tParticle.getSpeed()*tParticle.getMass() + Math.sin(cParticle.getDirection())*cParticle.getSpeed()*cParticle.getMass()) / (tParticle.getMass() + cParticle.getMass());
							double newSpeed = Math.sqrt(Math.pow(xCom, 2) + Math.pow(yCom, 2));
							double newDirection = Math.atan2(yCom, xCom);
							double newStrength = weightedAverage(tParticle.getStrength(), tParticle.getMass(), cParticle.getStrength(), cParticle.getMass());

							//System.out.print("tMass = " + tParticle.getMass() + ", cMass = " + cParticle.getMass());

							particles.add(new Particle(newXLoc, newYLoc, newRadius, newSpeed, newDirection, newDensity, newThermalEnergy, newThermalMass, newEmissivity, newStrength));
							//System.out.println(", nMass = " + newMass + ", pMass = " + particles.get(particles.size()-1).getMass());
							particles.remove(particles.indexOf(tParticle));
							particles.remove(particles.indexOf(cParticle));
						}
						else
						{
							double attractionForce = (tParticle.getMass()*cParticle.getMass()*0.0000000000667408*gravMultiplier) / Math.pow(distance , 2);
							double acceleration = attractionForce/tParticle.getMass();
							double direction = Math.atan2(tParticle.getyLoc() - cParticle.getyLoc(), tParticle.getxLoc() - cParticle.getxLoc());
							double newDirection = Math.atan2(Math.sin(tParticle.getDirection()) - Math.sin(direction)*acceleration, Math.cos(tParticle.getDirection()) - Math.cos(direction)*acceleration);
							tParticle.setDirection(newDirection);
							tParticle.setSpeed(tParticle.getSpeed()+acceleration/32);
							tParticle.setThermalEnergy(tParticle.getThermalEnergy() - (tParticle.getEmissivity()*tParticle.getSurfaceArea()*0.0000000567*(tParticle.getTemperature()+273))/32);
						}		
					}
				}
			}
			else
			{
				tParticle = particles.get(0);
				tParticle.setThermalEnergy(tParticle.getThermalEnergy() - (tParticle.getEmissivity()*tParticle.getSurfaceArea()*0.0000000567*(tParticle.getTemperature()+273))/32);
			}
		}

		for (int p = 0; p < particles.size(); p++)
		{
			Particle particle = particles.get(p);
			double newXLoc = particle.getSpeed()*Math.cos(particle.getDirection()) + particle.getxLoc();
			double newYLoc = particle.getSpeed()*Math.sin(particle.getDirection()) + particle.getyLoc();

			if (newXLoc-particle.getRadius() <= XSTARTBOUND || newXLoc+particle.getRadius() >= XENDBOUND)
			{
				particle.setDirection(0.5*Math.PI-particle.getDirection()+0.5*Math.PI);
				particle.setSpeed(particle.getSpeed() - particle.getSpeed()*0.4*Math.abs(Math.cos(particle.getDirection())));
				particle.setxLoc(particle.getSpeed()*Math.cos(particle.getDirection()) + particle.getxLoc());
				particle.setyLoc(particle.getSpeed()*Math.sin(particle.getDirection()) + particle.getyLoc());
			}
			else if (newYLoc-particle.getRadius() <= YSTARTBOUND || newYLoc+particle.getRadius() >= YENDBOUND)
			{
				particle.setDirection(Math.PI-particle.getDirection()+Math.PI);
				particle.setSpeed(particle.getSpeed() - particle.getSpeed()*0.4*Math.abs(Math.sin(particle.getDirection())));
				particle.setxLoc(particle.getSpeed()*Math.cos(particle.getDirection()) + particle.getxLoc());
				particle.setyLoc(particle.getSpeed()*Math.sin(particle.getDirection()) + particle.getyLoc());
			}
			else
			{
				particle.setxLoc(newXLoc);
				particle.setyLoc(newYLoc);
			}

			if (particle.getxLoc() > XENDBOUND+20 || particle.getxLoc() < XSTARTBOUND-20 || particle.getyLoc() > YENDBOUND+20 || particle.getyLoc() < YSTARTBOUND-20)
				particles.remove(particles.indexOf(particle));

		}

		simTime++;
		return;
	}

	private void render()
	{
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null)
		{
			this.createBufferStrategy(2);
			return;
		}

		Graphics g = bs.getDrawGraphics();

		//Background
		g.drawImage(backgroundImage, 0, 0, WINDOWWIDTH, WINDOWHEIGHT, 0, 0, 1920, 1080, null);

		//Particles
		for (int p = 0; p < particles.size(); p++)
		{
			g.setColor(particles.get(p).getColor());
			g.fillOval((int)(particles.get(p).getxLoc()-particles.get(p).getRadius()), 
					(int)(particles.get(p).getyLoc()-particles.get(p).getRadius()), 
					(int)(particles.get(p).getSize()), 
					(int)(particles.get(p).getSize()));
		}

		//Information display
		if (frameCounterState)
		{
			if(System.currentTimeMillis() - timer > 1000)
			{
				timer += 1000;
				//System.out.println("FPS: "+ frames);
				displayFrames = frames;
				displayTicks = ticks;
				frames = 0;
				ticks = 0;
			}
			g.setFont(new Font("Arial", Font.BOLD, 12));
			g.setColor(Color.GREEN);
			g.drawString("fps " + Integer.toString(displayFrames), 3, 13);
			g.drawString("tps " + Integer.toString(displayTicks), 3, 26);
			g.drawString("particles " + Integer.toString(particles.size()), 3, 39);
		}

		g.dispose();
		bs.show();
	}

	public Image texture(String imagePath)
	{
		File imageFile = new File(imagePath);

		if (imageFile.exists())
		{
			System.out.println("Found " + imagePath);
			return (new ImageIcon(imagePath).getImage());	
		}
		else
		{
			try
			{
				URL path = this.getClass().getClassLoader().getResource(imagePath);
				ImageIcon img = new ImageIcon(path);
				return(new ImageIcon(img.getImage()).getImage());
			}
			catch (NullPointerException e)
			{
				System.out.println("Could not find " + imagePath);
				System.exit(1);
				return(null);
			}

		}	
	}

	public static double average(double x, double y) {
		return (x+y)/2;
	}

	public static double weightedAverage(double x, double xMass, double y, double yMass) 
	{
		double totalMass = xMass+yMass;
		double xWeight = xMass/totalMass;
		double yWeight = yMass/totalMass;
		return (x*xWeight + y*yWeight);
	}

	public static int min(ArrayList<Double> list)
	{
		int minIndex = 0;
		double minValue = list.get(minIndex);

		for (int i = 0; i < list.size(); i++)
		{
			if (list.get(i) < minValue)
			{
				minIndex = i;
				minValue = list.get(i);
			}
		}
		return (minIndex);
	}

	public static int min(int a, int b)
	{
		if (a > b)
			return b;
		else if (b > a)
			return a;
		else
			return a;
	}
}