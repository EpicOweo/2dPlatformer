package com.epicoweo.platformer.etc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.epicoweo.platformer.entities.Player;
import com.epicoweo.platformer.maps.PNGMap;
import com.epicoweo.platformer.screens.GameScreen;

public class CameraController {

	private OrthographicCamera camera;
	private Player player;
	private PNGMap map;
	private boolean lerp = true;
	private float lerpCoef = 0.1f;
	
	public CameraController(OrthographicCamera camera) {
		this.camera = camera;
		this.player = GameScreen.player;
		this.map = player.map;
	}
	
	public void update() {
		this.player = GameScreen.player;
		if(GameScreen.iFrameCounter != 0 && GameScreen.iFrameCounter < 5) {
			lerp = false;
		} else lerp = true;
		moveCamera(player.lastSectIn != player.sectIn);
	}
	
	private void moveCamera(boolean changingSect) {
		
		float coef = lerp ? lerpCoef : 0f;
		
		float playerCenterX = player.getRect().x + 0.5f * player.getRect().width;
		float playerCenterY = player.getRect().y + 0.5f * player.getRect().height;
		
		if(map.cameraAttachPlayer || map.mapSections.size == 0) { // just follow the player
			camera.position.lerp(new Vector3(playerCenterX, playerCenterY, 0), coef);
			return;
		}
		
		Rectangle mapSect = player.sectIn;
		
		Vector2 sectCenter = new Vector2();
		mapSect.getCenter(sectCenter);
		
		//if sect is completely on screen, just look at the middle of the sect
		if(sectFitsScreen(mapSect)) {
			camera.position.lerp(new Vector3(sectCenter.x, sectCenter.y, 0), coef);
			return;
		}
		
		else if(sectFitsVertically(mapSect, true)) {
			//if left is visible, stop moving left and vice versa
			//right visible
			if(camera.position.x + camera.viewportWidth/2 > mapSect.x+mapSect.width) {
				if(playerInHThird(false)) { // if in bottom third
					camera.position.lerp(new Vector3(playerCenterX, sectCenter.y, 0), coef);
				} else {
					camera.position.lerp(new Vector3(mapSect.x+mapSect.width-camera.viewportWidth/2, sectCenter.y, 0), coef);
				}
				return;
			}
			
			//left visible
			else if(camera.position.x - camera.viewportWidth/2 < mapSect.x) {
				if(playerInHThird(true)) { // if in top third
					camera.position.lerp(new Vector3(playerCenterX, sectCenter.y, 0), coef);
				} else {
					camera.position.lerp(new Vector3(mapSect.x+camera.viewportWidth/2, sectCenter.y, 0), coef);
				}
				return;
			}

			//neither visible
			else {
				camera.position.lerp(new Vector3(playerCenterX, sectCenter.y, 0), coef);
				return;
			}
		} else if(sectFitsHorizontally(mapSect, true)) {
			//if ceiling is visible, stop moving up and vice versa
			//ceiling visible
			if(camera.position.y + camera.viewportHeight/2 > mapSect.y+mapSect.height) {
				if(playerInVThird(false)) { // if in bottom third
					camera.position.lerp(new Vector3(sectCenter.x, playerCenterY, 0), coef);
				} else {
					camera.position.lerp(new Vector3(sectCenter.x, mapSect.y+mapSect.height-camera.viewportHeight/2, 0), coef);
				}
				return;
			}
			
			//floor visible
			else if(camera.position.y - camera.viewportHeight/2 < mapSect.y) {
				if(playerInVThird(true)) { // if in top third
					camera.position.lerp(new Vector3(sectCenter.x, playerCenterY, 0), coef);
				} else {
					camera.position.lerp(new Vector3(sectCenter.x, mapSect.y+camera.viewportHeight/2, 0), coef);
				}
				return;
			}
			
			//neither visible
			else {
				camera.position.lerp(new Vector3(sectCenter.x, playerCenterY, 0), coef);
				return;
			}
			
		} else { //neither at all
			
			boolean ceilingVisible = camera.position.y + camera.viewportHeight/2 > mapSect.y+mapSect.height;
			boolean floorVisible = camera.position.y - camera.viewportHeight/2 < mapSect.y;
			boolean leftVisible = camera.position.x - camera.viewportWidth/2 < mapSect.x;
			boolean rightVisible = camera.position.x + camera.viewportWidth/2 > mapSect.x+mapSect.width;
			
			float lerpX = playerCenterX;
			float lerpY = playerCenterY;
			
			//none visible
			if(!ceilingVisible && !floorVisible && !leftVisible && !rightVisible) {
				camera.position.lerp(new Vector3(lerpX, lerpY, 0), coef);
				return;
			}
			
			if(ceilingVisible) {
				if(!playerInVThird(false)) { // if in bottom third
					lerpY = mapSect.y+mapSect.height-camera.viewportHeight/2;
				}
			} else if(floorVisible) {
				if(!playerInVThird(true)) { // if in top third
					lerpY = mapSect.y+camera.viewportHeight/2;
				}
			}
			
			if(rightVisible) {
				if(!playerInHThird(false)) { // if in left third
					lerpX = mapSect.x+mapSect.width-camera.viewportWidth/2;
				}
			} else if(leftVisible) {
				if(!playerInHThird(true)) { // if in right third
					lerpX = mapSect.x+camera.viewportWidth/2;
				}
			}
			
			camera.position.lerp(new Vector3(lerpX, lerpY, 0), coef);
		}
		
	}
	//vertical third
	private boolean playerInVThird(boolean top) { //top==true: checking in top third
		
		Vector3 playerCoords = new Vector3(player.getRect().x, player.getRect().y, 0);
		
		if(top) {
			if(camera.position.y + camera.viewportHeight/6 < playerCoords.y) {
				return true;
			}
		} else {
			if(camera.position.y - camera.viewportHeight/6 > playerCoords.y) {
				return true;
			}
		}

		return false;
	}
	//horizontal third
	private boolean playerInHThird(boolean right) { //right==true: checking in right third
		
		Vector3 playerCoords = new Vector3(player.getRect().x, player.getRect().y, 0);
		
		if(right) {
			if(camera.position.x + camera.viewportWidth/6 < playerCoords.x) {
				return true;
			}
		} else {
			if(camera.position.x - camera.viewportWidth/6 > playerCoords.x) {
				return true;
			}
		}
		
		
		return false;
	}
	
