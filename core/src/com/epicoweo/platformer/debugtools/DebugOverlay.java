package com.epicoweo.platformer.debugtools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.epicoweo.platformer.etc.Refs;
import com.epicoweo.platformer.screens.GameScreen;

public class DebugOverlay extends Overlay {
	
	SpriteBatch batch;
	BitmapFont font;
	float fps;
	boolean showVectors;
	boolean showHitboxes;
	
	public DebugOverlay(SpriteBatch batch, BitmapFont font, float delta, boolean showVectors, boolean showHitboxes) {
		this.batch = new SpriteBatch();
		this.font = font;
		this.fps = 1/delta;
		this.showVectors = showVectors;
		if(showVectors) {
			VectorOverlay vOverlay = new VectorOverlay(delta);
			vOverlay.render();
		}
		if(showHitboxes) {
			HitboxOverlay hOverlay = new HitboxOverlay(delta);
			hOverlay.render();
		}
	}
	
	@Override
	public void render() {
		batch.begin();
		//left side
		font.draw(batch, "FPS: " + (int)fps, (float)(0.05*Refs.APP_LENGTH), (float)(0.95*Refs.APP_WIDTH));
		font.draw(batch, "Mouse: (" + Gdx.input.getX() + ", " + Gdx.input.getY() + ")", (float)(0.05*Refs.APP_LENGTH), (float)(0.90*Refs.APP_WIDTH));
		font.draw(batch, "Player Position: (" + GameScreen.player.getRect().x + ", " + GameScreen.player.getRect().y + ")", (float)(0.05*Refs.APP_LENGTH), (float)(0.85*Refs.APP_WIDTH));
		font.draw(batch, "Player Velocity: (" + GameScreen.player.velocity.x + ", " + GameScreen.player.velocity.y + ")", (float)(0.05*Refs.APP_LENGTH), (float)(0.80*Refs.APP_WIDTH));
		font.draw(batch, "Player Acceleration: (" + GameScreen.player.acceleration.x + ", " + GameScreen.player.acceleration.y + ")", (float)(0.05*Refs.APP_LENGTH), (float)(0.75*Refs.APP_WIDTH));
		
		//right side
		font.draw(batch, "Press V to show vectors.", (float)(0.85*Refs.APP_LENGTH), (float)(0.95*Refs.APP_WIDTH));
		font.draw(batch, "Press R to respawn.", (float)(0.85*Refs.APP_LENGTH), (float)(0.90*Refs.APP_WIDTH));
		font.draw(batch, "Press F to toggle fly mode.", (float)(0.85*Refs.APP_LENGTH), (float)(0.85*Refs.APP_WIDTH));
		font.draw(batch, "Press H to show hitboxes.", (float)(0.85*Refs.APP_LENGTH), (float)(0.80*Refs.APP_WIDTH));
		batch.end();
		batch.dispose();
	}
}
