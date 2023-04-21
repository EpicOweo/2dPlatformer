package com.epicoweo.platformer.screens;

import java.io.IOException;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.epicoweo.platformer.PlatformerGame;
import com.epicoweo.platformer.controller.OweoControllerListener;
import com.epicoweo.platformer.controller.UniversalInput;
import com.epicoweo.platformer.controller.UniversalInput.ControllerType;
import com.epicoweo.platformer.debugtools.DebugOverlay;
import com.epicoweo.platformer.entities.BlackHole;
import com.epicoweo.platformer.entities.Enemy;
import com.epicoweo.platformer.entities.Entity;
import com.epicoweo.platformer.entities.MechaSuitPickup;
import com.epicoweo.platformer.entities.Player;
import com.epicoweo.platformer.entities.projectiles.Projectile;
import com.epicoweo.platformer.etc.Assets;
import com.epicoweo.platformer.etc.CameraController;
import com.epicoweo.platformer.etc.Log;
import com.epicoweo.platformer.etc.Log.MessageType;
import com.epicoweo.platformer.etc.Refs;
import com.epicoweo.platformer.etc.Utils;
import com.epicoweo.platformer.items.weapons.EmptyWeapon;
import com.epicoweo.platformer.items.weapons.Pistol;
import com.epicoweo.platformer.maps.PNGMap;
import com.epicoweo.platformer.tiles.BackgroundTile;
import com.epicoweo.platformer.tiles.ForegroundTile;
import com.epicoweo.platformer.tiles.GreenTile;
import com.epicoweo.platformer.tiles.NormalTile;
import com.epicoweo.platformer.tiles.OffTile;
import com.epicoweo.platformer.tiles.Platform;
import com.epicoweo.platformer.tiles.RedTile;
import com.epicoweo.platformer.tiles.Spike;
import com.epicoweo.platformer.tiles.Sprites;
import com.epicoweo.platformer.tiles.Tile;
import com.epicoweo.platformer.tiles.Tile.TileType;
import com.epicoweo.platformer.tiles.Tileable;
import com.epicoweo.platformer.ui.HUD;

public class GameScreen implements Screen {
	
	public static PlatformerGame game = null;
	OrthographicCamera camera;
	SpriteBatch hudBatch;
	Array<Rectangle> rects;
	public Array<Rectangle> mapRects;
	public Array<Polygon> mapPolys;
	Array<Projectile> projectiles;
	Array<Enemy> enemies;
	Array<Entity> otherEntities;
	public static Player player;
	//JsonMap map;
	public static PNGMap map;
	boolean showVectors = false;
	boolean showHitboxes = false;
	public static long timestamp = 0;
	public float stateTime = 0f;
	
	public static boolean debugMode = false;
	
	static int iFrames = 100;
	public static int iFrameCounter = 0;
	
	Music background;
	
	HUD hud;
	
	TextureRegion[][] tileRegions;
	
	boolean doMapSections = true;
	private TextureRegion filterTexture;
	
	public static Controller controller = null;
	public static ControllerType controllerType;
	
	public static CameraController cc;
	
