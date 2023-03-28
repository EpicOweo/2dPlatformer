package com.epicoweo.platformer.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector3;
import com.epicoweo.platformer.etc.Refs;
import com.epicoweo.platformer.maps.JsonMap;

public class BlackHole extends Entity {

	public static int pullRadius = 50;
	public static int accelerationFactor = 30;
	
	public Circle pullCircle;
	
	public BlackHole(float x, float y, JsonMap map) {
		super(x, y, 16, 16, map, false);
		this.circle = new Circle(x, y, rect.width / 2);
		this.pullCircle = new Circle(x, y, pullRadius);
		this.texture = new Texture("./assets/textures/entities/blackhole.png");
	}
	
	@Override
	public void update(float delta) {
		if(this.rect.overlaps(Refs.player.sectIn)) {
			//drawPullCircle();
		}
		
	}
	
	public void drawPullCircle() {
		Vector3 vec3X = new Vector3(circle.x, 0, 0);
		Vector3 vec3Y = new Vector3(0, circle.y, 0);
		
		System.out.println("drawing pull circle");
		ShapeRenderer renderer = new ShapeRenderer();
		Gdx.gl.glLineWidth(2);
		renderer.begin(ShapeRenderer.ShapeType.Line);
		renderer.circle(Refs.camera.project(vec3X).x, Refs.camera.project(vec3Y).y, Refs.camera.project(new Vector3(pullRadius, 0, 0)).x);
		renderer.end();
		renderer.dispose();
		Gdx.gl.glLineWidth(1);
	}

}
