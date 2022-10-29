package com.epicoweo.platformer.maps;

import java.io.IOException;

public class Level1 extends JsonMap {

	private static String levelPath = "../assets/levels/map_2.json";
	private static String backgroundPath = "../assets/textures/levels/level_skeleton.png";
	
	public Level1() throws IOException {
		super(levelPath, backgroundPath);
	}

}
