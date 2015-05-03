package toolObjects;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import main.GamePanel;


/**
 * A class dedicated to using larger moving background that are not tile based.
 *
 * @author Mark Hays and his students.
 *         Created Feb 5, 2015.
 */
public class Background {
	private BufferedImage image; // The background image.
	private double positionX; // Starting position.
	private double positionY; // Starting position.
	private double dx; // The shifting rate in the horizontal direction.
	private double dy; // The shifting rate in the vertical direction.
	
	/**
	 * Constructor for a Background object.
	 *
	 * @param fileName the location
	 * @param shiftingRate the rate the menu is moving (preferably horizontal)
	 */
	public Background(String fileName, double shiftingRate) {
		try {
			
			this.image = ImageIO.read(getClass().getResourceAsStream(fileName));
			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		} 
	}
	
	/**
	 * Sets the Vector of the Background.
	 * 
	 * @param dx the horizontal component vector.
	 * @param dy the vertical component vector.
	 */
	public void setVector(double dx, double dy) {
		this.dx = dx;
		this.dy = dy;
	}
	
	/**
	 * Updates the Background if it is moving.
	 *
	 */
	public void update() {
		this.positionX = this.positionX + this.dx;
		this.positionY = this.positionY + this.dy;
	}
	
	/**
	 * Draws the Background on a given Graphics2D object.
	 *
	 * @param g2 the given Graphics2D object.
	 */
	public void draw(Graphics2D g2) {
		// The overlapping images x value.
		int overlapX;
		
		// Draws main Background.
		g2.drawImage(this.image, (int) this.positionX, (int) this.positionY, null);
		
		// Checks where the main Background image is drifing towards.
		if (this.positionX != 0) {
			if (this.positionX < 0) {
				overlapX = (int) this.positionX + GamePanel.GAMEPANEL_WIDTH;
			} else {
				overlapX = (int) this.positionX - GamePanel.GAMEPANEL_WIDTH;
			}
			g2.drawImage(this.image, overlapX, (int) this.positionY, null);
		}

	}
}	
