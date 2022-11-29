package com.epicoweo.platformer.maps;

import java.io.IOException;

import com.badlogic.gdx.math.Rectangle;
import com.epicoweo.platformer.etc.Refs;

public class GravitySwitchLevel  extends JsonMap {

	private static String levelPath = "../assets/levels/map_11113.json";
	private static String backgroundPath = "../assets/textures/levels/level_skeleton.png";
	
	public GravitySwitchLevel() throws IOException {
		super(levelPath, backgroundPath);
		
		Rectangle sect1 = new Rectangle(0.5f * Refs.TEXTURE_SIZE, 0.5f * Refs.TEXTURE_SIZE, 49*Refs.TEXTURE_SIZE, 24 * Refs.TEXTURE_SIZE);
		
		this.mapSections.add(sect1);
	
		this.cameraAttachPlayer = true;
	}

}
