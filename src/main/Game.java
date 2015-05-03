package main;
import javax.swing.JFrame;

/**
 * The main class for your arcade game.
 * 
 * @author Mark Hays and his students.
 *
 */
public class Game {	
	/**
	 * @param args
	 * 
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame("Digger");
		
		GamePanel gamePanel = new GamePanel();
		frame.setContentPane(gamePanel);
		
		// Setting frame basics.
		frame.pack();
		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setResizable(true);
		
	} 

}
