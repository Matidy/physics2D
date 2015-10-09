package com.mygdx.bounce2;

import com.badlogic.gdx.math.Vector2;

public class CollisionInfo {

	public Vector2 collision_point;
	public Vector2 wall_to_ball;
	float intersection_scalar;
	
	public CollisionInfo(Vector2 collision_point, Vector2 wall_to_ball, float intersection_scalar) {
		this.collision_point = collision_point;
		this.wall_to_ball = wall_to_ball;
		this.intersection_scalar = intersection_scalar;
	}
}
