package org.golde.java.gameengine3d.rendering;

import java.io.File;

import org.golde.java.gameengine3d.GameEngineOptions;
import org.golde.java.gameengine3d.helpers.GELog;
import org.golde.java.gameengine3d.helpers.IconLoader;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.vector.Vector2f;

public class DisplayManager {
	
	private long lastFrameTime;
	private float delta;

	public void createDisplay() {
		
		ContextAttribs attribs = new ContextAttribs(3, 3)
				.withForwardCompatible(true)
				.withProfileCore(true);
		try {

			Display.setDisplayMode(new DisplayMode(GameEngineOptions.WINDOW_WIDTH, GameEngineOptions.WINDOW_HEIGHT));
			Display.setVSyncEnabled(true);
			Display.setResizable(false);
			Display.create(new PixelFormat(), attribs);
			Display.setTitle(GameEngineOptions.WINDOW_TITLE);
			if(GameEngineOptions.WINDOW_ICON != null) {
				Display.setIcon(IconLoader.load(new File("res/" + GameEngineOptions.WINDOW_ICON + ".png")));
			}
			

		} catch(LWJGLException e) {
			GELog.error(e, "Failed to create display!");
		}

		GL11.glViewport(0, 0, GameEngineOptions.WINDOW_WIDTH, GameEngineOptions.WINDOW_HEIGHT);
		lastFrameTime = getCurrentTime();

	}

	public void aboutToStartGameLoop() {
		
	}

	public void updateDisplay() {
		Display.sync(GameEngineOptions.WINDOW_FPS_CAP);
		Display.update();
		long currentFrameTime = getCurrentTime();
		delta = (currentFrameTime - lastFrameTime) / 1000f;
		lastFrameTime = currentFrameTime;
	}

	public float getFrameTimeSeconds() {
		return delta;
	}

	public void closeDisplay() {
		Display.destroy();
		System.exit(0);
	}

	private long getCurrentTime() {
		return Sys.getTime() * 1000 / Sys.getTimerResolution();
	}

	public Vector2f getNormailzedMouseCoordinates() {
		float nx = -1.0f + 2.0f * (float) Mouse.getX() / (float) Display.getWidth();
		float nz = 1.0f - 2.0f * (float) Mouse.getY() / (float) Display.getHeight();
		return new Vector2f(nx, nz);
	}
}
