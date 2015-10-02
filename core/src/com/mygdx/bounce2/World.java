package com.mygdx.bounce2;

import java.util.List;
import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;

public class World {
	final static float gravity = 2f*9.8f;
	final static float air_resistance = 1.5f;
	final static float move_speed = 40f;
	
	public ArrayList<Ball> model_balls;
	public List<Wall> walls;
	
	///////////////////////////DEBUG/////////////////////////////
	
	///////////////////////////DEBUG/////////////////////////////
	
	private ArrayList<CollisionQuin> collision_quins;
	
	public World() {
		model_balls = new ArrayList<Ball>(0);
		walls = new ArrayList<Wall>(0);
		
		//boxes
		List<Wall> box1 = createBox(new Vector2(100, 440), 100, 60);
		for (Wall edge : box1) {
			walls.add(edge);
		}
		List<Wall> box2 = createBox(new Vector2(40, 240), 60, 100);
		for (Wall edge : box2) {
			walls.add(edge);
		}
		List<Wall> box3 = createBox(new Vector2(200, 240), 60, 100);
		for (Wall edge : box3) {
			walls.add(edge);
		}
		//floor
		walls.add(new Wall(0, 65, 30, 50)); //PN
		walls.add(new Wall(30, 50, 270, 50)); //horizontal right
		walls.add(new Wall(270, 50, 300, 65)); //PP
		
		//ceiling
		walls.add(new Wall(300, 535, 270, 550)); //NP
		walls.add(new Wall(270, 550, 30, 550)); //horizontal left
		walls.add(new Wall(30, 550, 0, 535)); //NN
		
		//Side wall
		walls.add(new Wall(300, 65, 300, 535));
		walls.add(new Wall(0, 535, 0, 65));
		//sheer
		walls.add(new Wall(300, 65, 250, 600)); //broken
		
		//primary ball
		addBall(new Vector2(150, 300), 10.0f, 10);
	}
	
