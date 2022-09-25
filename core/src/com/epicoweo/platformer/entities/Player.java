package com.epicoweo.platformer.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.epicoweo.platformer.etc.Refs;
import com.epicoweo.platformer.items.weapons.Pistol;
import com.epicoweo.platformer.items.weapons.Weapon;
import com.epicoweo.platformer.maps.Map;
import com.epicoweo.platformer.screens.GameScreen;

public class Player extends Entity {

	int jumpVelocity = 490;
	int dashVelocityX = 300;
	int dashVelocityY = 490;
	boolean jumped = false;
	boolean dashed = false;
	public Weapon weapon;
	long lastDash = 0;
	
	public Player(float x, float y, int width, int height, Map map) {
		super(x, y, width, height, map, true);
		this.movementSpeed = 250;
		this.maxVelocity = new Vector2(250, 500);
		equipWeapon(new Pistol(this));
	}
	
	@Override
	public void update(float delta) {
		if(dead) {
			spawn();
		}
		if(jumped && velocity.y > 0) {
			grounded = false;
		}
		if(grounded) {
			jumped = false;
		}
		if(velocity.y < 0) {
			jumped = true;
			grounded = false;
		}
		
		if(!grounded && affectedByGravity) {
			acceleration.y = Refs.GRAVITY;
		}
		
		if(Math.abs(velocity.x) < 1) {
			velocity.x = 0;
		}
		
		checkDash();
		processInput();
		accelerate(delta);
		move(delta);
	}
	
	@Override
	public void accelerate(float delta) {
		if(Math.abs(velocity.x) < maxVelocity.x) {
			velocity.x += acceleration.x * delta;
		}
		if(Math.abs(velocity.y) < maxVelocity.y) {
			velocity.y += acceleration.y * delta;
		} else if(Math.abs(velocity.y) > maxVelocity.y && dashed) {
			velocity.y += acceleration.y * delta;
		}
	}
	
	protected void spawn() {
		GameScreen.createPlayer(map.playerSpawn.x, map.playerSpawn.y, (int)rect.width, (int)rect.height, map);
	}
	
	public void checkDash() {
		if(grounded) {
			dashed = false;
			affectedByGravity = true;
		}
		
		if(TimeUtils.nanoTime() - lastDash > 500000000) {
			affectedByGravity = true;
		}
	}
	
	public void processInput() {
		//movement
		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			acceleration.x = -1 * movementSpeed;
		} else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			acceleration.x = 1 * movementSpeed;
		} else {
			acceleration.x = 0;
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
			if(grounded && !jumped) {
				velocity.y += jumpVelocity;
				jumped = true;
			} else if (!dashed) {
				dash();
				dashed = true;
			}
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
		this.velocity = new Vector2((float)(dashVelocityX * Math.cos(dashAngle)), (float)(dashVelocityY * Math.sin(dashAngle)));
		lastDash = TimeUtils.nanoTime();
	}
	
}
