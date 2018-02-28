package org.golde.java.test;

import org.golde.java.gameengine3d.objs.base.colliders.Collider;
import org.golde.java.gameengine3d.objs.base.colliders.CylinderCollider;
import org.golde.java.gameengine3d.objs.base.entities.EntityAbstractPlayer;
import org.golde.java.gameengine3d.rendering.loader.Loader;
import org.lwjgl.util.vector.Vector3f;

public class Player extends EntityAbstractPlayer{

	public Player(Loader loader, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(loader, position, rotX, rotY, rotZ, scale);
		
		Collider collider = new CylinderCollider(new Vector3f(), 2, 11, 0);
		collider.blocks = false;
		addCollider(collider);
	}

	@Override
	public int getMaxHealth() {
		return 0;
	}
	
	

}
