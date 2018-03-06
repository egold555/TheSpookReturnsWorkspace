package org.golde.java.game.textures.terrain;

public class TerrainTexture {

	private int textureId;
	
	public TerrainTexture(int textureId) {
		this.textureId = textureId;
	}
	
	public int getTextureId() {
		return textureId;
	}

	@Override
	public String toString() {
		return "TerrainTexture [textureId=" + textureId + "]";
	}
	
	
	
}
