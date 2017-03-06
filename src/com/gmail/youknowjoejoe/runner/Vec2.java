package com.gmail.youknowjoejoe.runner;

public class Vec2 {
	private float x;
	private float y;
	
	public Vec2(float x, float y){
		this.x = x;
		this.y = y;
	}
	
	public Vec2(Vec2 a, Vec2 b){
		x = b.x-a.x;
		y = b.y-a.y;
	}
	
	public Vec2 plus(Vec2 v){
		return new Vec2(x+v.x,y+v.y);
	}
	
	public Vec2 minus(Vec2 v){
		return new Vec2(x-v.x,y-v.y);
	}
	
	public float dot(Vec2 v){
		return x*v.x+y*v.y;
	}
	
	public Vec2 scaledBy(float f){
		return new Vec2(x*f,y*f);
	}
	
	public float getMagnitudeSquared(){
		return this.dot(this);
	}
	
	public Vec2 projectOnto(Vec2 v){
		return v.scaledBy(v.dot(this)/v.getMagnitudeSquared());
	}
	
	public Vec2 normalized(){
		return this.scaledBy(1.0f/(float)Math.sqrt(this.getMagnitudeSquared()));
	}
	
	public Vec2 perpendicular1(){
		return new Vec2(-this.y,this.x);
	}
	
	public Vec2 perpendicular2(){
		return new Vec2(this.y,-this.x);
	}
	
	@Override
	public String toString(){
		return "Vec2:["+x+","+y+"]";
	}
	
	public float getX(){
		return x;
	}
	
	public float getY(){
		return y;
	}
	
	public static Vec2 zero = new Vec2(0,0);
}
