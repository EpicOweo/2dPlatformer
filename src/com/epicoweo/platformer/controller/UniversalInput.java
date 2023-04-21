package com.epicoweo.platformer.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.epicoweo.platformer.etc.Refs;
import com.epicoweo.platformer.screens.GameScreen;

public class UniversalInput {

	static Controller c = GameScreen.controller;
	static float deadZone = 0.3f;
	
	public static boolean left = false;
	public static boolean right = false;
	public static boolean up = false;
	public static boolean down = false;
	public static boolean jump = false;
	public static boolean justJumped = false;
	public static boolean jumpedLastTick = false;
	public static boolean shoot = false;
	public static boolean interact = false;
	private static boolean interactedLastFrame = false;
	
	public enum ControllerType {
		SWITCH, PS4, XBONE;
	}
	
	public static boolean interact() {
		if(c != null) {
			if(c.getButton(c.getMapping().buttonY)) {
				return true;
			}
		}
		
		if(Gdx.input.isKeyJustPressed(Keys.E)) {
			return true;
		}
		interactedLastFrame = false;
		return false;
	}

	public static boolean left() {
		if(c != null) {
			if(c.getAxis(c.getMapping().axisLeftX) < -deadZone) return true;
		}
		
		if(Gdx.input.isKeyPressed(Keys.A)) return true;
		return false;
	}
	
	public static boolean right() {
		if(c != null) {
			if(c.getAxis(c.getMapping().axisLeftX) > deadZone) return true;
		}
		
		if(Gdx.input.isKeyPressed(Keys.D)) return true;
		return false;
	}
	
	public static boolean up() {
		if(c != null) {
			if(c.getAxis(c.getMapping().axisLeftY) < -deadZone) return true;
		}
		
		if(Gdx.input.isKeyPressed(Keys.W)) return true;
		return false;
	}
	
	public static boolean down() {
		if(c != null) {
			if(c.getAxis(c.getMapping().axisLeftY) > deadZone) return true;
		}
		
		if(Gdx.input.isKeyPressed(Keys.S)) return true;
		return false;
	}
	
	public static boolean dash() {
		if(c != null) {
			if(c.getButton(c.getMapping().buttonX)) {
				return true;
			}
		}
		
		if(Gdx.input.isKeyJustPressed(Keys.SPACE)) return true;
		return false;
	}
	
	public static boolean jump() {
		if(c != null) {
			if(c.getButton(c.getMapping().buttonA) || c.getButton(c.getMapping().buttonB)) {
				jumpedLastTick = true;
				return true;
			}
		}
		
		if(Gdx.input.isKeyPressed(Keys.SPACE)) {
			jumpedLastTick = true;
			return true;
		}
		return false;
	}
	
	public static boolean justJumped() {
		if(c != null) {
			if((c.getButton(c.getMapping().buttonA) && !jumpedLastTick) || 
					(c.getButton(c.getMapping().buttonB) && !jumpedLastTick)) return true;
		}
		
		if(Gdx.input.isKeyJustPressed(Keys.SPACE)) return true;
		return false;
	}
	
	public static boolean shoot() {
		if(!(Refs.game.getScreen() instanceof GameScreen)) return false;
		if(c != null) {
			if(Math.abs(c.getAxis(c.getMapping().axisRightX)) > deadZone ||
					Math.abs(c.getAxis(c.getMapping().axisRightY)) > deadZone) return true;
		}
		if(Gdx.input.isButtonPressed(Buttons.LEFT)) return true;
		return false;
	}
	
	public static float getCursorAngle() {
		float x;
		float y;
		float theta;
		if(c != null) {
			x = c.getAxis(c.getMapping().axisRightX);
			y = c.getAxis(c.getMapping().axisRightY);
			theta = -(new Vector2(x, y).angleDeg());
		} else {
			x = Gdx.input.getX();
			y = Gdx.input.getY();
			
			Vector3 worldCoords = Refs.camera.unproject(new Vector3(x, y, 0));
			theta = new Vector2(worldCoords.x, worldCoords.y)
					.sub(new Vector2(Refs.player.getRect().x, Refs.player.getRect().y)).angleDeg();
		}
		
		return theta;
		
	}
	
	public static boolean aimingGun() {
		if(Math.abs(c.getAxis(c.getMapping().axisRightX)) > deadZone ||
				Math.abs(c.getAxis(c.getMapping().axisRightY)) > deadZone) return true;
		
		return false;
	}
	
	public static void updateInputs() {
		left = left();
		right = right();
		up = up();
		down = down();
		jump = jump();
		justJumped  = justJumped();
		jumpedLastTick = jump();
		shoot = shoot();
		interact = interact() && !interactedLastFrame;
		if(interact) interactedLastFrame = true;
	}

	
}
