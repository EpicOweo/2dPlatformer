package com.epicoweo.platformer.maps;

public class Level2 extends Map {

	private static String path = "../assets/levels/level2.txt";
	public static int height = 50;
	public static int width = 50;
	// TODO: fix width and height having to be the same
	
	public Level2() {
		super(path, width, height);
	}

}
