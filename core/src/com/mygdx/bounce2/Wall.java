package com.mygdx.bounce2;

import com.badlogic.gdx.math.Vector2;
import java.lang.Math;


public class Wall {
	
	public int quadrant; // <90 = quad1, <180 = quad2, <270 = quad3, <360 = quad4
	public float angle;
	
	public Vector2 ball_points[] = new Vector2[2];
	
	private Vector2 point1; 
	private Vector2 point2;
	private Vector2 vector;

	private float bearing = 0;

	public Wall (float pos_x1, float pos_y1, float pos_x2, float pos_y2) {
		point1 = new Vector2(pos_x1, pos_y1);
		point2 = new Vector2(pos_x2, pos_y2);
		
		int dx = (int)(pos_x1 - pos_x2);
		int dy = (int)(pos_y1 - pos_y2);
		vector = new Vector2(dx, dy);
		
		setQuadrant();
		
		if (dx != 0 && dy != 0) {
			angle = (float) (Math.atan(dy/dx));
			bearing += angle;
		}
	}
	
	private void setQuadrant() {
		float dx = vector.x;
		float dy = vector.y;
		
		if (dx != 0 || dy != 0) { // signs of x/y determine which quadrant bearing is in. i.e. +x -y -> 90-180
			if (dx == 0) {
				if (dy < 0) bearing = 180;
			}
			else if (dy == 0) {
				if (dx > 0) bearing = 90;
				else bearing = 270;
			}
			else if (dx > 0 && dy < 0)
				bearing = 90;
			else if (dx < 0 && dy < 0)
				bearing = 180;
			else if (dx < 0 && dy > 0)
				bearing = 270;
		}
		else /*handle invalid input of point1 == point 2*/;
		
		switch((int)(bearing))  { // sin/cos/tan only works for 90 degrees, so a full circle is four 90 degree quadrants
		case 0:
			quadrant = 1;
			break;
		case 90:
			quadrant = 2;
			break;
		case 180:
			quadrant = 3;
			break;
		case 270:
			quadrant = 4;
			break;
		}
	}
	
	public Vector2 getPoint1() {
		return point1;
	}
	
	public Vector2 getPoint2() {
		return point2;
	}
	
	public Vector2 getVector() {
		return vector;
	}
}
