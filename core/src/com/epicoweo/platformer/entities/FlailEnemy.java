package com.epicoweo.platformer.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.epicoweo.platformer.etc.Refs;
import com.epicoweo.platformer.maps.Map;

public class FlailEnemy extends Enemy {

	Vector2 centerpos;
	
	double angle; // radians
	double angularVelocity;
	int radius;
	
	public FlailEnemy(Vector2 centerpos, int width, int height, Map map) {
		super(centerpos.x + 30, centerpos.y, width, height, map, false);
		
		this.centerpos = centerpos;
		this.friction = 0;
		this.angle = 0;
		this.angularVelocity = 2.5;
		this.radius = 75;
	}
	
	@Override
	public void update(float delta) {
		rotate(delta);
		move(delta);
		updateChain();
	}
	
	public void updateChain() {
		Vector2 rectCenter = new Vector2();
		rect.getCenter(rectCenter);
		Vector3 v3RectCenter = new Vector3(rectCenter.x, rectCenter.y, 0);
		Vector3 v3CenterPos = new Vector3(centerpos.x, centerpos.y, 0);
		
		ShapeRenderer renderer = new ShapeRenderer();
		Gdx.gl.glLineWidth(2);
		renderer.begin(ShapeRenderer.ShapeType.Line);
		renderer.line(Refs.camera.project(v3RectCenter), Refs.camera.project(v3CenterPos));
		renderer.end();
		renderer.dispose();
		Gdx.gl.glLineWidth(1);
	}
	
	@Override
	public void move(float delta) {
		rect.setCenter((float)(radius * Math.cos(angle) + centerpos.x), (float)(radius * Math.sin(angle) + centerpos.y));
	}
	
	public void rotate(float delta) {
		double dtheta = angularVelocity * delta;
		if(angle >= Math.PI * 2) {
			angle -= Math.PI * 2;
		}
		angle += dtheta;
	}

}
