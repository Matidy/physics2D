package com.mygdx.bounce2;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Ball extends Position {

	
	private Vector2 pos;
	private float radius;
	private int weight;
	
	Rectangle rec;
	private float y_change;
	private float x_change;
	private float gravity = -9.8f;
	private float friction = 0.05f;
	
	public Ball(float posx, float posy, float radius, int weight) {
		pos = new Vector2(posx, posy);
		this.radius = radius;
		this.weight = weight;
	}
	
	public Vector2 getPos() {
		return pos;
	}
	
	public float getX() {
		return pos.x;
	}
	
	public float getY() {
		return pos.y;
	}
	
	public float getRadius() {
		return radius;
	}
	
	public int getWeight() {
		return weight; 
	}
	
	public void setX(float new_x) {
		pos.x = new_x;
	}
	
	public void setY(float new_y) {
		pos.y = new_y;
	}
	
	public void update () {
		//y_change += /*(1/60)*/(gravity);
		pos.y += y_change; 
		pos.x += x_change;
		
		if (x_change > 0) x_change -= friction*x_change;
		if (x_change < 0) x_change += friction*-x_change;
		if (y_change > 0) y_change -= friction*y_change;
		if (y_change < 0) y_change += friction*-y_change;
	}
	
	public void addForce (float x_force, float y_force) {
		x_change += x_force;
		y_change += y_force;
	}

}