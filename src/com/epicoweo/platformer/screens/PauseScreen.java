package com.epicoweo.platformer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.epicoweo.platformer.PlatformerGame;
import com.epicoweo.platformer.etc.Refs;
import com.epicoweo.platformer.maps.PNGMap;
import com.epicoweo.platformer.ui.Button;

public class PauseScreen implements Screen {

	final PlatformerGame game;
	public PNGMap map;
	
	OrthographicCamera camera;
	
	public Array<Button> buttons;
	
	public PauseScreen(final PlatformerGame game) {
		this.game = game;
		this.buttons = new Array<Button>();
		
		setupCamera();
		setupButtons();
		
	}
	
	void setupCamera() {
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Refs.APP_LENGTH, Refs.APP_WIDTH);
		camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
		camera.update();
		Refs.debugCamera = camera;
	}
	
	void setupButtons() {
		Button exitButton = new Button(150, Refs.APP_WIDTH - 150, 100, 30, Color.WHITE, "Exit Game");
		exitButton.setFunc(() -> {
			Gdx.app.exit();
			return null;
		});
		buttons.add(exitButton);
	}
	
	@Override
	public void show() {
		
		
	}

	public void renderBG(int r, int g, int b) {
		
		float bgWidth = (float) (0.8 * Refs.APP_LENGTH);
		float bgHeight = (float) (0.8 * Refs.APP_WIDTH);
		float bgOffsetX = (Refs.APP_LENGTH - bgWidth) / 2;
		float bgOffsetY = (Refs.APP_WIDTH - bgHeight) / 2;
		
		game.renderer.setProjectionMatrix(camera.combined);
		game.renderer.setColor(new Color(r/255f, g/255f, b/255f, 1));
		game.renderer.begin(ShapeType.Filled);
		game.renderer.rect(bgOffsetX, bgOffsetY, bgWidth, bgHeight);
		game.renderer.end();
	}
	
	void checkButtons() {
		for(Button b : buttons) {
			if(b.check()) {
				try {
					b.use();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public void render(float delta) {
		ScreenUtils.clear(0, 0, 0, 1);
		camera.update();
		
		renderBG(20, 22, 51);
		
		//game.renderer.begin(ShapeType.Filled);
		//game.renderer.setProjectionMatrix(camera.combined);
		//game.renderer.end();
		
		for(Button b : buttons) {
			b.draw();
		}
		
		checkButtons();
		
		if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
			game.setScreen(game.gameScreen);
		}
	}

	@Override
	public void resize(int width, int height) {
		
		
	}

	@Override
	public void pause() {
		
		
	}

	@Override
	public void resume() {
		
		
	}

	@Override
	public void hide() {
		
		
	}

	@Override
	public void dispose() {
		
		
	}

}
