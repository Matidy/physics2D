package com.mygdx.bounce2;

public class CollisionTriple {
	public Wall wall;
	public Ball ball;
	public int index;
	
	public CollisionTriple(Wall wall, Ball ball, int index) {
		this.wall = wall;
		this.ball = ball;
		this.index = index;
	}
}
