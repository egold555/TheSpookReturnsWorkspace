package org.golde.java.game.objects.terrain.decoration;

import org.golde.java.game.models.TexturedModel;
import org.golde.java.game.objects.base.colliders.CylinderCollider;
import org.golde.java.game.objects.base.entities.Entity;
import org.golde.java.game.renderEngine.Loader;
import org.golde.java.game.renderEngine.OBJLoader;
import org.golde.java.game.renderEngine.particles.ParticleSystem;
import org.golde.java.game.renderEngine.renderers.MasterRenderer.EnumRenderCall;
import org.golde.java.game.terrains.Terrain;
import org.golde.java.game.textures.model.ModelTexture;
import org.golde.java.game.textures.particles.ParticleTexture;
import org.lwjgl.util.vector.Vector3f;

public class EntityFirepit extends Entity{

	ParticleSystem psystem;

	public EntityFirepit(Loader loader, float x, float z, Terrain terrain, float scale) {
		super(getModel(loader), new Vector3f(x, terrain.getHeightOfTerrain(x, z), z), 0, 0, 0, scale);

		//smoke 8
		psystem = new ParticleSystem(new ParticleTexture(loader.loadTexture("particles/smoke"), 8), 3, 3, 0, 20, 1);
		psystem.randomizeRotation();
		psystem.setDirection(new Vector3f(0,1,0), 0.1f);
		psystem.setLifeError(0.1f);
		psystem.setScaleError(0.4f);
		psystem.setScaleError(0.8f);

		addCollider(new CylinderCollider(new Vector3f(), 6, 4, 0));
	}

	static TexturedModel model;

	static TexturedModel getModel(Loader loader)
	{
		if (model == null) {
			model = new TexturedModel(OBJLoader.loadObjectModel("models/firepit", loader), new ModelTexture(loader.loadTexture("models/firepit")));
		}
		return model;
	}

	@Override
	public void onRender(EnumRenderCall renderCall) {
		if(renderCall == EnumRenderCall.SCENE) {
			psystem.generateParticles(getPosition());
		}
		super.onRender(renderCall);
	}

}
