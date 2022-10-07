package com.epicoweo.platformer.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.epicoweo.platformer.entities.projectiles.Projectile;
import com.epicoweo.platformer.etc.Refs;
import com.epicoweo.platformer.maps.JsonMap;
import com.epicoweo.platformer.maps.Map;
import com.epicoweo.platformer.screens.GameScreen;

public class Entity {

	protected Rectangle rect;
	protected Polygon poly;
	public Vector2 velocity;
	public Vector2 maxVelocity;
	public Vector2 acceleration;
	public int movementSpeed;
	boolean affectedByGravity;
	boolean grounded;
	public JsonMap map;
	public boolean remove = false;
	protected boolean breakOnCollide = false;
	public boolean dead = false;
	double angle; // radians
	Circle circle = null;
	boolean onWall = false;
	protected long lastOnWall = 0;
	boolean wallJumped = false;
	int lastSideCollided; //0 left, 1 right
	boolean collidedNone; //if collided with no tiles
	
	public int friction = 500;
	
	public Entity(float x, float y, int width, int height, JsonMap map, boolean affectedByGravity) {
		this.rect = new Rectangle(x, y, width, height);
		this.velocity = new Vector2(0, 0);
		this.acceleration = new Vector2(0, 0);
		this.map = map;
		this.affectedByGravity = affectedByGravity;
		this.angle = 0;
		
		this.poly = new Polygon(new float[] {
			rect.x, rect.y,
			rect.x, rect.y + rect.height,
			rect.x + rect.width, rect.y + rect.height,
			rect.x + rect.width, rect.y
		});	
	}

	protected void updatePoly() {
		this.poly = new Polygon(new float[] {
			rect.x, rect.y,
			rect.x, rect.y + rect.height,
			rect.x + rect.width, rect.y + rect.height,
			rect.x + rect.width, rect.y
		});	
		this.poly.setRotation((float)Math.toDegrees(angle));
	}
	
	public Circle getCircle() {
		return this.circle;
	}
	
	public Rectangle getRect() {
		return this.rect;
	}
	
	public Polygon getPoly() {
		return this.poly;
	}
	