	/** 
	 * 
	 * Checks if a mapSect fits completely in the screen
	 * 
	 * @param mapSect
	 * 		the sect being checked
	 * @return true if fits, false otherwise
	 */
	private boolean sectFitsScreen(Rectangle mapSect) { 
		
		if(sectFitsVertically(mapSect, false) && sectFitsHorizontally(mapSect, false)) {
			return true;
		}
		
		return false;
	}
	
	/** 
	 * 
	 * Checks if a mapSect fits in the screen
	 * 
	 * @param mapSect
	 * 		the sect being checked
	 * @param exclusively
	 * 		sect ONLY fits on the screen vertically (not horizontally)
	 * @return true if fits (with respect to the exclusively param), false otherwise
	 */
	private boolean sectFitsVertically(Rectangle mapSect, boolean exclusively) {
		if(mapSect.height <= camera.viewportHeight) {
			if(exclusively) {
				if(sectFitsHorizontally(mapSect, false)) return false; // sect also fits horizontally
			}
			return true;
		}
		return false;
	}
	
	/** 
	 * 
	 * Checks if a mapSect fits in the screen
	 * 
	 * @param mapSect
	 * 		the sect being checked
	 * @param exclusively
	 * 		sect ONLY fits on the screen horizontally (not vertically)
	 * @return true if fits (with respect to the exclusively param), false otherwise
	 */
	private boolean sectFitsHorizontally(Rectangle mapSect, boolean exclusively) {
		if(mapSect.width <= camera.viewportWidth) {
			if(exclusively) {
				if(sectFitsVertically(mapSect, false)) return false; // sect also fits vertically
			}
			return true;
		}
		return false;
	}
}
