package com.epicoweo.platformer.tiles;

import com.epicoweo.platformer.tiles.Tile.TileType;

public class RedTile extends Tile {

	public static boolean activated = true;
	
	public RedTile() {
		super(TileType.OnOff);
		this.textures = Sprites.REDTILE;
	}

	@Override
	public void activateSpecialEffect() {
		// TODO Auto-generated method stub
		
	}
	
}
