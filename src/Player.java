import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import objects.Level;

public class Player extends Moveable {

	private final double STARTING_X = 3;
	private final double STARTING_Y = 3;
	
	private int life;
	private int maxLife;
	private int attack;
	private int maxAttack;
	private boolean dead;

	private boolean attacking;
	private int attackCost;
	private int attackDamage;

	private ArrayList<BufferedImage[]> sprites;

	private final int IDLE = 0;
	private final int ATTACK = 1;

	public Player(Level level) {
		super(level);
		
		this.boxWidth = 30;
		this.boxHeight = 30;
		this.collisionWidth = 20;
		this.collisionHeight = 20;

		this.moveSpeed = 0.2;
		this.maxSpeed = 0.5;

		this.facingRight = true;

		this.life = 3;
		this.maxLife = 5;

		this.attackCost = 5;
		this.attackDamage = 10;

		try {
			BufferedImage spriteSheet = ImageIO.read(new File(
					"res/playerSpriteSheet.png"));
			this.sprites = new ArrayList<BufferedImage[]>();
			BufferedImage[] playerFrames = new BufferedImage[18];

			for (int i = 0; i < playerFrames.length; i++) {
				playerFrames[i] = spriteSheet.getSubimage(i * this.boxWidth, i
						* this.boxHeight, this.boxWidth, this.boxHeight);
			}
			
			this.sprites.add(playerFrames);

			playerFrames = new BufferedImage[4];
			spriteSheet = ImageIO.read(new File("res/attack.png"));

			for (int i = 0; i < playerFrames.length; i++) {
				playerFrames[i] = spriteSheet.getSubimage(i * this.boxWidth, i
						* this.boxHeight, this.boxWidth, this.boxHeight);
			}

			this.sprites.add(playerFrames);

		} catch (Exception e) {
			e.printStackTrace();
		}

		this.animation = new Animation();
		this.currentAction = this.IDLE;
		System.out.println(sprites.size());
		this.animation.setFrames(this.sprites.get(this.IDLE));
		this.animation.setDelayTime(400);
	}

	public int getLife() {
		return this.life;
	}

	public int getMaxLife() {
		return this.maxLife;
	}

	public int getAttack() {
		return this.attack;
	}

	public int getMaxAttack() {
		return this.maxAttack;
	}

	public void setAttacking() {
		this.attacking = true;
	}
	
	public void getNextPosition() {
		if (this.moveLeft) {
			this.tileMoveX = this.tileMoveX - this.moveSpeed;
			if (Math.abs(this.tileMoveX) >= this.maxSpeed) {
				this.tileMoveX = this.maxSpeed;
			}
		} else if (this.moveRight) {
			this.tileMoveX = this.tileMoveX + this.moveSpeed;
			if (this.tileMoveX >= this.maxSpeed) {
				this.tileMoveX = this.maxSpeed;
			}
		}
		
		else {
			this.tileMoveX = 0;
		}
		
	}

	public void update() {
		getNextPosition();
		super.checkMapCollision();
		super.setTilePosition(this.oldX, this.oldY);

		if (this.attacking) {
			if (this.currentAction != this.ATTACK) {
				this.currentAction = this.ATTACK;
				this.animation.setFrames(this.sprites.get(this.ATTACK));
			}
		} else {
			if (this.currentAction != IDLE) {
				this.currentAction = this.IDLE;
				this.animation.setFrames(this.sprites.get(this.IDLE));
				this.animation.setDelayTime(4000);
			}
		}

		this.animation.update();

		if (this.currentAction != this.attack) {
			if (this.moveLeft) {
				this.facingRight = false;
			} else {
				this.facingRight = true;
			}
		}
	}

	public void draw(Graphics2D g2) {
		super.setMapPosition();

		if (this.facingRight) {
			g2.drawImage(
					this.animation.getImage(),
					(int) (this.tilePositionX + this.pixelPositionX - this.boxWidth / 2),
					(int) (this.tilePositionY + this.pixelPositionY - this.boxHeight / 2),
					null);
		} else {
			g2.drawImage(
					this.animation.getImage(),
					(int) (this.tilePositionX + this.pixelPositionX
							- this.boxWidth / 2 + this.boxWidth),
					(int) (this.tilePositionY + this.pixelPositionY - this.boxHeight / 2),
					-this.boxWidth, this.boxHeight, null);
		}
	}
	
	/**
	 * Resets the player's position to its starting point. Mainly used after
	 * levels are completed.
	 * 
	 */
	public void resetPlayerPosition() {
		this.tilePositionX = this.STARTING_X;
		this.tilePositionY = this.STARTING_Y;
	}

	/**
	 * Updates Digger's level field. Mainly used after levels are completed.
	 * 
	 * @param level
	 *            a new level file
	 */
	public void updateLevel(Level level) {
		this.level = level;
	}
}

