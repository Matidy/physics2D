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
		walls.add(new Wall(300, 65, 250, 600));
		
		//primary ball
		addBall(new Vector2(150, 300), 10.0f, 10);
	}
	
	private void checkCollision() {
		Vector2 wall_vector;
		float wall_scalar;
		
		Vector2 ball_point;
		Vector2 intersection;
		Vector2 wall_to_ball = new Vector2(100, 100);
		Vector2 ball_change = new Vector2 (0, 0);
		Vector2 relative_ball_point = new Vector2();
		Vector2 prev_relative_ball_point = new Vector2();
		
		Ball current_ball;
		
		float ball_change_scalar;
		float wall_intersection_scalar;
		float ball_intersection_scalar;
		Vector2 collision_point;
		CollisionInfo current_collision;
	
		for (int i=0; i<model_balls.size(); i++) {
			current_ball = model_balls.get(i);
			for (Wall wall: walls) {
				wall_vector = wall.getVector();
				wall_vector.nor();
					
				ball_change.x = current_ball.getPos().x-current_ball.getPrevPos().x;
				ball_change.y = current_ball.getPos().y-current_ball.getPrevPos().y;
				ball_change.nor();
				
				ball_point = wall.ball_points.get(i);
				relative_ball_point.x = ball_point.x+current_ball.getX();
				relative_ball_point.y = ball_point.y+current_ball.getY();
				prev_relative_ball_point.x = ball_point.x+current_ball.getPrevPos().x;
				prev_relative_ball_point.y = ball_point.y+current_ball.getPrevPos().y;
				
				// GET INTERSECTION FROM THE RELEVANT BALL POINT BETWEEN THE WALL AND IT'S ORTHOGONAL
				intersection = findIntersection(ball_change, prev_relative_ball_point, wall.getVector(), wall.getPoint1());
		
				// GET WALL POINT1 TO WALL POINT2 SCALAR AND WALL POINT1 TO INTERSECTION POINT SCALAR
				wall_scalar = getScalar(wall_vector, wall.getPoint1(), wall.getPoint2());
				wall_intersection_scalar = getScalar(wall_vector, wall.getPoint1(), intersection);
				
				// COLLISION TRIGGER CHECK
				if (wall_intersection_scalar >= 0 && wall_intersection_scalar <= wall_scalar) {
					// GET BALL TO INTERSECTION POINT SCALAR AND PREV BALL TO CURRENT BALL SCALAR
					ball_intersection_scalar = getScalar(ball_change, prev_relative_ball_point, intersection);
					ball_change_scalar = getScalar(ball_change, prev_relative_ball_point, relative_ball_point);
					
					if (ball_intersection_scalar >= 0 && ball_intersection_scalar <= ball_change_scalar) {
						wall_to_ball = wall.getOrthogonal();
						collision_point = intersection;
						current_collision = new CollisionInfo(collision_point, wall_to_ball, ball_intersection_scalar);
						current_ball.maintainCollisions(current_collision);
					}
				}
				else { // Ball's direction vector means it will not collide with the line at its default point.
					Vector2 new_ball_point;
					Vector2 new_prev_rela;
					Vector2 new_rela;
					Vector2 closest_wall_point;
					
					Vector2 point1_dist = new Vector2(wall.getPoint1().x - current_ball.getPos().x, wall.getPoint1().y - current_ball.getPos().y);
					Vector2 point2_dist = new Vector2(wall.getPoint2().x - current_ball.getPos().x, wall.getPoint2().y - current_ball.getPos().y);
					if (point1_dist.len() <= point2_dist.len()) {
						closest_wall_point = wall.getPoint1();
					}
					else {
						closest_wall_point = wall.getPoint2();
					}
					
					new_prev_rela = findIntersection(ball_change, closest_wall_point, current_ball);
					if (new_prev_rela.x == new_prev_rela.x) { // Check findIntersection returned vector is not (NaN, NaN).
						new_ball_point = new Vector2 (new_prev_rela.x-current_ball.getPrevPos().x, new_prev_rela.y-current_ball.getPrevPos().y);
						new_rela = new Vector2 (current_ball.getPos().x+new_ball_point.x,
												current_ball.getPos().y+new_ball_point.y);
						
						ball_intersection_scalar = getScalar(ball_change, new_prev_rela, closest_wall_point);
						ball_change_scalar = getScalar(ball_change, new_prev_rela, new_rela);
					
						if (ball_intersection_scalar >= 0 && ball_intersection_scalar <= ball_change_scalar) {
							wall_to_ball = new Vector2(current_ball.getPos().x-new_rela.x, current_ball.getPos().y-new_rela.y);
							collision_point = closest_wall_point;
							current_collision = new CollisionInfo(collision_point, wall_to_ball, ball_intersection_scalar);
							current_ball.maintainCollisions(current_collision);
						}
					}
				}
			}
		}
	}
	
	//**// TO-DO: Update to handle colliding with multiple walls at once.
	private void simulateCollision() {
		for(Ball model_ball : model_balls) {
			if (!model_ball.collisions.isEmpty()) {
				Vector2 collision_point = model_ball.collisions.get(0).collision_point;
				Vector2 wall_to_ball = new Vector2(0, 0);
				for(CollisionInfo collision : model_ball.collisions) {
					collision.wall_to_ball.nor();
					wall_to_ball.x += collision.wall_to_ball.x;
					wall_to_ball.y += collision.wall_to_ball.y;
				}
				wall_to_ball.nor();
			
				// Set ball.pos so the point the collided with the wall is where the ball's vector and the wall's vector intersect
				model_ball.setX(collision_point.x + wall_to_ball.x*(model_ball.getRadius()+0.2f));// 0.2f sets the ball just above the line to avoid triggering an immediate collision.
				model_ball.setY(collision_point.y + wall_to_ball.y*(model_ball.getRadius()+0.2f));
		
				//Calculate new direction vector and force imparted into the wall.
				Vector2 scnd_term = new Vector2();
				Vector2 ball_direction = new Vector2();
				Vector2 new_ball_direction = new Vector2();
				// Equation: r=d-2(d.n)n // r-reflection vector
										 // n-collision surface normal
										 // d-incidence vector
				ball_direction.x = model_ball.getDirection().x*model_ball.getSpeed();
				ball_direction.y = model_ball.getDirection().y*model_ball.getSpeed();
				scnd_term.x = wall_to_ball.dot(ball_direction)*wall_to_ball.x*2;//2(d.n)n
				scnd_term.y = wall_to_ball.dot(ball_direction)*wall_to_ball.y*2;
				new_ball_direction.x = ball_direction.x - scnd_term.x;
				new_ball_direction.y = ball_direction.y - scnd_term.y;
		
				model_ball.setSpeed(new_ball_direction.len());
				model_ball.setDirection(new_ball_direction);
				model_ball.collisions.clear();
			}
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
	
	public Vector2 findIntersection (Vector2 vec1, Vector2 c1, Ball ball) {
		Vector2 intersection = new Vector2();
		float scalar_pos;
		float scalar_neg;
		
		Vector2 G = new Vector2(c1.x-ball.getPrevPos().x, c1.y-ball.getPrevPos().y);
		float a = vec1.dot(vec1);
		float b = 2*(vec1.dot(G));
		float c = G.dot(G)-ball.getRadius()*ball.getRadius();
		
		float discriminant = b*b-4*a*c;
		scalar_pos = (-b + (float)Math.sqrt(discriminant))/(2*a);
		scalar_neg = (-b - (float)Math.sqrt(discriminant))/(2*a);
		
		if (scalar_pos > scalar_neg) {
			intersection.x = c1.x + vec1.x*scalar_pos;
			intersection.y = c1.y + vec1.y*scalar_pos;
		}
		else {
			intersection.x = c1.x + vec1.x*scalar_neg;
			intersection.y = c1.y + vec1.y*scalar_neg;
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
		//System.out.println("x: "+model_balls.get(0).getX());
		//System.out.println("y: "+model_balls.get(0).getY());
		
		//Update objects
		for (Ball ball: model_balls) {
			ball.update(dt);
		}
		
		//Check/simulate collisions
		checkCollision();
		simulateCollision();
		
		/*Vector2 test = findIntersection(new Vector2(2, 5),
						new Vector2(20, 2),
						new Ball(2, 4, 5, 0));
		System.out.println("test intersection: "+test.x +", "+test.y+"\n");*/
	}
}