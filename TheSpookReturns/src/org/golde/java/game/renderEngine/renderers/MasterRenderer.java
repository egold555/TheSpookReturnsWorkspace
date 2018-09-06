package org.golde.java.game.renderEngine.renderers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.golde.java.game.GLog;
import org.golde.java.game.helpers.Maths;
import org.golde.java.game.models.TexturedModel;
import org.golde.java.game.multiplayer.MPlayer;
import org.golde.java.game.objects.base.colliders.BoxCollider;
import org.golde.java.game.objects.base.colliders.Collider;
import org.golde.java.game.objects.base.colliders.CylinderCollider;
import org.golde.java.game.objects.base.entities.Entity;
import org.golde.java.game.objects.debug.EntityColliderBox;
import org.golde.java.game.objects.debug.EntityColliderCylinder;
import org.golde.java.game.objects.light.Light;
import org.golde.java.game.objects.player.Camera;
import org.golde.java.game.objects.terrain.decoration.WaterTile;
import org.golde.java.game.renderEngine.Loader;
import org.golde.java.game.renderEngine.WaterFrameBuffers;
import org.golde.java.game.shaders.EntityShader;
import org.golde.java.game.shaders.TerrainShader;
import org.golde.java.game.shaders.WaterShader;
import org.golde.java.game.terrains.Terrain;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

public class MasterRenderer {

	private static final float FOV = 70;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 1000;
	private Matrix4f projectionMatrix;
	private static final float SKY_RED = 0.1f;
	private static final float SKY_GREEN = 0.1f;
	private static final float SKY_BLUE = 0.1f;
	private static final float FOG_DENSITY = 0.007f; //0.007f
	private static final float FOG_GRADIENT = 1.5f; //1.5f;
	
	private EntityShader staticShader = new EntityShader();
	private EntityRenderer entityRenderer;
	private TerrainRenderer terrainRenderer;
	private TerrainShader terrainShader = new TerrainShader();
	private WaterShader waterShader = new WaterShader();
	private WaterRenderer waterRenderer;
	
	private SkyboxRenderer skybox;
	private Loader loader;
	
	private boolean renderColliders = false;
	
	public MasterRenderer(Loader loader, WaterFrameBuffers waterFbos) {
		enableCulling();
		createProjectionMatrix();
		entityRenderer = new EntityRenderer(staticShader, projectionMatrix);
		terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
		skybox = new SkyboxRenderer(loader, projectionMatrix);
		this.loader = loader;
		
		waterRenderer = new WaterRenderer(loader, waterShader, projectionMatrix, waterFbos);
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
	
	public void renderScene(List<Light> lights, Camera camera, List<Terrain> terrainsIn, List<Entity> entitiesIn, List<MPlayer> playersIn, Vector4f clipPlane, EnumRenderCall renderCall) {
		for(Terrain terrain : terrainsIn) {
			prossessTerrain(terrain);
		}
		
		for(Entity entity : entitiesIn) {
			processEntity(entity);
		}
		
		for(MPlayer player : playersIn) {
			processEntity(player.entity);
		}
		
		render(camera, lights, clipPlane, renderCall);
	}
	
	private void render(Camera camera, List<Light> lights, Vector4f clipPlane, EnumRenderCall renderCall) {
		
		
		
		prepare();
		
		staticShader.start();
		staticShader.loadClipPlane(clipPlane);
		staticShader.loadFog(FOG_DENSITY, FOG_GRADIENT);
		staticShader.loadSkyColor(SKY_RED, SKY_GREEN, SKY_BLUE);
		staticShader.loadLights(lights);
		staticShader.loadViewMatrix(camera);
		entityRenderer.render(entities, renderCall);
		if (renderColliders) {
			entityRenderer.render(colliderEntities, renderCall);
		}
		staticShader.stop();
		
		terrainShader.start();
		terrainShader.loadClipPlane(clipPlane);//TODO: FIX GROUND NOT RENDEING
		terrainShader.loadFog(FOG_DENSITY, FOG_GRADIENT);
		terrainShader.loadSkyColor(SKY_RED, SKY_GREEN, SKY_BLUE);
		terrainShader.loadLights(sortLights(lights, camera));
		terrainShader.loadViewMatrix(camera);
		terrainRenderer.render(terrains, renderCall);
		terrainShader.stop();
		
		skybox.render(camera, renderCall);
		
		terrains.clear();
		entities.clear();
		colliderEntities.clear();
	}
	
	public void renderWater(List<WaterTile> water, Camera camera, Light sun) {
		waterRenderer.render(water, camera, sun);
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
	
	private void prossessTerrain(Terrain terrain) {
		terrains.add(terrain);
	}
	
	private void processEntity(Entity entity) {
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
		GL11.glClearColor(SKY_RED, SKY_GREEN, SKY_BLUE, 1);
	}
	
	private void createProjectionMatrix() {
		projectionMatrix = new Matrix4f();
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;

		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;
	}
	
	
	public void cleanUp() {
		GLog.info("  Cleanup: shaders");
		staticShader.cleanUp();
		GLog.info("Cleanup: terrain");
		terrainShader.cleanUp();
		waterShader.cleanUp();
		
	}
	
	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}
	
	public static enum EnumRenderCall {
		WATER_REFLECTION, WATER_REFRACTION, SCENE
	}
	
}
