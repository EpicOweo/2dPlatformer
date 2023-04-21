package com.epicoweo.platformer.abilities;

import com.badlogic.gdx.graphics.Texture;
import com.epicoweo.platformer.entities.Player;
import com.epicoweo.platformer.etc.Refs;
import com.epicoweo.platformer.items.weapons.EmptyWeapon;
import com.epicoweo.platformer.items.weapons.Pistol;

public class MechaSuit extends Ability {
	
	public boolean inSuit = false;
	
	public MechaSuit() {
		this.uiTexture = new Texture("./assets/textures/ui/mechasuit.png");
	}
	
	@Override
	public void onUse() {
		Player player = Refs.player;
		player.inMechaSuit = !player.inMechaSuit;
		Refs.player.weapon = player.inMechaSuit ? new Pistol(player) : new EmptyWeapon(player);
	}
}
