import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

import objects.Digger;


public class KeyBoardListener implements KeyListener{
	
	private Digger player;
	private JFrame frame;
	
	public KeyBoardListener(Digger player, JFrame frame) {
		this.player = player;
		this.frame = frame;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_LEFT
				&& e.getKeyCode() != KeyEvent.VK_RIGHT
				&& e.getKeyCode() != KeyEvent.VK_UP
				&& e.getKeyCode() != KeyEvent.VK_DOWN) {
			this.player.move("left");
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT
				&& e.getKeyCode() != KeyEvent.VK_LEFT
				&& e.getKeyCode() != KeyEvent.VK_UP
				&& e.getKeyCode() != KeyEvent.VK_DOWN) {
			this.player.move("right");
		}
		if (e.getKeyCode() == KeyEvent.VK_UP
				&& e.getKeyCode() != KeyEvent.VK_LEFT
				&& e.getKeyCode() != KeyEvent.VK_RIGHT
				&& e.getKeyCode() != KeyEvent.VK_DOWN) {
			this.player.move("up");
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN
				&& e.getKeyCode() != KeyEvent.VK_LEFT
				&& e.getKeyCode() != KeyEvent.VK_RIGHT
				&& e.getKeyCode() != KeyEvent.VK_UP) {
			this.player.move("down");
		}
		
		this.frame.repaint();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// Does nothing 
		
	}
}
