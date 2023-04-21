package com.epicoweo.platformer.tiles;

import com.badlogic.gdx.math.Rectangle;
import com.epicoweo.platformer.etc.Refs;
import com.epicoweo.platformer.screens.GameScreen;

public class Spike extends Tile {

	//0 up 1 down 2 left 3 right
	public int spikeType;
	
	public Spike(int type) {
		super(TileType.Spike);
		this.spikeType = type;
		this.textures = Sprites.SPIKE;
	}

	@Override
	public void activateSpecialEffect() {
		if(GameScreen.iFrameCounter == 0) {
			Rectangle rect = getEffectiveHitbox();
			System.out.println(rect);
			if(GameScreen.player.getRect().overlaps(rect)) {
				GameScreen.respawnPlayer();
			
			}
		}
		
	}

	private Rectangle getEffectiveHitbox() {
		Rectangle efHB = new Rectangle();
		
		int x, y, width, height;
		
		switch(spikeType) {
		case 0: //up
			width = 16;
			height = 6;
			x = this.x*Refs.TEXTURE_SIZE;
			y = (GameScreen.player.map.mapTiles.size-1-this.y)*Refs.TEXTURE_SIZE;
			break;
		case 1: // down
			width = 16;
			height = 6;
			x = this.x*Refs.TEXTURE_SIZE;
			y = (GameScreen.player.map.mapTiles.size-2-this.y)*Refs.TEXTURE_SIZE+10;
			break;
		case 2: // left
			width = 6;
			height = 16;
			x = (this.x-1)*Refs.TEXTURE_SIZE+10;
			y = (GameScreen.player.map.mapTiles.size-1-this.y)*Refs.TEXTURE_SIZE;
			break;
		case 3: // right
			width = 6;
			height = 16;
			x = this.x*Refs.TEXTURE_SIZE;
			y = (GameScreen.player.map.mapTiles.size-1-this.y)*Refs.TEXTURE_SIZE;
			break;
		default:
			width = 0;
			height = 0;
			x = 0;
			y = 0;
		}
		
		efHB.set(x, y, width, height);
		
		return efHB;
	}

}
