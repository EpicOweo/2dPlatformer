package com.epicoweo.platformer.screens;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.epicoweo.platformer.PlatformerGame;
import com.epicoweo.platformer.debugtools.DebugOverlay;
import com.epicoweo.platformer.entities.Enemy;
import com.epicoweo.platformer.entities.Entity;
import com.epicoweo.platformer.entities.FlailEnemy;
import com.epicoweo.platformer.entities.Player;
import com.epicoweo.platformer.entities.projectiles.Projectile;
import com.epicoweo.platformer.etc.PolyUtils;
import com.epicoweo.platformer.etc.Refs;
import com.epicoweo.platformer.maps.JsonMap;
import com.epicoweo.platformer.maps.Level1;

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
	
	public GameScreen(final PlatformerGame game) {
		this.game = game;
		this.rects = new Array<Rectangle>();
		this.mapRects = new Array<Rectangle>();
		this.mapPolys = new Array<Polygon>();
		this.enemies = new Array<Enemy>();
		this.fullTextures = new Array<Texture>();
		this.slope45Textures = new Array<Texture>();
		this.projectiles = new Array<Projectile>();
		
		setupCamera();
		loadTextures();
		try {
			this.map = new Level1();
		} catch (IOException e) {
			e.printStackTrace();
		}
		loadMap();
		createPlayer(map.playerSpawn.x, map.playerSpawn.y, 20, 32, map);
		loadEntities();
		Refs.updateEntities(player, projectiles, enemies);
	}
	
	void setupCamera() {
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Refs.APP_LENGTH / 2, Refs.APP_WIDTH / 2);
		camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
		camera.update();
		Refs.camera = camera;
	}
	
	void loadTextures() {
		this.fullTextures.add(new Texture("../assets/textures/tiles/stone.png"));
		this.fullTextures.add(new Texture("../assets/textures/tiles/grass.png"));
		this.fullTextures.add(new Texture("../assets/textures/tiles/dirt.png"));
		
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
						mapRects.add(new Rectangle(i * Refs.TEXTURE_SIZE, (map.height - 1 - j) * Refs.TEXTURE_SIZE, Refs.TEXTURE_SIZE, Refs.TEXTURE_SIZE));
						game.batch.draw(fullTextures.get(map.mapLayout.get(j).get(i)-1), i * Refs.TEXTURE_SIZE, (map.height - 1 - j) * Refs.TEXTURE_SIZE);
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
					}
					
				}
			}
		}
		game.batch.end();
		
	}
	
	void moveCamera() {
		float newCameraX = player.getRect().x + player.getRect().width / 2;
		float newCameraY = camera.position.y;
		float playerCenterY = (float)(player.getRect().y + 0.5 * player.getRect().height) + 33;
		
		// if player is in the upper half of the screen
		if (newCameraY < playerCenterY) {
			newCameraY = playerCenterY;
		} else if (newCameraY > playerCenterY) {
			newCameraY = playerCenterY;
		}
		
		camera.position.lerp(new Vector3(newCameraX, newCameraY, 0), 0.1f);
	}
	
	public static void createPlayer(float x, float y, int width, int height, JsonMap map2) {
		player = new Player(x, y, width, height, map2);
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
		game.batch.draw(player.texture, playerRect.x, playerRect.y);
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
