package com.epicoweo.platformer.tiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.epicoweo.platformer.etc.Assets;
import com.epicoweo.platformer.screens.GameScreen;

public class NullTile extends NormalTile {

	public NullTile() {
		texture = new TextureRegion((Texture)GameScreen.game.manager.get(Assets.NULL));
	}
}
