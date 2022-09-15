package com.epicoweo.platformer.overlays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.epicoweo.platformer.etc.Refs;

public class DebugOverlay extends Overlay {
	
	SpriteBatch batch;
	BitmapFont font;
	float fps;
	boolean showVectors;
	
	public DebugOverlay(SpriteBatch batch, BitmapFont font, float delta, boolean showVectors) {
		this.batch = batch;
		this.font = font;
		this.fps = 1/delta;
		this.showVectors = showVectors;
		if(showVectors) {
			VectorOverlay vOverlay = new VectorOverlay(delta);
			vOverlay.render();
		}
	}
	
	@Override
	public void render() {
		batch.begin();
		//left side
		font.draw(batch, "FPS: " + (int)fps, (float)(0.05*Refs.APP_LENGTH), (float)(0.95*Refs.APP_WIDTH));
		font.draw(batch, "Mouse: (" + Gdx.input.getX() + ", " + Gdx.input.getY() + ")", (float)(0.05*Refs.APP_LENGTH), (float)(0.90*Refs.APP_WIDTH));
		
		//right side
		font.draw(batch, "Press V to show vectors.", (float)(0.85*Refs.APP_LENGTH), (float)(0.95*Refs.APP_WIDTH));
		batch.end();
	}
}
