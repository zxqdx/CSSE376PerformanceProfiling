package main;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import state.StateManager;

/**
 * A JPanel object in which every thing is painted on.
 * 
 * @author Mark Hays and his students.. Created Feb 5, 2015.
 */
@SuppressWarnings("serial")
public class GamePanel extends JPanel implements Runnable, KeyListener {
	// Constants
	public static final int GAMEPANEL_WIDTH = 640; // Pixel size of panel.
	public static final int GAMEPANEL_HEIGHT = 640; // Pixel size of panel.

	// Variables
	private Thread thread; // Main thread.
	private boolean isRunning; // If the game is running.

	private BufferedImage image; // The main image.
	private Graphics2D g2; // The main Graphics2D.

	// Wait time calculations.
	private int FPS = 60;
	private long targetTime = 1000 / this.FPS;

	// Stores StateManager
	private StateManager stateManager;

	// Pause status of the panel.
	private boolean pauseStatus;

	/**
	 * Constructs a GamePanel object, a Jpanel, with specific size preference
	 * 
	 */
	public GamePanel() {
		super();
		setPreferredSize(new Dimension(GamePanel.GAMEPANEL_WIDTH,
				GamePanel.GAMEPANEL_HEIGHT));
		setFocusable(true);
		requestFocus();
		this.stateManager = new StateManager();

		// Undeclared at construction;
		this.thread = null;
		this.isRunning = false;
		this.image = null;
		this.g2 = null;
	}

	@Override
	public void addNotify() {
		super.addNotify();
		if (this.thread == null) {
			this.thread = new Thread(this);
			addKeyListener(this);
			this.thread.start();
		}
	}

	@Override
	public void run() {
		initialize();

		// Main game loop.
		while (true) {
			if (!this.isRunning) {
				break;
			}

			long startTime = System.nanoTime();
			// Run the basics.
			update();
			render();
			draw();
			long endTime = System.nanoTime();
			long fps = 1000000000L/(endTime- startTime);
			System.err.println(fps);

			try {
				Thread.sleep(this.targetTime);
			} catch (Exception e) {
				// Print error stack trace and end program.
				e.printStackTrace();
				System.exit(0);
			}

		}

	}

	/**
	 * Sets undeclared variables.
	 * 
	 */
	private void initialize() {
		this.pauseStatus = false;

		// Set isRunning to true.
		this.isRunning = true;

		// Generate a new BufferImage and Graphics2D for use.
		this.image = new BufferedImage(GamePanel.GAMEPANEL_WIDTH,
				GamePanel.GAMEPANEL_HEIGHT, BufferedImage.TYPE_INT_RGB);
		this.g2 = (Graphics2D) this.image.getGraphics();
	}

	/**
	 * Updates any subsequent field if necessary.
	 * 
	 */
	private void update() {
		if (this.pauseStatus) {
			return;
		}

		this.stateManager.update();
	}

	/**
	 * Draws on the Graphics2D object, g2.
	 * 
	 */
	private void render() {
		this.stateManager.draw(this.g2);
	}

	/**
	 * Draws the rendered Graphics2D object.
	 * 
	 */
	private void draw() {
		Graphics g = getGraphics();
		g.drawImage(this.image, 0, 0, GamePanel.GAMEPANEL_WIDTH,
				GamePanel.GAMEPANEL_HEIGHT, null);
		g.dispose();
	}

	/**
	 * Sets the pause status of the panel.
	 * 
	 */
	public void setPause() {
		if (this.pauseStatus) {
			this.pauseStatus = false;
		} else {
			this.pauseStatus = true;
		}
	}

	/**
	 * Checkes the pause status. Preferable method of checking pauseStatus
	 * field.
	 * 
	 * @return the pauseStatus.
	 */
	public boolean isPause() {
		return this.pauseStatus;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// Pauses and Unpauses Game.
		if (e.getKeyCode() == KeyEvent.VK_P) {
			setPause();
			this.stateManager.keyPressed(KeyEvent.VK_P);
		}

		// If game is paused, do not run code.
		if (this.pauseStatus) {
			return;
		}

		// Pass keyCode for manager to handle.
		if (e.getKeyCode() != KeyEvent.VK_P) {
			this.stateManager.keyPressed(e.getKeyCode());
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		this.stateManager.keyReleased(e.getKeyCode());
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// Not Used.

	}
}
