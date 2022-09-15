package com.epicoweo.platformer.maps;

public class Level1 extends Map {

	private static String path = "../assets/level1.txt";
	public static int height = 150;
	public static int width = 150;
	// TODO: fix width and height having to be the same
	
	public Level1() {
		super(path, width, height);
	}

}
