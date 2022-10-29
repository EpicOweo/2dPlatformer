package com.epicoweo.platformer.maps;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.epicoweo.platformer.etc.Refs;

public class JsonMap {
	
	public OrthographicCamera camera;
	
	public Array<Array<Integer>> mapLayout;
	public Array<Array<Integer>> mapEntities;
	public Array<Array<Integer>> mapEtc;
	
	public Array<Array<String>> tileTypes;
	public Array<Array<Float>> tileRotations;
	
	private String mapFileContents = "";
	public String backgroundPath;
	
	public int height;
	public int width;
	
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
	
	public JsonMap(String path, String backgroundPath) throws IOException {
		
		this.backgroundPath = backgroundPath;
		
		File f = new File(path);
		Scanner scanner = new Scanner(f);
		
		while(scanner.hasNextLine()) {
			this.mapFileContents += scanner.nextLine() + "\n";
		}
		scanner.close();
		
		this.mapLayout = new Array<Array<Integer>>();
		this.mapEntities = new Array<Array<Integer>>();
		this.mapEtc = new Array<Array<Integer>>();
		this.tileTypes = new Array<Array<String>>();
		this.tileRotations = new Array<Array<Float>>();

		getMapData();
		getEntityData();
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
		
		for(int i = 0; i < this.height; i++) { // set arrays to default values
			Array<Integer> addArr = new Array<Integer>();
			Array<String> addArrStr = new Array<String>();
			Array<Float> addArrFloat = new Array<Float>();
			for(int j = 0; j < width; j++) {
				addArr.add(0);
				addArrStr.add("");
				addArrFloat.add(0f);
			}
			mapLayout.add(addArr);
			tileTypes.add(addArrStr);
			tileRotations.add(addArrFloat);
		}
		
		JsonValue mapLayoutJson = fromJson.get("mapLayout");
		
		for(int i = 0; i < mapLayoutJson.size; i++) {
			//get coords of the block
			ArrayList<Float> coords = json.fromJson(ArrayList.class, mapLayoutJson.get(i).get("coords").toJson(OutputType.json));
			//get texture ID
			int textureId = json.fromJson(Integer.class, mapLayoutJson.get(i).get("textureId").toJson(OutputType.json));
			String type = json.fromJson(String.class, mapLayoutJson.get(i).get("type").toJson(OutputType.json));
			float rotation = json.fromJson(Float.class, mapLayoutJson.get(i).get("rotation").toJson(OutputType.json));
			
			//add texture ID to the array
			mapLayout.get(height - 1 - (int)(double)coords.get(1)).insert((int)(double)coords.get(0), textureId);
			tileTypes.get(height - 1 - (int)(double)coords.get(1)).insert((int)(double)coords.get(0), type);
			tileRotations.get(height - 1 - (int)(double)coords.get(1)).insert((int)(double)coords.get(0), rotation);
			
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void getEntityData() {
		
		Json json = new Json();
		JsonValue fromJson = new JsonReader().parse(this.mapFileContents);

		for(int i = 0; i < this.height; i++) {
			mapEntities.add(new Array<Integer>());
		}
		
		JsonValue mapEntitiesJson = fromJson.get("mapEntities");
		
		for(int i = 0; i < mapEntitiesJson.size; i++) {
			//get coords of the block
			ArrayList<Float> coords = json.fromJson(ArrayList.class, mapEntitiesJson.get(i).get("coords").toJson(OutputType.json));
			//get type
			Integer type = json.fromJson(Integer.class, mapEntitiesJson.get(i).get("type").toJson(OutputType.json));
			
			//add type to array
			if(type == JsonMap.FLAILENEMY) {
				mapEntities.get((int)(double)coords.get(1)).insert((int)(double)coords.get(0), 1);
			} else {
				mapEntities.get((int)(double)coords.get(1)).insert((int)(double)coords.get(0), 0);
			}
			
		}
		
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
				mapEtc.get(mapEtc.size - 1 - (int)(double)coords.get(1)).insert((int)(double)coords.get(0), 1);
			} else {
				mapEtc.get(mapEtc.size - 1 - (int)(double)coords.get(1)).insert((int)(double)coords.get(0), 0);
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
	
	public void renderBackground() {
		Texture texture = new Texture(this.backgroundPath);
		SpriteBatch batch = new SpriteBatch();
		batch.begin();
		batch.draw(texture, 0, 0);
		batch.end();
		batch.dispose();
		texture.dispose();
	}
}
	