	private ArrayList<CollisionQuin> checkCollision() {
		ArrayList<CollisionQuin> collision_quins = new ArrayList<CollisionQuin>(0); //Container for the set of Wall, Ball and Index involved in a collision (Index lets wall know which ball hit it)
		Vector2 wall_vector;
		float wall_scalar;
		float x_wall_low_point;
		float x_wall_high_point;
		float y_wall_low_point;
		float y_wall_high_point;
		double theta;
		
		Vector2 ball_point;
		Vector2 intersection;
		Vector2 normal_intersection;
		Vector2 wall_to_ball = new Vector2(100, 100);
		Vector2 ball_change = new Vector2 (0, 0);
		Vector2 relative_ball_point = new Vector2();
		Vector2 prev_relative_ball_point = new Vector2();
		
		Ball current_ball;
		
		Vector2 temp_wall_to_ball = new Vector2(100, 100);
		
		float ball_change_scalar;
		float wall_intersection_scalar;
		float ball_intersection_scalar;
		Vector2 collision_point;
	
		for (int i=0; i<model_balls.size(); i++) {
			current_ball = model_balls.get(i);
			for (Wall wall: walls) {
				if (wall.getPoint1().x < wall.getPoint2().x) {
					x_wall_low_point = wall.getPoint1().x;
					x_wall_high_point = wall.getPoint2().x;
				}
				else {
					x_wall_low_point = wall.getPoint2().x;
					x_wall_high_point = wall.getPoint1().x;
				}
				if (wall.getPoint1().y < wall.getPoint2().y) {
					y_wall_low_point = wall.getPoint1().y;
					y_wall_high_point = wall.getPoint2().y;
				}
				else {
					y_wall_low_point = wall.getPoint2().y;
					y_wall_high_point = wall.getPoint1().y;
				}
				wall_vector = wall.getVector();
				wall_vector.nor();
					
				ball_point = wall.ball_points.get(i);
				relative_ball_point.x = ball_point.x+current_ball.getX();
				relative_ball_point.y = ball_point.y+current_ball.getY();
				prev_relative_ball_point.x = ball_point.x+current_ball.getPrevPos().x;
				prev_relative_ball_point.y = ball_point.y+current_ball.getPrevPos().y;
				ball_change.x = current_ball.getPos().x-current_ball.getPrevPos().x;
				ball_change.y = current_ball.getPos().y-current_ball.getPrevPos().y;
				ball_change.nor();
				
				////////
				// GET INTERSECTION FROM THE RELEVANT BALL POINT BETWEEN THE WALL AND IT'S ORTHOGONAL
				////
				intersection = findIntersection(ball_change, prev_relative_ball_point, wall.getVector(), wall.getPoint1());
		
				////////
				// GET WALL POINT1 TO WALL POINT2 SCALAR AND WALL POINT1 TO INTERSECTION POINT SCALAR
				////
				wall_scalar = getScalar(wall_vector, wall.getPoint1(), wall.getPoint2());
				wall_intersection_scalar = getScalar(wall_vector, wall.getPoint1(), intersection);
				
				////////
				// GET BALL TO INTERSECTION POINT SCALAR AND PREV BALL TO CURRENT BALL SCALAR
				////
				ball_intersection_scalar = getScalar(ball_change, prev_relative_ball_point, intersection);
				ball_change_scalar = getScalar(ball_change, prev_relative_ball_point, relative_ball_point);
				
				////////
				// COLLISION TRIGGER CHECK
				////
				if (ball_intersection_scalar >= 0 && ball_intersection_scalar <= ball_change_scalar) {
					if (wall_intersection_scalar >= 0 && wall_intersection_scalar <= wall_scalar) {
						wall_to_ball = wall.getOrthogonal();
						collision_point = intersection;
						collision_quins.add(new CollisionQuin(wall, current_ball, collision_point, wall_to_ball, i));
						break;
					}
					/*else {
						wall_to_ball.x = current_ball.getPrevPos().x-wall.getPoint1().x;
						wall_to_ball.y = current_ball.getPrevPos().y-wall.getPoint1().y;
						temp_wall_to_ball.x = wall.getPoint2().x-current_ball.getPrevPos().x;
						temp_wall_to_ball.y = wall.getPoint2().y-current_ball.getPrevPos().y;
						collision_point = wall.getPoint1();
						if (temp_wall_to_ball.len() < wall_to_ball.len()) {
							wall_to_ball = temp_wall_to_ball;
							collision_point = wall.getPoint2();
						}
					}*/
					
				}
			}
		}
		return collision_quins;
	}
	
	private void simulateCollision(ArrayList<CollisionQuin> collision_quins) {
		for(CollisionQuin collision_quin: collision_quins) {
			Wall wall = collision_quin.wall;
			Vector2 collision_point = collision_quin.collision_point;
			Vector2 wall_to_ball = collision_quin.wall_to_ball;
			wall_to_ball.nor();
			
			// Set ball.pos so the point the collided with the wall is where the ball's vector and the wall's vector intersect
			collision_quin.ball.setX(collision_point.x + wall_to_ball.x*(collision_quin.ball.getRadius()+0.2f));
			collision_quin.ball.setY(collision_point.y + wall_to_ball.y*(collision_quin.ball.getRadius()+0.2f));
		
			//Calculate new direction vector and force imparted into the wall.
			Vector2 scnd_term = new Vector2();
			Vector2 ball_direction = new Vector2();
			Vector2 new_ball_direction = new Vector2();
			// Equation: r=d-2(d.n)n // r-reflection vector
									 // n-collision surface normal
								 	 // d-incidence vector
			ball_direction.x = collision_quin.ball.getDirection().x*collision_quin.ball.getSpeed();
			ball_direction.y = collision_quin.ball.getDirection().y*collision_quin.ball.getSpeed();
			scnd_term.x = wall_to_ball.dot(ball_direction)*wall_to_ball.x*2;//2(d.n)n
			scnd_term.y = wall_to_ball.dot(ball_direction)*wall_to_ball.y*2;
			new_ball_direction.x = ball_direction.x - scnd_term.x;
			new_ball_direction.y = ball_direction.y - scnd_term.y;
		
			collision_quin.ball.setSpeed(new_ball_direction.len());
			collision_quin.ball.setDirection(new_ball_direction);
		}
	}
	
