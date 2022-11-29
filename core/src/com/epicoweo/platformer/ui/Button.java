package com.epicoweo.platformer.ui;

import java.util.concurrent.Callable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.epicoweo.platformer.etc.Refs;
import com.epicoweo.platformer.screens.GameScreen;
import com.epicoweo.platformer.screens.PauseScreen;

public class Button {

	public int length;
	public int width;
	public Rectangle rect;
	public Color color;
	public String text;
	public Screen screen;
	public OrthographicCamera camera;
	
	Callable<Void> func;
	
	public Button(float x, float y, int length, int width, Color color, String text) {
		
		this.length = length;
		this.width = width;
		this.color = color;
		this.text = text;
		this.rect = new Rectangle(x, y, length, width);
		this.camera = getCamera();
		
	}
	
	public Button(float x, float y, int length, int width, String text) {
		this(x, y, length, width, Color.GRAY, text);
	}
	
	public Button(float x, float y, int length, int width, Color color) {
		this(x, y, length, width, color, "");
	}
	
	public Button(float x, float y, int length, int width) {
		this(x, y, length, width, Color.GRAY, "");
	}
	
	public void setFunc(Callable<Void> func) {
		this.func = func;
	}
	
	public OrthographicCamera getCamera() {
		OrthographicCamera cam = new OrthographicCamera();
		if(screen instanceof GameScreen) {
			cam = Refs.camera;
		} else if(screen instanceof PauseScreen) {
			cam = Refs.debugCamera;
		}
		return cam;
	}
	
	public void draw() {
		ShapeRenderer renderer = new ShapeRenderer();
		renderer.setColor(color);
		renderer.begin(ShapeType.Filled);
		
		renderer.rect(rect.x, rect.y, length, width);
		
		renderer.end();
		
		SpriteBatch batch = new SpriteBatch();
	
		batch.begin();
		
		BitmapFont font = new BitmapFont();
		font.setColor(Color.BLACK);
		font.draw(batch, text, rect.x + 10, rect.y + ((rect.height - font.getCapHeight()) / 2) + font.getCapHeight());
		
		batch.end();
		batch.dispose();
	}
	
	public boolean check() {
		if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
			if(rect.contains(Gdx.input.getX(), Refs.APP_WIDTH - Gdx.input.getY())) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public void use() throws Exception {
		this.func.call();
	}
	
}
