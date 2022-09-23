package com.epicoweo.platformer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.epicoweo.platformer.PlatformerGame;
import com.epicoweo.platformer.debugtools.DebugOverlay;
import com.epicoweo.platformer.entities.Enemy;
import com.epicoweo.platformer.entities.FlailEnemy;
import com.epicoweo.platformer.entities.Player;
import com.epicoweo.platformer.entities.projectiles.Projectile;
import com.epicoweo.platformer.etc.Refs;
import com.epicoweo.platformer.maps.Level1;
import com.epicoweo.platformer.maps.Map;

public class GameScreen implements Screen {
	
	final PlatformerGame game;
	OrthographicCamera camera;
	Array<Rectangle> rects;
	Array<Rectangle> mapRects;
	Array<Projectile> projectiles;
	Array<Enemy> enemies;
	Array<Texture> textures;
	static Player player;
	Map map;
	boolean showVectors = false;
	
	public GameScreen(final PlatformerGame game) {
		this.game = game;
		this.rects = new Array<Rectangle>();
		this.mapRects = new Array<Rectangle>();
		this.enemies = new Array<Enemy>();
		this.textures = new Array<Texture>();
		
		camera = new OrthographicCamera();
		//camera.setToOrtho(false, Refs.APP_LENGTH, Refs.APP_WIDTH);
		camera.setToOrtho(false, Refs.APP_LENGTH / 2, Refs.APP_WIDTH / 2);
		camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
		camera.update();
		Refs.camera = camera;
		
		loadTextures();
		this.map = new Level1();
		loadMap();
		createPlayer(map.playerSpawn.x, map.playerSpawn.y, Refs.TEXTURE_SIZE, Refs.TEXTURE_SIZE * 2, map);
		loadEntities();
	}
	
	void loadTextures() {
		this.textures.add(new Texture("../assets/textures/tiles/stone.png"));
		this.textures.add(new Texture("../assets/textures/tiles/grass.png"));
		this.textures.add(new Texture("../assets/textures/tiles/dirt.png"));
		this.textures.add(new Texture("../assets/textures/tiles/brick_full_1.png"));
		this.textures.add(new Texture("../assets/textures/tiles/brick_half_1.png"));
		this.textures.add(new Texture("../assets/textures/tiles/brick_bg_1.png"));
	}
	
	void loadEntities() {
		for(int i = 0; i < map.width; i++) {
			for(int j = 0; j < map.height; j++) {
				if (map.mapEntities.get(i).get(j) == 1) {
					enemies.add(new FlailEnemy(new Vector2(Refs.TEXTURE_SIZE * (j+0.5f),Refs.TEXTURE_SIZE *(map.mapEntities.size - i+0.5f)), Refs.TEXTURE_SIZE, Refs.TEXTURE_SIZE, map));
				}
			}
		}
		
	}
	
	void loadMap() {
		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		for(int i = 0; i < map.width; i++) {
			for(int j = 0; j < map.height; j++) {
				if (map.mapLayout.get(i).get(j) >= 1) {
					mapRects.add(new Rectangle(j * Refs.TEXTURE_SIZE, (map.height - i - 1) * Refs.TEXTURE_SIZE, Refs.TEXTURE_SIZE, Refs.TEXTURE_SIZE));
					game.batch.draw(textures.get(map.mapLayout.get(i).get(j)-1), j * Refs.TEXTURE_SIZE, (map.height - i - 1) * Refs.TEXTURE_SIZE);
					
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
	
	public static void createPlayer(float x, float y, int width, int height, Map map) {
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
		
		DebugOverlay debugOverlay = new DebugOverlay(game.batch, game.font, delta, showVectors);
		loadMap();
		player.update(delta);
		for(Enemy e : enemies) {
			e.update(delta);
		}
		moveCamera();
		debugOverlay.render();
		
		game.renderer.setProjectionMatrix(camera.combined);
		game.renderer.begin(ShapeType.Filled);
		game.renderer.setColor(Color.WHITE);
		for(Rectangle rect : rects) {
			game.renderer.rect(rect.x, rect.y, rect.width, rect.height);
		}
		game.renderer.rect(player.getRect().x, player.getRect().y, player.getRect().width, player.getRect().height);
		for(Enemy e : enemies) {
			game.renderer.rect(e.getRect().x, e.getRect().y, e.getRect().width, e.getRect().height);
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
		
		projectiles = player.weapon.currentProjectiles;
		Refs.updateEntities(player, projectiles, enemies);
		Refs.updateUtils(game, game.batch, game.renderer, camera);
		
		//game.batch.setProjectionMatrix(camera.combined);
		//game.batch.begin();
		//game.batch.end(); //textures
		
		
		
		if(Gdx.input.isKeyJustPressed(Input.Keys.V)) {
			showVectors = !showVectors;
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
		for(Texture t : textures) {
			t.dispose();
		}

	}

}
