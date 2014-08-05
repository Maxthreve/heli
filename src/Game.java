import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Game extends JPanel implements ActionListener
{
	
	private final Set<Integer> pressed = new HashSet<Integer>();
	private ImageIcon grass_icon; 
	private Image grass;
	private ImageIcon sky_icon; 
	private Image sky;
	private Player player = new Player();
	private DataOutputStream out;
	private int Port = 4324;
	private Socket sock;
	
	public Game()
	{	
		Timer t = new Timer(10,this);
		t.start();
		setFocusable(true);
		addKeyListener(player);
		try
		{
			grass_icon = new ImageIcon("grass.png");
			sky_icon = new ImageIcon("sky.png");
			sky = sky_icon.getImage();
			grass = grass_icon.getImage();
		}catch (Exception e){}
		try
		{
			sock = new Socket("localhost", Port);
		}catch(Exception e){}
	}
	
	
	
	
	public void paintComponent(Graphics g)
	{
		for (int x = 0; x < this.getWidth(); x++)
			g.drawImage(sky,x,0,null);
		
		for (int x = 0; x < this.getWidth()/100 + 1;x++)
		{
			g.drawImage(grass,x*100,560,null);
		}
		player.paint(g);
	}
		
	

	@Override
	public void actionPerformed(ActionEvent e)
	{
		try
		{
			out.writeChars(player.toString());
		}catch (Exception a){}
		System.out.println(player);
		player.refresh();
		repaint();
	}
}
