package com.mygdx.bounce2;

import com.badlogic.gdx.math.Vector2;

public class CollisionQuin {
	public Wall wall;
	public Ball ball;
	public int index;
	public Vector2 collision_point;
	public Vector2 wall_to_ball;
	
	public CollisionQuin(Wall wall, Ball ball, Vector2 collision_point, Vector2 wall_to_ball, int index) {
		this.wall = wall;
		this.ball = ball;
		this.collision_point = collision_point;
		this.wall_to_ball = wall_to_ball;
		this.index = index;
	}
}
