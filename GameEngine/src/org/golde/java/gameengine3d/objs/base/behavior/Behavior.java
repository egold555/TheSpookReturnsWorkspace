package org.golde.java.gameengine3d.objs.base.behavior;

import org.golde.java.gameengine3d.GameEngine;
import org.golde.java.gameengine3d.objs.base.entities.Entity;

public abstract class Behavior {
	
	protected Entity player = GameEngine.getInstance().getPlayer();
	
	public abstract void onRender(final Entity entity);
	
	public void onCollision(Entity collidedWith) {};
	
}
