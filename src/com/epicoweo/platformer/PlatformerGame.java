package com.epicoweo.platformer;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.epicoweo.platformer.screens.GameScreen;
import com.epicoweo.platformer.screens.PauseScreen;

public class PlatformerGame extends Game {
	public SpriteBatch batch;
	
	public static BitmapFont font;
	
	public ShapeRenderer renderer;
	
	public GameScreen gameScreen;
	public PauseScreen debugScreen;
	public AssetManager manager;
	
	Array<Rectangle> rects;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		manager = new AssetManager();
		
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("./assets/font/homespun.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 16;
		font = generator.generateFont(parameter);
		generator.dispose();
		
		renderer = new ShapeRenderer();
		
		
		this.gameScreen = new GameScreen(this);
		this.debugScreen = new PauseScreen(this);
		this.setScreen(gameScreen);
	}

	@Override
	public void render () {
		super.render();
		
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		font.dispose();
		renderer.dispose();
		manager.dispose();
	}
	
	@Override
	public void setScreen(Screen screen) {
		super.setScreen(screen);
		
		// Reset font to default white when screen is changed
		font.setColor(Color.WHITE);
	}
}
