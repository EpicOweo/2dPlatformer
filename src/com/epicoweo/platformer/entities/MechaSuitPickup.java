package com.epicoweo.platformer.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.epicoweo.platformer.abilities.MechaSuit;
import com.epicoweo.platformer.controller.UniversalInput;
import com.epicoweo.platformer.etc.Refs;
import com.epicoweo.platformer.etc.Utils;
import com.epicoweo.platformer.maps.PNGMap;

public class MechaSuitPickup extends Entity {
	
	public Texture spriteSheet;
	public TextureRegion[][] spriteRegions;
	public boolean touched = false;
	
	public MechaSuitPickup(float x, float y, PNGMap map) {
		super(x, y, 16, 32, map, false);
		
		this.hasAnimation = true;
		makeAnimation();
		
	}
	
	@Override
	public void update(float delta) {
		if(Refs.player.getRect().overlaps(this.rect) && UniversalInput.interact) new MechaSuit().onUse();
	}
	
	public void makeAnimation() {
		spriteSheet = new Texture("./assets/textures/entities/mechasuitpickup.png");
		spriteRegions = new TextureRegion(spriteSheet).split(16, 32);
		this.animation = new Animation<TextureRegion>(0.5f, Utils.flattenSpriteSheet(spriteRegions));
		this.animation.setPlayMode(PlayMode.LOOP);
		
		//spriteSheet.dispose();
		
	}
	
	

}
