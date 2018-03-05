package org.golde.java.game.objects.terrain.decoration;

import org.golde.java.game.Main;
import org.golde.java.game.models.TexturedModel;
import org.golde.java.game.objects.base.colliders.BoxCollider;
import org.golde.java.game.objects.base.colliders.CylinderCollider;
import org.golde.java.game.objects.base.entities.Entity;
import org.golde.java.game.objects.light.Light;
import org.golde.java.game.renderEngine.Loader;
import org.golde.java.game.renderEngine.OBJLoader;
import org.golde.java.game.renderEngine.particles.ParticleSystem;
import org.golde.java.game.terrains.Terrain;
import org.golde.java.game.textures.model.ModelTexture;
import org.golde.java.game.textures.particles.ParticleTexture;
import org.lwjgl.util.vector.Vector3f;

public class EntityMagicCircle extends Entity {

	Light light;
	float origLocY = 0;
	ParticleSystem psystem;
	
	public EntityMagicCircle(Loader loader, float x, float z, Terrain terrain, float scale) {
		super(getModel(loader), new Vector3f(x, terrain.getHeightOfTerrain(x, z)-9.5f, z), 0, 0, 0, scale);
		this.getModel().getTexture().setUseFakeLightning(true);
		this.getModel().getTexture().setHasTransparency(true);
		origLocY = terrain.getHeightOfTerrain(x, z)+9;
		light = new Light(new Vector3f(x, origLocY , z), new Vector3f(1,0.5f, 0), new Vector3f(0.5f, 0.01f, 0.001f));
		Main.getLights().add(light);

		psystem = new ParticleSystem(new ParticleTexture(loader.loadTexture("particles/magic_circle2"), 1), 100, 25, 0, 4, 1);
		psystem.randomizeRotation();
		psystem.setDirection(new Vector3f(0,1,0), 0.1f);
		psystem.setLifeError(0.1f);
		psystem.setScaleError(0.4f);
		psystem.setScaleError(0.8f);
		
		//addCollider(new CylinderCollider(getPosition(), 10, 40, 1));
		
	}
	
	static TexturedModel model;
	
	static TexturedModel getModel(Loader loader)
	{
		if (model == null) {
			model = new TexturedModel(OBJLoader.loadObjectModel("models/magic_circle", loader), new ModelTexture(loader.loadTexture("models/magic_circle")));
		}
		return model;
	}
	
	@Override
	public void onRender() {
		psystem.generateParticles(getPosition());
		super.onRender();
	}

}
