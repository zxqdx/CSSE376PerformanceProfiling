import java.awt.image.BufferedImage;


public class Animation {
	
	private BufferedImage[] frames;
	private int currentFrame;
	
	private long startTime;
	private long delayTime;
	
	private boolean hasAnimated;
	
	public Animation() {
		this.hasAnimated = false;
	}
	
	public void setFrames(BufferedImage[] frames) {
		this.frames = frames;
		this.currentFrame = 0;
		this.startTime = System.nanoTime();
		this.hasAnimated = false;
	}
	
	public void setDelayTime(long delayTime) {
		this.delayTime = delayTime;
	}
	
	public void setFrame(int i) {
		this.currentFrame = i;
	}
	
	public void update() {
		if (this.delayTime == -1) {
			return;
		}
		
		long timeSince = (System.nanoTime() - this.startTime) / 1000000;
		
		if (this.delayTime < timeSince) {
			this.currentFrame++;
			this.startTime = System.nanoTime();
		}
		
		if (this.currentFrame == this.frames.length) {
			this.currentFrame = 0;
			this.hasAnimated = true;
		}
	}
	
	public int getFrame() {
		return this.currentFrame;
	}
	
	public BufferedImage getImage() {
		return this.frames[this.currentFrame];
	}
	
	public boolean hasAnimated() {
		return this.hasAnimated;
	}
	
}
