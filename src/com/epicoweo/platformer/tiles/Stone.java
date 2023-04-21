package com.epicoweo.platformer.tiles;

public class Stone extends NormalTile implements Tileable {
	
	public Stone() {
		isVaried = true;
		textures = Sprites.STONE;
	}
}
