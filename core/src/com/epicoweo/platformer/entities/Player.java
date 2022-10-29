package com.epicoweo.platformer.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.epicoweo.platformer.etc.Refs;
import com.epicoweo.platformer.items.weapons.Pistol;
import com.epicoweo.platformer.items.weapons.Weapon;
import com.epicoweo.platformer.maps.JsonMap;
import com.epicoweo.platformer.screens.GameScreen;

public class Player extends Entity {

	int jumpVelocity = 490;
	int dashVelocityX = 300;
	int dashVelocityY = 400;
	int aerialXAcceleration = 500;
	boolean jumped = false;
	boolean dashed = false;
	public Weapon weapon;
	long lastDash = 0;
	int framesOnWall = 0; //number of frames the player has been on the wall
	public Texture texture;
	String direction = "left";
	
	public boolean flyMode = false;
	
	int wallJumpBuffer = 250000000;
	
	public Player(float x, float y, int width, int height, JsonMap map) {
		super(x, y, width, height, map, true);
		this.movementSpeed = 250;
		this.maxVelocity = new Vector2(250, 500);
		equipWeapon(new Pistol(this));
		createTexture();
	}
	
	void createTexture() {
		Pixmap pixmap16 = new Pixmap(Gdx.files.internal("../assets/textures/player/player_" + direction + ".png"));
		Pixmap pixmap32 = new Pixmap(20, 32, pixmap16.getFormat());
		pixmap32.setFilter(Pixmap.Filter.NearestNeighbour);
		pixmap32.drawPixmap(pixmap16,
		        0, 0, pixmap16.getWidth(), pixmap16.getHeight(),
		        0, 0, pixmap32.getWidth(), pixmap32.getHeight()
		);
		texture = new Texture(pixmap32);
		
		pixmap16.dispose();
		pixmap32.dispose();
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
		if(jumped && velocity.y > 0) {
			grounded = false;
		}
		if(grounded) {
			jumped = false;
		}
		
		if(velocity.y > 490) {
			velocity.y = 490;
		}
		
		if(Math.abs(velocity.x) < 10f) {
			if(!(Gdx.input.isKeyPressed(Keys.A)) && !(Gdx.input.isKeyPressed(Keys.D))) {
				velocity.x = 0;
			}
		}
		
		if(velocity.y < 0) {
			jumped = true;
			grounded = false;
		}
		if(!grounded && affectedByGravity) {
			if(onWall && velocity.y < 0) {
				acceleration.y = 0;
				if(Gdx.input.isKeyPressed(Keys.S)) {
					velocity.y = -250;
				} else {
					velocity.y = -100;
				}
			} else {
				acceleration.y = Refs.GRAVITY;
			}
		} else if(!affectedByGravity) {
			acceleration.y = 0;
		}
		checkDash();
		
		checkWallFrames();
		processInput();
		
		this.onWall = false;
		accelerate(delta);
		move(delta);
		
		
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
			System.out.println(framesOnWall);
			dashed = true;
		}
	}
	
	@Override
	public void accelerate(float delta) {
		if(Math.abs(velocity.x) < maxVelocity.x) {
			velocity.x += acceleration.x * delta;
		} else {
			velocity.x = Math.signum(velocity.x) * maxVelocity.x;
		}
		if(Math.abs(velocity.y) < maxVelocity.y) {
			velocity.y += acceleration.y * delta;
		} else if(Math.abs(velocity.y) > maxVelocity.y && dashed) {
			velocity.y += acceleration.y * delta;
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
		
		if((TimeUtils.nanoTime() - lastDash > 100000000) || !Gdx.input.isKeyPressed(Keys.SPACE)) {
			affectedByGravity = true;
		}
	}
	
	public void jump() {
		System.out.println("jump");
		onWall = false;
		velocity.y += jumpVelocity;
	}
	
	public void wallJump() {
		System.out.println("walljump");
		onWall = false;
		velocity.y += jumpVelocity;
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
		
		//movement
		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			acceleration.x = -1 * movementSpeed;
			if(!grounded && velocity.x > 0){ // to allow better aerial control
				acceleration.x = (100 * Math.signum(velocity.x)) - velocity.x * aerialXAcceleration / 100;
			}
			if(direction == "right") {
				direction = "left";
				createTexture();
			}
		} else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			acceleration.x = 1 * movementSpeed;
			if(!grounded && velocity.x < 0){
				acceleration.x = (100 * Math.signum(velocity.x)) - velocity.x * aerialXAcceleration / 100;
			}
			if(direction == "left") {
				direction = "right";
				createTexture();
			}
		} else {
			acceleration.x = 0;
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
			System.out.println("grounded " + grounded);
			System.out.println("jumped " + jumped);
			System.out.println("onwall " + onWall);
			System.out.println("walljumped " + wallJumped);
			System.out.println("dashed " + dashed);
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
			dashAngle = Math.toRadians(90);
		}
		
		affectedByGravity = false;
		this.acceleration = new Vector2();
		this.velocity = new Vector2((float)(dashVelocityX * Math.cos(dashAngle)), (float)(dashVelocityY * Math.sin(dashAngle)));
		lastDash = TimeUtils.nanoTime();
	}
	
}
