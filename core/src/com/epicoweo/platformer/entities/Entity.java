package com.epicoweo.platformer.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.epicoweo.platformer.etc.Refs;
import com.epicoweo.platformer.maps.Map;

public class Entity {

	private Rectangle rect;
	public Vector2 velocity;
	public Vector2 maxVelocity;
	public Vector2 acceleration;
	public int movementSpeed;
	boolean affectedByGravity;
	boolean grounded;
	public Map map;
	public boolean remove = false;
	protected boolean breakOnCollide = false;
	
	public static int friction = 300;
	
	public Entity(float x, float y, int width, int height, Map map, boolean affectedByGravity) {
		this.rect = new Rectangle(x, y, width, height);
		this.velocity = new Vector2(0, 0);
		this.acceleration = new Vector2(0, 0);
		this.map = map;
		this.affectedByGravity = affectedByGravity;
	}
	
	public Rectangle getRect() {
		return this.rect;
	}
	
	public void update(float delta) {
		if(this instanceof Player) {
			((Player)this).processInput();
			if(((Player)this).jumped && velocity.y > 0) {
				grounded = false;
			}
			if(grounded) {
				((Player)this).jumped = false;
			}
		}
		if(!grounded && affectedByGravity) {
			acceleration.y = Refs.GRAVITY;
		}
		accelerate(delta);
		move(delta);
	}
	
	public void accelerate(float delta) {
		if(Math.abs(velocity.x) < maxVelocity.x) {
			velocity.x += acceleration.x * delta;
		}
		if(Math.abs(velocity.y) < maxVelocity.y) {
			velocity.y += acceleration.y * delta;
		}
	}
	
	//collidable rects
	Rectangle[] r = {new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle()};
	
	public void move(float delta) {
		rect.x += velocity.x * delta;
		fetchCollidableRects();
		for(int i = 0; i < r.length; i++) {
			if(rect.overlaps(r[i])) {
				if(breakOnCollide) {
					remove = true;
				}
				if(velocity.x < 0) {
					rect.x = r[i].x + r[i].width + 0.01f;
				} else {
					rect.x = r[i].x - rect.width - 0.01f;
				}
				velocity.x = 0;
			}
		}
		doFriction(delta);
		
		rect.y += velocity.y * delta;
		fetchCollidableRects();
		for(int i = 0; i < r.length; i++) {
			if(rect.overlaps(r[i])) {
				if(breakOnCollide) {
					remove = true;
				}
				if(velocity.y < 0) {
					rect.y = r[i].y + r[i].height + 0.01f;
					if(affectedByGravity) {
						grounded = true;
					}
				} else {
					rect.y = r[i].y - rect.height - 0.01f;
				}
				velocity.y = 0;
			}
		}
		
	}
	
	public void moveTo(float x, float y) {
		rect.x = x;
		rect.y = y;
	}
	
	public void doFriction(float delta) {
		if(grounded) {
			if(velocity.x > 0) {
				velocity.x -= friction * delta;
			} else if(velocity.x < 0) {
				velocity.x += friction * delta;
			}
		}
	}
	
	public void fetchCollidableRects() {
		int[][] tiles = map.mapLayout;
		//bottom left
		int p1x = (int)(rect.x / 32);
		int p1y = (int)Math.floor(rect.y / 32);
		//bottom right
		int p2x = (int)((rect.x + rect.width) / 32);
		int p2y = (int)Math.floor(rect.y / 32);
		//top right
		int p3x = (int)((rect.x + rect.width) / 32);
		int p3y = (int)((rect.y + rect.height) / 32);
		//top left 
		int p4x = (int)(rect.x / 32);
		int p4y = (int)((rect.y + rect.height) / 32);
		try {
			//grabbing the tiles that correspond to the positions of the points
			int tile1 = tiles[map.mapLayout.length - 1 - p1y][p1x];
			int tile2 = tiles[map.mapLayout[0].length - 1 - p2y][p2x];
			int tile3 = tiles[map.mapLayout[0].length - 1 - p3y][p3x];
			int tile4 = tiles[map.mapLayout[0].length - 1 - p4y][p4x];
			
			if(tile1 == Map.TILE) {
				r[0].set(p1x*32, p1y*32, 32, 32);
			} else {
				r[0].set(-1, -1, 0, 0);
			}
			if(tile2 == Map.TILE) {
				r[1].set(p2x*32, p2y*32, 32, 32);
			} else {
				r[1].set(-1, -1, 0, 0);
			}
			if(tile3 == Map.TILE) {
				r[2].set(p3x*32, p3y*32, 32, 32);
			} else {
				r[2].set(-1, -1, 0, 0);
			}
			if(tile4 == Map.TILE) {
				r[3].set(p4x*32, p4y*32, 32, 32);
			} else {
				r[3].set(-1, -1, 0, 0);
			}
		} catch(IndexOutOfBoundsException e) {
			remove = true;
		}
	}
	
	
}
