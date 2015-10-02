package com.mygdx.bounce2;

import com.badlogic.gdx.math.Vector2;

public class Ball extends Position {

	private Vector2 pos;
	private Vector2 prev_pos;
	private Vector2 direction;
	private double theta;
	private float speed;
	private float radius;
	private int weight;
	
	public Ball(float posx, float posy, float radius, int weight) {
		pos = new Vector2(posx, posy);
		prev_pos = new Vector2(pos);
		this.radius = radius;
		this.weight = weight;
		direction = new Vector2(0, 0);
		speed = 0;
	}
	
	public Vector2 getPos() 	  { return pos; }
	public Vector2 getPrevPos()   { return prev_pos; }
	public float   getX() 		  { return pos.x; }
	public float   getY() 		  { return pos.y; }
	public Vector2 getDirection() { return direction; }
	public float   getSpeed() 	  { return speed; }
	public float   getRadius() 	  { return radius; }
	public int	   getWeight()	  { return weight; }
	
	public double getTheta() {
		return theta;
	}
	
	public void setX(float new_x) {
		pos.x = new_x;
	}
	
	public void setY(float new_y) {
		pos.y = new_y;
	}
	
	public void setDirection(Vector2 new_dir) {
		direction = new_dir;
		direction.nor();
	}
	
	public void setSpeed(float new_speed) {
		if (new_speed < 0.25) {
			speed = 0; //Hard stop.
		}
		else {
			speed = new_speed;
		}
	}
	
	public void update (float dt) {
		//Gravity
		applyForce(World.gravity*dt, new Vector2(0, -1));
		
		//Air resistance
		applyForce(World.air_resistance*speed*dt, new Vector2(-direction.x,  -direction.y));
		
		//Update position
		prev_pos = new Vector2(pos);
		pos.x = pos.x + speed*direction.x;
		pos.y = pos.y + speed*direction.y;
		setTheta();
	}
	
	public void applyForce (float force, Vector2 direction) {
		//Convert to unit vector.
		direction.nor();
		
		this.direction.x = this.direction.x*speed + direction.x*force;
		this.direction.y = this.direction.y*speed + direction.y*force;
		setSpeed(this.direction.len());
		this.direction.nor();
	}
	
	// Find the point on the ball that's x coordinate is inline with another given x coordinate.
	///////////////
	// If y<0 then the point is invalid (returns NaN)
	public float getCorrespondingY(float x) {
		float y;
		y = (float)(Math.sqrt(getRadius()*getRadius() - (x-getX())*(x-getX())) - getY());
		return y;	
	}
	
	private void setTheta() {
		theta = Math.atan2(pos.y, pos.x);
	}
	
}