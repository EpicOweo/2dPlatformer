package com.epicoweo.platformer.maps;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.epicoweo.platformer.etc.Refs;
import com.epicoweo.platformer.tiles.BackgroundTile;
import com.epicoweo.platformer.tiles.Fence;
import com.epicoweo.platformer.tiles.ForegroundTile;
import com.epicoweo.platformer.tiles.Grass;
import com.epicoweo.platformer.tiles.GravitySwapDown;
import com.epicoweo.platformer.tiles.GravitySwapTile;
import com.epicoweo.platformer.tiles.GreenTile;
import com.epicoweo.platformer.tiles.OffTile;
import com.epicoweo.platformer.tiles.OnTile;
import com.epicoweo.platformer.tiles.Platform;
import com.epicoweo.platformer.tiles.RedTile;
import com.epicoweo.platformer.tiles.Spike;
import com.epicoweo.platformer.tiles.Stone;
import com.epicoweo.platformer.tiles.Tile;

public class PNGMap {
	
	public OrthographicCamera camera;
	
	public boolean cameraAttachPlayer = false;
	
	public Array<Array<Integer>> mapLayout;
	public Array<Array<Integer>> mapEntities;
	public Array<Array<Integer>> mapEtc;
	
	public Array<Array<Tile>> mapTiles;
	
	public Array<Rectangle> mapSections;
	public HashMap<Rectangle, Vector2> respawnPoints;
	
	public Array<Rectangle> platformRects = new Array<Rectangle>();
	
	private String jsonFileContents = "";
	public String backgroundPath;
	public Texture backgroundTexture;
	
	public int height;
	public int width;
	
	public Vector2 playerSpawn;

	private String mapSectionsJsonPath;
	
	public Rectangle[] outerMapRects;

	public Array<Array<Array<BackgroundTile>>> bgTiles;
	public Array<Array<Array<Integer>>> bgLayout;
	
	public Array<Array<Array<ForegroundTile>>> fgTiles;
	public Array<Array<Array<Integer>>> fgLayout;
	
	//tiles
	public static int EMPTY = 0;
	public static int STONE = 1;
	
	public static int OFFBLOCK = 2;
	public static int ONBLOCK = 3;
	
	public static int GRAVITYSWAP = 4;

	public static int PLATFORM = 5;
	public static int GRASS = 6;
	public static int REDTILE = 7;
	public static int GREENTILE = 8;
	public static int GRAVITYSWAPDOWN = 9;
	
	public static int SPIKEUP = 10;
	public static int SPIKEDOWN = 11;
	public static int SPIKELEFT = 12;
	public static int SPIKERIGHT = 13;
	
	//entities
	public static int FLAILENEMY = 1;
	
	//etc
	public static int SPAWNPOINT = 1;
	
	public PNGMap(String mapFolderPath) throws IOException {
		
		this.backgroundPath = mapFolderPath + "/background.png";
		this.mapSectionsJsonPath = mapFolderPath + "/mapsections.json";
		
		Pixmap mainPix = new Pixmap(new FileHandle(mapFolderPath + "/mainpix.png"));
		Pixmap bgPix = new Pixmap(new FileHandle(mapFolderPath + "/bgpix.png"));
		Pixmap fgPix = new Pixmap(new FileHandle(mapFolderPath + "/fgpix.png"));
		
		this.mapSections = new Array<Rectangle>();
		this.respawnPoints = new HashMap<Rectangle, Vector2>();
		
		this.mapLayout = new Array<Array<Integer>>();
		this.mapEntities = new Array<Array<Integer>>();
		this.mapEtc = new Array<Array<Integer>>();
		this.mapTiles = new Array<Array<Tile>>();
		this.bgTiles = new Array<Array<Array<BackgroundTile>>>();
		this.fgTiles = new Array<Array<Array<ForegroundTile>>>();
		this.bgLayout = new Array<Array<Array<Integer>>>();
		this.fgLayout = new Array<Array<Array<Integer>>>();

		getMapData(mainPix, bgPix, fgPix);
		
		this.outerMapRects = new Rectangle[]
				{new Rectangle(0-64, 0-64, 64, height*Refs.TEXTURE_SIZE), //left
				 new Rectangle(0-64, 0-64, width*Refs.TEXTURE_SIZE, 64), //bottom
				 new Rectangle(width*Refs.TEXTURE_SIZE, 0-64, 64, height*Refs.TEXTURE_SIZE), // right
				 new Rectangle(0-64, height*Refs.TEXTURE_SIZE, width*Refs.TEXTURE_SIZE, 64)};//top
		
		this.playerSpawn = new Vector2();
		
		setPlayerSpawn();
		
		backgroundTexture = new Texture("./assets/textures/levels/level_skeleton.png");
		mainPix.dispose();
	}
	
