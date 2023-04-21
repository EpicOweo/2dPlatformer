package com.epicoweo.platformer.items.weapons;

import com.epicoweo.platformer.entities.Player;

public class EmptyWeapon extends Weapon {

	public EmptyWeapon(Player player) {
		super(player, 0, 0);
	}

	@Override
	public void use() {
		return;
	}

	@Override
	public void update() {
		return;
	}

}
