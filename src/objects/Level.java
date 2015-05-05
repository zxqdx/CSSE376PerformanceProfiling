package objects;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class Level {
	// Tile id constants (add more here)
	public final int DIRT = 0;

	// General Barrier ID.
	public final int BARRIER = 1;
	public final int CLEAR = 2;

	// Item Object ID.
	public final int EMERALD = 3;
	public final int GOLD = 4;
	public final int TREASURE = 20;

	// ArrayList of the Barriers Below. Depends on specific usuage.
	public final ArrayList<Integer> BARRIERS = new ArrayList<>(Arrays.asList(1,
			5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19));

	// Life X Barrier ID. (Used for Life counter)
	public final int BARRIER_L = 5;
	public final int BARRIER_I = 6;
	public final int BARRIER_F = 7;
	public final int BARRIER_E = 8;
	public final int BARRIER_X = 9;

	// Number Barrier ID. (Used for Score counter))
	public final int BARRIER_1 = 10;
	public final int BARRIER_2 = 11;
	public final int BARRIER_3 = 12;
	public final int BARRIER_4 = 13;
	public final int BARRIER_5 = 14;
	public final int BARRIER_6 = 15;
	public final int BARRIER_7 = 16;
	public final int BARRIER_8 = 17;
	public final int BARRIER_9 = 18;
	public final int BARRIER_0 = 19;

	// Level Field Variables.
	private int mapWidth; // The map array width.
	private int mapHeight; // The map array height.
	private int[][] map; // The map array. (height, width)
	private boolean[][] mapFlag; // The map flag array. (height, width)
	private int tileSize; // The tileSize. Preferably 32.
	private ArrayList<Rectangle2D.Double> barrierCollisionBoxes; // The
																	// arraylist
																	// of
																	// barrier
																	// collision
																	// boxes.
	private ArrayList<Rectangle2D.Double> regularCollisionBoxes; // The
																	// arraylist
																	// of
																	// collision
																	// boxes.
	private ArrayList<Gold> goldObjects; // The arraylist of Gold objects.
	private HashMap<Integer, BufferedImage> images; // Hashmap of tile images.

	/**
	 * Constructs a Level object with given tileSize that searches for a
	 * specific fileName
	 * 
	 * @param fileName
	 *            the fileName to search for.
	 * @param tileSize
	 *            the pixel size of a tile. 32 is perferable.
	 */
	public Level(String fileName, int tileSize) {
		this.tileSize = tileSize;
		this.barrierCollisionBoxes = new ArrayList<Rectangle2D.Double>();
		this.regularCollisionBoxes = new ArrayList<Rectangle2D.Double>();
		this.goldObjects = new ArrayList<Gold>();

		this.images = new HashMap<Integer, BufferedImage>();

		populateImages();
		populateMap(fileName, tileSize);

	}

	/**
	 * Populates the images HashMap.
	 * 
	 */
	public void populateImages() {
		try {
			// Regular Tiles/Objects.
			this.images.put(
					this.DIRT,
					ImageIO.read(getClass().getResourceAsStream(
							"/tile/dirt.png")));
			this.images.put(
					this.CLEAR,
					ImageIO.read(getClass().getResourceAsStream(
							"/tile/clear.png")));
			this.images.put(
					this.EMERALD,
					ImageIO.read(getClass().getResourceAsStream(
							"/tile/emeraldOre.png")));
			this.images.put(
					this.GOLD,
					ImageIO.read(getClass().getResourceAsStream(
							"/tile/chest.png")));
			this.images.put(
					this.TREASURE,
					ImageIO.read(getClass().getResourceAsStream(
							"/tile/treasure.png")));

			// Barriers.
			this.images.put(
					this.BARRIER,
					ImageIO.read(getClass().getResourceAsStream(
							"/tile/barrier/barrier.png")));
			this.images.put(
					this.BARRIER_L,
					ImageIO.read(getClass().getResourceAsStream(
							"/tile/barrier/barrierLBlock.png")));
			this.images.put(
					this.BARRIER_I,
					ImageIO.read(getClass().getResourceAsStream(
							"/tile/barrier/barrierIBlock.png")));
			this.images.put(
					this.BARRIER_F,
					ImageIO.read(getClass().getResourceAsStream(
							"/tile/barrier/barrierFBlock.png")));
			this.images.put(
					this.BARRIER_E,
					ImageIO.read(getClass().getResourceAsStream(
							"/tile/barrier/barrierEBlock.png")));
			this.images.put(
					this.BARRIER_X,
					ImageIO.read(getClass().getResourceAsStream(
							"/tile/barrier/barrierXBlock.png")));
			this.images.put(
					this.BARRIER_0,
					ImageIO.read(getClass().getResourceAsStream(
							"/tile/barrier/barrierNumber0.png")));
			this.images.put(
					this.BARRIER_1,
					ImageIO.read(getClass().getResourceAsStream(
							"/tile/barrier/barrierNumber1.png")));
			this.images.put(
					this.BARRIER_2,
					ImageIO.read(getClass().getResourceAsStream(
							"/tile/barrier/barrierNumber2.png")));
			this.images.put(
					this.BARRIER_3,
					ImageIO.read(getClass().getResourceAsStream(
							"/tile/barrier/barrierNumber3.png")));
			this.images.put(
					this.BARRIER_4,
					ImageIO.read(getClass().getResourceAsStream(
							"/tile/barrier/barrierNumber4.png")));
			this.images.put(
					this.BARRIER_5,
					ImageIO.read(getClass().getResourceAsStream(
							"/tile/barrier/barrierNumber5.png")));
			this.images.put(
					this.BARRIER_6,
					ImageIO.read(getClass().getResourceAsStream(
							"/tile/barrier/barrierNumber6.png")));
			this.images.put(
					this.BARRIER_7,
					ImageIO.read(getClass().getResourceAsStream(
							"/tile/barrier/barrierNumber7.png")));
			this.images.put(
					this.BARRIER_8,
					ImageIO.read(getClass().getResourceAsStream(
							"/tile/barrier/barrierNumber8.png")));
			this.images.put(
					this.BARRIER_9,
					ImageIO.read(getClass().getResourceAsStream(
							"/tile/barrier/barrierNumber9.png")));

		} catch (IOException exception) {
			exception.printStackTrace();
			System.out.println("Error: A file was not found.");
		}
	}

	/**
	 * Initializes the Map 2D ArrayList.
	 * 
	 * @param fileName
	 *            the fileName to search for.
	 * @param tileSize
	 *            the pixel size of a tile. 32 is perferable.
	 */
	BufferedImage img = null;
	public void populateMap(String fileName, int tileSize) {
		try {
			String parser = " ";
			String currentLine = null;
			String[] currentLineValues = null;
//			FileReader fileInput = new FileReader(fileName);
//			BufferedReader imageReader = new BufferedReader(new FileReader(fileInput));
			InputStream fileInput = getClass().getResourceAsStream(fileName);
			BufferedReader imageReader = new BufferedReader(new InputStreamReader(fileInput));
			this.mapWidth = Integer.parseInt(imageReader.readLine());
			this.mapHeight = Integer.parseInt(imageReader.readLine());

			this.map = new int[this.mapHeight][this.mapWidth];
			this.mapFlag = new boolean[this.mapHeight][this.mapWidth];

			for (int r = 0; r < this.map.length; r++) {
				// Get line of numbers and spaces.
				currentLine = imageReader.readLine();

				// Eliminates currentLine's space and obtain a array of String
				// integers.
				currentLineValues = currentLine.split(parser);

				for (int c = 0; c < this.map[r].length; c++) {
					// Convert the String integers into integer values to be
					// stored in level.
					this.map[c][r] = Integer.parseInt(currentLineValues[c]);
				}
			}
			img = new BufferedImage(this.mapWidth*this.tileSize,this.mapHeight*this.tileSize,BufferedImage.TYPE_INT_RGB);
			imageReader.close();
			generateBarrierCollisionBoxes();
			generateRegularCollisionBoxes();
			generateGoldObjects();

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * Generates a collision box for the barriers of the level.
	 * 
	 */
	public void generateBarrierCollisionBoxes() {
		for (int r = 0; r < this.map.length; r++) {
			for (int c = 0; c < this.map[r].length; c++) {
				if (this.BARRIERS.contains(this.map[r][c])) {
					this.barrierCollisionBoxes.add(new Rectangle2D.Double(r
							* this.tileSize, c * this.tileSize, this.tileSize,
							this.tileSize));
				}
			}
		}
	}

	/**
	 * Returns the arraylist of barrier collision boxes.
	 * 
	 * @return the arraylist of barrier collision boxes.
	 */
	public ArrayList<Rectangle2D.Double> getBarrierCollisonBoxes() {
		return this.barrierCollisionBoxes;
	}

	/**
	 * Generates a collision box for the regular blocks (dirt and emerald) of
	 * the level.
	 * 
	 */
	public void generateRegularCollisionBoxes() {
		// Clear the array list everytime it is called.
		this.regularCollisionBoxes.clear();

		// Loop through map and generate only required collision boxes.
		for (int r = 1; r < this.map.length; r++) {
			for (int c = 1; c < this.map[r].length; c++) {
				if (!(this.BARRIERS.contains(this.map[r][c]))
						&& this.map[r][c] != this.CLEAR
						&& this.map[r][c] != this.GOLD) {
					this.regularCollisionBoxes.add(new Rectangle2D.Double(r
							* this.tileSize, c * this.tileSize, this.tileSize,
							this.tileSize));
				}
			}
		}
	}

	/**
	 * Returns the arraylist of regular collision boxes.
	 * 
	 * @return the arraylist of regular collision boxes.
	 */
	public ArrayList<Rectangle2D.Double> getRegularCollisionBoxes() {
		return this.regularCollisionBoxes;
	}

	/**
	 * Generates an array list of Gold objects.
	 * 
	 */
	public void generateGoldObjects() {
		// Loop through map and generate Gold objects of level.
		for (int r = 1; r < this.map.length; r++) {
			for (int c = 1; c < this.map[r].length; c++) {
				if (this.map[r][c] == this.GOLD) {
					this.goldObjects.add(new Gold(this, r, c));
				}
			}
		}

	}

	/**
	 * Returns the array list of goldObjects.
	 * 
	 * @return array list of goldObjects.
	 */
	public ArrayList<Gold> getGoldObjects() {
		return this.goldObjects;
	}

	/**
	 * Assigns the proper images to the tiles.
	 * 
	 * @param g2
	 *            the Graphics2D of where the object is painted.
	 */
	public void draw(Graphics2D g2) {
		int currentPosition;
		// cache the tile background in an image so tiles don't need to be drawn again and again redundantly.
		Graphics2D g = img.createGraphics();
		for (int r = 0; r < this.map.length; r++) {
			for (int c = 0; c < this.map[r].length; c++) {
				currentPosition = this.map[r][c];
				drawTileImage(currentPosition, r, c, g);
			}
		}
		g.dispose();
		// draw cached tiles
		g2.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), null);
	}

	/**
	 * Given a tileValue, a row, a column (col), and a Graphics2D object,
	 * drawTileImages obtains the correct image for the tile from the images
	 * HashMap and draws it.
	 * 
	 * @param tileValue
	 *            the tileValue at the row and column.
	 * @param row
	 *            the row of the tile.
	 * @param col
	 *            the column of the tile.
	 * @param g2
	 *            the Graphics2D object to draw on.
	 */
	public void drawTileImage(int tileValue, int row, int col, Graphics2D g2) {
		// FIXME: reduce the number of calls to the code below. When does drawImage really need to be called?
		if (this.mapFlag[row][col]) {return;}
		BufferedImage image = this.images.get(tileValue);
		g2.drawImage(image, row * this.tileSize, col * this.tileSize, null);
		this.mapFlag[row][col] = true;
	}

	/**
	 * Gets the Level's array.
	 * 
	 * @return the Level's array.
	 */
	public int[][] getMap() {
		return this.map;
	}

	/**
	 * Updates the tile at the specified x and y coordinates with the specified
	 * tileID.
	 * 
	 * @param x
	 *            the x coordinate to update.
	 * @param y
	 *            the y coordinate to update.
	 * @param tileID
	 *            the tileID number to update to.
	 */
	public void updateTile(int x, int y, int tileID) {
		this.map[x][y] = tileID;
		// DONE Add code here.
		this.mapFlag[x][y] = false;
	}

	/**
	 * Gets the number of emeralds on the Level.
	 * 
	 * Consider moving this to an emerald class for cohesion; however, if
	 * emerald doesn't do anything, leave as its.
	 * 
	 * @return the number of emerald tiles on the map
	 */
	public int getEmeraldCount() {
		int counter = 0;
		for (int r = 0; r < this.map.length; r++) {
			for (int c = 0; c < this.map[r].length; c++) {
				if (this.map[r][c] == this.EMERALD) {
					counter++;
				}
			}
		}
		return counter;
	}

	/**
	 * Returns the barriers ID arraylist.
	 * 
	 * @return the barriers ID arraylist.
	 */
	public ArrayList<Integer> getBarriersIDList() {
		return this.BARRIERS;
	}

	/**
	 * Converts a given integer number to the correlating barrier number. Mainly
	 * used in score conversion in LevelState.
	 * 
	 * @param numberToConvert
	 *            the integer number to convert.
	 * @return the corresponding barrier number.
	 */
	public int convertToTileID(int numberToConvert) {
		if (numberToConvert == 0) {
			return this.BARRIER_0;
		}
		if (numberToConvert == 1) {
			return this.BARRIER_1;
		}
		if (numberToConvert == 2) {
			return this.BARRIER_2;
		}
		if (numberToConvert == 3) {
			return this.BARRIER_3;
		}
		if (numberToConvert == 4) {
			return this.BARRIER_4;
		}
		if (numberToConvert == 5) {
			return this.BARRIER_5;
		}
		if (numberToConvert == 6) {
			return this.BARRIER_6;
		}
		if (numberToConvert == 7) {
			return this.BARRIER_7;
		}
		if (numberToConvert == 8) {
			return this.BARRIER_8;
		}
		if (numberToConvert == 9) {
			return this.BARRIER_9;
		}
		return -1;
	}

	// Old
	// code.-------------------------------------------------------------------------------
	// /**
	// *
	// *
	// */
	// public void populateLevel() {
	// File level = null;
	//
	// if (this.counter == 0) {
	// level = new File("res/Level 1.txt");
	// } else if (this.counter == 1) {
	// level = new File("res/Level 2.txt");
	// } else {
	// level = new File("res/Level 3.txt");
	// }
	//
	// Scanner input = null;
	//
	// try {
	// input = new Scanner(level);
	//
	// for (int r = 0; r < this.WIDTH; r++) {
	// for (int c = 0; c < this.HEIGHT; c++) {
	// if (!input.hasNextInt()) {
	// break;
	// }
	// this.level[r][c] = input.nextInt();
	// }
	// }
	// } catch (FileNotFoundException e) {
	// System.out.println("File " + level.getAbsolutePath()
	// + " could not be found.");
	// } catch (IOException ioe) {
	// ioe.printStackTrace();
	// } finally {
	// input.close();
	// this.counter++;
	// }
	// // System.out.println(Arrays.deepToString(this.level));
	// }

	// @Override
	// protected void paintComponent(Graphics g) {
	// Graphics2D g2 = (Graphics2D) g;
	//
	// for (int r = 0; r < this.level.length; r++) {
	// for (int c = 0; c < this.level[r].length; c++) {
	// if (this.level[r][c] == this.DIRT) {
	// g2.setColor(Color.GREEN);
	// } else if (this.level[r][c] == this.BARRIER) {
	// g2.setColor(Color.BLUE);
	// } else if (this.level[r][c] == this.CLEAR) {
	// g2.setColor(Color.BLACK);
	// }
	// g2.fillRect(r * this.TILE_SIZE, c * this.TILE_SIZE,
	// this.TILE_SIZE, this.TILE_SIZE);
	// g2.drawRect(r * this.TILE_SIZE, c * this.TILE_SIZE,
	// this.TILE_SIZE, this.TILE_SIZE);
	// }
	//
	// }
	// }
}
