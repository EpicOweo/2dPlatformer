package com.epicoweo.platformer.etc;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class PolyUtils {

	public static Polygon rotateAbout(Polygon p, Vector2 rotationPoint, float degrees) {
		Polygon rotatedP = p;
		
		float posX = rotationPoint.x;
		float posY = rotationPoint.y;
		rotatedP.setOrigin(posX, posY);
		rotatedP.rotate(degrees);
		return rotatedP;
	}
	
	public static Polygon rotateAboutCenter(Polygon p, float degrees) {
		
		Rectangle boundingRect = p.getBoundingRectangle();
		Vector2 rotationPoint = new Vector2(boundingRect.x + boundingRect.width / 2, boundingRect.y + boundingRect.height / 2);
		
		return rotateAbout(p, rotationPoint, degrees);
	}
	
	public static Polygon rectToPoly(Rectangle r) {
		Polygon p = new Polygon(new float[] {
				r.x, r.y,
				r.x + r.width, r.y,
				r.x + r.width, r.y + r.height,
				r.x, r.y + r.height
		});
		
		return p;
	}
}
