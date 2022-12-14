package com.epicoweo.platformer.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.epicoweo.platformer.etc.Refs;
import com.epicoweo.platformer.items.weapons.Pistol;
import com.epicoweo.platformer.items.weapons.Weapon;
import com.epicoweo.platformer.maps.JsonMap;
import com.epicoweo.platformer.screens.GameScreen;

public class Player extends Entity {

	int jumpVelocity = 200;
	int dashVelocityX = 200;
	int dashVelocityY = 200;
	int maxDashVelocity = 200;
	int aerialXAcceleration = 250;
	boolean jumped = false;
	boolean dashed = false;
	public Weapon weapon;
	long lastDash = 0;
	boolean dashing = false;
	int framesOnWall = 0; //number of frames the player has been on the wall
	public Texture texture;
	public Texture invertedTexture;
	String direction = "left";
	public long lastOnSpeedBoost = 0;
	
	public Rectangle sectIn;
	
	public boolean flyMode = false;
	
	int wallJumpBuffer = 250000000;
	
	public Player(float x, float y, int width, int height, JsonMap map) {
		super(x, y, width, height, map, true);
		this.movementSpeed = 200;
		this.maxVelocity = new Vector2(150, 200);
		this.sectIn = new Rectangle();
		equipWeapon(new Pistol(this));
		createTexture();
	}
	
	void createTexture() {
		Pixmap pixmap16 = new Pixmap(Gdx.files.internal("../assets/textures/player/player_" + direction + ".png"));
		texture = new Texture(pixmap16);
		
		final int width = pixmap16.getWidth();
		final int height = pixmap16.getHeight();
		
		Pixmap invertedPixmap = new Pixmap(width, height, pixmap16.getFormat());
		
		for (int x = 0; x < width; x++) {
	        for (int y = 0; y < height; y++) {
	            invertedPixmap.drawPixel(x, y, pixmap16.getPixel(x, height - y - 1));
	        }
	    }
		
		invertedTexture = new Texture(invertedPixmap);
		
		pixmap16.dispose();
		invertedPixmap.dispose();
	}
	
	@Override
	public void update(float delta) {
		if(flyMode) {
			updateWithFlyMode(delta);
			return;
		}
		
		if(dead) {
			spawn();
		}
		if(jumped && inverted*velocity.y > 0) {
			grounded = false;
		}
		if(grounded) {
			jumped = false;
		}
		
		if(Math.abs(velocity.y) > maxVelocity.y) {
			velocity.y = maxVelocity.y * Math.signum(velocity.y);
		}
		
		if(Math.abs(velocity.x) < 10f) {
			if(!(Gdx.input.isKeyPressed(Keys.A)) && !(Gdx.input.isKeyPressed(Keys.D))) {
				velocity.x = 0;
			}
		}
		
		if(-inverted*velocity.y < 0) {
			jumped = true;
			grounded = false;
		}
		if(!grounded && affectedByGravity) {
			if(onWall && inverted*velocity.y < 0) {
				acceleration.y = 0;
				if(inverted == 1) {
					if(Gdx.input.isKeyPressed(Keys.S)) {
						velocity.y = -125;
					} else {
						velocity.y = -50;
					}
				} else {
					if(Gdx.input.isKeyPressed(Keys.W)) {
						velocity.y = 125;
					} else {
						velocity.y = 50;
					}
				}
			} else {
				acceleration.y = inverted*Refs.GRAVITY;
			}
		} else if(!affectedByGravity) {
			acceleration.y = 0;
		}
		
		if(TimeUtils.millis() - lastOnSpeedBoost > 500) {
			movementSpeed = 200;
			this.maxVelocity = new Vector2(150, 200);
		}
		
		checkDash();
		
		checkWallFrames();
		processInput();
		
		if(TimeUtils.millis() - lastReadyToInvert > 150) {
			readyToInvert = false;
		}
		
		this.onWall = false;
		accelerate(delta);
		move(delta);
		
		for(Rectangle sect : map.mapSections) {
			if(this.rect.overlaps(sect)) {
				this.sectIn = sect;
			}
		}
		
		for(Hitbox h : this.hitboxes) {
			h.updateHitbox(delta);
		}
		if(this.onWall) {
			framesOnWall += 1;
		} else {
			framesOnWall = 0;
		}
		
		
	}
	
	public void updateWithFlyMode(float delta) {
		
		if(Math.abs(velocity.x) < 10f) {
			if(!(Gdx.input.isKeyPressed(Keys.A)) && !(Gdx.input.isKeyPressed(Keys.D))) {
				velocity.x = 0;
			}
		}
		
		processInputFlyMode();
		move(delta);
	}
	
	public void checkWallFrames() {
		if(framesOnWall > 5 && !dashed) {
			dashed = true;
		}
	}
	
	@Override
	public void accelerate(float delta) {
		velocity.x += acceleration.x * delta;
		
		if(Math.abs(velocity.x) > maxVelocity.x && !dashing && !dashed) {
			velocity.x = Math.signum(velocity.x) * maxVelocity.x;
		} else if(Math.abs(velocity.x) > maxDashVelocity && (dashing || dashed)) {
			velocity.x = Math.signum(velocity.x) * maxDashVelocity;
		}
		
		velocity.y += acceleration.y * delta;
		
		if(Math.abs(velocity.y) > maxVelocity.y) {
			velocity.y = Math.signum(velocity.y) * maxVelocity.y;
		}
	}
	
	protected void respawn() {
		this.dead = true;
	}
	