	private void getMapSections() throws FileNotFoundException {
		Scanner sc = new Scanner(new File(mapSectionsJsonPath));
		while(sc.hasNextLine()) {
			jsonFileContents += sc.nextLine();
		}
		sc.close();
		
		JSONParser parser = new JSONParser();
		try {
			JSONArray arr = (JSONArray) parser.parse(jsonFileContents);
			for(int i = 0; i < arr.size(); i++) {
				JSONObject obj = (JSONObject) arr.get(i);
				System.out.println(obj.toJSONString());
				JSONArray f = (JSONArray) obj.get("fromCoords");
				JSONArray t = (JSONArray) obj.get("toCoords");
				double fX = (double) f.get(0);
				double fY = (double) f.get(1);
				double tX = (double) t.get(0);
				double tY = (double) t.get(1);
				float fromX = (float) fX;
				float fromY = (float) fY;
				float toX = (float) tX;
				float toY = (float) tY;
				
				mapSections.add(new Rectangle(fromX*Refs.TEXTURE_SIZE, fromY*Refs.TEXTURE_SIZE,
						(toX-fromX)*Refs.TEXTURE_SIZE, (toY-fromY)*Refs.TEXTURE_SIZE));
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	private void getMapData(Pixmap mainPix, Pixmap bgPix, Pixmap fgPix) throws FileNotFoundException {
		
		getMapSections();
		
		this.width = mainPix.getWidth();
		this.height = mainPix.getHeight();
		
		prepareArrays();
		
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				//get coords of the block
				int x = j, y = i;
				
				//get tiles
				int tileId = (int)(new Color(mainPix.getPixel(x, y)).r*255);
				
				//add texture ID to the array
				mapLayout.get(height - 1 - y).insert(x, tileId);
				
				switch(tileId) {
				case 1:
					mapTiles.get(height - 1 - y).insert(x, new Stone());
					break;
				case 2:
					mapTiles.get(height - 1 - y).insert(x, new OffTile());
					break;
				case 3:
					mapTiles.get(height - 1 - y).insert(x, new OnTile());
					break;
				case 4:
					mapTiles.get(height - 1 - y).insert(x, new GravitySwapTile());
					break;
				case 5:
					mapTiles.get(height - 1 - y).insert(x, new Platform());
					break;
				case 6:
					mapTiles.get(height - 1 - y).insert(x, new Grass());
					break;
				case 7:
					mapTiles.get(height - 1 - y).insert(x, new RedTile());
					break;
				case 8:
					mapTiles.get(height - 1 - y).insert(x, new GreenTile());
					break;
				case 9:
					mapTiles.get(height - 1 - y).insert(x, new GravitySwapDown());
					break;
				case 10:
					mapTiles.get(height - 1 - y).insert(x, new Spike(0));
					break;
				case 11:
					mapTiles.get(height - 1 - y).insert(x, new Spike(1));
					break;
				case 12:
					mapTiles.get(height - 1 - y).insert(x, new Spike(2));
					break;
				case 13:
					mapTiles.get(height - 1 - y).insert(x, new Spike(3));
					break;
				}
				
				if(mapTiles.get(height - 1 - y).get(x) instanceof Platform) {
					platformRects.add(new Rectangle(x * Refs.TEXTURE_SIZE, (y + 0.75f) * Refs.TEXTURE_SIZE, Refs.TEXTURE_SIZE, Refs.PLATFORM_THICKNESS));
				}
				
				if(mapTiles.get(height-1-y).get(x) != null) {
					mapTiles.get(height-1-y).get(x).x = x;
					mapTiles.get(height-1-y).get(x).y = height-1-y;
				}
				
				//get entities
				int entityId = (int)(new Color(mainPix.getPixel(x, y)).g*255);
				mapEntities.get(mapEntities.size-1-y).insert(x, entityId);
				
				
				// get etc data
				int etcId = (int)(new Color(mainPix.getPixel(x, y)).b*255);
				
				mapEtc.get(mapEtc.size-1-y).insert(x, etcId);
				
				if(etcId == 1) {
					for(Rectangle sect : mapSections) {
						if(new Rectangle(x*Refs.TEXTURE_SIZE, y*Refs.TEXTURE_SIZE, 16, 16)
								.overlaps(sect)) {
							respawnPoints.put(sect, new Vector2(x*Refs.TEXTURE_SIZE, y*Refs.TEXTURE_SIZE));
						}
					}
					System.out.println(respawnPoints);
				}
				//get bg/fg stuff
				for(int k = 0; k < 3; k++) {
					int bgId = 0;
					int fgId = 0;
					switch(k) {
						case 0:
							bgId = (int)(new Color(bgPix.getPixel(x, y)).r*255);
							fgId = (int)(new Color(fgPix.getPixel(x, y)).r*255);
							break;
						case 1:
							bgId = (int)(new Color(bgPix.getPixel(x, y)).g*255);
							fgId = (int)(new Color(fgPix.getPixel(x, y)).g*255);
							break;
						case 2:
							bgId = (int)(new Color(bgPix.getPixel(x, y)).b*255);
							fgId = (int)(new Color(fgPix.getPixel(x, y)).b*255);
							break;
					}
					//height-1-y because getPixel starts at the top left
					
					if(bgId == 1) {
						bgLayout.get(k).get(height - 1 - y).set(x, bgId);
						bgTiles.get(k).get(height - 1 - y).set(x, new Fence());
					}
				}
				
			}
		}
		
	}
	
	private void prepareArrays() {
		
		for(int i = 0; i < 3; i++) {
			bgTiles.add(new Array<Array<BackgroundTile>>());
			bgLayout.add(new Array<Array<Integer>>());
			fgTiles.add(new Array<Array<ForegroundTile>>());
			fgLayout.add(new Array<Array<Integer>>());
		}
		
		for(int i = 0; i < this.height; i++) { // set arrays to default values
			Array<Integer> addArr = new Array<Integer>();
			Array<Integer> addArrBg = new Array<Integer>();
			Array<Integer> addArrFg = new Array<Integer>();
			Array<String> addArrStr = new Array<String>();
			Array<Float> addArrFloat = new Array<Float>();
			Array<Tile> addArrTile = new Array<Tile>();
			Array<BackgroundTile> addArrBgTile = new Array<BackgroundTile>();
			Array<ForegroundTile> addArrFgTile = new Array<ForegroundTile>();
			
			mapEntities.add(new Array<Integer>());
			
			mapEtc.add(new Array<Integer>());
			
			for(int j = 0; j < width; j++) {
				addArr.add(0); //maplayout
				addArrStr.add(""); //tiletypes
				addArrFloat.add(0f); //tilerotations
				addArrTile.add(null); //maptiles
				addArrBgTile.add(null);
				addArrFgTile.add(null);
				addArrBg.add(0);
				addArrFg.add(0);
				
				mapEntities.get(i).add(0);
				mapEtc.get(i).add(0);
			}
			mapLayout.add(addArr);
			mapTiles.add(addArrTile);
			
			for(int k = 0; k < 3; k++) {
				bgTiles.get(k).add(addArrBgTile);
				bgLayout.get(k).add(addArrBg);
				fgTiles.get(k).add(addArrFgTile);
				fgLayout.get(k).add(addArrFg);
			}
			
			
			
			
		}
		
	}

	private void setPlayerSpawn() {
		boolean set = false;
		for(Array<Integer> i : mapEtc) {
			for(int j : i) { 
				if(j == 1) {
					playerSpawn = new Vector2(Refs.TEXTURE_SIZE * i.indexOf(j, false), Refs.TEXTURE_SIZE * (mapEtc.size - mapEtc.indexOf(i, false)));
					set = true;
				}
			}
		}
		if(!set)
			playerSpawn = new Vector2(Refs.TEXTURE_SIZE * 4, Refs.TEXTURE_SIZE * 4);
	}
	
	public void renderBackground() {
		SpriteBatch batch = new SpriteBatch();
		batch.begin();
		batch.draw(backgroundTexture, 0, 0);
		batch.end();
		batch.dispose();
	}
	
	public void resizeBackground(int width, int height) {
		Pixmap oldBg = new Pixmap(Gdx.files.internal(backgroundPath));
		Pixmap newBg = new Pixmap(width, height, oldBg.getFormat());
		newBg.drawPixmap(oldBg,
				0, 0, oldBg.getWidth(), oldBg.getHeight(),
				0, 0, width, height);
		
		backgroundTexture = new Texture(newBg);
		
		oldBg.dispose();
		newBg.dispose();
	}
}
	