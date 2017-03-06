package com.gmail.youknowjoejoe.runner;

import java.awt.Graphics2D;

public interface Entity {
	public void step(float dt);
	public void draw(Graphics2D g2d);
}
