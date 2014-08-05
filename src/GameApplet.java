import java.awt.BorderLayout;

import javax.swing.JApplet;
import javax.swing.JPanel;


public class GameApplet extends JApplet
{
	public void init()
	{
		Game game  = new Game();
		JPanel p = new JPanel();
		add(p);
		p.setLayout(new BorderLayout());
		p.add(game, BorderLayout.CENTER);
		
		setSize(800,600);
		setVisible(true);
	}
}
