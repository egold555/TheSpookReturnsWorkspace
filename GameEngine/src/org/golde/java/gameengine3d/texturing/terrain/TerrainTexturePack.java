package org.golde.java.gameengine3d.texturing.terrain;

import org.golde.java.gameengine3d.GameEngine;

public class TerrainTexturePack {

	private int backgroundTexture;
	private int rTexture;
	private int gTexture;
	private int bTexture;
	
	public TerrainTexturePack(String backgroundTexture, String rTexture, String gTexture, String bTexture) {
		this.backgroundTexture = GameEngine.getInstance().getLoader().loadTexture("terrain/" + backgroundTexture);
		this.rTexture = GameEngine.getInstance().getLoader().loadTexture("terrain/" + rTexture);
		this.gTexture = GameEngine.getInstance().getLoader().loadTexture("terrain/" + gTexture);
		this.bTexture = GameEngine.getInstance().getLoader().loadTexture("terrain/" + bTexture);
	}

	public int getBackgroundTexture() {
		return backgroundTexture;
	}

	public int getrTexture() {
		return rTexture;
	}

	public int getgTexture() {
		return gTexture;
	}

	public int getbTexture() {
		return bTexture;
	}
	
}
