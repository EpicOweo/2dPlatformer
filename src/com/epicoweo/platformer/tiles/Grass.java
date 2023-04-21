package com.epicoweo.platformer.tiles;

public class Grass extends NormalTile implements Tileable {

	public Grass() {
		isVaried = true;
		textures = Sprites.GRASS;
	}
}
