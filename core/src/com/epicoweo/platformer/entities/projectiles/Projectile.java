package com.epicoweo.platformer.entities.projectiles;

import com.epicoweo.platformer.entities.Entity;
import com.epicoweo.platformer.maps.Map;

public class Projectile extends Entity {

	public Projectile(float x, float y, int width, int height, Map map, boolean affectedByGravity) {
		super(x, y, width, height, map, affectedByGravity);
		breakOnCollide = true;
	}

}
