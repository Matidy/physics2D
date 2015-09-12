package com.mygdx.bounce2;

import java.util.List;
import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;

public class World {
	final double gravity;
	
	public Ball model_ball;
	public List<Wall> walls;
	
	public World() {
		gravity = -9.8f;
		model_ball = new Ball(150, 300, 10, 0);
		walls = new ArrayList<Wall>(0);
		walls.add(new Wall(100, 500, 100, 100)); //vertical left
		walls.add(new Wall(200, 100, 200, 500)); //vertical right
		walls.add(new Wall(0, 65, 30, 50));
		walls.add(new Wall(30, 50, 270, 50));
		walls.add(new Wall(270, 50, 300, 65));
		for (Wall wall: walls) {
			wall.ball_point = getRelevantBallPoint(wall, model_ball);
		}	
	}
	
	public void checkCollision() {
		//System.out.println("'checkCollision' reached");
		Vector2 ball_point;
		Vector2 wall_vector;
		Vector2 point1;
		double orth_angle;
		float wall_ub; // upper bound (point on the wall vector)
		float wall_lb; // lower bound (point on the wall's orthogonal vector)
		final float tolerance_distance = 40; // the distance along the orthogonal vector, determining the lower bound
		
		List<Float> ball_y = new ArrayList<Float>(0);
		float ball_x;
		int i=0;
		float sign;

		for (Wall wall: walls) {
			ball_point = wall.ball_point;
			point1 = wall.getPoint1();
			wall_vector = wall.getVector();
			
			if (wall_vector.x == 0) { //Special case: vertical walls - general method divides by wall_vector.x
				ball_y.add(0f); //delete when done debugging
				float scalar1 = ((ball_point.y+model_ball.getY()) - point1.y)/wall_vector.y;
			
				wall_ub = point1.x + scalar1*wall_vector.x;
				orth_angle = (wall.getBearing()+(0.5*Math.PI))%2*Math.PI; //+90%360 in degrees
				sign = 1f; //preserves negative numbers returned by Math.sin
				if(Math.sin(orth_angle) >= 0f) 
					sign = -1f;
				wall_lb = wall_ub + sign*(tolerance_distance*(float)Math.sin(orth_angle));
			
				ball_x = ball_point.x+model_ball.getX();
				if (ball_x <= wall_ub && ball_x > wall_lb)
					simulateCollision(); //should replace with return walls that the ball has collided with and then run simulateCollision() from update.
			}
			else {
				float scalar1 = ((ball_point.x+model_ball.getX()) - point1.x)/wall_vector.x;
				/*float scalar2 = ((ball_points[1].x+model_ball.getX()) - point1.x)/wall_vector.x;*/
			
				wall_ub = point1.y + scalar1*wall_vector.y;
				orth_angle = (wall.getBearing()+(0.5*Math.PI))%2*Math.PI; //+90%360 in degrees
				sign = 1f; //preserves negative numbers returned by Math.sin
				if(Math.sin(orth_angle) >= 0f) 
					sign = -1f;
				wall_lb = wall_ub + sign*(tolerance_distance*(float)Math.sin(orth_angle));
			
				ball_y.add(ball_point.y+model_ball.getY());
				System.out.println("Ball point_y: " + ball_y.get(i));
				System.out.println("Upper y bound: " + wall_ub);
				System.out.println("Lower y bound: " + wall_lb + "\n");
				if 	(   ball_y.get(i) <= wall_ub && ball_y.get(i) > wall_lb)
					/*|| (ball_points[1].y+model_ball.getY()) == point1.y + scalar2*wall_vector.y)*/
						simulateCollision(); //should replace with return walls that the ball has collided with and then run simulateCollision() from update.
			}
			i++;
		}
	}
	
	private void simulateCollision() {
		System.out.println("'simulate' reached");
		//model_ball.getX(); 
		model_ball.setX(150);
		model_ball.setY(300);
	}
	
	public Vector2 getRelevantBallPoint (Wall wall, Ball ball) {
		double x1, y1;/*, x2, y2;*/
		double bearing_orth = (wall.getBearing()+(0.5*Math.PI))%(2*Math.PI); // +90(1/2PIr) as radius of circle is the normal to the wall
		Vector2 point;
		
		// Parametric equations for points on a circle
		x1 = (Math.sin(bearing_orth) * ball.getRadius());
		y1 = (Math.cos(bearing_orth) * ball.getRadius()); // rounding error: cos(90)/sin(180) = ~0
		point = new Vector2((float)x1, (float)y1);
		
		/*bearing_orth = (bearing_orth+Math.PI)%(2*Math.PI); // Radius is part of a vector defining the normal of the wall
												 			 // going through the centre of the circle.
		x2 = (Math.cos(bearing) * ball.getRadius());
		y2 = (Math.sin(bearing) * ball.getRadius());
		point[1] = new Vector2((float)x2, (float)y2);*/
		return point;
	}

	public void update() {
		checkCollision();
		model_ball.update();
	}
}