	public GameScreen(final PlatformerGame game) {
		Log.print(MessageType.INFO, "Instantiating game objects...");
		
		GameScreen.game = game;
		this.rects = new Array<Rectangle>();
		this.mapRects = new Array<Rectangle>();
		this.mapPolys = new Array<Polygon>();
		this.enemies = new Array<Enemy>();
		this.otherEntities = new Array<Entity>();
		this.projectiles = new Array<Projectile>();
		this.hudBatch = new SpriteBatch();
		
		Refs.game = game;
		
		Log.print(MessageType.INFO, "Setting up camera...");
		setupCamera();
		
		background = Gdx.audio.newMusic(Gdx.files.internal("./assets/music/background.ogg"));
		background.setLooping(true);
		background.setVolume(0.2f);
		background.play();
		
		Log.print(MessageType.INFO, "Loading textures...");
		loadTextures();
		Refs.updateTiles();
		try {
			//instantiate level
			//this.map = new Level1();
			GameScreen.map = new PNGMap("./assets/levels/map20");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Log.print(MessageType.INFO, "Loading map...");
		loadMap();
		
		if(!doMapSections) {
			map.mapSections.clear();
		}
		
		Log.print(MessageType.INFO, "Instantiating player...");
		createPlayer(map.playerSpawn.x, map.playerSpawn.y, 10, 16, map);
		
		Log.print(MessageType.INFO, "Loading entities...");
		map.mapEntities.get(81).set(92, 1); // make a mecha suit pickup
		loadEntities();
		
		Refs.updateEntities(player, projectiles, enemies, otherEntities);
		
		Log.print(MessageType.INFO, "Assets successfully loaded.");
		
		Controllers.addListener(new OweoControllerListener());
		
		if(Controllers.getControllers().size == 0) {

	        Log.print(MessageType.INFO, "Could not find a controller.");

	    } else {

	        GameScreen.controller = Controllers.getControllers().first();
	        Log.print(MessageType.INFO, "Using controller: " + controller.getName());
	        
	        if(controller.getName().toLowerCase().matches("switch")) {
	        	controllerType = ControllerType.SWITCH;
	        } else if(controller.getName().toLowerCase().matches("(ps4|playstation 4|playstation[(]tm[)] 4)")) {
	        	controllerType = ControllerType.PS4;
	        } else if(controller.getName().toLowerCase().matches("xbox")) {
	        	controllerType = ControllerType.XBONE;
	        }
	        
	    }
		
		hud = new HUD(new SpriteBatch());
		
		cc = new CameraController(camera);
		
	}
	
	void setupCamera() {
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Refs.APP_LENGTH / 4, Refs.APP_WIDTH / 4);
		camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
		camera.update();
		Refs.camera = camera;
	}
	
	void loadTextures() {
		
		game.manager.load(new AssetDescriptor<Texture>(Assets.SPRITESHEET, Texture.class));
		game.manager.load(new AssetDescriptor<Texture>(Assets.NULL, Texture.class));
		game.manager.load(new AssetDescriptor<Texture>(Assets.FILTER, Texture.class));
		game.manager.load(new AssetDescriptor<Texture>(Assets.PLAYER_GLOW, Texture.class));
		game.manager.load(new AssetDescriptor<Texture>(Assets.BLACK, Texture.class));
		game.manager.finishLoading();
		
		tileRegions = new TextureRegion((Texture)game.manager.get(Assets.SPRITESHEET)).split(16, 16);
		new Sprites(tileRegions);
		filterTexture = new TextureRegion((Texture)game.manager.get(Assets.FILTER));
		
	}
	
	void loadEntities() {
		for(int i = 0; i < map.height; i++) {
			for(int j = 0; j < map.width; j++) {
				if (map.mapEntities.get(i).get(j) == 1) {
					otherEntities.add(new MechaSuitPickup(Refs.TEXTURE_SIZE * (j),Refs.TEXTURE_SIZE *(map.mapEntities.size-i-1), map));
				}
			}
		}
		
	}
	
