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
		
		//boxes
		List<Wall> box1 = createBox(new Vector2(100, 440), 100, 60);
		for (Wall edge : box1) {
			walls.add(edge);
		}
		List<Wall> box2 = createBox(new Vector2(20, 240), 60, 100);
		for (Wall edge : box2) {
			walls.add(edge);
		}
		List<Wall> box3 = createBox(new Vector2(220, 240), 60, 100);
		for (Wall edge : box3) {
			walls.add(edge);
		}
		//floor
		walls.add(new Wall(0, 65, 30, 50)); //PN
		walls.add(new Wall(30, 50, 270, 50)); //horizontal right
		walls.add(new Wall(270, 50, 300, 65)); //PP
		for (Wall wall: walls) {
			wall.ball_point = getRelevantBallPoint(wall, model_ball);
		}	
	}
	
	public void checkCollision() {
		//System.out.println("'checkCollision' reached");
		Vector2 ball_point;
		Vector2 wall_vector;
		Vector2 point1;
		Vector2 point2;
		double theta;
		
		float scalar1;
		double orth_angle;
		float wall_ub; // upper bound (point on the wall vector)
		float wall_lb; // lower bound (point on the wall's orthogonal vector)
		final float tolerance_distance = 39; // the distance along the orthogonal vector, determining the lower bound
		
		float ball_x;
		float ball_y;

		for (Wall wall: walls) {
			point1 = wall.getPoint1();
			point2 = wall.getPoint2();
			wall_vector = wall.getVector();
			theta = wall.getTheta();
			ball_point = wall.ball_point;
			ball_x = ball_point.x+model_ball.getX();
			ball_y = ball_point.y+model_ball.getY();
			
			if (ball_y >= point1.y && ball_y <= point2.y
		     || ball_y <= point1.y && ball_y >= point2.y) {
				if (wall_vector.x == 0) { //Special case: vertical walls - general method divides by wall_vector.x
					orth_angle = (theta +(1.5*Math.PI))%(2*Math.PI); //+270%360 in degrees
					wall_lb = point1.x + (tolerance_distance*(float)Math.cos(orth_angle));
					if (ball_x <= point1.x && ball_x > wall_lb
					 || ball_x >= point1.x && ball_x < wall_lb)
						simulateCollision();
				}
			}
			if (ball_x >= point1.x && ball_x <= point2.x
			 || ball_x <= point1.x && ball_x >= point2.x) {
			
				scalar1 = Math.abs(((ball_x) - point1.x)/wall_vector.x);
			
				wall_ub = point1.y + scalar1*wall_vector.y;
				orth_angle = (theta +(1.5*Math.PI))%(2*Math.PI); //+270%360 in degrees
				wall_lb = wall_ub + (tolerance_distance*(float)Math.sin(orth_angle));
			
				/*System.out.println("Ball point_y: " + ball_y);
				System.out.println("Upper y bound: " + wall_ub);
				System.out.println("Lower y bound: " + wall_lb + "\n");*/
				if (ball_y <= wall_ub && ball_y > wall_lb
				 || ball_y >= wall_ub && ball_y < wall_lb)
					simulateCollision(); //should replace with return walls that the ball has collided with and then run simulateCollision() from update.
			
				
			}
		}
	}
	
	private void simulateCollision() {
		System.out.println("'simulate' reached");
		//model_ball.getX(); 
		model_ball.setX(150);
		model_ball.setY(300);
	}
	
	// Finds the point on the ball that each wall is closest to.
	public Vector2 getRelevantBallPoint (Wall wall, Ball ball) {
		double x1, y1;/*, x2, y2;*/
		double bearing_orth = (wall.getTheta()-(0.5*Math.PI))%(2*Math.PI); // +90(1/2PIr) as radius of circle is the normal to the wall
		Vector2 point;
		
		// Parametric equations for points on a circle
		x1 = (Math.cos(bearing_orth) * ball.getRadius());
		y1 = (Math.sin(bearing_orth) * ball.getRadius()); // rounding error: cos(90)/sin(180) = ~0
		point = new Vector2((float)x1, (float)y1);
	
		return point;
	}
	
	public List<Wall> createBox(Vector2 pos, int width, int height) {
		List<Wall> walls = new ArrayList<Wall>(0);
		walls.add(new Wall(pos.x, pos.y, pos.x+width, pos.y));
		walls.add(new Wall(pos.x+width, pos.y, pos.x+width, pos.y-height));
		walls.add(new Wall(pos.x+width, pos.y-height, pos.x, pos.y-height));
		walls.add(new Wall(pos.x, pos.y-height, pos.x, pos.y));
		return walls;
	}

	public void update() {
		checkCollision();
		model_ball.update();
	}
}


