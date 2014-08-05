import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;


public class Player extends JPanel implements KeyListener
{
	private int x = 300;
	private int y = 20;
	
	private boolean turnRight = false;
	private boolean turnLeft = false;
	private boolean onGround = false;
	private boolean showY = false;
	private boolean showX = false;
	private boolean showconstants = false;
	private boolean showthrust = false;
	
	private double netYForce = 0;
	private double netXForce = 0;
	
	private boolean thrust = false;
	private double direction = 90;
	private double yAcc = 0;
	private double xAcc = 0;
	
	private double yVel = 0;
	private double xVel = 0;
	
	private double gravity_constant = 6.67384*Math.pow(10,-11);
	private double earth_mass = 5.972*Math.pow(10, 24);
	private double earth_radius = 6.371*Math.pow(10,6);
	private double heli_mass = 4500;
	private int thrustForce = 0;
	private double gravity_force;
	private double drag;
	
	final int rows = 5;
	final int cols = 5;
	final int width = 40;
	final int height = 40;
	
	private int heliCount = 0;

	private BufferedImage[] sprites = new BufferedImage[rows * cols];
	private BufferedImage[] scaledSprites = new BufferedImage[rows * cols];
	private BufferedImage heli;
	
	public Player()
	{
		init();
	}
	
	public void init()
	{
		try
		{
			BufferedImage heliImage = ImageIO.read(new File("helicopter.png"));
			for (int i = 0; i < rows; i++)
			{
				for (int j = 0; j < cols; j++)
				{
					sprites[(i*cols) + j] = heliImage.getSubimage(
						j*width,
						i*height,
						width,
						height
					);
					AffineTransform at = new AffineTransform();
					at.scale(4.0,4.0);
					scaledSprites[(i*cols) + j] = new BufferedImage(4*width,4*width, BufferedImage.TYPE_4BYTE_ABGR);
					AffineTransformOp scaleOp = new AffineTransformOp(at, null);
					scaledSprites[(i*cols) + j] = scaleOp.filter(sprites[(i*cols) + j],scaledSprites[(i*cols) + j]);
				
				}
			}
			heli = scaledSprites[0];

		} catch (IOException e)
		{
			System.out.println("Helicopter Image does not exist!!");
			e.printStackTrace();
		}
	}
	
	public String toString()
	{
		return x + "," + y + "," + direction;
	}
	
	public void refresh()
	{
		heli = scaledSprites[heliCount];
		++heliCount;
		
		drag = xVel * .4;
		
		if (heliCount == 5)
		{
			heliCount = 0;
		}
		
		
		
		if (thrust)
		{
			if (thrustForce < 44187)
				thrustForce = 44187;
			else
				thrustForce=84000;
		}
		
		else
		{
			thrustForce = 0;
		}
		
		gravity_force = gravity_constant*heli_mass*earth_mass/(earth_radius*earth_radius);
		netYForce = gravity_force - Math.cos( (direction-90) *(Math.PI/180)) * thrustForce;
		netXForce = Math.sin((direction-90)*(Math.PI/180)) * thrustForce;
		
		xAcc = (netXForce/heli_mass)*.0056;
		xVel += xAcc;
		x += xVel;
		
		yAcc = (netYForce / heli_mass)*.0056;
		yVel += yAcc;
		y += yVel;
		
		
		
		if(turnRight)
			direction+=3;
		if(turnLeft)
			direction-=3;
		
		
		if(y >= 470)
		{
			y = 470;
			if (yVel > 1.5) {
			yVel = -yVel*.10;
			}
			else
			{
				yVel = 0;
				if (xVel > .0005)
					xVel = xVel*.3;
				else
					xVel = 0;
				onGround = true;
				heliCount = 2;
			}
			
		}
		else
		{
			onGround = false;
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e)
	{		
		if (e.getKeyCode() == (KeyEvent.VK_UP))
		{
			thrust = true;
		}
		if (e.getKeyCode() == (KeyEvent.VK_LEFT))
		{
			if (!onGround)
				turnLeft = true;
		}
		if (e.getKeyCode() == (KeyEvent.VK_RIGHT))
		{
			if (!onGround)
				turnRight = true;
		}

	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_LEFT )
		{
			turnLeft = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT)
		{
			turnRight = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_UP)
		{
			thrust = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_X)
		{
			showX = !showX;
		}
		if (e.getKeyCode() == KeyEvent.VK_Y)
		{
			showY = !showY;
		}
		if (e.getKeyCode() == KeyEvent.VK_C)
		{
			showconstants = !showconstants;
		}
		if (e.getKeyCode() == KeyEvent.VK_V)
		{
			showthrust = !showthrust;
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0)
	{
		
	}
	public void paint(Graphics g)
	{

		AffineTransform xform = new AffineTransform();
		heli = new BufferedImage(4*width, 4*height, BufferedImage.TYPE_4BYTE_ABGR);
		xform.setToRotation((direction-90)*(Math.PI / 180), width*2, height*2);
		AffineTransformOp rOp = new AffineTransformOp(xform,AffineTransformOp.TYPE_BILINEAR);
		heli = rOp.filter(scaledSprites[heliCount], heli);
		
		g.drawImage(heli, x, y, this);
		g.setColor(Color.BLACK);
		if (showthrust)
		{
			g.drawString("Thrust Force: " + thrustForce, x, y+ 140);
			g.drawString("Gravity Force: " + gravity_force, x, y + 15+ 140);
		}
		if (showY)
		{
			g.drawString("Y Force: " + netYForce, 200, 30);
			g.drawString("Y Acc: " + yAcc, 200, 45);
			g.drawString("Y Vel: " + yVel, 200, 60);
		}
		if (showX)
		{
			g.drawString("X Force: " + netXForce, 10, 0 + 30);
			g.drawString("X Acc: " + xAcc, 10, 45);
			g.drawString("X Vel: " + xVel, 10, 60);
		}
		if (showconstants)
		{
			g.drawString("CONSTANTS", 10, 90);
			g.drawString("Gravity Constant (G):         " + gravity_constant, 10, 105);
			g.drawString("Mass of Earth (M1):           " + earth_mass, 10, 120);
			g.drawString("Mass of Helicopter (M2):  " + heli_mass, 10, 135);
			g.drawString("Radius of Earth (R):          " + earth_radius, 10, 150);
		}
	}
}