	void loadMap() {
		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		mapPolys.clear();
		for(int i = 0; i < map.width; i++) {
			for(int j = 0; j < map.height; j++) {
				if (map.mapLayout.get(j).get(i) >= 1) {
					Rectangle mapRect = new Rectangle();
					
					if (map.mapTiles.get(j).get(i).type != TileType.Background 
							&& map.mapTiles.get(j).get(i).type != TileType.Platform) {
						mapRect = new Rectangle(i * Refs.TEXTURE_SIZE, (map.height - 1 - j)
								* Refs.TEXTURE_SIZE, Refs.TEXTURE_SIZE, Refs.TEXTURE_SIZE);
					} else if(map.mapTiles.get(j).get(i).type == TileType.Platform) {
						//mapRect = new Rectangle(i * Refs.TEXTURE_SIZE, (map.height - 1 - j + 0.75f)
								//* Refs.TEXTURE_SIZE, Refs.TEXTURE_SIZE, Refs.PLATFORM_THICKNESS);
						mapRect = new Rectangle(i * Refs.TEXTURE_SIZE, (map.height - 1 - j)
								* Refs.TEXTURE_SIZE, Refs.TEXTURE_SIZE, Refs.TEXTURE_SIZE);
					}
						
					mapRects.add(mapRect);
					
					//game.batch.draw(fullTextures.get(map.mapLayout.get(j).get(i)-1), i * Refs.TEXTURE_SIZE, (map.height - 1 - j) * Refs.TEXTURE_SIZE);
					
					Tile tile = map.mapTiles.get(j).get(i);
					
					
					float x = mapRect.getX();
					float y = mapRect.getY();
					
					if(tile.isAnimated) {
						tile.stateTime += Gdx.graphics.getDeltaTime();
						TextureRegion currentFrame = tile.animation.getKeyFrame(tile.stateTime, true);
						game.batch.draw(currentFrame, x, y);
						continue;
					}
					
					Sprite sprite;
					Tile currentTile;
					
					
					
					try { 
						currentTile = map.mapTiles.get(j).get(i);
					} catch(IndexOutOfBoundsException e) {
						continue;
					}
					
					if(currentTile.isVaried && !(currentTile instanceof Platform)) {
						drawVariedSprite(currentTile, i, j);
					} else if(currentTile instanceof Platform) {
						drawPlatform(currentTile, i, j);
					} else if(currentTile instanceof GreenTile) {
						if(GreenTile.activated) sprite = new Sprite(currentTile.textures.get("on"));
						else sprite = new Sprite(currentTile.textures.get("off"));
						game.batch.draw(sprite, x, y);
					} else if(currentTile instanceof RedTile) {
						if(RedTile.activated) sprite = new Sprite(currentTile.textures.get("on"));
						else sprite = new Sprite(currentTile.textures.get("off"));
						game.batch.draw(sprite, x, y);
					} else if(currentTile instanceof Spike) {
						switch(((Spike)currentTile).spikeType) {
						case 0:
							sprite = new Sprite(currentTile.textures.get("up"));
							break;
						case 1:
							sprite = new Sprite(currentTile.textures.get("down"));
							break;
						case 2:
							sprite = new Sprite(currentTile.textures.get("left"));
							break;
						case 3:
							sprite = new Sprite(currentTile.textures.get("right"));
							break;
						default:
							sprite = null;
						}
						
						game.batch.draw(sprite, x, y);
						
					} else {
						sprite = new Sprite(currentTile.texture);
						game.batch.draw(sprite, x, y);
					}
					
					if(player != null) {
						if(!mapRect.overlaps(player.sectIn)) { // make everything outside of current sect dark
							drawBlack(x, y);
							continue;
						}
					}
					
				}
			}
		}
		
		game.batch.end();
		
	}
	

	private void drawBlack(float x, float y) {
		TextureRegion trBlack = new TextureRegion(game.manager.get(Assets.BLACK),
				Refs.TEXTURE_SIZE, Refs.TEXTURE_SIZE);
		Sprite sprite = new Sprite(trBlack);
		game.batch.setColor(1f, 1f, 1f, 0.5f);
		game.batch.draw(sprite, x, y);
		game.batch.setColor(1f, 1f, 1f, 1f);
		
	}

	public static void respawnPlayer() {
		Rectangle sect = new Rectangle(player.sectIn.x, player.sectIn.y, player.sectIn.width, player.sectIn.height);
		System.out.println(sect);
		int playerWidth = (int)player.getRect().width;
		boolean mecha = player.inMechaSuit;
		GameScreen.player = null;
		
		Vector2 respawnPt = map.respawnPoints.get(sect);
		
		
		GameScreen.createPlayer(respawnPt.x, respawnPt.y, playerWidth, 16, GameScreen.map);
		player.inMechaSuit = mecha;
		if(mecha) {
			player.equipWeapon(new Pistol(player));
		}
	}
	
