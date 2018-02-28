package org.golde.java.gameengine3d.objs.base.entities;

import org.golde.java.gameengine3d.objs.terrain.Terrain;
import org.golde.java.gameengine3d.rendering.loader.Loader;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public abstract class EntityAbstractPlayer extends EntityMoveable {
	
	protected Loader loader;

	public EntityAbstractPlayer(Loader loader, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(null, position, rotX, rotY, rotZ, scale);
		this.loader = loader;
	}

	@Override
	public final void move(Terrain terrain) {
		checkInputs();
		onMove();
		super.move(terrain);
	}

	private void jump() {
		if(isInAir()) {return;}
		addVelocity(0, getJumpPower(), 0);
		onJump();
	}
	
	protected void onJump() {}

	protected void onLeftClick() {}

	protected void onRightClick() {}
	
	public void onScrollWheel(int scroll) {}
	
	protected void onMove() {}

	protected int getRunSpeed() {return 30;}
	protected int getSideSpeed() {return 30;}
	protected int getJumpPower() {return 20;}

	private void checkInputs() {
		float currentSpeed = 0;
		float currentSideSpeed = 0;

		if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
			currentSpeed = getRunSpeed();
		}
		else if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
			currentSpeed = -getRunSpeed();
		}
		else if(Keyboard.isKeyDown(Keyboard.KEY_A)) {
			currentSideSpeed = -getRunSpeed();
		}
		else if(Keyboard.isKeyDown(Keyboard.KEY_D)) {
			currentSideSpeed = getRunSpeed();
		}

		float rotX = (float) Math.toRadians(super.getRotX());

		float velX = (float) (currentSpeed * Math.sin(rotX) + currentSideSpeed * Math.cos(rotX));
		float velZ = (float) (-currentSpeed * Math.cos(rotX) + currentSideSpeed * Math.sin(rotX));

		super.setVelocity(velX, getVelocity().y, velZ);
		
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			jump();
		}

		if(Mouse.isButtonDown(0)) {
			onLeftClick();
		}

		if(Mouse.isButtonDown(1)) {
			onRightClick();
		}

	}

	@Override
	protected void onDeath() {}
	
}

