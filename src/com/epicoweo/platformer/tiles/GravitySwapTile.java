package com.epicoweo.platformer.tiles;

import com.badlogic.gdx.utils.TimeUtils;
import com.epicoweo.platformer.etc.Refs;
import com.epicoweo.platformer.screens.GameScreen;

public class GravitySwapTile extends Tile {
	
	public GravitySwapTile() {
		super(TileType.GravitySwap);
		texture = Sprites.GRAVITYSWAP;
	}

	@Override
	public void activateSpecialEffect() {
		getPlayer();
		player.readyToInvert = true;
		player.lastReadyToInvert = TimeUtils.millis();
		player.setupAnimations();
	}
	
}
