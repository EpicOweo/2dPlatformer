package com.epicoweo.platformer.controller;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;

public class OweoControllerListener implements ControllerListener {

	@Override
	public boolean axisMoved(Controller arg0, int arg1, float arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean buttonDown(Controller arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean buttonUp(Controller arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void connected(Controller arg0) {
		// TODO Auto-generated method stub
		System.out.println("connected");
	}

	@Override
	public void disconnected(Controller arg0) {
		// TODO Auto-generated method stub
		System.out.println("disconnected");
	}

}
