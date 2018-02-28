package org.golde.java.gameengine3d.objs.debug;

import org.golde.java.gameengine3d.GameEngine;
import org.golde.java.gameengine3d.objs.base.colliders.BoxCollider;
import org.golde.java.gameengine3d.objs.base.entities.Entity;
import org.golde.java.gameengine3d.objs.models.TexturedModel;
import org.golde.java.gameengine3d.rendering.loader.Loader;
import org.golde.java.gameengine3d.texturing.models.ModelTexture;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class EntityColliderBox extends Entity 
{
	Vector3f start, end;
	
	static TexturedModel texturedModel;
	
	private EntityColliderBox(TexturedModel model, BoxCollider boxCollider)
	{
		super(model);

		start = boxCollider.getStartPoint();
		end = boxCollider.getEndPoint();
	}
	
	public static EntityColliderBox create(Loader loader, BoxCollider boxCollider)
	{
		if (texturedModel == null) {
			texturedModel = new TexturedModel(GameEngine.getInstance().getOBJLoader().loadObjectModel("models/collider/box"), new ModelTexture(loader.loadTexture("models/collider/texture")));
		}
		return new EntityColliderBox(texturedModel, boxCollider);
	}

	
	@Override
	public Matrix4f getTransformationMatrix() {
		Vector3f center = new Vector3f((start.getX() + end.getX()) / 2.0F, (start.getY() + end.getY()) / 2.0F, (start.getZ() + end.getZ()) / 2.0F);
		float scaleX = (end.getX() - start.getX()) / 2.0F;
		float scaleY = (end.getY() - start.getY()) / 2.0F;
		float scaleZ = (end.getZ() - start.getZ()) / 2.0F;
		
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(center, matrix, matrix);
		Matrix4f.scale(new Vector3f(scaleX, scaleY, scaleZ), matrix, matrix);
		return matrix;
	}
}
