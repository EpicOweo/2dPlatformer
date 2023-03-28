package com.epicoweo.platformer.items.weapons;

import com.epicoweo.platformer.entities.Player;

public class AssaultRifle extends Gun {

	public AssaultRifle(Player player) {
		super(player, 5f, 0.25f);
		bulletVelocity = 500;
	}

}
