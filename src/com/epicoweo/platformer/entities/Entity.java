package com.epicoweo.platformer.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.epicoweo.platformer.controller.UniversalInput;
import com.epicoweo.platformer.entities.projectiles.Projectile;
import com.epicoweo.platformer.etc.Refs;
import com.epicoweo.platformer.maps.PNGMap;
import com.epicoweo.platformer.tiles.GravitySwapDown;
import com.epicoweo.platformer.tiles.GravitySwapTile;
import com.epicoweo.platformer.tiles.GreenTile;
import com.epicoweo.platformer.tiles.NormalTile;
import com.epicoweo.platformer.tiles.OffTile;
import com.epicoweo.platformer.tiles.OnTile;
import com.epicoweo.platformer.tiles.RedTile;
import com.epicoweo.platformer.tiles.Spike;
import com.epicoweo.platformer.tiles.Tile;
import com.epicoweo.platformer.tiles.Tile.TileType;

public class Entity {

	protected Rectangle rect;
	protected Polygon poly;
	public Vector2 velocity;
	public Vector2 maxVelocity;
	public Vector2 maxAerialVelocity;
	public Vector2 acceleration;
	public int movementSpeed;
	boolean affectedByGravity;
	boolean grounded;
	public PNGMap map;
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
	boolean collidedSlope;
	protected long lastOnSlope = 0;
	boolean goingUpSlope = false;
	boolean lockToSlope = true;
	public int inverted = 1; //-1 yes, 1 no
	public boolean readyToInvert = false;
	public long lastReadyToInvert = 0;
	public boolean collidedBlackHole = false;
	boolean collidedAnyBlackHole = false;
	boolean cancelCollideBlackHole = false;
	
	public boolean hasAnimation = false;
	public Animation<TextureRegion> animation;
	
	public long lastSwappedGravity = 0;
	public long lastCollidedBlackHole = 0;
	public long startedCollidingBlackHole = 0;
	
	public Texture texture = null;
	
	public Hitbox hitbox;
	public Hitbox boxCastLeft;
	public Hitbox boxCastRight;
	public Hitbox boxCastBottom;
	public Hitbox boxCastBottomLeft;
	public Hitbox boxCastBottomRight;
	
	public Array<Hitbox> hitboxes;
	
	public int friction = 750;
	
	public Entity(float x, float y, int width, int height, PNGMap map, boolean affectedByGravity) {
		this.rect = new Rectangle(x, y, width, height);
		this.velocity = new Vector2(0, 0);
		this.acceleration = new Vector2(0, 0);
		this.map = map;
		this.affectedByGravity = affectedByGravity;
		this.angle = 0;
		
		createHitboxes(width, height);
		
		this.poly = new Polygon(new float[] {
			rect.x, rect.y, // bottom left
			rect.x, (rect.y + rect.height + rect.y) / 2, // middle left
			rect.x, rect.y + rect.height, // top left
			(rect.x + rect.width + rect.x) / 2, rect.y + rect.height, //top middle
			rect.x + rect.width, rect.y + rect.height, // top right
			rect.x + rect.width, (rect.y + rect.height + rect.y) / 2, //middle right
			rect.x + rect.width, rect.y, // bottom right
			(rect.x + rect.width + rect.x) / 2, rect.y //bottom middle
		});	
	}

	private void createHitboxes(int width, int height) {
		this.hitbox = new Hitbox(width-2, height-1, this);
		this.boxCastBottom = new BoxCast(0, -2, width - 2, 5, this);
		this.boxCastBottomLeft = new BoxCast(-2, -2, 5, 5, this);
		this.boxCastBottomRight = new BoxCast(this.rect.width - 2, -2, 5, 5, this);
		this.boxCastLeft = new BoxCast(-2, 2, 5, (int) this.rect.height, this);
		this.boxCastRight = new BoxCast(this.rect.width-2, 2, 5, (int) this.rect.height, this);
		
		this.hitboxes = new Array<Hitbox>();		
		this.hitboxes.add(hitbox, boxCastBottom, boxCastBottomLeft, boxCastBottomRight);
		this.hitboxes.add(boxCastLeft, boxCastRight);
	}
	
