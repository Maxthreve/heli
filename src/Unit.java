import java.awt.Color;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class Unit extends JPanel implements ActionListener, KeyListener, MouseMotionListener
{
	
	private final Set<Integer> pressed = new HashSet<Integer>();
	
	private int x = 300;
	private int y = 440;
	private int origin[] = new int[2];
	private double mouse_x;
	private double mouse_y;
	private double mouse_direction;
	
	private int turnspeed = 4;
	
	private boolean turnRight = false;
	private boolean turnLeft = false;
	private boolean onGround = false;
	
	private double netYForce = 0;
	private double netXForce = 0;
	
	private boolean thrust = false;
	private double direction = 90;
	private double weaponDirection;
	private boolean hover = false;
	private double yAcc = 0;
	private double xAcc = 0;
	
	private double yVel = 0;
	private double xVel = 0;
	
	private double gravity = .098;
	private double mass = 4500;
	private double drag;
	
	final int rows = 14;
	final int cols = 2;
	final int width = 20;
	final int height = 30;
	
	private int heliCount = 0;
	private int unitOffset = 0;
	
	private BufferedImage[] sprites = new BufferedImage[rows * cols];
	private BufferedImage[] scaledSprites = new BufferedImage[rows * cols];
	private BufferedImage heli;
	private BufferedImage weapon;
	private BufferedImage weapon_draw;
		
	public Unit()
	{
		init();
		Timer t = new Timer(8,this);
		t.start();
		setFocusable(true);
		addKeyListener(this);
		addMouseMotionListener(this);
	}
	
	public void refresh()
	{
		
		heli = scaledSprites[heliCount];
		++unitOffset;
		if (unitOffset == 4)
		{
			++heliCount;
			unitOffset = 0;
		}
		
		drag = xVel * .4;
		
		if (heliCount == 14)
		{
			heliCount = 2;
		}
		int thrustPower = 0;
		if (thrust && onGround)
		{
				yVel = -4;
		}
		else
		{
			thrustPower = 0;
		}
		
		netYForce = gravity * mass - Math.cos( (direction-90) /(Math.PI*180)) * thrustPower;
		if (hover)
		{
			if (yVel > 0)
				netYForce = -netYForce*.6;
			else
			{
				netYForce = 0;
				yVel = 0;
			
			}
		}
		
		
		//netXForce = Math.sin((direction-90)/(Math.PI*180)) * thrustPower - drag;
		netXForce = Math.sin((direction-90)/(Math.PI*180)) * thrustPower;
		xAcc = netXForce / 5000;
		xVel += xAcc;
		x += xVel;
		
		yAcc = netYForce / mass;
		yVel += yAcc;
		y += yVel;
		
		
		
		if(turnRight&& onGround)
		{
			xVel=4;
		}
		if(turnLeft&& onGround)
		{
			xVel=-4;
		}
		if((!turnRight && !turnLeft && onGround))
			xVel = 0;
		
		if (yVel > 20)
		{
			yVel = 20;
		}
		//else if (yVel < -10)
		//{
			//yVel = -10;
		//}
		
		if(y >= 500)
		{
			y = 500;
			onGround = true;

		}
		else
		{
			onGround = false;
		}
		
		if (xVel == 0)
			heliCount = 0;
		
		origin[0] = x + 20;
		origin[1] = y + 30;
		
		repaint();
	}
	
	public void paintComponent(Graphics g)
	{
		Color sky = new Color(0x79b2ff);
		g.setColor(sky);
		g.fillRect(0,0,this.getWidth(),this.getHeight());
		
		g.setColor(new Color(0x477519));
		g.fillRect(0,560,this.getWidth(),this.getHeight());
		
		
		double radian = (mouse_direction - 90)*Math.PI/180;
		AffineTransform xform = new AffineTransform();
		weapon_draw = new BufferedImage(4*scaledSprites[14].getWidth(), 4*scaledSprites[14].getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		xform.setToRotation(radian, width, height);
		AffineTransformOp rOp = new AffineTransformOp(xform,null);
		rOp.filter(scaledSprites[14], weapon_draw);
		
		
		if (turnLeft)
		{
			xform = AffineTransform.getScaleInstance(-1,1);
			xform.translate(-heli.getWidth(null), 0);
			rOp = new AffineTransformOp(xform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			heli = rOp.filter(heli, null);
		}
		g.drawRect(x,y,width * 2,height * 2);
		g.drawImage(heli, x, y, this);
		g.drawImage(weapon_draw, x + 1, y + 1, this);
	}
		
	public void init()
	{
		try
		{
			BufferedImage heliImage = ImageIO.read(new File("UnitSheet.png"));
			for (int i = 0; i < cols; i++)
			{
				for (int j = 0; j < rows; j++)
				{
					sprites[(i*rows) + j] = heliImage.getSubimage(
						j*width,
						i*height,
						width,
						height
					);
					AffineTransform at = new AffineTransform();
					at.scale(2.0,2.0);
					scaledSprites[(i*rows) + j] = new BufferedImage(2*width,2*height, BufferedImage.TYPE_4BYTE_ABGR);
					AffineTransformOp scaleOp = new AffineTransformOp(at, null);
					scaledSprites[(i*rows) + j] = scaleOp.filter(sprites[(i*rows) + j],scaledSprites[(i*rows) + j]);
				
				}
			}
			heli = scaledSprites[0];

		} catch (IOException e)
		{
			System.out.println("Helicopter Image does not exist!!");
			e.printStackTrace();
		}
		weapon = scaledSprites[14];
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		refresh();
	}

	@Override
	public void keyPressed(KeyEvent e)
	{		
		if (e.getKeyCode() == (KeyEvent.VK_W))
		{
			thrust = true;
		}
		if (e.getKeyCode() == (KeyEvent.VK_A))
		{
				turnLeft = true;
		}
		if (e.getKeyCode() == (KeyEvent.VK_D))
		{
				turnRight = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_CONTROL)
		{
			hover = true;
		}

	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_A )
		{
			turnLeft = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_D)
		{
			turnRight = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_W)
		{
			thrust = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_CONTROL)
		{
			hover = false;
		}
		
	}

	@Override
	public void keyTyped(KeyEvent arg0)
	{
		
	}

	@Override
	public void mouseDragged(MouseEvent arg0)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0)
	{
		System.out.println("MOUSE: " + arg0.getX() + "," + arg0.getY());
		mouse_x = arg0.getX();
		mouse_y = arg0.getY();
		System.out.println("Difference: " + (mouse_x - origin[0]) + "," + (mouse_y - origin[1]));
		mouse_direction = Math.atan((mouse_y - origin[1])/(mouse_x - origin[0]))*180/(Math.PI)+90;
		
		if(mouse_y >= origin[1])
			mouse_direction += 180;
		
		System.out.println(mouse_direction);
		
	}
	
	
}
