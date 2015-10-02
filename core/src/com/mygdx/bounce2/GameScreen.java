package com.mygdx.bounce2;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;

public class GameScreen extends ScreenAdapter {
	OrthographicCamera td_cam;
	Main game;
	World world;
	Texture image_ball;
	ShapeRenderer shapeRenderer;
	boolean pressed = false;
	boolean debug_mode = false;
	
	public GameScreen(Main game) {
		this.game = game;
		world = new World();
		
		td_cam = new OrthographicCamera();
		td_cam.setToOrtho(false, 300, 600);
		shapeRenderer = new ShapeRenderer();
	}
	
	public void render(float dt) {
		update(dt);
		draw();
	}
	
	private void update(float dt) {
		//Debug Keys
		if (!pressed) {
			if (Gdx.input.isKeyPressed(Keys.T)){
				if (debug_mode) disableDebug();
				else debug_mode = true;
				pressed = true;
			}
		}
		else if (!Gdx.input.isKeyPressed(Keys.T)) {
			pressed = false;
		}
		if (Gdx.input.isKeyPressed(Keys.SPACE)) {
			world.model_balls.get(0).setX(150);
			world.model_balls.get(0).setY(300);
		}
		
		// Arrow key controls
		if (Gdx.input.isKeyPressed(Keys.RIGHT))
			world.model_balls.get(0).applyForce(World.move_speed*dt, new Vector2(1, 0));	
		if (Gdx.input.isKeyPressed(Keys.LEFT))
			world.model_balls.get(0).applyForce(World.move_speed*dt, new Vector2(-1, 0));
		if (Gdx.input.isKeyPressed(Keys.UP))
			world.model_balls.get(0).applyForce(World.move_speed*dt, new Vector2(0,  1));
		if (Gdx.input.isKeyPressed(Keys.DOWN))
			world.model_balls.get(0).applyForce(World.move_speed*dt, new Vector2(0, -1));
		
		// Mouse input
		if (Gdx.input.isButtonPressed(Buttons.LEFT))
			world.addBall(new Vector2(Gdx.input.getX(), 600-Gdx.input.getY()), 10f, 10);
		
		world.update(dt);
	}
	
	private void draw() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(0f, 1f, 0.4f, 1f);
		for (int i=0; i<world.model_balls.size(); i++) {
			if (i==1) shapeRenderer.setColor(0f, 0.4f, 1f, 1f);
			shapeRenderer.circle(world.model_balls.get(i).getX(), 
								 world.model_balls.get(i).getY(), 
								 world.model_balls.get(i).getRadius());
		}
		shapeRenderer.end();
		
		List<Wall> walls = world.walls;
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(0f, 0f, 0f, 1f);
		for (Wall wall : walls ) {
			shapeRenderer.line(wall.getPoint1(), wall.getPoint2());
		}
		
		/*if (debug_mode) {
			// Intersection point x
			shapeRenderer.setColor(0.9f, 0.2f, 0f, 1f);
			shapeRenderer.line(world.intersection.x-4, world.intersection.y-4, world.intersection.x+4, world.intersection.y+4);
			shapeRenderer.line(world.intersection.x-4, world.intersection.y+4, world.intersection.x+4, world.intersection.y-4);
			shapeRenderer.line(new Vector2 (150-4, 300-4), new Vector2(150+4, 300+4));
			shapeRenderer.line(new Vector2 (150-4, 300+4), new Vector2(150+4, 300-4));
			
			// ball_to_wall vector line
			shapeRenderer.setColor(0, 0.2f, 0.9f, 1f);
			shapeRenderer.line(world.model_balls.get(0).getPos(), new Vector2(world.model_balls.get(0).getPos().x+world.ball_to_wall.x, world.model_balls.get(0).getPos().y+world.ball_to_wall.y));
			
			// ball change vector line
			shapeRenderer.setColor(0, 0.2f, 0.9f, 1f);
			shapeRenderer.line(world.prev_relative_ball_point, new Vector2(world.prev_relative_ball_point.x+world.ball_change.x, world.prev_relative_ball_point.y+world.ball_change.y));
			
			// ball offset points
			shapeRenderer.line(world.prev_relative_ball_point.x-4, world.prev_relative_ball_point.y-4, world.prev_relative_ball_point.x+4, world.prev_relative_ball_point.y+4);
			shapeRenderer.line(world.prev_relative_ball_point.x-4, world.prev_relative_ball_point.y+4, world.prev_relative_ball_point.x+4, world.prev_relative_ball_point.y-4);
			//shapeRenderer.line(world.relative_ball_point.x-4, world.relative_ball_point.y-4, world.relative_ball_point.x+4, world.relative_ball_point.y+4);
			//shapeRenderer.line(world.relative_ball_point.x-4, world.relative_ball_point.y+4, world.relative_ball_point.x+4, world.relative_ball_point.y-4);
		}*/
		shapeRenderer.end();
		
		
	}
	
	private void disableDebug() {
		debug_mode = false;
	}
}
