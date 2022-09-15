package com.epicoweo.platformer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.epicoweo.platformer.PlatformerGame;
import com.epicoweo.platformer.entities.Player;
import com.epicoweo.platformer.entities.projectiles.Projectile;
import com.epicoweo.platformer.etc.Refs;
import com.epicoweo.platformer.maps.Level1;
import com.epicoweo.platformer.maps.Map;
import com.epicoweo.platformer.overlays.DebugOverlay;

public class GameScreen implements Screen {
	
	final PlatformerGame game;
	OrthographicCamera camera;
	Array<Rectangle> rects;
	Array<Rectangle> mapRects;
	Array<Projectile> projectiles;
	Player player;
	Map map;
	boolean showVectors = false;
	
	public GameScreen(final PlatformerGame game) {
		this.game = game;
		this.rects = new Array<Rectangle>();
		this.mapRects = new Array<Rectangle>();
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Refs.APP_LENGTH, Refs.APP_WIDTH);
		camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
		camera.update();
		
		loadMap(new Level1());
		createPlayer(Refs.APP_LENGTH / 2 - 32, Refs.APP_WIDTH / 2 - 64, 32, 64, map);
	}
	
	void loadMap(Map map) {
		this.map = map;
		game.renderer.begin(ShapeType.Filled);
		for(int i = 0; i < map.height; i++) {
			for(int j = 0; j < map.width; j++) {
				if (map.mapLayout[i][j] == 1) {
					mapRects.add(new Rectangle(j * Refs.TEXTURE_SIZE, (map.height - i - 1) * Refs.TEXTURE_SIZE, Refs.TEXTURE_SIZE, Refs.TEXTURE_SIZE));
					game.renderer.rect(j * Refs.TEXTURE_SIZE, (map.height - i - 1) * Refs.TEXTURE_SIZE, Refs.TEXTURE_SIZE, Refs.TEXTURE_SIZE);
				}
			}
		}
		game.renderer.end();
	}
	
	void moveCamera() {
		float newCameraX = player.getRect().x + player.getRect().width / 2;
		float newCameraY = camera.position.y;
		float playerCenterY = (float)(player.getRect().y + 0.5 * player.getRect().height) + 75;
		
		// if player is in the upper half of the screen
		if (newCameraY < playerCenterY) {
			newCameraY = playerCenterY;
		} else if (newCameraY > playerCenterY) {
			newCameraY = playerCenterY;
		}
		
		camera.position.lerp(new Vector3(newCameraX, newCameraY, 0), 0.1f);
	}
	
	public void createPlayer(float x, float y, int width, int height, Map map) {
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
		
		game.renderer.setProjectionMatrix(camera.combined);
		game.renderer.begin(ShapeType.Filled);
		game.renderer.setColor(Color.WHITE);
		for(Rectangle rect : rects) {
			game.renderer.rect(rect.x, rect.y, rect.width, rect.height);
		}
		game.renderer.rect(player.getRect().x, player.getRect().y, player.getRect().width, player.getRect().height);
		if(player.weapon.currentProjectiles.notEmpty()) {
			for(Projectile proj : player.weapon.currentProjectiles) {
				if(proj.remove) {
					player.weapon.currentProjectiles.removeValue(proj, false);
				}
				proj.update(delta);
				game.renderer.rect(proj.getRect().x, proj.getRect().y, proj.getRect().width, proj.getRect().height);
			}
		}
		game.renderer.end(); //rects etc
		
		projectiles = player.weapon.currentProjectiles;
		Refs.updateEntities(player, projectiles);
		Refs.updateUtils(game, game.batch, game.renderer, camera);
		
		//game.batch.setProjectionMatrix(camera.combined);
		//game.batch.begin();
		//game.batch.end(); //textures
		
		
		
		if(Gdx.input.isKeyJustPressed(Input.Keys.V)) {
			showVectors = !showVectors;
		}
		
		DebugOverlay debugOverlay = new DebugOverlay(game.batch, game.font, delta, showVectors);
		debugOverlay.render();
		loadMap(new Level1());
		player.update(delta);
		moveCamera();

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
		

	}

}
