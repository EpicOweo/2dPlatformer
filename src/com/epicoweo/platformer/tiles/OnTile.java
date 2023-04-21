package com.epicoweo.platformer.tiles;

import com.badlogic.gdx.utils.Array;
import com.epicoweo.platformer.maps.PNGMap;
import com.epicoweo.platformer.screens.GameScreen;
import com.epicoweo.platformer.tiles.Tile.TileType;

public class OnTile extends Tile {

	public boolean needsProjectile = true;
	
	public OnTile() {
		super(TileType.OnOff);
		this.texture = Sprites.ONBLOCK;
	}

	@Override
	public void activateSpecialEffect() {
		for(Array<Tile> row : GameScreen.map.mapTiles) {
			for(Tile t : row) {
				if(t == null) continue;
				if(t.type == TileType.OnOff && !(t instanceof GreenTile || t instanceof RedTile)) {
					OffTile newTile = new OffTile();
					newTile.x = t.x;
					newTile.y = t.y;
					GameScreen.map.mapTiles.get(t.y).set(t.x, newTile);
					GameScreen.map.mapLayout.get(t.y).set(t.x, PNGMap.OFFBLOCK);
				}
			}
		}
		
		GreenTile.activated = false;
		RedTile.activated = true;
	}

}
