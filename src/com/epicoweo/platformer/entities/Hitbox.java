package com.epicoweo.platformer.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Hitbox {

	public Vector2 bottomLeft;
	public Vector2 bottomRight;
	public Vector2 topLeft;
	public Vector2 topRight;
	public Vector2 bottomMiddle;
	public Vector2 topMiddle;
	public Vector2 leftMiddle;
	public Vector2 rightMiddle;
	
	public Array<Vector2> vertices;
	public Array<Vector2> bottomVertices;
	
	public Vector2 position;
	
	protected Vector2 offsets;
	
	int width;
	int height;
	
	Entity entity;
	Rectangle entityRect;
	
	Rectangle hitboxRect;
	
	HitboxType hitboxType;
	
	//hitbox types
	public static enum HitboxType {
		CENTERED, BOXCAST;
	}
	
	public Hitbox(int width, int height, Entity entity, HitboxType type, float x, float y) { 
		
		if(type == HitboxType.BOXCAST) {
			this.position = new Vector2(x, y);
		}
		
		this.entity = entity;
		this.entityRect = entity.getRect();
		this.hitboxType = type;
		this.width = Math.abs(width);
		this.height = Math.abs(height);
		
		this.vertices = new Array<Vector2>();
		this.bottomVertices = new Array<Vector2>();
		
		//get points on the hitbox
		this.offsets = computeOffsets(Math.signum(width), Math.signum(height));
		initializePoints();
		
		this.hitboxRect = new Rectangle(entity.getRect().x + offsets.x, entity.getRect().y + offsets.y, this.width, this.height);
		
		//get lines for hitbox
		
	}
	
	public Hitbox(int width, int height, Entity entity) { 
		this(width, height, entity, HitboxType.CENTERED);
	}
	
	public Hitbox(int width, int height, Entity entity, HitboxType type) { 
		this(width, height, entity, HitboxType.CENTERED, 0, 0);
	}
	
	public boolean collidesBottom(Rectangle r) {
		boolean collides = false;
		Array<Vector2> notBottomVertices = vertices;
		for(Vector2 vertex : bottomVertices) {
			if(r.contains(vertex)) collides = true;
			notBottomVertices.removeValue(vertex, false);
		}
		for(Vector2 vertex : notBottomVertices) {
			if(r.contains(vertex)) collides = false;
		}
		return collides;
	}
	
	protected Vector2 computeOffsets(float widthSign, float heightSign) {
		Vector2 offsets = new Vector2();
		
		switch(this.hitboxType) {
		case CENTERED:
			offsets.x = ((entityRect.width - width) / 2) - (1 - widthSign)*this.width;
			offsets.y = ((entityRect.height - height) / 2) - (1 - heightSign)*this.height;
			break;
		default: // default to centered
			offsets.x = ((entityRect.width - width) / 2) - (1 - widthSign)*this.width;
			offsets.y = ((entityRect.height - height) / 2) - (1 - heightSign)*this.height;
			break;
		}
		
		return offsets;
		
	}
	
	protected void initializePoints() {
		vertices.clear();
		
		float xOffset = offsets.x;
		float yOffset = offsets.y;
		
		this.bottomLeft = new Vector2(entity.rect.x + xOffset, entity.rect.y + yOffset);
		this.bottomRight = new Vector2(entity.rect.x + xOffset + width, entity.rect.y + yOffset);
		this.topLeft = new Vector2(entity.rect.x + xOffset, entity.rect.y + height + yOffset);
		this.topRight = new Vector2(entity.rect.x + width + xOffset, entity.rect.y + height + yOffset);
		this.bottomMiddle = new Vector2((bottomLeft.x + bottomRight.x) / 2, bottomLeft.y);
		this.topMiddle = new Vector2((bottomLeft.x + bottomRight.x) / 2, topLeft.y);
		this.leftMiddle = new Vector2(bottomLeft.x, (bottomLeft.y + topLeft.y) / 2);
		this.rightMiddle = new Vector2(bottomRight.x, (bottomLeft.y + topLeft.y) / 2);
		
		
		vertices.add(bottomLeft, bottomRight, topLeft, topRight);
		vertices.add(bottomMiddle, topMiddle, leftMiddle, rightMiddle);
		
		bottomVertices.add(bottomLeft, bottomMiddle, bottomRight);
	}
	
	protected void updateHitbox(float delta) {
		initializePoints();
		this.hitboxRect = new Rectangle(entity.getRect().x + offsets.x, entity.getRect().y + offsets.y, this.width, this.height);
	}

}
