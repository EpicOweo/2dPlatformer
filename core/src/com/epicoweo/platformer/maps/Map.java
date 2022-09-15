package com.epicoweo.platformer.maps;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Map {
	public int[][] mapLayout;
	public int height;
	public int width;
	private String path;
	
	public static int EMPTY = 0;
	public static int TILE = 1;
	
	public Map(String path, int width, int height) {
		this.height = height;
		this.width = width;
		this.path = path;
		
		this.mapLayout = new int[width][height];
		loadMap();
	}
	
	private void loadMap() {
		File f = new File(path);
		Scanner reader = null;
		try {
			reader = new Scanner(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		for(int i = 0; i < height; i++) {
			int[] line_arr = new int[width];
			String input = reader.nextLine();
			for(int j = 0; j < width; j++) {
				line_arr[j] = Character.digit(input.charAt(j), 10);
			}
			mapLayout[i] = line_arr;
			
		}
	}
	
	
}
