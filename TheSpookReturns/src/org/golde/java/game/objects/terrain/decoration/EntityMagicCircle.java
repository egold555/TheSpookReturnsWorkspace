package org.golde.java.game.objects.terrain.decoration;

import org.golde.java.game.Main;
import org.golde.java.game.models.TexturedModel;
import org.golde.java.game.objects.base.colliders.CylinderCollider;
import org.golde.java.game.objects.base.entities.Entity;
import org.golde.java.game.objects.light.Light;
import org.golde.java.game.objects.player.EntityPlayer;
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

		CylinderCollider coll = new CylinderCollider(new Vector3f(0,0,0), 1, 3, 0);
		coll.blocks = false;
		addCollider(coll);

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

	@Override
	public void onCollision(Entity collidedWith) {
		
		if(collidedWith instanceof EntityPlayer) {
			((EntityPlayer)collidedWith).addVelocity(0, 5, 0);
		}
		super.onCollision(collidedWith);
	}

	/*float y2 = 0;
	int radius = 2;
	private void doSpririal() {
		//Mke a abstract version of particle system
		//have like a shape particle system and have different functions for the shape like animated spririal or something

		final Vector3f loc = getPosition();
		for(double y = 0; y <= 50; y+=0.5) {
			double x = radius * Math.cos(y);
			double z = radius * Math.sin(y);
			Vector3f newPos = new Vector3f((float) (loc.getX() + x), (float) (loc.getY() + y), (float) (loc.getZ() + z));
			new Particle(particleTexture, newPos, new Vector3f(0,0,0), 0, 4, 0, 1);
		}
		double x = radius * Math.cos(y2);
		double z = radius * Math.sin(y2);
		Vector3f newPos = new Vector3f((float) (loc.getX() + x), (float) (loc.getY() + y2 + 4), (float) (loc.getZ() + z));
		
		new Particle(particleTexture, newPos, new Vector3f(0,0,0), 0.5f, 4, 0, 1);
		if(y2 >= 50) {	
			y2 = 0;
		}
		y2+=0.05;

	}*/

}
