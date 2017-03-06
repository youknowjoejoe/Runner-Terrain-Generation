package com.gmail.youknowjoejoe.runner;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Detail {
	
	private Vec2 coord;
	private BufferedImage img;
	
	public Detail(BufferedImage img, Vec2 coord){
		this.img = img;
		this.coord = coord;
	}
	
	public void draw(Graphics2D g2d){
		AffineTransform save = g2d.getTransform();
		
		g2d.translate((coord.getX()-img.getWidth()/2), (coord.getY()-img.getHeight()));
		g2d.drawImage(img,0,0,null);
		
		g2d.setTransform(save);
	}
}
