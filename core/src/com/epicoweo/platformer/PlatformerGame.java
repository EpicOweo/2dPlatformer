package com.epicoweo.platformer;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.epicoweo.platformer.screens.GameScreen;

public class PlatformerGame extends Game {
	public SpriteBatch batch;
	public BitmapFont font;
	public ShapeRenderer renderer;
	
	Array<Rectangle> rects;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		font = new BitmapFont();
		renderer = new ShapeRenderer();
		this.setScreen(new GameScreen(this));
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
	}
}
