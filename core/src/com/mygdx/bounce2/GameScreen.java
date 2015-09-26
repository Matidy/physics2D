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
		if (Gdx.input.isKeyPressed(Keys.T)){
			int x = Gdx.input.getX();
			int y = Gdx.input.getY();
			Wall wall1 = world.walls.get(0);
			Wall wall2 = world.walls.get(1);
			Vector2 intersection_point = world.findIntersection(wall1.getVector(), wall1.getPoint1(),
																wall2.getVector(), wall2.getPoint1());
			System.out.println("Mouse pos: "+x+", "+(600-y));
			System.out.println("Inter point: "+intersection_point.x+", "+intersection_point.y);
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
		
		List<Wall> walls = world.walls;
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(0f, 0f, 0f, 1f);
		for (Wall wall : walls ) {
			shapeRenderer.line(wall.getPoint1(), wall.getPoint2());
		}
		shapeRenderer.end();
		
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(0f, 1f, 0.4f, 1f);
		for (int i=0; i<world.model_balls.size(); i++) {
			if (i==1) shapeRenderer.setColor(0f, 0.4f, 1f, 1f);; 
			shapeRenderer.circle(world.model_balls.get(i).getX(), 
								 world.model_balls.get(i).getY(), 
								 world.model_balls.get(i).getRadius());
		}
		shapeRenderer.end();
	}
}
