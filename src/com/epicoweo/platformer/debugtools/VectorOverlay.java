package com.epicoweo.platformer.debugtools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.epicoweo.platformer.entities.Entity;
import com.epicoweo.platformer.etc.Refs;

public class VectorOverlay extends Overlay {

	private ShapeRenderer vectorRenderer = new ShapeRenderer();
	private int arrowLength = 5;
	private int arrowAngle = 45;
	
	Array<Entity> entities;
	float delta;
	
	public VectorOverlay(float delta) {
		entities = Refs.entities;
		this.delta = delta;
	}

	@Override
	public void render() {
		Vector2 centerPos = new Vector2();
		for(Entity e : entities) {
			e.getRect().getCenter(centerPos);
			float vMag = e.velocity.len();
			float vDir = e.velocity.angleDeg();
			Vector2 vVector = new Vector2(vMag, 0).setAngleDeg(vDir);
			Vector2 vEnd = vVector.scl(delta * 10).add(centerPos);
			drawLine(centerPos, vEnd, Color.LIME);
			
			
			//arrow tips for fun
			if(!e.velocity.epsilonEquals(new Vector2(0, 0))) {
				Vector2 vArrow0 = new Vector2(arrowLength, 0).setAngleDeg(-180 + vDir + arrowAngle).add(vEnd);
				Vector2 vArrow1 = new Vector2(arrowLength, 0).setAngleDeg(-180 + vDir - arrowAngle).add(vEnd);
				drawLine(vEnd, vArrow0, Color.LIME);
				drawLine(vEnd, vArrow1, Color.LIME);
			}
			
			float aMag = e.acceleration.len();
			float aDir = e.acceleration.angleDeg();
			Vector2 aVector = new Vector2(aMag, 0).setAngleDeg(aDir);
			Vector2 aEnd = aVector.scl(delta * 5).add(centerPos);
			drawLine(centerPos, aEnd, Color.RED);
			
			
			if(!e.acceleration.epsilonEquals(new Vector2(0, 0))) {
				Vector2 aArrow0 = new Vector2(arrowLength, 0).setAngleDeg(-180 + aDir + arrowAngle).add(aEnd);
				Vector2 aArrow1 = new Vector2(arrowLength, 0).setAngleDeg(-180 + aDir - arrowAngle).add(aEnd);
				drawLine(aEnd, aArrow0, Color.RED);
				drawLine(aEnd, aArrow1, Color.RED);
			}
		}
		
	}
	
	public void drawLine(Vector2 start, Vector2 end, Color color) {
		Gdx.gl.glLineWidth(2);
		vectorRenderer.setProjectionMatrix(Refs.camera.combined);
		vectorRenderer.begin(ShapeRenderer.ShapeType.Line);
		vectorRenderer.setColor(color);
		vectorRenderer.line(start, end);
		vectorRenderer.end();
		Gdx.gl.glLineWidth(1);
	}
	
}
