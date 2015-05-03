import java.awt.geom.Rectangle2D;

import objects.Level;

public abstract class Moveable {
	protected int tileSize;
	protected double tilePositionX;
	protected double tilePositionY;
	protected double tileMoveX;
	protected double tileMoveY;
	
	protected int collisionWidth;
	protected int collisionHeight;
	
	protected int boxWidth;
	protected int boxHeight;
	
	protected int currentPositonX;
	protected int currentPositonY;
	protected double nextX;
	protected double nextY;
	protected double oldX;
	protected double oldY;
	protected boolean upperLeft;
	protected boolean upperRight;
	protected boolean lowerLeft;
	protected boolean lowerRight;
	
	protected Animation animation;
	protected int currentAction;
	protected int previousAction;
	protected boolean facingRight;
	
	protected boolean moveLeft;
	protected boolean moveRight;
	protected boolean moveUp;
	protected boolean moveDown;
	
	protected double moveSpeed;
	protected double maxSpeed;
	
	protected Level level;
	protected int levelTileSize;
	
	protected int pixelPositionX;
	protected int pixelPositionY;
	
	/**
	 * TODO Put here a description of what this constructor does.
	 *
	 * @param level
	 */
	public Moveable(Level level) {
		this.level = level;
		this.levelTileSize = 32;
	}
	
	public Rectangle2D.Double generateRectangle() {
		return new Rectangle2D.Double(this.tilePositionX - this.collisionWidth, 
				this.tilePositionY - this.collisionHeight, this.collisionWidth,
				this.collisionHeight);
	}
	
	public boolean crosses(Moveable moveObj) {
		Rectangle2D.Double rect1 = generateRectangle();
		Rectangle2D.Double rect2 = moveObj.generateRectangle();
		return rect1.intersects(rect2);		
	}
	
//	public void calculateCorners(double x, double y) {
//        int leftTile = (int)(x - this.collisionWidth / 2) / this.tileSize;
//        int rightTile = (int)(x + this.collisionWidth / 2 - 1) / this.tileSize;
//        int topTile = (int)(y - this.collisionHeight / 2) / this.tileSize;
//        int bottomTile = (int)(y + this.collisionHeight / 2 - 1) / this.tileSize;
//        if(topTile < 0 || bottomTile >= this.level.getMapHeight() ||
//                leftTile < 0 || rightTile >= this.level.getMapWidth()) {
//        	this.upperLeft = false;
//        	this.upperRight = false;
//        	this.lowerLeft = false;
//        	this.lowerRight = false;
//            return;
//        }
//        int topLeft = this.level.getType(topTile, leftTile);
//        int topRight = this.level.getType(topTile, rightTile);
//        int bottomLeft = this.level.getType(bottomTile, leftTile);
//        int bottomRight = this.level.getType(bottomTile, rightTile);
//        if (topLeft == this.level.BARRIER) {
//        	this.upperLeft = true;
//        }
//        if (topRight == this.level.BARRIER) {
//        	this.upperRight = true;
//        }
//        if (bottomLeft == this.level.BARRIER) {
//        	this.lowerLeft = true;
//        }
//        if (bottomRight == this.level.BARRIER) {
//        	this.lowerRight = true;
//        }
//	}
	
	public void checkMapCollision() {
		this.currentPositonX = (int) this.tilePositionX / this.tileSize;
		this.currentPositonY = (int) this.tilePositionY / this.tileSize;
		
		this.nextX = this.tilePositionX + this.tileMoveX;
		this.nextY = this.tilePositionY + this.tileMoveY;
		
		this.oldX = this.tilePositionX;
		this.oldY = this.tilePositionY;
		
//		calculateCorners(this.tilePositionX, this.nextY);
		if (this.tileMoveY < 0) {
			if (this.upperLeft || this.upperRight) {
				this.tileMoveY = 0;
				this.oldY = this.currentPositonY * this.tileSize + this.collisionHeight / 2;
			} else {
				this.oldY = this.oldY + this.tileMoveY;
			}
		} else if (this.tileMoveY > 0) {
			if (this.lowerLeft || this.lowerRight) {
				this.tileMoveY = 0;
				this.oldY = (this.currentPositonY + 1) * this.tileSize + this.collisionHeight / 2;
			} else {
				this.oldY = this.oldY + this.tileMoveY;
			}
		}
		
//		calculateCorners(this.nextX, this.tilePositionY);
		if (this.tileMoveX < 0) {
			if (this.upperLeft || this.lowerLeft) {
				this.tileMoveX = 0;
				this.oldX = this.currentPositonX * this.tileSize + this.collisionWidth / 2;
			} else {
				this.oldX = this.oldX + this.tileMoveX;
			}
		} else if (this.tileMoveX > 0) {
			if (this.upperRight || this.lowerRight) {
				this.tileMoveX = 0;
				this.oldX = (this.currentPositonX + 1) * this.tileSize + this.collisionWidth / 2;
			} else {
				this.oldX = this.oldX + this.tileMoveX;
			}
		}
	}
	public int getTilePositionX() {
		return (int) this.tilePositionX;
	}
	
	public int getTilePositionY() {
		return (int) this.tilePositionY;
	}
	
	public int getWidth() {
		return this.boxWidth;
	}
	
	public int getHeight() {
		return this.boxHeight;
	}
	
	public int getCollisionWidth() {
		return this.collisionWidth;
	}
	
	public int getCollisionHeight() {
		return this.collisionHeight;
	}
	
	public void setTilePosition(double x, double y) {
		this.tilePositionX = x;
		this.tilePositionY = y;
	}
	
	public void setTileMove(double x, double y) {
		this.tileMoveX = x;
		this.tileMoveY = y;
	}
	
	public void setMapPosition() {
		this.pixelPositionX = (int) (this.tilePositionX * 32);
		this.pixelPositionY = (int) (this.tilePositionY * 32);
	}
	
	public void setMoveLeft(boolean value) {
		this.moveLeft = value;
	}
	
	public void setMoveRight(boolean value) {
		this.moveRight = value;
	}
	
	public void setMoveUp(boolean value) {
		this.moveUp = value;
	}
	
	public void setMoveDown(boolean value) {
		this.moveDown = value;
	}
}
