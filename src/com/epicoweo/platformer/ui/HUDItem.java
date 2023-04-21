package com.epicoweo.platformer.ui;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class HUDItem {

	Vector2 position = new Vector2();
	Texture texture;
	SpriteBatch b;
	
	public HUDItem(Vector2 position, SpriteBatch b) {
		this.position = position;
		this.b = b;
	}
	
	Texture scaleTexture(Texture t, float scale) {
		if(!t.getTextureData().isPrepared()) {
		    t.getTextureData().prepare();
		}
		Pixmap pixmap200 = texture.getTextureData().consumePixmap();
		Pixmap pixmap100 = new Pixmap((int)(pixmap200.getWidth()*scale), (int)(pixmap200.getHeight()*scale), pixmap200.getFormat());
		pixmap100.drawPixmap(pixmap200,
		        0, 0, pixmap200.getWidth(), pixmap200.getHeight(),
		        0, 0, pixmap100.getWidth(), pixmap100.getHeight()
		);
		Texture texture = new Texture(pixmap100);
		pixmap200.dispose();
		pixmap100.dispose();
		return texture;
	}

	public void draw(Vector2 offset) {
		b.begin();
		b.draw(texture, this.position.x, this.position.y);
		b.end();
	}
}
