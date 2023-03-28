package com.epicoweo.platformer.items.weapons;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.epicoweo.platformer.entities.Player;
import com.epicoweo.platformer.entities.projectiles.Projectile;
import com.epicoweo.platformer.entities.projectiles.SmallBullet;

public abstract class Weapon {
	
	public float damage;
	public float useSpeed;
	public float lastUse;
	public Player player;
	public int bulletVelocity;
	public Array<Projectile> currentProjectiles = new Array<Projectile>();;
	
	public Weapon(Player player, float damage, float useSpeed) {
		this.damage = damage;
		this.useSpeed = useSpeed; // seconds per use
		this.lastUse = 0;
		this.player = player;
	}
	
	public boolean usable() {
		long currentTime = TimeUtils.nanoTime();
		System.out.println(currentTime - lastUse);
		if (currentTime - lastUse < 1000000000 * useSpeed) return false;
		return true;
	}
	
	public Projectile spawnProjectile(Vector2 pos, float theta, int mag) {
		Projectile proj = new SmallBullet(pos.x, pos.y, player.map);
		proj.velocity = new Vector2(mag, 0).setAngleDeg(theta);
		proj.maxVelocity = new Vector2(bulletVelocity, bulletVelocity);
		return proj;
	}
	
	public abstract void use();
}
