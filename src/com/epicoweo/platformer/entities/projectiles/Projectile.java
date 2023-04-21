package com.epicoweo.platformer.entities.projectiles;

import com.epicoweo.platformer.entities.Entity;
import com.epicoweo.platformer.maps.PNGMap;

public class Projectile extends Entity {

	public Entity firedBy = null;
	
	public Projectile(float x, float y, int width, int height, PNGMap map, boolean affectedByGravity) {
		super(x, y, width, height, map, affectedByGravity);
		breakOnCollide = true;
	}

}
