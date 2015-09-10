package com.mygdx.bounce2;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.Input.Keys;

public class GameScreen extends ScreenAdapter {
	OrthographicCamera td_cam;
	Main game;
	World world;
	Texture image_ball;
	ShapeRenderer shapeRenderer;
	
	int count = 0;
	
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
		
		if (Gdx.input.isKeyPressed(Keys.RIGHT)) world.model_ball.addForce(0.6f, 0);
		if (Gdx.input.isKeyPressed(Keys.LEFT)) world.model_ball.addForce(-0.6f, 0);
		if (Gdx.input.isKeyPressed(Keys.UP)) world.model_ball.addForce(0, 0.6f);
		if (Gdx.input.isKeyPressed(Keys.DOWN)) world.model_ball.addForce(0, -0.6f);
		
		world.update();
	}
	
	private void draw() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		List<Wall> walls = world.walls;
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(0f, 0f, 0f, 1);
		for (Wall wall : walls ) {
			shapeRenderer.line(wall.getPoint1(), wall.getPoint2());
		}
		shapeRenderer.end();
		
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(0f, 1f, 0.3f, 1f);
		shapeRenderer.circle(world.model_ball.getX(), 
				world.model_ball.getY(), world.model_ball.getRadius());
		shapeRenderer.end();
	}
}
