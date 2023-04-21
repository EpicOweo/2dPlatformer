package com.epicoweo.platformer.tiles;

import com.badlogic.gdx.utils.TimeUtils;
import com.epicoweo.platformer.tiles.Tile.TileType;

public class GravitySwapDown extends Tile {
	
	public GravitySwapDown() {
		super(TileType.GravitySwap);
		texture = Sprites.GRAVITYSWAPDOWN;
	}

	@Override
	public void activateSpecialEffect() {
		getPlayer();
		player.readyToInvert = true;
		player.lastReadyToInvert = TimeUtils.millis();
		player.setupAnimations();
	}
}
