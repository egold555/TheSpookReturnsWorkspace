package org.golde.java.gameengine3d.rendering.renderers;

import java.util.List;

import org.golde.java.gameengine3d.helpers.Maths;
import org.golde.java.gameengine3d.objs.models.RawModel;
import org.golde.java.gameengine3d.objs.terrain.Terrain;
import org.golde.java.gameengine3d.rendering.VaoList;
import org.golde.java.gameengine3d.rendering.shaders.TerrainShader;
import org.golde.java.gameengine3d.texturing.terrain.TerrainTexturePack;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class TerrainRenderer {

	private TerrainShader shader;
	
	public TerrainRenderer(TerrainShader shader, Matrix4f projectionMatrix) {
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.connectTextureUnits();
		shader.stop();
	}
	
	public void render(List<Terrain> terrains) {
		for(Terrain terrain:terrains) {
			prepareTerrain(terrain);
			loadModelMatrix(terrain);
			GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			unbindTexturedModel();
		}
	}
	
	private void prepareTerrain(Terrain terrain) {
		RawModel model = terrain.getModel();
		GL30.glBindVertexArray(model.getVaoID());
		GL20.glEnableVertexAttribArray(VaoList.POSITIONS);
		GL20.glEnableVertexAttribArray(VaoList.TEXTURES);
		GL20.glEnableVertexAttribArray(VaoList.NORMALS);
		bindTextures(terrain);
		shader.loadShineVariables(1, 0);
		
	}
	
	private void bindTextures(Terrain terrain) {
		TerrainTexturePack texturePack = terrain.getTexturePack();
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getBackgroundTexture());
		
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getrTexture());
		
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getgTexture());
		
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getbTexture());
		
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getBlendMap());
	}
	
	private void unbindTexturedModel() {
		GL20.glDisableVertexAttribArray(VaoList.POSITIONS);
		GL20.glDisableVertexAttribArray(VaoList.TEXTURES);
		GL20.glDisableVertexAttribArray(VaoList.NORMALS);
		GL30.glBindVertexArray(0);
	}
	
	private void loadModelMatrix(Terrain terrain) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(new Vector3f(terrain.getX(), 0, terrain.getZ()), 0, 0, 0, 1);
		shader.loadTransformationMatrix(transformationMatrix);
	}
	
}
