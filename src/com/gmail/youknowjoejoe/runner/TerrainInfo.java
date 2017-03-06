package com.gmail.youknowjoejoe.runner;

public class TerrainInfo {
	private float y;
	private Vec2 parallel;
	
	public TerrainInfo(float y, Vec2 parallel){
		this.y = y;
		this.parallel = parallel;
	}
	
	public float getY(){
		return y;
	}
	
	public Vec2 getParallel(){
		return parallel;
	}
}
