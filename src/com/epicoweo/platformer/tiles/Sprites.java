package com.epicoweo.platformer.tiles;

import java.util.HashMap;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Sprites {

	static TextureRegion[][] spriteSheet;
	
	public static Object[] allTextures = new Object[256];
	
	public static HashMap<String, TextureRegion> STONE = new HashMap<String, TextureRegion>();
	public static HashMap<String, TextureRegion> FENCE = new HashMap<String, TextureRegion>();
	public static HashMap<String, TextureRegion> PLATFORM = new HashMap<String, TextureRegion>();
	public static HashMap<String, TextureRegion> GRASS = new HashMap<String, TextureRegion>();
	public static HashMap<String, TextureRegion> GREENTILE = new HashMap<String, TextureRegion>();
	public static HashMap<String, TextureRegion> REDTILE = new HashMap<String, TextureRegion>();
	public static HashMap<String, TextureRegion> SPIKE = new HashMap<String, TextureRegion>();
	
	public static TextureRegion OFFBLOCK;
	public static TextureRegion ONBLOCK;
	public static TextureRegion GRAVITYSWAP;
	public static TextureRegion GRAVITYSWAPDOWN;
	
	public Sprites(TextureRegion[][] sheet) {
		spriteSheet = sheet;
		
		STONE = putTiles(0, 0, STONE);
		STONE.put("itl", spriteSheet[3][1]);
		STONE.put("itr", spriteSheet[4][1]);
		STONE.put("ibl", spriteSheet[3][2]);
		STONE.put("ibr", spriteSheet[4][0]);
		STONE.put("full", spriteSheet[3][0]);
		
		
		
		FENCE = putTiles(3, 0, FENCE);
		//[y][x]
		PLATFORM.put("l", spriteSheet[0][6]);
		PLATFORM.put("c", spriteSheet[0][7]);
		PLATFORM.put("r", spriteSheet[0][8]);
		
		OFFBLOCK = spriteSheet[1][6];
		ONBLOCK = spriteSheet[1][7];
		GRAVITYSWAP = spriteSheet[1][8];

		GRASS = putTiles(0, 5, GRASS);
		GRASS.put("itl", spriteSheet[8][1]);
		GRASS.put("itr", spriteSheet[9][1]);
		GRASS.put("ibl", spriteSheet[8][2]);
		GRASS.put("ibr", spriteSheet[9][0]);
		GRASS.put("full", spriteSheet[8][0]);
		
		REDTILE.put("on", spriteSheet[2][6]);
		REDTILE.put("off", spriteSheet[3][6]);
		GREENTILE.put("on", spriteSheet[2][7]);
		GREENTILE.put("off", spriteSheet[3][7]);
		GRAVITYSWAPDOWN = spriteSheet[2][8];
		
		SPIKE.put("up", spriteSheet[3][3]);
		SPIKE.put("down", spriteSheet[5][3]);
		SPIKE.put("left", spriteSheet[4][2]);
		SPIKE.put("right", spriteSheet[4][4]);
		
		allTextures[0] = STONE;
		allTextures[1] = FENCE;
		allTextures[2] = OFFBLOCK;
		allTextures[3] = ONBLOCK;
		allTextures[4] = GRAVITYSWAP;
		allTextures[6] = PLATFORM;
		allTextures[7] = GRASS;
		allTextures[8] = REDTILE;
		allTextures[9] = GREENTILE;
		allTextures[10] = GRAVITYSWAPDOWN;
		allTextures[11] = SPIKE;
		
		
	}
	
	//put 3x3 rectangle into hashmap starting at x,y in the spritesheet
	private HashMap<String, TextureRegion> putTiles(int x, int y, HashMap<String, TextureRegion> map) {
		map.put("tl", spriteSheet[y+0][x+0]);
		map.put("tc", spriteSheet[y+0][x+1]);
		map.put("tr", spriteSheet[y+0][x+2]);
		map.put("cl", spriteSheet[y+1][x+0]);
		map.put("cc", spriteSheet[y+1][x+1]);
		map.put("cr", spriteSheet[y+1][x+2]);
		map.put("bl", spriteSheet[y+2][x+0]);
		map.put("bc", spriteSheet[y+2][x+1]);
		map.put("br", spriteSheet[y+2][x+2]);
		return map;
	}
	
}
