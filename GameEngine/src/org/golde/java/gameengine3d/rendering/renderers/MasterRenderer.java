package org.golde.java.gameengine3d.rendering.renderers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.golde.java.gameengine3d.GameEngineOptions;
import org.golde.java.gameengine3d.helpers.Maths;
import org.golde.java.gameengine3d.objs.Camera;
import org.golde.java.gameengine3d.objs.Light;
import org.golde.java.gameengine3d.objs.base.colliders.BoxCollider;
import org.golde.java.gameengine3d.objs.base.colliders.Collider;
import org.golde.java.gameengine3d.objs.base.colliders.CylinderCollider;
import org.golde.java.gameengine3d.objs.base.entities.Entity;
import org.golde.java.gameengine3d.objs.debug.EntityColliderBox;
import org.golde.java.gameengine3d.objs.debug.EntityColliderCylinder;
import org.golde.java.gameengine3d.objs.models.TexturedModel;
import org.golde.java.gameengine3d.objs.terrain.Terrain;
import org.golde.java.gameengine3d.rendering.loader.Loader;
import org.golde.java.gameengine3d.rendering.shaders.StaticShader;
import org.golde.java.gameengine3d.rendering.shaders.TerrainShader;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

public class MasterRenderer {
	private Matrix4f projectionMatrix;

	private StaticShader staticShader = new StaticShader();
	private EntityRenderer entityRenderer;
	private TerrainRenderer terrainRenderer;
	private TerrainShader terrainShader = new TerrainShader();
	private SkyboxRenderer skybox;
	private Loader loader;

	private boolean renderColliders = false;

	public MasterRenderer(Loader loader) {
		enableCulling();
		createProjectionMatrix();
		entityRenderer = new EntityRenderer(staticShader, projectionMatrix);
		terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);

		if(GameEngineOptions.GAME_SKYBOX_ENABLED) {
			skybox = new SkyboxRenderer(loader, projectionMatrix);
		}
		this.loader = loader;
	}

	private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
	private Map<TexturedModel, List<Entity>> colliderEntities = new HashMap<TexturedModel, List<Entity>>();
	private List<Terrain> terrains = new ArrayList<Terrain>();

	public static void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}

	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}

	public void setRenderColliders(boolean renderColliders) {
		this.renderColliders = renderColliders;
	}

	public void render(List<Light> lights, Camera camera) {
		prepare();

		staticShader.start();
		staticShader.loadFog(GameEngineOptions.FOG_DENSITY, GameEngineOptions.FOG_GRADIENT);
		staticShader.loadSkyColor(GameEngineOptions.SKY_RED, GameEngineOptions.SKY_GREEN, GameEngineOptions.SKY_BLUE);
		staticShader.loadLights(lights);
		staticShader.loadViewMatrix(camera);
		entityRenderer.render(entities);
		if (renderColliders) {
			entityRenderer.render(colliderEntities);
		}
		staticShader.stop();

		terrainShader.start();
		terrainShader.loadFog(GameEngineOptions.FOG_DENSITY, GameEngineOptions.FOG_GRADIENT);
		terrainShader.loadSkyColor(GameEngineOptions.SKY_RED, GameEngineOptions.SKY_GREEN, GameEngineOptions.SKY_BLUE);
		terrainShader.loadLights(sortLights(lights, camera));
		terrainShader.loadViewMatrix(camera);
		terrainRenderer.render(terrains);
		terrainShader.stop();

		if(GameEngineOptions.GAME_SKYBOX_ENABLED) {
			skybox.render(camera);
		}

		terrains.clear();
		entities.clear();
		colliderEntities.clear();
	}

	private List<Light> sortLights(List<Light> lights, final Camera camera)
	{
		List<Light> newList = new ArrayList<Light>(lights);
		newList.sort(new Comparator<Light>() {

			@Override
			public int compare(Light l1, Light l2) {
				double dist1 = distanceToLight(camera, l1);
				double dist2 = distanceToLight(camera, l2);

				if (dist1 < dist2)
					return -1;
				else if (dist1 > dist2)
					return 1;
				else
					return 0;
			}

		});

		return newList;
	}

	private static double distanceToLight(Camera camera, Light light)
	{
		if (light.isPermanent())
			return 0;
		else
			return Maths.distance(camera.getPosition(), light.getPosition());
	}


	public void prossessTerrain(Terrain terrain) {
		terrains.add(terrain);
	}

	public void processEntity(Entity entity) {
		TexturedModel entityModel = entity.getModel();
		List<Entity> batch = entities.get(entityModel);
		if(batch != null) {
			batch.add(entity);
		}else {
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			entities.put(entityModel, newBatch);
		}

		if (renderColliders) {
			for (Collider collider: entity.getColliders()) {
				Entity colliderEntity;
				if (collider instanceof BoxCollider) {
					colliderEntity = EntityColliderBox.create(loader, (BoxCollider)collider);
				}
				else if (collider instanceof CylinderCollider) {
					colliderEntity = EntityColliderCylinder.create(loader, (CylinderCollider)collider);
				}
				else {
					continue;
				}

				entityModel = colliderEntity.getModel();
				batch = colliderEntities.get(entityModel);
				if(batch != null) {
					batch.add(colliderEntity);
				}else {
					List<Entity> newBatch = new ArrayList<Entity>();
					newBatch.add(colliderEntity);
					colliderEntities.put(entityModel, newBatch);
				}
			}
		}
	}

	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(GameEngineOptions.SKY_RED, GameEngineOptions.SKY_GREEN, GameEngineOptions.SKY_BLUE, 1);
	}

	private void createProjectionMatrix() {
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(GameEngineOptions.GAME_FOV / 2f))) * aspectRatio);
		float x_scale = y_scale / aspectRatio;
		float frustum_length = GameEngineOptions.GAME_PLANE_FAR - GameEngineOptions.GAME_PLANE_NEAR;

		projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((GameEngineOptions.GAME_PLANE_FAR + GameEngineOptions.GAME_PLANE_NEAR) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * GameEngineOptions.GAME_PLANE_NEAR * GameEngineOptions.GAME_PLANE_FAR) / frustum_length);
		projectionMatrix.m33 = 0;
	}


	public void cleanUp() {
		staticShader.cleanUp();
		terrainShader.cleanUp();
	}

	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

}
