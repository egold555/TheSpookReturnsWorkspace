package org.golde.java.gameengine3d.objs.debug;

import org.golde.java.gameengine3d.GameEngine;
import org.golde.java.gameengine3d.objs.base.colliders.CylinderCollider;
import org.golde.java.gameengine3d.objs.base.entities.Entity;
import org.golde.java.gameengine3d.objs.models.TexturedModel;
import org.golde.java.gameengine3d.rendering.loader.Loader;
import org.golde.java.gameengine3d.texturing.models.ModelTexture;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class EntityColliderCylinder extends Entity 
{
	Vector3f bottomCenter;
	float radius, height, depth;
	
	static TexturedModel texturedModel;
	
	private EntityColliderCylinder(TexturedModel model, CylinderCollider collider)
	{
		super(model);

		bottomCenter = collider.getBottomCenter();
		radius = collider.getRadius();
		height = collider.getHeight();
		depth = collider.getDepth();
	}
	
	public static EntityColliderCylinder create(Loader loader, CylinderCollider collider)
	{
		if (texturedModel == null) {
			texturedModel = new TexturedModel(GameEngine.getInstance().getOBJLoader().loadObjectModel("models/collider/cyl"), new ModelTexture(loader.loadTexture("models/collider/texture")));
		}
		return new EntityColliderCylinder(texturedModel, collider);
	}

	
	@Override
	public Matrix4f getTransformationMatrix() {
		Vector3f center = new Vector3f(bottomCenter.getX(), bottomCenter.getY() - depth + (height + depth) / 2, bottomCenter.getZ());
		float scaleX = radius;
		float scaleZ = radius;
		float scaleY = (height + depth) / 2.0F;
		
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(center, matrix, matrix);
		Matrix4f.scale(new Vector3f(scaleX, scaleY, scaleZ), matrix, matrix);
		return matrix;
	}
}