	protected void updatePoly() {
		this.poly = new Polygon(new float[] {
				rect.x, rect.y, // bottom left
				rect.x, (rect.y + rect.height + rect.y) / 2, // middle left
				rect.x, rect.y + rect.height, // top left
				(rect.x + rect.width + rect.x) / 2, rect.y + rect.height, //top middle
				rect.x + rect.width, rect.y + rect.height, // top right
				rect.x + rect.width, (rect.y + rect.height + rect.y) / 2, //middle right
				rect.x + rect.width, rect.y, // bottom right
				(rect.x + rect.width + rect.x) / 2, rect.y //bottom middle
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
		} else if(grounded && affectedByGravity) {
			acceleration.y = 0;
		}
		move(delta);
		accelerate(delta);
		
		updatePoly();
		hitbox.updateHitbox(delta);
	}

	public void accelerate(float delta) {
		if(Math.abs(velocity.x) < maxVelocity.x || collidedBlackHole) {
			velocity.x += acceleration.x * delta;
		}
		if(Math.abs(velocity.y) < maxVelocity.y || collidedBlackHole) {
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
		if(inverted == 1) {
			if(velocity.y <= 0) {
				rect.y = r.y + r.height + 0.01f;
				grounded = true;
			} else {
				rect.y = r.y - rect.height - 0.01f;
			}
		} else {
			if(velocity.y >= 0) {
				rect.y = r.y - rect.height - 0.01f;
				grounded = true;
			} else {
				rect.y = r.y + r.height + 0.01f;
			}
		}
		
		if(!(this instanceof Player)) {
			velocity.y = -velocity.y;
		} else {
			velocity.y = 0;
		}
		
	}
	
	//collidable rects
	Rectangle[] cRects = {new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle()};
	Polygon[] cPolys = {new Polygon(), new Polygon(), new Polygon(), new Polygon(), new Polygon(), new Polygon(), new Polygon(), new Polygon()};
	float[] cPolyRotations = {0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
	Tile[] cTiles = {new NormalTile(), new NormalTile(), new NormalTile(), new NormalTile(), new NormalTile(), new NormalTile(), new NormalTile(), new NormalTile()};
	Array<Entity> collidableEntities = new Array<Entity>();
	
	private void polyCollideRect(Polygon p, Rectangle r) {
		this.poly = new Polygon(new float[] {
			rect.x, rect.y,
			rect.x, rect.y + rect.height,
			rect.x + rect.width, rect.y + rect.height,
			rect.x + rect.width, rect.y
		});	
	}
	
	Array<Vector2> getPolygonVertices(Polygon polygon) {
	    float[] vertices = polygon.getTransformedVertices();

	    Array<Vector2> result = new Array<>();
	    for (int i = 0; i < vertices.length/2; i++) {
	        float x = vertices[i * 2];
	        float y = vertices[i * 2 + 1];
	        result.add(new Vector2(x, y));
	    }
	    return result;
	}
	
	public void move(float delta) {
		boolean collidedX = false;
		boolean collidedY = false;
		collidedSlope = false;
		
		boolean alreadyMovedY = false;
		
		rect.x += velocity.x * delta;
		
		if(lockToSlope) {
			if(goingUpSlope) {
				rect.y += Math.abs(velocity.x) * delta;
			} else {
				rect.y -= Math.abs(velocity.x) * delta*delta;
			}
			alreadyMovedY = true;
		}
		
		fetchCollidableRects();
		
		updatePoly();
		
		// only do friction if momentum is the same direction as the key being held
		if(!(UniversalInput.left && velocity.x < 0) && !(UniversalInput.right && velocity.x > 0)) {
			doFriction(delta);
		}
		
		grounded = false;
		
		// slope collisions
		for(int i = 0; i < cPolys.length; i++) {
			for(Vector2 vertex : this.getPolygonVertices(this.poly)) {
				if(cPolys[i].contains(vertex)) {
					float amountCollidedX = 0;
					
					if(cPolys[i].getRotation() == 0.0) {
						amountCollidedX = vertex.x - cPolys[i].getBoundingRectangle().getX();
						goingUpSlope = true;
						goingUpSlope = velocity.x > 0;
					} else if(cPolys[i].getRotation() == 270.0) {
						amountCollidedX = cPolys[i].getBoundingRectangle().getX()+Refs.TEXTURE_SIZE-vertex.x;
						goingUpSlope = velocity.x < 0;
					}
					if(!alreadyMovedY) {
						this.rect.y = cPolys[i].getBoundingRectangle().getY() + amountCollidedX;
					}
					collidedSlope = true;
					affectedByGravity = true;
					grounded = true;
					
					velocity.y = 0;
					acceleration.y = 0;
					
					if(breakOnCollide) {
						remove = true;
					}
				}
			}
		}
				
		for(int i = 0; i < cRects.length; i++) {
			if(this.rect.overlaps(cRects[i])) {
				int rectPtX = (int) Math.floor(cRects[i].x / Refs.TEXTURE_SIZE);
				int rectPtY = (int) Math.floor(cRects[i].y / Refs.TEXTURE_SIZE);
				if(collidedSlope) {
				} else {
					if(cTiles[i].type == TileType.Background) continue;
					if(map.mapTiles.get(map.height-1-rectPtY).get(rectPtX) instanceof Spike) {
						map.mapTiles.get(map.height-1-rectPtY).get(rectPtX).activateSpecialEffect();
						continue;
					}
					collideX(cRects[i]);
					collidedX = true;
				}
				

				if((map.mapTiles.get(map.height-1-rectPtY).get(rectPtX) instanceof OffTile
						|| map.mapTiles.get(map.height-1-rectPtY).get(rectPtX) instanceof OnTile)
						&& this instanceof Projectile) {
					map.mapTiles.get(map.height-1-rectPtY).get(rectPtX).x = rectPtX;
					map.mapTiles.get(map.height-1-rectPtY).get(rectPtX).y = map.height-1-rectPtY;
					map.mapTiles.get(map.height-1-rectPtY).get(rectPtX).activateSpecialEffect();
				}
				
				
				if(breakOnCollide) {
					remove = true;
				}
			}
		}
		for(int i = 0; i < map.outerMapRects.length; i++) {
			if(this.rect.overlaps(map.outerMapRects[i])) {
				collideX(map.outerMapRects[i]);
				collidedX = true;
			}
		}
		
		if(collidedSlope) {
			lastOnSlope = TimeUtils.millis();
		}
		
		if(TimeUtils.millis() - lastOnSlope > 25) {
			lockToSlope = false;
		} else {
			lockToSlope = true; 
		}
		if(!lockToSlope) {
			rect.y += velocity.y * delta;
			fetchCollidableRects();
			for(int i = 0; i < cRects.length; i++) {
				if(rect.overlaps(cRects[i])) {
					if(collidedSlope) {
						
					}
					
					if(cTiles[i].type == TileType.Background) continue;
					
					int rectPtX = Math.round(cRects[i].x / Refs.TEXTURE_SIZE);
					int rectPtY = Math.round(cRects[i].y / Refs.TEXTURE_SIZE);
					
					if(cTiles[i].type == TileType.Spike) {
						map.mapTiles.get(map.height-1-rectPtY).get(rectPtX).activateSpecialEffect();
						continue;
					}
					collidedY = true;
					collideY(cRects[i]);
					
					if((map.mapTiles.get(map.height-1-rectPtY).get(rectPtX) instanceof GravitySwapTile)
							|| (map.mapTiles.get(map.height-1-rectPtY).get(rectPtX) instanceof GravitySwapDown)
							&& this instanceof Player) {
						map.mapTiles.get(map.height-1-rectPtY).get(rectPtX).activateSpecialEffect();
					}
					
					if((map.mapTiles.get(map.height-1-rectPtY).get(rectPtX) instanceof OffTile
							|| map.mapTiles.get(map.height-1-rectPtY).get(rectPtX) instanceof OnTile)
							&& this instanceof Projectile) {
						map.mapTiles.get(map.height-1-rectPtY).get(rectPtX).x = rectPtX;
						map.mapTiles.get(map.height-1-rectPtY).get(rectPtX).y = map.height-1-rectPtY;
						map.mapTiles.get(map.height-1-rectPtY).get(rectPtX).activateSpecialEffect();
					}
					
					if(breakOnCollide) {
						remove = true;
					}
					
				}
			}
			for(int i = 0; i < map.outerMapRects.length; i++) {
				if(this.rect.overlaps(map.outerMapRects[i])) {
					collideY(map.outerMapRects[i]);
					collidedY = true;
				}
			}
			for(Rectangle r : map.platformRects) {
				if(rect.overlaps(r)) {
					if(rect.y <= r.y + r.height && velocity.y <= 0) {
						collidedY = true;
						collideY(r);
					}
				}
			}
		}
		this.collidedNone = !collidedX && !collidedY; 
		
		collidedAnyBlackHole = false;
		
		for(int i = 0; i < collidableEntities.size; i++) {
			doEntityCollisions(collidableEntities.get(i));
		}
		
		if(cancelCollideBlackHole) {
			this.acceleration = new Vector2(-this.acceleration.x, -this.acceleration.y);
		}
		
		if(!collidedAnyBlackHole) {
			collidedBlackHole = false;
			cancelCollideBlackHole = false;
		} else {
			Refs.player.dashed = true;
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
		
		if(this instanceof Player && e instanceof BlackHole) {
			//if within the pull radius, then set acceleration to be towards the center of the
			//black hole so it pulls the player around
			Circle bh = ((BlackHole) e).getCircle();
			Vector2 centroid = new Vector2();
			this.poly.getCentroid(centroid);
			double distanceX = centroid.x - bh.x;
			double distanceY = centroid.y - bh.y;
			double distance = Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
			if(distance <= BlackHole.pullRadius) {
				Vector2 accelerationVector = new Vector2((float)-distanceX, (float)-distanceY);
				if(!collidedBlackHole) {
					this.startedCollidingBlackHole = TimeUtils.millis();
				}
				//+1 so no divide by zero
				accelerationVector.scl((float)(BlackHole.accelerationFactor * (BlackHole.pullRadius / (distance+1))));
				
				//if(TimeUtils.millis() - this.startedCollidingBlackHole <= 500) {
					this.acceleration = accelerationVector;
					Refs.player.dashed = false;
				//}
				this.collidedBlackHole = true;
				this.lastCollidedBlackHole = TimeUtils.millis();
				collidedAnyBlackHole = true;
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
	
	private void setCRectsPolys(int px, int py, int index) {
		if(cTiles[index].type == TileType.Platform || cTiles[index].type == TileType.Background
				|| (cTiles[index] instanceof GreenTile && !GreenTile.activated)
				|| (cTiles[index] instanceof RedTile && !RedTile.activated)) {
			cRects[index].set(-1, -1, 0, 0);
			return;
		} else {
			cRects[index].set(px*Refs.TEXTURE_SIZE, py*Refs.TEXTURE_SIZE, Refs.TEXTURE_SIZE, Refs.TEXTURE_SIZE);
		}
	}
	
	public void fetchCollidableRects() {
		Array<Array<Integer>> tiles = map.mapLayout;
		Polygon[] newCPolys = {new Polygon(), new Polygon(), new Polygon(), new Polygon(), new Polygon(), new Polygon(), new Polygon(), new Polygon()};
		cPolys = newCPolys;
		
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
			
			if(tile1 >= 1) {
				cTiles[0] = map.mapTiles.get(map.height - 1 - p1y).get(p1x);
				setCRectsPolys(p1x, p1y, 0);
			} else {
				cRects[0].set(-1, -1, 0, 0);
			}
			if(tile2 >= 1) {
				cTiles[1] = map.mapTiles.get(map.height - 1 - p2y).get(p2x);
				setCRectsPolys(p2x, p2y, 1);
			} else {
				cRects[1].set(-1, -1, 0, 0);
			}
			if(tile3 >= 1) {
				cTiles[2] = map.mapTiles.get(map.height - 1 - p3y).get(p3x);
				setCRectsPolys(p3x, p3y, 2);
			} else {
				cRects[2].set(-1, -1, 0, 0);
			}
			if(tile4 >= 1) {
				cTiles[3] = map.mapTiles.get(map.height - 1 - p4y).get(p4x);
				setCRectsPolys(p4x, p4y, 3);
			} else {
				cRects[3].set(-1, -1, 0, 0);
			}
			if(tile5 >= 1) {
				cTiles[4] = map.mapTiles.get(map.height - 1 - p5y).get(p5x);
				setCRectsPolys(p5x, p5y, 4);
			} else {
				cRects[4].set(-1, -1, 0, 0);
			}
			if(tile6 >= 1) {
				cTiles[5] = map.mapTiles.get(map.height - 1 - p6y).get(p6x);
				setCRectsPolys(p6x, p6y, 5);
			} else {
				cRects[5].set(-1, -1, 0, 0);
			}
			if(tile7 >= 1) {
				cTiles[6] = map.mapTiles.get(map.height - 1 - p7y).get(p7x);
				setCRectsPolys(p7x, p7y, 6);
			} else {
				cRects[6].set(-1, -1, 0, 0);
			}
			if(tile8 >= 1) {
				cTiles[7] = map.mapTiles.get(map.height - 1 - p8y).get(p8x);
				setCRectsPolys(p8x, p8y, 7);
			} else {
				cRects[7].set(-1, -1, 0, 0);
			}
			
		} catch(IndexOutOfBoundsException e) {
			//e.printStackTrace();
			remove = true;
		}
	}
	
	
}
