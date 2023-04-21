package com.epicoweo.platformer.tiles;

import com.badlogic.gdx.utils.Array;
import com.epicoweo.platformer.maps.PNGMap;
import com.epicoweo.platformer.screens.GameScreen;

public class OffTile extends Tile {

	public boolean needsProjectile = true;
	
	public OffTile() {
		super(TileType.OnOff);
		this.texture = Sprites.OFFBLOCK;
	}

	@Override
	public void activateSpecialEffect() {
		for(Array<Tile> row : GameScreen.map.mapTiles) {
			for(Tile t : row) {
				if(t == null) continue;
				if(t.type == TileType.OnOff && !(t instanceof GreenTile || t instanceof RedTile)) {
					OnTile newTile = new OnTile();
					newTile.x = t.x;
					newTile.y = t.y;
					GameScreen.map.mapTiles.get(t.y).set(t.x, newTile);
					GameScreen.map.mapLayout.get(t.y).set(t.x, PNGMap.ONBLOCK);
				}
			}
		}
		GreenTile.activated = true;
		RedTile.activated = false;
		
	}

}
