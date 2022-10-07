package com.epicoweo.platformer.maps;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.epicoweo.platformer.etc.Refs;

public class JsonMap {
	public Array<Array<Integer>> mapLayout;
	public Array<Array<Integer>> mapEntities;
	public Array<Array<Integer>> mapEtc;
	
	private String mapFileContents = "";
	
	public int height;
	public int width;
	private String path;
	
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
	
	public JsonMap(String path) throws IOException {
		this.path = path;
		
		File f = new File(path);
		Scanner scanner = new Scanner(f);
		
		while(scanner.hasNextLine()) {
			this.mapFileContents += scanner.nextLine() + "\n";
		}
		scanner.close();
		
		this.mapLayout = new Array<Array<Integer>>();
		this.mapEntities = new Array<Array<Integer>>();
		this.mapEtc = new Array<Array<Integer>>();

		getMapData();
		getEtcData();
		
		this.playerSpawn = new Vector2();
		
		setPlayerSpawn();
	}
	
	@SuppressWarnings("unchecked")
	private void getMapData() {
		
		Json json = new Json();
		JsonValue fromJson = new JsonReader().parse(this.mapFileContents);
		
		this.width = fromJson.getInt("levelWidth");
		this.height = fromJson.getInt("levelHeight");
		
		for(int i = 0; i < this.height; i++) {
			Array<Integer> addArr = new Array<Integer>();
			for(int j = 0; j < width; j++) {
				addArr.add(0);
			}
			mapLayout.add(addArr);
		}
		
		JsonValue mapLayoutJson = fromJson.get("mapLayout");
		
		for(int i = 0; i < mapLayoutJson.size; i++) {
			//get coords of the block
			ArrayList<Float> coords = json.fromJson(ArrayList.class, mapLayoutJson.get(i).get("coords").toJson(OutputType.json));
			//get texture ID
			int textureId = json.fromJson(Integer.class, mapLayoutJson.get(i).get("textureId").toJson(OutputType.json));
			
			//add texture ID to the array
			mapLayout.get(height - 1 - (int)(double)coords.get(1)).insert((int)(double)coords.get(0), textureId);
			
		}
		
		System.out.println(mapLayout);
		
	}
	
	@SuppressWarnings("unchecked")
	private void getEtcData() {
		
		Json json = new Json();
		JsonValue fromJson = new JsonReader().parse(this.mapFileContents);

		for(int i = 0; i < this.height; i++) {
			mapEtc.add(new Array<Integer>());
		}
		
		JsonValue mapEtcJson = fromJson.get("mapEtc");
		
		for(int i = 0; i < mapEtcJson.size; i++) {
			//get coords of the block
			ArrayList<Float> coords = json.fromJson(ArrayList.class, mapEtcJson.get(i).get("coords").toJson(OutputType.json));
			//get type
			Integer type = json.fromJson(Integer.class, mapEtcJson.get(i).get("type").toJson(OutputType.json));
			
			//add type to array
			if(type == JsonMap.SPAWNPOINT) {
				mapEtc.get((int)(double)coords.get(1)).insert((int)(double)coords.get(0), 1);
			} else {
				mapEtc.get((int)(double)coords.get(1)).insert((int)(double)coords.get(0), 0);
			}
			
		}
		
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
}
	