package org.golde.java.gameengine3d.objs.terrain;

import org.golde.java.gameengine3d.GameEngine;
import org.golde.java.gameengine3d.objs.models.RawModel;
import org.golde.java.gameengine3d.texturing.terrain.TerrainTexturePack;

/**
 * Generates flat terrain.
 * Useful for debugging
 * @author Eric
 *
 */
public class FlatTerrain extends Terrain{

	private static final int VERTEX_COUNT = 128;
	
	public FlatTerrain(int gridX, int gridZ, TerrainTexturePack texturePack, int blendMap) {
		this.texturePack = texturePack;
		this.blendMap = blendMap;
		this.px = gridX * SIZE;
		this.pz = gridZ * SIZE;
		this.model = generateTerrain("");
	}
	
	protected RawModel generateTerrain(String heightMap){
		int count = VERTEX_COUNT * VERTEX_COUNT;
		heights =new float[VERTEX_COUNT][VERTEX_COUNT];
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count*2];
		int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
		int vertexPointer = 0;
		for(int i=0;i<VERTEX_COUNT;i++){
			for(int j=0;j<VERTEX_COUNT;j++){
				heights[j][i] = 0;
				vertices[vertexPointer*3] = (float)j/((float)VERTEX_COUNT - 1) * SIZE;
				vertices[vertexPointer*3+1] = 0;
				vertices[vertexPointer*3+2] = (float)i/((float)VERTEX_COUNT - 1) * SIZE;
				normals[vertexPointer*3] = 0;
				normals[vertexPointer*3+1] = 1;
				normals[vertexPointer*3+2] = 0;
				textureCoords[vertexPointer*2] = (float)j/((float)VERTEX_COUNT - 1);
				textureCoords[vertexPointer*2+1] = (float)i/((float)VERTEX_COUNT - 1);
				vertexPointer++;
			}
		}
		int pointer = 0;
		for(int gz=0;gz<VERTEX_COUNT-1;gz++){
			for(int gx=0;gx<VERTEX_COUNT-1;gx++){
				int topLeft = (gz*VERTEX_COUNT)+gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		return GameEngine.getInstance().getLoader().loadToVAO(vertices, textureCoords, normals, indices);
	}
	
}
