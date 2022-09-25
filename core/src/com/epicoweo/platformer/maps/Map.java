package com.epicoweo.platformer.maps;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.epicoweo.platformer.etc.Refs;

public class Map {
	public Array<Array<Integer>> mapLayout;
	public Array<Array<Integer>> mapEntities;
	public Array<Array<Integer>> mapEtc;
	
	public int height;
	public int width;
	private String layoutPath;
	private String entitiesPath;
	private String etcPath;
	
	public Vector2 playerSpawn;
	
	//tiles
	public static int EMPTY = 0;
	public static int STONE = 1;
	public static int GRASS = 2;
	public static int DIRT = 3;
	
	//entities
	public static int FLAILENEMY = 1;
	
	//etc
	public static int SPAWNPOINT = 1;
	
	
	public Map(String path, int width, int height) {
		this.height = height;
		this.width = width;
		this.layoutPath = path;
		this.entitiesPath = path.substring(0, path.length()-4) + "_entities.txt";
		this.etcPath = path.substring(0, path.length()-4) + "_etc.txt";
		
		
		this.playerSpawn = new Vector2();
		
		this.mapLayout = new Array<Array<Integer>>();
		this.mapEntities = new Array<Array<Integer>>();
		this.mapEtc = new Array<Array<Integer>>();
		loadMap();
		grabEtcData();
		setPlayerSpawn();
	}
	
	private void setPlayerSpawn() {
		for(Array<Integer> i : mapEtc) {
			for(int j : i) { 
				if(j == 1) {
					playerSpawn = new Vector2(Refs.TEXTURE_SIZE * i.indexOf(j, false), Refs.TEXTURE_SIZE * (mapEtc.size - mapEtc.indexOf(i, false)));
					System.out.println(playerSpawn);
				}
			}
		}
	}
	
	private Array<Integer> stringArrToIntArr(Array<String> arr) {
		Array<Integer> newArr = new Array<Integer>();
		
		for(String s : arr) {
			newArr.add(Integer.valueOf(s));
		}
		
		return newArr;
	}
	
	private void grabEtcData() {
		
		File f = new File(etcPath);
		Scanner reader = null;
		try {
			reader = new Scanner(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String str = "";
		Array<Integer> line_arr = new Array<Integer>();
		//grab etc data
		for(int i = 0; i < height; i++) {
			line_arr.clear();
			
			
			for(String c : reader.nextLine().split("")) {
				str = str + c;
			}
			str = str + "\n";
			
			//for(int j = 0; j < width; j++) {
			//	line_arr.add(Integer.valueOf());
			//}
			mapEtc.add(line_arr);
		}
		Array<String> strs = Array.with(str.split("\n"));
		Array<Array<Integer>> intarr = new Array<Array<Integer>>();
		
		for(String s : strs) {
			Array<String> splitstr = Array.with(s.split(""));
			intarr.add(stringArrToIntArr(splitstr));
		}
		
		System.out.println(intarr.toString("\n"));
		
		mapEtc = intarr;
		
		reader.close();
	}
	
	private void grabMapData() {
		
		File f = new File(layoutPath);
		Scanner reader = null;
		try {
			reader = new Scanner(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String str = "";
		//grab etc data
		for(int i = 0; i < height; i++) {
			for(String c : reader.nextLine().split("")) {
				str = str + c;
			}
			str = str + "\n";
		}
		Array<String> strs = Array.with(str.split("\n"));
		Array<Array<Integer>> intarr = new Array<Array<Integer>>();
		
		for(String s : strs) {
			Array<String> splitstr = Array.with(s.split(""));
			intarr.add(stringArrToIntArr(splitstr));
		}
		
		System.out.println(intarr.toString("\n"));
		
		mapLayout = intarr;
		
		reader.close();
	}
	
	private void grabEntityData() {
		
		File f = new File(entitiesPath);
		Scanner reader = null;
		try {
			reader = new Scanner(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		String str = "";
		
		//grab entity data
		for(int i = 0; i < height; i++) {
			for(String c : reader.nextLine().split("")) {
				str = str + c;
			}
			str = str + "\n";
		}
		Array<String> strs = Array.with(str.split("\n"));
		Array<Array<Integer>> intarr = new Array<Array<Integer>>();
		
		for(String s : strs) {
			Array<String> splitstr = Array.with(s.split(""));
			intarr.add(stringArrToIntArr(splitstr));
		}
		
		System.out.println(intarr.toString("\n"));
		
		mapEntities = intarr;
		
		reader.close();
	}
	
	private void loadMap() {
		grabMapData();
		grabEntityData();
		grabEtcData();
	}
	
	
}
