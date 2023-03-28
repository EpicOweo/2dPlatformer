package com.epicoweo.platformer.maps;

import java.io.IOException;

import com.badlogic.gdx.math.Rectangle;
import com.epicoweo.platformer.etc.Refs;

public class GravitySwitchLevel  extends JsonMap {

	private static String levelPath = "./assets/levels/testmap.json";
	private static String backgroundPath = "./assets/textures/levels/level_skeleton.png";
	
	public GravitySwitchLevel() throws IOException {
		super(levelPath, backgroundPath);
	
		this.cameraAttachPlayer = true;
	}

}
