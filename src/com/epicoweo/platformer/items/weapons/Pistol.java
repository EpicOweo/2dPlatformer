package com.epicoweo.platformer.items.weapons;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.epicoweo.platformer.controller.UniversalInput;
import com.epicoweo.platformer.entities.Player;
import com.epicoweo.platformer.etc.PolyUtils;

public class Pistol extends Gun {
	
	
	public Pistol(Player player) {
		super(player, 10f, 0.5f);
		this.bulletVelocity = 300;
		
		Rectangle rect = this.player.getRect();
		
		texture = new TextureRegion(new Texture("./assets/textures/player/gun.png"));
		poly = PolyUtils.rectToPoly(new Rectangle(rect.x + rect.width + rect.height/2, rect.y,
				texture.getRegionWidth(), texture.getRegionHeight()));
	}
	
	@Override
	public void update() {
		Rectangle rect = player.getRect();
		poly = PolyUtils.rotateAbout(PolyUtils.rectToPoly(new Rectangle(rect.x + rect.width + rect.height/2 + 10, rect.y + 9,
				texture.getRegionWidth(), texture.getRegionHeight())),
				new Vector2(rect.x + rect.width / 2, rect.y + rect.height/2), UniversalInput.getCursorAngle());
		passiveRotation += 1.5;
		if(passiveRotation > 360) passiveRotation -= 360;
	}
	
}
