package com.epicoweo.platformer.entities.projectiles;

import com.epicoweo.platformer.maps.JsonMap;
import com.epicoweo.platformer.maps.Map;

public class SmallBullet extends Projectile {
	
	public SmallBullet(float x, float y, JsonMap map) {
		super(x, y, 2, 2, map, false);
		
	}

}
