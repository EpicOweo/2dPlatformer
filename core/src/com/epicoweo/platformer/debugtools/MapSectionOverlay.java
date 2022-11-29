package com.epicoweo.platformer.debugtools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.epicoweo.platformer.etc.Refs;
import com.epicoweo.platformer.maps.JsonMap;

public class MapSectionOverlay extends Overlay {

	private ShapeRenderer renderer = new ShapeRenderer();
	
	float delta;
	JsonMap map;
	
	public MapSectionOverlay(float delta, JsonMap map) {
		this.delta = delta;
		this.map = map;
	}

	@Override
	public void render() {
		for(Rectangle sect : map.mapSections) {
			drawLine(new Vector2(sect.x, sect.y), new Vector2(sect.x, sect.y + sect.height), Color.RED);
			drawLine(new Vector2(sect.x, sect.y + sect.height), new Vector2(sect.x + sect.width, sect.y + sect.height), Color.RED);
			drawLine(new Vector2(sect.x + sect.width, sect.y + sect.height), new Vector2(sect.x + sect.width, sect.y), Color.RED);
			drawLine(new Vector2(sect.x + sect.width, sect.y), new Vector2(sect.x, sect.y), Color.RED);
		}
		renderer.dispose();
		
	}
	
	public void drawLine(Vector2 start, Vector2 end, Color color) {
		Gdx.gl.glLineWidth(2);
		renderer.setProjectionMatrix(Refs.camera.combined);
		renderer.begin(ShapeRenderer.ShapeType.Line);
		renderer.setColor(color);
		renderer.line(start, end);
		renderer.end();
		Gdx.gl.glLineWidth(1);
	}


}
