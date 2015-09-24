package com.mygdx.bounce2;

import com.badlogic.gdx.math.Vector2;
import java.lang.Math;


public class Wall {
	
	// 360 degrees divided horizontally and vertically forms 4 quadrants.
	// A direction vector is comprised of an x and y value giving a direction in 2D.
	// The signs of these x and y values (+/-) determine which quadrant the vector is in.
	// For example, +x, +y describes a vector in the upper-right quadrant, +x, -y in the
	// bottom-right quadrant. P = +, N = -.
	public enum Quadrant {
		PP,
		PN,
		NN,
		NP;
	}
	
	public Vector2 ball_point;
	
	private Quadrant quadrant; // <90 = quad1, <180 = quad2, <270 = quad3, <360 = quad4
	private Vector2 point1; 
	private Vector2 point2;
	private Vector2 vector;
	private Vector2 vector_orth;

	private double theta; //taken anti-clockwise from +x axis.

	public Wall (float pos_x1, float pos_y1, float pos_x2, float pos_y2) {
		point1 = new Vector2(pos_x1, pos_y1);
		point2 = new Vector2(pos_x2, pos_y2);
		
		int dx = (int)(pos_x2 - pos_x1);
		int dy = (int)(pos_y2 - pos_y1);
		vector = new Vector2(dx, dy);
		vector_orth = new Vector2(-vector.y, vector.x);// orth:90 anti-clockwise
		
		if (dx != 0 || dy != 0) {
			setQuadrant();
			theta = Math.atan2(dy, dx); // get polar coordinates theta (radians).
		}
		else /*Handle error case vector = (0, 0)*/;
	}
	
	public Vector2  getPoint1() 	{ return point1; }
	public Vector2  getPoint2() 	{ return point2; }
	public Vector2  getVector() 	{ return vector; }
	public Vector2  getOrthogonal() { return vector_orth; }
	public double   getTheta() 		{ return theta; }
	public Quadrant getQuadrant() 	{ return quadrant; }
	
	private void setQuadrant() {
		float dx = vector.x;
		float dy = vector.y;
		
		//quadrant range 0-89
		if 		(dx >= 0 && dy > 0) //++
			quadrant = Quadrant.PP;
		else if (dx > 0 && dy <= 0) //+-
			quadrant = Quadrant.PN;
		else if (dx <= 0 && dy < 0) //--
			quadrant = Quadrant.NN;
		else if (dx < 0 && dy >= 0) //-+
			quadrant = Quadrant.NP;
	}
}