	// Accepts two 2D vectors and finds the point at which they intersect
	// Assumes the provided vectors are not parallel.
	//////////////
	public Vector2 findIntersection(Vector2 vec1, Vector2 c1, Vector2 vec2, Vector2 c2) {
		Vector2 intersection = new Vector2();
		float scalar;
		if(vec2.y==0) {
			scalar = (c1.x+vec1.x*(c2.y-c1.y)/vec1.y-c2.x)/     //Special case for y2==0
					  vec2.x;
			
			intersection.x = c2.x + vec2.x*scalar;
			intersection.y = c2.y + vec2.y*scalar;
		}
		else {
			scalar = (c2.x+vec2.x*((c1.y-c2.y)/vec2.y)-c1.x)/   //Scalar found via. setting vectors equal to each other
					  (vec1.x-vec1.y*vec2.x/vec2.y);			//to get 2 simultaneous equations, then using substitution
																//and rearranging to get an equation for scalar1.
			intersection.x = c1.x + vec1.x*scalar;
			intersection.y = c1.y + vec1.y*scalar;									 			 
		}
		
		return intersection;
	}
	
	public float getScalar(Vector2 vector, Vector2 start_point, Vector2 end_point) {
		float scalar;
		if (vector.x==0) {
			scalar = (end_point.y-start_point.y)/vector.y;
		}
		else {
			scalar = (end_point.x-start_point.x)/vector.x;
		}
		return scalar;
	}
	
	// Finds the point on the ball that each wall is closest to.
	///////////////
	private Vector2 getNearestBallPoint (Wall wall, Ball ball) {
		double x1, y1;/*, x2, y2;*/
		double bearing_orth = (wall.getTheta()-(0.5*Math.PI))%(2*Math.PI); // +90(1/2PIr) as radius of circle is the normal to the wall
		Vector2 point;
		
		// Parametric equations for points on a circle
		x1 = (Math.cos(bearing_orth) * ball.getRadius());
		y1 = (Math.sin(bearing_orth) * ball.getRadius()); // rounding error: cos(90)/sin(180) = ~0
		point = new Vector2((float)x1, (float)y1);
	
		return point;
	}
	
	public void addBall(Vector2 pos, float radius, int weight) {
		Ball added_ball = new Ball(pos.x, pos.y , radius, weight);
		model_balls.add(added_ball);
		for (Wall wall: walls) {
			wall.ball_points.add(getNearestBallPoint(wall, added_ball));
		}	
	}
	
	public List<Wall> createBox(Vector2 pos, int width, int height) {
		List<Wall> walls = new ArrayList<Wall>(0);
		walls.add(new Wall(pos.x, pos.y, pos.x+width, pos.y));
		walls.add(new Wall(pos.x+width, pos.y, pos.x+width, pos.y-height));
		walls.add(new Wall(pos.x+width, pos.y-height, pos.x, pos.y-height));
		walls.add(new Wall(pos.x, pos.y-height, pos.x, pos.y));
		return walls;
	}

	public void update(float dt) {
		System.out.println("x: "+model_balls.get(0).getX());
		System.out.println("y: "+model_balls.get(0).getY());
		
		//Update objects
		for (Ball ball: model_balls) {
			ball.update(dt);
		}
		
		//Check/simulate collisions
		collision_quins = checkCollision();
		if (collision_quins.size() != 0) {
			simulateCollision(collision_quins);
		}
	}
}