	public static void createPlayer(float x, float y, int width, int height, PNGMap map) {
		player = new Player(x, y, width, height, map);
		if(cc != null) {
			GameScreen.cc.update();
		}
	}
	
	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	public void tick(float delta) {
		camera.update();
		player.update(delta);
		UniversalInput.updateInputs();
		cc.update();
		
		for(Enemy e : enemies) {
			e.update(delta);
		}
		for(Entity e : otherEntities) {
			e.update(delta);
		}		
		stateTime += delta;
		
		if(iFrameCounter >= iFrames) {
			iFrameCounter = 0;
		}
		
		if(iFrameCounter >= 1) {
			iFrameCounter++;
		}
		
		if(player.dead) {
			iFrameCounter++;
		}
		
	}
	
	@Override
	public void render(float delta) {
		tick(delta);
		ScreenUtils.clear(0, 0, 0, 1);
		drawBg();
		loadMap();
		
		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		for(Entity e : Refs.entities) {
			if(e.hasAnimation) {
				TextureRegion currentFrame = e.animation.getKeyFrame(stateTime, false);
				game.batch.draw(currentFrame, e.getRect().x, e.getRect().y);
			} else {
				Texture eTexture = e.texture;
				
				if(e.texture == null) {
					eTexture = game.manager.get(Assets.NULL);
				}
				
				if(e instanceof Projectile) {
					continue;
				} else if (e instanceof Player) {
					continue;
					
				} else if(e instanceof BlackHole) {
					BlackHole bh = ((BlackHole) e);
					
					Circle bhCircle = bh.getCircle();
					
					game.batch.draw(eTexture, bhCircle.x - bhCircle.radius, bhCircle.y - bhCircle.radius);
					
					game.batch.end();
					game.renderer.begin(ShapeType.Line);
					game.renderer.circle(bh.pullCircle.x, bh.pullCircle.y, bh.pullCircle.radius);
					game.renderer.end();
					game.batch.begin();
					
					
				} else {
					
				}
			}
			
		}
		game.batch.end();
		
		game.renderer.setProjectionMatrix(camera.combined);
		game.renderer.setAutoShapeType(true);
		game.renderer.begin();
		game.renderer.set(ShapeType.Filled);
		game.renderer.setColor(Color.WHITE);
		for(Rectangle rect : rects) {
			game.renderer.rect(rect.x, rect.y, rect.width, rect.height);
		}
		
		if(player.weapon != null) {
			if(player.weapon.currentProjectiles.notEmpty()) {
				for(Projectile proj : player.weapon.currentProjectiles) {
					if(proj.remove) {
						player.weapon.currentProjectiles.removeValue(proj, false);
					}
					game.renderer.rect(proj.getRect().x, proj.getRect().y, proj.getRect().width, proj.getRect().height);
					proj.update(delta);
				}
			}
		}
		
		for(Enemy e : enemies) {
			if(e.remove) {
				enemies.removeValue(e, false);
			}
		}
		game.renderer.end(); //rects etc
		
		game.batch.begin();
		Rectangle playerRect = player.getRect();
		if(player.inverted == 1) { // either 1 or -1
			TextureRegion currentFrame = player.currentAnimation.getKeyFrame(stateTime, false);
			game.batch.draw(currentFrame, playerRect.x-3, playerRect.y);
			
		} else {
			TextureRegion currentFrame = player.currentAnimation.getKeyFrame(stateTime, false);
			game.batch.draw(currentFrame, playerRect.x-3, playerRect.y);
		}
		
		Rectangle pRect = player.getRect();
		
		if(!(player.weapon instanceof EmptyWeapon)) {
			float gunRotation;
			if(controller != null && UniversalInput.aimingGun() || controller == null) {
				gunRotation = UniversalInput.getCursorAngle();
				player.weapon.passiveRotation = gunRotation;
			}
			else gunRotation = player.weapon.passiveRotation;
			
			game.batch.draw(player.weapon.texture, pRect.x + pRect.width + pRect.height/2, pRect.y, //x, y
					-pRect.height/2-pRect.width/2, pRect.height/2, //originX, originY
					player.weapon.texture.getRegionWidth(), player.weapon.texture.getRegionHeight(), //width, height
					1, 1, //scaleX, scaleY
					gunRotation //rotation
					);
		}
		
		game.batch.end();
		drawFg();
		
		//finally, apply filter
		applyGlow();
		applyFilter();
		
		if(debugMode) {
			DebugOverlay debugOverlay = new DebugOverlay(game.batch, PlatformerGame.font, delta, showVectors, showHitboxes);
			debugOverlay.render();
		}
		
		
		projectiles = player.weapon.currentProjectiles;
		Refs.updateEntities(player, projectiles, enemies, otherEntities);
		Refs.updateUtils(game, game.batch, game.renderer, camera);
		
		//game.batch.setProjectionMatrix(camera.combined);
		//game.batch.begin();
		//game.batch.end(); //textures
		
		hud.render();
		
		
		if(Gdx.input.isKeyJustPressed(Input.Keys.V)) {
			showVectors = !showVectors;
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.H)) {
			showHitboxes = !showHitboxes;
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
			game.setScreen(game.debugScreen);
		}