	public void update(float delta) {
		if(dead) {
			this.remove = true;
		}
		
		if(Math.abs(velocity.x) < 1) {
			velocity.x = 0;
		}
		
		if(!grounded && affectedByGravity) {
			acceleration.y = Refs.GRAVITY;
		}
		move(delta);
		accelerate(delta);
		updatePoly();
		
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
			lastSideCollided = 0;
			rect.x = r.x + r.width + 0.01f;
		} else {
			lastSideCollided = 1;
			rect.x = r.x - rect.width - 0.01f;
		}
		if(!(this instanceof Player)) {
			velocity.x = -velocity.x;
		} else {
			velocity.x = 0;
			this.onWall = true;
			this.wallJumped = false;
			this.lastOnWall = TimeUtils.nanoTime();
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
	Rectangle[] cRects = {new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle()};
	Array<Entity> collidableEntities = new Array<Entity>();
	
	private void polyCollideRect(Polygon p, Rectangle r) {
		this.poly = new Polygon(new float[] {
			rect.x, rect.y,
			rect.x, rect.y + rect.height,
			rect.x + rect.width, rect.y + rect.height,
			rect.x + rect.width, rect.y
		});	
	}
	
	public void move(float delta) {
		boolean collidedX = false;
		boolean collidedY = false;
		
		rect.x += velocity.x * delta;
		fetchCollidableRects();
		for(int i = 0; i < cRects.length; i++) {
			if(rect.overlaps(cRects[i])) {
				collideX(cRects[i]);
				collidedX = true;
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
				collidedY = true;
				collideY(cRects[i]);
				if(breakOnCollide) {
					remove = true;
				}
				
			}
		}
		
		this.collidedNone = !collidedX && !collidedY; 
		
		for(int i = 0; i < collidableEntities.size; i++) {
			doEntityCollisions(collidableEntities.get(i));
		}
	}
	
	private void doEntityCollisions(Entity e) {
		boolean overlap = false;
		
		if(e.circle != null) {
			if(Intersector.overlaps(e.circle, this.rect)) {
				overlap = true;
			}
		} else {
			if(this.rect.overlaps(e.rect)) {
				overlap = true;
			}
		}
		if(!overlap) return;
		
		if(this instanceof Projectile && breakOnCollide && !(((Projectile)this).firedBy instanceof Player)) {
			remove = true;
		}
		if(this instanceof Enemy && e instanceof Projectile && !(((Projectile)e).firedBy instanceof Enemy)) {
			dead = true;
			e.remove = true;
		}
		if(this instanceof Player && e instanceof Enemy) {
			if(!e.dead) {
				dead = true;
			}
		}
	}
	
	public void moveTo(double x, double y) {
		rect.x = (float)x;
		rect.y = (float)y;
		updatePoly();
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
		Array<Array<Integer>> tiles = map.mapLayout;
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
		
		//bottom, top, left, right
		int p5x = (int)((p2x + p1x) / 2);
		int p5y = p1y;
		int p6x = (int)((p3x + p4x) / 2);
		int p6y = p3y;
		
		int p7x = p4x;
		int p7y = (int)((p4y + p1y) / 2);
		int p8x = p3x;
		int p8y = (int)((p3y + p2y) / 2);
		
		for(Entity e : Refs.entities) {
			if(!this.equals(e) && Math.abs(this.getRect().x - e.getRect().x) < 500) {
				collidableEntities.add(e);
			}
		}
		
		try {
			//grabbing the tiles that correspond to the positions of the points
			int tile1 = tiles.get(map.height - 1 - p1y).get(p1x);
			int tile2 = tiles.get(map.height - 1 - p2y).get(p2x);
			int tile3 = tiles.get(map.height - 1 - p3y).get(p3x);
			int tile4 = tiles.get(map.height - 1 - p4y).get(p4x);
			int tile5 = tiles.get(map.height - 1 - p5y).get(p5x);
			int tile6 = tiles.get(map.height - 1 - p6y).get(p6x);
			int tile7 = tiles.get(map.height - 1 - p7y).get(p7x);
			int tile8 = tiles.get(map.height - 1 - p8y).get(p8x);
			
			System.out.println(p1x);
			System.out.println(p1y);
			
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
			if(tile5 >= 1) {
				cRects[4].set(p5x*Refs.TEXTURE_SIZE, p5y*Refs.TEXTURE_SIZE, Refs.TEXTURE_SIZE, Refs.TEXTURE_SIZE);
			} else {
				cRects[4].set(-1, -1, 0, 0);
			}
			if(tile6 >= 1) {
				cRects[5].set(p6x*Refs.TEXTURE_SIZE, p6y*Refs.TEXTURE_SIZE, Refs.TEXTURE_SIZE, Refs.TEXTURE_SIZE);
			} else {
				cRects[5].set(-1, -1, 0, 0);
			}
			if(tile7 >= 1) {
				cRects[6].set(p7x*Refs.TEXTURE_SIZE, p7y*Refs.TEXTURE_SIZE, Refs.TEXTURE_SIZE, Refs.TEXTURE_SIZE);
			} else {
				cRects[6].set(-1, -1, 0, 0);
			}
			if(tile8 >= 1) {
				cRects[7].set(p8x*Refs.TEXTURE_SIZE, p8y*Refs.TEXTURE_SIZE, Refs.TEXTURE_SIZE, Refs.TEXTURE_SIZE);
			} else {
				cRects[7].set(-1, -1, 0, 0);
			}
			
		} catch(IndexOutOfBoundsException e) {
			//e.printStackTrace();
			remove = true;
		}
	}
	
	
}
