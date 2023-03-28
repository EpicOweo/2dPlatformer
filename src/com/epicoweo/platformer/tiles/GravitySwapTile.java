package com.epicoweo.platformer.tiles;

import com.badlogic.gdx.utils.TimeUtils;

public class GravitySwapTile extends Tile {
	
	public GravitySwapTile() {
		super(TileType.GravitySwap);
	}

	@Override
	public void activateSpecialEffect() {
		getPlayer();
		player.readyToInvert = true;
		player.lastReadyToInvert = TimeUtils.millis();
	}
	
}
