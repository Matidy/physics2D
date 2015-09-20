package com.mygdx.bounce2;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Ball extends Position {

	
	private Vector2 pos;
	private Vector2 direction;
	private float speed;
	private float radius;
	private int weight;
	
	Rectangle rec;
	
	public Ball(float posx, float posy, float radius, int weight) {
		pos = new Vector2(posx, posy);
		this.radius = radius;
		this.weight = weight;
		direction = new Vector2(0, 0);
		speed = 0;
	}
	
	public Vector2 getPos() 	  { return pos; }
	public float   getX() 		  { return pos.x; }
	public float   getY() 		  { return pos.y; }
	public Vector2 getDirection() { return direction; }
	public float   getSpeed() 	  { return speed; }
	public float   getRadius() 	  { return radius; }
	public int	   getWeight()	  { return weight; }
	
	public void setX(float new_x) {
		pos.x = new_x;
	}
	
	public void setY(float new_y) {
		pos.y = new_y;
	}
	
	public void update (float dt) {
		//Gravity
		//applyForce(World.gravity*dt, new Vector2(0, -1));
		
		//Air resistance
		if (speed < 0.1) {
			speed = 0; //Hard stop.
		}
		else {
			applyForce(World.air_resistance*speed*dt, new Vector2(-direction.x,  -direction.y));
		}
		//Update position
		pos.x = pos.x + speed*direction.x;
		pos.y = pos.y + speed*direction.y;
	}
	
	public void applyForce (float force, Vector2 direction) {
		//Convert to unit vector.
		direction.nor();
		
		this.direction.x = this.direction.x*speed + direction.x*force;
		this.direction.y = this.direction.y*speed + direction.y*force;
		speed = this.direction.len();
		this.direction.nor();
	}

}