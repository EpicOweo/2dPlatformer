package com.epicoweo.platformer.items.weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import com.epicoweo.platformer.entities.Player;
import com.epicoweo.platformer.entities.projectiles.Projectile;
import com.epicoweo.platformer.etc.Refs;

public class Gun extends Weapon{

	public Gun(Player player, float damage, float useSpeed) {
		super(player, damage, useSpeed);
	}

	@Override
	public void use() {
		if(!usable()) return;
		
		int mouse_x = Gdx.input.getX(); // from left
		int mouse_y = Gdx.input.getY(); // from top
		Vector2 player_coords = new Vector2();
		player.getRect().getCenter(player_coords);
		
		Projectile projectile = null;
		
		//convert mouse coords to game coords
		Vector3 worldCoords = Refs.camera.unproject(new Vector3(mouse_x, mouse_y, 0));
		//angle of the bullet
		double theta = new Vector2(worldCoords.x, worldCoords.y).sub(new Vector2(player_coords.x, player_coords.y)).angleDeg();
		
		player.getRect().getCenter(player_coords);
		
		
		projectile = spawnProjectile(player_coords, (float)theta, bulletVelocity);
		
		currentProjectiles.add(projectile);
		
		lastUse = TimeUtils.nanoTime();
		
	}

}
