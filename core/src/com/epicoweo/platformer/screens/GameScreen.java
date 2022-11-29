package com.epicoweo.platformer.screens;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.epicoweo.platformer.PlatformerGame;
import com.epicoweo.platformer.debugtools.DebugOverlay;
import com.epicoweo.platformer.entities.Enemy;
import com.epicoweo.platformer.entities.Entity;
import com.epicoweo.platformer.entities.FlailEnemy;
import com.epicoweo.platformer.entities.Player;
import com.epicoweo.platformer.entities.projectiles.Projectile;
import com.epicoweo.platformer.etc.Log;
import com.epicoweo.platformer.etc.Log.MessageType;
import com.epicoweo.platformer.etc.PolyUtils;
import com.epicoweo.platformer.etc.Refs;
import com.epicoweo.platformer.maps.GravitySwitchLevel;
import com.epicoweo.platformer.maps.JsonMap;
import com.epicoweo.platformer.maps.Level1;
import com.epicoweo.platformer.tiles.Tile;
import com.epicoweo.platformer.tiles.Tile.TileType;

public class GameScreen implements Screen {
	
	final PlatformerGame game;
	OrthographicCamera camera;
	Array<Rectangle> rects;
	public Array<Rectangle> mapRects;
	public Array<Polygon> mapPolys;
	Array<Projectile> projectiles;
	Array<Enemy> enemies;
	Array<Texture> fullTextures;
	Array<Texture> slope45Textures;
	public static Player player;
	JsonMap map;
	boolean showVectors = false;
	boolean showHitboxes = false;
	long timestamp = 0;
	
	public GameScreen(final PlatformerGame game) {
		Log.print(MessageType.INFO, "Instantiating game objects...");
		
		this.game = game;
		this.rects = new Array<Rectangle>();
		this.mapRects = new Array<Rectangle>();
		this.mapPolys = new Array<Polygon>();
		this.enemies = new Array<Enemy>();
		this.fullTextures = new Array<Texture>();
		this.slope45Textures = new Array<Texture>();
		this.projectiles = new Array<Projectile>();
		
		Log.print(MessageType.INFO, "Setting up camera...");
		setupCamera();
		
		Log.print(MessageType.INFO, "Loading textures...");
		loadTextures();
		try {
			this.map = new Level1();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.print(MessageType.INFO, "Loading map...");
		loadMap();
		Log.print(MessageType.INFO, "Instantiating player...");
		createPlayer(map.playerSpawn.x, map.playerSpawn.y, 10, 16, map);
		Log.print(MessageType.INFO, "Loading entities...");
		loadEntities();
		Refs.updateEntities(player, projectiles, enemies);
		
		Log.print(MessageType.INFO, "Assets successfully loaded.");
	}
	
	void setupCamera() {
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Refs.APP_LENGTH / 4, Refs.APP_WIDTH / 4);
		camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
		camera.update();
		Refs.camera = camera;
	}
	
	void loadTextures() {
		this.fullTextures.add(new Texture("../assets/textures/tiles/stone.png"));
		this.fullTextures.add(new Texture("../assets/textures/tiles/grass.png"));
		this.fullTextures.add(new Texture("../assets/textures/tiles/dirt.png"));
		this.fullTextures.add(new Texture("../assets/textures/tiles/gravityswap.png"));
		this.fullTextures.add(new Texture("../assets/textures/tiles/speedboost_0.png"));
		this.fullTextures.add(new Texture("../assets/textures/tiles/platform_0.png"));
		this.fullTextures.add(new Texture("../assets/textures/tiles/platform_1.png"));
		this.fullTextures.add(new Texture("../assets/textures/tiles/platform_2.png"));
		
		
		this.slope45Textures.add(new Texture("../assets/textures/tiles/stone_45.png"));
	}
	
	void loadEntities() {
		for(int i = 0; i < map.width; i++) {
			for(int j = 0; j < map.height; j++) {
				if (map.mapEntities.get(j).get(i) == 1) {
					enemies.add(new FlailEnemy(new Vector2(Refs.TEXTURE_SIZE * (j+0.5f),Refs.TEXTURE_SIZE *(map.mapEntities.size - i+0.5f)), Refs.TEXTURE_SIZE, Refs.TEXTURE_SIZE, map));
				}
			}
		}
		
	}
	
