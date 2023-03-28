package com.epicoweo.platformer.debugtools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.epicoweo.platformer.entities.Entity;
import com.epicoweo.platformer.entities.Hitbox;
import com.epicoweo.platformer.etc.Refs;

public class HitboxOverlay extends Overlay {

	private ShapeRenderer renderer = new ShapeRenderer();
	
	Array<Entity> entities;
	float delta;
	
	public HitboxOverlay(float delta) {
		entities = Refs.entities;
		this.delta = delta;
	}

	@Override
	public void render() {
		for(Entity e : entities) {
			for(Hitbox hitbox : e.hitboxes) {
				drawLine(hitbox.bottomLeft, hitbox.bottomRight, Color.LIME);
				drawLine(hitbox.bottomRight, hitbox.topRight, Color.LIME);
				drawLine(hitbox.topRight, hitbox.topLeft, Color.LIME);
				drawLine(hitbox.topLeft, hitbox.bottomLeft, Color.LIME);
			}
			
		}
		
		renderer.dispose();
		
	}
	
	public void drawLine(Vector2 start, Vector2 end, Color color) {
		Gdx.gl.glLineWidth(1);
		renderer.setProjectionMatrix(Refs.camera.combined);
		renderer.begin(ShapeRenderer.ShapeType.Line);
		renderer.setColor(color);
		renderer.line(start, end);
		renderer.end();
	}

}
