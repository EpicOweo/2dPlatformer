package com.epicoweo.platformer.entities;

import com.badlogic.gdx.math.Vector2;

public class BoxCast extends Hitbox {

	public BoxCast(float x, float y, int width, int height, Entity entity) {
		super(width, height, entity, HitboxType.BOXCAST, x, y);
		
	}
	
	@Override
	protected Vector2 computeOffsets(float widthSign, float heightSign) {
		Vector2 offsets = new Vector2();
		
		offsets.x = this.position.x;
		offsets.y = this.position.y;
		
		return offsets;
	}

}
