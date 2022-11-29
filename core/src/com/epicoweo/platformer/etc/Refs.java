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
	public static final int PLATFORM_THICKNESS = 4;
	public static final int GRAVITY = -500;
	public static final int FPS = 10;
	public static final int SECPERFRAME = 1/FPS;
	
	
	public static Array<Entity> entities = new Array<Entity>();
	public static Player player;
	
	public static void updateEntities(Player player, Array<Projectile> projectiles, Array<Enemy> enemies) {
		entities.clear();
		entities.add(player);
		Refs.player = player;
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
	
	//ANSI Colors
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";
	
}
