package com.mygdx.bounce2;

import java.util.List;
import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;

public class World {
	float gravity;
	
	public Ball model_ball;
	public List<Wall> walls;
	
	public World() {
		gravity = -9.8f;
		model_ball = new Ball(150, 300, 10, 0);
		walls = new ArrayList<Wall>(0);
		walls.add(new Wall(0, 65, 30, 50));
		walls.add(new Wall(30, 50, 270, 50));
		walls.add(new Wall(270, 50, 300, 65));
		for (Wall wall: walls) {
			wall.ball_points = getRelevantBallPoints(wall, model_ball);
		}	
	}
	
	public void checkCollision() {
		//System.out.println("'checkCollision' reached");
		
		for (Wall wall: walls) {
			Vector2[] ball_points = wall.ball_points;
			Vector2 point1 = wall.getPoint1();
			Vector2 vector = wall.getVector();
			
			float scalar1 = (ball_points[0].x - point1.x)/vector.x;
			float scalar2 = (ball_points[1].x - point1.x)/vector.x;
		
		
			if 	(  ball_points[0].y == point1.y + scalar1*vector.y
				|| ball_points[1].y == point1.y + scalar2*vector.y)
					simulateCollision();
		}
	}
	
	private void simulateCollision() {
		System.out.println("'simulate' reached");
		model_ball.getX(); 
	
	}
	
	public Vector2[] getRelevantBallPoints (Wall wall, Ball ball) {
		float x;
		float y;
		float bearing = wall.getBearing();
		

		if (wall.quadrant == Wall.Quadrant.PN || wall.quadrant == Wall.Quadrant.NP) { // quadrants are +/- 90 as the ball point is found from the normal of the wall
			x = (float) (Math.sin(bearing) * ball.getRadius());
			y = (float) (Math.cos(bearing) * ball.getRadius()); 
		} 
		else {
			x = (float) (Math.cos(bearing) * ball.getRadius());
			y = (float) (Math.sin(bearing) * ball.getRadius());
		}
		
		Vector2 points[] = new Vector2[2]; // based on quadrants
		if (x >= 0) { // again, +/- 90 due to working out the normal's vector
			points[0] = new Vector2(x*-1, y);
			points[1] = new Vector2(x, y*-1);
		}
		else {
			points[0] = new Vector2(x, y);
			points[1] = new Vector2(x*-1, y*-1);
		}
		return points;
	}

	public void update() {
		checkCollision();
		model_ball.update();
	}
}


