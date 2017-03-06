package com.gmail.youknowjoejoe.runner;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Scene {
	private Vec2[] pts;
	private List<Character> chars;
	private List<Detail> tallGrass;
	private Character cameraFocus;
	private float grassZoneLength;
	private BufferedImage[] grassTextures;
	
	private BufferedImage dirt;
	private BufferedImage grass;
	private int ptHeight;
	private int height;
	
	public Scene(float width, float ptsDensity, int viewHeight, long seed){
		generatePts(width,ptsDensity, width/8, 0.5f, seed);
		smooth(pts,10);
		this.ptHeight = getPtHeight();
		this.height = viewHeight+getPtHeight();
		this.chars = new ArrayList<Character>();
		this.tallGrass = new ArrayList<Detail>();
		grassZoneLength = 50;
		grassTextures = new ImageLoader("/resources/", new String[]{"tallGrass1.png"}, true).getImages();
		generateTallGrass(seed);
		generateGrass(3f);
		generateDirt();
	}
	
	public int getPtHeight(){
		int heighest = 0;
		for(Vec2 pt: pts){
			if(pt.getY() > heighest) heighest = (int) Math.ceil(pt.getY());
		}
		return heighest;
	}
	
	public void generatePts(float width, float density, float initialVariation, float scalar, long seed){ //scalar is below one
		Random r = new Random(seed);
		
		this.pts = new Vec2[(int) (width*density)];
		
		float maxHeight = initialVariation/(1-scalar);
		
		pts[0] = new Vec2(0,maxHeight);
		pts[pts.length-1] = new Vec2(width,maxHeight);
		
		randomizeMidpoint(r, initialVariation, scalar, 0, pts.length-1, pts);
	}
	
	public void generatePts(float width, float height, float density, boolean incline, long seed){
		Random r = new Random(seed);
		
		this.pts = new Vec2[(int) (width*density)];
		
		if(incline){
			pts[0] = new Vec2(0,height);
			pts[pts.length-1] = new Vec2(width,0);
		} else {
			pts[0] = new Vec2(0,0);
			pts[pts.length-1] = new Vec2(width,height);
		}
		
		randomizeMidpoint(r, 0, pts.length-1, pts);
	}
	
	public void randomizeMidpoint(Random r, int startIndex, int lastIndex, Vec2 pts[]){
		int midIndex = (startIndex+lastIndex)/2;
		if(midIndex == startIndex) return;
		float range = (pts[lastIndex].getY()-pts[startIndex].getY());
		pts[midIndex] = pts[startIndex].plus(pts[lastIndex]).scaledBy(0.5f).plus(new Vec2(0,r.nextFloat()*(range) - (range/2.0f)));
		randomizeMidpoint(r, startIndex, midIndex, pts);
		randomizeMidpoint(r, midIndex, lastIndex, pts);
	}
	
	public void randomizeMidpoint(Random r, float range, float scalar, int startIndex, int lastIndex, Vec2 pts[]){
		int midIndex = (startIndex+lastIndex)/2;
		if(midIndex == startIndex) return;
		pts[midIndex] = pts[startIndex].plus(pts[lastIndex]).scaledBy(0.5f).plus(new Vec2(0,r.nextFloat()*(range) - (range/2.0f)));
		randomizeMidpoint(r, range*scalar, scalar, startIndex, midIndex, pts);
		randomizeMidpoint(r, range*scalar, scalar, midIndex, lastIndex, pts);
	}
	
	public void smooth(Vec2[] pts, int iterations){
		for(int rep = 0; rep < iterations; rep++){
			for(int i = 0; i < pts.length/2 - 1; i++){
				pts[pts.length/2 + i] = new Vec2(pts[pts.length/2 + i].getX(),pts[pts.length/2 + i - 1].plus(pts[pts.length/2 + i]).plus(pts[pts.length/2 + i + 1]).scaledBy(1.0f/3.0f).getY());
				pts[pts.length/2 - i] = new Vec2(pts[pts.length/2 - i].getX(),pts[pts.length/2 - i + 1].plus(pts[pts.length/2 - i]).plus(pts[pts.length/2 - i - 1]).scaledBy(1.0f/3.0f).getY());
			}
		}
	}
	
	public void generateTallGrass(long seed){
		Random r = new Random(seed);
		
		for(float i = pts[0].getX(); i < pts[pts.length-1].getX(); i += grassZoneLength){
			BufferedImage img = grassTextures[(int) (r.nextDouble()*grassTextures.length)];
			float x = (float) (r.nextDouble()*(grassZoneLength-img.getWidth()/2.0) + i + img.getWidth()/2.0f);
			tallGrass.add(new Detail( img, new Vec2(x,getTerrainInfo(x).getY()) ));
		}
	}
	
	public void generateDirt(){
		BufferedImage dirtTile = new ImageLoader("/resources/", new String[]{"dirt.png"}, true).getImage(0);
		
		dirt = new BufferedImage((int) pts[pts.length-1].getX(), height, BufferedImage.TYPE_INT_ARGB);
		
		TexturePaint dirtPaint = new TexturePaint(dirtTile, new Rectangle(dirtTile.getWidth(),dirtTile.getHeight()));
		
		Graphics2D g2d = dirt.createGraphics();
		
		g2d.setPaint(dirtPaint);
		
		int[] x = new int[pts.length+2];
		int[] y = new int[pts.length+2];
		
		x[0] = (int) Math.ceil(pts[pts.length-1].getX());
		y[0] = (int) Math.ceil(height);
		x[1] = (int) Math.ceil(0);
		y[1] = y[0];
		
		for(int i = 0; i < pts.length; i++){
			x[i+2] = (int) (pts[i].getX());
			y[i+2] = (int) (pts[i].getY());
		}
		
		Polygon p = new Polygon(x,y,x.length);
		
		g2d.fill(p);
		g2d.dispose();
	}
	
	public void generateGrass(float thickness){
		BufferedImage grassTile = new ImageLoader("/resources/", new String[]{"grass.png"}, true).getImage(0);
		
		grass = new BufferedImage((int) Math.ceil(pts[pts.length-1].getX()), (int) Math.ceil(ptHeight+thickness), BufferedImage.TYPE_INT_ARGB);
		
		TexturePaint grassPaint = new TexturePaint(grassTile, new Rectangle(grassTile.getWidth(),grassTile.getHeight()));
		
		Graphics2D g2d = grass.createGraphics();
		
		g2d.setPaint(grassPaint);
		
		int[] x = new int[pts.length*2];
		int[] y = new int[pts.length*2];
		
		for(int i = 0; i < pts.length; i++){
			x[i] = (int) (pts[i].getX());
			y[i] = (int) (pts[i].getY());
		}
		
		for(int i = 0; i < pts.length; i++){
			x[pts.length-1+i] = (int) (pts[pts.length-1-i].getX());
			y[pts.length-1+i] = (int) (pts[pts.length-1-i].getY())+(int) thickness;
		}
		
		Polygon p = new Polygon(x,y,x.length);
		
		g2d.fill(p);
		
		g2d.dispose();
	}
	
	public void addCharacter(Character c, boolean onGround, boolean focus){
		chars.add(c);
		if(focus){
			cameraFocus = c;
		}
		if(onGround){
			c.translate(new Vec2(0,this.getTerrainInfo(c.getPos().getX()).getY()));
		}
	}
	
	public void step(float dt){
		
		for(Character c: chars){
			c.step(dt);
		}
		
		for(Character c: chars){
			c.solveCollision(this);
		}
	}
	
	public void draw(Graphics2D g2d){
		
		g2d.setBackground(Color.BLUE);
		
		g2d.translate(-cameraFocus.getPos().getX(), -cameraFocus.getPos().getY());
		
		g2d.drawImage(dirt,0,0,null);
		g2d.drawImage(grass,0,0,null);
		//drawPts(g2d);
		
		for(Character c: chars){
			c.draw(g2d);
		}
		
		drawDetails(tallGrass,g2d);
		
	}
	
	public void drawPts(Graphics2D g2d){
		for(int rep = 0; rep < pts.length-1; rep++){
			g2d.setColor(Color.black);
			g2d.drawLine((int) pts[rep].getX(), (int) pts[rep].getY(), (int) pts[rep+1].getX(), (int) pts[rep+1].getY());
			int width = 2;
			g2d.fillRect((int)pts[rep].getX()-width/2,(int)pts[rep].getY()-width/2,width,width);
		}
	}
	
	public void drawDetails(List<Detail> details, Graphics2D g2d){
		for(Detail g: details){
			g.draw(g2d);
		}
	}
	
	public TerrainInfo getTerrainInfo(float x){
		float y = pts[0].getY();
		Vec2 parallel = new Vec2(1,0);
		
		for(int rep = 1; rep < pts.length; rep++){
			
			if(x < pts[rep].getX()){
				
				Vec2 pt1 = pts[rep-1];
				Vec2 pt2 = pts[rep];
				
				parallel = pt2.minus(pt1).normalized();
				y = (((pt2.getY()-pt1.getY())*(x-pt1.getX()))/(pt2.getX()-pt1.getX())) + pt1.getY();
				break;
			}
		}
		
		if(x > pts[pts.length-1].getX()) y = pts[pts.length-1].getY();
		if(x < pts[0].getX()){ 
			y = pts[0].getY();
			parallel = new Vec2(1,0);
		}
		
		return new TerrainInfo(y,parallel);
	}
}
