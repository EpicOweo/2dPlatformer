package com.epicoweo.platformer.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.epicoweo.platformer.items.weapons.AssaultRifle;
import com.epicoweo.platformer.items.weapons.Pistol;
import com.epicoweo.platformer.items.weapons.Weapon;
import com.epicoweo.platformer.maps.Map;

public class Player extends Entity {

	int jumpVelocity = 490;
	boolean jumped = false;
	public Weapon weapon;
	
	public Player(float x, float y, int width, int height, Map map) {
		super(x, y, width, height, map, true);
		this.movementSpeed = 250;
		this.maxVelocity = new Vector2(250, 500);
		equipWeapon(new Pistol(this));
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
			}
		}
		if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
			weapon.use();
		}
	}
	
	public void equipWeapon(Weapon weapon) {
		this.weapon = weapon;
	}

}
