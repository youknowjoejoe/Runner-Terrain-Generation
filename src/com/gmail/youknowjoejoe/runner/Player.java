package com.gmail.youknowjoejoe.runner;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

public class Player extends Character implements KeyListener {
	
	private boolean zDown = false;
	private boolean zTapped = false;
	private boolean xDown = false;
	private boolean xTapped = false;
	private float lastTapped = 0;
	private float runTimeOut = 1.5f;
	private boolean running = false;
	
	private boolean downDown = false;
	private boolean upDown = false;
	
	private boolean rightStep = false;
	
	private float runDelta = 0f;
	private float slowRunDelta = 2.5f;
	private float tappedRunDelta = 20f;
	
	private float runFrame = 0;
	private float totalRunFrames = 16;
	
	private boolean rightDown = false;
	private boolean leftDown = false;
	
	private float speed = 120;
	private float runSpeed = 120;
	private float climbSpeed = 120;
	private float standingFriciton = 0.85f;
	private float runningFriction = 0.975f;
	private float climbingFriction = 0.9f;
	private float jump = 50;
	
	private int direction = 1;
	
	private BufferedImage[][] imgs;
	private int currentSequence;
	private int currentImg;
	
	private static final Vec2 dim = new Vec2(70,100);
	
	public Player(Vec2 pos, Vec2 offset) {
		super(pos, offset, dim, new Vec2(0,0));
		
		DecimalFormat df = new DecimalFormat("00000");
		String[] paths = new String[16];
		for(int i = 0; i < paths.length; i++){
			paths[i] = df.format(i) + ".png";
		}
		
		BufferedImage[] runLeft = new ImageLoader("/resources/runLeft/",paths, true).getImages();
		BufferedImage[] runRight = new ImageLoader("/resources/runRight/",paths, true).getImages();
		BufferedImage[] standRight = new ImageLoader("/resources/standingRight/", new String[]{"00000.png"}, true).getImages();
		BufferedImage[] standLeft = new ImageLoader("/resources/standingLeft/", new String[]{"00000.png"}, true).getImages();
		imgs = new BufferedImage[][]{runLeft, runRight, standLeft, standRight};
		currentSequence = 1;
		currentImg = 0;
	}
	
	@Override
	public void step(float dt){
		
		move(dt);
		
		setFrame(dt);
		
		super.step(dt);
	}
	
	private void move(float dt){
		if(running){
			if(upDown == downDown){
				this.setFriction(runningFriction);
				this.speed = runSpeed;
			} else if (upDown){
				this.setFriction(climbingFriction);
				this.speed = climbSpeed;
			} else if (downDown){
				
			}
		} else {
			this.setFriction(standingFriciton);
		}
		
		int tempDirection = 0;
		
		if(rightDown){
			tempDirection++; 
		}
		if(leftDown){
			tempDirection--;
		}
		if(tempDirection != 0){
			direction = tempDirection;
		}
		
		if((zTapped && rightStep && (runFrame > (totalRunFrames/2))) ^ (xTapped && !rightStep && (runFrame <= (totalRunFrames/2))) && direction != 0){
			this.accelerate(getTheta().scaledBy(speed*direction));
			lastTapped = 0;
			running = true;
			runDelta = tappedRunDelta;
			rightStep = xTapped;
		} else if(running){
			lastTapped+=dt;
		}
		
		zTapped = false;
		xTapped = false;
	}
	
	private void setFrame(float dt){
		if(running){
			boolean almostSlow1 = runFrame <= (totalRunFrames/2);
			boolean almostSlow2 = runFrame <= totalRunFrames && runFrame > totalRunFrames/2;
			runFrame += runDelta*dt;
			if(almostSlow1 && (runFrame > (totalRunFrames/2))){
				if(runDelta == slowRunDelta){
					stopRunning();
				}
				runDelta = slowRunDelta;
			}
			if(runFrame >= totalRunFrames){
				runFrame-=totalRunFrames;
			}
			if(almostSlow2 &&(runFrame <= (totalRunFrames/2))){
				if(runDelta == slowRunDelta){
					stopRunning();
				}
				runDelta = slowRunDelta;
			}
			
			if(lastTapped >= runTimeOut){
				stopRunning();
			}
			
			currentImg = (int) runFrame;
			if(direction < 0){
				currentSequence = 0;
			} else {
				currentSequence = 1;
			}
		} else {
			currentImg = 0;
			if(direction < 0){
				currentSequence = 2;
			} else {
				currentSequence = 3;
			}
		}
		
		
	}
	
	private void stopRunning(){
		runDelta = 0;
		running = false;
		runFrame = 0;
		rightStep = false;
		
		
	}
	
	@Override
	protected void drawImg(Graphics2D g2d) {
		g2d.drawImage(imgs[currentSequence][currentImg],0,0,null);
	}
	
	@Override
	public void draw(Graphics2D g2d){
		super.draw(g2d);
		g2d.setColor(Color.BLACK);
		g2d.drawString(""+runFrame, 0, 0);
		g2d.drawString(""+runDelta, 0, 10);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_Z){
			if(!zDown){
				zTapped = true;
			}
			zDown = true;
		}
		if(e.getKeyCode() == KeyEvent.VK_X){
			if(!xDown){
				xTapped = true;
			}
			xDown = true;
		}
		if(e.getKeyCode() == KeyEvent.VK_RIGHT){
			rightDown = true;
		}
		if(e.getKeyCode() == KeyEvent.VK_LEFT){
			leftDown = true;
		}
		if(e.getKeyCode() == KeyEvent.VK_DOWN){
			downDown = true;
		}
		if(e.getKeyCode() == KeyEvent.VK_UP){
			upDown = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_Z){
			zDown = false;
		}
		if(e.getKeyCode() == KeyEvent.VK_X){
			xDown = false;
		}
		if(e.getKeyCode() == KeyEvent.VK_RIGHT){
			rightDown = false;
		}
		if(e.getKeyCode() == KeyEvent.VK_LEFT){
			leftDown = false;
		}
		if(e.getKeyCode() == KeyEvent.VK_DOWN){
			downDown = false;
		}
		if(e.getKeyCode() == KeyEvent.VK_UP){
			upDown = false;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}
	
}
