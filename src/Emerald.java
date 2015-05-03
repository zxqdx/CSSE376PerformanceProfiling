import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;


public class Emerald extends JComponent {
	
	private BufferedImage img;
	
	public Emerald() {
		try {
			this.img = ImageIO.read(new File("res/emeraldOre.png"));
		} catch (IOException e) {
		}
	}
	
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(this.img, 0, 0, null);
	}
}