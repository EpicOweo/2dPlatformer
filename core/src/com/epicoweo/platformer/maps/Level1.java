package com.epicoweo.platformer.maps;

import java.io.IOException;

import com.badlogic.gdx.math.Rectangle;
import com.epicoweo.platformer.etc.Refs;

public class Level1 extends JsonMap {

	private static String levelPath = "../assets/levels/map_2.json";
	private static String backgroundPath = "../assets/textures/levels/level_skeleton.png";
	
	public Level1() throws IOException {
		super(levelPath, backgroundPath);
		
		Rectangle sect1 = new Rectangle(0.5f * Refs.TEXTURE_SIZE, 0.5f * Refs.TEXTURE_SIZE, 17*Refs.TEXTURE_SIZE, 13 * Refs.TEXTURE_SIZE);
		Rectangle sect2 = new Rectangle(sect1.x, sect1.y + sect1.height, 17*Refs.TEXTURE_SIZE, 11 * Refs.TEXTURE_SIZE);
		Rectangle sect3 = new Rectangle(sect1.x + sect1.width, sect1.y + sect1.height, 16*Refs.TEXTURE_SIZE, 11 * Refs.TEXTURE_SIZE);
		Rectangle sect4 = new Rectangle(sect1.x + sect1.width, sect1.y, 16*Refs.TEXTURE_SIZE, 13 * Refs.TEXTURE_SIZE);
		Rectangle sect5 = new Rectangle(sect1.x + 33*Refs.TEXTURE_SIZE, sect1.y, 16*Refs.TEXTURE_SIZE, 24 * Refs.TEXTURE_SIZE);
		
		this.mapSections.add(sect1);
		this.mapSections.add(sect2);
		this.mapSections.add(sect3);
		this.mapSections.add(sect4);
		this.mapSections.add(sect5);
	}

}
