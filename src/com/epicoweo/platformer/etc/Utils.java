package com.epicoweo.platformer.etc;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Utils {

	public static Texture resizeTexture(Texture texture, int width, int height) {
		
		if(!texture.getTextureData().isPrepared()) {
			texture.getTextureData().prepare();
		}
		Texture resized;
		Pixmap oldBg = new Pixmap(width, height, Format.Alpha);
		Pixmap newBg = new Pixmap(width, height, oldBg.getFormat());
		newBg.drawPixmap(oldBg,
				0, 0, oldBg.getWidth(), oldBg.getHeight(),
				0, 0, width, height);
		
		resized = new Texture(newBg);
		
		oldBg.dispose();
		newBg.dispose();
		return resized;
	}
	
	public static TextureRegion[] flattenSpriteSheet(TextureRegion[][] sprites) {
		TextureRegion[] flattened = new TextureRegion[sprites.length*sprites[0].length];
		int counter = 0;
		for(int i = 0; i < sprites.length; i++) {
			for(int j = 0; j < sprites[i].length; j++) {
				flattened[counter] = sprites[i][j];
				counter++;
			}
		}
		return flattened;
	}
}
