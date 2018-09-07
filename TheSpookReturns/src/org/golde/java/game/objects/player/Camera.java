package org.golde.java.game.objects.player;

import org.golde.java.game.GameState;
import org.golde.java.game.Main;
import org.golde.java.game.renderEngine.DisplayManager;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class Camera
{
	private float sensitivity = 10F;

	private EntityPlayer player;
	
	private float yaw;
	private float pitch;

	public Camera(EntityPlayer player)
	{
		this.player = player;
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
		return pitch;
	}

	public void setPitch(float pitch) 
	{
		player.setRotY(pitch);
	}
	
	public float getYaw()
	{
		return yaw;
	}

	public void setYaw(float yaw)
	{
		player.setRotX(yaw);
	}
	
	private void mouseMovements()
	{
		if(Main.getGameState() != GameState.PLAYING) {return;}
		pitch = player.getRotY();
		yaw = player.getRotX();
		
		Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
		pitch -= Mouse.getDY() * sensitivity * DisplayManager.getFrameTimeSeconds();
		yaw += Mouse.getDX() * sensitivity * DisplayManager.getFrameTimeSeconds();
		
		if(pitch > 90) {pitch = 90;}
		if(pitch < -90) {pitch = -90;}
		
		setYaw(yaw);
		setPitch(pitch);
		
		Mouse.setGrabbed(true);
	}

	public void invertPitch() {
		this.pitch = -this.pitch;
	}

}