	protected void spawn() {
		GameScreen.createPlayer(map.playerSpawn.x, map.playerSpawn.y, (int)rect.width, (int)rect.height, map);
	}
	
	public void checkDash() {
		if(grounded) {
			dashed = false;
			affectedByGravity = true;
		}
		
		if(TimeUtils.nanoTime() - lastDash < 500000000) {
			dashing = true;
		} else {
			dashing = false;
		}
		
		if((TimeUtils.nanoTime() - lastDash > 100000000) || !Gdx.input.isKeyPressed(Keys.SPACE)) {
			affectedByGravity = true;
		}
	}
	
	public void jump() {
		if(readyToInvert && TimeUtils.millis() - lastSwappedGravity > 100) {
			inverted = -inverted;
			lastSwappedGravity = TimeUtils.millis();
		} else {
			onWall = false;
			velocity.y += inverted*jumpVelocity;
		}
	}
	
	public void wallJump() {
		onWall = false;
		velocity.y += inverted*jumpVelocity;
		if(lastSideCollided == 0) { //if going left, jump right
			velocity.x += 200;
		} else {
			velocity.x -= 200;
		}
	}
	
	public void setFlyMode(boolean on) {
		if(on) {
			flyMode = true;
		} else {
			flyMode = false;
		}
	}
	
	public void toggleFlyMode() {
		flyMode = !flyMode;
	}
	
	public void processInputFlyMode() {
		if(Gdx.input.isKeyJustPressed(Input.Keys.F)) {
			toggleFlyMode();
			return;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			velocity.x = -1 * movementSpeed;
			if(direction == "right") {
				direction = "left";
				createTexture();
			}
		} else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			velocity.x = 1 * movementSpeed;
			if(direction == "left") {
				direction = "right";
				createTexture();
			}
			
		} else {
			velocity.x = 0;
		}
		
		if (Gdx.input.isKeyPressed(Input.Keys.W)) {
			velocity.y = 1 * movementSpeed;
		} else if(Gdx.input.isKeyPressed(Keys.S)) {
			velocity.y = -1 * movementSpeed;
		} else {
			velocity.y = 0;
		}
	}
	
	public void processInput() {
		
		if(Gdx.input.isKeyJustPressed(Input.Keys.F)) {
			toggleFlyMode();
			return;
		}
		
		if(flyMode) {
			processInputFlyMode();
		}
		
		if(velocity.x < 0) {
			if(direction == "right") {
				direction = "left";
				createTexture();
			}
		} else if (velocity.x > 0) {
			if(direction == "left") {
				direction = "right";
				createTexture();
			}
		}
		
		//movement
		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			acceleration.x = -1 * movementSpeed;
			if(!grounded && velocity.x > 0){ // to allow better aerial control
				acceleration.x = (100 * -Math.signum(velocity.x)) - velocity.x * aerialXAcceleration / 100;
			}
			
		} else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			acceleration.x = 1 * movementSpeed;
			if(!grounded && velocity.x < 0){
				acceleration.x = (100 * -Math.signum(velocity.x)) - velocity.x * aerialXAcceleration / 100;
			}
		} else {
			acceleration.x = 0;
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
			if(grounded && !jumped) {
				jump();
				jumped = true;
			} else if((onWall || TimeUtils.nanoTime() <= lastOnWall + wallJumpBuffer) && !wallJumped) {
				wallJump();
				wallJumped = true;
				dashed = false;
			} else if(wallJumped && !jumped) { // allow the player a jump after the one off the wall
				jump();
				wallJumped = false;
				jumped = true;
			} else if(!dashed) { // allow a dash
				dash();
				dashed = true;
			}
		}
		
		if(Gdx.input.isKeyJustPressed(Input.Keys.R)) {
			respawn();
		}
		
		if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
			weapon.use();
		}
	}
	
	public void equipWeapon(Weapon weapon) {
		this.weapon = weapon;
	}
	
	private void dash() {
		double dashAngle = 0; // degrees
		int totalKeysPressed = 0;
		boolean[] keys = {Gdx.input.isKeyPressed(Input.Keys.W), Gdx.input.isKeyPressed(Input.Keys.A),
				Gdx.input.isKeyPressed(Input.Keys.S), Gdx.input.isKeyPressed(Input.Keys.D)};
		
		if((keys[0] && keys[2]) || keys[1] && keys[3]) { //W,S or A, D
			return;
		}

		if(keys[0]) { // w
			dashAngle += 90;
			totalKeysPressed++;
		}
		if(keys[1]) { // a
			dashAngle += 180;
			totalKeysPressed++;
		}
		if(keys[2]) { // s
			dashAngle += 270;
			totalKeysPressed++;
		}
		if(keys[3]) { // d
			dashAngle += 0;
			totalKeysPressed++;
		}
		
		if(keys[2] && keys[3]) {
			dashAngle = Math.toRadians(315);
		} else if(keys[2] && keys[1]) {
			dashAngle = Math.toRadians(225);
		} else if(totalKeysPressed != 0) {
			dashAngle = Math.toRadians(dashAngle / totalKeysPressed); // average, to radians
		} else {
			if(inverted == 1) {
				dashAngle = Math.toRadians(90);
			} else {
				dashAngle = Math.toRadians(270);
			}
		}
		
		affectedByGravity = false;
		this.acceleration = new Vector2();
		this.velocity = new Vector2((float)(dashVelocityX * Math.cos(dashAngle)), (float)(dashVelocityY * Math.sin(dashAngle)));
		lastDash = TimeUtils.nanoTime();
		dashing = true;
	}
	
}
