package com.epicoweo.platformer.tiles;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.epicoweo.platformer.entities.Player;
import com.epicoweo.platformer.etc.Refs;

public abstract class Tile {
	
	public int x, y;
	public TileType type;
	public boolean isAnimated;
	public float stateTime;
	public Animation<TextureRegion> animation;
	
	Player player;
	
	public enum TileType {
		Normal, GravitySwap, SpeedBoost, Platform;
	}
	
	public Tile(TileType t) {
		this.type = t;
	}
	
	public void getPlayer() {
		this.player = Refs.player;
	}
	
	public abstract void activateSpecialEffect();
}
