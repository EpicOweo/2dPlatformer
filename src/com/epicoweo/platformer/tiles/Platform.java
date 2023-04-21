package com.epicoweo.platformer.tiles;

public class Platform extends Tile {
	
	public Platform() {
		super(TileType.Platform);
		isVaried = true;
		textures = Sprites.PLATFORM;
	}

	@Override
	public void activateSpecialEffect() {
		
		
	}

}
