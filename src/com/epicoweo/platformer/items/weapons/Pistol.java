package com.epicoweo.platformer.items.weapons;

import com.epicoweo.platformer.entities.Player;

public class Pistol extends Gun {
	
	public Pistol(Player player) {
		super(player, 10f, 0.5f);
		this.bulletVelocity = 300;
	}
	
}
