package com.epicoweo.platformer.entities;

import com.badlogic.gdx.math.Vector2;
import com.epicoweo.platformer.maps.Map;

public class Enemy extends Entity {

	public Enemy(float x, float y, int width, int height, Map map, boolean affectedByGravity) {
		super(x, y, width, height, map, affectedByGravity);
		this.movementSpeed = 300;
		this.maxVelocity = new Vector2(500, 500);
		this.velocity = new Vector2(-100, 0);
		this.friction = 0;
	}
	
}
