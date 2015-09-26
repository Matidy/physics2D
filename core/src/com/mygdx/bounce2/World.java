package com.mygdx.bounce2;

import java.util.List;
import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;

public class World {
	final static float gravity = 2*9.8f;
	final static float air_resistance = 1.5f;
	final static float move_speed = 40f;
	
	public ArrayList<Ball> model_balls;
	public List<Wall> walls;
	
	private ArrayList<CollisionTriple> collision_triples;
	
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
		/*//sheer
		walls.add(new Wall(300, 65, 250, 600)); //broken*/
		
		//primary ball
		addBall(new Vector2(150, 300), 10.0f, 10);
	}
	
	private ArrayList<CollisionTriple> checkCollision() {
		ArrayList<CollisionTriple> collision_triples = new ArrayList<CollisionTriple>(0);
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
		Ball current_ball;

		for (Wall wall: walls) {
			point1 = wall.getPoint1();
			point2 = wall.getPoint2();
			wall_vector = wall.getVector();
			theta = wall.getTheta();
			
			for (int i=0; i<model_balls.size(); i++) {
				current_ball = model_balls.get(i);
				ball_point = wall.ball_points.get(i);
				ball_x = ball_point.x+current_ball.getX();
				ball_y = ball_point.y+current_ball.getY();
			
				if (ball_y >= point1.y && ball_y <= point2.y
				 || ball_y <= point1.y && ball_y >= point2.y) {
					if (wall_vector.x == 0) { //Special case: vertical walls - general method divides by wall_vector.x
						orth_angle = (theta +(1.5*Math.PI))%(2*Math.PI); //+270%360 in degrees
						wall_lb = point1.x + (tolerance_distance*(float)Math.cos(orth_angle));
						if (ball_x <= point1.x && ball_x > wall_lb
						 || ball_x >= point1.x && ball_x < wall_lb)
							collision_triples.add(new CollisionTriple(wall, current_ball, i));
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
						collision_triples.add(new CollisionTriple(wall, current_ball, i));
				}
			}
		}
		return collision_triples;
	}
	
	private void simulateCollision(ArrayList<CollisionTriple> collision_triples) {
		for(CollisionTriple collision_triple: collision_triples) {
			// Set ball.pos so the point the collided with the wall is where the ball's vector and the wall's vector intersect
			Vector2 collision_point;
			Vector2 relevant_ball_point = new Vector2(collision_triple.ball.getX()+collision_triple.wall.ball_points.get(collision_triple.index).x,
													  collision_triple.ball.getY()+collision_triple.wall.ball_points.get(collision_triple.index).y);
			collision_point = findIntersection(collision_triple.ball.getDirection(), relevant_ball_point, collision_triple.wall.getVector(), collision_triple.wall.getPoint1());
			collision_triple.ball.setX(collision_point.x + collision_triple.wall.getOrthogonal().nor().x*(collision_triple.ball.getRadius()+0.2f));
			collision_triple.ball.setY(collision_point.y + collision_triple.wall.getOrthogonal().nor().y*(collision_triple.ball.getRadius()+0.2f));
		
			//Calculate new direction vector and force imparted into the wall.
			Vector2 wall_normal = collision_triple.wall.getOrthogonal().nor();
			Vector2 scnd_term = new Vector2();
			Vector2 ball_direction = new Vector2();
			Vector2 new_ball_direction = new Vector2();
			// Equation: r=d-2(d.n)n // r-reflection vector
									 // n-collision surface normal
								 	 // d-incidence vector
			ball_direction.x = collision_triple.ball.getDirection().x*collision_triple.ball.getSpeed();
			ball_direction.y = collision_triple.ball.getDirection().y*collision_triple.ball.getSpeed();
			scnd_term.x = wall_normal.dot(ball_direction)*wall_normal.x*2;//2(d.n)n
			scnd_term.y = wall_normal.dot(ball_direction)*wall_normal.y*2;
			new_ball_direction.x = ball_direction.x - scnd_term.x;
			new_ball_direction.y = ball_direction.y - scnd_term.y;
		
			collision_triple.ball.setSpeed(new_ball_direction.len());
			collision_triple.ball.setDirection(new_ball_direction);
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
	
	// Finds the point on the ball that each wall is closest to.
	///////////////
	private Vector2 getRelevantBallPoint (Wall wall, Ball ball) {
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
			wall.ball_points.add(getRelevantBallPoint(wall, added_ball));
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
		collision_triples = checkCollision();
		if (collision_triples.size() != 0) {
			simulateCollision(collision_triples);
		}
	}
}