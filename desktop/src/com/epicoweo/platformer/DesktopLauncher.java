package com.epicoweo.platformer;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.epicoweo.platformer.etc.Refs;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setWindowedMode(Refs.APP_LENGTH, Refs.APP_WIDTH);
		config.setTitle("2DPlatformer");
		new Lwjgl3Application(new PlatformerGame(), config);
	}
}
