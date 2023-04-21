package com.epicoweo.platformer.tiles;

import com.epicoweo.platformer.tiles.Tile.TileType;

public class GreenTile extends Tile {

	public static boolean activated = false;
	
	public GreenTile() {
		super(TileType.OnOff);
		this.textures = Sprites.GREENTILE;
	}

	@Override
	public void activateSpecialEffect() {
		// TODO Auto-generated method stub
		
	}
}
