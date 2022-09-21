package com.epicoweo.platformer.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.epicoweo.platformer.entities.projectiles.Projectile;
import com.epicoweo.platformer.etc.Refs;
import com.epicoweo.platformer.maps.Map;
import com.epicoweo.platformer.screens.GameScreen;

public class Entity {

	protected Rectangle rect;
	public Vector2 velocity;
	public Vector2 maxVelocity;
	public Vector2 acceleration;
	public int movementSpeed;
	boolean affectedByGravity;
	boolean grounded;
	public Map map;
	public boolean remove = false;
	protected boolean breakOnCollide = false;
	public boolean dead = false;
	
	public int friction = 200;
	
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
			if(dead) {
				spawn();
			}
			((Player)this).processInput();
			if(((Player)this).jumped && velocity.y > 0) {
				grounded = false;
			}
			if(grounded) {
				((Player)this).jumped = false;
			}
		} else {
			if(dead) {
				this.remove = true;
			}
		}
		if(!grounded && affectedByGravity) {
			acceleration.y = Refs.GRAVITY;
		}
		
		accelerate(delta);
		move(delta);
		
	}
	
	private void spawn() {
		GameScreen.createPlayer(100f, 100f, (int)rect.width, (int)rect.height, map);
	}

	public void accelerate(float delta) {
		if(Math.abs(velocity.x) < maxVelocity.x) {
			velocity.x += acceleration.x * delta;
		}
		if(Math.abs(velocity.y) < maxVelocity.y) {
			velocity.y += acceleration.y * delta;
		}
	}
	
	private void collideX(Rectangle r) {
		if(velocity.x < 0) {
			rect.x = r.x + r.width + 0.01f;
		} else {
			rect.x = r.x - rect.width - 0.01f;
		}
		if(!(this instanceof Player)) {
			velocity.x = -velocity.x;
		} else {
			velocity.x = 0;
		}
	}
	
	private void collideY(Rectangle r) {
		if(velocity.y < 0) {
			rect.y = r.y + r.height + 0.01f;
			if(affectedByGravity) {
				grounded = true;
			}
		} else {
			rect.y = r.y - rect.height - 0.01f;
		}
		velocity.y = 0;
	}
	
	//collidable rects
	Rectangle[] cRects = {new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle()};
	Array<Entity> collidableEntities = new Array<Entity>();
	
	public void move(float delta) {
		rect.x += velocity.x * delta;
		fetchCollidableRects();
		for(int i = 0; i < cRects.length; i++) {
			if(rect.overlaps(cRects[i])) {
				collideX(cRects[i]);
					
				if(breakOnCollide) {
					remove = true;
				}
			}
		}
		
		
		// only do friction if momentum is the same direction as the key being held
		if(!(Gdx.input.isKeyPressed(Input.Keys.A) && velocity.x < 0) && !(Gdx.input.isKeyPressed(Input.Keys.D) && velocity.x > 0)) {
			doFriction(delta);
		}
		
		rect.y += velocity.y * delta;
		fetchCollidableRects();
		for(int i = 0; i < cRects.length; i++) {
			if(rect.overlaps(cRects[i])) {
				collideY(cRects[i]);
				if(breakOnCollide) {
					remove = true;
				}
				
			}
		}
		
		for(int i = 0; i < collidableEntities.size; i++) {
			if(rect.overlaps(collidableEntities.get(i).getRect())) {
				if(this instanceof Projectile && breakOnCollide && !(((Projectile)this).firedBy instanceof Player)) {
					remove = true;
				}
				if(this instanceof Enemy && collidableEntities.get(i) instanceof Projectile && !(((Projectile)collidableEntities.get(i)).firedBy instanceof Enemy)) {
					dead = true;
					collidableEntities.get(i).remove = true;
				}
				if(this instanceof Player && collidableEntities.get(i) instanceof Enemy) {
					if(!collidableEntities.get(i).dead) {
						dead = true;
					}
				}
			}
		}
	}
	
	public void moveTo(double x, double y) {
		rect.x = (float)x;
		rect.y = (float)y;
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
		int p1x = (int)(rect.x / Refs.TEXTURE_SIZE);
		int p1y = (int)Math.floor(rect.y / Refs.TEXTURE_SIZE);
		//bottom right
		int p2x = (int)((rect.x + rect.width) / Refs.TEXTURE_SIZE);
		int p2y = (int)Math.floor(rect.y / Refs.TEXTURE_SIZE);
		
		//top right
		int p3x = (int)((rect.x + rect.width) / Refs.TEXTURE_SIZE);
		int p3y = (int)((rect.y + rect.height) / Refs.TEXTURE_SIZE);
		//top left 
		int p4x = (int)(rect.x / Refs.TEXTURE_SIZE);
		int p4y = (int)((rect.y + rect.height) / Refs.TEXTURE_SIZE);
		
		for(Entity e : Refs.entities) {
			if(!this.equals(e) && Math.abs(this.getRect().x - e.getRect().x) < 500) {
				collidableEntities.add(e);
			}
		}
		
		try {
			//grabbing the tiles that correspond to the positions of the points
			int tile1 = tiles[map.mapLayout[0].length - 1 - p1y][p1x];
			int tile2 = tiles[map.mapLayout[0].length - 1 - p2y][p2x];
			int tile3 = tiles[map.mapLayout[0].length - 1 - p3y][p3x];
			int tile4 = tiles[map.mapLayout[0].length - 1 - p4y][p4x];
			
			if(tile1 >= 1) {
				cRects[0].set(p1x*Refs.TEXTURE_SIZE, p1y*Refs.TEXTURE_SIZE, Refs.TEXTURE_SIZE, Refs.TEXTURE_SIZE);
			} else {
				cRects[0].set(-1, -1, 0, 0);
			}
			if(tile2 >= 1) {
				cRects[1].set(p2x*Refs.TEXTURE_SIZE, p2y*Refs.TEXTURE_SIZE, Refs.TEXTURE_SIZE, Refs.TEXTURE_SIZE);
			} else {
				cRects[1].set(-1, -1, 0, 0);
			}
			if(tile3 >= 1) {
				cRects[2].set(p3x*Refs.TEXTURE_SIZE, p3y*Refs.TEXTURE_SIZE, Refs.TEXTURE_SIZE, Refs.TEXTURE_SIZE);
			} else {
				cRects[2].set(-1, -1, 0, 0);
			}
			if(tile4 >= 1) {
				cRects[3].set(p4x*Refs.TEXTURE_SIZE, p4y*Refs.TEXTURE_SIZE, Refs.TEXTURE_SIZE, Refs.TEXTURE_SIZE);
			} else {
				cRects[3].set(-1, -1, 0, 0);
			}
			
		} catch(IndexOutOfBoundsException e) {
			remove = true;
		}
	}
	
	
}
