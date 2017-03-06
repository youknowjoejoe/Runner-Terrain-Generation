package com.gmail.youknowjoejoe.runner;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public abstract class Character implements Entity {
	
	private Vec2 pos;
	private Vec2 vel;
	private Vec2 oldVel;
	
	private Vec2 offset;
	
	private float friction; 
	private int img = 0;
	
	private Vec2 theta = new Vec2(1,0);
	private Vec2 gravity = new Vec2(0,300);
	
	public Character(Vec2 pos, Vec2 offset, Vec2 vel){
		this.pos = pos;
		this.offset = offset;
		this.vel = vel;
		this.oldVel = vel;
		this.friction = 0.9f;
	}
	
	public Character(Vec2 pos, Vec2 offset, Vec2 dim, Vec2 vel){
		this.pos = pos;
		this.offset = offset.minus(dim.scaledBy(0.5f));
		this.vel = vel;
		this.oldVel = vel;
		this.friction = 0.9f;
	}
	
	
	@Override
	public void step(float dt){
		vel = vel.plus(gravity.scaledBy(dt));
		
		pos = pos.plus(vel.plus(oldVel).scaledBy(dt/2));
		
		oldVel = vel;
	}
	
	private void applyFriction(){
		vel = vel.scaledBy(friction);
	}
	
	@Override
	public void draw(Graphics2D g2d){
		AffineTransform save = g2d.getTransform();
		AffineTransform tx = new AffineTransform();
		tx.translate(offset.getX()+pos.getX(), offset.getY()+pos.getY());
		//tx.rotate(theta.getX(),theta.getY());
		g2d.transform(tx);
		drawImg(g2d);
		g2d.setTransform(save);
	}
	
	protected abstract void drawImg(Graphics2D g2d);
	
	public void accelerate(Vec2 ax){
		this.vel = vel.plus(ax);
	}
	
	public void translate(Vec2 tx){
		this.pos = pos.plus(tx);
	}
	
	public void solveCollision(Scene sc){
		TerrainInfo ci = sc.getTerrainInfo(pos.getX());
		theta = ci.getParallel();
		
		if(pos.getY() > ci.getY()){
			pos = new Vec2(pos.getX(), ci.getY());
			Vec2 perp = theta.perpendicular1();
			vel = vel.minus(perp.scaledBy(perp.dot(vel)));
			applyFriction();
		}
	}
	
	public void setImg(int img){
		this.img = img;
	}
	
	public Vec2 getTheta(){
		return theta;
	}
	
	public Vec2 getPos(){
		return pos;
	}
	
	protected void setFriction(float friction){
		this.friction = friction;
	}
}
