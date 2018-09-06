package org.golde.java.game.objects.peaceful;

import org.golde.java.game.models.TexturedModel;
import org.golde.java.game.objects.base.entities.EntityMoveable;
import org.golde.java.game.renderEngine.Loader;
import org.golde.java.game.renderEngine.OBJLoader;
import org.golde.java.game.terrains.Terrain;
import org.golde.java.game.textures.model.ModelTexture;
import org.lwjgl.util.vector.Vector3f;

public class EntityCat extends EntityMoveable{

	public EntityCat(Loader loader, float x, float z, Terrain terrain, float scale) {
		super(getModel(loader), new Vector3f(x, terrain.getHeightOfTerrain(x, z) + 10, z), 0, 0, 0, scale);
		
		/*this.addBehavior(new BehaviorLookAtPlayer(3000));
		this.addBehavior(new BehaviorWalkToPlayer(20, true));
		
		BoxCollider coll = new BoxCollider(new Vector3f(0, 0, 0), new Vector3f(-0.5f, -0.5f, -0.5f));
		coll.blocks = false;
		this.addCollider(coll);*/
	}

	static TexturedModel model;

	static TexturedModel getModel(Loader loader)
	{
		if (model == null) {
			model = new TexturedModel(OBJLoader.loadObjectModel("models/cat", loader), new ModelTexture(loader.loadTexture("models/cat")));
		}
		return model;
	}

	@Override
	public int getMaxHealth() {
		return 100;
	}

	@Override
	public void onDeath() {
		
	}

}