		GameScreen.timestamp = TimeUtils.millis();
		
	}
	
	private void drawBg() {
		map.renderBackground(); // draw background image
		game.batch.begin();
		for(int i = 0; i < map.width; i++) { 
			for(int j = 0; j < map.height; j++) {
				for(int k = 0; k < 3; k++) {
					Tile currentTile = (Tile)map.bgTiles.get(k).get(j).get(i);
					if(map.bgTiles.get(k).get(j).get(i) == null) continue;
					if(currentTile.isVaried) {
						drawVariedSprite(currentTile, i, j, k);
						continue;
					}
						
					game.batch.draw(map.bgTiles.get(k).get(j).get(i).texture,
							i * Refs.TEXTURE_SIZE, (map.height - 1 - j) * Refs.TEXTURE_SIZE);
				}
			}
		}
		game.batch.end();
		
	}
	
	private void drawFg() {
		game.batch.begin();
		for(int i = 0; i < map.fgTiles.get(0).size; i++) { 
			for(int j = 0; j < map.fgTiles.get(0).get(0).size; j++) {
				for(int k = 0; k < 3; k++) {
					Tile currentTile = (Tile)map.fgTiles.get(k).get(j).get(i);
					if(map.fgTiles.get(k).get(j).get(i) == null) continue;
					if(currentTile.isVaried) {
						drawVariedSprite(currentTile, i, j, k);
						continue;
					}
						
					game.batch.draw(map.fgTiles.get(k).get(j).get(i).texture,
							j * Refs.TEXTURE_SIZE, (map.height - 1 - i) * Refs.TEXTURE_SIZE);
				}
			}
		}
		game.batch.end();
		
	}
	
	private void applyFilter() {
		hudBatch.begin();
		hudBatch.draw(filterTexture, 0, 0);
		hudBatch.end();
	}
	
	private void applyGlow() {
		//player center
		Vector2 playerCenter = new Vector2();
		player.getRect().getCenter(playerCenter);
		
		TextureRegion pGlow = new TextureRegion(game.manager.get(Assets.PLAYER_GLOW), 128, 128);
		
		game.batch.begin();
		game.batch.draw(pGlow, playerCenter.x-pGlow.getRegionWidth()/2,
				playerCenter.y-pGlow.getRegionHeight()/2);
		game.batch.end();
	}

	private void drawVariedSprite(Tile currentTile, int i, int j) {
		drawVariedSprite(currentTile, i, j, 0);
	}
	
	private void drawVariedSprite(Tile currentTile, int i, int j, int index) {
		//if(!(currentTile instanceof BackgroundTile)) return;
		
		boolean[] connects = {false, false, false, false}; //t, b, l, r
		boolean[] connectsCorners = {false, false, false, false}; //tl, tr, bl, br
		
		int id = 0;
		Array<Array<Integer>> workingGrid = null;
		if(currentTile instanceof NormalTile) {
			workingGrid = map.mapLayout;
			id = workingGrid.get(j).get(i);
		} else if(currentTile instanceof BackgroundTile) {
			workingGrid = map.bgLayout.get(index);
			id = workingGrid.get(j).get(i);
		} else if(currentTile instanceof ForegroundTile) {
			workingGrid = map.fgLayout.get(index);
		}

		@SuppressWarnings("unchecked")
		HashMap<String, TextureRegion> possibleTextures = (HashMap<String, TextureRegion>) currentTile.textures.clone();
		
		boolean canT = j-1 >= 0;
		boolean canB = j+1 < workingGrid.size;
		boolean canL = i-1 >= 0;
		boolean canR = i+1 < workingGrid.get(j).size;
		
		//make sure no index out of bounds:
		int top = canT ? workingGrid.get(j-1).get(i) : 0;
		int bottom = canB ? workingGrid.get(j+1).get(i) : 0;
		int left = canL ? workingGrid.get(j).get(i-1) : 0;
		int right = canR ? workingGrid.get(j).get(i+1) : 0;
		int bl = canB && canL ? workingGrid.get(j+1).get(i-1) : 0;
		int tl = canT && canL ? workingGrid.get(j-1).get(i-1) : 0;
		int br = canB && canR ? workingGrid.get(j+1).get(i+1) : 0;
		int tr = canT && canR ? workingGrid.get(j-1).get(i+1) : 0;
		
		// allow tiling with other tileable objects
		if(workingGrid == map.mapLayout && map.mapTiles.get(j).get(i) instanceof Tileable) {
			if(canT && map.mapTiles.get(j-1).get(i) instanceof Tileable) top = id;
			if(canB && map.mapTiles.get(j+1).get(i) instanceof Tileable) bottom = id;
			if(canL && map.mapTiles.get(j).get(i-1) instanceof Tileable) left = id;
			if(canR && map.mapTiles.get(j).get(i+1) instanceof Tileable) right = id;
			if(canT && canL && map.mapTiles.get(j-1).get(i-1) instanceof Tileable) tl = id;
			if(canB && canL && map.mapTiles.get(j+1).get(i-1) instanceof Tileable) bl = id;
			if(canB && canR && map.mapTiles.get(j+1).get(i+1) instanceof Tileable) br = id;
			if(canT && canR && map.mapTiles.get(j-1).get(i+1) instanceof Tileable) tr = id;
		}
		
		boolean edge = top != id || bottom != id || left != id || right != id;
		
		String verdict = "full";
		
		//check top, bottom, left, right
		//top
		if(top == id) connects[0] = true;
		if(bottom == id) connects[1] = true;
		if(left == id) connects[2] = true;
		if(right == id) connects[3] = true;
		if(tr == id) connectsCorners[0] = true;
		if(br == id) connectsCorners[1] = true;
		if(tl == id) connectsCorners[2] = true;
		if(bl == id) connectsCorners[3] = true;
		
		int counter = 0;
		int cornerCounter = 0;
		for(int k = 0; k < 4; k++) {
			if(connects[k]) counter++;
			if(connectsCorners[k]) cornerCounter++;
		}
		
		if(edge) {
			possibleTextures.remove("itr");
			possibleTextures.remove("itl");
			possibleTextures.remove("ibr");
			possibleTextures.remove("ibl");
			possibleTextures.remove("cc");
			
			if(counter <= 1) {
				verdict = "full";
			} else if(counter == 2) { //outside corner
				if(!connects[0] && !connects[2]) { //t, l
					verdict = "tl";
				} else if(!connects[1] && !connects[2]) { //b, l
					verdict = "bl";
				} else if(!connects[0] && !connects[3]) { //t, r
					verdict = "tr";
				} else if (!connects[1] && !connects[3]) { //b, r
					verdict = "br";
				} else {
					//System.out.println("somethin went quite wrong in corners");
				}
			} else { // edge
				if(!connects[0]) { //t
					verdict = "tc";
				} else if(!connects[1]) { //b
					verdict = "bc";
				} else if(!connects[2]) { //l
					verdict = "cl";
				} else if (!connects[3]) { //r
					verdict = "cr";
				} else {
					//System.out.println("somethin went quite wrong in edges");
				}
			}
		} else {
			possibleTextures.remove("tl");
			possibleTextures.remove("tc");
			possibleTextures.remove("tr");
			possibleTextures.remove("cl");
			possibleTextures.remove("cr");
			possibleTextures.remove("bl");
			possibleTextures.remove("bc");
			possibleTextures.remove("br");
			//possibleTextures.remove("full");
			
			if(cornerCounter == 4) {
				verdict = "cc";
			}
			// inside corner
			else if(!connectsCorners[0]) { //t, r
				verdict = "itr";
			} else if(!connectsCorners[1]) { //b, r
				verdict = "ibr";
			} else if(!connectsCorners[2]) { //t, l
				verdict = "itl";
			} else if (!connectsCorners[3]) { //b, l
				verdict = "ibl";
			} else {
				//System.out.println("somethin went quite wrong in inside corners");
			}
			
		}
		Sprite sprite;
		try {
			sprite = new Sprite(possibleTextures.get(verdict));
		} catch(NullPointerException e) {
			sprite = new Sprite(currentTile.textures.get("cc"));
		}
		
		sprite.setPosition(i*Refs.TEXTURE_SIZE, (map.height-j-1)*Refs.TEXTURE_SIZE);
		sprite.draw(game.batch);
		
	}
	
	private void drawPlatform(Tile currentTile, int i, int j) {
		boolean[] connects = {false, false}; //l, r -> connects to something that's air or a platform
		
		Array<Array<Integer>> workingGrid = map.mapLayout;
		int id = workingGrid.get(j).get(i);
		
		@SuppressWarnings("unchecked")
		HashMap<String, TextureRegion> possibleTextures = (HashMap<String, TextureRegion>) currentTile.textures.clone();
		
		boolean canL = i-1 >= 0;
		boolean canR = i+1 < workingGrid.get(j).size;
		
		int left = canL ? workingGrid.get(j).get(i-1) : 0;
		int right = canR ? workingGrid.get(j).get(i+1) : 0;
		
		String verdict = "c";
		
		if(left == id || left == 0) connects[0] = true;
		if(right == id || right == 0) connects[1] = true;
		
		if(connects[0] && connects[1]) verdict = "c";
		else if(connects[0]) verdict = "r";
		else if(connects[1]) verdict = "l";
		else verdict = "c";
		
		
		Sprite sprite = new Sprite(possibleTextures.get(verdict));
		
		sprite.setPosition(i*Refs.TEXTURE_SIZE, (map.height-j-1)*Refs.TEXTURE_SIZE);
		sprite.draw(game.batch);
	}
	
	@Override
	public void resize(int width, int height) {
		map.resizeBackground(width, height);
		if(width < 1280 && height > 720) {
			filterTexture = new TextureRegion(Utils.resizeTexture(filterTexture.getTexture(), width, height));
		}
		
		Refs.APP_LENGTH = width;
		Refs.APP_WIDTH = height;
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {

	}

}
