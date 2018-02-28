package org.golde.java.gameengine3d.objs;

import org.golde.java.gameengine3d.GameEngine;
import org.golde.java.gameengine3d.objs.base.entities.EntityAbstractPlayer;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class Camera {

	private float sensitivity = 10F;
	private Matrix4f projectionViewMatrix;

	private EntityAbstractPlayer player;

	public Camera(EntityAbstractPlayer player, Matrix4f projectionViewMatrix)
	{
		this.player = player;
		this.projectionViewMatrix = projectionViewMatrix;
	}

	public void movement()
	{
		mouseMovements();
	}

	public Vector3f getPosition()
	{
		return new Vector3f(player.getPosition().x, player.getPosition().y + 10, player.getPosition().z);
	}

	public float getPitch() 
	{
		return player.getRotY();
	}

	public void setPitch(float pitch) 
	{
		player.setRotY(pitch);
	}
	
	public float getYaw()
	{
		return player.getRotX();
	}

	public void setYaw(float yaw)
	{
		player.setRotX(yaw);
	}
	
	private void mouseMovements()
	{
		float pitch = getPitch();
		float yaw = getYaw();
		
		Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
		pitch -= Mouse.getDY() * sensitivity * GameEngine.getInstance().getDisplayManager().getFrameTimeSeconds();
		yaw += Mouse.getDX() * sensitivity * GameEngine.getInstance().getDisplayManager().getFrameTimeSeconds();
		
		if(pitch > 90) {pitch = 90;}
		if(pitch < -90) {pitch = -90;}
		
		setYaw(yaw);
		setPitch(pitch);
		
		Mouse.setGrabbed(true);
	}

	public Matrix4f getProjectionViewMatrix() {
		return projectionViewMatrix;
	}
	
}
