package com.epicoweo.platformer.etc;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.epicoweo.platformer.PlatformerGame;
import com.epicoweo.platformer.entities.Enemy;
import com.epicoweo.platformer.entities.Entity;
import com.epicoweo.platformer.entities.Player;
import com.epicoweo.platformer.entities.projectiles.Projectile;

public class Refs {
	public static final int APP_LENGTH = 1280;
	public static final int APP_WIDTH = 720;
	public static final int TEXTURE_SIZE = 16;
	public static final int GRAVITY = -1000;
	
	public static Array<Entity> entities = new Array<Entity>();
	
	public static void updateEntities(Player player, Array<Projectile> projectiles, Array<Enemy> enemies) {
		entities.clear();
		entities.add(player);
		for(Projectile p : projectiles) {
			entities.add(p);
		}
		for(Enemy e : enemies) {
			entities.add(e);
		}
		
	}

	public static Game game;
	public static SpriteBatch batch;
	public static ShapeRenderer renderer;
	public static OrthographicCamera camera;
	public static OrthographicCamera debugCamera;
	
	public static void updateUtils(PlatformerGame g, SpriteBatch b, ShapeRenderer r,
			OrthographicCamera c) {
		game = g;
		batch = b;
		renderer = r;
		camera = c;
		
	}
}