	void loadMap() {
		map.renderBackground();
		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		mapPolys.clear();
		for(int i = 0; i < map.width; i++) {
			for(int j = 0; j < map.height; j++) {
				if (map.mapLayout.get(j).get(i) >= 1) {
					if (map.tileTypes.get(j).get(i).equals("full")) {
						Rectangle mapRect = new Rectangle(i * Refs.TEXTURE_SIZE, (map.height - 1 - j) * Refs.TEXTURE_SIZE, Refs.TEXTURE_SIZE, Refs.TEXTURE_SIZE);
						mapRects.add(mapRect);
						//game.batch.draw(fullTextures.get(map.mapLayout.get(j).get(i)-1), i * Refs.TEXTURE_SIZE, (map.height - 1 - j) * Refs.TEXTURE_SIZE);
						
						Tile tile = map.mapTiles.get(j).get(i);
						
						
						float x = mapRect.getX();
						float y = mapRect.getY();
						
						Polygon mapPoly = PolyUtils.rotateAboutCenter(PolyUtils.rectToPoly(mapRect), map.tileRotations.get(j).get(i));

						
						mapPolys.add(mapPoly);
						
						if(tile.isAnimated) {
							tile.stateTime += Gdx.graphics.getDeltaTime();
							TextureRegion currentFrame = tile.animation.getKeyFrame(tile.stateTime, true);
							game.batch.draw(currentFrame, x, y);
							continue;
						}
						
						Sprite sprite = new Sprite(fullTextures.get(map.mapLayout.get(j).get(i)-1));
						
						
						//tile.setRotation(map.tileRotations.get(j).get(i));
						
						sprite.setRotation(mapPoly.getRotation());
						if(sprite.getRotation() == 180) {
							sprite.flip(true, false);
						}
						sprite.setPosition(x, y);
						sprite.draw(game.batch);
						
						
					} else if (map.tileTypes.get(j).get(i).equals("slope45")) {
						Polygon tile = new Polygon(new float[] {
								(map.width - 1 - i) * Refs.TEXTURE_SIZE, (map.height - 1 - j) * Refs.TEXTURE_SIZE,
								(map.width - i) * Refs.TEXTURE_SIZE, (map.height - j) * Refs.TEXTURE_SIZE,
								(map.width - i) * Refs.TEXTURE_SIZE, (map.height - 1 - j) * Refs.TEXTURE_SIZE,
						});
						Sprite sprite = new Sprite(slope45Textures.get(map.mapLayout.get(j).get(i)-1));
						Rectangle boundingRect = tile.getBoundingRectangle();
						float x = boundingRect.getX();
						float y = boundingRect.getY();
						
						//tile.setRotation(map.tileRotations.get(j).get(i));
						
						tile = PolyUtils.rotateAboutCenter(tile, map.tileRotations.get(j).get(i));
						//tile.setPosition(x, y);
						
						mapPolys.add(tile);
						
						sprite.setRotation(tile.getRotation());
						sprite.setPosition((map.width - 1) * Refs.TEXTURE_SIZE - x, y);
						sprite.draw(game.batch);
					} else if (map.tileTypes.get(j).get(i).equals("platform")) {
						Rectangle mapRect = new Rectangle(i * Refs.TEXTURE_SIZE, (map.height - 1 - j + 0.75f) * Refs.TEXTURE_SIZE, Refs.TEXTURE_SIZE, Refs.PLATFORM_THICKNESS);
						mapRects.add(mapRect);
						//game.batch.draw(fullTextures.get(map.mapLayout.get(j).get(i)-1), i * Refs.TEXTURE_SIZE, (map.height - 1 - j) * Refs.TEXTURE_SIZE);
						
						Tile tile = map.mapTiles.get(j).get(i);
						
						
						float x = mapRect.getX();
						float y = mapRect.getY();
						
						Polygon mapPoly = PolyUtils.rotateAboutCenter(PolyUtils.rectToPoly(mapRect), map.tileRotations.get(j).get(i));

						
						mapPolys.add(mapPoly);
						
						if(tile.isAnimated) {
							tile.stateTime += Gdx.graphics.getDeltaTime();
							TextureRegion currentFrame = tile.animation.getKeyFrame(tile.stateTime, true);
							game.batch.draw(currentFrame, x, y);
							continue;
						}
						
						Sprite sprite = new Sprite(fullTextures.get(map.mapLayout.get(j).get(i)-1));
						
						
						//tile.setRotation(map.tileRotations.get(j).get(i));
						
						sprite.setRotation(mapPoly.getRotation());
						if(sprite.getRotation() == 180) {
							sprite.flip(true, false);
						}
						sprite.setPosition(x, y);
						sprite.draw(game.batch);
					}
					
				}
			}
		}
		game.batch.end();
		
	}
	
