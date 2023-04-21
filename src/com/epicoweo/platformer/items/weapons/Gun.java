package com.epicoweo.platformer.items.weapons;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.epicoweo.platformer.controller.UniversalInput;
import com.epicoweo.platformer.entities.Player;
import com.epicoweo.platformer.entities.projectiles.Projectile;

public class Gun extends Weapon {
	
	public Gun(Player player, float damage, float useSpeed) {
		super(player, damage, useSpeed);
	}

	@Override
	public void use() {
		if(!usable()) return;
		
		Projectile projectile = null;
		//angle of the bullet
		double theta = UniversalInput.getCursorAngle();
		Vector2 coords = new Vector2();
		poly.getVertex(0, coords);
		
		projectile = spawnProjectile(coords, (float)theta, bulletVelocity);
		projectile.firedBy = player;
		
		currentProjectiles.add(projectile);
		
		lastUse = TimeUtils.nanoTime();
		
	}

	@Override
	public void update() {
		
	}

}
