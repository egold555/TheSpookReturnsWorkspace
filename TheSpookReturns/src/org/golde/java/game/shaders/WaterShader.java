package org.golde.java.game.shaders;

import org.golde.java.game.helpers.Maths;
import org.golde.java.game.objects.player.Camera;
import org.lwjgl.util.vector.Matrix4f;

public class WaterShader extends ShaderProgram {

	private static final String VERTEX_FILE = PACKAGE + "waterVertexShader.txt";
	private static final String FRAGMENT_FILE = PACKAGE + "waterFragmentShader.txt";

	private int location_modelMatrix;
	private int location_viewMatrix;
	private int location_projectionMatrix;
	private int location_reflectionTexture;
	private int location_refractiontexture;
	private int location_dduvMap;
	private int location_moveFactor;
	private int location_cameraPosition;

	public WaterShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}



	@Override
	protected void bindAttributes() {
		bindAttribute(0, "position");
	}

	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = getUniformLocation("projectionMatrix");
		location_viewMatrix = getUniformLocation("viewMatrix");
		location_modelMatrix = getUniformLocation("modelMatrix");
		location_reflectionTexture = getUniformLocation("reflectionTexture");
		location_refractiontexture = getUniformLocation("refractionTexture");
		location_dduvMap = getUniformLocation("dudvMap");
		location_moveFactor = getUniformLocation("moveFactor");
		location_cameraPosition = getUniformLocation("cameraPosition");
	}
	
	public void loadMoveFactor(float factor) {
		super.loadFloat(location_moveFactor, factor);
	}
	
	public void connectTextureUnits() {
		super.loadInt(location_reflectionTexture, 0);
		super.loadInt(location_refractiontexture, 1);
		super.loadInt(location_dduvMap, 2);
	}

	public void loadProjectionMatrix(Matrix4f projection) {
		loadMatrix(location_projectionMatrix, projection);
	}

	public void loadViewMatrix(Camera camera){
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		loadMatrix(location_viewMatrix, viewMatrix);
		super.loadVector(location_cameraPosition, camera.getPosition());
	}

	public void loadModelMatrix(Matrix4f modelMatrix){
		loadMatrix(location_modelMatrix, modelMatrix);
	}

}
