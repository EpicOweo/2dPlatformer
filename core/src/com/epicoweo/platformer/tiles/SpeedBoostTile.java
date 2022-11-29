package com.epicoweo.platformer.tiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.TimeUtils;

public class SpeedBoostTile extends Tile {

	Texture spriteSheet;
	private static final int FRAME_COLS = 4;
	private static final int FRAME_ROWS = 1;
	
	public SpeedBoostTile() {
		super(TileType.SpeedBoost);
		this.isAnimated = true;
		
		spriteSheet = new Texture(Gdx.files.internal("../assets/textures/tiles/speedboost.png"));
		
		// Use the split utility method to create a 2D array of TextureRegions. This is
		// possible because this sprite sheet contains frames of equal size and they are
		// all aligned.
		TextureRegion[][] tmp = TextureRegion.split(spriteSheet,
				spriteSheet.getWidth() / FRAME_COLS,
				spriteSheet.getHeight() / FRAME_ROWS);

		// Place the regions into a 1D array in the correct order, starting from the top
		// left, going across first. The Animation constructor requires a 1D array.
		TextureRegion[] frames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
		int index = 0;
		for (int i = 0; i < FRAME_ROWS; i++) {
			for (int j = 0; j < FRAME_COLS; j++) {
				frames[index++] = tmp[i][j];
			}
		}
		
		animation = new Animation<TextureRegion>(0.1f, frames);
		this.stateTime = 0f;
	}
	
	@Override
	public void activateSpecialEffect() {
		getPlayer();
		
		player.maxVelocity.x = 400;
		player.movementSpeed = 400;
		player.lastOnSpeedBoost = TimeUtils.millis();
		
	}

}
