package com.epicoweo.platformer.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.epicoweo.platformer.abilities.Ability;
import com.epicoweo.platformer.etc.Refs;

public class AbilitiesBar extends HUDItem {

	Array<Ability> abilities = new Array<Ability>();
	
	public AbilitiesBar(Vector2 position, SpriteBatch b) {
		super(position, b);
		texture = new Texture("./assets/textures/ui/abilitiesbar.png");
		
		abilities = Refs.player.abilities;
		
	}
	
	@Override
	public void draw(Vector2 offset) {
		super.draw(offset);
		//draw abilities
		b.begin();
		for(int i = 0; i < abilities.size; i++) {
			b.draw(abilities.get(i).uiTexture, position.x+offset.x+(6*(i+1))+
					(i*abilities.get(i).uiTexture.getWidth()), position.y+offset.y+6);
		}
		b.end();
	}

}