	void moveCamera() {
		
		float playerCenterX = (float)(player.getRect().x + 0.5 * player.getRect().width);
		float playerCenterY = (float)(player.getRect().y + 0.5 * player.getRect().height);
		
		if(map.cameraAttachPlayer) {
			camera.position.lerp(new Vector3(playerCenterX, playerCenterY, 0), 0.1f);
			return;
		}
		
		Rectangle mapSect = player.sectIn;
		
		Vector3 pRelativePos = new Vector3(player.getRect().x, player.getRect().y, 0);
		Vector3 pRelativePosWHeight = new Vector3(player.getRect().x, player.getRect().y + player.getRect().height, 0);
		
		camera.project(pRelativePos);
		camera.project(pRelativePosWHeight);
		
		float pRelativeCenterY = (pRelativePos.y + pRelativePosWHeight.y) / 2f;
		
		boolean higherThanScreen = mapSect.height > camera.viewportHeight;
		boolean playerInTopThird = pRelativeCenterY - (2*Gdx.graphics.getHeight() / 3) > 0;
		boolean playerInBottomThird = pRelativeCenterY - (2*Gdx.graphics.getHeight() / 3) < 0;
		
		System.out.println(pRelativeCenterY);
		
		float newCameraX = mapSect.x + mapSect.width / 2;
		float newCameraY = mapSect.y + mapSect.height / 2;
		
		System.out.println(playerInTopThird);
		
		if(higherThanScreen) {
			
			//if the top is on screen
			if(camera.position.y + camera.viewportHeight / 2 > mapSect.y + mapSect.height) {
				newCameraY = camera.position.y;
				if(playerInBottomThird) {
					newCameraY = playerCenterY;
				}
			}
			// if bottom is on screen
			else if (camera.position.y - camera.viewportHeight / 2 < mapSect.y) {
				newCameraY = camera.position.y;
				if(playerInTopThird) {
					newCameraY = playerCenterY;
				}
				
			} //if neither are on screen
			else {
				if(playerInTopThird) {
					newCameraY = playerCenterY;
				} else if(playerInBottomThird) {
					newCameraY = playerCenterY;
				} else {
					newCameraY = camera.position.y;
				}
				
			}
		}
		
		camera.position.lerp(new Vector3(newCameraX, newCameraY, 0), 0.1f);
	}
	
	public static void createPlayer(float x, float y, int width, int height, JsonMap map) {
		player = new Player(x, y, width, height, map);
	}
	
	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(float delta) {
		ScreenUtils.clear(0, 0, 0, 1);
		camera.update();
		loadMap();
		player.update(delta);
		for(Enemy e : enemies) {
			e.update(delta);
		}
		moveCamera();
		
		game.renderer.setProjectionMatrix(camera.combined);
		game.renderer.begin(ShapeType.Filled);
		game.renderer.setColor(Color.WHITE);
		for(Rectangle rect : rects) {
			game.renderer.rect(rect.x, rect.y, rect.width, rect.height);
		}
		
		for(Entity e : Refs.entities) {
			if(e instanceof Projectile) {
				continue;
			} else if (e instanceof Player) {
				continue;
			} else if (e.getCircle() != null) {
				game.renderer.circle(e.getCircle().x, e.getCircle().y, e.getCircle().radius);
			} else {
				game.renderer.rect(e.getRect().x, e.getRect().y, e.getRect().width, e.getRect().height);
			}
		}
		
		if(player.weapon.currentProjectiles.notEmpty()) {
			for(Projectile proj : player.weapon.currentProjectiles) {
				if(proj.remove) {
					player.weapon.currentProjectiles.removeValue(proj, false);
				}
				proj.update(delta);
				game.renderer.rect(proj.getRect().x, proj.getRect().y, proj.getRect().width, proj.getRect().height);
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
		if(player.inverted == 1)
			game.batch.draw(player.texture, playerRect.x-3, playerRect.y);
		else
			game.batch.draw(player.invertedTexture, playerRect.x-3, playerRect.y);
		game.batch.end();
		
		DebugOverlay debugOverlay = new DebugOverlay(game.batch, game.font, delta, showVectors, showHitboxes);
		debugOverlay.render();
		
		
		projectiles = player.weapon.currentProjectiles;
		Refs.updateEntities(player, projectiles, enemies);
		Refs.updateUtils(game, game.batch, game.renderer, camera);
		
		//game.batch.setProjectionMatrix(camera.combined);
		//game.batch.begin();
		//game.batch.end(); //textures
		
		
		
		if(Gdx.input.isKeyJustPressed(Input.Keys.V)) {
			showVectors = !showVectors;
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.H)) {
			showHitboxes = !showHitboxes;
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
			game.setScreen(game.debugScreen);
		}

		
	}
	
	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

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
		for(Texture t : fullTextures) {
			t.dispose();
		}
		for(Texture t : slope45Textures) {
			t.dispose();
		}

	}

}
