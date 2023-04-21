package com.epicoweo.platformer.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.epicoweo.platformer.etc.Refs;

public class HUD {
	
	private FitViewport viewport;
	
	protected static float scale;
	Array<HUDItem> items = new Array<HUDItem>();
	
	public HUD(SpriteBatch b) {
		items.add(new AbilitiesBar(new Vector2(10, 10), b));
	
		viewport = new FitViewport(0, 0, Refs.camera);
	}
	
	public void render() {
		for(HUDItem item : items) {
			item.draw(new Vector2(viewport.getScreenX(), viewport.getScreenY()));
		}
	}
	
	public void resize(int oldW, int newW) {
		scale = newW / oldW;
		
		for(HUDItem item : items) {
			item.texture = item.scaleTexture(item.texture, scale);
		}
	}
}
