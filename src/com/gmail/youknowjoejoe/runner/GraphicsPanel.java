package com.gmail.youknowjoejoe.runner;

import javax.swing.JPanel;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedList;
import java.awt.Dimension;
import java.awt.Color;

@SuppressWarnings("serial")
public class GraphicsPanel extends JPanel implements Runnable{
	
	private final int WIDTH = 800;
	private final int HEIGHT = 600;
	
	private Color clearScreen = new Color(0,0,0);
	private BufferedImage frameRender = null;
	public float dt;
	private double oldTime;
	private double currentTime;
	private double accumulatedTime = 0.0;
	private Double timePassed = 0.0;
	private double timeScale;
	
	private boolean running = true;
	
	private Scene currentScene;
	private Player p;
	
	public GraphicsPanel(float dt, double timeScale){
		this.dt = dt;
		this.timeScale = timeScale;
		
		this.setFocusable(true);
		this.requestFocus();
		this.setPreferredSize(new Dimension(WIDTH,HEIGHT));
		
		p = new Player(new Vec2(0,0), new Vec2(0,-43));
		this.addKeyListener(p);
		
		//Vec2[] pts = {new Vec2(-400,200), new Vec2(-350,210), new Vec2(-300,200), new Vec2(-275,190), new Vec2(-250,185),new Vec2(-200,185), new Vec2(400,185)};
		//currentScene = new Scene(pts,HEIGHT);
		long seed = (long)(Math.random()*1000000000000000000l);
		System.out.println(seed);
		currentScene = new Scene(20000f, 4f, HEIGHT,seed);
		currentScene.addCharacter(p, true, true);
	}
	
	@Override
	public void run(){
		currentTime = System.nanoTime()/1000000000.0;
		oldTime = currentTime;
		
		while(running){
			this.cycle();
		}
	}
	
	public void cycle(){
		currentTime = System.nanoTime()/1000000000.0;
		accumulatedTime += (currentTime-oldTime)*timeScale;
		if(accumulatedTime > dt){
			while(accumulatedTime > dt){
				this.logic(dt);
				accumulatedTime-=dt;
				timePassed+=dt;
			}
			this.repaint();
		} else {
			try {
				Thread.sleep((long) (1000*(dt-accumulatedTime)));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		this.oldTime = currentTime;
	}
	
	public void logic(float dt){
		currentScene.step(dt);
	}
	
	@Override
	public void paintComponent(Graphics g){
		
		frameRender = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		
		//save a normal frame onto frameRender
		{
			Graphics2D g2d = frameRender.createGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			g2d.fillRect(0,0,WIDTH,HEIGHT);
			
			AffineTransform save = g2d.getTransform();
			
			g2d.translate(WIDTH/2, HEIGHT/2);
			
			currentScene.draw(g2d);
			
			g2d.setTransform(save);
			
			g2d.dispose();
		}
		
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setColor(clearScreen);
		g2d.fillRect(0, 0, WIDTH, HEIGHT);
		
		g2d.drawImage(frameRender, 0, 0, null);
	}
	
	public static void main(String[] args){
		JFrame window = new JFrame("Runner");
		GraphicsPanel pane = new GraphicsPanel(1.0f/60.0f,2.0);
		window.add(pane);
		window.setResizable(false);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
		window.pack();
		(new Thread(pane)).start();
	}
}
